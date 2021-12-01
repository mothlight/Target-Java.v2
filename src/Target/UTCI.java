package Target;

public class UTCI
{
	



	
	public static double SurfAlbedo = 0.4;
	public static double stefanb = 0.000000056696;
	public static double diamGlobe = 0.05; // 0.05 = 50mm diam globe
	public static double diamWick = 0.007;
	public static double lenWick = 0.0254;
	public static double propDirect = 0.8;  // Assume a proportion of direct radiation = direct/(diffuse + direct)
	public static double ZenithAngle = 0;  // angle of sun from directly above
	public static double MinWindSpeed = 0.1;   // 0 wind speed upsets log function
	public static double AtmPressure = 101;  // Atmospheric pressure in kPa
	
	public static final double ERROR_RETURN = -9999;

	public static void main(String[] args)
	{
		
		UTCI utci = new UTCI();
		
		long start = System.currentTimeMillis();		
		double utciValue = utci.getUTCIForGrid_RH(21.5718096957703, 0.65358400732021216, 57, 20.4319988469031);
		System.out.println(utciValue);
		long time = System.currentTimeMillis() - start;
		System.out.printf("Took %.5f%n", time/1e3);
//		(21.5718096957703, 0.65358400732021216, 57, 20.4319988469031, 21.2979358451644)
		System.exit(1);
		
		
		double Ta = 33.0;
        double Tg = -99;
//		double Tg = 34;
        double Td = -99;
//		double Td = 31;
        double ws = 1.0;
        double solar = 222;
//        double solar = -99;
        double RH = 50;
//        double RH = -99;
        
		utci = new UTCI();
//		double returnValue = utci.fUTCI(Ta, Tg, Td, ws, solar, RH);
//		System.out.println("fUTCI=" + returnValue);
//		System.out.println("");
//		double returnValue2 = utci.fWBGTo(Ta, Tg, Td, ws, solar, RH);
//		System.out.println("fWBGTo=" + returnValue2); // this should be 28.2
//		System.out.println("");
//		double returnValue3 = utci.fWBGTi(Ta, Td, ws, RH);
//		System.out.println("fWBGTi=" + returnValue3);
//		System.out.println("");
		double returnValue4 = utci.fMRT(Ta, Tg, solar, ws);
		System.out.println("fMRT=" + returnValue4);
		
		double tmrt = 45.5;
		
		double returnValue = utci.fUTCI(Ta, 
				//Tg, Td, 
				ws, 
				//solar, 
				RH, tmrt);
		System.out.println("fUTCI=" + returnValue);
		
		

	}
	
	public double fTmrt(double Ta, double Tg, double ws)
	{
	//Thorrson calculation of mean radiant temperature
		double fTmrt = Math.pow(( Math.pow((Tg + 273.15), 4) + 1.335 *  Math.pow(ws, 0.71) * (Tg - Ta) / (0.95 *  Math.pow(0.15, 0.4) ) * 100000000), 0.25) - 273.15;    //takes Tg and changes it into Tr)
		return fTmrt;
	}
	
	
	
	
	public double fMRT(double Ta, double Tg, double solar, double ws)
	{
		//Use the solar to get the globe temperature (Liljegren) then use the globe temperature to get MRT using bernards formula
		double fMRT;
		if (solar == -99 
				//|| solar = ""
				) 
		{
			fMRT = fTmrtB(Ta, Tg, ws);
		}
		else if (Tg == -99 
				//|| Tg = ""
				) 
		{
			double RH = 60;  //assume a value - not very sensitive to RH
			Tg = fTg(Ta, RH, AtmPressure, ws, solar, propDirect, ZenithAngle, MinWindSpeed);
//			System.out.println("Tg in fMRT=" + Tg);
			fMRT = fTmrtB(Ta, Tg, ws);
		}
		else
		{
			System.err.println("Error:");
//			fMRT = "Error:"
			return ERROR_RETURN;
		}
		return fMRT;

	}
	
	

	 
	 



public double fWBGTo(double Ta, double Tg, double Td, double ws, double solar, double RH)
{
	//check for blank entry
//	if (RH = "" && Td = "") 
//	{
//		fWBGTo = "": Exit Function
//	}
//	if (Tg = "" && solar = "") 
//	{
//		fWBGTo = "": Exit Function
//	}
	if (RH == -99 && Td == -99) 
	{
//		Exit Function
		return ERROR_RETURN;
	}
	if (Tg == -99 && solar == -99) 
	{
//		Exit Function
		return ERROR_RETURN;
	}
	//Calculate RH given Td, and Td given RH
	if (RH != -99 
			//&& RH <> ""
			) 
	{
	    if (Td != -99 
	    		//&& Td <> ""
	    		) 
	    {
	    	System.err.println("Error:");
//	    	fWBGTo = "Error:": Exit Function
	    	return ERROR_RETURN;
	    }
	    Td = fTd(Ta, RH);
	}
	else
	{
	    RH = fRH(Ta, Td);
	}
	//Check to make sure Td < Ta
	if (Td > Ta) 
	{
    	System.err.println("Error:");
//    	fWBGTo = "Error:": Exit Function
    	return ERROR_RETURN;
	}
	
	//Calculate Tg given solar, calculate solar given Tg
	if (solar != -99 
			//&& solar <> ""
			) 
	{
	    if (Tg != -99 
	    		//&& Tg <> ""
	    		) 
	    {
	    	System.err.println("Error:");
//	    	fWBGTo = "Error:": Exit Function
	    	return ERROR_RETURN;
	    }
	    Tg = fTg(Ta, RH, AtmPressure, ws, solar, propDirect, ZenithAngle, MinWindSpeed);
//	    System.out.println("Tg in fWBGTo=" + Tg);
	}
	else
	{
	//Calculate solar by iteration of Tg - in big steps then small steps
		int smallsol=100;
		for (int sol = 100 ; sol <= 2000 ; sol+=100)
		{
//	    For sol = 100 To 2000 Step 100
	        double estTg = fTg(Ta, RH, AtmPressure, ws, sol, propDirect, ZenithAngle, MinWindSpeed);
//	        System.out.println("Tg in fWBGTo2=" + Tg);
	        if (estTg >= Tg) 
	        {
	        	smallsol = sol;
//	        	: Exit For
	        	break;
	        }
		}
	    
		for (int sol = smallsol -100 ; sol <= smallsol ; sol+=10)
		{
//	    For sol = smallsol - 100 To smallsol Step 10
	        double estTg = fTg(Ta, RH, AtmPressure, ws, sol, propDirect, ZenithAngle, MinWindSpeed);
//	        System.out.println("Tg in fWBGTo3=" + Tg);
	        if (estTg >= Tg) 
	        {
	        	solar = sol - 5;
//	        	: Exit For
	        	break;
	        }
	    }
//	    Next
	}
	double Tnwb = fTwb(Ta, Td, RH, AtmPressure, ws, MinWindSpeed, solar, propDirect, ZenithAngle, 1);
	double fWBGTo = 0.7 * Tnwb + 0.2 * Tg + 0.1 * Ta;
	return fWBGTo;

}




public double  fTwb(double Ta, double Td, double relh, double Pair, double speed, double speedMin, double solar, double fdir, double zenith, double irad)
{
//  Purpose: to calculate the natural wet bulb temperature
//  Author:  James C. Liljegren
//       Decision and Information Sciences Division
//       Argonne National Laboratory
// irad=1 -> include radiation; irad=0 -> no radiation (psychrometric web bulb temp)
// Pressure in kPa (Atm =101 kPa)
	double fTwb =0;
    Pair = Pair * 10;
    double emis_wick = 0.95;
    double alb_wick = 0.4;
    double emis_sfc = 0.999;
    double alb_sfc = SurfAlbedo;
    double converge = 0.05;
    double ratio = 1003.5 * 28.97 / 18.015;
    double Pr = 1003.5 / (1003.5 + (1.25 * 8314.34 / 28.97));
    //Fix up out-of bounds problems with zenith
    if (zenith <= 0) 
    {
    	zenith = 0.0000000001;
    }
    if (solar > 0 && zenith > 1.57) 
    {
    	zenith = 1.57;
    }
    if (solar > 15 && zenith > 1.54) 
    {
    	zenith = 1.54;
    }
    if (solar > 900 && zenith > 1.52) 
    {
    	zenith = 1.52;
    }
    double Tdew = Td + 273.15;
    double Tair = Ta + 273.15;
    double RH = relh * 0.01;
    double eair = RH * esat(Tair);
    double emis_at = 0.575 * Math.pow(eair, 0.143);
    double Tsfc = Tair;
    double Twb_prev = Tdew; // First guess is the dew point temperature
    
	//Do iteration
	int testno = 1;
	boolean continueLoop = true;
	while(continueLoop)
	{
//		Reiter:
		testno = testno + 1;
		if (testno > 1000) 
		{
			System.err.println("No convergence: values too extreme");
	//		Cells(2, 4) = "No convergence: values too extreme"
	//		Exit Function
			return ERROR_RETURN;
		}
		
	    double Tref = 0.5 * (Twb_prev + Tair); // Evaluate properties at the average temperature
	    //double evap = (313.15 - Twb_prev) / 30# * (-71100#) + 2407300;
	    double evap = (313.15 - Twb_prev) / 30 * (-71100) + 2407300;
	    double h = h_cylinder_in_air(Twb_prev, Pair, speed, speedMin);
	    double Fatm = stefanb * emis_wick * (0.5 * (emis_at * Math.pow(Tair, 4) + emis_sfc * Math.pow(Tsfc, 4)) - Math.pow(Twb_prev, 4)) + (1 - alb_wick) * solar * ((1 - fdir) * (1 + 0.25 * diamWick / lenWick) + ((Math.tan(zenith) / 3.1416) + 0.25 * diamWick / lenWick) * fdir + alb_sfc);
	    double ewick = esat(Twb_prev);
	    double density = Pair * 100 / (Tair * 8314.34 / 28.97);
	    double Sc = viscosity(Tair) / (density * diffusivity(Tref, Pair));
	    double Twb = Tair - evap / ratio * (ewick - eair) / (Pair - ewick) * Math.pow((Pr / Sc), 0.56) + Fatm / h * irad;
	    double dT = Twb - Twb_prev;
	    if (dT < -converge) 
	    {
	        Twb_prev = 0.9 * Twb_prev - 0.1 * Twb;
//	        GoTo Reiter
	        continueLoop = true;
	    }
	    else if (dT > converge) 
	    {
	        Twb_prev = 0.9 * Twb_prev + 0.1 * Twb;
//	        GoTo Reiter
	        continueLoop = true;
	    }
	    else
	    {
	        fTwb = Twb - 273.15;
	        continueLoop = false;
	    }
	}
	return fTwb;
}
   

    
public double  fTg(double Ta, double relh, double Pair, double speed, double solar, double fdir, double zenith, double speedMin)
{
//  Purpose: to calculate the globe temperature
//  Author:  James C. Liljegren
//       Decision and Information Sciences Division
//       Argonne National Laboratory
// Pressure in kPa (Atm =101 kPa)
//Fix up out-of bounds problems with zenith
    if (zenith <= 0) 
    {
    	zenith = 0.0000000001;
    }
    if (zenith > 1.57) 
    {
    	zenith = 1.57;
    }
    Pair = Pair * 10;
    double cza = Math.cos(zenith);
    double converge = 0.05;
    double alb_sfc = SurfAlbedo;
    double alb_globe = 0.05;
    double emis_globe = 0.95;
    double emis_sfc = 0.999;
    double Tair = Ta + 273.15;
    double RH = relh * 0.01;
    double Tsfc = Tair;
    double Tglobe_prev = Tair;
    
	//Do iteration
	int testno = 1;
	boolean continueLoop = true;
	double Tglobe=0;
	while (continueLoop)
	{
//		Reit:
		testno = testno + 1;
		if (testno > 1000) 
		{
			System.err.println("No convergence: values too extreme");
			//Cells(2, 4) = "No convergence: values too extreme"
			//Exit Function
			return ERROR_RETURN;
		}
	
	    double Tref = 0.5 * (Tglobe_prev + Tair); // Evaluate properties at the average temperature
	    double h = h_sphere_in_air(Tref, Pair, speed, speedMin);
	    Tglobe = Math.pow((0.5 * (emis_atm(Tair, RH) * Math.pow(Tair, 4) + emis_sfc * Math.pow(Tsfc, 4) ) - h / (emis_globe * stefanb) * (Tglobe_prev - Tair) + solar / (2 * emis_globe * stefanb) * (1 - alb_globe) * (fdir * (1 / (2 * cza) - 1) + 1 + alb_sfc)), 0.25);
	    double dT = Tglobe - Tglobe_prev;
	    if (Math.abs(dT) < converge) 
	    {
	       Tglobe = Tglobe - 273.15;
	       continueLoop = false;
	    }
	    else
	    {
	       Tglobe_prev = (0.9 * Tglobe_prev + 0.1 * Tglobe);
//	       GoTo Reit
	       continueLoop = true;
	    }
	}
    double fTg = Tglobe;
    return fTg;
}
    
    






public double fTmrtB(double Ta, double Tg, double ws)
{
	//Bernard
	double WF1 = 0.4 * Math.pow((Math.abs(Tg - Ta)), 0.25);
	double WF2 = 2.5 * Math.pow(ws, 0.6);
	double WF;
	if (WF1 > WF2) 
	{
		// if the wind speed is low
		WF = WF1 ;
	}
	else 
	{
		WF = WF2;
	}
	double fTmrtB = 100 * Math.pow((Math.pow(((Tg + 273.15) / 100), 4) + WF * (Tg - Ta)), 0.25) - 273.15;
	return fTmrtB;
}


public double fMRTmod(double Ta, double solar)
{
	//Calculated the mean radiant temperature from the solar radiation.  Modified based on direct and diffuse.  Gives values too small
	//Assumes a uniform surround temperature of Ta and short wave solar radiation only
	double solardirect;
	double solardiffuse;
	if (solar > 1000) 
	{
		solardirect = 0.75 * solar;
		solardiffuse = 0.25 * solar;
	}
	else if (solar > 250) 
	{
		//Gradually ramp up indirect component from 0.25 @ 1000 to 1 @ 250
		solardiffuse = (-0.001 * solar + 1.25) * solar;
		solardirect = (0.001 * solar - 0.25) * solar;
	}
	else 
	{
		solardirect = 0;
		solardiffuse = solar;
	}
	double fMRTmod = Math.pow(
			(0.97 * Math.pow((Ta + 273.2), 4 )
					+ 0.7 * solardiffuse / (0.97 * 0.0000000567) 
			 + 0.32 * 0.7 * solardirect / (0.97 * 0.0000000567)), 0.25) - 273.2;
	return fMRTmod;
}


public double  fTd(double Ta, double RH)
{
	//Calculation of dew point from RH
	double RHD = RH / 100;
	double fTd = 237.3 * (Math.log(RHD) / 17.27 + Ta / (237.3 + Ta)) / (1 - Math.log(RHD) / 17.27 - Ta / (237.3 + Ta));
	return fTd;
}


public double fRH(double Ta, double Td)
{
	//Calculation of RH from dew point
	double fRH = 100 * Math.exp(17.27 * Td / (237.7 + Td) - 17.27 * Ta / (237.7 + Ta));
	return fRH;
}


public double fWBGTi(double Ta, double Td, double ws, double RH)
{
	// Psychrometric Wet bulb temperature calculation
	 if (Td > Ta) 
	 {
		 System.out.println("Error:");
		 //fWBGTi = "Error:"
		 //GoTo LastLine
		 return ERROR_RETURN;
	 }
 
    if (ws < 0.1) 
    {
    	ws = 0.1;
    }
    if (
    		//RH <> "" && 
    		RH != -99) 
    {
    	Td = fTd(Ta, RH);
    }
    double Tw = Td;
    double Diff = 10000;
    double Diffold = Diff;
    double Ed = 0.6106 * Math.exp(17.27 * Td / (237.3 + Td));
    while (Math.abs(Diff) + Math.abs(Diffold) == Math.abs(Diff + Diffold) )
    {
         Diffold = Diff;
         double Ew = 0.6106 * Math.exp(17.27 * Tw / (237.3 + Tw));
         Diff = 1556 * Ed + 101 * Ta - 1556 * Ew + 1.484 * Ew * Tw - 1.484 * Ed * Tw - 101 * Tw;
         Tw = Tw + 0.2;
         if (Tw > Ta) 
         {
//        	 Exit Do
        	 break;
         }
//    Loop While Math.abs(Diff) + Math.abs(Diffold) = Math.abs(Diff + Diffold)
    }
    		
    if (Tw > Td + 0.3) 
    {
        Tw = Tw - 0.3;
    }
    else
    {
        Tw = Td;
    }
    double fWBGTi = 0.67 * Tw + 0.33 * Ta - 0.048 * Math.log(ws) / Math.log(10) * (Ta - Tw);
    return fWBGTi;

}


public double fUTCI(double Ta, double Tg, double Td, double ws, double solar, double RH)
{
//UTCI calculation using full formula from utci.org website
// Check if one and only one of RH or Td have been entered
  double D_Tmrt; 
 
  if (RH != -99)
  {
	  if (Td != -99)
	  {
		  System.err.println("Error:");
		  return ERROR_RETURN;
	  }
	  Td = fTd(Ta, RH);
  }
  else
  {
	  RH = fRH(Ta, Td);
  }
  
	//Check to make sure Td < Ta
	if (Td > Ta) 
	{
		System.err.println("Error:");
		return ERROR_RETURN;
//		fUTCI = "Error:":  Exit Function
	}
	
	//Calculate Tg given solar, calculate solar given Tg
	if (solar != -99)
	{
		if (Tg != -99)
		{
			System.err.println("Error:");
			return ERROR_RETURN;
		}
		Tg = fTg(Ta, RH, AtmPressure, ws, solar, propDirect, ZenithAngle, MinWindSpeed);
	}

	double Tmrt = fTmrtB(Ta, Tg, ws); //takes Tg and changes it into Tmrt uses Bernard formula

    // UTCI, Version a 0.002, October 2009
    // Copyright (C) 2009  Peter Broede
    // Program for calculating UTCI Temperature (UTCI)
    // released for public use after termination of COST Action 730
    // Copyright (C) 2009  Peter Broede
    // This program is distributed in the hope that it will be useful,
    // but WITHOUT ANY WARRANTY; without even the implied warranty of
    // MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

// Disclaimer of Warranty.
// THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN OTHERWISE STATED IN WRITING
// THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM “AS IS” WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED
// OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
// THE ENTIRE RISK AS TO THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE, YOU
// ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.

// Limitation of Liability.
// IN NO EVENT UNLESS REQUIRED BY APPLICABLE LAW OR AGREED TO IN WRITING WILL ANY COPYRIGHT HOLDER, OR ANY OTHER PARTY WHO
// MODIFIES AND/OR CONVEYS THE PROGRAM AS PERMITTED ABOVE, BE LIABLE TO YOU FOR DAMAGES, INCLUDING ANY GENERAL, SPECIAL,
// INCIDENTAL OR CONSEQUENTIAL DAMAGES ARISING OUT OF THE USE OR INABILITY TO USE THE PROGRAM
// (INCLUDING BUT NOT LIMITED TO LOSS OF DATA OR DATA BEING RENDERED INACCURATE OR LOSSES SUSTAINED BY YOU OR THIRD PARTIES
// OR A FAILURE OF THE PROGRAM TO OPERATE WITH ANY OTHER PROGRAMS), EVEN IF SUCH HOLDER OR OTHER PARTY HAS BEEN ADVISED
// OF THE POSSIBILITY OF SUCH DAMAGES.

 // DOUBLE PRECISION Function value is the UTCI in degree Celsius
 // computed by a 6th order approximating polynomial from the 4 Input paramters
 //
 // Input parameters (all of type DOUBLE PRECISION)
 // - Ta       : air temperature, degree Celsius
 // - ehPa    : water vapour presure, hPa=hecto Pascal
 // - Tmrt   : mean radiant temperature, degree Celsius
 // - va10m  : wind speed 10 m above ground level in m/s
 //
 //  UTCI_approx, Version a 0.002, October 2009
 //  Copyright (C) 2009  Peter Broede

      
      double PA = 0.6108 * Math.exp(17.29 * Td / (Td + 237.3)); //takes Td and changes it into Pa - vapour pressure in hPa)
      D_Tmrt = Tmrt - Ta;
        // calculate 6th order polynomial as approximation
      double ET1 = Ta + 0.607562052 - 0.0227712343 * Ta + 0.000806470249 * Ta * Ta - 0.000154271372 * Ta * Ta * Ta 
        - 0.00000324651735 * Ta * Ta * Ta * Ta + 7.32602852E-08 * Ta * Ta * Ta * Ta * Ta + 1.35959073E-09 * Ta * Ta * Ta * Ta * Ta * Ta 
        - 2.2583652 * ws + 0.0880326035 * Ta * ws + 0.00216844454 * Ta * Ta * ws - 0.0000153347087 * Ta * Ta * Ta * ws 
        - 0.000000572983704 * Ta * Ta * Ta * Ta * ws - 2.55090145E-09 * Ta * Ta * Ta * Ta * Ta * ws 
        - 0.751269505 * ws * ws - 0.00408350271 * Ta * ws * ws - 0.0000521670675 * Ta * Ta * ws * ws 
        + 0.00000194544667 * Ta * Ta * Ta * ws * ws + 1.14099531E-08 * Ta * Ta * Ta * Ta * ws * ws 
        + 0.158137256 * ws * ws * ws - 0.0000657263143 * Ta * ws * ws * ws + 0.000000222697524 * Ta * Ta * ws * ws * ws - 4.16117031E-08 * Ta * Ta * Ta * ws * ws * ws 
        - 0.0127762753 * ws * ws * ws * ws + 0.00000966891875 * Ta * ws * ws * ws * ws + 2.52785852E-09 * Ta * Ta * ws * ws * ws * ws 
        + 0.000456306672 * ws * ws * ws * ws * ws - 0.000000174202546 * Ta * ws * ws * ws * ws * ws - 0.00000591491269 * ws * ws * ws * ws * ws * ws 
        + 0.398374029 * D_Tmrt + 0.000183945314 * Ta * D_Tmrt - 0.00017375451 * Ta * Ta * D_Tmrt 
        - 0.000000760781159 * Ta * Ta * Ta * D_Tmrt + 3.77830287E-08 * Ta * Ta * Ta * Ta * D_Tmrt + 5.43079673E-10 * Ta * Ta * Ta * Ta * Ta * D_Tmrt 
        - 0.0200518269 * ws * D_Tmrt + 0.000892859837 * Ta * ws * D_Tmrt + 0.00000345433048 * Ta * Ta * ws * D_Tmrt 
        - 0.000000377925774 * Ta * Ta * Ta * ws * D_Tmrt - 1.69699377E-09 * Ta * Ta * Ta * Ta * ws * D_Tmrt + 0.000169992415 * ws * ws * D_Tmrt 
        - 0.0000499204314 * Ta * ws * ws * D_Tmrt + 0.000000247417178 * Ta * Ta * ws * ws * D_Tmrt + 1.07596466E-08 * Ta * Ta * Ta * ws * ws * D_Tmrt 
        + 0.0000849242932 * ws * ws * ws * D_Tmrt + 0.00000135191328 * Ta * ws * ws * ws * D_Tmrt - 6.21531254E-09 * Ta * Ta * ws * ws * ws * D_Tmrt 
        - 0.00000499410301 * ws * ws * ws * ws * D_Tmrt - 1.89489258E-08 * Ta * ws * ws * ws * ws * D_Tmrt + 8.15300114E-08 * ws * ws * ws * ws * ws 
        * D_Tmrt + 0.00075504309 * D_Tmrt * D_Tmrt;
        
      double ET3 = -0.0000565095215 * Ta * D_Tmrt * D_Tmrt + 
        (-0.000000452166564) * Ta * Ta * D_Tmrt * D_Tmrt + 
        (2.46688878E-08) * Ta * Ta * Ta * D_Tmrt * D_Tmrt + 
        (2.42674348E-10) * Ta * Ta * Ta * Ta * D_Tmrt * D_Tmrt + 
        (0.00015454725) * ws * D_Tmrt * D_Tmrt + 
        (0.0000052411097) * Ta * ws * D_Tmrt * D_Tmrt + 
        (-8.75874982E-08) * Ta * Ta * ws * D_Tmrt * D_Tmrt + 
        (-1.50743064E-09) * Ta * Ta * Ta * ws * D_Tmrt * D_Tmrt + 
        (-0.0000156236307) * ws * ws * D_Tmrt * D_Tmrt + 
        (-0.000000133895614) * Ta * ws * ws * D_Tmrt * D_Tmrt + 
        (2.49709824E-09) * Ta * Ta * ws * ws * D_Tmrt * D_Tmrt + 
        (0.000000651711721) * ws * ws * ws * D_Tmrt * D_Tmrt + 
        (1.94960053E-09) * Ta * ws * ws * ws * D_Tmrt * D_Tmrt + 
        (-1.00361113E-08) * ws * ws * ws * ws * D_Tmrt * D_Tmrt + 
        (-0.0000121206673) * D_Tmrt * D_Tmrt * D_Tmrt + 
        (-0.00000021820366) * Ta * D_Tmrt * D_Tmrt * D_Tmrt + 
        (7.51269482E-09) * Ta * Ta * D_Tmrt * D_Tmrt * D_Tmrt + 
        (9.79063848E-11) * Ta * Ta * Ta * D_Tmrt * D_Tmrt * D_Tmrt + 
        (0.00000125006734) * ws * D_Tmrt * D_Tmrt * D_Tmrt + 
        (-1.81584736E-09) * Ta * ws * D_Tmrt * D_Tmrt * D_Tmrt + 
        (-3.52197671E-10) * Ta * Ta * ws * D_Tmrt * D_Tmrt * D_Tmrt + 
        (-0.000000033651463) * ws * ws * D_Tmrt * D_Tmrt * D_Tmrt + 
        (1.35908359E-10) * Ta * ws * ws * D_Tmrt * D_Tmrt * D_Tmrt + 
        (4.1703262E-10) * ws * ws * ws * D_Tmrt * D_Tmrt * D_Tmrt + 
        (-1.30369025E-09) * D_Tmrt * D_Tmrt * D_Tmrt * D_Tmrt;
        
      double  ET4 = 4.13908461E-10 * Ta * D_Tmrt * D_Tmrt * D_Tmrt * D_Tmrt + 
        (9.22652254E-12) * Ta * Ta * D_Tmrt * D_Tmrt * D_Tmrt * D_Tmrt + 
        (-5.08220384E-09) * ws * D_Tmrt * D_Tmrt * D_Tmrt * D_Tmrt + 
        (-2.24730961E-11) * Ta * ws * D_Tmrt * D_Tmrt * D_Tmrt * D_Tmrt + 
        (1.17139133E-10) * ws * ws * D_Tmrt * D_Tmrt * D_Tmrt * D_Tmrt + 
        (6.62154879E-10) * D_Tmrt * D_Tmrt * D_Tmrt * D_Tmrt * D_Tmrt + 
        (4.0386326E-13) * Ta * D_Tmrt * D_Tmrt * D_Tmrt * D_Tmrt * D_Tmrt + 
        (1.95087203E-12) * ws * D_Tmrt * D_Tmrt * D_Tmrt * D_Tmrt * D_Tmrt + 
        (-4.73602469E-12) * D_Tmrt * D_Tmrt * D_Tmrt * D_Tmrt * D_Tmrt * D_Tmrt + 
        (5.12733497) * PA + 
        (-0.312788561) * Ta * PA + 
        (-0.0196701861) * Ta * Ta * PA + 
        (0.00099969087) * Ta * Ta * Ta * PA + 
        (0.00000951738512) * Ta * Ta * Ta * Ta * PA + 
        (-0.000000466426341) * Ta * Ta * Ta * Ta * Ta * PA + 
        (0.548050612) * ws * PA + 
        (-0.00330552823) * Ta * ws * PA + 
        (-0.0016411944) * Ta * Ta * ws * PA + 
        (-0.00000516670694) * Ta * Ta * Ta * ws * PA + 
        (0.000000952692432) * Ta * Ta * Ta * Ta * ws * PA + 
        (-0.0429223622) * ws * ws * PA + 
        (0.00500845667) * Ta * ws * ws * PA + 
        (0.00000100601257) * Ta * Ta * ws * ws * PA + 
        (-0.00000181748644) * Ta * Ta * Ta * ws * ws * PA + 
        (-0.00125813502) * ws * ws * ws * PA;
        
      double  ET5 = -0.000179330391 * Ta * ws * ws * ws * PA + 
        (0.00000234994441) * Ta * Ta * ws * ws * ws * PA + 
        (0.000129735808) * ws * ws * ws * ws * PA + 
        (0.0000012906487) * Ta * ws * ws * ws * ws * PA + 
        (-0.00000228558686) * ws * ws * ws * ws * ws * PA + 
        (-0.0369476348) * D_Tmrt * PA + 
        (0.00162325322) * Ta * D_Tmrt * PA + 
        (-0.000031427968) * Ta * Ta * D_Tmrt * PA + 
        (0.00000259835559) * Ta * Ta * Ta * D_Tmrt * PA + 
        (-4.77136523E-08) * Ta * Ta * Ta * Ta * D_Tmrt * PA + 
        (0.0086420339) * ws * D_Tmrt * PA + 
        (-0.000687405181) * Ta * ws * D_Tmrt * PA + 
        (-0.00000913863872) * Ta * Ta * ws * D_Tmrt * PA + 
        (0.000000515916806) * Ta * Ta * Ta * ws * D_Tmrt * PA + 
        (-0.0000359217476) * ws * ws * D_Tmrt * PA + 
        (0.0000328696511) * Ta * ws * ws * D_Tmrt * PA + 
        (-0.000000710542454) * Ta * Ta * ws * ws * D_Tmrt * PA + 
        (-0.00001243823) * ws * ws * ws * D_Tmrt * PA + 
        (-0.000000007385844) * Ta * ws * ws * ws * D_Tmrt * PA + 
        (0.000000220609296) * ws * ws * ws * ws * D_Tmrt * PA + 
        (-0.00073246918) * D_Tmrt * D_Tmrt * PA + 
        (-0.0000187381964) * Ta * D_Tmrt * D_Tmrt * PA + 
        (0.00000480925239) * Ta * Ta * D_Tmrt * D_Tmrt * PA + 
        (-0.000000087549204) * Ta * Ta * Ta * D_Tmrt * D_Tmrt * PA + 
        (0.000027786293) * ws * D_Tmrt * D_Tmrt * PA;
        
      double  ET6 = -0.00000506004592 * Ta * ws * D_Tmrt * D_Tmrt * PA + 
        (0.000000114325367) * Ta * Ta * ws * D_Tmrt * D_Tmrt * PA + 
        (0.00000253016723) * ws * ws * D_Tmrt * D_Tmrt * PA + 
        (-1.72857035E-08) * Ta * ws * ws * D_Tmrt * D_Tmrt * PA + 
        (-3.95079398E-08) * ws * ws * ws * D_Tmrt * D_Tmrt * PA + 
        (-0.000000359413173) * D_Tmrt * D_Tmrt * D_Tmrt * PA + 
        (0.000000704388046) * Ta * D_Tmrt * D_Tmrt * D_Tmrt * PA + 
        (-1.89309167E-08) * Ta * Ta * D_Tmrt * D_Tmrt * D_Tmrt * PA + 
        (-0.000000479768731) * ws * D_Tmrt * D_Tmrt * D_Tmrt * PA + 
        (7.96079978E-09) * Ta * ws * D_Tmrt * D_Tmrt * D_Tmrt * PA + 
        (1.62897058E-09) * ws * ws * D_Tmrt * D_Tmrt * D_Tmrt * PA + 
        (3.94367674E-08) * D_Tmrt * D_Tmrt * D_Tmrt * D_Tmrt * PA + 
        (-1.18566247E-09) * Ta * D_Tmrt * D_Tmrt * D_Tmrt * D_Tmrt * PA + 
        (3.34678041E-10) * ws * D_Tmrt * D_Tmrt * D_Tmrt * D_Tmrt * PA + 
        (-1.15606447E-10) * D_Tmrt * D_Tmrt * D_Tmrt * D_Tmrt * D_Tmrt * PA + 
        (-2.80626406) * PA * PA + 
        (0.548712484) * Ta * PA * PA + 
        (-0.0039942841) * Ta * Ta * PA * PA + 
        (-0.000954009191) * Ta * Ta * Ta * PA * PA + 
        (0.0000193090978) * Ta * Ta * Ta * Ta * PA * PA + 
        (-0.308806365) * ws * PA * PA + 
        (0.0116952364) * Ta * ws * PA * PA + 
        (0.000495271903) * Ta * Ta * ws * PA * PA + 
        (-0.0000190710882) * Ta * Ta * Ta * ws * PA * PA + 
        (0.00210787756) * ws * ws * PA * PA;
        
      double  ET7 = -0.000698445738 * Ta * ws * ws * PA * PA + 
        (0.0000230109073) * Ta * Ta * ws * ws * PA * PA + 
        (0.00041785659) * ws * ws * ws * PA * PA + 
        (-0.0000127043871) * Ta * ws * ws * ws * PA * PA + 
        (-0.00000304620472) * ws * ws * ws * ws * PA * PA + 
        (0.0514507424) * D_Tmrt * PA * PA + 
        (-0.00432510997) * Ta * D_Tmrt * PA * PA + 
        (0.0000899281156) * Ta * Ta * D_Tmrt * PA * PA + 
        (-0.000000714663943) * Ta * Ta * Ta * D_Tmrt * PA * PA + 
        (-0.000266016305) * ws * D_Tmrt * PA * PA + 
        (0.000263789586) * Ta * ws * D_Tmrt * PA * PA + 
        (-0.00000701199003) * Ta * Ta * ws * D_Tmrt * PA * PA + 
        (-0.000106823306) * ws * ws * D_Tmrt * PA * PA + 
        (0.00000361341136) * Ta * ws * ws * D_Tmrt * PA * PA + 
        (0.000000229748967) * ws * ws * ws * D_Tmrt * PA * PA + 
        (0.000304788893) * D_Tmrt * D_Tmrt * PA * PA + 
        (-0.0000642070836) * Ta * D_Tmrt * D_Tmrt * PA * PA + 
        (0.00000116257971) * Ta * Ta * D_Tmrt * D_Tmrt * PA * PA + 
        (0.00000768023384) * ws * D_Tmrt * D_Tmrt * PA * PA + 
        (-0.000000547446896) * Ta * ws * D_Tmrt * D_Tmrt * PA * PA + 
        (-0.000000035993791) * ws * ws * D_Tmrt * D_Tmrt * PA * PA + 
        (-0.00000436497725) * D_Tmrt * D_Tmrt * D_Tmrt * PA * PA + 
        (0.000000168737969) * Ta * D_Tmrt * D_Tmrt * D_Tmrt * PA * PA + 
        (2.67489271E-08) * ws * D_Tmrt * D_Tmrt * D_Tmrt * PA * PA + 
        (3.23926897E-09) * D_Tmrt * D_Tmrt * D_Tmrt * D_Tmrt * PA * PA;
        
      double  ET8 = -0.0353874123 * PA * PA * PA + 
        (-0.22120119) * Ta * PA * PA * PA + 
        (0.0155126038) * Ta * Ta * PA * PA * PA + 
        (-0.000263917279) * Ta * Ta * Ta * PA * PA * PA + 
        (0.0453433455) * ws * PA * PA * PA + 
        (-0.00432943862) * Ta * ws * PA * PA * PA + 
        (0.000145389826) * Ta * Ta * ws * PA * PA * PA + 
        (0.00021750861) * ws * ws * PA * PA * PA + 
        (-0.0000666724702) * Ta * ws * ws * PA * PA * PA + 
        (0.000033321714) * ws * ws * ws * PA * PA * PA + 
        (-0.00226921615) * D_Tmrt * PA * PA * PA + 
        (0.000380261982) * Ta * D_Tmrt * PA * PA * PA + 
        (-5.45314314E-09) * Ta * Ta * D_Tmrt * PA * PA * PA + 
        (-0.000796355448) * ws * D_Tmrt * PA * PA * PA + 
        (0.0000253458034) * Ta * ws * D_Tmrt * PA * PA * PA + 
        (-0.00000631223658) * ws * ws * D_Tmrt * PA * PA * PA + 
        (0.000302122035) * D_Tmrt * D_Tmrt * PA * PA * PA + 
        (-0.00000477403547) * Ta * D_Tmrt * D_Tmrt * PA * PA * PA + 
        (0.00000173825715) * ws * D_Tmrt * D_Tmrt * PA * PA * PA + 
        (-0.000000409087898) * D_Tmrt * D_Tmrt * D_Tmrt * PA * PA * PA + 
        (0.614155345) * PA * PA * PA * PA + 
        (-0.0616755931) * Ta * PA * PA * PA * PA + 
        (0.00133374846) * Ta * Ta * PA * PA * PA * PA + 
        (0.00355375387) * ws * PA * PA * PA * PA + 
        (-0.000513027851) * Ta * ws * PA * PA * PA * PA;
        
      double  ET9 = 0.000102449757 * ws * ws * PA * PA * PA * PA + 
        (-0.00148526421) * D_Tmrt * PA * PA * PA * PA + 
        (-0.0000411469183) * Ta * D_Tmrt * PA * PA * PA * PA + 
        (-0.00000680434415) * ws * D_Tmrt * PA * PA * PA * PA + 
        (-0.00000977675906) * D_Tmrt * D_Tmrt * PA * PA * PA * PA + 
        (0.0882773108) * PA * PA * PA * PA * PA + 
        (-0.00301859306) * Ta * PA * PA * PA * PA * PA + 
        (0.00104452989) * ws * PA * PA * PA * PA * PA + 
        (0.000247090539) * D_Tmrt * PA * PA * PA * PA * PA + 
        (0.00148348065) * PA * PA * PA * PA * PA * PA;

      double fUTCI = ET1 + ET3 + ET4 + ET5 + ET6 + ET7 + ET8 + ET9;
      return fUTCI;

}

public double fUTCI(double Ta, 
		//double Tg, double Td, 
		double ws, 
		//double solar, 
		double RH, double Tmrt)
{
//UTCI calculation using full formula from utci.org website
// Check if one and only one of RH or Td have been entered
  double D_Tmrt; 
  double Td;
 
//  if (RH != -99)
//  {
//	  if (Td != -99)
//	  {
//		  System.err.println("Error:");
//		  return ERROR_RETURN;
//	  }
	  Td = fTd(Ta, RH);
//  }
//  else
//  {
//	  RH = fRH(Ta, Td);
//  }
//  
//	//Check to make sure Td < Ta
//	if (Td > Ta) 
//	{
//		System.err.println("Error:");
//		return ERROR_RETURN;
////		fUTCI = "Error:":  Exit Function
//	}
//	
//	//Calculate Tg given solar, calculate solar given Tg
//	if (solar != -99)
//	{
//		if (Tg != -99)
//		{
//			System.err.println("Error:");
//			return ERROR_RETURN;
//		}
//		Tg = fTg(Ta, RH, AtmPressure, ws, solar, propDirect, ZenithAngle, MinWindSpeed);
//	}

//	double Tmrt = fTmrtB(Ta, Tg, ws); //takes Tg and changes it into Tmrt uses Bernard formula

    // UTCI, Version a 0.002, October 2009
    // Copyright (C) 2009  Peter Broede
    // Program for calculating UTCI Temperature (UTCI)
    // released for public use after termination of COST Action 730
    // Copyright (C) 2009  Peter Broede
    // This program is distributed in the hope that it will be useful,
    // but WITHOUT ANY WARRANTY; without even the implied warranty of
    // MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

// Disclaimer of Warranty.
// THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN OTHERWISE STATED IN WRITING
// THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM “AS IS” WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED
// OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
// THE ENTIRE RISK AS TO THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE, YOU
// ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.

// Limitation of Liability.
// IN NO EVENT UNLESS REQUIRED BY APPLICABLE LAW OR AGREED TO IN WRITING WILL ANY COPYRIGHT HOLDER, OR ANY OTHER PARTY WHO
// MODIFIES AND/OR CONVEYS THE PROGRAM AS PERMITTED ABOVE, BE LIABLE TO YOU FOR DAMAGES, INCLUDING ANY GENERAL, SPECIAL,
// INCIDENTAL OR CONSEQUENTIAL DAMAGES ARISING OUT OF THE USE OR INABILITY TO USE THE PROGRAM
// (INCLUDING BUT NOT LIMITED TO LOSS OF DATA OR DATA BEING RENDERED INACCURATE OR LOSSES SUSTAINED BY YOU OR THIRD PARTIES
// OR A FAILURE OF THE PROGRAM TO OPERATE WITH ANY OTHER PROGRAMS), EVEN IF SUCH HOLDER OR OTHER PARTY HAS BEEN ADVISED
// OF THE POSSIBILITY OF SUCH DAMAGES.

 // DOUBLE PRECISION Function value is the UTCI in degree Celsius
 // computed by a 6th order approximating polynomial from the 4 Input paramters
 //
 // Input parameters (all of type DOUBLE PRECISION)
 // - Ta       : air temperature, degree Celsius
 // - ehPa    : water vapour presure, hPa=hecto Pascal
 // - Tmrt   : mean radiant temperature, degree Celsius
 // - va10m  : wind speed 10 m above ground level in m/s
 //
 //  UTCI_approx, Version a 0.002, October 2009
 //  Copyright (C) 2009  Peter Broede

      
      double PA = 0.6108 * Math.exp(17.29 * Td / (Td + 237.3)); //takes Td and changes it into Pa - vapour pressure in hPa)
      D_Tmrt = Tmrt - Ta;
        // calculate 6th order polynomial as approximation
      double ET1 = Ta + 0.607562052 - 0.0227712343 * Ta + 0.000806470249 * Ta * Ta - 0.000154271372 * Ta * Ta * Ta 
        - 0.00000324651735 * Ta * Ta * Ta * Ta + 7.32602852E-08 * Ta * Ta * Ta * Ta * Ta + 1.35959073E-09 * Ta * Ta * Ta * Ta * Ta * Ta 
        - 2.2583652 * ws + 0.0880326035 * Ta * ws + 0.00216844454 * Ta * Ta * ws - 0.0000153347087 * Ta * Ta * Ta * ws 
        - 0.000000572983704 * Ta * Ta * Ta * Ta * ws - 2.55090145E-09 * Ta * Ta * Ta * Ta * Ta * ws 
        - 0.751269505 * ws * ws - 0.00408350271 * Ta * ws * ws - 0.0000521670675 * Ta * Ta * ws * ws 
        + 0.00000194544667 * Ta * Ta * Ta * ws * ws + 1.14099531E-08 * Ta * Ta * Ta * Ta * ws * ws 
        + 0.158137256 * ws * ws * ws - 0.0000657263143 * Ta * ws * ws * ws + 0.000000222697524 * Ta * Ta * ws * ws * ws - 4.16117031E-08 * Ta * Ta * Ta * ws * ws * ws 
        - 0.0127762753 * ws * ws * ws * ws + 0.00000966891875 * Ta * ws * ws * ws * ws + 2.52785852E-09 * Ta * Ta * ws * ws * ws * ws 
        + 0.000456306672 * ws * ws * ws * ws * ws - 0.000000174202546 * Ta * ws * ws * ws * ws * ws - 0.00000591491269 * ws * ws * ws * ws * ws * ws 
        + 0.398374029 * D_Tmrt + 0.000183945314 * Ta * D_Tmrt - 0.00017375451 * Ta * Ta * D_Tmrt 
        - 0.000000760781159 * Ta * Ta * Ta * D_Tmrt + 3.77830287E-08 * Ta * Ta * Ta * Ta * D_Tmrt + 5.43079673E-10 * Ta * Ta * Ta * Ta * Ta * D_Tmrt 
        - 0.0200518269 * ws * D_Tmrt + 0.000892859837 * Ta * ws * D_Tmrt + 0.00000345433048 * Ta * Ta * ws * D_Tmrt 
        - 0.000000377925774 * Ta * Ta * Ta * ws * D_Tmrt - 1.69699377E-09 * Ta * Ta * Ta * Ta * ws * D_Tmrt + 0.000169992415 * ws * ws * D_Tmrt 
        - 0.0000499204314 * Ta * ws * ws * D_Tmrt + 0.000000247417178 * Ta * Ta * ws * ws * D_Tmrt + 1.07596466E-08 * Ta * Ta * Ta * ws * ws * D_Tmrt 
        + 0.0000849242932 * ws * ws * ws * D_Tmrt + 0.00000135191328 * Ta * ws * ws * ws * D_Tmrt - 6.21531254E-09 * Ta * Ta * ws * ws * ws * D_Tmrt 
        - 0.00000499410301 * ws * ws * ws * ws * D_Tmrt - 1.89489258E-08 * Ta * ws * ws * ws * ws * D_Tmrt + 8.15300114E-08 * ws * ws * ws * ws * ws 
        * D_Tmrt + 0.00075504309 * D_Tmrt * D_Tmrt;
        
      double ET3 = -0.0000565095215 * Ta * D_Tmrt * D_Tmrt + 
        (-0.000000452166564) * Ta * Ta * D_Tmrt * D_Tmrt + 
        (2.46688878E-08) * Ta * Ta * Ta * D_Tmrt * D_Tmrt + 
        (2.42674348E-10) * Ta * Ta * Ta * Ta * D_Tmrt * D_Tmrt + 
        (0.00015454725) * ws * D_Tmrt * D_Tmrt + 
        (0.0000052411097) * Ta * ws * D_Tmrt * D_Tmrt + 
        (-8.75874982E-08) * Ta * Ta * ws * D_Tmrt * D_Tmrt + 
        (-1.50743064E-09) * Ta * Ta * Ta * ws * D_Tmrt * D_Tmrt + 
        (-0.0000156236307) * ws * ws * D_Tmrt * D_Tmrt + 
        (-0.000000133895614) * Ta * ws * ws * D_Tmrt * D_Tmrt + 
        (2.49709824E-09) * Ta * Ta * ws * ws * D_Tmrt * D_Tmrt + 
        (0.000000651711721) * ws * ws * ws * D_Tmrt * D_Tmrt + 
        (1.94960053E-09) * Ta * ws * ws * ws * D_Tmrt * D_Tmrt + 
        (-1.00361113E-08) * ws * ws * ws * ws * D_Tmrt * D_Tmrt + 
        (-0.0000121206673) * D_Tmrt * D_Tmrt * D_Tmrt + 
        (-0.00000021820366) * Ta * D_Tmrt * D_Tmrt * D_Tmrt + 
        (7.51269482E-09) * Ta * Ta * D_Tmrt * D_Tmrt * D_Tmrt + 
        (9.79063848E-11) * Ta * Ta * Ta * D_Tmrt * D_Tmrt * D_Tmrt + 
        (0.00000125006734) * ws * D_Tmrt * D_Tmrt * D_Tmrt + 
        (-1.81584736E-09) * Ta * ws * D_Tmrt * D_Tmrt * D_Tmrt + 
        (-3.52197671E-10) * Ta * Ta * ws * D_Tmrt * D_Tmrt * D_Tmrt + 
        (-0.000000033651463) * ws * ws * D_Tmrt * D_Tmrt * D_Tmrt + 
        (1.35908359E-10) * Ta * ws * ws * D_Tmrt * D_Tmrt * D_Tmrt + 
        (4.1703262E-10) * ws * ws * ws * D_Tmrt * D_Tmrt * D_Tmrt + 
        (-1.30369025E-09) * D_Tmrt * D_Tmrt * D_Tmrt * D_Tmrt;
        
      double  ET4 = 4.13908461E-10 * Ta * D_Tmrt * D_Tmrt * D_Tmrt * D_Tmrt + 
        (9.22652254E-12) * Ta * Ta * D_Tmrt * D_Tmrt * D_Tmrt * D_Tmrt + 
        (-5.08220384E-09) * ws * D_Tmrt * D_Tmrt * D_Tmrt * D_Tmrt + 
        (-2.24730961E-11) * Ta * ws * D_Tmrt * D_Tmrt * D_Tmrt * D_Tmrt + 
        (1.17139133E-10) * ws * ws * D_Tmrt * D_Tmrt * D_Tmrt * D_Tmrt + 
        (6.62154879E-10) * D_Tmrt * D_Tmrt * D_Tmrt * D_Tmrt * D_Tmrt + 
        (4.0386326E-13) * Ta * D_Tmrt * D_Tmrt * D_Tmrt * D_Tmrt * D_Tmrt + 
        (1.95087203E-12) * ws * D_Tmrt * D_Tmrt * D_Tmrt * D_Tmrt * D_Tmrt + 
        (-4.73602469E-12) * D_Tmrt * D_Tmrt * D_Tmrt * D_Tmrt * D_Tmrt * D_Tmrt + 
        (5.12733497) * PA + 
        (-0.312788561) * Ta * PA + 
        (-0.0196701861) * Ta * Ta * PA + 
        (0.00099969087) * Ta * Ta * Ta * PA + 
        (0.00000951738512) * Ta * Ta * Ta * Ta * PA + 
        (-0.000000466426341) * Ta * Ta * Ta * Ta * Ta * PA + 
        (0.548050612) * ws * PA + 
        (-0.00330552823) * Ta * ws * PA + 
        (-0.0016411944) * Ta * Ta * ws * PA + 
        (-0.00000516670694) * Ta * Ta * Ta * ws * PA + 
        (0.000000952692432) * Ta * Ta * Ta * Ta * ws * PA + 
        (-0.0429223622) * ws * ws * PA + 
        (0.00500845667) * Ta * ws * ws * PA + 
        (0.00000100601257) * Ta * Ta * ws * ws * PA + 
        (-0.00000181748644) * Ta * Ta * Ta * ws * ws * PA + 
        (-0.00125813502) * ws * ws * ws * PA;
        
      double  ET5 = -0.000179330391 * Ta * ws * ws * ws * PA + 
        (0.00000234994441) * Ta * Ta * ws * ws * ws * PA + 
        (0.000129735808) * ws * ws * ws * ws * PA + 
        (0.0000012906487) * Ta * ws * ws * ws * ws * PA + 
        (-0.00000228558686) * ws * ws * ws * ws * ws * PA + 
        (-0.0369476348) * D_Tmrt * PA + 
        (0.00162325322) * Ta * D_Tmrt * PA + 
        (-0.000031427968) * Ta * Ta * D_Tmrt * PA + 
        (0.00000259835559) * Ta * Ta * Ta * D_Tmrt * PA + 
        (-4.77136523E-08) * Ta * Ta * Ta * Ta * D_Tmrt * PA + 
        (0.0086420339) * ws * D_Tmrt * PA + 
        (-0.000687405181) * Ta * ws * D_Tmrt * PA + 
        (-0.00000913863872) * Ta * Ta * ws * D_Tmrt * PA + 
        (0.000000515916806) * Ta * Ta * Ta * ws * D_Tmrt * PA + 
        (-0.0000359217476) * ws * ws * D_Tmrt * PA + 
        (0.0000328696511) * Ta * ws * ws * D_Tmrt * PA + 
        (-0.000000710542454) * Ta * Ta * ws * ws * D_Tmrt * PA + 
        (-0.00001243823) * ws * ws * ws * D_Tmrt * PA + 
        (-0.000000007385844) * Ta * ws * ws * ws * D_Tmrt * PA + 
        (0.000000220609296) * ws * ws * ws * ws * D_Tmrt * PA + 
        (-0.00073246918) * D_Tmrt * D_Tmrt * PA + 
        (-0.0000187381964) * Ta * D_Tmrt * D_Tmrt * PA + 
        (0.00000480925239) * Ta * Ta * D_Tmrt * D_Tmrt * PA + 
        (-0.000000087549204) * Ta * Ta * Ta * D_Tmrt * D_Tmrt * PA + 
        (0.000027786293) * ws * D_Tmrt * D_Tmrt * PA;
        
      double  ET6 = -0.00000506004592 * Ta * ws * D_Tmrt * D_Tmrt * PA + 
        (0.000000114325367) * Ta * Ta * ws * D_Tmrt * D_Tmrt * PA + 
        (0.00000253016723) * ws * ws * D_Tmrt * D_Tmrt * PA + 
        (-1.72857035E-08) * Ta * ws * ws * D_Tmrt * D_Tmrt * PA + 
        (-3.95079398E-08) * ws * ws * ws * D_Tmrt * D_Tmrt * PA + 
        (-0.000000359413173) * D_Tmrt * D_Tmrt * D_Tmrt * PA + 
        (0.000000704388046) * Ta * D_Tmrt * D_Tmrt * D_Tmrt * PA + 
        (-1.89309167E-08) * Ta * Ta * D_Tmrt * D_Tmrt * D_Tmrt * PA + 
        (-0.000000479768731) * ws * D_Tmrt * D_Tmrt * D_Tmrt * PA + 
        (7.96079978E-09) * Ta * ws * D_Tmrt * D_Tmrt * D_Tmrt * PA + 
        (1.62897058E-09) * ws * ws * D_Tmrt * D_Tmrt * D_Tmrt * PA + 
        (3.94367674E-08) * D_Tmrt * D_Tmrt * D_Tmrt * D_Tmrt * PA + 
        (-1.18566247E-09) * Ta * D_Tmrt * D_Tmrt * D_Tmrt * D_Tmrt * PA + 
        (3.34678041E-10) * ws * D_Tmrt * D_Tmrt * D_Tmrt * D_Tmrt * PA + 
        (-1.15606447E-10) * D_Tmrt * D_Tmrt * D_Tmrt * D_Tmrt * D_Tmrt * PA + 
        (-2.80626406) * PA * PA + 
        (0.548712484) * Ta * PA * PA + 
        (-0.0039942841) * Ta * Ta * PA * PA + 
        (-0.000954009191) * Ta * Ta * Ta * PA * PA + 
        (0.0000193090978) * Ta * Ta * Ta * Ta * PA * PA + 
        (-0.308806365) * ws * PA * PA + 
        (0.0116952364) * Ta * ws * PA * PA + 
        (0.000495271903) * Ta * Ta * ws * PA * PA + 
        (-0.0000190710882) * Ta * Ta * Ta * ws * PA * PA + 
        (0.00210787756) * ws * ws * PA * PA;
        
      double  ET7 = -0.000698445738 * Ta * ws * ws * PA * PA + 
        (0.0000230109073) * Ta * Ta * ws * ws * PA * PA + 
        (0.00041785659) * ws * ws * ws * PA * PA + 
        (-0.0000127043871) * Ta * ws * ws * ws * PA * PA + 
        (-0.00000304620472) * ws * ws * ws * ws * PA * PA + 
        (0.0514507424) * D_Tmrt * PA * PA + 
        (-0.00432510997) * Ta * D_Tmrt * PA * PA + 
        (0.0000899281156) * Ta * Ta * D_Tmrt * PA * PA + 
        (-0.000000714663943) * Ta * Ta * Ta * D_Tmrt * PA * PA + 
        (-0.000266016305) * ws * D_Tmrt * PA * PA + 
        (0.000263789586) * Ta * ws * D_Tmrt * PA * PA + 
        (-0.00000701199003) * Ta * Ta * ws * D_Tmrt * PA * PA + 
        (-0.000106823306) * ws * ws * D_Tmrt * PA * PA + 
        (0.00000361341136) * Ta * ws * ws * D_Tmrt * PA * PA + 
        (0.000000229748967) * ws * ws * ws * D_Tmrt * PA * PA + 
        (0.000304788893) * D_Tmrt * D_Tmrt * PA * PA + 
        (-0.0000642070836) * Ta * D_Tmrt * D_Tmrt * PA * PA + 
        (0.00000116257971) * Ta * Ta * D_Tmrt * D_Tmrt * PA * PA + 
        (0.00000768023384) * ws * D_Tmrt * D_Tmrt * PA * PA + 
        (-0.000000547446896) * Ta * ws * D_Tmrt * D_Tmrt * PA * PA + 
        (-0.000000035993791) * ws * ws * D_Tmrt * D_Tmrt * PA * PA + 
        (-0.00000436497725) * D_Tmrt * D_Tmrt * D_Tmrt * PA * PA + 
        (0.000000168737969) * Ta * D_Tmrt * D_Tmrt * D_Tmrt * PA * PA + 
        (2.67489271E-08) * ws * D_Tmrt * D_Tmrt * D_Tmrt * PA * PA + 
        (3.23926897E-09) * D_Tmrt * D_Tmrt * D_Tmrt * D_Tmrt * PA * PA;
        
      double  ET8 = -0.0353874123 * PA * PA * PA + 
        (-0.22120119) * Ta * PA * PA * PA + 
        (0.0155126038) * Ta * Ta * PA * PA * PA + 
        (-0.000263917279) * Ta * Ta * Ta * PA * PA * PA + 
        (0.0453433455) * ws * PA * PA * PA + 
        (-0.00432943862) * Ta * ws * PA * PA * PA + 
        (0.000145389826) * Ta * Ta * ws * PA * PA * PA + 
        (0.00021750861) * ws * ws * PA * PA * PA + 
        (-0.0000666724702) * Ta * ws * ws * PA * PA * PA + 
        (0.000033321714) * ws * ws * ws * PA * PA * PA + 
        (-0.00226921615) * D_Tmrt * PA * PA * PA + 
        (0.000380261982) * Ta * D_Tmrt * PA * PA * PA + 
        (-5.45314314E-09) * Ta * Ta * D_Tmrt * PA * PA * PA + 
        (-0.000796355448) * ws * D_Tmrt * PA * PA * PA + 
        (0.0000253458034) * Ta * ws * D_Tmrt * PA * PA * PA + 
        (-0.00000631223658) * ws * ws * D_Tmrt * PA * PA * PA + 
        (0.000302122035) * D_Tmrt * D_Tmrt * PA * PA * PA + 
        (-0.00000477403547) * Ta * D_Tmrt * D_Tmrt * PA * PA * PA + 
        (0.00000173825715) * ws * D_Tmrt * D_Tmrt * PA * PA * PA + 
        (-0.000000409087898) * D_Tmrt * D_Tmrt * D_Tmrt * PA * PA * PA + 
        (0.614155345) * PA * PA * PA * PA + 
        (-0.0616755931) * Ta * PA * PA * PA * PA + 
        (0.00133374846) * Ta * Ta * PA * PA * PA * PA + 
        (0.00355375387) * ws * PA * PA * PA * PA + 
        (-0.000513027851) * Ta * ws * PA * PA * PA * PA;
        
      double  ET9 = 0.000102449757 * ws * ws * PA * PA * PA * PA + 
        (-0.00148526421) * D_Tmrt * PA * PA * PA * PA + 
        (-0.0000411469183) * Ta * D_Tmrt * PA * PA * PA * PA + 
        (-0.00000680434415) * ws * D_Tmrt * PA * PA * PA * PA + 
        (-0.00000977675906) * D_Tmrt * D_Tmrt * PA * PA * PA * PA + 
        (0.0882773108) * PA * PA * PA * PA * PA + 
        (-0.00301859306) * Ta * PA * PA * PA * PA * PA + 
        (0.00104452989) * ws * PA * PA * PA * PA * PA + 
        (0.000247090539) * D_Tmrt * PA * PA * PA * PA * PA + 
        (0.00148348065) * PA * PA * PA * PA * PA * PA;

      double fUTCI = ET1 + ET3 + ET4 + ET5 + ET6 + ET7 + ET8 + ET9;
      return fUTCI;

}
      
      
public double  es(double Ta)
{
// calculates saturation vapour pressure over water in hPa for input air temperature (ta) in celsius according to:
// Hardy, R.; ITS-90 Formulations for Vapor Pressure, Frostpoint Temperature, Dewpoint Temperature and Enhancement Factors in the Range -100 to 100 °C;
// Proceedings of Third International Symposium on Humidity and Moisture; edited by National Physical Laboratory (NPL), London, 1998, pp. 214-221
// http://www.thunderscientific.com/tech_info/reflibrary/its90formulas.pdf (retrieved 2008-10-01)
      double Tk = Ta + 273.15;  // air temp in K
      double es = 2.7150305 * Math.log(Tk) - 2836.5744 * Math.pow(Tk, (-2)) - 6028.076559 / Tk + 19.54263612 - 0.02737830188 * Tk + 0.000016261698 * Math.pow(Tk, 2) + 7.0229056E-10 * Math.pow(Tk, 3) - 1.8680009E-13 * Math.pow(Tk, 4);
      es = Math.exp(es) * 0.01; // *0.01: convert Pa to hPa
      return es;
}


public double  h_cylinder_in_air(double Tair, double Pair, double speed, double speedMin)
{
//  Purpose: to calculate the convective heat transfer coefficient for a long cylinder in cross flow.
//  Reference: Bedingfield and Drew, eqn 32
//  Author:  James C. Liljegren
//       Decision and Information Sciences Division
//       Argonne National Laboratory
//
    double Pr = 1003.5 / (1003.5 + 1.25 * 8314.34 / 28.97);
    double thermal_con = (1003.5 + 1.25 * 8314.34 / 28.97) * viscosity(Tair);
    //double density = Pair * 100# / (8314.34 / 28.97 * Tair);
    double density = Pair * 100 / (8314.34 / 28.97 * Tair);
    if (speed < speedMin) 
    {
    	speed = speedMin;
    }
    double Re = speed * density * diamWick / viscosity(Tair);
    double Nu = 0.281 * Math.pow(Re, 0.6) * Math.pow(Pr, 0.44);
    double h_cylinder_in_air = Nu * thermal_con / diamWick;  // W/(m2 K)
    return h_cylinder_in_air;
}
    
    
public double diffusivity(double Tair, double Pair)
{
//  Purpose: compute the diffusivity of water vapor in air, m2/s
//  Reference: BSL, page 505.
    //double pcrit13 = (36.4 * 218) ^ (1# / 3#);
	double pcrit13 = Math.pow((36.4 * 218), (1 / 3))  ;
    //double tcrit512 = (132 * 647.3) ^ (5# / 12#);
	double tcrit512 = Math.pow((132 * 647.3), (5 / 12)) ;
    double Tcrit12 = Math.pow((132 * 647.3), 0.5);
    //double Mmix = (1# / 28.97 + 1# / 18.015) ^ 0.5;
    double Mmix = Math.pow((1 / 28.97 + 1 / 18.015), 0.5) ;
    double diffusivity = 0.000364 * Math.pow((Tair / Tcrit12), 2.334 ) * pcrit13 * tcrit512 * Mmix / (Pair / 1013.25) * 0.0001;
    return diffusivity;
}


    public double h_sphere_in_air(double Tair, double Pair, double speed, double speedMin)
    {
//  Purpose: to calculate the convective heat tranfer coefficient for flow around a sphere.
//  Reference: Bird, Stewart, and Lightfoot (BSL), page 409.
	    double Rair = 8314.34 / 28.97;
	    double Pr = 1003.5 / (1003.5 + 1.25 * Rair);
	    double thermal_con = (1003.5 + 1.25 * 8314.34 / 28.97) * viscosity(Tair);
	    double density = Pair * 100 / (Rair * Tair);   // kg/m3
	    if (speed < speedMin) 
	    {
	    	speed = speedMin;
	    }
	    double Re = speed * density * diamGlobe / viscosity(Tair);
	    double Nu = 2 + 0.6 * Math.pow(Re, 0.5) * Math.pow(Pr, 0.3333);
	    double h_sphere_in_air = Nu * thermal_con / diamGlobe; // W/(m2 K)
	    return h_sphere_in_air;
    }
    
    
    public double emis_atm(double Ta, double RH)
    {
//  Reference: Oke (2nd edition), page 373.
	    double e = RH * esat(Ta);
	    double emis_atm = 0.575 * Math.pow(e, 0.143);
	    return emis_atm;
    }
    
    
    public double esat(double Tk)
    {
//  Purpose: calculate the saturation vapor pressure (mb) over liquid water given the temperature (K).
//  Reference: Buck's (1981) approximation (eqn 3) of Wexler's (1976) formulae.
//  over liquid water
	    double esat = 6.1121 * Math.exp(17.502 * (Tk - 273.15) / (Tk - 32.18));
	    esat = 1.004 * esat;  // correction for moist air, if pressure is not available; for pressure > 800 mb
	    return esat;
    }
    
    
    public double viscosity(double Tair)
    {
//  Purpose: Compute the viscosity of air, kg/(m s) given temperature, K
//  Reference: BSL, page 23.
	    double omega = (Tair / 97 - 2.9) / 0.4 * (-0.034) + 1.048;
	    double viscosity = 0.0000026693 * Math.pow((28.97 * Tair), 0.5 ) / (Math.pow(3.617, 2) * omega);
	    return viscosity;
    }
    
    
public double fTg_withdiam(double Ta, double relh, double speed, double solar, double diam)
{
	double fdir = 0.8;  //Assume a proportion of direct radiation = direct/(diffuse + direct)
	double zenith = 0;  //angle of sun from directly above
	double speedMin = 0.1;   //0 wind speed upsets log function
	double Pair = 101;
	
	//  Purpose: to calculate the globe temperature
	//  Author:  James C. Liljegren
	//       Decision and Information Sciences Division
	//       Argonne National Laboratory
	// Pressure in kPa (Atm =101 kPa)
	//Fix up out-of bounds problems with zenith
    if (zenith <= 0) 
    {
    	zenith = 0.0000000001;
    }	    		
    if (zenith > 1.57) 
    {
    	zenith = 1.57;
    }
    Pair = Pair * 10;
    double cza = Math.cos(zenith);
    double converge = 0.05;
    double alb_sfc = SurfAlbedo;
    double alb_globe = 0.05;
    double emis_globe = 0.95;
    double emis_sfc = 0.999;
    double Tair = Ta + 273.15;
    double RH = relh * 0.01;
    double Tsfc = Tair;
    double Tglobe_prev = Tair;
    
	//Do iteration
	int testno = 1;
	
	boolean continueLoop = true;
	double Tglobe=0;
	while(continueLoop)
	{
	//	Reit:
		testno = testno + 1;
		if (testno > 1000) 
		{
			System.err.println("No convergence: values too extreme");
			//Cells(2, 4) = "No convergence: values too extreme";
			//Exit Function
			return ERROR_RETURN;
		}
		double Tref = 0.5 * (Tglobe_prev + Tair); // Evaluate properties at the average temperature
		double h = h_sphere_in_air_withdiam(Tref, Pair, speed, speedMin, diam);
		Tglobe = Math.pow((0.5 * (emis_atm(Tair, RH) * Math.pow(Tair, 4 ) + emis_sfc * Math.pow(Tsfc, 4) ) - h / (emis_globe * stefanb) * (Tglobe_prev - Tair) + solar / (2 * emis_globe * stefanb) * (1 - alb_globe) * (fdir * (1 / (2 * cza) - 1) + 1 + alb_sfc)), 0.25);
		double dT = Tglobe - Tglobe_prev;
	    if (Math.abs(dT) < converge) 
	    {
	       Tglobe = Tglobe - 273.15;
	       continueLoop = false;
	    }
	    else
	    {
	       Tglobe_prev = (0.9 * Tglobe_prev + 0.1 * Tglobe);
//	       GoTo Reit
	       continueLoop = true;
	    }
	}
    
    double fTg_withdiam = Tglobe;
       
    return fTg_withdiam;
}
    
    
    public double h_sphere_in_air_withdiam(double Tair, double Pair, double speed, double speedMin, double diam)
    {
	//  Purpose: to calculate the convective heat tranfer coefficient for flow around a sphere.
	//  Reference: Bird, Stewart, and Lightfoot (BSL), page 409.
	    double Rair = 8314.34 / 28.97;
	    double Pr = 1003.5 / (1003.5 + 1.25 * Rair);
	    double thermal_con = (1003.5 + 1.25 * 8314.34 / 28.97) * viscosity(Tair);
	    double density = Pair * 100 / (Rair * Tair);   // kg/m3
	    if (speed < speedMin) 
	    {
	    	speed = speedMin;
	    }		
	    double Re = speed * density * diam / viscosity(Tair);
	    double Nu = 2 + 0.6 * Math.pow(Re , 0.5) * Math.pow(Pr , 0.3333);
	    double h_sphere_in_air_withdiam = Nu * thermal_con / diam; // W/(m2 K)
	    return h_sphere_in_air_withdiam;
    }
    
	public double sunpos(int jday, int tm, double lat)
	{
		double pi = 3.1415926;
		boolean sh = false;
		double hr_rad = 15. * pi / 180.;
		double theta = (jday - 1) * (2. * pi) / 365.0;
		double theta_inot = theta;
		// # for southern hemisphere:
		if (lat < 0)
		{
			theta = theta + pi % 2.0 * pi;
			sh = true;
			lat = Math.abs(lat);
		}
		// # declination angle
		double dec = 0.006918 - 0.399912 * Math.cos(theta) + 0.070257 * Math.sin(theta) - 0.006758 * Math.cos(2 * theta)
				+ 0.000907 * Math.sin(2 * theta) - 0.002697 * Math.cos(3 * theta) + 0.00148 * Math.sin(3 * theta);
		// # all the changes are from stull, meteorology for scientists and
		// # engineers 2000 - note: the current definition of hl give the solar
		// # position based on local mean solar time - to have solar position as
		// # as function of standard time in the time zone, must use stull's
		// # equation 2.9 on p. 26
		double hl = tm * hr_rad;
		// # cos(solar zenith)
		double cz = (Math.sin(lat) * Math.sin(dec)) - (Math.cos(lat) * Math.cos(dec) * Math.cos(hl));
		// # solar zenith
		double zen = Math.acos(cz);

		return zen;
	}
    
	public double getZenith(int yd, int timeis, double xlat)
	{
		double pi = 3.1415926;
		// # Solar angle and incoming shortwave (direct & diffuse) routines
		double LAT = xlat * pi / 180.;
		int TM = timeis % 24;
		int yd_actual = yd + (TM / 24);
		yd_actual = yd_actual % 365;
		double zeni = sunpos(yd_actual, TM, LAT);
		double zen = zeni * 180. / pi;
		return zen;
	}

	public double radianToDegree(double radian)
	{
		double PI = 3.1415926;
		double Degree180 = 180.0;
		double R_to_D = Degree180 / PI;
		// #D_to_R = PI/Degree180;
		double radianToDegree = radian * R_to_D;
		return radianToDegree;
	}

	public double calculateRH(double tempC, double vapor)
	{
		double calculateRH = 100 * vapor / (7.5152E8 * Math.exp(-42809 / (8.314 * (tempC + 273.0))));
		return calculateRH;
	}

	public double getFdir(double zenith, double solar)
	{
		double d = 1.0; // # should calculate earth-sun distance, but set to
						// mean value (1 A.U.)
		double zenDegrees = radianToDegree(zenith);
		// #print *,'zenDegrees',zenDegrees
		double fdir = 0.0; // # default, for zenDegrees > 89.5
		if (zenDegrees <= 89.5)
		{
			double s0 = 1367.0;
			double smax = s0 * Math.cos(zenith) / d * d;
			double sstar = solar / smax;
			// #division by zero error
			if (sstar == 0.0)
			{
				sstar = sstar + 0.000001;
			}

			fdir = Math.exp(3.0 - 1.34 * sstar - 1.65 / sstar);
		}
		double getFdir = fdir;
		return getFdir;
	}
	 
	public double getTmrtForGrid_RH(double Ta, double relh, double speed, double solar, double tsfc, double ldown,
			double lup, int yd_actual, int TM, double LAT)
	{
		double zenith = getZenith(yd_actual, TM, LAT);
		double Pair = 100.0;
		// #relh=calculateRH(Ta, vapor);
		double speedMin = 0.5;
		double emisAtmValue = emis_atm(Ta + 273.15, relh * 0.01);
		if (solar < 50)
		{
			emisAtmValue = 0.99;
		}

		double fdir = getFdir(zenith, solar);
		double Tg = fTg4(Ta, relh, Pair, speed, solar, fdir, zenith, speedMin, tsfc, emisAtmValue, ldown, lup);
//		System.out.println("Tg " + " " + Tg + " " + Ta + " " + relh + " " + Pair + " " + speed + " " + solar + " " 
//				+ fdir + " " + zenith + " " + speedMin + " " + tsfc + " " + emisAtmValue + " " + ldown + " " + lup);
		double tmrtC = fTmrtD(Ta, Tg, speed);
		double getTmrtForGrid = tmrtC;

		return getTmrtForGrid;
	}



	public double fTg4(double Ta, double relh, double PairIn, double speed, double solar, double fdir, double zenith,
			double speedMin, double Tsfc, double emisAtmValue, double lIn, double lOut)
	{
		double fTg4;

		double SurfAlbedo = 0.15;
		double stefanb = 0.000000056696;
		double diamGlobe = 0.15;
		double diamWick = 0.007;
		double lenWick = 0.0254;
		double propDirect = 0.8; // # Assume a proportion of direct radiation =
									// direct/(diffuse + direct)
		double ZenithAngle = 0.; // # angle of sun from directly above
		double MinWindSpeed = 0.1; // # 0 wind speed upsets log function
		double AtmPressure = 101; // # Atmospheric pressure in kPa
		double ERROR_RETURN = -9999;

		// # Purpose: to calculate the globe temperature
		// # Author: James C. Liljegren
		// # Decision and Information Sciences Division
		// # Argonne National Laboratory
		// # Pressure in kPa (Atm =101 kPa)
		// #Fix up out-of bounds problems with zenith
		if (zenith <= 0)
		{
			zenith = 0.0000000001;
		}

		if (zenith > 1.57)
		{
			zenith = 1.57;
		}

		double Pair = PairIn * 10;
		double cza = Math.cos(zenith);
		double converge = 0.05;
		double alb_sfc = SurfAlbedo;
		double alb_globe = 0.05;
		double emis_globe = 0.95;
		double emis_sfc = 0.999;
		double Tair = Ta + 273.15;
		double RH = relh * 0.01;

		double TsfcK = Tsfc + 273.15;
		double Tglobe_prev = Tair;
		double area = 3.1415 * diamGlobe * diamGlobe;

		// #Do iteration
		int testno = 1;
		boolean continueLoop = true;
		double Tglobe = 0.0;

		while (continueLoop)
		{
			testno = testno + 1;
			if (testno > 1000)
			{
				System.out.println("No convergence: values too extreme");
				fTg4 = ERROR_RETURN;
				return fTg4;
			}
			// # Evaluate properties at the average temperature
			double Tref = 0.5 * (Tglobe_prev + Tair); 

			double h = h_sphere_in_air(Tref, Pair, speed, speedMin);
			// #a=area * 0.5 * emis_globe * stefanb * (emisAtmValue * Tair**4 +  emis_sfc * TsfcK**4 )
			// # term for energy gained by globe due to thermal radiation from atm and surface
			double a = area * 0.5 * emis_globe * (lIn + lOut); 
			// term for energy gained due to diffuse irradiance
			double b = area * 0.5 * (1 - alb_globe) * (1 - fdir) * solar; 
			// term for energy gained due to direct irradiance
			double c = area * 0.25 * (1 - alb_globe) * fdir * solar / cza;
			// # term for solar irradiance reflected from the surface that is absorbed by the globe														
			double d = area * 0.5 * (1 - alb_globe) * alb_sfc * solar; 
			// # term for energy lost from the globe due to convection
			double e = area * h * (Tglobe_prev - Tair); 
			// #print *,'a,b,c,d,e',a,b,c,d,e

			Tglobe = Math.pow(((a + b + c + d - e) / (area * emis_globe * stefanb)), 0.25);
			double dT = Tglobe - Tglobe_prev;

			if (Math.abs(dT) < converge)
			{
				continueLoop = false;
			}
			else
			{
				Tglobe_prev = (0.9 * Tglobe_prev + 0.1 * Tglobe);
				continueLoop = true;
			}
		}
		fTg4 = Tglobe - 273.15;
		return fTg4;
	}
    
	public double fTmrtD(double Ta, double Tg, double ws)
	{
		// #from Kantor and Unger 2011
		double emis_globe = 0.95;
		double diamGlobe = 0.15;
		double wsCm = ws / 100;// # convert m/s to cm/s
		// #Tmrt = Tg + 2.42 * wsCm * (Tg - Ta) ;
		double Tmrt = Math.pow((((Math.pow((Tg + 273.15), 4)
				+ (1.1 * Math.pow(10, 8) * Math.pow(wsCm, 0.6) / (emis_globe * Math.pow(diamGlobe, 0.4)))
						* (Tg - Ta)))),
				0.25) - 273.15;
		double fTmrtD = Tmrt;
		return fTmrtD;

	}     

	public double getUTCIForGrid_RH(double Ta, double ws, double RH, double tmrt)
	{
		// # UTCI.fUTCI2(Ta, ws, RH, tmrt);
		// #RH=calculateRH(Ta, vapor);
		// #print (Ta.item(), ws.item(), float(RH), tmrt.item());
		double getUTCIForGrid = fUTCI2(Ta, ws, RH, tmrt);
		return getUTCIForGrid;

	}  
    

public double fUTCI2(double Ta, 
		//double Tg, double Td, 
		double ws, 
		//double solar, 
		double RH, double Tmrt)
{
//UTCI calculation using full formula from utci.org website
// Check if one and only one of RH or Td have been entered
  double D_Tmrt; 
  double Td;
 
//  if (RH != -99)
//  {
//	  if (Td != -99)
//	  {
//		  System.err.println("Error:");
//		  return ERROR_RETURN;
//	  }
	  Td = fTd(Ta, RH);
//  }
//  else
//  {
//	  RH = fRH(Ta, Td);
//  }
//  
//	//Check to make sure Td < Ta
//	if (Td > Ta) 
//	{
//		System.err.println("Error:");
//		return ERROR_RETURN;
////		fUTCI = "Error:":  Exit Function
//	}
//	
//	//Calculate Tg given solar, calculate solar given Tg
//	if (solar != -99)
//	{
//		if (Tg != -99)
//		{
//			System.err.println("Error:");
//			return ERROR_RETURN;
//		}
//		Tg = fTg(Ta, RH, AtmPressure, ws, solar, propDirect, ZenithAngle, MinWindSpeed);
//	}

//	double Tmrt = fTmrtB(Ta, Tg, ws); //takes Tg and changes it into Tmrt uses Bernard formula

    // UTCI, Version a 0.002, October 2009
    // Copyright (C) 2009  Peter Broede
    // Program for calculating UTCI Temperature (UTCI)
    // released for public use after termination of COST Action 730
    // Copyright (C) 2009  Peter Broede
    // This program is distributed in the hope that it will be useful,
    // but WITHOUT ANY WARRANTY; without even the implied warranty of
    // MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

// Disclaimer of Warranty.
// THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN OTHERWISE STATED IN WRITING
// THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM “AS IS” WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED
// OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
// THE ENTIRE RISK AS TO THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE, YOU
// ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.

// Limitation of Liability.
// IN NO EVENT UNLESS REQUIRED BY APPLICABLE LAW OR AGREED TO IN WRITING WILL ANY COPYRIGHT HOLDER, OR ANY OTHER PARTY WHO
// MODIFIES AND/OR CONVEYS THE PROGRAM AS PERMITTED ABOVE, BE LIABLE TO YOU FOR DAMAGES, INCLUDING ANY GENERAL, SPECIAL,
// INCIDENTAL OR CONSEQUENTIAL DAMAGES ARISING OUT OF THE USE OR INABILITY TO USE THE PROGRAM
// (INCLUDING BUT NOT LIMITED TO LOSS OF DATA OR DATA BEING RENDERED INACCURATE OR LOSSES SUSTAINED BY YOU OR THIRD PARTIES
// OR A FAILURE OF THE PROGRAM TO OPERATE WITH ANY OTHER PROGRAMS), EVEN IF SUCH HOLDER OR OTHER PARTY HAS BEEN ADVISED
// OF THE POSSIBILITY OF SUCH DAMAGES.

 // DOUBLE PRECISION Function value is the UTCI in degree Celsius
 // computed by a 6th order approximating polynomial from the 4 Input paramters
 //
 // Input parameters (all of type DOUBLE PRECISION)
 // - Ta       : air temperature, degree Celsius
 // - ehPa    : water vapour presure, hPa=hecto Pascal
 // - Tmrt   : mean radiant temperature, degree Celsius
 // - va10m  : wind speed 10 m above ground level in m/s
 //
 //  UTCI_approx, Version a 0.002, October 2009
 //  Copyright (C) 2009  Peter Broede

      
      double PA = 0.6108 * Math.exp(17.29 * Td / (Td + 237.3)); //takes Td and changes it into Pa - vapour pressure in hPa)
      D_Tmrt = Tmrt - Ta;
        // calculate 6th order polynomial as approximation
      double ET1 = Ta + 0.607562052 - 0.0227712343 * Ta + 0.000806470249 * Ta * Ta - 0.000154271372 * Ta * Ta * Ta 
        - 0.00000324651735 * Ta * Ta * Ta * Ta + 7.32602852E-08 * Ta * Ta * Ta * Ta * Ta + 1.35959073E-09 * Ta * Ta * Ta * Ta * Ta * Ta 
        - 2.2583652 * ws + 0.0880326035 * Ta * ws + 0.00216844454 * Ta * Ta * ws - 0.0000153347087 * Ta * Ta * Ta * ws 
        - 0.000000572983704 * Ta * Ta * Ta * Ta * ws - 2.55090145E-09 * Ta * Ta * Ta * Ta * Ta * ws 
        - 0.751269505 * ws * ws - 0.00408350271 * Ta * ws * ws - 0.0000521670675 * Ta * Ta * ws * ws 
        + 0.00000194544667 * Ta * Ta * Ta * ws * ws + 1.14099531E-08 * Ta * Ta * Ta * Ta * ws * ws 
        + 0.158137256 * ws * ws * ws - 0.0000657263143 * Ta * ws * ws * ws + 0.000000222697524 * Ta * Ta * ws * ws * ws - 4.16117031E-08 * Ta * Ta * Ta * ws * ws * ws 
        - 0.0127762753 * ws * ws * ws * ws + 0.00000966891875 * Ta * ws * ws * ws * ws + 2.52785852E-09 * Ta * Ta * ws * ws * ws * ws 
        + 0.000456306672 * ws * ws * ws * ws * ws - 0.000000174202546 * Ta * ws * ws * ws * ws * ws - 0.00000591491269 * ws * ws * ws * ws * ws * ws 
        + 0.398374029 * D_Tmrt + 0.000183945314 * Ta * D_Tmrt - 0.00017375451 * Ta * Ta * D_Tmrt 
        - 0.000000760781159 * Ta * Ta * Ta * D_Tmrt + 3.77830287E-08 * Ta * Ta * Ta * Ta * D_Tmrt + 5.43079673E-10 * Ta * Ta * Ta * Ta * Ta * D_Tmrt 
        - 0.0200518269 * ws * D_Tmrt + 0.000892859837 * Ta * ws * D_Tmrt + 0.00000345433048 * Ta * Ta * ws * D_Tmrt 
        - 0.000000377925774 * Ta * Ta * Ta * ws * D_Tmrt - 1.69699377E-09 * Ta * Ta * Ta * Ta * ws * D_Tmrt + 0.000169992415 * ws * ws * D_Tmrt 
        - 0.0000499204314 * Ta * ws * ws * D_Tmrt + 0.000000247417178 * Ta * Ta * ws * ws * D_Tmrt + 1.07596466E-08 * Ta * Ta * Ta * ws * ws * D_Tmrt 
        + 0.0000849242932 * ws * ws * ws * D_Tmrt + 0.00000135191328 * Ta * ws * ws * ws * D_Tmrt - 6.21531254E-09 * Ta * Ta * ws * ws * ws * D_Tmrt 
        - 0.00000499410301 * ws * ws * ws * ws * D_Tmrt - 1.89489258E-08 * Ta * ws * ws * ws * ws * D_Tmrt + 8.15300114E-08 * ws * ws * ws * ws * ws 
        * D_Tmrt + 0.00075504309 * D_Tmrt * D_Tmrt;
        
      double ET3 = -0.0000565095215 * Ta * D_Tmrt * D_Tmrt + 
        (-0.000000452166564) * Ta * Ta * D_Tmrt * D_Tmrt + 
        (2.46688878E-08) * Ta * Ta * Ta * D_Tmrt * D_Tmrt + 
        (2.42674348E-10) * Ta * Ta * Ta * Ta * D_Tmrt * D_Tmrt + 
        (0.00015454725) * ws * D_Tmrt * D_Tmrt + 
        (0.0000052411097) * Ta * ws * D_Tmrt * D_Tmrt + 
        (-8.75874982E-08) * Ta * Ta * ws * D_Tmrt * D_Tmrt + 
        (-1.50743064E-09) * Ta * Ta * Ta * ws * D_Tmrt * D_Tmrt + 
        (-0.0000156236307) * ws * ws * D_Tmrt * D_Tmrt + 
        (-0.000000133895614) * Ta * ws * ws * D_Tmrt * D_Tmrt + 
        (2.49709824E-09) * Ta * Ta * ws * ws * D_Tmrt * D_Tmrt + 
        (0.000000651711721) * ws * ws * ws * D_Tmrt * D_Tmrt + 
        (1.94960053E-09) * Ta * ws * ws * ws * D_Tmrt * D_Tmrt + 
        (-1.00361113E-08) * ws * ws * ws * ws * D_Tmrt * D_Tmrt + 
        (-0.0000121206673) * D_Tmrt * D_Tmrt * D_Tmrt + 
        (-0.00000021820366) * Ta * D_Tmrt * D_Tmrt * D_Tmrt + 
        (7.51269482E-09) * Ta * Ta * D_Tmrt * D_Tmrt * D_Tmrt + 
        (9.79063848E-11) * Ta * Ta * Ta * D_Tmrt * D_Tmrt * D_Tmrt + 
        (0.00000125006734) * ws * D_Tmrt * D_Tmrt * D_Tmrt + 
        (-1.81584736E-09) * Ta * ws * D_Tmrt * D_Tmrt * D_Tmrt + 
        (-3.52197671E-10) * Ta * Ta * ws * D_Tmrt * D_Tmrt * D_Tmrt + 
        (-0.000000033651463) * ws * ws * D_Tmrt * D_Tmrt * D_Tmrt + 
        (1.35908359E-10) * Ta * ws * ws * D_Tmrt * D_Tmrt * D_Tmrt + 
        (4.1703262E-10) * ws * ws * ws * D_Tmrt * D_Tmrt * D_Tmrt + 
        (-1.30369025E-09) * D_Tmrt * D_Tmrt * D_Tmrt * D_Tmrt;
        
      double  ET4 = 4.13908461E-10 * Ta * D_Tmrt * D_Tmrt * D_Tmrt * D_Tmrt + 
        (9.22652254E-12) * Ta * Ta * D_Tmrt * D_Tmrt * D_Tmrt * D_Tmrt + 
        (-5.08220384E-09) * ws * D_Tmrt * D_Tmrt * D_Tmrt * D_Tmrt + 
        (-2.24730961E-11) * Ta * ws * D_Tmrt * D_Tmrt * D_Tmrt * D_Tmrt + 
        (1.17139133E-10) * ws * ws * D_Tmrt * D_Tmrt * D_Tmrt * D_Tmrt + 
        (6.62154879E-10) * D_Tmrt * D_Tmrt * D_Tmrt * D_Tmrt * D_Tmrt + 
        (4.0386326E-13) * Ta * D_Tmrt * D_Tmrt * D_Tmrt * D_Tmrt * D_Tmrt + 
        (1.95087203E-12) * ws * D_Tmrt * D_Tmrt * D_Tmrt * D_Tmrt * D_Tmrt + 
        (-4.73602469E-12) * D_Tmrt * D_Tmrt * D_Tmrt * D_Tmrt * D_Tmrt * D_Tmrt + 
        (5.12733497) * PA + 
        (-0.312788561) * Ta * PA + 
        (-0.0196701861) * Ta * Ta * PA + 
        (0.00099969087) * Ta * Ta * Ta * PA + 
        (0.00000951738512) * Ta * Ta * Ta * Ta * PA + 
        (-0.000000466426341) * Ta * Ta * Ta * Ta * Ta * PA + 
        (0.548050612) * ws * PA + 
        (-0.00330552823) * Ta * ws * PA + 
        (-0.0016411944) * Ta * Ta * ws * PA + 
        (-0.00000516670694) * Ta * Ta * Ta * ws * PA + 
        (0.000000952692432) * Ta * Ta * Ta * Ta * ws * PA + 
        (-0.0429223622) * ws * ws * PA + 
        (0.00500845667) * Ta * ws * ws * PA + 
        (0.00000100601257) * Ta * Ta * ws * ws * PA + 
        (-0.00000181748644) * Ta * Ta * Ta * ws * ws * PA + 
        (-0.00125813502) * ws * ws * ws * PA;
        
      double  ET5 = -0.000179330391 * Ta * ws * ws * ws * PA + 
        (0.00000234994441) * Ta * Ta * ws * ws * ws * PA + 
        (0.000129735808) * ws * ws * ws * ws * PA + 
        (0.0000012906487) * Ta * ws * ws * ws * ws * PA + 
        (-0.00000228558686) * ws * ws * ws * ws * ws * PA + 
        (-0.0369476348) * D_Tmrt * PA + 
        (0.00162325322) * Ta * D_Tmrt * PA + 
        (-0.000031427968) * Ta * Ta * D_Tmrt * PA + 
        (0.00000259835559) * Ta * Ta * Ta * D_Tmrt * PA + 
        (-4.77136523E-08) * Ta * Ta * Ta * Ta * D_Tmrt * PA + 
        (0.0086420339) * ws * D_Tmrt * PA + 
        (-0.000687405181) * Ta * ws * D_Tmrt * PA + 
        (-0.00000913863872) * Ta * Ta * ws * D_Tmrt * PA + 
        (0.000000515916806) * Ta * Ta * Ta * ws * D_Tmrt * PA + 
        (-0.0000359217476) * ws * ws * D_Tmrt * PA + 
        (0.0000328696511) * Ta * ws * ws * D_Tmrt * PA + 
        (-0.000000710542454) * Ta * Ta * ws * ws * D_Tmrt * PA + 
        (-0.00001243823) * ws * ws * ws * D_Tmrt * PA + 
        (-0.000000007385844) * Ta * ws * ws * ws * D_Tmrt * PA + 
        (0.000000220609296) * ws * ws * ws * ws * D_Tmrt * PA + 
        (-0.00073246918) * D_Tmrt * D_Tmrt * PA + 
        (-0.0000187381964) * Ta * D_Tmrt * D_Tmrt * PA + 
        (0.00000480925239) * Ta * Ta * D_Tmrt * D_Tmrt * PA + 
        (-0.000000087549204) * Ta * Ta * Ta * D_Tmrt * D_Tmrt * PA + 
        (0.000027786293) * ws * D_Tmrt * D_Tmrt * PA;
        
      double  ET6 = -0.00000506004592 * Ta * ws * D_Tmrt * D_Tmrt * PA + 
        (0.000000114325367) * Ta * Ta * ws * D_Tmrt * D_Tmrt * PA + 
        (0.00000253016723) * ws * ws * D_Tmrt * D_Tmrt * PA + 
        (-1.72857035E-08) * Ta * ws * ws * D_Tmrt * D_Tmrt * PA + 
        (-3.95079398E-08) * ws * ws * ws * D_Tmrt * D_Tmrt * PA + 
        (-0.000000359413173) * D_Tmrt * D_Tmrt * D_Tmrt * PA + 
        (0.000000704388046) * Ta * D_Tmrt * D_Tmrt * D_Tmrt * PA + 
        (-1.89309167E-08) * Ta * Ta * D_Tmrt * D_Tmrt * D_Tmrt * PA + 
        (-0.000000479768731) * ws * D_Tmrt * D_Tmrt * D_Tmrt * PA + 
        (7.96079978E-09) * Ta * ws * D_Tmrt * D_Tmrt * D_Tmrt * PA + 
        (1.62897058E-09) * ws * ws * D_Tmrt * D_Tmrt * D_Tmrt * PA + 
        (3.94367674E-08) * D_Tmrt * D_Tmrt * D_Tmrt * D_Tmrt * PA + 
        (-1.18566247E-09) * Ta * D_Tmrt * D_Tmrt * D_Tmrt * D_Tmrt * PA + 
        (3.34678041E-10) * ws * D_Tmrt * D_Tmrt * D_Tmrt * D_Tmrt * PA + 
        (-1.15606447E-10) * D_Tmrt * D_Tmrt * D_Tmrt * D_Tmrt * D_Tmrt * PA + 
        (-2.80626406) * PA * PA + 
        (0.548712484) * Ta * PA * PA + 
        (-0.0039942841) * Ta * Ta * PA * PA + 
        (-0.000954009191) * Ta * Ta * Ta * PA * PA + 
        (0.0000193090978) * Ta * Ta * Ta * Ta * PA * PA + 
        (-0.308806365) * ws * PA * PA + 
        (0.0116952364) * Ta * ws * PA * PA + 
        (0.000495271903) * Ta * Ta * ws * PA * PA + 
        (-0.0000190710882) * Ta * Ta * Ta * ws * PA * PA + 
        (0.00210787756) * ws * ws * PA * PA;
        
      double  ET7 = -0.000698445738 * Ta * ws * ws * PA * PA + 
        (0.0000230109073) * Ta * Ta * ws * ws * PA * PA + 
        (0.00041785659) * ws * ws * ws * PA * PA + 
        (-0.0000127043871) * Ta * ws * ws * ws * PA * PA + 
        (-0.00000304620472) * ws * ws * ws * ws * PA * PA + 
        (0.0514507424) * D_Tmrt * PA * PA + 
        (-0.00432510997) * Ta * D_Tmrt * PA * PA + 
        (0.0000899281156) * Ta * Ta * D_Tmrt * PA * PA + 
        (-0.000000714663943) * Ta * Ta * Ta * D_Tmrt * PA * PA + 
        (-0.000266016305) * ws * D_Tmrt * PA * PA + 
        (0.000263789586) * Ta * ws * D_Tmrt * PA * PA + 
        (-0.00000701199003) * Ta * Ta * ws * D_Tmrt * PA * PA + 
        (-0.000106823306) * ws * ws * D_Tmrt * PA * PA + 
        (0.00000361341136) * Ta * ws * ws * D_Tmrt * PA * PA + 
        (0.000000229748967) * ws * ws * ws * D_Tmrt * PA * PA + 
        (0.000304788893) * D_Tmrt * D_Tmrt * PA * PA + 
        (-0.0000642070836) * Ta * D_Tmrt * D_Tmrt * PA * PA + 
        (0.00000116257971) * Ta * Ta * D_Tmrt * D_Tmrt * PA * PA + 
        (0.00000768023384) * ws * D_Tmrt * D_Tmrt * PA * PA + 
        (-0.000000547446896) * Ta * ws * D_Tmrt * D_Tmrt * PA * PA + 
        (-0.000000035993791) * ws * ws * D_Tmrt * D_Tmrt * PA * PA + 
        (-0.00000436497725) * D_Tmrt * D_Tmrt * D_Tmrt * PA * PA + 
        (0.000000168737969) * Ta * D_Tmrt * D_Tmrt * D_Tmrt * PA * PA + 
        (2.67489271E-08) * ws * D_Tmrt * D_Tmrt * D_Tmrt * PA * PA + 
        (3.23926897E-09) * D_Tmrt * D_Tmrt * D_Tmrt * D_Tmrt * PA * PA;
        
      double  ET8 = -0.0353874123 * PA * PA * PA + 
        (-0.22120119) * Ta * PA * PA * PA + 
        (0.0155126038) * Ta * Ta * PA * PA * PA + 
        (-0.000263917279) * Ta * Ta * Ta * PA * PA * PA + 
        (0.0453433455) * ws * PA * PA * PA + 
        (-0.00432943862) * Ta * ws * PA * PA * PA + 
        (0.000145389826) * Ta * Ta * ws * PA * PA * PA + 
        (0.00021750861) * ws * ws * PA * PA * PA + 
        (-0.0000666724702) * Ta * ws * ws * PA * PA * PA + 
        (0.000033321714) * ws * ws * ws * PA * PA * PA + 
        (-0.00226921615) * D_Tmrt * PA * PA * PA + 
        (0.000380261982) * Ta * D_Tmrt * PA * PA * PA + 
        (-5.45314314E-09) * Ta * Ta * D_Tmrt * PA * PA * PA + 
        (-0.000796355448) * ws * D_Tmrt * PA * PA * PA + 
        (0.0000253458034) * Ta * ws * D_Tmrt * PA * PA * PA + 
        (-0.00000631223658) * ws * ws * D_Tmrt * PA * PA * PA + 
        (0.000302122035) * D_Tmrt * D_Tmrt * PA * PA * PA + 
        (-0.00000477403547) * Ta * D_Tmrt * D_Tmrt * PA * PA * PA + 
        (0.00000173825715) * ws * D_Tmrt * D_Tmrt * PA * PA * PA + 
        (-0.000000409087898) * D_Tmrt * D_Tmrt * D_Tmrt * PA * PA * PA + 
        (0.614155345) * PA * PA * PA * PA + 
        (-0.0616755931) * Ta * PA * PA * PA * PA + 
        (0.00133374846) * Ta * Ta * PA * PA * PA * PA + 
        (0.00355375387) * ws * PA * PA * PA * PA + 
        (-0.000513027851) * Ta * ws * PA * PA * PA * PA;
        
      double  ET9 = 0.000102449757 * ws * ws * PA * PA * PA * PA + 
        (-0.00148526421) * D_Tmrt * PA * PA * PA * PA + 
        (-0.0000411469183) * Ta * D_Tmrt * PA * PA * PA * PA + 
        (-0.00000680434415) * ws * D_Tmrt * PA * PA * PA * PA + 
        (-0.00000977675906) * D_Tmrt * D_Tmrt * PA * PA * PA * PA + 
        (0.0882773108) * PA * PA * PA * PA * PA + 
        (-0.00301859306) * Ta * PA * PA * PA * PA * PA + 
        (0.00104452989) * ws * PA * PA * PA * PA * PA + 
        (0.000247090539) * D_Tmrt * PA * PA * PA * PA * PA + 
        (0.00148348065) * PA * PA * PA * PA * PA * PA;

      double fUTCI = ET1 + ET3 + ET4 + ET5 + ET6 + ET7 + ET8 + ET9;
      return fUTCI;

}

public double Tmrt_sstr(double Ta, double Kdown, double ea)
{
	 // Input variables:
    // dsm = digital surface model
    // scale = height to pixel size (2m pixel gives scale = 0.5)
    // header = ESRI Ascii Grid header
    // sizey,sizex = no. of pixels in x and y
    // svf,svfN,svfW,svfE,svfS = SVFs for building and ground
    // svfveg,svfNveg,svfEveg,svfSveg,svfWveg = Veg SVFs blocking sky
    // svfaveg,svfEaveg,svfSaveg,svfWaveg,svfNaveg = Veg SVFs blocking buildings
    // vegdem = Vegetation canopy DSM
    // vegdem2 = Vegetation trunk zone DSM
    // albedo_b = buildings
   
  
    // ewall = Emissivity of building walls

    // altitude = Sun altitude (degree)
    // azimuth = Sun azimuth (degree)
    // zen = Sun zenith angle (radians)
    // jday = day of year
    // usevegdem = use vegetation scheme
    // onlyglobal = calculate dir and diff from global
    // buildings = Boolena grid to identify building pixels
    // location = geographic location
    // height = height of measurements point
    // psi = 1 - Transmissivity of shortwave through vegetation
    // output = output settings
    // fileformat = fileformat of output grids
    // landcover = use landcover scheme !!!NEW IN 2015a!!!
    // sensorheight = Sensorheight of wind sensor
    // lc_grid = grid with landcoverclasses
    // lc_class = table with landcover properties
    // dectime = decimal time
    // altmax = maximum sun altitude
    // dirwalls = aspect of walls
    // walls = one pixel row outside building footprints
    
	// cyl = consider man as cylinder instead of cube
	// absK = human absorption coefficient for shortwave radiation
	double absK = 0.7;
	// absL = human absorption coefficient for longwave radiation
	double absL = 0.97;
    // Fside = The angular factors between a person and the surrounding surfaces
    // Fup = The angular factors between a person and the surrounding surfaces
	
	int cyl = 1;
	
	double alb_sfc = SurfAlbedo;
    double alb_globe = 0.05;
    double emis_globe = 0.95;
    double emis_sfc = 0.999;
    double Kup = Kdown * (1.0-alb_sfc);

    // %Instrument offset in degrees
    double t = 0.;

    // %Stefan Bolzmans Constant
    double SBC = 5.67051e-8;
    
//    // Find sunrise decimal hour - new from 2014a
//    double SNUP = daylen.daylen_(jday, latitude);

//    // Vapor pressure
//    double ea = 6.107 * Math.pow(10, ((7.5 * Ta) / (237.3 + Ta))) * (RH / 100.);

//    // Determination of clear - sky emissivity from Prata (1996)
//    double msteg = 46.5 * (ea / (Ta + 273.15));
//    double esky = (1 - (1 + msteg) * Math.exp(-(Math.pow((1.2 + 3.0 * msteg), 0.5) ))) + elvis;  // -0.04 old error from Jonsson et al.2006

	
//    // // // // // // // Calculation of shortwave daytime radiative fluxes // // // // // // //
//    double Kdown = radI * shadow * Math.sin(altitude * (Math.PI / 180)) + radD * svfbuveg + albedo_b * (1 - svfbuveg) 
//    		*  (radG * (1 - F_sh) + radD * F_sh);
	
	double Sstr;
	double Knorth=0,Keast=0,Ksouth=0,Kwest=0;
	double Lnorth=0,Least=0,Lsouth=0,Lwest=0;
	double Fup = 1.0;
//	double Fdown = 1.0;
	double Fside = 0.0;
	double KsideI = 0.0;
	
//	! Prata's clear sky formula (QJRMS 1996)
	double Ldown=(1.-(1.+46.5*ea/Ta)*Math.exp(-(Math.pow((1.2+3.*46.5*ea/Ta),(0.5)) )))*SBC*Math.pow(Ta,4);
	double Lup = emis_sfc * SBC * Math.pow(Ta +273.15,4);
	
	Knorth=Kdown;
	Keast=Kdown;
	Ksouth=Kdown;
	Kwest=Kdown;
	Lnorth=Ldown;
	Least=Ldown;
	Lsouth=Ldown;
	Lwest=Ldown;
	
    // // // // Calculation of radiant flux density and Tmrt // // // //
    if (cyl == 1)  // Human body considered as an cyliner
    {
        Sstr = absK * (KsideI + (Kdown + Kup) * Fup + (Knorth + Keast + Ksouth + Kwest) * Fside) 
        	 + absL * (Ldown * Fup + Lup * Fup + Lnorth * Fside + Least * Fside + Lsouth * Fside + Lwest * Fside);
    }
    else // Human body considered as a standing cube
    {
        Sstr = absK * ((Kdown + Kup) * Fup + (Knorth + Keast + Ksouth + Kwest) * Fside) 
        	 + absL * (Ldown * Fup + Lup * Fup + Lnorth * Fside + Least * Fside + Lsouth * Fside + Lwest * Fside);
    }

	
	double Tmrt = Math.sqrt(Math.sqrt((Sstr / (absL * SBC)))) - 273.2;
	return Tmrt;
}

public double calcTmrt(double kd, double ld, double ku, double lu)
{
    double downAngular = 0.5;
    double upAngular = 0.5;
    double AbsorbCoeffSW=0.7;
    double EmmisHumanBody=0.97;
    double k = kd * downAngular + ku * upAngular;
    double l = ld * downAngular + lu * upAngular;
    double s = AbsorbCoeffSW * k + EmmisHumanBody * l;
    double tmrt = ( Math.pow((s / (EmmisHumanBody * 5.67E-8) ),0.25) ) -273.15;
    return tmrt;
}

//https://github.com/mostaphaRoudsari/ladybug/blob/master/src/Ladybug_Thermal%20Comfort%20Indices.py
public double meanRadiantTemperature(double Ta, double mrt, double Tdp, double rh, double ws, double SR, 
		double N, double Tground, double Rprim, double vapourPressure, double Epot, double age, double sex, 
		double heightCM, double heightM, double weight, double bodyPosition, double Icl, double ac, 
		double acclimated, double M, double activityDuration, double HRrates, double dehydrationRiskRates, double climate)
{
//    # inputs: (Ta, Tground, Rprim, e, N):
//    # formula by Man-ENvironment heat EXchange model (MENEX_2005)
	double SBC = 5.67051e-8;
	// incoming long-wave radiation emitted from the sky hemisphere, in W/m2
    double La = 5.5*(1e-8) *(Math.pow((273.15 + Ta),4)) *(0.82 - 0.25*(Math.pow(10,(-0.094*0.75*vapourPressure)))) 
    		* (1 + 0.22*(Math.pow((N/10),2.75))) ; 
 // outgoing long-wave radiation emitted by the ground, in W/m2
    double Lg = 5.5 *(1e-8) * (Math.pow((273.15 + Tground),4));  
 // in C
    double MRT = (Math.pow(((Rprim + 0.5*Lg + 0.5*La) / (0.95*SBC)),0.25)) - 273.15 ;     
//    MRT = (((Rprim + 0.5*Lg + 0.5*La) / (0.95*5.667*(10**(-8))))**(0.25)) - 273 # in C
    
    return MRT;
}

//    		https://github.com/mostaphaRoudsari/ladybug/blob/master/src/Ladybug_Thermal%20Comfort%20Indices.py
public double meanRadiantTemperature2(double Ta, double Tground, double Rprim, double e, double N)
	{
//    # formula by Man-ENvironment heat EXchange model (MENEX_2005)
	double SBC = 5.67051e-8;
	// incoming long-wave radiation emitted from the sky hemisphere, in W/m2
    double La = 5.5*(Math.pow(10,-8)) *(Math.pow((273.15 + Ta),4)) *(0.82 - 0.25*(Math.pow(10,(-0.094*0.75*e)))) 
    		* (1 + 0.22*(Math.pow((N/10),2.75)));  
 // outgoing long-wave radiation emitted by the ground, in W/m2
    double Lg = 5.5 *(1e-8) * (Math.pow((273.15 + Tground),4));  
 // in C
    double MRT = (Math.pow(((Rprim + 0.5*Lg + 0.5*La) / (0.95*SBC)),0.25)) - 273;  
//    MRT = (((Rprim + 0.5*Lg + 0.5*La) / (0.95*5.667*(10**(-8))))**(0.25)) - 273 # in C
    
    return MRT;
	}

public double ladybugTmrt(double Ta, double Kglob, double e)
{
	double latitude = -37.4;
	double longitude = 104.0;
	double timeZone = 10;
	int month = 1;
	int day = 1;
	int hour = 12;
	double Tmrt;
	double ac = 37;
	double solarAltitudeD = noaaSolarCalculator(latitude, longitude, timeZone, month, day, hour);
	double hSl = solarAltitudeD;
	double Rprim = solarRadiationNudeMan(Kglob, hSl, ac);
	double N = 6;
	double Tground = groundTemperature(Ta, N);
//	double e; //vapour pressure
	
	Tmrt = meanRadiantTemperature2(Ta, Tground, Rprim, e, N);
	
	return Tmrt;
}

/// ac = 37  # default in %, medium colored clothes
/// Tground = groundTemperature(TaL[valueIndex], NL[valueIndex])  # in C
/// solarZenithD, solarAzimuthD, solarAltitudeD = noaaSolarCalculator(latitude, longitude, timeZone, months[listIndex], days[listIndex], hours[listIndex])  # in degrees
/// Rprim = solarRadiationNudeMan(SRL[valueIndex], solarAltitudeD, ac)  # in W/m2
//  https://raw.githubusercontent.com/mostaphaRoudsari/ladybug/master/src/Ladybug_Thermal%20Comfort%20Indices.py
public double solarRadiationNudeMan(double Kglob, double hSl, double ac)
{
	double Rprim=0.;
//    # formula from: Bioclimatic principles of recreation and tourism in Poland, 2nd edition, Blazejczyk, Kunert, 2011 (MENEX_2005 model)
    double Kt = Kglob / (-0.0015*(Math.pow(hSl,3)) + 0.1796*(Math.pow(hSl,2)) + 9.6375*hSl - 11.9);
    
    double ac_ = 1 - 0.01*ac;
    
//    # Rprim - solar radiation absorbed by nude man (W/m2)
    if (hSl <= 12)
    {
        Rprim = ac_*(0.0014*(Math.pow(Kglob,2)) + 0.476*Kglob - 3.8);
    }
    else if (hSl > 12 && Kt <= 0.8)
    {
        Rprim = 0.2467*ac_*(Math.pow(Kglob,0.9763));
    }
    else if (hSl > 12 && Kt >0.8 && Kt <=1.05)
    {
        Rprim = 3.6922*ac_*(Math.pow(Kglob,0.5842));
    }
	else if (hSl > 12 && Kt > 1.05 && Kt <=1.2)
	{
        Rprim = 43.426*ac_*(Math.pow(Kglob,0.2326));
	}
	else if (hSl > 12 && Kt >1.2)
	{
        Rprim = 8.9281*ac_*(Math.pow(Kglob,0.4861));
	}
        
    if (Rprim < 0)
    {
        Rprim = 0;
    }
    
    return Rprim;
   }



//if (len(N) == 0) or (N[0] == None):
//    NL = [6]  # default 6 tens, continental humid climate
//elif (len(N) == 8767) and (type(N[0]) == System.String):
//    # the totalSkyCover_ input contains annual data from "Import Epw" component and not 8767 numerical values
//    NL = N[7:]
//elif (len(N) > 0) and (len(N) < 8767):
//    NL = [float(N[i])  for i in range(len(N))]
public double groundTemperature(double Ta, double N)
{
	double Tground=0;
//    # formula from: Assessment of bioclimatic differentiation of Poland. Based on the human heat balance, Geographia Polonica, Matzarakis, Blazejczyk, 2007
    double N100 = N *10; //converting weather data totalSkyCover from 0 to 10% to 0 to 100%
    
    if ((N100 == 0) || (N100 >= 80))
    {
        Tground = Ta;
    }
    else if ((N100 < 80) && (Ta >= 0))
    {
        Tground = 1.25*Ta;
    }
    else if ((N100 < 80) && (Ta < 0))
    {
        Tground = 0.9*Ta;
    }
    
    return Tground;
}

public double noaaSolarCalculator(double latitude, double longitude, double timeZone, int month, int day, int hour)
{
	Common common = new Common();
//    # by NOAA Earth System Research Laboratory
//    # NOAA defines longitude and time zone as positive to the west:
    timeZone = -timeZone;
    longitude = -longitude;
//    int DOY = int(lb_preparation.getJD(month, day));
    int DOY = common.getDayOfYearFromDayAndMonth(2012, day, month);
    int minute = 0;  // default
    int second = 0;  // default
    double gamma = (2*Math.PI)/365*(DOY-1+((hour-12)/24));
    double eqtime = 229.18*(0.000075 + 0.001868*Math.cos(gamma) - 0.032077*Math.sin(gamma) - 0.014615*Math.cos(2*gamma) 
    		- 0.040849*Math.sin(2*gamma));
    double declAngle = 0.006918 - 0.399912*Math.cos(gamma) + 0.070257*Math.sin(gamma) - 0.006758*Math.cos(2*gamma) 
    	+ 0.000907*Math.sin(2*gamma) - 0.002697*Math.cos(3*gamma) + 0.00148*Math.sin(3*gamma);
    double time_offset = eqtime-4*longitude+60*timeZone;
    double tst = hour *60 + minute + second / 60 + time_offset;
    double solarHangle = (tst / 4) - 180;
    
//    # solar zenith angle
    double solarZenithR = Math.acos(Math.sin(Math.toRadians(latitude)) * Math.sin(declAngle) 
    		+ Math.cos(Math.toRadians(latitude)) * Math.cos(declAngle) * Math.cos(Math.toRadians(solarHangle)));
    double solarZenithD = Math.toDegrees(solarZenithR);
    if (solarZenithD > 90)
    {
        solarZenithD = 90;
    }
    else if (solarZenithD < 0)
    {
        solarZenithD = 0;
    }
    
//    # solar altitude angle
    double solarAltitudeD = 90 - solarZenithD;
    
//    # solar azimuth angle
    double solarAzimuthR = - (Math.sin(Math.toRadians(latitude)) * Math.cos(solarZenithR) 
    		- Math.sin(declAngle)) / (Math.cos(Math.toRadians(latitude)) * Math.sin(solarZenithR));
    solarAzimuthR = Math.acos(solarAzimuthR);
    double solarAzimuthD = Math.toDegrees(solarAzimuthR);
    
//    return solarZenithD, solarAzimuthD, solarAltitudeD;
    return solarAltitudeD;
}


}

