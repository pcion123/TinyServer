package com.tinybee.platform.module.component.impl;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.tinybee.common.enitiy.WebMachine;
import com.tinybee.common.enitiy.base.MachineBase;
import com.tinybee.common.enums.MachineEnum;
import com.tinybee.common.util.CommonUtil;
import com.tinybee.common.util.JsonUtil;
import com.tinybee.platform.module.component.LoadBalanceManager;
import com.tinybee.platform.util.Config;

@Component
public class LoadBalanceManagerImpl implements LoadBalanceManager
{
	private static final String KEYFORMAT = "machine:%d";
	
	@Autowired
	private Config config;
	@Autowired
	private StringRedisTemplate sessionRedisTemplate;
	private WebMachine self;
	
	@Override
	public <T extends MachineBase> T get(final Class<T> clazz)
	{
		String value;
		for (int i = 1; i <= 2; i++)
		{
			value = sessionRedisTemplate.boundValueOps(String.format(KEYFORMAT, i)).get();
			if (value != null)
			{
				return JsonUtil.toObject(value, clazz);
			}
		}
		return null;
	}

	@Override
	public <T extends MachineBase> T get(final Class<T> clazz, final int id)
	{
		String key = String.format(KEYFORMAT, id);
		String value = sessionRedisTemplate.boundValueOps(key).get();
		if (value == null)
		{
			return null;
		}
		return JsonUtil.toObject(value, clazz);
	}

	@Override
	public void set(final MachineBase viewer)
	{
		sessionRedisTemplate.boundValueOps(viewer.getKey()).set(JsonUtil.obj2JsonStr(viewer));
	}

	@Override
	public void setExpire(final MachineBase viewer, final long seconds)
	{
		sessionRedisTemplate.boundValueOps(viewer.getKey()).set(JsonUtil.obj2JsonStr(viewer), seconds, TimeUnit.SECONDS);
	}
	
	@Override
	public boolean exists(final int id)
	{
		String key = String.format(KEYFORMAT, id);
		return sessionRedisTemplate.hasKey(key);
	}
	
	@Override
	public long getExpire(final int id)
	{
		String key = String.format(KEYFORMAT, id);
		return sessionRedisTemplate.getExpire(key);
	}
	
	@Override
	public void resetExpire(final MachineBase viewer, final long seconds)
	{
		sessionRedisTemplate.boundValueOps(viewer.getKey()).expire(seconds, TimeUnit.SECONDS);
	}
	
	@Override
	public void remove(final int id)
	{
		String key = String.format(KEYFORMAT, id);
		sessionRedisTemplate.delete(key);
	}
	
	@Scheduled(cron="0/60 * *  * * ? ")
	private void update()
	{
		if (self == null)
		{
			self = new WebMachine();
			self.setMachineType(MachineEnum.WEB.getCode());
			self.setMachineId(config.getId());
			self.setMachineIp(CommonUtil.getLocalIP());
		}
		self.setUpdateTime(System.currentTimeMillis());
		setExpire(self, 60);
	}
}
