package Target;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class GISOutput
{

	Common common = new Common();
	String linefeed = "\n";
	String tab = "\t";
	String underscore = "_";
	
	public static void main(String[] args)
	{
		

	}
	
	
	
	public void output(String outputDirectory,
			ArrayList<HashMap<Integer,Double>> mod_rslts_tmrt_utci,
			ArrayList<HashMap<Integer,Double>> mod_rslts,
			int i,
			long simulationStartTimeLong, int timestep,
			ArrayList<double[]> latLontoLCMap
			)
	{
		int Year; // YYYY 
//		double Dectime; //  DOY.00  
		long Day; //  DOY 
		String Time; // HHMM
//		double NetRad;  // W/m2    
//		double SWup;  // W/m2
//		double LWup;  // W/m2    
//		double QHup;  // W/m2    
//		double QEup;  // W/m2    
//		double SWdown;  // W/m2    
//		double LWdown;  // W/m2    
//		double dQS;  // W/m2    
//		double Qanth;  // W/m2    
		
//		double RoofSurfT; //Tsfc roof
//		double RoadSurfT; //Tsfc road
//		double WallSurfT; 
//		double TairCanyon;
//		double SoilTemp; //K

		
		int timeIdx = i;
		int minutes = (int)timestep / 60;
		long minutesDelta = minutes * timeIdx;
		long currentSimulationTime = simulationStartTimeLong + (minutesDelta*60L*1000L);
		
		Date simulationCurrentDate = new Date(currentSimulationTime);
	    Calendar calendar = Calendar.getInstance();
	    calendar.setTime(simulationCurrentDate);
	    
//	    int day = calendar.get(Calendar.DAY_OF_MONTH);
//	    String dayStr = common.padLeft(day+"", 2, '0');
//	    int month = calendar.get(Calendar.MONTH) + 1;
//	    String monthStr = common.padLeft(month+"", 2, '0');
	    int year = calendar.get(Calendar.YEAR);
	    int hour = calendar.get(Calendar.HOUR_OF_DAY);
	    String hourStr = common.padLeft(hour, 2, '0');
	    int minute = calendar.get(Calendar.MINUTE);
	    String minuteStr = common.padLeft(minute, 2, '0');
	    int dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);
	    
//	    String currentSimulationTimeStr = year + "-" + monthStr + "-" + dayStr + "_" +hourStr + "" +minuteStr;
		
		Year = year;
//		double decHourMinute = (hour*60. + minute)/1440.0;
//		Dectime = dayOfYear + decHourMinute;
		Day = dayOfYear;
		Time = hourStr + minuteStr;

		String gisOutputFile = "GISOutput_" + Year + underscore 
				+ Day+ underscore 
				+ Time
				+ ".out";
		

		String header = "lat"
						+ tab
						+ "lon"
						+ tab
						+ "tmrtValue"
						+ tab
						+ "utciValue"
						+ tab
						+ "qeValue"
						+ tab
						+ "KuValue"
						+ tab
						+ "LuValue"
						+ tab
						+ "KdValue"
						+ tab
						+ "LdValue"
						+ tab
						+ "TmValue"
						+ tab
						+ "qhValue"
						+ tab
						+ "qgValue"
						+ tab
						+ "rnValue"
						+ tab
						+ "tacValue"
						+ tab
						+ "ucanValue"
						+ tab
						+ "tsurfHorzValue"
						+ tab
						+ "tsurfCanValue"
						+ tab
						+ "tsurfWallValue"	
						;
		writeOutput(outputDirectory, gisOutputFile, header, true);
		

		for (int count = 0;count<mod_rslts.size();count++)
		{
			double[] latLon = latLontoLCMap.get(count);
			double lat = latLon[0];
			double lon = latLon[1];

			HashMap<Integer,Double> mod_rslts_grid = mod_rslts.get(count);
			HashMap<Integer,Double> mod_rslts_tmrt_utci_grid = mod_rslts_tmrt_utci.get(count);
			
			double tmrtValue = mod_rslts_tmrt_utci_grid.get(TargetModule.FOR_TAB_UTCI_tmrt_INDEX);
			double utciValue = mod_rslts_tmrt_utci_grid.get(TargetModule.FOR_TAB_UTCI_utci_INDEX);				
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
			
			String tacValueStr = common.roundToDecimals(tacValue, 5)+"";
			String utciValueStr = common.roundToDecimals(utciValue, 5)+"";
			String tmrtValueStr = common.roundToDecimals(tmrtValue, 5)+"";
			
			if (tacValue == -999.)
			{
				tacValueStr = "NaN";
			}
			if (tmrtValue == -999.)
			{
				tmrtValueStr = "NaN";
			}
			if (utciValue == -999.)
			{
				utciValueStr = "NaN";
			}
			
			
			String outputLine = lat
					+ tab
					+ lon
					+ tab
					+ tmrtValueStr
					+ tab
					+ utciValueStr
					+ tab
					+ common.roundToDecimals(qeValue, 5)
					+ tab
					+ common.roundToDecimals(KuValue, 5)
					+ tab
					+ common.roundToDecimals(LuValue, 5)
					+ tab
					+ common.roundToDecimals(KdValue, 5)
					+ tab
					+ common.roundToDecimals(LdValue, 5)
					+ tab
					+ common.roundToDecimals(TmValue, 5)
					+ tab
					+ common.roundToDecimals(qhValue, 5)
					+ tab
					+ common.roundToDecimals(qgValue, 5)
					+ tab
					+ common.roundToDecimals(rnValue, 5)
					+ tab
					+ tacValueStr
					+ tab
					+ common.roundToDecimals(ucanValue, 5)
					+ tab
					+ common.roundToDecimals(tsurfHorzValue, 5)
					+ tab
					+ common.roundToDecimals(tsurfCanValue, 5)
					+ tab
					+ common.roundToDecimals(tsurfWallValue	, 5)
					;


			writeOutput(outputDirectory, gisOutputFile, outputLine, false);



	}
		

		
	
		
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
