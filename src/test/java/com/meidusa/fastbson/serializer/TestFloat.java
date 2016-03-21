package com.meidusa.fastbson.serializer;

import org.junit.Test;

import com.meidusa.fastbson.FastBsonSerializer;
import com.meidusa.fastbson.parse.ByteArrayBSONScanner;
import com.meidusa.fastbson.parse.BSONWriter;
import com.meidusa.fastbson.util.HexDump;

public class TestFloat {

	public static class A {
		private float x;
		private Float x2;
		private double x3;
		private Double x4;
		public float getX() {
			return x;
		}

		public void setX(float x) {
			this.x = x;
		}

		
		public Float getX2() {
			return x2;
		}

		public void setX2(Float x2) {
			this.x2 = x2;
		}
		
		public double getX3() {
			return x3;
		}

		public void setX3(double x3) {
			this.x3 = x3;
		}

		public Double getX4() {
			return x4;
		}

		public void setX4(Double x4) {
			this.x4 = x4;
		}

		@Override
		public String toString() {
			return "A [x=" + x + ", x2=" + x2 + ", x3=" + x3 + ", x4=" + x4
					+ "]";
		}

		
		
		
		

	}
	
	@Test
	public void floatType(){
		A a = new A();
		a.setX(1.25f);
		a.setX2(1.27f);
		a.setX3(9999.99);
		a.setX4(9999.9999);
		FastBsonSerializer serializer = new FastBsonSerializer();
		BSONWriter writer = serializer.encode(a);
		System.out.println(HexDump.dumpHexData("float", writer.getBuffer(), writer.getLength()));
		A a1 = (A) serializer.decode(new ByteArrayBSONScanner(writer.getBuffer()), A.class);
		System.out.println(a1);
	}
}
