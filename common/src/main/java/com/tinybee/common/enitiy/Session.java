package com.tinybee.common.enitiy;

import com.alibaba.fastjson.annotation.JSONField;

public class Session
{
	@JSONField(name="uid")
	private int userId;
	@JSONField(name="token")
	private String token;
	@JSONField(name="utime")
	private long updateTime;
	@JSONField(name="cip")
	private String clientIp;
	@JSONField(name="sip")
	private String serverIp;
	@JSONField(name="sId")
	private int serverId;

	public static Session valueOf(int userId, String token, long updateTime, String clientIp, String serverIp, int serverId)
	{
		Session session = new Session(); 
		session.userId = userId;
		session.token = token;
		session.updateTime = updateTime;
		session.clientIp = clientIp;
		session.serverIp = serverIp;
		session.serverId = serverId;
		return session;
	}
	
	public String getKey()
	{
		return String.format("token:%d", userId);
	}

	public int getUserId()
	{
		return userId;
	}

	public void setUserId(int userId)
	{
		this.userId = userId;
	}

	public String getToken()
	{
		return token;
	}

	public void setToken(String token)
	{
		this.token = token;
	}

	public long getUpdateTime()
	{
		return updateTime;
	}

	public void setUpdateTime(long updateTime)
	{
		this.updateTime = updateTime;
	}

	public String getClientIp()
	{
		return clientIp;
	}

	public void setClientIp(String clientIp)
	{
		this.clientIp = clientIp;
	}

	public String getServerIp()
	{
		return serverIp;
	}

	public void setServerIp(String serverIp)
	{
		this.serverIp = serverIp;
	}
	
	public int getServerId()
	{
		return serverId;
	}
	
	public void setServerId(int serverId)
	{
		this.serverId = serverId;
	}
}
