package com.insure.server;

public class WrongUserIdException extends Exception{
	public WrongUserIdException(){
		super();
	}

	public WrongUserIdException(String message){
		super(message);
	}
}
