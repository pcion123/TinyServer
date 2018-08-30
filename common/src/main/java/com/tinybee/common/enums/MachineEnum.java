package com.tinybee.common.enums;

import java.util.HashMap;

public enum MachineEnum
{
	NONE(0, "none"),
	WEB(1, "webserver"),
	GAME(2, "gameserver")
    ;

	private static final HashMap<Integer, MachineEnum> codeMap;
	
	static
	{
		codeMap = new HashMap<>();
		for (MachineEnum type : values())
		{
			codeMap.put(type.code, type);
		}
	}
	
	public static MachineEnum getByCode(int code)
	{
		if (!codeMap.containsKey(code))
		{
			return null;
		}
		else
		{
			return codeMap.get(code);
		}
	}

	private final int code;
	private final String message;
	
	MachineEnum(int code, String message)
	{
		this.code = code;
		this.message= message;
	}
	
	public int getCode()
	{
		return code;
	}
	
	public String getMessage()
	{
		return message;
	}
}
