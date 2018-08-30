package com.tinybee.platform.excepiton;

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
