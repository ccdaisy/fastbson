package com.meidusa.fastbson;

import java.lang.reflect.Type;
import java.util.List;

import com.meidusa.fastbson.serializer.ObjectSerializer;
import com.meidusa.fastbson.util.TypeHelper;

public class TypeWrapper {


	List<Class> classes;

	public TypeWrapper(Type type) {
		this.classes = TypeHelper.getGenericDeserializer(type);
	}

	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((classes == null) ? 0 : classes.hashCode());
		return result;
	}



	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TypeWrapper other = (TypeWrapper) obj;
		if (classes == null) {
			if (other.classes != null)
				return false;
		} else if (!classes.equals(other.classes))
			return false;
		return true;
	}

}
