package com.tinybee.server.exception;

public class SqlException extends Exception
{
	public SqlException(String msg)
	{
		super(msg);
	}

	public SqlException(String msg, Throwable e)
	{
		super(msg, e);
	}
}
