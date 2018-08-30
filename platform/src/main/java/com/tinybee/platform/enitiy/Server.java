package com.tinybee.platform.enitiy;

import com.tinybee.common.enitiy.base.MachineBase;

public class Server
{
	private int id;
	private String ip;
	private int port;
	
	public static Server valueOf(MachineBase gs)
	{
		Server server = new Server();
		server.id = gs.getMachineId();
		server.ip = gs.getMachineIp();
		server.port = gs.getMachinePort();
		return server;
	}
	
	public int getId()
	{
		return id;
	}
	
	public void setId(int id)
	{
		this.id = id;
	}
	
	public String getIp()
	{
		return ip;
	}
	
	public void setIp(String ip)
	{
		this.ip = ip;
	}
	
	public int getPort()
	{
		return port;
	}
	
	public void setPort(int port)
	{
		this.port = port;
	}
}
