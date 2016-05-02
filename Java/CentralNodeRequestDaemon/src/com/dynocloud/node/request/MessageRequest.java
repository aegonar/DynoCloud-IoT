package com.dynocloud.node.request;
import java.util.ArrayList;



public class MessageRequest {
	
	ArrayList<Header> headerList = new ArrayList<Header>();
	
	String payload;
	
	String method;
	
	String path;

	public ArrayList<Header> getHeaderList() {
		return headerList;
	}

	public void setHeaderList(ArrayList<Header> headerList) {
		this.headerList = headerList;
	}

	public String getPayload() {
		return payload;
	}

	public void setPayload(String payload) {
		this.payload = payload;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	@Override
	public String toString() {
		return "Message [headerList=" + headerList + ", payload=" + payload + ", method=" + method + ", path=" + path
				+ "]";
	}
	
	

}
