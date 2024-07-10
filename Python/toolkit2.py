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

from configobj import ConfigObj
import pandas
import numpy as np
import datetime
from datetime import timedelta
import os
import Tkinter, tkFileDialog
from confirm import confirm
######################################################
import constants1 as cs     # This is the main constants file where constants are defined. Contains dictionary called cs
################## functions used by the code
from rn_calc import rn_calc   # net radiation calcs  (3.1 tech notes)
from LUMPS import LUMPS       # energy balance calcs (3.2 tech notes)
from force_restore import Ts_calc_surf   # force restore calcs (3.3 tech notes)
from simple_water import Ts_EB_W     # simple water body model (3.4 tech notes)
from ld_mod import ld_mod            # model ldown (appendix tech notes)
from Ta_module_new import calc_ta    # air temperature module (3.5 tech notes)
from plotting import val_ts, val_ta, gis    # Ash Broadbent's plotting functions 
surfs =   ['roof', 'road' , 'watr', 'conc' , 'Veg'  , 'dry'  , 'irr']       ## surfaces types that are modelled. 

import sys
print 'Number of arguments:', len(sys.argv), 'arguments.'
print 'Argument List:', str(sys.argv)
if (len(sys.argv) > 1):
  ControlFileName = sys.argv[1]
  print (ControlFileName)
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

#########  MAIN PROGRAM BEGINS HERE ###################
if confirm('This run will be called: '+run) == True:    # prints the run name to screen, user has to input "Y" to begin simulation 

    ########## LC GRID FILE #############################
    if cfM['gis_plot'] == 'Y':
        if not os.path.exists(os.path.join('..','GIS',cfM['site_name'],'mod',run)):          
            os.makedirs(os.path.join('..','GIS',cfM['site_name'],'mod',run))      ## creates a directory to output at GIS grid (shapefile) | gets used in plotting.py

    ########## DEFINE FIG DIR ########################### 
    figdir= os.path.join('..','plots',cfM['site_name'],run)       ## defines a director for outputing plots | only gets used if validating air temp (plotting.py)
    if not os.path.exists(figdir):          
            os.makedirs(figdir)

    ################# read LC data  #####################        
    lc_data = pandas.read_csv(os.path.join('..','input',cfM['site_name'],'LC',cfM['inpt_lc_file'])) # reads the input land cover data
 
    ############## OBS AWS DATA files  #############################
    if cfM['val_ts'] == 'Y':
        obs_file = os.path.join('..','obs',cfM['site_name'],'stations_MET',cfM['inpt_obs_file'])  # file for observed AWS data
        obs_data = pandas.read_csv(obs_file,parse_dates =['TIMESTAMP'], date_parser=dateparse, index_col=['TIMESTAMP'])  # reads observed AWS data and puts in dataframe  | only gets used if validating air temp (plotting.py)

    ########## DEFINE INPUT MET FILE LOCATION HERE #######
    met_file = os.path.join('..','input',cfM['site_name'],'MET',cfM['inpt_met_file'])    # input meteorological forcing data file 
    met_data = pandas.read_csv(met_file,parse_dates=['datetime'], date_parser=dateparse,index_col=['datetime']) # convert to data frame'
    met_data_all = met_data.ix[date1A:date2]   # main forcing meteorological dataframe (including spin up)
    met_data_all = met_data_all.interpolate(method='time') # interpolates forcing data 
    if cfM['mod_ldwn'] == 'Y':                                          # model Ldown in data is not available
        for i in range(len(met_data_all)):
            met_data_all.ix[i]['Ld'] = ld_mod(met_data_all.ix[i])['Ld_md'] ## Ld_mod is added to meteorological forcing data frame

    ########## DEFINE MAIN DATAFRAME ####################  dataframe for different modelled variables     
    mod_data_ts_=np.zeros((nt), np.dtype([('roof','<f8'), ('road','<f8'), ('watr','<f8'), ('conc','<f8'), ('Veg','<f8'), ('dry','<f8'), ('irr','<f8'), ('TSOIL','<f8'), ('avg','<f8'),('date',object)]))   # surface temperature of each surface    
    mod_data_tm_=np.zeros((nt), np.dtype([('roof','<f8'), ('road','<f8'), ('watr','<f8'), ('conc','<f8'), ('Veg','<f8'), ('dry','<f8'), ('irr','<f8'), ('TSOIL','<f8'), ('avg','<f8'),('date',object)]))   # ground temperature of each surface
    mod_data_qh_=np.zeros((nt), np.dtype([('roof','<f8'), ('road','<f8'), ('watr','<f8'), ('conc','<f8'), ('Veg','<f8'), ('dry','<f8'), ('irr','<f8'), ('TSOIL','<f8'), ('avg','<f8'),('date',object)]))   # sensible heat flux of each surface
    mod_data_qe_=np.zeros((nt), np.dtype([('roof','<f8'), ('road','<f8'), ('watr','<f8'), ('conc','<f8'), ('Veg','<f8'), ('dry','<f8'), ('irr','<f8'), ('TSOIL','<f8'), ('avg','<f8'),('date',object)]))    # latent heat flux of each surface
    mod_data_qg_=np.zeros((nt), np.dtype([('roof','<f8'), ('road','<f8'), ('watr','<f8'), ('conc','<f8'), ('Veg','<f8'), ('dry','<f8'), ('irr','<f8'), ('TSOIL','<f8'), ('avg','<f8'),('date',object)]))    # storage heat flux of each surface
    mod_data_rn_=np.zeros((nt), np.dtype([('roof','<f8'), ('road','<f8'), ('watr','<f8'), ('conc','<f8'), ('Veg','<f8'), ('dry','<f8'), ('irr','<f8'), ('TSOIL','<f8'), ('avg','<f8'),('date',object)]))    # net radiation of each surface
    ## NB: "TSOIL" is the soil temperature below the water layer

    mod_rslts=np.zeros((nt,len(lc_data),1),  np.dtype([('ID',np.int32),('Ws', '<f8'), ('Ts','<f8'), ('Ta','<f8'),('Rn', '<f8'),('Qg', '<f8'), ('Qe','<f8'), ('Qh','<f8'),('date',object)]))   # this is the main data array where surface averaged outputs are stored
    
        
    stations = lc_data['FID'].values
    hk=-1
    
    # begin looping through the met forcing data file
    for i in range(0, len(met_data_all)):
        if not i == len(met_data_all)-1: 
            ############ Met variables for each time step (generate dataframe) ##########
            dte   = date1A
            dte   = dte + timedelta(minutes=(i*int(tmstp)))  # current timestep 
            Dats['dte'] = dte 
            met_d = met_data_all 
            print dte, i 
            for surf in surfs:      # cycle through surface type for current timestep
            #################### radiation balance non-water ###########################
                rad  = rn_calc(cs,cfM,met_d,surf,Dats,mod_data_ts_,i)  # creates dictionary with radiation variables for current timestep and surface type
                if surf != 'watr':                                  
                    ##################### ENG BALANCE non-water #######################
                    eng_bals=LUMPS(rad,cs,cfM,met_d,surf,Dats,i)            # creates dictionary with energy balance for current timestep and surface type
                    ##################### CALC LST non-water #########################
                    Ts_stfs =Ts_calc_surf(eng_bals,cs,cfM,mod_data_ts_,mod_data_tm_, Dats,surf,i)   # creates dictionary with surface temperature for current timestep and surface type
                    ################################################################################
                    ### append modelled data to dataframes below... 
                    mod_data_ts_[i][surf] = Ts_stfs['TS']
                    mod_data_tm_[i][surf] = Ts_stfs['TM']                 
                    mod_data_qh_[i][surf] = eng_bals['Qh']
                    mod_data_qe_[i][surf] = eng_bals['Qe']
                    mod_data_qg_[i][surf] = eng_bals['Qg']
                    mod_data_rn_[i][surf] = rad['Rn']
                    
                if surf == 'watr':
                    wtr_stf = Ts_EB_W(met_d,cs,cfM,mod_data_ts_,mod_data_tm_,Dats,i,rad) # creates dictionary with water surface temperature and energy balance 
                    ### append modelled water variables to dataframes below...                    
                    mod_data_ts_[i][surf] = wtr_stf['TsW']
                    mod_data_tm_[i][surf] = wtr_stf['TM']                 
                    mod_data_qh_[i][surf]= wtr_stf['QhW']
                    mod_data_qe_[i][surf] = wtr_stf['QeW']
                    mod_data_qg_[i][surf]= wtr_stf['QgW']
                    mod_data_ts_[i]['TSOIL'] = wtr_stf['TSOIL']
                    mod_data_rn_[i][surf] = rad['Rn']   
   
            mod_data_ts_[i]['Veg'] = met_d['Ta'][i]     #  Ts of tree is assumed equal to Ta (see model validation report Figure 3.8 for justification)
            counter=-1
            for grid in range(0,len(lc_data)):      # now cycle through each grid point
                hk=+1
                counter+=1
                LC = [lc_data['roof'][grid], lc_data['road'][grid], lc_data['watr'][grid], lc_data['conc'][grid], lc_data['Veg'][grid],lc_data['dry'][grid],lc_data['irr'][grid]]  # list with land cover for grid point
                H  = lc_data['H'][grid]         # building height for grid point
                W  = lc_data['W'][grid]         # stree width for grid point 
                
                if cfM['use_obs_ws'] == "Y":                         
                    obs_ws = obs_data['WS_ms_Avg_'+cfM['STa'][counter]]     # observed wind speed file  | users will not used observed wind speed, this is just for testing
                else:
                    obs_ws = []  # create an empty list for obs wind speed  - place filler
                ##################### Aggregate by land cover  ###################
                tS =sum([a*b for a,b in zip(LC,[mod_data_ts_['roof'][i],mod_data_ts_['road'][i],mod_data_ts_['watr'][i],mod_data_ts_['conc'][i],mod_data_ts_['Veg'][i],mod_data_ts_['dry'][i],mod_data_ts_['irr'][i]])])    # surface averaged Tsurf
                qH =sum([a*b for a,b in zip(LC,[mod_data_qh_['roof'][i],mod_data_qh_['road'][i],mod_data_qh_['watr'][i],mod_data_qh_['conc'][i],mod_data_qh_['Veg'][i],mod_data_qh_['dry'][i],mod_data_qh_['irr'][i]])])    # surface averaged Qh
                qE =sum([a*b for a,b in zip(LC,[mod_data_qe_['roof'][i],mod_data_qe_['road'][i],mod_data_qe_['watr'][i],mod_data_qe_['conc'][i],mod_data_qe_['Veg'][i],mod_data_qe_['dry'][i],mod_data_qe_['irr'][i]])])    # surface averaged Qe
                qG =sum([a*b for a,b in zip(LC,[mod_data_qg_['roof'][i],mod_data_qg_['road'][i],mod_data_qg_['watr'][i],mod_data_qg_['conc'][i],mod_data_qg_['Veg'][i],mod_data_qg_['dry'][i],mod_data_qg_['irr'][i]])])    # surface averaged Qg
                rN =sum([a*b for a,b in zip(LC,[mod_data_rn_['roof'][i],mod_data_rn_['road'][i],mod_data_rn_['watr'][i],mod_data_rn_['conc'][i],mod_data_rn_['Veg'][i],mod_data_rn_['dry'][i],mod_data_rn_['irr'][i]])])    # surface average Rn
                ##################### CALC air temperature ########################
                wS_Ta = calc_ta(W,H,met_d,cs,qH,tS,Dats,cfM,obs_ws,i)  # dictionary for canopy air temperature and wind speed
                ############################ append everyhing to output table #####
                for_tab     = (lc_data.ix[grid]['FID'],wS_Ta['Ucan'],tS,wS_Ta['Ta_f'],rN, qG,qE,qH,dte)   
                mod_rslts[i][grid]   = for_tab  ## append the main data to the main modelled data frame 

##########################################################################################
    mod_rslts = mod_rslts[1:] ### THIS IS THE FINAL DATA ARRAY WITH MODEL OUTPUTS  ######
##########################################################################################
    outdir= os.path.join('..','output',cfM['site_name'])       ## defines a director for outputing plot
    if not os.path.exists(outdir):          
            os.makedirs(outdir)
    np.save(os.path.join('..','output',cfM['site_name'], run), mod_rslts)   ### saves the output array as a numpy array can load with numpy.load

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
        
## save the control file....      
    inpt1 = open(ControlFileName, 'r')
    outpt1 = open(os.path.join(figdir,'main_control_file.txt'),'w')
    txt1 = inpt1.read()
    outpt1.write(txt1)
    inpt1.close()
    outpt1.close()

## save the constants file..    
    inpt1 = open(os.path.join('.','constants1.py'),'r')
    outpt1 = open(os.path.join(figdir,'constants.txt'),'w')
    txt1 = inpt1.read()
    outpt1.write(txt1)
    inpt1.close()
    outpt1.close()

