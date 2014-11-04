package com.klaeff.cf.service;

public class ServicesFactory {

	public synchronized static Services create() {
		Services s = new Services();
		return s;
	}

}
