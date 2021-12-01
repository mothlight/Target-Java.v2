	// PET calculation routine (2017)
	// Edouard Walther (AREP, France) and Quentin Goestchel (ENS Paris-Saclay, France)
	// based on: Peter Hoeppe's PET fortran code, from the VDI Norm 3787, Blatt 2
	// and on : Djordje Spasic's python code downloaded on github:
	// https://github.com/stgeorges/ladybug/commit/b0c2ea970252b62d22bf0e35d739db7f385a3f26
	// the reference publication can be found on 
    // ResearchGate https://www.researchgate.net/publication/324168880_The_PET_comfort_index_Questioning_the_model
	// (also available on Elsevier https://www.sciencedirect.com/science/article/pii/S0360132318301896 )

//  https://github.com/eddes/AREP/blob/master/PET_fsolve_code.py
// Java port by Kerry Nice / September 2018


package Target.HTC;

public class PETFsolveCode
{
	
	//# Skin and core temperatures set values
	public double tc_set=36.6; //# 36.8
	public double tsk_set=34; //# 33.7
	public double tbody_set=0.1*tsk_set+0.9*tc_set; //# Calculation of the body temperature through a weighted average
	
	// Input data
	// definition of constants
	public double po = 1013.25; //[hPa]
	public double rob = 1.06; // Blood density [kg/L]
	public double cb = 3.64 * 1000.; // Blood specific heat [J/kg/k]
	public double cair = 1.01 * 1000.; // Air specific heat  [J/kg/K]
	public double emsk = 0.99; // Skin emissivity
	public double emcl = 0.95; // Clothing emissivity
	public double Lvap = 2.42 * Math.pow(10., 6.) ; // Latent heat of evaporation [J/Kg]
	public double sigm = 5.67 * Math.pow(10., (-8.)) ; // Stefan-Boltzmann constant [W/(m2*K^(-4))]
	public double eta = 0.; // Body efficiency

	// Initialisation of Temperature vector with respectively: Tcore, Tskin, Tcl
	public double[] T = new double[]{38,40,40};
	public double eps = Math.pow(10,(-6)) ; // numerical tolerance
	// Dichotomy search interval (a=min / b=max)
	public double Tmin = -40;
	public double Tmax = 60;

	// Input data for the PET 
	public double Ta=30; // Air temperature in [oC]
	public double Tmrt=60; // Mean radiant temperature in [oC]
	public double HR=50; // Air relative humidity [%]
	public double v=1; // Wind velocity [m/s]
	public double age = 35;
	public double sex = 1; // 1 for men and 2 for women
	public double pos = 1;
	public double mbody = 75; //[kg]
	public double ht = 1.80; //[m]
	public double p = 1013.25; //[hPa]
	public double M = 80; // [W] Metabolic activity level
	public double icl = 0.5; // [clo] Clothing level
	
	public static final int qmblood_index = 0;
	public static final int alpha_index = 1;

	public static void main(String[] args)
	{
		PETFsolveCode petSolve = new PETFsolveCode();
//		petSolve.calculate();
		
//		double tbody = 37.01547497584506;
//		double tsk =  35.71933988388632;
//		double Suda = petSolve.Suda(tbody, tsk);
//		System.out.println(Suda);
		
//		double tcore = 37.159489986062695;
//		double tsk =  35.71933988388632;
//		double[] vasoC = petSolve.vasoC(tcore, tsk);
//		System.out.println(vasoC[qmblood_index] + " " + vasoC[alpha_index]);
		
		double[] T; double Ta; double Tmrt; double HR; double v; double age;
		double sex; double ht; double mbody; double pos; double M; double icl; boolean mode;
		double[] systReturn;
//		('Syst', array([38, 40, 40]), 30, 60, 50, 1, 35, 1, 1.8, 75, 1, 80, 0.5, True)
//		('mode', [270.4404236032846, -379.1202976508495, 10.662547756116908])
//		T = new double[]{38, 40, 40};
//		Ta = 30;
//		Tmrt = 60;
//		HR = 50;
//		v = 1;
//		age = 35;
//		sex = 1;
//		ht = 1.8 ;
//		mbody =75 ;
//		pos = 1;
//		M = 80;
//		icl= 0.5;
//		mode = true;
//		systReturn = petSolve.Syst(T, Ta, Tmrt, HR, v, age, sex, ht, mbody, pos, M, icl, mode);
//		for (double item : systReturn)
//		{
//			System.out.println(item);
//		}
		
		
//		('Syst', array([37.15948999, 35.71933988, 38.25965412]), 39.273811876773834, 39.273811876773834, 50, 0.1, 35, 
		//  1, 1.8, 75, 1, 80, 0.9, False)
//		('enbal_scal', 9.916089425132668e-07)
		
//		T = new double[]{37.15948999, 35.71933988, 38.25965412};
//		Ta = 39.273811876773834;
//		Tmrt = 39.273811876773834;
//		HR = 50;
//		v = 0.1;
//		age = 35;
//		sex = 1;
//		ht = 1.8 ;
//		mbody =75 ;
//		pos = 1;
//		M = 80;
//		icl= 0.9;
//		mode = false;
//		systReturn = petSolve.Syst(T, Ta, Tmrt, HR, v, age, sex, ht, mbody, pos, M, icl, mode);
//		for (double item : systReturn)
//		{
//			System.out.println(item);
//		}
		
//		('Syst', array([37.15948999, 35.71933988, 38.25965412]), -40, -40, 50, 0.1, 35, 1, 1.8, 75, 1, 80, 0.9, False)
//		('enbal_scal', -734.7784570984672)

//		T = new double[]{37.15948999, 35.71933988, 38.25965412};
//		Ta = -40;
//		Tmrt = -40;
//		HR = 50;
//		v = 0.1;
//		age = 35;
//		sex = 1;
//		ht = 1.8 ;
//		mbody =75 ;
//		pos = 1;
//		M = 80;
//		icl= 0.9;
//		mode = false;
//		systReturn = petSolve.Syst(T, Ta, Tmrt, HR, v, age, sex, ht, mbody, pos, M, icl, mode);
//		for (double item : systReturn)
//		{
//			System.out.println(item);
//		}
		
//		double age; double sex; double ht; double mbody; double pos; double M; double icl; 
		double[] Tstable;
		double Tmin; double Tmax; double eps;
//		(35, 1, 1.8, 75, 1, 80, 0.5, array([37.15948999, 35.71933988, 38.25965412]), -40, 60, 1e-06)
//		('PET:', 39.27)
		age = 35; 
		sex= 1; 
		ht=1.8; 
		mbody=75;
		pos=1; 
		mbody=80; 
		icl=0.5; 
		Tstable= new double[]{37.15948999, 35.71933988, 38.25965412}; 
		Tmin= -40; 
		Tmax= 60 ; 
		eps=1e-06;
		double pet = petSolve.PET(age, sex, ht, mbody, pos, mbody, icl, Tstable, Tmin, Tmax, eps);
		System.out.println(pet);

	}
	
//	public void calculate()
//	{
////			import numpy as np
////			import math as math
////			import scipy.optimize as optimize
//
//	
//		// Results 
//		double[][] Tstable = resolution(Ta,Tmrt,HR,v,age,sex,ht,mbody,pos,M,icl,T)[0];
//		System.out.println("Nodes temperature [T_core, T_skin, T_clo] " + Tstable);
////		print("Nodes temperature [T_core, T_skin, T_clo]",Tstable);
//		System.out.println("Thermal Balance " + Syst(Tstable, Ta, Tmrt, HR, v, age, sex, ht, mbody, pos, M, icl,true,
//				p, po, eta, cair, Lvap, emsk, sigm, emcl, cb,tsk_set, tc_set,tbody_set));
////		print('Thermal Balance', Syst(Tstable, Ta, Tmrt, HR, v, age, sex, ht, mbody, pos, M, icl,True)[0]);
//		System.out.println("PET:" +  PET(age, sex, ht, mbody, pos, M, icl, Tstable, Tmin, Tmax, eps));
////		print('PET:', round(PET(age, sex, ht, mbody, pos, M, icl, Tstable, Tmin, Tmax, eps),2))	;
//		
//
//	}
//	

	// Skin blood flow calculation function:
	public double[] vasoC(double tcore,double tsk
//			, double tsk_set, double tc_set
			)
	{
		double[] returnValue = new double[2];
	    //Set value signals
	    double sig_skin = tsk_set - tsk;
	    double sig_core = tcore - tc_set;
	    if (sig_core<0)
	    {
	        // In this case, Tcore<Tc_set --> the blood flow is reduced
	        sig_core=0.;
	    }
	    if (sig_skin<0)
	    {
	        // In this case, Tsk>Tsk_set --> the blood flow is increased
	        sig_skin=0.;
	    }
	    // 6.3 L/m^2/h is the set value of the blood flow
	    double qmblood = (6.3 + 75. * sig_core) / (1. + 0.5 * sig_skin);
	    // 90 L/m^2/h is the blood flow upper limit
	    if (qmblood>90)
	    {
	        qmblood=90.;
	    }
	    // in the transient model, alpha is used to update tbody
	    //alpha = 0.04177 + 0.74518 / (qmblood + 0.585417)
	    double alpha = 0.1;
	    returnValue[qmblood_index]=qmblood;
	    returnValue[alpha_index]=alpha;
//	    return (qmblood,alpha);
	    return returnValue;
	}


	// Sweating calculation function
	public double Suda(double tbody, double tsk
//			, double tbody_set, double tsk_set
			)
	{
	    double sig_body = tbody - tbody_set;
	    double sig_skin = tsk - tsk_set;
	    if (sig_body<0)
	    {
	        //In this case, Tbody<Tbody_set --> The sweat flow is 0
	        sig_body=0.;
	    }
	    if (sig_skin<0)
	    {
	        // In this case, Tsk<Tsk_set --> the sweat flow is reduced
	        sig_skin=0.;
	    }
	    //qmsw = 170 * sig_body * Math.exp((sig_skin) / 10.7)  // [g/m2/h] is the expression from Gagge's model
	    double qmsw = 304.94*Math.pow(10,(-3) ) * sig_body;
	    // 500 g/m^2/h is the upper sweat rate limit
	    if (qmsw > 500)
	    {
	        qmsw = 500;
	    }
	    return qmsw;
	}

	// Vectorial MEMI balance calculation function
	public double[] Syst(double[] T, double Ta, double Tmrt, double HR, double v, double age, double sex, 
			double ht, double mbody, double pos, double M, double icl, boolean mode
//			, 
//			double p, double po, double eta, double cair, double Lvap, double emsk, double sigm, double emcl, double cb,
//			double tsk_set, double tc_set, double tbody_set
			)
	{
		
		
		double vpa;
		double metab;
		double fec;
		
	    // Conversion of T vector in an array
		double[] arr = new double[3];
		arr[0] = T[0];//Corresponds to T_core
		arr[1] = T[1];//Corresponds to T_skin
		arr[2] = T[2];//Corresponds to T_clothes
//	    arr = np.ones((3,1));
//	    arr[0,0]=T[0]; //Corresponds to T_core
//	    arr[1,0]=T[1]; //Corresponds to T_skin
//	    arr[2,0]=T[2]; //Corresponds to T_clothes
	    T=arr;
		double[] enbal_vec = new double[3]; //required for the vectorial expression of the balance
//	    enbal_vec = np.zeros((3,1)); //required for the vectorial expression of the balance

	    // Area parameters of the body:
	    double Adu = 0.203 * Math.pow(mbody, 0.425)* Math.pow(ht, 0.725);
	    double feff=0.725;
	    if (pos == 1 || pos == 3)
	    {
	        feff = 0.725;
	    }
	    if (pos == 2)
	    {
	        feff = 0.696;
	    }
	    // Calculation of the Burton surface increase coefficient, k = 0.31 for Hoeppe:
	    double fcl = 1 + (0.31 * icl); // Increase heat exchange surface depending on clothing level
	    double facl = (173.51 * icl - 2.36 - 100.76 * icl * icl + 19.28 * Math.pow(icl, 3.0)) / 100;
	    double Aclo = Adu * facl + Adu * (fcl - 1.0);
	    double Aeffr = Adu * feff;  // Effective radiative area depending on the position of the subject
	    
		// Partial pressure of water in the air depending on relative humidity and air temperature:
	    if (mode) // mode=True is the calculation of the actual environment
	    {
	        vpa = HR / 100.0 * 6.105 * Math.exp(17.27 * Ta / (237.7 + Ta )); //[hPa]
	    }
	    else // mode=False means we are calculating the PET
	    {
	        vpa= 12; // [hPa] vapour pressure of the standard environment
	    }

	    // Convection coefficient depending on wind velocity and subject position
	    double hc = 0;
	    if (pos == 1)
	    {
	        hc = 2.67 + (6.5 *Math.pow(v,0.67));
	    }
	    if (pos == 2)
	    {
	        hc = 2.26 + (7.42 *Math.pow(v,0.67));
	    }
	    if (pos == 3)
	    {
	        hc = 8.6 * (Math.pow(v, 0.513));
	    }
	    // modification of hc with the total pressure
		hc = hc * Math.pow((p / po), 0.55);

	    // Base metabolism for men and women in [W]
		double metab_female=3.19*Math.pow(mbody,0.75)*(1.0+0.004*(30.0-age)+0.018*(ht*100.0/Math.pow(mbody,(1.0/3.0))- 42.1));
		double metab_male=3.45*Math.pow(mbody,0.75)*(1.0+0.004*(30.0-age)+0.01*(ht*100.0/Math.pow(mbody,(1.0/3.0))-43.4));
	    // Source term : metabolic activity
	    if (mode==true) // = actual environment
	    {
			metab = (M + metab_male)/Adu;
			fec = (M + metab_female)/Adu;
	    }
	    else// False=reference environment
	    {
	        metab = (80 + metab_male)/Adu;
	        fec = (80 + metab_female)/Adu;
	    }
		
	    double he = 0.0;
	    // Attribution of internal energy depending on the sex of the subject
	    if (sex == 1)
	    {
	        he = metab;
	    }
	    else if (sex == 2)
	    {
	        he = fec;
	    }
	    double h = he *(1.0 - eta); // [W/m2]

	    // Respiratory energy losses
	    // Expired air temperature calculation:
	    double texp = 0.47 * Ta + 21.0;  // [degC]
	    // Pulmonary flow rate
	    double dventpulm = he * 1.44 * Math.pow(10.0,(-6.0)) ;
	    // Sensible heat energy loss:
	    double eres = cair * (Ta - texp) * dventpulm;  // [W/m2]
	    // Latent heat energy loss:
	    double vpexp = 6.11 *Math.pow(10.0,(7.45 * texp / (235.0 + texp))  );
	    double erel = 0.623 * Lvap / p * (vpa-vpexp) * dventpulm ; // [W/m2]
	    double ere = eres + erel;  // [W/m2]

	    // Clothed fraction of the body approximation
	    double rcl = icl / 6.45;  // Conversion in m2.K/W
	    double y = 0;
	    if (facl > 1.0)
	    {
	        facl = 1.0;
	    }
	    if (icl >= 2.0)
	    {
	        y = 1.0;
	    }
	    if (icl > 0.6 && icl < 2.0)
	    {
	        y = (ht - 0.2)/ht;
	    }
	    if (icl <= 0.6 && icl > 0.3)
	    {
	        y = 0.5;
	    }
	    if (icl <= 0.3 && icl > 0.0)
	    {
	        y = 0.1;
	    }
	    // calculation of the closing radius depending on the clothing level (6.28 = 2* pi !)
	    double r2 = Adu * (fcl - 1.0 + facl) / (6.28 * ht * y);  // External radius
	    double r1 = facl * Adu /(6.28 * ht * y);  // Internal radius
	    double di = r2 - r1;
	    // Calculation of the equivalent thermal resistance of body tissues
	    double alpha = vasoC(T[0],T[1]
//	    		,tsk_set,tc_set
	    		)
	    		[alpha_index]
	    				;
	    double tbody = alpha * T[1] + (1 - alpha) * T[0];
	    double htcl = (6.28 * ht * y * di)/(rcl * Math.log(r2/r1)*Aclo);  // [W/(m2.K)]
	    // Calculation of sweat losses
	    double qmsw = Suda(tbody,T[1]
//	    		,tbody_set, tsk_set
	    		);
	    // Lvap/1000 = 2400 000[J/kg] divided by 1000 = [J/g] // qwsw/3600 for [g/m2/h] to [g/m2/s]
	    double esw = Lvap/1000* qmsw/3600;  // [W/m2]
	    // Saturation vapor pressure at temperature Tsk
	    double Pvsk = 6.105*Math.exp((17.27 * (T[1]+273.15) - 4717.03)/ (237.7 + T[1])); // [hPa]
		// Calculation of vapour transfer
	    double Lw = 16.7*Math.pow(10,(-1));  // [K/hPa] Lewis factor
	    double he_diff = hc * Lw; // diffusion coefficient of air layer
	    double fecl=1/(1+0.92*hc*rcl); // Burton efficiency factor
	    double emax = he_diff * fecl * (Pvsk - vpa); // maximum diffusion at skin surface
	    double w = esw / emax;  // skin wettedness
	    if (w > 1)
	    {
	        w=1;
	        double delta = esw-emax;
	        if (delta < 0)
	        {
	            esw=emax;
	        }
	    }
	    if (esw < 0)
	    {
	        esw=0;
	    }
	    double i_m=0.38; // Woodcock's ratio
	    double R_ecl=(1/(fcl*hc) + rcl)/(Lw*i_m); // clothing vapour transfer resistance after Woodcock's method
	    //R_ecl=0.79*1e7 // Hoeppe's method for E_diff
	    double ediff = (1 - w)*(Pvsk - vpa)/R_ecl;  // diffusion heat transfer
	    double evap = -(ediff + esw);  // [W/m2]

	    // Radiation losses
	    // For bare skin area:
	    double rbare = Aeffr*(1.0 - facl) * emsk * sigm * (Math.pow((Tmrt + 273.15),(4.0))
	    		- Math.pow((T[1] + 273.15),(4.0)))/Adu;
	    // For dressed area:
	    double rclo = feff * Aclo * emcl * sigm * (Math.pow((Tmrt + 273.15),(4.0))
	    		- Math.pow((T[2] + 273.15),(4.0)))/Adu;
	    double rsum = rclo+rbare;

	    // Convection losses //
	    double cbare = hc * (Ta - T[1]) * Adu * (1.0 - facl)/Adu;  // [w/m^2]
	    double cclo = hc * (Ta - T[2]) * Aclo/Adu;  // [W/m^2]
	    double csum = cclo+cbare;

	    // Balance equations of the 3-nodes model
	    double vasoCReturn = vasoC(T[0],T[1]
//	    		,tsk_set,tc_set
	    		)[qmblood_index];
	 // Core balance [W/m^2]
	    enbal_vec[0] = h + ere - (vasoCReturn/3600*cb+5.28)*(T[0]-T[1]); 
	 // Skin balance [W/m^2]
	    enbal_vec[1] = rbare + cbare + evap + (vasoCReturn/3600*cb+5.28)*(T[0]-T[1])- htcl*(T[1]-T[2]);  
	    // Clothes balance [W/m^2]
	    enbal_vec[2] = cclo + rclo + htcl*(T[1]-T[2]);
	    double enbal_scal = h + ere + rsum + csum +evap;

		//returning either the calculated core,skin,clo temperatures or the PET
	    if (mode)
	    {
			// if we solve for the system we need to return 3 temperatures 
	    	double[] returnValues = new double[]{enbal_vec[0],enbal_vec[1],enbal_vec[2]};
//	        return [enbal_vec[0][0],enbal_vec[1][0],enbal_vec[2][0]];
	    	return returnValues;
	    }
	    else
	    {
			// solving for the PET requires the scalar balance only
	    	double[] returnValues = new double[]{enbal_scal};
//	        return enbal_scal;
	    	return returnValues;
	    }
	}

	// Solving the 3 equation non-linear system
//	public void resolution(double Ta, double Tmrt, double HR, double v, double age, double sex, double ht, 
//			double mbody, double pos, double M, double icl, double Tx)
//	{
//	    double Tn = optimize.fsolve(Syst,Tx ,args=(Ta, Tmrt, HR, v, age, sex, ht, mbody, pos, M, icl, True));
//	    return (Tn, 1);
//	}
	
    // Definition of a function with the input variables of the PET reference situation
    public double[] f(double Tx, double[] Tstable, double age, double sex, double ht, double mbody, double pos, double M)
    {
        return Syst(Tstable, Tx, Tx, 50, 0.1, age, sex, ht, mbody, pos, M, 0.9, false);
    }

	// PET calculation with dichotomy method 
	public double PET (double age, double sex, double ht, double mbody, double pos, double M, double icl, double[] Tstable,
			double Tmin, double Tmax, double eps)
	{

	    double Ti = Tmin ;// Start of the search interval
	    double Tf = Tmax; // End 	of the search interval
	    double pet = 0.;
	    while (Tf-Ti>eps) // Dichotomy loop
	    {
	    	double f1 = f(Ti,Tstable,age,sex,ht,mbody,pos,M)[0];
	    	double f2 = f(pet,Tstable,age,sex,ht,mbody,pos,M)[0];
	        if (f1*f2<0.)
	        {
	            Tf = pet;
	        }
	        else
	        {
	            Ti = pet;
	        }
	        pet = (Ti + Tf) / 2.0;
	    }
	    return pet;
	}

	

}
