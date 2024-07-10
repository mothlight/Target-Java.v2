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

#from configobj import ConfigObj
#import pandas
import numpy as np
#import datetime
from datetime import timedelta
import os
#import Tkinter, tkFileDialog
#from confirm import confirm
######################################################
import constants2 as cs     # This is the main constants file where constants are defined. Contains dictionary called cs
################## functions used by the code
from rn_calc_3 import rn_calc_new   # net radiation calcs  (3.1 tech notes)
from LUMPS import LUMPS       # energy balance calcs (3.2 tech notes)
from force_restore import Ts_calc_surf   # force restore calcs (3.3 tech notes)
from simple_water import Ts_EB_W     # simple water body model (3.4 tech notes)
from ld_mod import ld_mod            # model ldown (appendix tech notes)
from Ta_module_new import calc_ta    # air temperature module (3.5 tech notes)
#from plotting import val_ts, val_ta, gis    # Ash Broadbent's plotting functions 
#import sys
from utci import getTmrtForGrid_RH,getUTCIForGrid_RH

surfs =   ['roof', 'road' , 'watr', 'conc' , 'Veg'  , 'dry'  , 'irr', 'wall']       ## surfaces types that are modelled. 

def modelRun(nt, cfM, lc_data, met_data_all, date1A, Dats):
    run = cfM['run_name']                               # model run name 
    tmstp = cfM['timestep']                             # time step (minutes)
    #########  MAIN PROGRAM BEGINS HERE ###################
    #if confirm('This run will be called: '+run) == True:    # prints the run name to screen, user has to input "Y" to begin simulation 
    #if (True):

    ########## LC GRID FILE #############################
    #if cfM['gis_plot'] == 'Y':
    #    if not os.path.exists(os.path.join('..','GIS',cfM['site_name'],'mod',run)):          
    #        os.makedirs(os.path.join('..','GIS',cfM['site_name'],'mod',run))      ## creates a directory to output at GIS grid (shapefile) | gets used in plotting.py

    ########## DEFINE FIG DIR ########################### 
    figdir= os.path.join('..','plots',cfM['site_name'],run)       ## defines a directory for outputing plots | only gets used if validating air temp (plotting.py)
    if not os.path.exists(figdir):          
            os.makedirs(figdir)

    ################# read LC data  #####################        
    #lc_data = pandas.read_csv(os.path.join('..','input',cfM['site_name'],'LC',cfM['inpt_lc_file'])) # reads the input land cover data

    ############## OBS AWS DATA files  #############################
    #if cfM['val_ts'] == 'Y':
    #    obs_file = os.path.join('..','obs',cfM['site_name'],'stations_MET',cfM['inpt_obs_file'])  # file for observed AWS data
    #    obs_data = pandas.read_csv(obs_file,parse_dates =['TIMESTAMP'], date_parser=dateparse, index_col=['TIMESTAMP'])  # reads observed AWS data and puts in dataframe  | only gets used if validating air temp (plotting.py)

    ########## DEFINE INPUT MET FILE LOCATION HERE #######
    #met_file = os.path.join('..','input',cfM['site_name'],'MET',cfM['inpt_met_file'])    # input meteorological forcing data file 
    #met_data = pandas.read_csv(met_file,parse_dates=['datetime'], date_parser=dateparse,index_col=['datetime']) # convert to data frame
    #met_data_all = met_data.ix[date1A:date2]   # main forcing meteorological dataframe (including spin up)
    #met_data_all = met_data_all.interpolate(method='time') # interpolates forcing data 
    #if cfM['mod_ldwn'] == 'Y':                                          # model Ldown in data is not available
    #    for i in range(len(met_data_all)):
    #        met_data_all.ix[i]['Ld'] = ld_mod(met_data_all.ix[i])['Ld_md'] ## Ld_mod is added to meteorological forcing data frame

    ########## DEFINE MAIN DATAFRAME ####################  dataframe for different modelled variables     
    mod_data_ts_=np.zeros((nt,10), np.dtype([('wall','<f8'),('roof','<f8'), ('road','<f8'), ('watr','<f8'), ('conc','<f8'), ('Veg','<f8'), ('dry','<f8'), ('irr','<f8'), ('TSOIL','<f8'), ('avg','<f8'),('date',object)]))   # surface temperature of each surface    
    mod_data_tm_=np.zeros((nt,10), np.dtype([('wall','<f8'),('roof','<f8'), ('road','<f8'), ('watr','<f8'), ('conc','<f8'), ('Veg','<f8'), ('dry','<f8'), ('irr','<f8'), ('TSOIL','<f8'), ('avg','<f8'),('date',object)]))   # ground temperature of each surface
    mod_data_qh_=np.zeros((nt,10), np.dtype([('wall','<f8'),('roof','<f8'), ('road','<f8'), ('watr','<f8'), ('conc','<f8'), ('Veg','<f8'), ('dry','<f8'), ('irr','<f8'), ('TSOIL','<f8'), ('avg','<f8'),('date',object)]))   # sensible heat flux of each surface
    mod_data_qe_=np.zeros((nt,10), np.dtype([('wall','<f8'),('roof','<f8'), ('road','<f8'), ('watr','<f8'), ('conc','<f8'), ('Veg','<f8'), ('dry','<f8'), ('irr','<f8'), ('TSOIL','<f8'), ('avg','<f8'),('date',object)]))    # latent heat flux of each surface
    mod_data_qg_=np.zeros((nt,10), np.dtype([('wall','<f8'),('roof','<f8'), ('road','<f8'), ('watr','<f8'), ('conc','<f8'), ('Veg','<f8'), ('dry','<f8'), ('irr','<f8'), ('TSOIL','<f8'), ('avg','<f8'),('date',object)]))    # storage heat flux of each surface
    mod_data_rn_=np.zeros((nt,10), np.dtype([('wall','<f8'),('roof','<f8'), ('road','<f8'), ('watr','<f8'), ('conc','<f8'), ('Veg','<f8'), ('dry','<f8'), ('irr','<f8'), ('TSOIL','<f8'), ('avg','<f8'),('date',object)]))    # net radiation of each surface
    ## NB: "TSOIL" is the soil temperature below the water layer

    mod_rslts=np.zeros((nt,len(lc_data),1),  np.dtype([('ID',np.int32),('Ws', '<f8'), ('Ts','<f8'), ('Ta','<f8'),('Rn', '<f8'),('Qg', '<f8'), ('Qe','<f8'), ('Qh','<f8'),('date',object)]))   # this is the main data array where surface averaged outputs are stored
    
    mod_rslts_tmrt_utci=np.zeros((nt,len(lc_data),1),  np.dtype([('ID',np.int32),('tmrt', '<f8'), ('utci','<f8'),('date',object)]))
   
        
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
            for vf in range(0,10):
                svfg = float(vf+1)/10.0
                for surf in surfs:      # cycle through surface type for current timestep
                #################### radiation balance non-water ###########################
                    if not surf in ['watr','Veg']:
                        rad  = rn_calc_new(cs,cfM,met_d,surf,Dats,mod_data_ts_[:,vf],i,svfg)  # creates dictionary with radiation variables for current timestep and surface type                             
                        ##################### ENG BALANCE non-water #######################
                        eng_bals=LUMPS(rad,cs,cfM,met_d,surf,Dats,i)            # creates dictionary with energy balance for current timestep and surface type
                        ##################### CALC LST non-water #########################
                        Ts_stfs =Ts_calc_surf(eng_bals,cs,cfM,mod_data_ts_[:,vf],mod_data_tm_[:,vf], Dats,surf,i)   # creates dictionary with surface temperature for current timestep and surface type
                        ################################################################################
                        ### append modelled data to dataframes below... 
                        mod_data_ts_[i][vf][surf] = Ts_stfs['TS']
                        mod_data_tm_[i][vf][surf] = Ts_stfs['TM']                 
                        mod_data_qh_[i][vf][surf] = eng_bals['Qh']
                        mod_data_qe_[i][vf][surf] = eng_bals['Qe']
                        mod_data_qg_[i][vf][surf] = eng_bals['Qg']
                        mod_data_rn_[i][vf][surf] = rad['Rn']                   
                    if (surf == 'watr'):
                        rad  = rn_calc_new(cs,cfM,met_d,surf,Dats,mod_data_ts_[:,vf],i,svfg)  # creates dictionary with radiation variables for current timestep and surface type                             
                        wtr_stf = Ts_EB_W(met_d,cs,cfM,mod_data_ts_[:,vf],mod_data_tm_[:,vf],Dats,i,rad) # creates dictionary with water surface temperature and energy balance 
                        ### append modelled water variables to dataframes below...                    
                        mod_data_ts_[i][vf][surf] = wtr_stf['TsW']
                        mod_data_tm_[i][vf][surf] = wtr_stf['TM']                 
                        mod_data_qh_[i][vf][surf]= wtr_stf['QhW']
                        mod_data_qe_[i][vf][surf] = wtr_stf['QeW']
                        mod_data_qg_[i][vf][surf]= wtr_stf['QgW']
                        mod_data_ts_[i][vf]['TSOIL'] = wtr_stf['TSOIL']
                        mod_data_rn_[i][vf][surf] = rad['Rn']   
                    if (surf == 'Veg'):
                        rad  = rn_calc_new(cs,cfM,met_d,surf,Dats,mod_data_ts_[:,vf],i,svfg)  # creates dictionary with radiation variables for current timestep and surface type  
                        ##################### ENG BALANCE tree #######################
                        eng_bals=LUMPS(rad,cs,cfM,met_d,surf,Dats,i)            # creates dictionary with energy balance for current timestep and surface type
                        #####################  LST tree #########################
                        mod_data_ts_[i][vf]['Veg'] = met_d['Ta'][i]     #  Ts of tree is assumed equal to Ta (see model validation report Figure 3.8 for justification)
                        mod_data_qh_[i][vf]['Veg'] = eng_bals['Qh']
                        mod_data_qe_[i][vf]['Veg'] = eng_bals['Qe']
                        mod_data_qg_[i][vf]['Veg'] = eng_bals['Qg']
                        mod_data_rn_[i][vf]['Veg'] = rad['Rn']

                        
            counter=-1
            for grid in range(0,len(lc_data)):      # now cycle through each grid point
                hk=+1
                counter+=1
                LC = [lc_data['roof'][grid], lc_data['road'][grid], lc_data['watr'][grid], lc_data['conc'][grid], lc_data['Veg'][grid],lc_data['dry'][grid],lc_data['irr'][grid]]  # list with land cover for grid point
                H  = lc_data['H'][grid]         # building height for grid point
                W  = lc_data['W'][grid]         # stree width for grid point 
                if W < 5.0:
                    W = 5.0
                svfgA = (1.0+(H/W)**2)**0.5 - H/W 
                svfwA = 1./2.*(1.+W/H-(1.+(W/H)**2.)**0.5)
                
                horz_area = cs.cs['res'] * cs.cs['res']  
                wall_area = 2.*(H/W)*(1.-lc_data['roof'][grid])
                
                tot_area = wall_area + horz_area
                LCarea    = [c*horz_area for c in LC]
                #LC2 = [e/tot_area for e in LCarea]
                #LC2.insert(len(LC2),(wall_area/tot_area))
                LC.insert(len(LC),wall_area)
                LC2 = [e/sum(LC) for e in LC]

                if svfgA <= 0.1:
                    fg = 0
                if (svfgA > 0.1) and (svfgA <= 0.2):
                    fg = 1
                if (svfgA > 0.2) and (svfgA <= 0.3):
                    fg = 2
                if (svfgA > 0.3) and (svfgA <= 0.4):
                    fg = 3
                if (svfgA > 0.4) and (svfgA <= 0.5):
                    fg = 4
                if (svfgA > 0.5) and (svfgA <= 0.6):
                    fg = 5
                if (svfgA > 0.6) and (svfgA <= 0.7):
                    fg = 6
                if (svfgA > 0.7) and (svfgA <= 0.8):
                    fg = 7
                if (svfgA > 0.8) and (svfgA <= 0.9):
                    fg = 8
                if (svfgA > 0.9) and (svfgA <= 1.0):
                    fg = 9
                    
                if svfwA <= 0.1:
                    fw = 0
                if (svfwA > 0.1) and (svfwA <= 0.2):
                    fw = 1
                if (svfwA > 0.2) and (svfwA <= 0.3):
                    fw = 2
                if (svfwA > 0.3) and (svfwA <= 0.4):
                    fw = 3
                if (svfwA > 0.4) and (svfwA <= 0.5):
                    fw = 4
                if (svfwA > 0.5) and (svfwA <= 0.6):
                    fw = 5
                if (svfwA > 0.6) and (svfwA <= 0.7):
                    fw = 6
                if (svfwA > 0.7) and (svfwA <= 0.8):
                    fw = 7
                if (svfwA > 0.8) and (svfwA <= 0.9):
                    fw = 8
                if (svfwA > 0.9) and (svfwA <= 1.0):
                    fw = 9
                            
                if cfM['use_obs_ws'] == "Y":                         
                    obs_ws = obs_data['WS_ms_Avg_'+cfM['STa'][counter]]     # observed wind speed file  | users will not used observed wind speed, this is just for testing
                else:
                    obs_ws = []  # create an empty list for obs wind speed  - place filler
                ##################### Aggregate by land cover  ###################
                
                tS =sum([a*b for a,b in zip(LC2,[mod_data_ts_['roof'][i][fg],mod_data_ts_['road'][i][fg],mod_data_ts_['watr'][i][fg],mod_data_ts_['conc'][i][fg],mod_data_ts_['Veg'][i][fg],mod_data_ts_['dry'][i][fg],mod_data_ts_['irr'][i][fg],mod_data_ts_['wall'][i][fw]])])    # surface averaged Tsurf
                qH =sum([a*b for a,b in zip(LC,[mod_data_qh_['roof'][i][fg],mod_data_qh_['road'][i][fg],mod_data_qh_['watr'][i][fg],mod_data_qh_['conc'][i][fg],mod_data_qh_['Veg'][i][fg],mod_data_qh_['dry'][i][fg],mod_data_qh_['irr'][i][fg],mod_data_qh_['wall'][i][fw]])])    # surface averaged Qh
                qE =sum([a*b for a,b in zip(LC,[mod_data_qe_['roof'][i][fg],mod_data_qe_['road'][i][fg],mod_data_qe_['watr'][i][fg],mod_data_qe_['conc'][i][fg],mod_data_qe_['Veg'][i][fg],mod_data_qe_['dry'][i][fg],mod_data_qe_['irr'][i][fg],mod_data_qe_['wall'][i][fw]])])    # surface averaged Qe
                qG =sum([a*b for a,b in zip(LC,[mod_data_qg_['roof'][i][fg],mod_data_qg_['road'][i][fg],mod_data_qg_['watr'][i][fg],mod_data_qg_['conc'][i][fg],mod_data_qg_['Veg'][i][fg],mod_data_qg_['dry'][i][fg],mod_data_qg_['irr'][i][fg],mod_data_qg_['wall'][i][fw]])])    # surface averaged Qg
                rN =sum([a*b for a,b in zip(LC,[mod_data_rn_['roof'][i][fg],mod_data_rn_['road'][i][fg],mod_data_rn_['watr'][i][fg],mod_data_rn_['conc'][i][fg],mod_data_rn_['Veg'][i][fg],mod_data_rn_['dry'][i][fg],mod_data_rn_['irr'][i][fg],mod_data_rn_['wall'][i][fw]])])    # surface average Rn
                ##################### CALC air temperature ########################
                wS_Ta = calc_ta(W,H,met_d,cs,qH,tS,Dats,cfM,obs_ws,i)  # dictionary for canopy air temperature and wind speed
                ############################ append everyhing to output table #####
                for_tab     = (lc_data.ix[grid]['FID'],wS_Ta['Ucan'],tS,wS_Ta['Ta_f'],rN, qG,qE,qH,dte)   
                mod_rslts[i][grid]   = for_tab  ## append the main data to the main modelled data frame 
                
                

                # lat hardcoded for now (it is needed to calculate the zenith), should move to config file eventually 
                lat = -37.8136
                yd_actual = dte.timetuple().tm_yday
                TM = dte.timetuple().tm_hour
                lup = cs.cs['sb']*(wS_Ta['Ta_f']+273.15)**4
                #print (wS_Ta['Ta_f'],met_d['RH'][i],wS_Ta['Ucan'],met_d['Kd'][i],tS,met_d['Ld'][i], lup ,yd_actual, TM, lat)
                tmrt = getTmrtForGrid_RH(wS_Ta['Ta_f'],met_d['RH'][i],wS_Ta['Ucan'],met_d['Kd'][i],tS,met_d['Ld'][i], lup ,yd_actual, TM, lat)
                #print (tmrt)
                utci = getUTCIForGrid_RH(wS_Ta['Ta_f'],wS_Ta['Ucan'],met_d['RH'][i],tmrt)
                for_tab_tmrt_utci     = (lc_data.ix[grid]['FID'],tmrt,utci,dte) 
                #print (tmrt,utci)
                mod_rslts_tmrt_utci[i][grid] = for_tab_tmrt_utci
                
                
                
                #if svfg < 1.0:
                #    print grid, mod_rslts[i][grid] , svfg
##########################################################################################
    mod_rslts = mod_rslts[1:] ### THIS IS THE FINAL DATA ARRAY WITH MODEL OUTPUTS  ######
##########################################################################################
    mod_rslts_tmrt_utci = mod_rslts_tmrt_utci[1:]
    return mod_rslts,mod_rslts_tmrt_utci
