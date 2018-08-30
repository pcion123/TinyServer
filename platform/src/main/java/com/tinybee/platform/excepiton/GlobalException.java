package com.tinybee.platform.excepiton;

import com.tinybee.platform.enums.ResponseEnum;

public class GlobalException extends RuntimeException
{
	private int code;
	private String message;

	public GlobalException()
	{

	}

	public GlobalException(ResponseEnum responseEnum)
	{
		super(responseEnum.getMessage());
		
		this.code = responseEnum.getCode();
		this.message = responseEnum.getMessage();
	}

	public GlobalException(ResponseEnum responseEnum, Throwable cause)
	{
		super(responseEnum.getMessage(), cause);

		this.code = responseEnum.getCode();
		this.message = responseEnum.getMessage();
	}

	public GlobalException(int code, String message)
	{
		super(message);
		
		this.code = code;
		this.message = message;
	}

	public int getCode()
	{
		return code;
	}

	public void setCode(int code)
	{
		this.code = code;
	}

	public String getMessage()
	{
		return message;
	}

	public void setMessage(String message)
	{
		this.message = message;
	}
}
