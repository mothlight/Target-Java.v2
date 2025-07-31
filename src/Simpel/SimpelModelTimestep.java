package Simpel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

public class SimpelModelTimestep
{	
	  //TODO utilize kdown, ldown, wind speed, now available in Global_Input
	  //TODO, Use timestep values, Hourly value for timestep now configured in Soil_physics: Timestep;24;
	
    int krowEnd = 0;
    boolean hasKdown = true;
    boolean hasWindSpeed = false;
    boolean hasLdown = false;
    
    ETo eto = new ETo();
	double width1 = 1;
	double width2 = 1;
	int timestepHours = 1;
	
//	The latitude of the met station (dec deg) 
	double lat=-43.6;
//	The longitude of the met station (dec deg) (only needed if calculating ETo hourly)
	double lon=172;
//	The longitude of the center of the time zone (dec deg) (only needed if calculating ETo hourly).
	double TZ_lon=173;
//	Elevation of the met station above mean sea level (m) 
	double z_msl=500;
//	The height of the wind speed measurement (m). Default is 2 m.
	double z_u=2;
//	Wind speed at height z (m/s), set to NaN to calculate
	double U_z=Double.NaN;
//	Albedo. Should be 0.23 for the reference crop.
	double alb = 0.23;
//	Day of Year
//	int Day =1;		
//	Time frequency string of the input and output. The minimum frequency is hours (H) and the maximum is month (M).
	int freq=ETo.HOURLY;

	
	boolean round=true;
	int roundDigits = 3;
	
	


	public static void main(String[] args)
	{
		SimpelModelTimestep s = new SimpelModelTimestep();		
		s.runTimestep();
	}
	
	public void runTimestep()
	{
		HashMap<Integer,Double> metInput = new HashMap<Integer,Double>();

//		String[] InputStr = new String[] {"20.1.2021.0","20","0","63.7493333333333","13.49","0.255833333333333","0","0"};
		metInput.put(SimpelConstants.INPUT_DATE, null);
		metInput.put(SimpelConstants.INPUT_DOY, 20.);
		metInput.put(SimpelConstants.INPUT_P, 0.);
		metInput.put(SimpelConstants.INPUT_T14, 13.49);
		metInput.put(SimpelConstants.INPUT_R14, 63.7493333333333);		
		metInput.put(SimpelConstants.INPUT_K_DOWN, 300.);
		
		metInput.put(SimpelConstants.INPUT_MONTH, 1.);
		metInput.put(SimpelConstants.INPUT_HOUR, 14.);

		TreeMap<Integer,Double> previousValues = null;
		double[][] returnValues = SIMPLE_function(metInput, SimpelConstants.Landuse, SimpelConstants.LAI_model, SimpelConstants.Soil, previousValues);
		
		TreeMap<Integer,Double> previousValues2 = setPreviousValues(returnValues);
		
//		String[] InputStr2 = new String[] {"20.1.2021.1","20","0","59.315","14.3271666666667","0.443066666666667","0","0"};
		metInput.put(SimpelConstants.INPUT_DATE, null);
		metInput.put(SimpelConstants.INPUT_DOY, 20.);
		metInput.put(SimpelConstants.INPUT_P, 0.);
		metInput.put(SimpelConstants.INPUT_T14, 14.3271666666667);
		metInput.put(SimpelConstants.INPUT_R14, 59.315);
		metInput.put(SimpelConstants.INPUT_MONTH, 1.);
		metInput.put(SimpelConstants.INPUT_HOUR, 15.);
		
		metInput.put(SimpelConstants.INPUT_K_DOWN, 400.);
		
		double[][] returnValues2 = SIMPLE_function(metInput, SimpelConstants.Landuse, SimpelConstants.LAI_model, SimpelConstants.Soil, previousValues2);

	}
	
	public TreeMap<Integer,Double> setPreviousValues(double[][] bucket_model)
	{
		TreeMap<Integer,Double> previousValues = new TreeMap<Integer,Double>();
		
		previousValues.put(SimpelConstants.SNOW_WATER_EQUI,bucket_model[0][SimpelConstants.SNOW_WATER_EQUI]);
		previousValues.put(SimpelConstants.I_BAL,bucket_model[0][SimpelConstants.I_BAL]);
		previousValues.put(SimpelConstants.I_CAP,bucket_model[0][SimpelConstants.I_CAP]);
		previousValues.put(SimpelConstants.CONTENT,bucket_model[0][SimpelConstants.CONTENT]);
		previousValues.put(SimpelConstants.STORAGE,bucket_model[0][SimpelConstants.STORAGE]);
		
		return previousValues;
	}
	
	public void printResults(double[][] bucket_model)
	{
		int width = bucket_model[0].length;
		System.out.print(bucket_model[0][0] );
		for (int i=1;i<width;i++)
		{
			System.out.print("," + bucket_model[0][i] );
		}
		System.out.println();
		
	}
	
	public double[][] SIMPLE_function(HashMap<Integer,Double> metInput, ArrayList<String[]> landuse, 
			ArrayList<int[]> LAI_model, TreeMap<String,Double> soil,
			TreeMap<Integer,Double> previousValues, double etp)
	{		
		
		
		if (etp > 0.7)
		{
			System.out.println();
			System.out.println(previousValues.toString());
			System.out.println(metInput.toString());
			System.out.println(etp);
//			System.exit(1);
		}
		
		double t14_1 = metInput.get(SimpelConstants.INPUT_T14);
		double t14 = metInput.get(SimpelConstants.INPUT_T14);
		double rh14 = metInput.get(SimpelConstants.INPUT_R14); // $kf: added for internal ETP calculation
		int doy = (int) Math.round(metInput.get(SimpelConstants.INPUT_DOY));// Input[krow][INPUT_DOY]
//		double p = metInput.get(SimpelConstants.INPUT_P);// Input[krow][INPUT_P]
		int month = (int) Math.round(metInput.get(SimpelConstants.INPUT_MONTH));
//		int hour = (int) Math.round(metInput.get(SimpelConstants.INPUT_HOUR));
		double[][] bucket_model = new double[metInput.size()][SimpelConstants.numberOfModelOutputs];
		

		
		bucket_model[0][SimpelConstants.PRECIPITATION]=metInput.get(SimpelConstants.INPUT_P);
		double irrigationAmount = 0.;
		// if irrigation, add to precipitation
		if (metInput.containsKey(SimpelConstants.INPUT_IRR))
		{
			irrigationAmount = metInput.get(SimpelConstants.INPUT_IRR);
//			bucket_model[0][SimpelConstants.PRECIPITATION] += irrigationAmount;
		}	
		if (hasKdown)
		{
			bucket_model[0][SimpelConstants.K_DOWN]=metInput.get(SimpelConstants.INPUT_K_DOWN);
		}
		if (hasWindSpeed)
		{
			bucket_model[0][SimpelConstants.WIND_SPEED]=metInput.get(SimpelConstants.INPUT_WIND_SPEED);
		}
		if (hasLdown)
		{
			bucket_model[0][SimpelConstants.L_DOWN]=metInput.get(SimpelConstants.INPUT_L_DOWN);
		}
		

	
		double Field_Capacity_Percent=soil.get(SimpelConstants.FIELD_CAPACITY_PERCENT);//Soil[1][2]
		double Permanent_Wilting_Point=soil.get(SimpelConstants.PERMANENT_WILTING_POINT);//Soil[2][2]
		double Start_of_Reduction_Percent=soil.get(SimpelConstants.START_OF_REDUCTION_PERCENT);//Soil[3][2]
		double Root_Depth=soil.get(SimpelConstants.ROOT_DEPTH);//Soil[4][2]
		double Init_Value_Soil_Percent=soil.get(SimpelConstants.INIT_VALUE_SOIL_PERCENT);//Soil[5][2]
		double Field_Capacity= Field_Capacity_Percent * Root_Depth / 10.;//Double.parseDouble(Soil.get(FIELD_CAPACITY)[1]);//Soil[6][2]
		double Perm_Wilting_Point=Permanent_Wilting_Point * Root_Depth / 10.;//Double.parseDouble(Soil.get(PERM_WILTING_POINT)[1]);//Soil[7][2]
		double Start_of_Reduction= Start_of_Reduction_Percent * Root_Depth / 10.;///Double.parseDouble(Soil.get(START_OF_REDUCTION)[1]);//Soil[9][2]
		double Init_Value_Soil=Init_Value_Soil_Percent * Root_Depth / 10.;//Double.parseDouble(Soil.get(INIT_VALUE_SOIL)[1]);//Soil[10][2]
		double Depth_of_soil=Root_Depth*10.;//Double.parseDouble(Soil.get(DEPTH_OF_SOIL)[1]);//Soil[11][2]
		double Land_use=soil.get(SimpelConstants.LAND_USE);//Soil[12][2]
		double Minimum_LAI=soil.get(SimpelConstants.MINIMUM_LAI);//Soil[13][2]
		double Maximum_LAI=soil.get(SimpelConstants.MAXIMUM_LAI);//Soil[14][2]
		double vcover=soil.get(SimpelConstants.INTC_COVERED_FRACTION);
		double layer_thickness=soil.get(SimpelConstants.INTC_LAYER_THICKNESS);
		double intc_drainage_coeff_b=soil.get(SimpelConstants.INTC_DRAINAGE_EXP_B);
		double intc_drainage_max_d=soil.get(SimpelConstants.INTC_DRAINAGE_MAX);
		double Direct_runoff_factor=soil.get(SimpelConstants.DIRECT_RUNOFF_FACTOR);//Soil[15][2]			
		double Glugla_Coeff_c=soil.get(SimpelConstants.GLUGLA_C);//Soil[16][2]
		double Lambda=Glugla_Coeff_c/Depth_of_soil/Depth_of_soil;///Double.parseDouble(Soil.get(LAMBDA)[1]);//Soil[17][2]
		double Cap_Litter=soil.get(SimpelConstants.CAP_LITTER);//Soil[18][2]
		double Litter_Reduction_factor=soil.get(SimpelConstants.LITTER_REDUCTION_FACTOR);//Soil[20][2]

		// KN 5/7/23, added a timestep. Date values in Global_Input can be
		// 02.01.1995.00, 02.01.1995.12 
		// ETP_COEFF requires the month to be the second item in the date (split by '.')
		double timestep=soil.get(SimpelConstants.TIMESTEP);  
		
		int landuseIndex = (int) Math.round(Land_use); 

//		# SIMPLE_function
//		# original Excel spreadsheet developed by Georg Hörmann
//		# extended (snow, surface runoff) by Kristian Förster 2022
//		# translated to R by Zoe Bovermann 2023
//      # translated to Java by Kerry Nice 20 June 2023
		  		  
//		  # water balance check
//		  double sum_prec   = 0.;
//		  double sum_etr    = 0.;
//		  double sum_runoff = 0.;
		  double init_stor  = Init_Value_Soil; 
		  double init_swe   = 0.;
		  if(previousValues == null)
		  {}
		  else
		  {
			  init_stor = previousValues.get(SimpelConstants.STORAGE);
			  init_swe = previousValues.get(SimpelConstants.SNOW_WATER_EQUI);
		  }
		    
		  double[][] laiModel = new double[4][4];
		  for (int i=0;i<LAI_model.size();i++)
		  {
			  laiModel[i][SimpelConstants.LAI_IND] = LAI_model.get(i)[0];
			  laiModel[i][SimpelConstants.LAI_DOY] = LAI_model.get(i)[1];
			  laiModel[i][SimpelConstants.LAI_PHASE] = LAI_model.get(i)[2];
		  }
		  
//		  # update LAI model according to soil physics ($kf 2023-03-28) -> ignore land use table
//		  # update Litter according to land use table
		  laiModel[0][SimpelConstants.LAI_LAI]= soil.get(SimpelConstants.MINIMUM_LAI);
		  laiModel[1][SimpelConstants.LAI_LAI]= soil.get(SimpelConstants.MAXIMUM_LAI);
		  laiModel[2][SimpelConstants.LAI_LAI]= soil.get(SimpelConstants.MAXIMUM_LAI);
		  laiModel[3][SimpelConstants.LAI_LAI]= soil.get(SimpelConstants.MINIMUM_LAI);
//		  # update Litter according to land use table
		  double landuse_DayDegree = Double.parseDouble(landuse.get(SimpelConstants.LANDUSE_DAY_DEGREE_LINE)[landuseIndex]);
		  
		  double laimodel13 = laiModel[0][SimpelConstants.LAI_LAI]; 
		  double laimodel12 = laiModel[0][SimpelConstants.LAI_DOY]; 
		  
		  double laimodel23 = laiModel[1][SimpelConstants.LAI_LAI]; 
		  double laimodel22 = laiModel[1][SimpelConstants.LAI_DOY]; 
		  
		  double laimodel33 = laiModel[2][SimpelConstants.LAI_LAI]; 
		  double laimodel32 = laiModel[2][SimpelConstants.LAI_DOY]; 
		  
		  double laimodel43 = laiModel[3][SimpelConstants.LAI_LAI]; 
		  double laimodel42 = laiModel[3][SimpelConstants.LAI_DOY]; 

		  double ts = timestep * 3600; // timestep from soil physics is given in hours, ts is a conversion to seconds
		  double nt = ts / SimpelConstants.SECONDS_PER_DAY; // fractional time step
		  // In the model the drying is controlled by the ”litter reduction factor” which 
		  // specifies the maximum evaporation expressed as part of the water content. 
		  // Therefore, a factor 2 indicates that within each step of calculation a maximum 
		  // of half of the storage capacity can evaporate. (source: SIMPEL dcos)
		  // The input value refers to daily time step. Thus it's adjusted to arbitrary
		  // time steps through division by nt.
		  double Litter_Reduction_factor_dt = Litter_Reduction_factor / nt;		
		  
		  double intc_drainage_max = intc_drainage_max_d / 86400 * ts; // max. drainage per time step
		  
		  // define empty storage for first time step and correct for adjustments in C
		  double t_surplus = 0.;
		  double ci = 0.; // first time step: initialize interception capacity, assuming empty storage
		  
		  double intc_drainage;
		   
		  int krow=0;
//		  # calculate the rest
//		  for(int krow=0;krow<1;krow++)
//		  {			  
//			  double t14 = Double.parseDouble(Input.get(krow)[INPUT_T14]);
//			  double rh14 = Double.parseDouble(Input.get(krow)[INPUT_R14]); // $kf: added for internal ETP calculation
//			  double doy = Double.parseDouble(Input.get(krow)[INPUT_DOY]);// Input[krow][INPUT_DOY]
//			  double p = Double.parseDouble(Input.get(krow)[INPUT_P]);// Input[krow][INPUT_P]
//		    # col 3+4:  
//		    # first row
		    if(previousValues == null)
		    {    	
//		      # col 3: C snow water equi
		    	bucket_model[krow][SimpelConstants.SNOW_WATER_EQUI] = init_swe; //# col 3 // $kf set to init_swe for consistency with water balance
		      
//		      # col 4: D Snow melt + rain
		      double[] temp = new double[]{0, landuse_DayDegree*t14 * nt};
		      if(t14 >= 0)
		      {
		        bucket_model[krow][SimpelConstants.SNOW_MELT_RAIN] = Math.min(0,(max(temp))) + bucket_model[krow][SimpelConstants.PRECIPITATION];
		      }
		      else
		      {
		        bucket_model[krow][SimpelConstants.SNOW_MELT_RAIN] = Math.min(0,(max(temp))) + 0;
		      }
		      
//		      # col 3 (dependent on col 4)
		      bucket_model[krow][SimpelConstants.SNOW_WATER_EQUI] = bucket_model[krow][SimpelConstants.SNOW_WATER_EQUI] + bucket_model[krow][SimpelConstants.PRECIPITATION] 
		    		  - bucket_model[krow][SimpelConstants.SNOW_MELT_RAIN];
		    }
		    
		    
//		    # rest of the rows
		    if(previousValues != null) // $kf: applies to all rows greater than zero
		    {
//		    	double t14_1 = Double.parseDouble(Input.get(krow)[INPUT_T14]);
		    	
		    	double[] temp = new double[]{0, landuse_DayDegree*t14_1 * nt};
		    	if(t14_1 < 0)  // temperature below freezing
		    	{
//		        # col 4: Snow melt + rain 
		    		bucket_model[krow][SimpelConstants.SNOW_MELT_RAIN] = Math.min(max(temp), previousValues.get(SimpelConstants.SNOW_WATER_EQUI)
		    				+bucket_model[krow][SimpelConstants.PRECIPITATION]);
		        
//		        # col 3 (dependent on col 4)
		    		bucket_model[krow][SimpelConstants.SNOW_WATER_EQUI] = previousValues.get(SimpelConstants.SNOW_WATER_EQUI) 
		    				+ bucket_model[krow][SimpelConstants.PRECIPITATION] - bucket_model[krow][SimpelConstants.SNOW_MELT_RAIN]+0;
		    	}
		    	else // temperature above freezing
		    	{
		    		bucket_model[krow][SimpelConstants.SNOW_MELT_RAIN] = Math.min(max(temp),(previousValues.get(SimpelConstants.SNOW_WATER_EQUI))+0) 
		    				+ bucket_model[krow][SimpelConstants.PRECIPITATION];
		    		bucket_model[krow][SimpelConstants.SNOW_WATER_EQUI] = previousValues.get(SimpelConstants.SNOW_WATER_EQUI) + 0 
		    				- bucket_model[krow][SimpelConstants.SNOW_MELT_RAIN] +  bucket_model[krow][SimpelConstants.PRECIPITATION];
		    	}
		    }
//		    # col 5: E ETP Coeff.Landuse[17,which(colnames(Landuse)==Soil[12,2])]

		    bucket_model[krow][SimpelConstants.ETP_COEFF] = Double.parseDouble(landuse.get(month-1)[landuseIndex]); //TODO make sure the values reflect northern/southern hemisphere values
		    
//		    # col 6: F ETP Input (potential evaporation)
//		    if(readETfromFile) // if(numColumnsInput >= 6) // $kf
//		    {   
//		    	  //# check if input variable is given
////		          # if yes, use given ETP0
////		    	 double et0_1 = Double.parseDouble(Input.get(krow)[INPUT_ET0]);
//		    	 bucket_model[krow][ETP_INPUT] = et0_1;
//		    	 if(krow==2)
//		    	 {
//		    		 System.out.println("Reading external ETP from file...");
//		    	 }
//		    }
		    if (!Double.isNaN(etp))
		    {
		    	bucket_model[krow][SimpelConstants.ETP_INPUT] = etp;
		    }
		    else 
		    { 
		    	//# if not calculate ETP
		    	bucket_model[krow][SimpelConstants.ETP_INPUT] = Math.min(7, bucket_model[krow][SimpelConstants.ETP_COEFF]*6.11*
			    		  Math.pow(10,((7.5*t14)/(237.3+t14)))*(1.-(rh14/100.))) * nt; // $kf: relative humidity fixed
		    }
		    
//		    # col 7: G LAI
		    if(doy <= laimodel12)
		    {
		      bucket_model[krow][SimpelConstants.LAI] = laimodel13;
		    }
		    else if(doy <= laimodel22)
		    {
		      bucket_model[krow][SimpelConstants.LAI] = Minimum_LAI+(laimodel23-laimodel13)*((doy-laimodel12)/(laimodel22-laimodel12));
		    }
		    else if(doy <= laimodel32)
		    {
		      bucket_model[krow][SimpelConstants.LAI] = laimodel33;
		    }
		    else if(doy <= laimodel42)
		    {
		      bucket_model[krow][SimpelConstants.LAI] = Maximum_LAI+(laimodel43-laimodel33)*((doy-laimodel32)/(laimodel42-laimodel32));
		    }
		    else
		    {
		      bucket_model[krow][SimpelConstants.LAI] = laimodel43;
		    }

		    // NEW INTERCEPTION MODEL (which works with different time steps)
		    // Rutter, A.J., Kershaw, K.A., Robins, P.C., Morton, A.J., 1971. 
		    // A predictive model of rainfall interception in forests, 1. Derivation 
		    // of the model from observations in a plantation of Corsican pine. 
		    // Agr. Meteorol. 9, 367–384.

//		    # col 8: H I-Cap
		    bucket_model[krow][SimpelConstants.I_CAP] = layer_thickness*bucket_model[krow][SimpelConstants.LAI];

		    
		    // Maximum Drainage rate adjusted to capacity
	 	    if(intc_drainage_max > bucket_model[krow][SimpelConstants.I_CAP]) 
	 	    {
	 	    	intc_drainage_max = bucket_model[krow][SimpelConstants.I_CAP];
	 	    }
		    
	 	    // empty storage (t_surplus) for first time step and correct for adjustments in C, initialized above before the main loop
	 	    // first time step: initialize interception capacity (ci), assuming empty storage, initialized above before the main loop
		    if(previousValues != null)
		    {
		    	ci = previousValues.get(SimpelConstants.I_BAL); // intercepted water from previous time step
			// Adjust storage as a result of changing capacity
		    	if( previousValues.get(SimpelConstants.I_CAP) !=  bucket_model[krow][SimpelConstants.I_CAP]  && ci >  bucket_model[krow][SimpelConstants.I_CAP] )
		    	{
			        t_surplus = ci - bucket_model[krow][SimpelConstants.I_CAP];
        			ci = bucket_model[krow][SimpelConstants.I_CAP];
		        }
		    }
		    //double ntf = 0.25; // fraction of precipitation that always becomes throughfall, todo: make parameter adjustable
		    double direct_throughfall =  bucket_model[krow][SimpelConstants.SNOW_MELT_RAIN] *(1.-vcover);
		    double intc_in =  bucket_model[krow][SimpelConstants.SNOW_MELT_RAIN] - direct_throughfall;
		    double intc_stor_guess = intc_in+ci; // storage depth

//		    # col 9: I ETi: Evaporation from wetted leaf 
		    // bucket_model[krow][INT_ETI_LEAF] = Math.min(bucket_model[krow][I_CAP], bucket_model[krow][ETP_INPUT]);
		    if(intc_stor_guess > bucket_model[krow][SimpelConstants.I_CAP])
		    {
			    bucket_model[krow][SimpelConstants.INT_ETI_LEAF] = Math.min(bucket_model[krow][SimpelConstants.ETP_INPUT],intc_stor_guess); // real evaporation
		    }
		    else
		    {
		    	bucket_model[krow][SimpelConstants.INT_ETI_LEAF] = 
		    			Math.min(bucket_model[krow][SimpelConstants.ETP_INPUT]*intc_stor_guess/ bucket_model[krow][SimpelConstants.I_CAP], intc_stor_guess);
		    }
//		    # col 10: J I-Bal.: Temp calcalation
		    //bucket_model[krow][I_BAL] = bucket_model[krow][SNOW_MELT_RAIN] - bucket_model[krow][INT_ETI_LEAF];

		    if(bucket_model[krow][SimpelConstants.I_CAP] < intc_stor_guess-bucket_model[krow][SimpelConstants.INT_ETI_LEAF]) 
		    {
		    	intc_drainage = Math.max(intc_stor_guess-bucket_model[krow][SimpelConstants.INT_ETI_LEAF]-bucket_model[krow][SimpelConstants.I_CAP],intc_drainage_max);
		    }
		    else
		    {
		    	intc_drainage = Math.min(intc_drainage_max*Math.exp(intc_drainage_coeff_b*(intc_stor_guess-bucket_model[krow][SimpelConstants.INT_ETI_LEAF] 
		    			- bucket_model[krow][SimpelConstants.I_CAP])/bucket_model[krow][SimpelConstants.I_CAP]), intc_stor_guess-bucket_model[krow][SimpelConstants.INT_ETI_LEAF]);
		    }

		    // updated meaning of col 10 "I-BAL" is interception storage
		    bucket_model[krow][SimpelConstants.I_BAL] = intc_stor_guess-bucket_model[krow][SimpelConstants.INT_ETI_LEAF]-intc_drainage;
		    if(bucket_model[krow][SimpelConstants.I_BAL] <0)
		    {
		    	intc_drainage = Math.max(intc_stor_guess+ bucket_model[krow][SimpelConstants.I_BAL],0);
		    	bucket_model[krow][SimpelConstants.I_BAL] = 0.;
		    }
		    
//		    # col 11: K I-Prec.: Throughfall   
		    bucket_model[krow][SimpelConstants.I_PREC] = direct_throughfall + intc_drainage + t_surplus;
		    
//		    # col 12: L I-Rem.: Remaining ETa passed to subsequent model
		    // bucket_model[krow][I_REM] = -1*Math.min(bucket_model[krow][I_BAL],0)
		    // 		-bucket_model[krow][INT_ETI_LEAF]+bucket_model[krow][ETP_INPUT];
		    bucket_model[krow][SimpelConstants.I_REM] = bucket_model[krow][SimpelConstants.ETP_INPUT] - bucket_model[krow][SimpelConstants.INT_ETI_LEAF];	

//		    # col 13+14+15
//		    # first row
//		    # litter reduction is now adjusted to time step of input series
		    if(previousValues == null)
		    {
//		      # col 13: M ETi Litter
		      double[] temp = new double[]{Cap_Litter,(0+bucket_model[krow][SimpelConstants.I_PREC])/Litter_Reduction_factor_dt};
		      bucket_model[krow][SimpelConstants.I_ETI_LITTER] = Math.min(bucket_model[krow][SimpelConstants.I_REM],min(temp));
		      
//		      # col 14: N Bilanz (needed for col 13 & 15)
		      bucket_model[krow][SimpelConstants.BILANZ] = 0 + bucket_model[krow][SimpelConstants.I_PREC] - bucket_model[krow][SimpelConstants.I_ETI_LITTER];
		      
//		      # col 15: O Content (needed for col 13 & 14)
		      if(bucket_model[krow][SimpelConstants.BILANZ] > Cap_Litter)
		      {
		        bucket_model[krow][SimpelConstants.CONTENT] = Cap_Litter;
		      }
		      else 
		      {
		    	  bucket_model[krow][SimpelConstants.CONTENT] = Math.max(0,bucket_model[krow][SimpelConstants.BILANZ]);
		      }		      
		    }
		    else  // litter reduction, timesteps
		    {//# rest of the rows
//		      # col 13: M ETi Litter
		      double[] temp = new double[]{Cap_Litter, (previousValues.get(SimpelConstants.CONTENT)+bucket_model[krow][SimpelConstants.I_PREC])/Litter_Reduction_factor_dt};
		      bucket_model[krow][SimpelConstants.I_ETI_LITTER] = Math.min(bucket_model[krow][SimpelConstants.I_REM],min(temp));
		      
//		      # col 14: N Bilanz (needed for col 13 & 15)
		      bucket_model[krow][SimpelConstants.BILANZ] = previousValues.get(SimpelConstants.CONTENT)  + bucket_model[krow][SimpelConstants.I_PREC] 
		    		  - bucket_model[krow][SimpelConstants.I_ETI_LITTER];
		      
//		      # col 15: O Content (needed for col 13 & 14)
		      if(bucket_model[krow][SimpelConstants.BILANZ] > Cap_Litter)
		      {
		        bucket_model[krow][SimpelConstants.CONTENT] = Cap_Litter;
		      }
		      else
		      {
		    	  bucket_model[krow][SimpelConstants.CONTENT] = Math.max(0,bucket_model[krow][SimpelConstants.BILANZ]);
		      }
		    }		    
//		    # col 16: P S-REstn
		    bucket_model[krow][SimpelConstants.S_RESTN] = Math.max(0, (bucket_model[krow][SimpelConstants.BILANZ]-bucket_model[krow][SimpelConstants.CONTENT]));
		    		    
//		    # col 17-24:
//		    # first row
//		    # Groundwater recharge computation with Glugla approach now adjusted to time step length
		    if(previousValues == null)
		    {
//		      # col 17: Q Inf-Limit
		      bucket_model[krow][SimpelConstants.INF_LIMIT] = (Field_Capacity-Init_Value_Soil)*0.25; // *(1.-Direct_runoff_factor/100.); /* bug fix */
		      
//		      # col 18: R P-Inf
		      bucket_model[krow][SimpelConstants.P_LINF] = Math.min(bucket_model[krow][SimpelConstants.S_RESTN],bucket_model[krow][SimpelConstants.INF_LIMIT])
		    		  *(1.-Direct_runoff_factor/100.) * nt;
		      
			    //NEW KN, add irrigation to P_LINF
//		      bucket_model[krow][SimpelConstants.P_LINF] += irrigationAmount;
		      
//		      # col 19: S S-Rest
		      bucket_model[krow][SimpelConstants.REST_ETA] = -1*Math.min(0,bucket_model[krow][SimpelConstants.BILANZ])
		    		  +bucket_model[krow][SimpelConstants.I_REM]-bucket_model[krow][SimpelConstants.I_ETI_LITTER];
		      
//		      # col 20: T Balance soil
		      bucket_model[krow][SimpelConstants.BALANCE_SOIL] = Init_Value_Soil + bucket_model[krow][SimpelConstants.P_LINF];
		    
		      System.out.println("start of reduction " + bucket_model[krow][SimpelConstants.BALANCE_SOIL] + " " + Start_of_Reduction);
//		      # col 21: U ETa // actual evapotranspiration
		      if(bucket_model[krow][SimpelConstants.BALANCE_SOIL] > Start_of_Reduction)
		      {
		        bucket_model[krow][SimpelConstants.ETA] =  bucket_model[krow][SimpelConstants.REST_ETA];
		      }
		      else
		      {
		        bucket_model[krow][SimpelConstants.ETA] =  bucket_model[krow][SimpelConstants.REST_ETA]
		        		*(bucket_model[krow][SimpelConstants.BALANCE_SOIL]-Perm_Wilting_Point)/
		        		(Start_of_Reduction- Perm_Wilting_Point);
		      }		      
//		      # col 22: V ET-Balance
		      bucket_model[krow][SimpelConstants.ET_BALANCE] = bucket_model[krow][SimpelConstants.BALANCE_SOIL] - bucket_model[krow][SimpelConstants.ETA];
		      
//		      # col 23: W Seepage
		      if(bucket_model[krow][SimpelConstants.ET_BALANCE] <= Field_Capacity)
		      {
		        bucket_model[krow][SimpelConstants.SEEPAGE] =  Lambda*Math.pow( (bucket_model[krow][SimpelConstants.ET_BALANCE]-Perm_Wilting_Point),2) * nt;
		      }
		      else
		      {
		        bucket_model[krow][SimpelConstants.SEEPAGE] =  Lambda*Math.pow( (Field_Capacity-Perm_Wilting_Point),2) * nt;
		      }		      
//		      # col 24: X Storage Init.-Value
		      if(bucket_model[krow][SimpelConstants.ET_BALANCE] > Field_Capacity)
		      {
		        bucket_model[krow][SimpelConstants.STORAGE] =  Field_Capacity;
		      }
		      else
		      {
		        bucket_model[krow][SimpelConstants.STORAGE] = bucket_model[krow][SimpelConstants.ET_BALANCE] - bucket_model[krow][SimpelConstants.SEEPAGE];
		      }	
		    }
		    else  // Groundwater recharge computation timesteps
		    {
//		      # col 17: Q Inf-Limit //infiltration limit
		      bucket_model[krow][SimpelConstants.INF_LIMIT] = (Field_Capacity-previousValues.get(SimpelConstants.STORAGE))*0.25;
		      
//		      # col 18: R P-Inf
		      bucket_model[krow][SimpelConstants.P_LINF] = Math.min(bucket_model[krow][SimpelConstants.S_RESTN],bucket_model[krow][SimpelConstants.INF_LIMIT])
		    		  *(1.-Direct_runoff_factor/100.);
		      
			    //NEW KN, add irrigation to P_LINF
		      bucket_model[krow][SimpelConstants.P_LINF] += irrigationAmount;
		      
//		      # col 19: S S-Rest
		      bucket_model[krow][SimpelConstants.REST_ETA] = -1.*Math.min(0,bucket_model[krow][SimpelConstants.BILANZ])
		    		  	+bucket_model[krow][SimpelConstants.I_REM]-bucket_model[krow][SimpelConstants.I_ETI_LITTER];
		      
//		      # col 20: T Balance soil
		      bucket_model[krow][SimpelConstants.BALANCE_SOIL] = previousValues.get(SimpelConstants.STORAGE) + bucket_model[krow][SimpelConstants.P_LINF];
		      
//		      # col 21: U ETa
		      if(bucket_model[krow][SimpelConstants.BALANCE_SOIL] > Start_of_Reduction)
		      {
		        bucket_model[krow][SimpelConstants.ETA] =  bucket_model[krow][SimpelConstants.REST_ETA];
		      }
		      else
		      {
		        bucket_model[krow][SimpelConstants.ETA] =  bucket_model[krow][SimpelConstants.REST_ETA]
		        		*(bucket_model[krow][SimpelConstants.BALANCE_SOIL]-Perm_Wilting_Point)/
		        		(Start_of_Reduction- Perm_Wilting_Point);
		      }
		      
//		      # col 22: V ET-Balance
		      bucket_model[krow][SimpelConstants.ET_BALANCE] = bucket_model[krow][SimpelConstants.BALANCE_SOIL] - bucket_model[krow][SimpelConstants.ETA];
		      
//		      # col 23: W Seepage
		      if(bucket_model[krow][SimpelConstants.ET_BALANCE] <= Field_Capacity)
		      {
		        bucket_model[krow][SimpelConstants.SEEPAGE] =  Lambda*
		        		Math.pow((bucket_model[krow][SimpelConstants.ET_BALANCE]-Perm_Wilting_Point),2) * nt;
		      }
		      else
		      {
		        bucket_model[krow][SimpelConstants.SEEPAGE] =  Lambda*Math.pow((Field_Capacity-Perm_Wilting_Point),2) * nt;
		      }
		      
//		      # col 24: X Storage Init.-Value
		      if(bucket_model[krow][SimpelConstants.ET_BALANCE] > Field_Capacity)
		      {
		        bucket_model[krow][SimpelConstants.STORAGE] =  Field_Capacity;
		      }
		      else
		      {
		        bucket_model[krow][SimpelConstants.STORAGE] = bucket_model[krow][SimpelConstants.ET_BALANCE] - bucket_model[krow][SimpelConstants.SEEPAGE];
		      }
		    }
//		    # col 25: Y surface runoff
		    if(bucket_model[krow][SimpelConstants.ET_BALANCE] > Field_Capacity)
		    {
		      bucket_model[krow][SimpelConstants.SURFACE_RUNOFF] = bucket_model[krow][SimpelConstants.ET_BALANCE]-Field_Capacity
		    		  +bucket_model[krow][SimpelConstants.S_RESTN]-bucket_model[krow][SimpelConstants.P_LINF];		      
		    }
		    else
		    {
		        bucket_model[krow][SimpelConstants.SURFACE_RUNOFF] = bucket_model[krow][SimpelConstants.S_RESTN]-bucket_model[krow][SimpelConstants.P_LINF];		        
		    }
		    
//		    # col 26: Z Runofftotal
		    bucket_model[krow][SimpelConstants.RUNOFF_TOTAL] = bucket_model[krow][SimpelConstants.SURFACE_RUNOFF] + bucket_model[krow][SimpelConstants.SEEPAGE];
		    
//		    # col 27: AA I-Leaf
		    bucket_model[krow][SimpelConstants.I_LEAF] = bucket_model[krow][SimpelConstants.ETP_INPUT] -  bucket_model[krow][SimpelConstants.I_REM];
		    
//		    # col 28: AB I-Litter
		    bucket_model[krow][SimpelConstants.I_LITTER] = bucket_model[krow][SimpelConstants.I_REM] -  bucket_model[krow][SimpelConstants.REST_ETA];
		    
//		    # col 29: AC ETa Total
		    bucket_model[krow][SimpelConstants.ETA_TOTAL] = bucket_model[krow][SimpelConstants.I_LITTER] + bucket_model[krow][SimpelConstants.I_LEAF] 
		    		+ bucket_model[krow][SimpelConstants.ETA];
//		    System.out.println(  roundTo2Decimals(bucket_model[krow][ETA_TOTAL]) + " " + roundTo2Decimals(bucket_model[krow][ETA]) 
//		    		+ " " + roundTo2Decimals(bucket_model[krow][I_LITTER]) + " " + roundTo2Decimals(bucket_model[krow][I_LEAF]) + " " + roundTo2Decimals(bucket_model[krow][INF_LIMIT]) 
//		    				+ " " + roundTo2Decimals(bucket_model[krowEnd][STORAGE]) + " " + roundTo2Decimals(bucket_model[krowEnd][CONTENT]) + " " + Field_Capacity);

//		    sum_prec = sum_prec + bucket_model[0][SimpelConstants.PRECIPITATION];
//		    sum_etr  = sum_etr  + bucket_model[krow][SimpelConstants.ETA_TOTAL];
//		    sum_runoff = sum_runoff + bucket_model[krow][SimpelConstants.RUNOFF_TOTAL];
		    double sum_prec =  bucket_model[0][SimpelConstants.PRECIPITATION];
		    double sum_etr  =  bucket_model[krow][SimpelConstants.ETA_TOTAL];
		    double sum_runoff =  bucket_model[krow][SimpelConstants.RUNOFF_TOTAL];
		    double storage = bucket_model[krowEnd][SimpelConstants.STORAGE] ;
		    double swe = bucket_model[krowEnd][SimpelConstants.SNOW_WATER_EQUI];
		    double i_bal_prev = 0;
		    double i_bal = bucket_model[krowEnd][SimpelConstants.I_BAL] ;
		    double storage_prev = 0;
		    if (previousValues != null)
		    {
		    	 i_bal_prev = previousValues.get(SimpelConstants.I_BAL);
		    	 storage_prev = previousValues.get(SimpelConstants.STORAGE);
		    }
		    
		    
		    double content_timestep_end = bucket_model[krowEnd][SimpelConstants.CONTENT];
		    krowEnd = krow;
		    
//		  # water balance check
//		  double water_balance = sum_prec - sum_etr - sum_runoff + init_swe + init_stor - storage_timestep_end 
//				  - swe_timestep_end - i_bal_timestep_end
//						  - content_timestep_end;
		    
		  double water_balance = sum_prec + storage_prev - storage - sum_runoff + i_bal_prev - i_bal - sum_etr;
		  
		  bucket_model[0][SimpelConstants.WATER_BALANCE]= water_balance;
		  bucket_model[0][SimpelConstants.SUM_PREC]= sum_prec;
		  bucket_model[0][SimpelConstants.SUM_ETR]= sum_etr;
		  bucket_model[0][SimpelConstants.SUM_RUNOFF]= sum_runoff;
		  bucket_model[0][SimpelConstants.INIT_SWE]= init_swe;
		  bucket_model[0][SimpelConstants.INIT_STOR]= init_stor;
		  
		  System.out.println("water balance=" + water_balance);
		  System.out.println(sum_prec + "\t" + init_swe + "\t" + init_stor);
		  System.out.println(sum_etr + "\t" + sum_runoff + "\t" + storage + "\t" + swe + "\t" + i_bal + "\t" + content_timestep_end);
		
		  
//		  System.out.println("bucket eta="+  bucket_model[krow][SimpelConstants.ETA_TOTAL] + "   " + bucket_model[krow][SimpelConstants.ETP_INPUT]  
//				  + " " +  bucket_model[krow][SimpelConstants.I_LITTER]
//						  + " " +  bucket_model[krow][SimpelConstants.I_LEAF]
//								  + " " +  bucket_model[krow][SimpelConstants.ETA]
//						  );
		  
		  return bucket_model;
		}
	
	
		
	
	public double[][] SIMPLE_function(HashMap<Integer,Double> metInput, ArrayList<String[]> landuse, 
			ArrayList<int[]> LAI_model, TreeMap<String,Double> soil,
			TreeMap<Integer,Double> previousValues)
	{		
		
		double t14_1 = metInput.get(SimpelConstants.INPUT_T14);
		double t14 = metInput.get(SimpelConstants.INPUT_T14);
		double rh14 = metInput.get(SimpelConstants.INPUT_R14); // $kf: added for internal ETP calculation
		int doy = (int) Math.round(metInput.get(SimpelConstants.INPUT_DOY));// Input[krow][INPUT_DOY]
		double p = metInput.get(SimpelConstants.INPUT_P);// Input[krow][INPUT_P]
		int month = (int) Math.round(metInput.get(SimpelConstants.INPUT_MONTH));
		int hour = (int) Math.round(metInput.get(SimpelConstants.INPUT_HOUR));
		double[][] bucket_model = new double[metInput.size()][SimpelConstants.numberOfModelOutputs];
		
		bucket_model[0][SimpelConstants.PRECIPITATION]=metInput.get(SimpelConstants.INPUT_P);
		if (hasKdown)
		{
			bucket_model[0][SimpelConstants.K_DOWN]=metInput.get(SimpelConstants.INPUT_K_DOWN);
		}
		if (hasWindSpeed)
		{
			bucket_model[0][SimpelConstants.WIND_SPEED]=metInput.get(SimpelConstants.INPUT_WIND_SPEED);
		}
		if (hasLdown)
		{
			bucket_model[0][SimpelConstants.L_DOWN]=metInput.get(SimpelConstants.INPUT_L_DOWN);
		}
		
		// if irrigation, add to precipitation
		if (metInput.containsKey(SimpelConstants.INPUT_IRR))
		{
			double irrigationAmount = metInput.get(SimpelConstants.INPUT_IRR);
			p += irrigationAmount;
		}	
	
		double Field_Capacity_Percent=soil.get(SimpelConstants.FIELD_CAPACITY_PERCENT);//Soil[1][2]
		double Permanent_Wilting_Point=soil.get(SimpelConstants.PERMANENT_WILTING_POINT);//Soil[2][2]
		double Start_of_Reduction_Percent=soil.get(SimpelConstants.START_OF_REDUCTION_PERCENT);//Soil[3][2]
		double Root_Depth=soil.get(SimpelConstants.ROOT_DEPTH);//Soil[4][2]
		double Init_Value_Soil_Percent=soil.get(SimpelConstants.INIT_VALUE_SOIL_PERCENT);//Soil[5][2]
		double Field_Capacity= Field_Capacity_Percent * Root_Depth / 10.;//Double.parseDouble(Soil.get(FIELD_CAPACITY)[1]);//Soil[6][2]
		double Perm_Wilting_Point=Permanent_Wilting_Point * Root_Depth / 10.;//Double.parseDouble(Soil.get(PERM_WILTING_POINT)[1]);//Soil[7][2]
		double Start_of_Reduction= Start_of_Reduction_Percent * Root_Depth / 10.;///Double.parseDouble(Soil.get(START_OF_REDUCTION)[1]);//Soil[9][2]
		double Init_Value_Soil=Init_Value_Soil_Percent * Root_Depth / 10.;//Double.parseDouble(Soil.get(INIT_VALUE_SOIL)[1]);//Soil[10][2]
		double Depth_of_soil=Root_Depth*10.;//Double.parseDouble(Soil.get(DEPTH_OF_SOIL)[1]);//Soil[11][2]
		double Land_use=soil.get(SimpelConstants.LAND_USE);//Soil[12][2]
		double Minimum_LAI=soil.get(SimpelConstants.MINIMUM_LAI);//Soil[13][2]
		double Maximum_LAI=soil.get(SimpelConstants.MAXIMUM_LAI);//Soil[14][2]
		double vcover=soil.get(SimpelConstants.INTC_COVERED_FRACTION);
		double layer_thickness=soil.get(SimpelConstants.INTC_LAYER_THICKNESS);
		double intc_drainage_coeff_b=soil.get(SimpelConstants.INTC_DRAINAGE_EXP_B);
		double intc_drainage_max_d=soil.get(SimpelConstants.INTC_DRAINAGE_MAX);
		double Direct_runoff_factor=soil.get(SimpelConstants.DIRECT_RUNOFF_FACTOR);//Soil[15][2]			
		double Glugla_Coeff_c=soil.get(SimpelConstants.GLUGLA_C);//Soil[16][2]
		double Lambda=Glugla_Coeff_c/Depth_of_soil/Depth_of_soil;///Double.parseDouble(Soil.get(LAMBDA)[1]);//Soil[17][2]
		double Cap_Litter=soil.get(SimpelConstants.CAP_LITTER);//Soil[18][2]
		double Litter_Reduction_factor=soil.get(SimpelConstants.LITTER_REDUCTION_FACTOR);//Soil[20][2]

		// KN 5/7/23, added a timestep. Date values in Global_Input can be
		// 02.01.1995.00, 02.01.1995.12 
		// ETP_COEFF requires the month to be the second item in the date (split by '.')
		double timestep=soil.get(SimpelConstants.TIMESTEP);  
		
		int landuseIndex = (int) Math.round(Land_use); 

//		# SIMPLE_function
//		# original Excel spreadsheet developed by Georg Hörmann
//		# extended (snow, surface runoff) by Kristian Förster 2022
//		# translated to R by Zoe Bovermann 2023
//      # translated to Java by Kerry Nice 20 June 2023
		  		  
//		  # water balance check
		  double sum_prec   = 0.;
		  double sum_etr    = 0.;
		  double sum_runoff = 0.;
		  double init_stor  = Init_Value_Soil; 
		  double init_swe   = 0.;
		    
		  double[][] laiModel = new double[4][4];
		  for (int i=0;i<LAI_model.size();i++)
		  {
			  laiModel[i][SimpelConstants.LAI_IND] = LAI_model.get(i)[0];
			  laiModel[i][SimpelConstants.LAI_DOY] = LAI_model.get(i)[1];
			  laiModel[i][SimpelConstants.LAI_PHASE] = LAI_model.get(i)[2];
		  }
		  
//		  # update LAI model according to soil physics ($kf 2023-03-28) -> ignore land use table
//		  # update Litter according to land use table
		  laiModel[0][SimpelConstants.LAI_LAI]= soil.get(SimpelConstants.MINIMUM_LAI);
		  laiModel[1][SimpelConstants.LAI_LAI]= soil.get(SimpelConstants.MAXIMUM_LAI);
		  laiModel[2][SimpelConstants.LAI_LAI]= soil.get(SimpelConstants.MAXIMUM_LAI);
		  laiModel[3][SimpelConstants.LAI_LAI]= soil.get(SimpelConstants.MINIMUM_LAI);
//		  # update Litter according to land use table
		  double landuse_DayDegree = Double.parseDouble(landuse.get(SimpelConstants.LANDUSE_DAY_DEGREE_LINE)[landuseIndex]);
		  
		  double laimodel13 = laiModel[0][SimpelConstants.LAI_LAI]; 
		  double laimodel12 = laiModel[0][SimpelConstants.LAI_DOY]; 
		  
		  double laimodel23 = laiModel[1][SimpelConstants.LAI_LAI]; 
		  double laimodel22 = laiModel[1][SimpelConstants.LAI_DOY]; 
		  
		  double laimodel33 = laiModel[2][SimpelConstants.LAI_LAI]; 
		  double laimodel32 = laiModel[2][SimpelConstants.LAI_DOY]; 
		  
		  double laimodel43 = laiModel[3][SimpelConstants.LAI_LAI]; 
		  double laimodel42 = laiModel[3][SimpelConstants.LAI_DOY]; 

		  double ts = timestep * 3600; // timestep from soil physics is given in hours, ts is a conversion to seconds
		  double nt = ts / SimpelConstants.SECONDS_PER_DAY; // fractional time step
		  // In the model the drying is controlled by the ”litter reduction factor” which 
		  // specifies the maximum evaporation expressed as part of the water content. 
		  // Therefore, a factor 2 indicates that within each step of calculation a maximum 
		  // of half of the storage capacity can evaporate. (source: SIMPEL dcos)
		  // The input value refers to daily time step. Thus it's adjusted to arbitrary
		  // time steps through division by nt.
		  double Litter_Reduction_factor_dt = Litter_Reduction_factor / nt;		
		  
		  double intc_drainage_max = intc_drainage_max_d / 86400 * ts; // max. drainage per time step
		  
		  // define empty storage for first time step and correct for adjustments in C
		  double t_surplus = 0.;
		  double ci = 0.; // first time step: initialize interception capacity, assuming empty storage
		  
		  double intc_drainage;
		   
		  int krow=0;
//		  # calculate the rest
//		  for(int krow=0;krow<1;krow++)
//		  {			  
//			  double t14 = Double.parseDouble(Input.get(krow)[INPUT_T14]);
//			  double rh14 = Double.parseDouble(Input.get(krow)[INPUT_R14]); // $kf: added for internal ETP calculation
//			  double doy = Double.parseDouble(Input.get(krow)[INPUT_DOY]);// Input[krow][INPUT_DOY]
//			  double p = Double.parseDouble(Input.get(krow)[INPUT_P]);// Input[krow][INPUT_P]
//		    # col 3+4:  
//		    # first row
		    if(previousValues == null)
		    {    	
//		      # col 3: C snow water equi
		    	bucket_model[krow][SimpelConstants.SNOW_WATER_EQUI] = init_swe; //# col 3 // $kf set to init_swe for consistency with water balance
		      
//		      # col 4: D Snow melt + rain
		      double[] temp = new double[]{0, landuse_DayDegree*t14 * nt};
		      if(t14 >= 0)
		      {
		        bucket_model[krow][SimpelConstants.SNOW_MELT_RAIN] = Math.min(0,(max(temp))) + bucket_model[krow][SimpelConstants.PRECIPITATION];
		      }
		      else
		      {
		        bucket_model[krow][SimpelConstants.SNOW_MELT_RAIN] = Math.min(0,(max(temp))) + 0;
		      }
		      
//		      # col 3 (dependent on col 4)
		      bucket_model[krow][SimpelConstants.SNOW_WATER_EQUI] = bucket_model[krow][SimpelConstants.SNOW_WATER_EQUI] + bucket_model[krow][SimpelConstants.PRECIPITATION] - bucket_model[krow][SimpelConstants.SNOW_MELT_RAIN];
		    }
		    
		    
//		    # rest of the rows
		    if(previousValues != null) // $kf: applies to all rows greater than zero
		    {
//		    	double t14_1 = Double.parseDouble(Input.get(krow)[INPUT_T14]);
		    	
		    	double[] temp = new double[]{0, landuse_DayDegree*t14_1 * nt};
		    	if(t14_1 < 0)  // temperature below freezing
		    	{
//		        # col 4: Snow melt + rain 
		    		bucket_model[krow][SimpelConstants.SNOW_MELT_RAIN] = Math.min(max(temp), previousValues.get(SimpelConstants.SNOW_WATER_EQUI)+bucket_model[krow][SimpelConstants.PRECIPITATION]);
		        
//		        # col 3 (dependent on col 4)
		    		bucket_model[krow][SimpelConstants.SNOW_WATER_EQUI] = previousValues.get(SimpelConstants.SNOW_WATER_EQUI) + bucket_model[krow][SimpelConstants.PRECIPITATION] - bucket_model[krow][SimpelConstants.SNOW_MELT_RAIN]+0;
		    	}
		    	else // temperature above freezing
		    	{
		    		bucket_model[krow][SimpelConstants.SNOW_MELT_RAIN] = Math.min(max(temp),(previousValues.get(SimpelConstants.SNOW_WATER_EQUI))+0) + bucket_model[krow][SimpelConstants.PRECIPITATION];
		    		bucket_model[krow][SimpelConstants.SNOW_WATER_EQUI] = previousValues.get(SimpelConstants.SNOW_WATER_EQUI) + 0 - bucket_model[krow][SimpelConstants.SNOW_MELT_RAIN] +  bucket_model[krow][SimpelConstants.PRECIPITATION];
		    	}
		    }
//		    # col 5: E ETP Coeff.Landuse[17,which(colnames(Landuse)==Soil[12,2])]

		    bucket_model[krow][SimpelConstants.ETP_COEFF] = Double.parseDouble(landuse.get(month-1)[landuseIndex]); //TODO make sure the values reflect northern/southern hemisphere values
		    
////		    # col 6: F ETP Input (potential evaporation)
//		    if(readETfromFile) // if(numColumnsInput >= 6) // $kf
//		    {   
//		    	  //# check if input variable is given
////		          # if yes, use given ETP0
////		    	 double et0_1 = Double.parseDouble(Input.get(krow)[INPUT_ET0]);
//		    	 bucket_model[krow][ETP_INPUT] = et0_1;
//		    	 if(krow==2)
//		    	 {
//		    		 System.out.println("Reading external ETP from file...");
//		    	 }
//		    }
//		    else 
//		    { 
		    	//# if not calculate ETP
		    	bucket_model[krow][SimpelConstants.ETP_INPUT] = Math.min(7, bucket_model[krow][SimpelConstants.ETP_COEFF]*6.11*
			    		  Math.pow(10,((7.5*t14)/(237.3+t14)))*(1.-(rh14/100.))) * nt; // $kf: relative humidity fixed
//		    }
		    
//		    # col 7: G LAI
		    if(doy <= laimodel12)
		    {
		      bucket_model[krow][SimpelConstants.LAI] = laimodel13;
		    }
		    else if(doy <= laimodel22)
		    {
		      bucket_model[krow][SimpelConstants.LAI] = Minimum_LAI+(laimodel23-laimodel13)*((doy-laimodel12)/(laimodel22-laimodel12));
		    }
		    else if(doy <= laimodel32)
		    {
		      bucket_model[krow][SimpelConstants.LAI] = laimodel33;
		    }
		    else if(doy <= laimodel42)
		    {
		      bucket_model[krow][SimpelConstants.LAI] = Maximum_LAI+(laimodel43-laimodel33)*((doy-laimodel32)/(laimodel42-laimodel32));
		    }
		    else
		    {
		      bucket_model[krow][SimpelConstants.LAI] = laimodel43;
		    }

		    // NEW INTERCEPTION MODEL (which works with different time steps)
		    // Rutter, A.J., Kershaw, K.A., Robins, P.C., Morton, A.J., 1971. 
		    // A predictive model of rainfall interception in forests, 1. Derivation 
		    // of the model from observations in a plantation of Corsican pine. 
		    // Agr. Meteorol. 9, 367–384.

//		    # col 8: H I-Cap
		    bucket_model[krow][SimpelConstants.I_CAP] = layer_thickness*bucket_model[krow][SimpelConstants.LAI];

		    
		    // Maximum Drainage rate adjusted to capacity
	 	    if(intc_drainage_max > bucket_model[krow][SimpelConstants.I_CAP]) 
	 	    {
	 	    	intc_drainage_max = bucket_model[krow][SimpelConstants.I_CAP];
	 	    }
		    
	 	    // empty storage (t_surplus) for first time step and correct for adjustments in C, initialized above before the main loop
	 	    // first time step: initialize interception capacity (ci), assuming empty storage, initialized above before the main loop
		    if(previousValues != null)
		    {
		    	ci = previousValues.get(SimpelConstants.I_BAL); // intercepted water from previous time step
			// Adjust storage as a result of changing capacity
		    	if( previousValues.get(SimpelConstants.I_CAP) !=  bucket_model[krow][SimpelConstants.I_CAP]  && ci >  bucket_model[krow][SimpelConstants.I_CAP] )
		    	{
			        t_surplus = ci - bucket_model[krow][SimpelConstants.I_CAP];
        			ci = bucket_model[krow][SimpelConstants.I_CAP];
		        }
		    }
		    //double ntf = 0.25; // fraction of precipitation that always becomes throughfall, todo: make parameter adjustable
		    double direct_throughfall =  bucket_model[krow][SimpelConstants.SNOW_MELT_RAIN] *(1.-vcover);
		    double intc_in =  bucket_model[krow][SimpelConstants.SNOW_MELT_RAIN] - direct_throughfall;
		    double intc_stor_guess = intc_in+ci; // storage depth

//		    # col 9: I ETi: Evaporation from wetted leaf 
		    // bucket_model[krow][INT_ETI_LEAF] = Math.min(bucket_model[krow][I_CAP], bucket_model[krow][ETP_INPUT]);
		    if(intc_stor_guess > bucket_model[krow][SimpelConstants.I_CAP])
		    {
			    bucket_model[krow][SimpelConstants.INT_ETI_LEAF] = Math.min(bucket_model[krow][SimpelConstants.ETP_INPUT],intc_stor_guess); // real evaporation
		    }
		    else
		    {
		    	bucket_model[krow][SimpelConstants.INT_ETI_LEAF] = Math.min(bucket_model[krow][SimpelConstants.ETP_INPUT]*intc_stor_guess/ bucket_model[krow][SimpelConstants.I_CAP], intc_stor_guess);
		    }
//		    # col 10: J I-Bal.: Temp calcalation
		    //bucket_model[krow][I_BAL] = bucket_model[krow][SNOW_MELT_RAIN] - bucket_model[krow][INT_ETI_LEAF];

		    if(bucket_model[krow][SimpelConstants.I_CAP] < intc_stor_guess-bucket_model[krow][SimpelConstants.INT_ETI_LEAF]) 
		    {
		    	intc_drainage = Math.max(intc_stor_guess-bucket_model[krow][SimpelConstants.INT_ETI_LEAF]-bucket_model[krow][SimpelConstants.I_CAP],intc_drainage_max);
		    }
		    else
		    {
		    	intc_drainage = Math.min(intc_drainage_max*Math.exp(intc_drainage_coeff_b*(intc_stor_guess-bucket_model[krow][SimpelConstants.INT_ETI_LEAF] - bucket_model[krow][SimpelConstants.I_CAP])/bucket_model[krow][SimpelConstants.I_CAP]), intc_stor_guess-bucket_model[krow][SimpelConstants.INT_ETI_LEAF]);
		    }

		    // updated meaning of col 10 "I-BAL" is interception storage
		    bucket_model[krow][SimpelConstants.I_BAL] = intc_stor_guess-bucket_model[krow][SimpelConstants.INT_ETI_LEAF]-intc_drainage;
		    if(bucket_model[krow][SimpelConstants.I_BAL] <0)
		    {
		    	intc_drainage = Math.max(intc_stor_guess+ bucket_model[krow][SimpelConstants.I_BAL],0);
		    	bucket_model[krow][SimpelConstants.I_BAL] = 0.;
		    }
		    
//		    # col 11: K I-Prec.: Throughfall   
		    bucket_model[krow][SimpelConstants.I_PREC] = direct_throughfall + intc_drainage + t_surplus;
		    
//		    # col 12: L I-Rem.: Remaining ETa passed to subsequent model
		    // bucket_model[krow][I_REM] = -1*Math.min(bucket_model[krow][I_BAL],0)
		    // 		-bucket_model[krow][INT_ETI_LEAF]+bucket_model[krow][ETP_INPUT];
		    bucket_model[krow][SimpelConstants.I_REM] = bucket_model[krow][SimpelConstants.ETP_INPUT] - bucket_model[krow][SimpelConstants.INT_ETI_LEAF];	

//		    # col 13+14+15
//		    # first row
//		    # litter reduction is now adjusted to time step of input series
		    if(previousValues == null)
		    {
//		      # col 13: M ETi Litter
		      double[] temp = new double[]{Cap_Litter,(0+bucket_model[krow][SimpelConstants.I_PREC])/Litter_Reduction_factor_dt};
		      bucket_model[krow][SimpelConstants.I_ETI_LITTER] = Math.min(bucket_model[krow][SimpelConstants.I_REM],min(temp));
		      
//		      # col 14: N Bilanz (needed for col 13 & 15)
		      bucket_model[krow][SimpelConstants.BILANZ] = 0 + bucket_model[krow][SimpelConstants.I_PREC] - bucket_model[krow][SimpelConstants.I_ETI_LITTER];
		      
//		      # col 15: O Content (needed for col 13 & 14)
		      if(bucket_model[krow][SimpelConstants.BILANZ] > Cap_Litter)
		      {
		        bucket_model[krow][SimpelConstants.CONTENT] = Cap_Litter;
		      }
		      else 
		      {
		    	  bucket_model[krow][SimpelConstants.CONTENT] = Math.max(0,bucket_model[krow][SimpelConstants.BILANZ]);
		      }		      
		    }
		    else  // litter reduction, timesteps
		    {//# rest of the rows
//		      # col 13: M ETi Litter
		      double[] temp = new double[]{Cap_Litter, (previousValues.get(SimpelConstants.CONTENT)+bucket_model[krow][SimpelConstants.I_PREC])/Litter_Reduction_factor_dt};
		      bucket_model[krow][SimpelConstants.I_ETI_LITTER] = Math.min(bucket_model[krow][SimpelConstants.I_REM],min(temp));
		      
//		      # col 14: N Bilanz (needed for col 13 & 15)
		      bucket_model[krow][SimpelConstants.BILANZ] = previousValues.get(SimpelConstants.CONTENT)  + bucket_model[krow][SimpelConstants.I_PREC] - bucket_model[krow][SimpelConstants.I_ETI_LITTER];
		      
//		      # col 15: O Content (needed for col 13 & 14)
		      if(bucket_model[krow][SimpelConstants.BILANZ] > Cap_Litter)
		      {
		        bucket_model[krow][SimpelConstants.CONTENT] = Cap_Litter;
		      }
		      else
		      {
		    	  bucket_model[krow][SimpelConstants.CONTENT] = Math.max(0,bucket_model[krow][SimpelConstants.BILANZ]);
		      }
		    }		    
//		    # col 16: P S-REstn
		    bucket_model[krow][SimpelConstants.S_RESTN] = Math.max(0, (bucket_model[krow][SimpelConstants.BILANZ]-bucket_model[krow][SimpelConstants.CONTENT]));
		    
//		    # col 17-24:
//		    # first row
//		    # Groundwater recharge computation with Glugla approach now adjusted to time step length
		    if(previousValues == null)
		    {
//		      # col 17: Q Inf-Limit
		      bucket_model[krow][SimpelConstants.INF_LIMIT] = (Field_Capacity-Init_Value_Soil)*0.25; // *(1.-Direct_runoff_factor/100.); /* bug fix */
		      
//		      # col 18: R P-Inf
		      bucket_model[krow][SimpelConstants.P_LINF] = Math.min(bucket_model[krow][SimpelConstants.S_RESTN],bucket_model[krow][SimpelConstants.INF_LIMIT])*(1.-Direct_runoff_factor/100.) * nt;
		      
//		      # col 19: S S-Rest
		      bucket_model[krow][SimpelConstants.REST_ETA] = -1*Math.min(0,bucket_model[krow][SimpelConstants.BILANZ])+bucket_model[krow][SimpelConstants.I_REM]-bucket_model[krow][SimpelConstants.I_ETI_LITTER];
		      
//		      # col 20: T Balance soil
		      bucket_model[krow][SimpelConstants.BALANCE_SOIL] = Init_Value_Soil + bucket_model[krow][SimpelConstants.P_LINF];
		      
//		      # col 21: U ETa // actual evapotranspiration
		      if(bucket_model[krow][SimpelConstants.BALANCE_SOIL] > Start_of_Reduction)
		      {
		        bucket_model[krow][SimpelConstants.ETA] =  bucket_model[krow][SimpelConstants.REST_ETA];
		      }
		      else
		      {
		        bucket_model[krow][SimpelConstants.ETA] =  bucket_model[krow][SimpelConstants.REST_ETA]*(bucket_model[krow][SimpelConstants.BALANCE_SOIL]-Perm_Wilting_Point)/
		          (Start_of_Reduction- Perm_Wilting_Point);
		      }		      
//		      # col 22: V ET-Balance
		      bucket_model[krow][SimpelConstants.ET_BALANCE] = bucket_model[krow][SimpelConstants.BALANCE_SOIL] - bucket_model[krow][SimpelConstants.ETA];
		      
//		      # col 23: W Seepage
		      if(bucket_model[krow][SimpelConstants.ET_BALANCE] <= Field_Capacity)
		      {
		        bucket_model[krow][SimpelConstants.SEEPAGE] =  Lambda*Math.pow( (bucket_model[krow][SimpelConstants.ET_BALANCE]-Perm_Wilting_Point),2) * nt;
		      }
		      else
		      {
		        bucket_model[krow][SimpelConstants.SEEPAGE] =  Lambda*Math.pow( (Field_Capacity-Perm_Wilting_Point),2) * nt;
		      }		      
//		      # col 24: X Storage Init.-Value
		      if(bucket_model[krow][SimpelConstants.ET_BALANCE] > Field_Capacity)
		      {
		        bucket_model[krow][SimpelConstants.STORAGE] =  Field_Capacity;
		      }
		      else
		      {
		        bucket_model[krow][SimpelConstants.STORAGE] = bucket_model[krow][SimpelConstants.ET_BALANCE] - bucket_model[krow][SimpelConstants.SEEPAGE];
		      }		      
		    }
		    else  // Groundwater recharge computation timesteps
		    {
//		      # col 17: Q Inf-Limit //infiltration limit
		      bucket_model[krow][SimpelConstants.INF_LIMIT] = (Field_Capacity-previousValues.get(SimpelConstants.STORAGE))*0.25;
		      
//		      # col 18: R P-Inf
		      bucket_model[krow][SimpelConstants.P_LINF] = Math.min(bucket_model[krow][SimpelConstants.S_RESTN],bucket_model[krow][SimpelConstants.INF_LIMIT])*(1.-Direct_runoff_factor/100.);
		      
//		      # col 19: S S-Rest
		      bucket_model[krow][SimpelConstants.REST_ETA] = -1*Math.min(0,bucket_model[krow][SimpelConstants.BILANZ])+bucket_model[krow][SimpelConstants.I_REM]-bucket_model[krow][SimpelConstants.I_ETI_LITTER];
		      
//		      # col 20: T Balance soil
		      bucket_model[krow][SimpelConstants.BALANCE_SOIL] = previousValues.get(SimpelConstants.STORAGE) + bucket_model[krow][SimpelConstants.P_LINF];
		      
//		      # col 21: U ETa
		      if(bucket_model[krow][SimpelConstants.BALANCE_SOIL] > Start_of_Reduction)
		      {
		        bucket_model[krow][SimpelConstants.ETA] =  bucket_model[krow][SimpelConstants.REST_ETA];
		      }
		      else
		      {
		        bucket_model[krow][SimpelConstants.ETA] =  bucket_model[krow][SimpelConstants.REST_ETA]*(bucket_model[krow][SimpelConstants.BALANCE_SOIL]-Perm_Wilting_Point)/
		          (Start_of_Reduction- Perm_Wilting_Point);
		      }
		      
//		      # col 22: V ET-Balance
		      bucket_model[krow][SimpelConstants.ET_BALANCE] = bucket_model[krow][SimpelConstants.BALANCE_SOIL] - bucket_model[krow][SimpelConstants.ETA];
		      
//		      # col 23: W Seepage
		      if(bucket_model[krow][SimpelConstants.ET_BALANCE] <= Field_Capacity)
		      {
		        bucket_model[krow][SimpelConstants.SEEPAGE] =  Lambda*
		        		Math.pow((bucket_model[krow][SimpelConstants.ET_BALANCE]-Perm_Wilting_Point),2) * nt;
		      }
		      else
		      {
		        bucket_model[krow][SimpelConstants.SEEPAGE] =  Lambda*Math.pow((Field_Capacity-Perm_Wilting_Point),2) * nt;
		      }
		      
//		      # col 24: X Storage Init.-Value
		      if(bucket_model[krow][SimpelConstants.ET_BALANCE] > Field_Capacity)
		      {
		        bucket_model[krow][SimpelConstants.STORAGE] =  Field_Capacity;
		      }
		      else
		      {
		        bucket_model[krow][SimpelConstants.STORAGE] = bucket_model[krow][SimpelConstants.ET_BALANCE] - bucket_model[krow][SimpelConstants.SEEPAGE];
		      }
		    }
//		    # col 25: Y surface runoff
		    if(bucket_model[krow][SimpelConstants.ET_BALANCE] > Field_Capacity)
		    {
		      bucket_model[krow][SimpelConstants.SURFACE_RUNOFF] = bucket_model[krow][SimpelConstants.ET_BALANCE]-Field_Capacity+bucket_model[krow][SimpelConstants.S_RESTN]-bucket_model[krow][SimpelConstants.P_LINF];		      
		    }
		    else
		    {
		        bucket_model[krow][SimpelConstants.SURFACE_RUNOFF] = bucket_model[krow][SimpelConstants.S_RESTN]-bucket_model[krow][SimpelConstants.P_LINF];		        
		    }
		    
//		    # col 26: Z Runofftotal
		    bucket_model[krow][SimpelConstants.RUNOFF_TOTAL] = bucket_model[krow][SimpelConstants.SURFACE_RUNOFF] + bucket_model[krow][SimpelConstants.SEEPAGE];
		    
//		    # col 27: AA I-Leaf
		    bucket_model[krow][SimpelConstants.I_LEAF] = bucket_model[krow][SimpelConstants.ETP_INPUT] -  bucket_model[krow][SimpelConstants.I_REM];
		    
//		    # col 28: AB I-Litter
		    bucket_model[krow][SimpelConstants.I_LITTER] = bucket_model[krow][SimpelConstants.I_REM] -  bucket_model[krow][SimpelConstants.REST_ETA];
		    
//		    # col 29: AC ETa Total
		    bucket_model[krow][SimpelConstants.ETA_TOTAL] = bucket_model[krow][SimpelConstants.I_LITTER] + bucket_model[krow][SimpelConstants.I_LEAF] + bucket_model[krow][SimpelConstants.ETA];
//		    System.out.println(  roundTo2Decimals(bucket_model[krow][ETA_TOTAL]) + " " + roundTo2Decimals(bucket_model[krow][ETA]) 
//		    		+ " " + roundTo2Decimals(bucket_model[krow][I_LITTER]) + " " + roundTo2Decimals(bucket_model[krow][I_LEAF]) + " " + roundTo2Decimals(bucket_model[krow][INF_LIMIT]) 
//		    				+ " " + roundTo2Decimals(bucket_model[krowEnd][STORAGE]) + " " + roundTo2Decimals(bucket_model[krowEnd][CONTENT]) + " " + Field_Capacity);

		    sum_prec = sum_prec + p;
		    sum_etr  = sum_etr  + bucket_model[krow][SimpelConstants.ETA_TOTAL];
		    sum_runoff = sum_runoff + bucket_model[krow][SimpelConstants.RUNOFF_TOTAL];
		    krowEnd = krow;
		    
//		  # water balance check
		  double water_balance = sum_prec - sum_etr - sum_runoff + init_swe + init_stor - bucket_model[krowEnd][SimpelConstants.STORAGE] - bucket_model[krowEnd][SimpelConstants.SNOW_WATER_EQUI] - bucket_model[krowEnd][SimpelConstants.I_BAL] - bucket_model[krowEnd][SimpelConstants.CONTENT];
		  
		  return bucket_model;
		}
	
	public double qeFromETA(double eta)
	{
		double qe = eta * 2257. * 1000. / 60. /60.;
		return qe;
	}
	
	public double qeFromETA2(double eta)
	{
		double qe = 2.5E6 * eta / 3600.;
		return qe;
	}
	
	double max(double[] items)
	{
		return Math.max(items[0], items[1]);
	}
	double min(double[] items)
	{
		return Math.min(items[0], items[1]);
	}
	
	public double roundToDecimals(double d, int c) 
	{
		int temp=(int)((d*Math.pow(10,c)));
		return (((double)temp)/Math.pow(10,c));
	}
	public double roundTo2Decimals(double d) 
	{
		return roundToDecimals(d, 2); 
	}
	
	public double CalculateVaporPressure(double t_hmp, double rh_hmp)
	{
		rh_hmp = rh_hmp*0.01;

//		// 'Find the HMP45C vapor pressure, in kPa, using a sixth order polynomial (Lowe, 1976).
//		double e_sat = 0.1*(A_0+t_hmp*(A_1+t_hmp*(A_2+t_hmp*(A_3+t_hmp*(A_4+t_hmp*(A_5+t_hmp*A_6))))));

		double e_sat = CalculateVaporPressurekPa(t_hmp);

		double e = e_sat*rh_hmp;

		//hmp in this case just refers to the instrument taking the temperature (t_hmp) and humidity (rh_hmp) measurements

		return e;
	}
	
	public double CalculateVaporPressurekPa(double t_hmp)
	{
		double A_0 = 6.107800;
		double A_1 = 4.436519e-1;
		double A_2 = 1.428946e-2;
		double A_3 = 2.650648e-4;
		double A_4 = 3.031240e-6;
		double A_5 = 2.034081e-8;
		double A_6 = 6.136821e-11;

		// 'Find the HMP45C vapor pressure, in kPa, using a sixth order polynomial (Lowe, 1976).
		double e_sat = 0.1*(A_0+t_hmp*(A_1+t_hmp*(A_2+t_hmp*(A_3+t_hmp*(A_4+t_hmp*(A_5+t_hmp*A_6))))));

		return e_sat;
	}
}