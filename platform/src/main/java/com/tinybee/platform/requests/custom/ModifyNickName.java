package com.tinybee.platform.requests.custom;

import com.tinybee.platform.requests.base.BaseRequest;

public class ModifyNickName extends BaseRequest
{
	private static final long serialVersionUID = 1L;
	
	private String nickName;
	
	public String getNickName()
	{
		return nickName;
	}
	
	public void setNickName(String nickName)
	{
		this.nickName = nickName;
	}
}
