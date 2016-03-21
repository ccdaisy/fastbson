package com.meidusa.fastbson.serializer.given.xml;

import java.util.Date;

import org.junit.Test;

import com.meidusa.fastbson.FastBsonSerializer;
import com.meidusa.fastbson.parse.BSONScanner;
import com.meidusa.fastbson.parse.BSONWriter;
import com.meidusa.fastbson.parse.ByteArrayBSONScanner;
import com.meidusa.fastbson.util.HexDump;
import com.meidusa.fastbson.util.ObjectId;
import com.thoughtworks.xstream.XStream;

public class TestGivenSerializer {

	public static XStream xstream = new XStream();

	@Test
	public void xstreamSerialize() {

		Person person = new Person();
		person.setFax(new PhoneNumber(111, "111"));
		person.setFirstname("aaa");
		person.setLastname("bbb");
		person.setPhone(new PhoneNumber(111, "111"));

		xstream.alias("person", Person.class);

		String xml = xstream.toXML(person);

		System.out.println(xml);

		FastBsonSerializer serializer = new FastBsonSerializer();
		FastBsonSerializer.registerSerializer(Person.class,
				new PersonSerializer());
		BSONWriter writer = serializer.encode(person);
		System.out.println(HexDump.dumpHexData("person", writer.getBuffer(),
				writer.getLength()));
		Person personDecoded = (Person) serializer.decode(
				new ByteArrayBSONScanner(writer.getBuffer()), Person.class);

		System.out.println(personDecoded.toString());
	}
}
