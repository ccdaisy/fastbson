package com.meidusa.fastbson;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;

import com.meidusa.fastbson.parse.BSONScanner;
import com.meidusa.fastbson.parse.BSONWriter;
import com.meidusa.fastbson.parse.ByteArrayBSONWriter;
import com.meidusa.fastbson.serializer.ObjectSerializer;
import com.meidusa.fastbson.serializer.UnknownTypeSerializer;

public class FastBsonSerializer {
	private static ThreadLocal<BSONWriter> bufferThreadLocal = new ThreadLocal<BSONWriter>();
	private static HashMap<TypeWrapper, SerializerGroup> serializerGroupMap = new HashMap<TypeWrapper, SerializerGroup>();

	public static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	
	
	public BSONWriter encode(Object obj) {
		BSONWriter writer = bufferThreadLocal.get();
		if (writer == null) {
			writer = new ByteArrayBSONWriter();
			bufferThreadLocal.set(writer);
		}
		writer.clear();
		ObjectSerializer serializer = UnknownTypeSerializer.getInstance();
		serializer.serialize(writer, obj, null, 0);
		return writer;
	}
	
	public void encode(Object obj, BSONWriter writer) {
		ObjectSerializer serializer = UnknownTypeSerializer.getInstance();
		serializer.serialize(writer, obj, null, 0);
	}
	
	public BSONWriter encode(Object obj, Type type) {
		BSONWriter writer = bufferThreadLocal.get();
		if (writer == null) {
			writer = new ByteArrayBSONWriter();
			bufferThreadLocal.set(writer);
		}
		writer.clear();
		TypeWrapper wrapper = new TypeWrapper(type);
		SerializerGroup group = serializerGroupMap.get(wrapper);
		if(group == null) {
			synchronized (serializerGroupMap) {
				group = serializerGroupMap.get(wrapper);
				if (group == null) {
					group = generateSerializerGroup(wrapper);
					serializerGroupMap.put(wrapper, group);
				}
			}
		}
		group.serialize(writer, obj);
		return writer;
	}

	public Object decode(BSONScanner scanner, Type type) {
		if (type != null) {
			TypeWrapper wrapper = new TypeWrapper(type);
			SerializerGroup group = serializerGroupMap.get(wrapper);
			if (group == null) {
				synchronized (serializerGroupMap) {
					group = serializerGroupMap.get(wrapper);
					if (group == null) {
						group = generateSerializerGroup(wrapper);
						serializerGroupMap.put(wrapper, group);
					}
				}
			}
			return group.deserialize(scanner);
		} else {
			return decode(scanner);
		}
	}
	
	public Object decode(BSONScanner scanner) {

		ObjectSerializer serializer = UnknownTypeSerializer.getInstance();
		return serializer.deserialize(scanner, null, 0);
	}
	
	public SerializerGroup generateSerializerGroup(TypeWrapper wrapper) {
		List<Class> classes = wrapper.classes;
		SerializerGroup group = new SerializerGroup();
		group.rawSerialzer = ASMSerializerFactory.getSerializer(classes.get(0));
		int subsize = 0;
		if ((subsize = classes.size() - 1) > 0) {
			ObjectSerializer[] subSerializers = new ObjectSerializer[subsize];
			for(int i = 0 ; i < subsize; i ++) {
				subSerializers[i] = ASMSerializerFactory.getSerializer(classes.get(i+1));
			}
			group.subSerializers = subSerializers;
		}
		return group;
	}
	
	public static void registerReplace(Class<?> replaced, Class<?> replace) {
		ASMSerializerFactory.createAndReplace(replaced, replace);
	}
	
	public static void registerSerializer(Class<?> replaced, ObjectSerializer serializer) {
		ASMSerializerFactory.replace(replaced, serializer);
	}
	
	
	public void registerDateFormat(DateFormat format) {
		dateFormat = format;
	}
}