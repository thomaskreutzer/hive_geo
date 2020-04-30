# Hive Geo

Additional functions as needed to be added to Hive that are missing in ESRI. 

## Function List

1. ST_Azimuth

## Function Examples

###ST_Azimuth


```SQL
ADD JAR hdfs:///path/to/the_jar/hive_geo-0.0.1-SNAPSHOT.jar;

CREATE TEMPORARY FUNCTION ST_Azimuth AS 'com.dot.hive.spatial.GenericST_AzimuthUDF';
SELECT ST_Azimuth('POINT(-73.99191613398102 40.85293293570688)', 'POINT(-73.98966951517899 40.85034641308286)');

+---------------------+
|         _c0         |
+---------------------+
| 2.4264066074139135  |
+---------------------+
```
