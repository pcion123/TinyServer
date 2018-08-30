package com.tinybee.platform.requests.custom;

import com.tinybee.platform.requests.base.BaseRequest;

public class ModifyAvatarReq extends BaseRequest
{
	private static final long serialVersionUID = 1L;
	
	private int avatarId;
	
	public int getAvatarId()
	{
		return avatarId;
	}
	
	public void setAvatarId(int avatarId)
	{
		this.avatarId = avatarId;
	}
}
