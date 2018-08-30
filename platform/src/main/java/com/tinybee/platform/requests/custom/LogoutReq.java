package com.tinybee.platform.requests.custom;

import com.tinybee.platform.requests.base.BaseRequest;

public class LogoutReq extends BaseRequest
{
	private static final long serialVersionUID = 1L;
	
	private int userId;
	
	public int getUserId()
	{
		return userId;
	}
	
	public void setUserId(int userId)
	{
		this.userId = userId;
	}
}
