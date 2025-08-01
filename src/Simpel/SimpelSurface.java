package Simpel;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.TreeMap;

import Target.Common;

public class SimpelSurface 
{
	Common common = new Common();
	SimpelModelTimestep simpel = new SimpelModelTimestep();		
	
	double lat; 
	double lon; 
	double meridian ; 
	double elevation; 
	double windSpeedHeight ; 
	POT pot = new POT();
	//for the first timestep, these are null values, will be set in following timesteps
	TreeMap<Integer,Double> simpelPreviousTimestepValues = null;
	
   	TreeMap<String,Double> Soil = (TreeMap<String, Double>) SimpelConstants.Soil.clone();

	public void initSurface(double lat, double lon, double meridian, double elevation, double windSpeedHeight,
			double timestep ,
			double fieldcapacity,
			double wiltingpoint ,
			double startofreduction,
			double rootdepth,
			double initvaluesoil,
			double landuse,
			double minlai,
			double maxlai,
			double vegetationfraction,
			double layerthickness,
			double drainagecoeff,
			double maxdrainagerate,
			double caplitter,
			double initvaluelitter,
			double litterreductionfactor,
			double directrunofffactor,
			double gluglacoeff)
	{
		this.lat = lat;
		this.lon = lon;
		this.meridian = meridian;
		this.elevation = elevation;
		this.windSpeedHeight = windSpeedHeight;
		
		Soil.put("Timestep",timestep);
		Soil.put("Field Capacity %",fieldcapacity);
		Soil.put("Permanent Wilting Point %",wiltingpoint);
		Soil.put("Start of Reduction %",startofreduction);
		Soil.put("Root Depth",rootdepth);
		Soil.put("Init-Value Soil %",initvaluesoil);
		Soil.put("Land use",landuse+0.0);
		Soil.put("Minimum LAI",minlai);
		Soil.put("Maximum LAI",maxlai);
		Soil.put("Vegetation Fraction",vegetationfraction);
		Soil.put("Layer Thickness",layerthickness);
		Soil.put("Drainage Coeff. b",drainagecoeff);
		Soil.put("Max. Drainage Rate",maxdrainagerate);
		Soil.put("Cap. Litter",caplitter);
		Soil.put("Init-Value Litter",initvaluelitter);
		Soil.put("Litter Reduction factor",litterreductionfactor);
		Soil.put("Direct runoff factor",directrunofffactor);		
		Soil.put("Glugla coeff.",gluglacoeff);	
		
	}

	public double runTimestep(double Ta, double RH, double Kd, double Ws, Date dte, double irrigationTime, double irrigationAmount, int i)
	{
		

        // run Simpel for the timestep
		HashMap<Integer,Double> simpelMetInput = new HashMap<Integer,Double>();
//		String[] InputStr = new String[] {"20.1.2021.0","20","0","63.7493333333333","13.49","0.255833333333333","0","0"};
//		simpelMetInput.put(SimpelConstants.INPUT_P, metP0[zone]);
		simpelMetInput.put(SimpelConstants.INPUT_P, 0.0);//oops, this is precipitation, where P in TARGET is pressure
		simpelMetInput.put(SimpelConstants.INPUT_T14, Ta);
		simpelMetInput.put(SimpelConstants.INPUT_R14, RH);		
		simpelMetInput.put(SimpelConstants.INPUT_K_DOWN, Kd);
		
	    HashMap<Integer,Double> dayMonth = getDayMonth(dte);
	    int doy=(int) Math.round(dayMonth.get(SimpelConstants.INPUT_DOY));
	    int hour=(int) Math.round(dayMonth.get(SimpelConstants.INPUT_HOUR));
		
		simpelMetInput.put(SimpelConstants.INPUT_DOY, dayMonth.get(SimpelConstants.INPUT_DOY));
		simpelMetInput.put(SimpelConstants.INPUT_MONTH, dayMonth.get(SimpelConstants.INPUT_MONTH));
		simpelMetInput.put(SimpelConstants.INPUT_HOUR, dayMonth.get(SimpelConstants.INPUT_HOUR));
		
		System.out.println(dayMonth.get(SimpelConstants.INPUT_HOUR));
		if (dayMonth.get(SimpelConstants.INPUT_HOUR) == irrigationTime) 
		{
			simpelMetInput.put(SimpelConstants.INPUT_IRR, irrigationAmount);
			System.out.println("@@@@@@@@@@@@@@@irrigation=" + irrigationAmount);
		}

		double windSpeed=Ws;
		double airTemp=Ta;
		double radiation=Kd;
		double dewPoint = pot.computeDewPoint(RH, Ta);
		double dayOfYear=doy;
		double sunangle = pot.getSunangle(lat,doy);
		
		double[] potReturnValues = pot.et_calc(radiation, airTemp, windSpeed, dewPoint, dayOfYear, hour, lat, lon, meridian, elevation, sunangle, windSpeedHeight);
		double etoValue = potReturnValues[0];
		if (etoValue < 0)
		{
			etoValue = 0;
	    }
		
		double[][] simpelReturnValues = simpel.SIMPLE_function(simpelMetInput, SimpelConstants.Landuse, SimpelConstants.LAI_model, 
				Soil, simpelPreviousTimestepValues, etoValue);    	
//		double[][] simpelReturnValues = simpel.SIMPLE_function(simpelMetInput, SimpelConstants.Landuse, SimpelConstants.LAI_model, 
//				SimpelConstants.Soil, simpelPreviousTimestepValues);    	
		simpelPreviousTimestepValues = simpel.setPreviousValues(simpelReturnValues);
		
		double water_balance = simpelReturnValues[0][SimpelConstants.WATER_BALANCE];
		double sum_prec = simpelReturnValues[0][SimpelConstants.SUM_PREC];
		double sum_etr = simpelReturnValues[0][SimpelConstants.SUM_ETR];
		double sum_runoff = simpelReturnValues[0][SimpelConstants.SUM_RUNOFF];
		double init_swe = simpelReturnValues[0][SimpelConstants.INIT_SWE];
		double init_stor = simpelReturnValues[0][SimpelConstants.INIT_STOR];
		double snow_water_equi = simpelReturnValues[0][SimpelConstants.SNOW_WATER_EQUI];   		  
		double i_bal = simpelReturnValues[0][SimpelConstants.I_BAL];   	
		double content = simpelReturnValues[0][SimpelConstants.CONTENT];   	
		double precipitation = simpelReturnValues[0][SimpelConstants.PRECIPITATION];
		double seepage = simpelReturnValues[0][SimpelConstants.SEEPAGE];
		double surface_runoff = simpelReturnValues[0][SimpelConstants.SURFACE_RUNOFF];
		double balance_soil = simpelReturnValues[0][SimpelConstants.BALANCE_SOIL];
		double et_balance = simpelReturnValues[0][SimpelConstants.ET_BALANCE];
		
		double simpelETA = simpelReturnValues[0][SimpelConstants.ETA_TOTAL];
		double storage = simpelReturnValues[0][SimpelConstants.STORAGE];
		double simpelQe = simpel.qeFromETA2(simpelETA);
//		String output = "ETA " + common.roundToDecimals(simpelETA,4 ) 
//		+ "\tQe " + common.roundToDecimals(simpelQe,4) 
//		+ "\tETO " + common.roundToDecimals(etoValue,4)
//		+ "\tETOQe " + common.roundToDecimals(simpel.qeFromETA2(etoValue),2)
//		+ "\tstorage " + common.roundToDecimals(storage,2)
//		+ "\tKdown " + metKd0[zone];
//		System.out.println(
//				output
//				
//				) ;
		 
		double i_litter = simpelReturnValues[0][SimpelConstants.I_LITTER];
		double i_leaf = simpelReturnValues[0][SimpelConstants.I_LEAF];
		double eta = simpelReturnValues[0][SimpelConstants.ETA];

		String output2 =  i 
				+ "\t" +	common.roundToDecimals(simpelETA,4 ) 
		+ "\t" + common.roundToDecimals(simpelQe,4) 
		+ "\t" + common.roundToDecimals(etoValue,4)
		+ "\t" + common.roundToDecimals(simpel.qeFromETA2(etoValue),2)
		+ "\t" + common.roundToDecimals(storage,2)
		+ "\t" + Kd
				+ "\t" +water_balance	
				+ "\t" +sum_prec
				+ "\t" +sum_etr
				+ "\t" +sum_runoff
				+ "\t" +init_swe
				+ "\t" +init_stor
				+ "\t" +i_bal
				+ "\t" +content
				+ "\t" + precipitation
				+ "\t" + seepage
				+ "\t" + surface_runoff
				+ "\t" + i_litter
				+ "\t" + i_leaf
				+ "\t" + eta
				+ "\t" + balance_soil
				+ "\t" + et_balance
				;
		System.out.println(output2);
		common.appendFile(output2,"/tmp/output.csv");
		//TODO 
//		simpelQe = Double.NaN;
//		simpelQe=0;
		
//		System.out.println("Qe="+simpelQe);
		
		// end Simpel	
//		System.exit(1);
     
		return simpelQe;
	}
	
    
	public HashMap<Integer,Double> getDayMonth(Date simulationCurrentDate)
	{
		
		////	int timeIdx = i;
		//	int minutes = (int)timestep / 60;
		//	long minutesDelta = minutes * timeIdx;
		//	long currentSimulationTime = simulationStartTimeLong + (minutesDelta*60L*1000L);
		//	
		//	Date simulationCurrentDate = new Date(currentSimulationTime);
		 Calendar calendar = Calendar.getInstance();
		 calendar.setTime(simulationCurrentDate);
		 
		 int day = calendar.get(Calendar.DAY_OF_MONTH);
		// String dayStr = common.padLeft(day+"", 2, '0');
		 int month = calendar.get(Calendar.MONTH) + 1;
		// String monthStr = common.padLeft(month+"", 2, '0');
		// int year = calendar.get(Calendar.YEAR);
		 int hour = calendar.get(Calendar.HOUR_OF_DAY);
		// String hourStr = common.padLeft(hour, 2, '0');
		 int minute = calendar.get(Calendar.MINUTE);
		// String minuteStr = common.padLeft(minute, 2, '0');
		 int dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);
		 	
		 HashMap<Integer,Double> dayMonth = new HashMap<Integer,Double>();
		 dayMonth.put(SimpelConstants.INPUT_DOY, dayOfYear*1.0);
		 dayMonth.put(SimpelConstants.INPUT_MONTH, month*1.0);
		 dayMonth.put(SimpelConstants.INPUT_HOUR, hour*1.0);
		 return dayMonth;
	 
	}

	
}
