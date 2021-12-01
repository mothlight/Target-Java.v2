package Target;

import java.util.ArrayList;
import java.util.TreeMap;

public class Constants
{

//	this is a dictionary of constants used by the air temperature module


	//# von Karman constant
	public static double cs_karman    = 0.4; 	
	//# Stefan-Boltzman constant, W/m2/K4
	public static double cs_sb        = 5.67E-8;	
	//# albedos //for detailed, roof 0.15 -> 0.10, dry 0.19 -> 0.10, irr 0.19 -> 0.10
 	public static TreeMap<String,Double> cs_alb  = new TreeMap<String,Double>() 
	{{this.put("roof", 0.10); this.put("wall", 0.15); this.put("road", 0.08); this.put("watr", 0.10); this.put("conc",0.20); this.put("dry",0.10); 
	this.put("irr",0.10); this.put("Veg",0.10);}};

	//# emissivities
	public static TreeMap<String,Double> cs_emis  = new TreeMap<String,Double>() 
	{{this.put("roof", 0.90); this.put("wall", 0.90); this.put("road", 0.95); this.put("watr", 0.97); this.put("conc",0.94); this.put("dry",0.98); 
	this.put("irr",0.98); this.put("Veg",0.98);}};

	//# stomatal resistances
	public static TreeMap<String,Double> cs_rs  = new TreeMap<String,Double>() 
	{{this.put("roof", -999.); this.put("wall", -999.); this.put("road", -999.); this.put("watr", 0.0); this.put("conc",-999.); this.put("dry",-999.); 
	this.put("irr",40.0); this.put("Veg",40.0);}};

	//# heat capacity  (J m^-3 K^-1)
	public static TreeMap<String,Double> cs_C  = new TreeMap<String,Double>() 
	{{this.put("roof", 1250000.); this.put("wall", 1250000.); this.put("road",1940000.); this.put("watr",4180000.); this.put("conc",2110000.); 
	this.put("dry",1350000.); this.put("irr",2190000.); this.put("soilW",3030000.);}};

	//# thermal diffusivity (m^2 s^-1)
	public static TreeMap<String,Double> cs_K  = new TreeMap<String,Double>() 
	{{this.put("roof", 0.00000005); this.put("wall", 0.00000005); this.put("road",0.00000038); this.put("watr",0.00000014); this.put("conc",0.00000072); 
	this.put("dry",0.00000021); this.put("irr",0.00000042); this.put("soilW",0.00000063);}};

	//# SoilW = saturated soil layer benath water
	//# Tm intial conditions [SPIN2 - good]
	public static TreeMap<String,Double> cs_Tm  = new TreeMap<String,Double>() 
	{{this.put("roof", 28.2); this.put("wall", 28.2); this.put("road",29.0); this.put("watr",23.6); this.put("conc",27.9); this.put("dry",23.0); 
	this.put("irr",22.0);}};

	//# Ts intial conditions [BASE]
	public static TreeMap<String,Double> cs_Ts  = new TreeMap<String,Double>() 
	{{this.put("roof", 20.0); this.put("wall", 20.0); this.put("road",20.0); this.put("watr",20.0); this.put("conc",20.); this.put("dry",20.0); 
	this.put("irr",20.0); }};

	public static double cs_dW       = Math.sqrt((2. * cs_K.get("soilW") /(2.*Math.PI / 86400.)));
	public static double cs_ww   =     (2.*Math.PI / 86400.);
	//## eddy diffusivity of water (m^2 s^-1) - [used for the water Ts part.]
	public static double cs_Kw 	      = 6.18*Math.pow(10,(-7));  	
	//##  specific heat of air (J / kg C)
	public static double cs_cp     = 0.001013;  	
	//## Unitless - ratio of molecular weight of water to dry air
	public static double cs_e      = 0.622 ;
	//## density of dry air (1.2 kg m-3)
	public static double cs_pa     = 1.2;		
	//# heat capacity of air
	public static double cs_cpair=1004.67;   
	//## bulk transfer coefficient for water energy balance modelled
	public static double cs_hv = 1.4*(Math.pow(10,(-3)));	
	//## amount of radiation immediately absorbed by the first layer of water (set to 0.45) (martinez et al., 2006).
	public static double cs_betaW  = 0.45 ;			
	//## depth of the water layer (m)
	public static double cs_zW = 0.3;	
	//## extinction coefficient after Subin et al., (2012)
	public static double cs_NW  = (1.1925*Math.pow(cs_zW,(-0.424)))	;
	//## the latent heat of vaporisation (MJ Kg^-1)
	public static double cs_Lv = 2.43*(Math.pow(10,6))	;		

	//# BOM reference height
	public static double cs_z_URef = 	10.;  
	//# air temperature measurement height
	public static double cs_z_TaRef = 2. ;  
	//## roughness length, used in new Ta module.
	public static double cs_z0m = 1.; 
	//# average building height in domain
	public static double cs_zavg = 4.5 ;

	 //# horizontal resolution of the model  
	public static double cs_res = 30.;
	
    // #  Roughness length for momentum (m)
	public static double z0m_rur = 0.45;
	
	public static TreeMap<String,ArrayList<Double>> cs_LUMPS1  = new TreeMap<String,ArrayList<Double>>() 
	{{this.put("roof", new ArrayList<Double>() {{this.add(0.12);this.add(0.24);this.add(-4.5);}}); 
	this.put("wall", new ArrayList<Double>() {{this.add(0.12);this.add(0.24);this.add(-4.5);}}); 
	this.put("road",new ArrayList<Double>() {{this.add(0.50);this.add(0.28);this.add(-31.45);}} ); 
	this.put("conc",new ArrayList<Double>() {{this.add(0.61);this.add(0.28);this.add(-23.9);}} ); 
	this.put("dry",new ArrayList<Double>() {{this.add(0.27);this.add(0.33);this.add(-21.75);}} ); 
	this.put("irr",new ArrayList<Double>() {{this.add(0.32);this.add(0.54);this.add(-27.40);}} ); 
	this.put("Veg",new ArrayList<Double>() {{this.add(0.11);this.add(0.11);this.add(-12.3);}}  );}};
	

	public static TreeMap<String,Double> cs_alphapm  = new TreeMap<String,Double>() 
	{{this.put("roof", 0.0); this.put("wall", 0.0); this.put("road",0.0); this.put("conc",0.0); this.put("dry",0.2); this.put("irr",1.2); this.put("Veg",1.2);}};
	
	public static TreeMap<String,Double> cs_beta  = new TreeMap<String,Double>() 
	{{this.put("roof", 3.0); this.put("wall", 3.0); this.put("road",3.0); this.put("conc",3.0); this.put("dry",3.0); this.put("irr",3.0); this.put("Veg",3.0);}};
	
	public static boolean directRoofs=true;


//	#cs['c1'] = 5.3e-13 # emperical constant from Holstag and Van Ulden (1983) used for Ld calcs
//	#cs['c2'] = 60. # emperical constant from Holstag and Van Ulden (1983)
//	#cs['alphadcan'] = 4.43		# is an empirical coefficient - CLMU tehcnical notes (eq. 3.55)
//	#cs['zomB']      = 1.		# correction to the drag coefficient to account for variable obstacles shapes & flow conditions (eq. 3.57)
//	#cs['zomCD']     =  1.2		# depth intergrated mean drag coefficient for surface mounted cubes in shear flow (eq. 3.57)
//	#cs['N']         = 	0. 	# cloudness factor
//	#cs['Tm'] = {"roof":29. , "road":30. , "watr": 29.0  , "conc":29. , "Veg":24.0 , "dry":24.0 , "irr":24.0 }	# Tm intial conditions [WARM]
//	#cs['Tm'] = {"roof":25. , "road":26. , "watr": 25.0  , "conc":25. , "Veg":20.0 , "dry":20.0 , "irr":20.0 }	# Tm intial conditions [BASE]
	


}
