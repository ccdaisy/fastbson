package com.meidusa.fastbson.parse;

import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Date;
import java.util.Stack;

import com.meidusa.fastbson.util.BSON;

/**
 * @author lichencheng.daisy
 * 
 */
public class ByteArrayBSONWriter implements BSONWriter {

	private static final byte BLANK = 0;
	private static final byte TRUE = 1;
	private static final byte FALSE = 0;
	private byte[] buffer;

	private int current;

	private Stack<Integer> subBeginPos = new Stack<Integer>();

	public void clear() {
		if (buffer.length > BSONWriter.BUFFER_REINITIALIZE_SIZE) {
			buffer = new byte[BSONWriter.BUF_SIZE];
		}
		if (subBeginPos.size() > BSONWriter.INNER_OBJECT_LAYER_MAX_SIZE) {
			subBeginPos = new Stack<Integer>();
		}
		current = 0;

	}

	private void ensureLength(int length) {
		if (current + length >= buffer.length) {
			byte[] newBuffer = new byte[Math.max(buffer.length * 2,current + length)];
			System.arraycopy(buffer, 0, newBuffer, 0, buffer.length);
			buffer = newBuffer;
		}

	}

	public void skip(int num) {
		current += num;
	}

	public void writeBytes(byte[] bts) {
		ensureLength(bts.length);
		System.arraycopy(bts, 0, buffer, current, bts.length);
		current += bts.length;
	}

	private void writeIntAt(int position, int i) {
		ensureLength(position + 4);
		buffer[position++] = (byte) i;
		buffer[position++] = (byte) (i >> 8);
		buffer[position++] = (byte) (i >> 16);
		buffer[position++] = (byte) (i >> 24);

	}

	public ByteArrayBSONWriter() {
		buffer = new byte[BSONWriter.BUF_SIZE];
	}

	public void begin() {
		skip(4);
	}

	public void end() {
		writeIntAt(0, current);
		write((byte) BSON.EOO);
	}

	public void writeValue(boolean b) {
		ensureLength(1);
		buffer[current++] = (b == true ? TRUE : FALSE);
	}

	public void writeValue(Boolean b) {
		ensureLength(1);
		buffer[current++] = (b == true ? TRUE : FALSE);
	}

	public void writeValue(int i) {
		ensureLength(4);
		buffer[current++] = (byte) i;
		buffer[current++] = (byte) (i >> 8);
		buffer[current++] = (byte) (i >> 16);
		buffer[current++] = (byte) (i >> 24);
	}

	public void writeValue(Integer i) {
		ensureLength(4);
		buffer[current++] = (byte) (i >> 0);
		buffer[current++] = (byte) (i >> 8);
		buffer[current++] = (byte) (i >> 16);
		buffer[current++] = (byte) (i >> 24);
	}

	public void writeValue(long l) {
		ensureLength(8);
		buffer[current++] = (byte) l;
		buffer[current++] = (byte) (l >> 8);
		buffer[current++] = (byte) (l >> 16);
		buffer[current++] = (byte) (l >> 24);
		buffer[current++] = (byte) (l >> 32);
		buffer[current++] = (byte) (l >> 40);
		buffer[current++] = (byte) (l >> 48);
		buffer[current++] = (byte) (l >> 56);

	}

	public void writeValue(Long l) {
		ensureLength(8);
		buffer[current++] = (byte) (l >> 0);
		buffer[current++] = (byte) (l >> 8);
		buffer[current++] = (byte) (l >> 16);
		buffer[current++] = (byte) (l >> 24);
		buffer[current++] = (byte) (l >> 32);
		buffer[current++] = (byte) (l >> 40);
		buffer[current++] = (byte) (l >> 48);
		buffer[current++] = (byte) (l >> 56);
	}

	public void writeValue(float f) {
		ensureLength(8);
		Long l = Double.doubleToLongBits(f);
		buffer[current++] = (byte) (l >> 0);
		buffer[current++] = (byte) (l >> 8);
		buffer[current++] = (byte) (l >> 16);
		buffer[current++] = (byte) (l >> 24);
		buffer[current++] = (byte) (l >> 32);
		buffer[current++] = (byte) (l >> 40);
		buffer[current++] = (byte) (l >> 48);
		buffer[current++] = (byte) (l >> 56);

	}

	public void writeValue(Float f) {
		ensureLength(8);
		Long l = Double.doubleToLongBits(f);
		buffer[current++] = (byte) (l >> 0);
		buffer[current++] = (byte) (l >> 8);
		buffer[current++] = (byte) (l >> 16);
		buffer[current++] = (byte) (l >> 24);
		buffer[current++] = (byte) (l >> 32);
		buffer[current++] = (byte) (l >> 40);
		buffer[current++] = (byte) (l >> 48);
		buffer[current++] = (byte) (l >> 56);
	}

	public void writeValue(double d) {
		ensureLength(8);
		Long l = Double.doubleToLongBits(d);
		buffer[current++] = (byte) (l >> 0);
		buffer[current++] = (byte) (l >> 8);
		buffer[current++] = (byte) (l >> 16);
		buffer[current++] = (byte) (l >> 24);
		buffer[current++] = (byte) (l >> 32);
		buffer[current++] = (byte) (l >> 40);
		buffer[current++] = (byte) (l >> 48);
		buffer[current++] = (byte) (l >> 56);
	}

	public void writeValue(Double d) {
		ensureLength(8);
		Long l = Double.doubleToLongBits(d);
		buffer[current++] = (byte) (l >> 0);
		buffer[current++] = (byte) (l >> 8);
		buffer[current++] = (byte) (l >> 16);
		buffer[current++] = (byte) (l >> 24);
		buffer[current++] = (byte) (l >> 32);
		buffer[current++] = (byte) (l >> 40);
		buffer[current++] = (byte) (l >> 48);
		buffer[current++] = (byte) (l >> 56);
	}

	public void writeValue(String s) {
		byte[] bts = s.getBytes(Charset.forName("UTF-8"));
		ensureLength(bts.length + 5);
		int i = bts.length + 1;
		buffer[current++] = (byte) (i >> 0);
		buffer[current++] = (byte) (i >> 8);
		buffer[current++] = (byte) (i >> 16);
		buffer[current++] = (byte) (i >> 24);
		System.arraycopy(bts, 0, buffer, current, i - 1);
		current += i - 1;
		buffer[current++] = BLANK;

	}

	public void writeValue(Date dt) {
		ensureLength(8);
		long l = dt.getTime();
		buffer[current++] = (byte) l;
		buffer[current++] = (byte) (l >> 8);
		buffer[current++] = (byte) (l >> 16);
		buffer[current++] = (byte) (l >> 24);
		buffer[current++] = (byte) (l >> 32);
		buffer[current++] = (byte) (l >> 40);
		buffer[current++] = (byte) (l >> 48);
		buffer[current++] = (byte) (l >> 56);
	}

	public void writeValue(BigDecimal dt) {
		byte[] bts = dt.unscaledValue().toByteArray();
		int scale = dt.scale();
		int size = bts.length;

		ensureLength(bts.length + 8);
		buffer[current++] = (byte) (size >> 0);
		buffer[current++] = (byte) (size >> 8);
		buffer[current++] = (byte) (size >> 16);
		buffer[current++] = (byte) (size >> 24);
		System.arraycopy(bts, 0, buffer, current, size);
		current += size;

		buffer[current++] = (byte) (scale >> 0);
		buffer[current++] = (byte) (scale >> 8);
		buffer[current++] = (byte) (scale >> 16);
		buffer[current++] = (byte) (scale >> 24);

	}

	public void write(byte b) {
		ensureLength(1);
		buffer[current++] = b;
	}
	
	public void writeValue(byte b) {
		this.writeValue((int)b);

	}

	public void writeValue(Byte b) {
		this.writeValue((int)b);

	}

	public void writeCString(String s) {
		ensureLength(s.length() + 1);
		byte[] strBts = s.getBytes(Charset.forName("UTF-8"));
		this.writeBytes(strBts);
		buffer[current++] = BLANK;

	}

	//

	public void beginArray() {
		this.subBeginPos.push(current);
		ensureLength(4);
		skip(4);
	}

	public void beginObject() {
		this.subBeginPos.push(current);
		ensureLength(4);
		skip(4);
	}

	public void endArray() {
		ensureLength(1);
		write(BSON.EOO);
		int pos = subBeginPos.pop();
		writeIntAt(pos, current - pos);
	}

	public void endObject() {
		ensureLength(1);
		write(BSON.EOO);
		int pos = subBeginPos.pop();
		writeIntAt(pos, current - pos);
	}

	public byte[] getBuffer() {
		return buffer;
	}

	public int getLength() {
		return current;
	}


	public void writeValue(byte[] val) {
		this.writeValue(val.length);
		this.write((byte) 0x00);
		this.writeBytes(val);

	}

	@Override
	public String toString() {
		return "ByteArrayBSONWriter [buffer=" + Arrays.toString(buffer) + ", current=" + current
				+ ", subBeginPos=" + subBeginPos + "]";
	}

}
