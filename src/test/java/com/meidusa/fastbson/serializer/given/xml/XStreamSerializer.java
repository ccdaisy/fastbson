package com.meidusa.fastbson.serializer.given.xml;

import com.meidusa.fastbson.parse.BSONScanner;
import com.meidusa.fastbson.parse.BSONWriter;
import com.meidusa.fastbson.serializer.ObjectSerializer;
import com.meidusa.fastbson.util.BSON;
import com.thoughtworks.xstream.XStream;

public abstract class XStreamSerializer implements ObjectSerializer {

	private static XStream xstream = new XStream();
	static {
		xstream.alias("person", Person.class);
		xstream.alias("phonenumber", PhoneNumber.class);
	}
	
	@Override
	public Object deserialize(BSONScanner scanner,
			ObjectSerializer[] subSerializer, int i) {
		String xmlStr = scanner.readString();
		Object obj = xstream.fromXML(xmlStr);
		return obj;
	}

	@Override
	public void serialize(BSONWriter writer, Object value,
			ObjectSerializer[] subSerializer, int i) {
		String xmlStr = xstream.toXML(value);
		writer.writeValue(xmlStr);
		
	}

	@Override
	public byte getBsonSuffix() {
		return BSON.STRING;
	}

	
}
