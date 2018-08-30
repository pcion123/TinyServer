package com.tinybee.server.netty.server.header;

import com.tinybee.common.netty.header.HeaderBase;

public class Header extends HeaderBase
{
	public final static int LENGTH = 58;
	
	private byte serialId;
	private String token; //36 -> 4 + 32
	private int userId;
	
	public Header(short version, byte mainNo, byte subNo, boolean isCompress, long sessionId, String token, byte serialId, int userId, int len)
	{
		super(version, mainNo, subNo, isCompress, sessionId, len);
		
		this.token = token;
		this.serialId = serialId;
		this.userId = userId;
	}
	
	@Override
	public String toString()
	{
		return String.format("version=%d mainNo=%d subNo=%d isCompress=%s sessionId=%d token=%s serialId=%d userId=%d len=%d", version, mainNo, subNo, isCompress, sessionId, token, serialId, userId, len);
	}
	
	public String getToken()
	{
		return token;
	}
	
	public void setToken(String token)
	{
		this.token = token;
	}
	
	public byte getSerialId()
	{
		return serialId;
	}
	
	public void setSerialId(byte serialId)
	{
		this.serialId = serialId;
	}
	
	public int getUserId()
	{
		return userId;
	}
	
	public void setUserId(int userId)
	{
		this.userId = userId;
	}
}
