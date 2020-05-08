package com.dot.hive.spatial;

import java.util.ArrayList;

import org.apache.hadoop.hive.ql.exec.UDFArgumentException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDF;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.PrimitiveObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;
import org.apache.hadoop.io.Text;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.osgeo.proj4j.BasicCoordinateTransform;
import org.osgeo.proj4j.CRSFactory;
import org.osgeo.proj4j.CoordinateReferenceSystem;
import org.osgeo.proj4j.ProjCoordinate;

public class GenericST_ProjectionUDF extends GenericUDF {
	private final Text wktOut = new Text();
	PrimitiveObjectInspector inputOI0;
	PrimitiveObjectInspector inputOI1;
	PrimitiveObjectInspector inputOI2;
	CRSFactory crsFactory;
	GeometryFactory geometryFactory;
	WKTReader wktReader;
	
	public GenericST_ProjectionUDF() {
		
		crsFactory = new CRSFactory();
		geometryFactory = new GeometryFactory();
		wktReader = new WKTReader();
	}
	
	
	@Override
	public ObjectInspector initialize(ObjectInspector[] arguments) throws UDFArgumentException {
		checkArgsSize(arguments, 3, 3);
		checkArgPrimitive(arguments, 0);
		
		inputOI0 = (PrimitiveObjectInspector)arguments[0];
		inputOI1 = (PrimitiveObjectInspector)arguments[1];
		inputOI2 = (PrimitiveObjectInspector)arguments[2];
		ObjectInspector outputOI = PrimitiveObjectInspectorFactory.writableStringObjectInspector;
		return outputOI;
	}
	
	@Override
	public Object evaluate(DeferredObject[] arguments) throws HiveException {
		Object arg0 = arguments[0].get(); //WKT
		Object arg1 = arguments[1].get(); //FROM - Example - EPSG:4326
		Object arg2 = arguments[2].get(); //TO - Example - EPSG:3857
		
		//Get the resolution variable
		String wkt = (String) inputOI0.getPrimitiveJavaObject(arg0);
		String fromCRS = (String) inputOI1.getPrimitiveJavaObject(arg1);
		String toCRS = (String) inputOI2.getPrimitiveJavaObject(arg2);
		
		CoordinateReferenceSystem srcCrs = crsFactory.createFromName(fromCRS);
		CoordinateReferenceSystem dstCrs = crsFactory.createFromName(toCRS);
		BasicCoordinateTransform transform = new BasicCoordinateTransform(srcCrs, dstCrs);
		
		Geometry g;
		try {
			g = wktReader.read(wkt);
		} catch (ParseException e) {
			throw new HiveException("Could not parse, not valid polygon WKT");
		}
		
		
		if( g instanceof Polygon ) {
			ArrayList<Coordinate> points = new ArrayList<Coordinate>();
			for (int i=0; i<g.getCoordinates().length; i++) {
				System.out.println("Lng: " + g.getCoordinates()[i].x + " Lat: " + g.getCoordinates()[i].y);
				ProjCoordinate sCoord = new ProjCoordinate(g.getCoordinates()[i].x, g.getCoordinates()[i].y);
				ProjCoordinate dCoord = new ProjCoordinate();
				ProjCoordinate c = transform.transform(sCoord, dCoord);
				points.add(new Coordinate(c.x, c.y));
			}
			
			Polygon newPoly = geometryFactory.createPolygon((Coordinate[]) points.toArray(new Coordinate[] {}));
			wktOut.set(newPoly.toString());
		}
		else if ( g instanceof Point ) {
			ProjCoordinate sCoord = new ProjCoordinate(g.getCoordinate().x, g.getCoordinate().y);
			ProjCoordinate dCoord = new ProjCoordinate();
			ProjCoordinate c = transform.transform(sCoord, dCoord);
			Point pnt = geometryFactory.createPoint( new Coordinate(c.x, c.y) );
			wktOut.set(pnt.toString());
		}
		else if ( g instanceof MultiPolygon ) {
			ArrayList<Polygon> polygons = new ArrayList<Polygon>();
			//Loop all the multipolygon's geometries
			for (int i=0; i<g.getNumGeometries(); i++) {
				//Loop the coordinates in each geometry
				ArrayList<Coordinate> pnts = new ArrayList<Coordinate>();
				for (int x=0; x<g.getGeometryN(i).getCoordinates().length; x++) {
					ProjCoordinate sCoord = new ProjCoordinate(g.getGeometryN(i).getCoordinates()[x].x, g.getGeometryN(i).getCoordinates()[x].y);
					ProjCoordinate dCoord = new ProjCoordinate();
					ProjCoordinate c = transform.transform(sCoord, dCoord);
					pnts.add(new Coordinate(c.x, c.y));
				}
				polygons.add( geometryFactory.createPolygon( (Coordinate[]) pnts.toArray(new Coordinate[] {}) ) );
			}
			MultiPolygon multiPolygon = geometryFactory.createMultiPolygon( (Polygon[]) polygons.toArray(new Polygon[] {}) );
			wktOut.set(multiPolygon.toString());
		} 
		else if ( g instanceof LineString ) {
			ArrayList<Coordinate> points = new ArrayList<Coordinate>();
			for (int i=0; i< g.getCoordinates().length; i++) {
				System.out.println("Lng: " + g.getCoordinates()[i].x + " Lat: " + g.getCoordinates()[i].y);
				ProjCoordinate sCoord = new ProjCoordinate(g.getCoordinates()[i].x, g.getCoordinates()[i].y);
				ProjCoordinate dCoord = new ProjCoordinate();
				ProjCoordinate c = transform.transform(sCoord, dCoord);
				points.add(new Coordinate(c.x, c.y));
			}
			LineString lineString = geometryFactory.createLineString((Coordinate[]) points.toArray(new Coordinate[] {}));
			wktOut.set(lineString.toString());
		}
		else {
			throw new HiveException("WKT mismatch for ST_Projection! This function allows valid Polygon, Point, MultiPolygon and LineString.");
		}
		return wktOut;
	}
	
	@Override
	public String getDisplayString(String[] children) {
		return getStandardDisplayString("GenericST_ProjectionUDF", children, ",");
	}
}
