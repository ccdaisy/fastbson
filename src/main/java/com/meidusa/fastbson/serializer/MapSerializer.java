package com.meidusa.fastbson.serializer;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.meidusa.fastbson.parse.BSONScanner;
import com.meidusa.fastbson.parse.BSONWriter;
import com.meidusa.fastbson.util.BSON;

public abstract class MapSerializer extends AbstractObjectSerializer{

	public Object deserialize(BSONScanner scanner, ObjectSerializer[] subSerializer, int i) {
		ObjectSerializer subSerializerCurrent = subSerializer[i++];
		scanner.skip(4);
		Map map = new HashMap();
		String name = "";
		while(scanner.readType() != BSON.EOO) {
			name = scanner.readCString();
			Object o = subSerializerCurrent.deserialize(scanner, subSerializer, i);
			map.put(name, o);
		}
		return map;
	}

	public void serialize(BSONWriter writer, Object value, ObjectSerializer[] subSerializer, int i) {
		ObjectSerializer subSerializerCurrent = subSerializer[i++];
		Map map = (Map) value;
		writer.beginObject();
		for (Iterator iterator = map.entrySet().iterator(); iterator.hasNext();) {
			Entry entry = (Entry) iterator.next();
			if(subSerializerCurrent instanceof UnknownTypeSerializer) {
				writer.write(getUnknownBsonSuffix(entry.getValue()));
			} else {
				writer.write(subSerializerCurrent.getBsonSuffix());
			}
			writer.writeCString((String) entry.getKey());
			subSerializerCurrent.serialize(writer, entry.getValue(), subSerializer, i);
		}
		writer.endObject();
		
	}

	public abstract Class<?> getSerializedClass();

	public byte getBsonSuffix() {
		return 0x03;
	}

}
