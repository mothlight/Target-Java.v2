package Target;

import java.util.ArrayList;

public class ld_mod
{
	
	

//	"""
//	This module can module Ldown if Ldown data is unavailable
//	After Loridan et al. (2010)
//
//	see tech notes appendix 
//
//	"""

	public double ld_mod_(ArrayList<Object> met)
	{	
		double Ta = (double) met.get(MetData.Ta);
		double RH = (double) met.get(MetData.RH);

		return ld_modCalc(Ta,RH);
	}
	
	public double ld_modCalc(double Ta, double RH)
	{
	    double bcof = 0.015+((1.9*(Math.pow(10,-4)))*(Ta));  //# eq 7 Loridan et al., (2010)
	    double flcd = 0.185*((Math.exp(bcof*RH)-1)) ;        //# eq 6 Loridan et al., (2010)
	    double ea =0.611*Math.exp(17.27*Ta/(237.3+Ta))/100*RH;
	    double w = 46.5*(ea/Ta);  // # eq 6 Loridan et al., (2010)
	    double Emis_clr = 1-(1+w)*Math.exp(-Math.sqrt(1.2+(3*w)));

	    double LD = (Emis_clr+(1-Emis_clr)*flcd)*((Math.pow((Ta+273.15),4))*(5.67*(Math.pow(10,-8))));  //## eq 9 Loridan et al., (2010)

	    return LD;
	}
	
}
