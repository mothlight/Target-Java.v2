# -*- coding: utf-8 -*-
"""
Created on Wed Nov 23 13:21:14 2016

@author: ambro17
"""
import datetime
import numpy as np
import pandas 

def do_spinup(cfM):
    
    date1=datetime.datetime(2011,1,14,0)
    date2=datetime.datetime(2011,2,13,23)
    tD = date2-date1
    date_range = pandas.date_range(date1,date2,freq='1H') 
    nt=divmod(tD.days * 86400 + tD.seconds, (60*int(tmstp)))[0] # number of timesteps

    met_file = os.path.join('..','input',cfM['site_name'],'MET',cfM['inpt_met_file'])    # input meteorological forcing data file 
    met_data = pandas.read_csv(met_file,parse_dates=['datetime'], date_parser=dateparse,index_col=['datetime']) # convert to data frame
    met_data_all = met_data.ix[date1A:date2]   # main forcing meteorological dataframe (including spin up)
    met_data_all = met_data_all.interpolate(method='time') # interpolates forcing data 
    
    ########## DEFINE MAIN DATAFRAME ####################  dataframe for different modelled variables     
    mod_data_ts_=np.zeros((nt,10), np.dtype([('wall','<f8'),('roof','<f8'), ('road','<f8'), ('watr','<f8'), ('conc','<f8'), ('Veg','<f8'), ('dry','<f8'), ('irr','<f8'), ('TSOIL','<f8'), ('avg','<f8'),('date',object)]))   # surface temperature of each surface    
    mod_data_tm_=np.zeros((nt,10), np.dtype([('wall','<f8'),('roof','<f8'), ('road','<f8'), ('watr','<f8'), ('conc','<f8'), ('Veg','<f8'), ('dry','<f8'), ('irr','<f8'), ('TSOIL','<f8'), ('avg','<f8'),('date',object)]))   # ground temperature of each surface
    mod_data_qh_=np.zeros((nt,10), np.dtype([('wall','<f8'),('roof','<f8'), ('road','<f8'), ('watr','<f8'), ('conc','<f8'), ('Veg','<f8'), ('dry','<f8'), ('irr','<f8'), ('TSOIL','<f8'), ('avg','<f8'),('date',object)]))   # sensible heat flux of each surface
    mod_data_qe_=np.zeros((nt,10), np.dtype([('wall','<f8'),('roof','<f8'), ('road','<f8'), ('watr','<f8'), ('conc','<f8'), ('Veg','<f8'), ('dry','<f8'), ('irr','<f8'), ('TSOIL','<f8'), ('avg','<f8'),('date',object)]))    # latent heat flux of each surface
    mod_data_qg_=np.zeros((nt,10), np.dtype([('wall','<f8'),('roof','<f8'), ('road','<f8'), ('watr','<f8'), ('conc','<f8'), ('Veg','<f8'), ('dry','<f8'), ('irr','<f8'), ('TSOIL','<f8'), ('avg','<f8'),('date',object)]))    # storage heat flux of each surface
    mod_data_rn_=np.zeros((nt,10), np.dtype([('wall','<f8'),('roof','<f8'), ('road','<f8'), ('watr','<f8'), ('conc','<f8'), ('Veg','<f8'), ('dry','<f8'), ('irr','<f8'), ('TSOIL','<f8'), ('avg','<f8'),('date',object)]))    # net radiation of each surface

    #mod_rslts = pandas.DataFrame(index=date_range1A, columns=['ID','Ws','Ts','Ta','Rn', 'Qg','Qe'])
    ln=len(met_data_all)
    hk=-1
    for i in range(0, len(met_data_all)):
        if not i == ln-1: 
            ############ Met variables for each time step (generate dataframe) ##########
            dte   = date1A
            dte   = dte + timedelta(minutes=(i*int(tmstp)))
            Dats['dte'] = dte
            met_d = met_data_all
            print dte, i
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