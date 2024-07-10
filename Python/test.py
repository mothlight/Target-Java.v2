# -*- coding: utf-8 -*-


#from plotting import val_ta, gis
import sys
from configobj import ConfigObj
import pandas
import os
import numpy as np

#from datetime import datetime
#from datetime import timedelta

import datetime as dt
from datetime import datetime, timedelta
import matplotlib.pylab as mp
from scipy.stats import stats 
from matplotlib.pyplot import cm
import itertools  

def gis(cfM,mod_rslts,run):

    import arcpy
    from arcpy.sa import *
    
## make this do day and night
    
    daTes = [datetime(2011, 2, 15, 3, 0),datetime(2011, 2, 16, 15, 0)]
    #ax1 = fig.add_subplot(121)
    #ax2 = fig.add_subplot(122)
    c=-1
    for prd in ['night','day']: 
        fig = mp.figure(1,figsize=(3, 3), dpi=300)

        c+=1
        arcpy.env.workspace = r'..\GIS'+'\\'+cfM['site_name']+'\\'+'mod'+'\\'+run
        arcpy.env.overwriteOutput = True
        arcpy.CheckOutExtension("Spatial")
    
        in_fshnt = r'..\GIS'+'\\'+cfM['site_name']+'\\'+'obs'+'\\'+"empty"+'\\'+cfM['inpt_grid_file']
 
        obs_ras = r'..\GIS\Mawson\obs\MAWSON_LST'+'\\'+prd+'\\'+'tsfnl30.txt'
        obsN =    r'..\GIS\Mawson\obs\MAWSON_LST'+'\\'+prd+"\\"+'tsfnl30'

    
        mod_rslts1 = mod_rslts[mod_rslts['date'] == daTes[c]]
        if not os.path.exists(r'..\GIS'+'\\'+cfM['site_name']+'\\'+'mod'+'\\'+run):
            os.makedirs(r'..\GIS'+'\\'+cfM['site_name']+'\\'+'mod'+'\\'+run)
        out_fshntN = r'..\GIS'+'\\'+cfM['site_name']+'\\'+'mod'+'\\'+run+'\\'+'Grid'+prd[0]+'.shp'
    #                                  
        arcpy.CopyFeatures_management(in_fshnt, out_fshntN)                            
        arcpy.da.ExtendTable(out_fshntN,"FID",mod_rslts1,"ID")
    # 
        shapeN = out_fshntN
        out_rasN = r'..\GIS'+'\\'+cfM['site_name']+'\\'+'mod'+'\\'+run+'\\'+prd[0]+'_Ts'
        out_rasN_Ta = r'..\GIS'+'\\'+cfM['site_name']+'\\'+'mod'+'\\'+run+'\\'+prd[0]+'_Ta'   
    #    
        arcpy.PolygonToRaster_conversion(in_features=shapeN, value_field="Ts", out_rasterdataset=out_rasN, cell_assignment="CELL_CENTER", priority_field="NONE", cellsize="30")
        arcpy.PolygonToRaster_conversion(in_features=shapeN, value_field="Ta", out_rasterdataset=out_rasN_Ta, cell_assignment="CELL_CENTER", priority_field="NONE", cellsize="30")
        
        #arcpy.PolygonToRaster_conversion(in_features=shapeN, value_field="Ts", out_rasterdataset=r"in_memory\poly", cell_assignment="CELL_CENTER", priority_field="NONE", cellsize="30")
        #arcpy.PolygonToRaster_conversion(in_features=shapeN, value_field="Ta", out_rasterdataset=r"in_memory\poly", cell_assignment="CELL_CENTER", priority_field="NONE", cellsize="30")

        #obsN = r'..\GIS\Mawson\obs\MAWSON_LST'+'\\'+prd+"\\"+'tsfnl30'
        
        new_mod_tsN = SetNull(IsNull(obsN),out_rasN)
        new_mod_taN = SetNull(IsNull(obsN),out_rasN_Ta)
    #
        new_mod_tsN.save( r'..\GIS'+'\\'+cfM['site_name']+'\\'+'mod'+'\\'+run+'\\'+'30mFn'+prd[0]+'_Ts')
        new_mod_taN.save( r'..\GIS'+'\\'+cfM['site_name']+'\\'+'mod'+'\\'+run+'\\'+'30mFn'+prd[0]+'_Ta')
    #
    # 
        mod_rasN = arcpy.RasterToASCII_conversion(r'..\GIS'+'\\'+cfM['site_name']+'\\'+'mod'+'\\'+run+'\\'+'30mFn'+prd[0]+'_Ts', r'..\GIS'+'\\'+cfM['site_name']+'\\'+'mod'+'\\'+run+'\\'+'30mFn'+prd[0]+'_Ts.txt')
    #      
        file_m = open(r'..\GIS'+'\\'+cfM['site_name']+'\\'+'mod'+'\\'+run+'\\'+'30mFn'+prd[0]+'_Ts.txt', 'r')
        file_o = open(obs_ras, 'r')
     
        obs=[]
        mod=[]
        
        for line in file_m:
            mod.append(line.split(' '))
        
        
        for line in file_o:
            obs.append(line.split(' '))
        
        mod = mod[6:]
        obs = obs[6:]
        
        
        mod=list(itertools.chain(*mod[0:]))
        obs=list(itertools.chain(*obs[0:]))
        
        
        mod1 = []
        obs1 = []
        for i in range(0, len(obs)):
            if obs[i] != '-9999': 
                    if (obs[i] !='\n') :
                          if (mod[i] !='\n'):    
                                      mod1.append(float(mod[i]))
                                      obs1.append(float(obs[i]))
        
        b, a, r_value, p_value, std_err = stats.linregress(obs1,mod1)
        ei  = [x-y for x,y, in zip(mod1,obs1)]
        r_value = round(r_value,1)
        rmse = round(np.sqrt(sum(np.transpose(ei)*ei)/len(obs1)),1)   
        
        x=[-100,100]
        y=[-100,100]
        
        ax1 = fig.add_subplot(111)
        ax1.scatter(obs1,mod1,alpha=0.4)
        
        ax1.set_xlim((  (min(obs1)*0.95), (max(obs1)*1.1)  ))
        ax1.set_ylim((  (min(obs1)*0.95), (max(obs1)*1.1)  ))
        ax1.text((min(obs1)*1.1),(max(obs1)*0.95), 'RMSE='+str(rmse))
        ax1.text((min(obs1)*1.1),(max(obs1)*0.9), '$r^2$='+str(r_value))
        ax1.text((min(obs1)*1.1),(max(obs1)*0.85), '$n$='+str(len(obs1)))
#        if prd == 'night':
#            ax1.text(5.5,5.5, '(a)')
#        if prd == 'day':
#            ax1.text(18.5,18.5, '(b)')


        ax1.set_xlabel('Observed $T_{surf}$ ($^\circ$C)')
        ax1.set_ylabel('Modelled $T_{surf}$ ($^\circ$C)')
        ax1.plot(x,y, 'k--')
        
        if not os.path.exists(r'..\plots'+'\\'+cfM['site_name']+'\\'+run):
            os.makedirs(r'..\plots'+'\\'+cfM['site_name']+'\\'+run)
        fig.tight_layout(pad=0.4,w_pad=0.5,h_pad=1.0)
        fig.savefig(r'..\plots'+'\\'+cfM['site_name']+'\\'+run+'\\'+prd+'_scatter.png') 
        fig.clf()
        ax1.cla()
        mp.close('all') 

def val_ts(cfM,run,stations,mod_rslts):
                   
            run = cfM['run_name']
            radi = cfM['radius']  
            color=cm.rainbow(np.linspace(0,1,len(stations)))            


    ############# Ts_validation for AWS ##############################
            Ts_obs_file1 = os.path.join('..','obs',cfM['site_name'],'stations_LST',cfM['Ts_prd1'])
            Ts_obs1 = pandas.read_csv(os.path.join(Ts_obs_file1,radi,radi+'-radius_Ts_obs.csv'))
            
            Ts_obs_file2 = os.path.join('..','obs',cfM['site_name'],'stations_LST',cfM['Ts_prd2'])
            Ts_obs2 = pandas.read_csv(os.path.join(Ts_obs_file2,radi,radi+'-radius_Ts_obs.csv'))
            
            
            Tsfigdir = os.path.join('..','plots',cfM['site_name'],run)
            if not os.path.exists(Tsfigdir):
                os.makedirs(Tsfigdir)
            
            ts_min1 = min(Ts_obs1['Ts_obs'])
            ts_max1 = max(Ts_obs1['Ts_obs'])
            ts_min2 = min(Ts_obs2['Ts_obs'])
            ts_max2 = max(Ts_obs2['Ts_obs'])
            	
            Ts_obs1 = Ts_obs1[~(Ts_obs1['station']==7)&~(Ts_obs1['station']==14)&~(Ts_obs1['station']==15)]
            Ts_obs2 = Ts_obs2[~(Ts_obs2['station']==7)&~(Ts_obs2['station']==14)&~(Ts_obs2['station']==15)]
            
            date1Ts1 = datetime(int(cfM['date1Ts1'][0]), int(cfM['date1Ts1'][1]),int(cfM['date1Ts1'][2]), int(cfM['date1Ts1'][3]))
            date1Ts2 = datetime(int(cfM['date1Ts2'][0]), int(cfM['date1Ts2'][1]),int(cfM['date1Ts2'][2]), int(cfM['date1Ts2'][3]))
            
            date2Ts1 = datetime(int(cfM['date2Ts1'][0]), int(cfM['date2Ts1'][1]),int(cfM['date2Ts1'][2]), int(cfM['date2Ts1'][3]))
            date2Ts2 = datetime(int(cfM['date2Ts2'][0]), int(cfM['date2Ts2'][1]),int(cfM['date2Ts2'][2]), int(cfM['date2Ts2'][3]))
            
            figTs1 = mp.figure(num=112,figsize=(8.,3.), dpi=300)
            #figTs2 = mp.figure(200)
            
            axTs1 = figTs1.add_subplot(121)
            axTs2 = figTs1.add_subplot(122)
                        
            Ts1_o=[] ## defining some cummulative lists
            Ts1_m=[] ## defining some cummulative lists
            Ts2_o=[]
            Ts2_m=[] ## defining some cummulative lists
            
            cnt=-1
            for st in stations:
                cnt+=1
                mod_station = mod_rslts[mod_rslts['ID']==st]
                mod_dates1 = mod_station[(mod_station['date'] >= date1Ts1) & (mod_station['date'] <= date1Ts2)]
                modTs1=(np.mean(mod_dates1['Ts']))
                
                mod_dates2 = mod_station[(mod_station['date'] >= date2Ts1) & (mod_station['date'] <= date2Ts2)]
                modTs2=(np.mean(mod_dates2['Ts']))
                st_name = st
                    
     
                ts_obs_plt1 = Ts_obs1[Ts_obs1['station']==int(st)]
                ts_obs_plt2 = Ts_obs2[Ts_obs2['station']==int(st)]

                print (ts_obs_plt1['Ts_obs'])
                
                axTs1.scatter(float(ts_obs_plt1['Ts_obs']),modTs1,c=color[cnt],label=st_name)
                axTs2.scatter(float(ts_obs_plt2['Ts_obs']),modTs2,c=color[cnt],label=st_name)
        
                axTs1.text(float(ts_obs_plt1['Ts_obs']),modTs1,st_name, fontsize=10)
                axTs2.text(float(ts_obs_plt2['Ts_obs']),modTs2,st_name, fontsize=10) 
                
                Ts1_o.append(float(ts_obs_plt1['Ts_obs']))
                Ts1_m.append(modTs1)
        
                Ts2_o.append(float(ts_obs_plt2['Ts_obs']))
                Ts2_m.append(modTs2)
                
            axTs1.set_ylim((ts_min1)-(0.1*ts_min1) ,ts_max1+(0.1*ts_max1))
            axTs2.set_ylim((ts_min2)-(0.1*ts_min2) ,ts_max2+(0.1*ts_max2))
            
            axTs1.set_xlim((ts_min1)-(0.1*ts_min1) ,ts_max1+(0.1*ts_max1))
            axTs2.set_xlim((ts_min2)-(0.1*ts_min2) ,ts_max2+(0.1*ts_max2))
            
            axTs1.plot([10, 75],[10,75], "k--")
            axTs2.plot([10, 75],[10,75], "k--")
                
            axTs1.set_xlabel(u'observed $T_{surf}$ (°C)')
            axTs1.set_ylabel(u'modelled $T_{surf}$ (°C)')
            
            axTs2.set_xlabel(u'observed $T_{surf}$ (°C)')
            
            
            slope1, intercept1, r_value1, p_value1, std_err1 = stats.linregress(Ts1_o,Ts1_m)
            slope2, intercept2, r_value2, p_value2, std_err2 = stats.linregress(Ts2_o,Ts2_m)
            
            ei1  = [x-y for x,y, in zip(Ts1_o,Ts1_m)]
            ei2  = [x-y for x,y, in zip(Ts2_o,Ts2_m)]
            mae1=abs(np.mean(ei1))
            mae2=abs(np.mean(ei2))
            rmse1 = round(np.sqrt(sum(np.transpose(ei1)*ei1)/len(Ts1_m)),1)
            rmse2 = round(np.sqrt(sum(np.transpose(ei2)*ei2)/len(Ts2_m)),1)
            
            axTs1.text(ts_max1*0.98,ts_min1+(0.08*ts_max1), 'r2='+str(round(r_value1,2)))
            axTs1.text(ts_max1*0.98,ts_min1+(0.05*ts_max1), 'rmse='+str(round(rmse1,2)))
            axTs1.text(ts_max1*0.98,ts_min1+(0.02*ts_max1), 'mae='+str(round(mae1,2)))
            
            axTs2.text(ts_max2*0.98,ts_min2+(0.06*ts_max2), 'r2='+str(round(r_value2,2)))
            axTs2.text(ts_max2*0.98,ts_min2+(0.03*ts_max2), 'rmse='+str(round(rmse2,2)))
            axTs2.text(ts_max2*0.98,ts_min2+(0.00*ts_max2), 'mae='+str(round(mae2,2)))
            axTs1.text(29,29, '(a)')
            axTs2.text(14,14, '(b)')
        
            
                    #ax2.axhline(float(ts_obs_plt1['Ts_obs']), color='red')
                    #ax2.axhline(float(ts_obs_plt2['Ts_obs']), color='orange')
            figTs1.tight_layout(pad=0.4,w_pad=0.5,h_pad=1.0)
            figTs1.savefig(os.path.join(Tsfigdir,'TS_for_AWS_'+cfM['run_name']))
            axTs1.cla()
            axTs2.cla()
            figTs1.clf()
            mp.close('all') 


def val_ta(cfM,met_data,stations,obs_data,mod_rslts,Dats):
            run = cfM['run_name']

            figdir= os.path.join('..','plots',cfM['site_name'],run)
            if not os.path.exists(figdir):
                os.makedirs(figdir)
    
            date1=Dats['date1']
            date2=Dats['date2']
            date_range = Dats['date_range']
            radi = cfM['radius']  
            color=cm.rainbow(np.linspace(0,1,len(stations)))
                  
            nfig=6
            mfig  = mp.figure(1,figsize=(6.,6.), dpi=300)
            mfig1 = mp.figure(2,figsize=(6.,6.), dpi=300)
            figbp = mp.figure(3,figsize=(8.,4.), dpi=300)
            figbp2= mp.figure(4,figsize=(8.,4.), dpi=300)
            #width = 0.35
            
    
            ws_min = min(met_data['WS'])
            ws_max = max(met_data['WS'])
            ta_min = min(met_data['Ta'])
            ta_max = max(met_data['Ta'])
    
            ax_ta = mfig.add_subplot(111)
            ax_ws = mfig1.add_subplot(111)
            ax_bp = figbp.add_subplot(111)
            ax_bp1 = figbp2.add_subplot(111)
            #ax_scat = figscat.add_subplot(111)
            #sax_scata = figscat_a.add_subplot(111)
    
            
            clrs=['green','blue']*len(stations)
            clrs2=['green','green','blue','blue']*len(stations) 
    
            Ta_o=[]
            Ta_m=[]
            
        
            data2plt=[]
            xticks=[]
            xtick2=[]
#            xlabs =[]
            locs=[]
#            stat_delta=[]
#            stat_imperv=[]
#            stat_conc=[]
#            stat_roof=[]
            cnt=-1
            c=0
            STa = ['01','02','03','04','05','06','07','08','09','10','11','12','13','16','17','18','19','20','21','22','23','24','25','26','27','28','29','30']
            for st in stations:
                
                nfig = nfig + 1
                fig = mp.figure(nfig,figsize=(8.,8.), dpi=300)
                ax1 = fig.add_subplot(331)
                ax2 = fig.add_subplot(332)
                ax3 = fig.add_subplot(333)
                ax4 = fig.add_subplot(334)
                ax5 = fig.add_subplot(335)
                ax6 = fig.add_subplot(336)
                ax7 = fig.add_subplot(337)
                            
                cnt+=1
                c+=1
                st_name = st
                
                d=(c*8+c)
                e=(c*4+c)
                
                ind = [(d-1.5),d]
                locs.append(e-1.5)
                locs.append(e)
                xticks.append(np.mean(ind))
                xtick2.append(np.mean([(e-1.5),e]))
                
#                obs_station = obs_data[obs_data['station']==int(st)]  		## index to station 
#                obs_station1 = obs_station.ix[date1:date2]        		## index obs data to dates
#                obs_station1 = obs_station1.reindex(date_range,method='ffill')  	## nan out missing values
#                obs_station1 = obs_station1.ix[date1:date2-timedelta(minutes=30)]    ## index obs data to dates         
                        
                obs_data1 = obs_data.ix[date1:date2]        		## index obs data to dates
                obs_data1 = obs_data1.reindex(date_range,method='ffill')  	## nan out missing values
                obs_data1 = obs_data1.ix[date1:date2-timedelta(minutes=30)]    ## index obs data to dates         
                                                
    #
                mod_rslts1 = mod_rslts[(mod_rslts['date'] >= date1) & (mod_rslts['date'] <= date2)]
                mod_rslts2 = mod_rslts1[mod_rslts1['ID']==int(st)]
                    
                obs_dataM = obs_data1[pandas.notnull(obs_data1['AirTC_Avg_'+STa[cnt]]) & pandas.notnull(mod_rslts2['Ta'])]
                
#                obs_stationM = obs_station1[pandas.notnull(obs_station1['Ta']) & pandas.notnull(mod_rslts2['Ta'])]  	## define array with NaN values removed for stats
                mod_rsltsM   = mod_rslts2[~np.isnan(obs_data1['AirTC_Avg_'+STa[cnt]]).values  & ~np.isnan(mod_rslts2['Ta'])] 	## define array with NaN values removed for stats
    
                ax1.plot(list(obs_data1['AirTC_Avg_'+STa[cnt]]))	### plot time series of observed air temperature
                ax1.plot(list(mod_rslts2['Ta']), ls='--') ### plot times series modelled air temperature
                ax2.plot(list(mod_rslts2['Ts']))		### plot times series modelled surface temperature
                
                ax3.plot(list(obs_data1['WS_ms_Avg_'+STa[cnt]]))
                ax3.plot(list(mod_rslts2['Ws']), ls='--')   ### plot times series wind speed
                ax4.plot(list(mod_rslts2['Qh']))
                ax5.plot(list(mod_rslts2['Qe']))
                ax6.plot(list(mod_rslts2['Qg']))
                ax7.plot(list(mod_rslts2['Rn']))
                
                ax1.set_title('station '+str(st))
                ax1.set_ylabel(u'$T_{ac}$ (°C)')
                ax1.set_ylim(10,65)
                ax2.set_ylim(10,65)
                ax2.set_ylabel(u'$T_{surf}$ (°C)')
                ax3.set_ylabel('WS (m/s)')
                ax3.set_ylim(ws_min -3  ,ws_max+(0.15*ws_max))
    
                ax4.set_ylim(-100 ,500)
                ax4.set_ylabel(u'$H$ $(Wm^{2})$')
                ax5.set_ylabel(u'$LE$ $(Wm^{2})$')
                ax5.set_ylim(-100 ,200)
                ax6.set_ylabel(u'$G$ $(Wm^{2})$')
                ax6.set_ylim(-100 ,500)
                ax7.set_ylabel(u'$R_{n}$ $(Wm^{2})$')
                ax7.set_ylim(-100 ,800)
                
                data2plt.append(list(obs_data1['AirTC_Avg_'+STa[cnt]])) ## cummulative list
                data2plt.append(list(mod_rslts2['Ta']))
                
                Ta_m.append(mod_rsltsM['Ta'])	## cummulative list
                Ta_o.append(obs_dataM['AirTC_Avg_'+STa[cnt]])
                				
                ax_ta.scatter(obs_data1['AirTC_Avg_'+STa[cnt]],mod_rslts2['Ta'], c=color[cnt],label=st_name, alpha=0.4 )
                ax_ws.scatter(obs_data1['WS_ms_Avg_'+STa[cnt]],mod_rslts2['Ws'], c=color[cnt],label=st_name, alpha = 0.4 )
    
                avgminM=[]
                avgmaxM=[]
                avgminO=[]
                avgmaxO=[]
                
                days = np.unique(obs_dataM.index.dayofyear)	
                
                for dy in days:
                    arry  = mod_rsltsM[np.where(obs_dataM.index.dayofyear == dy)]
                    arry1 = obs_dataM[obs_dataM.index.dayofyear == dy]
                    if len(arry)>0:
                        avgminM.append(np.min(arry['Ta']))
                        avgmaxM.append(np.max(arry['Ta']))
                        avgminO.append(np.min(arry1['AirTC_Avg_'+STa[cnt]]))
                        avgmaxO.append(np.max(arry1['AirTC_Avg_'+STa[cnt]]))
                
                means=[np.mean(obs_dataM['AirTC_Avg_'+STa[cnt]]),np.mean(mod_rsltsM['Ta'])]
                maxs =[np.mean(avgmaxO),np.mean(avgmaxM)]
                mins=[np.mean(avgminO),np.mean(avgminM)]
                
                mns_obs = means[0] - mins[0]
                mns_mod = means[1] - mins[1]
                
                mxs_obs = maxs[0]-means[0] 
                mxs_mod = maxs[1]-means[1] 
                
                ax_bp.errorbar(ind[0], means[0], fmt='or')
                ax_bp.errorbar(ind[1], means[1], fmt='ob')
                ax_bp.errorbar(ind[0], means[0], yerr=[[mns_obs], [mxs_obs]], fmt=' ', ecolor='red')
                ax_bp.errorbar(ind[1], means[1], yerr=[[mns_mod], [mxs_mod]], fmt=' ', ecolor='blue')                #boxO = ax_bp1.boxplot(obs_stationM['Ta'].values,  positions=ind[0], widths =1)
                #boxM = ax_bp1.boxplot  (mod_rsltsM['Ta'].values, positions=ind[1], widths =1)
                							
                
    #            stat_delta.append(means[1]-means[0])
    #            stat_imperv.append((lc[0]+lc[1]+lc[3]/sum(lc)))
    #            stat_conc.append(lc[1]+lc[3]/sum(lc))
    #            stat_roof.append(lc[0]/sum(lc))
                
                ax1.text(2,48.0,"avg 24h o|m = "+str(round(np.mean(obs_dataM['AirTC_Avg_'+STa[cnt]]),1))+' | '+str(round(np.mean(mod_rsltsM['Ta']),1)),fontsize=8)
                ax1.text(2,45.0,"avg min o|m = "+str(round(np.mean(avgminO),1))+' | '+str(round(np.mean(avgminM),1)),fontsize=8)
                ax1.text(2,42.0,"avg max o|m = "+str(round(np.mean(avgmaxO),1))+' | '+str(round(np.mean(avgmaxM),1)),fontsize=8)
                fig.tight_layout(pad=0.4,w_pad=0.5,h_pad=1.0)
                fig.savefig(figdir+'//'+radi+'_station_'+str(st)+'.png')
                
                ax1.cla()
                ax2.cla()
                ax3.cla()
                ax4.cla()
                ax6.cla()
                ax7.cla()
    
                fig.clf()
                
            box=ax_bp1.boxplot(data2plt,vert=1, positions=locs)
    #				avgs_bp = [np.mean(mn) for mn in data2plt]
    
            for patch, color in zip(box['boxes'], clrs):
                patch.set_color(color)
            for whisk, color in zip(box['whiskers'], clrs2):
                whisk.set_color(color)
            for cap, color in zip(box['caps'], clrs2):
                cap.set_color(color)
                cap.set_linewidth(1.5)
            for med in box['medians']:
                med.set_linewidth(1.5)
            for flier in box['fliers']:
                flier.set(marker='.', color='yellow', alpha=0.5)
                
                
            ax_bp.set_xticks(xticks)
            ax_bp1.set_xticks(xtick2)
            
            ax_bp.set_xticklabels(stations, size='small')
            ax_bp1.set_xticklabels(stations, size='small')
            
            ax_bp.set_ylim((15 ,35))
            ax_bp1.set_ylim((15 ,35))
            
            ax_bp.set_xlim((0,xticks[-1]+9))
            ax_bp1.set_xlim((0,xtick2[-1]+5))
            
            ax_bp.set_xlabel('station')
            ax_bp.set_ylabel(u'$T_{ac}$ (°C)')
            ax_bp.text(xticks[(len(xticks)/2)],34, "observed", color='red')
            ax_bp.text(xticks[(len(xticks)/2)],33, "modelled", color='blue')
            
            ax_bp1.set_xlabel('station')
            ax_bp1.set_ylabel(u'$T_{ac}$ (°C)')
            ax_bp1.text(xtick2[(len(xtick2)/2)],34, "observed", color='green')
            ax_bp1.text(xtick2[(len(xtick2)/2)],33, "modelled", color='blue')
            
            figbp.savefig(figdir+'//'+radi+'_boxplot')
            figbp2.savefig(figdir+'//'+radi+'_boxplot2')
            
            ax_bp.cla()
            ax_bp1.cla()
            figbp.clf()
            figbp2.clf()
            
          #  ax_scat.set_ylabel('average Mod Ta')
          #  ax_scat.set_xlabel('imperv frac')
            
            Ta_o = list(itertools.chain(*Ta_o))
            Ta_m = list(itertools.chain(*Ta_m))
            
            ax_ta.axis('equal')
            ax_ws.axis('equal')
            
            ax_ta.set_ylim((ta_min-(0.1*ta_min) ,ta_max+(0.1*ta_max)))
            ax_ta.set_xlim((ta_min-(0.1*ta_min) ,ta_max+(0.1*ta_max)))
            ax_ta.plot([-100, 100],[-100,100], "k--")
            ax_ta.legend(loc=2,fontsize='small',ncol=3,scatterpoints=1)
            ax_ta.set_xlabel(u'obs  $T_{ac}$ (°C)')
            ax_ta.set_ylabel(u'mod  $T_{ac}$ (°C)')
            
            
            ax_ws.set_ylim((ws_min-(0.1*ws_min),ws_max+(0.1*ws_max)))
            ax_ws.set_xlim((ws_min-(0.1*ws_min),ws_max+(0.1*ws_max)))
            ax_ws.plot([-100, 100],[-100,100], "k--")
            ax_ws.legend(loc=2,fontsize='small',ncol=3,scatterpoints=1)
            ax_ws.set_xlabel('obs WS (m/s)')
            ax_ws.set_ylabel('mod WS (m/s)')
            
            slope, intercept, r_value, p_value, std_err = stats.linregress(Ta_o,Ta_m)
            ei  = [x-y for x,y, in zip(Ta_o,Ta_m)]
            mae=abs(np.mean(ei))
            rmse = round(np.sqrt(sum(np.transpose(ei)*ei)/len(Ta_m)),1)
            ax_ta.text(30,17.5, 'r='+str(round(r_value,2)))
            ax_ta.text(30,16.5, 'rmse='+str(round(rmse,2)))
            ax_ta.text(30,15.5, 'mae='+str(round(mae,2)))
            
    
            	
            mfig.savefig(figdir+'//'+radi+'_all_stations_scatter_Ta')
            mfig1.savefig(figdir+'//'+radi+'_all_stations_scatter_Ws')
    
            ax_ta.cla()
            ax_ws.cla()        
            
            mfig.clf()
            mfig1.clf()
            figbp.clf()
            figbp2.clf()

            mp.close('all')  

ControlFileName = sys.argv[1]
cfM = ConfigObj(ControlFileName)

dateparse = lambda x: pandas.datetime.strptime(x, cfM['date_fmt'])      # parse dates for input met file using format defined in control file
#print dateparse
run = cfM['run_name']                               # model run name 
#print (run)
tmstp = cfM['timestep']                             # time step (minutes)


lc_data = pandas.read_csv(os.path.join('..','input',cfM['site_name'],'LC',cfM['inpt_lc_file'])) # reads the input land cover data
print (lc_data)
stations = lc_data['FID'].values
#print (stations)

mod_rslts = np.load('../output/Mawson/Mawson-grid.npy')  

val_ts(cfM,run,stations,mod_rslts)    

met_file = os.path.join('..','input',cfM['site_name'],'MET',cfM['inpt_met_file'])    # input meteorological forcing data file 
met_data = pandas.read_csv(met_file,parse_dates=['datetime'], date_parser=dateparse,index_col=['datetime']) # convert to data frame

obs_file = os.path.join('..','obs',cfM['site_name'],'stations_MET',cfM['inpt_obs_file'])  # file for observed AWS data
obs_data = pandas.read_csv(obs_file,parse_dates =['TIMESTAMP'], date_parser=dateparse, index_col=['TIMESTAMP'])  # reads observed AWS data and puts in dataframe  | only gets used if validating air temp (plotting.py)

date1A=dt.datetime(int(cfM['date1A'][0]), int(cfM['date1A'][1]),int(cfM['date1A'][2]), int(cfM['date1A'][3]))   ## the date/time that the simulation starts
date1=dt.datetime(int(cfM['date1'][0]), int(cfM['date1'][1]),int(cfM['date1'][2]), int(cfM['date1'][3]))        ## the date/time for period of interest (i.e. before this will not be saved)
date2=dt.datetime(int(cfM['date2'][0]), int(cfM['date2'][1]),int(cfM['date2'][2]), int(cfM['date2'][3]))        ## end date/time of simulation 
tD = date2-date1A   ## time difference between start and end date
nt=divmod(tD.days * 86400 + tD.seconds, (60*int(tmstp)))[0] # number of timesteps
date_range = pandas.date_range(date1,date2,freq= tmstp + 'T')               #  date range for model period 
date_range1A =pandas.date_range(date1A,(date2-timedelta(hours=1)),freq= tmstp + 'T') # date range for model period (i.e. including spin-up period)
Dats={'date1A':date1A, 'date1':date1, 'date2':date2, 'date_range':date_range,'date_rangeA':date_range1A} # this is a dictionary with all the date/time information 
    
val_ta(cfM,met_data,stations,obs_data,mod_rslts,Dats)

gis(cfM,mod_rslts,run)
