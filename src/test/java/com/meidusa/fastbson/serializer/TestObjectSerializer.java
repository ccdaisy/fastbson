package com.meidusa.fastbson.serializer;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.HashMap;

import org.junit.Test;

import com.meidusa.fastbson.ASMSerializerFactory;
import com.meidusa.fastbson.FastBsonSerializer;
import com.meidusa.fastbson.parse.ByteArrayBSONScanner;
import com.meidusa.fastbson.parse.BSONWriter;
import com.meidusa.fastbson.parse.ByteArrayBSONWriter;
import com.meidusa.fastbson.util.HexDump;

public class TestObjectSerializer {

	public static class A {
		private String a;
		private Integer b;
		private String c;

		public String getA() {
			return a;
		}

		public void setA(String a) {
			this.a = a;
		}
		
		public void setD(int[] d) {
			
		}
		
		public void setE(BigDecimal bd) {
			
		}

		public Integer getB() {
			return b;
		}

		public void setB(Integer b) {
			this.b = b;
		}

		public String getC() {
			return c;
		}

		public void setC(String c) {
			this.c = c;
		}

		@Override
		public String toString() {
			return "A [a=" + a + ", b=" + b + ", c=" + c + "]";
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((a == null) ? 0 : a.hashCode());
			result = prime * result + ((b == null) ? 0 : b.hashCode());
			result = prime * result + ((c == null) ? 0 : c.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			A other = (A) obj;
			if (a == null) {
				if (other.a != null)
					return false;
			} else if (!a.equals(other.a))
				return false;
			if (b == null) {
				if (other.b != null)
					return false;
			} else if (!b.equals(other.b))
				return false;
			if (c == null) {
				if (other.c != null)
					return false;
			} else if (!c.equals(other.c))
				return false;
			return true;
		}

	}

	@Test
	public void objectSerialize() {
		A a = new A();
		a.setA("aaa");
		a.setB(1);
		a.setC("ccc");

		BSONWriter writer = new ByteArrayBSONWriter();
		ObjectSerializer serialzer = ASMSerializerFactory.getSerializer(A.class);
		serialzer.serialize(writer, a, null, 0);

		ByteArrayBSONScanner scanner = new ByteArrayBSONScanner(writer.getBuffer());
		A a2 = (A) serialzer.deserialize(scanner, null, 0);

		org.junit.Assert.assertEquals(a, a2);

	}

	@Test
	public void objectInMapSerialize() {
		A a = new A();
		a.setA("aaa");
		a.setB(1);
		a.setC("ccc");
		HashMap<String, A> map = new HashMap<String, A>();
		map.put("a", a);
		map.put("b", a);

		ParameterizedType type = new ParameterizedType() {

			public Type getRawType() {
				return HashMap.class;
			}

			public Type getOwnerType() {
				// TODO Auto-generated method stub
				return null;
			}

			public Type[] getActualTypeArguments() {
				return new Type[] { String.class, A.class };
			}
		};
		FastBsonSerializer serializer = new FastBsonSerializer();
		System.out.println(System.currentTimeMillis());
		BSONWriter writer = serializer.encode(map);
		System.out.println(HexDump.dumpHexData("", writer.getBuffer(), writer.getLength()));
		ByteArrayBSONScanner scanner = new ByteArrayBSONScanner(writer.getBuffer());
		HashMap<String, A> map2 = (HashMap<String, A>) serializer.decode(scanner, type);
		System.out.println(System.currentTimeMillis());

		// org.junit.Assert.assertEquals(map, map2);

	}

	@Test
	public void objectInListSerialize() {
		A a = new A();
		a.setA("aaa");
		a.setB(1);
		a.setC("ccc");
		HashMap<String, A> map = new HashMap<String, A>();
		map.put("a", a);
		map.put("b", a);

		ParameterizedType type = new ParameterizedType() {

			public Type getRawType() {
				return HashMap.class;
			}

			public Type getOwnerType() {
				// TODO Auto-generated method stub
				return null;
			}

			public Type[] getActualTypeArguments() {
				return new Type[] { String.class, A.class };
			}
		};
		FastBsonSerializer serializer = new FastBsonSerializer();
		System.out.println(System.nanoTime());
		BSONWriter writer = serializer.encode(map);
		// System.out.println(HexDump.dumpHexData("bytes[]",
		// writer.getBuffer(), writer.getLength()));
		ByteArrayBSONScanner scanner = new ByteArrayBSONScanner(writer.getBuffer());
		HashMap<String, A> map2 = (HashMap<String, A>) serializer.decode(scanner, type);
		System.out.println(System.nanoTime());

		 org.junit.Assert.assertEquals(map, map2);

	}
}
