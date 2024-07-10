"""
this is a dictionary of constants used by the air temperature module

"""

import math

cs = {}

cs['karman']    = 0.4 		# von Karman constant
cs['sb']        = 5.67E-8	# Stefan-Boltzman constant, W/m2/K4
cs['alb']  = {"roof": 0.15 , "wall": 0.15 , "road": 0.08  , "watr": 0.10 , "conc":0.20     , "dry":0.19  , "irr":0.19, 'Veg':0.10 }		# albedos
cs['emis'] = {"roof": 0.90 , "wall": 0.90 , "road": 0.95   , "watr": 0.97 , "conc":0.94     , "dry":0.98  , "irr":0.98, 'Veg':0.98 }		# emissivities
cs['rs']   = {"roof":-999. , "wall":-999. , "road":-999.  , "watr": 0.0  , "conc":-999. , "Veg":40.0 , "dry":-999. , "irr":40.0 }		# stomatal resistances
#      	      Roof              | Road              |    watr     | Conc        |  Veg        |  dry       | irr	 |
cs['C']  =   {"roof":1250000.   , "wall":1250000.   , "road":1940000.   ,"watr":4180000.    ,"conc": 2110000.    , "dry": 1350000.    ,"irr": 2190000., "soilW" : 3030000.}	# heat capacity  (J m^-3 K^-1)
cs['K']  =   {"roof":0.00000005 , "wall":0.00000005 ,"road":0.00000038 ,"watr":0.00000014  ,"conc":0.00000072   , "dry": 0.00000021  ,"irr": 0.00000042, "soilW": 0.00000063}	# thermal diffusivity (m^2 s^-1)
# SoilW = saturated soil layer benath water
cs['Tm'] = {"roof":28.2, "wall":28.2,  "road":29.0, "watr":23.6, "conc":27.9, "dry":23.0, "irr":22.0} 	# Tm intial conditions [SPIN2 - good]
#cs['Tm'] = {"roof":28.2, "wall":28.2,  "road":29.0, "watr":24.5, "conc":27.9, "dry":22.4, "irr":21.5} 	# Tm intial conditions [SPIN2 - good]
#cs['Tm'] = {"roof":31.4, "wall":29.8,  "road":30.1, "watr":23.6, "conc":29.0, "dry":23.0, "irr":21.9}

cs['Ts'] = {"roof":20.0, "wall":20.0,  "road":20.0 , "watr": 20.0, "conc":20. , "dry":20.0 , "irr":20.0 }	# Ts intial conditions [BASE]
cs['dW']       = math.sqrt((2.*cs['K']['soilW']/(2.*math.pi / 86400.)))
cs['ww']   =     (2.*math.pi / 86400.)
cs['Kw'] 	      = 6.18*10**(-7)   		## eddy diffusivity of water (m^2 s^-1) - [used for the water Ts part.]
cs['cp']     = 0.001013  		##  specific heat of air (J / kg C)
cs['e']      = 0.622 			## Unitless - ratio of molecular weight of water to dry air
cs['pa']     = 1.2 			## density of dry air (1.2 kg m-3)

cs['cpair']=1004.67   # heat capacity of air

cs['hv'] = 1.4*(10**(-3))		## bulk transfer coefficient for water energy balance modelled
cs['betaW']  = 0.45 			## amount of radiation immediately absorbed by the first layer of water (set to 0.45) (martinez et al., 2006).
cs['zW'] = 0.3			## depth of the water layer (m)
cs['NW']  = (1.1925*cs['zW']**(-0.424))	## extinction coefficient after Subin et al., (2012)
cs['Lv'] = 2.43*(10**6)			## the latent heat of vaporisation (MJ Kg^-1)

cs['z_URef'] = 	10.  # BOM reference height
cs['z_TaRef'] = 2.   # air temperature measurement height
cs['z0m'] = 1. ## roughness length, used in new Ta module.
cs['zavg'] = 4.5 # average building height in domain

cs['res'] = 30. # horizontal resolutoin of the model     

cs['LUMPS1'] = {'roof':[0.12,0.24,-4.5], 'wall':[0.12,0.24,-4.5],'road': [0.50,0.28,-31.45],"conc":[0.61,0.28,-23.9],'dry':[0.27,0.33,-21.75],'irr':[0.32,0.54,-27.40], 'Veg':[0.11,0.11,-12.3]}


#By the way, I reviewed the soil moisture parameters. We can't actually set that value directly in Target. It uses the Alpha Penman-
#Monteith (pm), based on Hanna & Chang 1992. The range of values for this can be seen on page 234 of the attached.
#
#It is set in the code (in Constants2.py) by:
#cs['alphapm'] = {"roof":0.0 ,"road":0.0,"conc":0.0 , "Veg":1.2 , "dry":0.2 , "irr":1.2 }
#
#So, I think for Target, we are already at the high range for those values for the irrigated grass. So, I think for the scenario 1 and 2, those should probably be adjusted down towards the 0.5 to 1.0 range. But if you give me the range for the various scenarios (in whatever you have m3 m-3 or in % volume), I can experiment and see what the model should be set to for the scenarios. 

cs['alphapm'] = {"roof":0.0 ,"wall":0.0,"road":0.0,"conc":0.0 , "Veg":1.2 , "dry":0.2 , "irr":1.2 }
cs['beta'] =    {"roof":3.0 ,"wall":3.0 ,"road":3.0,"conc":3.0 , "Veg":3.0 , "dry":3.0 , "irr":3.0 }

#cs['c1'] = 5.3e-13 # emperical constant from Holstag and Van Ulden (1983) used for Ld calcs
#cs['c2'] = 60. # emperical constant from Holstag and Van Ulden (1983)
#cs['alphadcan'] = 4.43		# is an empirical coefficient - CLMU tehcnical notes (eq. 3.55)
#cs['zomB']      = 1.		# correction to the drag coefficient to account for variable obstacles shapes & flow conditions (eq. 3.57)
#cs['zomCD']     =  1.2		# depth intergrated mean drag coefficient for surface mounted cubes in shear flow (eq. 3.57)
#cs['N']         = 	0. 	# cloudness factor
#cs['Tm'] = {"roof":29. , "road":30. , "watr": 29.0  , "conc":29. , "Veg":24.0 , "dry":24.0 , "irr":24.0 }	# Tm intial conditions [WARM]
#cs['Tm'] = {"roof":25. , "road":26. , "watr": 25.0  , "conc":25. , "Veg":20.0 , "dry":20.0 , "irr":20.0 }	# Tm intial conditions [BASE]
