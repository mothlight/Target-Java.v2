package Target;

public class CalcSoilMoisture
{
	public static void main(String[] args)
	{
		CalcSoilMoisture calc = new CalcSoilMoisture();
		
		double w;
		//double w = 50.0; //water content [mass-%]
		double trd = 1.6 * 1000; // dry density of soil [kg m-3], ranges seem to be 1.6 to 2.03 http://environment.uwe.ac.uk/geocal/SoilMech/compaction/index.htm
		double temperature = 21.0; //temperature in C]
//		for (int i=0;i<100;i=i+5)
//		{
//			w = i;
//			double cv = calc.calculateCvFromW(w, trd, temperature);
//			w = calc.calculateWFromCV(cv, trd, temperature);
//		}
		
		//calculate the water content from the Target heat capacities constants
		// cs['C']  =   {"roof":1250000.   , "wall":1250000.   , "road":1940000.   ,"watr":4180000.    ,"conc": 2110000.    , "dry": 1350000.    ,"irr": 2190000., "soilW" : 3030000.}	# heat capacity  (J m^-3 K^-1)
		
		double cv = 3030000;
		System.out.println("soilW,3030000.");
		w = calc.calculateWFromCV(cv, trd, temperature);
		
		System.out.println("irr,2190000.");
		cv = 2190000;
		w = calc.calculateWFromCV(cv, trd, temperature);
		
		System.out.println("dry,1350000");
		cv = 1350000;
		w = calc.calculateWFromCV(cv, trd, temperature);
		
		System.out.println("conc,2110000.");
		cv = 2110000;
		w = calc.calculateWFromCV(cv, trd, temperature);
		
		System.out.println("watr,4180000.");
		cv = 4180000;
		w = calc.calculateWFromCV(cv, trd, temperature);
		
		System.out.println("road,1940000.");
		cv = 1940000;
		w = calc.calculateWFromCV(cv, trd, temperature);
		
		System.out.println("wall,1250000.");
		cv = 1250000;
		w = calc.calculateWFromCV(cv, trd, temperature);
		
		System.out.println("roof,1250000.");
		cv = 1250000;
		w = calc.calculateWFromCV(cv, trd, temperature);
	}
	public double calculateCvFromW(double w, double trd, double temperature)
	{
		//Cs = specific heat capacity of the dry soil [J * kg-1 * K-1]
		double cs = 1.64 * temperature + 704; 
		
		//Cp = specific Heat capacity of the wet soil [J * kg-1 * K-1]
		double cp = (100 * cs + 4190 * w) / (100 + w); 
		
		//Cv = volumetric heat capacity [J * kg-1 * m-3]
		double cv = cp * trd;
		cv = Math.round(cv);
		
		System.out.println("w=" + w + " cv=" + cv);
		return cv;
	}
	
	//this is the formulas from calculateCvFromW() solved for w
	public double calculateWFromCV(double cv, double trd, double temperature)
	{
		double cs = 1.64 * temperature + 704; 
		double cp = cv / trd;
		double w = ( (100 * cp) - (100 * cs) ) / (4190 - cp);
		w = Math.round(w);
		System.out.println("cv=" +cv + " " + "w=" + w);
		return w;
	}

//	The heat capacities of soils can be calculated from the sum of its components.
//	The main soil forming minerals have similar heat capacities. Exceptions are water and organic materials (Data: Bachmann, 2005):
//	Quarz: 0,76 J g-1 K-1 at 10°C
//	Clay minerals: 0,76 J g-1 K-1 at 10°C
//	Organic: 1,93 J g-1 K-1 at 10°C
//	Water: 4,19 J g-1 K-1 at 10°C
//	Ice: 2,04 J g-1 K-1 at 0°C
//	Quarz: 0,8 J g-1 K-1 at 55-60°C
//	Kaolinit: 0,94 J g-1 K-1 at 55-60°C
//	CaCO: 0,85 J g-1 K-1 at 55-60°C
//	Because of this the SIA (1996) suggests a way of calculating the heat capacity of wet soils. They use an average value of the heat capacity of soil forming minerals and:
//	·        the temperature
//	·        the water content
//	·        the dry density
//	of the soil.
//	This is an approach. But I developed a similar approach in my thesis and it worked quite good.
//	Calculation of heat capacity is carried out by using the following formula (SIA 1996):
//	Cs = 1,64 * Temp [°C] + 704
//	Cp = (100 * Cs + 4190 * w) / (100 + w)
//	Cv = Cp * TRD
//	Cp = specific Heat capacity of the wet soil [J * kg-1 * K-1]
//	Cs = specific heat capacity of the dry soil [J * kg-1 * K-1]
//	Cv = volumetric heat capacity [J * kg-1 * m-3]
//	TRD = dry density of the soil [kg * m-3]
//	w = water content [mass-%]
//	This formula should work for unfrozen soils. I took the formula not directly from the SIA but from http://www.lbeg.niedersachsen.de/download/1220 (page 30, in German) please check it before working with it.
//	Many data to thermal properties of soils are given in Farouki (1986) http://www.dtic.mil/cgi-bin/GetTRDoc?AD=ADA111734 and in the papers of Campbell et al.
//	https://www.researchgate.net/profile/Gaylon_Campbell?_sg=DSMoX2JrtMOEuDUNNxcsJG4OXw75BaZ5BlK71QELwh4NvBbynCx_FBgvEmib9mp9FyNCIyh3vlccFZ2N0BTBBg.Pue5GiHpkiLjCWFKmCHQFG3CWvfogG7jVPJG3GNF-aFMgtb9u8Pr_Dk6iC-Ayc7g
//	Literature:
//	Bachmann, J. (2005): Thermisches Verhalten der Böden, Kap. 2.6.4., In: Blume, H.P., P. Felix- Henningsen, W.R. Fischer, H.-G. Frede, R. Horn und K. Stahr (Hrsg.). Handbuch der Bodenkunde, 22. Erg. Lfg. 8/0532 S. Ecomed Verlag, Landsberg/Lech.
//	SIA – SCHWEIZER INGENIEUR UND ARCHITEKTENVEREIN (1996): Grundlagen zur Nutzung der untiefen Erdwärme für Heizsysteme. – SIA Dokumentation D 0136, Schweizer Ingenieur- und Architektenverein; Zürich.

}
