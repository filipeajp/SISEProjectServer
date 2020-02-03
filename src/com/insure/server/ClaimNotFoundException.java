package com.insure.server;

public class ClaimNotFoundException extends Exception {
	public ClaimNotFoundException () {
		super();
	}

	public ClaimNotFoundException (String message) {
		super(message);
	}
}
