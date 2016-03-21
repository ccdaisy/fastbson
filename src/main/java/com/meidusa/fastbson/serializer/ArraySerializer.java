package com.meidusa.fastbson.serializer;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import com.meidusa.fastbson.parse.BSONScanner;
import com.meidusa.fastbson.parse.BSONWriter;
import com.meidusa.fastbson.util.BSON;

public class ArraySerializer extends AbstractObjectSerializer {
	class ArrayObject {
		
		Class<?> clazz;
		
		List<Object> list = new ArrayList<Object>();
		
		void add(Object o) {
			list.add(o);
		}
		
		Object getAll() {
			Object array = Array.newInstance(clazz, list.size());
			for (int i = 0; i < Array.getLength(array); i++) {
				Array.set(array, i, list.get(i));
			}
			return array;
		}
	}
	
	public Object deserialize(BSONScanner scanner, ObjectSerializer[] subSerializer, int i) {
		ObjectSerializer subSerializerCurrent = subSerializer[i++];
		ArrayObject arr = new ArrayObject();
		scanner.skip(4);
		arr.clazz = subSerializerCurrent.getSerializedClass();
		while(scanner.readType() != BSON.EOO) {
			scanner.readCString();
			arr.add(subSerializerCurrent.deserialize(scanner, subSerializer, i));
		}
		return arr.getAll();
	}

	public void serialize(BSONWriter writer, Object value, ObjectSerializer[] subSerializer, int i) {
		ObjectSerializer subSerializerCurrent = subSerializer[i++];
		writer.beginArray();
		for ( int j=0; j<Array.getLength(value); j++) {
			Object object = Array.get(value, j);
			if(subSerializerCurrent instanceof UnknownTypeSerializer) {
				writer.write(getUnknownBsonSuffix(object));
			} else {
				writer.write(subSerializerCurrent.getBsonSuffix());
			}
			writer.writeCString(String.valueOf(j));
			subSerializerCurrent.serialize(writer, object, subSerializer, i);
		}
		writer.endArray();

	}

	public Class<?> getSerializedClass() {
		// array object don't have a specified object , so return null instead
		return null;
	}

	public byte getBsonSuffix() {
		return 0x04;
	}


}
