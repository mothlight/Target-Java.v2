package Target;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class ForceRestore
{
//	"""
//	calculates surface temperature using force-restore method (Jacobs, 2000)
//	 
//	see section 3.3 tech notes for details
//
//	inputs:
//	    eng_bal     = energy balance dictionary
//	    cs          = constants dictionary
//	    cfM         = main control file
//	    mod_ts      = modelled surface temperature dataframe
//	    mod_tm      = modelled ground temperature dataframe 
//	    surf        = current surface type   
//	    Dats        = dates dictionary
//	    i           = current index 
//	    
//	Outputs:
//	    Ts = modelled surface temperature (Tsurf)
//	    Tm = modelled soil (ground)  temperature (Tm)
//	 
//	 
//	 
//	"""
//	import math
//	from datetime import timedelta
	
	public static final String TS_KEY = "TS";
	public static final String TM_KEY = "TM";
	


	public HashMap<String,Double> Ts_calc_surf(HashMap<String,Double> eng_bal,Cfm cfm, ArrayList<Double> mod_ts,
			ArrayList<Double> mod_tm, HashMap<String,Date> Dats,String surf, int i)
	{
		HashMap<String,Double> returnValues = new HashMap<String,Double>();

		double tS;
		double tM;
		
		String tmstp = cfm.getValue("timestep");                            // # time step (minutes)
		int tmstpInt = new Integer( tmstp.replaceAll("S", "").replaceAll("'", "") ).intValue();
		Date dte = Dats.get("dte");
		Date spinUp = Dats.get("SpinUp");
		int timedelta = 2*tmstpInt*1000;
		
	    if (dte.getTime() <= spinUp.getTime() +  timedelta  )
	    {
				tS=Constants.cs_Ts.get(surf);	//## intial conditions for Tsurf
				tM=Constants.cs_Tm.get(surf);	//## intial conditions for Tm
	    }
	    else
	    {
			double modTsMinus1Surf = mod_ts.get(0);
			double modTmMinus1Surf = mod_tm.get(0);
			double QGS 	= eng_bal.get(Lumps.QG_KEY);		//## calculate ground heat flux 
            
			double D = Math.sqrt((2*Constants.cs_K.get(surf))/((2*Math.PI)/86400.));	//# the damping depth for the annual temperature cycle
			double Dy = D * Math.sqrt(365.);	

			double delta_Tg = ((2/(Constants.cs_C.get(surf)*D)*QGS))-(((2*Math.PI)/86400.)*(modTsMinus1Surf-modTmMinus1Surf));	//## the change in Tsurf per second 
			double delta_Tm = QGS/(Constants.cs_C.get(surf)*Dy);		//## change in Tm per second
                 
			tM = modTmMinus1Surf + (delta_Tm*tmstpInt);		//# update Tm (seconds)
			tS = modTsMinus1Surf + (delta_Tg*tmstpInt);		//# update Tsurf (seconds)
	    }
	    
	    returnValues.put(TS_KEY, tS);
	    returnValues.put(TM_KEY, tM);

	    //return {'TS':tS, 'TM':tM}
	    return returnValues;
	}






}
