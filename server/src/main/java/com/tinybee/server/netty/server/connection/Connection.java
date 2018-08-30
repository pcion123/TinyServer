package com.tinybee.server.netty.server.connection;

import com.tinybee.common.netty.buffer.ByteArrayBuffer;
import com.tinybee.common.netty.message.Message;
import com.tinybee.common.netty.server.connection.ConnectionBase;
import com.tinybee.server.enitity.Player;
import com.tinybee.server.netty.server.GameSocket;
import com.tinybee.server.netty.server.header.Header;

import io.netty.channel.Channel;

public class Connection extends ConnectionBase
{
	private String mToken;
	private int mUserId;
	private long mTimeout;
	private Player mPlayer;
	
	public Connection()
	{
		super();
	}
	
	public Connection(Channel channel, long sessionId, long connectTime)
	{
		super(channel, sessionId, connectTime);
	}
	
	@Override
	public String toString()
	{
		return String.format("Id=%s SessionId=%d Token=%s UserId=%d Address=%s ConnectTime=%s", getId(), getSessionId(), getToken(), getUserId(), getAddress(), getConnectTime());
	}
	
	@Override
	public void setSessionId(long sessionId)
	{
		super.setSessionId(sessionId);
		
		if (mPlayer != null)
		{
			mPlayer.setSessionId(sessionId);
		}
	}
	
	@Override
	public void setConnectTime(long connectTime)
	{
		super.setConnectTime(connectTime);
		
		mTimeout = connectTime + 15 * 60 * 1000;
	}
	
	public String getToken()
	{
		return mToken;
	}
	
	public void setToken(String token)
	{
		mToken = token;
		
		setProperty(String.class, mChannel, "token", token);
	}
	
	public int getUserId()
	{
		return mUserId;
	}
	
	public void setUserId(int userId)
	{
		mUserId = userId;
		
		setProperty(int.class, mChannel, "userId", userId);
	}
	
	public long getTimeout()
	{
		return mTimeout;
	}
	
	public void setTimeout(long timeout)
	{
		mTimeout = timeout;
		
		if (mPlayer != null)
			mPlayer.setTimeout(timeout);
	}
	
	public Player getPlayer()
	{
		return mPlayer;
	}
	
	public void setPlayer(Player player)
	{
		mPlayer = player;
		
		if (player != null)
		{
			player.setSessionId(mSessionId);
			player.setToken(mToken);
			player.setTimeout(System.currentTimeMillis() + 15 * 60 * 1000);
		}
		setProperty(Player.class, mChannel, "player", player);
	}
	
	public void send(int mainNo, int subNo, ByteArrayBuffer buffer)
	{
		send((byte)mainNo, (byte)subNo, buffer);
	}
	
	@Override
	protected void send(byte mainNo, byte subNo, ByteArrayBuffer buffer)
	{
		if (mChannel != null)
		{
			mChannel.writeAndFlush(pack(GameSocket.VERSION, mainNo, subNo, mSessionId, mToken, mUserId, buffer));
		}
	}
	
	private Message pack(short version, byte mainNo, byte subNo, long sessionId, String token, int userId, ByteArrayBuffer buffer)
	{
		return pack(version, mainNo, subNo, sessionId, token, (byte)0, userId, buffer);
	}
	
	private Message pack(short version, byte mainNo, byte subNo, long sessionId, String token, byte serialId, int userId, ByteArrayBuffer buffer)
	{
		Header header = new Header(version, mainNo, subNo, false, sessionId, token, serialId, userId, buffer.getAvailable());
		Message message = new Message(header, buffer);
		return message;
	}
}
