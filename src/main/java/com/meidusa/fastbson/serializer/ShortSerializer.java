package com.meidusa.fastbson.serializer;

import com.meidusa.fastbson.parse.BSONScanner;
import com.meidusa.fastbson.parse.BSONWriter;

public class ShortSerializer implements ObjectSerializer{

	public Object deserialize(BSONScanner scanner, ObjectSerializer[] subSerializer, int i) {
		return Short.valueOf((short)scanner.readInt());
	}

	public void serialize(BSONWriter writer, Object value, ObjectSerializer[] subSerializer, int i) {
		writer.writeValue((Short)value);
		
	}

	public Class<?> getSerializedClass() {
		return short.class;
	}

	public byte getBsonSuffix() {
		return 0x10;
	}

}
