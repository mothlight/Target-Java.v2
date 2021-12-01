package Target;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Set;
import java.util.TimeZone;
import ucar.ma2.Array;
import ucar.ma2.ArrayFloat;
import ucar.ma2.InvalidRangeException;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;

public class AccessMetData
{
	Common common = new Common();
	
	private HashMap<Integer,int[]> latLonGridMap = new HashMap<Integer,int[]>();
	private HashMap<String,ArrayList<Integer>> latLonGridReverseMap = new HashMap<String,ArrayList<Integer>>();
	private ArrayList<HashMap<String,HashMap<String,Float>>> gridData = new ArrayList<HashMap<String,HashMap<String,Float>>>();
	private ArrayList<Long> timesteps = new ArrayList<Long>();
	private ArrayList<String> zones = new ArrayList<String>();
	private ArrayList<double[]> latLontoLCMap = new ArrayList<double[]>();



	private float north = 4.980469f;
	private float south = -54.902344f;
	private float west = 95.009766f;
	private float east = 169.892578f;	
	private int numLat = 512;
	private int numLon = 427;
	
	private double xGradient = (north - south) / numLat;
	private double yGradient = (west - east) / numLon;
	
	
//	private int tifWidth = numLon;
//	private int tifHeight = numLat;
	private double originX = west;
	private double originY = north;
//	double pixelSizeX = xGradient;
//	double pixelSizeY = yGradient;
	
	private String accessNc4File = "/media/kerryn/87d9469d-56aa-4a1f-a62d-5f03d7599bbf/Data/BOMAccess/IDY25001.APS3.all-flds.slv.2019100812.024.surface.nc4";
	private String lcLatLonMappingFile = "/media/kerryn/87d9469d-56aa-4a1f-a62d-5f03d7599bbf/Data/TARGET_runs/Melbourne_SOM_Clusters_test_forcing/input/Melbourne_SOM_Clusters/LC/MelbourneFeatures_LC_LatLon.csv";
	ArrayFloat.D2 temp_scrnArray;
	ArrayFloat.D2 qsair_scrnArray;
	ArrayFloat.D2 u10Array;
	ArrayFloat.D2 v10Array;
	ArrayFloat.D2 av_mslpArray;
	ArrayFloat.D2 av_swsfcdownArray;
	ArrayFloat.D2 av_lwsfcdownArray;
	public static String underscore = "_";
	
	public static final String TEMPERATURE = "temperature";
	public static final String RH = "RH";
	public static final String P = "P";
	public static final String WS = "WS";
	public static final String SW = "SW";
	public static final String LW = "LW";
//	private int timesteps = 1;
	
	
	public static void main(String[] args)
	{
		AccessMetData a = new AccessMetData();
		
		
		int[] domainDim=new int[]{1697749,1};
		double latEdge=-37.505054;
		double lonEdge=144.647901;
		double latResolution=.00004294;
		double lonResolution=.0021849;
		int latX=domainDim[0];
		int lonY=domainDim[1];
//		String mapFile = "/media/kerryn/87d9469d-56aa-4a1f-a62d-5f03d7599bbf/Data/TARGET_runs/Melbourne_SOM_Clusters_test_forcing/input/Melbourne_SOM_Clusters/LC/MelbourneFeatures_LC_LatLon.csv";
		
		a.initLatLonGrid(latX, lonY, latEdge, latResolution, lonEdge, lonResolution, a.lcLatLonMappingFile);
		a.initAccessData();
		a.produceGridData();
	}
	
	
	
	public AccessMetData()
	{
		super();
	}



	public AccessMetData(float north, float south, float west,
			float east, int numLat, int numLon,
			String accessNc4File, String lcLatLonMappingFile,
			int latX, int lonY, double latEdge, double lonEdge, double latResolution, double lonResolution)
	{
		super();
		this.north = north;
		this.south = south;
		this.west = west;
		this.east = east;
		this.numLat = numLat;
		this.numLon = numLon;
		this.originX = west;
		this.originY = north;
		this.accessNc4File = accessNc4File;
		this.lcLatLonMappingFile = lcLatLonMappingFile;
		
		this.xGradient = (north - south) / numLat;
		this.yGradient = (west - east) / numLon;

		
//		int[] domainDim=new int[]{1697749,1};
//		double latEdge=-37.505054;
//		double lonEdge=144.647901;
//		double latResolution=.00004294;
//		double lonResolution=.0021849;
//		int latX=latX;
//		int lonY=lonY;
	
		initLatLonGrid(latX, lonY, latEdge, latResolution, lonEdge, lonResolution, lcLatLonMappingFile);
		initAccessData();
		produceGridData();

	}



	public void loadAccessMetData()
	{
		int[] domainDim=new int[]{1697749,1};
		double latEdge=-37.505054;
		double lonEdge=144.647901;
		double latResolution=.00004294;
		double lonResolution=.0021849;
		int latX=domainDim[0];
		int lonY=domainDim[1];
		
		
		initLatLonGrid(latX, lonY, latEdge, latResolution, lonEdge, lonResolution, lcLatLonMappingFile);
		initAccessData();
		produceGridData();
	}
	
	public void produceGridData()
	{
		for (int i=0;i<timesteps.size();i++)
		{
			Set<String> keySet = latLonGridReverseMap.keySet();
			HashMap<String,HashMap<String,Float>> dataForTimestep = new HashMap<String,HashMap<String,Float>>();
			for (String key : keySet)
			{
				String[] xy = key.split(underscore);
				int x = new Integer(xy[0]).intValue();
				int y = new Integer(xy[1]).intValue();
				
				float temp = temp_scrnArray.get(x, y)-273.15f;
//				System.out.println("temp="+temp + " " + x + " " + y);		
				float qs = qsair_scrnArray.get(x, y);
//				System.out.println("qs="+qs + " " + x + " " + y);
				float u10 = u10Array.get(x, y);
//				System.out.println("u10="+u10 + " " + x + " " + y);
				float v10 = v10Array.get(x, y);
//				System.out.println("v10="+v10 + " " + x + " " + y);
				float p = av_mslpArray.get(x, y)/100.0f;
//				System.out.println("p="+p + " " + x + " " + y);
				float sw = av_swsfcdownArray.get(x, y);
//				System.out.println("sw="+sw + " " + x + " " + y);
				float lw = av_lwsfcdownArray.get(x, y);
//				System.out.println("lw="+lw + " " + x + " " + y);
//				double windDir = common.calcWindDir(u10, v10);
				float windSpeed = (float)common.calcWindSpeed(u10, v10);
				
				float rh = common.convertSpecHumidityToRH(qs,temp,p)*100f;
				
				HashMap<String,Float> item = new HashMap<String,Float>();
				item.put(TEMPERATURE, temp);
				item.put(RH, rh);
				item.put(P, p);
				item.put(WS, windSpeed);
				item.put(SW, sw);
				item.put(LW, lw);
				
//				System.out.println(key + " " + item.toString());
				
				dataForTimestep.put(key, item);					
			}
			gridData.add(dataForTimestep);	
		}
	}

	
	
	public void initLatLonGrid(int latX, int lonY, double latEdge, double latResolution, double lonEdge, double lonResolution, String mapFile)
	{
		//read the lat/lon coordinates from a file and map 1 by 1 to grids
		if (latX == 1 || lonY ==1)
		{
			int count = 0;
			// lat     lon
//			-38.08501235746281      144.3020243462435
//			-38.08546249510884      144.3020077916968
			ArrayList<String> fileContents = common.readLargerTextFileAlternateToArray(mapFile);
			for (String line : fileContents)
			{
				if (line.startsWith("lat"))
				{
					continue;
				}
				
				String[] lineSplit = Common.splitOnWhitespace(line);
				String latStr = lineSplit[0];
				String lonStr = lineSplit[1];
				
				double lat = new Double(latStr).doubleValue();
				double lon = new Double(lonStr).doubleValue();
				
				int[] xy = getLatLonXY(lat, lon);
				int x = xy[0];
				int y = xy[1];
				latLonGridMap.put(count, xy);
				String key = x + underscore + y;
				ArrayList<Integer> list = latLonGridReverseMap.get(key);
				if (list == null)
				{
					list = new ArrayList<Integer>();
				}
				list.add(count);
				latLonGridReverseMap.put(key, list);
				
				latLontoLCMap.add(new double[]{lat,lon});
				
				count ++;
			}
		}
		else
		{
			float[] latArray = new float[latX];
			for (int i=0;i<latX;i++)
			{
				float value = (float) (latEdge + (latResolution * i));
				latArray[i]=value;
			}
			float[] lonArray = new float[lonY];
			for (int i=0;i<lonY;i++)
			{
				float value = (float) (lonEdge + (lonResolution * i));
				lonArray[i]=value;
			}
		
//			try
//			{
//				writer.write(lat, Array.factory(latArray));
//				writer.write(lon, Array.factory(lonArray));
//			}
			
//			int count = 0;
//			for (int latIdx = 0; latIdx < latDim.getLength(); latIdx++)
//			{
//				for (int lonIdx = 0; lonIdx < lonDim.getLength(); lonIdx++)
//				{
//					
//				}
//			}
//			
//			
//			int count = 0;
//			for (int latIdx = 0; latIdx < latDim.getLength(); latIdx++)
//			{
//				for (int lonIdx = 0; lonIdx < lonDim.getLength(); lonIdx++)
//				{
//				}
//			}
		}
		
		Set<String> listOfZones = latLonGridReverseMap.keySet();
		zones = new ArrayList<String>();
		for (String zone : listOfZones)
		{
			zones.add(zone);
		}
		
		
	}
	
//	public void test()
//	{
//		double lat;
//		double lon;
//		
//		lat = -37.73;
//		lon = 145.01;
//		
////		lat = 4.97;
////		lon = 95.1;
//		
////		lat = 4.04;
////		lon = 95.1;
//		
//		int[] xy = getLatLonXY(lat, lon);
//		int x = xy[0];
//		int y = xy[1];
//		
//		float temp = temp_scrnArray.get(x, y);
//		System.out.println("temp="+temp + " " + x + " " + y);		
//		float qs = qsair_scrnArray.get(x, y);
//		System.out.println("qs="+qs + " " + x + " " + y);
//		float u10 = u10Array.get(x, y);
//		System.out.println("u10="+u10 + " " + x + " " + y);
//		float v10 = v10Array.get(x, y);
//		System.out.println("v10="+v10 + " " + x + " " + y);
//		float p = av_mslpArray.get(x, y);
//		System.out.println("p="+p + " " + x + " " + y);
//		float sw = av_swsfcdownArray.get(x, y);
//		System.out.println("sw="+sw + " " + x + " " + y);
//		float lw = av_lwsfcdownArray.get(x, y);
//		System.out.println("lw="+lw + " " + x + " " + y);
//		double windDir = common.calcWindDir(u10, v10);
//		
//		
//	}
	
	public int[] getLatLonXY(double lat, double lon)
	{
//		double y = (lat - originY)/pixelSizeY ;
//		double x = (lon - originX)/pixelSizeX ;
		
		double y = (originY - lat)/xGradient ;  //should be about 366
		double x = (originX - lon)/yGradient ;  //should be about 284
		
		return new int[]{(int) Math.round(y),(int) Math.round(x)};
	}
	
//	public int[] getPopDensityLatLonXY(double lat, double lon)
//	{
////		   #  lats/lons vary by 0.0416
////	    latVary = 0.0416
////	    lonVary = 0.0416
////		int xVary = 8640;
////		int yVary = 4320;
//		
//		double originY = -180.;
//		double originX = 90.;
//		double pixelSizeY = 0.0416;
//		double pixelSizeX = -0.0416;
//		
//		pixelSizeY = 180./4320.;
//		pixelSizeX = 360./8640.;
//		
//		double y = Math.abs( (lon - originY)/pixelSizeY );
//		double x = Math.abs( (lat - originX)/pixelSizeX );
//		
//		return new int[]{(int) Math.round(y),(int) Math.round(x)};
//	}
	
	
//	netcdf file:/media/kerryn/87d9469d-56aa-4a1f-a62d-5f03d7599bbf/Data/BOMAccess/IDY25001.APS3.all-flds.slv.2019100812.024.surface.nc4 {
//		  dimensions:
//		    time = UNLIMITED;   // (1 currently)
//		    bnds = 2;
//		    lat = 512;
//		    lon = 427;
//		    soil_lvl = 4;
//		    char_size = 4;
	
	public void initAccessData()
	{
		String metAccessFile = accessNc4File;		
		NetcdfFile dataFile = null;
		try
		{
			dataFile = NetcdfFile.open(metAccessFile);
			
//			Variable latVar = dataFile.findVariable("latitude");
//			int[] latShape = latVar.getShape();
//			int[] latOrigin = new int[2];
//			ArrayDouble.D1 latArray;
//			latArray = (ArrayDouble.D1) latVar.read(latOrigin,latShape);
//			System.out.println(latArray.toString());
				
//			Variable lonVar = dataFile.findVariable("longitude");
//			int[] lonShape = lonVar.getShape();
//			int[] lonOrigin = new int[2];
//			ArrayDouble.D1 lonArray;
//			lonArray = (ArrayDouble.D1) lonVar.read(lonOrigin,lonShape);
//			System.out.println(lonArray.toString());
			
			
//			   #  lats/lons vary by 0.0416
//			    latVary = 0.0416
//			    lonVary = 0.0416
			
//			float flux(latitude=1800, longitude=3600);
//			  :units = "kgC/m^2/y";
			
			
//		    double time(time=1);
//	      :axis = "T";
//	      :bounds = "time_bnds";
//	      :units = "days since 2019-10-08 12:00:00";
//	      :standard_name = "time";
//	      :long_name = "time";
//	      :calendar = "gregorian";
//	      :_ChunkSizes = 1U; // uint
			


			Variable temp_scrnVar = dataFile.findVariable("temp_scrn");
			Variable qsair_scrnVar = dataFile.findVariable("qsair_scrn");
			Variable u10Var = dataFile.findVariable("u10");
			Variable v10Var = dataFile.findVariable("v10");
			Variable av_mslpVar = dataFile.findVariable("av_mslp");
			Variable av_swsfcdownVar = dataFile.findVariable("av_swsfcdown");
			Variable av_lwsfcdownVar = dataFile.findVariable("av_lwsfcdown");

			int[] temp_scrnShape = temp_scrnVar.getShape();
			int[] temp_scrnOrigin = new int[4];
			temp_scrnOrigin[0] = 0;		
			
			
			Variable timeVar = dataFile.findVariable("forecast_reference_time");
//			int[] timeShape = timeVar.getShape();
			String timeUnits = timeVar.getUnitsString();
			// "seconds since 2019-10-08 12:00:00"
			//  [days, since, 2019-10-08, 12:00:00]
			String[] unitsSplit = timeUnits.split(" ");
			String day = unitsSplit[2];
			String timeOfDay = unitsSplit[3];
			String[] yyyy_mm_dd = day.split("-");
			String yearStr = yyyy_mm_dd[0];
			String monthStr = yyyy_mm_dd[1];
			String dayStr = yyyy_mm_dd[2];
			String[] hh_mm_ss = timeOfDay.split(":");
			String hourStr = hh_mm_ss[0];
			String minuteStr = hh_mm_ss[1];
			String secondStr = hh_mm_ss[2];
			
			Array timeArray = timeVar.read();
			for (int i=0;i<timeArray.getSize();i++)
			{
				long millis = timeArray.getLong(i) + getFormattedDatetimeLong(yearStr, monthStr, dayStr, hourStr, minuteStr, secondStr) ;
				timesteps.add(millis);				
			}
			
			
			
			
			
//			Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+10:00"));
//			localCalendar.setTimeInMillis(millis);
//			
//			int newYear = localCalendar.get(Calendar.YEAR) ;
//		    int newMonth = localCalendar.get(Calendar.MONTH) + 1;
//		    int newDay = localCalendar.get(Calendar.DAY_OF_MONTH) ;
//		    int newHour = localCalendar.get(Calendar.HOUR_OF_DAY) ;
//		    int newMinute = localCalendar.get(Calendar.MINUTE) ;
//		    
//		    String newMinuteStr = common.padLeft(newMinute, 2, '0');
//		    String newHourStr = common.padLeft(newHour, 2, '0');
//		    String newMonthStr = common.padLeft(newMonth, 2, '0');
//		    String newDayStr = common.padLeft(newDay, 2, '0');
//	
//		    String reformattedTime = newHourStr + ":" + newMinuteStr;
//		    String reformattedDate = newDayStr + "/" + newMonthStr + "/" + newYear;
//		    System.out.println( reformattedDate + " " + reformattedTime);
			
					
//			ArrayDouble.D0 timeArray =  (ArrayDouble.D0) timeVar.read(temp_scrnOrigin,timeShape).reduce();	
//			double timeDouble = timeArray.get();
			
//			int64 forecast_reference_time(time) ;
//			forecast_reference_time:units = "seconds since 2019-10-08 12:00:00" ;
//			forecast_reference_time:standard_name = "forecast_reference_time" ;
//			forecast_reference_time:calendar = "gregorian" ;


			
			temp_scrnArray =  (ArrayFloat.D2) temp_scrnVar.read(temp_scrnOrigin,temp_scrnShape).reduce();			
			qsair_scrnArray =  (ArrayFloat.D2) qsair_scrnVar.read(temp_scrnOrigin,temp_scrnShape).reduce();
			u10Array =  (ArrayFloat.D2) u10Var.read(temp_scrnOrigin,temp_scrnShape).reduce();
			v10Array =  (ArrayFloat.D2) v10Var.read(temp_scrnOrigin,temp_scrnShape).reduce();
			av_mslpArray =  (ArrayFloat.D2) av_mslpVar.read(temp_scrnOrigin,temp_scrnShape).reduce();
			av_swsfcdownArray =  (ArrayFloat.D2) av_swsfcdownVar.read(temp_scrnOrigin,temp_scrnShape).reduce();
			av_lwsfcdownArray =  (ArrayFloat.D2) av_lwsfcdownVar.read(temp_scrnOrigin,temp_scrnShape).reduce();

		}
		catch (InvalidRangeException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{			
			e.printStackTrace();
		}
	}

	public HashMap<Integer, int[]> getLatLonGridMap()
	{
		return latLonGridMap;
	}

	public void setLatLonGridMap(HashMap<Integer, int[]> latLonGridMap)
	{
		this.latLonGridMap = latLonGridMap;
	}

	public HashMap<String, ArrayList<Integer>> getLatLonGridReverseMap()
	{
		return latLonGridReverseMap;
	}

	public void setLatLonGridReverseMap(HashMap<String, ArrayList<Integer>> latLonGridReverseMap)
	{
		this.latLonGridReverseMap = latLonGridReverseMap;
	}

	public ArrayList<HashMap<String, HashMap<String, Float>>> getGridData()
	{
		return gridData;
	}

	public void setGridData(ArrayList<HashMap<String, HashMap<String, Float>>> gridData)
	{
		this.gridData = gridData;
	}

	public float getNorth()
	{
		return north;
	}

	public void setNorth(float north)
	{
		this.north = north;
	}

	public float getSouth()
	{
		return south;
	}

	public void setSouth(float south)
	{
		this.south = south;
	}

	public float getWest()
	{
		return west;
	}

	public void setWest(float west)
	{
		this.west = west;
	}

	public float getEast()
	{
		return east;
	}

	public void setEast(float east)
	{
		this.east = east;
	}

	public int getNumLat()
	{
		return numLat;
	}

	public void setNumLat(int numLat)
	{
		this.numLat = numLat;
	}

	public int getNumLon()
	{
		return numLon;
	}

	public void setNumLon(int numLon)
	{
		this.numLon = numLon;
	}

	public double getxGradient()
	{
		return xGradient;
	}

	public void setxGradient(double xGradient)
	{
		this.xGradient = xGradient;
	}

	public double getyGradient()
	{
		return yGradient;
	}

	public void setyGradient(double yGradient)
	{
		this.yGradient = yGradient;
	}

	public double getOriginX()
	{
		return originX;
	}

	public void setOriginX(double originX)
	{
		this.originX = originX;
	}

	public double getOriginY()
	{
		return originY;
	}

	public void setOriginY(double originY)
	{
		this.originY = originY;
	}

	public String getFilename()
	{
		return accessNc4File;
	}

	public void setFilename(String filename)
	{
		this.accessNc4File = filename;
	}

	public ArrayList<Long> getTimesteps()
	{
		return timesteps;
	}

	public void setTimesteps(ArrayList<Long> timesteps)
	{
		this.timesteps = timesteps;
	}
	
	public long getFormattedDatetimeLong(String yearStr, String monthStr, String dayStr, String hourStr, String minuteStr, String secondStr)
	{
		int year = new Integer(yearStr).intValue();
		int month = new Integer(monthStr).intValue()-1;
		int day = new Integer(dayStr).intValue();
		int hour = new Integer(hourStr).intValue();
		int minute = new Integer(minuteStr).intValue();
		int second = new Integer(secondStr).intValue();
		
	    Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
	    calendar.set(Calendar.ZONE_OFFSET, TimeZone.getTimeZone("UTC").getRawOffset());
	    calendar.set(Calendar.YEAR, year);
	    calendar.set(Calendar.MONTH, month);
	    calendar.set(Calendar.DAY_OF_MONTH, day);
	    calendar.set(Calendar.HOUR_OF_DAY, hour);
	    calendar.set(Calendar.MINUTE, minute);
	    calendar.set(Calendar.SECOND, second);

		long millis = calendar.getTimeInMillis();
		
		return millis;
//		Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+10:00"));
//		localCalendar.setTimeInMillis(millis);
//		
//		int newYear = localCalendar.get(Calendar.YEAR) ;
//	    int newMonth = localCalendar.get(Calendar.MONTH) + 1;
//	    int newDay = localCalendar.get(Calendar.DAY_OF_MONTH) ;
//	    int newHour = localCalendar.get(Calendar.HOUR_OF_DAY) ;
//	    int newMinute = localCalendar.get(Calendar.MINUTE) ;
//	    
//	    String newMinuteStr = common.padLeft(newMinute, 2, '0');
//	    String newHourStr = common.padLeft(newHour, 2, '0');
//	    String newMonthStr = common.padLeft(newMonth, 2, '0');
//	    String newDayStr = common.padLeft(newDay, 2, '0');
//
//	    String reformattedTime = newHourStr + ":" + newMinuteStr;
//	    String reformattedDate = newDayStr + "/" + newMonthStr + "/" + newYear;
//	    return reformattedDate + " " + reformattedTime;
	}
	
	public String getFormattedDatetime(String yearStr, String monthStr, String dayStr, String hourStr, String minuteStr, String secondStr)
	{
		int year = new Integer(yearStr).intValue();
		int month = new Integer(monthStr).intValue()-1;
		int day = new Integer(dayStr).intValue();
		int hour = new Integer(hourStr).intValue();
		int minute = new Integer(minuteStr).intValue();
		int second = new Integer(secondStr).intValue();
		
	    Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
	    calendar.set(Calendar.ZONE_OFFSET, TimeZone.getTimeZone("UTC").getRawOffset());
	    calendar.set(Calendar.YEAR, year);
	    calendar.set(Calendar.MONTH, month);
	    calendar.set(Calendar.DAY_OF_MONTH, day);
	    calendar.set(Calendar.HOUR_OF_DAY, hour);
	    calendar.set(Calendar.MINUTE, minute);
	    calendar.set(Calendar.SECOND, second);

		long millis = calendar.getTimeInMillis();
		
		
		Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+10:00"));
		localCalendar.setTimeInMillis(millis);
		
		int newYear = localCalendar.get(Calendar.YEAR) ;
	    int newMonth = localCalendar.get(Calendar.MONTH) + 1;
	    int newDay = localCalendar.get(Calendar.DAY_OF_MONTH) ;
	    int newHour = localCalendar.get(Calendar.HOUR_OF_DAY) ;
	    int newMinute = localCalendar.get(Calendar.MINUTE) ;
	    
	    String newMinuteStr = common.padLeft(newMinute, 2, '0');
	    String newHourStr = common.padLeft(newHour, 2, '0');
	    String newMonthStr = common.padLeft(newMonth, 2, '0');
	    String newDayStr = common.padLeft(newDay, 2, '0');

	    String reformattedTime = newHourStr + ":" + newMinuteStr;
	    String reformattedDate = newDayStr + "/" + newMonthStr + "/" + newYear;
	    return reformattedDate + " " + reformattedTime;
	}
	
	

//File "IDY25001.APS3.all-flds.slv.2019100812.024.surface.nc4"
//File type: Hierarchical Data Format, version 5
// 
//netcdf file:/media/kerryn/87d9469d-56aa-4a1f-a62d-5f03d7599bbf/Data/BOMAccess/IDY25001.APS3.all-flds.slv.2019100812.024.surface.nc4 {
//  dimensions:
//    time = UNLIMITED;   // (1 currently)
//    bnds = 2;
//    lat = 512;
//    lon = 427;
//    soil_lvl = 4;
//    char_size = 4;
//  variables:
//    float sfc_pres(time=1, lat=512, lon=427);
//      :standard_name = "surface_air_pressure";
//      :long_name = "surface pressure";
//      :units = "Pa";
//      :um_stash_source = "m01s00i409";
//      :accum_type = "instantaneous";
//      :coverage_content_type = "modelResult";
//      :grid_type = "spatial";
//      :level_type = "single";
//      :marsParam = 134; // int
//      :grid_mapping = "latitude_longitude";
//      :coordinates = "forecast_reference_time";
//      :_ChunkSizes = 1U, 512U, 427U; // uint
//
//    int latitude_longitude;
//      :earth_radius = 6371229.0; // double
//      :grid_mapping_name = "latitude_longitude";
//      :longitude_of_prime_meridian = 0.0; // double
//
//    double time(time=1);
//      :axis = "T";
//      :bounds = "time_bnds";
//      :units = "days since 2019-10-08 12:00:00";
//      :standard_name = "time";
//      :long_name = "time";
//      :calendar = "gregorian";
//      :_ChunkSizes = 1U; // uint
//
//    double time_bnds(time=1, bnds=2);
//      :_ChunkSizes = 1U, 2U; // uint
//
//    float lat(lat=512);
//      :axis = "Y";
//      :bounds = "lat_bnds";
//      :units = "degrees_north";
//      :standard_name = "latitude";
//      :long_name = "latitudes";
//      :type = "uniform";
//      :valid_max = 5.0f; // float
//      :valid_min = -55.0f; // float
//
//    float lat_bnds(lat=512, bnds=2);
//
//    float lon(lon=427);
//      :axis = "X";
//      :bounds = "lon_bnds";
//      :units = "degrees_east";
//      :standard_name = "longitude";
//      :long_name = "longitudes";
//      :type = "uniform";
//      :valid_max = 170.0f; // float
//      :valid_min = 95.0f; // float
//
//    float lon_bnds(lon=427, bnds=2);
//
//    long forecast_reference_time(time=1);
//      :units = "seconds since 2019-10-08 12:00:00";
//      :standard_name = "forecast_reference_time";
//      :calendar = "gregorian";
//      :_ChunkSizes = 1U; // uint
//
//    float temp_scrn(time=1, lat=512, lon=427);
//      :standard_name = "air_temperature";
//      :long_name = "screen level temperature";
//      :units = "K";
//      :um_stash_source = "m01s03i236";
//      :accum_type = "instantaneous";
//      :coverage_content_type = "modelResult";
//      :grid_type = "spatial";
//      :level_type = "single";
//      :marsParam = 167; // int
//      :grid_mapping = "latitude_longitude";
//      :coordinates = "forecast_reference_time height";
//      :_ChunkSizes = 1U, 512U, 427U; // uint
//
//    float height;
//      :units = "m";
//      :long_name = "height";
//
//    float qsair_scrn(time=1, lat=512, lon=427);
//      :standard_name = "specific_humidity";
//      :long_name = "screen level specific humidity";
//      :units = "kg kg-1";
//      :um_stash_source = "m01s03i237";
//      :accum_type = "instantaneous";
//      :coverage_content_type = "modelResult";
//      :grid_type = "spatial";
//      :level_type = "single";
//      :marsParam = 81; // int
//      :grid_mapping = "latitude_longitude";
//      :coordinates = "forecast_reference_time height";
//      :_ChunkSizes = 1U, 512U, 427U; // uint
//
//    float dewpt_scrn(time=1, lat=512, lon=427);
//      :standard_name = "dew_point_temperature";
//      :long_name = "screen level dewpoint temperature";
//      :units = "K";
//      :um_stash_source = "m01s03i250";
//      :accum_type = "instantaneous";
//      :coverage_content_type = "modelResult";
//      :grid_type = "spatial";
//      :level_type = "single";
//      :marsParam = 168; // int
//      :grid_mapping = "latitude_longitude";
//      :coordinates = "forecast_reference_time height";
//      :_ChunkSizes = 1U, 512U, 427U; // uint
//
//    float rh2m(time=1, lat=512, lon=427);
//      :standard_name = "relative_humidity";
//      :long_name = "screen level relative humidity";
//      :units = "%";
//      :um_stash_source = "m01s03i245";
//      :accum_type = "instantaneous";
//      :coverage_content_type = "modelResult";
//      :grid_type = "spatial";
//      :level_type = "single";
//      :marsParam = 101; // int
//      :grid_mapping = "latitude_longitude";
//      :coordinates = "forecast_reference_time height";
//      :_ChunkSizes = 1U, 512U, 427U; // uint
//
//    float precwtr(time=1, lat=512, lon=427);
//      :standard_name = "atmosphere_mass_content_of_water_vapor";
//      :long_name = "precipitable water";
//      :units = "kg m-2";
//      :accum_type = "instantaneous";
//      :coverage_content_type = "modelResult";
//      :grid_type = "spatial";
//      :level_type = "single";
//      :marsParam = 137; // int
//      :grid_mapping = "latitude_longitude";
//      :coordinates = "forecast_reference_time";
//      :_ChunkSizes = 1U, 512U, 427U; // uint
//
//    float u50r(time=1, lat=512, lon=427);
//      :standard_name = "eastward_wind";
//      :long_name = "zonal wind at the 50m rho level";
//      :units = "m s-1";
//      :accum_type = "instantaneous";
//      :coverage_content_type = "modelResult";
//      :grid_type = "spatial";
//      :level_type = "single";
//      :marsParam = 231033; // int
//      :grid_mapping = "latitude_longitude";
//      :coordinates = "forecast_reference_time";
//      :_ChunkSizes = 1U, 512U, 427U; // uint
//
//    float v50r(time=1, lat=512, lon=427);
//      :standard_name = "northward_wind";
//      :long_name = "meridional wind at the 50m rho level";
//      :units = "m s-1";
//      :accum_type = "instantaneous";
//      :coverage_content_type = "modelResult";
//      :grid_type = "spatial";
//      :level_type = "single";
//      :marsParam = 231034; // int
//      :grid_mapping = "latitude_longitude";
//      :coordinates = "forecast_reference_time";
//      :_ChunkSizes = 1U, 512U, 427U; // uint
//
//    float av_netswsfc(time=1, lat=512, lon=427);
//      :standard_name = "surface_net_downward_shortwave_flux";
//      :long_name = "average net shortwave radiation at surface";
//      :units = "W m-2";
//      :um_stash_source = "m01s01i202";
//      :accum_type = "mean";
//      :accum_units = "hrs";
//      :accum_value = 1; // int
//      :coverage_content_type = "modelResult";
//      :grid_type = "spatial";
//      :level_type = "single";
//      :marsParam = 228211; // int
//      :cell_methods = "time: mean";
//      :grid_mapping = "latitude_longitude";
//      :coordinates = "forecast_reference_time";
//      :_ChunkSizes = 1U, 512U, 427U; // uint
//
//    float av_swirrtop(time=1, lat=512, lon=427);
//      :standard_name = "toa_incoming_shortwave_flux";
//      :long_name = "average incoming shortwave radiation flux";
//      :units = "W m-2";
//      :um_stash_source = "m01s01i207";
//      :accum_type = "mean";
//      :accum_units = "hrs";
//      :accum_value = 1; // int
//      :coverage_content_type = "modelResult";
//      :grid_type = "spatial";
//      :level_type = "single";
//      :marsParam = 228216; // int
//      :cell_methods = "time: mean";
//      :grid_mapping = "latitude_longitude";
//      :coordinates = "forecast_reference_time";
//      :_ChunkSizes = 1U, 512U, 427U; // uint
//
//    float av_sfc_sw_dir(time=1, lat=512, lon=427);
//      :standard_name = "surface_direct_downwelling_shortwave_flux_in_air";
//      :long_name = "average surface shortwave direct radiation flux";
//      :units = "W m-2";
//      :um_stash_source = "m01s01i215";
//      :accum_type = "mean";
//      :accum_units = "hrs";
//      :accum_value = 1; // int
//      :coverage_content_type = "modelResult";
//      :grid_type = "spatial";
//      :level_type = "single";
//      :marsParam = 228115; // int
//      :cell_methods = "time: mean";
//      :grid_mapping = "latitude_longitude";
//      :coordinates = "forecast_reference_time";
//      :_ChunkSizes = 1U, 512U, 427U; // uint
//
//    float av_sfc_sw_dif(time=1, lat=512, lon=427);
//      :standard_name = "surface_diffuse_downwelling_shortwave_flux_in_air";
//      :long_name = "average surface shortwave diffuse radiation flux";
//      :units = "W m-2";
//      :um_stash_source = "m01s01i216";
//      :accum_type = "mean";
//      :accum_units = "hrs";
//      :accum_value = 1; // int
//      :coverage_content_type = "modelResult";
//      :grid_type = "spatial";
//      :level_type = "single";
//      :marsParam = 228047; // int
//      :cell_methods = "time: mean";
//      :grid_mapping = "latitude_longitude";
//      :coordinates = "forecast_reference_time";
//      :_ChunkSizes = 1U, 512U, 427U; // uint
//
//    float av_swsfcdown(time=1, lat=512, lon=427);
//      :standard_name = "surface_downwelling_shortwave_flux_in_air";
//      :long_name = "average downwards shortwave radiation at the surface";
//      :units = "W m-2";
//      :um_stash_source = "m01s01i235";
//      :accum_type = "mean";
//      :accum_units = "hrs";
//      :accum_value = 1; // int
//      :coverage_content_type = "modelResult";
//      :grid_type = "spatial";
//      :level_type = "single";
//      :marsParam = 228214; // int
//      :cell_methods = "time: mean";
//      :grid_mapping = "latitude_longitude";
//      :coordinates = "forecast_reference_time";
//      :_ChunkSizes = 1U, 512U, 427U; // uint
//
//    float av_netlwsfc(time=1, lat=512, lon=427);
//      :standard_name = "surface_net_downward_longwave_flux";
//      :long_name = "average net longwave radiation at surface";
//      :units = "W m-2";
//      :um_stash_source = "m01s02i201";
//      :accum_type = "mean";
//      :accum_units = "hrs";
//      :accum_value = 1; // int
//      :coverage_content_type = "modelResult";
//      :grid_type = "spatial";
//      :level_type = "single";
//      :marsParam = 228212; // int
//      :cell_methods = "time: mean";
//      :grid_mapping = "latitude_longitude";
//      :coordinates = "forecast_reference_time";
//      :_ChunkSizes = 1U, 512U, 427U; // uint
//
//    float av_olr(time=1, lat=512, lon=427);
//      :standard_name = "toa_outgoing_longwave_flux";
//      :long_name = "average outgoing longwave radiation";
//      :units = "W m-2";
//      :um_stash_source = "m01s02i205";
//      :accum_type = "mean";
//      :accum_units = "hrs";
//      :accum_value = 1; // int
//      :coverage_content_type = "modelResult";
//      :grid_type = "spatial";
//      :level_type = "single";
//      :marsParam = 228215; // int
//      :cell_methods = "time: mean";
//      :grid_mapping = "latitude_longitude";
//      :coordinates = "forecast_reference_time";
//      :_ChunkSizes = 1U, 512U, 427U; // uint
//
//    float av_lwsfcdown(time=1, lat=512, lon=427);
//      :standard_name = "surface_downwelling_longwave_flux_in_air";
//      :long_name = "average downwards longwave radiation at the surface";
//      :units = "W m-2";
//      :um_stash_source = "m01s02i207";
//      :accum_type = "mean";
//      :accum_units = "hrs";
//      :accum_value = 1; // int
//      :coverage_content_type = "modelResult";
//      :grid_type = "spatial";
//      :level_type = "single";
//      :marsParam = 228213; // int
//      :cell_methods = "time: mean";
//      :grid_mapping = "latitude_longitude";
//      :coordinates = "forecast_reference_time";
//      :_ChunkSizes = 1U, 512U, 427U; // uint
//
//    float accum_ls_rain(time=1, lat=512, lon=427);
//      :standard_name = "stratiform_rainfall_amount";
//      :long_name = "accumulative large scale rainfall";
//      :units = "kg m-2";
//      :um_stash_source = "m01s04i201";
//      :accum_type = "accumulative";
//      :accum_units = "hrs";
//      :accum_value = 27; // int
//      :coverage_content_type = "modelResult";
//      :grid_type = "spatial";
//      :level_type = "single";
//      :marsParam = 228242; // int
//      :grid_mapping = "latitude_longitude";
//      :cell_methods = "time_0: sum";
//      :coordinates = "forecast_reference_time time_0";
//      :_ChunkSizes = 1U, 512U, 427U; // uint
//
//    float accum_ls_snow(time=1, lat=512, lon=427);
//      :standard_name = "stratiform_snowfall_amount";
//      :long_name = "accumulative large scale snowfall";
//      :units = "kg m-2";
//      :um_stash_source = "m01s04i202";
//      :accum_type = "accumulative";
//      :accum_units = "hrs";
//      :accum_value = 27; // int
//      :coverage_content_type = "modelResult";
//      :grid_type = "spatial";
//      :level_type = "single";
//      :marsParam = 228240; // int
//      :grid_mapping = "latitude_longitude";
//      :cell_methods = "time_0: sum";
//      :coordinates = "forecast_reference_time time_0";
//      :_ChunkSizes = 1U, 512U, 427U; // uint
//
//    float sfc_rough_len(time=1, lat=512, lon=427);
//      :standard_name = "surface_roughness_length_for_momentum_in_air";
//      :long_name = "surface roughness length for momentum";
//      :units = "m";
//      :um_stash_source = "m01s03i028";
//      :accum_type = "instantaneous";
//      :coverage_content_type = "modelResult";
//      :grid_type = "spatial";
//      :level_type = "single";
//      :marsParam = 229109; // int
//      :grid_mapping = "latitude_longitude";
//      :coordinates = "forecast_reference_time";
//      :_ChunkSizes = 1U, 512U, 427U; // uint
//
//    float u10(time=1, lat=512, lon=427);
//      :standard_name = "eastward_wind";
//      :long_name = "10m wind u component";
//      :units = "m s-1";
//      :um_stash_source = "m01s03i209";
//      :accum_type = "instantaneous";
//      :coverage_content_type = "modelResult";
//      :grid_type = "spatial";
//      :level_type = "single";
//      :marsParam = 165; // int
//      :grid_mapping = "latitude_longitude";
//      :coordinates = "forecast_reference_time height_0";
//      :_ChunkSizes = 1U, 512U, 427U; // uint
//
//    float height_0;
//      :units = "m";
//      :long_name = "height";
//
//    float av10u(time=1, lat=512, lon=427);
//      :standard_name = "eastward_wind";
//      :long_name = "average zonal wind at 10m";
//      :units = "m s-1";
//      :um_stash_source = "m01s03i209";
//      :accum_type = "mean";
//      :accum_units = "hrs";
//      :accum_value = 1; // int
//      :coverage_content_type = "modelResult";
//      :grid_type = "spatial";
//      :level_type = "single";
//      :marsParam = 228233; // int
//      :cell_methods = "time: mean";
//      :grid_mapping = "latitude_longitude";
//      :coordinates = "forecast_reference_time height_0";
//      :_ChunkSizes = 1U, 512U, 427U; // uint
//
//    float v10(time=1, lat=512, lon=427);
//      :standard_name = "northward_wind";
//      :long_name = "10m wind v component";
//      :units = "m s-1";
//      :um_stash_source = "m01s03i210";
//      :accum_type = "instantaneous";
//      :coverage_content_type = "modelResult";
//      :grid_type = "spatial";
//      :level_type = "single";
//      :marsParam = 166; // int
//      :grid_mapping = "latitude_longitude";
//      :coordinates = "forecast_reference_time height_0";
//      :_ChunkSizes = 1U, 512U, 427U; // uint
//
//    float av10v(time=1, lat=512, lon=427);
//      :standard_name = "northward_wind";
//      :long_name = "average meridional wind at 10m";
//      :units = "m s-1";
//      :um_stash_source = "m01s03i210";
//      :accum_type = "mean";
//      :accum_units = "hrs";
//      :accum_value = 1; // int
//      :coverage_content_type = "modelResult";
//      :grid_type = "spatial";
//      :level_type = "single";
//      :marsParam = 228234; // int
//      :cell_methods = "time: mean";
//      :grid_mapping = "latitude_longitude";
//      :coordinates = "forecast_reference_time height_0";
//      :_ChunkSizes = 1U, 512U, 427U; // uint
//
//    float sens_hflx(time=1, lat=512, lon=427);
//      :standard_name = "surface_upward_sensible_heat_flux";
//      :long_name = "surface sensible heat flux";
//      :units = "W m-2";
//      :um_stash_source = "m01s03i217";
//      :accum_type = "instantaneous";
//      :coverage_content_type = "modelResult";
//      :grid_type = "spatial";
//      :level_type = "single";
//      :marsParam = 146; // int
//      :grid_mapping = "latitude_longitude";
//      :coordinates = "forecast_reference_time";
//      :_ChunkSizes = 1U, 512U, 427U; // uint
//
//    float av_sens_hflx(time=1, lat=512, lon=427);
//      :standard_name = "surface_upward_sensible_heat_flux";
//      :long_name = "average surface sensible heat flux";
//      :units = "W m-2";
//      :um_stash_source = "m01s03i217";
//      :accum_type = "mean";
//      :accum_units = "hrs";
//      :accum_value = 1; // int
//      :coverage_content_type = "modelResult";
//      :grid_type = "spatial";
//      :level_type = "single";
//      :marsParam = 228222; // int
//      :cell_methods = "time: mean";
//      :grid_mapping = "latitude_longitude";
//      :coordinates = "forecast_reference_time";
//      :_ChunkSizes = 1U, 512U, 427U; // uint
//
//    float av_sfc_mois_flx(time=1, lat=512, lon=427);
//      :long_name = "average total surface moisture flux";
//      :units = "kg m-2 s-1";
//      :um_stash_source = "m01s03i223";
//      :accum_type = "mean";
//      :accum_units = "hrs";
//      :accum_value = 1; // int
//      :coverage_content_type = "modelResult";
//      :grid_type = "spatial";
//      :level_type = "single";
//      :marsParam = 233; // int
//      :cell_methods = "time: mean";
//      :grid_mapping = "latitude_longitude";
//      :coordinates = "forecast_reference_time";
//      :_ChunkSizes = 1U, 512U, 427U; // uint
//
//    float accum_evap(time=1, lat=512, lon=427);
//      :standard_name = "water_evaporation_amount";
//      :long_name = "accumulated evaporation";
//      :units = "kg m-2";
//      :um_stash_source = "m01s03i229";
//      :accum_type = "accumulative";
//      :accum_units = "hrs";
//      :accum_value = 27; // int
//      :coverage_content_type = "modelResult";
//      :fill_value = 1.0E36f; // float
//      :grid_type = "spatial";
//      :level_type = "single";
//      :marsParam = 228057; // int
//      :missing_value = 1.0E36f; // float
//      :grid_mapping = "latitude_longitude";
//      :cell_methods = "time_0: sum";
//      :coordinates = "forecast_reference_time time_0";
//      :_ChunkSizes = 1U, 512U, 427U; // uint
//
//    float av_evap_sea(time=1, lat=512, lon=427);
//      :standard_name = "surface_water_evaporation_flux";
//      :long_name = "average rate of evaporation over open sea";
//      :units = "kg m-2 s-1";
//      :um_stash_source = "m01s03i232";
//      :accum_type = "mean";
//      :accum_units = "hrs";
//      :accum_value = 1; // int
//      :coverage_content_type = "modelResult";
//      :grid_type = "spatial";
//      :level_type = "single";
//      :marsParam = 228055; // int
//      :cell_methods = "time: mean";
//      :grid_mapping = "latitude_longitude";
//      :coordinates = "forecast_reference_time";
//      :_ChunkSizes = 1U, 512U, 427U; // uint
//
//    float lat_hflx(time=1, lat=512, lon=427);
//      :standard_name = "surface_upward_latent_heat_flux";
//      :long_name = "surface latent heat flux";
//      :units = "W m-2";
//      :um_stash_source = "m01s03i234";
//      :accum_type = "instantaneous";
//      :coverage_content_type = "modelResult";
//      :grid_type = "spatial";
//      :level_type = "single";
//      :marsParam = 147; // int
//      :grid_mapping = "latitude_longitude";
//      :coordinates = "forecast_reference_time";
//      :_ChunkSizes = 1U, 512U, 427U; // uint
//
//    float av_lat_hflx(time=1, lat=512, lon=427);
//      :standard_name = "surface_upward_latent_heat_flux";
//      :long_name = "average surface latent heat flux";
//      :units = "W m-2";
//      :um_stash_source = "m01s03i234";
//      :accum_type = "mean";
//      :accum_units = "hrs";
//      :accum_value = 1; // int
//      :coverage_content_type = "modelResult";
//      :grid_type = "spatial";
//      :level_type = "single";
//      :marsParam = 228221; // int
//      :cell_methods = "time: mean";
//      :grid_mapping = "latitude_longitude";
//      :coordinates = "forecast_reference_time";
//      :_ChunkSizes = 1U, 512U, 427U; // uint
//
//    float tmin_scrn(time=1, lat=512, lon=427);
//      :standard_name = "air_temperature";
//      :long_name = "screen level min temperature";
//      :units = "K";
//      :um_stash_source = "m01s03i236";
//      :accum_type = "minimum";
//      :accum_units = "hrs";
//      :accum_value = 1; // int
//      :coverage_content_type = "modelResult";
//      :grid_type = "spatial";
//      :level_type = "single";
//      :marsParam = 202; // int
//      :cell_methods = "time: minimum";
//      :grid_mapping = "latitude_longitude";
//      :coordinates = "forecast_reference_time height";
//      :_ChunkSizes = 1U, 512U, 427U; // uint
//
//    float av_temp_scrn(time=1, lat=512, lon=427);
//      :standard_name = "air_temperature";
//      :long_name = "average screen level air temperature";
//      :units = "K";
//      :um_stash_source = "m01s03i236";
//      :accum_type = "mean";
//      :accum_units = "hrs";
//      :accum_value = 1; // int
//      :coverage_content_type = "modelResult";
//      :grid_type = "spatial";
//      :level_type = "single";
//      :marsParam = 228200; // int
//      :cell_methods = "time: mean";
//      :grid_mapping = "latitude_longitude";
//      :coordinates = "forecast_reference_time height";
//      :_ChunkSizes = 1U, 512U, 427U; // uint
//
//    float tmax_scrn(time=1, lat=512, lon=427);
//      :standard_name = "air_temperature";
//      :long_name = "screen level max temperature";
//      :units = "K";
//      :um_stash_source = "m01s03i236";
//      :accum_type = "maximum";
//      :accum_units = "hrs";
//      :accum_value = 1; // int
//      :coverage_content_type = "modelResult";
//      :grid_type = "spatial";
//      :level_type = "single";
//      :marsParam = 201; // int
//      :cell_methods = "time: maximum";
//      :grid_mapping = "latitude_longitude";
//      :coordinates = "forecast_reference_time height";
//      :_ChunkSizes = 1U, 512U, 427U; // uint
//
//    float av_qsair_scrn(time=1, lat=512, lon=427);
//      :standard_name = "specific_humidity";
//      :long_name = "average screen level specific humidity";
//      :units = "kg kg-1";
//      :um_stash_source = "m01s03i237";
//      :accum_type = "mean";
//      :accum_units = "hrs";
//      :accum_value = 1; // int
//      :coverage_content_type = "modelResult";
//      :grid_type = "spatial";
//      :level_type = "single";
//      :marsParam = 228253; // int
//      :cell_methods = "time: mean";
//      :grid_mapping = "latitude_longitude";
//      :coordinates = "forecast_reference_time height";
//      :_ChunkSizes = 1U, 512U, 427U; // uint
//
//    float fog_fraction(time=1, lat=512, lon=427);
//      :standard_name = "fog_area_fraction";
//      :long_name = "fog fraction";
//      :units = "1";
//      :um_stash_source = "m01s03i248";
//      :accum_type = "instantaneous";
//      :coverage_content_type = "modelResult";
//      :grid_type = "spatial";
//      :level_type = "single";
//      :marsParam = 229210; // int
//      :grid_mapping = "latitude_longitude";
//      :coordinates = "forecast_reference_time height";
//      :_ChunkSizes = 1U, 512U, 427U; // uint
//
//    float vis_precip(time=1, lat=512, lon=427);
//      :standard_name = "visibility_in_air";
//      :long_name = "visibility at 1.5m (incl precip)";
//      :units = "m";
//      :um_stash_source = "m01s03i281";
//      :accum_type = "instantaneous";
//      :coverage_content_type = "modelResult";
//      :grid_type = "spatial";
//      :level_type = "single";
//      :marsParam = 229121; // int
//      :grid_mapping = "latitude_longitude";
//      :coordinates = "forecast_reference_time height";
//      :_ChunkSizes = 1U, 512U, 427U; // uint
//
//    float prob_vis_5km_ppt(time=1, lat=512, lon=427);
//      :long_name = "probability of vis less than 5 km (incl precip)";
//      :units = "1";
//      :um_stash_source = "m01s03i283";
//      :accum_type = "instantaneous";
//      :coverage_content_type = "modelResult";
//      :grid_type = "spatial";
//      :level_type = "single";
//      :marsParam = 229123; // int
//      :grid_mapping = "latitude_longitude";
//      :coordinates = "forecast_reference_time height";
//      :_ChunkSizes = 1U, 512U, 427U; // uint
//
//    float uwnd_strs(time=1, lat=512, lon=427);
//      :standard_name = "surface_downward_eastward_stress";
//      :long_name = "surface zonal wind stress";
//      :units = "N m-2";
//      :um_stash_source = "m01s03i460";
//      :accum_type = "instantaneous";
//      :coverage_content_type = "modelResult";
//      :grid_type = "spatial";
//      :level_type = "single";
//      :marsParam = 180; // int
//      :grid_mapping = "latitude_longitude";
//      :coordinates = "forecast_reference_time";
//      :_ChunkSizes = 1U, 512U, 427U; // uint
//
//    float av_uwnd_strs(time=1, lat=512, lon=427);
//      :standard_name = "surface_downward_eastward_stress";
//      :long_name = "average surface zonal wind stress";
//      :units = "N m-2";
//      :um_stash_source = "m01s03i460";
//      :accum_type = "mean";
//      :accum_units = "hrs";
//      :accum_value = 1; // int
//      :coverage_content_type = "modelResult";
//      :grid_type = "spatial";
//      :level_type = "single";
//      :marsParam = 228224; // int
//      :cell_methods = "time: mean";
//      :grid_mapping = "latitude_longitude";
//      :coordinates = "forecast_reference_time";
//      :_ChunkSizes = 1U, 512U, 427U; // uint
//
//    float vwnd_strs(time=1, lat=512, lon=427);
//      :standard_name = "surface_downward_northward_stress";
//      :long_name = "surface meridional wind stress";
//      :units = "N m-2";
//      :um_stash_source = "m01s03i461";
//      :accum_type = "instantaneous";
//      :coverage_content_type = "modelResult";
//      :grid_type = "spatial";
//      :level_type = "single";
//      :marsParam = 181; // int
//      :grid_mapping = "latitude_longitude";
//      :coordinates = "forecast_reference_time";
//      :_ChunkSizes = 1U, 512U, 427U; // uint
//
//    float av_vwnd_strs(time=1, lat=512, lon=427);
//      :standard_name = "surface_downward_northward_stress";
//      :long_name = "average surface meridional wind stress";
//      :units = "N m-2";
//      :um_stash_source = "m01s03i461";
//      :accum_type = "mean";
//      :accum_units = "hrs";
//      :accum_value = 1; // int
//      :coverage_content_type = "modelResult";
//      :grid_type = "spatial";
//      :level_type = "single";
//      :marsParam = 228225; // int
//      :cell_methods = "time: mean";
//      :grid_mapping = "latitude_longitude";
//      :coordinates = "forecast_reference_time";
//      :_ChunkSizes = 1U, 512U, 427U; // uint
//
//    float wndgust10m(time=1, lat=512, lon=427);
//      :standard_name = "wind_speed_of_gust";
//      :long_name = "10m wind gust";
//      :units = "m s-1";
//      :um_stash_source = "m01s03i463";
//      :accum_type = "instantaneous";
//      :coverage_content_type = "modelResult";
//      :grid_type = "spatial";
//      :level_type = "single";
//      :marsParam = 49; // int
//      :grid_mapping = "latitude_longitude";
//      :coordinates = "forecast_reference_time height_0";
//      :_ChunkSizes = 1U, 512U, 427U; // uint
//
//    float accum_conv_rain(time=1, lat=512, lon=427);
//      :standard_name = "convective_rainfall_amount";
//      :long_name = "accumulative convective rainfall";
//      :units = "kg m-2";
//      :um_stash_source = "m01s05i201";
//      :accum_type = "accumulative";
//      :accum_units = "hrs";
//      :accum_value = 27; // int
//      :coverage_content_type = "modelResult";
//      :grid_type = "spatial";
//      :level_type = "single";
//      :marsParam = 228243; // int
//      :grid_mapping = "latitude_longitude";
//      :cell_methods = "time_0: sum";
//      :coordinates = "forecast_reference_time time_0";
//      :_ChunkSizes = 1U, 512U, 427U; // uint
//
//    float accum_conv_snow(time=1, lat=512, lon=427);
//      :standard_name = "convective_snowfall_amount";
//      :long_name = "accumulative convective snowfall";
//      :units = "kg m-2";
//      :um_stash_source = "m01s05i202";
//      :accum_type = "accumulative";
//      :accum_units = "hrs";
//      :accum_value = 27; // int
//      :coverage_content_type = "modelResult";
//      :grid_type = "spatial";
//      :level_type = "single";
//      :marsParam = 228239; // int
//      :grid_mapping = "latitude_longitude";
//      :cell_methods = "time_0: sum";
//      :coordinates = "forecast_reference_time time_0";
//      :_ChunkSizes = 1U, 512U, 427U; // uint
//
//    float conv_cldbse_pres(time=1, lat=512, lon=427);
//      :standard_name = "air_pressure_at_convective_cloud_base";
//      :long_name = "convective cloud-base pressure";
//      :units = "Pa";
//      :um_stash_source = "m01s05i207";
//      :accum_type = "instantaneous";
//      :coverage_content_type = "modelResult";
//      :fill_value = 1.0E36f; // float
//      :grid_type = "spatial";
//      :level_type = "single";
//      :marsParam = 89; // int
//      :missing_value = 1.0E36f; // float
//      :grid_mapping = "latitude_longitude";
//      :coordinates = "forecast_reference_time";
//      :_ChunkSizes = 1U, 512U, 427U; // uint
//
//    float conv_cldtop_pres(time=1, lat=512, lon=427);
//      :standard_name = "air_pressure_at_convective_cloud_top";
//      :long_name = "convective cloud-top pressure";
//      :units = "Pa";
//      :um_stash_source = "m01s05i208";
//      :accum_type = "instantaneous";
//      :coverage_content_type = "modelResult";
//      :fill_value = 1.0E36f; // float
//      :grid_type = "spatial";
//      :level_type = "single";
//      :marsParam = 90; // int
//      :missing_value = 1.0E36f; // float
//      :grid_mapping = "latitude_longitude";
//      :coordinates = "forecast_reference_time";
//      :_ChunkSizes = 1U, 512U, 427U; // uint
//
//    float accum_prcp(time=1, lat=512, lon=427);
//      :standard_name = "precipitation_amount";
//      :long_name = "accumulated precipitation";
//      :units = "kg m-2";
//      :um_stash_source = "m01s05i226";
//      :accum_type = "accumulative";
//      :accum_units = "hrs";
//      :accum_value = 27; // int
//      :coverage_content_type = "modelResult";
//      :grid_type = "spatial";
//      :level_type = "single";
//      :marsParam = 228061; // int
//      :grid_mapping = "latitude_longitude";
//      :cell_methods = "time_0: sum";
//      :coordinates = "forecast_reference_time time_0";
//      :_ChunkSizes = 1U, 512U, 427U; // uint
//
//    float soil_mois_cont(time=1, lat=512, lon=427);
//      :long_name = "soil moisture available for transpiration";
//      :units = "kg m-2";
//      :um_stash_source = "m01s08i208";
//      :accum_type = "instantaneous";
//      :coverage_content_type = "modelResult";
//      :fill_value = 1.0E36f; // float
//      :grid_type = "spatial";
//      :level_type = "single";
//      :marsParam = 86; // int
//      :missing_value = 1.0E36f; // float
//      :grid_mapping = "latitude_longitude";
//      :coordinates = "forecast_reference_time";
//      :_ChunkSizes = 1U, 512U, 427U; // uint
//
//    float canopy_wtr_cont(time=1, lat=512, lon=427);
//      :standard_name = "canopy_water_amount";
//      :long_name = "canopy water content";
//      :units = "kg m-2";
//      :um_stash_source = "m01s08i209";
//      :accum_type = "instantaneous";
//      :coverage_content_type = "modelResult";
//      :fill_value = 1.0E36f; // float
//      :grid_type = "spatial";
//      :level_type = "single";
//      :marsParam = 87; // int
//      :missing_value = 1.0E36f; // float
//      :grid_mapping = "latitude_longitude";
//      :coordinates = "forecast_reference_time";
//      :_ChunkSizes = 1U, 512U, 427U; // uint
//
//    float soil_mois(time=1, soil_lvl=4, lat=512, lon=427);
//      :standard_name = "moisture_content_of_soil_layer";
//      :long_name = "soil moisture content";
//      :units = "kg m-2";
//      :um_stash_source = "m01s08i223";
//      :accum_type = "instantaneous";
//      :coverage_content_type = "modelResult";
//      :fill_value = 1.0E36f; // float
//      :grid_type = "spatial";
//      :level_type = "multi";
//      :marsParam = 140; // int
//      :missing_value = 1.0E36f; // float
//      :grid_mapping = "latitude_longitude";
//      :coordinates = "forecast_reference_time";
//      :_ChunkSizes = 1U, 2U, 512U, 427U; // uint
//
//    float soil_lvl(soil_lvl=4);
//      :axis = "Z";
//      :bounds = "soil_lvl_bnds";
//      :units = "m";
//      :standard_name = "depth";
//      :long_name = "soil levels";
//      :positive = "down";
//      :type = "depth";
//
//    float soil_lvl_bnds(soil_lvl=4, bnds=2);
//
//    float soil_temp(time=1, soil_lvl=4, lat=512, lon=427);
//      :standard_name = "soil_temperature";
//      :long_name = "soil temperature";
//      :units = "K";
//      :um_stash_source = "m01s08i225";
//      :accum_type = "mean";
//      :accum_units = "hrs";
//      :coverage_content_type = "modelResult";
//      :fill_value = 1.0E36f; // float
//      :grid_type = "spatial";
//      :level_type = "multi";
//      :marsParam = 139; // int
//      :missing_value = 1.0E36f; // float
//      :cell_methods = "soil_lvl: mean";
//      :grid_mapping = "latitude_longitude";
//      :coordinates = "forecast_reference_time";
//      :_ChunkSizes = 1U, 2U, 512U, 427U; // uint
//
//    float low_cld(time=1, lat=512, lon=427);
//      :standard_name = "low_type_cloud_area_fraction";
//      :long_name = "low cloud cover";
//      :units = "1";
//      :um_stash_source = "m01s09i203";
//      :accum_type = "instantaneous";
//      :coverage_content_type = "modelResult";
//      :grid_type = "spatial";
//      :level_type = "single";
//      :marsParam = 186; // int
//      :grid_mapping = "latitude_longitude";
//      :coordinates = "forecast_reference_time";
//      :_ChunkSizes = 1U, 512U, 427U; // uint
//
//    float mid_cld(time=1, lat=512, lon=427);
//      :standard_name = "medium_type_cloud_area_fraction";
//      :long_name = "middle cloud cover";
//      :units = "1";
//      :um_stash_source = "m01s09i204";
//      :accum_type = "instantaneous";
//      :coverage_content_type = "modelResult";
//      :grid_type = "spatial";
//      :level_type = "single";
//      :marsParam = 187; // int
//      :grid_mapping = "latitude_longitude";
//      :coordinates = "forecast_reference_time";
//      :_ChunkSizes = 1U, 512U, 427U; // uint
//
//    float hi_cld(time=1, lat=512, lon=427);
//      :standard_name = "high_type_cloud_area_fraction";
//      :long_name = "high cloud cover";
//      :units = "1";
//      :um_stash_source = "m01s09i205";
//      :accum_type = "instantaneous";
//      :coverage_content_type = "modelResult";
//      :grid_type = "spatial";
//      :level_type = "single";
//      :marsParam = 188; // int
//      :grid_mapping = "latitude_longitude";
//      :coordinates = "forecast_reference_time";
//      :_ChunkSizes = 1U, 512U, 427U; // uint
//
//    float ttl_cld(time=1, lat=512, lon=427);
//      :standard_name = "cloud_area_fraction";
//      :long_name = "total cloud cover";
//      :units = "1";
//      :um_stash_source = "m01s09i217";
//      :accum_type = "instantaneous";
//      :coverage_content_type = "modelResult";
//      :grid_type = "spatial";
//      :level_type = "single";
//      :marsParam = 164; // int
//      :grid_mapping = "latitude_longitude";
//      :coordinates = "forecast_reference_time";
//      :_ChunkSizes = 1U, 512U, 427U; // uint
//
//    float av_ttl_cld(time=1, lat=512, lon=427);
//      :standard_name = "cloud_area_fraction";
//      :long_name = "average total cloud coverage";
//      :units = "1";
//      :um_stash_source = "m01s09i217";
//      :accum_type = "mean";
//      :accum_units = "hrs";
//      :accum_value = 1; // int
//      :coverage_content_type = "modelResult";
//      :grid_type = "spatial";
//      :level_type = "single";
//      :marsParam = 228217; // int
//      :cell_methods = "time: mean";
//      :grid_mapping = "latitude_longitude";
//      :coordinates = "forecast_reference_time";
//      :_ChunkSizes = 1U, 512U, 427U; // uint
//
//    float mslp(time=1, lat=512, lon=427);
//      :standard_name = "air_pressure_at_sea_level";
//      :long_name = "mean sea level pressure";
//      :units = "Pa";
//      :um_stash_source = "m01s16i222";
//      :accum_type = "instantaneous";
//      :coverage_content_type = "modelResult";
//      :grid_type = "spatial";
//      :level_type = "single";
//      :marsParam = 151; // int
//      :grid_mapping = "latitude_longitude";
//      :coordinates = "forecast_reference_time";
//      :_ChunkSizes = 1U, 512U, 427U; // uint
//
//    float av_mslp(time=1, lat=512, lon=427);
//      :standard_name = "air_pressure_at_sea_level";
//      :long_name = "average mean sea level pressure";
//      :units = "Pa";
//      :um_stash_source = "m01s16i222";
//      :accum_type = "mean";
//      :accum_units = "hrs";
//      :accum_value = 1; // int
//      :coverage_content_type = "modelResult";
//      :grid_type = "spatial";
//      :level_type = "single";
//      :marsParam = 228202; // int
//      :cell_methods = "time: mean";
//      :grid_mapping = "latitude_longitude";
//      :coordinates = "forecast_reference_time";
//      :_ChunkSizes = 1U, 512U, 427U; // uint
//
//    float sfc_temp(time=1, lat=512, lon=427);
//      :standard_name = "surface_temperature";
//      :long_name = "surface temperature";
//      :units = "K";
//      :um_stash_source = "m01s00i024";
//      :accum_type = "instantaneous";
//      :coverage_content_type = "modelResult";
//      :grid_type = "spatial";
//      :level_type = "single";
//      :marsParam = 125; // int
//      :grid_mapping = "latitude_longitude";
//      :coordinates = "forecast_reference_time";
//      :_ChunkSizes = 1U, 512U, 427U; // uint
//
//    float abl_ht(time=1, lat=512, lon=427);
//      :standard_name = "atmosphere_boundary_layer_thickness";
//      :long_name = "planetary boundary layer height";
//      :units = "m";
//      :um_stash_source = "m01s00i025";
//      :accum_type = "instantaneous";
//      :coverage_content_type = "modelResult";
//      :grid_type = "spatial";
//      :level_type = "single";
//      :marsParam = 159; // int
//      :grid_mapping = "latitude_longitude";
//      :coordinates = "forecast_reference_time";
//      :_ChunkSizes = 1U, 512U, 427U; // uint
//
//    float lnd_mask(time=1, lat=512, lon=427);
//      :standard_name = "land_binary_mask";
//      :long_name = "land mask";
//      :units = "1";
//      :um_stash_source = "m01s00i030";
//      :accum_type = "instantaneous";
//      :coverage_content_type = "modelResult";
//      :grid_type = "spatial";
//      :level_type = "single";
//      :marsParam = 228172; // int
//      :grid_mapping = "latitude_longitude";
//      :coordinates = "forecast_reference_time";
//      :_ChunkSizes = 1U, 512U, 427U; // uint
//
//    float topog(time=1, lat=512, lon=427);
//      :standard_name = "surface_altitude";
//      :long_name = "topography height";
//      :units = "m";
//      :um_stash_source = "m01s00i033";
//      :accum_type = "instantaneous";
//      :coverage_content_type = "modelResult";
//      :grid_type = "spatial";
//      :level_type = "single";
//      :marsParam = 228156; // int
//      :grid_mapping = "latitude_longitude";
//      :coordinates = "forecast_reference_time";
//      :_ChunkSizes = 1U, 512U, 427U; // uint
//
//    int wrtn_time(time=1);
//      :WARNING = "DEPRECATED, DO NOT USE";
//      :long_name = "time (HHMM) that this segment was written";
//      :_ChunkSizes = 1U; // uint
//
//    int wrtn_date(time=1);
//      :long_name = "date (YYYYMMDD) that this segment was written";
//      :WARNING = "DEPRECATED, DO NOT USE";
//      :_ChunkSizes = 1U; // uint
//
//    int base_date(time=1);
//      :units = "yyyymmdd";
//      :long_name = "base date (YYYYMMDD) of archive file";
//      :WARNING = "DEPRECATED, DO NOT USE";
//      :_ChunkSizes = 1U; // uint
//
//    int base_time(time=1);
//      :units = "hhmm UTC";
//      :long_name = "base time (HHMM) of archive file";
//      :WARNING = "DEPRECATED, DO NOT USE";
//      :_ChunkSizes = 1U; // uint
//
//    int valid_date(time=1);
//      :units = "yyyymmdd";
//      :long_name = "valid date (YYYYMMDD) of this segment";
//      :WARNING = "DEPRECATED, DO NOT USE";
//      :_ChunkSizes = 1U; // uint
//
//    int valid_time(time=1);
//      :units = "hhmm UTC";
//      :long_name = "valid time (HHMM) of this segment";
//      :WARNING = "DEPRECATED, DO NOT USE";
//      :_ChunkSizes = 1U; // uint
//
//    float forc_minutes(time=1);
//      :long_name = "forecast minutes of this segment";
//      :units = "minutes";
//      :WARNING = "DEPRECATED, DO NOT USE";
//      :_ChunkSizes = 1U; // uint
//
//    float forc_hrs(time=1);
//      :long_name = "forecast hours of this segment";
//      :units = "hours";
//      :WARNING = "DEPRECATED, DO NOT USE";
//      :_ChunkSizes = 1U; // uint
//
//    char seg_type(time=1, char_size=4);
//      :long_name = "segment of bmrc header type";
//      :_ChunkSizes = 1U, 4U; // uint
//
//    int char_size(char_size=4);
//      :units = "1";
//      :long_name = "char_size";
//
//    double time_0(time=1);
//      :axis = "T";
//      :units = "days since 2019-10-08 12:00:00";
//      :standard_name = "time";
//      :long_name = "time";
//      :calendar = "gregorian";
//      :bounds = "time_0_bnds";
//      :_ChunkSizes = 1U; // uint
//
//    double time_0_bnds(time=1, bnds=2);
//      :_ChunkSizes = 1U, 2U; // uint
//
//  // global attributes:
//  :date_created = "2019-10-09T02:54:52";
//  :expt_id = "0001";
//  :institution = "Australian Bureau of Meteorology";
//  :license = "The Bureau of Meteorology licences this data under its Express Licence Agreement";
//  :modl_vrsn = "ACCESS-G";
//  :source = "ACCESS-G/GE3 v3.0.0";
//  :standard_name_vocabulary = "CF Standard Name Table v67";
//  :stash_vocabulary = "http://reference.metoffice.gov.uk/um/stash";
//  :summary = "Global Numerical Weather Prediction Data";
//  :title = "ACCESS-Global Model Forecast";
//  :Conventions = "CF-1.6 ACDD-1.3";
//}
	
	
	
//	public void read990mLat()
//	{
//	
//		
//		final int xSize = 109;
//		final int ySize = 99;
//		
//		NetcdfFile ncfile = null;
//		try
//		{
//			ncfile = NetcdfFile.open(filename);
//			
//			
//			String band1 = "Band1";
//			Variable band1Var = ncfile.findVariable(band1);
//			Array band1Data = band1Var.read();
//			//NCdump.printArray(band1Data, band1, System.out, null);
//			
//			//System.out.println(band1Data.getSize());
//			
//			int band1Count =0;
//			for (int y=0;y<ySize;y++)
//			
//			{				
//				for (int x=0;x<xSize;x++)	
//				{
//					String valueStr = "   ";
//					float value = band1Data.getFloat(band1Count);
//					if (value == Float.NaN)
//					{
//						valueStr = "  ";
//					}
//					else
//					{						
//					    value = (int)Math.round(value * 100);
//					    valueStr = new Integer((int)value).toString();
//					    if (valueStr.equals("0"))
//					    {
//					    	valueStr = "  ";
//					    }
//					}					
//					
//					//valueStr = getLatLong(x, y);
//					
//					float tempValue = temperatureData.getDatafileData(time, getLatitude(x, y), getLongitude(x, y), variableType);
//					if (valueStr.equals("  "))
//					{
//						
//					}
//					else 
//					{
//						int tempValueInt = Math.round(tempValue);
//						//valueStr = "" + round(tempValue, 0);	
//						valueStr = "" + tempValueInt;
//					}
//					
//					
//					
//					if (x == xSize -1 )
//					{
//						System.out.println(valueStr);	
//					}
//					else 
//					{
//						System.out.print(valueStr + " ");	
//					}
//										
//					band1Count ++;
//				}
//				
//
//			}
//			public float getLatitude(int x, int y)
//			{
//				float longitude = (float) (west + (xGradient * x));
//				float latitude = (float) (north + (yGradient * y));
//				longitude = (float) round(longitude, 2);		
//				latitude = (float) round(latitude, 2);
//				
//				return latitude;
//			}
//			
//			public float getLongitude(int x, int y)
//			{
//				float longitude = (float) (west + (xGradient * x));
//				float latitude = (float) (north + (yGradient * y));
//				longitude = (float) round(longitude, 2);		
//				latitude = (float) round(latitude, 2);
//				
//				return longitude;
//			}	
		
	public ArrayList<String> getZones()
	{
		return zones;
	}



	public void setZones(ArrayList<String> zones)
	{
		this.zones = zones;
	}



	public ArrayList<double[]> getLatLontoLCMap()
	{
		return latLontoLCMap;
	}



	public void setLatLontoLCMap(ArrayList<double[]> latLontoLCMap)
	{
		this.latLontoLCMap = latLontoLCMap;
	}
	
	
}
