package com.tinybee.common.netty.server;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tinybee.common.enitiy.base.MachineBase;
import com.tinybee.common.netty.buffer.ByteArrayBuffer;
import com.tinybee.common.netty.header.HeaderBase;
import com.tinybee.common.netty.message.Message;
import com.tinybee.common.netty.server.connection.ConnectionBase;
import com.tinybee.common.system.ISystem;
import com.tinybee.common.util.DateUtil;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

public abstract class ServerSocket implements Runnable, ISystem
{
	private static Logger mLogger = LoggerFactory.getLogger(ServerSocket.class);
	
	protected Class<? extends ChannelInitializer<SocketChannel>> mInitializerClass;
	protected MachineBase mMachineHeader;
	protected Map mConnectionMap;
	protected int mMaxConnectLimit;
	protected AtomicInteger mMaxConnect = new AtomicInteger(0);
	protected AtomicInteger mNowConnect = new AtomicInteger(0);
	
	protected boolean mRunning = true;
	protected boolean mPause = false;
	
	protected ExecutorService mMainThread = Executors.newSingleThreadExecutor();
	protected ScheduledExecutorService mScheduledThread = Executors.newScheduledThreadPool(5);
	
	private Queue<Message> mMessageQueue = new LinkedBlockingQueue<>();
	private Consumer<Message>[][] mProcesses = new Consumer[128][128];
	
	private AtomicLong mSessionId = new AtomicLong(0);
	private int mId;
	private String mHostname;
	private int mPort;
	
	private EventLoopGroup mBossGroup;
	private EventLoopGroup mWorkerGroup;
	private ServerBootstrap mBootStrap;
	
	protected ServerSocket(int id, String hostname, int port, int maxConnectLimit)
	{
		this(id, hostname, port, maxConnectLimit, null, null);
	}
	
	protected ServerSocket(int id, String hostname, int port, int maxConnectLimit, MachineBase machineHeader)
	{
		this(id, hostname, port, maxConnectLimit, machineHeader, null);
	}
	
	protected ServerSocket(int id, String hostname, int port, int maxConnectLimit, Class<? extends ChannelInitializer<SocketChannel>> initializerClass)
	{
		this(id, hostname, port, maxConnectLimit, null, initializerClass);
	}
	
	protected ServerSocket(int id, String hostname, int port, int maxConnectLimit, MachineBase machineHeader, Class<? extends ChannelInitializer<SocketChannel>> initializerClass)
	{
		mMaxConnectLimit = maxConnectLimit;
		
		mId = id;
		mHostname = hostname;
		mPort = port;
		
		mMachineHeader = machineHeader;
		mInitializerClass = initializerClass;
	}
	
	public long genSessionId()
	{		
		if (mSessionId.longValue() >= Long.MAX_VALUE)
			mSessionId.set(0);
		
		return mSessionId.incrementAndGet();
	}
	
	public int getMaxConnectLimit()
	{
		return mMaxConnectLimit;
	}
	
	public int getMaxConnect()
	{
		return mMaxConnect != null ? mMaxConnect.intValue() : 0;
	}
	
	public int getNowConnect()
	{
		return mNowConnect != null ? mNowConnect.intValue() : 0;
	}
	
	public int getId()
	{
		return mId;
	}
	
	public String getHostname()
	{
		return mHostname;
	}
	
	public int getPort()
	{
		return mPort;
	}
	
	@Override
	public void run()
	{
        try
        {
        	ChannelInitializer<SocketChannel> handler = null;
        	if (mInitializerClass != null)
        	{
        		Constructor ctor = mInitializerClass.getDeclaredConstructor(ServerSocket.class);
        		ctor.setAccessible(true);
        		handler = mInitializerClass.cast(ctor.newInstance(this));
        	}
        	else
        	{
        		throw new Exception("initializer class can not be null");
        	}
    		mBossGroup = new NioEventLoopGroup();
    		mWorkerGroup = new NioEventLoopGroup();
    		mBootStrap = new ServerBootstrap();
    		mBootStrap.group(mBossGroup, mWorkerGroup)
    		.channel(NioServerSocketChannel.class)
    		.option(ChannelOption.SO_BACKLOG, 1024)
    		.childOption(ChannelOption.SO_KEEPALIVE, true)
    		.childHandler(handler);
            
        	mLogger.info("server is open");
        	
        	ChannelFuture f = mBootStrap.bind(mPort).sync();
			while (mRunning)
			{
				try
				{
					while (!mMessageQueue.isEmpty())
					{
						dispatcher(popMessage());
					}
				}
				catch (Exception e)
				{
					mLogger.error("protocol has error => {}", e.getMessage());
				}
			}
        	f.channel().closeFuture();
        }
        catch (Exception e)
        {
        	mLogger.error(e.getMessage());
        }
        finally
        {
        	mRunning = false;
        	mPause = false;
        	mWorkerGroup.shutdownGracefully();
            mBossGroup.shutdownGracefully();
            mScheduledThread.shutdown();
            mLogger.info("server is close");
        }
	}
	
	@Override
	public void work(Object[] message)
	{
		mMainThread.execute(this);
	}
	
	@Override
	public void shutdown(Object[] message)
	{
        synchronized (this)
        {
        	mRunning = false;
        }
        mMainThread.shutdown();
        mScheduledThread.shutdown();
	}
	
	@Override
	public void pause(Object[] message)
	{
        synchronized (this)
        {
        	mPause = true;
        }
	}
	
	@Override
	public void resume(Object[] message)
	{
        synchronized (this)
        {
        	mPause = false;
        }
	}
	
	@Override
	public void restart(Object[] message)
	{
		//TODO:什麼也不做
	}
	
	public <T> T getProperty(Class<T> clazz, Channel channel, String property)
	{
		AttributeKey<T> k = AttributeKey.valueOf(property);
		if (!channel.hasAttr(k))
		{
			mLogger.error("channel key is not exist => {}", channel.id());
			return null;
		}
		Attribute<T> v = channel.attr(k);
		return v.get();
	}
	
	public <T> void setProperty(Class<T> clazz, Channel channel, String property, T value)
	{
		AttributeKey<T> k = AttributeKey.valueOf(property);
		Attribute<T> v = channel.attr(k);
		v.set(value);
	}
	
	public <T extends ConnectionBase> T[] getConnections(Class<T> clazz)
	{
		T[] array = (T[])Array.newInstance(clazz, mConnectionMap.size());
		mConnectionMap.values().toArray(array);
		return array;
	}
	
	public <T extends ConnectionBase> T getConnection(Class<T> clazz, long sessionId)
	{
		if (!mConnectionMap.containsKey(sessionId))
			return null;
		
		return clazz.cast(mConnectionMap.get(sessionId));
	}
	
	public <T extends ConnectionBase> T getConnection(Class<T> clazz, Channel channel)
	{
		if (channel == null)
			return null;
		
		long sessionId = getProperty(long.class, channel, "sessionId");
		if (!mConnectionMap.containsKey(sessionId))
		{
			mLogger.error("connection key is not exist => {}", sessionId);
			return null;
		}
		return clazz.cast(mConnectionMap.get(sessionId));
	}
	
	public <T extends ConnectionBase> boolean putConnection(Class<T> clazz, Channel channel)
	{
		if (channel == null)
			return false;
		
		long sessionId = genSessionId();
		long connectTime = DateUtil.getCurrentTimestamp();
		T connection = null;
		try
		{
			connection = clazz.newInstance();
			connection.setChannel(channel);
			connection.setSessionId(sessionId);
			connection.setConnectTime(connectTime);
		}
		catch (InstantiationException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IllegalAccessException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (connection == null)
		{
			mLogger.error("connection can not create instance => {}", sessionId);
			return false;
		}
		if (mConnectionMap.containsKey(sessionId))
		{
			mLogger.error("connection key has already exist => {}", sessionId);
			return false;
		}
		mConnectionMap.put(sessionId, connection);
		mNowConnect.incrementAndGet();
		
		mLogger.info("add connection id={} sessionId={} address={}", channel.id(), sessionId, channel.remoteAddress());
		
		return true;
	}
	
	public <T extends ConnectionBase> T removeConnection(Class<T> clazz, Channel channel)
	{
		if (channel == null)
			return null;
		
		long sessionId = getProperty(long.class, channel, "sessionId");
		if (!mConnectionMap.containsKey(sessionId))
		{
			mLogger.error("connection key is not exist => {}", sessionId);
			return null;
		}
		
		T connection = clazz.cast(mConnectionMap.get(sessionId));
		mConnectionMap.remove(sessionId);
		mNowConnect.decrementAndGet();
		
		mLogger.info("remove connection id={} sessionId={} address={}", channel.id(), sessionId, channel.remoteAddress());
		
		return connection;
	}
	
	public void registerProtocol(int mainNo, int subNo, Consumer<Message> event)
	{
		registerProtocol((byte)mainNo, (byte)subNo, event);
	}
	
	private void registerProtocol(byte mainNo, byte subNo, Consumer<Message> event)
	{
		mProcesses[mainNo][subNo] = event;
	}
	
	public void unregisterProtocol(int mainNo, int subNo)
	{
		unregisterProtocol((byte)mainNo, (byte)subNo);
	}
	
	private void unregisterProtocol(byte mainNo, byte subNo)
	{
		mProcesses[mainNo][subNo] = null;
	}
	
	public void putMessage(Message message)
	{
		mMessageQueue.add(message);
	}
	
	protected Message popMessage()
	{
		return mMessageQueue.size() > 0 ? mMessageQueue.poll() : null;
	}
	
	public void dispatcher(Message message)
	{
		HeaderBase header = message.getHeader();
		byte mainNo = header.getMainNo();
		byte subNo = header.getSubNo();
		if (mProcesses[mainNo][subNo] != null)
		{
			mProcesses[mainNo][subNo].accept(message);
		}
		else
		{
			mLogger.info("process[{}][{}] is not create", mainNo, subNo);
		}
	}
	
	protected abstract void broadcast(byte mainNo, byte subNo, ByteArrayBuffer buffer);
	
	protected abstract void send(long sessionId, byte mainNo, byte subNo, ByteArrayBuffer buffer);
	
	protected abstract void send(Channel channel, byte mainNo, byte subNo, ByteArrayBuffer buffer);
}
