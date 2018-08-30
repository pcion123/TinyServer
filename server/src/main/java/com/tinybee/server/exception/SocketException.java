package com.tinybee.server.exception;

public class SocketException extends Exception
{
	public SocketException(String msg)
	{
		super(msg);
	}

	public SocketException(String msg, Throwable e)
	{
		super(msg, e);
	}
}
