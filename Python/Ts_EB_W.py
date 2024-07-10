"""

calculates the energy balance and surface temperature for water

"""

from datetime import  timedelta
import math	

def Ts_EB_W(met_d,cs,cfM,mod_ts, mod_tm, Dats,i,rad):

	

    if Dats['dte'] <= Dats['date1A'] +  timedelta(minutes=(2*int(cfM['timestep']))):

        Tw1   = cs.cs['Ts']['watr']			## intial conditions
        Tsoil = cs.cs['Ts']['dry']				## intial conditions
        Gw       = 0.
        LEw 	 = 0.
        Hs       = 0.              
        tM = cs.cs['Tm']['watr']

    else:

        RnW     = rad['Rn']											# net radiation water surface 
        
        Sab = (met_d['Kd'][i]*(1-0.08))*(cs.cs['betaW']+((1-cs.cs['betaW'])*(1-(math.exp(-(cs.cs['NW'])))))) 		# Kd that penetrates based on Beer's law
        Hs  =  cs.cs['pa'] * (cs.cs['cp']*1000000.)*cs.cs['hv'] *met_d['WS'][i]* (met_d['Ta'][i] - mod_ts['watr'][i-1])			# The sensible heat flux is given by Martinez et al. (2006)
                
        Gw      = -cs.cs['C']['watr'] * cs.cs['Kw'] * ((mod_ts['TSOIL'][i-1]-mod_ts['watr'][i-1])/cs.cs['zW'])					# the convective heat flux at the bottom of the water layer (and into the soil below)
        
        dlt_soil = ((2/(cs.cs['C']['soilW']*cs.cs['dW'])*Gw))-(cs.cs['ww']*(mod_ts['TSOIL'][i-1]-mod_tm['watr'][i-1]))				# force restore calc -- change soil temperature change 
        Tsoil = mod_ts['TSOIL'][i-1]+(dlt_soil*int(cfM['timestep'])* 60.)											# soil layer temperature (C)
        
        	
        es = 0.611*math.exp(17.27*mod_ts['watr'][i-1]/(237.3+mod_ts['watr'][i-1]))								# saturation vapour pressure (es) (kPA) at the water surface
        ea = 0.611*math.exp(17.27*met_d['Ta'][i]/(237.3+met_d['Ta'][i]))/100*met_d['RH'][i]						# vapour pressure (kPa) of the air (ea)
        qs = (0.622*es)/101.3												# saturated specific humidity (qs) (kg kg-1)
        pu = 101325./(287.04*((mod_ts['watr'][i-1]+273.15)*1.+0.61*qs))									# density of moist air (pv) (kg m-3)
        qa = (0.622*ea)/101.3												# specific humidity of air (qa), 
        LEw = pu*cs.cs['Lv']*cs.cs['hv']*met_d['WS'][i]*(qs-qa)										# The latent heat flux (Qe) (W m-2) 
        Q1 = (Sab+(RnW-(met_d['Kd'][i]*(1-0.08))))+Hs-LEw-Gw										# The chage in heat storage of water layer 
        
        dlt_Tw = Q1/(cs.cs['C']['watr']*cs.cs['zW'])*int(cfM['timestep'])* 60. 										#  change in surface water Temperature (C)
        Tw1 = mod_ts['watr'][i-1] + dlt_Tw	

        D = math.sqrt((2*cs.cs['K']['watr'])/((2*math.pi)/86400.))	# the damping depth for the annual temperature cycle
        Dy = D * math.sqrt(365.)												#  surface water temperature (C) 
        delta_Tm = Gw/(cs.cs['C']['soilW']*Dy)		## change in Tm per second
        tM = mod_tm['watr'][i-1] + (delta_Tm*int(cfM['timestep'])* 60.)


    return {"TsW":Tw1, "TSOIL":Tsoil, 'QeW': LEw, 'QhW': Hs, 'QgW':Gw , 'TM':tM}