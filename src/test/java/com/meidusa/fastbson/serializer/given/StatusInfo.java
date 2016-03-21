package com.meidusa.fastbson.serializer.given;

import java.io.Serializable;
import java.util.Arrays;

import com.meidusa.fastbson.serializer.eunm.Last;

public class StatusInfo implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private String bareJID;
	
	private byte status;
	
	private Last last;
	
	private String serverIp;
	
	private byte[] array;
	
	private long lastTime;

	
	
	public Last getLast() {
		return last;
	}

	public void setLast(Last last) {
		this.last = last;
	}

	public String getBareJID() {
		return bareJID;
	}

	public void setBareJID(String bareJID) {
		this.bareJID = bareJID;
	}

	public byte getStatus() {
		return status;
	}

	public void setStatus(byte code) {
		this.status = code;
	}

	public long getLastTime() {
		return lastTime;
	}

	public void setLastTime(long lastTime) {
		this.lastTime = lastTime;
	}

	public String getServerIp() {
		return serverIp;
	}

	public void setServerIp(String serverIp) {
		this.serverIp = serverIp;
	}
	
	
//	
//	public boolean isOnline() {
//		return status == Constants.ONLINE;
//	}
//	

	public byte[] getArray() {
		return array;
	}

	public void setArray(byte[] array) {
		this.array = array;
	}

	@Override
	public String toString() {
		return "StatusInfo [bareJID=" + bareJID + ", status=" + status + ", last=" + last + ", serverIp=" + serverIp
				+ ", array=" + Arrays.toString(array) + ", lastTime=" + lastTime + "]";
	}


	

	

	

}