# -*- coding:utf-8 -*-

"""
This is the main script that runs the toolkit air temperature module 

Developed by Ashley Broadbent, Andrew Coutts, and Matthias Demuzere.

This script should be run from the ./scripts directory

see ./documents/Toolkit-2_tech-description.docx for full description of the module

These scripts should run in Windows and Linux - however the gis function (arcpy) can't be used in Linux

Developed in Windows with Python 2.7.12 Anaconda 4.1.1 (32-bit)

Tested with Python 2.7.9 (Linux)


"""

import ToolkitModule_newSVF_fast_walls_newTree_TbRur_Tbcan_roof as tkmd

import sys
#import time
from configobj import ConfigObj
import pandas
import numpy as np
import datetime
from datetime import timedelta
import os
#os.chdir(r'Z:\Documents\PhD\Uni\CRC_toolkit\Toolkit2\scripts')
#import Tkinter, tkFileDialog
#from confirm import confirm
#import math
#from sympy import solve, Eq, Symbol
######################################################
#import constants2 as cs     # This is the main constants file where constants are defined. Contains dictionary called cs
################## functions used by the code
#from rn_calc_3 import rn_calc_new   # net radiation calcs  (3.1 tech notes)
#from LUMPS import LUMPS       # energy balance calcs (3.2 tech notes)
#from force_restore import Ts_calc_surf   # force restore calcs (3.3 tech notes)
#from simple_water import Ts_EB_W     # simple water body model (3.4 tech notes)
#from ld_mod import ld_mod            # model ldown (appendix tech notes)
#from Ta_module_new import calc_ta    # air temperature module (3.5 tech notes)
#from plotting import val_ts, val_ta, gis    # Ash Broadbent's plotting functions 
#from lc_sort import lc_sort          # lc_sorting and SVFs
#from sfc_ri import sfc_ri # Richardson's number calc
#from httc import httc # heat transfer coefficient
#from cd import CD 
surfs =   ['roof', 'road' , 'watr', 'conc' , 'Veg'  , 'dry'  , 'irr', 'wall']       ## surfaces types that are modelled. 

if (len(sys.argv) > 1):
  ControlFileName = sys.argv[1]
  #print (ControlFileName)
else:
  ######## SELECTS MAIN CONTROL FILE (uses Tkinter package) ####
  root = Tkinter.Tk(); root.withdraw()
  ControlFileName = tkFileDialog.askopenfilename()
  root.destroy()

######## SELECTS MAIN CONTROL FILE (uses Tkinter package) ####
#root = Tkinter.Tk(); root.withdraw()    
#ControlFileName = tkFileDialog.askopenfilename()
#root.destroy()
cfM = ConfigObj(ControlFileName)  ### This is the dictionary that contains all the control file information 
##############################################################
dateparse = lambda x: pandas.datetime.strptime(x, cfM['date_fmt'])      # parse dates for input met file using format defined in control file
run = cfM['run_name']                               # model run name 
tmstp = cfM['timestep'] + 'T'                           # time step (minutes)
#tmstp2 = cfM['timestep']
#print (tmstp)
######### DEFINE START AND FINISH DATES HERE ########
date1A=datetime.datetime(int(cfM['date1A'][0]), int(cfM['date1A'][1]),int(cfM['date1A'][2]), int(cfM['date1A'][3]))   ## the date/time that the simulation starts
date1=datetime.datetime(int(cfM['date1'][0]), int(cfM['date1'][1]),int(cfM['date1'][2]), int(cfM['date1'][3]))        ## the date/time for period of interest (i.e. before this will not be saved)
date2=datetime.datetime(int(cfM['date2'][0]), int(cfM['date2'][1]),int(cfM['date2'][2]), int(cfM['date2'][3]))        ## end date/time of simulation 
tD = date2-date1A   ## time difference between start and end date
nt=divmod(tD.days * 86400 + tD.seconds, (int(tmstp[:-1])))[0] # number of timesteps
date_range = pandas.date_range(date1,date2,freq= tmstp)               #  date range for model period 
date_range1A =pandas.date_range(date1A,(date2-timedelta(hours=1)),freq= tmstp) # date range for model period (i.e. including spin-up period)
Dats={'date1A':date1A, 'date1':date1, 'date2':date2, 'date_range':date_range,'date_rangeA':date_range1A} # this is a dictionary with all the date/time information 



################# read LC data  #####################        
lc_data = pandas.read_csv(os.path.join('..','input',cfM['site_name'],'LC',cfM['inpt_lc_file'])) # reads the input land cover data
#print (lc_data)
#avg_lc_data = np.mean(lc_data)
            
#LC_rur = [0.0,0.0, 0.0,0.0,0.0,1.0,0.0]  # list with land cover for grid point               
#fw_rur   = 9
#fg_rur   = 9
        
############## OBS AWS DATA files  #############################
if cfM['val_ts'] == 'Y':
    obs_file = os.path.join('..','obs',cfM['site_name'],'stations_MET',cfM['inpt_obs_file'])  # file for observed AWS data
    obs_data = pandas.read_csv(obs_file,parse_dates =['TIMESTAMP'], date_parser=dateparse, index_col=['TIMESTAMP'])  # reads observed AWS data and puts in dataframe  | only gets used if validating air temp (plotting.py)

########## DEFINE INPUT MET FILE LOCATION HERE #######
met_file = os.path.join('..','input',cfM['site_name'],'MET',cfM['inpt_met_file'])    # input meteorological forcing data file 
met_data = pandas.read_csv(met_file,parse_dates=['datetime'], date_parser=dateparse,index_col=['datetime']) # convert to data frame
#print (met_data)
met_data= met_data.resample(tmstp).ffill() # interpolates forcing data 
met_data_all = met_data.ix[date1A:date2]   # main forcing meteorological dataframe (including spin up)
#met_data_all = met_data_all.interpolate(method='time') # interpolates forcing data 
if cfM['mod_ldwn'] == 'Y':                                          # model Ldown in data is not available
    for i in range(len(met_data_all)):
        met_data_all.ix[i]['Ld'] = ld_mod(met_data_all.ix[i])['Ld_md'] ## Ld_mod is added to meteorological forcing data frame
#print (met_data_all)



mod_rslts = tkmd.modelRun(nt, cfM, lc_data, met_data_all, date1A, Dats)    
    
    
outdir= os.path.join('..','output',cfM['site_name'])       ## defines a director for outputing plot
if not os.path.exists(outdir):          
        os.makedirs(outdir)
#np.save(os.path.join('..','output',cfM['site_name'], run), mod_rslts)   ### saves the output array as a numpy array can load with numpy.load
#print (  (mod_rslts.shape) ) 
#print ( mod_rslts[8519-1][80840-1][0] )
# (8519, 80840, 1)

        


