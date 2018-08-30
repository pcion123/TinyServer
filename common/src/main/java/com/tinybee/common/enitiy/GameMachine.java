package com.tinybee.common.enitiy;

import com.alibaba.fastjson.annotation.JSONField;
import com.tinybee.common.enitiy.base.MachineBase;

public class GameMachine extends MachineBase
{
	@JSONField(name="muser")
	private int maxUser;
	@JSONField(name="cuser")
	private int currentUser;
	
	public int getMaxUser()
	{
		return maxUser;
	}
	
	public void setMaxUser(int maxUser)
	{
		this.maxUser = maxUser;
	}
	
	public int getCurrentUser()
	{
		return maxUser;
	}
	
	public void setCurrentUser(int currentUser)
	{
		this.currentUser = currentUser;
	}
}
