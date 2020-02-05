package com.insure.server;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;
//Obrigado Andree
public class Claim {
	private final AtomicInteger uuid;
	private String description;
	private HashMap<Integer, Document> documents;
	private int docId = 1;
	private int userId;

	public Claim (int id, String description, int userId) {
		AtomicInteger aId = new AtomicInteger(id);
		this.userId = userId;
		this.uuid = aId;
		this.description = description;
		this.documents = new HashMap<Integer, Document>();

	}

	public int getUuid () {
		return this.uuid.get();
	}

	public String getDescription () {
		return this.description;
	}

	public int getUserId(){
		return this.userId;
	}

	public synchronized void setDescription (String description) {
		this.description = description;
	}

	public String toString () {
		return "{ID:" + this.uuid + ", Description:" + this.description + "}\n";
	}

	public int addDocument (String name, String description, String signature) {
		Document document = new Document(this.docId, name, description, signature);
		this.documents.put(this.docId++, document);
		return document.getDocId();
	}

	public Document getDocument (int docId) {
		Document document = this.documents.get(docId);
		return document;
	}

	public HashMap<Integer, Document> getAllDocuments(){
		return this.documents;
	}

	public int getNumOfDocs () {
		return this.documents.size();
	}

	public void deleteDocument (int docId) {
		this.documents.remove(docId);
	}

	public void updateDocument (int docId, String newContent) {
		this.documents.get(docId).setContent(newContent);
	}
}
