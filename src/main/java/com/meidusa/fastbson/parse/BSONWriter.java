package com.meidusa.fastbson.parse;

import java.math.BigDecimal;
import java.util.Date;

public interface BSONWriter {
	
	public static final int BUF_SIZE = 64*1024;
	
	public static final int BUFFER_REINITIALIZE_SIZE = 512 * 1024;
	
	public static final int INNER_OBJECT_LAYER_MAX_SIZE = 10;
	
	public void clear();
	public void skip(int length);
	
	public void write(byte bt);

	public void writeBytes(byte[] bts);
	
	public void writeValue(BigDecimal b);
	
	public void writeValue(boolean b);
	public void writeValue(Boolean b);
	
	public void writeValue(int i);
	public void writeValue(Integer i);
	
	public void writeValue(long l);
	public void writeValue(Long l);
	
	public void writeValue(float f);
	public void writeValue(Float f);
	
	public void writeValue(double d);
	public void writeValue(Double d);
	
	public void writeValue(String s);
	
	public void writeValue(byte b);
	public void writeValue(Byte b);

	public void writeValue(byte[] val);
	public void writeValue(Date dt);
	
	public void writeCString(String s);

	public void beginArray();

	public void endArray();
	
	public void beginObject();

	public void endObject();

	public void begin();

	public void end();
	
	public byte[] getBuffer();
	
	public int getLength();
	
	
}
