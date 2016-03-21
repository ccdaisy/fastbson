package com.meidusa.fastbson.serializer.eunm;

public enum Last {
	A,
	B,
	C,
	D;
	
	
	public static void main(String[] args) {
		System.out.println(Last.valueOf("A"));
		Last.A.toString();
	}
}
