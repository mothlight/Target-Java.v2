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
os.chdir(r'Z:\Documents\PhD\Uni\CRC_toolkit\Toolkit2\scripts')
import Tkinter, tkFileDialog
from confirm import confirm
import math
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
    LC_avg = [avg_lc_data['roof'], avg_lc_data['road'], avg_lc_data['watr'], avg_lc_data['conc'], avg_lc_data['Veg'],avg_lc_data['dry'],avg_lc_data['irr']]

    lc_stuff = lc_sort(LC_avg,H_avg,W_avg,cs)
    LC_avg   = lc_stuff['LC']
    LC2_avg  = lc_stuff['LC2']
    LCavg_avg = lc_stuff['LCavg']
    fw_avg   = int(lc_stuff['fw'])
    fg_avg   = int(lc_stuff['fg'])
    
    LC_avg = {}                                 # list with land cover for grid point
    LC_avg['roof']=avg_lc_data['roof']
    LC_avg['road']=avg_lc_data['road']
    LC_avg['watr']=avg_lc_data['watr'] 
    LC_avg['conc']=avg_lc_data['conc']                
    LC_avg['Veg']=avg_lc_data['Veg']
    LC_avg['dry']=avg_lc_data['dry']
    LC_avg['irr']=avg_lc_data['irr'] 
    

    H_avg  = avg_lc_data['H']       # building height for grid point
    W_avg  = avg_lc_data['W']       # stree width for grid point      

    lc_stuff = lc_sort(LC_avg,H_avg,W_avg,cs)

################################### STATT FROM HERE 

    

    
    albedo_avg=[]
    emiss_avg=[]
    LUMPS_avg =[]
    for s in enumerate(surfs[:-1]):
        albedo_avg.append(cs.cs['alb'][s[1]]*LC2_avg[s[0]])
        emiss_avg.append(cs.cs['emis'][s[1]]*LC2_avg[s[0]])
        
    for s in enumerate(["roof","road","conc", "Veg", "dry", "irr",'wall']):    
        tu = cs.cs['LUMPS1'][s[1]]
        tb=[]
        for t in tu:
            tb.append(t*LCavg_avg[s[0]])
        LUMPS_avg.append(tb)
    
    OHM1=[]
    OHM2=[]
    OHM3=[]
    for x in LUMPS_avg:
        OHM1.append(x[0])
        OHM2.append(x[1])
        OHM3.append(x[2])
    OHM1 = np.sum(OHM1)
    OHM2 = np.sum(OHM2)
    OHM3 = np.sum(OHM3)
    LUMPS_avg = [OHM1,OHM2,OHM3]
    
    albedo_avg = np.sum(albedo_avg)
    emiss_avg  = np.sum(emiss_avg)
        
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

    mod_rslts=np.zeros((nt,len(lc_data),1),  np.dtype([('ID',np.int32),('Ws', '<f8'), ('Ts','<f8'), ('Ta','<f8'),('Rn', '<f8'),('Qg', '<f8'), ('Qe','<f8'), ('Qh','<f8'),('date',object)]))   # this is the main data array where surface averaged outputs are stored
    
        
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
            
            if Dats['dte'] == Dats['date1A']:
                # intial values set to 0.
                Rn	   = 0.
                Rnprev = 0.
                Rnnext = 0.
                Rnstar = 0.
            else:    
                Ta_srfp_avg = sum([a*b for a,b in zip(LC2_avg,[mod_data_ts_['roof'][i-3][9],mod_data_ts_['road'][i-3][fg_avg],mod_data_ts_['watr'][i-3][fg_avg],mod_data_ts_['conc'][i-3][fg_avg],mod_data_ts_['Veg'][i-3][9],mod_data_ts_['dry'][i-3][fg_avg],mod_data_ts_['irr'][i-3][fg_avg],mod_data_ts_['wall'][i-3][fw_avg]])])	# "previous" modelled T_surf (3 timesteps back)
                Ta_srf_avg	= sum([a*b for a,b in zip(LC2_avg,[mod_data_ts_['roof'][i-2][9],mod_data_ts_['road'][i-3][fg_avg],mod_data_ts_['watr'][i-2][fg_avg],mod_data_ts_['conc'][i-2][fg_avg],mod_data_ts_['Veg'][i-2][9],mod_data_ts_['dry'][i-2][fg_avg],mod_data_ts_['irr'][i-2][fg_avg],mod_data_ts_['wall'][i-2][fw_avg]])])		# "current" modelled T_Surf (2 time steps back)
                Ta_srfn_avg = sum([a*b for a,b in zip(LC2_avg,[mod_data_ts_['roof'][i-1][9],mod_data_ts_['road'][i-3][fg_avg],mod_data_ts_['watr'][i-1][fg_avg],mod_data_ts_['conc'][i-1][fg_avg],mod_data_ts_['Veg'][i-1][9],mod_data_ts_['dry'][i-1][fg_avg],mod_data_ts_['irr'][i-1][fg_avg],mod_data_ts_['wall'][i-1][fw_avg]])])	# "next" modelled T_Surf (1 time steps back)
                
                Rn_avg	   = met_d['Kd'][i]*(1-albedo_avg)	     + (emiss_avg*(met_d['Ld'][i]	  - (cs.cs['sb']*(Ta_srf_avg+273.15)**4)))	 
                Rnprev_avg = met_d['Kd'][i-1]*(1-albedo_avg)	 + (emiss_avg*(met_d['Ld'][i-1]   - (cs.cs['sb']*(Ta_srfp_avg+273.15)**4)))
                Rnnext_avg = met_d['Kd'][i+1]*(1-albedo_avg)	 + (emiss_avg*(met_d['Ld'][i+1]   - (cs.cs['sb']*(Ta_srfn_avg+273.15)**4)))
                Rnstar_avg  = 0.5*(Rnnext_avg - Rnprev_avg)
                
                
            if Dats['dte'] <= Dats['date1A'] +  timedelta(minutes=(2*int(cfM['timestep']))):
        
                Qh_avg=0.
                Qe_avg=0.
                Qg_avg=0.
                alphapm_avg=0.
                
                Tb_avg = met_d['Ta'][i]
    
            else:
    
                Qg_avg = (LUMPS_avg[0]*Rn_avg) + (LUMPS_avg[1]*Rnstar_avg) + (LUMPS_avg[2])
                ## ALPHA PARAMETER
                alphapm = alphapm_avg
                ##  BETA PARAMETER 
                betA = 3.
                                        
                Lambda =  2.501 - 0.002361*met_d['Ta'][i]                                 # MJ / kg -  latent heat of vaporization

                gamma  = ((met_d['P'][i]/10)*cs.cs['cp']) / (cs.cs['e']*Lambda)                         # kPa / C- psychrometric constant
                ew = 6.1121*(1.0007+3.46e-6*(met_d['P'][i]/10))**((17.502*(met_d['Ta'][i]))/(240.97+(met_d['Ta'][i])))          # in kPa
                s  = 0.62197*(ew/((met_d['P'][i]/10)-0.378*ew)) 

                Qh_avg = ((((1.-alphapm) + gamma/s) / (1. + gamma/s) ) * (Rn_avg-Qg_avg)) - betA
                Qe_avg = (alphapm/(1.+(gamma/s))) * (Rn_avg - Qg_avg) + betA
                
            ############ GEOMETRY CALCS #########################
                if H_avg < cs.cs['zavg']:
                    H_avg = cs.cs['zavg']
                # Height to width ratio
                HW_avg = H_avg/lc_stuff['Wtree']
                # Eq. 3.56 - plan area index (-)
                lambdap_avg = HW_avg / (HW_avg +1)
                # Eq. 3.55 - canopy displacement height (m)
                #dcan = H * (1 + cs.cs['alphadcan']**(-lambdap) * (lambdap - 1))  ## old formulation 
                dcan_avg = (2./3.) * H_avg
                # Eq. 3.58 - frontal area index
                lambdaf_avg = (1.-lambdap_avg) * HW_avg * lambdap_avg 
                # Eq. 3.57 - Roughness lenght for momentum (m)
                #zom = H * (1-(dcan/H)) * math.exp(-(0.5*cs.cs['zomB']*(cs.cs['zomCD']/cs.cs['karman']**2)*(1-(dcan/H))*lambdaf)**(-0.5) )   ## old formulation
                zom_avg = 0.1 * H_avg
                #zom  = cs.cs['z0m'] # using uniform value, defined in constants file.
                # set the height of the top of the canopy (Utop) (z) below:
                z_avg= H_avg  # Utop height 
                if z_avg > 10.:
                    z_avg = 10.  # if buildings are above 10m we assume the BoM reference data is == Utop
                if z_avg < cs.cs['zavg']:
                    z_avg =  cs.cs['zavg']  # if building height is below domain average (set to 4.5) we set z to the domain average 
            ##################################################
            ####### DEFINE REFERENCE WIND SPEED (Uz) #######

                if  met_d['WS'][i]  < 0.75:
                    WSs = 0.75		## if wind speed if 0. it is converted to 0.25 m/s -  [stability is not accounted for in the model]
                    Uz_avg =WSs * ((math.log(z_avg/zom_avg))/(math.log(cs.cs['zref']/zom_avg)))	## convert to wind speed for Utop height using log profile. 
                else:
                    WSs = met_d['WS'][i]
                    Uz = WSs * ((math.log(z_avg/zom_avg))/(math.log(cs.cs['zref']/zom_avg)))  ## convert to wind speed for Utop height using log profile.
            ###############################################
                # calculate the resistance (ra) between canopy layer and atmosphere after Yang et al 2011.     
                ra_avg   = (math.log((cs.cs['zref']-dcan_avg)/zom_avg)*math.log((cs.cs['zref']-dcan_avg)/zom_avg))/(cs.cs['karman']**2 *WSs )  
                # calcuate the above canopy air temperature (Tb)  after Yang et al 2011. 
                Tb_avg   = met_d['Ta'][i] - ((Qh_avg/(cs.cs['pa']*(cs.cs['cp']*1000000)))*ra_avg)

            print Tb_avg
            
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
                        
                        
                        #mod_data_ts_[i][vf]['Veg'] = met_d['Ta'][i]     #  Ts of tree is assumed equal to Ta (see model validation report Figure 3.8 for justification)
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
                    
                LC = LC
                LC2 = LC_wRoofAvg
                
                tS_can =sum([mod_data_ts_['roof'][i][9]*lc['roof'],mod_data_ts_['road'][i][fg]*lc['road'],mod_data_ts_['watr'][i][fg]*lc['watr'],mod_data_ts_['conc'][i][fg]*lc['conc'],mod_data_ts_['Veg'][i][9]*lc['Veg'],mod_data_ts_['dry'][i][fg]*lc['dry'],mod_data_ts_['irr'][i][fg]*lc['irr'],mod_data_ts_['wall'][i][fw]*lc['wall']])    # surface averaged Tsurf of canyon               
                qH =sum([mod_data_qh_['roof'][i][9]*LC['roof'],mod_data_qh_['road'][i][fg]*LC['road'],mod_data_qh_['watr'][i][fg]*LC['watr'],mod_data_qh_['conc'][i][fg]*LC['conc'],mod_data_qh_['Veg'][i][9]*LC['Veg'],mod_data_qh_['dry'][i][fg]*LC['dry'],mod_data_qh_['irr'][i][fg]*LC['irr'],mod_data_qh_['wall'][i][fw]*LC['wall']])      # surface averaged Qh
                qE =sum([mod_data_qe_['roof'][i][9]*LC['roof'],mod_data_qe_['road'][i][fg]*LC['road'],mod_data_qe_['watr'][i][fg]*LC['watr'],mod_data_qe_['conc'][i][fg]*LC['conc'],mod_data_qe_['Veg'][i][9]*LC['Veg'],mod_data_qe_['dry'][i][fg]*LC['dry'],mod_data_qe_['irr'][i][fg]*LC['irr'],mod_data_qe_['wall'][i][fw]*LC['wall']])      # surface averaged Qe
                qG =sum([mod_data_qg_['roof'][i][9]*LC['roof'],mod_data_qg_['road'][i][fg]*LC['road'],mod_data_qg_['watr'][i][fg]*LC['watr'],mod_data_qg_['conc'][i][fg]*LC['conc'],mod_data_qg_['Veg'][i][9]*LC['Veg'],mod_data_qg_['dry'][i][fg]*LC['dry'],mod_data_qg_['irr'][i][fg]*LC['irr'],mod_data_qg_['wall'][i][fw]*LC['wall']])     # surface averaged Qg
                rN =sum([mod_data_rn_['roof'][i][9]*LC['roof'],mod_data_rn_['road'][i][fg]*LC['road'],mod_data_rn_['watr'][i][fg]*LC['watr'],mod_data_rn_['conc'][i][fg]*LC['conc'],mod_data_rn_['Veg'][i][9]*LC['Veg'],mod_data_rn_['dry'][i][fg]*LC['dry'],mod_data_rn_['irr'][i][fg]*LC['irr'],mod_data_rn_['wall'][i][fw]*LC['wall']])     # surface average Rn
                 
                ##################### CALC air temperature ########################
                wS_Ta = calc_ta(lc_stuff['Wtree'],H,met_d,cs,qH,tS,Dats,cfM,obs_ws,i,Tb_avg)  # dictionary for canopy air temperature and wind speed
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
    inpt1 = open(os.path.join('.','constants2.py'),'r')
    outpt1 = open(os.path.join(figdir,'constants.txt'),'w')
    txt1 = inpt1.read()
    outpt1.write(txt1)
    inpt1.close()
    outpt1.close()

