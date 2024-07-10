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

    met_file = "../input/"+cfM['site_name']+'/'+cfM['inpt_met_file']
    met_data = pandas.read_csv(met_file,parse_dates=['datetime'], date_parser=dateparse,index_col=['datetime'])
    met_data_all = met_data.ix[date1:date2]   # actual siumation df (including spin up)
    met_data_all = met_data_all.interpolate(method='time')              # validation date range
    
    ########## DEFINE MAIN DATAFRAME ####################    
    mod_data_ts_=np.zeros((nt), np.dtype([('roof','<f8'), ('road','<f8'), ('watr','<f8'), ('conc','<f8'), ('Veg','<f8'), ('dry','<f8'), ('irr','<f8'), ('TSOIL','<f8'), ('avg','<f8'),('date',object)]))
    mod_data_tm_=np.zeros((nt), np.dtype([('roof','<f8'), ('road','<f8'), ('watr','<f8'), ('conc','<f8'), ('Veg','<f8'), ('dry','<f8'), ('irr','<f8'), ('TSOIL','<f8'), ('avg','<f8'),('date',object)]))
    mod_data_qh_=np.zeros((nt), np.dtype([('roof','<f8'), ('road','<f8'), ('watr','<f8'), ('conc','<f8'), ('Veg','<f8'), ('dry','<f8'), ('irr','<f8'), ('TSOIL','<f8'), ('avg','<f8'),('date',object)]))
    mod_data_qe_=np.zeros((nt), np.dtype([('roof','<f8'), ('road','<f8'), ('watr','<f8'), ('conc','<f8'), ('Veg','<f8'), ('dry','<f8'), ('irr','<f8'), ('TSOIL','<f8'), ('avg','<f8'),('date',object)]))
    mod_data_qg_=np.zeros((nt), np.dtype([('roof','<f8'), ('road','<f8'), ('watr','<f8'), ('conc','<f8'), ('Veg','<f8'), ('dry','<f8'), ('irr','<f8'), ('TSOIL','<f8'), ('avg','<f8'),('date',object)]))
    mod_data_rn_=np.zeros((nt), np.dtype([('roof','<f8'), ('road','<f8'), ('watr','<f8'), ('conc','<f8'), ('Veg','<f8'), ('dry','<f8'), ('irr','<f8'), ('TSOIL','<f8'), ('avg','<f8'),('date',object)]))

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
            for surf in surfs:
                if surf != 'watr':                                  
                    #################### radiation balance ###########################
                    rad  = rn_calc(cs,cfM,met_d,surf,Dats,mod_data_ts_,i)
                    ##################### ENG BALANCE non-water #######################
                    eng_bals=LUMPS(rad,cs,cfM,met_d,surf,Dats,i)
                    ##################### CALC LST non-water #########################
                    Ts_stfs =Ts_calc_surf_G(eng_bals,cs,cfM,mod_data_ts_,mod_data_tm_, Dats,surf,i)
                    ################################################################################
                    mod_data_ts_[i][surf] = Ts_stfs['TS']
                    mod_data_tm_[i][surf] = Ts_stfs['TM']                 
                    mod_data_qh_[i][surf] = eng_bals['Qh']
                    mod_data_qe_[i][surf] = eng_bals['Qe']
                    mod_data_qg_[i][surf]= eng_bals['Qg']
                    mod_data_rn_[i][surf] = rad['Rn']
                    
                if surf == 'watr':
                    radW  = rn_calc(cs,cfM,met_d,surf,Dats,mod_data_ts_,i)
                    wtr_stf = Ts_EB_W_G(met_d,cs,cfM,mod_data_ts_,mod_data_tm_,Dats,i,radW)
                    mod_data_ts_[i][surf] = wtr_stf['TsW']
                    mod_data_tm_[i][surf] = wtr_stf['TM']                 
                    mod_data_qh_[i][surf]= wtr_stf['QhW']
                    mod_data_qe_[i][surf] = wtr_stf['QeW']
                    mod_data_qg_[i][surf]= wtr_stf['QgW']
                    mod_data_ts_[i]['TSOIL'] = wtr_stf['TSOIL']
                    mod_data_rn_[i][surf] = rad['Rn']   