package com.insure.server;

import java.sql.Timestamp;

public class Document {
	private final int docId;
	private String name;
	private String content;
	private Timestamp timeStamp;


	// attributes: id, name, type description, timestamp
	// timestamp = new Timestamp(System.currentTimeMillis());
	public Document (int id, String name, String content) {
		this.docId = id;
		this.name = name;
		this.content = content;
		this.timeStamp = new Timestamp(System.currentTimeMillis());
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

	public Timestamp getTimeStamp () {
		return timeStamp;
	}

	public String toString () {
		return "{DocID:" + this.docId + ", Name:" + this.name + ", Content:" + this.content + ", Timestamp" + this.timeStamp + "}\n";
	}

	public void setContent (String newContent) {
		this.content = newContent;
	}


}
