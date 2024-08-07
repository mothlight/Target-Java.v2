"""
calculates surface temperature using force-restore method (Jacobs, 2000)
 
see section 3.3 tech notes for details

inputs:
    eng_bal     = energy balance dictionary
    cs          = constants dictionary
    cfM         = main control file
    mod_ts      = modelled surface temperature dataframe
    mod_tm      = modelled ground temperature dataframe 
    surf        = current surface type   
    Dats        = dates dictionary
    i           = current index 
    
Outputs:
    Ts = modelled surface temperature (Tsurf)
    Tm = modelled soil (ground)  temperature (Tm)
 
 
 
"""
import math
from datetime import timedelta

def Ts_calc_surf(eng_bal,cs,cfM,mod_ts,mod_tm, Dats,surf,i):

	
    if Dats['dte'] <= Dats['date1A'] +  timedelta(minutes=(2*int(cfM['timestep']))):

			tS=float(cs.cs['Ts'][surf])	## intial conditions for Tsurf
			tM=float(cs.cs['Tm'][surf])	## intial conditions for Tm
    else:
			QGS 	= eng_bal['Qg']		## calculate ground heat flux 

                 
			D = math.sqrt((2*cs.cs['K'][surf])/((2*math.pi)/86400.))	# the damping depth for the annual temperature cycle
			Dy = D * math.sqrt(365.)	

                 
			delta_Tg = ((2/(cs.cs['C'][surf]*D)*QGS))-(((2*math.pi)/86400.)*(mod_ts[surf][i-1]-mod_tm[surf][i-1]))	## the change in Tsurf per second 
			delta_Tm = QGS/(cs.cs['C'][surf]*Dy)		## change in Tm per second
                 
			tM = mod_tm[surf][i-1] + (delta_Tm*int(cfM['timestep'])* 60.)		# update Tm (3600. seconds in an hour timestep)
			tS = mod_ts[surf][i-1] + (delta_Tg*int(cfM['timestep'])* 60.)		# update Tsurf (3600. seconds in an hour timestep)

    return {'TS':tS, 'TM':tM}





