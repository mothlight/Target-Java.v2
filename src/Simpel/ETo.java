package Simpel;

public class ETo 
{
	public static int DAILY=0;
	public static int HOURLY=1;
	
	public static void main(String[] args)
	{
		ETo t = new ETo();		
		t.testHourly();
		t.testrhtoea();
	}
	
	public void testrhtoea()
	{
		double esat = esat(30.0);
		double ea= 50.0/ 100.0 * esat;
		System.out.println(ea);
	}
	
	public void testHourly()
	{
		double swDown = 294.0;// w/m2, convert to MJ/m2 for 1 hour
		double mjm2 = wm2ToMjm2(swDown, 1);
		System.out.println(mjm2);
		
		double wm2 = mjm2ToWm2(mjm2,1);
		System.out.println(wm2);
		
//		double R_s = 13.941666; 
//		double wm2 = mjm2ToWm2(R_s);
//		System.out.println(wm2);
		

		
		
		ETo eto = new ETo();
		
//		The latitude of the met station (dec deg) 
		double lat=-43.6;
//		The longitude of the met station (dec deg) (only needed if calculating ETo hourly)
		double lon=172;
//		The longitude of the center of the time zone (dec deg) (only needed if calculating ETo hourly).
		double TZ_lon=173;
//		Elevation of the met station above mean sea level (m) 
		double z_msl=500;
//		The height of the wind speed measurement (m). Default is 2 m.
		double z_u=2;
//		Wind speed at height z (m/s), set to NaN to calculate
		double U_z=Double.NaN;
//		Albedo. Should be 0.23 for the reference crop.
		double alb = 0.23;
//		Day of Year
		int Day =1;		
//		Time frequency string of the input and output. The minimum frequency is hours (H) and the maximum is month (M).
		int freq=ETo.HOURLY;
//		Time of day
		int hour = 10;		
//		Incoming shortwave radiation (MJ/m2)
		double R_s_hourly = 13.941666; 
//		Actual Vapour pressure derrived from RH
		double e_a_hourly = 1.6333333015441895;  
//		Mean Temperature (deg C)
		double T_mean_hourly = 15.550000190734863;
		
		double etoValue = eto.eto_fao_hourly(freq, lat, Day, lon, TZ_lon, z_msl, e_a_hourly, R_s_hourly, 
				T_mean_hourly, z_u, U_z, alb, hour);
		System.out.println(etoValue + " should be " + 2.0260339333668225);
		
		double width1 = 1;
		double width2 = 1;
		double hours = 1;
		double qe = convertMMETToLEWm2(etoValue/1000.,width1,width2,hours);
//		double qe = convertMMETToLEWm2(4.0/1000.,width1,width2,hours);
		System.out.println(qe);
		
	}
	
    public double convertMMETToLEWm2(double mm,double width1,double width2,double hours)
    {
//        double mm;
        double convertMMETToLEWm2;
        
        // width1,2 are dimensions of plot, x meters, y meters
        // mm is mm of ET
        //  18.0152ml/mol of water
        // heat of vaporization 40.7 KJ/mol
        
        // time (hours) * mm ET * 1M/1000mm * width1 (m) * width2 (m) 10E+06ml/m^3 * 1 mol/18.0152ml * 40.7 KJ/mol *  1W/1000KJ/sec * 60 sec/1 min * 60 min/hour * hours
        
        convertMMETToLEWm2=hours * mm * width1 * width2 / 18.0152 * 40.7 * 60*60 ;
        return convertMMETToLEWm2;
        
    }
	
	public double mjm2ToWm2(double mjm2, int hours)
	{
		double seconds = 3600.0*hours;
		double wm2 = mjm2 * (1000000.0 / seconds);
		return wm2;
	}
	public double wm2ToMjm2(double wm2, int hours)
	{		
		double seconds = 3600.0*hours;
		double mjm2 = wm2 / (1000000.0 / seconds);		
		return mjm2;
	}
	
	public double eto_fao_hourly(int freq, double lat, int Day, double lon, double TZ_lon, double z_msl, double e_a_hourly, double R_s_hourly, 
			double T_mean, double z_u, double U_z, double alb, int hour, boolean daytime)
	{
		double eto=Double.NaN;
		
		double P = P(z_msl);		
		double gamma = gamma(P);
		double R_a = radiation(lat, Day, freq, lon, TZ_lon, hour);		
		double delta = delta(T_mean);
		double R_so = R_so(z_msl, R_a);
		double R_ns = R_ns(R_s_hourly, alb);
		double R_nl = R_nl(freq, T_mean, e_a_hourly, R_s_hourly, R_so);
		double R_n = R_n(R_ns, R_nl);
//		double G = G();
		double G = G(R_n,daytime);
		double U_2 = U2(U_z, z_u);
		double e_s = e_s(T_mean);
		System.out.println("R_s "+R_s_hourly);
//		eto = eto_fao(freq, delta, R_n, G, gamma, T_mean, U_2, e_s, e_a_hourly);
		eto = eto_fao(freq, delta, R_n, G, gamma, T_mean, U_2, e_s, e_a_hourly);
		return eto;
	}
	
	public double eto_fao_hourly(int freq, double lat, int Day, double lon, double TZ_lon, double z_msl, double e_a_hourly, double R_s_hourly, 
			double T_mean, double z_u, double U_z, double alb, int hour, boolean daytime, double Rnet_simpel)
	{
		double eto=Double.NaN;
		
		double P = P(z_msl);		
		double gamma = gamma(P);
		double R_a = radiation(lat, Day, freq, lon, TZ_lon, hour);		
		double delta = delta(T_mean);
		double R_so = R_so(z_msl, R_a);
		double R_ns = R_ns(R_s_hourly, alb);
		double R_nl = R_nl(freq, T_mean, e_a_hourly, R_s_hourly, R_so);
		double R_n = R_n(R_ns, R_nl);
//		double G = G();
		double G = G(R_n,daytime);
		double U_2 = U2(U_z, z_u);
		double e_s = e_s(T_mean);
//		eto = eto_fao(freq, delta, R_n, G, gamma, T_mean, U_2, e_s, e_a_hourly);
		eto = eto_fao(freq, delta, Rnet_simpel, G, gamma, T_mean, U_2, e_s, e_a_hourly);
		return eto;
	}
	
	public double eto_fao_hourly(int freq, double lat, int Day, double lon, double TZ_lon, double z_msl, double e_a_hourly, double R_s_hourly, 
			double T_mean, double z_u, double U_z, double alb, int hour)
	{
		double eto=Double.NaN;
		
		double P = P(z_msl);		
		double gamma = gamma(P);
		double R_a = radiation(lat, Day, freq, lon, TZ_lon, hour);		
		double delta = delta(T_mean);
		double R_so = R_so(z_msl, R_a);
		double R_ns = R_ns(R_s_hourly, alb);
		double R_nl = R_nl(freq, T_mean, e_a_hourly, R_s_hourly, R_so);
		double R_n = R_n(R_ns, R_nl);
		double G = G();
		double U_2 = U2(U_z, z_u);
		double e_s = e_s(T_mean);
		eto = eto_fao(freq, delta, R_n, G, gamma, T_mean, U_2, e_s, e_a_hourly);
		return eto;
	}
	public double eto_fao(int freq, double delta, double R_n, double G, double gamma, double T_mean, double U_2, double e_s, double e_a)
	{
	//def eto_fao(self, max_ETo=15, min_ETo=0, interp=False, maxgap=15):
	//    """
	//    Function to estimate reference ET (ETo) from the `FAO 56 paper <http://www.fao.org/docrep/X0490E/X0490E00.htm>`_ [1]_ 
	//	using a minimum of T_min and T_max for daily estimates and T_mean and RH_mean for hourly, but optionally utilising the maximum number of available met parameters. 
	//	The function prioritizes the estimation of specific parameters based on the available input data.
	//
	//    Parameters
	//    ----------
	//    max_ETo : float or int
	//        The max realistic value of ETo (mm).
	//    min_ETo : float or int
	//        The min realistic value of ETo (mm).
	//    interp : False or str
	//        Should missing values be filled by interpolation? Either False if no interpolation should be performed, or a string of the interpolation method. See Pandas interpolate function for methods. Recommended interpolators are 'linear' or 'pchip'.
	//    maxgap : int
	//        The maximum missing value gap for the interpolation.
	//
	//    Returns
	//    -------
	//    DataFrame or Series
	//        If fill=False, then the function will return a Series of estimated ETo in mm. If fill is a str, then the function will return a DataFrame with an additional column for the filled ETo value in mm.
	//
	//    References
	//    ----------
	//
	//    .. [1] Allen, R. G., Pereira, L. S., Raes, D., & Smith, M. (1998). Crop evapotranspiration-Guidelines for computing crop water requirements-FAO Irrigation and drainage paper 56. FAO, Rome, 300(9), D05109.
	//    """
	
	//    ## ETo equation
		double ETo = Double.NaN;
		double Cn = 900.;
		double Cd = 0.34;
	    if (freq==HOURLY)
	    {
	    	double e_mean = e_s;	        
	        ETo = (0.408*delta*(R_n - G) + gamma*37.0/(T_mean + 273)*U_2*(e_mean - e_a))/(delta + gamma*(1 + 0.34*U_2));
	        System.out.println("! " + R_n + " " + G + " " + T_mean + " " + U_2 + " " + e_mean + " " + e_a + " ");
	    }
	    else
	    {
	    	ETo = (0.408*delta*(R_n - G) + gamma*Cn/(T_mean + 273.)*U_2*(e_s - e_a))/(delta + gamma*(1. + Cd*U_2));
	    }	   
		//    ## Remove extreme values
//	    ETo_FAO[ETo_FAO > max_ETo] = np.nan
//	    ETo_FAO[ETo_FAO < min_ETo] = np.nan	
	
	    return ETo;
		
	}
	public double P(double z_msl)
	{
//		    # Air Pressure
		    double P =  1000000.;
		    P = 101.3*Math.pow(((293 - 0.0065*z_msl)/293),5.26);
		    return P;
	}
	public double gamma(double P)
	{
//		    # Psychrometric constant
		    double gamma = (0.665*Math.pow(10,-3))*P;
		    return gamma;
	}
	public double radiation(double lat, int Day, int freq, double lon, double TZ_lon, int hour)
	{
//		    ## Raditation components
			double R_a=0.0;
//		    # R_a
		    double phi = lat*Math.PI/180.;
		    double delta = 0.409*Math.sin(2.*Math.PI*Day/365.-1.39);
		    double d_r = 1.+0.033*Math.cos(2.*Math.PI*Day/365.);
		    double w_s = Math.acos(-Math.tan(phi)*Math.tan(delta));

		    if (freq==HOURLY)
		    {		    	
		        double b = (2.*Math.PI*(Day - 81.))/364.;
		        double S_c = 0.1645*Math.sin(2.*b) - 0.1255*Math.cos(b) - 0.025*Math.sin(b);
		        double w = Math.PI/12.*(((hour+0.5) + 0.6666667*(TZ_lon - lon) + S_c) - 12.);
		        double w_1 = w - (Math.PI*1.)/24.; 
		        double w_2 = w + (Math.PI*1.)/24.; 

		        R_a = 12.*60./Math.PI*0.082*d_r*((w_2 - w_1)*Math.sin(phi)*Math.sin(delta) + Math.cos(phi)*Math.cos(delta)*(Math.sin(w_2) - Math.sin(w_1)));
		    }
		    else
		    {
		    	R_a = 24.*60./Math.PI*0.082*d_r*(w_s*Math.sin(phi)*Math.sin(delta) + Math.cos(phi)*Math.cos(delta)*Math.sin(w_s));
		    }		        
		    return R_a;
	}
	
//  10.1016/j.agwat.2007.01.014 based on
	public double G(double Rn, boolean daytime)
	{
		double G;

		if (daytime)
		{
			G = 0.1 * Rn;
		}
		else
		{
			G = 0.5 * Rn;
		}
		System.out.println("G="+G + " Rn="+ Rn);
		    return G;		    
	}
	public double delta(double T_mean)
	{
//		    # Delta
		    double delta = 4098.*(0.6108*Math.exp(17.27*T_mean/(T_mean + 237.3)))/( Math.pow(T_mean + 237.3,2));
		    return delta;
	}
	public double R_so(double z_msl, double R_a)
	{
//		    # R_so
		    double R_so = (0.75 + 2*Math.pow(10,-5)*z_msl)*R_a;
		    return R_so;
	}
	public double R_ns(double R_s, double alb)
	{
//		    # R_ns from R_s
		    double R_ns = (1. - alb)*R_s;
		    return R_ns;
	}
	public double R_nl(int freq, double T_mean, double e_a, double R_s, double R_so)
	{
		double R_nl = Double.NaN;
//		# R_nl
	    if (freq==HOURLY)
	    {
	        R_nl = (2.043*Math.pow(10,-10))*(Math.pow((T_mean + 273.16),4))*(0.34-0.14*Math.pow(e_a,0.5))*((1.35*R_s/R_so) - 0.35);
	    }
	    else
	    {
	        R_nl = (4.903*Math.pow(10,-9))*((Math.pow(T_mean + 273.16,4) + Math.pow(T_mean + 273.16 ,4))/2.)*(0.34-0.14*Math.pow(e_a,0.5))*((1.35*R_s/R_so) - 0.35);
	    }
	    return R_nl;
	}
	public double R_n(double R_ns, double R_nl)
	{
		double R_n;
//		    # R_n
	    R_n = R_ns - R_nl;
	    return R_n;
	}
	public double G()
	{
		double G;
//		    # G
		    G = 0;
		    return G;
	}
	public double U2(double U_z, double z_u)
	{
		double U_2 = 2.0;
//		    ## Wind component
		if (!Double.isNaN(U_z))
		{
			U_2 = U_z*4.87/(Math.log(67.8*z_u - 5.42));
		}
//		    # or use 2 if wind speed is not known
		return U_2;
	}
	public double e_s(double T_mean)
	{
//        double e_max = 0.6108*Math.exp(17.27*T_mean/(T_mean+237.3));
//        double e_min = 0.6108*Math.exp(17.27*T_mean/(T_mean+237.3));
//        double e_s = (e_max+e_min)/2.;
		double e_s = 0.6108*Math.exp(17.27*T_mean/(T_mean+237.3));
        return e_s;
	}
	
	public double es(double Ta)
	{
	// calculates saturation vapour pressure over water in hPa for input air temperature (ta) in celsius according to:
	// Hardy, R.; ITS-90 Formulations for Vapor Pressure, Frostpoint Temperature, Dewpoint Temperature and Enhancement Factors in the Range -100 to 100 °C;
	// Proceedings of Third International Symposium on Humidity and Moisture; edited by National Physical Laboratory (NPL), London, 1998, pp. 214-221
	// http://www.thunderscientific.com/tech_info/reflibrary/its90formulas.pdf (retrieved 2008-10-01)
	      double Tk = Ta + 273.15;  // air temp in K
	      double es = 2.7150305 * Math.log(Tk) - 2836.5744 * Math.pow(Tk, (-2)) - 6028.076559 / Tk + 19.54263612 - 0.02737830188 
	    		  * Tk + 0.000016261698 * Math.pow(Tk, 2) + 7.0229056E-10 * Math.pow(Tk, 3) - 1.8680009E-13 * Math.pow(Tk, 4);
	      es = Math.exp(es) * 0.01; // *0.01: convert Pa to hPa
	      return es;
	}
	
    public double esat(double Ta)
    {
//  Purpose: calculate the saturation vapor pressure (mb) over liquid water given the temperature (K).
//  Reference: Buck's (1981) approximation (eqn 3) of Wexler's (1976) formulae.
//  over liquid water
    	double Tk = Ta + 273.15;  // air temp in K
	    double esat = 6.1121 * Math.exp(17.502 * (Tk - 273.15) / (Tk - 32.18));
	    esat = 1.004 * esat;  // correction for moist air, if pressure is not available; for pressure > 800 mb
	    return esat;
    }
	
}
