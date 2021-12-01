package Target;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class Lumps
{
//	"""
//
//
//	This module calcuates the surface energy balances using the Local-scale Urban Parameterisation Scheme from Grimmond & Oke (2002)
//
//	see section 3.2 tech notes for details
//
//	inputs:
//	    rad     = radiation dictionary
//	    cs      = constants dictionary
//	    cfM     = main control file
//	    met     = met forcing data frame
//	    surf    = current surface type   
//	    Dats    = dates dictionary
//	    i       = current index 
//	    
//	Outputs:
//	    Qe = latent heat flux for surface type
//	    Qh = sensible heat flux for surface type
//	    Qg = ground heat flux for surface type 
//
//
//	"""


//	from datetime import timedelta
	public static final String QH_KEY = "Qh";
	public static final String QG_KEY = "Qg";
	public static final String QE_KEY = "Qe";
	public static final String ALPHAPM_KEY = "alphapm";

	public HashMap<String,Double> lumps(HashMap<String,Double> rad, Cfm cfm, ArrayList<ArrayList<Object>> met, String surf, HashMap<String,Date> Dats, int i)
	{
		

		
		HashMap<String,Double> returnValues = new HashMap<String,Double> ();

		double rn = rad.get(RnCalcNew.RN_KEY);
		double rnstar = rad.get(RnCalcNew.RNSTAR_KEY);
		ArrayList<Object> met0 = met.get(i);			
		double metTa0 = (double)met0.get(MetData.Ta);
		double metP0 = (double)met0.get(MetData.P);
		
	 
		
        double Qh;
        double Qe;
        double Qg;
        double alphapm;
		
		String tmstp = cfm.getValue("timestep");                            // # time step (minutes)
		int tmstpInt = new Integer( tmstp.replaceAll("S", "").replaceAll("'", "") ).intValue();
		Date dte = Dats.get("dte");
		Date spinUp = Dats.get("SpinUp");
		int timedelta = 2*tmstpInt*1000;
		
	    if (dte.getTime() <= spinUp.getTime() +  timedelta  )
	    {
	        Qh=0.;
	        Qe=0.;
	        Qg=0.;
	        alphapm=0.;
	    }
	    
	    else
	    {	    	
	        Qg = (Constants.cs_LUMPS1.get(surf).get(0)*rn) 
	        		+ (Constants.cs_LUMPS1.get(surf).get(1)*rnstar) 
	        		+ (Constants.cs_LUMPS1.get(surf).get(2));
	    }
	    
	   // ## ALPHA PARAMETER	    
	    alphapm = Constants.cs_alphapm.get(surf);
	    
	  //  ##  BETA PARAMETER 	    
	    double betA = Constants.cs_beta.get(surf);
	    		     
	      
	    double Lambda =  2.501 - 0.002361*metTa0 ;                               // # MJ / kg -  latent heat of vaporization

	    double gamma  = ((metP0/10)*Constants.cs_cp) / (Constants.cs_e*Lambda);                         //# kPa / C- psychrometric constant
	    double ew = 6.1121*Math.pow((1.0007+3.46e-6*(metP0/10)),((17.502*(metTa0))/(240.97+(metTa0))));        //# in kPa
	    double s  = 0.62197*(ew/((metP0/10)-0.378*ew)) ;


	    Qh = ((((1.-alphapm) + gamma/s) / (1. + gamma/s) ) * (rn-Qg)) - betA;
	    Qe = (alphapm/(1.+(gamma/s))) * (rn - Qg) + betA;
	    
		returnValues.put(QH_KEY, Qh);
		returnValues.put(QG_KEY, Qg);
		returnValues.put(QE_KEY, Qe);
		returnValues.put(ALPHAPM_KEY, alphapm);

	    return returnValues;
	}
	
	
	
	public HashMap<String,Double> lumps(HashMap<String,Double> rad, Cfm cfm, double metTa0, double metP0, String surf, HashMap<String,Date> Dats)
	{
		

		
		HashMap<String,Double> returnValues = new HashMap<String,Double> ();

		double rn = rad.get(RnCalcNew.RN_KEY);
		double rnstar = rad.get(RnCalcNew.RNSTAR_KEY);
//		ArrayList<Object> met0 = met.get(i);			
//		double metTa0 = (double)met0.get(MetData.Ta);
//		double metP0 = (double)met0.get(MetData.P);
		
	 
		
        double Qh;
        double Qe;
        double Qg;
        double alphapm;
		
		String tmstp = cfm.getValue("timestep");                            // # time step (minutes)
		int tmstpInt = new Integer( tmstp.replaceAll("S", "").replaceAll("'", "") ).intValue();
		Date dte = Dats.get("dte");
		Date spinUp = Dats.get("SpinUp");
		int timedelta = 2*tmstpInt*1000;
		
	    if (dte.getTime() <= spinUp.getTime() +  timedelta  )
	    {
	        Qh=0.;
	        Qe=0.;
	        Qg=0.;
	        alphapm=0.;
	    }
	    
	    else
	    {	    	
	        Qg = (Constants.cs_LUMPS1.get(surf).get(0)*rn) 
	        		+ (Constants.cs_LUMPS1.get(surf).get(1)*rnstar) 
	        		+ (Constants.cs_LUMPS1.get(surf).get(2));
	    }
	    
	   // ## ALPHA PARAMETER	    
	    alphapm = Constants.cs_alphapm.get(surf);
	    
	  //  ##  BETA PARAMETER 	    
	    double betA = Constants.cs_beta.get(surf);
	    		     
	      
	    double Lambda =  2.501 - 0.002361*metTa0 ;                               // # MJ / kg -  latent heat of vaporization

	    double gamma  = ((metP0/10)*Constants.cs_cp) / (Constants.cs_e*Lambda);                         //# kPa / C- psychrometric constant
	    double ew = 6.1121*Math.pow((1.0007+3.46e-6*(metP0/10)),((17.502*(metTa0))/(240.97+(metTa0))));        //# in kPa
	    double s  = 0.62197*(ew/((metP0/10)-0.378*ew)) ;


	    Qh = ((((1.-alphapm) + gamma/s) / (1. + gamma/s) ) * (rn-Qg)) - betA;
	    Qe = (alphapm/(1.+(gamma/s))) * (rn - Qg) + betA;
	    
		returnValues.put(QH_KEY, Qh);
		returnValues.put(QG_KEY, Qg);
		returnValues.put(QE_KEY, Qe);
		returnValues.put(ALPHAPM_KEY, alphapm);

	    return returnValues;
	}

}
