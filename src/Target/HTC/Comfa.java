package Target.HTC;

public class Comfa
{
	//Prandtl number
	public static final double Pr = 0.71; 
	// kinematic viscosity of air (taken from Eq 7 of Kenny et al. 2009)
	public static final double v = 1.5E-05; 
	//thermal diffusivy of air (taken from Eq 7 of Kenny et al. 2009)
	public static final double k = 22E-06; 
	 //latent heat of evaporation (Taken from Eq 7 of Jacobs et al 2014)
	public static final double L = 2.405E06;
	//from eq 4 of Jacobs et al 2014
	public static final double emiss = 0.95; 
	public static final double sigma = 5.67E-08;
	//average weight in kg // from Eq 9 of Jacobs et al 2014 
	public static final double w = 78.5; 
	//average height in cm // from Eq 9 of Jacobs et al 2014 	
	public static final double z = 168.7; 
	//boundary air resistance (Taken from Eq 6 of Jacobs et al 2014)
	public static final double ra = 63;
	//skin albedo (taken from de Freitas el al 1985 and Eq 3 of Jacobs et al 2014)
	public static final double albedo = 0.35; 
	 // volumetric heat capacity of air (taken from Eq 5 of Jacobs et al 2014)
	public static final double pCp = 1170.0;
	// body tissue resistance (taken from Eq 5 of Jacobs et al 2014)
	public static final double rt = 59.0; 
	// apparent metabolic rate (taken from Eq 5 of Jacobs et al 2014)
	public static final double Ma = 55; 
	//reduction factor for radiative area of human body (taken from Eq 3 of Jacobs et al 2014 and Campbell & Norman 1998)
	public static final double Aeff = 0.78; 
	//air permeability of clothing fabric (L m^2 s^-1) //not sure if this is a good value, this is in cm3/s/cm2, from Table 1 10.1016/S1567-4231(09)70057-7
	// but Table 13.2 of Campbell & Norman 1998, open weave shirt 1.1 to wind resistant JO cloth 0.10. So pick 0.61, average of shirts
	public static final double P=0.61; 
	// insulation value of clothing //used track suit, socks, shoes. From Table 3 Kenny et al 2009
	public static final double rco=140; 
	//density of air (kg m-3)
	public static final double p = 1.16;

	public static void main(String[] args)
	{
		Comfa c = new Comfa();
		double t =21.0; // dry bulb temperature (degrees C)
		double e = 30; // vapour pressure (hPa)
		double u = 2.0 ; // 10m wind speed (m s^-1)
		double swDown = 800; //short wave down
		double lwDown = 400; //long wave down
		double value = c.calculate(t, e, u, swDown, lwDown);
		System.out.println(value);
		
//		double M = c.getM(e, t, Ma);
//		System.out.println(M);
	}
	
	// Eq 1 of Jacobs et al 2014
	public double calculate(double t, double e, double u, double swr, double lwrin)
	{ 
		double M = getM(e, t, Ma); //metabolic heat production
		double Ts = getTs(M, rt, pCp); // skin temperature
		double H = getH(pCp, t, Ts, u, ra); // air skin flux of sensible heat
		double lwrout = getLOut(Ts); // longwave up
//		double ts ; // temperature surface (K)
		double LE = getLE(t, u, e, Ts, L); //air skin flux of latent heat
		double HR=0.0 ; // respiratory flux of sensible heat // Jacobs 2014 (from Kenny 2009) says this is accounted for in the metabolic heat production Eq 5 Jacobs 2014: getTs()
		double A = getA(w, z); // from Eq 9 of Jacobs et al 2014	
		double LER = getLER(M); // from Eq 8 of Jacobs et al 2014	 //respiratory flux of latent heat
		double Rrt = Aeff * ((1-albedo) * swr + lwrin + lwrout);  //radiation absorbed	
		double NF = M + Rrt + H + HR + LE + LER; // from Eq 3 of Jacobs et al 2014	
		
		double wl = getWL(A, LE, LER, L);	
		return NF;
	}
	
	// Eq 7 of Jacobs et al 2014, orig Eq 16 of Kenny et al 2009
	public double getLE(double T, double u, double e, double Ts, double L)
	{
		double le;
		double Tk = T + 273.15;
		// next two, see Launiainen & Vihma 1990
		double qa; // air specific humidity (kg water/kg moist air)
		qa= (0.622 * e) / (1013 - 0.387 * e); // Eq A15 of Launiainen & Vihma 1990
		double qs; // saturation specific humidity for skin temperature
//		double ts ; // temperature surface (K)
		double epsilon;
		//Eq A11, A12 of Launiainen & Vihma 1990 
		if (Tk>273.15)
		{
			epsilon = Math.exp( (-6763.6/Tk)-4.9283 * Math.log(Tk) +54.23   );
		}
		else
		{
			epsilon = Math.exp( (-6141/Tk)+24.3   );
		}
		
//	      ee = exp(-6763.6/(taup(i)+273.15)-  4.9283*log(taup(i)+273.15)+54.23) 
			
		double es = epsilon * Ts; // saturation pressure at the temperature of a surface //note 2, sec 2 of Eq A12 of Launiainen & Vihma 1990
		qs= (0.622 * es) / (1013 - 0.387 * es); // Eq A15 of Launiainen & Vihma 1990
		
		double ra = 63;//boundary air resistance (Taken from Eq 6 of Jacobs et al 2014)
		ra = getRa(u);//but try calculating it
		double rc ;//clothing resistance
		rc = getRc(u);
		
		double rcv = rc; 
		double rav = 0.92 * ra;
		le = p * L * (qa -qs)/(rcv + rav);
		
		return le;
	}
	
	//  Eq 6 of Jacobs et al 2014 // air skin flux of sensible heat
	public double getH(double pCp, double Ta, double Ts, double u, double ra)
	{
		double h;
		ra = getRa(u);//but try calculating it
		double rc ;//clothing resistance
		rc = getRc(u);
		h = pCp * (  (Ta - Ts)/(rc + ra) );
		return h;
	}
	
	// Eq 7 of Kenny et al 2009
	public double getRa(double u)
	{
		double ra;
		
		double V =u; // free stream air velocity. I think this means air speed above roughness layer
		double A,n; // empirical constants derived from experiments on heat flow from cylinders

		double Re = 0.17 * V / v;
		
		// Kreith & Black 1980
		if (Re < 4000)
		{
			A=0.683;
			n=0.466;
		}
		else if (Re < 40000)
		{
			A=0.193;
			n=0.618;
		}
		else
		{
			A=0.0266;
			n=0.805;
		}
		
		ra = 0.17 / (A * Math.pow(Re, n) * Math.pow(Pr, 0.33) * k);
		
		return ra;
	}
//  Eq 10 of Jacobs et al 2014
	public double getWL(double A, double LE, double LER, double L)
	{
		double wl ;	
		wl=-3600 * A * (LE + LER) / L;
		return wl;
	}
	
	//  Eq 8 of Jacobs et al 2014
	public double getLER(double M)
	{
		return 0.42 * (M - 58);
	}
	
	//  Eq 9 of Jacobs et al 2014
	public double getA(double w, double z)
	{
		return 0.00718 * Math.pow(w, 0.425) * Math.pow(z, 0.752);
	}
	
	// Eq 8 of Kenny et al 2009
	public double getRc(double V)
	{
		double rc;
		rc = rco * (1-0.05*Math.pow((0.196*P), 0.4)*Math.pow(V, 0.5));
		return rc;
	}
	
	//  Eq 2 of Jacobs et al 2014
	public double getAt(double t, double e, double u)
	{
		double at; //apparent temperature
		at = t + (0.33 * e) - (0.7 * u) -4.0;
		return at;
	}
	
	// Brown & Gillespie 1986, Eq 2
	public double getM(double e, double Ta, double Ma)
	{
		double f = 0.150 - (0.0173 * e) - (0.0014 * Ta);
		double M = (1.0 - f) * (Ma);
		return M;
	}
	
	//  Eq 5 of Jacobs et al 2014 //skin temperature
	public double getTs(double M, double rt, double pCp)
	{
		double Tc = getTc(M);
		double Ts = Tc - (M * rt / pCp);
		return Ts;
	}
	
	// Brown & Gillespie 1986, Eq 3
	public double getTc(double M)
	{
		double tc = 36.5 + (0.0043 * M) + 273.15;
		return tc;
	}
	
//  Eq 4 of Jacobs et al 2014
	public double getLOut(double Ts)
	{
		double lOut = emiss * sigma * Math.pow(Ts, 4);
		return lOut;
	}
	
	
//	Budget values of COMFA (Equation (1) [24]) were calculated at the reference and strategy site
//	and then compared. 
//	B = M + RRT − C − E − L (1)
//	where B = COMFA Budget value (W m−2); 
//	M = Human metabolic energy (W m−2); 
//	RRT = Radiation absorbed (W m−2); 
//	C = Convective sensible heat flux (W m−2); 
//	E = Convective latent heat flux (W m−2); 
//	L = Longwave radiation emitted (W m−2). 
//			In Equation (1), RRT and M are inputs to the human energy budget, while C, E (due to wind flow
//	past an individual), and L are outputs.

}
