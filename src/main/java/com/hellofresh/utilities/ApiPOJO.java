package com.hellofresh.utilities;

import java.util.Map;

public class ApiPOJO {
	private String url;
	private String method;
	private Map<String,String> headers;
	private String body;
	private String name;
	
	public ApiPOJO(){		
	}
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
