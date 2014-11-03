package com.klaeff.cf;

public class NotFoundException extends Exception {

	private static final long serialVersionUID = 1L;

	public NotFoundException(String pathInfo) {
		super(pathInfo);
	}

}
