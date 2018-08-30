package com.tinybee.server.netty.server.initializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tinybee.common.netty.server.ServerSocket;
import com.tinybee.server.netty.server.handler.ConnectHandler;
import com.tinybee.server.netty.server.handler.MessageHandler;
import com.tinybee.server.netty.server.handler.decoder.HeaderDecoderHandler;
import com.tinybee.server.netty.server.handler.encoder.HeaderEncoderHandler;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

public class Initializer extends ChannelInitializer<SocketChannel>
{
	private static Logger mLogger = LoggerFactory.getLogger(Initializer.class);
	
	private ServerSocket socket;
	
	public Initializer(ServerSocket socket)
	{
		this.socket = socket;
	}
	
	@Override
    public void initChannel(SocketChannel channel) throws Exception
	{
		if (socket != null)
		{
			int maxConnectLimit = socket.getMaxConnectLimit();
			int nowConnect = socket.getNowConnect();
			if (nowConnect >= maxConnectLimit)
			{
				throw new Exception("over connected");
			}
			ChannelPipeline pipeline = channel.pipeline();
	        pipeline.addLast("encoder", new HeaderEncoderHandler());
	        pipeline.addLast("decoder", new HeaderDecoderHandler());
	        pipeline.addLast("message", new MessageHandler(socket));
	        pipeline.addLast("connect", new ConnectHandler(socket));
	        mLogger.info("client {} is connected", channel.remoteAddress());
		}
    }
}
