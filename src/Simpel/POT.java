package Simpel;

//import static org.junit.Assert.*;

public class POT {

	public static void main(String[] args)
	{
		POT pot = new POT();
		pot.test();

	}
	
	public void test()
	{
//		Date,R,Temp,U,Td
//		12/31/2014 15:00,143,11.9,4.6,-7.6
		double radiation= 143;
	    double airTemp=11.9;
	    double windSpeed=4.6;
	    double airTempDiff=-7.6;
	    double dayOfYear=31.;
	    double hour=16.;
	    
	    
        double lat = 38.5;
        double lon = 121.5;
        double meridian = 120;
        double elevation = 18.5;
        double sunangle = 17.0;
        double windSpeedHeight = 2.;
        
       double[] returnValues = et_calc(radiation, airTemp, windSpeed, airTempDiff, dayOfYear, hour, lat, lon, meridian, elevation, sunangle, windSpeedHeight);
       double[] expectedValues = new double[] {0.22586906,0.36249248};
//       assertArrayEquals(expectedValues, returnValues, 0.001);

//       1/1/2014 4:00,0,0.2,0.5,-4.4
		radiation=0;
	    airTemp=0.2;
	    windSpeed=0.5;
	    airTempDiff=-4.4;
	    dayOfYear=1.;
	    hour=5.;
	    
	    returnValues = et_calc(radiation, airTemp, windSpeed, airTempDiff, dayOfYear, hour, lat, lon, meridian, elevation, sunangle, windSpeedHeight);
	    expectedValues = new double[] {-0.00508976,-0.0005715};
//	    assertArrayEquals(expectedValues, returnValues, 0.001);
       
	    
//      1/5/2014 15:00,188,18.8,2.8,-10.1
		radiation=188;
	    airTemp=18.8;
	    windSpeed=2.8;
	    airTempDiff=-10.1;
	    dayOfYear=5.;
	    hour=16.;
	    
	    returnValues = et_calc(radiation, airTemp, windSpeed, airTempDiff, dayOfYear, hour, lat, lon, meridian, elevation, sunangle, windSpeedHeight);
	    expectedValues = new double[] {0.23885275,0.37833384};
//	    assertArrayEquals(expectedValues, returnValues, 0.001);
	    
//	    10/19/2014 10:00,606,22,0.9,15.5
	    radiation=606;
	    airTemp=22;
	    windSpeed=0.9;
	    airTempDiff=15.5;
	    dayOfYear=19.;
	    hour=11.;
	    
	    returnValues = et_calc(radiation, airTemp, windSpeed, airTempDiff, dayOfYear, hour, lat, lon, meridian, elevation, sunangle, windSpeedHeight);
	    expectedValues = new double[] {0.37864732,0.39927874};
//	    assertArrayEquals(expectedValues, returnValues, 0.001);
	    
//	    10/12/2014 12:00,708,30.1,6.5,-3.3
	    radiation=708;
	    airTemp=30.1;
	    windSpeed=6.5;
	    airTempDiff=-3.3;
	    dayOfYear=12.;
	    hour=13.;
	    
	    returnValues = et_calc(radiation, airTemp, windSpeed, airTempDiff, dayOfYear, hour, lat, lon, meridian, elevation, sunangle, windSpeedHeight);
	    expectedValues = new double[] {0.81769737,1.18549866};
//	    assertArrayEquals(expectedValues, returnValues, 0.001);

	}
	
	
	//https://github.com/hckaraman/Hourly-POT-Calculation/blob/master/POT.py
//	#region modules
//
//	import numpy as np
//	import matplotlib.pyplot as plt
//	import math
//	import pandas as pd
//	import os
//	import matplotlib.pyplot as plt
//	import seaborn
//	from scipy import stats
//	from matplotlib.offsetbox import AnchoredText
//	import matplotlib.dates as mdates
//	pd.plotting.register_matplotlib_converters(explicit=True)
//	seaborn.set()
//	np.seterr(all='ignore')
//
//	#endregion




//	    @property
//	    def process_path(self):
//	        return self._working_directory

//	    @process_path.setter
//	    def process_path(self, value):
//	        self._working_directory = value
//	        pass

//	    def DataRead(self):
//	        self.df = pd.read_csv(self.Data_file, sep=',', parse_dates=[0], header=0)
//	        self.df['Date'] = pd.to_datetime(self.df['Date'])
//	        self.df = self.df.set_index('Date')
//	        self.Date = self.df.index.to_list()
//	        // self.df['Day'] = pd.DatetimeIndex(self.Date).day
//	        self.df['dayofyear'] = pd.DatetimeIndex(self.Date).day
//	        self.df['hour'] = pd.DatetimeIndex(self.Date).hour
//	        self.df['hour'] = self.df['hour'] + 1



//	    def InitData(self):
//	        self.R = self.df.R
//	        self.T = self.df.Temp
//	        self.U = self.df.U
//	        self.Td = self.df.Td
//	        self.J = np.array(self.df.dayofyear[:])
//	        self.t = np.array(self.df.hour)
//	        self.n = self.df.__len__()
	
//	public void calc_dew_point(double temperature, double humidity)
//	{
//	    dew_point = round((((humidity / 100) ** 0.125) * (112 + 0.9 * temperature) + (0.1 * temperature) - 112),1)
//	    dew_point = float(dew_point)
//	    return(dew_point)
//	}

	    		
//	    		public void get_dew_point_c(t_air_c, rel_humidity)
//	    		{
//	    		    """Compute the dew point in degrees Celsius
//	    		    :param t_air_c: current ambient temperature in degrees Celsius
//	    		    :type t_air_c: float
//	    		    :param rel_humidity: relative humidity in %
//	    		    :type rel_humidity: float
//	    		    :return: the dew point in degrees Celsius
//	    		    :rtype: float
//	    		    """
//	    		    A = 17.27
//	    		    B = 237.7
//	    		    alpha = ((A * t_air_c) / (B + t_air_c)) + math.log(rel_humidity/100.0)
//	    		    return (B * alpha) / (A - alpha)
//	    		}
	    		
	public static final double A= 17.27;
	 public static final double B= 237.7;
    public double computeDewPoint(double humidity, double temperature)
    {
        double func = (A*temperature)/(B+temperature) + Math.log(humidity/100.);
        double dewPoint=(B*func)/(A-func) ;
        return dewPoint;
    }
    
    public double getSunangle( double Lat, int DOY)
    {
    	
        double Latrad=(Math.PI*Lat)/180.;
//        print (Latrad)
        double SunDec=0.409*Math.sin(2*Math.PI/365*DOY-1.39);
        double SunAngle=(Math.acos(-Math.tan(Latrad)*Math.tan(SunDec)));
        return SunAngle;
    	
    }

	    public double[] et_calc(
	    		double radiation, double airTemp, double windSpeed, double dewPoint, double dayOfYear, double hour,
	    		 double lat, double lon, double meridian, double elevation, double sunangle, double windSpeedHeight
	    		)
	    {
//	        self.DataRead()
//	        self.InitData()

	        // GSC = solar constant in MJ m-2 min-1
	        double GSC = 0.082;

	        // σ = Steffan-Boltzman constant in MJ m-2 h-1 K-4
	        double SB = 2.04e-10;

	        // Latitude in radians converted from latitude (L) in degrees
	        double Teta = Math.PI * lat / 180.;

	        // dr = correction for eccentricity of Earth’s orbit around the sun
	        double dr = 1. + 0.033 * Math.cos(2. * Math.PI * dayOfYear / 365.);

	        // δ = Declination of the sun above the celestial equator in radians
	        double Dec = 0.409 * Math.sin((2. * Math.PI * dayOfYear / 365.) - 1.39);

	        // Sc = solar time correction for wobble in Earth’s rotation
	        double Sc = 0.1645 * Math.sin(2. * (2. * Math.PI * (dayOfYear - 81.) / 364.)) - 0.1255 * Math.cos(
	            (2. * Math.PI * (dayOfYear - 81.) / 364.)) - 0.025 * Math.sin((2. * Math.PI * (dayOfYear - 81.) / 364.));

	        // ω = hour angle in radians
	        // ω1 = hour angle ½ hour before ω in radians
	        // ω2 = hour angle ½ hour after ω in radians

	        double w = (Math.PI / 12.) * (((hour - 0.5) + 0.06667 * (meridian - lon) + Sc - 12.));
	        double w1 = w - 0.5 * Math.PI / 12.;
	        double w2 = w + 0.5 * Math.PI / 12.;

	        // Ra = extraterrestrial radiation (MJ m-2 h-1)

	        double sint = (w2 - w1) * Math.sin(Teta) * Math.sin(Dec) + Math.cos(Teta) * Math.cos(Dec) * (
	                Math.sin(w2) - Math.sin(w1));
	        double Ra = dr * sint * 60.0 * GSC * 12.0 / (Math.PI);

	        // β = solar altitude in degrees

	        double Beta = (Ra < 0.0 ? 0.0 : Math.asin(Math.sin(Teta) * Math.sin(Dec) + Math.cos(Teta) * Math.cos(Dec) * Math.cos(w)) * 180. / Math.PI);
	        // Rso = clear sky total global solar radiation at the Earth’s surface in MJ m-2 h-1

	        double Rso = (Beta == 0.0 ? 0.0 : Ra * (0.75 + 2.0e-5 * elevation));

	        double Rs = (radiation < 0.0 ? 0.0 : radiation * 0.0036);

	        // es = saturation vapor pressure (kPa) at the mean hourly air temperature (T) in oC
	        // ea = actual vapor pressure or saturation vapor pressure (kPa) at the mean dew point temperature
	        // ε′ = apparent ‘net’ clear sky emissivity
	        // es = np.zeros(n)
	        // ea = np.zeros(n)
	        // eps = np.zeros(n)

	        double es = 0.6108 * Math.exp(17.27 * airTemp / (airTemp + 237.3));
	        double ea = 0.6108 * Math.exp(17.27 * dewPoint / (dewPoint + 237.3));
	        double eps = 0.34 - 0.14 * Math.sqrt(ea);
	        double ratio = 0.0;
	        ratio = (Beta < sunangle ? 0. : (Rs / Rso < 0.3 ? 0.3 : (Rs / Rso > 1. ? 1. : Rs / Rso)));

	        // f = a cloudiness function of RS and RSO

	        double f = 0.0;
	        f = (Beta < sunangle ? 0.6 : 1.35 * ratio - 0.35);
//	        f[1:n] = (Beta[1:n] < sunangle ? f[0:n - 1] : 1.35 * ratio[1:n] - 0.35);

	        // Rns = net short wave radiation as a function of measured solar radiation (Rs) in MJ m-2 h-1
	        // Rns = np.zeros(n)

	        double Rns = (1. - 0.23) * Rs;

	        // Rnl = net long wave radiation in MJ m-2 h-1
	        // Rn = net radiation over grass in MJ m-2 h-1

	        double Rnl = - f * eps * SB * ( Math.pow((airTemp + 273.15), 4) );
	        double Rn = Rns + Rnl;

	        // Bp = barometric pressure in kPa as a function of elevation (El) in meters

	        double Bp = 101.3 * ( Math.pow(((293. - 0.0065 * elevation) / 293.), 5.26) );

	        // λ = latent heat of vaporization in (MJ kg-1 )

	        double Alfa = 2.45;

	        // γ = psychrometric constant in kPao C-1

	        double psi = 0.0;
	        psi = 0.00163 * Bp / Alfa;

//	        double Gs = (Rn < 0. ? 0.5 * Rn : 0.1 * Rn);
//	        double Gt = (Rn < 0. ? 0.2 * Rn : .04 * Rn);

	        // wind speed

	        double u2 = windSpeed * (4.87 / (Math.log(67.8 * windSpeedHeight - 5.42)));

	        // ra = aerodynamic resistance in s m-1 is estimated for a 0.12 m tall crop as a function of
	        // these two are never used
//	        double rs = (Rn < 0. ? 200. : 50.);
//	        double ra = (windSpeed < 0.5 ? 208. / 0.5 : 208. / windSpeed);

	        // Modified psychrometric constant (γ∗)
	        // For short canopy

//	        double Ks = psi * (1. + rs / ra);

//	        rs = (Rn < 0. ? 200. : 30.);
//	        ra = (windSpeed < 0.5 ? 118. / 0.5 : 118. / windSpeed);

//	        double Kt = psi * (1. + rs / ra);

	        // ∆ = slope of the saturation vapor pressure curve (kPao C-1 ) at mean air temperature (T)

	        double delta = 4099. * es / (Math.pow((airTemp + 237.3), 2));

	        // G = soil heat flux density (MJ m-2 h-1)
	        // Gos for ETos
	        // Grs for ETrs

	        double Gos = (Rn > 0. ? 0.1 * Rn : 0.5 * Rn);
//	        double Grs = (Rn > 0. ? 0.04 * Rn : 0.2 * Rn);

	        // R is the radiation term of the Penman-Monteith and Penman equations in mm d-1 .

	        double Ros = (Rn > 0. ? (0.408 * delta * (Rn - Gos) / (delta + psi * (1. + 0.24 * u2))) : 0.408 * delta * (Rn - Gos) / (delta + psi * (1. + 0.96 * u2)));
//	        double Rot = (Rn > 0. ? (0.408 * delta * (Rn - Gos) / (delta + psi * (1. + 0.25 * u2))) : 0.408 * delta * (Rn - Gos) / (delta + psi * (1. + 1.7 * u2)));
//	        double Rop = (0.408 * delta * (Rn - Gos)) / (delta + psi);

	        // A = aerodynamic term of the Penman-Monteith equation in mm d-1 with u2 the wind
	        // speed at 2 m height
	        // As = np.zeros(n)
	        // At = np.zeros(n)
	        // Ap = np.zeros(n)

	        double As = (Rn > 0. ? ((37. * psi / (airTemp + 273.)) * u2 * (es - ea)) / (delta + psi * (1. + 0.24 * u2)) 
	        		: ((37. * psi / (airTemp + 273.)) * u2 * (es - ea)) / (delta + psi * (1. + 0.96 * u2)));
//	        double At = (Rn > 0. ? ((66. * psi / (airTemp + 273.)) * u2 * (es - ea)) / (delta + psi * (1. + 0.25 * u2)) 
//	        		: ((66. * psi / (airTemp + 273.)) * u2 * (es - ea)) / (delta + psi * (1. + 1.7 * u2)));
//	        double Ap = (Rn > 0. ? ((37. * psi / (airTemp + 273.)) * u2 * (es - ea)) / (delta + psi) : ((37. * psi / (airTemp + 273.)) * u2 * (es - ea)) / (delta + psi));

	        double Ets = Ros + As;  // this one is potential evapotransiration
//	        double Ett = Rot + At;

//	        double LEs = Ets * 2.45;
//	        double Hs = Rn - Gs - LEs;
	        return new double[] { Ets
//	        		,Ett
	        		};
//	        return new double[]{};
	    }

//	    def write_topd(self):
//	        self.df['Short_ET'] = self.Ets
//	        self.df['Tall_ET'] = self.Ett

//	    def interpolation(self):
//	        fit = np.polyfit(self.Ets, self.Ett, 1)
//	        fit_fn = np.poly1d(fit)
//	        return fit_fn

//	    def stats(self,):
//	        return stats.linregress(self.Ets, self.Ett)

	    // Exports results

//	    def export(self,path):
//	        self.write_topd()
//	        self.df.to_csv(os.path.join(path, "Output.csv"),columns=['Short_ET','Tall_ET'])

//	    def draw(self):
//	        fit = self.interpolation()
//	        stats = self.stats()
//	        fig, ax1 = plt.subplots(figsize = (12,8))
//	        ax1.set_title('Short and Tall Canopy Comparison', style='italic', fontweight='bold', fontsize=16)
//	        color = 'tab:orange'
//	        ax1.set_xlabel(r'Hpurly $Et_s$ (mm $hr^{-1}$)', style='italic', fontweight='bold', fontsize=14)
//	        ax1.set_ylabel(r'Hourly $Et_t$ (mm $hr^{-1}$)', color=color, style='italic', fontweight='bold', fontsize=14)
//	        ax1.plot(self.Ets, self.Ett, 'bo', self.Ets, fit(self.Ets), '--k')
//	        ax1.tick_params(axis='y', labelcolor=color)
//	        ax1.tick_params(axis='x')
//	        anchored_text = AnchoredText("y = %.2f\n$R^2$ = %0.2f" %(stats[0],(stats[2]) ** 2), loc=5)
//	        ax1.add_artist(anchored_text)
//	        fig.tight_layout()
//	        // plt.show()

//	    def drawall(self):
//	        self.write_topd()
//	        fit = self.interpolation()
//	        stats = self.stats()
//	        f = plt.figure(figsize=(12, 8))
//	        ax1 = f.add_subplot(212)
//	        ax2 = f.add_subplot(211,sharex=ax1,sharey = ax1)
//	        // ax3 = f.add_subplot(312)
//	        color = 'tab:blue'
//	        ax2.set_ylabel(r'Hpurly $Et_o$ (mm $hr^{-1}$)', color=color, style='italic', fontweight='bold', fontsize=14)
//	        ax2.plot(self.df['Tall_ET'])
//	        ax2.tick_params(axis='y', labelcolor=color)
//	        ax2.tick_params(axis='x', labelrotation=45)
//	        ax2.legend(['Tall Canopy Evapotranspiration'])
//	        ax1.set_xlabel('Date', style='italic', fontweight='bold', labelpad=20, fontsize=13)
//	        // ax3.set_ylabel(r'Short and Tall Canopy Comparison', color=color, style='italic', fontweight='bold', fontsize=14)
//	        // ax3.plot(self.Ets, self.Ett, 'bo', self.Ets, fit(self.Ets), '--k')
//	        // ax3.tick_params(axis='y', labelcolor=color)
//	        // ax3.tick_params(axis='x', labelrotation=45)
//	        // ax3.legend(['Tall Canopy Evapotranspiration'])
//	        // ax3.set_xlabel('Date', style='italic', fontweight='bold', labelpad=20, fontsize=13)
//	        color = 'tab:orange'
//	        ax2.set_title('Potentional Evapotranspiration', style='italic', fontweight='bold', fontsize=16)
//	        ax1.set_ylabel(r'Hourly $Et_o$ (mm $hr^{-1}$)', color=color, style='italic', fontweight='bold', fontsize=14)
//	        ax1.plot(self.df['Short_ET'],color = color)
//	        ax1.tick_params(axis='y', labelcolor=color)
//	        ax1.tick_params(axis='x', rotation=45)
//	        ax1.legend(['Short Canopy Evapotranspiration'])
//	        // plt.setp(ax1.get_xticklabels(), visible=False)
//	        plt.setp(ax2.get_xticklabels(), visible=False)
//	        // plt.setp(ax3.get_xticklabels(), visible=False)
//	        f.tight_layout()
//	        self.draw()
//	        plt.show()

//	# Initilize object
//	a = et()
//	# Process path
//	a.process_path = r'D:\DRIVE\TUBITAK\ET'
//	# Data file
//	a.Data_file = os.path.join(a.process_path, "ET.csv")
//	# Calculate POT
//	a.et_calc()
//	# Export results to a specified path
//	a.export(r'D:\DRIVE\TUBITAK\ET')
//	# Draw results
//	a.drawall()

}
