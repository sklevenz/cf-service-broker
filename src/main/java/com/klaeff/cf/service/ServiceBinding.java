package com.klaeff.cf.service;

import com.google.gson.annotations.SerializedName;

public class ServiceBinding {

	@SerializedName("service_id")
	private String serviceId;
	@SerializedName("plan_id")
	private String planId;

	@SerializedName("app_guid")
	private String appGuid;

	public String getServiceId() {
		return serviceId;
	}

	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}

	public String getPlanId() {
		return planId;
	}

	public void setPlanId(String planId) {
		this.planId = planId;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ServiceBinding) {
			ServiceBinding s = (ServiceBinding) obj;
			String s1 = serviceId + planId + appGuid ;
			String s2 = s.getServiceId() + s.getPlanId() + s.getAppGuid();
			
			return s1.equalsIgnoreCase(s2);
		}
		return false;
	}

	public String getAppGuid() {
		return appGuid;
	}

	public void setAppGuid(String appGuid) {
		this.appGuid = appGuid;
	}

}
