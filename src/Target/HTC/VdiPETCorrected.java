// 	PET calculation after the LadyBug plugin (retrieved on Djordje Spasic's github :
//	https://github.com/stgeorges/ladybug/commit/b0c2ea970252b62d22bf0e35d739db7f385a3f26)
//
//	2017.11.10 by Edouard Walther and Quentin Goestschel:
//		- fixed the error on the reference environment (see paper, https://doi.org/10.1016/j.buildenv.2018.03.054)
//   https://github.com/eddes/AREP
// You are free to use them on the condition to cite the authors "E. Walther (AREP) and Q. Goestchel (ENS Paris-Saclay),
//  after D. Spasic's version". or alternately the article reference 
//   https://www.sciencedirect.com/science/article/pii/S0360132318301896
// 	
// Port to Java by Kerry Nice / September 2018	

package Target.HTC;

public class VdiPETCorrected
{
	public static final int tsk_index=0;
	public static final int enbal_index=1;
	public static final int esw_index=2;
	public static final int ediff_index=3;
	public static final int tx_index=4;
	public static final int tcl_index=5;
	
	public static final int tcore2_index=0;
	public static final int tsk2_index=1;
	public static final int tcl2_index=2;
	public static final int esw2_index=3;
	
	// Environment constants  //
	 // atmospheric pressure [hPa]
	double po = 1013.25; 
	 // real pressure [hPa]
	double p = 1013.25; 
	// Blood density kg/L
	double rob = 1.06;  
	// Blood specific heat [j/kg/k]
	double cb = 3640.0; 
	 // Skin emissivity [-]
	double emsk = 0.99; 
	// Clothes emissivity [-]
	double emcl = 0.95;  
	 // Latent heat of evaporation [J/Kg]
	double Lvap = 2.42 * Math.pow(10.0, 6.0); 
	 // Stefan-Boltzmann constant [W/(m2*K^(-4))]
	double sigm = 5.67 * Math.pow(10.0, -8.0); 
	 // Air specific heat  [J./kg/K-]
	double cair = 1010.0; 
	 // Skin diffusivity
	double rdsk = 0.79 * Math.pow(10.0, 7.0); 
	 // Clothes diffusivity
	double rdcl = 0.0;
	
	int sex = 1;
	double pos = 1;
	double age = 35;
	double mbody = 75; // Subject weight[kg]
	double ht = 1.80; // Subject size[m]
	double Adu = 0.203*Math.pow(mbody, 0.425)*Math.pow(ht,0.725); //Dubois body area
	String bodyPosition="standing";
	double feff = 0.725;
	String sexStr ="male";
	
	// Initialisation of the temperature set values
	double tc_set=36.6;
	double tsk_set=34;
	double tbody_set=0.1*tsk_set+0.9*tc_set;
	
	
	public static void main(String[] args)
	{
		VdiPETCorrected pet = new VdiPETCorrected();
	
		double Tair=25;  //air temp in C
		double Tmrt=30;  //tmrt in C
		double v_air=0.9; //air velocity in m/s
		
		double petValue = pet.petCalculationDefault(pet.po, pet.p, Tair, Tmrt, v_air);
		System.out.println("PET value = " + petValue);
	}
	
	public double petCalculationDefault(double po, double p,double Tair, double Tmrt, double v_air)
	{
		double icl=0.9;
		double M_activity=80; // [W]
		double pvap=12.0; //Imposed value of Pvap
		
		double[] systemReturn  = system(Tair, Tmrt, pvap, v_air, M_activity, icl );
		double tc = systemReturn[tcore2_index];
		double tsk = systemReturn[tsk2_index];
		double tcl = systemReturn[tcl2_index];
		double esw_real = systemReturn[esw2_index];
		double[] petReturn = pet(tc,tsk,tcl,Tair, esw_real  );
		tsk= petReturn[tsk_index]; 
		double enbal= petReturn[enbal_index]; 
		double esw= petReturn[esw_index]; 
		double ed= petReturn[ediff_index]; 
		double PET= petReturn[tx_index]; 
		tcl= petReturn[tcl_index]; 
//		tc, tsk, tcl, esw_real = system(Tair, Tmrt, pvap, v_air, M_activity, icl);
//		tsk, enbal, esw, ed, PET, tcl = pet(tc,tsk,tcl,Tair, esw_real);
		
//		System.out.println("PET value=" + PET);
//		System.out.println("Tc=" + tc);
//		System.out.println("Tsk value=" + tsk);
//		System.out.println("Tcl=" + tcl);
		return PET;
	}
	
	public void petCalcuation()
	{
			// real environment
			double M_activity=80; // [W]
			double icl=0.9;
			double Tair=21;
			double Tmrt=21;
			double v_air=0.1;
			double pvap=12.0; //Imposed value of Pvap

			double[] systemReturn  = system(Tair, Tmrt, pvap, v_air, M_activity, icl );
			double tc = systemReturn[tcore2_index];
			double tsk = systemReturn[tsk2_index];
			double tcl = systemReturn[tcl2_index];
			double esw_real = systemReturn[esw2_index];
			double[] petReturn = pet(tc,tsk,tcl,Tair, esw_real );
			tsk= petReturn[tsk_index]; 
			double enbal= petReturn[enbal_index]; 
			double esw= petReturn[esw_index]; 
			double ed= petReturn[ediff_index]; 
			double PET= petReturn[tx_index]; 
			tcl= petReturn[tcl_index]; 
//			tc, tsk, tcl, esw_real = system(Tair, Tmrt, pvap, v_air, M_activity, icl);
//			tsk, enbal, esw, ed, PET, tcl = pet(tc,tsk,tcl,Tair, esw_real);
			
			System.out.println("PET value=" + PET);
			System.out.println("Tc=" + tc);
			System.out.println("Tsk value=" + tsk);
			System.out.println("Tcl=" + tcl);		
	}

			// calcul de la reaction metabolique
	public double[] system(double ta, double tmrt, double rh, double v_air, double M, double Icl )
	{
		double[] returnValue = new double[4];
		double feff=0.0,met_base;
		double esw=0.0;
		double xx=0.0;
		boolean g100=false;
		double vb=0.0;
		double swm=0.0;
		double ed=0.0;
		int index;
		
		double vpa=rh; // ACHTUNG
	    // Area parameters of the body: //
		if (Icl< 0.03)
		{
			Icl = 0.02;
		}
		double icl = Icl;  // [clo] Clothing level
		double eta = 0.0; // Body efficiency
	    // Calculation of the Burton coefficient, k = 0.31 for Hoeppe:
		// Increasment of the exchange area depending on the clothing level:
		double fcl = 1 + (0.31 * icl); 
		if (bodyPosition.equals("sitting"))
		{
			feff = 0.696;
		}
		else if (bodyPosition.equals("standing"))
		{
			feff = 0.725;
		}
		else if (bodyPosition.equals("crouching"))
		{
			feff = 0.67;
		}
		double facl = (173.51 * icl - 2.36 - 100.76 * icl * icl + 19.28 * Math.pow(icl, 3.0)) / 100.0;

	    // Basic metabolism for men and women in [W/m2] //
	    // Attribution of internal energy depending on the sex of the subject
		if (sexStr.equals("male"))
		{
			met_base = 3.45 * Math.pow(mbody, 0.75) * (1.0 + 0.004 * (30.0 - age) + 0.01 * 
					(ht * 100.0 / Math.pow(mbody, 1.0 / 3.0) - 43.4));
		}
		else
		{
			met_base = 3.19 * Math.pow(mbody, 0.75) * (1.0 + 0.004 * (30.0 - age) + 0.018 * 
					(ht * 100.0 / Math.pow(mbody, 1.0 / 3.0) - 42.1));
		}
	    // Source term : metabolic activity
		double he = M + met_base;
		double h = he * (1.0 - eta);

	    // Respiratory energy losses //
	    // Expired air temperature calculation:
		double texp = 0.47 * ta + 21.0;

	    // Pulmonary flow rate
		double rtv = he * 1.44 * Math.pow(10.0, -6.0);

	    // Sensible heat energy loss:
		double Cres = cair * (ta - texp) * rtv;

	    // Latent heat energy loss:
		// Partial pressure of the breathing air
		double vpexp = 6.11 * Math.pow(10.0, 7.45 * texp / (235.0 + texp)); 
		double Eres = 0.623 * Lvap / p * (vpa - vpexp) * rtv;
		// total breathing heat loss
		double qresp = (Cres + Eres);

		double[] c = new double[11];

		// Core temperature list
		double[] tcore = new double[7];
		//Convection coefficient
		double hc = 2.67 + 6.5 * Math.pow(v_air,0.67); 
		// Correction with pressure
		hc = hc * Math.pow(p / po,0.55); 

	    // Clothed fraction of the body approximation //
		// conversion in m2.K/W
		double rcl = icl / 6.45; 
		double y=0;
		if (facl > 1.0)
		{
			facl = 1.0;
			rcl = icl / 6.45; // conversion clo --> m2.K/W
		}
		// y : equivalent clothed height of the cylinder
		// High clothing level : all the height of the cylinder is covered
		if (icl >= 2.0)
		{
			y = 1.0;
		}
		if (icl > 0.6 && icl < 2.0)
		{
			y = (ht - 0.2) / ht;
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
		// External radius
		double r2 = Adu * (fcl - 1.0 + facl) / (6.28 * ht * y);  
		// Internal radius
		double r1 = facl * Adu / (6.28 * ht * y) ; 
		double di = r2 - r1;
		// clothed surface
		double Acl = Adu * facl + Adu * (fcl - 1.0);
		// skin temperatures
		for (int j=1;j<=7;j++)	
		{
			double tsk = tsk_set;
			int count1 = 0;
			// Average value between the temperatures to estimate Tclothes
			double tcl = (ta + tmrt + tsk) / 3.0; 
			double enbal2 = 0.0;
			while (true)
			{
				for (int count2=1;count2<100;count2++)
				{
	                // Estimation of the radiation losses
					double rclo2 = emcl * sigm * (Math.pow(tcl + 273.2, 4.0) - Math.pow(tmrt + 273.2, 4.0)) * feff;
	                // Calculation of the thermal resistance of the body:
					double htcl = (6.28 * ht * y * di) / (rcl * Math.log(r2 / r1) * Acl);
					 // Skin temperature calculation
					tsk = (hc * (tcl - ta) + rclo2)/htcl + tcl; 

	                // Radiation losses //
					// Effective radiative area depending on the position of the subject
					double Aeffr = Adu * feff;  
	                // For bare skin area:
					double rbare = Aeffr * (1.0 - facl) * emsk * sigm * (Math.pow(tmrt + 273.2, 4.0) - Math.pow(tsk + 273.2,4.0));
	                // For dressed area:
					double rclo = feff * Acl * emcl * sigm * (Math.pow(tmrt + 273.2, 4.0) - Math.pow(tcl + 273.2,4.0));
					double rsum = rbare + rclo; //[W]

	                // Convection losses //
					double cbare = hc*(ta - tsk)*Adu * (1.0 - facl);
					double cclo  = hc*(ta - tcl)*Acl;
					double csum  = cbare + cclo;  //[W]

					// Calculation of the Terms of the second order polynomial :
					double K_blood = Adu * rob * cb;
					c[0] = h + qresp;
					c[2] = tsk_set/2 -0.5*tsk;
					c[3] = 5.28 * Adu * c[2];
					c[4] = 13.0 / 625.0 * K_blood;
					c[5] = 0.76275 * K_blood;
					c[6] =  c[3] - c[5] - tsk * c[4];
					c[7] = -c[0] * c[2] - tsk * c[3] + tsk * c[5];
					c[9] = 5.28 * Adu - 0.76275 * K_blood -  13.0 / 625.0 * K_blood * tsk;
					// discriminant //1 (b^2 - 4*a*c)
					c[10] = Math.pow((5.28*Adu - 0.76275*K_blood -  13.0/625.0*K_blood*tsk),2) - 4.0 * c[4] * (c[5] * tsk - c[0] - 5.28 * Adu * tsk);
					// discriminant //2 (b^2 - 4*a*c)
					c[8] = c[6]*c[6] -4.0*c[4]*c[7];
					if (tsk == tsk_set)
					{
						tsk = tsk_set+0.01;
					}
					//Calculation of Tcore[]:
					// case 6 : Set blood flow only
					tcore[6] = (h + qresp)/(5.28*Adu + K_blood*6.3/3600.0) + tsk;
					// cas 2 : Set blood flow + regulation
					tcore[2] = (h + qresp)/(5.28*Adu + K_blood*6.3/3600.0 / (1.0+0.5*(tsk_set - tsk))) +tsk;
					// case 3 : Maximum blood flow only
				//  max flow = 90 [L/m2/h]/3600 <=> 1/40
					tcore[3] = c[0] / (5.28*Adu + K_blood*1.0/40.0) + tsk; 
					// Roots calculation //1
					if (c[10] >= 0.0) // Numerical safety to avoid negative roots
					{
						tcore[5] = (-c[9] - Math.pow(c[10], 0.5)) / (2.0 * c[4]);
						tcore[0] = (-c[9] + Math.pow(c[10], 0.5)) / (2.0 * c[4]);
					}
					// Roots calculation //2
					if (c[8] >= 0.0)
					{
						tcore[1] = (-c[6] + Math.pow(Math.abs(c[8]), 0.5)) / (2.0 * c[4]);
						tcore[4] = (-c[6] - Math.pow(Math.abs(c[8]), 0.5)) / (2.0 * c[4]);
					}

	                // Calculation of sweat losses  //
					double tbody = 0.1 * tsk + 0.9 * tcore[j - 1];
	                // Sweating flow calculation
					swm = 304.94 * (tbody - tbody_set) * Adu / 3600000.0;
	                // Saturation vapor pressure at temperature Tsk and for 100% HR
					double vpts = 6.11 * Math.pow(10.0, 7.45 * tsk / (235.0 + tsk));
					if (tbody <= tbody_set)
					{
						swm = 0.0;
					}
					if (sex == 2)
					{
						swm=0.7 * swm;
					}
					double esweat = -swm * Lvap;
					 // Evaporation coefficient [W/(m^2*Pa)]
					double hm = 0.633 * hc / (p * cair);
					double fec = 1.0 / (1.0 + 0.92 * hc * rcl);
					 // Max latent flux
					double emax = hm * (vpa - vpts) * Adu * Lvap * fec;
					// skin wettedness
					double wetsk = esweat / emax; 
	                // esw: Latent flux depending on w [W.m-2]
					if (wetsk > 1.0)
					{
						wetsk = 1.0;
					}
					// difference between sweating and max capacity
					double eswdif = esweat - emax; 
					if (eswdif <= 0.0)
					{
						esw = emax;
					}
					if (eswdif > 0.0)
					{
						esw = esweat;
					}
					if (esw > 0.0)
					{
						esw = 0.0;
					}
					 // diffusion heat flux
					ed = Lvap / (rdsk + rdcl) * Adu * (1.0 - wetsk) * (vpa - vpts);
					// difference for the volume blood flow calculation
					double vb1 = tsk_set - tsk; 
					//  idem
					double vb2 = tcore[j - 1] - tc_set; 
					if (vb2 < 0.0)
					{
						vb2 = 0.0;
					}
					if (vb1 < 0.0)
					{
						vb1 = 0.0;
					}
					// Calculation of the blood flow depending on the difference with the set value
					vb = (6.3 + 75 * vb2) / (1.0 + 0.5 * vb1);
					// energy balance MEMI modele
					double enbal = h + ed + qresp + esw + csum + rsum;
					// clothing temperature
					if (count1 == 0)
					{
						xx = 1.0;
					}
					if (count1 == 1)
					{
						xx = 0.1;
					}
					if (count1 == 2)
					{
						xx = 0.01;
					}
					if (count1 == 3)
					{
						xx = 0.001;
					}
					if (enbal > 0.0)
					{
						tcl = tcl + xx;
					}
					if (enbal < 0.0)
					{
						tcl = tcl - xx;
					}
					if ((enbal > 0.0 || enbal2 <= 0.0) && (enbal < 0.0 || enbal2 >= 0.0))
					{
						enbal2 = enbal;
						count2 += 1;
					}
					else
					{
						break;
					}
				}
				if (count1 == 0.0 || count1 == 1.0 || count1 == 2.0)
				{
					count1 = count1 + 1;
					enbal2 = 0.0;
				}
				else
				{
					break;
				}
			// end "While True" (using 'break' statements)
			}
			for (int k=0;k<20;k++)
			{
				g100=false;
				if (count1 == 3.0 && (j != 2 && j != 5))
				{
					if (j != 6 && j != 1)
					{
						if (j != 3)
						{
							if (j != 7)
							{
								if (j == 4)
								{
									g100 = true;
									break;
								}
							}
							else
							{
								if (tcore[j - 1] >= tc_set || tsk <= tsk_set)
								{
									g100 = false;
									break;
								}
								g100 = true;
								break;
							}
						}
						else
						{
							if (tcore[j - 1] >= tc_set || tsk > tsk_set)
							{
								g100 = false;
								break;
							}
							g100 = true;
							break;
						}
					}
					else
					{
						if (c[10] < 0.0 || (tcore[j - 1] < tc_set || tsk <= 33.85))
						{
							g100 = false;
							break;
						}
						g100 = true;
						break;
					}
				}
				if (c[8] < 0.0 || (tcore[j - 1] < tc_set || tsk > tsk_set+0.05))
				{
					g100 = false;
					break;
				}
			}
			if (g100 == false)
			{
				continue;
			}
			else
			{
				if ((j == 4 || vb < 91.0) && (j != 4 || vb >= 89.0))
				{
					// Maximum blood flow
					if (vb > 90.0)
					{
						vb = 90.0;
					}
					// water loss in g/m2/h
					double ws = swm * 3600.0 * 1000.0;
					if (ws > 2000.0)
					{
						ws = 2000.0;
					}
					double wd = ed / Lvap * 3600.0 * (-1000.0);
					double wr = Eres / Lvap * 3600.0 * (-1000.0);
					double wsum = ws + wr + wd;
//							return tcore[j - 1], tsk, tcl, esw;
					returnValue[tcore2_index] = tcore[j - 1];
					returnValue[tsk2_index] = tsk;
					returnValue[tcl2_index] = tcl;
					returnValue[esw2_index] = esw;
					return returnValue;
				}
			}
			// water loss
			// sweating
			double ws = swm * 3600.0 * 1000.0; 
			// diffusion = perspiration
			double wd = ed / Lvap * 3600.0 * (-1000.0); 
			// respiration latent
			double wr = Eres / Lvap * 3600.0 * (-1000.0); 
			double wsum = ws + wr + wd;
			if (j - 3 < 0)
			{
				index = 3;
			}
			else
			{
				index = j - 3;
			}
			returnValue[tcore2_index] = tcore[index];
			returnValue[tsk2_index] = tsk;
			returnValue[tcl2_index] = tcl;
			returnValue[esw2_index] = esw;
//					return tcore[index],tsk, tcl, esw;
			return returnValue;
		}
		
		return returnValue;
	}

	public double[] pet(double tc,double tsk,double tcl,double ta_init, double esw_real )
	{
		double[] returnValue = new double[6];
		double met_base,esw=0.0,xx=0.0;
		double enbal=0.0;
		
	    // Input variables of the PET reference situation:
		// clo
		double icl_ref= 0.9; 
		// W
		double M_activity_ref=80; 
		// m/s
		double v_air_ref=0.1; 
		 // hPa
		double vpa_ref=12;
		double icl=icl_ref;

		double tx = ta_init;
		double tbody=0.1*tsk+0.9*tc;
		double enbal2 = 0.0;
		double count1 = 0;

		// base metabolism
		if (sexStr.equals("male"))
		{
			met_base = 3.45 * Math.pow(mbody, 0.75) * (1.0 + 0.004 * (30.0 - age) + 0.01 * 
					(ht * 100.0 / Math.pow(mbody, 1.0 / 3.0) - 43.4));
		}
		else
		{
			met_base = 3.19 * Math.pow(mbody, 0.75) * (1.0 + 0.004 * (30.0 - age) + 0.018 * 
					(ht * 100.0 / Math.pow(mbody, 1.0 / 3.0) - 42.1));
		}
		// breathing flow rate
		double rtv_ref = (M_activity_ref + met_base) * 1.44 * Math.pow(10.0, -6.0);
		//sweating flow rate 
		double swm = 304.94 * (tbody - tbody_set) * Adu / 3600000.0; 
		// saturated vapour pressure at skin surface
		double vpts = 6.11 * Math.pow(10.0, 7.45 * tsk / (235.0 + tsk)); 
		if (tbody <= tbody_set)
		{
			swm = 0.0;
		}
		if (sexStr.equals("female"))
		{
			swm = swm*0.7;
		}
		double esweat = -swm * Lvap;
		esweat=esw_real;
		// standard environment
		double hc = 2.67 + 6.5 * Math.pow(v_air_ref, 0.67);
		hc = hc * Math.pow(p/po, 0.55);
		// radiation saldo
		double Aeffr = Adu * feff;
		double facl = (173.51 * icl - 2.36 - 100.76 *icl*icl + 19.28 * Math.pow(icl, 3.0)) / 100.0;
		if (facl > 1.0)
		{
			facl = 1.0;
		}
	    // Increase of the exchange area depending on the clothing level
		double fcl = 1 + (0.31 * icl);
		double Acl = Adu * facl + Adu * (fcl - 1.0);
		// Evaporation coefficient [W/(m^2*Pa)]
		double hm = 0.633 * hc / (p * cair); 
		// vapour transfer efficiency for reference clothing
		double fec = 1.0 / (1.0 + 0.92 * hc * 0.155*icl_ref); 
		// max latetn flux for the reference vapour pressure 12 hPa
		double emax = hm * (vpa_ref - vpts) * Adu * Lvap * fec ;
		double wetsk = esweat / emax;
		// skin wettedness
		if (wetsk > 1.0)
		{
			wetsk = 1.0;
		}
		double eswdif = esweat - emax;
		// diffusion
				double ediff = Lvap / (rdsk + rdcl) * Adu * (1.0 - wetsk) * (vpa_ref-vpts);
		// esw: sweating [W.m-2] from the actual environment : in depends only on the difference with the core set temperature
		if (eswdif <= 0.0)
		{
			esw = emax;
		}
		if (eswdif > 0.0)
		{
			esw = esweat;
		}
		if (esw > 0.0)
		{
			esw = 0.0;
		}

		while (count1 != 4)
		{
			double rbare = Aeffr * (1.0-facl)*emsk*sigm*(Math.pow(tx + 273.2, 4.0) - Math.pow(tsk + 273.2, 4.0));
			double rclo = feff * Acl * emcl * sigm*(Math.pow(tx + 273.2, 4.0) - Math.pow(tcl + 273.2, 4.0));
			double rsum = rbare + rclo; // Recalculation of the radiative losses
			// convection
			double cbare = hc * (tx - tsk) * Adu * (1.0 - facl);
			double cclo = hc * (tx - tcl) * Acl;
			// Recalculation of the convective losses
			double csum = cbare + cclo; 
			// breathing
			double texp = 0.47 * tx + 21.0;
			double Cres = cair * (tx - texp) * rtv_ref;
			double vpexp = 6.11 * Math.pow(10.0, 7.45 * texp / (235.0 + texp));
			double Eres = 0.623 * Lvap / p * (vpa_ref - vpexp) * rtv_ref;
			double qresp = (Cres + Eres);
			// ----------------------------------------
			// energy balance
			enbal = (M_activity_ref + met_base) + ediff + qresp + esw + csum + rsum;
			if (count1 == 0)
			{
				xx = 1.0;
			}
			if (count1 == 1)
			{
				xx = 0.1;
			}
			if (count1 == 2)
			{
				xx = 0.01;
			}
			if (count1 == 3)
			{
				xx = 0.001;
			}
			if (enbal > 0.0)
			{
				tx = tx - xx;
			}
			if (enbal < 0.0)
			{
				tx += xx;
			}
			if ((enbal > 0.0 || enbal2 <= 0.0) && (enbal < 0.0 || enbal2 >= 0.0))
			{
				enbal2 = enbal;
			}
			else
			{
				count1 = count1 + 1;
			}
		}
		returnValue[tsk_index]=tsk;
		returnValue[enbal_index]=enbal;
		returnValue[esw_index]=esw;
		returnValue[ediff_index]=ediff;
		returnValue[tx_index]=tx;
		returnValue[tcl_index]=tcl;
//				return tsk, enbal, esw, ediff, tx, tcl;
		return returnValue;
	}


	

}
