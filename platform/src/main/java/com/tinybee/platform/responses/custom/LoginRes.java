package com.tinybee.platform.responses.custom;

import com.tinybee.platform.enitiy.Server;
import com.tinybee.platform.responses.base.BaseResponse;

public class LoginRes extends BaseResponse
{
	private static final long serialVersionUID = 1L;
	
	private Server server;
	
	public static LoginRes valueOf(Server server)
	{
		LoginRes loginRes = new LoginRes();
		loginRes.server = server;
		return loginRes;
	}
	
	public Server getServer()
	{
		return server;
	}
	
	public void setServer(Server server)
	{
		this.server = server;
	}
}
