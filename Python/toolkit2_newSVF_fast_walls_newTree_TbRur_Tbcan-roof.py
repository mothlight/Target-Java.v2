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
import time
from configobj import ConfigObj
import pandas
import numpy as np
import datetime
from datetime import timedelta
import os
os.chdir(r'Z:\Documents\PhD\Uni\CRC_toolkit\Toolkit2\scripts')
import Tkinter, tkFileDialog
from confirm import confirm
import math
from sympy import solve, Eq, Symbol
######################################################
import constants2 as cs     # This is the main constants file where constants are defined. Contains dictionary called cs
################## functions used by the code
from rn_calc_3 import rn_calc_new   # net radiation calcs  (3.1 tech notes)
from LUMPS import LUMPS       # energy balance calcs (3.2 tech notes)
from force_restore import Ts_calc_surf   # force restore calcs (3.3 tech notes)
from simple_water import Ts_EB_W     # simple water body model (3.4 tech notes)
from ld_mod import ld_mod            # model ldown (appendix tech notes)
from Ta_module_new import calc_ta    # air temperature module (3.5 tech notes)
from plotting import val_ts, val_ta, gis    # Ash Broadbent's plotting functions 
from lc_sort import lc_sort          # lc_sorting and SVFs
from sfc_ri import sfc_ri # Richardson's number calc
from httc import httc # heat transfer coefficient
from cd import CD 
surfs =   ['roof', 'road' , 'watr', 'conc' , 'Veg'  , 'dry'  , 'irr', 'wall']       ## surfaces types that are modelled. 

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
nt=divmod(tD.days * 86400 + tD.seconds, (int(tmstp[:-1])))[0] # number of timesteps
date_range = pandas.date_range(date1,date2,freq= tmstp)               #  date range for model period 
date_range1A =pandas.date_range(date1A,(date2-timedelta(hours=1)),freq= tmstp) # date range for model period (i.e. including spin-up period)
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
    avg_lc_data = np.mean(lc_data)
               
    LC_rur = [0.0,0.0, 0.0,0.0,0.0,1.0,0.0]  # list with land cover for grid point               
    fw_rur   = 9
    fg_rur   = 9
          
    ############## OBS AWS DATA files  #############################
    if cfM['val_ts'] == 'Y':
        obs_file = os.path.join('..','obs',cfM['site_name'],'stations_MET',cfM['inpt_obs_file'])  # file for observed AWS data
        obs_data = pandas.read_csv(obs_file,parse_dates =['TIMESTAMP'], date_parser=dateparse, index_col=['TIMESTAMP'])  # reads observed AWS data and puts in dataframe  | only gets used if validating air temp (plotting.py)

    ########## DEFINE INPUT MET FILE LOCATION HERE #######
    met_file = os.path.join('..','input',cfM['site_name'],'MET',cfM['inpt_met_file'])    # input meteorological forcing data file 
    met_data = pandas.read_csv(met_file,parse_dates=['datetime'], date_parser=dateparse,index_col=['datetime']) # convert to data frame
    met_data= met_data.resample(tmstp).ffill() # interpolates forcing data 
    met_data_all = met_data.ix[date1A:date2]   # main forcing meteorological dataframe (including spin up)
    #met_data_all = met_data_all.interpolate(method='time') # interpolates forcing data 
    if cfM['mod_ldwn'] == 'Y':                                          # model Ldown in data is not available
        for i in range(len(met_data_all)):
            met_data_all.ix[i]['Ld'] = ld_mod(met_data_all.ix[i])['Ld_md'] ## Ld_mod is added to meteorological forcing data frame

    ########## DEFINE MAIN DATAFRAME ####################  dataframe for different modelled variables     
    mod_data_ts_=np.zeros((nt,10), np.dtype([('wall','<f8'),('roof','<f8'), ('road','<f8'), ('watr','<f8'), ('conc','<f8'), ('Veg','<f8'), ('dry','<f8'), ('irr','<f8'), ('TSOIL','<f8'), ('avg','<f8'),('date',object)]))   # surface temperature of each surface    
    mod_data_tm_=np.zeros((nt,10), np.dtype([('wall','<f8'),('roof','<f8'), ('road','<f8'), ('watr','<f8'), ('conc','<f8'), ('Veg','<f8'), ('dry','<f8'), ('irr','<f8'), ('TSOIL','<f8'), ('avg','<f8'),('date',object)]))   # ground temperature of each surface
    mod_data_qh_=np.zeros((nt,10), np.dtype([('wall','<f8'),('roof','<f8'), ('road','<f8'), ('watr','<f8'), ('conc','<f8'), ('Veg','<f8'), ('dry','<f8'), ('irr','<f8'), ('TSOIL','<f8'), ('avg','<f8'),('date',object)]))   # sensible heat flux of each surface
    mod_data_qe_=np.zeros((nt,10), np.dtype([('wall','<f8'),('roof','<f8'), ('road','<f8'), ('watr','<f8'), ('conc','<f8'), ('Veg','<f8'), ('dry','<f8'), ('irr','<f8'), ('TSOIL','<f8'), ('avg','<f8'),('date',object)]))    # latent heat flux of each surface
    mod_data_qg_=np.zeros((nt,10), np.dtype([('wall','<f8'),('roof','<f8'), ('road','<f8'), ('watr','<f8'), ('conc','<f8'), ('Veg','<f8'), ('dry','<f8'), ('irr','<f8'), ('TSOIL','<f8'), ('avg','<f8'),('date',object)]))    # storage heat flux of each surface
    mod_data_rn_=np.zeros((nt,10), np.dtype([('wall','<f8'),('roof','<f8'), ('road','<f8'), ('watr','<f8'), ('conc','<f8'), ('Veg','<f8'), ('dry','<f8'), ('irr','<f8'), ('TSOIL','<f8'), ('avg','<f8'),('date',object)]))    # net radiation of each surface
    ## NB: "TSOIL" is the soil temperature below the water layer

    #mod_rslts=np.zeros((nt,len(lc_data),1),  np.dtype([('ID',np.int32),('Ws', '<f8'), ('Ts','<f8'), ('Ta','<f8'),('Rn', '<f8'),('Qg', '<f8'), ('Qe','<f8'), ('Qh','<f8'),('date',object)]))   # this is the main data array where surface averaged outputs are stored
    mod_rslts=np.zeros((nt,len(lc_data),1),  np.dtype([('ID',np.int32),('Ws', '<f8'), ('Ts','<f8'), ('Ta','<f8'),('date',object)]))   # this is the main data array where surface averaged outputs are stored
    
    mod_fm = np.zeros((nt,1))
    mod_cd = np.zeros((nt,1))
    mod_U_TaRef = np.zeros((nt,1))

  
    
        
    stations = lc_data['FID'].values
    hk=-1
    
    # begin looping through the met forcing data file
    for i in range(0, len(met_data_all)):
        if not i == len(met_data_all)-1: 
            ############ Met variables for each time step (generate dataframe) ##########
            dte   = date1A
            dte   = dte + timedelta(seconds=(i*int(tmstp[:-1])))  # current timestep 
            Dats['dte'] = dte 
            met_d = met_data_all 
            print dte, i
            
            ## BEGIN CALCULATION OF Tb_urb
            
            rad_rur  = rn_calc_new(cs,cfM,met_d,'dry',Dats,mod_data_ts_[:,9],i,1.0)  # creates dictionary with radiation variables for current timestep and surface type                             
            ##################### ENG BALANCE non-water #######################
            eng_bals_rur=LUMPS(rad_rur,cs,cfM,met_d,'dry',Dats,i)            # creates dictionary with energy balance for current timestep and surface type
            ##################### CALC LST non-water #########################
            Ts_stfs_rur =Ts_calc_surf(eng_bals_rur,cs,cfM,mod_data_ts_[:,9],mod_data_tm_[:,9], Dats,'dry',i)   # creates dictionary with surface temperature for current timestep and surface type
                                           
            #  canopy displacement height (m)
            dcan_rur = 0.0  
            #  Roughness lenght for momentum (m)
            z0m_rur = 0.005
            #  Roughness lenght for heat (m)
            z0h_rur = z0m_rur/10.

            z_Hx3 = max(lc_data['H']) * 3.0 # height of Tb (3 x max building height)  -  this is the height of the main Tb value 
            z_Hx2  = max(lc_data['H']) * 2.0 # height of Tb (2 x max building height) - this is the secondary height used for Tb above canyon 
            
            z_TaRef = cs.cs['z_TaRef']  # height of air temperature measurements (usually 2 m)
            z_Uref  = cs.cs['z_URef']   # height of reference wind speed measurement (usually 10 m)
            
            Tlow_surf = Ts_stfs_rur['TS']       # surface temperature at rural (reference) site
            ref_ta   = met_d['Ta'][i]        # observed air temperature
            
            ####### DEFINE REFERENCE WIND SPEED RURAL #######
            mod_U_TaRef[i] = max(met_d['WS'][i] * ((math.log(z_TaRef/z0m_rur))/(math.log(z_Uref/z0m_rur))),0.5)  ## convert to wind speed for Utop height using log profile.         CHECK initial extroplation is based on log profile (alternative is to use Fm from prev time step)
            ####### calculate Richardson's number and heat transfer coefficient for rural site 
            Ri_rur = sfc_ri(z_TaRef-z0m_rur,ref_ta,Tlow_surf,mod_U_TaRef[i])['Ri']   ## calculate Richardon's number for Rural site            
            httc_rural = httc(Ri_rur,mod_U_TaRef[i],z_TaRef-z0m_rur,z0m_rur,z0h_rur, met_d,cs,i,Tlow_surf,ref_ta)       ## calculate httc for Rural site           
            httc_rur = httc_rural['httc']
            ###### sensible heat flux
            Qh_ = httc_rur*(Tlow_surf-ref_ta)
            ###### calculate cd, fm, ustar used for calcuating wind speed    
            cd_out =  CD(Ri_rur,z_TaRef-z0m_rur,z0m_rur,z0h_rur,met_d,cs,i)           
            mod_fm[i]= cd_out['Fm']
            mod_cd[i]=cd_out['cd_out']        
            ustar=math.sqrt(mod_cd[i])*max(mod_U_TaRef[i],0.5)   
            ###### calculate wind speed at Tb height (3 x H) -- accounts for stability (unlike log profile)
            UTb=max(ustar/cs.cs['karman']*math.log(z_Hx3/z0m_rur)/math.sqrt(mod_fm[i]),0.01) 
            
            ###### Solve Richardson's number eq for "high temperature" aka Tb_rur 
            #Thi_tb = Symbol('Thi_tb')
            dz = z_Hx3 - z_TaRef
            #Tb_rur = solve(9.806*dz*(Thi_tb-ref_ta)*2.0/(Thi_tb+ref_ta)/(UTb-mod_U_TaRef[i])**2.0-Ri_rur, Thi_tb)[0]
            Tb_rur   = -ref_ta * ( (Ri_rur*(UTb**2.)+ (2.*9.806) * dz) /  (Ri_rur*(UTb**2.) - (2.*9.806) * dz ) )
            Tb_rur = Tb_rur[0]
            Tb_rur = Tb_rur - 9.806/1004.67*dz
            
            ###### Begin calculating modelled variables for 10 different SVF values... 
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
                        
                        mod_data_qh_[i][vf]['Veg'] = eng_bals['Qh']
                        mod_data_qe_[i][vf]['Veg'] = eng_bals['Qe']
                        mod_data_qg_[i][vf]['Veg'] = eng_bals['Qg']
                        mod_data_rn_[i][vf]['Veg'] = rad['Rn']

                        
            counter=-1
            for grid in range(0,len(lc_data)):      # now cycle through each grid point
                hk=+1
                counter+=1
                LC = {}                                 # list with land cover for grid point
                LC['roof']=lc_data['roof'][grid] 
                LC['road']=lc_data['road'][grid]
                LC['watr']=lc_data['watr'][grid] 
                LC['conc']=lc_data['conc'][grid]                
                LC['Veg']=lc_data['Veg'][grid]
                LC['dry']=lc_data['dry'][grid]
                LC['irr']=lc_data['irr'][grid]  
                
                H  = lc_data['H'][grid]         # building height for grid point
                W  = lc_data['W'][grid]         # stree width for grid point 
                           
                if not i == 0:
                    mod_data_ts_[i][9]['Veg'] = mod_rslts[i-1][grid]['Ta']
                else:
                    mod_data_ts_[i][9]['Veg'] = met_d['Ta'][i]
                        
                lc_stuff = lc_sort(LC,H,W,cs)
                
                LC            = lc_stuff['LC']                  # all surfaces not averaged (can be > 1.0)
                LC_woRoofAvg  = lc_stuff['LC_woRoofAvg']        # averaged over the  3D surface excluding roofs (will be = 1.0)
                LC_wRoofAvg   = lc_stuff['LC_wRoofavg']         # average over the total 3D surface (will be = 1.0)
                LC_canyon   = lc_stuff['LC_canyon']         # average over the total 3D surface that interacts with the canyon (i.e not roofs, but the space between roofs)(will be = 1.0)

                
                fw   = lc_stuff['fw']       # interger used to indicate relevant wall SVF
                fg   = lc_stuff['fg']       # interger used to indicate relevant ground SVF
                            
                if cfM['use_obs_ws'] == "Y":                         
                    obs_ws = obs_data['WS_ms_Avg_'+cfM['STa'][counter]]     # observed wind speed file  | users will not used observed wind speed, this is just for testing
                else:
                    obs_ws = []  # create an empty list for obs wind speed  - place filler
                ##################### Aggregate by land cover  ###################
                lc = LC_wRoofAvg
                              
                tS_can =sum([mod_data_ts_['roof'][i][9]*lc['roof'],mod_data_ts_['road'][i][fg]*lc['road'],mod_data_ts_['watr'][i][fg]*lc['watr'],mod_data_ts_['conc'][i][fg]*lc['conc'],mod_data_ts_['Veg'][i][9]*lc['Veg'],mod_data_ts_['dry'][i][fg]*lc['dry'],mod_data_ts_['irr'][i][fg]*lc['irr'],mod_data_ts_['wall'][i][fw]*lc['wall']])    # surface averaged Tsurf of canyon               
                qH =sum([mod_data_qh_['roof'][i][9]*LC['roof'],mod_data_qh_['road'][i][fg]*LC['road'],mod_data_qh_['watr'][i][fg]*LC['watr'],mod_data_qh_['conc'][i][fg]*LC['conc'],mod_data_qh_['Veg'][i][9]*LC['Veg'],mod_data_qh_['dry'][i][fg]*LC['dry'],mod_data_qh_['irr'][i][fg]*LC['irr'],mod_data_qh_['wall'][i][fw]*LC['wall']])      # surface averaged Qh
                qE =sum([mod_data_qe_['roof'][i][9]*LC['roof'],mod_data_qe_['road'][i][fg]*LC['road'],mod_data_qe_['watr'][i][fg]*LC['watr'],mod_data_qe_['conc'][i][fg]*LC['conc'],mod_data_qe_['Veg'][i][9]*LC['Veg'],mod_data_qe_['dry'][i][fg]*LC['dry'],mod_data_qe_['irr'][i][fg]*LC['irr'],mod_data_qe_['wall'][i][fw]*LC['wall']])      # surface averaged Qe
                qG =sum([mod_data_qg_['roof'][i][9]*LC['roof'],mod_data_qg_['road'][i][fg]*LC['road'],mod_data_qg_['watr'][i][fg]*LC['watr'],mod_data_qg_['conc'][i][fg]*LC['conc'],mod_data_qg_['Veg'][i][9]*LC['Veg'],mod_data_qg_['dry'][i][fg]*LC['dry'],mod_data_qg_['irr'][i][fg]*LC['irr'],mod_data_qg_['wall'][i][fw]*LC['wall']])     # surface averaged Qg
                rN =sum([mod_data_rn_['roof'][i][9]*LC['roof'],mod_data_rn_['road'][i][fg]*LC['road'],mod_data_rn_['watr'][i][fg]*LC['watr'],mod_data_rn_['conc'][i][fg]*LC['conc'],mod_data_rn_['Veg'][i][9]*LC['Veg'],mod_data_rn_['dry'][i][fg]*LC['dry'],mod_data_rn_['irr'][i][fg]*LC['irr'],mod_data_rn_['wall'][i][fw]*LC['wall']])     # surface average Rn
                 
                start_time = time.time()
                z = max(H,0.1)
                z0m_urb = 0.1 * z   ## urban roughness length momentum [CHECK]
                z0h_urb = z0m_urb/10.0  ##  urban roughness lenght heat [CHECK]
                 
                #=max(ustar/cs.cs['karman']*math.log(z_Hx3/z0m_urb)/math.sqrt(mod_fm[i]),0.1) #WS above than canyon at 3xH
                #Tb=max(ustar/cs.cs['karman']*math.log(z_Hx3/z0m_rur)/math.sqrt(mod_fm[i]),0.1) 
                Uz_urb_2xH =max(ustar/cs.cs['karman']*math.log(z_Hx2/z0m_rur)/math.sqrt(mod_fm[i]),0.5) # WS above the canyon at 2xH
                Uz_urb_H =max(ustar/cs.cs['karman']*math.log(z/z0m_rur)/math.sqrt(mod_fm[i]),0.5) # WS above canyon at H
                
                Hz = max(z, cs.cs['zavg'])
                        
                Ucan     = UTb*math.exp(-0.386*(Hz/lc_stuff["Wtree"]))	## calculate wind speed in the canyon - [CHECK] which forcing wind speed height should we use for this? currently using 3xH
                rs_can   = (cs.cs['pa']*cs.cs['cpair'])/(11.8+(4.2*Ucan))		# calculate surface resistance (s/m)
                httc_can = 1.0/rs_can  ## heat transfer coefficient for the canyon 
                httc_can = httc_can[0]
                          
                ## begin portion roof Tsurf effects Tac             
                ###### calcualte Ta above roof
                roofTsrf = mod_data_ts_['roof'][i][9]
               
                Ri_roof = sfc_ri(z_Hx3-H-z0m_urb,Tb_rur,roofTsrf,UTb)['Ri']   ## calculate Richardon's number for roof   
               
                dz_roof = z_Hx2 - H
                
                #Thi_tb = Symbol('Thi_tb')                
                #Tb_roof = solve(9.806*dz_roof*(Thi_tb-roofTsrf)*2.0/(Thi_tb+roofTsrf)/(Uz_urb_2xH-Uz_urb_H)**2.0-Ri_roof, Thi_tb)[0]
                Tb_roof   = -roofTsrf * ( (Ri_roof*(Uz_urb_2xH**2.)+ (2.*9.806) * dz_roof) /  (Ri_roof*(Uz_urb_2xH**2.) - (2.*9.806) * dz_roof ) )
                Tb_roof = Tb_roof - 9.806/1004.67*dz_roof
                
                
                ###### calcualte Ta above canyon
                
                if not i == 0:
                    Tacprv=mod_rslts[i-1][grid]['Ta']
                else:
                    Tacprv = met_d['Ta'][i]
                    
                Ri_can = sfc_ri(z_Hx3-H-z0m_urb,Tb_rur,Tacprv,UTb)['Ri']   ## calculate Richardon's number for roof             
                dz_roof = z_Hx2 - H
                
                #Thi_tb = Symbol('Thi_tb')
                #Tb_can = solve(9.806*dz_roof*(Thi_tb-Tacprv)*2.0/(Thi_tb+Tacprv)/(Uz_urb_2xH-Uz_urb_H)**2.0-Ri_can, Thi_tb)[0]
                Tb_can   = -Tacprv * ( (Ri_can*(Uz_urb_2xH**2.)+ (2.*9.806) * dz_roof) /  (Ri_can*(Uz_urb_2xH**2.) - (2.*9.806) * dz_roof ) )
                Tb_can = Tb_can[0]
                Tb_can = Tb_can - 9.806/1004.67*dz_roof

                ##### average Ta above canyon and Ta above roof by canyon and roof fraction
               # Lc = LC_wRoofAvg
                
                LCroof = LC['roof']
                LCcan  = LC['road']+LC['watr']+LC['conc']+LC['Veg']+LC['dry']+LC['irr']
                LChorz  = LCroof+LCcan
                # new Tb temperature - roof and canyon blend 
                Tb_new = ((LCroof/LChorz)*Tb_roof) + ((LCcan/LChorz)*Tb_can)
                
                Ri_new = sfc_ri(z_Hx2-H-z0m_urb,Tb_new,Tacprv,Uz_urb_2xH)['Ri']   ## calculate Richardon's number for roof             
                httc_new = httc(Ri_new,Uz_urb_2xH,z_Hx2-H-z0m_urb,z0m_urb,z0h_urb,met_d,cs,i,Tacprv,Tb_new)       ## calculate httc for Rural site      
                httc_new = httc_new['httc'][0]

                if cfM['use Tb_new'] == 'Y':
                    httc_urb = httc_new
                    Tb = Tb_new
 
                if cfM['use Tb_new'] == 'N': 
                    httc_urb = httc_rur
                    Tb = Tb_rur
                    
         
                Lc = LC
                if cfM['stability'] == 'Y':
               
                    #Tac =  ((mod_data_ts_['conc'][i][fg]*httc_can*Lc['conc']) +  (mod_data_ts_['road'][i][fg]*httc_can*Lc['road']) + (mod_data_ts_['watr'][i][fg]*httc_can*Lc['watr']) + (mod_data_ts_['dry'][i][fg]*httc_can*Lc['dry'])  +  (mod_data_ts_['irr'][i][fg]*httc_can*Lc['irr']) + (mod_data_ts_['wall'][i][fg]*httc_can*Lc['wall'])  + (mod_data_ts_['roof'][i][9]*httc_can*Lc['roof']) + (mod_data_ts_['Veg'][i][9]*httc_can*Lc['Veg']) + (Tb_rur*httc_rur))  / (httc_can + httc_rur)
                    Tac =  ((mod_data_ts_['conc'][i][fg]*httc_can*Lc['conc']) +  (mod_data_ts_['road'][i][fg]*httc_can*Lc['road']) + (mod_data_ts_['watr'][i][fg]*httc_can*Lc['watr']) + (mod_data_ts_['dry'][i][fg]*httc_can*Lc['dry'])  +  (mod_data_ts_['irr'][i][fg]*httc_can*Lc['irr']) + (mod_data_ts_['wall'][i][fg]*httc_can*Lc['wall']) + (mod_data_ts_['roof'][i][9]*httc_can*Lc['roof']) + (mod_data_ts_['Veg'][i][9]*httc_can*Lc['Veg']) + (Tb*httc_urb)) / (httc_urb + httc_can)
                    for_tab     = (lc_data.ix[grid]['FID'],Ucan,tS_can,Tac,dte)   

                
                if cfM['stability'] == 'N':
                    wS_Ta = calc_ta(lc_stuff['Wtree'],H,met_d,cs,qH,tS_can,Dats,cfM,obs_ws,i,Tb_rur,Uz_urb_H)  # dictionary for canopy air temperature and wind speed
                    for_tab     = (lc_data.ix[grid]['FID'],Ucan,tS_can,wS_Ta['Ta_f'],dte) 
                
                #if lc_data['FID'][grid] == 10 :
                    #print 'ID', lc_data['FID'][grid]
                    #print 'Tac', Tac 
                    #print 'Ts_can', tS_can
                    #print 'httc_can', httc_can 
                    #print 'httc_urb', httc_urb 
                    #print 'httc_rur', httc_rur 
                    #print 'httc_new', httc_new
                    
                    # print 'ri_rur', Ri_rur
                    # print "UTb", UTb
                    # print "z_Tb", z_Tb
                    # print "Tb_rur", Tb_rur
                    
                    #print 'z', z 
                    #print 'Uz_can', Uz_can
                    #print 'z0m_urb', z0m_urb
                    #print 'z0h_urb', z0h_urb        
                
                ############################ append everyhing to output table #####
                #print grid
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
    # if cfM['gis_plot'] == 'Y':
        # if confirm('plot GIS for grid?') == True:
            # gis(cfM,mod_rslts,run)
        
## save the control file....      
    inpt1 = open(ControlFileName, 'r')
    outpt1 = open(os.path.join(figdir,'main_control_file.txt'),'w')
    txt1 = inpt1.read()
    outpt1.write(txt1)
    inpt1.close()
    outpt1.close()

## save the constants file..    
    inpt1 = open(os.path.join('.','constants2.py'),'r')
    outpt1 = open(os.path.join(figdir,'constants.txt'),'w')
    txt1 = inpt1.read()
    outpt1.write(txt1)
    inpt1.close()
    outpt1.close()

