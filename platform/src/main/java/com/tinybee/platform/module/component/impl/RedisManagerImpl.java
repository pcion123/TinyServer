package com.tinybee.platform.module.component.impl;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import com.tinybee.common.util.JsonUtil;
import com.tinybee.platform.module.component.RedisManager;

@Component
public class RedisManagerImpl implements RedisManager
{
	@Autowired
	private StringRedisTemplate sessionRedisTemplate;
	
	public <T> T get(final Class<T> clazz, final String key)
	{
		String value = sessionRedisTemplate.boundValueOps(key).get();
		if (value == null)
		{
			return null;
		}
		return JsonUtil.toObject(value, clazz);
	}
	
	public void set(final String key, final Object value)
	{
		sessionRedisTemplate.boundValueOps(key).set(JsonUtil.obj2JsonStr(value));
	}
	
	public void setExpire(final String key, final Object value, final long seconds)
	{
		sessionRedisTemplate.boundValueOps(key).set(JsonUtil.obj2JsonStr(value), seconds, TimeUnit.SECONDS);
	}
	
	public boolean exists(final String key)
	{
		return sessionRedisTemplate.hasKey(key);
	}
	
	public long getExpire(final String key)
	{
		return sessionRedisTemplate.getExpire(key);
	}
	
	public void resetExpire(final String key, final long seconds)
	{
		sessionRedisTemplate.boundValueOps(key).expire(seconds, TimeUnit.SECONDS);
	}
	
	public void remove(final String key)
	{
		sessionRedisTemplate.delete(key);
	}
}
