"""
this is a dictionary of constants used by the air temperature module

"""

import math

cs = {}

cs['karman']    = 0.4 		# von Karman constant
cs['sb']        = 5.67E-8	# Stefan-Boltzman constant, W/m2/K4
cs['alb']  = {"roof": 0.15 , "road": 0.1  , "watr": 0.08 , "conc":0.2   , "Veg":0.1  , "dry":0.19  , "irr":0.19 }		# albedos
cs['emis'] = {"roof": 0.90 , "road":0.95  , "watr": 0.97 , "conc":0.94  , "Veg":0.98 , "dry":0.98  , "irr":0.98 }		# emissivities
cs['rs']   = {"roof":-999. , "road":-999. , "watr": 0.0  , "conc":-999. , "Veg":40.0 , "dry":-999. , "irr":40.0 }		# stomatal resistances
#      	      Roof              | Road              |    watr     | Conc        |  Veg        |  dry       | irr	 |
cs['C']  =   {"roof":90317.     , "road":1940000.   ,"watr":4180000.    ,"conc": 2110000.    , "Veg":2546000.    ,"dry": 1350000.    ,"irr": 2190000., "soilW" : 3030000.}	# heat capacity  (J m^-3 K^-1) 
cs['K']  =   {"roof":0.00001917 , "road":0.00000038 ,"watr":0.00000014  ,"conc":0.00000072   , "Veg":0.00000012  ,"dry": 0.00000021  ,"irr": 0.00000042, "soilW": 0.00000063}	# thermal diffusivity (m^2 s^-1)
# SoilW = saturated soil layer benath water 
cs['Tm'] = {"roof":28.2, "road":29.0, "watr":24.5, "conc":27.9, "Veg":20.8,"dry":22.4, "irr":21.5} 	# Tm intial conditions [SPIN2 - good]
cs['Ts'] = {"roof":20. , "road":20. , "watr": 20.0  , "conc":20. , "Veg":20.0 , "dry":20.0 , "irr":20.0 }	# Ts intial conditions [BASE]
cs['dW']       = math.sqrt((2.*cs['K']['soilW']/(2.*math.pi / 86400.)))
cs['ww']   =     (2.*math.pi / 86400.)
cs['Kw'] 	      = 6.18*10**(-7)   		## eddy diffusivity of water (m^2 s^-1) - [used for the water Ts part.] 
cs['cp']     = 0.001013  		##  specific heat of air (J / kg C)
cs['e']      = 0.622 			## Unitless - ratio of molecular weight of water to dry air
cs['pa']     = 1.2 			## density of dry air (1.2 kg m-3)

cs['hv'] = 1.4*(10**(-3))		## bulk transfer coefficient for water energy balance modelled
cs['betaW']  = 0.45 			## amount of radiation immediately absorbed by the first layer of water (set to 0.45) (martinez et al., 2006). 
cs['zW'] = 0.3			## depth of the water layer (m)
cs['NW']  = (1.1925*cs['zW']**(-0.424))	## extinction coefficient after Subin et al., (2012)
cs['Lv'] = 2.43*(10**6)			## the latent heat of vaporisation (MJ Kg^-1) 

cs['zref'] = 	10.  # BOM reference height     										
cs['z0m'] = 1. ## roughness length, used in new Ta module.
cs['zavg'] = 4.5 # average building height in domain 

cs['LUMPS1'] = {'roof':[0.12,0.24,-4.5],'road': [0.36,0.23,-19.30],'watr':[0.50,0.21,-39.1],"conc":[0.62,0.29,-30.47],'Veg':[0.11,0.11,-12.3],'dry':[0.21,0.11,-16.10],'irr':[0.32,0.54,-27.40]}     
cs['alphapm'] = {"roof":0.0 ,"road":0.0,"conc":0.0 , "Veg":1.2 , "dry":0.2 , "irr":1.2 }
cs['beta'] =    {"roof":3.0 ,"road":3.0,"conc":3.0 , "Veg":3.0 , "dry":3.0 , "irr":3.0 }

#cs['c1'] = 5.3e-13 # emperical constant from Holstag and Van Ulden (1983) used for Ld calcs
#cs['c2'] = 60. # emperical constant from Holstag and Van Ulden (1983)
#cs['alphadcan'] = 4.43		# is an empirical coefficient - CLMU tehcnical notes (eq. 3.55) 
#cs['zomB']      = 1.		# correction to the drag coefficient to account for variable obstacles shapes & flow conditions (eq. 3.57)
#cs['zomCD']     =  1.2		# depth intergrated mean drag coefficient for surface mounted cubes in shear flow (eq. 3.57) 
#cs['N']         = 	0. 	# cloudness factor
#cs['Tm'] = {"roof":29. , "road":30. , "watr": 29.0  , "conc":29. , "Veg":24.0 , "dry":24.0 , "irr":24.0 }	# Tm intial conditions [WARM]
#cs['Tm'] = {"roof":25. , "road":26. , "watr": 25.0  , "conc":25. , "Veg":20.0 , "dry":20.0 , "irr":20.0 }	# Tm intial conditions [BASE]
