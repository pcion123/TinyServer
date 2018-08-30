package com.tinybee.server.module.system;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.tinybee.common.system.SubSystem;
import com.tinybee.common.util.JsonUtil;
import com.tinybee.server.constant.EventId;
import com.tinybee.server.util.BeanHelper;

public class Redis extends SubSystem
{
	private static Logger mLogger = LoggerFactory.getLogger(Redis.class);
	
	private static Redis mInstance;
	
	private StringRedisTemplate mSessionRedisTemplate;
	
	public Redis() throws Exception
	{
		super("redis");
		
		mSessionRedisTemplate = (StringRedisTemplate)BeanHelper.getBean("sessionRedisTemplate");
	}
	
	public static Redis getInstance() throws Exception
	{
		if (mInstance == null)
		{
			synchronized (Redis.class)
			{
				if (mInstance == null)
				{
					mInstance = new Redis();
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
	public void shutdown(Object[] message)
	{
		removeSystem("redis");
	}
	
	public <T> T get(final Class<T> clazz, final String key)
	{
		String value = mSessionRedisTemplate.boundValueOps(key).get();
		if (value == null)
		{
			return null;
		}
		return JsonUtil.toObject(value, clazz);
	}
	
	public void set(final String key, final Object value)
	{
		mSessionRedisTemplate.boundValueOps(key).set(JsonUtil.obj2JsonStr(value));
	}
	
	public void setExpire(final String key, final Object value, final long seconds)
	{
		mSessionRedisTemplate.boundValueOps(key).set(JsonUtil.obj2JsonStr(value), seconds, TimeUnit.SECONDS);
	}
	
	public boolean exists(final String key)
	{
		return mSessionRedisTemplate.hasKey(key);
	}
	
	public long getExpire(final String key)
	{
		return mSessionRedisTemplate.getExpire(key);
	}
	
	public void resetExpire(final String key, final long seconds)
	{
		mSessionRedisTemplate.boundValueOps(key).expire(seconds, TimeUnit.SECONDS);
	}
	
	public void remove(final String key)
	{
		mSessionRedisTemplate.delete(key);
	}
}
