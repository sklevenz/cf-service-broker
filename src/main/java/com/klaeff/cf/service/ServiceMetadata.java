package com.klaeff.cf.service;

public class ServiceMetadata {

	private String displayName = "mock service";
	private ServiceUri imageUrl = new ServiceUri(
			"{0}://{1}:{2,number,#}/service-broker/img/service.jpg");
	private String longDescription = "This is a mock service provider";
	private String providerDisplayName = "mock service provider";
	private ServiceUri documentationUrl = new ServiceUri(
			"{0}://{1}:{2,number,#}/service-broker/");
	private ServiceUri supportUrl = new ServiceUri(
			"{0}://{1}:{2,number,#}/service-broker/");

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getImageUrl() {
		return imageUrl.getUri();
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl.setUri(imageUrl);
	}

	public String getLongDescription() {
		return longDescription;
	}

	public void setLongDescription(String longDescription) {
		this.longDescription = longDescription;
	}

	public String getProviderDisplayName() {
		return providerDisplayName;
	}

	public void setProviderDisplayName(String providerDisplayName) {
		this.providerDisplayName = providerDisplayName;
	}

	public String getDocumentationUrl() {
		return documentationUrl.getUri();
	}

	public void setDocumentationUrl(String documentationUrl) {
		this.documentationUrl.setUri(documentationUrl);
	}

	public String getSupportUrl() {
		return supportUrl.getUri();
	}

	public void setSupportUrl(String supportUrl) {
		this.supportUrl.setUri(supportUrl);
	}

	// "displayName":"CloudAMQP",
	// "imageUrl":"https://d33na3ni6eqf5j.cloudfront.net/app_resources/18492/thumbs_112/img9069612145282015279.png",
	// "longDescription":"Managed, highly available, RabbitMQ clusters in the cloud",
	// "providerDisplayName":"84codes AB",
	// "documentationUrl":"http://docs.cloudfoundry.com/docs/dotcom/marketplace/services/cloudamqp.html",
	// "supportUrl":"http://www.cloudamqp.com/support.html"

}
