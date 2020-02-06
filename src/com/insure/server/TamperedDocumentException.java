package com.insure.server;

public class TamperedDocumentException extends Exception{
	public TamperedDocumentException(){
		super();
	}

	public TamperedDocumentException(String message){
		super(message);
	}
}
