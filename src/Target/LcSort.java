package Target;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.HashMap;

public class LcSort
{
//	""""
//
//	This function takes land cover data and averages/sorts etc.
//
//	It also calculates SVFs
//
//	            #       0       1       2         3        4       5        6      7      
//	      #surfs =   ['roof', 'road' , 'watr', 'conc' , 'Veg'  , 'dry'  , 'irr', 'wall']       ## surfaces types that are modelled.       
//	    
//
//	"""
	
	public static final String LC_KEY ="LC";
	public static final String LC_woRoofAvg_KEY ="LC_woRoofAvg";
	public static final String LC_woRoofAvgN_KEY ="LC_woRoofAvgN";
	public static final String LC_wRoofAvg_KEY ="LC_wRoofavg";
	public static final String H_KEY ="H";
	public static final String W_KEY ="W";
	public static final String Wtree_KEY ="Wtree";
	public static final String fw_KEY ="fw";
	public static final String fg_KEY ="fg";
	
	public static final String svfwA_KEY ="svfwA";
	public static final String svfgA_KEY ="svfgA";


	
	
	public HashMap<String,Object> lc_sort(ArrayList<Double> LC, double H, double W)
	{
		HashMap<String,Object> returnValues = new HashMap<String,Object>();

		//ArrayList<Double> LC_canyon = new ArrayList<Double>();
		
		ArrayList<Double> LC_canyon = new ArrayList<Double>(Arrays.asList(new Double[LC.size()]));
		
		Collections.copy(LC_canyon, LC);
  
	    if (W < 1.0)
	    {
	        W = 1.0;
	    }
	        
	    
	    double cs_res = Constants.cs_res;
	    double tree_area =  ((Math.pow((cs_res),2))*LC.get(LCData.Veg));
	    double tree_width = tree_area/cs_res;
	    
	    double Wtree = W-tree_width;
	    
	    if (Wtree <= 1.0)
	    {
	        Wtree = 1.0;
	    }
	        
	    if (H <= 1.0)
	    {
	        H = 1.0;
	    }
	                        
	    double LCgrndSum = LC.get(LCData.road) + LC.get(LCData.watr) + LC.get(LCData.conc) + LC.get(LCData.dry) + LC.get(LCData.irr);

	    		
	    //# this part add the surface below trees...
	    
	    if (LCgrndSum > 0.)
	    {

	    	LC.set(LCData.road, LC.get(LCData.road)+((LC.get(LCData.road)/LCgrndSum)*LC.get(LCData.Veg)));
	    	LC.set(LCData.watr, LC.get(LCData.watr)+((LC.get(LCData.watr)/LCgrndSum)*LC.get(LCData.Veg)) );
	    	LC.set(LCData.conc, LC.get(LCData.conc)+((LC.get(LCData.conc)/LCgrndSum)*LC.get(LCData.Veg)) );
	    	LC.set(LCData.dry, LC.get(LCData.dry)+((LC.get(LCData.dry)/LCgrndSum)*LC.get(LCData.Veg)) );
	    	LC.set(LCData.irr, LC.get(LCData.irr)+((LC.get(LCData.irr)/LCgrndSum)*LC.get(LCData.Veg)) );
	    }
	    else
	    {
	    	LC.set(LCData.conc, LC.get(LCData.conc) + LC.get(LCData.Veg) );
	    }
	        

		List<Double> LC_woRoofAvg = new ArrayList<Double>(Arrays.asList(new Double[LC.size()]));
		Collections.copy(LC_woRoofAvg, LC);
	    //## copy of LC without roofs 
	     
	    double svfgA = Math.pow((1.0+Math.pow((H/Wtree),2)),0.5)- H/Wtree ;

	    double svfwA = 1./2.*(1.+W/H-Math.pow((1.+Math.pow((W/H),2.)),0.5));
	    
	    double horz_area = cs_res * cs_res ; 
	    double wall_area = 2.*(H/W)*(1.-LC.get(LCData.roof));
	    
	    
	    ArrayList<Double> LC_woRoofAvgN = new ArrayList<Double>(Arrays.asList(new Double[LC.size()]));
		Collections.copy(LC_woRoofAvgN, LC);
		

		ArrayList<Double> LC_wRoofAvg = new ArrayList<Double>(Arrays.asList(new Double[LC.size()]));
		Collections.copy(LC_wRoofAvg, LC);
		//## copy of LC without roofs 
	    
		LC.set(LCData.wall, wall_area);
	    
       
	    if (!(LC_woRoofAvgN.get(LCData.roof) > 0.99))
	    {
	    	LC_woRoofAvgN.set(LCData.roof, 0.0);
	    }
	        
	    double LC_woRoofAvgNSum = sumSurfaces(LC_woRoofAvgN);
	    if (! (LC_woRoofAvgNSum == 0.))
	    {

	    	double value = LC_woRoofAvgN.get(LCData.road);	    	
	    	value = LC_woRoofAvgN.get(LCData.road) ;
	    	LC_woRoofAvgN.set(LCData.road, value/LC_woRoofAvgNSum);
	    	
	    	value = LC_woRoofAvgN.get(LCData.conc) ;
	    	LC_woRoofAvgN.set(LCData.conc, value/LC_woRoofAvgNSum);
	    	
	    	value = LC_woRoofAvgN.get(LCData.dry) ;
	    	LC_woRoofAvgN.set(LCData.dry, value/LC_woRoofAvgNSum);
	    	
	    	value = LC_woRoofAvgN.get(LCData.irr) ;
	    	LC_woRoofAvgN.set(LCData.irr, value/LC_woRoofAvgNSum);
	    	
	    	value = LC_woRoofAvgN.get(LCData.Veg);
	    	LC_woRoofAvgN.set(LCData.Veg, value/LC_woRoofAvgNSum);
	    	
	    	value = LC_woRoofAvgN.get(LCData.watr);
	    	LC_woRoofAvgN.set(LCData.watr, value/LC_woRoofAvgNSum);
	    	

	    }
	    LC_woRoofAvgN.set(LCData.wall, wall_area);
	            
	            
	    double LC_wRoofAvgSum = sumSurfaces(LC_wRoofAvg);

	    if (! (LC_wRoofAvgSum == 0.))
	    {

	        
	    	double value = LC_wRoofAvg.get(LCData.road);	
	    	
	    	value = LC_wRoofAvg.get(LCData.roof) ;
	    	LC_wRoofAvg.set(LCData.roof, value/LC_wRoofAvgSum);
	    	
	    	value = LC_wRoofAvg.get(LCData.road) ;
	    	LC_wRoofAvg.set(LCData.road, value/LC_wRoofAvgSum);
	    	
	    	value = LC_wRoofAvg.get(LCData.conc) ;
	    	LC_wRoofAvg.set(LCData.conc, value/LC_wRoofAvgSum);
	    	
	    	value = LC_wRoofAvg.get(LCData.dry) ;
	    	LC_wRoofAvg.set(LCData.dry, value/LC_wRoofAvgSum);
	    	
	    	value = LC_wRoofAvg.get(LCData.irr) ;
	    	LC_wRoofAvg.set(LCData.irr, value/LC_wRoofAvgSum);
	    	
	    	value = LC_wRoofAvg.get(LCData.Veg);
	    	LC_wRoofAvg.set(LCData.Veg, value/LC_wRoofAvgSum);
	    	
	    	value = LC_wRoofAvg.get(LCData.watr);
	    	LC_wRoofAvg.set(LCData.watr, value/LC_wRoofAvgSum);
	    }

	        
	    int fg=0,fw=0;
	    if (svfgA < 0.1)
	    {
	        fg = 0;
	    }
	    if ((svfgA > 0.1) && (svfgA <= 0.2))
	    {
	        fg = 1;
	    }
	    if ((svfgA > 0.2) && (svfgA <= 0.3))
	    {
	        fg = 2;
	    }
	    if ((svfgA > 0.3) && (svfgA <= 0.4))
	    {
	        fg = 3;
	    }
	    if ((svfgA > 0.4) && (svfgA <= 0.5))
	    {
	        fg = 4;
	    }
	    if ((svfgA > 0.5) && (svfgA <= 0.6))
	    {
	        fg = 5;
	    }
	    if ((svfgA > 0.6) && (svfgA <= 0.7))
	    {
	        fg = 6;
	    }
	    if ((svfgA > 0.7) && (svfgA <= 0.8))
	    {
	        fg = 7;
	    }
	    if ((svfgA > 0.8) && (svfgA <= 0.9))
	    {
	        fg = 8;
	    }
	    if (svfgA > 0.9)
	    {
	        fg = 9;
	    }
	        
	    if (svfwA < 0.1)
	    {
	        fw = 0;
	    }
	    if ((svfwA > 0.1) && (svfwA <= 0.2))
	    {
	        fw = 1;
	    }
	    if ((svfwA > 0.2) && (svfwA <= 0.3))
	    {
	        fw = 2;
	    }
	    if ((svfwA > 0.3) && (svfwA <= 0.4))
	    {
	        fw = 3;
	    }
	    if ((svfwA > 0.4) && (svfwA <= 0.5))
	    {
	        fw = 4;
	    }
	    if ((svfwA > 0.5) && (svfwA <= 0.6))
	    {
	        fw = 5;
	    }
	    if ((svfwA > 0.6) && (svfwA <= 0.7))
	    {
	        fw = 6;
	    }
	    if ((svfwA > 0.7) && (svfwA <= 0.8))
	    {
	        fw = 7;
	    }
	    if ((svfwA > 0.8) && (svfwA <= 0.9))
	    {
	        fw = 8;
	    }
	    if (svfwA > 0.9)
	    {
	        fw = 9;
	    }
	                          
	    
	    returnValues.put(LC_KEY, LC);
	    returnValues.put(LC_woRoofAvg_KEY, LC_woRoofAvg);
	    returnValues.put(LC_woRoofAvgN_KEY, LC_woRoofAvgN);
	    returnValues.put(LC_wRoofAvg_KEY, LC_wRoofAvg);
	    returnValues.put(H_KEY, H);
	    returnValues.put(W_KEY, W);
	    returnValues.put(Wtree_KEY, Wtree);
	    returnValues.put(fw_KEY, fw);
	    returnValues.put(fg_KEY, fg);
	    
	    returnValues.put(svfwA_KEY, svfwA);
	    returnValues.put(svfgA_KEY, svfgA);
	    
	    
	    return returnValues;
	}
	
	public double sumSurfaces(ArrayList<Double> LC)
	{
		double roofArea = LC.get(LCData.roof);
		double LCSum = LC.get(LCData.road) + LC.get(LCData.watr) + LC.get(LCData.conc) + LC.get(LCData.dry) + LC.get(LCData.irr) + LC.get(LCData.Veg) + roofArea;
		
		return LCSum;
		
	}
}
