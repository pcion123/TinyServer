package com.tinybee.common.system;

public interface ISystem
{
	void work(Object[] message);
	void shutdown(Object[] message);
	void pause(Object[] message);
	void resume(Object[] message);
	void restart(Object[] message);
}
