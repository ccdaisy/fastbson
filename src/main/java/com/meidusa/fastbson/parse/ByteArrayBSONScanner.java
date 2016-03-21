package com.meidusa.fastbson.parse;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.util.Date;

import com.meidusa.fastbson.FastBsonSerializer;
import com.meidusa.fastbson.exception.SerializeException;
import com.meidusa.fastbson.util.BSON;
import com.meidusa.fastbson.util.ObjectId;

public class ByteArrayBSONScanner implements BSONScanner{

	public static Charset CHARSET = Charset.forName("UTF-8");

	private int position = 0;
	private byte[] bsonPacket;
	
	private byte currentType = 0;;

	public ByteArrayBSONScanner(byte[] bsonPacket) {
		super();
		this.bsonPacket = bsonPacket;
	}
	
	public ByteArrayBSONScanner(byte[] bsonPacket, int position) {
		super();
		this.bsonPacket = bsonPacket;
		this.position = position;
	}
	
	
	public byte getCurrentType() {
		if(currentType == 0) {
			return BSON.OBJECT;
		} else {
			return currentType;
	
		}
	}

	public void reset() {
		this.position = 0;
	}
	public void skip(int skipNum) {
		position += skipNum;
	}
	public void skipValue() {
		byte type = currentType;
		switch (type) {
		case BSON.NUMBER:
			this.skip(8); //as long
			break;
		case BSON.ARRAY:
		case BSON.STRING:
		case BSON.OBJECT:
		case BSON.BINARY:
			this.skip(this.readInt() - 4);
			break;
		case BSON.NULL:
			break;
		case BSON.BIG_DECIMAL:
			this.skip(this.readInt() + 4);
			break;
		case BSON.NUMBER_INT:
			this.skip(4);
			break;
		case BSON.NUMBER_LONG:
			this.skip(8);
			break;
		case BSON.BOOLEAN:
			this.skip(1);
			break;
		case BSON.OID:
			this.skip(24);
			break;
		case BSON.TIMESTAMP:
		case BSON.DATE:
			this.skip(8);
			break;
		default:
			break;
		}
	}
	public void skipField() {
		byte type = currentType;
		this.readCString();
		switch (type) {
		case BSON.NUMBER:
			this.skip(8); //as long
			break;
		case BSON.ARRAY:
		case BSON.STRING:
		case BSON.OBJECT:
		case BSON.BINARY:
			this.skip(this.readInt() - 4);
			break;
		case BSON.NULL:
			break;
		case BSON.BIG_DECIMAL:
			this.skip(this.readInt() + 4);
			break;
		case BSON.NUMBER_INT:
			this.skip(4);
			break;
		case BSON.NUMBER_LONG:
			this.skip(8);
			break;
		case BSON.BOOLEAN:
			this.skip(1);
			break;
		case BSON.OID:
			this.skip(24);
			break;
		case BSON.TIMESTAMP:
		case BSON.DATE:
			this.skip(8);
			break;
		default:
			break;
		}
	}
	
	public byte readType() {
		currentType = bsonPacket[position++];
		return currentType;
	}

	public byte readBSONByte() {
		return (byte) this.readBSONInt();
	}
	
	public int readBSONInt() {
		switch (currentType) {
		case BSON.NUMBER_INT:
			return this.readInt();
		case BSON.NUMBER_LONG:
			return (int) this.readLong();
		case BSON.NUMBER:
			return (int) this.readDouble();
		case BSON.BIG_DECIMAL:
			return this.readBigDecimal().intValue();
		case BSON.STRING:
			return Integer.valueOf(this.readString());
		case BSON.NULL:
			return 0;
		default:
			throw new SerializeException.ErrorPacketException("not correct type for int");
		}
	}
	
	public int readInt() {
		int x = 0;
		x |= (0xFF & bsonPacket[position++]) << 0;
		x |= (0xFF & bsonPacket[position++]) << 8;
		x |= (0xFF & bsonPacket[position++]) << 16;
		x |= (0xFF & bsonPacket[position++]) << 24;
		return x;
	}

	public long readBSONLong() {
		switch (currentType) {
		case BSON.NUMBER_INT:
			return this.readInt();
		case BSON.NUMBER_LONG:
			return this.readLong();
		case BSON.NUMBER:
			return  (long) this.readDouble();
		case BSON.BIG_DECIMAL:
			return this.readBigDecimal().longValue();
		case BSON.STRING:
			return Long.valueOf(this.readString());
		case BSON.NULL:
			return 0L;
		default:
			throw new SerializeException.ErrorPacketException("not correct type for long");
		}
	}
	
	public long readLong() {
		long x = 0;
		x |= (long) (0xFFL & bsonPacket[position++]) << 0;
		x |= (long) (0xFFL & bsonPacket[position++]) << 8;
		x |= (long) (0xFFL & bsonPacket[position++]) << 16;
		x |= (long) (0xFFL & bsonPacket[position++]) << 24;
		x |= (long) (0xFFL & bsonPacket[position++]) << 32;
		x |= (long) (0xFFL & bsonPacket[position++]) << 40;
		x |= (long) (0xFFL & bsonPacket[position++]) << 48;
		x |= (long) (0xFFL & bsonPacket[position++]) << 56;
		return x;
	}
	
	public boolean readBSONBoolean() {
		switch (currentType) {
		case BSON.NUMBER_INT:
			return this.readInt() > 0 ? true:false;
		case BSON.NUMBER_LONG:
			return this.readLong() > 0 ? true:false;
		case BSON.NUMBER:
			return this.readDouble() > 0 ? true:false;
		case BSON.BIG_DECIMAL:
			return this.readBigDecimal().doubleValue() != 0 ? true:false;
		case BSON.STRING:
			return this.readString().toLowerCase().equals("true");
		case BSON.NULL:
			return false;
		case BSON.BOOLEAN:
			return this.readBoolean();
		default:
			throw new SerializeException.ErrorPacketException("not correct type for bool");
		}
	}
	public boolean readBoolean() {
		return bsonPacket[position++] > 0 ? true : false;
	}
	
	public Date readBSONDate() {
		switch (currentType) {
		case BSON.NUMBER_LONG:
			return new Date(this.readLong());
		case BSON.NULL:
			return null;
		case BSON.DATE:
			return this.readDate();
		case BSON.STRING:
			try {
				return FastBsonSerializer.dateFormat.parse(this.readString());
			} catch (ParseException e) {
				throw new SerializeException.ErrorPacketException("wrong date format");
			}
		default:
			throw new SerializeException.ErrorPacketException("not correct type for date");
		}
	}
	public Date readDate() {
		return new Date(this.readLong());
	}

	public ObjectId readBSONOid() {
		byte[] oid = new byte[12];
		System.arraycopy(bsonPacket, position, oid, 0, 12);
		position += 12;
		return new ObjectId(oid);
	}
	
	public String readBSONString() {
		switch (currentType) {
		case BSON.NUMBER_INT:
			return String.valueOf(this.readInt());
		case BSON.NUMBER_LONG:
			return String.valueOf(this.readLong());
		case BSON.NUMBER:
			return String.valueOf(this.readDouble());
		case BSON.BIG_DECIMAL:
			return String.valueOf(this.readBigDecimal());
		case BSON.STRING:
			return this.readString();
		case BSON.NULL:
			return null;
		case BSON.BOOLEAN:
			return String.valueOf(this.readBoolean());
		case BSON.DATE:
			return FastBsonSerializer.dateFormat.format(this.readDate()); 
		default:
			throw new SerializeException.ErrorPacketException("not correct type for string");
		}

	}
	
	public String readString() {
		String returnStr = null;
		int strLength = this.readInt();
		if (strLength < 0 || strLength > (3 * 1024 * 1024))
			throw new RuntimeException("bad string size: " + strLength);
		returnStr = new String(bsonPacket, position, strLength - 1, CHARSET);
		position += strLength;
		return returnStr;

	}
	public String readCString() {
		String returnStr = null;
		int begin = position;
		while (position < bsonPacket.length) {
			if (bsonPacket[position++] == 0x00) {
				break;
			}
		}
		int length = position - begin - 1;
		returnStr = new String(bsonPacket, begin, length, CHARSET);
		return returnStr;
	}

	public double readBSONDouble() {
		switch (currentType) {
		case BSON.BIG_DECIMAL:
			return this.readBigDecimal().doubleValue();
		case BSON.NUMBER:
			return this.readDouble();
		case BSON.NUMBER_INT:
			return this.readInt();
		case BSON.NUMBER_LONG:
			return this.readLong();
		case BSON.NULL:
			return 0;
		case BSON.STRING:
			return Double.valueOf(this.readString());
		default:
			throw new SerializeException.ErrorPacketException("not correct type for double");
		}
	}
	
	public double readDouble() {
		return Double.longBitsToDouble(this.readLong());
	}

	public BigDecimal readBSONBigDecimal() {
		switch (currentType) {
		case BSON.BIG_DECIMAL:
			return this.readBigDecimal();
		case BSON.NUMBER:
			return new BigDecimal(this.readDouble());
		case BSON.NUMBER_INT:
			return new BigDecimal(this.readInt());
		case BSON.NUMBER_LONG:
			return new BigDecimal(this.readLong());
		case BSON.NULL:
			return new BigDecimal(0);
		case BSON.STRING:
			return new BigDecimal(this.readString());
		default:
			throw new SerializeException.ErrorPacketException("not correct type for double");
		}
	}
	public BigDecimal readBigDecimal() {
		int size = this.readInt();
		byte[] bts = new byte[size];
		System.arraycopy(bsonPacket, position, bts, 0, size);
		position += size;
		int scale = this.readInt();
		return new BigDecimal(new BigInteger(bts), scale);

	}

	public boolean hasRemaining() {
		return position < bsonPacket.length;
	}

	public byte[] readBSONBinary() {
		int size = this.readInt();
		this.skip(1);
		byte[] bts = new byte[size];
		System.arraycopy(bsonPacket, position, bts, 0, size);
		position+=size;
		return bts;
	}

	public boolean match(byte[] given) {
		for (int i = 0; i < given.length; i++) {
			try {
				if (given[i] != bsonPacket[position + i]) {
					return false;
				}
			} catch (Exception e) {
				return false;
			}
		}
		position += given.length;
		return true;
	}

	public byte[] getBsonPacket() {
		return bsonPacket;
	}

	public void setBsonPacket(byte[] bsonPacket) {
		this.bsonPacket = bsonPacket;
	}
}
