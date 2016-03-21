package com.meidusa.fastbson.serializer;

import com.meidusa.fastbson.parse.BSONScanner;
import com.meidusa.fastbson.parse.BSONWriter;

public class PrimitiveDoubleSerializer implements ObjectSerializer{

	public Object deserialize(BSONScanner scanner, ObjectSerializer[] subSerializer, int i) {
		return scanner.readDouble();
	}

	public void serialize(BSONWriter writer, Object value, ObjectSerializer[] subSerializer, int i) {
		writer.writeValue(((Double)value).doubleValue());
		
	}

	public Class<?> getSerializedClass() {
		return double.class;
	}

	public byte getBsonSuffix() {
		return 0x01;
	}

}
