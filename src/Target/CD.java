package Target;

import java.util.HashMap;

public class CD
{
//	import math
	
	public static final String CD_OUT_KEY = "cd_out";
	public static final String FM_KEY = "Fm";

	public HashMap<String,Double> cd(double Ri,double z,double z0m,double z0h)
	{
//	# inputs
//	#      real Ri,z,z0m,z0h
//	# outputs
//	#      real cd_out,Fm
//	# other variables
//	 #     real mu,Cstarm,pm,lnzz0m,aa,Cm
//
//	# checks: Mascart procedure not so good if these to conditions
//	# are not met (i.e. z0m/z0h must be between 1 and 200)
		
		HashMap<String,Double> returnValues = new HashMap<String,Double>();

	    z0h=Math.max(z0m/200.,z0h);
	    double mu=Math.max(0.,Math.log(z0m/z0h));

	    double Cstarm=6.8741+2.6933*mu-0.3601*Math.pow(mu,2)+0.0154*Math.pow(mu,3);
	    double pm=0.5233-0.0815*mu+0.0135*Math.pow(mu,2)-0.001*Math.pow(mu,3);

	    double lnzz0m=Math.log(z/z0m);
	    double aa=Math.pow((0.4/(lnzz0m)),2);

	    double Cm=Cstarm*aa*9.4*Math.pow((z/z0m),pm);
	    
	    double Fm;

	    if(Ri > 0.)
	    {
	        Fm=Math.pow((1.+4.7*Ri),(-2));
	    }
	    else
	    {
	        Fm=1.-9.4*Ri/(1.+Cm*Math.pow((Math.abs(Ri)),(0.5)));
	    }

	    double cd_out=aa*Fm;
	    
	    returnValues.put(CD_OUT_KEY,cd_out);
	    returnValues.put(FM_KEY,Fm);

//	    return{'cd_out':cd_out, 'Fm':Fm}
	    return returnValues;
	}

}
