package com.meidusa.fastbson.util;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.meidusa.fastbson.exception.SerializeException.GenericTypeNotDefinedException;
import com.meidusa.fastbson.exception.SerializeException.WrongGenericTypeException;

public class TypeHelper {

	private static Class array = Object[].class;

	public static List<Class> getGenericDeserializer(Type type) {
		List<Class> typeList = new ArrayList<Class>();
		while (true) {
			if (type instanceof Class) {
				if (((Class) type).isArray()) {
					if(type == byte[].class) {
						typeList.add(byte[].class);
						break;
					}
					typeList.add(array);
					typeList.add(((Class) type).getComponentType());
					break;
				} else {
					typeList.add((Class) type);
					if (Collection.class.isAssignableFrom((Class) type)) {
						typeList.add(Object.class);
					} else if (Map.class.isAssignableFrom((Class) type)) {
						typeList.add(Object.class);
					}
					break;
				}
			} else if (type instanceof ParameterizedType) {
				ParameterizedType parameterizedType = (ParameterizedType) type;
				Class<?> clazz = (Class<?>) parameterizedType.getRawType();
				if (Collection.class.isAssignableFrom(clazz)) {
					Type[] genericClass = ((ParameterizedType) type).getActualTypeArguments();
					if (genericClass == null || genericClass.length != 1) {
						throw new GenericTypeNotDefinedException(
								"Collection should be given a generic type , such as Collection<Account> or Collection<String>");
					}
					typeList.add((Class) ((ParameterizedType) type).getRawType());
					type = genericClass[0];
				} else if (Map.class.isAssignableFrom(clazz)) {
					Type[] genericClass = ((ParameterizedType) type).getActualTypeArguments();
					if (genericClass == null || genericClass.length != 2) {
						throw new GenericTypeNotDefinedException(
								"Map should be given a generic type, such as Map<String, Account>");
					}
					if (genericClass[0] != String.class) {
						throw new WrongGenericTypeException("Map's first generic type should be String");
					}
					typeList.add((Class) ((ParameterizedType) type).getRawType());
					type = genericClass[1];
				} else {
					typeList.add((Class) ((ParameterizedType) type).getRawType());
					break;
				}
			} else if (type instanceof GenericArrayType) {
				typeList.add(array);
				type = ((GenericArrayType) type).getGenericComponentType();
			} else if (type instanceof WildcardType) {
				typeList.add(Object.class);
			} else {
				throw new WrongGenericTypeException("wrong type" + type);
			}
		}
		return typeList;
	}
}
