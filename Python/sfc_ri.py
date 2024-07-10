""""
#call SFC_RI(zref-zH+z0,Ta,Tcan,Ua,Ri)

Calculate Richardson number 
"""
def sfc_ri(dz,Thi,Tlo,Uhi):

    Tcorrhi=Thi+9.806/1004.67*dz
    Ri=9.806*dz*(Tcorrhi-Tlo)*2./(Thi+Tlo)/Uhi**2

    return {'Ri':Ri}




