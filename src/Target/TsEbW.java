package Target;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class TsEbW
{

//	calculates the energy balance and surface temperature for water

	public static final String TSW_KEY = "TsW";
	public static final String TSOIL_KEY = "TSOIL";
	public static final String QEW_KEY = "QeW";
	public static final String QHW_KEY = "QhW";
	public static final String QGW_KEY = "QgW";
	public static final String TM_KEY = "TM";	

	public HashMap<String,Double> ts_EB_WOld(ArrayList<ArrayList<Object>> met_d,Cfm cfm, double[][][] mod_ts, double[][][] mod_tm,
			HashMap<String,Date> Dats, int i, HashMap<String,Double> rad, int vf)
	{
		HashMap<String,Double> returnValues = new HashMap<String,Double>();
		
        double Tw1;   
        double Tsoil; 
        double Gw;  
        double LEw; 
        double Hs;     
        double tM; 

		String tmstp = cfm.getValue("timestep");                            // # time step (minutes)
		int tmstpInt = new Integer( tmstp.replaceAll("S", "").replaceAll("'", "") ).intValue();
		Date dte = Dats.get("dte");
		Date spinUp = Dats.get("SpinUp");
		int timedelta = 2*tmstpInt*1000;
		
	    if (dte.getTime() <= spinUp.getTime() +  timedelta  )
	    {
	        Tw1   = Constants.cs_Ts.get("watr");			//## intial conditions
	        Tsoil = Constants.cs_Ts.get("dry") ;		//## intial conditions
	        Gw       = 0.;
	        LEw 	 = 0.;
	        Hs       = 0. ;	        
	        tM = Constants.cs_Tm.get("watr");
	    }

	    else
	    {
			double rn = rad.get(RnCalcNew.RN_KEY);

			
			ArrayList<Object> met0 = met_d.get(i);			
			double metTa0 = (double)met0.get(MetData.Ta);

			double metKd0 = (double)met0.get(MetData.Kd);
			double metWS0 = (double)met0.get(MetData.WS);
			double metRH0 = (double)met0.get(MetData.RH);
			
			double cs_betaw = Constants.cs_betaW;
			double cs_nw = Constants.cs_NW;
			double cs_pa = Constants.cs_pa;
			double cs_cp = Constants.cs_cp;
			double cs_hv = Constants.cs_hv;
			double cs_kw = Constants.cs_Kw;
			double cs_zw = Constants.cs_zW;
			double cs_c_watr = Constants.cs_C.get("watr");
			double cs_c_soilW = Constants.cs_C.get("soilW");
			double cs_k_watr = Constants.cs_K.get("watr");
			double cs_dw = Constants.cs_dW;
			double cs_ww = Constants.cs_ww;
			double cs_lv = Constants.cs_Lv;

			

			double modTsMinus1Watr = mod_ts[i-1][vf][TargetModule.getSurfIndex("watr")];	
			double modTsMinus1TSOIL = mod_ts[i-1][vf][TargetModule.getSurfIndex("TSOIL")];
			double modTmMinus1Watr = mod_tm[i-1][vf][TargetModule.getSurfIndex("watr")];
			
	        double RnW = rn;											//# net radiation water surface 
	        
	        double Sab = (metKd0*(1-0.08))*(cs_betaw+((1-cs_betaw)*(1-(Math.exp(-(cs_nw)))))) ;		//# Kd that penetrates based on Beer's law
	        Hs  =  cs_pa * (cs_cp*1000000.)*cs_hv *metWS0* (metTa0 - modTsMinus1Watr)	;		//# The sensible heat flux is given by Martinez et al. (2006)
	                
	        Gw      = -cs_c_watr * cs_kw * ((modTsMinus1TSOIL-modTsMinus1Watr)/cs_zw)	;				//# the convective heat flux at the bottom of the water layer (and into the soil below)
	        
	        double dlt_soil = ((2/(cs_c_soilW*cs_dw)*Gw))-(cs_ww*(modTsMinus1TSOIL-modTmMinus1Watr));				//# force restore calc -- change soil temperature change 
	        Tsoil = modTsMinus1TSOIL+(dlt_soil*tmstpInt);											//# soil layer temperature (C)
	        
	        double es = 0.611*Math.exp(17.27*modTsMinus1Watr/(237.3+modTsMinus1Watr));							//# saturation vapour pressure (es) (kPA) at the water surface
	        double ea = 0.611*Math.exp(17.27*metTa0/(237.3+metTa0))/100*metRH0;					//# vapour pressure (kPa) of the air (ea)
	        double qs = (0.622*es)/101.3;												//# saturated specific humidity (qs) (kg kg-1)
	        double pu = 101325./(287.04*((modTsMinus1Watr+273.15)*1.+0.61*qs));								//# density of moist air (pv) (kg m-3)
	        double qa = (0.622*ea)/101.3;											//# specific humidity of air (qa), 
	        LEw = pu*cs_lv*cs_hv*metWS0*(qs-qa);									//# The latent heat flux (Qe) (W m-2) 
	        double Q1 = (Sab+(RnW-(metKd0*(1-0.08))))+Hs-LEw-Gw;										//# The chage in heat storage of water layer 
	        
	        double dlt_Tw = Q1/(cs_c_watr*cs_zw)*tmstpInt;									//#  change in surface water Temperature (C)
	        Tw1 = modTsMinus1Watr + dlt_Tw;

	        double D = Math.sqrt((2*cs_k_watr)/((2*Math.PI)/86400.));	//# the damping depth for the annual temperature cycle
	        double Dy = D * Math.sqrt(365.);											//#  surface water temperature (C) 
	        double delta_Tm = Gw/(cs_c_soilW*Dy);		//## change in Tm per second
	        tM = modTmMinus1Watr + (delta_Tm*tmstpInt);
	        
	    }

	    returnValues.put(TSW_KEY,Tw1);
	    returnValues.put(TSOIL_KEY,Tsoil);
	    returnValues.put(QEW_KEY,LEw);
	    returnValues.put(QHW_KEY,Hs);
	    returnValues.put(QGW_KEY,Gw);
	    returnValues.put(TM_KEY,tM);
	    return returnValues;
	}
	
	
	public HashMap<String,Double> ts_EB_W(double metTa0,double metKd0,double metWS0,double metRH0,Cfm cfm, double[][][][] mod_ts, double[][][][] mod_tm,
			HashMap<String,Date> Dats, int i, HashMap<String,Double> rad, int vf, int zone)
	{
		HashMap<String,Double> returnValues = new HashMap<String,Double>();
		
        double Tw1;   
        double Tsoil; 
        double Gw;  
        double LEw; 
        double Hs;     
        double tM; 

		String tmstp = cfm.getValue("timestep");                            // # time step (minutes)
		int tmstpInt = new Integer( tmstp.replaceAll("S", "").replaceAll("'", "") ).intValue();
		Date dte = Dats.get("dte");
		Date spinUp = Dats.get("SpinUp");
		int timedelta = 2*tmstpInt*1000;
		
	    if (dte.getTime() <= spinUp.getTime() +  timedelta  )
	    {
	        Tw1   = Constants.cs_Ts.get("watr");			//## intial conditions
	        Tsoil = Constants.cs_Ts.get("dry") ;		//## intial conditions
	        Gw       = 0.;
	        LEw 	 = 0.;
	        Hs       = 0. ;	        
	        tM = Constants.cs_Tm.get("watr");
	    }

	    else
	    {
			double rn = rad.get(RnCalcNew.RN_KEY);

			
//			ArrayList<Object> met0 = met_d.get(i);			
//			double metTa0 = (double)met0.get(MetData.Ta);
//			double metKd0 = (double)met0.get(MetData.Kd);
//			double metWS0 = (double)met0.get(MetData.WS);
//			double metRH0 = (double)met0.get(MetData.RH);
			
			double cs_betaw = Constants.cs_betaW;
			double cs_nw = Constants.cs_NW;
			double cs_pa = Constants.cs_pa;
			double cs_cp = Constants.cs_cp;
			double cs_hv = Constants.cs_hv;
			double cs_kw = Constants.cs_Kw;
			double cs_zw = Constants.cs_zW;
			double cs_c_watr = Constants.cs_C.get("watr");
			double cs_c_soilW = Constants.cs_C.get("soilW");
			double cs_k_watr = Constants.cs_K.get("watr");
			double cs_dw = Constants.cs_dW;
			double cs_ww = Constants.cs_ww;
			double cs_lv = Constants.cs_Lv;

			

			double modTsMinus1Watr = mod_ts[i-1][vf][TargetModule.getSurfIndex("watr")][zone];	
			double modTsMinus1TSOIL = mod_ts[i-1][vf][TargetModule.getSurfIndex("TSOIL")][zone];
			double modTmMinus1Watr = mod_tm[i-1][vf][TargetModule.getSurfIndex("watr")][zone];
			
	        double RnW = rn;											//# net radiation water surface 
	        
	        double Sab = (metKd0*(1-0.08))*(cs_betaw+((1-cs_betaw)*(1-(Math.exp(-(cs_nw)))))) ;		//# Kd that penetrates based on Beer's law
	        Hs  =  cs_pa * (cs_cp*1000000.)*cs_hv *metWS0* (metTa0 - modTsMinus1Watr)	;		//# The sensible heat flux is given by Martinez et al. (2006)
	                
	        Gw      = -cs_c_watr * cs_kw * ((modTsMinus1TSOIL-modTsMinus1Watr)/cs_zw)	;				//# the convective heat flux at the bottom of the water layer (and into the soil below)
	        
	        double dlt_soil = ((2/(cs_c_soilW*cs_dw)*Gw))-(cs_ww*(modTsMinus1TSOIL-modTmMinus1Watr));				//# force restore calc -- change soil temperature change 
	        Tsoil = modTsMinus1TSOIL+(dlt_soil*tmstpInt);											//# soil layer temperature (C)
	        
	        double es = 0.611*Math.exp(17.27*modTsMinus1Watr/(237.3+modTsMinus1Watr));							//# saturation vapour pressure (es) (kPA) at the water surface
	        double ea = 0.611*Math.exp(17.27*metTa0/(237.3+metTa0))/100*metRH0;					//# vapour pressure (kPa) of the air (ea)
	        double qs = (0.622*es)/101.3;												//# saturated specific humidity (qs) (kg kg-1)
	        double pu = 101325./(287.04*((modTsMinus1Watr+273.15)*1.+0.61*qs));								//# density of moist air (pv) (kg m-3)
	        double qa = (0.622*ea)/101.3;											//# specific humidity of air (qa), 
	        LEw = pu*cs_lv*cs_hv*metWS0*(qs-qa);									//# The latent heat flux (Qe) (W m-2) 
	        double Q1 = (Sab+(RnW-(metKd0*(1-0.08))))+Hs-LEw-Gw;										//# The chage in heat storage of water layer 
	        
	        double dlt_Tw = Q1/(cs_c_watr*cs_zw)*tmstpInt;									//#  change in surface water Temperature (C)
	        Tw1 = modTsMinus1Watr + dlt_Tw;

	        double D = Math.sqrt((2*cs_k_watr)/((2*Math.PI)/86400.));	//# the damping depth for the annual temperature cycle
	        double Dy = D * Math.sqrt(365.);											//#  surface water temperature (C) 
	        double delta_Tm = Gw/(cs_c_soilW*Dy);		//## change in Tm per second
	        tM = modTmMinus1Watr + (delta_Tm*tmstpInt);
	        
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
