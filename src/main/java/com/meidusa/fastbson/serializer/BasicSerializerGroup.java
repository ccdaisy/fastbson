package com.meidusa.fastbson.serializer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;

public class BasicSerializerGroup {
	public static ObjectSerializer hashMapSerializer = new MapSerializer() {
		@Override
		public Class<?> getSerializedClass() {
			return HashMap.class;
		}
	};
	public static ObjectSerializer linkedHashMapSerializer = new MapSerializer() {
		@Override
		public Class<?> getSerializedClass() {
			return LinkedHashMap.class;
		}
	};
	public static ObjectSerializer arrayListSerializer = new CollectionSerializer() {
		
		@Override
		public Class<?> getSerializedClass() {
			return ArrayList.class;
		}
	};
	
	public static ObjectSerializer linkedListSerializer = new CollectionSerializer() {
		
		@Override
		public Class<?> getSerializedClass() {
			return LinkedList.class;
		}
	};
	
	public static ObjectSerializer hashSetSerializer = new CollectionSerializer() {
		
		@Override
		public Class<?> getSerializedClass() {
			return HashSet.class;
		}
	};
	
	public static ObjectSerializer linkedHashSetSerializer = new CollectionSerializer() {
		
		@Override
		public Class<?> getSerializedClass() {
			return LinkedHashSet.class;
		}
	};
}
