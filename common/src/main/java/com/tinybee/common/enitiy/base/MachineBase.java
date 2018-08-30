package com.tinybee.common.enitiy.base;

import com.alibaba.fastjson.annotation.JSONField;

public class MachineBase
{
	@JSONField(name="type")
	protected int machineType;
	@JSONField(name="id")
	protected int machineId;
	@JSONField(name="ip")
	protected String machineIp;
	@JSONField(name="port")
	protected int machinePort;
	@JSONField(name="utime")
	protected long updateTime;
	
	public String getKey()
	{
		return String.format("machine:%d", machineId);
	}
	
	public int getMachineType()
	{
		return machineType;
	}

	public void setMachineType(int machineType)
	{
		this.machineType = machineType;
	}	

	public int getMachineId()
	{
		return machineId;
	}

	public void setMachineId(int machineId)
	{
		this.machineId = machineId;
	}
	
	public String getMachineIp()
	{
		return machineIp;
	}

	public void setMachineIp(String machineIp)
	{
		this.machineIp = machineIp;
	}
	
	public int getMachinePort()
	{
		return machinePort;
	}

	public void setMachinePort(int machinePort)
	{
		this.machinePort = machinePort;
	}

	public long getUpdateTime()
	{
		return updateTime;
	}

	public void setUpdateTime(long updateTime)
	{
		this.updateTime = updateTime;
	}
}
