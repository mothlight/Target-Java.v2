package Target;

public class SfcRi
{

//	Calculate Richardson number 
	public double sfc_ri(double dz, double Thi, double Tlo, double Uhi)
	{

	    double Tcorrhi=Thi+9.806/1004.67*dz;
	    double Ri=9.806*dz*(Tcorrhi-Tlo)*2./(Thi+Tlo)/Math.pow(Uhi,2);

	    return Ri;


	}


}
