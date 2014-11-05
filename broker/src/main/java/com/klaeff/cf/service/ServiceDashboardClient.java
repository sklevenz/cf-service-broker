package com.klaeff.cf.service;

import com.google.gson.annotations.SerializedName;


public class ServiceDashboardClient {

	private String id = "client-id-1";
	private String secret = "secret-1";
	@SerializedName("redirect_uri")
	private ServiceUri redirectUrl =  new ServiceUri("{0}://{1}:{2,number,#}/service-broker/");

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	public String getRedirectUrl() {
		return redirectUrl.getUri();
	}

	public void setRedirectUrl(String redirectUrl) {
		this.redirectUrl.setUri(redirectUrl);
	}

}
