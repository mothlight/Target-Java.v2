package Simpel;


import java.util.ArrayList;
import java.util.TreeMap;

public class SimpelConstants 
{
	
	
	public final static int PRECIPITATION=0; 
	public final static int SNOW_WATER_EQUI=1; 
	public final static int SNOW_MELT_RAIN=2;
    public final static int ETP_COEFF=3; 
    public final static int ETP_INPUT=4; 
    public final static int LAI=5; 
    public final static int I_CAP=6; 
    public final static int INT_ETI_LEAF=7;
    public final static int I_BAL=8;
    public final static int I_PREC=9;
    public final static int I_REM=10; 
    public final static int I_ETI_LITTER=11; 
    public final static int BILANZ=12; 
    public final static int CONTENT=13; 
    public final static int S_RESTN=14; 
    public final static int INF_LIMIT=15; 
    public final static int P_LINF=16; 
    public final static int REST_ETA=17; 
    public final static int BALANCE_SOIL=18;
    public final static int ETA=19; 
    public final static int ET_BALANCE=20; 
    public final static int SEEPAGE=21;	
    public final static int STORAGE=22;	
    public final static int SURFACE_RUNOFF=23;
    public final static int RUNOFF_TOTAL=24; 
    public final static int I_LEAF=25;	
    public final static int I_LITTER=26;	
    public final static int ETA_TOTAL=27;	
 
    
    public final static int K_DOWN=28;	
    public final static int WIND_SPEED=29;	
    public final static int L_DOWN=30;	
        
    public final static int WATER_BALANCE=31;	
    public final static int SUM_PREC=32;	
    public final static int SUM_ETR=33;	
    public final static int SUM_RUNOFF=34;	
    public final static int INIT_SWE=35;	
    public final static int INIT_STOR=36;	
    public final static int numberOfModelOutputs = INIT_STOR+1;
    
    
    public final static int LANDUSE_DAY_DEGREE_LINE = 16;
    
    public static int INPUT_DATE=0;
    public static int INPUT_DOY=1;	
    public static int INPUT_P=2;	
    public static int INPUT_T14=3;	
    public static int INPUT_R14=4;	
    public static int INPUT_ET0=5;    
    public static int INPUT_K_DOWN=6;
    public static int INPUT_WIND_SPEED=7;
    public static int INPUT_L_DOWN=8;
    public static int INPUT_MONTH=9;
    public static int INPUT_HOUR=10;
    public static int INPUT_IRR=11;
    
    
    
    public final static int LAI_IND = 0;
    public final static int LAI_DOY = 1;
    public final static int LAI_PHASE = 2;
    public final static int LAI_LAI = 3;
    public final static double SECONDS_PER_DAY = 86400.;
    
    
    
    public final static String FIELD_CAPACITY_PERCENT = "Field Capacity %";
    public final static String PERMANENT_WILTING_POINT = "Permanent Wilting Point %";
    public final static String START_OF_REDUCTION_PERCENT = "Start of Reduction %";
    public final static String ROOT_DEPTH = "Root Depth";
    public final static String INIT_VALUE_SOIL_PERCENT = "Init-Value Soil %";
    //public final static String FIELD_CAPACITY = "Field Capacity";
    //public final static String PERM_WILTING_POINT = "Perm. Wilting Point";
    //public final static String FWC = "FWC";
    //public final static String START_OF_REDUCTION = "Start of Reduction";
    //public final static String INIT_VALUE_SOIL = "Init-Value Soil";
    //public final static String DEPTH_OF_SOIL = "Depth of soil";	
    public final static String LAND_USE = "Land use";
    public final static String MINIMUM_LAI = "Minimum LAI";
    public final static String MAXIMUM_LAI = "Maximum LAI";
    public final static String INTC_COVERED_FRACTION = "Vegetation Fraction";
    public final static String INTC_LAYER_THICKNESS = "Layer Thickness";
    public final static String INTC_DRAINAGE_EXP_B = "Drainage Coeff. b";
    public final static String INTC_DRAINAGE_MAX = "Max. Drainage Rate";
    public final static String DIRECT_RUNOFF_FACTOR = "Direct runoff factor";				
    public final static String GLUGLA_C = "Glugla coeff.";
    //public final static String LAMBDA = "Lambda";	
    public final static String CAP_LITTER = "Cap. Litter";
    public final static String INIT_VALUE_LITTER = "Init-Value Litter";
    public final static String LITTER_REDUCTION_FACTOR = "Litter Reduction factor";	
    public final static String TIMESTEP = "Timestep";	
    
    public final static int landuse_beech = 0;	
    public final static int landuse_corn = 1;
    public final static int landuse_grassland	= 2;
    public final static int landuse_spruce = 3;
    public final static int landuse_sugar_beet = 4;
    public final static int landuse_urban = 5;
    public final static int landuse_wiwheat = 6;
    
    public final String tab = "\t";
    public final String linefeed = "\n";
	
	public static TreeMap<String,Double> Soil = new TreeMap<String,Double>()	
	{
		private static final long serialVersionUID = 1L;
		{
			this.put("Timestep",1.);
			this.put("Field Capacity %",45.);
			this.put("Permanent Wilting Point %",8.0);
			this.put("Start of Reduction %",30.);
			this.put("Root Depth",20.);
			this.put("Init-Value Soil %",10.);
			this.put("Land use",landuse_grassland+0.0);
			this.put("Minimum LAI",2.);
			this.put("Maximum LAI",2.);
			this.put("Vegetation Fraction",0.75);
			this.put("Layer Thickness",0.35);
			this.put("Drainage Coeff. b",3.7);
			this.put("Max. Drainage Rate",2.88);
			this.put("Cap. Litter",0.);
			this.put("Init-Value Litter",0.);
			this.put("Litter Reduction factor",3.);
			this.put("Direct runoff factor",0.);				
			this.put("Glugla coeff.",0.);
		}
	};
	
	public static ArrayList<String[]> Landuse = new ArrayList<String[]>()
	{
		private static final long serialVersionUID = 1L;
		{
			this.add(new String[] {"1","0.10","0.11","0.20","0.10","0.14","0.10","0.18"});
			this.add(new String[] {"2","0.10","0.11","0.21","0.10","0.14","0.11","0.18"});
			this.add(new String[] {"3","0.10","0.11","0.21","0.10","0.14","0.11","0.19"});
			this.add(new String[] {"4","0.10","0.17","0.29","0.30","0.15","0.15","0.26"});
			this.add(new String[] {"5","0.23","0.21","0.29","0.39","0.23","0.15","0.34"});
			this.add(new String[] {"6","0.28","0.24","0.28","0.33","0.30","0.14","0.38"});
			this.add(new String[] {"7","0.32","0.25","0.26","0.31","0.36","0.13","0.34"});
			this.add(new String[] {"8","0.26","0.26","0.25","0.25","0.32","0.13","0.22"});
			this.add(new String[] {"9","0.17","0.21","0.23","0.25","0.26","0.12","0.21"});
			this.add(new String[] {"10","0.10","0.18","0.22","0.22","0.19","0.11","0.20"});
			this.add(new String[] {"11","0.10","0.11","0.20","0.10","0.14","0.10","0.18"});
			this.add(new String[] {"12","0.10","0.11","0.20","0.10","0.14","0.10","0.18"});
			this.add(new String[] {"Direct_runoff_factor","0","20","15","0","20","50","20"});
			this.add(new String[] {"LAImin","2.50","0.50","1.00","7.00","0.50","0.10","0.50"});
			this.add(new String[] {"LAImax","6.00","3.00","2.00","7.00","3.00","0.50","3.00"});
			this.add(new String[] {"Litter_cap","1.50","0.00","0.00","2.00","0.00","0.00","0.00"});
			this.add(new String[] {"Day_degree","2.40","3.00","3.00","1.80","3.00","5.00","3.00"});
		}
	};
	
	public static ArrayList<int[]> LAI_model = new ArrayList<int[]>()
	{
		private static final long serialVersionUID = 1L;
		{
			this.add(new int[] {1,121,1}); 
			this.add(new int[] {2,135,2}); 
			this.add(new int[] {3,305,2}); 
			this.add(new int[] {4,319,1}); 
		}
	};
	

}
