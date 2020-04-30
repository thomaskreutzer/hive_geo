package com.dot.hive.spatial;

import org.apache.hadoop.hive.ql.exec.UDFArgumentException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDF;
import org.apache.hadoop.hive.serde2.io.DoubleWritable;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.PrimitiveObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;
import org.h2gis.functions.spatial.trigonometry.ST_Azimuth;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;


public class GenericST_AzimuthUDF extends GenericUDF {
	private final DoubleWritable doubleReturn = new DoubleWritable();
	PrimitiveObjectInspector inputOI0;
	PrimitiveObjectInspector inputOI1;
	WKTReader wktReader;
	ST_Azimuth stAzimuth;
	
	//Constructor
	public GenericST_AzimuthUDF() {
		wktReader = new WKTReader();
	}
	
	@Override
	public ObjectInspector initialize(ObjectInspector[] arguments) throws UDFArgumentException {
		checkArgsSize(arguments, 2, 2);
		checkArgPrimitive(arguments, 0);
		
		inputOI0 = (PrimitiveObjectInspector)arguments[0];
		inputOI1 = (PrimitiveObjectInspector)arguments[1];
		ObjectInspector outputOI = PrimitiveObjectInspectorFactory.writableDoubleObjectInspector;
		return outputOI;
	}
	
	@Override
	public Object evaluate(DeferredObject[] arguments) throws HiveException {
		Geometry p1;
		Geometry p2;
		
		Object arg0 = arguments[0].get(); //point 01
		Object arg1 = arguments[1].get(); //point 02
		
		if (arg0 == null) return null;
		if (arg1 == null) return null;
		
		//Get the resolution variable
		String point1 = (String) inputOI0.getPrimitiveJavaObject(arg0);
		String point2 = (String) inputOI1.getPrimitiveJavaObject(arg1);
		
		try {
			p1 = wktReader.read(point1);
			p2 = wktReader.read(point2);
		} catch (ParseException e) {
			throw new HiveException("Geometry was not created from point.");
		}
		
		Double d = ST_Azimuth.azimuth(p1, p2);

		doubleReturn.set(d);
		return doubleReturn;
	}

	@Override
	public String getDisplayString(String[] children) {
		return getStandardDisplayString("GenericST_AzimuthUDF", children, ",");
	}
}
