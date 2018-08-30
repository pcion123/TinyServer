package com.tinybee.platform.enums;

import java.util.HashMap;

public enum ResponseEnum
{
	NONE(0, "無"),
	SUCCESS(1, "成功"),
	FAIL(2, "失敗"),
	
	LOGIN_FAIL(51, "登入失敗"),
	LOGIN_SERVER_FULL(52, "登入人數超出上限"),
	LOGIN_SERVER_NOT_EXIST(53, "目前無服務器可供服務"),
	
	ACCOUNT_ERROR(101, "帳號資料異常"),
	ACCOUNT_NOT_EXIST(102, "帳號不存在"),
	ACCOUNT_REGISTER_FAIL(103, "註冊失敗"),
	ACCOUNT_PASSWORD_ERROR(104, "密碼錯誤"),
	ACCOUNT_ATTACH_FAIL(105, "綁定帳號失敗"),
	
	USER_ERROR(201, "用戶資料異常"),
	USER_NOT_EXIST(202, "用戶不存在"),
	USER_CREATE_FAIL(203, "創建用戶失敗"),
	
	PARAM_TOKEN_ERROR(901, "連接金鑰錯誤"),
	PARAM_REQUESTID_ERROR(902, "請求編號錯誤"),
	PARAM_DEVICEID_ERROR(903, "機器錯誤"),
	PARAM_PLATFORM_ERROR(904, "平台錯誤"),
	PARAM_VERSION_ERROR(905, "版本錯誤"),
	PARAM_ERROR(906, "參數錯誤"),
	WORK_ERROR(998, "業務錯誤"),
	SYSTEM_ERROR(999, "系統錯誤")
    ;

	private static final HashMap<Integer, ResponseEnum> codeMap;
	
	static
	{
		codeMap = new HashMap<>();
		for (ResponseEnum type : values())
		{
			codeMap.put(type.code, type);
		}
	}
	
	public static ResponseEnum getByCode(int code)
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
	
	ResponseEnum(int code, String message)
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
