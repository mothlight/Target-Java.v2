package Target;

public class TestTbRurSolver
{
	private String workingDirectory;
	private TbRurSolver_python tbRurSolver = new TbRurSolver_python();

	public static void main(String[] args)
	{
		TestTbRurSolver solver = new TestTbRurSolver();
		solver.test();

	}
	public void test()
	{
		 workingDirectory = System.getProperty("user.dir");
		 double Tb_rur ;
		 
		 int i = 1 ;
		 double dz=-1.0;
		 double ref_ta = 21.9;
		 double UTb = 2.63038178;
		 double[] mod_U_TaRef = new double[]{0.,3.05879268};
		 double Ri_rur = 0.24555776;
         
         
         tbRurSolver.setWorkingDirectory(this.workingDirectory);
         Tb_rur = tbRurSolver.converge(" "+i+" ", dz+" ", ref_ta+" ", UTb+" ", mod_U_TaRef[i]+" ", Ri_rur+" ");
         
         if (Tb_rur == TbRurSolver.ERROR_RETURN || Tb_rur == 0.0)
         {
         	System.out.println("Error with Tb_rur");
//         	Tb_rur = Tb_rur_prev;
//         	System.out.println("using previous Tb_rur=" + Tb_rur_prev);
         	//System.exit(1);
         }
         else
         {
         	System.out.println("Tb_rur=" + Tb_rur);
//         	Tb_rur_prev = Tb_rur;
         }
	}
	


}
