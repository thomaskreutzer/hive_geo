package com.dot.hive.spatial;

import java.util.ArrayList;

import org.apache.hadoop.hive.ql.exec.Description;
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
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.osgeo.proj4j.BasicCoordinateTransform;
import org.osgeo.proj4j.CRSFactory;
import org.osgeo.proj4j.CoordinateReferenceSystem;
import org.osgeo.proj4j.ProjCoordinate;


@Description(name = "GenericST_PolyProjectionUDF",
value = "_FUNC_(string wktpoly, string crsFrom, string crsTo) - returns WKT Polygon\n",
extended = "Returns NULL if any argument is NULL.\n"
+ "Example:\n"
+ "  > CREATE TEMPORARY FUNCTION ST_PolyProjection AS 'com.dot.hive.spatial.GenericST_PolyProjectionUDF';\n"
+ "  > SELECT ST_PolyProjection('POLYGON((-73.99184674763184 40.851169994189114,-73.99177736650066 40.84940709139823,-73.98960021718895 40.84858352856829,-73.98749236284553 40.849522800877686,-73.98756158290558 40.85128570584109,-73.98973881838064 40.852109336325405,-73.98980812679454 40.85387229829392,-73.99198552554876 40.85469591594949,-73.99409352970484 40.85375650398033,-73.99402406020228 40.85199354419126,-73.99184674763184 40.851169994189114))'\n"
+ "  > ,'EPSG:4326', 'EPSG:3857') AS wkt;"
)

public class GenericST_PolyProjectionUDF extends GenericUDF {
	private final Text wktOut = new Text();
	PrimitiveObjectInspector inputOI0;
	PrimitiveObjectInspector inputOI1;
	PrimitiveObjectInspector inputOI2;
	CRSFactory crsFactory;
	GeometryFactory geometryFactory;
	WKTReader wktReader;
	
	public GenericST_PolyProjectionUDF() {
		
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
		return wktOut;
	}
	
	@Override
	public String getDisplayString(String[] children) {
		return getStandardDisplayString("GenericST_PolyProjectionUDF", children, ",");
	}
	
}
