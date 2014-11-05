package com.klaeff.cf.service;

import java.util.Hashtable;


public class ServicePlanMetadataCostAmount {
	
	private Hashtable<String , String> amount = new Hashtable<String, String>();
	
	{
		amount.put("usd", "12.54");
		amount.put("eur", "10.12");
	}

	public Hashtable<String , String> getAmount() {
		return amount;
	}

	public void setAmount(Hashtable<String , String> amount) {
		this.amount = amount;
	}

}
