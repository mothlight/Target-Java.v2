import math

SurfAlbedo = 0.4
stefanb = 0.000000056696
diamGlobe = 0.05 # 0.05 = 50mm diam globe
diamWick = 0.007
lenWick = 0.0254
propDirect = 0.8  # Assume a proportion of direct radiation = direct/(diffuse + direct)
ZenithAngle = 0  # angle of sun from directly above
MinWindSpeed = 0.1   # 0 wind speed upsets log function
AtmPressure = 101  # Atmospheric pressure in kPa
ERROR_RETURN=-9999

def calcZen2(ltime,jday,timezone,eqtime,lon,lat):
  import math
  import numpy as np
  # melbourne is timezone=10,lon=145,lat=-37.8
  a0=229.18
  a1=0.000075
  a2=0.001868
  a3=-0.032077
  a4=-0.014615
  a5=-0.040849
  a6=0.006918
  a7=-0.399912
  a8=0.070257
  a9=-0.006758
  a10=0.000907
  a11=-0.002697
  a12=0.00148
  b0=90.833
  pi=3.1415926

  #do the calculation: ltime=local time
  ga=2*pi*(jday-1+(ltime-12.)/24.)/365.
  Eqtime=a0*(a1+a2*np.cos(ga)+a3*np.sin(ga)+a4*np.cos(2*ga)+a5*np.sin(2*ga))
  decl=a6+a7*np.cos(ga)+a8*np.sin(ga)+a9*np.cos(2*ga)+a10*np.sin(2*ga)+a11*np.cos(3*ga)+a12*np.sin(3*ga)
  time_offset=eqtime-4*lon+60.*timezone
  tst=ltime*60.+time_offset
  ha=(tst/4.)-180. #degree
  csza=np.sin(lat*pi/180.)*np.sin(decl)+np.cos(lat*pi/180.)*np.cos(decl)*np.cos(ha*pi/180.)
  zenith=np.deg2rad( np.arccos(csza)*180./pi )
  
  #ha1=acos(cos(b0*pi/180.)/(cos(lat*pi/180.)*cos(decl))-tan(lat*pi/180.)*tan(decl))*180./pi #radian
  #sunrise=(720+4*(lon-ha1)-eqtime)/60.-timezone #localtime
  #sunset =(720+4*(lon+ha1)-eqtime)/60.-timezone #localtime
  #snoon =(720+4*lon-eqtime)/60.-timezone #local noon
  #if you want to eliminate low solar zenith, do the following
  #if zenith gt 82 and zenith le 87.5 then zenith=-1
  #if ltime ge sunrise and ltime le sunset and zenith le 82 then zenith=zenith else zenith=0  
  return zenith



def calcAzEl( t, localtime, latitude, longitude, zone ):
    import sun
    """
Calculate sun azimuth and zenith angle
    """
    eqTime = calcEquationOfTime(t)
    theta  = calcSunDeclination(t)

    solarTimeFix = eqTime + 4.0 * longitude - 60.0 * zone
    earthRadVec = calcSunRadVector(t)

    trueSolarTime = localtime + solarTimeFix
    while trueSolarTime > 1440:
        trueSolarTime -= 1440.

    hourAngle = trueSolarTime / 4.0 - 180.0
    if hourAngle < -180.:
        hourAngle += 360.0

    haRad = np.radians(hourAngle)
    csz = np.sin(np.radians(latitude)) * np.sin(np.radians(theta)) + np.cos(np.radians(latitude)) * np.cos(np.radians(theta)) * np.cos(haRad)
    if csz > 1.0:
        csz = 1.0
    elif csz < -1.0:
        csz = -1.0
    zenith = np.degrees(np.arccos(csz))
    return zenith
    


def calcTmrtNew(Ta, ws, solar, RH, zenith):
  Tg = fTg(Ta, RH, AtmPressure, ws, solar, propDirect, zenith, MinWindSpeed)
  Tmrt = fTmrtB(Ta, Tg, ws); #takes Tg and changes it into Tmrt uses Bernard formula
  return Tmrt

def calcTmrt(kd, ld,  ku,  lu):
    downAngular = 0.5
    upAngular = 0.5
    AbsorbCoeffSW=0.7
    EmmisHumanBody=0.97
    k = kd * downAngular + ku * upAngular
    l = ld * downAngular + lu * upAngular
    s = AbsorbCoeffSW * k + EmmisHumanBody * l
    tmrt = ( (s / (EmmisHumanBody * 5.67E-8) ) **0.25 ) -273.15
    return tmrt

def calcRH(tempC,  vapor):
    rh = 100 * vapor / (7.5152E8 * math.exp(-42809/(8.314*(tempC + 273.0))))
    return rh

def fTmrtB(Ta, Tg, ws):
    WF1 = 0.4 * pow((abs(Tg - Ta)), 0.25)
    WF2 = 2.5 * pow(ws, 0.6)
    WF=0
    if (WF1 > WF2):
       WF = WF1
    else :
        WF = WF2
    temp2 = math.pow(((Tg + 273) / 100), 4)
    temp1 = math.pow(( temp2 + WF * (Tg - Ta)), 0.25)
    #fTmrtB = 100 * math.pow((math.pow(((Tg + 273) / 100), 4) + WF * (Tg - Ta)), 0.25) - 273
    fTmrtB = 100 * temp1 - 273
    return fTmrtB
   
def fUTCI(Ta, Tg, Td, ws, solar, RH):
    D_Tmrt=0 
    #UTCI calculation using full formula from utci.org website
    # Check if one and only one of RH or Td have been entered
    
    if (RH != -99):
        if (Td != -99):
            print("Error:")
            return ERROR_RETURN
        Td = fTd(Ta, RH)
    else:
        RH = fRH(Ta, Td)
        
    #Check to make sure Td < Ta
    if (Td > Ta) :
        print("Error:")
        return ERROR_RETURN
        
    #Calculate Tg given solar, calculate solar given Tg
    if (solar != -99):
        if (Tg != -99):
            print("Error:")
            return ERROR_RETURN
        Tg = fTg(Ta, RH, AtmPressure, ws, solar, propDirect, ZenithAngle, MinWindSpeed)
    
    Tmrt = fTmrtB(Ta, Tg, ws); #takes Tg and changes it into Tmrt uses Bernard formula

    # UTCI, Version a 0.002, October 2009
    # Copyright (C) 2009  Peter Broede
    # Program for calculating UTCI Temperature (UTCI)
    # released for public use after termination of COST Action 730
    # Copyright (C) 2009  Peter Broede
    # This program is distributed in the hope that it will be useful,
    # but WITHOUT ANY WARRANTY; without even the implied warranty of
    # MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

 # DOUBLE PRECISION Function value is the UTCI in degree Celsius
 # computed by a 6th order approximating polynomial from the 4 Input paramters
 #
 # Input parameters (all of type DOUBLE PRECISION)
 # - Ta       : air temperature, degree Celsius
 # - ehPa    : water vapour presure, hPa=hecto Pascal
 # - Tmrt   : mean radiant temperature, degree Celsius
 # - va10m  : wind speed 10 m above ground level in m/s
 #
 #  UTCI_approx, Version a 0.002, October 2009
 #  Copyright (C) 2009  Peter Broede


    PA = 0.6108 * math.exp(17.29 * Td / (Td + 237.3)) #takes Td and changes it into Pa - vapour pressure in hPa)
    D_Tmrt = Tmrt - Ta
    
        # calculate 6th order polynomial as approximation
    ET1 = Ta + 0.607562052 - 0.0227712343 * Ta + 0.000806470249 * Ta * Ta - 0.000154271372 * Ta * Ta * Ta - 0.00000324651735 * Ta * Ta * Ta * Ta \
    + 7.32602852E-08 * Ta * Ta * Ta * Ta * Ta + 1.35959073E-09 * Ta * Ta * Ta * Ta * Ta * Ta - 2.2583652 * ws + 0.0880326035 * Ta * ws \
    + 0.00216844454 * Ta * Ta * ws - 0.0000153347087 * Ta * Ta * Ta * ws - 0.000000572983704 * Ta * Ta * Ta * Ta * ws - 2.55090145E-09 * Ta * Ta * Ta * Ta * Ta * ws \
    - 0.751269505 * ws * ws - 0.00408350271 * Ta * ws * ws - 0.0000521670675 * Ta * Ta * ws * ws + 0.00000194544667 * Ta * Ta * Ta * ws * ws \
    + 1.14099531E-08 * Ta * Ta * Ta * Ta * ws * ws + 0.158137256 * ws * ws * ws - 0.0000657263143 * Ta * ws * ws * ws + 0.000000222697524 * Ta * Ta * ws * ws * ws \
    - 4.16117031E-08 * Ta * Ta * Ta * ws * ws * ws - 0.0127762753 * ws * ws * ws * ws + 0.00000966891875 * Ta * ws * ws * ws * ws + 2.52785852E-09 * Ta * Ta * ws * ws * ws * ws \
    + 0.000456306672 * ws * ws * ws * ws * ws - 0.000000174202546 * Ta * ws * ws * ws * ws * ws - 0.00000591491269 * ws * ws * ws * ws * ws * ws \
    + 0.398374029 * D_Tmrt + 0.000183945314 * Ta * D_Tmrt - 0.00017375451 * Ta * Ta * D_Tmrt - 0.000000760781159 * Ta * Ta * Ta * D_Tmrt \
    + 3.77830287E-08 * Ta * Ta * Ta * Ta * D_Tmrt + 5.43079673E-10 * Ta * Ta * Ta * Ta * Ta * D_Tmrt - 0.0200518269 * ws * D_Tmrt + 0.000892859837 * Ta * ws * D_Tmrt \
    + 0.00000345433048 * Ta * Ta * ws * D_Tmrt - 0.000000377925774 * Ta * Ta * Ta * ws * D_Tmrt - 1.69699377E-09 * Ta * Ta * Ta * Ta * ws * D_Tmrt \
    + 0.000169992415 * ws * ws * D_Tmrt - 0.0000499204314 * Ta * ws * ws * D_Tmrt + 0.000000247417178 * Ta * Ta * ws * ws * D_Tmrt \
    + 1.07596466E-08 * Ta * Ta * Ta * ws * ws * D_Tmrt + 0.0000849242932 * ws * ws * ws * D_Tmrt + 0.00000135191328 * Ta * ws * ws * ws * D_Tmrt \
    - 6.21531254E-09 * Ta * Ta * ws * ws * ws * D_Tmrt - 0.00000499410301 * ws * ws * ws * ws * D_Tmrt - 1.89489258E-08 * Ta * ws * ws * ws * ws * D_Tmrt \
    + 8.15300114E-08 * ws * ws * ws * ws * ws * D_Tmrt + 0.00075504309 * D_Tmrt * D_Tmrt
    
    ET3 = -0.0000565095215 * Ta * D_Tmrt * D_Tmrt + (-0.000000452166564) * Ta * Ta * D_Tmrt * D_Tmrt + (2.46688878E-08) * Ta * Ta * Ta * D_Tmrt * D_Tmrt \
    + (2.42674348E-10) * Ta * Ta * Ta * Ta * D_Tmrt * D_Tmrt + (0.00015454725) * ws * D_Tmrt * D_Tmrt + (0.0000052411097) * Ta * ws * D_Tmrt * D_Tmrt \
    + (-8.75874982E-08) * Ta * Ta * ws * D_Tmrt * D_Tmrt + (-1.50743064E-09) * Ta * Ta * Ta * ws * D_Tmrt * D_Tmrt + (-0.0000156236307) * ws * ws * D_Tmrt * D_Tmrt \
    + (-0.000000133895614) * Ta * ws * ws * D_Tmrt * D_Tmrt + (2.49709824E-09) * Ta * Ta * ws * ws * D_Tmrt * D_Tmrt + (0.000000651711721) * ws * ws * ws * D_Tmrt * D_Tmrt \
    + (1.94960053E-09) * Ta * ws * ws * ws * D_Tmrt * D_Tmrt + (-1.00361113E-08) * ws * ws * ws * ws * D_Tmrt * D_Tmrt + (-0.0000121206673) * D_Tmrt * D_Tmrt * D_Tmrt \
    + (-0.00000021820366) * Ta * D_Tmrt * D_Tmrt * D_Tmrt + (7.51269482E-09) * Ta * Ta * D_Tmrt * D_Tmrt * D_Tmrt + (9.79063848E-11) * Ta * Ta * Ta * D_Tmrt * D_Tmrt * D_Tmrt \
    + (0.00000125006734) * ws * D_Tmrt * D_Tmrt * D_Tmrt + (-1.81584736E-09) * Ta * ws * D_Tmrt * D_Tmrt * D_Tmrt + (-3.52197671E-10) * Ta * Ta * ws * D_Tmrt * D_Tmrt * D_Tmrt \
    + (-0.000000033651463) * ws * ws * D_Tmrt * D_Tmrt * D_Tmrt + (1.35908359E-10) * Ta * ws * ws * D_Tmrt * D_Tmrt * D_Tmrt + (4.1703262E-10) * ws * ws * ws * D_Tmrt * D_Tmrt * D_Tmrt \
    + (-1.30369025E-09) * D_Tmrt * D_Tmrt * D_Tmrt * D_Tmrt
        
    ET4 = 4.13908461E-10 * Ta * D_Tmrt * D_Tmrt * D_Tmrt * D_Tmrt + (9.22652254E-12) * Ta * Ta * D_Tmrt * D_Tmrt * D_Tmrt * D_Tmrt \
    + (-5.08220384E-09) * ws * D_Tmrt * D_Tmrt * D_Tmrt * D_Tmrt + (-2.24730961E-11) * Ta * ws * D_Tmrt * D_Tmrt * D_Tmrt * D_Tmrt \
    + (1.17139133E-10) * ws * ws * D_Tmrt * D_Tmrt * D_Tmrt * D_Tmrt + (6.62154879E-10) * D_Tmrt * D_Tmrt * D_Tmrt * D_Tmrt * D_Tmrt \
    + (4.0386326E-13) * Ta * D_Tmrt * D_Tmrt * D_Tmrt * D_Tmrt * D_Tmrt + (1.95087203E-12) * ws * D_Tmrt * D_Tmrt * D_Tmrt * D_Tmrt * D_Tmrt \
    + (-4.73602469E-12) * D_Tmrt * D_Tmrt * D_Tmrt * D_Tmrt * D_Tmrt * D_Tmrt + (5.12733497) * PA + (-0.312788561) * Ta * PA \
    + (-0.0196701861) * Ta * Ta * PA + (0.00099969087) * Ta * Ta * Ta * PA + (0.00000951738512) * Ta * Ta * Ta * Ta * PA + (-0.000000466426341) * Ta * Ta * Ta * Ta * Ta * PA \
    + (0.548050612) * ws * PA + (-0.00330552823) * Ta * ws * PA + (-0.0016411944) * Ta * Ta * ws * PA + (-0.00000516670694) * Ta * Ta * Ta * ws * PA \
    + (0.000000952692432) * Ta * Ta * Ta * Ta * ws * PA + (-0.0429223622) * ws * ws * PA + (0.00500845667) * Ta * ws * ws * PA + (0.00000100601257) * Ta * Ta * ws * ws * PA \
    + (-0.00000181748644) * Ta * Ta * Ta * ws * ws * PA + (-0.00125813502) * ws * ws * ws * PA
        
    ET5 = -0.000179330391 * Ta * ws * ws * ws * PA + (0.00000234994441) * Ta * Ta * ws * ws * ws * PA + (0.000129735808) * ws * ws * ws * ws * PA \
    + (0.0000012906487) * Ta * ws * ws * ws * ws * PA + (-0.00000228558686) * ws * ws * ws * ws * ws * PA + (-0.0369476348) * D_Tmrt * PA \
    + (0.00162325322) * Ta * D_Tmrt * PA + (-0.000031427968) * Ta * Ta * D_Tmrt * PA + (0.00000259835559) * Ta * Ta * Ta * D_Tmrt * PA \
    + (-4.77136523E-08) * Ta * Ta * Ta * Ta * D_Tmrt * PA + (0.0086420339) * ws * D_Tmrt * PA + (-0.000687405181) * Ta * ws * D_Tmrt * PA \
    + (-0.00000913863872) * Ta * Ta * ws * D_Tmrt * PA + (0.000000515916806) * Ta * Ta * Ta * ws * D_Tmrt * PA + (-0.0000359217476) * ws * ws * D_Tmrt * PA \
    + (0.0000328696511) * Ta * ws * ws * D_Tmrt * PA + (-0.000000710542454) * Ta * Ta * ws * ws * D_Tmrt * PA + (-0.00001243823) * ws * ws * ws * D_Tmrt * PA \
    + (-0.000000007385844) * Ta * ws * ws * ws * D_Tmrt * PA + (0.000000220609296) * ws * ws * ws * ws * D_Tmrt * PA + (-0.00073246918) * D_Tmrt * D_Tmrt * PA \
    + (-0.0000187381964) * Ta * D_Tmrt * D_Tmrt * PA + (0.00000480925239) * Ta * Ta * D_Tmrt * D_Tmrt * PA + (-0.000000087549204) * Ta * Ta * Ta * D_Tmrt * D_Tmrt * PA \
    + (0.000027786293) * ws * D_Tmrt * D_Tmrt * PA
        
    ET6 = -0.00000506004592 * Ta * ws * D_Tmrt * D_Tmrt * PA + (0.000000114325367) * Ta * Ta * ws * D_Tmrt * D_Tmrt * PA + (0.00000253016723) * ws * ws * D_Tmrt * D_Tmrt * PA \
    + (-1.72857035E-08) * Ta * ws * ws * D_Tmrt * D_Tmrt * PA + (-3.95079398E-08) * ws * ws * ws * D_Tmrt * D_Tmrt * PA + (-0.000000359413173) * D_Tmrt * D_Tmrt * D_Tmrt * PA \
    + (0.000000704388046) * Ta * D_Tmrt * D_Tmrt * D_Tmrt * PA + (-1.89309167E-08) * Ta * Ta * D_Tmrt * D_Tmrt * D_Tmrt * PA + (-0.000000479768731) * ws * D_Tmrt * D_Tmrt * D_Tmrt * PA \
    + (7.96079978E-09) * Ta * ws * D_Tmrt * D_Tmrt * D_Tmrt * PA + (1.62897058E-09) * ws * ws * D_Tmrt * D_Tmrt * D_Tmrt * PA + (3.94367674E-08) * D_Tmrt * D_Tmrt * D_Tmrt * D_Tmrt * PA \
    + (-1.18566247E-09) * Ta * D_Tmrt * D_Tmrt * D_Tmrt * D_Tmrt * PA + (3.34678041E-10) * ws * D_Tmrt * D_Tmrt * D_Tmrt * D_Tmrt * PA \
    + (-1.15606447E-10) * D_Tmrt * D_Tmrt * D_Tmrt * D_Tmrt * D_Tmrt * PA + (-2.80626406) * PA * PA + (0.548712484) * Ta * PA * PA + (-0.0039942841) * Ta * Ta * PA * PA \
    + (-0.000954009191) * Ta * Ta * Ta * PA * PA + (0.0000193090978) * Ta * Ta * Ta * Ta * PA * PA + (-0.308806365) * ws * PA * PA + (0.0116952364) * Ta * ws * PA * PA \
    + (0.000495271903) * Ta * Ta * ws * PA * PA + (-0.0000190710882) * Ta * Ta * Ta * ws * PA * PA + (0.00210787756) * ws * ws * PA * PA
        
    ET7 = -0.000698445738 * Ta * ws * ws * PA * PA + (0.0000230109073) * Ta * Ta * ws * ws * PA * PA + (0.00041785659) * ws * ws * ws * PA * PA \
    + (-0.0000127043871) * Ta * ws * ws * ws * PA * PA + (-0.00000304620472) * ws * ws * ws * ws * PA * PA + (0.0514507424) * D_Tmrt * PA * PA \
    + (-0.00432510997) * Ta * D_Tmrt * PA * PA + (0.0000899281156) * Ta * Ta * D_Tmrt * PA * PA + (-0.000000714663943) * Ta * Ta * Ta * D_Tmrt * PA * PA \
    + (-0.000266016305) * ws * D_Tmrt * PA * PA + (0.000263789586) * Ta * ws * D_Tmrt * PA * PA + (-0.00000701199003) * Ta * Ta * ws * D_Tmrt * PA * PA \
    + (-0.000106823306) * ws * ws * D_Tmrt * PA * PA + (0.00000361341136) * Ta * ws * ws * D_Tmrt * PA * PA + (0.000000229748967) * ws * ws * ws * D_Tmrt * PA * PA \
    + (0.000304788893) * D_Tmrt * D_Tmrt * PA * PA + (-0.0000642070836) * Ta * D_Tmrt * D_Tmrt * PA * PA + (0.00000116257971) * Ta * Ta * D_Tmrt * D_Tmrt * PA * PA \
    + (0.00000768023384) * ws * D_Tmrt * D_Tmrt * PA * PA + (-0.000000547446896) * Ta * ws * D_Tmrt * D_Tmrt * PA * PA + (-0.000000035993791) * ws * ws * D_Tmrt * D_Tmrt * PA * PA \
    + (-0.00000436497725) * D_Tmrt * D_Tmrt * D_Tmrt * PA * PA + (0.000000168737969) * Ta * D_Tmrt * D_Tmrt * D_Tmrt * PA * PA + (2.67489271E-08) * ws * D_Tmrt * D_Tmrt * D_Tmrt * PA * PA \
    + (3.23926897E-09) * D_Tmrt * D_Tmrt * D_Tmrt * D_Tmrt * PA * PA
        
    ET8 = -0.0353874123 * PA * PA * PA + (-0.22120119) * Ta * PA * PA * PA + (0.0155126038) * Ta * Ta * PA * PA * PA + (-0.000263917279) * Ta * Ta * Ta * PA * PA * PA \
    + (0.0453433455) * ws * PA * PA * PA + (-0.00432943862) * Ta * ws * PA * PA * PA + (0.000145389826) * Ta * Ta * ws * PA * PA * PA \
    + (0.00021750861) * ws * ws * PA * PA * PA + (-0.0000666724702) * Ta * ws * ws * PA * PA * PA + (0.000033321714) * ws * ws * ws * PA * PA * PA \
    + (-0.00226921615) * D_Tmrt * PA * PA * PA + (0.000380261982) * Ta * D_Tmrt * PA * PA * PA + (-5.45314314E-09) * Ta * Ta * D_Tmrt * PA * PA * PA \
    + (-0.000796355448) * ws * D_Tmrt * PA * PA * PA + (0.0000253458034) * Ta * ws * D_Tmrt * PA * PA * PA + (-0.00000631223658) * ws * ws * D_Tmrt * PA * PA * PA \
    + (0.000302122035) * D_Tmrt * D_Tmrt * PA * PA * PA + (-0.00000477403547) * Ta * D_Tmrt * D_Tmrt * PA * PA * PA + (0.00000173825715) * ws * D_Tmrt * D_Tmrt * PA * PA * PA \
    + (-0.000000409087898) * D_Tmrt * D_Tmrt * D_Tmrt * PA * PA * PA + (0.614155345) * PA * PA * PA * PA + (-0.0616755931) * Ta * PA * PA * PA * PA \
    + (0.00133374846) * Ta * Ta * PA * PA * PA * PA + (0.00355375387) * ws * PA * PA * PA * PA + (-0.000513027851) * Ta * ws * PA * PA * PA * PA 
        
    ET9 = 0.000102449757 * ws * ws * PA * PA * PA * PA + (-0.00148526421) * D_Tmrt * PA * PA * PA * PA + (-0.0000411469183) * Ta * D_Tmrt * PA * PA * PA * PA \
    + (-0.00000680434415) * ws * D_Tmrt * PA * PA * PA * PA + (-0.00000977675906) * D_Tmrt * D_Tmrt * PA * PA * PA * PA + (0.0882773108) * PA * PA * PA * PA * PA \
    + (-0.00301859306) * Ta * PA * PA * PA * PA * PA + (0.00104452989) * ws * PA * PA * PA * PA * PA + (0.000247090539) * D_Tmrt * PA * PA * PA * PA * PA \
    + (0.00148348065) * PA * PA * PA * PA * PA * PA

    fUTCI = ET1 + ET3 + ET4 + ET5 + ET6 + ET7 + ET8 + ET9
    return fUTCI

def fRH(Ta, Td):
    #Calculation of RH from dew point
    fRH = 100 * math.exp(17.27 * Td / (237.7 + Td) - 17.27 * Ta / (237.7 + Ta))
    return fRH
    
def fMRT(Ta, Tg, solar, ws):
    #Use the solar to get the globe temperature (Liljegren) then use the globe temperature to get MRT using bernards formula
    fMRT=0.0
    if (solar == -99 ):
        fMRT = fTmrtB(Ta, Tg, ws)
    elif (Tg == -99 ) :
        RH = 60  #assume a value - not very sensitive to RH
        Tg = fTg(Ta, RH, AtmPressure, ws, solar, propDirect, ZenithAngle, MinWindSpeed)
        fMRT = fTmrtB(Ta, Tg, ws)
    else:
        print("Error:")
        return ERROR_RETURN
    return fMRT
    
def fTd(Ta, RH):
    #Calculation of dew point from RH
    RHD=RH/100
    fTd = 237.3 * (math.log(RHD) / 17.27 + Ta / (237.3 + Ta)) / (1 - math.log(RHD) / 17.27 - Ta / (237.3 + Ta))
    return fTd

def  fTg(Ta, relh, Pair, speed, solar, fdir, zenith, speedMin):

#  Purpose: to calculate the globe temperature
#  Author:  James C. Liljegren
#       Decision and Information Sciences Division
#       Argonne National Laboratory
# Pressure in kPa (Atm =101 kPa)
#Fix up out-of bounds problems with zenith
    if (zenith <= 0) :
        zenith = 0.0000000001
    if (zenith > 1.57) :
        zenith = 1.57

    Pair = Pair * 10
    cza = math.cos(zenith)
    converge = 0.05
    alb_sfc = SurfAlbedo
    alb_globe = 0.05
    emis_globe = 0.95
    emis_sfc = 0.999
    Tair = Ta + 273.15
    RH = relh * 0.01
    Tsfc = Tair
    Tglobe_prev = Tair
    
    #Do iteration
    testno = 1
    continueLoop = True;
    Tglobe=0.0
    while (continueLoop):
        testno = testno + 1
        if (testno > 1000) :
            print("No convergence: values too extreme")
            return ERROR_RETURN
            
        Tref = 0.5 * (Tglobe_prev + Tair) # Evaluate properties at the average temperature
        h = h_sphere_in_air(Tref, Pair, speed, speedMin)
        Tglobe = pow((0.5 * (emis_atm(Tair, RH) * pow(Tair, 4) + emis_sfc * pow(Tsfc, 4) ) - h / (emis_globe * stefanb) * (Tglobe_prev - Tair) + solar / (2 * emis_globe * stefanb) * (1 - alb_globe) * (fdir * (1 / (2 * cza) - 1) + 1 + alb_sfc)), 0.25)
        dT = Tglobe - Tglobe_prev
        if (abs(dT) < converge) :
            Tglobe = Tglobe - 273.15
            continueLoop = False
        else:
            Tglobe_prev = (0.9 * Tglobe_prev + 0.1 * Tglobe)
            continueLoop = True

    fTg = Tglobe
    return fTg
 
def h_sphere_in_air(Tair, Pair, speed, speedMin):
    
#  Purpose: to calculate the convective heat tranfer coefficient for flow around a sphere.
#  Reference: Bird, Stewart, and Lightfoot (BSL), page 409.
    Rair = 8314.34 / 28.97;
    Pr = 1003.5 / (1003.5 + 1.25 * Rair)
    thermal_con = (1003.5 + 1.25 * 8314.34 / 28.97) * viscosity(Tair)
    density = Pair * 100 / (Rair * Tair)   # kg/m3
    if (speed < speedMin) :
        speed = speedMin

    Re = speed * density * diamGlobe / viscosity(Tair)
    Nu = 2 + 0.6 * math.pow(Re, 0.5) * math.pow(Pr, 0.3333)
    h_sphere_in_air = Nu * thermal_con / diamGlobe; # W/(m2 K)
    return h_sphere_in_air
    
def viscosity(Tair):
#  Purpose: Compute the viscosity of air, kg/(m s) given temperature, K
#  Reference: BSL, page 23.
    omega = (Tair / 97 - 2.9) / 0.4 * (-0.034) + 1.048
    viscosity = 0.0000026693 * math.pow((28.97 * Tair), 0.5 ) / (math.pow(3.617, 2) * omega)
    return viscosity

def emis_atm(Ta, RH):    
#  Reference: Oke (2nd edition), page 373.
    e = RH * esat(Ta);
    emis_atm = 0.575 * math.pow(e, 0.143)
    return emis_atm

def esat(Tk):
#  Purpose: calculate the saturation vapor pressure (mb) over liquid water given the temperature (K).
#  Reference: Buck's (1981) approximation (eqn 3) of Wexler's (1976) formulae.
#  over liquid water
    esat=6.1121 * math.exp(17.502 * (Tk - 273.15) / (Tk - 32.18))
    esat = 1.004 * esat  # correction for moist air, if pressure is not available; for pressure > 800 mb
    return esat



def fUTCI2(Ta, ws, RH,  Tmrt):
    D_Tmrt=0 
    #UTCI calculation using full formula from utci.org website
    
    Td = fTd(Ta, RH)
    

    # UTCI, Version a 0.002, October 2009
    # Copyright (C) 2009  Peter Broede
    # Program for calculating UTCI Temperature (UTCI)
    # released for public use after termination of COST Action 730
    # Copyright (C) 2009  Peter Broede
    # This program is distributed in the hope that it will be useful,
    # but WITHOUT ANY WARRANTY; without even the implied warranty of
    # MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

 # DOUBLE PRECISION Function value is the UTCI in degree Celsius
 # computed by a 6th order approximating polynomial from the 4 Input paramters
 #
 # Input parameters (all of type DOUBLE PRECISION)
 # - Ta       : air temperature, degree Celsius
 # - ehPa    : water vapour presure, hPa=hecto Pascal
 # - Tmrt   : mean radiant temperature, degree Celsius
 # - va10m  : wind speed 10 m above ground level in m/s
 #
 #  UTCI_approx, Version a 0.002, October 2009
 #  Copyright (C) 2009  Peter Broede


    PA = 0.6108 * math.exp(17.29 * Td / (Td + 237.3)) #takes Td and changes it into Pa - vapour pressure in hPa)
    D_Tmrt = Tmrt - Ta
    
        # calculate 6th order polynomial as approximation
    ET1 = Ta + 0.607562052 - 0.0227712343 * Ta + 0.000806470249 * Ta * Ta - 0.000154271372 * Ta * Ta * Ta - 0.00000324651735 * Ta * Ta * Ta * Ta \
    + 7.32602852E-08 * Ta * Ta * Ta * Ta * Ta + 1.35959073E-09 * Ta * Ta * Ta * Ta * Ta * Ta - 2.2583652 * ws + 0.0880326035 * Ta * ws \
    + 0.00216844454 * Ta * Ta * ws - 0.0000153347087 * Ta * Ta * Ta * ws - 0.000000572983704 * Ta * Ta * Ta * Ta * ws - 2.55090145E-09 * Ta * Ta * Ta * Ta * Ta * ws \
    - 0.751269505 * ws * ws - 0.00408350271 * Ta * ws * ws - 0.0000521670675 * Ta * Ta * ws * ws + 0.00000194544667 * Ta * Ta * Ta * ws * ws \
    + 1.14099531E-08 * Ta * Ta * Ta * Ta * ws * ws + 0.158137256 * ws * ws * ws - 0.0000657263143 * Ta * ws * ws * ws + 0.000000222697524 * Ta * Ta * ws * ws * ws \
    - 4.16117031E-08 * Ta * Ta * Ta * ws * ws * ws - 0.0127762753 * ws * ws * ws * ws + 0.00000966891875 * Ta * ws * ws * ws * ws + 2.52785852E-09 * Ta * Ta * ws * ws * ws * ws \
    + 0.000456306672 * ws * ws * ws * ws * ws - 0.000000174202546 * Ta * ws * ws * ws * ws * ws - 0.00000591491269 * ws * ws * ws * ws * ws * ws \
    + 0.398374029 * D_Tmrt + 0.000183945314 * Ta * D_Tmrt - 0.00017375451 * Ta * Ta * D_Tmrt - 0.000000760781159 * Ta * Ta * Ta * D_Tmrt \
    + 3.77830287E-08 * Ta * Ta * Ta * Ta * D_Tmrt + 5.43079673E-10 * Ta * Ta * Ta * Ta * Ta * D_Tmrt - 0.0200518269 * ws * D_Tmrt + 0.000892859837 * Ta * ws * D_Tmrt \
    + 0.00000345433048 * Ta * Ta * ws * D_Tmrt - 0.000000377925774 * Ta * Ta * Ta * ws * D_Tmrt - 1.69699377E-09 * Ta * Ta * Ta * Ta * ws * D_Tmrt \
    + 0.000169992415 * ws * ws * D_Tmrt - 0.0000499204314 * Ta * ws * ws * D_Tmrt + 0.000000247417178 * Ta * Ta * ws * ws * D_Tmrt \
    + 1.07596466E-08 * Ta * Ta * Ta * ws * ws * D_Tmrt + 0.0000849242932 * ws * ws * ws * D_Tmrt + 0.00000135191328 * Ta * ws * ws * ws * D_Tmrt \
    - 6.21531254E-09 * Ta * Ta * ws * ws * ws * D_Tmrt - 0.00000499410301 * ws * ws * ws * ws * D_Tmrt - 1.89489258E-08 * Ta * ws * ws * ws * ws * D_Tmrt \
    + 8.15300114E-08 * ws * ws * ws * ws * ws * D_Tmrt + 0.00075504309 * D_Tmrt * D_Tmrt
    
    ET3 = -0.0000565095215 * Ta * D_Tmrt * D_Tmrt + (-0.000000452166564) * Ta * Ta * D_Tmrt * D_Tmrt + (2.46688878E-08) * Ta * Ta * Ta * D_Tmrt * D_Tmrt \
    + (2.42674348E-10) * Ta * Ta * Ta * Ta * D_Tmrt * D_Tmrt + (0.00015454725) * ws * D_Tmrt * D_Tmrt + (0.0000052411097) * Ta * ws * D_Tmrt * D_Tmrt \
    + (-8.75874982E-08) * Ta * Ta * ws * D_Tmrt * D_Tmrt + (-1.50743064E-09) * Ta * Ta * Ta * ws * D_Tmrt * D_Tmrt + (-0.0000156236307) * ws * ws * D_Tmrt * D_Tmrt \
    + (-0.000000133895614) * Ta * ws * ws * D_Tmrt * D_Tmrt + (2.49709824E-09) * Ta * Ta * ws * ws * D_Tmrt * D_Tmrt + (0.000000651711721) * ws * ws * ws * D_Tmrt * D_Tmrt \
    + (1.94960053E-09) * Ta * ws * ws * ws * D_Tmrt * D_Tmrt + (-1.00361113E-08) * ws * ws * ws * ws * D_Tmrt * D_Tmrt + (-0.0000121206673) * D_Tmrt * D_Tmrt * D_Tmrt \
    + (-0.00000021820366) * Ta * D_Tmrt * D_Tmrt * D_Tmrt + (7.51269482E-09) * Ta * Ta * D_Tmrt * D_Tmrt * D_Tmrt + (9.79063848E-11) * Ta * Ta * Ta * D_Tmrt * D_Tmrt * D_Tmrt \
    + (0.00000125006734) * ws * D_Tmrt * D_Tmrt * D_Tmrt + (-1.81584736E-09) * Ta * ws * D_Tmrt * D_Tmrt * D_Tmrt + (-3.52197671E-10) * Ta * Ta * ws * D_Tmrt * D_Tmrt * D_Tmrt \
    + (-0.000000033651463) * ws * ws * D_Tmrt * D_Tmrt * D_Tmrt + (1.35908359E-10) * Ta * ws * ws * D_Tmrt * D_Tmrt * D_Tmrt + (4.1703262E-10) * ws * ws * ws * D_Tmrt * D_Tmrt * D_Tmrt \
    + (-1.30369025E-09) * D_Tmrt * D_Tmrt * D_Tmrt * D_Tmrt
        
    ET4 = 4.13908461E-10 * Ta * D_Tmrt * D_Tmrt * D_Tmrt * D_Tmrt + (9.22652254E-12) * Ta * Ta * D_Tmrt * D_Tmrt * D_Tmrt * D_Tmrt \
    + (-5.08220384E-09) * ws * D_Tmrt * D_Tmrt * D_Tmrt * D_Tmrt + (-2.24730961E-11) * Ta * ws * D_Tmrt * D_Tmrt * D_Tmrt * D_Tmrt \
    + (1.17139133E-10) * ws * ws * D_Tmrt * D_Tmrt * D_Tmrt * D_Tmrt + (6.62154879E-10) * D_Tmrt * D_Tmrt * D_Tmrt * D_Tmrt * D_Tmrt \
    + (4.0386326E-13) * Ta * D_Tmrt * D_Tmrt * D_Tmrt * D_Tmrt * D_Tmrt + (1.95087203E-12) * ws * D_Tmrt * D_Tmrt * D_Tmrt * D_Tmrt * D_Tmrt \
    + (-4.73602469E-12) * D_Tmrt * D_Tmrt * D_Tmrt * D_Tmrt * D_Tmrt * D_Tmrt + (5.12733497) * PA + (-0.312788561) * Ta * PA \
    + (-0.0196701861) * Ta * Ta * PA + (0.00099969087) * Ta * Ta * Ta * PA + (0.00000951738512) * Ta * Ta * Ta * Ta * PA + (-0.000000466426341) * Ta * Ta * Ta * Ta * Ta * PA \
    + (0.548050612) * ws * PA + (-0.00330552823) * Ta * ws * PA + (-0.0016411944) * Ta * Ta * ws * PA + (-0.00000516670694) * Ta * Ta * Ta * ws * PA \
    + (0.000000952692432) * Ta * Ta * Ta * Ta * ws * PA + (-0.0429223622) * ws * ws * PA + (0.00500845667) * Ta * ws * ws * PA + (0.00000100601257) * Ta * Ta * ws * ws * PA \
    + (-0.00000181748644) * Ta * Ta * Ta * ws * ws * PA + (-0.00125813502) * ws * ws * ws * PA
        
    ET5 = -0.000179330391 * Ta * ws * ws * ws * PA + (0.00000234994441) * Ta * Ta * ws * ws * ws * PA + (0.000129735808) * ws * ws * ws * ws * PA \
    + (0.0000012906487) * Ta * ws * ws * ws * ws * PA + (-0.00000228558686) * ws * ws * ws * ws * ws * PA + (-0.0369476348) * D_Tmrt * PA \
    + (0.00162325322) * Ta * D_Tmrt * PA + (-0.000031427968) * Ta * Ta * D_Tmrt * PA + (0.00000259835559) * Ta * Ta * Ta * D_Tmrt * PA \
    + (-4.77136523E-08) * Ta * Ta * Ta * Ta * D_Tmrt * PA + (0.0086420339) * ws * D_Tmrt * PA + (-0.000687405181) * Ta * ws * D_Tmrt * PA \
    + (-0.00000913863872) * Ta * Ta * ws * D_Tmrt * PA + (0.000000515916806) * Ta * Ta * Ta * ws * D_Tmrt * PA + (-0.0000359217476) * ws * ws * D_Tmrt * PA \
    + (0.0000328696511) * Ta * ws * ws * D_Tmrt * PA + (-0.000000710542454) * Ta * Ta * ws * ws * D_Tmrt * PA + (-0.00001243823) * ws * ws * ws * D_Tmrt * PA \
    + (-0.000000007385844) * Ta * ws * ws * ws * D_Tmrt * PA + (0.000000220609296) * ws * ws * ws * ws * D_Tmrt * PA + (-0.00073246918) * D_Tmrt * D_Tmrt * PA \
    + (-0.0000187381964) * Ta * D_Tmrt * D_Tmrt * PA + (0.00000480925239) * Ta * Ta * D_Tmrt * D_Tmrt * PA + (-0.000000087549204) * Ta * Ta * Ta * D_Tmrt * D_Tmrt * PA \
    + (0.000027786293) * ws * D_Tmrt * D_Tmrt * PA
        
    ET6 = -0.00000506004592 * Ta * ws * D_Tmrt * D_Tmrt * PA + (0.000000114325367) * Ta * Ta * ws * D_Tmrt * D_Tmrt * PA + (0.00000253016723) * ws * ws * D_Tmrt * D_Tmrt * PA \
    + (-1.72857035E-08) * Ta * ws * ws * D_Tmrt * D_Tmrt * PA + (-3.95079398E-08) * ws * ws * ws * D_Tmrt * D_Tmrt * PA + (-0.000000359413173) * D_Tmrt * D_Tmrt * D_Tmrt * PA \
    + (0.000000704388046) * Ta * D_Tmrt * D_Tmrt * D_Tmrt * PA + (-1.89309167E-08) * Ta * Ta * D_Tmrt * D_Tmrt * D_Tmrt * PA + (-0.000000479768731) * ws * D_Tmrt * D_Tmrt * D_Tmrt * PA \
    + (7.96079978E-09) * Ta * ws * D_Tmrt * D_Tmrt * D_Tmrt * PA + (1.62897058E-09) * ws * ws * D_Tmrt * D_Tmrt * D_Tmrt * PA + (3.94367674E-08) * D_Tmrt * D_Tmrt * D_Tmrt * D_Tmrt * PA \
    + (-1.18566247E-09) * Ta * D_Tmrt * D_Tmrt * D_Tmrt * D_Tmrt * PA + (3.34678041E-10) * ws * D_Tmrt * D_Tmrt * D_Tmrt * D_Tmrt * PA \
    + (-1.15606447E-10) * D_Tmrt * D_Tmrt * D_Tmrt * D_Tmrt * D_Tmrt * PA + (-2.80626406) * PA * PA + (0.548712484) * Ta * PA * PA + (-0.0039942841) * Ta * Ta * PA * PA \
    + (-0.000954009191) * Ta * Ta * Ta * PA * PA + (0.0000193090978) * Ta * Ta * Ta * Ta * PA * PA + (-0.308806365) * ws * PA * PA + (0.0116952364) * Ta * ws * PA * PA \
    + (0.000495271903) * Ta * Ta * ws * PA * PA + (-0.0000190710882) * Ta * Ta * Ta * ws * PA * PA + (0.00210787756) * ws * ws * PA * PA
        
    ET7 = -0.000698445738 * Ta * ws * ws * PA * PA + (0.0000230109073) * Ta * Ta * ws * ws * PA * PA + (0.00041785659) * ws * ws * ws * PA * PA \
    + (-0.0000127043871) * Ta * ws * ws * ws * PA * PA + (-0.00000304620472) * ws * ws * ws * ws * PA * PA + (0.0514507424) * D_Tmrt * PA * PA \
    + (-0.00432510997) * Ta * D_Tmrt * PA * PA + (0.0000899281156) * Ta * Ta * D_Tmrt * PA * PA + (-0.000000714663943) * Ta * Ta * Ta * D_Tmrt * PA * PA \
    + (-0.000266016305) * ws * D_Tmrt * PA * PA + (0.000263789586) * Ta * ws * D_Tmrt * PA * PA + (-0.00000701199003) * Ta * Ta * ws * D_Tmrt * PA * PA \
    + (-0.000106823306) * ws * ws * D_Tmrt * PA * PA + (0.00000361341136) * Ta * ws * ws * D_Tmrt * PA * PA + (0.000000229748967) * ws * ws * ws * D_Tmrt * PA * PA \
    + (0.000304788893) * D_Tmrt * D_Tmrt * PA * PA + (-0.0000642070836) * Ta * D_Tmrt * D_Tmrt * PA * PA + (0.00000116257971) * Ta * Ta * D_Tmrt * D_Tmrt * PA * PA \
    + (0.00000768023384) * ws * D_Tmrt * D_Tmrt * PA * PA + (-0.000000547446896) * Ta * ws * D_Tmrt * D_Tmrt * PA * PA + (-0.000000035993791) * ws * ws * D_Tmrt * D_Tmrt * PA * PA \
    + (-0.00000436497725) * D_Tmrt * D_Tmrt * D_Tmrt * PA * PA + (0.000000168737969) * Ta * D_Tmrt * D_Tmrt * D_Tmrt * PA * PA + (2.67489271E-08) * ws * D_Tmrt * D_Tmrt * D_Tmrt * PA * PA \
    + (3.23926897E-09) * D_Tmrt * D_Tmrt * D_Tmrt * D_Tmrt * PA * PA
        
    ET8 = -0.0353874123 * PA * PA * PA + (-0.22120119) * Ta * PA * PA * PA + (0.0155126038) * Ta * Ta * PA * PA * PA + (-0.000263917279) * Ta * Ta * Ta * PA * PA * PA \
    + (0.0453433455) * ws * PA * PA * PA + (-0.00432943862) * Ta * ws * PA * PA * PA + (0.000145389826) * Ta * Ta * ws * PA * PA * PA \
    + (0.00021750861) * ws * ws * PA * PA * PA + (-0.0000666724702) * Ta * ws * ws * PA * PA * PA + (0.000033321714) * ws * ws * ws * PA * PA * PA \
    + (-0.00226921615) * D_Tmrt * PA * PA * PA + (0.000380261982) * Ta * D_Tmrt * PA * PA * PA + (-5.45314314E-09) * Ta * Ta * D_Tmrt * PA * PA * PA \
    + (-0.000796355448) * ws * D_Tmrt * PA * PA * PA + (0.0000253458034) * Ta * ws * D_Tmrt * PA * PA * PA + (-0.00000631223658) * ws * ws * D_Tmrt * PA * PA * PA \
    + (0.000302122035) * D_Tmrt * D_Tmrt * PA * PA * PA + (-0.00000477403547) * Ta * D_Tmrt * D_Tmrt * PA * PA * PA + (0.00000173825715) * ws * D_Tmrt * D_Tmrt * PA * PA * PA \
    + (-0.000000409087898) * D_Tmrt * D_Tmrt * D_Tmrt * PA * PA * PA + (0.614155345) * PA * PA * PA * PA + (-0.0616755931) * Ta * PA * PA * PA * PA \
    + (0.00133374846) * Ta * Ta * PA * PA * PA * PA + (0.00355375387) * ws * PA * PA * PA * PA + (-0.000513027851) * Ta * ws * PA * PA * PA * PA 
        
    ET9 = 0.000102449757 * ws * ws * PA * PA * PA * PA + (-0.00148526421) * D_Tmrt * PA * PA * PA * PA + (-0.0000411469183) * Ta * D_Tmrt * PA * PA * PA * PA \
    + (-0.00000680434415) * ws * D_Tmrt * PA * PA * PA * PA + (-0.00000977675906) * D_Tmrt * D_Tmrt * PA * PA * PA * PA + (0.0882773108) * PA * PA * PA * PA * PA \
    + (-0.00301859306) * Ta * PA * PA * PA * PA * PA + (0.00104452989) * ws * PA * PA * PA * PA * PA + (0.000247090539) * D_Tmrt * PA * PA * PA * PA * PA \
    + (0.00148348065) * PA * PA * PA * PA * PA * PA

    fUTCI = ET1 + ET3 + ET4 + ET5 + ET6 + ET7 + ET8 + ET9
    return fUTCI
    
 
#Ta = 33.0
#Tg = -99
##Tg = 34.0
#Td=-99
##Td = 31
#ws = 1.0
#solar = 222.0
##solar = -99
#RH=50.0
##RH = -99
#
#answer = fUTCI(Ta, Tg, Td, ws, solar, RH)
#print fUTCI(Ta, Tg, Td, ws, solar, RH)
##print fMRT(Ta, Tg, solar, ws)
#
##Tmrt = 45.0973349897
##print fUTCI2(Ta, ws, solar, RH, Tmrt)



##########################################


def getZenith(yd,timeis,xlat):
    pi=3.1415926
    # Solar angle and incoming shortwave (direct & diffuse) routines
    LAT=xlat*pi/180.
    TM=timeis % 24.0
    yd_actual=yd+int(TM/24.)
    yd_actual=yd_actual % 365
    zeni=sunpos(yd_actual,TM,LAT)
    zen=zeni*180./pi
    return zen
       
def sunpos(jday,tm,lat):
    pi=3.1415926
    sh=False
    hr_rad=15.*pi/180.
    theta = (jday-1)*(2.*pi)/365.0
    theta_inot=theta
# for southern hemisphere:
    if(lat<0):
        theta= theta+pi % 2.0*pi
        sh=True
        lat=abs(lat)
    # declination angle
    dec = 0.006918-0.399912*math.cos(theta)+0.070257*math.sin(theta)-0.006758*math.cos(2*theta)+0.000907*math.sin(2*theta)-0.002697*math.cos(3*theta)+0.00148*math.sin(3*theta)
    # all the changes are from stull, meteorology for scientists and
    # engineers 2000 - note: the current definition of hl give the solar
    # position based on local mean solar time - to have solar position as
    # as function of standard time in the time zone, must use stull's
    # equation 2.9 on p. 26
    hl=tm*hr_rad
    # cos(solar zenith)
    cz = (math.sin(lat)*math.sin(dec))-(math.cos(lat)*math.cos(dec)*math.cos(hl))
    # solar zenith
    zen=math.acos(cz)
 
    return zen



def radianToDegree(radian):
    PI = 3.1415926
    Degree180 = 180.0
    R_to_D = Degree180/PI
    #D_to_R = PI/Degree180
    radianToDegree = radian * R_to_D      
    return radianToDegree
      
def getFdir(zenith, solar): 
    d = 1.0  # should calculate earth-sun distance, but set to mean value (1 A.U.)
    zenDegrees = radianToDegree(zenith)
    #print *,'zenDegrees',zenDegrees
    fdir = 0.0 # default, for zenDegrees > 89.5
    if (zenDegrees <= 89.5):   
        s0 = 1367.0
        smax = s0 * math.cos(zenith)/ d * d
        sstar = solar / smax
        #division by zero error 
        if (sstar == 0.0):
            sstar = sstar + 0.000001  
            
        fdir = math.exp(3.0 - 1.34*sstar - 1.65/sstar)    
    getFdir = fdir
    return getFdir

def calculateRH(tempC,  vapor):       
    calculateRH = 100 * vapor / (7.5152E8 * math.exp(-42809/(8.314*(tempC + 273.0))) )
    return calculateRH

def fTg4(Ta, relh, PairIn, speed, solar, fdir, zenith, speedMin, Tsfc,emisAtmValue,lIn,lOut):

    SurfAlbedo = 0.15
    stefanb = 0.000000056696
    diamGlobe = 0.15  
    diamWick = 0.007
    lenWick = 0.0254
    propDirect = 0.8  # Assume a proportion of direct radiation = direct/(diffuse + direct)
    ZenithAngle = 0  # angle of sun from directly above
    MinWindSpeed = 0.1   # 0 wind speed upsets log function
    AtmPressure = 101  # Atmospheric pressure in kPa
    ERROR_RETURN=-9999
    
#  Purpose: to calculate the globe temperature
#  Author:  James C. Liljegren
#       Decision and Information Sciences Division
#       Argonne National Laboratory
# Pressure in kPa (Atm =101 kPa)
#Fix up out-of bounds problems with zenith
    if (zenith <= 0):
        zenith = 0.0000000001
    
    if (zenith > 1.57):
        zenith = 1.57
    
    Pair = PairIn * 10
    cza = math.cos(zenith)
    converge = 0.05
    alb_sfc = SurfAlbedo
    alb_globe = 0.05
    emis_globe = 0.95
    emis_sfc = 0.999
    Tair = Ta + 273.15
    RH = relh * 0.01

    TsfcK = Tsfc + 273.15
    Tglobe_prev = Tair
    area = 3.1415 * diamGlobe * diamGlobe
    
    #Do iteration
    testno = 1
    continueLoop = True
    Tglobe=0.0

    
    while (continueLoop):
        testno = testno + 1
        if (testno > 1000):
            print ('No convergence: values too extreme')
            fTg4= ERROR_RETURN
            return fTg4
        
        Tref = 0.5 * (Tglobe_prev + Tair) # Evaluate properties at the average temperature

        h = h_sphere_in_air(Tref, Pair, speed, speedMin)
        #a=area * 0.5 * emis_globe * stefanb * (emisAtmValue * Tair**4 + emis_sfc * TsfcK**4 ) 
        a=area * 0.5 * emis_globe * (lIn+lOut)   # term for energy gained by globe due to thermal radiation from atm and surface
        b=area * 0.5 * (1 - alb_globe)*(1-fdir)*solar  # term for energy gained due to diffuse irradiance
        c=area * 0.25 * (1 - alb_globe) * fdir * solar/ cza  # term for energy gained due to direct irradiance
        d=area * 0.5 * (1 - alb_globe) * alb_sfc * solar  # term for solar irradiance reflected from the surface that is absorbed by the globe
        e=area * h * (Tglobe_prev - Tair)  # term for energy lost from the globe due to convection
        #print *,'a,b,c,d,e',a,b,c,d,e

        Tglobe =  ((a + b + c + d - e) / (area *emis_globe * stefanb)) **0.25
        dT = Tglobe - Tglobe_prev

        if (abs(dT) < converge):
            continueLoop = False
        else:
            Tglobe_prev = (0.9 * Tglobe_prev + 0.1 * Tglobe)
            continueLoop = True
        
    fTg4 = Tglobe - 273.15
    return fTg4
    
def fTmrtD(Ta, Tg, ws):
    #from Kantor and Unger 2011
    emis_globe = 0.95
    diamGlobe = 0.15
    wsCm = ws / 100 # convert m/s to cm/s
    #Tmrt = Tg + 2.42 * wsCm * (Tg - Ta)  
    Tmrt = ((((Tg+273.15)**4+(1.1 * 10**8 * wsCm**0.6/( emis_globe * diamGlobe**0.4 )) * (Tg-Ta))))**0.25 - 273.15  
    fTmrtD = Tmrt
    return fTmrtD


def getTmrtForGrid(Ta,vapor,speed,solar,zenith,tsfc,ldown,lup ):
    Pair = 100.0
    relh=calculateRH(Ta, vapor)
    speedMin = 0.5
    emisAtmValue=emis_atm(Ta + 273.15, relh * 0.01)
    if (solar < 50):
        emisAtmValue = 0.99
    
    fdir = getFdir(zenith, solar)
    Tg=fTg4(Ta, relh, Pair, speed, solar, fdir, zenith, speedMin,tsfc,emisAtmValue,ldown,lup)
    tmrtC=fTmrtD(Ta, Tg, speed)  
    getTmrtForGrid=tmrtC
    
    return getTmrtForGrid

def getTmrtForGrid_RH(Ta,relh,speed,solar,tsfc,ldown,lup,yd_actual, TM, LAT ):
    zenith=getZenith(yd_actual,TM,LAT)
    Pair = 100.0
    #relh=calculateRH(Ta, vapor)
    speedMin = 0.5
    emisAtmValue=emis_atm(Ta + 273.15, relh * 0.01)
    if (solar < 50):
        emisAtmValue = 0.99
    
    fdir = getFdir(zenith, solar)
    Tg=fTg4(Ta, relh, Pair, speed, solar, fdir, zenith, speedMin,tsfc,emisAtmValue,ldown,lup)
    tmrtC=fTmrtD(Ta, Tg, speed)  
    getTmrtForGrid=tmrtC
    
    return getTmrtForGrid



def getUTCIForGrid(Ta,ws,vapor,tmrt):
    #  UTCI.fUTCI2(Ta, ws, RH, tmrt)
    RH=calculateRH(Ta, vapor)
    getUTCIForGrid=fUTCI2(Ta, ws, RH, Tmrt)
    return getUTCIForGrid


def getUTCIForGrid_RH(Ta,ws,RH,tmrt):
    #  UTCI.fUTCI2(Ta, ws, RH, tmrt)
    #RH=calculateRH(Ta, vapor)
    #print (Ta.item(), ws.item(), float(RH), tmrt.item())
    getUTCIForGrid=fUTCI2(Ta.item(), ws.item(), float(RH), tmrt.item())
    return getUTCIForGrid

