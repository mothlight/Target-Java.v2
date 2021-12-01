package Target;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class RunToolkit
{
	public double latEdge = 0;
	public double latResolution = 0;
	public double lonEdge = 0;
	public double lonResolution = 0;

	public int y = 0; //lon
	public int x = 0; //lat
	
	Common common = new Common();
	public static String workingDirectory;
//	private boolean usingAccessForcing = false;
	private AccessMetData accessMetData = null;
	
	
	/*
	config file should include:
	
#---------------------------------------------------------------------------------------------------------
####### Example Main Control File #######
#--------------------------------------------------------------------------------------------------------
####### INPUTS #######
site_name=Mawson				     # site name (string)
run_name=MawsonExample                               # run name (string)
inpt_met_file=/home/kerryn/git/Target_Java/example/input/Mawson/MET/Mawson-meteorology_KentTown_30min.csv	# input meteorolgical file (i.e. forcing file)
inpt_lc_file=/home/kerryn/git/Target_Java/example/input/Mawson/LC/100m_lc_grid.csv                        #  input land cover data file
output_dir=/home/kerryn/git/Target_Java/example/output/Mawson                 # directory output will be saved in
date_fmt=%d/%m/%Y %H:%M                              # format of datetime in input met files
timestep=1800S                                       # define in seconds 
include roofs=Y                                      # turn roofs on and off to affect Tac
direct roofs=Y                                       # turn roofs on and off to affect Tac
#--------------------------------------------------------------------------------------------------------
# dates
#---------------------------------------------------------------------------------------------------------
SpinUp=2011,2,14,0					# year,month,day,hour	#start date for simulation (should be a minimum of 24 hours prior to date1)
StartDate =2011,2,15,0					# year,month,day,hour	## the date/time for period of interest (i.e. before this will not be saved)
EndDate =2011,2,16,18 					# year,month,day,hour	# end date for validation period
######################

mod_ldwn=N             # use modelled ldown
lat=-37.8136
domainDim=32,31
latEdge=-34.79829
lonEdge=138.79829
latResolution=0.00088
lonResolution=0.00110
### disabled output options are Fid,Utb,TsurfWall,TsurfCan,TsurfHorz,Ucan,Utb,Tsurfwall,TsurfCan,TsurfHorz,Ucan,Pet
disableOutput=Fid,Utb,TsurfWall,TsurfCan,TsurfHorz,Ucan,Pet

#override default parameters, remove '#' comment to use
#z0m_rur=0.45
#z_URef=10.0
#z_TaRef=2.0
#zavg=4.5
#### options for reference surfaces are Veg, road, watr, conc, dry, irr, and roof
#ref_surf=dry
	 */


	public static void main(String[] args)
	{
		
		RunToolkit runtoolkit = new RunToolkit();
		runtoolkit.run(args);
		
//		This is the main script that runs the toolkit air temperature module 
//
//		Developed by Ashley Broadbent, Andrew Coutts, Matthias Demuzere, and Kerry Nice
//		see ./documents/Toolkit-2_tech-description.docx for full description of the module
//      converted to Java 8
	}
	 public void run(String[] args)
	 {
		 workingDirectory = System.getProperty("user.dir");
 
		String controlFileName=null;
		Cfm cfm = null;  //control file data structure
		//### This is the dictionary that contains all the control file information 
		if (args.length > 0)
		{
			controlFileName = args[0];
			cfm = new Cfm(controlFileName);
		}
		else 
		{
			System.out.println("Usage: Target.RunToolkit /pathto/ControlFile.txt");
			System.exit(1);
		}
				
		String outputDir = cfm.getValue("output_dir");
		String outputFile = outputDir + "/" + cfm.getValue("run_name") + ".nc";
		common.createDirectory(outputDir);	
		
		lonResolution = cfm.getDoubleValue("lonResolution");
		latResolution = cfm.getDoubleValue("latResolution");
		lonEdge = cfm.getDoubleValue("lonEdge");
		latEdge = cfm.getDoubleValue("latEdge");
		
		String domainDim=cfm.getValue("domainDim");
		String[] domainDimSplit = domainDim.split(",");
		y=new Integer(domainDimSplit[0]).intValue();
		x=new Integer(domainDimSplit[1]).intValue();
		
		//		######### DEFINE START AND FINISH DATES HERE ########		
		Date spinUp = cfm.getDateValue("SpinUp");
		Date startDate = cfm.getDateValue("StartDate");
		Date endDate = cfm.getDateValue("EndDate");
		
		String accessNc4File = cfm.getValue("AccessForcingFile");
		if (accessNc4File == null ||  accessNc4File.equals(""))
		{
			
		}
		else
		{
//			usingAccessForcing = true;
			String AccessForcingnorthStr = cfm.getValue("AccessForcingnorth");
			float north = new Float(AccessForcingnorthStr).floatValue();
			String AccessForcingsouthStr = cfm.getValue("AccessForcingsouth");
			float south = new Float(AccessForcingsouthStr).floatValue();
			String AccessForcingwestStr = cfm.getValue("AccessForcingwest");
			float west = new Float(AccessForcingwestStr).floatValue();
			String AccessForcingeastStr = cfm.getValue("AccessForcingeast");
			float east = new Float(AccessForcingeastStr).floatValue();
			String AccessForcingnumLatStr = cfm.getValue("AccessForcingnumLat");
			int numLat = new Integer(AccessForcingnumLatStr).intValue();
			String AccessForcingnumLonStr = cfm.getValue("AccessForcingnumLon");
			int numLon = new Integer(AccessForcingnumLonStr).intValue();			
			String lcLatLonMappingFile = cfm.getValue("AccessForcingMap");								
			accessMetData = new AccessMetData(north, south, west, east, numLat, numLon, 
					accessNc4File, lcLatLonMappingFile, x, y , latEdge, lonEdge, latResolution, lonResolution);
			ArrayList<Long> timesteps = accessMetData.getTimesteps();
//			ArrayList<HashMap<String, HashMap<String, Float>>> gridData = accessMetData.getGridData();
//			HashMap<Integer, int[]> latLonGridMap = accessMetData.getLatLonGridMap();
//			HashMap<String, ArrayList<Integer>> latLonReverseGridMap = accessMetData.getLatLonGridReverseMap();
			
			spinUp = cfm.getDateValue(timesteps.get(0));
			startDate = cfm.getDateValue(timesteps.get(0));
			endDate = cfm.getDateValue(timesteps.get(timesteps.size()-1));
		}
		
		



		HashMap<String,Date> Dats = new HashMap<String,Date>();
		Dats.put("SpinUp", spinUp);
		Dats.put("StartDate", startDate);
		Dats.put("EndDate", endDate);
			
		String lcFilename = cfm.getValue("inpt_lc_file");
	
		LCData lcDataClass = new LCData(lcFilename);
		ArrayList<ArrayList<Double>> lc_data = lcDataClass.getlcData();

		double maxH = lcDataClass.getMaxH();
//		if (maxH > 20)
//		{
//			maxH = lcDataClass.getAveH();
//		}
		double maxW = lcDataClass.getMaxW();

		String metFilename =  cfm.getValue("inpt_met_file");
		MetData metDataClass = new MetData(metFilename, cfm.getValue("mod_ldwn"), spinUp, endDate);
		ArrayList<ArrayList<Object>> met_data = metDataClass.getMetData();
		TargetModule tkmd = new TargetModule(workingDirectory);
		tkmd.setOutputDirectory(outputDir);
		tkmd.modelRun(cfm, lc_data, met_data, Dats, maxH, maxW, x, y, latEdge, latResolution, lonEdge, lonResolution, outputFile, accessMetData);
	}

}
