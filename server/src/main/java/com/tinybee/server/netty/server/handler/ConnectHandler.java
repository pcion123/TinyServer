package com.tinybee.server.netty.server.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tinybee.common.netty.server.ServerSocket;
import com.tinybee.server.netty.server.connection.Connection;

import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;

public class ConnectHandler extends ChannelDuplexHandler
{
	private static Logger mLogger = LoggerFactory.getLogger(ConnectHandler.class);
	
	private ServerSocket socket;
	
	public ConnectHandler(ServerSocket socket)
	{
		this.socket = socket;
	}
	
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception
    {
    	Channel incoming = ctx.channel();
    	if (socket != null)
    		socket.putConnection(Connection.class, incoming);
    	
    	mLogger.info("client: {} is online channelId={}", incoming.remoteAddress(), incoming.id());
    }
    
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception
    {
    	Channel incoming = ctx.channel();
    	if (socket != null)
    		socket.removeConnection(Connection.class, incoming);
    	
    	mLogger.info("client: {} is offline channelId={}", incoming.remoteAddress(), incoming.id());
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
    { 
    	Channel incoming = ctx.channel();
    	mLogger.info("client: {} has exception = {}", incoming.remoteAddress(), cause.getMessage());
        //cause.printStackTrace();
        ctx.close();
    }
}
