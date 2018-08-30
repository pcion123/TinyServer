package com.tinybee.platform.module.component;

import com.tinybee.common.enitiy.base.MachineBase;

public interface LoadBalanceManager
{
	<T extends MachineBase> T get(Class<T> clazz);
	<T extends MachineBase> T get(Class<T> clazz, int id);
	void set(MachineBase viewer);
	void setExpire(MachineBase viewer, long seconds);
	boolean exists(int id);
	long getExpire(int id);
	void resetExpire(MachineBase viewer, long seconds);
	void remove(int id);
}
