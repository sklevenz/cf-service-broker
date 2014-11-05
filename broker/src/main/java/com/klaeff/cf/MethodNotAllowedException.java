package com.klaeff.cf;

public class MethodNotAllowedException extends Exception {

	private static final long serialVersionUID = 1L;

	public MethodNotAllowedException(String method) {
		super(method);
	}
}
