package com.meidusa.fastbson.serializer.given;


import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;

import org.apache.commons.lang.builder.ToStringBuilder;

public class Hello implements Serializable  {
	private static final long serialVersionUID = 1L;
	private String name;
	private String greeting;
	private int age;
	private byte flag;
	private double cost;
	private Map<String,Object> map;
	private BigDecimal bigDecimal;
	
	public byte getFlag() {

		return flag;
	}

	public void setFlag(byte flag) {
		this.flag = flag;
	}

	public Map<String, Object> getMap() {
		return map;
	}

	public void setMap(Map<String, Object> map) {
		this.map = map;
	}

	public String getName() {
		return name;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public double getCost() {
		return cost;
	}

	public void setCost(double cost) {
		this.cost = cost;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getGreeting() {
		return greeting;
	}

	public void setGreeting(String greeting) {
		this.greeting = greeting;
	}

	
	public String toString(){
		return ToStringBuilder.reflectionToString(this);
	}

	public BigDecimal getBigDecimal() {
		return bigDecimal;
	}

	public void setBigDecimal(BigDecimal bigDecimal) {
		this.bigDecimal = bigDecimal;
	}
	
	
}
