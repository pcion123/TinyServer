package com.tinybee.common.netty.message;

import com.tinybee.common.netty.buffer.ByteArrayBuffer;
import com.tinybee.common.netty.header.HeaderBase;

public class Message
{
	private HeaderBase header;
	private ByteArrayBuffer buffer;
	
	public Message(HeaderBase header, ByteArrayBuffer buffer)
	{
		this.header = header;
		this.buffer = buffer;
	}
	
	public Message(HeaderBase header, byte[] buffer)
	{
		this.header = header;
		this.buffer = new ByteArrayBuffer(buffer);
	}
	
	@Override
	public String toString()
	{
		return header.toString();
	}
	
	public <T extends HeaderBase> T getHeader(Class<T> clazz)
	{
		return clazz.cast(header);
	}
	
	public HeaderBase getHeader()
	{
		return header;
	}
	
	public void setHeader(HeaderBase header)
	{
		this.header = header;
	}
	
	public ByteArrayBuffer getBuffer()
	{
		return buffer;
	}
	
	public void setBuffer(ByteArrayBuffer buffer)
	{
		this.buffer = buffer;
	}
}
