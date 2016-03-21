package com.meidusa.fastbson.serializer.given;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.meidusa.fastbson.FastBsonSerializer;
import com.meidusa.fastbson.parse.BSONWriter;
import com.meidusa.fastbson.parse.ByteArrayBSONScanner;

public class HelloTest {

	
	@Test
	public void helloObject() {
			Hello hello = new Hello();
			hello.setAge(1001);
			hello.setName("asdfqwerqwer");
			hello.setGreeting("venus服务框架欢迎您。。。");
			Map<String,Object> map = new HashMap<String,Object>();
			hello.setMap(map);
			map.put("1", 1);
			map.put("2", new Long(2));
			map.put("3", new Integer(3));
			hello.setBigDecimal(new BigDecimal("1.341241233412"));
			
			FastBsonSerializer serializer = new FastBsonSerializer();
			BSONWriter writer = serializer.encode(hello);
			Object obj = serializer.decode(new ByteArrayBSONScanner(writer.getBuffer()), Hello.class);
			System.out.println("BSON result ={"+obj+" }");
	}
}
