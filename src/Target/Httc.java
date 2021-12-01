package Target;

import java.util.ArrayList;
import java.util.HashMap;

public class Httc
{
//	""""
//
//	#Mascart (1995) BLM heat and momentum transfer coefficients
//
//	"""
//
//	import math
	
	public static final String HTTC_KEY = "httc";
	public static final String FH_KEY = "Fh";

	public HashMap<String,Double> httc(double Ri,double u,double z,double z0m,double z0h,ArrayList<ArrayList<Object>> met,int i,double Tlow,double Thi)
	{
		HashMap<String,Double> returnValues = new HashMap<String,Double>();

		double Fh;

		
		ArrayList<Object> met0 = met.get(i);			
		double metTa0 = (double)met0.get(MetData.Ta);
		double metP0 = (double)met0.get(MetData.P);
		
	    double rho = metP0*100./287.04/(metTa0+273.15);

//	    # from Louis (1979):
	    double R=0.74;

//	    # checks: Mascart procedure not so good if these to conditions
//	    # are not met (i.e. z0m/z0h must be between 1 and 200)
	    z0h=Math.max(z0m/200.,z0h);
	    double mu=Math.max(0.,(Math.log(z0m/z0h)));

	    
	    double Cstarh=3.2165+4.3431*mu+0.536*Math.pow(mu,2)-0.0781*Math.pow(mu,3);
	    double ph=0.5802-0.1571*mu+0.0327*Math.pow(mu,2)-0.0026*Math.pow(mu,3);

	    double lnzz0m=Math.log(z/z0m);
	    double lnzz0h=Math.log(z/z0h);
	    double aa=Math.pow((0.4/lnzz0m),2);

	    double Ch=Cstarh*aa*9.4*(lnzz0m/lnzz0h)*Math.pow((z/z0h),ph);

	    if (Ri > 0.)
	    {
	        Fh=lnzz0m/lnzz0h*Math.pow((1.+4.7*Ri),(-2));
	    }
	    else
	    {
	        Fh=lnzz0m/lnzz0h*(1.-9.4*Ri/(1.+Ch*Math.pow((Math.abs(Ri)),(0.5))));
	    }

	    double httc_out=u*aa/R*Fh; 

	    

	    returnValues.put(HTTC_KEY,httc_out);
	    returnValues.put(FH_KEY,Fh);
	    return returnValues;
	}

	public HashMap<String,Double> httc(double Ri,double u,double z,double z0m,double z0h,double metTa0, double metP0,double Tlow,double Thi)
	{
		HashMap<String,Double> returnValues = new HashMap<String,Double>();

		double Fh;

		
//		ArrayList<Object> met0 = met.get(i);			
//		double metTa0 = (double)met0.get(MetData.Ta);
//		double metP0 = (double)met0.get(MetData.P);
		
	    double rho = metP0*100./287.04/(metTa0+273.15);

//	    # from Louis (1979):
	    double R=0.74;

//	    # checks: Mascart procedure not so good if these to conditions
//	    # are not met (i.e. z0m/z0h must be between 1 and 200)
	    z0h=Math.max(z0m/200.,z0h);
	    double mu=Math.max(0.,(Math.log(z0m/z0h)));

	    
	    double Cstarh=3.2165+4.3431*mu+0.536*Math.pow(mu,2)-0.0781*Math.pow(mu,3);
	    double ph=0.5802-0.1571*mu+0.0327*Math.pow(mu,2)-0.0026*Math.pow(mu,3);

	    double lnzz0m=Math.log(z/z0m);
	    double lnzz0h=Math.log(z/z0h);
	    double aa=Math.pow((0.4/lnzz0m),2);

	    double Ch=Cstarh*aa*9.4*(lnzz0m/lnzz0h)*Math.pow((z/z0h),ph);

	    if (Ri > 0.)
	    {
	        Fh=lnzz0m/lnzz0h*Math.pow((1.+4.7*Ri),(-2));
	    }
	    else
	    {
	        Fh=lnzz0m/lnzz0h*(1.-9.4*Ri/(1.+Ch*Math.pow((Math.abs(Ri)),(0.5))));
	    }

	    double httc_out=u*aa/R*Fh; 

	    

	    returnValues.put(HTTC_KEY,httc_out);
	    returnValues.put(FH_KEY,Fh);
	    return returnValues;
	}

}
