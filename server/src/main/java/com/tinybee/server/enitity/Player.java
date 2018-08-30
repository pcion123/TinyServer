package com.tinybee.server.enitity;

import com.tinybee.common.enitiy.User;

public class Player
{
	private User mUser;
	private long mSessionId;
	private String mToken;
	private long mTimeout;
	
	public static Player valueOf(User user)
	{
		Player player = null;
		if (user == null)
		{
			
		}
		else
		{
			player = new Player();
			player.mUser = user;
		}
		return player;
	}
	
	@Override
	public String toString()
	{
		return String.format("UserId=%d SessionId=%d Token=%s Timeout=%d", getUserId(), getSessionId(), getToken(), getTimeout());
	}
	
	public User getUser()
	{
		return mUser;
	}
	
	public int getUserId()
	{
		return mUser != null ? mUser.getUserId() : 0;
	}
	
	public String getFbid()
	{
		return mUser != null ? mUser.getFbid() : null;
	}
	
	public void setFbid(String fbid)
	{
		if (mUser != null)
			mUser.setFbid(fbid);
	}
	
	public String getNickName()
	{
		return mUser != null ? mUser.getNickName() : null;
	}
	
	public void setNickName(String nickName)
	{
		if (mUser != null)
			mUser.setNickName(nickName);
	}
	
	public long getSessionId()
	{
		return mSessionId;
	}
	
	public void setSessionId(long sessionId)
	{
		mSessionId = sessionId;
	}
	
	public String getToken()
	{
		return mToken;
	}
	
	public void setToken(String token)
	{
		mToken = token;
	}
	
	public long getTimeout()
	{
		return mTimeout;
	}
	
	public void setTimeout(long timeout)
	{
		mTimeout = timeout;
	}
}
