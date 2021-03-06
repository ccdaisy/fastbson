package com.meidusa.fastbson.serializer;

import com.meidusa.fastbson.parse.BSONScanner;
import com.meidusa.fastbson.parse.BSONWriter;

public class PrimitiveFloatSerializer implements ObjectSerializer{

	public Object deserialize(BSONScanner scanner, ObjectSerializer[] subSerializer, int i) {
		return (float)scanner.readDouble();
	}

	public void serialize(BSONWriter writer, Object value, ObjectSerializer[] subSerializer, int i) {
		writer.writeValue(((Float)value).floatValue());		
	}

	public Class<?> getSerializedClass() {
		return float.class;
	}

	public byte getBsonSuffix() {
		return 0x01;
	}

}
