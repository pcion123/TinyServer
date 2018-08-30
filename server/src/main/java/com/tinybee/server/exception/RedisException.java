package com.tinybee.server.exception;

public class RedisException extends Exception
{
	public RedisException(String msg)
	{
		super(msg);
	}

	public RedisException(String msg, Throwable e)
	{
		super(msg, e);
	}
}
