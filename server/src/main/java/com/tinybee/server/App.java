package com.tinybee.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tinybee.common.system.AbstractSystem;
import com.tinybee.common.system.SubSystem;
import com.tinybee.server.constant.EventId;
import com.tinybee.server.constant.SystemId;
import com.tinybee.server.module.system.Account;
import com.tinybee.server.module.system.Console;
import com.tinybee.server.module.system.Net;
import com.tinybee.server.module.system.Redis;
import com.tinybee.server.module.system.User;
import com.tinybee.server.util.BeanHelper;
import com.tinybee.server.util.Config;

public class App extends SubSystem
{
	private static Logger mLogger = LoggerFactory.getLogger(App.class);
	
	private static App mInstance;
	
	private Config mConfig = (Config)BeanHelper.getBean("config");
	private boolean mRunning = true;
	
	public App() throws Exception
	{
		this(SystemId.SYSTEM_APP, true);
	}
	
	public App(String name, boolean mainable) throws Exception
	{
		super(name, mainable);
	}
	
	public static App getInstance() throws Exception
	{
		if (mInstance == null)
		{
			synchronized (App.class)
			{
				if (mInstance == null)
				{
					mInstance = new App();
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
			Console.getInstance().init();
			Redis.getInstance().init();
			Net.getInstance().init();
			Account.getInstance().init();
			User.getInstance().init();
		}
		catch (Exception e)
		{
			mRunning = false;
			mLogger.error(e.getMessage());
		}
	}
	
	@Override
	public void work(Object[] message)
	{
		mLogger.info("app start work");
		try
		{
			Console.getInstance().work(message);
			Redis.getInstance().work(message);
			Net.getInstance().work(message);
			Account.getInstance().work(message);
			User.getInstance().work(message);
			while (mRunning)
			{
				Thread.currentThread().sleep(1000);
			}
			finish();
		}
		catch (Exception e)
		{
			mLogger.error(e.getMessage());
		}
		mLogger.info("app stop work");
	}
	
	@Override
	public void shutdown(Object[] message)
	{
        synchronized (this)
        {
        	mRunning = false;
        }
		if (mInstance != null)
			mInstance.broadcast(EventId.EVENT_SYSTEM_SHUTDOWN, null);
		
		removeSystem(SystemId.SYSTEM_APP);
	}
	
	private void finish() throws InterruptedException
	{
		while (count > 0)
		{
			Thread.currentThread().sleep(100);
		}
	}
	
	public static int getId()
	{
		if (mInstance != null)
		{
			return mInstance.mConfig != null ? mInstance.mConfig.getId() : 0;
		}
		return 0;
	}
	
	public static <T extends AbstractSystem> T getSubSystem(Class<T> clazz, String key)
	{
		return mInstance != null ? mInstance.getSystem(clazz, key) : null;
	}
	
	public static AbstractSystem getSubSystem(String key)
	{
		return mInstance != null ? mInstance.getSystem(key) : null;
	}
	
	public static void sendMessage(String name, int eventId, Object[] message)
	{
		if (mInstance != null)
			mInstance.send(name, eventId, message);
	}
	
	public static void broadcastMessage(int eventId, Object[] message)
	{
		if (mInstance != null)
			mInstance.broadcast(eventId, message);
	}
	
	public static void main(String[] args)
	{
		if (args != null)
		{
			
		}
		try
		{
			App.getInstance().init();
			App.getInstance().work(null);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
