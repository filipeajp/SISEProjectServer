package com.insure.server;

public class DocumentNotFoundException extends Exception {
	public DocumentNotFoundException () {
		super();
	}

	public DocumentNotFoundException (String message) {
		super(message);
	}
}
