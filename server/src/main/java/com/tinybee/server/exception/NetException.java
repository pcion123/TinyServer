package com.tinybee.server.exception;

public class NetException extends Exception
{
	public NetException(String msg)
	{
		super(msg);
	}

	public NetException(String msg, Throwable e)
	{
		super(msg, e);
	}
}
