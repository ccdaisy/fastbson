package com.meidusa.fastbson.serializer;

import com.meidusa.fastbson.parse.BSONScanner;
import com.meidusa.fastbson.parse.BSONWriter;

public class PrimitiveLongSerializer implements ObjectSerializer{

	public Object deserialize(BSONScanner scanner, ObjectSerializer[] subSerializer, int i) {
		return Long.valueOf(scanner.readLong());
	}

	public void serialize(BSONWriter writer, Object value, ObjectSerializer[] subSerializer, int i) {
		writer.writeValue(((Long)value).longValue());
	}

	public Class<?> getSerializedClass() {
		return long.class;
	}

	public byte getBsonSuffix() {
		return 0x12;
	}

}
