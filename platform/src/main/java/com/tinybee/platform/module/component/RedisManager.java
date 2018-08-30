package com.tinybee.platform.module.component;

public interface RedisManager
{
	<T> T get(Class<T> clazz, String key);
	void set(String key, Object value);
	void setExpire(String key, Object value, long seconds);
	boolean exists(String key);
	long getExpire(String key);
	void resetExpire(String key, long seconds);
	void remove(String key);
}
