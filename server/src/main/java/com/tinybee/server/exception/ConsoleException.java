package com.tinybee.server.exception;

public class ConsoleException extends Exception
{
	public ConsoleException(String msg)
	{
		super(msg);
	}

	public ConsoleException(String msg, Throwable e)
	{
		super(msg, e);
	}
}
