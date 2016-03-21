package com.meidusa.fastbson;

import java.lang.reflect.Type;
import java.util.List;

public class FieldInfo implements Comparable<FieldInfo> {

    private final String   name;

    private final Type     fieldType;
    
    private List<Class> types;
    private boolean needSerializer;
    private boolean isPublic;
    private Byte bsonType;
    private String asName;
    
    
    
    
    public Byte getBsonType() {
		return bsonType;
	}


	public void setBsonType(Byte bsonType) {
		this.bsonType = bsonType;
	}


	public FieldInfo(String name,boolean isPublic, Type fieldType){
        this.name = name;
        this.isPublic = isPublic;
        this.fieldType = fieldType;
    }

    
    public String getAsName() {
		return asName;
	}


	public void setAsName(String asName) {
		this.asName = asName;
	}


	public Class<?> getFieldClass() {
        return types.get(0);
    }

    public Type getFieldType() {
        return fieldType;
    }

    public String getName() {
        return name;
    }


    public int compareTo(FieldInfo o) {
        return this.name.compareTo(o.name);
    }

	public List<Class> getTypes() {
		return types;
	}

	public void setTypes(List<Class> subTypes) {
		this.types = subTypes;
	}

	public boolean isNeedSerializer() {
		return needSerializer;
	}

	public void setNeedSerializer(boolean needSerializer) {
		this.needSerializer = needSerializer;
	}

	public boolean isPublic() {
		return isPublic;
	}

	public void setPublic(boolean isPublic) {
		this.isPublic = isPublic;
	}
	
	

    
}
