package Simpel;

import java.util.HashMap;
import java.util.TreeMap;

import com.leelory.suncalc.SunCalc4JavaUtils;

public class TestSimpelMethods 

{
	public static void main(String[] args)
	{
		TestSimpelMethods t = new TestSimpelMethods();
		t.testsimpel();
//		t.test();
//		t.testet();
//		t.testhourlyeto();

	}

	public void testsimpel()
	{
		
        // run Simpel for the timestep
		HashMap<Integer,Double> simpelMetInput = new HashMap<Integer,Double>();
//		String[] InputStr = new String[] {"20.1.2021.0","20","0","63.7493333333333","13.49","0.255833333333333","0","0"};
//		simpelMetInput.put(SimpelConstants.INPUT_P, metP0[zone]);
		simpelMetInput.put(SimpelConstants.INPUT_P, 0.0);//oops, this is precipitation, where P in TARGET is pressure
		simpelMetInput.put(SimpelConstants.INPUT_T14, 11.929993);
		simpelMetInput.put(SimpelConstants.INPUT_R14, 48.416237);		
		simpelMetInput.put(SimpelConstants.INPUT_K_DOWN, Double.NaN );
		
//	    HashMap<Integer,Double> dayMonth = getDayMonth(dte);
//	    int doy=(int) Math.round(303);
//	    int hour=(int) Math.round(dayMonth.get(SimpelConstants.INPUT_HOUR));
		
		simpelMetInput.put(SimpelConstants.INPUT_DOY, 303.);
		simpelMetInput.put(SimpelConstants.INPUT_MONTH, 11.);
//		simpelMetInput.put(SimpelConstants.INPUT_HOUR, dayMonth.get(SimpelConstants.INPUT_HOUR));
		
//		System.out.println(dayMonth.get(SimpelConstants.INPUT_HOUR));
//		if (dayMonth.get(SimpelConstants.INPUT_HOUR) == 13) //TODO, set from property file, what time, how much irrigation
//		{
//			simpelMetInput.put(SimpelConstants.INPUT_IRR, 4.0);
//		}
//		
//		//first calculate potential ETO
//		double lat=-37.5; //TODO config files
//		double lon=145; //TODO config files
//		double meridian = 0.0; //TODO config files
//		double elevation=93.0; //TODO config files
//		double windSpeedHeight = 2.; //TODO config files
//		POT pot = new POT();
		
//		double windSpeed=metWS0[zone];
//		double airTemp=metTa0[zone];
//		double radiation=metKd0[zone];
//		double dewPoint = pot.computeDewPoint(metRH0[zone], metTa0[zone]);
//		double dayOfYear=doy;
//		double sunangle = pot.getSunangle(lat,doy);
//		
//		 	double[] potReturnValues = pot.et_calc(radiation, airTemp, windSpeed, dewPoint, dayOfYear, hour, lat, lon, meridian, elevation, sunangle, windSpeedHeight);
//		 	double etoValue = potReturnValues[0];
//	       if (etoValue < 0)
//	       {
//	    	   etoValue = 0;
//	       }
		double etoValue = 0.2974444494209455;
			//for the first timestep, these are null values, will be set in following timesteps
			TreeMap<Integer,Double> simpelPreviousTimestepValues = null;
	   	SimpelModelTimestep simpel = new SimpelModelTimestep();	
	   	TreeMap<String,Double> Soil = (TreeMap<String, Double>) SimpelConstants.Soil.clone();
	   	Soil.put("Timestep",1.);
	   	Soil.put("Field Capacity %",20.);
	   	Soil.put("Permanent Wilting Point %",5.0);
		Soil.put("Start of Reduction %",12.);
		Soil.put("Root Depth",25.);
		Soil.put("Init-Value Soil %",20.);
		Soil.put("Land use",SimpelConstants.landuse_spruce+0.0);
		Soil.put("Minimum LAI",5.);
		Soil.put("Maximum LAI",5.);
		Soil.put("Vegetation Fraction",0.75);
		Soil.put("Layer Thickness",0.35);
		Soil.put("Drainage Coeff. b",3.7);
		Soil.put("Max. Drainage Rate",2.88);
		Soil.put("Cap. Litter",0.);
		Soil.put("Init-Value Litter",0.);
		Soil.put("Litter Reduction factor",3.);
		Soil.put("Direct runoff factor",46.5);				
		Soil.put("Glugla coeff.",100.);
		double[][] simpelReturnValues = simpel.SIMPLE_function(simpelMetInput, SimpelConstants.Landuse, SimpelConstants.LAI_model, 
				Soil, simpelPreviousTimestepValues, etoValue);  
		double simpelETA = simpelReturnValues[0][SimpelConstants.ETA_TOTAL];
		System.out.println(simpelETA);
		
		simpelPreviousTimestepValues = simpel.setPreviousValues(simpelReturnValues);
		double[][] simpelReturnValues2 = simpel.SIMPLE_function(simpelMetInput, SimpelConstants.Landuse, SimpelConstants.LAI_model, 
				Soil, simpelPreviousTimestepValues, etoValue);  
		
		
//		{1=0.0, 6=1.75, 8=0.0, 13=0.0, 22=32.88231420446636}
//		{1=46.0, 2=0.0, 3=25.776, 4=52.16, 6=992.003, 9=2.0, 10=1.0}
//		0.7085409635926422
		simpelPreviousTimestepValues.put(SimpelConstants.SNOW_WATER_EQUI,0.0);
		simpelPreviousTimestepValues.put(SimpelConstants.I_BAL,0.0);
		simpelPreviousTimestepValues.put(SimpelConstants.I_CAP,1.75);
		simpelPreviousTimestepValues.put(SimpelConstants.CONTENT,0.0);
		simpelPreviousTimestepValues.put(SimpelConstants.STORAGE,32.88231420446636);
		etoValue = 0.7085409635926422;
		
		simpelMetInput.put(SimpelConstants.INPUT_P, 0.0);
		simpelMetInput.put(SimpelConstants.INPUT_T14, 25.776);
		simpelMetInput.put(SimpelConstants.INPUT_R14, 52.16);		
		simpelMetInput.put(SimpelConstants.INPUT_K_DOWN, 992.003);		
		simpelMetInput.put(SimpelConstants.INPUT_DOY, 46.0);
		simpelMetInput.put(SimpelConstants.INPUT_MONTH, 2.0);
		
		double[][] simpelReturnValues3 = simpel.SIMPLE_function(simpelMetInput, SimpelConstants.Landuse, SimpelConstants.LAI_model, 
				Soil, simpelPreviousTimestepValues, etoValue);  

		
	}
	
	public void test()
	{
		
		SunCalc4JavaUtils s = new SunCalc4JavaUtils();
		int hour=16;
		int dayOfYear=31;
		double lat=38.5;
		double lon=121.5;
		System.out.println(s.getAzimuth(hour,dayOfYear,lat,lon) + " should be something like 15");
		
		ETo eto = new ETo();
		double esat = eto.esat(30.0);
		double ea= 50.0/ 100.0 * esat;
		System.out.println(ea);
		
		
		double swDown = 294.0;// w/m2, convert to MJ/m2 for 1 hour
		double mjm2 = eto.wm2ToMjm2(swDown, 1);
		System.out.println(mjm2);
		
		double wm2 = eto.mjm2ToWm2(mjm2,1);
		System.out.println(wm2);
		
		

		
	}
	
	public void testet()
	{
		POT p = new POT();
		
		double lat=-37.7306; 
		String lonStr="145.0145"; 
		double airTemp=11.929993; 
		double windSpeed=4.7719283; 
		int dayOfYearInt=303; 
		int hourInt=13; 
		double dewPoint=1.3845375532694912; 
		double elevation=93.0; 
		double meridian=0.0; 
		double radiation=493.85;
		
	    double dayOfYear=dayOfYearInt;
	    double hour=hourInt;
	    
	    double humidity=48.416237;
	    double temperature=11.929993;
	    dewPoint = p.computeDewPoint(humidity, temperature);
	    
	    double airTempDiff = dewPoint;
	    double lon = Double.parseDouble(lonStr);
	    
	    double sunangle = p.getSunangle(lat,dayOfYearInt);
	    double windSpeedHeight = 2.;
		
		 double[] returnValues = p.et_calc(radiation, airTemp, windSpeed, airTempDiff, dayOfYear, hour, lat, lon, meridian, elevation, sunangle, windSpeedHeight);
	      // 0.2974444494209455
		 System.out.println(returnValues[0]);
	}
	
//	public void testet()
//	{
//		POT p = new POT();
////		Date,R,Temp,U,Td
////		12/31/2014 15:00,143,11.9,4.6,-7.6
//		double radiation= 143;
//	    double airTemp=11.9;
//	    double windSpeed=4.6;
//	    double airTempDiff=-7.6;
//	    double dayOfYear=31.;
//	    double hour=16.;
//	    
//        double lat = 38.5;
//        double lon = 121.5;
//        double meridian = 120;
//        double elevation = 18.5;
//        double sunangle = 17.0;
//        double windSpeedHeight = 2.;
//        
//       double[] returnValues = p.et_calc(radiation, airTemp, windSpeed, airTempDiff, dayOfYear, hour, lat, lon, meridian, elevation, sunangle, windSpeedHeight);
//       double[] expectedValues = new double[] {0.22586906,0.36249248};
////       assertArrayEquals(expectedValues, returnValues, 0.001);
//
////       1/1/2014 4:00,0,0.2,0.5,-4.4
//		radiation=0;
//	    airTemp=0.2;
//	    windSpeed=0.5;
//	    airTempDiff=-4.4;
//	    dayOfYear=1.;
//	    hour=5.;
//	    
//	    returnValues = p.et_calc(radiation, airTemp, windSpeed, airTempDiff, dayOfYear, hour, lat, lon, meridian, elevation, sunangle, windSpeedHeight);
//	    expectedValues = new double[] {-0.00508976,-0.0005715};
////	    assertArrayEquals(expectedValues, returnValues, 0.001);
//       
//	    
////      1/5/2014 15:00,188,18.8,2.8,-10.1
//		radiation=188;
//	    airTemp=18.8;
//	    windSpeed=2.8;
//	    airTempDiff=-10.1;
//	    dayOfYear=5.;
//	    hour=16.;
//	    
//	    returnValues = p.et_calc(radiation, airTemp, windSpeed, airTempDiff, dayOfYear, hour, lat, lon, meridian, elevation, sunangle, windSpeedHeight);
//	    expectedValues = new double[] {0.23885275,0.37833384};
////	    assertArrayEquals(expectedValues, returnValues, 0.001);
//	    
////	    10/19/2014 10:00,606,22,0.9,15.5
//	    radiation=606;
//	    airTemp=22;
//	    windSpeed=0.9;
//	    airTempDiff=15.5;
//	    dayOfYear=19.;
//	    hour=11.;
//	    
//	    returnValues = p.et_calc(radiation, airTemp, windSpeed, airTempDiff, dayOfYear, hour, lat, lon, meridian, elevation, sunangle, windSpeedHeight);
//	    expectedValues = new double[] {0.37864732,0.39927874};
////	    assertArrayEquals(expectedValues, returnValues, 0.001);
//	    
////	    10/12/2014 12:00,708,30.1,6.5,-3.3
//	    radiation=708;
//	    airTemp=30.1;
//	    windSpeed=6.5;
//	    airTempDiff=-3.3;
//	    dayOfYear=12.;
//	    hour=13.;
//	    
//	    returnValues = p.et_calc(radiation, airTemp, windSpeed, airTempDiff, dayOfYear, hour, lat, lon, meridian, elevation, sunangle, windSpeedHeight);
//	    expectedValues = new double[] {0.81769737,1.18549866};
////	    assertArrayEquals(expectedValues, returnValues, 0.001);
//
//	}
	
	public void testhourlyeto()
	{

		
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
		boolean daytime = true;
		double etoValue = eto.eto_fao_hourly(freq, lat, Day, lon, TZ_lon, z_msl, e_a_hourly, R_s_hourly, 
				T_mean_hourly, z_u, U_z, alb, hour, daytime);
		System.out.println(etoValue + " should be " + 2.0260339333668225);
		
		
	}
}
