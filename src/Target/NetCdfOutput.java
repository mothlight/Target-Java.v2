package Target;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import ucar.ma2.Array;
import ucar.ma2.ArrayDouble;
import ucar.ma2.DataType;
import ucar.ma2.InvalidRangeException;
import ucar.nc2.Attribute;
import ucar.nc2.Dimension;
import ucar.nc2.NetcdfFileWriter;
import ucar.nc2.Variable;

public class NetCdfOutput
{
	NetcdfFileWriter writer = null;
	public static final String UTB = "Utb";
	public static final String FID = "Fid";
	public static final String MODUTAREF = "modUTaRef";	
	public static final String TBRUR = "TbRur";
	public static final String HTTCCAN = "HttcCan";
	public static final String HTTCURBNEW = "HttcUrbNew";
	public static final String TSURFWALL = "TsurfWall";
	public static final String TSURFCAN = "TsurfCan";
	public static final String TSURFHORZ = "TsurfHorz";
	public static final String UCAN = "Ucan";
	public static final String PET = "Pet";
	
	private boolean disableUtb = false;
	private boolean disableFid = false;
	private boolean disableModUTaRef = false;	
	private boolean disableTbRur = false;
	private boolean disableHttcCan = false;
	private boolean disableHttcUrbNew = false;
	private boolean disableTsurfWall = false;
	private boolean disableTsurfCan = false;
	private boolean disableTsurfHorz = false;
	private boolean disableUcan = false;
	private boolean disablePet = false;
	
	private boolean individualNetcdfFiles = false;
	private long simulationStartTimeLong = 0;
	
	Common common = new Common();

	public static void main(String[] args)
	{
		
	}
	

	public void setDisabled(String[] disabled)
	{
		for (String value : disabled)
		{
			switch(value)
			{
				case UTB: 
					disableUtb = true;
					break;
				case FID:
					disableFid = true;
					break;
				case MODUTAREF:
					disableModUTaRef = true;
					break;
				case TBRUR:
					disableTbRur = true;
					break;
				case HTTCCAN:
					disableHttcCan = true;
					break;
				case HTTCURBNEW:
					disableHttcUrbNew = true;
					break;
				case TSURFWALL:
					disableTsurfWall = true;
					break;
				case TSURFCAN:
					disableTsurfCan = true;
					break;
				case TSURFHORZ:
					disableTsurfHorz = true;
					break;
				case UCAN:
					disableUcan = true;
					break;
				case PET:
					disablePet = true;
					break;
			}
				
		}
	}
	
	public void outputNetcdf2(String filename, int latX, int lonY, 
			ArrayList<HashMap<Integer,Double>> mod_rslts,
			ArrayList<HashMap<Integer,Double>> mod_rslts_tmrt_utci, 
			int timeIdx, int timestep, String date1ADateStr,
			double latEdge, double latResolution, double lonEdge, double lonResolution) 
	{
		int minutes = (int)timestep / 60;

		if (isIndividualNetcdfFiles())
		{
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
		    
		    String currentSimulationTimeStr = year + "-" + monthStr + "-" + dayStr + "_" +hourStr + "" +minuteStr;
			
			filename = filename.replace(".nc", "_"
					+ currentSimulationTimeStr
					+ ".nc");
		}
		
		NetcdfFileWriter writer = null;
		try
		{
			writer = NetcdfFileWriter.createNew(NetcdfFileWriter.Version.netcdf3, filename);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		// define dimensions, including unlimited
		Dimension latDim = writer.addDimension(null, "lat", latX);
		Dimension lonDim = writer.addDimension(null, "lon", lonY);
		Dimension timeDim = writer.addUnlimitedDimension("time");

		// define Variables
		Variable lat = writer.addVariable(null, "lat", DataType.FLOAT, "lat");
		lat.addAttribute(new Attribute("units", "degrees_north"));
		Variable lon = writer.addVariable(null, "lon", DataType.FLOAT, "lon");
		lon.addAttribute(new Attribute("units", "degrees_east"));

		Variable airTemp = writer.addVariable(null, "TEMP_AIR", DataType.DOUBLE, "time lat lon");
		airTemp.addAttribute(new Attribute("long_name", "air temperature"));
		airTemp.addAttribute(new Attribute("units", "degC"));
		Variable time = writer.addVariable(null, "time", DataType.INT, "time");		
		time.addAttribute(new Attribute("units", "minutes since "
				//+ "1990-01-01"
				+ date1ADateStr
				));
		
		Variable tempmrt = writer.addVariable(null, "Tmrt", DataType.DOUBLE, "time lat lon");
		tempmrt.addAttribute(new Attribute("long_name", "mean radiant temperature"));
		tempmrt.addAttribute(new Attribute("units", "degC"));
		
		Variable utci = writer.addVariable(null, "UTCI", DataType.DOUBLE, "time lat lon");
		utci.addAttribute(new Attribute("long_name", "UTCI temperature"));
		utci.addAttribute(new Attribute("units", "degC"));
		
		Variable qe = writer.addVariable(null, "Qe", DataType.DOUBLE, "time lat lon");
		qe.addAttribute(new Attribute("long_name", "Latent heat flux"));
		qe.addAttribute(new Attribute("units", "W/m2"));
		
		Variable qh = writer.addVariable(null, "Qh", DataType.DOUBLE, "time lat lon");
		qh.addAttribute(new Attribute("long_name", "Sensible heat flux"));
		qh.addAttribute(new Attribute("units", "W/m2"));
		
		Variable qg = writer.addVariable(null, "Qg", DataType.DOUBLE, "time lat lon");
		qg.addAttribute(new Attribute("long_name", "Ground heat flux"));
		qg.addAttribute(new Attribute("units", "W/m2"));
		
		Variable rn = writer.addVariable(null, "Rn", DataType.DOUBLE, "time lat lon");
		rn.addAttribute(new Attribute("long_name", "Net energy"));
		rn.addAttribute(new Attribute("units", "W/m2"));
		
		Variable pet = null;
		if (!disablePet)
		{
			pet = writer.addVariable(null, "PET", DataType.DOUBLE, "time lat lon");
			pet.addAttribute(new Attribute("long_name", "PET temperature"));
			pet.addAttribute(new Attribute("units", "degC"));
		}

		Variable ucan = null;
		if (!disableUcan)
		{
			ucan = writer.addVariable(null, "UCAN", DataType.DOUBLE, "time lat lon");
			ucan.addAttribute(new Attribute("long_name", "Wind speed canopy"));
			ucan.addAttribute(new Attribute("units", "m/s"));
		}

		Variable tsurfHorz = null;
		if (!disableTsurfHorz)
		{
			tsurfHorz = writer.addVariable(null, "TSURF_HORZ", DataType.DOUBLE, "time lat lon");
			tsurfHorz.addAttribute(new Attribute("long_name", "Surface temperature horizontal"));
			tsurfHorz.addAttribute(new Attribute("units", "degC"));
		}

		Variable tsurfCan = null;
		if (!disableTsurfCan)
		{
			tsurfCan = writer.addVariable(null, "TSURF_CAN", DataType.DOUBLE, "time lat lon");
			tsurfCan.addAttribute(new Attribute("long_name", "Surface temperature canyon"));
			tsurfCan.addAttribute(new Attribute("units", "degC"));
		}
		
		Variable tsurfWall = null;
		if (!disableTsurfWall)
		{
			tsurfWall = writer.addVariable(null, "TSURF_WALL", DataType.DOUBLE, "time lat lon");
			tsurfWall.addAttribute(new Attribute("long_name", "Surface temperature wall"));
			tsurfWall.addAttribute(new Attribute("units", "degC"));
		}

		Variable httcUrbNew = null;
		if (!disableHttcUrbNew)
		{
			httcUrbNew = writer.addVariable(null, "httcUrbNew", DataType.DOUBLE, "time lat lon");
			httcUrbNew.addAttribute(new Attribute("long_name", "heat transfer coefficient for convection"));
			httcUrbNew.addAttribute(new Attribute("units", "W/(m2•K)"));
		}

		Variable httcCan = null;
		if (!disableHttcCan)
		{
			httcCan = writer.addVariable(null, "httcCan", DataType.DOUBLE, "time lat lon");
			httcCan.addAttribute(new Attribute("long_name", "heat transfer coefficient for convection"));
			httcCan.addAttribute(new Attribute("units", "W/(m2•K)"));
		}

		Variable tbRur = null;
		if (!disableTbRur)
		{
			tbRur = writer.addVariable(null, "tbRur", DataType.DOUBLE, "time lat lon");
			tbRur.addAttribute(new Attribute("long_name", "Richardson's number eq for \"high temperature\" aka Tb_rur "));
			tbRur.addAttribute(new Attribute("units", "-"));
		}
		
		Variable modUTaRef = null;
		if (!disableModUTaRef)
		{
			modUTaRef = writer.addVariable(null, "modUTaRef", DataType.DOUBLE, "time lat lon");
			modUTaRef.addAttribute(new Attribute("long_name", "uTopHeight"));
			modUTaRef.addAttribute(new Attribute("units", "m"));
		}

		Variable utb = null;
		if (!disableUtb)
		{
			utb = writer.addVariable(null, "utb", DataType.DOUBLE, "time lat lon");
			utb.addAttribute(new Attribute("long_name", "utb"));
			utb.addAttribute(new Attribute("units", "-"));
		}
		Variable fid = null;
		if (!disableFid)
		{
			fid = writer.addVariable(null, "fid", DataType.DOUBLE, "time lat lon");
			fid.addAttribute(new Attribute("long_name", "fid"));
			fid.addAttribute(new Attribute("units", "-"));
		}

		// create the file
		try
		{
			writer.create();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
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
	
		try
		{
			writer.write(lat, Array.factory(latArray));
			writer.write(lon, Array.factory(lonArray));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (InvalidRangeException e)
		{
			e.printStackTrace();
		}

		ArrayDouble.D3 tempData = new ArrayDouble.D3(1, latDim.getLength(), lonDim.getLength());
		ArrayDouble.D3 utciData = new ArrayDouble.D3(1, latDim.getLength(), lonDim.getLength());
		ArrayDouble.D3 tmrtData = new ArrayDouble.D3(1, latDim.getLength(), lonDim.getLength());
		ArrayDouble.D3 petData =  new ArrayDouble.D3(1, latDim.getLength(), lonDim.getLength());
		
		ArrayDouble.D3 qeData = new ArrayDouble.D3(1, latDim.getLength(), lonDim.getLength());
		ArrayDouble.D3 qhData = new ArrayDouble.D3(1, latDim.getLength(), lonDim.getLength());
		ArrayDouble.D3 qgData = new ArrayDouble.D3(1, latDim.getLength(), lonDim.getLength());
		ArrayDouble.D3 rnData = new ArrayDouble.D3(1, latDim.getLength(), lonDim.getLength());
		
		ArrayDouble.D3 ucanData = new ArrayDouble.D3(1, latDim.getLength(), lonDim.getLength());
		ArrayDouble.D3 tsurfHorzData = new ArrayDouble.D3(1, latDim.getLength(), lonDim.getLength());
		ArrayDouble.D3 tsurfCanData = new ArrayDouble.D3(1, latDim.getLength(), lonDim.getLength());
		ArrayDouble.D3 tsurfWallData = new ArrayDouble.D3(1, latDim.getLength(), lonDim.getLength());
		ArrayDouble.D3 httcUrbNewData = new ArrayDouble.D3(1, latDim.getLength(), lonDim.getLength());
		ArrayDouble.D3 httcCanData = new ArrayDouble.D3(1, latDim.getLength(), lonDim.getLength());
		ArrayDouble.D3 tbRurData = new ArrayDouble.D3(1, latDim.getLength(), lonDim.getLength());
		ArrayDouble.D3 modUTaRefData = new ArrayDouble.D3(1, latDim.getLength(), lonDim.getLength());
		ArrayDouble.D3 utbData = new ArrayDouble.D3(1, latDim.getLength(), lonDim.getLength());
		ArrayDouble.D3 fidData = new ArrayDouble.D3(1, latDim.getLength(), lonDim.getLength());
		
		Array timeData = Array.factory(DataType.INT, new int[]
		{ 1 });

		int[] origin = new int[]
		{ 0, 0, 0 };
		int[] time_origin = new int[]
		{ 0 };
		
		timeData.setInt(timeData.getIndex(), timeIdx * minutes);

		int count = 0;
		for (int latIdx = 0; latIdx < latDim.getLength(); latIdx++)
		{
			for (int lonIdx = 0; lonIdx < lonDim.getLength(); lonIdx++)
			{
				HashMap<Integer,Double> mod_rslts_grid = mod_rslts.get(count);
				HashMap<Integer,Double> mod_rslts_tmrt_utci_grid = mod_rslts_tmrt_utci.get(count);
				
				double tmrtValue = mod_rslts_tmrt_utci_grid.get(TargetModule.FOR_TAB_UTCI_tmrt_INDEX);
				double utciValue = mod_rslts_tmrt_utci_grid.get(TargetModule.FOR_TAB_UTCI_utci_INDEX);
				
				double qeValue = mod_rslts_tmrt_utci_grid.get(TargetModule.FOR_TAB_UTCI_Qe_INDEX);
				double qhValue = mod_rslts_tmrt_utci_grid.get(TargetModule.FOR_TAB_UTCI_Qh_INDEX);
				double qgValue = mod_rslts_tmrt_utci_grid.get(TargetModule.FOR_TAB_UTCI_Qg_INDEX);
				double rnValue = mod_rslts_tmrt_utci_grid.get(TargetModule.FOR_TAB_UTCI_Rn_INDEX);
				
//				double petValue = mod_rslts_tmrt_utci_grid.get(TargetModule.FOR_TAB_UTCI_PET_INDEX);
				double tacValue = mod_rslts_grid.get(TargetModule.FOR_TAB_Tac_INDEX);
				
				double ucanValue = mod_rslts_grid.get(TargetModule.FOR_TAB_Ucan_INDEX);
				double tsurfHorzValue = mod_rslts_grid.get(TargetModule.FOR_TAB_Tsurf_horz_INDEX);
				double tsurfCanValue = mod_rslts_grid.get(TargetModule.FOR_TAB_Tsurf_can_INDEX);
				double tsurfWallValue = mod_rslts_grid.get(TargetModule.FOR_TAB_Tsurf_wall_INDEX);					
//				double dteValue = mod_rslts_grid.get(TargetModule.FOR_TAB_dte_INDEX);
				double httcUrbNewValue = mod_rslts_grid.get(TargetModule.FOR_TAB_httc_urb_new_INDEX);
				double httcCanValue = mod_rslts_grid.get(TargetModule.FOR_TAB_httc_can_INDEX);
				double tbRurValue = mod_rslts_grid.get(TargetModule.FOR_TAB_Tb_rur_INDEX);
				double modUTaRefValue = mod_rslts_grid.get(TargetModule.FOR_TAB_mod_U_TaRef_INDEX);
				double utbValue = mod_rslts_grid.get(TargetModule.FOR_TAB_UTb_INDEX);
     				
				double fidValue = mod_rslts_grid.get(TargetModule.FOR_TAB_FID_INDEX);

				if (tacValue== -999.0)
				{
					tacValue = Double.NaN;
				}
				if (tmrtValue== -999.0)
				{
					tmrtValue = Double.NaN;
				}
				if (utciValue== -999.0)
				{
					utciValue = Double.NaN;
				}
				
//				if (petValue== -999.0)
//				{
//					petValue = Double.NaN;
//				}
				
				if (ucanValue== -999.0)
				{
					ucanValue = Double.NaN;
				}
				
				if (tsurfHorzValue== -999.0)
				{
					tsurfHorzValue = Double.NaN;
				}
				if (tsurfCanValue== -999.0)
				{
					tsurfCanValue = Double.NaN;
				}
				if (tsurfWallValue== -999.0)
				{
					tsurfWallValue = Double.NaN;
				}
				if (httcUrbNewValue== -999.0)
				{
					httcUrbNewValue = Double.NaN;
				}
				if (httcCanValue== -999.0)
				{
					httcCanValue = Double.NaN;
				}
				if (tbRurValue== -999.0)
				{
					tbRurValue = Double.NaN;
				}
				if (modUTaRefValue== -999.0)
				{
					modUTaRefValue = Double.NaN;
				}
				if (utbValue== -999.0)
				{
					utbValue = Double.NaN;
				}
											
				tempData.set(0, latIdx, lonIdx, tacValue);
				tmrtData.set(0, latIdx, lonIdx, tmrtValue);
				utciData.set(0, latIdx, lonIdx, utciValue);
//				petData.set (0, latIdx, lonIdx, petValue);
				
				qeData.set(0, latIdx, lonIdx, qeValue);
				qhData.set(0, latIdx, lonIdx, qhValue);
				qhData.set(0, latIdx, lonIdx, qhValue);
				rnData.set(0, latIdx, lonIdx, rnValue);
				
				ucanData.set(0, latIdx, lonIdx, ucanValue);
				tsurfHorzData.set(0, latIdx, lonIdx, tsurfHorzValue);
				tsurfCanData.set(0, latIdx, lonIdx, tsurfCanValue);
				tsurfWallData.set(0, latIdx, lonIdx, tsurfWallValue);
				httcUrbNewData.set(0, latIdx, lonIdx, httcUrbNewValue);
				httcCanData.set(0, latIdx, lonIdx, httcCanValue);
				tbRurData.set(0, latIdx, lonIdx, tbRurValue);
				modUTaRefData.set(0, latIdx, lonIdx, modUTaRefValue);
				utbData.set(0, latIdx, lonIdx, utbValue);
				fidData.set(0, latIdx, lonIdx, fidValue);
	
				count ++;
			}
		}

		if (isIndividualNetcdfFiles())
		{
			time_origin[0] = 0;
			origin[0] = 0;
		}
		else
		{
			time_origin[0] = timeIdx;
			origin[0] = timeIdx;
		}
		
		try
		{
			writer.write(airTemp, origin, tempData);
			writer.write(tempmrt, origin, tmrtData);
			writer.write(utci, origin, utciData);
			
			writer.write(qe, origin, qeData);
			writer.write(qh, origin, qhData);
			writer.write(qg, origin, qgData);
			writer.write(rn, origin, rnData);
			
			if (!disableUcan)
			{
				writer.write(ucan, origin, ucanData);
			}
			
			if (!disableTsurfHorz)
			{
				writer.write(tsurfHorz, origin, tsurfHorzData);
			}
			
			if (!disableTsurfCan)
			{
				writer.write(tsurfCan, origin, tsurfCanData);
			}
			
			if (!disableTsurfWall)
			{
				writer.write(tsurfWall, origin, tsurfWallData);
			}
			
			if (!disableHttcUrbNew)
			{
				writer.write(httcUrbNew, origin, httcUrbNewData);
			}
			
			if (!disableHttcCan)
			{
				writer.write(httcCan, origin, httcCanData);
			}
			
			if (!disableTbRur)
			{
				writer.write(tbRur, origin, tbRurData);
			}
			
			if (!disableModUTaRef)
			{
				writer.write(modUTaRef, origin, modUTaRefData);
			}
			
			if (!disableUtb)
			{
				writer.write(utb, origin, utbData);
			}
			
			if (!disableFid)
			{
				writer.write(fid, origin, fidData);
			}
			if (!disablePet)
			{
				writer.write(pet, origin, petData);
			}
			
			writer.write(time, time_origin, timeData);
		}
		catch (IOException e)
		{				
			e.printStackTrace();
		}
		catch (InvalidRangeException e)
		{				
			e.printStackTrace();
		}

		try
		{
			writer.close();
		}
		catch (IOException e)
		{			
			e.printStackTrace();
		}
		
	}


	public boolean isIndividualNetcdfFiles()
	{
		return individualNetcdfFiles;
	}


	public void setIndividualNetcdfFiles(boolean individualNetcdfFiles)
	{
		this.individualNetcdfFiles = individualNetcdfFiles;
	}


	public long getSimulationStartTimeLong()
	{
		return simulationStartTimeLong;
	}


	public void setSimulationStartTimeLong(long simulationStartTimeLong)
	{
		this.simulationStartTimeLong = simulationStartTimeLong;
	}

	

	

}
