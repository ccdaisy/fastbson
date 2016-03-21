package com.meidusa.fastbson;

import com.meidusa.fastbson.parse.BSONScanner;
import com.meidusa.fastbson.parse.BSONWriter;
import com.meidusa.fastbson.serializer.ObjectSerializer;

public class SerializerGroup {

	ObjectSerializer rawSerialzer;
	ObjectSerializer[] subSerializers;
	
	public Object deserialize(BSONScanner scanner) {
		return rawSerialzer.deserialize(scanner, subSerializers, 0);
	}

	public void serialize(BSONWriter writer, Object value) {
		rawSerialzer.serialize(writer, value, subSerializers, 0);
		
	}

}
