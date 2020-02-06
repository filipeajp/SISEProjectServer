package com.insure.server;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/*
The class Claim will allow the creation of objects of this type. Claims are then created in the ClaimDataStore class
that have access to this class. This class includes methods that add documents to a data structure, where all documents
are listed.
*/


public class Claim {
	private final int uuid;
	private String description;
	private ConcurrentHashMap<Integer, Document> documents;
	private AtomicInteger docId = new AtomicInteger(1);
	private final int userId;

	public Claim (int id, String description, int userId) {
		this.userId = userId;
		this.uuid = id;
		this.description = description;
		this.documents = new ConcurrentHashMap<Integer, Document>();

	}

	public int getUuid () {
		return this.uuid;
	}

	public String getDescription () {
		return this.description;
	}

	public int getUserId () {
		return this.userId;
	}

	public synchronized void setDescription (String description) {
		this.description = description;
	}

	public String toString () {
		return "{UserID:" + this.userId + ", ClaimID:" + this.uuid + ", Description:" + this.description + "}\n";
	}

	public int addDocument (int ownerId, String name, String description, String signature) {
		Document document = new Document(ownerId, this.docId.get(), name, description, signature);
		this.documents.put(this.docId.getAndIncrement(), document);
		return document.getDocId();
	}

	public Document getDocument (int docId) throws DocumentNotFoundException {
		if (!this.documents.containsKey(docId))
			throw new DocumentNotFoundException("Document " + docId + " does not exist.");
		Document document = this.documents.get(docId);
		return document;
	}

	public ConcurrentHashMap<Integer, Document> getAllDocuments () {
		return this.documents;
	}

	public int getNumOfDocs () {
		return this.documents.size();
	}

	public void deleteDocument (int docId) throws DocumentNotFoundException {
		if (!this.documents.containsKey(docId))
			throw new DocumentNotFoundException("Document " + docId + " does not exist.");
		this.documents.remove(docId);
	}

	public void updateDocument (int docId, String newContent) throws DocumentNotFoundException {
		if (!this.documents.containsKey(docId))
			throw new DocumentNotFoundException("Document " + docId + " does not exist.");
		this.documents.get(docId).setContent(newContent);
	}
}
