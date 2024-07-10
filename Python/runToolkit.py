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
import ToolkitModule as tkmd

import sys
from configobj import ConfigObj
import pandas
import numpy as np
import datetime
from datetime import timedelta
import os
#os.chdir(r'Z:\Documents\PhD\Uni\CRC_toolkit\Toolkit2\scripts')
#import Tkinter, tkFileDialog
#from confirm import confirm
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

#print 'Number of arguments:', len(sys.argv), 'arguments.'
#print 'Argument List:', str(sys.argv)
if (len(sys.argv) > 1):
  ControlFileName = sys.argv[1]
  #print (ControlFileName)
else:
  ######## SELECTS MAIN CONTROL FILE (uses Tkinter package) ####
  root = Tkinter.Tk(); root.withdraw()
  ControlFileName = tkFileDialog.askopenfilename()
  root.destroy()
cfM = ConfigObj(ControlFileName)  ### This is the dictionary that contains all the control file information 
##############################################################
dateparse = lambda x: pandas.datetime.strptime(x, cfM['date_fmt'])      # parse dates for input met file using format defined in control file
run = cfM['run_name']                               # model run name 
tmstp = cfM['timestep']                             # time step (minutes)
######### DEFINE START AND FINISH DATES HERE ########
date1A=datetime.datetime(int(cfM['date1A'][0]), int(cfM['date1A'][1]),int(cfM['date1A'][2]), int(cfM['date1A'][3]))   ## the date/time that the simulation starts
date1=datetime.datetime(int(cfM['date1'][0]), int(cfM['date1'][1]),int(cfM['date1'][2]), int(cfM['date1'][3]))        ## the date/time for period of interest (i.e. before this will not be saved)
date2=datetime.datetime(int(cfM['date2'][0]), int(cfM['date2'][1]),int(cfM['date2'][2]), int(cfM['date2'][3]))        ## end date/time of simulation 
tD = date2-date1A   ## time difference between start and end date
nt=divmod(tD.days * 86400 + tD.seconds, (60*int(tmstp)))[0] # number of timesteps
date_range = pandas.date_range(date1,date2,freq= tmstp + 'T')               #  date range for model period 
date_range1A =pandas.date_range(date1A,(date2-timedelta(hours=1)),freq= tmstp + 'T') # date range for model period (i.e. including spin-up period)
Dats={'date1A':date1A, 'date1':date1, 'date2':date2, 'date_range':date_range,'date_rangeA':date_range1A} # this is a dictionary with all the date/time information 

lc_data = pandas.read_csv(os.path.join('..','input',cfM['site_name'],'LC',cfM['inpt_lc_file'])) # reads the input land cover data

met_file = os.path.join('..','input',cfM['site_name'],'MET',cfM['inpt_met_file'])    # input meteorological forcing data file 
met_data = pandas.read_csv(met_file,parse_dates=['datetime'], date_parser=dateparse,index_col=['datetime']) # convert to data frame
met_data_all = met_data.ix[date1A:date2]   # main forcing meteorological dataframe (including spin up)
met_data_all = met_data_all.interpolate(method='time') # interpolates forcing data 
if cfM['mod_ldwn'] == 'Y':                                          # model Ldown in data is not available
    for i in range(len(met_data_all)):
        met_data_all.ix[i]['Ld'] = ld_mod(met_data_all.ix[i])['Ld_md'] ## Ld_mod is added to meteorological forcing data frame

mod_rslts,mod_rslts_utci = tkmd.modelRun(nt, cfM, lc_data, met_data_all, date1A, Dats)

outdir= os.path.join('..','output',cfM['site_name'])       ## defines a director for outputing plot
if not os.path.exists(outdir):          
        os.makedirs(outdir)
np.save(os.path.join('..','output',cfM['site_name'], run), mod_rslts)   ### saves the output array as a numpy array can load with numpy.load
np.save(os.path.join('..','output',cfM['site_name'], run + '_utci' ), mod_rslts_utci)

if (False):
    ### run Ash's plottling scripts...    
        if cfM['val_ts'] == 'Y':
            if confirm('validate Ts for AWS?') == True:      
                val_ts(cfM,run,stations,mod_rslts)    
        if cfM['val_ta'] == "Y": 
            if confirm('validate Ta for AWS?') == True:
                val_ta(cfM,met_data,stations,obs_data,mod_rslts,Dats)
        if cfM['gis_plot'] == 'Y':
            if confirm('plot GIS for grid?') == True:
                gis(cfM,mod_rslts,run)

if (False):  
    ## save the control file....      
        inpt1 = open(ControlFileName, 'r')
        outpt1 = open(os.path.join(figdir,'main_control_file.txt'),'w')
        txt1 = inpt1.read()
        outpt1.write(txt1)
        inpt1.close()
        outpt1.close()

if (False):
    ## save the constants file..    
        inpt1 = open(os.path.join('.','constants1.py'),'r')
        outpt1 = open(os.path.join(figdir,'constants.txt'),'w')
        txt1 = inpt1.read()
        outpt1.write(txt1)
        inpt1.close()
        outpt1.close()






