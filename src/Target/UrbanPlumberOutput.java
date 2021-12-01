package Target;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class UrbanPlumberOutput
{

	Common common = new Common();
		
	public static void main(String[] args)
	{
		

	}
	
//	# This file forms part of the Urban-PLUMBER benchmarking evaluation project for urban areas. Copyright 2020.
//	# example (partial) model output in local standard time
//	#   YYYY    DOY.00       DOY      HHMM      W/m2      W/m2      W/m2      W/m2      W/m2      W/m2      W/m2      W/m2      W/m2  
//	    Year   Dectime       Day      Time    NetRad      SWup      LWup      QHup      QEup    SWdown    LWdown       dQS     Qanth  
//	    2004    1.4167         1      1000  625.5300  120.9671  431.4429   66.4826  113.9582    862.81    315.13     0.000    11.529
	
	public void output(String outputDirectory,
			ArrayList<HashMap<Integer,Double>> mod_rslts_tmrt_utci,
			ArrayList<HashMap<Integer,Double>> mod_rslts,
			int i,
			int latX, int lonY, long simulationStartTimeLong, int timestep,
//			ArrayList<Object> met0
			
		 	double[] metTa0 ,
			double[] metKd0 ,
			double[] metWS0 ,
			double[] metRH0 ,
			double[] metLD0 ,
			double[] metP0 
			
//			int time_out, 
//			boolean first_write, 
//			double timeis, 
//			double Ldn, 
//			double Tcan, 
//			double Aplan, double Rnet_tot, double Kup, double Lup, double Qh_tot, double Qe_tot, double Kdn_grid, double Qg_tot, double yd, int year,
//			double Tsfc_R, double Tsfc_T, double Tsfc_N, double Tsfc_S, double Tsfc_E, double Tsfc_W ,
//			int numroof2, int numstreet2, int numNwall2, int numSwall2, int numEwall2, int numWwall2,
//			double Kdir, double Kdif
			)
	{
//    	double metTa0 = (double)met0.get(MetData.Ta);
//    	double metKd0 = (double)met0.get(MetData.Kd);
//    	double metWS0 = (double)met0.get(MetData.WS);
//    	double metRH0 = (double)met0.get(MetData.RH);
//    	double metLD0 = (double)met0.get(MetData.Ld);
//    	double metP0 = (double)met0.get(MetData.P);
    	
//    	double eair = metRH0 * common.esat(metTa0+273.15);
    	double specificHumidity = common.rhToSh(getAverage(metRH0)/100.0, getAverage(metTa0)+273.15);
    	
		double ForcingSWdown = getAverage(metKd0);  // this is forcing data (at forcing height)
		double ForcingLWdown = getAverage(metLD0);  // this is forcing data (at forcing height)
		double Tair = getAverage(metTa0)+273.15; // this is forcing data (at forcing height)
		double Qair = specificHumidity; // this is forcing data (at forcing height)
		double Wind = getAverage(metWS0); // this is forcing data (at forcing height)
		double PSurf = getAverage(metP0); // this is forcing data (at forcing height)
		
		
		
		int Year; // YYYY 
		double Dectime; //  DOY.00  
		long Day; //  DOY 
		String Time; // HHMM
		double NetRad;  // W/m2    
		double SWup;  // W/m2
		double LWup;  // W/m2    
		double QHup;  // W/m2    
		double QEup;  // W/m2    
		double SWdown;  // W/m2    
		double LWdown;  // W/m2    
		double dQS;  // W/m2    
		double Qanth;  // W/m2    
		
		double RoofSurfT; //Tsfc roof
		double RoadSurfT; //Tsfc road
		double WallSurfT; 
		double TairCanyon;
		double SoilTemp; //K

		int timeIdx = i;
		int minutes = (int)timestep / 60;
		long minutesDelta = minutes * timeIdx;
		long currentSimulationTime = simulationStartTimeLong + (minutesDelta*60L*1000L);
		
		Date simulationCurrentDate = new Date(currentSimulationTime);
	    Calendar calendar = Calendar.getInstance();
	    calendar.setTime(simulationCurrentDate);
	    
	    int day = calendar.get(Calendar.DAY_OF_MONTH);
	    String dayStr = common.padLeft(day+"", 2, '0');
	    int month = calendar.get(Calendar.MONTH) + 1;
	    String monthStr = common.padLeft(month+"", 2, '0');
	    int year = calendar.get(Calendar.YEAR);
	    int hour = calendar.get(Calendar.HOUR_OF_DAY);
	    String hourStr = common.padLeft(hour, 2, '0');
	    int minute = calendar.get(Calendar.MINUTE);
	    String minuteStr = common.padLeft(minute, 2, '0');
	    int dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);
	    
//	    String currentSimulationTimeStr = year + "-" + monthStr + "-" + dayStr + "_" +hourStr + "" +minuteStr;
		
		Year = year;
		double decHourMinute = (hour*60. + minute)/1440.0;
		Dectime = dayOfYear + decHourMinute;
		Day = dayOfYear;
		Time = hourStr + minuteStr;
		
//		String formattedTime;

		String linefeed = "\n";
		String tab = "\t";
		
		String urbanPlumberOutputFile = null;
		urbanPlumberOutputFile = "UrbanPlumber.out";
//		int decimalPoints = 5;		
//		formattedTime =  common.padLeft( time_out, 6, '0') ;
		
		int count = 0;
//		double tmrtTotal = 0.0;
//		double utciTotal = 0.0;
		double qeTotal = 0.0;
		double qhTotal = 0.0;
		double qgTotal = 0.0;
		double rnTotal = 0.0;
		double tacTotal = 0.0;
//		double ucanTotal = 0.0;
		double tsurfHorzTotal = 0.0;
		double tsurfCanTotal = 0.0;
		double tsurfWallTotal = 0.0;
		
		double kuTotal = 0.0;
		double luTotal = 0.0;
		double kdTotal = 0.0;
		double ldTotal = 0.0;
		double tmTotal = 0.0;

		
		for (int latIdx = 0; latIdx < latX; latIdx++)
		{
			for (int lonIdx = 0; lonIdx < lonY; lonIdx++)
			{
				HashMap<Integer,Double> mod_rslts_grid = mod_rslts.get(count);
				HashMap<Integer,Double> mod_rslts_tmrt_utci_grid = mod_rslts_tmrt_utci.get(count);
				
//				double tmrtValue = mod_rslts_tmrt_utci_grid.get(TargetModule.FOR_TAB_UTCI_tmrt_INDEX);
//				double utciValue = mod_rslts_tmrt_utci_grid.get(TargetModule.FOR_TAB_UTCI_utci_INDEX);				
				double qeValue = mod_rslts_tmrt_utci_grid.get(TargetModule.FOR_TAB_UTCI_Qe_INDEX);
				double qhValue = mod_rslts_tmrt_utci_grid.get(TargetModule.FOR_TAB_UTCI_Qh_INDEX);
				double qgValue = mod_rslts_tmrt_utci_grid.get(TargetModule.FOR_TAB_UTCI_Qg_INDEX);
				double rnValue = mod_rslts_tmrt_utci_grid.get(TargetModule.FOR_TAB_UTCI_Rn_INDEX);	
				
				double KuValue = mod_rslts_tmrt_utci_grid.get(TargetModule.FOR_TAB_UTCI_Ku_INDEX);
				double LuValue = mod_rslts_tmrt_utci_grid.get(TargetModule.FOR_TAB_UTCI_Lu_INDEX);
				double KdValue = mod_rslts_tmrt_utci_grid.get(TargetModule.FOR_TAB_UTCI_Kd_INDEX);
				double LdValue = mod_rslts_tmrt_utci_grid.get(TargetModule.FOR_TAB_UTCI_Ld_INDEX);
				
				double TmValue = mod_rslts_tmrt_utci_grid.get(TargetModule.FOR_TAB_UTCI_Tm_INDEX);
				
//					double petValue = mod_rslts_tmrt_utci_grid.get(TargetModule.FOR_TAB_UTCI_PET_INDEX);
				double tacValue = mod_rslts_grid.get(TargetModule.FOR_TAB_Tac_INDEX);				
				double ucanValue = mod_rslts_grid.get(TargetModule.FOR_TAB_Ucan_INDEX);
				double tsurfHorzValue = mod_rslts_grid.get(TargetModule.FOR_TAB_Tsurf_horz_INDEX);
				double tsurfCanValue = mod_rslts_grid.get(TargetModule.FOR_TAB_Tsurf_can_INDEX);
				double tsurfWallValue = mod_rslts_grid.get(TargetModule.FOR_TAB_Tsurf_wall_INDEX);					
//					double dteValue = mod_rslts_grid.get(TargetModule.FOR_TAB_dte_INDEX);
//				double httcUrbNewValue = mod_rslts_grid.get(TargetModule.FOR_TAB_httc_urb_new_INDEX);
//				double httcCanValue = mod_rslts_grid.get(TargetModule.FOR_TAB_httc_can_INDEX);
//				double tbRurValue = mod_rslts_grid.get(TargetModule.FOR_TAB_Tb_rur_INDEX);
//				double modUTaRefValue = mod_rslts_grid.get(TargetModule.FOR_TAB_mod_U_TaRef_INDEX);
//				double utbValue = mod_rslts_grid.get(TargetModule.FOR_TAB_UTb_INDEX);						
//				double fidValue = mod_rslts_grid.get(TargetModule.FOR_TAB_FID_INDEX);
				
//				tmrtTotal += tmrtValue;
//				utciTotal += utciValue;
				qeTotal += qeValue;
				qhTotal += qhValue;
				qgTotal += qgValue;
				rnTotal += rnValue;
				tacTotal += tacValue;
//				ucanTotal += ucanValue;
				tsurfHorzTotal += tsurfHorzValue;
				tsurfCanTotal += tsurfCanValue;
				tsurfWallTotal += tsurfWallValue;
				
				kdTotal += KdValue;
				ldTotal += LdValue;
				kuTotal += KuValue;
				luTotal += LuValue;
				
				tmTotal += TmValue;
				
				count++;
			}
		}
	
		
		
//		double tmrtAve = tmrtTotal/count ;
//		double utciAve = utciTotal/count ;
		double qeAve = qeTotal/count ;
		double qhAve = qhTotal/count ;
		double qgAve = qgTotal/count ;
		double rnAve = rnTotal/count ;
		double tacAve = tacTotal/count ;
//		double ucanAve = ucanTotal/count ;
		double tsurfHorzAve = tsurfHorzTotal/count ;
		double tsurfCanAve = tsurfCanTotal/count ;
		double tsurfWallAve = tsurfWallTotal/count ;
		
		double kdAve = kdTotal / count;
		double kuAve = kuTotal / count;
		double ldAve = ldTotal / count;
		double luAve = luTotal / count;
		
		double tmAve = tmTotal / count;
	
		NetRad = rnAve;
		QHup = qhAve;
		QEup = qeAve;
		Qanth= 0.0;
		TairCanyon = tacAve + 273.15;
		WallSurfT = tsurfWallAve + 273.15;
		RoofSurfT = tsurfCanAve + 273.15;
		RoadSurfT = tsurfHorzAve + 273.15;
		dQS = qgAve;
		
		
		SWup = kuAve;
		LWup = luAve;
		SWdown = kdAve;
		LWdown = ldAve;

		SoilTemp = tmAve + 273.15;
		
		

		if (i==0)
		{
			String header = "# This file forms part of the Urban-PLUMBER benchmarking evaluation project for urban areas. Copyright 2020." + linefeed +
							"# example (partial) model output in local standard time"+ linefeed +
							"#YYYY"+tab+"DOY.00"+tab+"DOY"+tab+"HHMM"+tab+"W/m2"+tab+"W/m2"+tab+"W/m2"+tab+"W/m2"+tab+"W/m2"+tab+"W/m2"+tab+"W/m2"+tab+"W/m2"+tab+"W/m2" 
							+tab+"K"
							+tab+"K"
							+tab+"K"
							+tab+"K"
							
							+tab + "W/m2"
							+tab + "W/m2"
							+tab+"K"
							+tab+"kg/kg"
							+tab+"Pa"
							+tab+"m/s"
							+tab+"K"
							
							+ linefeed +
							"Year"
							+ tab
							+ "Dectime"
							+ tab
							+ "Day"
							+ tab
							+ "Time"
							+ tab
							+ "NetRad"
							+ tab
							+ "SWup"
							+ tab
							+ "LWup"
							+ tab
							+ "QHup"
							+ tab
							+ "QEup"
							+ tab
							+ "SWdown"
							+ tab
							+ "LWdown"
							+ tab
							+ "dQS"
							+ tab
							+ "Qanth"
							
							+ tab
							+ "RoofSurfT"
							+ tab
							+ "RoadSurfT"
							+ tab
							+ "WallSurfT"
							+ tab
							+ "TairCanyon"
							
							+ tab
							+ "ForcingSWdown"
							+ tab
							+ "ForcingLWdown"
							+ tab
							+ "Tair"
							+ tab
							+ "Qair"
							+ tab
							+ "PSurf"
							+ tab
							+ "Wind"	
							+ tab
							+ "SoilTemp"
							
							;
			writeOutput(outputDirectory, urbanPlumberOutputFile, header, true);
			
			//also pad out file with no-data starting at 1993
//			Forcing files include the following time metadata, with values for the first site (Preston) shown:
//				• time_coverage_start: first date and time of forcing in UTC:
//				 (1993-01-01 00:00:00)
//				• time_coverage_end: last date and time of forcing in UTC:
//				 (2004-11-28 13:00:00)
//				• time_analysis_start: first date and time of analysis period in UTC: (2003-08-12 03:30:00)
//				• local_utc_offset_hours: local standard time offset from UTC:
//				 (10.0)
//				• timestep_interval_seconds: timestep interval in seconds:
//				 (1800)
//				• timestep_number_spinup: timestep number during spinup:
//				 (186007)
//				• timestep_interval_seconds: timestep number during analysis:
//				 (22772)

			// currentSimulationTime is when the modelling starts, pad out time starting with timeCoverageStartLong
			
			Calendar timeCoverageStart = Calendar.getInstance();
			timeCoverageStart.set(1993, 0, 1, 0, 0);
			long timeCoverageStartLong = timeCoverageStart.getTimeInMillis();
			
			//while (timeCoverageStartLong < currentSimulationTime)
			//{				
////			    int dayPad = timeCoverageStart.get(Calendar.DAY_OF_MONTH);
////			    String dayPadStr = common.padLeft(day+"", 2, '0');
////			    int monthPad = timeCoverageStart.get(Calendar.MONTH) + 1;
////			    String monthPadStr = common.padLeft(month+"", 2, '0');
			    //int yearPad = timeCoverageStart.get(Calendar.YEAR);
			    //int hourPad = timeCoverageStart.get(Calendar.HOUR_OF_DAY);
			    //String hourPadStr = common.padLeft(hourPad, 2, '0');
			    //int minutePad = timeCoverageStart.get(Calendar.MINUTE);
			    //String minutePadStr = common.padLeft(minutePad, 2, '0');
			    //int dayOfYearPad = timeCoverageStart.get(Calendar.DAY_OF_YEAR);
				
				//int YearPad = yearPad;
				//double decHourMinutePad = (hourPad*60. + minutePad)/1440.0;
				//double DectimePad = dayOfYearPad + decHourMinutePad;
				//int DayPad = dayOfYearPad;
				//String TimePad = hourPadStr + minutePadStr;
				
				//timeCoverageStartLong += 1000*60*30;
				//timeCoverageStart.setTimeInMillis(timeCoverageStartLong);

				//String outputLine = YearPad
						//+ tab
						//+ DectimePad
						//+ tab
						//+ DayPad
						//+ tab
						//+ TimePad
						//+ tab
						//+ "-9999."
						//+ tab
						//+ "-9999."
						//+ tab
						//+ "-9999."
						//+ tab
						//+ "-9999."
						//+ tab
						//+ "-9999."
						//+ tab
						//+ "-9999."
						//+ tab
						//+ "-9999."
						//+ tab
						//+ "-9999."
						//+ tab
						//+ "-9999."
						
						//+ tab
						//+ "-9999."
						//+ tab
						//+ "-9999."
						//+ tab
						//+ "-9999."
						//+ tab
						//+ "-9999."
						
						//+ tab
						//+ "-9999."
						//+ tab
						//+ "-9999."
						//+ tab
						//+ "-9999."
						//+ tab
						//+ "-9999."
						//+ tab
						//+ "-9999."
						//+ tab				
						//+ "-9999."	
						//+ tab				
						//+ "-9999."	
						//;
				//writeOutput(outputDirectory, urbanPlumberOutputFile, outputLine, false);
				
			//}
			
			
		}
		
//		boolean isLeapYear = Year.isLeap(year);
		
			

		
//		RoofSurfT = common.roundToDecimals(Tsfc_R / (1.0*numroof2), decimalPoints);  
//		RoadSurfT = common.roundToDecimals(Tsfc_T / (1.0*numstreet2), decimalPoints);  
//		WallSurfT =common.roundToDecimals(( (Tsfc_N / (1.0*numNwall2) ) 
//				 + (Tsfc_S / (1.0*numSwall2) ) 
//				 + (Tsfc_E / (1.0*numEwall2) ) 
//				 + (Tsfc_W / (1.0*numWwall2) ))
//				 	/ 4.0, decimalPoints);  
//		TairCanyon = common.roundToDecimals(Tcan, decimalPoints);  
		

		
//		double daysElapsed = timeis / 24.0;
//		System.out.println("daysElapsed=" + daysElapsed + " " + timeis);
//		// this will allow 2 year simulations to be run, but this won't work for more then 2 years
//		if (isLeapYear)
//		{
//			if (daysElapsed > 366)
//			{
//				year = year + 1;
//			}
//		}
//		else
//		{
//			if (daysElapsed > 365)
//			{
//				year = year + 1;
//			}
//		}
		
//		Year = year; 
//		Dectime = common.roundToDecimals(yd + daysElapsed , decimalPoints);  
//		Day = Math.round(Math.floor(Dectime)); 
//		double hourminute = Dectime - Day;
//		long hourminute24 = Math.round(24.0*hourminute);
//		long hour = hourminute24*100;
//		long hourminute48 = Math.round(48.0*hourminute);  // if it is 20, then 1000, if 21 then 1030
//		if (isEven(hourminute48))
//		{}
//		else
//		{
//			hour = hour + 30;
//		}
//		
//		Time = hour;				
//		NetRad = common.roundToDecimals(Rnet_tot / Aplan , decimalPoints);  
//		SWup = common.roundToDecimals(Kup , decimalPoints) ;  
//		LWup = common.roundToDecimals(Lup , decimalPoints);    
//		QHup = common.roundToDecimals(Qh_tot / Aplan , decimalPoints);   
//		QEup = common.roundToDecimals(Qe_tot / Aplan, decimalPoints);   
//		SWdown = common.roundToDecimals(Kdir + Kdif , decimalPoints); 
//		LWdown = common.roundToDecimals(Ldn , decimalPoints) ;  
//		dQS = common.roundToDecimals(Qg_tot / Aplan , decimalPoints);  
//		Qanth = 0;  //TARGET does not calculate this
//		

		
		String outputLine = Year
				+ tab
				+ common.roundToDecimals(Dectime, 5)
				+ tab
				+ Day
				+ tab
				+ Time
				+ tab
				+ common.roundToDecimals(NetRad, 5)
				+ tab
				+ common.roundToDecimals(SWup, 5)
				+ tab
				+ common.roundToDecimals(LWup, 5)
				+ tab
				+ common.roundToDecimals(QHup, 5)
				+ tab
				+ common.roundToDecimals(QEup, 5)
				+ tab
				+ common.roundToDecimals(SWdown, 5)
				+ tab
				+ common.roundToDecimals(LWdown, 5)
				+ tab
				+ common.roundToDecimals(dQS, 5)
				+ tab
				+ common.roundToDecimals(Qanth, 5)
				
				+ tab
				+ common.roundToDecimals(RoofSurfT, 5)
				+ tab
				+ common.roundToDecimals(RoadSurfT, 5)
				+ tab
				+ common.roundToDecimals(WallSurfT, 5)
				+ tab
				+ common.roundToDecimals(TairCanyon, 5)
				
				+ tab
				+ common.roundToDecimals(ForcingSWdown, 5)
				+ tab
				+ common.roundToDecimals(ForcingLWdown, 5)
				+ tab
				+ common.roundToDecimals(Tair, 5)
				+ tab
				+ common.roundToDecimals(Qair, 5)
				+ tab
				+ common.roundToDecimals(PSurf, 5)
				+ tab				
				+ common.roundToDecimals(Wind, 5)
				+ tab				
				+ common.roundToDecimals(SoilTemp, 5)
				;
		writeOutput(outputDirectory, urbanPlumberOutputFile, outputLine, false);


		

		
	}
	
	double getAverage(double[] array)
	{
		double total = 0.0;
		for (double item : array)
		{
			total += item;
		}
		return total / array.length;
	}
	
	boolean isEven(long num)
	{
		return ((num % 2) == 0 );
	}
	
	public void writeOutput(String outputDirectory, String file, String text, boolean deleteExisting)
	{
		String filename= outputDirectory + "/" + file;
		if (deleteExisting)
		{
			File fileToDelete = new File(filename);
			fileToDelete.delete();
		}
		common.appendFile(text, filename);
	}


    
	
}
