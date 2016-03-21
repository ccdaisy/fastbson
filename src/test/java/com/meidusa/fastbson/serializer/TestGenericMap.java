package com.meidusa.fastbson.serializer;

import java.util.*;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.junit.Test;

public class TestGenericMap {

	@Test
	public void testGenericMap (){
		Type a = new ParameterizedType() {
			
			public Type getRawType() {
				return HashMap.class;
			}
			
			public Type getOwnerType() {
				return null;
			}
			
			public Type[] getActualTypeArguments() {
				return new Class[] {String.class, List.class};
			}
			
		};
		Type b = new ParameterizedType() {
			
			public Type getRawType() {
				return HashMap.class;
			}
			
			public Type getOwnerType() {
				return null;
			}
			
			public Type[] getActualTypeArguments() {
				return new Class[] {String.class, ArrayList.class};
			}
		};
		
		Type c = new ParameterizedType() {
			
			public Type getRawType() {
				return HashMap.class;
			}
			
			public Type getOwnerType() {
				return null;
			}
			
			public Type[] getActualTypeArguments() {
				return new Class[] {String.class, int.class};
			}
		};
		Type d = new ParameterizedType() {
			
			public Type getRawType() {
				return HashMap.class;
			}
			
			public Type getOwnerType() {
				return null;
			}
			
			public Type[] getActualTypeArguments() {
				return new Class[] {String.class, int.class};
			}
		};
		
		System.out.println(a.hashCode());
		System.out.println(b.hashCode());
		System.out.println(c.hashCode());
		System.out.println(d.hashCode());
		System.out.println(c.equals(d));
		

		System.out.println(Integer.class.hashCode());
		System.out.println(Integer.class.hashCode());
		System.out.println(int.class.hashCode());
		System.out.println(Long.class.hashCode());
	}
}
