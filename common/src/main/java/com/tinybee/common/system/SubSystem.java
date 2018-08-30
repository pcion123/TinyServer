package com.tinybee.common.system;

public abstract class SubSystem extends AbstractSystem
{
	public SubSystem(String name) throws Exception
	{
		this(name, false);
	}
	
	public SubSystem(String name, boolean mainable) throws Exception
	{
		super(name);
		if (mainable)
		{
			if (main != null)
			{
				throw new Exception("has already exist main system -> " + name);
			}
			main = this;
		}
	}
	
	public void work(Object[] message)
	{
		
	}
	
	public void shutdown(Object[] message)
	{
		
	}
	
	public void pause(Object[] message)
	{
		
	}
	
	public void resume(Object[] message)
	{
		
	}
	
	public void restart(Object[] message)
	{
		
	}
}
