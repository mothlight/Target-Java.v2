package Target;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class RnCalcNew
{
	public static final String RN_KEY = "Rn";
	public static final String RNPREV_KEY = "Rnprev";
	public static final String RNSTAR_KEY = "Rnstar";
	public static final String MetDataKd_KEY = "MetDataKd";
	public static final String MetDataLd_KEY = "MetDataLd";
	public static final String Ku_KEY = "Ku";
	public static final String Lu_KEY = "Lu";
	public static final String newRn_KEY = "newRn";
	
//	"""
//
//	calculates net radiation for current, previous, and next time step
//
//	see section 3.1 tech description for more details
//
//	inputs:
//		cs		= constants dictionary
//		cfM		= main control file
//		met		= met forcing data frame
//		surf	= current surface type
//		Dats	= dates dictionary
//		mod_ts	= surface temperature data frame
//		i		= current index
//
//	Outputs:
//		Rn	   = net radiation
//		Rnprev = net radiation (t-1)
//		Rnstar = 0.5*(Rn(t-1) + Rn(t+1))
//
//
//
//	"""

	public HashMap<String,Double> rn_calc_new(Cfm cfm, ArrayList<ArrayList<Object>> met, String surf, HashMap<String,Date> Dats, 
			ArrayList<Double> mod_ts, int i, double svf)
	{
		HashMap<String,Double> returnValues = new HashMap<String,Double> ();
		
		Double albedo = Constants.cs_alb.get(surf);		//# surface albedo
	        System.out.println("albedo for " + surf + "=" + albedo);
                System.exit(1);
		Double emiss  = Constants.cs_emis.get(surf);	//# surface emissivity
		
		double cs_sb = Constants.cs_sb;
		
		double Rn	   = 0.;
		double Rnprev = 0.;
		double Rnnext = 0.;
		double Rnstar = 0.;
		double Kup = 0.;
		double Lup = 0.;
		double newRn = 0.0;
		
		Date dte = Dats.get("dte");
		Date spinUp = Dats.get("SpinUp");
				
		if (dte.getTime() == spinUp.getTime())
		{
			//# intial values set to 0.
			Rn	   = 0.;
			Rnprev = 0.;
			Rnnext = 0.;
			Rnstar = 0.;
			
			returnValues.put(Ku_KEY, 0.);
			returnValues.put(Lu_KEY, 0.);
			returnValues.put(newRn_KEY, 0.);
			returnValues.put(MetDataKd_KEY, 0.);
			returnValues.put(MetDataLd_KEY, 0.);
			
		}
		else
		{   

			
			double Ta_srfp = mod_ts.get(2);			//# "previous" modelled T_surf (3 timesteps back)
			double Ta_srf	= mod_ts.get(1);			//# "current" modelled T_Surf (2 time steps back)
			double Ta_srfn = mod_ts.get(0);			//# "next" modelled T_Surf (1 time steps back)
			
			ArrayList<Object> met0 = met.get(i);	
			double metKd0 = (double)met0.get(MetData.Kd);
			double metLd0 = (double)met0.get(MetData.Ld);
//			double metTa0 = (double)met0.get(MetData.Ta);
			returnValues.put(MetDataKd_KEY, metKd0);
			returnValues.put(MetDataLd_KEY, metLd0);
			
			ArrayList<Object> metPlus1 = met.get(i+1);			
			double metKdPlus1 = (double)metPlus1.get(MetData.Kd);
			double metLdPlus1 = (double)metPlus1.get(MetData.Ld);
			
			ArrayList<Object> metMinus1 = met.get(i-1);			
			double metKdMinus1 = (double)metMinus1.get(MetData.Kd);
			double metLdMinus1 = (double)metMinus1.get(MetData.Ld);
			
			if (!surf.equals("roof"))
			{
//				System.out.println("albedo of roof surface " + surf + " is " + albedo);
			
				Rn	   = ((metKd0*(1.0-albedo))*svf) + ((emiss*(metLd0 - (cs_sb*Math.pow((Ta_srf+273.15),4))))*svf);	 //# modified version of eq 11 Loridan et al. (2011)
				Rnprev = ((metKdMinus1*(1.0-albedo))*svf) + ((emiss*(metLdMinus1 - (cs_sb*Math.pow((Ta_srfp+273.15),4))))*svf);
				Rnnext = ((metKdPlus1*(1.0-albedo))*svf) + ((emiss*(metLdPlus1 - (cs_sb*Math.pow((Ta_srfn+273.15),4))))*svf);
				Rnstar = 0.5*(Rnnext - Rnprev);
				
				Kup = metKd0*(1.0-albedo)*svf;
				Lup = cs_sb*Math.pow((Ta_srf+273.15),4);		
				newRn = metKd0*svf + metLd0 - Kup - Lup; 
			}
				
			if (surf.equals("roof"))
			{
				
//				System.out.println("albedo of non-roof surface " + surf + " is " + albedo);
				
				Rn	   = metKd0*(1.0-albedo) + (emiss*(metLd0 - (cs_sb*Math.pow((Ta_srf+273.15),4))))	; //# modified version of eq 11 Loridan et al. (2011)
				Rnprev = metKdMinus1*(1.0-albedo) + (emiss*(metLdMinus1 - (cs_sb*Math.pow((Ta_srfp+273.15),4))));
				Rnnext = metKdPlus1*(1.0-albedo) + (emiss*(metLdPlus1 - (cs_sb*Math.pow((Ta_srfn+273.15),4))));
				Rnstar = 0.5*(Rnnext - Rnprev);
				
				Kup = metKd0*(1.0-albedo);
				Lup = cs_sb*Math.pow((Ta_srf+273.15),4);	
				newRn = metKd0 + metLd0 - Kup - Lup; 
			}

	        

			returnValues.put(Ku_KEY, Kup);
			returnValues.put(Lu_KEY, Lup);
			returnValues.put(newRn_KEY, newRn);
	}


			
		returnValues.put(RN_KEY, Rn);
		returnValues.put(RNPREV_KEY, Rnprev);
		returnValues.put(RNSTAR_KEY, Rnstar);
		
		return returnValues;
}
	
	
	
	public HashMap<String,Double> rn_calc_new(Cfm cfm, double metKd0, double metLd0, double metKdPlus1, double metLdPlus1, double metKdMinus1, double metLdMinus1, String surf, HashMap<String,Date> Dats, 
			ArrayList<Double> mod_ts, double svf)
	{
		HashMap<String,Double> returnValues = new HashMap<String,Double> ();
		
		Double albedo = Constants.cs_alb.get(surf);		//# surface albedo
		Double emiss  = Constants.cs_emis.get(surf);	//# surface emissivity
		
		double cs_sb = Constants.cs_sb;
		
		double Rn	   = 0.;
		double Rnprev = 0.;
		double Rnnext = 0.;
		double Rnstar = 0.;
		double Kup = 0.;
		double Lup = 0.;
		double newRn = 0.0;
		
		Date dte = Dats.get("dte");
		Date spinUp = Dats.get("SpinUp");
				
		if (dte.getTime() == spinUp.getTime())
		{
			//# intial values set to 0.
			Rn	   = 0.;
			Rnprev = 0.;
			Rnnext = 0.;
			Rnstar = 0.;
			
			returnValues.put(Ku_KEY, 0.);
			returnValues.put(Lu_KEY, 0.);
			returnValues.put(newRn_KEY, 0.);
			returnValues.put(MetDataKd_KEY, 0.);
			returnValues.put(MetDataLd_KEY, 0.);
			
		}
		else
		{   

			
			double Ta_srfp = mod_ts.get(2);			//# "previous" modelled T_surf (3 timesteps back)
			double Ta_srf	= mod_ts.get(1);			//# "current" modelled T_Surf (2 time steps back)
			double Ta_srfn = mod_ts.get(0);			//# "next" modelled T_Surf (1 time steps back)
			
//			ArrayList<Object> met0 = met.get(i);	
//			double metKd0 = (double)met0.get(MetData.Kd);
//			double metLd0 = (double)met0.get(MetData.Ld);
//			double metTa0 = (double)met0.get(MetData.Ta);
			returnValues.put(MetDataKd_KEY, metKd0);
			returnValues.put(MetDataLd_KEY, metLd0);
			
//			ArrayList<Object> metPlus1 = met.get(i+1);			
//			double metKdPlus1 = (double)metPlus1.get(MetData.Kd);
//			double metLdPlus1 = (double)metPlus1.get(MetData.Ld);
			
//			ArrayList<Object> metMinus1 = met.get(i-1);			
//			double metKdMinus1 = (double)metMinus1.get(MetData.Kd);
//			double metLdMinus1 = (double)metMinus1.get(MetData.Ld);
			
			if (!surf.equals("roof"))
			{
//				System.out.println("albedo of roof surface " + surf + " is " + albedo);
			
				Rn	   = ((metKd0*(1.0-albedo))*svf) + ((emiss*(metLd0 - (cs_sb*Math.pow((Ta_srf+273.15),4))))*svf);	 //# modified version of eq 11 Loridan et al. (2011)
				Rnprev = ((metKdMinus1*(1.0-albedo))*svf) + ((emiss*(metLdMinus1 - (cs_sb*Math.pow((Ta_srfp+273.15),4))))*svf);
				Rnnext = ((metKdPlus1*(1.0-albedo))*svf) + ((emiss*(metLdPlus1 - (cs_sb*Math.pow((Ta_srfn+273.15),4))))*svf);
				Rnstar = 0.5*(Rnnext - Rnprev);
				
				Kup = metKd0*(1.0-albedo)*svf;
				Lup = cs_sb*Math.pow((Ta_srf+273.15),4);		
				newRn = metKd0*svf + metLd0 - Kup - Lup; 
			}
				
			if (surf.equals("roof"))
			{
				
//				System.out.println("albedo of non-roof surface " + surf + " is " + albedo);
				
				Rn	   = metKd0*(1.0-albedo) + (emiss*(metLd0 - (cs_sb*Math.pow((Ta_srf+273.15),4))))	; //# modified version of eq 11 Loridan et al. (2011)
				Rnprev = metKdMinus1*(1.0-albedo) + (emiss*(metLdMinus1 - (cs_sb*Math.pow((Ta_srfp+273.15),4))));
				Rnnext = metKdPlus1*(1.0-albedo) + (emiss*(metLdPlus1 - (cs_sb*Math.pow((Ta_srfn+273.15),4))));
				Rnstar = 0.5*(Rnnext - Rnprev);
				
				Kup = metKd0*(1.0-albedo);
				Lup = cs_sb*Math.pow((Ta_srf+273.15),4);	
				newRn = metKd0 + metLd0 - Kup - Lup; 
			}

	        

			returnValues.put(Ku_KEY, Kup);
			returnValues.put(Lu_KEY, Lup);
			returnValues.put(newRn_KEY, newRn);
	}


			
		returnValues.put(RN_KEY, Rn);
		returnValues.put(RNPREV_KEY, Rnprev);
		returnValues.put(RNSTAR_KEY, Rnstar);
		
		return returnValues;
}
	
	
	
	
	
}
