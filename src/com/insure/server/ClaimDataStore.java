package com.insure.server;

import javax.jws.WebService;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;


/*
The class ClaimDataStore defines the data store structure that holds the claims created and all the related methods
to it. The methods will allow the creation of a new claim (that will be saved in the data structure), getting
the claim, (by searching for it in the data structure, using the claim id), retrieve the claim (retrieves
the claim in the string format) and update claim (allowing to set a new description for the claim).
Since documents are directly related to claims, the methods to list, create, read, get, update, delete and
get number of documents are included in this class.
*/


//define a datastore
@WebService
public class ClaimDataStore {
	// Unique ID
	private static AtomicInteger uuid = new AtomicInteger(1);
	// HashMap to store the data from claims: {key: unique ID}, {value: description}
	private ConcurrentHashMap<Integer, Claim> claimDataStore = new ConcurrentHashMap<Integer, Claim>();

	public ClaimDataStore () {
	}

	private void userIdVerification (int userId, Claim claim) throws WrongUserIdException {
		if (userId != claim.getUserId())
			throw new WrongUserIdException("This claim belongs to another user!");
	}

	//create/get/update claims
	public int createClaim (String description, int userId) {
		Claim claim = new Claim(this.uuid.getAndIncrement(), description, userId);
		this.claimDataStore.putIfAbsent(claim.getUuid(), claim);
		return claim.getUuid();
	}

	public Claim getClaim (int id) throws ClaimNotFoundException {
		if (!this.claimDataStore.containsKey(id)) {
			throw new ClaimNotFoundException("Claim " + id + " does not exist");
		}
		return this.claimDataStore.get(id);
	}

	public int getClaimUser (int claimId) throws ClaimNotFoundException {
		Claim claim = getClaim(claimId);
		return claim.getUserId();
	}

	public String retrieveClaim (int claimId, int userId) throws ClaimNotFoundException, WrongUserIdException {
		Claim claim = getClaim(claimId);

		userIdVerification(userId, claim);

		return claim.toString();
	}

	public void updateClaim (int userId, int claimId, String description) throws ClaimNotFoundException, WrongUserIdException {
		Claim claim = this.getClaim(claimId);

		userIdVerification(userId, claim);

		claim.setDescription(description);
	}

	public int getNumberOfDocs (int claimId) throws ClaimNotFoundException {
		Claim claim = this.getClaim(claimId);
		return claim.getNumOfDocs();

	}

	//list/create/read/update/delete documents of claims on the datastore safely.
	public void createDocument (int claimId, String docName, String docContent, int userId, int ownerId, String encryptedHash) throws ClaimNotFoundException, WrongUserIdException, TamperedDocumentException, Exception {
		Signature sign = new Signature();

		sign.validateSignature("publicKeys\\" + "user" + ownerId + "PublicKey", encryptedHash, docContent);

		Claim claim = this.getClaim(claimId);
		userIdVerification(userId, claim);
		claim.addDocument(ownerId, docName, docContent, encryptedHash);
	}

	public String readDocument (int userId, int claimId, int docId) throws ClaimNotFoundException, DocumentNotFoundException, WrongUserIdException {
		// we need to validate the signature to see if the content of document was not changed (integrity)
		Claim claim = this.getClaim(claimId);

		userIdVerification(userId, claim);

		Document document = claim.getDocument(docId);

		return document.toString();
	}

	public Document getDocument (int claimId, int docId) throws ClaimNotFoundException, DocumentNotFoundException {
		Claim claim = getClaim(claimId);
		Document document = claim.getDocument(docId);
		return document;
	}

	public int getDocumentOwner (int claimId, int docId) throws ClaimNotFoundException, DocumentNotFoundException {
		Claim claim = getClaim(claimId);
		Document document = claim.getDocument(docId);

		return document.getOwnerId();
	}

	public void updateDocument (int userId, int claimId, int docId, String newContent) throws ClaimNotFoundException, DocumentNotFoundException, WrongUserIdException {
		Claim claim = this.getClaim(claimId);
		userIdVerification(userId, claim);
		claim.updateDocument(docId, newContent);
	}

	public void deleteDocument (int userId, int claimId, int docId) throws ClaimNotFoundException, DocumentNotFoundException, WrongUserIdException {
		Claim claim = this.getClaim(claimId);
		userIdVerification(userId, claim);
		claim.deleteDocument(docId);
	}

	public String[] listDocuments (int userId, int claimId) throws ClaimNotFoundException, DocumentNotFoundException, WrongUserIdException {
		Claim claim = this.getClaim(claimId);

		String[] docsArray = (String[]) new String[claim.getNumOfDocs()];

		if (claim.getNumOfDocs() == 0)
			throw new DocumentNotFoundException("Claim " + claimId + " does not have associated documents.");

		int j = 1;
		int i = 0;
		while (i < claim.getAllDocuments().size()) {
			if (claim.getAllDocuments().containsKey(j)) {
				docsArray[i] = "DocId: " + claim.getDocument(j).getDocId() + ", Document Name: " + claim.getDocument(j).getName() + "\n";
				i++;
			}
			j++;
		}

		return docsArray;
	}

	// simulate an illegal document tampering, any user can simulate this
	public void simulateTampering (int claimId, int docId, String docContent) throws DocumentNotFoundException, ClaimNotFoundException, TamperedDocumentException, Exception {
		Document document = this.getDocument(claimId, docId);
		document.setContent(docContent);
	}
}




