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


###ST_Projection


```SQL
ADD JAR hdfs:///user/nifi/livy-jars/hive_geo-0.0.1-SNAPSHOT.jar;
CREATE TEMPORARY FUNCTION ST_Projection AS 'com.dot.hive.spatial.GenericST_ProjectionUDF';


--Polygon Projection
SELECT ST_Projection('POLYGON((-73.99184674763184 40.851169994189114,-73.99177736650066 40.84940709139823,-73.98960021718895 40.84858352856829,-73.98749236284553 40.849522800877686,-73.98756158290558 40.85128570584109,-73.98973881838064 40.852109336325405,-73.98980812679454 40.85387229829392,-73.99198552554876 40.85469591594949,-73.99409352970484 40.85375650398033,-73.99402406020228 40.85199354419126,-73.99184674763184 40.851169994189114)),POLYGON((-73.99184674763184 40.851169994189114,-73.99177736650066 40.84940709139823,-73.98960021718895 40.84858352856829,-73.98749236284553 40.849522800877686,-73.98756158290558 40.85128570584109,-73.98973881838064 40.852109336325405,-73.98980812679454 40.85387229829392,-73.99198552554876 40.85469591594949,-73.99409352970484 40.85375650398033,-73.99402406020228 40.85199354419126,-73.99184674763184 40.851169994189114))','EPSG:4326', 'EPSG:3857') AS wkt;

--Point Projection
SELECT ST_projection('POINT(-105.89054624819013 -30.32377110841559)','EPSG:4326', 'EPSG:3857') AS wkt;

--MultiPolygon Projection
SELECT ST_projection('MULTIPOLYGON(((-71.1031880899493 42.3152774590236,-71.1031627617667 42.3152960829043,-71.102923838298 42.3149156848307,-71.1023097974109 42.3151969047397,-71.1019285062273 42.3147384934248,-71.102505233663 42.3144722937587,-71.10277487471 42.3141658254797,-71.103113945163 42.3142739188902,-71.10324876416 42.31402489987,-71.1033002961013 42.3140393340215,-71.1033488797549 42.3139495090772,-71.103396240451 42.3138632439557,-71.1041521907712 42.3141153348029,-71.1041411411543 42.3141545014533,-71.1041287795912 42.3142114839058,-71.1041188134329 42.3142693656241,-71.1041112482575 42.3143272556118,-71.1041072845732 42.3143851580048,-71.1041057218871 42.3144430686681,-71.1041065602059 42.3145009876017,-71.1041097995362 42.3145589148055,-71.1041166403905 42.3146168544148,-71.1041258822717 42.3146748022936,-71.1041375307579 42.3147318674446,-71.1041492906949 42.3147711126569,-71.1041598612795 42.314808571739,-71.1042515013869 42.3151287620809,-71.1041173835118 42.3150739481917,-71.1040809891419 42.3151344119048,-71.1040438678912 42.3151191367447,-71.1040194562988 42.3151832057859,-71.1038734225584 42.3151140942995,-71.1038446938243 42.3151006300338,-71.1038315271889 42.315094347535,-71.1037393329282 42.315054824985,-71.1035447555574 42.3152608696313,-71.1033436658644 42.3151648370544,-71.1032580383161 42.3152269126061,-71.103223066939 42.3152517403219,-71.1031880899493 42.3152774590236)),((-71.1043632495873 42.315113108546,-71.1043583974082 42.3151211109857,-71.1043443253471 42.3150676015829,-71.1043850704575 42.3150793250568,-71.1043632495873 42.315113108546)))','EPSG:4326', 'EPSG:3857') AS wkt;

--Linestring projection
SELECT ST_projection('LINESTRING(-71.160281 42.258729,-71.160837 42.259113,-71.161144 42.25932)','EPSG:4326', 'EPSG:3857') AS wkt;
```



## Issues
When using with Hive this function may be bundled with a version of jackson-core that is not compatible. Please update the pom.xml in order to match the version deployed on your HDP cluster with Hive. 


**Example:** HDP 3.1.5

```XML
		<dependency>
			<groupId>com.fasterxml.jackson.core</groupId>
			<artifactId>jackson-core</artifactId>
			<version>2.10.0</version>
		</dependency>
```
