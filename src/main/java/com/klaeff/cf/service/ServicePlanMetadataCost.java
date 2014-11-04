package com.klaeff.cf.service;

public class ServicePlanMetadataCost {

	private String unit = "1GB of messages over 20GB";
	
	private ServicePlanMetadataCostAmount [] amounts = {new ServicePlanMetadataCostAmount(), new ServicePlanMetadataCostAmount()};

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public ServicePlanMetadataCostAmount[] getAmounts() {
		return amounts;
	}

	public void setAmounts(ServicePlanMetadataCostAmount[] amounts) {
		this.amounts = amounts;
	}
	
}
