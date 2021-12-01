package Target;

public class TbRurSolver
{

	public static final double ERROR_RETURN = -9999.;
	public static final int ITERATIONS = 10000;
	
	public static void main(String[] args)
	{
		// TODO Auto-generated method stub
		TbRurSolver solver = new TbRurSolver();
		double dz=-1.0;
		double ref_ta = 21.9;
		double UTb = 2.63038178;
		double[] mod_U_TaRef = new double[1];
		mod_U_TaRef[0] = 3.05879268;
		int i=0;
		double Ri_rur = 0.24555776;
		
		//  ('Tb_rur=', 31.4718836061206, 26, '-1.0_30.7_[ 8.94329807]_[ 10.39989511]_[-0.11332934]', 31.4621231872766)
		dz=-1.0;
		ref_ta = 30.7;
		UTb = 8.94329807;
		mod_U_TaRef = new double[1];
		mod_U_TaRef[0] = 10.39989511;
		Ri_rur = -0.11332934;
		//TODO this one

		
//		('Ok calculating Tb_rur', 0.471325730671747*(-19.612*Thi_tb + 594.2436)/(Thi_tb + 30.3) + 0.103263339976444)
//		('Tb_rur=', 30.9943884684212, 24, '-1.0_30.3_[ 8.94329807]_[ 10.39989511]_[-0.10326334]', 30.9846280495772)
		dz=-1.0;
		ref_ta = 30.3;
		UTb = 8.94329807;
		mod_U_TaRef = new double[1];
		mod_U_TaRef[0] = 10.39989511;
		Ri_rur = -0.10326334;
		//TODO this one

		
//		dz=15.0 ;
//		ref_ta =16.5 ;
//		UTb =  3.975255797000494 ;
//		mod_U_TaRef = new double[1];
//		mod_U_TaRef[0] =  1.8692621937604166  ;
//		Ri_rur = -1.3626911282063752;
		//  15.8356754268024
		
		//15.0 16.5 3.975255797000494 1.8692621937604166 0 -1.3626911282063752
		
		
//		dz=15.0 ;
//		ref_ta =16.8 ;
//		UTb =  6.143577139636226 ;
//		mod_U_TaRef = new double[1];
//		mod_U_TaRef[0] =  2.8888597534372273 ;
//		Ri_rur = -0.5326357056213218;
		//   16.1676869029464
		//  dz + ref_ta + UTb + mod_U_TaRef[i] + i + Ri_rur = 15.0 16.8 6.143577139636226 2.8888597534372273 7 -0.5326357056213218

		


//		dz=15.0 ;
//		ref_ta =16.6 ;
//		UTb =  1.8069344530637708 ;
//		mod_U_TaRef = new double[1];
//		mod_U_TaRef[0] =  0.8496646334718477 ;
//		Ri_rur = -6.956094923672547;
		// 15.8958756964009
		// dz + ref_ta + UTb + mod_U_TaRef[i] + i + Ri_rur = 15.0 16.6 1.8069344530637708 0.8496646334718477 12 -6.956094923672547

		
//		dz=15.0 ;
//		ref_ta =23.5 ;
//		UTb =  0.21266443039771887 ;
//		mod_U_TaRef = new double[1];
//		mod_U_TaRef[0] =  0.1  ;
//		Ri_rur = -370.55369919381894;
//		//TODO this should be 22.760359533246
		
//		dz=15.0 ;
//		ref_ta =24.4 ;
//		UTb =  0.21266443039771887 ;
//		mod_U_TaRef = new double[1];
//		mod_U_TaRef[0] =  0.1 ;
//		Ri_rur = -602.7585857212;
		//  23.1629904161214
		//  15.0 24.4 0.21266443039771887 0.1 38 -602.7585857212
		
//		dz=15.0 ;
//		ref_ta =18.4  ;
//		UTb =  1.445547562190818  ;
//		mod_U_TaRef = new double[1];
//		mod_U_TaRef[0] =  0.6797317066551265  ;
//		Ri_rur = -9.705888769990096;
//		 15.0 18.4 1.445547562190818 0.6797317066551265 59 -9.705888769990096
//		 Python Tb_rur=	17.7014531204627
		
		
		
		
		
		
		
		
//		dz=15.0 ;
//		ref_ta =20.4  ;
//		UTb =   0.7227737817459052  ;
//		mod_U_TaRef = new double[1];
//		mod_U_TaRef[0] =  0.33986585363344246   ;
//		Ri_rur = -22.889685313478864;		
//		15.0 20.4 0.7227737817459052 0.33986585363344246 50 -22.889685313478864
//		Python Tb_rur=	19.9397973578936
		//TODO
		
//		dz=15.0 ;
//		ref_ta =19.6   ;
//		UTb =   0.7227737817459055   ;
//		mod_U_TaRef = new double[1];
//		mod_U_TaRef[0] = 0.33986585363344246  ;
//		Ri_rur = -29.853963929463763;	
//		15.0 19.6 0.7227737817459055 0.33986585363344246 51 -29.853963929463763
//		Python Tb_rur=	19.0252892485087
		
//		dz=15.0 ;
//		ref_ta =18.7   ;
//		UTb =   0.7227737817459053   ;
//		mod_U_TaRef = new double[1];
//		mod_U_TaRef[0] = 0.33986585363344246   ;
//		Ri_rur =  -36.639661688103196;	
//		15.0 18.7 0.7227737817459053 0.33986585363344246 56 -36.639661688103196
//		Python Tb_rur=	18.0292827501739
		
//		15.0 18.6 0.7227737817459053 0.33986585363344246 57 -38.06643036395199
//		Python Tb_rur=	17.9073747333698
		
//		15.0 18.4 0.7227737817459053 0.33986585363344246 58 -39.80777369502297
//		Python Tb_rur=	17.6840887203085
		
//		15.0 18.2 0.21266443039771887 0.1 60 -477.5169573455006
//		Python Tb_rur=	17.4651588495703
		
//		 15.0 18.2 0.7227737817459051 0.33986585363344246 61 -45.041233210330724
//		 Python Tb_rur=	17.4008177671521
		
//		15.0 20.2 0.7227737817459055 0.33986585363344246 100 -27.733286267304386
//		Python Tb_rur=	19.649196768224
		
//		15.0 19.6 0.7227737817459053 0.33986585363344246 107 -28.76141635260905
//		Python Tb_rur=	19.046024362753
		
//		 15.0 18.2 0.21266443039771887 0.1 60 -477.5169573455006
//		 Python Tb_rur=	17.4651588495703
		
		
//		dz=15.0 ;
//		ref_ta =18.2    ;
//		UTb =   0.21266443039771887    ;
//		mod_U_TaRef = new double[1];
//		mod_U_TaRef[0] = 0.1    ;
//		Ri_rur =  -477.5169573455006;	
//		15.0 18.2 0.21266443039771887 0.1 60 -477.5169573455006
//		Python Tb_rur=	17.4651588495703
		
		
		dz=0.01  ;
		ref_ta =29.1   ;
		UTb =   6.381580845822257    ;
		mod_U_TaRef = new double[1];
		mod_U_TaRef[0] =5.2911037366041676   ;
		Ri_rur =    0.19807380438759148;	
		
		TbRurSolver_python solverP = new TbRurSolver_python();
		solverP.setWorkingDirectory("/home/kerryn/git/Target_Java/bin");
		double Tb_rur = solverP.converge(" "+i+" ", dz+" ", ref_ta+" ", UTb+" ", mod_U_TaRef[i]+" ", Ri_rur+" ");
		System.out.println(Tb_rur);	
		
		double value = solver.convergeNewVersion(dz, ref_ta, UTb, mod_U_TaRef, i, Ri_rur);
		System.out.println(value);
		
//		TbRurSolver_python solverPython = new TbRurSolver_python();
//		String workingDirectory = System.getProperty("user.dir");
//		solverPython.setWorkingDirectory(workingDirectory);
//		double value2 = solverPython.converge(i+"", dz+"", ref_ta+"", UTb+"", mod_U_TaRef+"", Ri_rur+"");
//		System.out.println(value2);
		
		
//  ('Tb_rur=', 21.8093381484548, 0, '-1.0_21.9_[ 2.63038178]_[ 3.05879268]_[ 0.24555776]', 21.7995777296108)
//('Tb_rur=', 21.8093381484548, 0, '-1.0_21.9_[ 2.63038178]_[ 3.05879268]_[ 0.24555776]', 21.7995777296108)
//		2009-02-05 00:30:00 1
//		('Tb_rur=', 22.4708725309899, 1, '-1.0_22.6_[ 4.73468721]_[ 5.50582682]_[ 0.10165256]', 22.4611121121459)
//		2009-02-05 01:00:00 2
//		('Tb_rur=', 23.2257456165725, 2, '-1.0_23.4_[ 5.78683992]_[ 6.7293439]_[ 0.08715121]', 23.2159851977285)
//		2009-02-05 01:30:00 3
//		('Error calculating Tb_rur', +inf*(-19.612*Thi_tb + 419.6968)/(Thi_tb + 21.4) - 109.818267569608, ' using 21.795 instead')
//		-1.0_21.4_0.1_[ 0.1]_[ 109.81826757]
//		('Tb_rur=', 23.2257456165725, 3, '-1.0_21.4_0.1_[ 0.1]_[ 109.81826757]', 23.2159851977285)
//		2009-02-05 02:00:00 4
//		('Error calculating Tb_rur', +inf*(-19.612*Thi_tb + 404.0072)/(Thi_tb + 20.6) + 36.7517207549013, ' using 21.795 instead')
//		-1.0_20.6_0.1_[ 0.1]_[-36.75172075]
//		('Tb_rur=', 23.2257456165725, 4, '-1.0_20.6_0.1_[ 0.1]_[-36.75172075]', 23.2159851977285)
//
//
		//2009-02-05 00:00:00 0
		//('Ok calculating Tb_rur', 5.4485254465654*(-19.612*Thi_tb + 429.5028)/(Thi_tb + 21.9) - 0.245557760307039)
		//('Tb_rur=', 21.8093381484548, 0, '-1.0_21.9_[ 2.63038178]_[ 3.05879268]_[ 0.24555776]', 21.7995777296108)
//     9.806 * dz * (Thi_tb - ref_ta) * 2.0 / (Thi_tb + ref_ta) / Math.pow((UTb - mod_U_TaRef[i]), 2.0) - Ri_rur;
//		                                         0.183535899
		
		
//		('Ok calculating Tb_rur', 2.12833025256461*(-19.612*Thi_tb + 382.434)/(Thi_tb + 19.5) + 0.100823358330785)
//		('Tb_rur=', 19.6041915418324, 6, '-1.0_19.5_[ 4.20861085]_[ 4.89406829]_[-0.10082336]', 19.5944311229884)

	}

//	public double solve(double dz, double ref_ta, double UTb, double[] mod_U_TaRef, int i, double Ri_rur)
//	{
//		double Tb_rur_itr = 9.806 * dz * (Thi_tb - ref_ta) * 2.0 / (Thi_tb + ref_ta)
//				/ Math.pow((UTb - mod_U_TaRef[i]), 2.0) - Ri_rur;
//	}
	
	
	
	
	public double converge(double dz, double ref_ta, double UTb, double[] mod_U_TaRef, int i, double Ri_rur)
	{
	
		
//		double dz=-1.0;
//		double ref_ta = 21.9;
//		double UTb = 2.63038178;
//		double mod_U_TaRef = 3.05879268;
//		int i=0;
//		double Ri_rur = 0.24555776;
		
//		1 -1.0 21.9 2.63038178 3.05879268 0.24555776
//		21.7995777279967
		
		//String ExpressionString = generateExpressionStr(dz, ref_ta, UTb, mod_U_TaRef, Ri_rur);
		boolean lowRiRur = false;
		double previousValue = 0.0;
		double testValue = 15.0;
		if (Ri_rur < -400)
		{
			testValue = 17.0;
			lowRiRur = true;
//			System.out.println("Ri_rur is low="+ Ri_rur);
		}
		else if (Ri_rur < -200)
		{
			testValue = 22.0;
			lowRiRur = true;
//			System.out.println("Ri_rur is low="+ Ri_rur);
		}
		else if (Ri_rur < -50)
		{
			testValue = 20.0;
			lowRiRur = true;
//			System.out.println("Ri_rur is low="+ Ri_rur);
		}
		else if (Ri_rur < -44)
		{
			testValue = 17.4;
			lowRiRur = true;
//			System.out.println("Ri_rur is low="+ Ri_rur);
		}
		else if (Ri_rur < -37)
		{
			testValue = 17.9;
			lowRiRur = true;
//			System.out.println("Ri_rur is low="+ Ri_rur);
		}
		else if (Ri_rur < -34)
		{
			testValue = 18.0;
			lowRiRur = true;
//			System.out.println("Ri_rur is low="+ Ri_rur);
		}
		else if (Ri_rur < -28)
		{
			testValue = 19.0;
			lowRiRur = true;
//			System.out.println("Ri_rur is low="+ Ri_rur);
		}
		else if (Ri_rur < -23)
		{
			testValue = 19.0;
			lowRiRur = true;
//			System.out.println("Ri_rur is low="+ Ri_rur);
		}
		else if (Ri_rur < -20)
		{
			testValue = 20.0;
			lowRiRur = true;
//			System.out.println("Ri_rur is low="+ Ri_rur);
		}
		int count = 0;
		while (count < ITERATIONS)
		{
//			double returnValue = eval(testValue, ExpressionString);
			double returnValue = calculateExpression(dz, ref_ta, UTb, mod_U_TaRef[i], Ri_rur, testValue);
			double scale = Math.abs(returnValue/testValue);
			double previousValueDifference =  returnValue - previousValue;
			double scaleIncrement =  0.01;
			if (previousValueDifference < 0)
			{
				scale = scale * -1.;
			}
//			System.out.println(count + "\t" + returnValue + "\t" + testValue + "\t" + scale + "\t" + previousValueDifference);
//			if (Ri_rur < -200 && Math.abs(returnValue) < 50)
//			{
//				scaleIncrement = 0.000001;
//			}
//			if (lowRiRur)
//			{
//				testValue = getAlternativeIncrement(returnValue, testValue, scaleIncrement);
//			}
//			else if (returnValue > 0)
//			{
//				testValue = testValue + (1.0 * scale);		
//			}
//			else
//			{
//				testValue = testValue - (1.0 * scale);	
//			}
			
			if (lowRiRur)
			{
				testValue = getAlternativeIncrement(returnValue, testValue, scaleIncrement, Ri_rur);
			}
			else 
			{
				testValue = testValue - (1.0 * scale);		
			}
			
				
			if (Math.abs(returnValue) < 1.0E-12)
			{
				break;
			}
			if (count == ITERATIONS-1)
			{
				if (lowRiRur && (Math.abs(returnValue) < 1.0E-2))
				{
					//calling it close enough
					break;
				}
				testValue = ERROR_RETURN;
				break;
			}
			
			count ++;
			}
			return testValue;
	}
	
	public double convergeNewVersion(double dz, double ref_ta, double UTb, double[] mod_U_TaRef, int i, double Ri_rur)
	{
	
		
//		double dz=-1.0;
//		double ref_ta = 21.9;
//		double UTb = 2.63038178;
//		double mod_U_TaRef = 3.05879268;
//		int i=0;
//		double Ri_rur = 0.24555776;
		
//		1 -1.0 21.9 2.63038178 3.05879268 0.24555776
//		21.7995777279967
		
		
		double lowestNegativeTestResult = -99999.;
		double lowestPositiveTestResult  = 99999.;
		double previousNegativeTestValue = -3000;
		double previousPositiveTestValue = 3000;
		double testValue=18.0;
		boolean converged = false;
		
		//String ExpressionString = generateExpressionStr(dz, ref_ta, UTb, mod_U_TaRef, Ri_rur);
//		boolean lowRiRur = false;
//		double previousValue = 0.0;
//		double testValue = 15.0;
		

		int count = 0;
		while (count < ITERATIONS)
		{
			
			double testValueDifference = previousNegativeTestValue-previousPositiveTestValue;
//			if (testValueDifference == 0)
//			{
//				if ( calculateExpression(dz, ref_ta, UTb, mod_U_TaRef[i], Ri_rur, testValue) < 0 )
//				{
//					testValue = previousNegativeTestValue - (previousNegativeTestValue * 0.5);
//					previousNegativeTestValue = testValue;
//				}
//				else
//				{
//					testValue = previousPositiveTestValue + (previousPositiveTestValue * 0.5);
//					previousPositiveTestValue = testValue;
//				}
//			}
//			else
//			{
				testValue = (previousNegativeTestValue + previousPositiveTestValue) / 2.0;
//			}
			
			

			count ++;
//			if (count > 10000)
//			{
//				break;
//			}
			double returnValue = calculateExpression(dz, ref_ta, UTb, mod_U_TaRef[i], Ri_rur, testValue);
			
			//converged?
			if (Math.abs(returnValue) < 1.0E-12)
			{
				converged = true;
				break;
			}
			
			
//			if (testValueDifference == 0.0)
//			{
//				previousNegativeTestValue = previousNegativeTestValue + 1.0E-13;
//			}
			
			if (returnValue < 0)
			{
//				if (testValueDifference == 0.0)
//				{
//					previousPositiveTestValue = previousPositiveTestValue -  (0.01 * previousPositiveTestValue);
//				}
				
				if ( returnValue > lowestNegativeTestResult)
				{
					lowestNegativeTestResult = returnValue;
					previousNegativeTestValue = testValue;
					
				}
				else
				{
//					System.out.println("neg difference="+ testValueDifference );
					previousNegativeTestValue = previousNegativeTestValue - (testValueDifference/1.000000001);
				}
			}
			else //return value > 0
			{
//				if (testValueDifference == 0.0)
//				{
//					previousNegativeTestValue = previousNegativeTestValue+ (-.1 *previousNegativeTestValue) ;
//				}
				if ( returnValue < lowestPositiveTestResult)
				{
					lowestPositiveTestResult = returnValue;
					previousPositiveTestValue = testValue;
				}
				else
				{
//					System.out.println("pos difference="+ testValueDifference );
//					previousPositiveTestValue = testValue - (previousPositiveTestValue + previousNegativeTestValue)/4;
					previousPositiveTestValue = previousPositiveTestValue + (testValueDifference/1.000000001);
				}
			}
//			System.out.println(count + "\t" + returnValue + "\t" + lowestNegativeTestResult + "\t" + lowestPositiveTestResult + "\t" 
//					+ testValue + "\t" + previousNegativeTestValue + "\t" + previousPositiveTestValue  );
			
			
		}	
//			double scale = Math.abs(returnValue/testValue);
//			double previousValueDifference =  returnValue - previousValue;
//			double scaleIncrement =  0.01;
//			if (previousValueDifference < 0)
//			{
//				scale = scale * -1.;
//			}
//			if (lowRiRur)
//			{
//				testValue = getAlternativeIncrement(returnValue, testValue, scaleIncrement, Ri_rur);
//			}
//			else 
//			{
//				testValue = testValue - (1.0 * scale);		
//			}
//			
//
//			if (count == ITERATIONS-1)
//			{
//				if (lowRiRur && (Math.abs(returnValue) < 1.0E-2))
//				{
//					//calling it close enough
//					break;
//				}
//				testValue = ERROR_RETURN;
//				break;
//			}
//			
//			count ++;

		    if (converged)
		    {
//			    System.out.println("result=" + testValue + " iterations=" + count);
				return testValue;
		    }
		    else
		    {
		    	System.out.println("ERROR result=" + testValue + " iterations=" + count);
		    	return converge(dz, ref_ta, UTb, mod_U_TaRef, i, Ri_rur);
//				return ERROR_RETURN;
		    }

			
			
	}
	
	
	public double getAlternativeIncrement(double returnValue, double testValue, double scaleIncrement, double Ri_rur)
	{
		double absReturnValue = Math.abs(returnValue);
		double newTestValue = testValue;
		
		if (Ri_rur > -50)
		{
			if (returnValue > 0)
			{
				scaleIncrement = scaleIncrement * -1;
			}

			if (absReturnValue > 10 )
			{
				newTestValue = testValue + scaleIncrement;
			}
			else if (absReturnValue > 5 )
			{
				newTestValue = testValue + scaleIncrement * 0.5;
			}
			else if (absReturnValue > 2.5 )
			{
				newTestValue = testValue + scaleIncrement * 0.25;
			}
			else if (absReturnValue > 1 )
			{
				newTestValue = testValue + scaleIncrement * 0.25 * 0.5;
			}
			else if (absReturnValue > 0.1 )
			{
				newTestValue = testValue + scaleIncrement * 0.25 * 0.5 * 0.5 * 0.5;
			}
			else  if (absReturnValue > 0.001 )
			{
				newTestValue = testValue + scaleIncrement * 0.000001;
			}
			else  
			{
				newTestValue = testValue + scaleIncrement * 0.00000001;
			}
			return newTestValue;
		}
	
		if (Ri_rur > -500)
		{
			
			if (returnValue > 0)
			{
				scaleIncrement = scaleIncrement * -1;
			}

			if (absReturnValue > 10 )
			{
				newTestValue = testValue + scaleIncrement;
			}
			else if (absReturnValue > 5 )
			{
				newTestValue = testValue + scaleIncrement * 0.5;
			}
			else if (absReturnValue > 2.5 )
			{
				newTestValue = testValue + scaleIncrement * 0.25;
			}
			else if (absReturnValue > 1 )
			{
				newTestValue = testValue + scaleIncrement * 0.001;
			}
			else if (absReturnValue > 0.1 )
			{
				newTestValue = testValue + scaleIncrement * 0.00001;
			}
			else  if (absReturnValue > 0.001 )
			{
				newTestValue = testValue + scaleIncrement * 0.0000001;
			}
			else  
			{
				newTestValue = testValue + scaleIncrement * 0.000000001;
			}
//			System.out.println(newTestValue);
			return newTestValue;
		}


		if (returnValue > 0)
		{
			scaleIncrement = scaleIncrement * -1;
		}

		if (absReturnValue > 10 )
		{
			newTestValue = testValue + scaleIncrement;
		}
		else if (absReturnValue > 5 )
		{
			newTestValue = testValue + scaleIncrement * 0.5;
		}
		else if (absReturnValue > 2.5 )
		{
			newTestValue = testValue + scaleIncrement * 0.25;
		}
		else if (absReturnValue > 1 )
		{
			newTestValue = testValue + scaleIncrement * 0.25 * 0.5;
		}
		else if (absReturnValue > 0.1 )
		{
			newTestValue = testValue + scaleIncrement * 0.25 * 0.5 * 0.5 * 0.5;
		}
		else  if (absReturnValue > 0.001 )
		{
			newTestValue = testValue + scaleIncrement * 0.0000001;
		}
		else  
		{
			newTestValue = testValue + scaleIncrement * 0.0000000001;
		}

		return newTestValue;
	}
	
	public double calculateExpression(double dz, double ref_ta, double UTb, double mod_U_TaRef, double Ri_rur, double Thi_tb)
	{
		
		double expressionValue = 9.806 * dz *(Thi_tb- ref_ta)*2.0/(Thi_tb + ref_ta )/ Math.pow((UTb-mod_U_TaRef),2.0) -  Ri_rur;
		
		return expressionValue;
	}

	public double converge_obsolete(double dz, double ref_ta, double UTb, double[] mod_U_TaRef, int i, double Ri_rur)
	{
		boolean bigRi_rur = false;
		boolean mediumRi_rur = false;
		double convergeValue= ERROR_RETURN;
		double converge = 0.000001;
		
		double Thi_tb_prevResult= 0.0;
		double Thi_tb_result=0.0;
//		double Thi_tb_result2=0.0;
		double Thi_tb = 15.0;
//		if (Ri_rur < 0)
//		{
//			Thi_tb = 15.;
//			mediumRi_rur = true;
//		}
//		else 
			if (Ri_rur <  -100)
		{
			Thi_tb = 22.76;
			bigRi_rur = true;
		}
		else if (Ri_rur <  -600)
		{
			Thi_tb = 15;
			bigRi_rur = true;
		}
		double dTPrev = 0.0;
		double increment = 1.0;
		int direction = 1;

		// #Do iteration
		int testno = 1;
		boolean continueLoop = true;
		//double Tglobe = 0.0;
		double factor1 = Math.pow((UTb - mod_U_TaRef[i]), 2.0);
		double factor2 = 9.806 * dz;
		double factor3 = (2.0 * factor2 * ref_ta);
		double factor4 = 2.0 * factor2;

		while (continueLoop)
		{
			testno = testno + 1;
			if (testno > ITERATIONS)
			{
				System.out.println("                      No convergence: values too extreme");
				convergeValue = ERROR_RETURN;
				System.exit(1);
				return convergeValue;
			}
//			Thi_tb_result = 9.806 * dz * (Thi_tb - ref_ta) * 2.0 / (Thi_tb + ref_ta) / Math.pow((UTb - mod_U_TaRef[i]), 2.0) - Ri_rur;
			//Thi_tb_result2 = factor2 * (Thi_tb - ref_ta) * 2.0 / (Thi_tb + ref_ta) / factor1 - Ri_rur;
//			Thi_tb_result2 =  (2.0 * factor2 * Thi_tb - factor3)  / (Thi_tb + ref_ta) / factor1 - Ri_rur;
			Thi_tb_result =  (factor4 * Thi_tb - factor3)  / (Thi_tb + ref_ta) / factor1 - Ri_rur;

			double dT = Thi_tb_result - Thi_tb_prevResult;
			dTPrev = dTPrev - dT;

			System.out.println(Thi_tb_result + " " + Thi_tb_prevResult + " " + dT + " " + dTPrev  + " " + increment + " " + testno + " " + Thi_tb);
			
			if (Math.abs(Thi_tb_result) < converge)
			{
//				System.out.println("Converge " +  dT + " " + testno);
				continueLoop = false;
			}
			else
			{	
				double Thi_tb_result_compare = Math.abs(Thi_tb_result);				
				increment = getIncrement(Thi_tb_result_compare);	
				if (bigRi_rur)
				{
					increment = getIncrement3(Thi_tb_result, Thi_tb, Thi_tb_prevResult);	
				}
				if (mediumRi_rur)
				{
					increment = getIncrement4(Thi_tb_result, Thi_tb, Thi_tb_prevResult);	
				}
				if (dT>0)
				{
					direction = -1;					
				}
				else
				{
					direction = 1;	
				}
				increment = increment * direction;
				Thi_tb_prevResult = Thi_tb_result;
				Thi_tb = Thi_tb + increment;
				//fail safe, gone way too far, start again
				if (Thi_tb > 50 || Thi_tb < -50)
				{
					Thi_tb = 8.;
//					Thi_tb = Thi_tb / 2.;
				}
//				if (dT == 0.0)
//				{
//					Thi_tb = 22.760359533246;
//				}
				continueLoop = true;
			}
		}			
		convergeValue = Thi_tb - 9.806/1004.67*dz;
		return convergeValue;
	}
	
	public double getIncrement(double Thi_tb_result_compare)
	{
		double increment = 0.0;
		
		increment = Thi_tb_result_compare * 0.5;
		
		
//		if (Thi_tb_result_compare > 100.)
//		{
//			increment = 100;
//		}
//		
//		System.out.println("getIncrement=" + Thi_tb_result_compare + " " + increment);
		
		return increment;
	}

	public double getIncrement2(double Thi_tb_result_compare)
	{
		double increment = 0;
		if (Thi_tb_result_compare < 15)
		{
			increment = 0.20;
		}
		if (Thi_tb_result_compare < 3)
		{
			increment = 0.15;
		}
		if (Thi_tb_result_compare < 2)
		{
			increment = 0.10;
		}
		if (Thi_tb_result_compare < 1)
		{
			increment = 0.05;
		}
		if (Thi_tb_result_compare < 0.5)
		{
			increment = 0.01;
		}
		if (Thi_tb_result_compare < 0.1)
		{
			increment = 0.005;
		}
		return increment;
	}
	
	public double getIncrement3(double Thi_tb_result_compare, double Thi_tb, double Thi_tb_prevResult)
	{
		double increment = 0.0;
		double scaling = Thi_tb_result_compare;
		
		//increment = Thi_tb * 0.00001 ;
		increment = 0.0001 * Thi_tb * scaling;
		if (Thi_tb_prevResult < 0)
		{
			increment = increment * -1;
		}
		
		
//		if (Thi_tb_result_compare > 100.)
//		{
//			increment = 100;
//		}
//		
//		System.out.println("getIncrement=" + Thi_tb_result_compare + " " + increment);
		
		return increment;
	}
	
	public double getIncrement4(double Thi_tb_result_compare, double Thi_tb, double Thi_tb_prevResult)
	{
		double increment = 0.0;
		double scaling = Math.abs(Thi_tb_result_compare);
		
		//increment = Thi_tb * 0.00001 ;
		increment = 0.001 * Thi_tb * scaling;
		if (Thi_tb_prevResult < 0)
		{
			increment = increment * -1;
		}
		
//		System.out.println("getIncrement=" + Thi_tb_result_compare + " " + increment);
		
		return increment;
	}
}




/*
 
 C **********************************************************************
C			SUBROUTINE SFCXCH
C			-----------------
C       THIS ROUTINE CALCULATES THE BULK RICHARDSON NUMBER AND THE
C       SURFACE EXCHANGE COEFFICIENTS FOR MOMENTUM AND HEAT (=MOISTURE).
C ----------------------------------------------------------------------
C	SOURCES:  LOUIS, ET AL., 1982; HOLTSLAG AND BELJAARS, 1989.
C ----------------------------------------------------------------------
C	ARGUMENTS:
C	INPUT:   T1, T2, TH2, Q2, Z, Z0, Z0H, Z01, S2, SPD
C	OUTPUT:  RIB, CM, CH
C ----------------------------------------------------------------------
C	COMMON BLOCKS/ELEMENTS USED:  NONE
C ----------------------------------------------------------------------
C	SUBROUTINES CALLED:  NONE
C **********************************************************************
      
c      SUBROUTINE SFCXCH(T1,T2,TH2,Q1,Q2,Z,Z0,Z0H,Z01,S2,SPD,RIB,CM,CH)
      SUBROUTINE SFCXCH
     .         (T1,T2,THETAS,TH2,Q1,Q2,Z,Z0,Z0H,Z01,S2,SPD,RIB,CM,CH)
      
      PARAMETER (NSOLD=10)

      DATA PRNEUT,EXMCH /1.,-1./
C      DATA B1,B2,B3,B4,CUS,VK,G /10.,15.,10.,8.,7.5,0.40,9.806/
C      DATA B1,B2,CUS,RCUCT,VK,G /9.4,9.4,7.4,0.716,0.40,9.806/      
C      DATA B1,B2,CUS,RCUCT,VK,G /10.,15.,7.5,0.716,0.40,9.806/      
      DATA BUS,BTS,CUS,CTS,VK,G /10.,15.,7.5,5.,0.40,9.806/      

C ----------------------------------------------------------------------
C     INITIALIZE Z01, TSV, AND TH2V.
C ----------------------------------------------------------------------

      Z01=Z0
c      TSV=T1*(1.+0.61*Q1)
c      THSV=THETAS*(1.+0.61*Q1)
      THSV=THETAS*(1.+0.61*Q2)
      TH2V=TH2*(1.+0.61*Q2)
      
C ----------------------------------------------------------------------
C     COMPUTE BULK RICHARDSON NUMBER AND CONSTANTS.
C ----------------------------------------------------------------------

      AA = VK/ALOG(Z/Z01)
      AH = AA*VK/ALOG(Z/Z0H)
      AM = AA*AA
      
      IF (SPD .NE. 0.) THEN
      
c        RIB = G*Z*(TH2V-TSV)/(TH2V*S2)
        RIB = G*Z*(TH2V-THSV)/(TH2V*S2)
	
        IF(RIB)140,130,130

C ----------------------------------------------------------------------
C	STABLE STRATIFICATION USING METHOD OF MAHRT (1987).
C       IF RIB IS SO LARGE THAT THE NUMERICAL CALCULATION OF CM = 0,
C       SPECIFY CM AND CH AS A VERY SMALL NUMBER.
C ----------------------------------------------------------------------

130     CM = AM*SPD*EXP(EXMCH*RIB)
        IF (CM .LT. 1.E-6) THEN
          CM = 1.E-6
          CH = 1.E-6
        ELSE
          CH = (AH*SPD*EXP(EXMCH*RIB))/PRNEUT
	ENDIF
	
        GOTO 150

C ----------------------------------------------------------------------
C	UNSTABLE STRATIFICATION FROM LOUIS, ET AL. (1982).
C ----------------------------------------------------------------------

140     CUP = CUS*AM*BUS*SQRT(-RIB*Z/Z01)
        CTP = CTS*AH*BTS*SQRT(-RIB*Z/Z0H)
	
        CM = (1. - BUS*RIB/(1.+CUP)) * AM*SPD
        CH = (1. - BTS*RIB/(1.+CTP)) * AH*SPD

150     CONTINUE

      ELSE
      
C ----------------------------------------------------------------------
C	NO WIND; DETERMINE IF UNSTABLE OR STABLE CASE.
C ----------------------------------------------------------------------

c        DTV = TH2V - TSV
c	 IF (DTV .LT. 0.) THEN
        DTHV = TH2V - THSV
	IF (DTHV .LT. 0.) THEN
	
C ----------------------------------------------------------------------
C	  NO WIND, UNSTABLE (FREE CONVECTION) CASE.
C ----------------------------------------------------------------------

c          CM = SQRT(-G*DTV*Z01/TSV) / CUS
c          CH = SQRT(-G*DTV*Z0H/TSV) / CTS
          CM = SQRT(-G*DTHV*Z01/THSV) / CUS
          CH = SQRT(-G*DTHV*Z0H/THSV) / CTS
        ELSE

C ----------------------------------------------------------------------
C	  NO WIND, STABLE CASE; LET RIB = A VERY LARGE NUMBER.  (THE
C         VALUES BELOW ARE NECESSARY TO AVOID LATER DIVISION BY ZERO.)
C ----------------------------------------------------------------------

          CM = 1.E-6
          CH = 1.E-6
          RIB = 1.E3
        ENDIF
	
      ENDIF

      RETURN
      END
 
 */

/*
 
 2009-02-05 00:00:00 0
('Tb_rur=', 21.7995777296108, 0, '-1.0_21.9_[ 2.63038178]_[ 3.05879268]_[ 0.24555776]')
function for grid takes 155.368804
2009-02-05 00:30:00 1
('Tb_rur=', 22.4611121121459, 1, '-1.0_22.6_[ 4.73468721]_[ 5.50582682]_[ 0.10165256]')
^CTraceback (most recent call last):
  File "../../bin2/runToolkit.py", line 77, in <module>
    mod_rslts,mod_rslts_utci = tkmd.modelRun(nt, cfM, lc_data, met_data_all, date1A, Dats)
  File "/home/kerryn/Documents/Work/Toolkit2-Runs/bin2/TARGETModuleThreaded.py", line 469, in modelRun
    tmrt = getTmrtForGrid_RH(Tac,met_d['RH'][i],Ucan,met_d['Kd'][i],Tsurf_can,met_d['Ld'][i], lup ,yd_actual, TM, lat)
  File "/home/kerryn/Documents/Work/Toolkit2-Runs/bin2/utci.py", line 684, in getTmrtForGrid_RH
    Tg=fTg4(Ta, relh, Pair, speed, solar, fdir, zenith, speedMin,tsfc,emisAtmValue,ldown,lup)
  File "/home/kerryn/Documents/Work/Toolkit2-Runs/bin2/utci.py", line 625, in fTg4
    Tref = 0.5 * (Tglobe_prev + Tair) # Evaluate properties at the average temperature
  File "/usr/lib/python2.7/dist-packages/sympy/core/decorators.py", line 77, in __sympifyit_wrapper
    return func(a, b)
  File "/usr/lib/python2.7/dist-packages/sympy/core/decorators.py", line 118, in binary_op_wrapper
    return func(self, other)
  File "/usr/lib/python2.7/dist-packages/sympy/core/expr.py", line 146, in __rmul__
    return Mul(other, self)
  File "/usr/lib/python2.7/dist-packages/sympy/core/cache.py", line 93, in wrapper
    retval = cfunc(*args, **kwargs)
  File "/usr/lib/python2.7/dist-packages/sympy/core/compatibility.py", line 872, in wrapper
    result = user_function(*args, **kwds)
  File "/usr/lib/python2.7/dist-packages/sympy/core/operations.py", line 30, in __new__
    args = list(map(_sympify, args))
  File "/usr/lib/python2.7/dist-packages/sympy/core/sympify.py", line 350, in _sympify
    return sympify(a, strict=True)
  File "/usr/lib/python2.7/dist-packages/sympy/core/sympify.py", line 234, in sympify
    evaluate = global_evaluate[0]
KeyboardInterrupt

kerryn@mu00038561:~/.../Toolkit2-Runs/Sunbury3ExtremeB$ sh run_Sunbury3ExtremeB.sh 
2009-02-05 00:00:00 0
('Tb_rur=', 21.7995777296108, 0, '-1.0_21.9_[ 2.63038178]_[ 3.05879268]_[ 0.24555776]')
^CTraceback (most recent call last):
  File "../../bin2/runToolkit.py", line 77, in <module>
    mod_rslts,mod_rslts_utci = tkmd.modelRun(nt, cfM, lc_data, met_data_all, date1A, Dats)
  File "/home/kerryn/Documents/Work/Toolkit2-Runs/bin2/TARGETModuleThreaded.py", line 490, in modelRun
    for_tab_tmrt_utci     = (lc_data.ix[grid]['FID'],tmrt,utci,dte) 
  File "/usr/lib/python2.7/dist-packages/pandas/core/indexing.py", line 70, in __getitem__
    return self._getitem_axis(key, axis=0)
  File "/usr/lib/python2.7/dist-packages/pandas/core/indexing.py", line 967, in _getitem_axis
    return self._get_label(key, axis=axis)
  File "/usr/lib/python2.7/dist-packages/pandas/core/indexing.py", line 86, in _get_label
    return self.obj._xs(label, axis=axis)
  File "/usr/lib/python2.7/dist-packages/pandas/core/generic.py", line 1514, in xs
    dtype=new_values.dtype)
  File "/usr/lib/python2.7/dist-packages/pandas/core/series.py", line 229, in __init__
    generic.NDFrame.__init__(self, data, fastpath=True)
  File "/usr/lib/python2.7/dist-packages/pandas/core/generic.py", line 106, in __init__
    object.__setattr__(self, 'is_copy', None)
KeyboardInterrupt

kerryn@mu00038561:~/.../Toolkit2-Runs/Sunbury3ExtremeB$ sh run_Sunbury3ExtremeB.sh 
2009-02-05 00:00:00 0
('Tb_rur=', 21.8093381484548, 0, '-1.0_21.9_[ 2.63038178]_[ 3.05879268]_[ 0.24555776]', 21.7995777296108)
function for grid takes 156.663378
2009-02-05 00:30:00 1
('Tb_rur=', 22.4708725309899, 1, '-1.0_22.6_[ 4.73468721]_[ 5.50582682]_[ 0.10165256]', 22.4611121121459)
function for grid takes 155.264940
2009-02-05 01:00:00 2
('Tb_rur=', 23.2257456165725, 2, '-1.0_23.4_[ 5.78683992]_[ 6.7293439]_[ 0.08715121]', 23.2159851977285)
function for grid takes 147.983515
2009-02-05 01:30:00 3
('Error calculating Tb_rur', +inf*(-19.612*Thi_tb + 419.6968)/(Thi_tb + 21.4) - 109.818267569608, ' using 21.795 instead')
-1.0_21.4_0.1_[ 0.1]_[ 109.81826757]
('Tb_rur=', 23.2257456165725, 3, '-1.0_21.4_0.1_[ 0.1]_[ 109.81826757]', 23.2159851977285)
function for grid takes 314.457433
2009-02-05 02:00:00 4
('Error calculating Tb_rur', +inf*(-19.612*Thi_tb + 404.0072)/(Thi_tb + 20.6) + 36.7517207549013, ' using 21.795 instead')
-1.0_20.6_0.1_[ 0.1]_[-36.75172075]
('Tb_rur=', 23.2257456165725, 4, '-1.0_20.6_0.1_[ 0.1]_[-36.75172075]', 23.2159851977285)
^Z
[1]+  Stopped                 sh run_Sunbury3ExtremeB.sh
kerryn@mu00038561:~/.../Toolkit2-Runs/Sunbury3ExtremeB$ fg
sh run_Sunbury3ExtremeB.sh
function for grid takes 2707.887494
2009-02-05 02:30:00 5
('Error calculating Tb_rur', +inf*(-19.612*Thi_tb + 405.9684)/(Thi_tb + 20.7) + 63.2968257031893, ' using 21.795 instead')
-1.0_20.7_0.1_[ 0.1]_[-63.2968257]
('Tb_rur=', 23.2257456165725, 5, '-1.0_20.7_0.1_[ 0.1]_[-63.2968257]', 23.2159851977285)
function for grid takes 320.975800
2009-02-05 03:00:00 6
('Tb_rur=', 19.6041915418324, 6, '-1.0_19.5_[ 4.20861085]_[ 4.89406829]_[-0.10082336]', 19.5944311229884)
^CTraceback (most recent call last):
  File "../../bin2/runToolkit.py", line 77, in <module>
    mod_rslts,mod_rslts_utci = tkmd.modelRun(nt, cfM, lc_data, met_data_all, date1A, Dats)
  File "/home/kerryn/Documents/Work/Toolkit2-Runs/bin2/TARGETModuleThreaded.py", line 473, in modelRun
    #t12 = time() 
  File "/home/kerryn/Documents/Work/Toolkit2-Runs/bin2/utci.py", line 684, in getTmrtForGrid_RH
    Tg=fTg4(Ta, relh, Pair, speed, solar, fdir, zenith, speedMin,tsfc,emisAtmValue,ldown,lup)
  File "/home/kerryn/Documents/Work/Toolkit2-Runs/bin2/utci.py", line 627, in fTg4
    h = h_sphere_in_air(Tref, Pair, speed, speedMin)
  File "/home/kerryn/Documents/Work/Toolkit2-Runs/bin2/utci.py", line 334, in h_sphere_in_air
    density = Pair * 100 / (Rair * Tair)   # kg/m3
  File "/usr/lib/python2.7/dist-packages/sympy/core/decorators.py", line 76, in __sympifyit_wrapper
    b = sympify(b, strict=True)
  File "/usr/lib/python2.7/dist-packages/sympy/core/sympify.py", line 248, in sympify
    return converter[cls](a)
  File "/usr/lib/python2.7/dist-packages/sympy/core/numbers.py", line 612, in __new__
    elif isinstance(num, float) and num == 0:
KeyboardInterrupt

kerryn@mu00038561:~/.../Toolkit2-Runs/Sunbury3ExtremeB$ 
kerryn@mu00038561:~/.../Toolkit2-Runs/Sunbury3ExtremeB$ 
kerryn@mu00038561:~/.../Toolkit2-Runs/Sunbury3ExtremeB$ 
kerryn@mu00038561:~/.../Toolkit2-Runs/Sunbury3ExtremeB$ 
kerryn@mu00038561:~/.../Toolkit2-Runs/Sunbury3ExtremeB$ fg
bash: fg: current: no such job
kerryn@mu00038561:~/.../Toolkit2-Runs/Sunbury3ExtremeB$ sh run_Sunbury3ExtremeB.sh 
2009-02-05 00:00:00 0
('Ok calculating Tb_rur', 5.4485254465654*(-19.612*Thi_tb + 429.5028)/(Thi_tb + 21.9) - 0.245557760307039)
('Tb_rur=', 21.8093381484548, 0, '-1.0_21.9_[ 2.63038178]_[ 3.05879268]_[ 0.24555776]', 21.7995777296108)
function for grid takes 152.183858
2009-02-05 00:30:00 1
('Ok calculating Tb_rur', 1.68164365634734*(-19.612*Thi_tb + 443.2312)/(Thi_tb + 22.6) - 0.10165256118564)
('Tb_rur=', 22.4708725309899, 1, '-1.0_22.6_[ 4.73468721]_[ 5.50582682]_[ 0.10165256]', 22.4611121121459)
function for grid takes 150.193231
2009-02-05 01:00:00 2
('Ok calculating Tb_rur', 1.1257283980507*(-19.612*Thi_tb + 458.9208)/(Thi_tb + 23.4) - 0.0871512054753985)
('Tb_rur=', 23.2257456165725, 2, '-1.0_23.4_[ 5.78683992]_[ 6.7293439]_[ 0.08715121]', 23.2159851977285)
function for grid takes 146.587218
2009-02-05 01:30:00 3
('Error calculating Tb_rur', +inf*(-19.612*Thi_tb + 419.6968)/(Thi_tb + 21.4) - 109.818267569608, ' using 21.795 instead')
-1.0_21.4_0.1_[ 0.1]_[ 109.81826757]
('Tb_rur=', 23.2257456165725, 3, '-1.0_21.4_0.1_[ 0.1]_[ 109.81826757]', 23.2159851977285)
function for grid takes 322.037684
2009-02-05 02:00:00 4
('Error calculating Tb_rur', +inf*(-19.612*Thi_tb + 404.0072)/(Thi_tb + 20.6) + 36.7517207549013, ' using 21.795 instead')
-1.0_20.6_0.1_[ 0.1]_[-36.75172075]
('Tb_rur=', 23.2257456165725, 4, '-1.0_20.6_0.1_[ 0.1]_[-36.75172075]', 23.2159851977285)
function for grid takes 329.466623
2009-02-05 02:30:00 5
('Error calculating Tb_rur', +inf*(-19.612*Thi_tb + 405.9684)/(Thi_tb + 20.7) + 63.2968257031893, ' using 21.795 instead')
-1.0_20.7_0.1_[ 0.1]_[-63.2968257]
('Tb_rur=', 23.2257456165725, 5, '-1.0_20.7_0.1_[ 0.1]_[-63.2968257]', 23.2159851977285)
function for grid takes 331.602933
2009-02-05 03:00:00 6
('Ok calculating Tb_rur', 2.12833025256461*(-19.612*Thi_tb + 382.434)/(Thi_tb + 19.5) + 0.100823358330785)
('Tb_rur=', 19.6041915418324, 6, '-1.0_19.5_[ 4.20861085]_[ 4.89406829]_[-0.10082336]', 19.5944311229884)
function for grid takes 320.706979
2009-02-05 03:30:00 7
('Ok calculating Tb_rur', 8.51332101025843*(-19.612*Thi_tb + 405.9684)/(Thi_tb + 20.7) + 0.199359109476422)
('Tb_rur=', 20.7592523502593, 7, '-1.0_20.7_[ 2.10430543]_[ 2.44703414]_[-0.19935911]', 20.7494919314153)
function for grid takes 354.776641
2009-02-05 04:00:00 8
('Error calculating Tb_rur', +inf*(-19.612*Thi_tb + 392.24)/(Thi_tb + 20.0) + 225.82109104161, ' using 21.795 instead')
-1.0_20.0_0.1_[ 0.1]_[-225.82109104]
('Tb_rur=', 20.7592523502593, 8, '-1.0_20.0_0.1_[ 0.1]_[-225.82109104]', 20.7494919314153)
function for grid takes 320.554460
2009-02-05 04:30:00 9
('Ok calculating Tb_rur', 5.44852544656541*(-19.612*Thi_tb + 394.2012)/(Thi_tb + 20.1) + 0.245141714771919)
('Tb_rur=', 20.2021961484253, 9, '-1.0_20.1_[ 2.63038178]_[ 3.05879268]_[-0.24514171]', 20.1924357295813)
function for grid takes 323.366493
2009-02-05 05:00:00 10
('Ok calculating Tb_rur', 2.12833025256462*(-19.612*Thi_tb + 394.2012)/(Thi_tb + 20.1) + 0.100597354071638)
('Tb_rur=', 20.2068783991275, 10, '-1.0_20.1_[ 4.20861085]_[ 4.89406829]_[-0.10059735]', 20.1971179802835)
function for grid takes 307.796434
2009-02-05 05:30:00 11
('Error calculating Tb_rur', +inf*(-19.612*Thi_tb + 378.5116)/(Thi_tb + 19.3) + 349.713200351072, ' using 21.795 instead')
-1.0_19.3_0.1_[ 0.1]_[-349.71320035]
('Tb_rur=', 20.2068783991275, 11, '-1.0_19.3_0.1_[ 0.1]_[-349.71320035]', 20.1971179802835)
function for grid takes 331.533072
2009-02-05 06:00:00 12
('Ok calculating Tb_rur', 5.4485254465654*(-19.612*Thi_tb + 372.628)/(Thi_tb + 19.0) + 0.42446136359834)
('Tb_rur=', 19.1613081566680, 12, '-1.0_19.0_[ 2.63038178]_[ 3.05879268]_[-0.42446136]', 19.1515477378240)
function for grid takes 320.516068
2009-02-05 06:30:00 13
('Ok calculating Tb_rur', 2.12833025256461*(-19.612*Thi_tb + 429.5028)/(Thi_tb + 21.9) + 0.0239527139322764)
('Tb_rur=', 21.9349092163618, 13, '-1.0_21.9_[ 4.20861085]_[ 4.89406829]_[-0.02395271]', 21.9251487975178)
function for grid takes 309.311213
2009-02-05 07:00:00 14
('Error calculating Tb_rur', +inf*(-19.612*Thi_tb + 474.6104)/(Thi_tb + 24.2) - 178.06201628634, ' using 21.795 instead')
-1.0_24.2_0.1_[ 0.1]_[ 178.06201629]
('Tb_rur=', 21.9349092163618, 14, '-1.0_24.2_0.1_[ 0.1]_[ 178.06201629]', 21.9251487975178)
function for grid takes 316.979829
2009-02-05 07:30:00 15
('Ok calculating Tb_rur', 2.12833025256461*(-19.612*Thi_tb + 498.1448)/(Thi_tb + 25.4) - 0.116271725367618)
('Tb_rur=', 25.2686468248213, 15, '-1.0_25.4_[ 4.20861085]_[ 4.89406829]_[ 0.11627173]', 25.2588864059773)
function for grid takes 304.596234
2009-02-05 08:00:00 16
('Ok calculating Tb_rur', 34.0532840410338*(-19.612*Thi_tb + 535.4076)/(Thi_tb + 27.3) - 2.93178401296889)
('Tb_rur=', 27.0711214256152, 16, '-1.0_27.3_[ 1.05215271]_[ 1.22351707]_[ 2.93178401]', 27.0613610067712)
function for grid takes 315.283141
2009-02-05 08:30:00 17
('Ok calculating Tb_rur', 1.1257283980507*(-19.612*Thi_tb + 570.7092)/(Thi_tb + 29.1) - 0.121010294056856)
('Tb_rur=', 28.7925000039371, 17, '-1.0_29.1_[ 5.78683992]_[ 6.7293439]_[ 0.12101029]', 28.7827395850931)
function for grid takes 287.284304
2009-02-05 09:00:00 18
('Ok calculating Tb_rur', 0.80599488854518*(-19.612*Thi_tb + 629.5452)/(Thi_tb + 32.1) - 0.0889435216461339)
('Tb_rur=', 31.7505422079408, 18, '-1.0_32.1_[ 6.83899264]_[ 7.95286097]_[ 0.08894352]', 31.7407817890968)
function for grid takes 311.457397
2009-02-05 09:30:00 19
('Ok calculating Tb_rur', 0.605391716285046*(-19.612*Thi_tb + 651.1184)/(Thi_tb + 33.2) - 0.0128788799798492)
('Tb_rur=', 33.1378127077982, 19, '-1.0_33.2_[ 7.89114535]_[ 9.17637804]_[ 0.01287888]', 33.1280522889542)
function for grid takes 371.177032
2009-02-05 10:00:00 20
('Ok calculating Tb_rur', 0.805994888545177*(-19.612*Thi_tb + 686.42)/(Thi_tb + 35.0) + 0.0137684786673938)
('Tb_rur=', 35.0707854867098, 20, '-1.0_35.0_[ 6.83899264]_[ 7.95286097]_[-0.01376848]', 35.0610250678658)
function for grid takes 379.301669
2009-02-05 10:30:00 21
('Ok calculating Tb_rur', 0.805994888545179*(-19.612*Thi_tb + 604.0496)/(Thi_tb + 30.8) + 0.102551002807463)
('Tb_rur=', 31.2120077492929, 21, '-1.0_30.8_[ 6.83899264]_[ 7.95286097]_[-0.102551]', 31.2022473304489)
function for grid takes 374.732011
2009-02-05 11:00:00 22
('Ok calculating Tb_rur', 0.605391716285046*(-19.612*Thi_tb + 604.0496)/(Thi_tb + 30.8) + 0.0990791273953201)
('Tb_rur=', 31.3281352408931, 22, '-1.0_30.8_[ 7.89114535]_[ 9.17637804]_[-0.09907913]', 31.3183748220491)
function for grid takes 361.794231
2009-02-05 11:30:00 23
('Ok calculating Tb_rur', 0.605391716285045*(-19.612*Thi_tb + 606.0108)/(Thi_tb + 30.9) + 0.114518369913079)
('Tb_rur=', 31.5116468007914, 23, '-1.0_30.9_[ 7.89114535]_[ 9.17637804]_[-0.11451837]', 31.5018863819474)
function for grid takes 359.394322
2009-02-05 12:00:00 24
('Ok calculating Tb_rur', 0.471325730671747*(-19.612*Thi_tb + 594.2436)/(Thi_tb + 30.3) + 0.103263339976444)
('Tb_rur=', 30.9943884684212, 24, '-1.0_30.3_[ 8.94329807]_[ 10.39989511]_[-0.10326334]', 30.9846280495772)
function for grid takes 350.696951
2009-02-05 12:30:00 25
('Ok calculating Tb_rur', 0.420410914086837*(-19.612*Thi_tb + 600.1272)/(Thi_tb + 30.6) + 0.0968098568256843)
('Tb_rur=', 31.3368778842192, 25, '-1.0_30.6_[ 9.46937442]_[ 11.01165365]_[-0.09680986]', 31.3271174653752)
function for grid takes 358.208418
2009-02-05 13:00:00 26
('Ok calculating Tb_rur', 0.471325730671749*(-19.612*Thi_tb + 602.0884)/(Thi_tb + 30.7) + 0.113329342578151)
('Tb_rur=', 31.4718836061206, 26, '-1.0_30.7_[ 8.94329807]_[ 10.39989511]_[-0.11332934]', 31.4621231872766)
function for grid takes 362.679567
2009-02-05 13:30:00 27
('Ok calculating Tb_rur', 1.1257283980507*(-19.612*Thi_tb + 788.4024)/(Thi_tb + 40.2) + 0.137752737269529)
('Tb_rur=', 40.7145600367714, 27, '-1.0_40.2_[ 5.78683992]_[ 6.7293439]_[-0.13775274]', 40.7047996179274)
^Z

 
 */
