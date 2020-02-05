package com.insure.server;

import javax.jws.WebService;
import javax.swing.*;
import java.lang.annotation.Documented;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

//define a datastore
@WebService
public class ClaimDataStore {
	// Unique ID
	private static AtomicInteger uuid = new AtomicInteger(1);
	// HashMap to store the data from claims: {key: unique ID}, {value: description}
	private ConcurrentHashMap<Integer, Claim> claimDataStore = new ConcurrentHashMap<Integer, Claim>();

	public ClaimDataStore () {
	}

	//create/get/update claims
	public int createClaim (String description, int userId) {
		Claim claim = new Claim(this.uuid.getAndIncrement(), description, userId);
		this.claimDataStore.putIfAbsent(claim.getUuid(), claim);
		return claim.getUuid();
	}

	public Claim getClaim (int id) throws ClaimNotFoundException {
		if (!this.claimDataStore.containsKey(id)) {
			throw new ClaimNotFoundException("There is no such claim with the id: " + id);
	}
		return this.claimDataStore.get(id);
	}

	public int getClaimUser(int claimId) throws ClaimNotFoundException {
		Claim claim = getClaim(claimId);
		return claim.getUserId();
	}

	public String retrieveClaim(int claimId) throws ClaimNotFoundException {
		Claim claim = getClaim(claimId);
		return claim.toString();
	}

	// retrieve a printed claim and his associated documents

	public void updateClaim (int claimId, String description) throws ClaimNotFoundException {
		Claim claim = this.getClaim(claimId);
		claim.setDescription(description);
	}

	public int getNumberOfDocs(int claimId) throws ClaimNotFoundException {
		Claim claim = this.getClaim(claimId);
		return claim.getNumOfDocs();

	}

	//list/create/read/update/delete documents of claims on the datastore safely.
	public String[] listDocuments (int claimId) throws ClaimNotFoundException, DocumentNotFoundException {
		Claim claim = this.getClaim(claimId);
		HashMap<Integer, Document> docRepository = claim.getAllDocuments();

		String[] docsArray = (String[]) new String[docRepository.size()];

		for (int i = 0; i < docRepository.size(); i++) {
			docsArray[i] = this.readDocument(claimId, i + 1);
		}
		return docsArray;
	}

	public boolean createDocument (int claimId, String docName, String docContent, String userId, String encryptedHash) throws Exception {
		Signature sign = new Signature();
		boolean validation = sign.validateSignature("publicKeys\\" + "user" + userId + "PublicKey", encryptedHash, docContent);

		if(validation){
			Claim claim = this.getClaim(claimId);
			int docId = claim.addDocument(docName, docContent, encryptedHash);
		}

		return validation;
	}

	public String readDocument (int claimId, int docId) throws ClaimNotFoundException, DocumentNotFoundException {
		// we need to validate the signature to see if the content of document was not changed (integrity)
		Claim claim = this.getClaim(claimId);
		Document document = claim.getDocument(docId);

		return document.toString();
	}

	public Document getDocument (int claimId, int docId) throws ClaimNotFoundException, DocumentNotFoundException {
		Claim claim = getClaim(claimId);
		Document document = claim.getDocument(docId);
		return document;
	}

	public void updateDocument (String userId, int claimId, int docId, String newContent) throws ClaimNotFoundException {
		Claim claim = this.getClaim(claimId);
		claim.updateDocument(docId, newContent);
	}

	public void deleteDocument (String userId, int claimId, int docId) throws ClaimNotFoundException {
		Claim claim = this.getClaim(claimId);
		claim.deleteDocument(docId);
	}

}




