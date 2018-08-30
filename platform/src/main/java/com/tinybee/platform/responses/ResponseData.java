package com.tinybee.platform.responses;

import java.io.Serializable;

import com.tinybee.platform.enums.ResponseEnum;
import com.tinybee.platform.responses.base.BaseResponse;

public class ResponseData<T extends BaseResponse> implements Serializable
{
	private static final long serialVersionUID = 1L;

	private int code;
	private String message;
	private String token;
	private T response;

	public ResponseData()
	{

	}

	public ResponseData(int code, String message)
	{
		this.code = code;
		this.message = message;
	}

	public ResponseData(int code, String message, String token)
	{
		this.code = code;
		this.message = message;
		this.token = token;
	}

	public ResponseData(int code, String message, String token, T response)
	{
		this.code = code;
		this.message = message;
		this.token = token;
		this.response = response;
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

	public String getToken()
	{
		return token;
	}

	public void setToken(String token)
	{
		this.token = token;
	}

	public T getResponse()
	{
		return response;
	}

	public void setResponse(T response)
	{
		this.response = response;
	}
	
	public static ResponseData create(ResponseEnum responseEnum)
	{
		return new ResponseData(responseEnum.getCode(), responseEnum.getMessage(), null);
	}
	
	public static ResponseData create(ResponseEnum responseEnum, String token)
	{
		return new ResponseData(responseEnum.getCode(), responseEnum.getMessage(), token);
	}
	
	public static <T extends BaseResponse> ResponseData<T> create(ResponseEnum responseEnum, T response)
	{
		return new ResponseData(responseEnum.getCode(), responseEnum.getMessage(), null, response);
	}
	
	public static <T extends BaseResponse> ResponseData<T> create(ResponseEnum responseEnum, T response, String token)
	{
		return new ResponseData(responseEnum.getCode(), responseEnum.getMessage(), token, response);
	}
	
	public static ResponseData create(int code, String message)
	{
		return new ResponseData(code, message, null);
	}
	
	public static ResponseData create(int code, String message, String token)
	{
		return new ResponseData(code, message, token);
	}
	
	public static <T extends BaseResponse> ResponseData<T> create(int code, String message, T response)
	{
		return new ResponseData(code, message, null, response);
	}
	
	public static <T extends BaseResponse> ResponseData<T> create(int code, String message, T response, String token)
	{
		return new ResponseData(code, message, token, response);
	}
}
