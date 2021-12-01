package Target;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.TreeMap;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Common
{


	public static final int z_Topo = 1;
	public static final int T_Surface_K = 2;
	public static final int T_Surface_Diff = 3;
	public static final int T_Surface_Change = 4;
	public static final int Q_Surface = 5;
	public static final int uv_above_Surface = 6;
	public static final int Sensible_heat_flux = 7;
	public static final int Exchange_Coeff_Heat = 8;
	public static final int Latent_heat_flux = 9;
	public static final int Soil_heat_Flux = 10;
	public static final int Sw_Direct_Radiation = 11;
	public static final int Sw_Diffuse_Radiation = 12;
	public static final int Lambert_Factor = 13;
	public static final int Longwave_Radiation_Budget = 14;
	public static final int Longwave_Rad_from_vegetation = 15;
	public static final int Longwave_Rad_from_environment = 16;
	public static final int Water_Flux = 17;
	public static final int Sky_View_Faktor = 18;
	public static final int Building_Height = 19;
	public static final int Surface_Albedo = 20;
	public static final int Deposition_Speed = 21;
	public static final int Mass_Deposed = 22;

	public static int DEFAULT_ROUNDING_PRECISION = 2;

	public static String[] splitOnWhitespace(String line)
	{
		return line.split("\\s+");
	}

	public void writeFile(String text, String filename)
	{	
		FileOutputStream out; // declare a file output object
		PrintStream p; // declare a print stream object

		try
		{
			out = new FileOutputStream(filename);
			p = new PrintStream(out);
			p.println(text);
			p.close();
		} catch (Exception e)
		{
			System.err.println("Error writing to file");
		}

	}
	
	public String convertTimecodeTotimecodeaammjjhh(String timecode, String year, String dayOfYear, String yearmonth)
	{
		// convert 20043332300 to  1996100601
		// convert 20043332300 to  2004100601
		
		String month = yearmonth.substring(4,6);
		int day = getDayOfMonthFromDayOfYear(year, dayOfYear);	
		String dayStrPadded = padLeft(day, 2, '0');
		String hour = timecode.substring(7,9);
		String returnValue = year + month + dayStrPadded + hour;						
		return returnValue;		
	}
	
	public String convertTimecode2Toddmmyyyyhhmm(String timecode, String year, String dayOfYear, String yearmonth)
	{
		// convert 201112010320 to "28/02/2011 23:00"
		
		
		String month = yearmonth.substring(4,6);
		int day = getDayOfMonthFromDayOfYear(year, dayOfYear);	
		String dayStrPadded = padLeft(day, 2, '0');
		String hour = timecode.substring(8,10);
		if (hour.startsWith("0"))
		{
			hour = hour.substring(1, 2);
		}
		String minute = timecode.substring(10,12);
		String returnValue = day
				+ "/"
				+ month
				+ "/"
				+ year
				+ " "
				+ hour
				+ ":"
				+ minute;					
		return returnValue;		
	}
	
	public String convertObsTimeToddmmyyyyhhmm(String timecode)
	{
		// convert 2016-01-18 21:00:00 to "28/02/2011 23:00"
		
		String year = timecode.substring(0,4);
		String month = timecode.substring(5,7);
		String day = timecode.substring(8,10);
		String hour = timecode.substring(11,13);
		String minute = timecode.substring(14,16);
		
		//String month = yearmonth.substring(4,6);
		//int day = getDayOfMonthFromDayOfYear(year, dayOfYear);	
		//String dayStrPadded = padLeft(day, 2, '0');
		//String hour = timecode.substring(8,10);
		if (hour.startsWith("0"))
		{
			hour = hour.substring(1, 2);
		}
		//String minute = timecode.substring(10,12);
		String returnValue = day
				+ "/"
				+ month
				+ "/"
				+ year
				+ " "
				+ hour
				+ ":"
				+ minute;					
		return returnValue;		
	}
	
	public String convertTimecodeToddmmyyyyhhmm(String timecode, String year, String dayOfYear, String yearmonth)
	{
		// convert to "28/02/2011 23:00"
		
		
		String month = yearmonth.substring(4,6);
		int day = getDayOfMonthFromDayOfYear(year, dayOfYear);	
		String dayStrPadded = padLeft(day, 2, '0');
		String hour = timecode.substring(7,9);
		if (hour.startsWith("0"))
		{
			hour = hour.substring(1, 2);
		}
		String minute = timecode.substring(9,11);
		String returnValue = day
				+ "/"
				+ month
				+ "/"
				+ year
				+ " "
				+ hour
				+ ":"
				+ minute;					
		return returnValue;		
	}
	
	public String convertToKelvin(String tempC)
	{
		Double tempCDouble = new Double(tempC).doubleValue();
		Double tempKDouble =  roundTwoDecimals( tempCDouble + 273.15 ); 
		
		return tempKDouble.toString();
	}
	
	public String multiplyString(String number, int times)
	{
		Integer numberInt = new Integer(number).intValue();
		Integer newNumber = numberInt * times;
		return newNumber.toString();
		
		
	}
	
	public String shortenYearTo2Digits( int year)
	{		
		String shortedYearStr = new Integer(year).toString();
		if (shortedYearStr.length() == 4)
		{
			shortedYearStr = shortedYearStr.substring(2, 4);
		}		
		return shortedYearStr;		
	}
	
	public String increaseYearTo4Digits(String year)
	{
		if (year.length() == 4)
		{
			return year;
		}
		
		if (year.length() == 2)
		{
			Integer yearInt = new Integer(year).intValue();
			if (yearInt < 50)
			{
				year = "20" + year ;
			}
			else
			{
				year = "19" + year;
			}
			
		}
//		else if (year.length() == 1)
//		{
//			Integer yearInt = new Integer(year).intValue();
//			if (yearInt < 50)
//			{
//				year = "200" + yearInt ;
//			}
//			else
//			{
//				year = "19" + yearInt;
//			}
//		}		
		else
		{
			return year;
		}
		
		return year;
		
		
	}
	
//	public String getRunDesc(String runPrefix)
//	{
//		String desc="";
//		try
//		{
//			// Pr0012_DESC
//			String propStr = runPrefix + "_DESC";
//			
//			desc = Messages.getString(propStr);	
//		}
//		catch(Exception e)
//		{
//			
//		}		
//		return desc;		
//	}	
	
//	public String getHostnameTempDirPath()
//	{
//		String path = "";
//		String localHostname = getHostname();
//		if (localHostname.equals("d-ges-08034-c"))
//		{
//			path = Messages.getString("ProcessSUEWSTemp.WORK");
//		} 
//		else
//		{
//			path = Messages.getString("ProcessSUEWSTemp.HOME");
//		}		
//		return path;
//	}
	
//	public String getHostnameWorkDirPath()
//	{
//		String path = "";
//		String localHostname = getHostname();
//		if (localHostname.equals("d-ges-08034-c"))
//		{
//			path = Messages.getString("ProcessSUEWSRun.WORK");
//		} 
//		else
//		{
//			path = Messages.getString("ProcessSUEWSRun.HOME");
//		}		
//		return path;
//	}	
	
	
	public String getHostname()
	{
		String localHostname = "";
		try 
		{
		    InetAddress addr = InetAddress.getLocalHost();

		    // Get IP Address
		    byte[] ipAddr = addr.getAddress();

		    // Get hostname
		    localHostname = addr.getHostName();
		} 
		catch (UnknownHostException e) 
		{
		}
		return localHostname;

	}
	
	public int getYVWQuarterForMonth(int month)
	{
		int quarter = 0;
		
		switch (month) {
		  case 1:
			  quarter = 0;
		    break;
		  case 2:	
			 quarter = 0;
		    break;
		  case 3:		   
			  quarter = 0;
		    break;
		  case 4:		   
			  quarter = 1;
		    break;
		  case 5:		   
			  quarter = 1;
		    break;
		  case 6:		   
			  quarter = 1;
		    break;
		  case 7:		   
			  quarter = 2;
		    break;
		  case 8:		   
			  quarter = 2;
		    break;
		  case 9:		   
			  quarter = 2;
		    break;
		  case 10:		   
			  quarter = 3;
		    break;
		  case 11:		   
			  quarter = 3;
		    break;
		  case 12:		   
			  quarter = 3;
		    break;		    
		  
		  default: 
		  
		}
		
		return quarter;
	}
	
	public String getYVWKeyForDate(String year, String month, int day)
	{
		String key = day + "-" + month + "-" + year;
		return key;
	}
	
	public String getMonthForMonthInt(int month)
	{
		String monthStr = "";
		switch (month) {
		  case 1:
			  monthStr = "Jan";
		    break;
		  case 2:	
			  monthStr = "Feb";
		    break;
		  case 3:		   
			  monthStr = "Mar";
		    break;
		  case 4:		   
			  monthStr = "Apr";
		    break;
		  case 5:		   
			  monthStr = "May";
		    break;
		  case 6:		   
			  monthStr = "Jun";
		    break;
		  case 7:		   
			  monthStr = "Jul";
		    break;
		  case 8:		   
			  monthStr = "Aug";
		    break;		    
		  case 9:		   
			  monthStr = "Sep";
		    break;
		  case 10:		   
			  monthStr = "Oct";
		    break;
		  case 11:		   
			  monthStr = "Nov";
		    break;
		  case 12:		   
			  monthStr = "Dec";
		    break;		    
		    
		  default: 
		  
		}
		
		return monthStr;
	}
	
	public String getYVWKeyForDate(int year, int month, int day)
	{
		
		String monthStr = getMonthForMonthInt(month);
		String yearStr = new Integer(year).toString();
		if (yearStr.length() == 4)
		{
			yearStr = yearStr.substring(2);
		}
		
		String key = day + "-" + monthStr + "-" + yearStr;
		return key;
	}	
	
	public String getHourFromFraction(String fraction)
	{
		double fractionDouble = new Double(fraction).doubleValue();
		double hour = fractionDouble * 24;
		long roundHour = Math.round(hour);

//		long hourLong = (long) (hour % 1);
		
		return new Long(roundHour).toString();
		

	}
	
	public int getWeekOfYearFromDayAndMonth(String yearStr, String dayStr, String monthStr)
	{
		int month = new Integer(monthStr).intValue() - 1;
		int day = new Integer(dayStr).intValue();
		int year = new Integer(yearStr).intValue();	

	    return getWeekOfYearFromDayAndMonth(year, day, month);
	}	
	
	public int getWeekOfYearFromDayAndMonth(int year, int day, int month)
	{

	    Calendar calendar = Calendar.getInstance();
	    calendar.set(Calendar.MONTH, month);
	    calendar.set(Calendar.DAY_OF_MONTH, day);	    
	    //calendar.set(Calendar.DAY_OF_YEAR, dayOfYear);
	    calendar.set(Calendar.YEAR, year);	    
	    
	    int weekOfYear = calendar.get(Calendar.WEEK_OF_YEAR);

	    //System.out.println("Day of year " + dayOfYear + " = " + calendar.getTime());
	    return weekOfYear;
	}		
	
	public int getDayOfYearFromDayAndMonth(String yearStr, String dayStr, String monthStr)
	{
		int month = new Integer(monthStr).intValue() - 1;
		int day = new Integer(dayStr).intValue();
		int year = new Integer(yearStr).intValue();	

	    return getDayOfYearFromDayAndMonth(year, day, month);
	}
	
	// "1990-01-01"
	public String getYearMonthDayStrFromDate(Date date)
	{
	    Calendar calendar = Calendar.getInstance();
	    calendar.setTime(date);
	    
	    int day = calendar.get(Calendar.DAY_OF_MONTH);
	    String dayStr = padLeft(day+"", 2, '0');
	    int month = calendar.get(Calendar.MONTH) + 1 ;
	    String monthStr = padLeft(month+"", 2, '0');
	    int year = calendar.get(Calendar.YEAR)  ;
	    
	    return year + "-" + monthStr + "-" + dayStr;
	}
	
	public int getDayOfYearFromDayAndMonth(int year, int day, int month)
	{

	    Calendar calendar = Calendar.getInstance();
	    calendar.set(Calendar.MONTH, month);
	    calendar.set(Calendar.DAY_OF_MONTH, day);	    
	    //calendar.set(Calendar.DAY_OF_YEAR, dayOfYear);
	    calendar.set(Calendar.YEAR, year);	    
	    
	    int dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);

	    //System.out.println("Day of year " + dayOfYear + " = " + calendar.getTime());
	    return dayOfYear;
	}	

	public int getDayOfMonthFromDayOfYear(int year, int dayOfYear)
	{

	    Calendar calendar = Calendar.getInstance();
	    calendar.set(Calendar.DAY_OF_YEAR, dayOfYear);
	    calendar.set(Calendar.YEAR, year);
	    int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

	    //System.out.println("Day of year " + dayOfYear + " = " + calendar.getTime());
	    return dayOfMonth;
	}
	
	public int getDayOfMonthFromDayOfYear(int year, String dayOfYear)
	{

		int dayOfYearInt = new Integer(dayOfYear).intValue();
	    Calendar calendar = Calendar.getInstance();
	    calendar.set(Calendar.DAY_OF_YEAR, dayOfYearInt);
	    calendar.set(Calendar.YEAR, year);
	    int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

	    //System.out.println("Day of year " + dayOfYear + " = " + calendar.getTime());
	    return dayOfMonth;
	}
	
	public int getDayOfMonthFromDayOfYear(String yearStr, String dayOfYear)
	{
		int year = new Integer(yearStr).intValue();
		int dayOfYearInt = new Integer(dayOfYear).intValue();
	    Calendar calendar = Calendar.getInstance();
	    calendar.set(Calendar.DAY_OF_YEAR, dayOfYearInt);
	    calendar.set(Calendar.YEAR, year);
	    int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

	    //System.out.println("Day of year " + dayOfYear + " = " + calendar.getTime());
	    return dayOfMonth;
	}
	
	public int getMonthFromDayOfYear(int year, String dayOfYearStr)
	{
		
		Integer dayOfYear = new Integer(dayOfYearStr).intValue(); 

	    Calendar calendar = Calendar.getInstance();
	    calendar.set(Calendar.DAY_OF_YEAR, dayOfYear);
	    calendar.set(Calendar.YEAR, year);
	    int month = calendar.get(Calendar.MONTH) + 1;

	    //System.out.println("dayOfYear=" + dayOfYear + " month=" + month);
	    
	    //System.out.println("Day of year " + dayOfYear + " = " + calendar.getTime());
	    return month;
	}	
	
	public String getMonthStrFromDayOfYear(int year, String dayOfYearStr)
	{
		
		Integer dayOfYear = new Integer(dayOfYearStr).intValue(); 

	    Calendar calendar = Calendar.getInstance();
	    calendar.set(Calendar.DAY_OF_YEAR, dayOfYear);
	    calendar.set(Calendar.YEAR, year);
	    int month = calendar.get(Calendar.MONTH) + 1;
	    String monthStr = new Integer(month).toString();

	    //System.out.println("dayOfYear=" + dayOfYear + " month=" + month);
	    
	    //System.out.println("Day of year " + dayOfYear + " = " + calendar.getTime());
	    return monthStr;
	}	
	
	public String getMonthStrFromDayOfYear(String yearStr, String dayOfYearStr)
	{
		Integer year = new Integer(yearStr).intValue();
		Integer dayOfYear = new Integer(dayOfYearStr).intValue(); 

	    Calendar calendar = Calendar.getInstance();
	    calendar.set(Calendar.DAY_OF_YEAR, dayOfYear);
	    calendar.set(Calendar.YEAR, year);
	    int month = calendar.get(Calendar.MONTH) + 1;
	    String monthStr = new Integer(month).toString();

	    //System.out.println("dayOfYear=" + dayOfYear + " month=" + month);
	    
	    //System.out.println("Day of year " + dayOfYear + " = " + calendar.getTime());
	    return monthStr;
	}	

	public int getMonthFromDayOfYear(int year, int dayOfYear)
	{

	    Calendar calendar = Calendar.getInstance();
	    calendar.set(Calendar.DAY_OF_YEAR, dayOfYear);
	    calendar.set(Calendar.YEAR, year);
	    int month = calendar.get(Calendar.MONTH) + 1;

	    //System.out.println("dayOfYear=" + dayOfYear + " month=" + month);
	    
	    //System.out.println("Day of year " + dayOfYear + " = " + calendar.getTime());
	    return month;
	}
	
	//20040011500 to 2004
	public int getYearFromTimecode(String timecode)
	{
		int year = 0;
		
		String yearStr = timecode.substring(0, 4);
		try
		{
			year = new Integer(yearStr).intValue();	
		}
		catch (NumberFormatException e)
		{
			year = 0;
		}		
		
		return year;
	}
	
	//20040011500 to month
	public int getMonthFromTimecode(String timecode)
	{
		int year = getYearFromTimecode(timecode);
		int month = 0;
		int dayOfYear = 0;
		
		String dayOfYearStr = timecode.substring(4, 7);
		try
		{
			dayOfYear = new Integer(dayOfYearStr).intValue();	
		}
		catch (NumberFormatException e)
		{
			month = 0;
		}	
		month = getMonthFromDayOfYear(year, dayOfYear);
		
		return month;
	}	
	
	//20040011500 to day
	public int getDayOfMonthFromTimecode(String timecode)
	{
		int year = getYearFromTimecode(timecode);
		int dayOfMonth = 0;
		int dayOfYear = 0;
		
		String dayOfYearStr = timecode.substring(4, 7);
		try
		{
			dayOfYear = new Integer(dayOfYearStr).intValue();	
		}
		catch (NumberFormatException e)
		{
			dayOfMonth = 0;
		}	
		dayOfMonth = getDayOfMonthFromDayOfYear(year, dayOfYear);
		
		return dayOfMonth;
	}	
	
	//20040011500 to 15
	public int getHourFromTimecode(String timecode)
	{		
		int time;		
		String timeStr = timecode.substring(7, 9);
		try
		{
			time = new Integer(timeStr).intValue();	
		}
		catch (NumberFormatException e)
		{
			time = 0;
		}			
		return time;
	}
	
	public double roundTwoDecimals(double d) 
	{
        DecimalFormat twoDForm = new DecimalFormat("#.##");
        return Double.valueOf(twoDForm.format(d));
	}
	
	public long round0Decimal (double d)
	{
		return Math.round(d);
	}
	
	public double roundToDecimals(double d, int c) 
	{
		int temp=(int)((d*Math.pow(10,c)));
		return (((double)temp)/Math.pow(10,c));
	}
	
	public String roundToDecimals(String d, int c) 
	{
		double value = new Double(d).doubleValue();
		int temp=(int)((value*Math.pow(10,c)));
		return (((double)temp)/Math.pow(10,c)) + "";
	}
	
	public String removeLeadingCharacters(String str, char remove)
	{
		int beginIndex = 0;
		for (int i=0;i<str.length();i++)
		{			
			if (str.charAt(i) == remove)
			{
				beginIndex = i;
				//System.out.println("remove " + i + " from " + str);
			}
			else
			{
				//System.out.println("final string " + str.substring(beginIndex));
				break;
			}
		}
		
		return str.substring(beginIndex + 1);
	}

	public ArrayList<Double> energyBalance(String net, String QG, String QH, String QE, boolean fake)
	{
		ArrayList<Double> returnValues = new ArrayList<Double>();

		if (net == null || QG== null || QH== null || QE== null)
		{
			returnValues.add(0.0);
			returnValues.add(0.0);
			returnValues.add(0.0);
			return returnValues;
		}
		try
		{
			double netRadiation = new Double(net).doubleValue();
			double soilHeatFlux = new Double(QG).doubleValue();
			double sensibleHeatFlux = new Double(QH).doubleValue();
			double latentHeatFlux = new Double(QE).doubleValue();
		}
		catch(NumberFormatException e)
		{
			//e.printStackTrace();
			returnValues.add(0.0);
			returnValues.add(0.0);
			returnValues.add(0.0);
			return returnValues;

		}

		double netRadiation = new Double(net).doubleValue();
		double soilHeatFlux = new Double(QG).doubleValue();
		double availableEnergy = netRadiation - soilHeatFlux;
		double sensibleHeatFlux = new Double(QH).doubleValue();
		double latentHeatFlux = new Double(QE).doubleValue();
		double dailyEnergyBalance = netRadiation - soilHeatFlux - sensibleHeatFlux - latentHeatFlux;
		double Rn_G_H_LE = netRadiation - soilHeatFlux - sensibleHeatFlux - latentHeatFlux;

		returnValues.add(availableEnergy);
		returnValues.add(dailyEnergyBalance);
		returnValues.add(Rn_G_H_LE);

		return returnValues;

	}

	public ArrayList<Double> energyBalance(String kdown, String kup, String ldown, String lup, String QG, String QH, String QE, String temp)
	{
		ArrayList<Double> returnValues = new ArrayList<Double>();
		double surfaceAlbedo = 0.15;

		//first try and see if you have to calculate kup and lup
		if (kup != null && lup != null && kup.equals("?") && lup.equals("?") && temp != null)
		{
			try
			{
				double swIn = new Double(kdown).doubleValue();
				double tSurfaceK = new Double(temp).doubleValue() + 273;
				double lwIn = new Double(ldown).doubleValue();
			}
			catch(NumberFormatException e)
			{
				//e.printStackTrace();
				returnValues.add(0.0);
				returnValues.add(0.0);
				returnValues.add(0.0);
				return returnValues;

			}

			double lwIn = new Double(ldown).doubleValue();
			double swIn = new Double(kdown).doubleValue();
			double tSurfaceK = new Double(temp).doubleValue() + 273;

			double swOut = (surfaceAlbedo) * (swIn);
			double lwOut = 0.0000000567 * Math.pow(tSurfaceK, 4);


			double netRadiation = (swIn - swOut) + (lwIn - lwOut);
			double soilHeatFlux = new Double(QG).doubleValue();
			double availableEnergy = netRadiation - soilHeatFlux;
			double sensibleHeatFlux = new Double(QH).doubleValue();
			double latentHeatFlux = new Double(QE).doubleValue();
			double dailyEnergyBalance = netRadiation - soilHeatFlux - sensibleHeatFlux - latentHeatFlux;
			double Rn_G_H_LE = netRadiation - soilHeatFlux - sensibleHeatFlux - latentHeatFlux;

			returnValues.add(availableEnergy);
			returnValues.add(dailyEnergyBalance);
			returnValues.add(Rn_G_H_LE);

			return returnValues;
		}

		//ok, then calculate using kup,down and lup,down
		if (kdown == null || kup == null || ldown== null || lup== null || QG== null || QH== null || QE== null)
		{
			returnValues.add(0.0);
			returnValues.add(0.0);
			returnValues.add(0.0);
			return returnValues;
		}

		try
		{
			double swIn = new Double(kdown).doubleValue();
			double swOut = new Double(kup).doubleValue();
			double lwIn = new Double(ldown).doubleValue();
			double lwOut = new Double(lup).doubleValue();
			double soilHeatFlux = new Double(QG).doubleValue();
			double sensibleHeatFlux = new Double(QH).doubleValue();
			double latentHeatFlux = new Double(QE).doubleValue();
		}
		catch(NumberFormatException e)
		{
			//e.printStackTrace();
			returnValues.add(0.0);
			returnValues.add(0.0);
			returnValues.add(0.0);
			return returnValues;

		}
		double swIn = new Double(kdown).doubleValue();
		double swOut = new Double(kup).doubleValue();
		double lwIn = new Double(ldown).doubleValue();
		double lwOut = new Double(lup).doubleValue();
		double netRadiation = (swIn - swOut) + (lwIn - lwOut);
		double soilHeatFlux = new Double(QG).doubleValue();
		double availableEnergy = netRadiation - soilHeatFlux;
		double sensibleHeatFlux = new Double(QH).doubleValue();
		double latentHeatFlux = new Double(QE).doubleValue();
		double dailyEnergyBalance = netRadiation - soilHeatFlux - sensibleHeatFlux - latentHeatFlux;
		double Rn_G_H_LE = netRadiation - soilHeatFlux - sensibleHeatFlux - latentHeatFlux;

		returnValues.add(availableEnergy);
		returnValues.add(dailyEnergyBalance);
		returnValues.add(Rn_G_H_LE);

		return returnValues;


//		double swIn = swDirectRadiation + swDiffuseRadiation;
//		double swOut = (surfaceAlbedo) * (swIn);
//		//double lwIn = longwaveRadFromEnvironment;
//		double lwIn = rs.getDouble(REC_QLW_SKY);

//		double lwOut = 0.0000000567 * Math.pow(tSurfaceK, 4);
//		double netRadiation = (swIn - swOut) + (lwIn - lwOut);
//		double availableEnergy = netRadiation - soilHeatFlux;

//		double dailyEnergyBalance = netRadiation - soilHeatFlux - sensibleHeatFlux - latentHeatFlux;

		// Rn-G-H-LE

	}
	
	public String convertTimesToTimeDecimal(String time)
	{	
		//1330 to 13.5
		String hour = time.substring(0, 2);
		String minute = time.substring(2, 4);
		if (minute.equals("00"))
		{
			minute = "";
		}
		else
		{
			minute = ".5";
		}
		
		return hour + minute;
	}
	
	public String convertTimeToTimecode(String timecode)
	{
		// 20040011230 to 1.5416666667
		//String year = timecode.substring(0, 4);
		String day = timecode.substring(4, 7);
		String time = timecode.substring(7, 11);
		
		String convertTime = convertTimesToTimeDecimal(time);
		double convertTimeDouble = new Double(convertTime).doubleValue();
		double timecodeFraction = convertTimeDouble / 24;
		return day + timecodeFraction;
	}
	
	public String padLeft(int str, int size, char padChar)
	{
		//StringBuffer padded = new StringBuffer(str);
		String padded = new Integer(str).toString();
		while (padded.length() < size)
		{
			padded = padChar + padded;
			//padded.append(padChar);
		}
		//return padded.toString();
		return padded;
	}	

	public String padLeft(String str, int size, char padChar)
	{
		//StringBuffer padded = new StringBuffer(str);
		String padded = str;
		while (padded.length() < size)
		{
			padded = padChar + padded;
			//padded.append(padChar);
		}
		//return padded.toString();
		return padded;
	}
	
	public String padLeftTrimRight(String str, int size, char padChar)
	{
		if (str.length() >= size)
		{
			str = str.substring(0, str.length() -2);
		}
		//StringBuffer padded = new StringBuffer(str);
		String padded = str;
		while (padded.length() < size)
		{
			padded = padChar + padded;
			//padded.append(padChar);
		}
		//return padded.toString();
		return padded;
	}

	public String padRight(String str, int size, char padChar)
	{
		StringBuffer padded = new StringBuffer(str);
		//String padded = str;
		while (padded.length() < size)
		{
			//padded = padChar + padded;
			padded.append(padChar);
		}
		return padded.toString();
		//return padded;
	}


//	public static String padLeft(String s, int n)
//	{
//		return String.format("%0" + n,s);
//	    //return String.format("%1$#" + n + "s", s);
//	}

//	public static String padRight(String s, int n)
//	{
//	     return String.format("%1$-" + n + "s", s);
//	}
	
//	public void copyFileOnFilesystem(String sourceStr, String destinationStr)
//	{
//		// The source file name to be copied.
//        File source = new File("january.doc");
//        
//        // The target file name to which the source file will be copied.
//        File target = new File("january-backup.doc");
//        
//        // A temporary folder where we are gonna copy the source file to.
//        // Here we use the temporary folder of the OS, which can be obtained
//        // using java.io.tmpdir property.
//        File targetDir = new File(System.getProperty("java.io.tmpdir"));
//        
//        try
//        {
//            // Using FileUtils.copyFile() method to copy a file.
//            System.out.println("Copying " + source + " file to " + target);
//            FileUtils.copyFile(source, target);
//            
//            // To copy a file to a specified folder we can use the
//            // FileUtils.copyFileToDirectory() method.
//            System.out.println("Copying " + source + " file to " + targetDir);
//            FileUtils.copyFileToDirectory(source, targetDir);
//        } catch (IOException e)
//        {
//            // Errors will be reported here if any error occures during copying
//            // the file
//            e.printStackTrace();
//        }
//	}
	
	public void createSymlink(String source, String target)
	{
		Runtime rt=Runtime.getRuntime();
		Process result=null;
		String exe=new String("ln"+" -s "+source+" "+target);
		try
		{
			result=rt.exec(exe);
			
			result.waitFor();
			
			String s;
			BufferedReader stdInput = new BufferedReader(new 
		             InputStreamReader(result.getInputStream()));

		    BufferedReader stdError = new BufferedReader(new 
		             InputStreamReader(result.getErrorStream()));

		        // read the output from the command
		    //System.out.println("Here is the standard output of the command:\n");
//		    while ((s = stdInput.readLine()) != null) 
//		    {
//		            System.out.println(s);
//		    }
//
//		    // read any errors from the attempted command
//		    //System.out.println("Here is the standard error of the command (if any):\n");
//		    while ((s = stdError.readLine()) != null) 
//		    {
//		            System.out.println(s);
//		    }
			
			
			
		} catch (IOException e)
		{			
			e.printStackTrace();
		} catch (InterruptedException e) 
		{			
			e.printStackTrace();
		}
	}
	
	public String generateLotsOfRColors(int numColors)
	{		
		String scriptStr = "plot_colors <- colorRampPalette(c('red','green','orange','blue','yellow'))(" +
				numColors +
				") " + '\n'; 
		
		//# use 5 colors to create a sequence (you can add more colors if you want to)
		//f.c <- colorRampPalette(c('red','green','orange','blue','yellow'))(40)
		//plot(0,type='n', ylim=c(0,255), xlim=c(0,1))
		//for (i in seq(1,40,1)) segments(0, i, 1, i, col=f.c[i])
		
		return scriptStr;
		
	}
	
	public void runR(String runDirectory, String rScript, String imageName)
	{
		String rFilename = imageName +
				".r";
		String scriptFilename = "run.sh";
		String scriptStr = "cd " + runDirectory + '\n' + "/usr/bin/R CMD BATCH --no-save " + rFilename + "\n";
		createDirectory(runDirectory);
		writeFile(scriptStr, runDirectory + scriptFilename);
		writeFile(rScript, runDirectory + rFilename);
		
		
		Runtime rt=Runtime.getRuntime();		
		Process result=null;
		//String exe=new String("wine " + exeStr);
		//String[] exe={new String("/bin/sh " + exeStr + "exe.sh"),exeStr};
		String exe=new String("/bin/sh " + runDirectory + scriptFilename);
		try
		{
			
			result=rt.exec(exe);
			result.waitFor();
			
			String s;
			BufferedReader stdInput = new BufferedReader(new 
		             InputStreamReader(result.getInputStream()));

		    BufferedReader stdError = new BufferedReader(new 
		             InputStreamReader(result.getErrorStream()));

		        // read the output from the command
		    //System.out.println("Here is the standard output of the command:\n");
		    while ((s = stdInput.readLine()) != null) 
		    {
		            System.out.println(s);
		    }

		    // read any errors from the attempted command
		    //System.out.println("Here is the standard error of the command (if any):\n");
		    while ((s = stdError.readLine()) != null) 
		    {
		            System.out.println(s);
		    }
			
			
			
			
			//System.out.println(result.getOutputStream());
		} catch (Exception e)
		{			
			e.printStackTrace();
		}
	}
	
	/**
	 * @param runDirectory
	 * @param rScript
	 */
	@Deprecated
	public void runR(String runDirectory, String rScript)
	{
		String rFilename = "run.r";
		String scriptFilename = "run.sh";
		String scriptStr = "cd " + runDirectory + '\n' + "/usr/bin/R CMD BATCH " + rFilename + "\n";
		createDirectory(runDirectory);
		writeFile(scriptStr, runDirectory + scriptFilename);
		writeFile(rScript, runDirectory + rFilename);
		
		
		Runtime rt=Runtime.getRuntime();		
		Process result=null;
		//String exe=new String("wine " + exeStr);
		//String[] exe={new String("/bin/sh " + exeStr + "exe.sh"),exeStr};
		String exe=new String("/bin/sh " + runDirectory + scriptFilename);
		try
		{
			
			result=rt.exec(exe);
			result.waitFor();
			
			String s;
			BufferedReader stdInput = new BufferedReader(new 
		             InputStreamReader(result.getInputStream()));

		    BufferedReader stdError = new BufferedReader(new 
		             InputStreamReader(result.getErrorStream()));

		        // read the output from the command
		    //System.out.println("Here is the standard output of the command:\n");
		    while ((s = stdInput.readLine()) != null) 
		    {
		            System.out.println(s);
		    }

		    // read any errors from the attempted command
		    //System.out.println("Here is the standard error of the command (if any):\n");
		    while ((s = stdError.readLine()) != null) 
		    {
		            System.out.println(s);
		    }
			
			
			
			
			//System.out.println(result.getOutputStream());
		} catch (Exception e)
		{			
			e.printStackTrace();
		}
	}	
	
	public void runWineExe(String runDirectory, String exeStr)
	{
		String filename = "run.sh";
		String scriptStr = "cd " + runDirectory + '\n' + "wine " + exeStr + "\n";
		createDirectory(runDirectory);
		writeFile(scriptStr, runDirectory + filename);
		
		
		Runtime rt=Runtime.getRuntime();		
		Process result=null;
		//String exe=new String("wine " + exeStr);
		//String[] exe={new String("/bin/sh " + exeStr + "exe.sh"),exeStr};
		String exe=new String("/bin/sh " + runDirectory + "run.sh");
		try
		{
			
			result=rt.exec(exe);
			result.waitFor();
			
			String s;
			BufferedReader stdInput = new BufferedReader(new 
		             InputStreamReader(result.getInputStream()));

		    BufferedReader stdError = new BufferedReader(new 
		             InputStreamReader(result.getErrorStream()));

		        // read the output from the command
		    System.out.println("Here is the standard output of the command:\n");
		    while ((s = stdInput.readLine()) != null) 
		    {
		            System.out.println(s);
		    }

		    // read any errors from the attempted command
		    System.out.println("Here is the standard error of the command (if any):\n");
		    while ((s = stdError.readLine()) != null) 
		    {
		            System.out.println(s);
		    }
			
			
			
			
			//System.out.println(result.getOutputStream());
		} catch (Exception e)
		{			
			e.printStackTrace();
		}
	}	



	public boolean createDirectory(String directory)
	{
//	    // Create one directory
//	    boolean success = (new File(directory)).mkdir();
//	    if (success) {
//	      System.out.println("Directory: " + directory + " created");
//	    }


	    // Create multiple directories
	    boolean success = (new File(directory)).mkdirs();
	    if (success) {
	      System.out.println("Directories: " + directory + " created");
	    }

	    return success;


	}

	@SuppressWarnings("unchecked")
	public String[] getDirectoryList(String directory)
	{
		FilenameFilter filter = new FilenameFilter()
		{
			public boolean accept(File dir, String name)
			{
				boolean accept = true;
				if (name.contains("##check"))
				{
					accept = false;
				} else if (name.startsWith("."))
				{
					accept = false;
				}
				else if (name.contains("##Check"))
				{
					accept = false;
				}
				return accept;
			}
		};


		File dir = new File(directory);

		File files[] = dir.listFiles(filter);
		Arrays.sort( files, new Comparator<File>()
		{
//		     public int compare(final Object o1, final Object o2) {
//		       return new Long(((File)o1).lastModified()).compareTo
//		             (new Long(((File) o2).lastModified()));
//		      }

			@Override
			public int compare(File o1, File o2)
			{
				return new Long((o1).lastModified()).compareTo
	             (new Long(( o2).lastModified()));
			}
		});

		String[] fileNames = new String[files.length];
		int count = 0;
		for (File file : files)
		{
			fileNames[count] = file.getName();
			//System.out.println(fileNames[count]);
			count ++;
		}


//		//String[] children = dir.list();
//		if (files == null)
//		{
//			// Either dir does not exist or is not a directory
//		} else
//		{
//			for (int i = 0; i < children.length; i++)
//			{
//				// Get filename of file or directory
//				String filename = children[i];
//			}
//		}
//
//		children = dir.list(filter);
//		return children;
		return fileNames;

	}

	public Common()
	{
		super();
		// TODO Auto-generated constructor stub
	}
	
	public double calcWindSpeed(double U, double V)
	{
		return Math.sqrt( U*U + V*V  );
	}
	public double calcWindDir(double U, double V)
	{
		return Math.atan(V/U);
	}	
	
	public double calcWindDirDegrees2(double U, double V)
	{
		return (180.+Math.toDegrees(Math.atan2(U,V)) % 360);
	}
	
	public double calcWindDirDegrees(double U, double V)
	{
		double windAbs = calcWindSpeed(U,V);
		double windDirTrigTo = Math.atan2(U/windAbs, V/windAbs);
		double windDirTrigToDegrees =  windDirTrigTo * 180.0/Math.PI;
		return 90.0 - windDirTrigToDegrees;
	}	
	
	public double convertSpecHumidityToRH(double qair, double temp, double press)
	{
	
//	##' Convert specific humidity to relative humidity
//	##'
//	##' converting specific humidity into relative humidity
//	##' NCEP surface flux data does not have RH
//	##' from Bolton 1980 The computation of Equivalent Potential Temperature 
//	##' url{http://www.eol.ucar.edu/projects/ceop/dm/documents/refdata_report/eqns.html}
//	##' @title qair2rh
//	##' @param qair specific humidity, dimensionless (e.g. kg/kg) ratio of water mass / total air mass
//	##' @param temp degrees C
//	##' @param press pressure in mb
//	##' @return rh relative humidity, ratio of actual water mixing ratio to saturation mixing ratio
//	##' @export
//	##' @author David LeBauer
//	qair2rh <- function(qair, temp, press = 1013.25){
	    double es =  6.112 * Math.exp((17.67 * temp)/(temp + 243.5));
	    double e = qair * press / (0.378 * qair + 0.622);
	    double rh = e / es;
	    if (rh > 1)
	    {
	    	rh = 1;
	    }
	    if (rh < 0)	    	
	    {
	    	rh = 0;
	    }
//	    rh[rh > 1] <- 1;
//	    rh[rh < 0] <- 0;
	    return(rh);
	}
	
	public float convertSpecHumidityToRH(float qair, float temp, float press)
	{
	
//	##' Convert specific humidity to relative humidity
//	##'
//	##' converting specific humidity into relative humidity
//	##' NCEP surface flux data does not have RH
//	##' from Bolton 1980 The computation of Equivalent Potential Temperature 
//	##' url{http://www.eol.ucar.edu/projects/ceop/dm/documents/refdata_report/eqns.html}
//	##' @title qair2rh
//	##' @param qair specific humidity, dimensionless (e.g. kg/kg) ratio of water mass / total air mass
//	##' @param temp degrees C
//	##' @param press pressure in mb
//	##' @return rh relative humidity, ratio of actual water mixing ratio to saturation mixing ratio
//	##' @export
//	##' @author David LeBauer
//	qair2rh <- function(qair, temp, press = 1013.25){
		double es =  6.112 * Math.exp((17.67 * temp)/(temp + 243.5));
		double e = qair * press / (0.378 * qair + 0.622);
		Double rh = e / es;
	    if (rh > 1)
	    {
	    	rh = 1.;
	    }
	    if (rh < 0)	    	
	    {
	    	rh = 0.;
	    }
//	    rh[rh > 1] <- 1;
//	    rh[rh < 0] <- 0;
	    float rhFloat = rh.floatValue();
	    return rhFloat;
	}
	
		
	
	/**
	 * @param rh relative humidity (proportion not %)
	 * @param TaK temperature (K)
	 * @return specific humidity
	 */
	public double rhToSh(double rh, double TaK)
	{
		double qair = rh * 2.541e6 * Math.exp(-5415.0/TaK) * 18.0/29.0;
		return qair;
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
	
	public TreeMap<String,ArrayList<String>> readCSVFile(String file, String sepCharacter)
	{
		// whitespace is "\\s+"
		String[] header = null;
		TreeMap<String,ArrayList<String>> arrayOfFileContents = new TreeMap<String,ArrayList<String>>();
		ArrayList<String> fileContents = readTextFileToArray(file);
		for (int i=0;i<fileContents.size();i++)
		{
			String line = fileContents.get(i);
			String[] lineSplit = line.split(sepCharacter,-1);
			if (i==0)
			{
				header = new String[lineSplit.length];
				for (int j=0;j<lineSplit.length;j++)
				{
					header[j] = lineSplit[j];
				}	
				continue;
			}
			for (int j=0;j<lineSplit.length;j++)
			{
				ArrayList<String> item = arrayOfFileContents.get(header[j]);
				if (item == null)
				{
					item = new ArrayList<String>();
				}
				item.add(lineSplit[j]);
				arrayOfFileContents.put(header[j], item);
			}
		}
		return arrayOfFileContents;
		
	}
	
	final static Charset ENCODING = StandardCharsets.UTF_8;
	
	public void appendFile(String text, String filename)
	{
		BufferedWriter bw = null;

		try
		{
			// APPEND MODE SET HERE
			bw = new BufferedWriter(new FileWriter(filename, true));
			bw.write(text);
			bw.newLine();
			bw.flush();
		}
		catch (IOException ioe)
		{
			ioe.printStackTrace();
		}
		finally
		{ // always close the file
			if (bw != null)
				try
				{
					bw.close();
				}
				catch (IOException ioe2)
				{
					// just ignore it
				}
		} // end try/catch/finally

	}
	
   public ArrayList<String> readLargerTextFileAlternateToArray(String aFileName, int lines) 
   {
	   int count =0;
	   ArrayList<String> returnValue = new ArrayList<String>();
	   //StringBuffer sb = new StringBuffer();
	    Path path = Paths.get(aFileName);
	    try (BufferedReader reader = Files.newBufferedReader(path, ENCODING))
	    {
	      String line = null;
	      while ((line = reader.readLine()) != null) 
	      {
	    	  //sb.append(line + '\n');
	    	  returnValue.add(line);
	    	  count++;
	    	  if (count > lines)
	    	  {
	    		  break;
	    	  }
	      }      
	    }
		catch (IOException e)
		{			
			e.printStackTrace();
		}
	    //return sb.toString();
	    return returnValue;
	  }
	
   public ArrayList<String> readLargerTextFileAlternateToArray(String aFileName) 
   {
	   ArrayList<String> returnValue = new ArrayList<String>();
	   //StringBuffer sb = new StringBuffer();
	    Path path = Paths.get(aFileName);
	    try (BufferedReader reader = Files.newBufferedReader(path, ENCODING))
	    {
	      String line = null;
	      while ((line = reader.readLine()) != null) 
	      {
	    	  if (line == null || line.trim().equals(""))
	    	  {
	    		  continue;
	    	  }
	    	  //sb.append(line + '\n');
	    	  returnValue.add(line);
	      }      
	    }
		catch (IOException e)
		{			
			e.printStackTrace();
		}
	    //return sb.toString();
	    return returnValue;
	  }
	
   public String readLargerTextFileAlternate(String aFileName) 
   {	   
	   StringBuffer sb = new StringBuffer();
	    Path path = Paths.get(aFileName);
	    try (BufferedReader reader = Files.newBufferedReader(path, ENCODING))
	    {
	      String line = null;
	      while ((line = reader.readLine()) != null) 
	      {
	    	  sb.append(line + '\n');
	      }      
	    }
		catch (IOException e)
		{			
			e.printStackTrace();
		}
	    return sb.toString();
	  }
	   
	   public String readTextFileToString(String filename)
	   {
		   return readLargerTextFileAlternate(filename);
	   }
	   
	   public ArrayList<String> readTextFileToArray(String filename)
	   {
		   ArrayList<String>  sb = new ArrayList<String> ();
		    Path path = Paths.get(filename);
		    try (BufferedReader reader = Files.newBufferedReader(path, ENCODING))
		    {
		      String line = null;
		      while ((line = reader.readLine()) != null) 
		      {
		    	  if (line == null || line.trim().equals(""))
		    	  {
		    		  continue;
		    	  }
		    	  sb.add(line);
		      }      
		    }
			catch (IOException e)
			{			
				e.printStackTrace();
			}
		    return sb;
		    
	   }
	   
	public String addStringDoubles(String double1, String double2)
	{
		if (double1 == null || double1.equals(""))
		{
			double1 ="0.0";
		}
		if (double2 == null || double2.equals(""))
		{
			double2 ="0.0";
		}
		Double double1Double = new Double(double1).doubleValue(); 
		Double double2Double = new Double(double2).doubleValue(); 
		double addedDoubles = double1Double + double2Double;
		return addedDoubles + "";
	}
	
	public String addDayToDay(String day, int additionalDays)
	{
		Integer startingDay = new Integer(day).intValue();
		Integer nextDay = startingDay + additionalDays;
		return nextDay.toString();
	}
	
	public String averageListOfStrings(ArrayList<String> data)
	{
		String averageStr = "0";
		if (data == null || data.size() == 0)
		{
			return averageStr;
		}			
		
		double total = 0.0;
		Double average = 0.0;
		
		for (String item : data)
		{
			Double itemDouble;
			try
			{
				itemDouble = new Double(item).doubleValue();
			}
			catch (Exception e)
			{
				return "0";
			}
			total = total + itemDouble;
		}
		average = total / data.size();
		average = roundTwoDecimals(average);
		averageStr = average.toString();
		
		return averageStr;
		
	}
	
	public String getAverageOf2DoubleStrings(String item1, String item2)
	{
		Double item1Double = new Double(item1).doubleValue();
		Double item2Double = new Double(item2).doubleValue();
		Double average =  roundToDecimals(  ((item1Double + item2Double) / 2), 2);		
		
		return average.toString();
	}


	public void CalculateH(double t1, double rh1, double t2)
	{

			t1 = t1 + 273.0;
			t2 = t2 + 273.0;

			double	p0, deltaH, R;
			p0 = 7.5152E8;
			deltaH = 42809;
			R = 8.314;

			double sat_p1, sat_p2, vapor, rh2, dew;
			sat_p1 = p0 * Math.exp(-deltaH/(R*t1));
			sat_p2 = p0 * Math.exp(-deltaH/(R*t2));
			vapor = sat_p1 * rh1/100;
			rh2 = (vapor/sat_p2)*100;
			dew = -deltaH/(R*Math.log(vapor/p0)) - 273;

			//vapor = Math.round(vapor*10)/10;
			//rh2   = Math.round(rh2*10)/10;
			//dew   = Math.round(dew*10)/10;

			System.out.println("rh2=" + rh2);
			System.out.println("dew=" + dew);
			System.out.println("vapor=" + vapor);




//			rh2text   = rh2.toString();
//			dewtext   = dew.toString();
//			vaportext = vapor.toString();

	}
	
	public String convertHpaToKpa(String kpaValue)
	{
		String returnValue = "101.30" ;
		
		try 
		{
			Double kpaDouble = new Double(kpaValue).doubleValue();
			kpaDouble = kpaDouble * 0.1;
			kpaDouble = roundToDecimals(kpaDouble, 2);
			returnValue = kpaDouble.toString();
		}
		catch (Exception e)
		{
			
		}
		return returnValue; 
		
		
	}
	
	public String convertKpaToPa(String kpaValue)
	{
		String returnValue = "" ;
		
		try 
		{
			Double kpaDouble = new Double(kpaValue).doubleValue();
			Double paDouble = kpaDouble / 0.001;
			paDouble = roundToDecimals(paDouble, 2);
			int paInt = paDouble.intValue();
			
			returnValue = paInt+"";
		}
		catch (Exception e)
		{
			
		}
		return returnValue; 
		
		
	}

	public void CalculateH(double t1, double rh1)
	{

			t1 = t1 + 273.0;
			//t2 = t2 + 273.0;

			double	p0, deltaH, R;
			p0 = 7.5152E8;
			deltaH = 42809;
			R = 8.314;

			double sat_p1, sat_p2, vapor, rh2, dew;
			sat_p1 = p0 * Math.exp(-deltaH/(R*t1));
			//sat_p2 = p0 * Math.exp(-deltaH/(R*t2));
			vapor = sat_p1 * rh1/100;
			//rh2 = (vapor/sat_p2)*100;
			dew = -deltaH/(R*Math.log(vapor/p0)) - 273;

			//vapor = Math.round(vapor*10)/10;
			//rh2   = Math.round(rh2*10)/10;
			//dew   = Math.round(dew*10)/10;

			//System.out.println("rh2=" + rh2);
			System.out.println("dew=" + dew);
			System.out.println("vapor=" + vapor);




//			rh2text   = rh2.toString();
//			dewtext   = dew.toString();
//			vaportext = vapor.toString();

	}

	public double CalculateRH(double tempC, double vapor)
	{
		double rh1=0;
		double tempK = tempC + 273.0;

		double p0 = 7.5152E8;
		double deltaH = 42809;
		double R = 8.314;

		double sat_p1 = 7.5152E8 * Math.exp(-42809/(8.314*tempK));

		rh1 = 100 * vapor / sat_p1;

		//vapor = sat_p1 * rh1/100;

//		double sat_p1 = p0 * Math.exp(-deltaH/(R*tempK));
//		vapor = sat_p1 * rh1/100;

		//double dew = -deltaH/(R*Math.log(vapor/p0)) - 273;

		//System.out.println("dew=" + dew);
		System.out.println("vapor=" + vapor);
		System.out.println("rh1=" + rh1);

		return rh1;

	}

	public double CalculateRH2(double tempC, double vapor)
	{

//		double tempK = tempC + 273.0;

//		double p0 = 7.5152E8;
//		double deltaH = 42809;
//		double R = 8.314;

//		double sat_p1 = 7.5152E8 * Math.exp(-42809/(8.314*(tempC + 273.0)));

		double rh = 100 * vapor / (7.5152E8 * Math.exp(-42809/(8.314*(tempC + 273.0))));

		//vapor = sat_p1 * rh1/100;

//		double sat_p1 = p0 * Math.exp(-deltaH/(R*tempK));
//		vapor = sat_p1 * rh1/100;

		//double dew = -deltaH/(R*Math.log(vapor/p0)) - 273;

		//System.out.println("dew=" + dew);
		//System.out.println("vapor=" + vapor);
		//System.out.println("rh1=" + rh);

		return rh;

	}
	
	public String convertkmhToms(String WS)
	{
		double windSpeed = new Double(WS).doubleValue();
		
//		km/h -> m/s
		
		double windSpeedMS = windSpeed * 1000 / 3600;
		double wsMsRound = roundToDecimals(windSpeedMS, 2);
		return wsMsRound+ "";
		
	}
	
	public double convertToDouble(String value)
	{
		Double returnValue = new Double(value).doubleValue();
		return returnValue;
	}
	
	// using http://www.engineeringtoolbox.com/humidity-ratio-air-d_686.html
	public double calculateHumidityRatioByVaporPartialPressure(double pw, double pa)
	{
		double x = 0.62198 * pw / (pa - pw) ;
		return x;
	}

	public double CalculateVaporPressure(double t_hmp, double rh_hmp)
	{

		double A_0 = 6.107800;
		double A_1 = 4.436519e-1;
		double A_2 = 1.428946e-2;
		double A_3 = 2.650648e-4;
		double A_4 = 3.031240e-6;
		double A_5 = 2.034081e-8;
		double A_6 = 6.136821e-11;

		rh_hmp = rh_hmp*0.01;

//		// 'Find the HMP45C vapor pressure, in kPa, using a sixth order polynomial (Lowe, 1976).
//		double e_sat = 0.1*(A_0+t_hmp*(A_1+t_hmp*(A_2+t_hmp*(A_3+t_hmp*(A_4+t_hmp*(A_5+t_hmp*A_6))))));

		double e_sat = CalculateVaporPressurekPa(t_hmp);

		double e = e_sat*rh_hmp;

		//hmp in this case just refers to the instrument taking the temperature (t_hmp) and humidity (rh_hmp) measurements

		return e;
	}

	public double CalculateRHFromVapor(double t_hmp, double e)
	{
		double e_sat = CalculateVaporPressurekPa(t_hmp);

		double rh_hmp = e/e_sat ;

		//double e = e_sat*rh_hmp;

		return rh_hmp * 100;
	}

	public double CalculateRHFromVapor2(double t_hmp, double e)
	{
		return e/(0.1*(6.107800+t_hmp*(4.436519e-1+t_hmp*(1.428946e-2+t_hmp*(2.650648e-4+t_hmp*(3.031240e-6+t_hmp*(2.034081e-8+t_hmp*6.136821e-11)))))))*100;
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


	public Connection getMySqlConnection()
	{

		String USERNAME = "envimet";
		String PASSWORD = "envimet";
		String URL = "jdbc:mysql://localhost/envimet";

		Connection conn = null;
		try
		{
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);

		} catch (Exception e)
		{
			e.printStackTrace();
		}

		return conn;
	}
	
	public Connection getSuewsMySqlConnection()
	{

		String USERNAME = "suews";
		String PASSWORD = "suews";
		String URL = "jdbc:mysql://localhost/suews";

		Connection conn = null;
		try
		{
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);

		} catch (Exception e)
		{
			e.printStackTrace();
		}

		return conn;
	}
	
//	public Connection getPrestonSqlite3Connection()
//	{
//		Connection conn = null;
//		// SQLite.Database db = null;
//		try
//		{			
//			Class.forName("org.sqlite.JDBC").newInstance();
//			conn = DriverManager
//					.getConnection("jdbc:sqlite:/" +
//							getHostnameWorkDirPath() +
//							Messages.getString("PrestonWeatherData.PRESTON_SQLITE") );
//			// java.lang.reflect.Method m = conn.getClass().getMethod(
//			// "getSQLiteDatabase", null);
//			// db = (SQLite.Database) m.invoke(conn, null);
//		} catch (Exception e)
//		{
//			e.printStackTrace();			
//		}
//
//		return conn;
//	}
	
	public int getCoMDatabaseYear(int year, String day)
	{
		//2011 database has 2011 12 01 0000 to 2012 03 31 2350 and 2011 database has added fields (precip, etc)
		//2012 database has 2012 12 01 0000 to 2012 12 21 1930
		
		int dayOfYear = new Integer(day).intValue();
		int databaseYear =2011;
		
		if (year == 2011)
		{
			databaseYear = 2011;
		}
		//else if (year == 2012 && day <= 3/31)
		else if (year == 2012 && dayOfYear <= 91)
		{
			databaseYear = 2011;
		}
		else
		{
			databaseYear = 2012;
		}
			
		
		return databaseYear;
	}
	
//	public Connection getCoMSqlite3Connection(int year)
//	{
//		Connection conn = null;
//		// SQLite.Database db = null;
//		try
//		{			
//			Class.forName("org.sqlite.JDBC").newInstance();
//			conn = DriverManager
//					.getConnection("jdbc:sqlite:/" +							
//							Messages.getString("CoMDatabase"
//									+ year) );
//			// java.lang.reflect.Method m = conn.getClass().getMethod(
//			// "getSQLiteDatabase", null);
//			// db = (SQLite.Database) m.invoke(conn, null);
//		} catch (Exception e)
//		{
//			e.printStackTrace();			
//		}
//
//		return conn;
//	}	
	
//	public Connection getLincolnSquareSqliteConnection()
//	{
//		Connection conn = null;
//		// SQLite.Database db = null;
//		try
//		{	
//			Class.forName("org.sqlite.JDBC").newInstance();
//			conn = DriverManager.getConnection("jdbc:sqlite:/" + Messages.getString("LincolnSquare") );
//					
////			Class.forName("SQLite.JDBCDriver").newInstance();
////			conn = DriverManager
////					.getConnection("jdbc:sqlite:/" +							
////							Messages.getString("CoMDatabase"
////									) );
//			// java.lang.reflect.Method m = conn.getClass().getMethod(
//			// "getSQLiteDatabase", null);
//			// db = (SQLite.Database) m.invoke(conn, null);
//		} catch (Exception e)
//		{
//			e.printStackTrace();
//			//System.out.println("Falling back to other sqlite driver");
//			//conn = getCoMSqlite3Connection();
//		}
//
//		return conn;
//	}
	
//	public Connection getMelbourneSWSqliteConnection()
//	{
//		Connection conn = null;
//		// SQLite.Database db = null;
//		try
//		{			
//			Class.forName("org.sqlite.JDBC").newInstance();
//			conn = DriverManager
//					.getConnection("jdbc:sqlite:/" +							
//							Messages.getString("MelbourneSW") );
//			// java.lang.reflect.Method m = conn.getClass().getMethod(
//			// "getSQLiteDatabase", null);
//			// db = (SQLite.Database) m.invoke(conn, null);
//		} catch (Exception e)
//		{
//			e.printStackTrace();			
//		}
//
//		return conn;
//	}
	
//	public Connection getCoMSqliteConnection(int year)
//	{
//		Connection conn = null;
//		// SQLite.Database db = null;
//		try
//		{			
//			Class.forName("SQLite.JDBCDriver").newInstance();
//			conn = DriverManager
//					.getConnection("jdbc:sqlite:/" +							
//							Messages.getString("CoMDatabase"
//									+ year) );
//			// java.lang.reflect.Method m = conn.getClass().getMethod(
//			// "getSQLiteDatabase", null);
//			// db = (SQLite.Database) m.invoke(conn, null);
//		} catch (Exception e)
//		{
//			//e.printStackTrace();
//			//System.out.println("Falling back to other sqlite driver");
//			conn = getCoMSqlite3Connection(year);
//		}
//
//		return conn;
//	}
	
//	public Connection getSmithStSqliteConnection()
//	{
//		Connection conn = null;
//		// SQLite.Database db = null;
//		try
//		{			
//			Class.forName("SQLite.JDBCDriver").newInstance();
//			conn = DriverManager
//					.getConnection("jdbc:sqlite:/" +							
//							Messages.getString("SmithSt") );
//			// java.lang.reflect.Method m = conn.getClass().getMethod(
//			// "getSQLiteDatabase", null);
//			// db = (SQLite.Database) m.invoke(conn, null);
//		} catch (Exception e)
//		{
//			//e.printStackTrace();
//			//System.out.println("Falling back to other sqlite driver");
//			conn = getSmithStSqlite3Connection();
//		}
//
//		return conn;
//	}
	
//	public Connection getSmithStSqlite3Connection()
//	{
//		Connection conn = null;
//		// SQLite.Database db = null;
//		try
//		{			
//			Class.forName("org.sqlite.JDBC").newInstance();
//			conn = DriverManager
//					.getConnection("jdbc:sqlite:/" +							
//							Messages.getString("SmithSt") );
//			// java.lang.reflect.Method m = conn.getClass().getMethod(
//			// "getSQLiteDatabase", null);
//			// db = (SQLite.Database) m.invoke(conn, null);
//		} catch (Exception e)
//		{
//			e.printStackTrace();			
//		}
//
//		return conn;
//	}
//	
//	public Connection getAdelaideAirportSqlite3Connection()
//	{
//		Connection conn = null;
//		// SQLite.Database db = null;
//		try
//		{			
//			Class.forName("org.sqlite.JDBC").newInstance();
//			conn = DriverManager
//					.getConnection("jdbc:sqlite:/" +							
//							Messages.getString("AdelaideAirport") );
//			// java.lang.reflect.Method m = conn.getClass().getMethod(
//			// "getSQLiteDatabase", null);
//			// db = (SQLite.Database) m.invoke(conn, null);
//		} catch (Exception e)
//		{
//			e.printStackTrace();			
//		}
//
//		return conn;
//	}
	
//	public Connection getPrestonSqliteConnection()
//	{
//		Connection conn = null;
//		// SQLite.Database db = null;
//		try
//		{			
//			Class.forName("SQLite.JDBCDriver").newInstance();
//			conn = DriverManager
//					.getConnection("jdbc:sqlite:/" +
//							getHostnameWorkDirPath() +
//							Messages.getString("PrestonWeatherData.PRESTON_SQLITE") );
//			// java.lang.reflect.Method m = conn.getClass().getMethod(
//			// "getSQLiteDatabase", null);
//			// db = (SQLite.Database) m.invoke(conn, null);
//		} catch (Exception e)
//		{
//			//e.printStackTrace();
//			//System.out.println("Falling back to other sqlite driver");
//			conn = getPrestonSqlite3Connection();
//		}
//
//		return conn;
//	}	
	
	//calculate ldown
	public String ld_mod(String Ta, String RH)
	{
		double TaDouble = new Double(Ta).doubleValue();
		double RHDouble = new Double(RH).doubleValue();
		
	    double bcof = 0.015+((1.9*(Math.pow(10,-4)))*(TaDouble)) ; //# eq 7 Loridan et al., (2010)
	    double flcd = 0.185*((Math.exp(bcof*RHDouble)-1))  ;       //# eq 6 Loridan et al., (2010)
	    double ea =0.611*Math.exp(17.27*TaDouble/(237.3+TaDouble))/100*RHDouble;
	    double w = 46.5*(ea/TaDouble);  //# eq 6 Loridan et al., (2010)
	    double Emis_clr = 1-(1+w)*Math.exp(-Math.sqrt(1.2+(3*w)));
	    //#Emis_sky = Emis_clr+(1-Emis_clr)*(flcd^2)
	    double LD = (Emis_clr+(1-Emis_clr)*flcd)*(( Math.pow((TaDouble+273.15),4))  ) *(5.67*(Math.pow(10,-8)));  //## eq 9 Loridan et al., (2010)
	    
	    return LD+"";
	}
	
	
//	public Connection getBomObservationsSqliteConnection()
//	{
//		Connection conn = null;
//		// SQLite.Database db = null;
//		try
//		{			
//			Class.forName("SQLite.JDBCDriver").newInstance();
//			conn = DriverManager
//					.getConnection("jdbc:sqlite:/" +							
//							Messages.getString("BOMOBSERVATIONS_SQLITE") );
//
//		} catch (Exception e)
//		{
//
//			conn = getBomObservationsSqlite3Connection();
//		}
//
//		return conn;
//	}
	
	
//	public Connection getBomObservationsSqlite3Connection()
//	{
//		Connection conn = null;
//		try
//		{			
//			Class.forName("org.sqlite.JDBC").newInstance();
//			conn = DriverManager
//					.getConnection("jdbc:sqlite:/" +							
//							Messages.getString("BOMOBSERVATIONS_SQLITE") );
//		} catch (Exception e)
//		{
//			e.printStackTrace();			
//		}
//
//		return conn;
//	}
	
	
	
//	
//	
//	public Connection getAdelaide1MinSolarSqliteConnection()
//	{
//		Connection conn = null;
//		// SQLite.Database db = null;
//		try
//		{			
//			Class.forName("SQLite.JDBCDriver").newInstance();
//			conn = DriverManager
//					.getConnection("jdbc:sqlite:/" +							
//							Messages.getString("ADELAIDE1MINSOLAR_SQLITE") );
//
//		} catch (Exception e)
//		{
//
//			conn = getAdelaide1MinSolarSqlite3Connection();
//		}
//
//		return conn;
//	}
	
//	public Connection getMelbourne1MinSolarSqliteConnection()
//	{
//		Connection conn = null;
//		// SQLite.Database db = null;
//		try
//		{			
//			Class.forName("SQLite.JDBCDriver").newInstance();
//			conn = DriverManager
//					.getConnection("jdbc:sqlite:/" +							
//							Messages.getString("MELBOURNE1MINSOLAR_SQLITE") );
//
//		} catch (Exception e)
//		{
//
//			conn = getMelbourne1MinSolarSqlite3Connection();
//		}
//
//		return conn;
//	}
	
//	public Connection getAdelaide1MinSolarSqlite3Connection()
//	{
//		Connection conn = null;
//		try
//		{			
//			Class.forName("org.sqlite.JDBC").newInstance();
//			conn = DriverManager
//					.getConnection("jdbc:sqlite:/" +							
//							Messages.getString("ADELAIDE1MINSOLAR_SQLITE") );
//		} catch (Exception e)
//		{
//			e.printStackTrace();			
//		}
//
//		return conn;
//	}
	
//	public Connection getMelbourne1MinSolarSqlite3Connection()
//	{
//		Connection conn = null;
//		try
//		{			
//			Class.forName("org.sqlite.JDBC").newInstance();
//			conn = DriverManager
//					.getConnection("jdbc:sqlite:/" +							
//							Messages.getString("MELBOURNE1MINSOLAR_SQLITE") );
//		} catch (Exception e)
//		{
//			e.printStackTrace();			
//		}
//
//		return conn;
//	}
	
//	public Connection getMelbourne1Min30SolarSqlite3Connection()
//	{
//		Connection conn = null;
//		try
//		{			
//			Class.forName("org.sqlite.JDBC").newInstance();
//			conn = DriverManager
//					.getConnection("jdbc:sqlite:/" +							
//							Messages.getString("MELBOURNE1MIN30SOLAR_SQLITE") );
//		} catch (Exception e)
//		{
//			e.printStackTrace();			
//		}
//
//		return conn;
//	}

	public Connection getSqliteConnection()
	{
		Connection conn = null;
		// SQLite.Database db = null;
		try
		{
			Class.forName("SQLite.JDBCDriver").newInstance();
			conn = DriverManager
					.getConnection("jdbc:sqlite://home/nice/Documents/MonashMasters/Research Dissertation/Envimet-data.sqlite3");
			// java.lang.reflect.Method m = conn.getClass().getMethod(
			// "getSQLiteDatabase", null);
			// db = (SQLite.Database) m.invoke(conn, null);
		} catch (Exception e)
		{
			e.printStackTrace();
		}

		return conn;
	}

	public ArrayList<String> getOutputFiles(int runID, int variable)
	{
		ArrayList<String> list = new ArrayList<String>();
		String query = "select distinct output_file from run_data where run_id = "
				+ "?" +
				// + runID +
				" and variable_type = " + "?"
		// + variable
		;
		System.out.println(query);

		Connection conn = getMySqlConnection();
		try
		{
			PreparedStatement ps = conn.prepareStatement(query);
			ps.setInt(1, runID);
			ps.setInt(2, variable);
			// Statement stat = conn.createStatement();

			// ResultSet rs = stat.executeQuery(query);
			ResultSet rs = ps.executeQuery();

			while (rs.next())
			{
				String outputFile = rs.getString("output_file");
				list.add(outputFile);
				 System.out.println("add output_file " + outputFile);
				 System.out.println("list=" + list.toString());

			}
			rs.close();
			conn.close();

		} catch (Exception e)
		{
			e.printStackTrace();
		}

		return list;
	}

	public ArrayList<Integer> getDistinctVariableTypesForRun(int runID)
	{
		ArrayList<Integer> runVariableTypes = new ArrayList<Integer>();
		Connection conn = getMySqlConnection();
		String query = "select distinct variable_type from run_data where run_id = "
				+ runID;

		try
		{

			Statement stat = conn.createStatement();

			ResultSet rs = stat.executeQuery(query);

			while (rs.next())
			{
				Integer variableType = rs.getInt("variable_type");
				runVariableTypes.add(variableType);
			}
			rs.close();

		} catch (Exception e)
		{
			e.printStackTrace();
		}

		try
		{
			conn.close();
		} catch (SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return runVariableTypes;
	}


	public TreeMap<String, Integer> getDataVariableTypes()
	{
		TreeMap<String, Integer> variables = new TreeMap<String, Integer>();
		String query = "select variable_id, variable_name from run_variables ";

		Connection conn = getMySqlConnection();
		try
		{
			Statement stat = conn.createStatement();

			ResultSet rs = stat.executeQuery(query);
			while (rs.next())
			{
				Integer variableID = rs.getInt("variable_id");
				String variableName = rs.getString("variable_name");

				variables.put(variableName, variableID);
			}
			rs.close();
			conn.close();

		} catch (Exception e)
		{
			e.printStackTrace();
		}

		System.out.println(variables.toString());
		return variables;
	}




	public String removeIllegalCharacters(String str)
	{
		return str.replaceAll("/", "_");
	}

    public void actualPlotCmd(String plotname, String outputDirectory)
    {
    	try
		{
			String[] commands = new String[]{"/home/kerryn/bin/gnuplot_bin_indiv.sh", plotname, outputDirectory};
			Process aProcess = Runtime.getRuntime().exec(commands);
			aProcess.waitFor();

		} catch (Exception e)
		{
			System.err.println(e);
			System.exit(1);
		}
    }


    public boolean verifyFileExists(String filePathString)
    { 
    	boolean fileExists = false;
    	File f = new File(filePathString);
    	if(f.exists() && !f.isDirectory()) 
    	{ 
    	    fileExists = true;
    	}

    	return fileExists;

    }

}
