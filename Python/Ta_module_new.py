# -*- coding: utf-8 -*-
"""
calculates canyon air temperature (Tac)
 
see section 3.5 tech notes for details

inputs:
    H           = average building height
    W           = average street width 
    qH          = average sensible heat flux
    Tsurf       = average surface temperature
    cs          = constants dictionary
    cfM         = main control file
    Dats        = dates dictionary
    obs         = observed wind speed 
    i           = current index 

    
Outputs:
    Ta_f = canyon air temperature (Tac)
    Tb   = air temperature above the canyon (Tb)
    ra   = resistance between the canyon and atmopshere
    Ucan = canyon wind speed 
"""

import math

def calc_ta(W,H,met,cs,qH,Tsurf,Dats,cfM,obs,i):

    

############ GEOMETRY CALCS #########################

    # Height to width ratio
    HW = H/W
    
    # Eq. 3.56 - plan area index (-)
    lambdap = HW / (HW +1)
    
    # Eq. 3.55 - canopy displacement height (m)
    #dcan = H * (1 + cs.cs['alphadcan']**(-lambdap) * (lambdap - 1))  ## old formulation 
    dcan = (2./3.) * H
    
    # Eq. 3.58 - frontal area index
    lambdaf = (1.-lambdap) * HW * lambdap 
    
    # Eq. 3.57 - Roughness lenght for momentum (m)
    #zom = H * (1-(dcan/H)) * math.exp(-(0.5*cs.cs['zomB']*(cs.cs['zomCD']/cs.cs['karman']**2)*(1-(dcan/H))*lambdaf)**(-0.5) )   ## old formulation
    #zom = 0.1 * H
    zom  = cs.cs['z0m'] # using uniform value, defined in constants file.

    # set the height of the top of the canopy (Utop) (z) below:
    z = H  # Utop height 
    if z > 10.:
        z = 10.  # if buildings are above 10m we assume the BoM reference data is == Utop
    if z < cs.cs['zavg']:
        z =  cs.cs['zavg']  # if building height is below domain average (set to 4.5) we set z to the domain average 
##################################################

####### DEFINE REFERENCE WIND SPEED (Uz) #######

    if  met['WS'][i]  == 0.:
        WSs = 0.25		## if wind speed if 0. it is converted to 0.25 m/s -  [stability is not accounted for in the model]
        Uz =WSs * ((math.log(z/zom))/(math.log(cs.cs['zref']/zom)))	## convert to wind speed for Utop height using log profile. 
    else:
        WSs = met['WS'][i]
        Uz = WSs * ((math.log(z/zom))/(math.log(cs.cs['zref']/zom)))  ## convert to wind speed for Utop height using log profile.
###############################################

    # calculate the resistance (ra) between canopy layer and atmosphere after Yang et al 2011.  
    #print (  cs.cs['zref'],dcan)
    ra   = (math.log((cs.cs['zref']-dcan)/zom)*math.log((cs.cs['zref']-dcan)/zom))/(cs.cs['karman']**2 *WSs )  

    # calcuate the above canopy air temperature (Tb)  after Yang et al 2011. 
    Tb   = met['Ta'][i] - ((qH/(cs.cs['pa']*(cs.cs['cp']*1000000)))*ra)

############### Calculate average canopy wind speed (Ucan - vertical and vorizontal) from observed wind speed ########
    if Dats['dte'] >= Dats['date1A'] and cfM['use_obs_ws'] == "Y":
		ucan = obs[dte]
		ustar = (cs.cs['karman']*met['WS'][i]) / math.log((cs.cs['zref']-dcan)/zom)
		wcan = ustar
		Ucan = (ucan**2. + wcan**2.)**0.5
###############################################################
##################### Calculate canopy wind speed #############
    else:
        Ucan = Uz*math.exp(-0.386*(H/W))	

    rs   = (cs.cs['pa']*(cs.cs['cp']*1000000))/(11.8+(4.2*Ucan))		# calculate surface resistance (s/m)
    hc_srf = 1./rs	# convert resistance to conductance
    hc_atm = 1./ra	# convert resistance to conductance
    
############## CALCUALTE AIR TEMPERAURE IN THE CANYON #########
    Ta_f = ((hc_atm*Tb)+(hc_srf*Tsurf))/(hc_atm+hc_srf)

    return {'Ta_f':Ta_f, 'Ta_b': Tb, 'ra':ra, 'Ucan': Ucan}
    