package Target;

public class TbRurSolverBisectional
{

	public static final double STUCK_VALUE = 1E-15;
	public static final double ERROR_RETURN = -9999.;
	public static final int ITERATIONS = 400000;
	double a = -1; /* left point */
	double b = 50; /* right point */
	boolean DEBUG=false;
	boolean SKIP=true;
	
	public static void main(String[] args)
	{
		TbRurSolverBisectional t = new TbRurSolverBisectional();
		t.runTests();

	}
	
	public void runTests()
	{
		TbRurSolver_python solverP = new TbRurSolver_python();
		solverP.setWorkingDirectory("/home/kerryn/git/Target_Java/bin");
		
		TbRurSolverBisectional solver = new TbRurSolverBisectional();
		double dz=-1.0;
		double ref_ta = 21.9;
		double UTb = 2.63038178;
		double[] mod_U_TaRef = new double[1];
		mod_U_TaRef[0] = 3.05879268;
		int i=0;
		double Ri_rur = 0.24555776;
		
		if (!SKIP)
		{	
		
		test(dz, ref_ta, UTb, mod_U_TaRef, Ri_rur, 0, solverP, solver);
		
		
		//  ('Tb_rur=', 31.4718836061206, 26, '-1.0_30.7_[ 8.94329807]_[ 10.39989511]_[-0.11332934]', 31.4621231872766)
		dz=-1.0;
		ref_ta = 30.7;
		UTb = 8.94329807;
		mod_U_TaRef = new double[1];
		mod_U_TaRef[0] = 10.39989511;
		Ri_rur = -0.11332934;
		//TODO this one
		test(dz, ref_ta, UTb, mod_U_TaRef, Ri_rur, 0, solverP, solver);
		
//		('Ok calculating Tb_rur', 0.471325730671747*(-19.612*Thi_tb + 594.2436)/(Thi_tb + 30.3) + 0.103263339976444)
//		('Tb_rur=', 30.9943884684212, 24, '-1.0_30.3_[ 8.94329807]_[ 10.39989511]_[-0.10326334]', 30.9846280495772)
		dz=-1.0;
		ref_ta = 30.3;
		UTb = 8.94329807;
		mod_U_TaRef = new double[1];
		mod_U_TaRef[0] = 10.39989511;
		Ri_rur = -0.10326334;
		//TODO this one
		test(dz, ref_ta, UTb, mod_U_TaRef, Ri_rur, 0, solverP, solver);
		
		dz=15.0 ;
		ref_ta =23.5 ;
		UTb =  0.21266443039771887 ;
		mod_U_TaRef = new double[1];
		mod_U_TaRef[0] =  0.1  ;
		Ri_rur = -370.55369919381894;
		//TODO this should be 22.760359533246
		test(dz, ref_ta, UTb, mod_U_TaRef, Ri_rur, 0, solverP, solver);
		
		
		
		dz=0.01  ;
		ref_ta =29.1   ;
		UTb =   6.381580845822257    ;
		mod_U_TaRef = new double[1];
		mod_U_TaRef[0] =5.2911037366041676   ;
		Ri_rur =    0.19807380438759148;	
		test(dz, ref_ta, UTb, mod_U_TaRef, Ri_rur, 0, solverP, solver);
		
		
		
//		Result : Called with i=4815 dz=10.8 ref_ta=0.991 UTb[zone]=2.054333739545617 mod_U_TaRef[i]=0.7588885786582178 Ri_rur=-32.56131218890129 
//				Trying python version=0.584535577047438

		dz=10.8  ;
		ref_ta =0.991   ;
		UTb =   2.054333739545617   ;
		mod_U_TaRef = new double[1];
		mod_U_TaRef[0] =0.7588885786582178   ;
		Ri_rur =  -32.56131218890129 ;	
		test(dz, ref_ta, UTb, mod_U_TaRef, Ri_rur, 0, solverP, solver);
		
		
		}
		
//		Result : Called with i=4816 dz=10.8 ref_ta=-0.0022 UTb[zone]=1.9725614388271884 mod_U_TaRef[i]=0.7286811864165139 Ri_rur=-57.09104260554186 
//				  Python=-9.05060356078974E-4  1
		dz=10.8  ;
		ref_ta =-0.0022   ;
		UTb =  1.9725614388271884   ;
		mod_U_TaRef = new double[1];
		mod_U_TaRef[0] =0.7286811864165139   ;
		Ri_rur =  -57.09104260554186 ;	
		test(dz, ref_ta, UTb, mod_U_TaRef, Ri_rur, 0, solverP, solver);
		
	}
	
	public void test(double dz, double ref_ta, double UTb, double[] mod_U_TaRef, double Ri_rur, int i, TbRurSolver_python solverP, TbRurSolverBisectional solver)
	{
		double Tb_rur = solverP.converge(" "+i+" ", dz+" ", ref_ta+" ", UTb+" ", mod_U_TaRef[i]+" ", Ri_rur+" ");
		System.out.println(Tb_rur);	
		double value = solver.bisectionalSolver(dz, ref_ta, UTb, mod_U_TaRef, i, Ri_rur);
		System.out.println(value);		
		System.out.println("_________________________________");
	}

	public void bisectionTesting()
	{
		int i=0;
		double dz=15.0 ;
		double ref_ta =23.5 ;
		double UTb =  0.21266443039771887 ;
		double[] mod_U_TaRef = new double[1];
		mod_U_TaRef[0] =  0.1  ;
		double Ri_rur = -370.55369919381894;
		//TODO this should be 22.760359533246
		
		double c = bisectionalSolver(dz, ref_ta, UTb, mod_U_TaRef, i, Ri_rur);
	}
	
	// adapted from https://x-engineer.org/bisection-method/
		public double bisectionalSolver(double dz, double ref_ta, double UTb, double[] mod_U_TaRef, int i, double Ri_rur)
	{
		double localA = a;
		double localB = b;
		double TOL = 1.0E-12; /* tolerance */
		double NMAX = ITERATIONS; /* maximum number of iterations */
		double c = 0; /* estimated root */
		int index = 0; /* index */
		int stuckCount = 0;
		 
		c = (localA + localB)/2.0; /* calculate the midpoint */
		 /* Evaluate loop until the result is less than the tolerance
		 * maximum number of iterations is not yet reached*/
		 
		 if (calculateExpression(dz, ref_ta, UTb, mod_U_TaRef, i, Ri_rur, c) == 0)
		 {
		  /* If the first midpoint gives f(c) = 0, c is the root */
//			 System.out.println("root is " + c);
		 }
		 else
		 {
			 while ((Math.abs(calculateExpression(dz, ref_ta, UTb, mod_U_TaRef, i, Ri_rur, c)) > TOL) && (index<=NMAX))
			 {
				 if (sign(calculateExpression(dz, ref_ta, UTb, mod_U_TaRef, i, Ri_rur, c)) == sign(calculateExpression(dz, ref_ta, UTb, mod_U_TaRef, i, Ri_rur, localA)))
				 {
					 /* f(c) has same sign as f(a) */
					 localA = c;
				 }
				 else
				 {
					 /* f(c) has same sign as f(b) */
					 localB = c;
				 } 
				 c = (localA+localB)/2.0; /* midpoint update */
				 double difference = Math.abs(localA-localB);
				 if (DEBUG)
				 {
					 System.out.println( (index+1) + " " + localA + " " + localB + " " + difference + " " + c );
				 }
				
				 //did it get stuck? Try different variations of end points
				 if (difference < STUCK_VALUE  )
				 {					 
					 if (stuckCount > 1)
					 {
						 localA = -1. - Math.random();
						 localB = 1. + Math.random();
					 }					 
					 if (stuckCount > 2)
					 {
						 localA = 0.0-Math.random();
						 localB =  0.0;
					 }
					 if (stuckCount > 3)
					 {				 
						 if (stuckCount % 2 == 0)
						 {
							 localA = -0.001;
							 localB =  0.0-Math.random()/1000.;
						 }
						 else
						 {
							 localA = -0.001;
							 localB =  Math.random();
						 }
					 }
					 if (stuckCount < 1)
					 {
						 c = c - 400;
					 } 
					 stuckCount ++;
				 }

				 index++; /* index increment */
			 }
		 } 
		 
		 if (index>=NMAX)
		 {
			 if (DEBUG)
			 {
				 System.out.println("Root not found " + c + " after " + index + " iterations");	
			 }
			 
			 return ERROR_RETURN;
		 }
		 
		 /* Display results */
		 if (DEBUG)
		 {
			 System.out.println("Root is " + c + " found after " + index + " iterations");		 
		 }
		
		 return c;
	}

	public double calculateExpression(double dz, double ref_ta, double UTb, double[] mod_U_TaRef, int i, double Ri_rur, double Thi_tb)
	{
		
		double expressionValue = 9.806 * dz *(Thi_tb- ref_ta)*2.0/(Thi_tb + ref_ta )/ Math.pow((UTb-mod_U_TaRef[i]),2.0) -  Ri_rur;
		
		return expressionValue;
	}
	
	public double sign(double x)
	{
		return (x > 0) ? 1 : ((x < 0) ? -1 : 0);
	}

	
}


