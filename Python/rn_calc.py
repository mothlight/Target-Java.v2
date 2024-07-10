"""

calculates net radiation for current, previous, and next time step

see section 3.1 tech description for more details

inputs:
    cs      = constants dictionary
    cfM     = main control file
    met     = met forcing data frame
    surf    = current surface type   
    Dats    = dates dictionary
    mod_ts  = surface temperature data frame
    i       = current index 
    
Outputs:
    Rn     = net radiation
    Rnprev = net radiation (t-1)
    Rnstar = 0.5*(Rn(t-1) + Rn(t+1))
    
    
    
"""

def rn_calc(cs,cfM,met,surf,Dats,mod_ts,i):
    
    albedo = cs.cs['alb'][surf]     # surface albedo
    emiss  = cs.cs['emis'][surf]    # surface emissivity

    
    if Dats['dte'] == Dats['date1A']:

            # intial values set to 0.  
    
            Rn     = 0.
            Rnprev = 0.     
            Rnnext = 0. 
            Rnstar = 0.
    else:

        if surf != 'watr':
                
        
                if met['Kd'][i] < 10:       # modelled Tsurf used instead of Ta at night (eg 2 tech notes)
                    
                    Ta_srfp = mod_ts[surf][i-3]         # "previous" modelled T_surf (3 timesteps back)
                    Ta_srf  = mod_ts[surf][i-2]         # "current" modelled T_Surf (2 time steps back)
                    Ta_srfn = mod_ts[surf][i-1]         # "next" modelled T_Surf (1 time steps back)
                    
                    #print (met['Kd'][i], met['Ld'][i], type(met['Kd'][i]), type(met['Ld'][i]))
                    if (met['Ld'][i] == 380.0):
                        print ('Using fake Ld value')
                 
                    Rn     = met['Kd'][i]*(1-albedo)     + emiss*(met['Ld'][i]   - cs.cs['sb']*(Ta_srf+273.15)**4)     # modified version of eq 11 Loridan et al. (2011)
                    Rnprev = met['Kd'][i-1]*(1-albedo)   + emiss*(met['Ld'][i-1]   - cs.cs['sb']*(Ta_srfp+273.15)**4)     
                    Rnnext = met['Kd'][i+1]*(1-albedo)   + emiss*(met['Ld'][i+1]   - cs.cs['sb']*(Ta_srfn+273.15)**4)    
                    Rnstar = 0.5*(Rnnext - Rnprev)
                    
                if met['Kd'][i] >= 10:      # traditional formulation from Loridan (eq 1 tech notes) used during the day
            
                    Rn     = met['Kd'][i]*(1-albedo)     + emiss*(met['Ld'][i]     - cs.cs['sb']*(met['Ta'][i]+273.15)**4)  -0.08*met['Kd'][i]*(1-albedo)     # eq 11 Loridan et al. (2011)
                    Rnprev = met['Kd'][i-1]*(1-albedo)   + emiss*(met['Ld'][i-1]   - cs.cs['sb']*(met['Ta'][i-1]+273.15)**4)-0.08*met['Kd'][i-1]*(1-albedo)     
                    Rnnext = met['Kd'][i+1]*(1-albedo)   + emiss*(met['Ld'][i+1]   - cs.cs['sb']*(met['Ta'][i+1]+273.15)**4)-0.08*met['Kd'][i+1]*(1-albedo)     
                    Rnstar = 0.5*(Rnnext - Rnprev)
                    
        if surf == 'watr':          # traditional formulation from Loridan (eq 1 tech notes) used day and night for water 
            
                        
            Rn     = met['Kd'][i]*(1-albedo)     + emiss*(met['Ld'][i]     - cs.cs['sb']*(met['Ta'][i]  +273.15)**4) -0.08*met['Kd'][i]*(1-albedo)     
            Rnprev = met['Kd'][i-1]*(1-albedo)   + emiss*(met['Ld'][i-1]   - cs.cs['sb']*(met['Ta'][i-1]+273.15)**4) -0.08*met['Kd'][i-1]*(1-albedo)     
            Rnnext = met['Kd'][i+1]*(1-albedo)   + emiss*(met['Ld'][i+1]   - cs.cs['sb']*(met['Ta'][i+1]+273.15)**4) -0.08*met['Kd'][i+1]*(1-albedo)     
            Rnstar = 0.5*(Rnnext - Rnprev)
                    
                

    return {'Rn':Rn,'Rnprev':Rnprev, 'Rnstar':Rnstar}