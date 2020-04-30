package com.dot.hive.spatial.example;

import java.io.IOException;

import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDF.DeferredJavaObject;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDF.DeferredObject;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfoFactory;
import org.apache.hadoop.io.Text;

import com.dot.hive.spatial.GenericST_AzimuthUDF;



public class ExampleGenericST_AzimuthUDF {
	
	public static void main(String[] args) throws HiveException, IOException {
		ExampleGenericST_AzimuthUDF t = new ExampleGenericST_AzimuthUDF();
		t.testConst();
	}
	
	public void testConst() throws HiveException, IOException {
		String point1 = "POINT(-73.99191613398102 40.85293293570688)";
		String point2 = "POINT(-73.98966951517899 40.85034641308286)";
		
		Text point1Writeable = new Text(point1);
		Text point2Writeable = new Text(point2);
		
		ObjectInspector valueOI0 = PrimitiveObjectInspectorFactory.getPrimitiveWritableConstantObjectInspector(TypeInfoFactory.stringTypeInfo, point1Writeable);
		ObjectInspector valueOI1 = PrimitiveObjectInspectorFactory.getPrimitiveWritableConstantObjectInspector(TypeInfoFactory.stringTypeInfo, point2Writeable);
		ObjectInspector[] oiArgs = { valueOI0, valueOI1 };
		
		DeferredObject valueObj0 = new DeferredJavaObject(point1Writeable);
		DeferredObject valueObj1 = new DeferredJavaObject(point2Writeable);
		DeferredObject[] doArgs = { valueObj0, valueObj1 };
		
		GenericST_AzimuthUDF udf = new GenericST_AzimuthUDF();
		udf.initialize(oiArgs);
		
		System.out.println(udf.evaluate(doArgs));
		
		udf.close();
		
	}


}
