package com.tinybee.server.exception;

public class AccountException extends Exception
{
	public AccountException(String msg)
	{
		super(msg);
	}

	public AccountException(String msg, Throwable e)
	{
		super(msg, e);
	}
}
