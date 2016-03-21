package com.meidusa.fastbson.serializer.given;

import org.junit.Test;

import com.meidusa.fastbson.ASMSerializerFactory;
import com.meidusa.fastbson.FastBsonSerializer;
import com.meidusa.fastbson.parse.ByteArrayBSONScanner;
import com.meidusa.fastbson.parse.BSONWriter;
import com.meidusa.fastbson.serializer.ObjectSerializer;
import com.meidusa.fastbson.serializer.eunm.Last;
import com.meidusa.fastbson.util.HexDump;

public class TestStatusInfoSerializer {

	
	@Test
	public void serializeStatusInfo () {
		StatusInfo info = new StatusInfo();
		info.setBareJID("aaa");
		info.setLastTime(111l);
		info.setServerIp("192.168.1.1");
		info.setStatus((byte) 1);
		info.setArray(new byte[]{1,2,3});
		info.setLast(Last.A);
		FastBsonSerializer serializer = new FastBsonSerializer();
		BSONWriter bts = serializer.encode(info);
		System.out.println(HexDump.dumpHexData("packet", bts.getBuffer(), bts.getLength()));
		StatusInfo info2 = (StatusInfo) serializer.decode(new ByteArrayBSONScanner(bts.getBuffer()),StatusInfo.class);
		System.out.println(info2);
	}
}
