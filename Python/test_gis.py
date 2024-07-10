# -*- coding: utf-8 -*-


#from plotting import val_ta, gis
import sys
from configobj import ConfigObj
import pandas
import os
import numpy as np



#import datetime as dt
from datetime import datetime, timedelta
import matplotlib.pylab as mp
#from scipy.stats import stats 
#from matplotlib.pyplot import cm
#import itertools  


import random
from osgeo import gdal, ogr   

#from osgeo import gdal
from osgeo.gdalconst import *
#import numpy
import struct

RASTERIZE_COLOR_FIELD = "__color__"

def read_adf(filename):    
    #Change the value with your raster filename here
    raster_file = '/home/kerryn/Documents/Work/Toolkit2-Runs/Mawson/GIS/Mawson/obs/MAWSON_LST/night/tsfnl30/w001001.adf'
    #output_file = 'classified.tiff'

    #classification_values = [0,500,1000,1500,2000,2500,3000,3500,4000] #The interval values to classify
    #classification_output_values = [10,20,30,40,50,60,70,80,90] #The value assigned to each interval



    #Opening the raster file
    dataset = gdal.Open(raster_file, GA_ReadOnly )
    band = dataset.GetRasterBand(1)
    #Reading the raster properties
    projectionfrom = dataset.GetProjection()
    geotransform = dataset.GetGeoTransform()
    xsize = band.XSize
    ysize = band.YSize
    datatype = band.DataType

    #Reading the raster values
    values = band.ReadRaster( 0, 0, xsize, ysize, xsize, ysize, datatype )
    #Conversion between GDAL types and python pack types (Can't use complex integer or float!!)
    data_types ={'Byte':'B','UInt16':'H','Int16':'h','UInt32':'I','Int32':'i','Float32':'f','Float64':'d'}
    values = struct.unpack(data_types[gdal.GetDataTypeName(band.DataType)]*xsize*ysize,values)
    return values


def read_grd(filename):
    print (filename)
    with open(filename) as infile:
        ncols = int(infile.readline().split()[1])
        nrows = int(infile.readline().split()[1])
        xllcorner = float(infile.readline().split()[1])
        yllcorner = float(infile.readline().split()[1])
        cellsize = float(infile.readline().split()[1])
        nodata_value = int(infile.readline().split()[1])
        version = float(infile.readline().split()[1])
    longitude = xllcorner + cellsize * np.arange(ncols)
    latitude = xllcorner + cellsize * np.arange(nrows)
    value = np.loadtxt(filename, skiprows=7)

    return longitude, latitude, value

def copyShapeFile(filename, pixel_size=25):
    # Open the data source
    orig_data_source = ogr.Open(filename)
    # Make a copy of the layer's data source because we'll need to 
    # modify its attributes table
    source_ds = ogr.GetDriverByName("Memory").CopyDataSource(orig_data_source, "")
    source_layer = source_ds.GetLayer(0)
    source_srs = source_layer.GetSpatialRef()
    x_min, x_max, y_min, y_max = source_layer.GetExtent()
    # Create a field in the source layer to hold the features colors
    field_def = ogr.FieldDefn(RASTERIZE_COLOR_FIELD, ogr.OFTReal)
    source_layer.CreateField(field_def)
    source_layer_def = source_layer.GetLayerDefn()
    field_index = source_layer_def.GetFieldIndex(RASTERIZE_COLOR_FIELD)
    
    return source_layer

def rasterize(filename, pixel_size=25):
    # Open the data source
    orig_data_source = ogr.Open(filename)
    # Make a copy of the layer's data source because we'll need to 
    # modify its attributes table
    source_ds = ogr.GetDriverByName("Memory").CopyDataSource(orig_data_source, "")
    source_layer = source_ds.GetLayer(0)
    source_srs = source_layer.GetSpatialRef()
    x_min, x_max, y_min, y_max = source_layer.GetExtent()
    # Create a field in the source layer to hold the features colors
    field_def = ogr.FieldDefn(RASTERIZE_COLOR_FIELD, ogr.OFTReal)
    source_layer.CreateField(field_def)
    source_layer_def = source_layer.GetLayerDefn()
    field_index = source_layer_def.GetFieldIndex(RASTERIZE_COLOR_FIELD)
    # Generate random values for the color field (it's here that the value
    # of the attribute should be used, but you get the idea)
    for feature in source_layer:
        feature.SetField(field_index, random.randint(0, 255))
        source_layer.SetFeature(feature)
    # Create the destination data source
    x_res = int((x_max - x_min) / pixel_size)
    y_res = int((y_max - y_min) / pixel_size)
    target_ds = gdal.GetDriverByName('GTiff').Create('test.tif', x_res,
            y_res, 3, gdal.GDT_Byte)
    target_ds.SetGeoTransform((
            x_min, pixel_size, 0,
            y_max, 0, -pixel_size,
        ))
    if source_srs:
        # Make the target raster have the same projection as the source
        target_ds.SetProjection(source_srs.ExportToWkt())
    else:
        # Source has no projection (needs GDAL >= 1.7.0 to work)
        target_ds.SetProjection('LOCAL_CS["arbitrary"]')
    # Rasterize
    err = gdal.RasterizeLayer(target_ds, (3, 2, 1), source_layer,
            burn_values=(0, 0, 0),
            options=["ATTRIBUTE=%s" % RASTERIZE_COLOR_FIELD])
    if err != 0:
        raise Exception("error rasterizing layer: %s" % err)
        
    return target_ds

def polygonToRaster(source_layer, pixel_size=25):
    # Open the data source
    #orig_data_source = ogr.Open(filename)
    # Make a copy of the layer's data source because we'll need to 
    # modify its attributes table
    #source_ds = ogr.GetDriverByName("Memory").CopyDataSource(orig_data_source, "")
    #source_layer = source_ds.GetLayer(0)
    source_srs = source_layer.GetSpatialRef()
    x_min, x_max, y_min, y_max = source_layer.GetExtent()
    # Create a field in the source layer to hold the features colors
    field_def = ogr.FieldDefn(RASTERIZE_COLOR_FIELD, ogr.OFTReal)
    source_layer.CreateField(field_def)
    source_layer_def = source_layer.GetLayerDefn()
    field_index = source_layer_def.GetFieldIndex(RASTERIZE_COLOR_FIELD)
    # Generate random values for the color field (it's here that the value
    # of the attribute should be used, but you get the idea)
    for feature in source_layer:
        feature.SetField(field_index, random.randint(0, 255))
        source_layer.SetFeature(feature)
    # Create the destination data source
    x_res = int((x_max - x_min) / pixel_size)
    y_res = int((y_max - y_min) / pixel_size)
    target_ds = gdal.GetDriverByName('GTiff').Create('test.tif', x_res,
            y_res, 3, gdal.GDT_Byte)
    target_ds.SetGeoTransform((
            x_min, pixel_size, 0,
            y_max, 0, -pixel_size,
        ))
    if source_srs:
        # Make the target raster have the same projection as the source
        target_ds.SetProjection(source_srs.ExportToWkt())
    else:
        # Source has no projection (needs GDAL >= 1.7.0 to work)
        target_ds.SetProjection('LOCAL_CS["arbitrary"]')
    # Rasterize
    err = gdal.RasterizeLayer(target_ds, (3, 2, 1), source_layer,
            burn_values=(0, 0, 0),
            options=["ATTRIBUTE=%s" % RASTERIZE_COLOR_FIELD])
    if err != 0:
        raise Exception("error rasterizing layer: %s" % err)
        
    return target_ds
    
def fullprint(*args, **kwargs):
  from pprint import pprint
  import numpy
  opt = numpy.get_printoptions()
  numpy.set_printoptions(threshold='nan')
  pprint(*args, **kwargs)
  numpy.set_printoptions(**opt)    
    
    
def convertVectorToArray(vector_fn):
    import ogr, gdal
    
    #vector_fn = 'test.shp'
    
    # Define pixel_size and NoData value of new raster
    pixel_size = 25
    NoData_value = 255
    
    # Open the data source and read in the extent
    source_ds = ogr.Open(vector_fn)
    source_layer = source_ds.GetLayer()
    source_srs = source_layer.GetSpatialRef()
    x_min, x_max, y_min, y_max = source_layer.GetExtent()
    
    # Create the destination data source
    x_res = int((x_max - x_min) / pixel_size)
    y_res = int((y_max - y_min) / pixel_size)
    target_ds = gdal.GetDriverByName('MEM').Create('', x_res, y_res, gdal.GDT_Byte)
    target_ds.SetGeoTransform((x_min, pixel_size, 0, y_max, 0, -pixel_size))
    band = target_ds.GetRasterBand(1)
    band.SetNoDataValue(NoData_value)
    
    # Rasterize
    gdal.RasterizeLayer(target_ds, [1], source_layer, burn_values=[1])
    
    # Read as array
    array = band.ReadAsArray()
    print (array) 

def gis(cfM,mod_rslts,run):

 #   import arcpy
 #   from arcpy.sa import *
    
    
    daTes = [datetime(2011, 2, 15, 3, 0),datetime(2011, 2, 16, 15, 0)]
    c=-1
    for prd in ['night','day']: 
        fig = mp.figure(1,figsize=(3, 3), dpi=300)

        c+=1
#        arcpy.env.workspace = r'../GIS'+'/'+cfM['site_name']+'/'+'mod'+'/'+run
#        arcpy.env.overwriteOutput = True
#        arcpy.CheckOutExtension("Spatial")
#    
        in_fshnt = r'../GIS'+'/'+cfM['site_name']+'/'+'obs'+'/'+"empty"+'/'+cfM['inpt_grid_file']
        print (in_fshnt)
        #if (os.path.isfile(in_fshnt) ):
        #    print "is file"
        #else:
        #    print "not file"
 
        obs_ras = r'../GIS/Mawson/obs/MAWSON_LST'+'/'+prd+'/'+'tsfnl30.txt'
        print (obs_ras)
        obsN =    r'../GIS/Mawson/obs/MAWSON_LST'+'/'+prd+"/"+'tsfnl30'
        print (obsN)
        
    
        mod_rslts1 = mod_rslts[mod_rslts['date'] == daTes[c]]
        print (mod_rslts1)
        
        if not os.path.exists(r'../GIS'+'/'+cfM['site_name']+'/'+'mod'+'/'+run):
            os.makedirs(r'../GIS'+'/'+cfM['site_name']+'/'+'mod'+'/'+run)
        out_fshntN = r'../GIS'+'/'+cfM['site_name']+'/'+'mod'+'/'+run+'/'+'Grid'+prd[0]+'.shp'
#                               
#        arcpy.CopyFeatures_management(in_fshnt, out_fshntN)    

        out_fshntN=copyShapeFile(in_fshnt)    
        
        
        
        ####3print (out_fshntN) 
        #import osgeo.ogr as ogr
        #import osgeo.osr as osr
        #driver = ogr.GetDriverByName("ESRI Shapefile")
        #data_source = driver.CreateDataSource(out_fshntN)
        #srs=osr.SpatialReference()
        #srs.ImportFromEPSG(4326)
        #data_source.CreateLayer("FID", srs, ogr.wkbPoint )
  
        convertVectorToArray(in_fshnt)
        
#        arcpy.da.ExtendTable(out_fshntN,"FID",mod_rslts1,"ID")
#
#        shapeN = out_fshntN
        out_rasN = r'../GIS'+'/'+cfM['site_name']+'/'+'mod'+'/'+run+'/'+prd[0]+'_Ts'
        out_rasN_Ta = r'../GIS'+'/'+cfM['site_name']+'/'+'mod'+'/'+run+'/'+prd[0]+'_Ta'  
        
        #longitude, latitude, value = read_grd(obs_ras)
        #print (value)
        #exit (1)
#  
#        arcpy.PolygonToRaster_conversion(in_features=shapeN, value_field="Ts", out_rasterdataset=out_rasN, cell_assignment="CELL_CENTER", priority_field="NONE", cellsize="30")
#        arcpy.PolygonToRaster_conversion(in_features=shapeN, value_field="Ta", out_rasterdataset=out_rasN_Ta, cell_assignment="CELL_CENTER", priority_field="NONE", cellsize="30")
#        
#        new_mod_tsN = SetNull(IsNull(obsN),out_rasN)
#        new_mod_taN = SetNull(IsNull(obsN),out_rasN_Ta)

        new_mod_tsN = out_rasN
        new_mod_taN = out_rasN_Ta

        new_mod_tsN.save( r'../GIS'+'/'+cfM['site_name']+'/'+'mod'+'/'+run+'/'+'30mFn'+prd[0]+'_Ts')
        new_mod_taN.save( r'../GIS'+'/'+cfM['site_name']+'/'+'mod'+'/'+run+'/'+'30mFn'+prd[0]+'_Ta')

#        mod_rasN = arcpy.RasterToASCII_conversion(r'../GIS'+'/'+cfM['site_name']+'/'+'mod'+'/'+run+'/'+'30mFn'+prd[0]+'_Ts', r'../GIS'+'/'+cfM['site_name']+'/'+'mod'+'/'+run+'/'+'30mFn'+prd[0]+'_Ts.txt')
#     
#        file_m = open(r'../GIS'+'/'+cfM['site_name']+'/'+'mod'+'/'+run+'/'+'30mFn'+prd[0]+'_Ts.txt', 'r')
#        file_o = open(obs_ras, 'r')
#     
#        obs=[]
#        mod=[]
#        
#        for line in file_m:
#            mod.append(line.split(' '))
#        
#        
#        for line in file_o:
#            obs.append(line.split(' '))
#        
#        mod = mod[6:]
#        obs = obs[6:]
#        
#        
#        mod=list(itertools.chain(*mod[0:]))
#        obs=list(itertools.chain(*obs[0:]))
#        
#        
#        mod1 = []
#        obs1 = []
#        for i in range(0, len(obs)):
#            if obs[i] != '-9999': 
#                    if (obs[i] !='\n') :
#                          if (mod[i] !='\n'):    
#                                      mod1.append(float(mod[i]))
#                                      obs1.append(float(obs[i]))
#        
#        b, a, r_value, p_value, std_err = stats.linregress(obs1,mod1)
#        ei  = [x-y for x,y, in zip(mod1,obs1)]
#        r_value = round(r_value,1)
#        rmse = round(np.sqrt(sum(np.transpose(ei)*ei)/len(obs1)),1)   
#        
#        x=[-100,100]
#        y=[-100,100]
#        
#        ax1 = fig.add_subplot(111)
#        ax1.scatter(obs1,mod1,alpha=0.4)
#        
#        ax1.set_xlim((  (min(obs1)*0.95), (max(obs1)*1.1)  ))
#        ax1.set_ylim((  (min(obs1)*0.95), (max(obs1)*1.1)  ))
#        ax1.text((min(obs1)*1.1),(max(obs1)*0.95), 'RMSE='+str(rmse))
#        ax1.text((min(obs1)*1.1),(max(obs1)*0.9), '$r^2$='+str(r_value))
#        ax1.text((min(obs1)*1.1),(max(obs1)*0.85), '$n$='+str(len(obs1)))
#
#
#
#        ax1.set_xlabel('Observed $T_{surf}$ ($^\circ$C)')
#        ax1.set_ylabel('Modelled $T_{surf}$ ($^\circ$C)')
#        ax1.plot(x,y, 'k--')
#        
#        if not os.path.exists(r'..\plots'+'/'+cfM['site_name']+'/'+run):
#            os.makedirs(r'..\plots'+'/'+cfM['site_name']+'/'+run)
#        fig.tight_layout(pad=0.4,w_pad=0.5,h_pad=1.0)
#        fig.savefig(r'..\plots'+'/'+cfM['site_name']+'/'+run+'/'+prd+'_scatter.png') 
#        fig.clf()
#        ax1.cla()
#        mp.close('all') 



ControlFileName = sys.argv[1]
cfM = ConfigObj(ControlFileName)

dateparse = lambda x: pandas.datetime.strptime(x, cfM['date_fmt'])      # parse dates for input met file using format defined in control file
#print dateparse
run = cfM['run_name']                               # model run name 
#print (run)
#tmstp = cfM['timestep']                             # time step (minutes)


#lc_data = pandas.read_csv(os.path.join('..','input',cfM['site_name'],'LC',cfM['inpt_lc_file'])) # reads the input land cover data
#print (lc_data)
#stations = lc_data['FID'].values
#print (stations)

mod_rslts = np.load('../output/Mawson/Mawson-grid.npy')  

#val_ts(cfM,run,stations,mod_rslts)    

#met_file = os.path.join('..','input',cfM['site_name'],'MET',cfM['inpt_met_file'])    # input meteorological forcing data file 
#met_data = pandas.read_csv(met_file,parse_dates=['datetime'], date_parser=dateparse,index_col=['datetime']) # convert to data frame

#obs_file = os.path.join('..','obs',cfM['site_name'],'stations_MET',cfM['inpt_obs_file'])  # file for observed AWS data
#obs_data = pandas.read_csv(obs_file,parse_dates =['TIMESTAMP'], date_parser=dateparse, index_col=['TIMESTAMP'])  # reads observed AWS data and puts in dataframe  | only gets used if validating air temp (plotting.py)

#date1A=dt.datetime(int(cfM['date1A'][0]), int(cfM['date1A'][1]),int(cfM['date1A'][2]), int(cfM['date1A'][3]))   ## the date/time that the simulation starts
#date1=dt.datetime(int(cfM['date1'][0]), int(cfM['date1'][1]),int(cfM['date1'][2]), int(cfM['date1'][3]))        ## the date/time for period of interest (i.e. before this will not be saved)
#date2=dt.datetime(int(cfM['date2'][0]), int(cfM['date2'][1]),int(cfM['date2'][2]), int(cfM['date2'][3]))        ## end date/time of simulation 
#tD = date2-date1A   ## time difference between start and end date
#nt=divmod(tD.days * 86400 + tD.seconds, (60*int(tmstp)))[0] # number of timesteps
#date_range = pandas.date_range(date1,date2,freq= tmstp + 'T')               #  date range for model period 
#date_range1A =pandas.date_range(date1A,(date2-timedelta(hours=1)),freq= tmstp + 'T') # date range for model period (i.e. including spin-up period)
#Dats={'date1A':date1A, 'date1':date1, 'date2':date2, 'date_range':date_range,'date_rangeA':date_range1A} # this is a dictionary with all the date/time information 
    
#val_ta(cfM,met_data,stations,obs_data,mod_rslts,Dats)

gis(cfM,mod_rslts,run)
