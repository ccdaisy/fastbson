package com.meidusa.fastbson.serializer;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import com.meidusa.fastbson.ASMSerializerFactory;
import com.meidusa.fastbson.exception.SerializeException;
import com.meidusa.fastbson.parse.BSONScanner;
import com.meidusa.fastbson.parse.ByteArrayBSONScanner;
import com.meidusa.fastbson.parse.BSONWriter;
import com.meidusa.fastbson.util.BSON;
import com.meidusa.fastbson.util.ObjectId;

public class UnknownTypeSerializer implements ObjectSerializer {

	private static ObjectSerializer OBJECT_SERIALIZER = BasicSerializerGroup.hashMapSerializer;
	private static ObjectSerializer ARRAY_SERIALIZER = BasicSerializerGroup.arrayListSerializer;
	private static ObjectSerializer UNKNOWN_SERIALIZER = new UnknownTypeSerializer();
	
	public static ObjectSerializer getInstance() {
		return UNKNOWN_SERIALIZER;
	}
	
	public Object deserialize(BSONScanner scanner, ObjectSerializer[] subSerializer, int i) {
		byte type = scanner.getCurrentType();

		switch (type) {
		case BSON.STRING:
			return scanner.readBSONString();
		case BSON.NUMBER:
			return scanner.readBSONDouble();
		case BSON.BINARY:
			return scanner.readBSONBinary();
		case BSON.NUMBER_INT:
			return scanner.readBSONInt();
		case BSON.NUMBER_LONG:
			return scanner.readBSONLong();
		case BSON.BIG_DECIMAL:
			return scanner.readBSONBigDecimal();
		case BSON.BOOLEAN:
			return scanner.readBSONBoolean();
		case BSON.OID:
			return scanner.readBSONOid();
		case BSON.DATE:
			return scanner.readDate();
		case BSON.OBJECT:
			return OBJECT_SERIALIZER.deserialize(scanner, new ObjectSerializer[] { UNKNOWN_SERIALIZER }, 0);
		case BSON.ARRAY:
			return ARRAY_SERIALIZER.deserialize(scanner, new ObjectSerializer[] { UNKNOWN_SERIALIZER }, 0);
		default:
			throw new SerializeException.UnsupportedTypeException("type not supported " + type);
		}
	}

	public void serialize(BSONWriter writer, Object val, ObjectSerializer[] subSerializer, int i) {
		if (val == null) {
		} else if (val instanceof String) {
			writer.writeValue((String) val);
		} else if (val instanceof Number) {
			if (val instanceof Integer || val instanceof Short || val instanceof Byte || val instanceof AtomicInteger) {
				writer.writeValue(((Number) val).intValue());
			} else if (val instanceof Long || val instanceof AtomicLong) {
				writer.writeValue(((Number) val).longValue());
			} else if (val instanceof BigDecimal) {
				writer.writeValue((BigDecimal) val);
			} else {
				writer.writeValue(((Number) val).doubleValue());
			}
		} else if (val instanceof Date) {
			writer.writeValue((Date) val);
		} else if (val instanceof Map) {
			ASMSerializerFactory.getSerializer(val.getClass()).serialize(writer, val,
					new ObjectSerializer[] { UNKNOWN_SERIALIZER }, 0);
		}else if (val instanceof ObjectId) {
			writer.writeBytes(((ObjectId) val).toByteArray());
		} else if (val instanceof Boolean) {
			writer.writeValue((Boolean) val);
		}  else if (val instanceof List) {
			ASMSerializerFactory.getSerializer(val.getClass()).serialize(writer, val,
					new ObjectSerializer[] { UNKNOWN_SERIALIZER }, 0);
		} else if (val instanceof byte[]) {
			writer.writeValue((byte[]) val);
		} else if (val.getClass().isArray()) {
			Class listClass = val.getClass();
			Class componentClass = listClass.getComponentType();
			ObjectSerializer serializer = ASMSerializerFactory.getSerializer(Object[].class);
			ObjectSerializer[] componentSerializer = new ObjectSerializer[] { ASMSerializerFactory
					.getSerializer(componentClass) };
			serializer.serialize(writer, val, componentSerializer, 0);
		} else if (val instanceof Date) {
			writer.writeValue((Date) val);
		} else if (val.getClass().isEnum()) {
			writer.writeValue(val.toString());
		} else {
			ObjectSerializer serializer = ASMSerializerFactory.getSerializer(val.getClass());
			serializer.serialize(writer, val, new ObjectSerializer[] {}, 0);

		}
	}

	public Class<?> getSerializedClass() {
		// unknowdtype serializer do not need to specified class;
		return null;
	}

	public byte getBsonSuffix() {
		// unknow type, not really know what itself is
		return 0;
	}

}
