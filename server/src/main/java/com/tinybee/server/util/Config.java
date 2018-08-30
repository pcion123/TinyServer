package com.tinybee.server.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Config
{
	@Value("${server.id}")
	private int id;
	@Value("${server.hostname}")
	private String hostname;
	@Value("${server.port}")
	private int port;
	@Value("${server.maxConnect}")
	private int maxConnect;
	@Value("${server.threadCount}")
	private int threadCount;

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}
	
	public String getHostname()
	{
		return hostname;
	}
	
	public void setHostname(String hostname)
	{
		this.hostname = hostname;
	}
	
	public int getPort()
	{
		return port;
	}
	
	public void setPort(int port)
	{
		this.port = port;
	}
	
	public int getMaxConnect()
	{
		return maxConnect;
	}
	
	public void setMaxConnect(int maxConnect)
	{
		this.maxConnect = maxConnect;
	}
	
	public int getThreadCount()
	{
		return threadCount;
	}
	
	public void setThreadCount(int threadCount)
	{
		this.threadCount = threadCount;
	}
}
