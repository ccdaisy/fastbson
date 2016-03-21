package com.meidusa.fastbson.serializer;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ComplicatedObject {

	private int[] a;
	private HashMap<String, ComplicatedComponentObject> b;
	private String c;
	private List<Long> d;
	private BigDecimal e;
	private List<XXX> f;
	private XXX x;
	
	
	public static enum XXX {
		X1,
		X2,
		X3
	}
	
	
	

	public List<XXX> getF() {
		return f;
	}

	public void setF(List<XXX> f) {
		this.f = f;
	}

	public XXX getX() {
		return x;
	}

	public void setX(XXX x) {
		this.x = x;
	}

	public int[] getA() {
		return a;
	}

	public void setA(int[] a) {
		this.a = a;
	}

	public HashMap<String, ComplicatedComponentObject> getB() {
		return b;
	}

	public void setB(HashMap<String, ComplicatedComponentObject> b) {
		this.b = b;
	}

	public String getC() {
		return c;
	}

	public void setC(String c) {
		this.c = c;
	}

	public List<Long> getD() {
		return d;
	}

	public void setD(List<Long> d) {
		this.d = d;
	}

	public BigDecimal getE() {
		return e;
	}

	public void setE(BigDecimal e) {
		this.e = e;
	}

	@Override
	public String toString() {
		return "ComplicatedObject [a=" + Arrays.toString(a) + ", b=" + b + ", c=" + c + ", d=" + d + ", e=" + e
				+ ", f=" + f + ", x=" + x + "]";
	}



	
}
