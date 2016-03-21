package com.meidusa.fastbson.serializer;

import com.meidusa.fastbson.parse.BSONScanner;
import com.meidusa.fastbson.parse.BSONWriter;

public abstract class EnumSerializer extends AbstractObjectSerializer{

	@Override
	public Object deserialize(BSONScanner scanner, ObjectSerializer[] subSerializer, int i) {
		return Enum.valueOf(getSerializedClass(), scanner.readString());
	}

	@Override
	public void serialize(BSONWriter writer, Object value, ObjectSerializer[] subSerializer, int i) {
		writer.writeValue(((Enum)value).name());
		
	}

	public abstract Class<? extends Enum> getSerializedClass();

}
