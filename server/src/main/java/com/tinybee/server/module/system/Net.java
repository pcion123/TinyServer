package com.tinybee.server.module.system;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tinybee.common.system.SubSystem;
import com.tinybee.server.constant.EventId;
import com.tinybee.server.constant.SystemId;
import com.tinybee.server.netty.server.GameSocket;
import com.tinybee.server.netty.server.connection.Connection;
import com.tinybee.server.netty.server.initializer.Initializer;
import com.tinybee.server.util.BeanHelper;
import com.tinybee.server.util.Config;

public class Net extends SubSystem
{
	private static Logger mLogger = LoggerFactory.getLogger(Net.class);
	
	private static Net mInstance;
	
	private Config mConfig = (Config)BeanHelper.getBean("config");
	private GameSocket mSocket;
	
	public Net() throws Exception
	{
		super(SystemId.SYSTEM_NET);
	}
	
	public static Net getInstance() throws Exception
	{
		if (mInstance == null)
		{
			synchronized (Net.class)
			{
				if (mInstance == null)
				{
					mInstance = new Net();
				}
			}
		}
		return mInstance;
	}
	
	public static void dispose()
	{
		mInstance.shutdown(null);
		mInstance = null;
	}
	
	@Override
	public void init()
	{
		try
		{
			registerEvent(EventId.EVENT_SYSTEM_SHUTDOWN, message -> shutdown(message));
			registerEvent(EventId.EVENT_SYSTEM_RESTART, message -> restart(message));
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void work(Object[] message)
	{
		try
		{
			if (mConfig == null)
			{
				throw new Exception("can not find config");
			}
			int id = mConfig.getId();
			String hostname = mConfig.getHostname();
			int port = mConfig.getPort();
			int maxConnect = mConfig.getMaxConnect();
			mSocket = new GameSocket(id, hostname, port, maxConnect, Initializer.class);
			mSocket.work(message);
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void shutdown(Object[] message)
	{
		if (mSocket != null)
			mSocket.shutdown(message);
		
		removeSystem(SystemId.SYSTEM_NET);
	}
	
	@Override
	public void restart(Object[] message)
	{
		if (mSocket != null)
			mSocket.shutdown(message);
		
		try
		{
			if (mConfig == null)
			{
				throw new Exception("can not find config");
			}
			int id = mConfig.getId();
			String hostname = mConfig.getHostname();
			int port = mConfig.getPort();
			int maxConnect = mConfig.getMaxConnect();
			mSocket = new GameSocket(id, hostname, port, maxConnect, Initializer.class);
			mSocket.work(message);
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Connection getConnection(long sessionId)
	{
		return mSocket != null ? mSocket.getConnection(sessionId) : null;
	}
	
	public void showStatus()
	{
		if (mSocket != null)
		{
			int id = mSocket.getId();
			String hostname = mSocket.getHostname();
			int port = mSocket.getPort();
			int maxConnectLimit = mSocket.getMaxConnectLimit();
			int maxConnect = mSocket.getMaxConnect();
			int nowConnect = mSocket.getNowConnect();
			System.out.println(String.format("id=%d %s %d   MaxConnectLimit=%d MaxConnect=%d NowConnect=%d", id, hostname, port, maxConnectLimit, maxConnect, nowConnect));
		}
		else
		{
			System.out.println("socket not be ready");
		}
	}
	
	public void showPeoples()
	{
		if (mSocket != null)
		{
			Connection[] connections = mSocket.getConnections();
			if (connections == null || connections.length == 0)
			{
				System.out.println("no people");
			}
			else
			{
				for (Connection connection : connections)
				{
					System.out.println(connection.toString());
				}
			}
		}
		else
		{
			System.out.println("socket not be ready");
		}
	}
}
