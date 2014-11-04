package com.klaeff.cf.service;

import java.lang.reflect.Type;
import java.text.MessageFormat;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class ServiceUriSerializer implements JsonSerializer<ServiceUri> {

	private String scheme;
	private String host;
	private int port;

	public ServiceUriSerializer(String scheme, String host, int port) {
		this.scheme = scheme;
		this.host = host;
		this.port = port;
	}

	public JsonElement serialize(ServiceUri uri, Type typeOfSrc,
			JsonSerializationContext context) {

		String rewriteUri = MessageFormat.format(uri.getUri(), scheme,
				host, port);

		return new JsonPrimitive(rewriteUri);
	}
}
