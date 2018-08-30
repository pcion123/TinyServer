package com.tinybee.server.module.system;

import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tinybee.common.system.SubSystem;
import com.tinybee.server.App;
import com.tinybee.server.constant.EventId;
import com.tinybee.server.constant.SystemId;

public class Console extends SubSystem
{
	private static Logger mLogger = LoggerFactory.getLogger(Console.class);
	
	private static Console mInstance;
	
	private boolean mRunning = true;
	private ExecutorService mMainThread = Executors.newSingleThreadExecutor();
	
	public Console() throws Exception
	{
		super(SystemId.SYSTEM_CONSOLE);
	}
	
	public static Console getInstance() throws Exception
	{
		if (mInstance == null)
		{
			synchronized (Console.class)
			{
				if (mInstance == null)
				{
					mInstance = new Console();
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
		mMainThread.execute(new FIFO());
	}
	
	@Override
	public void shutdown(Object[] message)
	{
		mRunning = false;
		mMainThread.shutdown();
		
		removeSystem(SystemId.SYSTEM_CONSOLE);
	}
	
	private void parse(String command) throws Exception
	{
		if ("status".equals(command))
		{
			Net netSystem = getSystem(Net.class, SystemId.SYSTEM_NET);
			if (netSystem == null)
			{
				mLogger.info("net system not be ready");
			}
			else
			{
				netSystem.showStatus();
			}
		}
		else if ("peoples".equals(command))
		{
			Net netSystem = getSystem(Net.class, SystemId.SYSTEM_NET);
			if (netSystem == null)
			{
				mLogger.info("net system not be ready");
			}
			else
			{
				netSystem.showPeoples();
			}
		}
		else if ("players".equals(command))
		{
			User userSystem = getSystem(User.class, SystemId.SYSTEM_USER);
			if (userSystem == null)
			{
				mLogger.info("user system not be ready");
			}
			else
			{
				userSystem.showPlayers();
			}
		}
		else if ("shutdown".equals(command))
		{
			App.getInstance().shutdown(null);
		}
		else
		{
			mLogger.info("command error");
		}
	}
	
	private class FIFO implements Runnable
	{
		@Override
		public void run()
		{
			Scanner scanner = new Scanner(System.in);
			while (mRunning)
			{
				try
				{
					parse(scanner.nextLine());
				}
				catch (Exception e)
				{
					mLogger.error(e.getMessage());
					e.printStackTrace();
				}
			}
			scanner.close();
		}
	}
}
