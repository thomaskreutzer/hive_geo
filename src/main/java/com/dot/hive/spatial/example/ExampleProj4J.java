package com.dot.hive.spatial.example;

import java.util.ArrayList;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.osgeo.proj4j.BasicCoordinateTransform;
import org.osgeo.proj4j.CRSFactory;
import org.osgeo.proj4j.CoordinateReferenceSystem;
import org.osgeo.proj4j.ProjCoordinate;



public class ExampleProj4J {

	public static void main(String[] args) throws ParseException {
		CRSFactory factory = new CRSFactory();
		GeometryFactory geometryFactory = new GeometryFactory();
		CoordinateReferenceSystem srcCrs = factory.createFromName("EPSG:4326");
		CoordinateReferenceSystem dstCrs = factory.createFromName("EPSG:3857");
		String n = "\n";

		BasicCoordinateTransform transform = new BasicCoordinateTransform(srcCrs, dstCrs);

		// Note these are x, y so lng, lat
		ProjCoordinate srcCoord = new ProjCoordinate(-73.99191613398102, 40.85293293570688);
		ProjCoordinate dstCoord = new ProjCoordinate();

		// Writes result into dstCoord
		ProjCoordinate converted = transform.transform(srcCoord, dstCoord);
		
		
		Double lat = converted.x;
		Double lng = converted.y;
		
		
		System.out.println("Converted Lat: " + lat + "\n" + "Converted Lng: " + lng);
		String poly = "POLYGON((-73.99184674763184 40.851169994189114,-73.99177736650066 40.84940709139823,-73.98960021718895 40.84858352856829,-73.98749236284553 40.849522800877686,-73.98756158290558 40.85128570584109,-73.98973881838064 40.852109336325405,-73.98980812679454 40.85387229829392,-73.99198552554876 40.85469591594949,-73.99409352970484 40.85375650398033,-73.99402406020228 40.85199354419126,-73.99184674763184 40.851169994189114))";
		
		WKTReader wktReader = new WKTReader();
		
		Geometry g = wktReader.read(poly);
		
		System.out.println("Class: " + g.getClass() + n +
				"SRID: " + g.getSRID() + n + 
				"Length: " + g.getLength() + n + 
				"Number of Geometries: " + g.getNumGeometries() + n + 
				"Coordinates Length: " + g.getCoordinates().length + n
				);
		
		for (int i=0; i<g.getCoordinates().length; i++) {
			System.out.println(g.getCoordinates()[i]);
		}
		
		//Converted
		ArrayList<Coordinate> points = new ArrayList<Coordinate>();
		
		for (int i=0; i<g.getCoordinates().length; i++) {
			System.out.println("Lng: " + g.getCoordinates()[i].x + " Lat: " + g.getCoordinates()[i].y);
			ProjCoordinate sCoord = new ProjCoordinate(g.getCoordinates()[i].x, g.getCoordinates()[i].y);
			ProjCoordinate dCoord = new ProjCoordinate();
			ProjCoordinate c = transform.transform(sCoord, dCoord);
			points.add(new Coordinate(c.x, c.y));
		}
		
		Polygon newPoly = geometryFactory.createPolygon((Coordinate[]) points.toArray(new Coordinate[] {}));
		
		System.out.println(newPoly.toString());
		
		if(newPoly instanceof Polygon) {
			System.out.println("Is a polygon class type");
		}
		
		
		
		//-----------------------------------------------------------------------------------------------------
		// POINT
		//-----------------------------------------------------------------------------------------------------
		String point = "POINT(-105.89054624819013 -30.32377110841559)";
		Geometry pg = wktReader.read(point);
		
		System.out.println(pg.getClass().toString() + "\nLng: " + pg.getCoordinate().x + "\nLat: " + pg.getCoordinate().y);
		
		ProjCoordinate sCoord2 = new ProjCoordinate(pg.getCoordinate().x, pg.getCoordinate().y);
		ProjCoordinate dCoord2 = new ProjCoordinate();
		ProjCoordinate c2 = transform.transform(sCoord2, dCoord2);
		
		Point pnt = geometryFactory.createPoint( new Coordinate(c2.x, c2.y) );
		
		System.out.println(pnt);
		
		
		
		
		
		//-----------------------------------------------------------------------------------------------------
		// MULTIPOLYGON
		//-----------------------------------------------------------------------------------------------------
		String multi = "MULTIPOLYGON(((-71.1031880899493 42.3152774590236,-71.1031627617667 42.3152960829043,-71.102923838298 42.3149156848307,-71.1023097974109 42.3151969047397,-71.1019285062273 42.3147384934248,-71.102505233663 42.3144722937587,-71.10277487471 42.3141658254797,-71.103113945163 42.3142739188902,-71.10324876416 42.31402489987,-71.1033002961013 42.3140393340215,-71.1033488797549 42.3139495090772,-71.103396240451 42.3138632439557,-71.1041521907712 42.3141153348029,-71.1041411411543 42.3141545014533,-71.1041287795912 42.3142114839058,-71.1041188134329 42.3142693656241,-71.1041112482575 42.3143272556118,-71.1041072845732 42.3143851580048,-71.1041057218871 42.3144430686681,-71.1041065602059 42.3145009876017,-71.1041097995362 42.3145589148055,-71.1041166403905 42.3146168544148,-71.1041258822717 42.3146748022936,-71.1041375307579 42.3147318674446,-71.1041492906949 42.3147711126569,-71.1041598612795 42.314808571739,-71.1042515013869 42.3151287620809,-71.1041173835118 42.3150739481917,-71.1040809891419 42.3151344119048,-71.1040438678912 42.3151191367447,-71.1040194562988 42.3151832057859,-71.1038734225584 42.3151140942995,-71.1038446938243 42.3151006300338,-71.1038315271889 42.315094347535,-71.1037393329282 42.315054824985,-71.1035447555574 42.3152608696313,-71.1033436658644 42.3151648370544,-71.1032580383161 42.3152269126061,-71.103223066939 42.3152517403219,-71.1031880899493 42.3152774590236)),((-71.1043632495873 42.315113108546,-71.1043583974082 42.3151211109857,-71.1043443253471 42.3150676015829,-71.1043850704575 42.3150793250568,-71.1043632495873 42.315113108546)))";
		
		Geometry mpg = wktReader.read(multi);
		
		System.out.println(mpg.getClass().toString() + " Number of Geometries: " + mpg.getNumGeometries() + " Length: " + mpg.getCoordinates().length);
		
		ArrayList<Polygon> plys = new ArrayList<Polygon>();
		//Loop all the multipolygon's geometries
		for (int i=0; i<mpg.getNumGeometries(); i++) {
			//Loop the coordinates in each geometry
			ArrayList<Coordinate> pnts = new ArrayList<Coordinate>();
			for (int x=0; x<mpg.getGeometryN(i).getCoordinates().length; x++) {
				ProjCoordinate sCoord = new ProjCoordinate(mpg.getGeometryN(i).getCoordinates()[x].x, mpg.getGeometryN(i).getCoordinates()[x].y);
				ProjCoordinate dCoord = new ProjCoordinate();
				ProjCoordinate c = transform.transform(sCoord, dCoord);
				pnts.add(new Coordinate(c.x, c.y));
			}
			plys.add( geometryFactory.createPolygon( (Coordinate[]) pnts.toArray(new Coordinate[] {}) ) );
		}
		MultiPolygon multiPolygon = geometryFactory.createMultiPolygon( (Polygon[]) plys.toArray(new Polygon[] {}) );
		
		System.out.println(multiPolygon.toText());
		
		
		
		
		//-----------------------------------------------------------------------------------------------------
		// LINESTRING
		//-----------------------------------------------------------------------------------------------------
		String ls = "LINESTRING(-71.160281 42.258729,-71.160837 42.259113,-71.161144 42.25932)";
		Geometry lsg = wktReader.read(ls);
		System.out.println(lsg.getClass().toString() + " Number of Geometries: " + mpg.getNumGeometries() + " Length: " + mpg.getCoordinates().length);
		ArrayList<Coordinate> lspoints = new ArrayList<Coordinate>();
		
		for (int i=0; i< lsg.getCoordinates().length; i++) {
			System.out.println("Lng: " + lsg.getCoordinates()[i].x + " Lat: " + lsg.getCoordinates()[i].y);
			ProjCoordinate sCoord = new ProjCoordinate(lsg.getCoordinates()[i].x, lsg.getCoordinates()[i].y);
			ProjCoordinate dCoord = new ProjCoordinate();
			ProjCoordinate c = transform.transform(sCoord, dCoord);
			lspoints.add(new Coordinate(c.x, c.y));
		}
		
		
		LineString newLs = geometryFactory.createLineString((Coordinate[]) lspoints.toArray(new Coordinate[] {}));
		
		System.out.println(newLs.toText());
	}

}
