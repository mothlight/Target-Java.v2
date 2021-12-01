package Target;

import java.util.ArrayList;
import java.util.Date;
import java.util.TreeMap;

public class SimpleWater
{
//	"""
//
//	simple water body model 
//
//	calculates water surface temperature AND water energy balance 
//
//	see section 3.4 tech description for more details
//
//	inputs:
//	    cs      = constants dictionary
//	    cfM     = main control file
//	    met_d   = met forcing data frame
//	    Dats    = dates dictionary
//	    mod_ts  = surface temperature (Tsurf) data frame
//	    mod_tm  = ground temperature (Tm) data frame
//	    i       = current index 
//	    rad     = net radiation dictionary 
//	    
//	Outputs:
//	    TsW  = surface temperature water
//	    TSOIL = soil surface temperature below water body
//	    QeW  = latent heat over water
//	    QhW  = sensible heat flux water
//	    QgH  = storage heat flux water 
//	    TM   =  The average soil (ground) temperature below TSOIL layer 
//	        
//	    

	
	public static final String TSW_KEY = "TsW";
	public static final String TSOIL_KEY = "TSOIL";
	public static final String QEW_KEY = "QeW";
	public static final String QHW_KEY = "QhW";
	public static final String QGW_KEY = "QgW";
	public static final String TM_KEY = "TM";	
	
	public TreeMap<String,Double> Ts_EB_W_no_longer_used(ArrayList<ArrayList<Object>> met_d, Cfm cfm, ArrayList<TreeMap<String,Object>> mod_ts, 
			ArrayList<TreeMap<String,Object>> mod_tm, TreeMap<String,Date> Dats, int i,TreeMap<String,Double> rad)
	{
		TreeMap<String,Double> returnValues = new TreeMap<String,Double>();

		String tmstp = cfm.getValue("timestep");                            // # time step (minutes)
		int tmstpInt = new Integer( tmstp.replaceAll("S", "").replaceAll("'", "") ).intValue();
		Date dte = Dats.get("dte");
		Date date1A = Dats.get("date1A");
		int timedelta = 2*tmstpInt*1000;
		
		double cs_ts_watr = Constants.cs_Ts.get("watr");
		double cs_ts_dry = Constants.cs_Ts.get("dry");
		double cs_tm_watr = Constants.cs_Tm.get("watr");
		double cs_betaw = Constants.cs_betaW;
		double cs_nw = Constants.cs_NW;
		double cs_pa = Constants.cs_pa;
		double cs_cp = Constants.cs_cp;
		double cs_hv =  Constants.cs_hv;
		double cs_kw = Constants.cs_Kw;
		double cs_zw = Constants.cs_zW;
		double cs_c_watr = Constants.cs_C.get("watr");
		double cs_c_soilw = Constants.cs_C.get("soilW");
		double cs_dw = Constants.cs_dW;
		double cs_ww = Constants.cs_ww;
		double cs_lv = Constants.cs_Lv;
		double cs_k_watr = Constants.cs_K.get("watr");
		
        double Tw1   ;
        double Tsoil ;
        double Gw  ;
        double LEw ;
        double Hs   ;         
        double tM ;
		
	    if (dte.getTime() <= date1A.getTime() +  timedelta  )
	    {
	        Tw1   = cs_ts_watr;			//## intial conditions
	        Tsoil = cs_ts_dry;			//## intial conditions
	        Gw       = 0.;
	        LEw 	 = 0.;
	        Hs       = 0.;              
	        tM =cs_tm_watr;
	    }

	    else
	    {
	    	double rn = rad.get(RnCalcNew.RN_KEY);
	    	
			TreeMap<String,Object> modTsMinus1 = mod_ts.get(i-1);	
			double modTsMinus1Watr = (double)modTsMinus1.get("watr");
			double modTsMinus1TSOIL = (double)modTsMinus1.get("TSOIL");
			TreeMap<String,Object> modTmMinus1 = mod_tm.get(i-1);	
			double modTmMinus1Watr = (double)modTmMinus1.get("watr");

			
			ArrayList<Object> met0 = met_d.get(i);			
			double metTa0 = (double)met0.get(MetData.Ta);

			double metKd0 = (double)met0.get(MetData.Kd);
			double metWS0 = (double)met0.get(MetData.WS);
			double metRH0 = (double)met0.get(MetData.RH);

	        double RnW     = rn;											//# net radiation water surface 
	        
	        double Sab = (metKd0*(1-0.08))*(cs_betaw+((1-cs_betaw)*(1-(Math.exp(-(cs_nw)))))); 		//# Kd that penetrates based on Beer's law
	        Hs  =  cs_pa * (cs_cp*1000000.)*cs_hv *metWS0* (metTa0 - modTsMinus1Watr)	;		//# The sensible heat flux is given by Martinez et al. (2006)
	                
	        Gw      = -cs_c_watr * cs_kw * ((modTsMinus1TSOIL-modTsMinus1Watr)/cs_zw)	;				//# the convective heat flux at the bottom of the water layer (and into the soil below)
	        
	        double dlt_soil = ((2/(cs_c_soilw*cs_dw)*Gw))-(cs_ww*(modTsMinus1TSOIL-modTmMinus1Watr))	;			//# force restore calc -- change soil temperature change 
	        Tsoil = modTsMinus1TSOIL+(dlt_soil*timedelta);											//# soil layer temperature (C)
	        
	        	
	        double es = 0.611*Math.exp(17.27*modTsMinus1Watr/(237.3+modTsMinus1Watr))	;							//# saturation vapour pressure (es) (kPA) at the water surface
	        double ea = 0.611*Math.exp(17.27*metTa0/(237.3+metTa0))/100*metRH0		;				//# vapour pressure (kPa) of the air (ea)
	        double qs = (0.622*es)/101.3;												//# saturated specific humidity (qs) (kg kg-1)
	        double pu = 101325./(287.04*((modTsMinus1Watr+273.15)*1.+0.61*qs))	;								//# density of moist air (pv) (kg m-3)
	        double qa = (0.622*ea)/101.3	;											//# specific humidity of air (qa), 
	        LEw = pu*cs_lv*cs_hv*metWS0*(qs-qa)	;									//# The latent heat flux (Qe) (W m-2) 
	        double Q1 = (Sab+(RnW-(metKd0*(1-0.08))))+Hs-LEw-Gw;										//# The chage in heat storage of water layer 
	        
	        double dlt_Tw = Q1/(cs_c_watr*cs_zw)*timedelta	;									//#  change in surface water Temperature (C)
	        Tw1 = modTsMinus1Watr + dlt_Tw	;

	        double D = Math.sqrt((2*cs_k_watr)/((2*Math.PI)/86400.));	//# the damping depth for the annual temperature cycle
	        double Dy = D * Math.sqrt(365.);												//#  surface water temperature (C) 
	        double delta_Tm = Gw/(cs_c_soilw*Dy);		//## change in Tm per second
	        tM = modTmMinus1Watr + (delta_Tm*timedelta);
	    }

	    
	    returnValues.put(TSW_KEY,Tw1);
	    returnValues.put(TSOIL_KEY,Tsoil);
	    returnValues.put(QEW_KEY,LEw);
	    returnValues.put(QHW_KEY,Hs);
	    returnValues.put(QGW_KEY,Gw);
	    returnValues.put(TM_KEY,tM);
	    return returnValues;
	}
}
