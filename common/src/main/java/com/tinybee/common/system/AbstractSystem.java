package com.tinybee.common.system;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractSystem
{
	private static Logger mLogger = LoggerFactory.getLogger(AbstractSystem.class);
	
	private static Map<String,AbstractSystem> systemMap = new HashMap<>();
	
	protected static AbstractSystem main;
	protected static int count;
	
	protected String mName;
	private Map<Integer,Consumer<Object[]>> mEvents = new HashMap<>();
	
	public AbstractSystem(String name) throws Exception
	{
		mName = name;
		
		addSystem(name, this);
	}
	
	public abstract void init();
	
	public String getName()
	{
		return mName;
	}
	
	public <T extends AbstractSystem> T getSystem(Class<T> clazz, String key)
	{
		if (!systemMap.containsKey(key))
			return null;
		
		return clazz.cast(systemMap.get(key));
	}
	
	public AbstractSystem getSystem(String key)
	{
		if (!systemMap.containsKey(key))
			return null;
		
		return systemMap.get(key);
	}
	
	public AbstractSystem addSystem(String key, AbstractSystem value) throws Exception
	{
		if (systemMap.containsKey(key))
		{
			throw new Exception("has already exist system -> " + key);
		}
		systemMap.put(key, value);
		
		synchronized (AbstractSystem.class)
		{
			count++;
		}
		
		mLogger.info("add system -> {}", key);
		
		return value;
	}
	
	public AbstractSystem removeSystem(String key)
	{
		if (!systemMap.containsKey(key))
			return null;
		
		mLogger.info("remove system -> {}", key);
		
		synchronized (AbstractSystem.class)
		{
			if (main == this)
				main = null;
			
			count--;
		}
		
		return systemMap.remove(key);
	}
	
	protected void registerEvent(int eventId, Consumer<Object[]> evnet) throws Exception
	{
		if (mEvents.containsKey(eventId))
		{
			throw new Exception("has already exist event -> " + eventId);
		}
		mEvents.put(eventId, evnet);
	}
	
	protected void unregisterEvent(int eventId)
	{
		if (!mEvents.containsKey(eventId))
			return;
		
		mEvents.remove(eventId);
	}
	
	private void execute(int eventId, Object[] message)
	{
		if (!mEvents.containsKey(eventId))
			return;
		
		Consumer<Object[]> event = mEvents.get(eventId);
		try
		{
			if (event != null)
				event.accept(message);
		}
		catch (Exception e)
		{
			mLogger.error(e.getMessage());
		}
	}
	
	protected void send(String name, int eventId, Object[] message)
	{
		if (!systemMap.containsKey(name))
			return;
		
		AbstractSystem system = systemMap.get(name);
		if (system != null)
			system.execute(eventId, message);
	}
	
	protected void broadcast(int eventId, Object[] message)
	{
		AbstractSystem[] systems = new AbstractSystem[systemMap.size()];
		systemMap.values().toArray(systems);
		for (AbstractSystem system : systems)
		{
			if (system != null)
				system.execute(eventId, message);
		}
	}
}
