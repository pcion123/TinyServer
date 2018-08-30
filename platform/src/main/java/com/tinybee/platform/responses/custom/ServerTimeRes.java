package com.tinybee.platform.responses.custom;

import java.util.Date;

import com.tinybee.platform.responses.base.BaseResponse;

public class ServerTimeRes extends BaseResponse
{
	private static final long serialVersionUID = 1L;
	
	private Date time;
	
	public static ServerTimeRes valueOf()
	{
		ServerTimeRes serverTimeRes = new ServerTimeRes();
		serverTimeRes.time = new Date();
		return serverTimeRes;
	}
	
	public Date getTime()
	{
		return time;
	}
	
	public void setTime(Date time)
	{
		this.time = time;
	}
}
