package com.meidusa.fastbson.serializer;

import com.meidusa.fastbson.parse.BSONScanner;
import com.meidusa.fastbson.parse.BSONWriter;

public interface ObjectSerializer {
	Object deserialize(BSONScanner scanner,ObjectSerializer[] subSerializer, int i);
	
	void serialize(BSONWriter writer, Object value, ObjectSerializer[] subSerializer, int i);

	Class<?> getSerializedClass();
	
	byte getBsonSuffix();
	
}
