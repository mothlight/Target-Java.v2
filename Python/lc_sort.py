""""

This function takes land cover data and averages/sorts etc.

It also calculates SVFs

            #       0       1       2         3        4       5        6      7      
      #surfs =   ['roof', 'road' , 'watr', 'conc' , 'Veg'  , 'dry'  , 'irr', 'wall']       ## surfaces types that are modelled.       
    

"""

def lc_sort(LC,H,W,cs):

    LC_canyon  = LC.copy()


    if W < 1.0:
        W = 1.0
        
    tree_area =  ((float(cs.cs['res'])**2)*LC['Veg'])
    tree_width = tree_area/float(cs.cs['res'])
    
    Wtree = W-tree_width
    
    if Wtree <= 1.0:
        Wtree = 1.0
        
    if H <= 1.0:
        H = 1.0
                        
    LCgrnd = [LC['road'],LC['watr'],LC['conc'],LC['dry'],LC['irr']]
   
    if sum(LCgrnd) > 0.:
        LC['road'] = LC['road']+((LC['road']/sum(LCgrnd))*LC['Veg'])
        LC['watr'] = LC['watr']+((LC['watr']/sum(LCgrnd))*LC['Veg']) 
        LC['conc'] = LC['conc']+((LC['conc']/sum(LCgrnd))*LC['Veg'])
        LC['dry'] = LC['dry']+((LC['dry']/sum(LCgrnd))*LC['Veg'])      
        LC['irr'] = LC['irr']+((LC['irr']/sum(LCgrnd))*LC['Veg'])
    else:
        LC['conc'] = LC['conc'] + LC['Veg']
        
    LC_woRoofAvg = LC.copy()  ## copy of LC without roofs 
     
    svfgA = (1.0+(H/Wtree)**2)**0.5 - H/Wtree 

    svfwA = 1./2.*(1.+W/H-(1.+(W/H)**2.)**0.5)
    
    horz_area = cs.cs['res'] * cs.cs['res']  
    wall_area = 2.*(H/W)*(1.-LC['roof'])
    
    LC['wall'] = wall_area
    
    LC_woRoofAvg = LC.copy()  ## copy of LC without roofs 
    LC_wRoofAvg  = LC.copy() 
    
    above = LC_canyon['road']+LC_canyon['watr']+LC_canyon['conc']+LC_canyon['dry']+LC_canyon['irr']
    
    LC_canyon['roof'] = 0.0
    LC_canyon['road']  = LC['road']/above
    LC_canyon['watr'] = LC['watr']/above
    LC_canyon['conc'] = LC['conc']/above
    LC_canyon['dry'] = LC['dry']/above
    LC_canyon['irr'] = LC['irr']/above
    LC_canyon['Veg'] = LC['Veg']/above
    LC_canyon['wall'] = LC['wall']/above


        
# is the above correct??? need to work this out.        
    
    if not LC_woRoofAvg['roof'] > 0.99:
        LC_woRoofAvg['roof'] = 0.0
        
    if not sum(LC_woRoofAvg.values()) == 0.:
        LC1sum = sum(LC_woRoofAvg.values())
        for key, value in LC_woRoofAvg.iteritems():
            LC_woRoofAvg[key] = value/LC1sum
            
    if not sum(LC_wRoofAvg.values()) == 0.:
        LCsum = sum(LC_wRoofAvg.values())
        for key, value in LC_wRoofAvg.iteritems():
            LC_wRoofAvg[key] = value/LCsum

        
    if svfgA < 0.1:
        fg = 0
    if (svfgA > 0.1) and (svfgA <= 0.2):
        fg = 1
    if (svfgA > 0.2) and (svfgA <= 0.3):
        fg = 2
    if (svfgA > 0.3) and (svfgA <= 0.4):
        fg = 3
    if (svfgA > 0.4) and (svfgA <= 0.5):
        fg = 4
    if (svfgA > 0.5) and (svfgA <= 0.6):
        fg = 5
    if (svfgA > 0.6) and (svfgA <= 0.7):
        fg = 6
    if (svfgA > 0.7) and (svfgA <= 0.8):
        fg = 7
    if (svfgA > 0.8) and (svfgA <= 0.9):
        fg = 8
    if (svfgA > 0.9):
        fg = 9
        
    if svfwA < 0.1:
        fw = 0
    if (svfwA > 0.1) and (svfwA <= 0.2):
        fw = 1
    if (svfwA > 0.2) and (svfwA <= 0.3):
        fw = 2
    if (svfwA > 0.3) and (svfwA <= 0.4):
        fw = 3
    if (svfwA > 0.4) and (svfwA <= 0.5):
        fw = 4
    if (svfwA > 0.5) and (svfwA <= 0.6):
        fw = 5
    if (svfwA > 0.6) and (svfwA <= 0.7):
        fw = 6
    if (svfwA > 0.7) and (svfwA <= 0.8):
        fw = 7
    if (svfwA > 0.8) and (svfwA <= 0.9):
        fw = 8
    if (svfwA > 0.9):
        fw = 9        
                          
    return {'LC':LC, 'LC_woRoofAvg':LC_woRoofAvg, 'LC_canyon':LC_canyon, 'LC_wRoofavg':LC_wRoofAvg,'H':H,'W':W, 'Wtree':Wtree, 'fw':fw,'fg':fg}