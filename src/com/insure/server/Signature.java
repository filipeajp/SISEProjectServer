package com.insure.server;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

public class Signature {
	public Signature () {
	}

	private String generateHash (Document document) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		return Base64.getEncoder().encodeToString(digest.digest(document.toString().getBytes("UTF-8")));
	}

	// create hash(message) and encrypt hash
	public String createSignature (String fileName, Document document) throws Exception {
		//create hash
		String hash = generateHash(document);

		// encrypt hash
		AsymEncryptPriv cipher = new AsymEncryptPriv();
		PrivateKey privKey = cipher.getPrivate(fileName);
		String encryptedHash = cipher.encryptText(hash, privKey);

		return encryptedHash;

	}

	//decrypt hash and compare hash obtained from the message
	// validate integrity and authenticity
	public boolean validateSignature (String fileName, String encryptedHash, Document document) throws Exception {
		// Decrypt encrypted hash
		AsymDecryptPub cipher = new AsymDecryptPub();
		PublicKey pubKey = cipher.getPublic(fileName);
		String decryptedHash = cipher.decryptText(encryptedHash, pubKey);

		// Create hash of document
		String docHash = generateHash(document);

		// Validate
		if (!docHash.equals(decryptedHash)) return false;

		return true;
	}


}


