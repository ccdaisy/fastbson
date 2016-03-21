package com.meidusa.fastbson;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.ClassUtils;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import com.meidusa.fastbson.annotation.Serialize;
import com.meidusa.fastbson.exception.SerializeException;
import com.meidusa.fastbson.parse.BSONScanner;
import com.meidusa.fastbson.parse.BSONWriter;
import com.meidusa.fastbson.serializer.AbstractObjectSerializer;
import com.meidusa.fastbson.serializer.ArraySerializer;
import com.meidusa.fastbson.serializer.BasicSerializerGroup;
import com.meidusa.fastbson.serializer.BigDecimalSerializer;
import com.meidusa.fastbson.serializer.BooleanSerializer;
import com.meidusa.fastbson.serializer.DoubleSerializer;
import com.meidusa.fastbson.serializer.EnumSerializer;
import com.meidusa.fastbson.serializer.FloatSerializer;
import com.meidusa.fastbson.serializer.IntegerSerializer;
import com.meidusa.fastbson.serializer.LongSerializer;
import com.meidusa.fastbson.serializer.ObjectSerializer;
import com.meidusa.fastbson.serializer.PrimitiveBooleanSerializer;
import com.meidusa.fastbson.serializer.PrimitiveDoubleSerializer;
import com.meidusa.fastbson.serializer.PrimitiveFloatSerializer;
import com.meidusa.fastbson.serializer.PrimitiveIntSerializer;
import com.meidusa.fastbson.serializer.PrimitiveLongSerializer;
import com.meidusa.fastbson.serializer.PrimitiveShortSerializer;
import com.meidusa.fastbson.serializer.StringSerializer;
import com.meidusa.fastbson.serializer.UnknownTypeSerializer;
import com.meidusa.fastbson.util.ASMClassLoader;
import com.meidusa.fastbson.util.BSON;
import com.meidusa.fastbson.util.TypeHelper;

/**
 * invoke getInstance to get the singleton
 * 
 * @author daisyli89
 * 
 */
public class ASMSerializerFactory {

	/**
	 * classloader to load generated class
	 */
	private ASMClassLoader classLoader = new ASMClassLoader();

	/**
	 * singleton instance
	 */
	private static ASMSerializerFactory serializerFactory = new ASMSerializerFactory();

	/**
	 * @return the singleton of this class
	 */
	private static ASMSerializerFactory getInstance() {
		return serializerFactory;
	}

	/**
	 * to make sure this class is singleton
	 */
	private ASMSerializerFactory() {
		initSerializers();
	}

	private HashMap<Class<?>, ObjectSerializer> serializerMap;

	// init the serializer map , list/map/array/... serializers
	private void initSerializers() {
		serializerMap = new HashMap<Class<?>, ObjectSerializer>();
		serializerMap.put(ArrayList.class, BasicSerializerGroup.arrayListSerializer);
		serializerMap.put(BigDecimal.class, new BigDecimalSerializer());
		serializerMap.put(Boolean.class, new BooleanSerializer());
		serializerMap.put(Double.class, new DoubleSerializer());
		serializerMap.put(Float.class, new FloatSerializer());
		serializerMap.put(LinkedHashMap.class, BasicSerializerGroup.linkedHashMapSerializer);
		serializerMap.put(HashMap.class, BasicSerializerGroup.hashMapSerializer);
		serializerMap.put(Map.class, BasicSerializerGroup.hashMapSerializer);
		serializerMap.put(Set.class, BasicSerializerGroup.hashSetSerializer);
		serializerMap.put(HashSet.class, BasicSerializerGroup.hashSetSerializer);
		serializerMap.put(LinkedHashSet.class, BasicSerializerGroup.linkedHashSetSerializer);
		serializerMap.put(Integer.class, new IntegerSerializer());
		serializerMap.put(LinkedList.class, BasicSerializerGroup.linkedListSerializer);
		serializerMap.put(List.class, BasicSerializerGroup.arrayListSerializer);
		serializerMap.put(Long.class, new LongSerializer());
		serializerMap.put(Object[].class, new ArraySerializer());
		serializerMap.put(boolean.class, new PrimitiveBooleanSerializer());
		serializerMap.put(double.class, new PrimitiveDoubleSerializer());
		serializerMap.put(float.class, new PrimitiveFloatSerializer());
		serializerMap.put(int.class, new PrimitiveIntSerializer());
		serializerMap.put(long.class, new PrimitiveLongSerializer());
		serializerMap.put(short.class, new PrimitiveShortSerializer());
		serializerMap.put(String.class, new StringSerializer());
		serializerMap.put(Object.class, new UnknownTypeSerializer());
	}


	/**
	 * @param clazz
	 * @return the serializer for specified class
	 */
	private ObjectSerializer innerGetSerializer(Class<?> clazz) {
		// return the serializer of the specified clazz
		ObjectSerializer serializer = serializerMap.get(clazz);
		if (serializer == null) {
			synchronized (serializerMap) {
				serializer = serializerMap.get(clazz);
				if (serializer == null) {
					serializer = createSerializer(clazz);
					serializerMap.put(clazz, serializer);
				}
			}
		}
		return serializer;
	}
	
	public static ObjectSerializer getSerializer(Class<?> clazz) {
		return getInstance().innerGetSerializer(clazz);
	}

	static void createAndReplace(Class<?> replaced, Class<?> replace) {
		ObjectSerializer serializerReplaced = getInstance().serializerMap.get(replaced);
		if(serializerReplaced != null) return;
		ObjectSerializer serializerReplace = getSerializer(replace);
		if(serializerReplace != null) {
			synchronized (getInstance().serializerMap) {
				if(serializerReplaced== null) {
					getInstance().serializerMap.put(replaced, serializerReplace);
					getInstance().serializerMap.put(replace, serializerReplace);
					return;
				}
			}
		}
	}
	
	static void replace(Class<?> replaced, ObjectSerializer serializerReplace) {
			synchronized (getInstance().serializerMap) {
				if(serializerReplace!= null) {
					getInstance().serializerMap.put(replaced, serializerReplace);
					return;
				}
			}
	}
	
	/**
	 * Using asm to construct a new serializer for specified class
	 * 
	 * @param clazz
	 * @return serializer class instance
	 */
	public ObjectSerializer createSerializer(final Class<?> clazz) {
		
		if(clazz.isEnum()) {
			return new EnumSerializer() {
				
				@Override
				public Class<? extends Enum> getSerializedClass() {
					return (Class<? extends Enum>) clazz;
				}
			};
		}

		ClassWriter clsWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		String className = this.getClassName(clazz);

		clsWriter.visit(Opcodes.V1_5, Opcodes.ACC_PUBLIC | Opcodes.ACC_SUPER, getClassName(clazz), "null", SUPER_CLASS,
				INTERFACE);

		List<FieldInfo> fieldInfoList = new ArrayList<FieldInfo>();

		analyzeFields(clazz, fieldInfoList);

		ASMContext ctx = new ASMContext(4);
		ctx.serializerClass = className;
		ctx.fieldInfoList = fieldInfoList;
		ctx.baseClass = clazz;
		this.writeField(clsWriter, ctx);
		this.writeInit(clsWriter, ctx);
		this.writeDeserialize(clsWriter, ctx);
		this.writeSerialize(clsWriter, ctx);
		this.writeGetSerializedClass(clsWriter, ctx.baseClass);

		byte[] code = clsWriter.toByteArray();

//		 just for write class to disk and use jad to analyze it
//		 try {
//		 org.apache.commons.io.IOUtils.write(code, new
//		 java.io.FileOutputStream("/Users/daisy/" + className
//		 + ".class"));
//		 } catch (Exception e) {
//		 e.printStackTrace();
//		 }

		// load class to jvm

		Class<?> exampleClass = classLoader.defineClassPublic(className, code, 0, code.length);
		Object instance = null;

		try {
			instance = exampleClass.newInstance();
			// it must be an ObjectSerializer
			return (ObjectSerializer) instance;
		} catch (Exception e) {
			e.printStackTrace();
			throw new SerializeException.ASMWrapperException("can't create class", e);
		}

	}
	
	private String getClassName(Class<?> clazz) {
		String className = clazz.getCanonicalName();
		className = className.replace('.', '_');
		className += "_asm_generated";
		return className;
	}

	public void analyzeFields(Class<?> clazz, List<FieldInfo> fieldInfoList) {
		do {
			// make sure field name is unique
			HashSet<String> fieldNames = new HashSet<String>();
			for (Field field : clazz.getDeclaredFields()) {
				int modifiers = field.getModifiers();
				// fastbson will not process static and transient field
				if (Modifier.isStatic(modifiers) || Modifier.isTransient(modifiers) )
					continue;
				if ( field.getName().equals("this$0")) 
					continue;
				// for public field, fastbson will get/set field directly
				FieldInfo info = new FieldInfo(field.getName(), Modifier.isPublic(modifiers),field.getGenericType());
				// for field with a given "As" annotation, the field name will
				// be replaced
				Serialize name = field.getAnnotation(Serialize.class);
				String fieldName;
				if (name != null) {
					fieldName = name.name();
				} else {
					fieldName = info.getName();
				}
				if (fieldNames.contains(fieldName)) {
					throw new SerializeException.DuplicateFieldNameException(clazz.toString()
							+ "has duplicate fieldName: " + fieldName);
				}
				info.setAsName(fieldName);
				info.setTypes(TypeHelper.getGenericDeserializer(info.getFieldType()));
				
				//Set the bson type for a specified field
				info.setBsonType(this.getBsonType(field.getType()));
				fieldInfoList.add(info);
			}
			// the while clause is to make sure to get the super class field as
			// well
		} while ((clazz = clazz.getSuperclass()) != Object.class);
	}

	/**
	 * for write asm description
	 * 
	 * @param type
	 * @return
	 */
	private String getPrimitiveLetter(Class<?> type) {
		if (Integer.TYPE.equals(type)) {
			return "I";
		} else if (Void.TYPE.equals(type)) {
			return "V";
		} else if (Boolean.TYPE.equals(type)) {
			return "Z";
		} else if (Character.TYPE.equals(type)) {
			return "C";
		} else if (Byte.TYPE.equals(type)) {
			return "B";
		} else if (Short.TYPE.equals(type)) {
			return "S";
		} else if (Float.TYPE.equals(type)) {
			return "F";
		} else if (Long.TYPE.equals(type)) {
			return "J";
		} else if (Double.TYPE.equals(type)) {
			return "D";
		}

		throw new IllegalStateException("Type: " + type.getCanonicalName() + " is not a primitive type");
	}

	/**
	 * for write asm description
	 * 
	 * @param type
	 * @return
	 */
	public String getType(Class<?> type) {
		if (type.isArray()) {
			return "[" + getDesc(type.getComponentType());
		} else {
			if (!type.isPrimitive()) {
				String clsName = type.getCanonicalName();

				if (type.isMemberClass()) {
					int lastDot = clsName.lastIndexOf(".");
					clsName = clsName.substring(0, lastDot) + "$" + clsName.substring(lastDot + 1);
				}
				return clsName.replaceAll("\\.", "/");
			} else {
				return getPrimitiveLetter(type);
			}
		}
	}

	/**
	 * for write asm description
	 * 
	 * @param type
	 * @return
	 */
	public String getDesc(Class<?> type) {
		if (type.isPrimitive()) {
			return getPrimitiveLetter(type);
		} else if (type.isArray()) {
			return "[" + getDesc(type.getComponentType());
		} else {
			return "L" + getType(type) + ";";
		}
	}
	

	/**
	 * construct description for specified method
	 * @param returnType
	 * @param paramType
	 * @return
	 */
	private String constructMethodDesc(Class<?> returnType, Class<?>... paramType) {
		StringBuilder methodDesc = new StringBuilder();
		methodDesc.append('(');
		for (int i = 0; i < paramType.length; i++) {
			methodDesc.append(getDesc(paramType[i]));
		}
		methodDesc.append(')');
		if (returnType == Void.class) {
			methodDesc.append("V");
		} else {
			methodDesc.append(getDesc(returnType));
		}
		return methodDesc.toString();
	}

	
	/**
	 * get the first byte for prefix
	 * @param clazz
	 * @return
	 */
	private Byte getBsonType(Class<?> clazz) {

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
		} else if (clazz == Object.class) {
			return null;
		} else {
			return BSON.OBJECT;
		}
	}
	
	private void writeField(ClassWriter clsWriter, ASMContext ctx) {

		// byte array bson prefix
		// public byte authenticate_bson;
		
		for (int i = 0; i < ctx.fieldInfoList.size(); i++) {
			FieldInfo fieldInfo = ctx.fieldInfoList.get(i);
			FieldVisitor fdVisitor = clsWriter.visitField(Opcodes.ACC_PUBLIC, fieldInfo.getName() + BYTE_FIELD_SUFFIX,
					getType(byte[].class), "", null);
			fdVisitor.visitEnd();
		}
		
		// public byte authenticate_prefix[];

		for (int i = 0; i < ctx.fieldInfoList.size(); i++) {
			FieldInfo fieldInfo = ctx.fieldInfoList.get(i);
			if (fieldInfo.getBsonType() != null) {
				FieldVisitor fdVisitor = clsWriter.visitField(Opcodes.ACC_PUBLIC, fieldInfo.getName()
						+ BSON_FIELD_SUFFIX, getType(byte.class), "", null);
				fdVisitor.visitEnd();
			}
		}

		// subserializers
		//
		// public ObjectSerializer map_serializer;
		// public ObjectSerializer map_sub_serializer[];

		
		for (int i = 0; i < ctx.fieldInfoList.size(); i++) {
			FieldInfo fieldInfo = ctx.fieldInfoList.get(i);
			if (shouldHaveSerializer(fieldInfo)) {
				fieldInfo.setNeedSerializer(true);
				FieldVisitor fw = clsWriter.visitField(Opcodes.ACC_PUBLIC, fieldInfo.getName()
						+ SERIALIZER_FIELD_SUFFIX, getDesc(ObjectSerializer.class), "", null);
				fw.visitEnd();
				List<Class> types = fieldInfo.getTypes();
				if (types.size() > 1) {
					FieldVisitor fwSub = clsWriter.visitField(Opcodes.ACC_PUBLIC, fieldInfo.getName()
							+ SUB_SERIALIZER_FIELD_SUFFIX, getDesc(ObjectSerializer[].class), "", null);
					fwSub.visitEnd();
				}

			}
		}
	}
	
	private boolean shouldHaveSerializer(FieldInfo info) {
		if(info.getFieldType() instanceof Class){

			return this.isSimpleClass((Class<?>) info.getFieldType());
		} else {
			return this.isSimpleClass(info.getClass());
		}
	}

	private boolean isSimpleClass(Class<?> clazz) {

		if (clazz.isPrimitive() || ClassUtils.isAssignable(clazz, Number.class) || clazz == String.class
				|| clazz == Date.class || clazz == byte[].class || clazz.isEnum()) {
			return false;
		}
		return true;
	}

	private void writeInit(ClassWriter clsWriter, ASMContext ctx) {
		// super init
		MethodVisitor mw = clsWriter.visitMethod(Opcodes.ACC_PUBLIC, "<init>",
				constructMethodDesc(Void.class, new Class<?>[0]), null, null);
		mw.visitVarInsn(Opcodes.ALOAD, 0);
		mw.visitMethodInsn(Opcodes.INVOKESPECIAL, getType(AbstractObjectSerializer.class), "<init>",
				constructMethodDesc(Void.class, new Class<?>[0]));
		for (int i = 0; i < ctx.fieldInfoList.size(); i++) {
			// init byte array : name_prefix = new byte["name".length() + 1];
			FieldInfo info = ctx.fieldInfoList.get(i);
			String fieldName = info.getName();
			Class<?> clazz = info.getFieldClass();
			mw.visitVarInsn(Opcodes.ALOAD, 0);
			mw.visitLdcInsn(info.getAsName());
			mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, getType(String.class), "length",
					constructMethodDesc(int.class, new Class<?>[0]));
			mw.visitInsn(Opcodes.ICONST_1);
			mw.visitInsn(Opcodes.IADD);
			mw.visitIntInsn(Opcodes.NEWARRAY, Opcodes.T_BYTE);
			mw.visitFieldInsn(Opcodes.PUTFIELD, ctx.serializerClass, fieldName + BYTE_FIELD_SUFFIX,
					getDesc(byte[].class));
//			// write first byte : name_prefix[0] = BSON.STRING;
//			mw.visitVarInsn(Opcodes.ALOAD, 0);
//			mw.visitFieldInsn(Opcodes.GETFIELD, ctx.serializerClass, fieldName + BYTE_FIELD_SUFFIX,
//					getDesc(byte[].class));
//			mw.visitInsn(Opcodes.ICONST_0);
//			if (info.getFieldType() instanceof Class) {
//				mw.visitIntInsn(Opcodes.BIPUSH, this.getBsonType((Class<?>) info.getFieldType()));
//
//			} else {
//				mw.visitIntInsn(Opcodes.BIPUSH, this.getBsonType(clazz));
//			}
//			mw.visitInsn(Opcodes.BASTORE);
			// copy other bytes : System.arraycopy("name".getBytes(), 0,
			// name_prefix, 1, "name".length());
			mw.visitLdcInsn(info.getAsName());
			mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, getType(String.class), "getBytes",
					constructMethodDesc(byte[].class, new Class<?>[0]));
			mw.visitInsn(Opcodes.ICONST_0);
			mw.visitVarInsn(Opcodes.ALOAD, 0);
			mw.visitFieldInsn(Opcodes.GETFIELD, ctx.serializerClass, fieldName + BYTE_FIELD_SUFFIX,
					getType(byte[].class));
			mw.visitInsn(Opcodes.ICONST_0);
			mw.visitLdcInsn(info.getAsName());
			mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, getType(String.class), "length",
					constructMethodDesc(int.class, new Class<?>[0]));
			mw.visitMethodInsn(Opcodes.INVOKESTATIC, getType(System.class), "arraycopy",
					constructMethodDesc(Void.class, Object.class, int.class, Object.class, int.class, int.class));
			// init bson suffix
			if(info.getBsonType() != null) {
				mw.visitVarInsn(Opcodes.ALOAD, 0);
				mw.visitLdcInsn(info.getBsonType());
				mw.visitFieldInsn(Opcodes.PUTFIELD, ctx.serializerClass, fieldName + BSON_FIELD_SUFFIX,
						getDesc(byte.class));
			}
			// init serializer and sub serializers
			if (this.shouldHaveSerializer(info)) {
				mw.visitVarInsn(Opcodes.ALOAD, 0);
				mw.visitLdcInsn(org.objectweb.asm.Type.getType(info.getFieldClass()));
				mw.visitMethodInsn(Opcodes.INVOKESTATIC, getType(ASMSerializerFactory.class), "getSerializer",
						constructMethodDesc(ObjectSerializer.class, Class.class));
				mw.visitFieldInsn(Opcodes.PUTFIELD, ctx.serializerClass, fieldName + SERIALIZER_FIELD_SUFFIX,
						getDesc(ObjectSerializer.class));
				List<Class> types;
				if ((types = info.getTypes()).size() > 1) {
					mw.visitVarInsn(Opcodes.ALOAD, 0);
					mw.visitIntInsn(Opcodes.BIPUSH, types.size() - 1);
					mw.visitTypeInsn(Opcodes.ANEWARRAY, getType(ObjectSerializer.class));
					mw.visitFieldInsn(Opcodes.PUTFIELD, ctx.serializerClass, fieldName + SUB_SERIALIZER_FIELD_SUFFIX,
							getDesc(ObjectSerializer[].class));
					for (int j = 1; j < types.size(); j++) {
						Class<?> subClass;
						Class subType = types.get(j);
						subClass = (Class<?>) subType;
						mw.visitVarInsn(Opcodes.ALOAD, 0);
						mw.visitFieldInsn(Opcodes.GETFIELD, ctx.serializerClass, fieldName
								+ SUB_SERIALIZER_FIELD_SUFFIX, getDesc(ObjectSerializer[].class));
						mw.visitIntInsn(Opcodes.BIPUSH, j-1);
						// primivitive type should have there primitive
						// serializer
						if (subClass.isPrimitive()) {
							if (subClass == boolean.class) {
								mw.visitFieldInsn(Opcodes.GETSTATIC, getType(Boolean.class), "TYPE",
										getDesc(Class.class));
							} else if (subClass == int.class) {
								mw.visitFieldInsn(Opcodes.GETSTATIC, getType(Integer.class), "TYPE",
										getDesc(Class.class));

							} else if (subClass == long.class) {
								mw.visitFieldInsn(Opcodes.GETSTATIC, getType(Long.class), "TYPE", getDesc(Class.class));

							} else if (subClass == float.class) {
								mw.visitFieldInsn(Opcodes.GETSTATIC, getType(Float.class), "TYPE", getDesc(Class.class));

							} else if (subClass == double.class) {
								mw.visitFieldInsn(Opcodes.GETSTATIC, getType(Double.class), "TYPE",
										getDesc(Class.class));

							}
						} else {
							mw.visitLdcInsn(org.objectweb.asm.Type.getType(subClass));

						}
						mw.visitMethodInsn(Opcodes.INVOKESTATIC, getType(ASMSerializerFactory.class), "getSerializer",
								constructMethodDesc(ObjectSerializer.class, Class.class));
						mw.visitInsn(Opcodes.AASTORE);
					}
				}
			}
		}
		mw.visitInsn(Opcodes.RETURN);
		mw.visitMaxs(0, 0);
		mw.visitEnd();

	}

	private void writeSerialize(ClassWriter clsWriter, ASMContext ctx) {
		MethodVisitor mw = clsWriter.visitMethod(Opcodes.ACC_PUBLIC, "serialize",
				constructMethodDesc(Void.class, BSONWriter.class, Object.class, ObjectSerializer[].class, int.class),
				null, null);
		mw.visitVarInsn(Opcodes.ALOAD, 2);
		mw.visitTypeInsn(Opcodes.CHECKCAST, getType(ctx.baseClass));
		mw.visitVarInsn(Opcodes.ASTORE, 5);
		List<FieldInfo> fieldInfoList = ctx.fieldInfoList;
		mw.visitVarInsn(Opcodes.ALOAD, 1);
		mw.visitMethodInsn(Opcodes.INVOKEINTERFACE, getType(BSONWriter.class), "beginObject",
				constructMethodDesc(Void.class, new Class[] {}));
		Label[] labels = new Label[fieldInfoList.size()];
		for (int i = 0; i < labels.length; i++) {
			labels[i] = new Label();
		}
		int i = 0;
		for (Iterator<FieldInfo> iterator = fieldInfoList.iterator(); iterator.hasNext();) {
			FieldInfo fieldInfo = (FieldInfo) iterator.next();

			PropertyDescriptor descripter;
			String getter = null;

			Class fieldClass = fieldInfo.getFieldClass();
			if(fieldClass.isArray() && fieldClass.getComponentType()!=byte.class) {
				fieldClass = Array.newInstance(fieldInfo.getTypes().get(1), 0).getClass();
			}
			if (!fieldInfo.isPublic()) {
				try {
					descripter = new PropertyDescriptor(fieldInfo.getName(), ctx.baseClass);
					getter = descripter.getReadMethod().getName();
				} catch (IntrospectionException e) {
					throw new SerializeException.ASMWrapperException("can't find getter method for field"
							+ fieldInfo.getName(), e);
				}
			}
			if (!fieldInfo.getFieldClass().isPrimitive()) {
				mw.visitVarInsn(Opcodes.ALOAD, 5);
				if (!fieldInfo.isPublic()) {
					mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, getType(ctx.baseClass), getter,
							constructMethodDesc(fieldClass, new Class[] {}));

				} else {
					mw.visitFieldInsn(Opcodes.GETFIELD, getType(ctx.baseClass), fieldInfo.getName(),
							getDesc(fieldClass));

				}
				mw.visitJumpInsn(Opcodes.IFNULL, labels[i]);
			}
			//write bson suffix, if it's a unknowd type ,for example , an Object, then analyze it
			mw.visitVarInsn(Opcodes.ALOAD, 1);
			if(fieldInfo.getBsonType()!=null) {
				mw.visitVarInsn(Opcodes.ALOAD, 0);
				mw.visitFieldInsn(Opcodes.GETFIELD, ctx.serializerClass, fieldInfo.getName() + BSON_FIELD_SUFFIX,
						getDesc(byte.class));
			} else {
				//mw.visitVarInsn(Opcodes.ALOAD, 5);

				mw.visitVarInsn(Opcodes.ALOAD, 5);
				if (!fieldInfo.isPublic()) {
					mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, getType(ctx.baseClass), getter,
							constructMethodDesc(fieldClass, new Class[] {}));
				} else {
					mw.visitFieldInsn(Opcodes.GETFIELD, getType(ctx.baseClass), fieldInfo.getName(),
							getDesc(fieldInfo.getFieldClass()));
				}
				mw.visitMethodInsn(Opcodes.INVOKESTATIC, getType(AbstractObjectSerializer.class), 
						"getUnknownBsonSuffix", constructMethodDesc(byte.class, Object.class));
			}
			mw.visitMethodInsn(Opcodes.INVOKEINTERFACE, getType(BSONWriter.class), "write",
					constructMethodDesc(Void.class, byte.class));

			//write field name
			mw.visitVarInsn(Opcodes.ALOAD, 1);
			mw.visitVarInsn(Opcodes.ALOAD, 0);
			mw.visitFieldInsn(Opcodes.GETFIELD, ctx.serializerClass, fieldInfo.getName() + BYTE_FIELD_SUFFIX,
					getDesc(byte[].class));
			mw.visitMethodInsn(Opcodes.INVOKEINTERFACE, getType(BSONWriter.class), "writeBytes",
					constructMethodDesc(Void.class, byte[].class));
			

			if (fieldInfo.isNeedSerializer()) {
				// using serializer to write byte array
				mw.visitVarInsn(Opcodes.ALOAD, 0);
				mw.visitFieldInsn(Opcodes.GETFIELD, ctx.serializerClass, fieldInfo.getName() + SERIALIZER_FIELD_SUFFIX,
						getDesc(ObjectSerializer.class));
				mw.visitVarInsn(Opcodes.ALOAD, 1);
				mw.visitVarInsn(Opcodes.ALOAD, 5);
				if (!fieldInfo.isPublic()) {
					mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, getType(ctx.baseClass), getter,
							constructMethodDesc(fieldClass, new Class[] {}));
				} else {
					mw.visitFieldInsn(Opcodes.GETFIELD, getType(ctx.baseClass), fieldInfo.getName(),
							getDesc(fieldInfo.getFieldClass()));

				}
				if (fieldInfo.getTypes().size() <= 1 ) {
					mw.visitInsn(Opcodes.ACONST_NULL);
				} else {
					mw.visitVarInsn(Opcodes.ALOAD, 0);
					mw.visitFieldInsn(Opcodes.GETFIELD, ctx.serializerClass, fieldInfo.getName()
							+ SUB_SERIALIZER_FIELD_SUFFIX, getDesc(ObjectSerializer[].class));
				}
				mw.visitInsn(Opcodes.ICONST_0);
				mw.visitMethodInsn(
						Opcodes.INVOKEINTERFACE,
						getType(ObjectSerializer.class),
						"serialize",
						constructMethodDesc(Void.class, BSONWriter.class, Object.class, ObjectSerializer[].class,
								int.class));
			} else {
				// just write, do not use serializer
				mw.visitVarInsn(Opcodes.ALOAD, 1);
				mw.visitVarInsn(Opcodes.ALOAD, 5);
				if (!fieldInfo.isPublic()) {
					mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, getType(ctx.baseClass), getter,
							constructMethodDesc(fieldClass, new Class[] {}));
				} else {
					mw.visitFieldInsn(Opcodes.GETFIELD, getType(ctx.baseClass), fieldInfo.getName(),
							getDesc(fieldInfo.getFieldClass()));
				}
				if (fieldClass.isEnum()) {
					mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, getType(fieldClass), "toString",
							constructMethodDesc(String.class, new Class[] {}));
					mw.visitMethodInsn(Opcodes.INVOKEINTERFACE, getType(BSONWriter.class), "writeValue",
							constructMethodDesc(Void.class, String.class));
				} else {
					mw.visitMethodInsn(Opcodes.INVOKEINTERFACE, getType(BSONWriter.class), "writeValue",
							constructMethodDesc(Void.class, fieldClass));

				}
			}
			if (!fieldInfo.getFieldClass().isPrimitive()) {
				mw.visitLabel(labels[i]);
			}
			i++;
		}
		mw.visitVarInsn(Opcodes.ALOAD, 1);
		mw.visitMethodInsn(Opcodes.INVOKEINTERFACE, getType(BSONWriter.class), "endObject",
				constructMethodDesc(Void.class, new Class[] {}));
		mw.visitInsn(Opcodes.RETURN);
		mw.visitMaxs(1, 0);
		mw.visitEnd();
	}

	private void writeGetSerializedClass(ClassWriter clsWriter, Class<?> clazz) {
		MethodVisitor mw = clsWriter.visitMethod(Opcodes.ACC_PUBLIC, "getSerializedClass",
				constructMethodDesc(Class.class, new Class[] {}), null, null);
		mw.visitLdcInsn(org.objectweb.asm.Type.getType(clazz));
		mw.visitInsn(Opcodes.ARETURN);
		mw.visitMaxs(0, 0);
		mw.visitEnd();
	}

	private void writeDeserialize(ClassWriter clsWriter, ASMContext ctx) {
		MethodVisitor mw = clsWriter.visitMethod(
				Opcodes.ACC_PUBLIC,
				"deserialize",
				constructMethodDesc(Object.class, new Class<?>[] { BSONScanner.class, ObjectSerializer[].class,
						int.class }), null, null);
		// scanner.skip(4);
		mw.visitVarInsn(Opcodes.ALOAD, 1);
		mw.visitInsn(Opcodes.ICONST_4);
		mw.visitMethodInsn(Opcodes.INVOKEINTERFACE, getType(BSONScanner.class), "skip",
				constructMethodDesc(Void.class, int.class));
		mw.visitTypeInsn(Opcodes.NEW, getType(ctx.baseClass));
		mw.visitInsn(Opcodes.DUP);
		mw.visitMethodInsn(Opcodes.INVOKESPECIAL, getType(ctx.baseClass), "<init>",
				constructMethodDesc(Void.class, new Class<?>[0]));
		mw.visitVarInsn(Opcodes.ASTORE, ctx.var("temp"));
		for (int i = 0; i < ctx.fieldInfoList.size(); i++) {
			mw.visitInsn(Opcodes.ICONST_0);
			mw.visitVarInsn(Opcodes.ISTORE, ctx.var(ctx.fieldInfoList.get(i).getName() + FIELD_SET));
		}

		Label _while_clause = new Label();
		Label _loop = new Label();
		Label[] fieldLabel = new Label[ctx.fieldInfoList.size()];
		for (int i = 0; i < fieldLabel.length; i++) {
			fieldLabel[i] = new Label();
		}
		// fieldLabel[fieldLabel.length - 1] = _while_clause;
		mw.visitJumpInsn(Opcodes.GOTO, _while_clause);
		mw.visitLabel(_loop);
		for (int i = 0; i < ctx.fieldInfoList.size(); i++) {
			FieldInfo info = ctx.fieldInfoList.get(i);
			mw.visitVarInsn(Opcodes.ILOAD, ctx.var(info.getName() + FIELD_SET));
			mw.visitJumpInsn(Opcodes.IFNE, fieldLabel[i]);
			mw.visitVarInsn(Opcodes.ALOAD, 1);
			mw.visitVarInsn(Opcodes.ALOAD, 0);
			mw.visitFieldInsn(Opcodes.GETFIELD, ctx.serializerClass, info.getName() + BYTE_FIELD_SUFFIX,
					getDesc(byte[].class));
			mw.visitMethodInsn(Opcodes.INVOKEINTERFACE, getType(BSONScanner.class), "match",
					constructMethodDesc(boolean.class, byte[].class));
			mw.visitJumpInsn(Opcodes.IFEQ, fieldLabel[i]);
			// jump to next if clause
			writePutMethod(mw, info, ctx);
			mw.visitInsn(Opcodes.ICONST_1);
			mw.visitVarInsn(Opcodes.ISTORE, ctx.var(info.getName() + FIELD_SET));
			mw.visitJumpInsn(Opcodes.GOTO, _while_clause);
			mw.visitLabel(fieldLabel[i]);
		}

		// if not match , skip this field
		mw.visitVarInsn(Opcodes.ALOAD, 1);
		mw.visitMethodInsn(Opcodes.INVOKEINTERFACE, getType(BSONScanner.class), "skipField",
				constructMethodDesc(Void.class, new Class<?>[0]));

		// while not EOF
		mw.visitLabel(_while_clause);
		mw.visitVarInsn(Opcodes.ALOAD, 1);
		mw.visitMethodInsn(Opcodes.INVOKEINTERFACE, getType(BSONScanner.class), "readType",
				constructMethodDesc(byte.class, new Class<?>[0]));
		mw.visitJumpInsn(Opcodes.IFNE, _loop);

//		// skip last EOF
//		mw.visitVarInsn(Opcodes.ALOAD, 1);
//		mw.visitInsn(Opcodes.ICONST_1);
//		mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, getType(BSONScanner.class), "skip",
//				constructMethodDesc(Void.class, int.class));

		// return instance of object to deserialize
		mw.visitVarInsn(Opcodes.ALOAD, ctx.var("temp"));
		mw.visitMaxs(1, ctx.fieldInfoList.size());
		mw.visitEnd();
		mw.visitInsn(Opcodes.ARETURN);

	}

	private void writePutMethod(MethodVisitor mw, FieldInfo info, ASMContext ctx) {
		mw.visitVarInsn(Opcodes.ALOAD, ctx.var("temp"));
		Class<?> fieldClass;
		if(info.getFieldType() instanceof Class) {
			fieldClass = (Class<?>) info.getFieldType();
		} else {
			fieldClass = info.getFieldClass();
		}
		String fieldName = info.getName();

		// different type should use different method to get its value

		if (fieldClass == int.class || fieldClass == Integer.class) {
			mw.visitVarInsn(Opcodes.ALOAD, 1);
			mw.visitMethodInsn(Opcodes.INVOKEINTERFACE, getType(BSONScanner.class), "readBSONInt",
					constructMethodDesc(int.class, new Class<?>[0]));

			if (!fieldClass.isPrimitive()) {
				mw.visitMethodInsn(Opcodes.INVOKESTATIC, getType(Integer.class), "valueOf",
						constructMethodDesc(Integer.class, int.class));
			}
		} else if (fieldClass == long.class || fieldClass == Long.class) {
			mw.visitVarInsn(Opcodes.ALOAD, 1);
			mw.visitMethodInsn(Opcodes.INVOKEINTERFACE, getType(BSONScanner.class), "readBSONLong",
					constructMethodDesc(long.class, new Class<?>[0]));

			if (!fieldClass.isPrimitive()) {
				mw.visitMethodInsn(Opcodes.INVOKESTATIC, getType(Long.class), "valueOf",
						constructMethodDesc(Long.class, long.class));
			}
		} else if (fieldClass == double.class || fieldClass == Double.class) {
			mw.visitVarInsn(Opcodes.ALOAD, 1);
			mw.visitMethodInsn(Opcodes.INVOKEINTERFACE, getType(BSONScanner.class), "readBSONDouble",
					constructMethodDesc(double.class, new Class<?>[0]));
			if (!fieldClass.isPrimitive()) {
				mw.visitMethodInsn(Opcodes.INVOKESTATIC, getType(Double.class), "valueOf",
						constructMethodDesc(Double.class, double.class));
			}

		} else if (fieldClass == float.class || fieldClass == Float.class) {
			mw.visitVarInsn(Opcodes.ALOAD, 1);
			mw.visitMethodInsn(Opcodes.INVOKEINTERFACE, getType(BSONScanner.class), "readBSONDouble",
					constructMethodDesc(double.class, new Class<?>[0]));

			mw.visitInsn(Opcodes.D2F);
			if (!fieldClass.isPrimitive()) {
				mw.visitMethodInsn(Opcodes.INVOKESTATIC, getType(Float.class), "valueOf",
						constructMethodDesc(Float.class, float.class));
			}
		} else if (fieldClass == String.class) {
			mw.visitVarInsn(Opcodes.ALOAD, 1);
			mw.visitMethodInsn(Opcodes.INVOKEINTERFACE, getType(BSONScanner.class), "readBSONString",
					constructMethodDesc(String.class, new Class<?>[0]));

		} else if (fieldClass.isEnum()) {
			mw.visitVarInsn(Opcodes.ALOAD, 1);
			mw.visitMethodInsn(Opcodes.INVOKEINTERFACE, getType(BSONScanner.class), "readString",
					constructMethodDesc(String.class, new Class<?>[0]));
			mw.visitMethodInsn(Opcodes.INVOKESTATIC, getType(fieldClass), "valueOf",
					constructMethodDesc(fieldClass, new Class[]{String.class}));
			
		} else if (fieldClass == Date.class) {
			mw.visitVarInsn(Opcodes.ALOAD, 1);
			mw.visitMethodInsn(Opcodes.INVOKEINTERFACE, getType(BSONScanner.class), "readBSONDate",
					constructMethodDesc(Date.class, new Class<?>[0]));
		} else if (fieldClass == boolean.class || fieldClass == Boolean.class) {
			mw.visitVarInsn(Opcodes.ALOAD, 1);
			mw.visitMethodInsn(Opcodes.INVOKEINTERFACE, getType(BSONScanner.class), "readBSONBoolean",
					constructMethodDesc(boolean.class, new Class<?>[0]));
			if (!fieldClass.isPrimitive()) {
				mw.visitMethodInsn(Opcodes.INVOKESTATIC, getType(Boolean.class), "valueOf",
						constructMethodDesc(Boolean.class, boolean.class));
			}

		} else if (fieldClass == BigDecimal.class) {
			mw.visitVarInsn(Opcodes.ALOAD, 1);
			mw.visitMethodInsn(Opcodes.INVOKEINTERFACE, getType(BSONScanner.class), "readBSONBigDecimal",
					constructMethodDesc(BigDecimal.class, new Class<?>[0]));

		}else if (fieldClass==byte.class || fieldClass== Byte.class) {
			mw.visitVarInsn(Opcodes.ALOAD, 1);
			mw.visitMethodInsn(Opcodes.INVOKEINTERFACE, getType(BSONScanner.class), "readBSONByte",
					constructMethodDesc(byte.class, new Class<?>[0]));
			if (!fieldClass.isPrimitive()) {
				mw.visitMethodInsn(Opcodes.INVOKESTATIC, getType(Byte.class), "valueOf",
						constructMethodDesc(Byte.class, byte.class));
			}
			
		} else if (ClassUtils.isAssignable(fieldClass, Map.class)) {
			mw.visitVarInsn(Opcodes.ALOAD, 0);
			mw.visitFieldInsn(Opcodes.GETFIELD, ctx.serializerClass, fieldName + SERIALIZER_FIELD_SUFFIX,
					getDesc(ObjectSerializer.class));
			mw.visitVarInsn(Opcodes.ALOAD, 1);
			mw.visitVarInsn(Opcodes.ALOAD, 0);
			mw.visitFieldInsn(Opcodes.GETFIELD, ctx.serializerClass, fieldName + SUB_SERIALIZER_FIELD_SUFFIX,
					getDesc(ObjectSerializer[].class));
			mw.visitInsn(Opcodes.ICONST_0);
			mw.visitMethodInsn(Opcodes.INVOKEINTERFACE, getType(ObjectSerializer.class), "deserialize",
					constructMethodDesc(Object.class, BSONScanner.class, ObjectSerializer[].class, int.class));
			mw.visitTypeInsn(Opcodes.CHECKCAST, getType(fieldClass));
		} else if (ClassUtils.isAssignable(fieldClass, List.class)) {
			mw.visitVarInsn(Opcodes.ALOAD, 0);
			mw.visitFieldInsn(Opcodes.GETFIELD, ctx.serializerClass, fieldName + SERIALIZER_FIELD_SUFFIX,
					getDesc(ObjectSerializer.class));
			mw.visitVarInsn(Opcodes.ALOAD, 1);
			mw.visitVarInsn(Opcodes.ALOAD, 0);
			mw.visitFieldInsn(Opcodes.GETFIELD, ctx.serializerClass, fieldName + SUB_SERIALIZER_FIELD_SUFFIX,
					getDesc(ObjectSerializer[].class));
			mw.visitInsn(Opcodes.ICONST_0);
			mw.visitMethodInsn(Opcodes.INVOKEINTERFACE, getType(ObjectSerializer.class), "deserialize",
					constructMethodDesc(Object.class, BSONScanner.class, ObjectSerializer[].class, int.class));
			mw.visitTypeInsn(Opcodes.CHECKCAST, getType(fieldClass));
		} else if (fieldClass.isArray()) {
			if(fieldClass.getComponentType()==byte.class) {
				mw.visitVarInsn(Opcodes.ALOAD, 1);
				mw.visitMethodInsn(Opcodes.INVOKEINTERFACE, getType(BSONScanner.class), "readBSONBinary",
						constructMethodDesc(byte[].class, new Class<?>[0]));
			} else {
				mw.visitVarInsn(Opcodes.ALOAD, 0);
				mw.visitFieldInsn(Opcodes.GETFIELD, ctx.serializerClass, fieldName + SERIALIZER_FIELD_SUFFIX,
						getDesc(ObjectSerializer.class));
				mw.visitVarInsn(Opcodes.ALOAD, 1);
				mw.visitVarInsn(Opcodes.ALOAD, 0);
				mw.visitFieldInsn(Opcodes.GETFIELD, ctx.serializerClass, fieldName + SUB_SERIALIZER_FIELD_SUFFIX,
						getDesc(ObjectSerializer[].class));
				mw.visitInsn(Opcodes.ICONST_0);
				mw.visitMethodInsn(Opcodes.INVOKEINTERFACE, getType(ObjectSerializer.class), "deserialize",
						constructMethodDesc(Object.class, BSONScanner.class, ObjectSerializer[].class, int.class));
				mw.visitTypeInsn(Opcodes.CHECKCAST, getType(Array.newInstance(info.getTypes().get(1), 0).getClass()));

			}
		}else {
			mw.visitVarInsn(Opcodes.ALOAD, 0);
			mw.visitFieldInsn(Opcodes.GETFIELD, ctx.serializerClass, fieldName + SERIALIZER_FIELD_SUFFIX,
					getDesc(ObjectSerializer.class));
			mw.visitVarInsn(Opcodes.ALOAD, 1);
			mw.visitInsn(Opcodes.ACONST_NULL);
			mw.visitInsn(Opcodes.ICONST_0);
			mw.visitMethodInsn(Opcodes.INVOKEINTERFACE, getType(ObjectSerializer.class), "deserialize",
					constructMethodDesc(Object.class, BSONScanner.class, ObjectSerializer[].class, int.class));
			mw.visitTypeInsn(Opcodes.CHECKCAST, getType(fieldClass));

		}
		if (!info.isPublic()) {
			try {
				String setterName = null;
				String setterDesc = null;
				PropertyDescriptor desc = new PropertyDescriptor(info.getName(), ctx.baseClass);
				Method setter = desc.getWriteMethod();
				setterName = setter.getName();
				setterDesc = constructMethodDesc(setter.getReturnType(), setter.getParameterTypes());
				mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, getType(ctx.baseClass), setterName, setterDesc);
			} catch (IntrospectionException e) {
				throw new SerializeException.ASMWrapperException("can't find setter method for field "
						+ info.getAsName(), e);

			}
		} else {
			//public field put field directly
			Class fieldRealClass;
			if(info.getFieldType() instanceof ParameterizedType) {
				fieldRealClass = (Class) ((ParameterizedType)info.getFieldType()).getRawType();
			} else {
				fieldRealClass = (Class) info.getFieldType();
			}
			
			mw.visitFieldInsn(Opcodes.PUTFIELD, getType(ctx.baseClass), info.getName(), getDesc(fieldRealClass));

		}

	}




	// some definition for generate class

	private final String BSON_FIELD_SUFFIX = "_bson";
	private final String BYTE_FIELD_SUFFIX = "_prefix";
	private final String SERIALIZER_FIELD_SUFFIX = "_serializer";
	private final String SUB_SERIALIZER_FIELD_SUFFIX = "_sub_serializer";
	private final String FIELD_SET = "_set";
	private final String SUPER_CLASS = getType(AbstractObjectSerializer.class);
	private final String[] INTERFACE = new String[] { getType(ObjectSerializer.class) };

}
