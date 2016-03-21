package com.meidusa.fastbson.serializer.given;

import org.junit.Test;

import com.meidusa.fastbson.FastBsonSerializer;
import com.meidusa.fastbson.parse.BSONWriter;
import com.meidusa.fastbson.parse.ByteArrayBSONScanner;

public class TestReplace {
	public static interface MyInterface<T> {

	}

	public static class DefaultMyImpl implements MyInterface<Integer> {
		private String name;
		private int age;
		private boolean yes;
		
		
		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public int getAge() {
			return age;
		}

		public void setAge(int age) {
			this.age = age;
		}

		public boolean isYes() {
			return yes;
		}

		public void setYes(boolean yes) {
			this.yes = yes;
		}

	}
	@Test
	public void replace() {

		
		FastBsonSerializer.registerReplace(MyInterface.class, DefaultMyImpl.class);
		FastBsonSerializer serializer = new FastBsonSerializer();
		DefaultMyImpl hello = new DefaultMyImpl();
		hello.setName("asdfasfasdf 阿斯地方");
		hello.setAge(1111);
		hello.setYes(true);
		BSONWriter bts = serializer.encode(hello);
		MyInterface obj = (MyInterface) serializer.decode(new ByteArrayBSONScanner(bts.getBuffer()), MyInterface.class);
	}
}
