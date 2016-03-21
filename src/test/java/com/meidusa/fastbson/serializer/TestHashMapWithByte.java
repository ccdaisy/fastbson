package com.meidusa.fastbson.serializer;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.meidusa.fastbson.FastBsonSerializer;
import com.meidusa.fastbson.parse.ByteArrayBSONScanner;
import com.meidusa.fastbson.parse.BSONWriter;
import com.meidusa.fastbson.util.HexDump;

public class TestHashMapWithByte {
	
	@Test
	public void testBtsMap() {
		HashMap map = new HashMap<String, byte[]> ();
		map.put("aaa", new  byte[] {1,2,3});
		map.put("bbb", new  byte[] {1,2,3});
		map.put("ccc", new  byte[] {1,2,3});
		
		FastBsonSerializer serializer = new FastBsonSerializer();
		BSONWriter writer = serializer.encode(map);
		System.out.println(HexDump.dumpHexData("map", writer.getBuffer(), writer.getLength()));
		
		Map mapGotton = (Map) serializer.decode(new ByteArrayBSONScanner(writer.getBuffer()));
		
	}
	
	@Test
	public void testByteMap() {
		HashMap map = new HashMap<String, byte[]> ();
		map.put("aaa", (byte)1);
		map.put("bbb", (byte)2);
		map.put("ccc", (byte)3);
		
		FastBsonSerializer serializer = new FastBsonSerializer();
		BSONWriter writer = serializer.encode(map);
		System.out.println(HexDump.dumpHexData("map", writer.getBuffer(), writer.getLength()));
		
		Map mapGotton = (Map) serializer.decode(new ByteArrayBSONScanner(writer.getBuffer()));
		
	}
}
