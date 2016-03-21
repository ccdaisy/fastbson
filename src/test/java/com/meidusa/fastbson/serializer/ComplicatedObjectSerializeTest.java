package com.meidusa.fastbson.serializer;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.meidusa.fastbson.FastBsonSerializer;
import com.meidusa.fastbson.parse.ByteArrayBSONScanner;
import com.meidusa.fastbson.parse.BSONWriter;
import com.meidusa.fastbson.serializer.ComplicatedObject.XXX;
import com.meidusa.fastbson.util.HexDump;

public class ComplicatedObjectSerializeTest {
	
	private ComplicatedObject obj;
	private FastBsonSerializer serializer;

	@Before
	public void constructClass() {
		serializer = new FastBsonSerializer();
		obj = new ComplicatedObject();
		obj.setE(new BigDecimal("1.2598673"));
		obj.setA(new int[] { 1, 2, 3 });
		HashMap<String, ComplicatedComponentObject> map = new HashMap<String, ComplicatedComponentObject>();
		map.put("1~~", new ComplicatedComponentObject("1", 12345));
		map.put("2~~", new ComplicatedComponentObject("2", 23456));
		obj.setB(map);
		obj.setC("ccc");
		List<Long> list = new LinkedList<Long>();
		list.add(1L);
		list.add(2L);
		list.add(3L);
		obj.setX(ComplicatedObject.XXX.X2);
		obj.setD(list);
		List<XXX> enumlist = new LinkedList<XXX>();
		enumlist.add(XXX.X1);
		enumlist.add(XXX.X2);
		enumlist.add(XXX.X3);
		obj.setF(enumlist);
	}

	@Test
	public void testSerialize() {
		System.out.println(obj);
		long begin = System.currentTimeMillis();
		long encodeTime = 0;
		long decodeTime = 0;
		for (int i = 0; i < 1; i++) {
			Object o = null;

			long encodeBegin = System.currentTimeMillis();
			BSONWriter writer = serializer.encode(obj);
			 System.out.println(HexDump.dumpHexData("ComplicatedObject",
					 writer.getBuffer(), writer.getLength()));
			encodeTime += System.currentTimeMillis() - encodeBegin;
			long decodeBegin = System.currentTimeMillis();
			o = serializer.decode(new ByteArrayBSONScanner(writer.getBuffer()),
					obj.getClass());
			System.out.println(o);
			decodeTime += System.currentTimeMillis() - decodeBegin;
		}
		System.out.println(System.currentTimeMillis() - begin);
		System.out.println(encodeTime);
		System.out.println(decodeTime);
	}
}
