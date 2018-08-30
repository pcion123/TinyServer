package com.tinybee.server.netty.server.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tinybee.common.netty.message.Message;
import com.tinybee.common.netty.server.ServerSocket;
import com.tinybee.server.App;
import com.tinybee.server.constant.SystemId;
import com.tinybee.server.enitity.Player;
import com.tinybee.server.exception.SocketException;
import com.tinybee.server.module.system.User;
import com.tinybee.server.netty.server.connection.Connection;
import com.tinybee.server.netty.server.header.Header;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class MessageHandler extends SimpleChannelInboundHandler<Message>
{
	private static Logger mLogger = LoggerFactory.getLogger(MessageHandler.class);
	
	private ServerSocket socket;
	
	public MessageHandler(ServerSocket socket)
	{
		this.socket = socket;
	}
    
    @Override
	protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception
    {
		if (socket != null)
		{
			Connection connection = socket.getConnection(Connection.class, ctx.channel());
			if (connection != null)
			{
				// 更新時間
				connection.setTimeout(System.currentTimeMillis() + 15 * 60 * 1000);
				// 設定檔頭
				Header header = msg.getHeader(Header.class);
				if (header.getSessionId() > 0 && header.getSessionId() != connection.getSessionId())
				{
					User userSystem = App.getSubSystem(User.class, SystemId.SYSTEM_USER);
					if (userSystem == null)
					{
						throw new SocketException("user system is not ready");
					}
					Player player = userSystem.getPlayer(header.getSessionId());
					if (player == null)
					{
						throw new SocketException(String.format("player is not exsit -> sessionId=%d", header.getSessionId()));
					}
					int userId = player.getUserId();
					String token = player.getToken();
					if (userId != header.getUserId() || !token.equals(header.getToken()))
					{
						throw new SocketException(String.format("header is can not verification -> token=%s userId=%d", header.getToken(), header.getUserId()));
					}
					connection.setToken(token);
					connection.setUserId(userId);
					connection.setPlayer(player);
					
					userSystem.replacePlayer(header.getSessionId(), connection.getSessionId(), player);
				}
				header.setSessionId(connection.getSessionId());
				header.setUserId(connection.getUserId());
				// 放入協定佇列
				socket.putMessage(msg);
			}
		}
	}
}
