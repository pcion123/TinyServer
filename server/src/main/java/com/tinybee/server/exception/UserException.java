package com.tinybee.server.exception;

public class UserException extends Exception
{
	public UserException(String msg)
	{
		super(msg);
	}

	public UserException(String msg, Throwable e)
	{
		super(msg, e);
	}
}
