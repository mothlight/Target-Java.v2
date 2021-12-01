package Target;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class TbRurSolver_python
{
	Common common = new Common();
	private String workingDirectory;
	public static final double ERROR_RETURN = -9999.;
	public double innerClassReturnValue;

	public static void main(String[] args)
	{
		String i="1";
		String dz="-1.0";
		String ref_ta="21.9";
		String UTb="2.63038178";
		String mod_U_TaRef="3.05879268";
		String Ri_rur="0.24555776";
		
		dz="-1.0";
		ref_ta = "30.7";
		UTb = "8.94329807";
		mod_U_TaRef = "10.39989511";
		Ri_rur = "-0.11332934";
		
		dz="-1.0";
		ref_ta = "30.3";
		UTb = "8.94329807";
		mod_U_TaRef = "10.39989511";
		Ri_rur = "-0.10326334";
		
		dz="15.0" ;
		ref_ta ="23.5" ;
		UTb =  "0.21266443039771887" ;
		mod_U_TaRef =  "0.1"  ;
		Ri_rur = "-370.55369919381894";
		
		dz="15.0" ;
		ref_ta ="16.5" ;
		UTb =  "3.975255797000494" ;
		mod_U_TaRef =  "1.8692621937604166"  ;
		Ri_rur = "-1.3626911282063752";
		
		dz="15.0" ;
		ref_ta ="16.8" ;
		UTb =  "6.143577139636226" ;
		mod_U_TaRef =  "2.8888597534372273" ;
		Ri_rur = "-0.5326357056213218";
		
		dz="15.0" ;
		ref_ta ="24.4" ;
		UTb =  "0.21266443039771887" ;
		mod_U_TaRef =  "0.1" ;
		Ri_rur = "-602.7585857212";
		
		dz="15.0" ;
		ref_ta ="16.6" ;
		UTb =  "1.8069344530637708" ;
		mod_U_TaRef = "0.8496646334718477" ;
		Ri_rur = "-6.956094923672547";

	        dz="23.0" ;
		ref_ta ="-2.123" ;
		UTb =  "3.5358602011752187" ;
		mod_U_TaRef = "1.0885243414486574" ;
		Ri_rur = "-31.442507728007048";
		
		TbRurSolver_python solver = new TbRurSolver_python();
		solver.setWorkingDirectory("/home/kerryn/git/Target_Java/bin");
		double returnValue = solver.converge(i, dz, ref_ta, UTb, mod_U_TaRef, Ri_rur);
		System.out.println(returnValue);
	}
	
//	public TbRurSolver_python(String workingDirectory)
//	{
//		this.workingDirectory = workingDirectory;				
//	}
	
	public double converge(int i, double dz, double ref_ta, double UTb, double mod_U_TaRef, double Ri_rur)
	{
		return converge(""+i, ""+ dz, ""+ ref_ta, ""+ UTb, ""+ mod_U_TaRef, ""+ Ri_rur);
	}
		
	public double converge(String i, String dz, String ref_ta, String UTb, String mod_U_TaRef, String Ri_rur)
	{
		double ret = ERROR_RETURN;
//		try
//		{
//			String solverLocation = this.workingDirectory + "/../" + "TbRurSolver.py";
////			System.out.println("solverLocation="+solverLocation);
//			
//			solverLocation = findTbRurPython(this.workingDirectory);
////			System.out.println("Final solverLocation="+solverLocation);
//			
//			ProcessBuilder pb = new ProcessBuilder("/usr/bin/python", solverLocation,i,dz,ref_ta,UTb,mod_U_TaRef,Ri_rur);
////			ProcessBuilder pb = new ProcessBuilder("python","/home/kerryn/git/Target_Java/TbRurSolver.py",
////					"1","-1.0","21.9","2.63038178","3.05879268","0.24555776");
//			Process p = pb.start();
//
//			BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
//			String returnValue = in.readLine();
////			System.out.println(returnValue);
//			ret = new Double(returnValue).doubleValue();
////			System.out.println("value is : " + ret);
//		}
//		catch (Exception e)
//		{
//			System.out.println(e);
//		}
//		
//		
		
		
		
		
		try
		{
			//System.out.println("Try new version");
			String solverLocation = this.workingDirectory + "/../" + "TbRurSolver.py";
			//System.out.println("solverLocation="+solverLocation);
			
			solverLocation = findTbRurPython(this.workingDirectory);
			//System.out.println("Final solverLocation="+solverLocation);
			
			ProcessBuilder pb = new ProcessBuilder("/usr/bin/python", solverLocation,i,dz,ref_ta,UTb,mod_U_TaRef,Ri_rur);
//			ProcessBuilder pb = new ProcessBuilder("python","/home/kerryn/git/Target_Java/TbRurSolver.py",
//					"1","-1.0","21.9","2.63038178","3.05879268","0.24555776");
			

		final Process p = pb.start();
		// then start a thread to read the output.
		new Thread(new Runnable() 
		{
		  public void run() 
		  {
		    BufferedReader output = new BufferedReader(new InputStreamReader(p.getInputStream()));
//		    String line;
		    System.out.print("Result : ");
		    try
			{
//				while ((line = output.readLine()) != null) 
//				{
//				 System.out.println(line);
//				}
		    	String returnValue = output.readLine();
		    	double retValue = new Double(returnValue).doubleValue();
		    	innerClassReturnValue = retValue;
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		  }
		}).start();
		p.waitFor();


		}
		catch (Exception e)
		{
			System.out.println(e);
		}
		ret = innerClassReturnValue;
		
		
		return ret;
	}
	
	public String findTbRurPython(String workingDirectory)
	{
		String returnValue = "Can't find solver file";
		
		String solverLocation = this.workingDirectory + "/../" + "TbRurSolver.py";
		boolean exists = common.verifyFileExists(solverLocation);
		if (exists)
		{
			return solverLocation;
		}
		solverLocation = this.workingDirectory + "/" + "TbRurSolver.py";
		exists = common.verifyFileExists(solverLocation);
		if (exists)
		{
			return solverLocation;
		}
		
		
		
		return returnValue;
	}
	
//	public double converge2(String i, String dz, String ref_ta, String UTb, String mod_U_TaRef, String Ri_rur)
//	{
//		double ret = ERROR_RETURN;
//		
//		try
//		{
//			String parameters = i+" "+dz+" "+ref_ta+" "+UTb+" "+mod_U_TaRef+" "+Ri_rur;
//			String solverLocation = this.workingDirectory + "/" + "TbRurSolver.py" ;
//			String totalCommand = "python  " + solverLocation + " " + parameters;
//			System.out.println(totalCommand);
//			Process p = Runtime.getRuntime().exec(totalCommand);
//		    BufferedReader in = new BufferedReader(new InputStreamReader(
//		            p.getInputStream()));
//
//		    String line;  
//		        while ((line = in.readLine()) != null) 
//		        {  
//		            //System.out.println(line);  
//		            ret = new Double(line).doubleValue();
//		        }  
//		        in.close();
//		        p.waitFor();
//		}
//		catch (Exception e)
//		{
//			System.out.println(e);
//		}
//		
//		
//		
////		try
////		{
////			String solverLocation = this.workingDirectory + "/" + "TbRurSolver.py";
////			System.out.println("solverLocation="+solverLocation);
////			ProcessBuilder pb = new ProcessBuilder("/usr/bin/python", solverLocation,i,dz,ref_ta,UTb,mod_U_TaRef,Ri_rur);
//////			ProcessBuilder pb = new ProcessBuilder("python","/home/kerryn/git/Target_Java/TbRurSolver.py",
//////					"1","-1.0","21.9","2.63038178","3.05879268","0.24555776");
////			Process p = pb.start();
////
////			BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
////			String returnValue = in.readLine();
////			System.out.println(returnValue);
////			ret = new Double(returnValue).doubleValue();
////			//System.out.println("value is : " + ret);
////		}
////		catch (Exception e)
////		{
////			System.out.println(e);
////		}
//		return ret;
//	}

	public String getWorkingDirectory()
	{
		return workingDirectory;
	}

	public void setWorkingDirectory(String workingDirectory)
	{
		this.workingDirectory = workingDirectory;
	}

}
