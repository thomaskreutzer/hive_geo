package com.dot.hive.spatial.example;

import java.util.ArrayList;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.osgeo.proj4j.BasicCoordinateTransform;
import org.osgeo.proj4j.CRSFactory;
import org.osgeo.proj4j.CoordinateReferenceSystem;
import org.osgeo.proj4j.ProjCoordinate;
import org.locationtech.jts.geom.Polygon;

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
	}

}
