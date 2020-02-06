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

	private String generateHash (String docContent) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		return Base64.getEncoder().encodeToString(digest.digest(docContent.getBytes("UTF-8")));
	}

	private String encryptHash (String fileName, String hash) throws Exception {
		AsymEncryptPriv cipher = new AsymEncryptPriv();
		PrivateKey privKey = cipher.getPrivate(fileName);
		return cipher.encryptText(hash, privKey);
	}

	private String decryptHash (String fileName, String encryptedHash) throws Exception {
		AsymDecryptPub cipher = new AsymDecryptPub();
		PublicKey pubKey = cipher.getPublic(fileName);
		return cipher.decryptText(encryptedHash, pubKey);
	}

	// create hash(message) and encrypt hash
	public String createSignature (String fileName, String docContent) throws Exception {
		//create hash
		String hash = generateHash(docContent);

		// encrypt hash
		String encryptedHash = encryptHash(fileName, hash);

		return encryptedHash;

	}

	//decrypt hash and compare hash obtained from the message
	// validate integrity and authenticity
	public void validateSignature (String fileName, String encryptedHash, String docContent) throws TamperedDocumentException, Exception {
		// Decrypt encrypted hash
		String decryptedHash = decryptHash(fileName, encryptedHash);

		// Create hash of document
		String docHash = generateHash(docContent);

		// Validate
		if (!docHash.equals(decryptedHash)) throw new TamperedDocumentException("This document was tampered!");
	}
}


