package com.tinybee.server.netty.server;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tinybee.common.enitiy.GameMachine;
import com.tinybee.common.enitiy.base.MachineBase;
import com.tinybee.common.enums.MachineEnum;
import com.tinybee.common.netty.buffer.ByteArrayBuffer;
import com.tinybee.common.netty.message.Message;
import com.tinybee.common.netty.server.ServerSocket;
import com.tinybee.server.constant.ProtocolId;
import com.tinybee.server.module.system.Redis;
import com.tinybee.server.netty.server.connection.Connection;
import com.tinybee.server.netty.server.header.Header;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

public class GameSocket extends ServerSocket
{
	private static Logger mLogger = LoggerFactory.getLogger(GameSocket.class);
	
	public final static short VERSION = 1;
	
	public GameSocket(int id, String hostname, int port, int maxConnectLimit, Class<? extends ChannelInitializer<SocketChannel>> initializerClass)
	{
		this(id, hostname, port, maxConnectLimit, null, initializerClass);
	}
	
	public GameSocket(int id, String hostname, int port, int maxConnectLimit, MachineBase machineHeader, Class<? extends ChannelInitializer<SocketChannel>> initializerClass)
	{
		super(id, hostname, port, maxConnectLimit, machineHeader, initializerClass);
		
		GameMachine machine = new GameMachine();
		machine.setMachineType(MachineEnum.GAME.getCode());
		machine.setMachineId(id);
		machine.setMachineIp(hostname);
		machine.setMachinePort(port);
		mMachineHeader = machine;
		
		mConnectionMap = new ConcurrentHashMap<Long,Connection>();
		
		registerProtocol(000, 000, message -> ping(message));
		
		registerProtocol(ProtocolId.PRO_USER, 000, message -> Protocol.rcv_002_000(message));
		registerProtocol(ProtocolId.PRO_USER, 001, message -> Protocol.rcv_002_001(message));
		registerProtocol(ProtocolId.PRO_USER, 002, message -> Protocol.rcv_002_002(message));
		registerProtocol(ProtocolId.PRO_USER, 003, message -> Protocol.rcv_002_003(message));
		registerProtocol(ProtocolId.PRO_USER, 004, message -> Protocol.rcv_002_004(message));
		registerProtocol(ProtocolId.PRO_USER, 005, message -> Protocol.rcv_002_005(message));
	}
	
	@Override
	public void work(Object[] message)
	{
		super.work(message);
		
		mScheduledThread.scheduleAtFixedRate(new UpdateMachine(), 30 * 1000, 60 * 1000, TimeUnit.MILLISECONDS);
		mScheduledThread.scheduleAtFixedRate(new UpdateConnection(), 0, 5 * 60 * 1000, TimeUnit.MILLISECONDS);
	}
	
	private void ping(Message message)
	{
		send(message.getHeader().getSessionId(), message.getHeader().getMainNo(), message.getHeader().getSubNo(), new ByteArrayBuffer());
	}
	
	public Connection[] getConnections()
	{
		return getConnections(Connection.class);
	}
	
	public Connection getConnection(long sessionId)
	{
		return getConnection(Connection.class, sessionId);
	}	
	
	public Connection getConnection(Channel channel)
	{
		return getConnection(Connection.class, channel);
	}
	
	public void broadcast(int mainNo, int subNo, ByteArrayBuffer buffer)
	{
		broadcast((byte)mainNo, (byte)subNo, buffer);
	}
	
	@Override
	protected void broadcast(byte mainNo, byte subNo, ByteArrayBuffer buffer)
	{
		Connection[] connections = getConnections();
		for (Connection connection : connections)
			connection.send(mainNo, subNo, buffer);
	}
	
	public void send(long sessionId, int mainNo, int subNo, ByteArrayBuffer buffer)
	{
		send(sessionId, (byte)mainNo, (byte)subNo, buffer);
	}
	
	@Override
	protected void send(long sessionId, byte mainNo, byte subNo, ByteArrayBuffer buffer)
	{
		Connection connection = getConnection(sessionId);
		if (connection != null)
		{
			Channel channel = connection.getChannel();
			String token = connection.getToken();
			int userId = connection.getUserId();
			if (channel != null)
			{
				channel.writeAndFlush(pack(mainNo, subNo, sessionId, token, userId, buffer));
			}
		}
	}
	
	public void send(Channel channel, int mainNo, int subNo, ByteArrayBuffer buffer)
	{
		send(channel, (byte)mainNo, (byte)subNo, buffer);
	}
	
	@Override
	protected void send(Channel channel, byte mainNo, byte subNo, ByteArrayBuffer buffer)
	{
		Connection connection = getConnection(channel);
		if (connection != null)
		{
			long sessionId = connection.getSessionId();
			String token = connection.getToken();
			int userId = connection.getUserId();
			if (channel != null)
			{
				channel.writeAndFlush(pack(mainNo, subNo, sessionId, token, userId, buffer));
			}
		}
	}
	
	private Message pack(byte mainNo, byte subNo, long sessionId, String token, int userId, ByteArrayBuffer buffer)
	{
		return pack(mainNo, subNo, sessionId, token, (byte)0, userId, buffer);
	}
	
	private Message pack(byte mainNo, byte subNo, long sessionId, String token, byte serialId, int userId, ByteArrayBuffer buffer)
	{
		Header header = new Header(VERSION, mainNo, subNo, false, sessionId, token, serialId, userId, buffer.getAvailable());
		Message message = new Message(header, buffer);
		return message;
	}
	
	private class UpdateMachine implements Runnable
	{
		@Override
		public void run()
		{
			try
			{
				if (mRunning && mMachineHeader != null)
				{
					GameMachine machine = (GameMachine)mMachineHeader;
					machine.setMaxUser(mMaxConnect.intValue());
					machine.setMaxUser(mNowConnect.intValue());
					machine.setUpdateTime(System.currentTimeMillis());
					Redis.getInstance().setExpire(machine.getKey(), machine, 30 * 60);
				}
			}
			catch (Exception e)
			{
				mLogger.error(e.getMessage());
			}
		}
	}
	
	private class UpdateConnection implements Runnable
	{
		@Override
		public void run()
		{
			try
			{
				if (mRunning)
				{
					Connection[] connections = getConnections();
					if (connections != null && connections.length > 0)
					{
						long now = System.currentTimeMillis();
						for (Connection connection : connections)
						{
							if (now > connection.getTimeout())
							{
								connection.disconnect();
							}
						}
					}
				}
			}
			catch (Exception e)
			{
				mLogger.error(e.getMessage());
			}
		}
	}
}
