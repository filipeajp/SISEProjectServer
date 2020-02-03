package com.insure.server;

import javax.jws.WebService;
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
	public int createClaim (String description) {
		Claim claim = new Claim(this.uuid.getAndIncrement(), description);
		this.claimDataStore.putIfAbsent(claim.getUuid(), claim);
		return claim.getUuid();
	}

	public Claim getClaim (int id) throws ClaimNotFoundException {
		if (!this.claimDataStore.containsKey(id)) {
			throw new ClaimNotFoundException("There is no such claim with the id: " + id);
	}
		return this.claimDataStore.get(id);
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
	public String[] listDocuments (String userId, int claimId) throws ClaimNotFoundException {
		Claim claim = this.getClaim(claimId);
		HashMap<Integer, Document> docRepository = claim.getAllDocuments();
		/*List<Document> docs = new LinkedList<Document>( docRepository.values());
		List<String> docString = new LinkedList<String>();

		for (int i = 0; i <docs.size(); i++)
			docString.add(docs.get(i).toString());

		String[] output = (String[]) docString.toArray();

		return output;*/
		String[] docsArray = (String[]) new String[docRepository.size()];
		for (int i = 0; i < docRepository.size(); i++)
			docsArray[i] = this.readDocument(userId, claimId, i+1);
		return docsArray;
	}

	public void createDocument (int claimId, String docName, String content, String userId, String privKeyFileName) throws Exception {
		Claim claim = this.getClaim(claimId);
		int docId = claim.addDocument(docName, content);
		Document document = claim.getDocument(docId);
		//we need to create a digital signature
		//Signature sign = new Signature();
		//sign.createSignature(privKeyFileName, document);
	}


	public String readDocument (String userId, int claimId, int docId) throws ClaimNotFoundException {
		// we need to validate the signature to see if the content of document was not changed (integrity)
		Claim claim = this.getClaim(claimId);
		Document document = claim.getDocument(docId);
		return document.toString();
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




