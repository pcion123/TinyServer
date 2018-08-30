package com.tinybee.platform.requests.custom;

import com.tinybee.platform.requests.base.BaseRequest;

public class CreateUserReq extends BaseRequest
{
	private static final long serialVersionUID = 1L;
	
	private int avatarId;
	private String nickName;
	
	public int getAvatarId()
	{
		return avatarId;
	}
	
	public void setAvatarId(int avatarId)
	{
		this.avatarId = avatarId;
	}
	
	public String getNickName()
	{
		return nickName;
	}
	
	public void setNickName(String nickName)
	{
		this.nickName = nickName;
	}
}
