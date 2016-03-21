package com.meidusa.fastbson.serializer;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;

import org.apache.commons.lang.ClassUtils;

import com.meidusa.fastbson.util.BSON;

public abstract class AbstractObjectSerializer implements ObjectSerializer {

	public byte getBsonSuffix() {
		return BSON.OBJECT;
	}
	
	public static byte getUnknownBsonSuffix(Object o) {
		if (o == null) {
			return BSON.NULL;
		}
		Class clazz = o.getClass();
		if (clazz == int.class || clazz == Integer.class) {
			return BSON.NUMBER_INT;
		} else if (clazz == long.class || clazz == Long.class) {
			return BSON.NUMBER_LONG;
		} else if (clazz == double.class || clazz == Double.class) {
			return BSON.NUMBER;
		} else if (clazz == float.class || clazz == Float.class) {
			return BSON.NUMBER;
		} else if (clazz == boolean.class || clazz == Boolean.class) {
			return BSON.BOOLEAN;
		} else if (clazz == byte.class || clazz == Byte.class) {
			return BSON.NUMBER_INT;
		} else if (clazz == Date.class) {
			return BSON.DATE;
		} else if (clazz == String.class || clazz.isEnum()) {
			return BSON.STRING;
		} else if (clazz == byte[].class) {
			return BSON.BINARY;
		} else if (clazz == BigDecimal.class) {
			return BSON.BIG_DECIMAL;
		} else if (clazz.isArray() || ClassUtils.isAssignable(clazz, Collection.class)) {
			return BSON.ARRAY;
		} else {
			return BSON.OBJECT;
		}
	}
	



}
