package com.meidusa.fastbson.parse;

import java.math.BigDecimal;
import java.util.Date;

import com.meidusa.fastbson.util.ObjectId;

public interface BSONScanner {
	void reset();
	void skip(int skipNum) ;
	void skipField();
	void skipValue();
	public byte readType();
	byte getCurrentType();
	int readInt();
	long readLong();
	boolean readBoolean();
	Date readDate();
	String readString();
	String readCString();
	double readDouble();
	BigDecimal readBigDecimal();
	public byte readBSONByte();
	public int readBSONInt();
	public long readBSONLong();
	boolean readBSONBoolean();
	Date readBSONDate();
	String readBSONString();
	double readBSONDouble();
	BigDecimal readBSONBigDecimal();
	byte[] readBSONBinary();
	ObjectId readBSONOid();
	boolean hasRemaining();
	boolean match(byte[] given);
	
}
