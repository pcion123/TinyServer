package com.tinybee.platform.excepiton;

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
