package com.meidusa.fastbson;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ASMContext {
	/**
	 * base class , the class to generate serializer for
	 */
	public Class<?> baseClass;
	
	/**
	 * the serializer's class itself
	 */
	public String serializerClass;
	
	/**
	 * field informations, for each field in base class 
	 */
	public List<FieldInfo> fieldInfoList;

	/**
	 * asm stuff the variable index for write method
	 */
	private int variantIndex = 4;

	private Map<String, Integer> variants = new HashMap<String, Integer>();


	public ASMContext(int initVariantIndex) {
		this.variantIndex = initVariantIndex;
	}

	public int getVariantCount() {
		return variantIndex;
	}

	public int var(String name, int increment) {
		Integer i = variants.get(name);
		if (i == null) {
			variants.put(name, variantIndex);
			variantIndex += increment;
		}
		i = variants.get(name);
		return i.intValue();
	}

	public int var(String name) {
		Integer i = variants.get(name);
		if (i == null) {
			variants.put(name, variantIndex++);
		}
		i = variants.get(name);
		return i.intValue();
	}
}