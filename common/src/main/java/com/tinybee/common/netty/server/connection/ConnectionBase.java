package com.tinybee.common.netty.server.connection;

import com.tinybee.common.netty.buffer.ByteArrayBuffer;
import com.tinybee.common.netty.message.Message;
import com.tinybee.common.util.DateUtil;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelId;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

public abstract class ConnectionBase
{
	protected Channel mChannel;
	protected long mSessionId;
	protected long mConnectTime;
	
	protected ConnectionBase()
	{
		this(null, 0L, 0L);
	}
	
	protected ConnectionBase(Channel channel, long sessionId, long connectTime)
	{
		mChannel = channel;
		mSessionId = sessionId;
		mConnectTime = connectTime;
	}
	
	@Override
	public String toString()
	{
		return String.format("Id=%s SessionId=%d Address=%s ConnectTime=%s", getId(), getSessionId(), getAddress(), getConnectTime());
	}
	
	protected <T> T getProperty(Class<T> clazz, Channel channel, String property)
	{
		AttributeKey<T> k = AttributeKey.valueOf(property);
		if (!channel.hasAttr(k))
		{
			return null;
		}
		Attribute<T> v = channel.attr(k);
		return v.get();
	}
	
	protected <T> void setProperty(Class<T> clazz, Channel channel, String property, T value)
	{
		AttributeKey<T> k = AttributeKey.valueOf(property);
		Attribute<T> v = channel.attr(k);
		v.set(value);
	}
	
	public Channel getChannel()
	{
		return mChannel;
	}
	
	public void setChannel(Channel channel)
	{
		mChannel = channel;
	}
	
	public long getSessionId()
	{
		return mSessionId;
	}
	
	public void setSessionId(long sessionId)
	{
		mSessionId = sessionId;
		
		if (mChannel != null)
		{
			setProperty(long.class, mChannel, "sessionId", sessionId);
		}
	}
	
	public String getConnectTime()
	{
		return DateUtil.getCurrentDateTime(mConnectTime);
	}
	
	public void setConnectTime(long connectTime)
	{
		mConnectTime = connectTime;
	}
	
	public ChannelId getId()
	{
		return mChannel != null ? mChannel.id() : null;
	}
	
	public String getAddress()
	{
		return mChannel != null ? mChannel.remoteAddress().toString() : "";
	}
	
	public void disconnect()
	{
		if (mChannel != null)
		{
			mChannel.close();
		}
	}
	
	protected abstract void send(byte mainNo, byte subNo, ByteArrayBuffer buffer);
}
