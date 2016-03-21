package com.meidusa.fastbson.serializer;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.meidusa.fastbson.ASMSerializerFactory;
import com.meidusa.fastbson.FastBsonSerializer;
import com.meidusa.fastbson.parse.ByteArrayBSONScanner;
import com.meidusa.fastbson.parse.BSONWriter;
import com.meidusa.fastbson.parse.ByteArrayBSONWriter;
import com.meidusa.fastbson.util.HexDump;

public class TestUnknownTypeSerializer {
	/**
	 * @author daisyli89
	 * 
	 */
	public static class TestDomain {
		public int a;
		public Object b;

		@Override
		public String toString() {
			return "TestDomain [a=" + a + ", b=" + b + "]";
		}

	}

	@Test
	public void testUnknownTypeSerialize() {
		TestDomain domain = new TestDomain();
		domain.a = 0;
		HashMap map = new HashMap();
		map.put("a", 123);
		TestDomain domain2 = new TestDomain();
		domain2.a = 5;
		domain2.b = new int[] { 1, 2, 3 };
		map.put("b", "abcde");
		map.put("c", domain2);
		domain.b = map;

		System.out.println(domain);
		FastBsonSerializer serializer = new FastBsonSerializer();
		BSONWriter writer = serializer.encode(domain);
		System.out.println(HexDump.dumpHexData("unknown", writer.getBuffer(), writer.getLength()));
		TestDomain domain3 = (TestDomain) serializer.decode(new ByteArrayBSONScanner(
				writer.getBuffer()), TestDomain.class);

		System.out.println(domain3);

	}

	@Test
	public void unknownType() {
		FastBsonSerializer serializer = new FastBsonSerializer();
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("name", "aadsfasdf");
		data.put("cost", 5.01);
		BSONWriter writer = serializer.encode(data);
		Map map = (Map) serializer.decode(new ByteArrayBSONScanner(writer.getBuffer()),
				HashMap.class);
		System.out.println(map);
	}
}
