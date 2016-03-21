package com.meidusa.fastbson.serializer;

import com.meidusa.fastbson.parse.BSONScanner;
import com.meidusa.fastbson.parse.BSONWriter;

public class PrimitiveIntSerializer implements ObjectSerializer{

	public Object deserialize(BSONScanner scanner, ObjectSerializer[] subSerializer, int i) {
		return scanner.readInt();
	}

	public void serialize(BSONWriter writer, Object value, ObjectSerializer[] subSerializer, int i) {
		writer.writeValue(((Integer)value).intValue());
		
	}

	public Class<?> getSerializedClass() {
		return int.class;
	}

	public byte getBsonSuffix() {
		return 0x10;
	}

}
