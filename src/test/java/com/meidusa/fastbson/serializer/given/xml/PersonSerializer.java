package com.meidusa.fastbson.serializer.given.xml;


public class PersonSerializer extends XStreamSerializer{

	@Override
	public Class<?> getSerializedClass() {
		return Person.class;
	}
	
}