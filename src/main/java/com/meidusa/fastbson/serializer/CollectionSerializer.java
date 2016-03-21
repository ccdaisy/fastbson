package com.meidusa.fastbson.serializer;

import java.util.Collection;

import com.meidusa.fastbson.exception.SerializeException;
import com.meidusa.fastbson.parse.BSONScanner;
import com.meidusa.fastbson.parse.BSONWriter;
import com.meidusa.fastbson.util.BSON;

public abstract class CollectionSerializer extends AbstractObjectSerializer {

	public Object deserialize(BSONScanner scanner, ObjectSerializer[] subSerializer, int i) {
		ObjectSerializer subSerializerCurrent = subSerializer[i++];
		scanner.skip(4);
		Collection coll;
		try {
			coll = (Collection) getSerializedClass().newInstance();
		} catch (Exception e) {
			throw new SerializeException("couldn't happened....");
		}
		while (scanner.readType() != BSON.EOO) {
			scanner.readCString();
			coll.add(subSerializerCurrent.deserialize(scanner, subSerializer, i));
		}
		return coll;
	}

	public void serialize(BSONWriter writer, Object value, ObjectSerializer[] subSerializer, int i) {
		ObjectSerializer subSerializerCurrent = subSerializer[i++];
		Collection listValue = (Collection) value;
		writer.beginArray();
		int j = 0;
		for (Object o : listValue) {
			if(subSerializerCurrent instanceof UnknownTypeSerializer) {
				writer.write(getUnknownBsonSuffix(o));
			} else {
				writer.write(subSerializerCurrent.getBsonSuffix());
			}
			writer.writeCString(String.valueOf(j++));
			subSerializerCurrent.serialize(writer, o, subSerializer, i);

		}
		writer.endArray();

	}

	public abstract Class<?> getSerializedClass();

	public byte getBsonSuffix() {
		return 0x03;
	}

}
