package com.meidusa.fastbson.serializer;

import java.math.BigDecimal;

import com.meidusa.fastbson.parse.BSONScanner;
import com.meidusa.fastbson.parse.BSONWriter;

public class BigDecimalSerializer implements ObjectSerializer{

	public Object deserialize(BSONScanner scanner, ObjectSerializer[] subSerializer, int i) {
		return scanner.readBigDecimal();
	}

	public void serialize(BSONWriter writer, Object value, ObjectSerializer[] subSerializer, int i) {
		writer.writeValue((BigDecimal)value);
		
	}

	public Class<?> getSerializedClass() {
		return BigDecimalSerializer.class;
	}

	public byte getBsonSuffix() {
		return 0x7E;
	}

}
