package com.tinybee.platform.requests.base;

import java.io.Serializable;

public class BaseRequest implements Serializable
{
	private static final long serialVersionUID = 1L;

	private String token;
	private long requestId;
	private String deviceId;
	private String platform;
	private String version;
	
	public int getUserId()
	{
    	String[] tokenArray = token.split("\\:");
    	return Integer.valueOf(tokenArray[1]);
	}

	public String getToken()
	{
		return token;
	}

	public void setToken(String token)
	{
		this.token = token;
	}

	public long getRequestId()
	{
		return requestId;
	}

	public void setRequestId(long requestId)
	{
		this.requestId = requestId;
	}

	public String getDeviceId()
	{
		return deviceId;
	}

	public void setDeviceId(String deviceId)
	{
		this.deviceId = deviceId;
	}

	public String getPlatform()
	{
		return platform;
	}

	public void setPlatform(String platform)
	{
		this.platform = platform;
	}

	public String getVersion()
	{
		return version;
	}

	public void setVersion(String version)
	{
		this.version = version;
	}
}
