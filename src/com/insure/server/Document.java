package com.insure.server;

import java.sql.Timestamp;

public class Document {
	private final int docId;
	private final int ownerId;
	private String name;
	private String content;
	private String signature;
	private Timestamp timestamp = new Timestamp(System.currentTimeMillis());


	// attributes: id, name, type description, timestamp
	// timestamp = new Timestamp(System.currentTimeMillis());

	public Document (int ownerId, int id, String name, String content, String signature) {
		this.ownerId = ownerId;
		this.docId = id;
		this.timestamp = new Timestamp(System.currentTimeMillis());
		this.name = name;
		this.content = content;
		this.signature = signature;
	}

	public int getOwnerId () {
		return this.ownerId;
	}

	public int getDocId () {
		return this.docId;
	}

	public String getName () {
		return this.name;
	}

	public String getContent () {
		return this.content;
	}

	public String getSignature () {
		return this.signature;
	}

	public Timestamp getTimestamp () {
		return timestamp;
	}

	public String toString () {
		return "{DocID:" + this.docId + ", Name:" + this.name + ", Content:" + this.content + ", Timestamp" + this.timestamp + "}\n";
	}

	public void setContent (String newContent) {
		this.content = newContent;
	}

	public void setSignature (String newSignature) {
		this.signature = newSignature;
	}


}
