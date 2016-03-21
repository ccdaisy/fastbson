package com.meidusa.fastbson.serializer;

import com.meidusa.fastbson.parse.BSONScanner;
import com.meidusa.fastbson.parse.BSONWriter;

public class BooleanSerializer implements ObjectSerializer{

	public Object deserialize(BSONScanner scanner, ObjectSerializer[] subSerializer, int i) {
		return scanner.readBoolean();
	}

	public void serialize(BSONWriter writer, Object value, ObjectSerializer[] subSerializer, int i) {
		writer.writeValue((Boolean)value);
		
	}

	public Class<?> getSerializedClass() {
		return Boolean.class;
	}

	public byte getBsonSuffix() {
		return 0x08;
	}

}
