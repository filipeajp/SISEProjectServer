package com.insure.server;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

/*
The class Signature allows the creation of a document signature. This class allows the
creation of a signature by creating a hash that will be encrypted and its validation (matching
the decrypted hash). The method createSignature receives a document content, for which generates a hash.
It also encrypts it by using the PrivateKey. This method is only used on the Client's side. This class uses
the classes AsymEncryptPriv and AsymDecryptPub to encrypt the hash related to the document content.
*/

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


