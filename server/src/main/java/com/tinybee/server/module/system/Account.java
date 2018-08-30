package com.tinybee.server.module.system;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tinybee.common.enitiy.Session;
import com.tinybee.common.netty.buffer.ByteArrayBuffer;
import com.tinybee.common.netty.message.Message;
import com.tinybee.common.system.SubSystem;
import com.tinybee.common.util.CommonUtil;
import com.tinybee.common.util.TokenUtil;
import com.tinybee.server.App;
import com.tinybee.server.constant.AccountType;
import com.tinybee.server.constant.EventId;
import com.tinybee.server.constant.SystemId;
import com.tinybee.server.enitity.Player;
import com.tinybee.server.exception.AccountException;
import com.tinybee.server.exception.RedisException;
import com.tinybee.server.exception.SqlException;
import com.tinybee.server.exception.UserException;
import com.tinybee.server.module.service.account.IAccountService;
import com.tinybee.server.module.service.account.impl.FacebookAccountServiceImpl;
import com.tinybee.server.module.service.account.impl.GoogleAccountServiceImpl;
import com.tinybee.server.module.service.account.impl.IosAccountServiceImpl;
import com.tinybee.server.module.service.account.impl.PlatformAccountServiceImpl;
import com.tinybee.server.netty.server.connection.Connection;
import com.tinybee.server.netty.server.header.Header;

import io.netty.util.internal.StringUtil;

public class Account extends SubSystem
{
	private static Logger mLogger = LoggerFactory.getLogger(Account.class);
	
	private static Account mInstance;
	
	public Account() throws Exception
	{
		super(SystemId.SYSTEM_ACCOUNT);
	}
	
	public static Account getInstance() throws Exception
	{
		if (mInstance == null)
		{
			synchronized (Account.class)
			{
				if (mInstance == null)
				{
					mInstance = new Account();
				}
			}
		}
		return mInstance;
	}
	
	public static void dispose()
	{
		mInstance.shutdown(null);
		mInstance = null;
	}
	
	@Override
	public void init()
	{
		try
		{
			registerEvent(EventId.EVENT_SYSTEM_SHUTDOWN, message -> shutdown(message));
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void shutdown(Object[] message)
	{
		removeSystem(SystemId.SYSTEM_ACCOUNT);
	}
	
	private IAccountService getService(int type)
	{
		switch (type)
		{
			case AccountType.ACC_PLATFORM: return PlatformAccountServiceImpl.getInstance();
			case AccountType.ACC_GOOGLE: return GoogleAccountServiceImpl.getInstance();
			case AccountType.ACC_IOS: return IosAccountServiceImpl.getInstance();
			case AccountType.ACC_FACEBOOK: return FacebookAccountServiceImpl.getInstance();
		}
		return null;
	}
	
	public void loginByToken(Message message)
	{
		Net netSystem = getSystem(Net.class, SystemId.SYSTEM_NET);
		Redis redisSystem = getSystem(Redis.class, SystemId.SYSTEM_REDIS);
		User userSystem = getSystem(User.class, SystemId.SYSTEM_USER);
		
		if (netSystem == null || redisSystem == null || userSystem == null)
			return;
		
		Header header = message.getHeader(Header.class);
		long sessionId = message.getHeader().getSessionId();
		Connection connection = netSystem.getConnection(sessionId);
		ByteArrayBuffer request = message.getBuffer();
		int userId = request.readInt();
		String token = request.readString();
		Session session = redisSystem.get(Session.class, String.format("token:%d", userId));
		if (session == null)
		{
			ByteArrayBuffer response = new ByteArrayBuffer();
			response.writeInt(1);
			connection.send(header.getMainNo(), header.getSubNo(), response);
			return;
		}
		if (!session.getToken().equals(String.format("token:%d:%s", userId, token)))
		{
			ByteArrayBuffer response = new ByteArrayBuffer();
			response.writeInt(2);
			connection.send(header.getMainNo(), header.getSubNo(), response);
			return;
		}
		Player player = userSystem.getPlayer(userId);
		if (player == null)
		{
			ByteArrayBuffer response = new ByteArrayBuffer();
			response.writeInt(3);
			connection.send(header.getMainNo(), header.getSubNo(), response);
			return;
		}
		try
		{
			userSystem.putPlayer(sessionId, player);
			connection.setToken(token);
			connection.setUserId(userId);
			connection.setPlayer(player);
			
			if (StringUtil.isNullOrEmpty(player.getNickName()))
			{
				ByteArrayBuffer response = new ByteArrayBuffer();
				response.writeInt(51);
				connection.send(header.getMainNo(), header.getSubNo(), response);
				return;
			}
		}
		catch (UserException e)
		{
			ByteArrayBuffer response = new ByteArrayBuffer();
			response.writeInt(4);
			connection.send(header.getMainNo(), header.getSubNo(), response);
			return;
		}
		ByteArrayBuffer response = new ByteArrayBuffer();
		response.writeInt(0);
		response.writeStruct(com.tinybee.common.enitiy.User.class, player.getUser());
		connection.send(header.getMainNo(), header.getSubNo(), response);
	}
	
	public void loginByAccount(Message message)
	{
		Net netSystem = getSystem(Net.class, SystemId.SYSTEM_NET);
		Redis redisSystem = getSystem(Redis.class, SystemId.SYSTEM_REDIS);
		User userSystem = getSystem(User.class, SystemId.SYSTEM_USER);
		
		if (netSystem == null || redisSystem == null || userSystem == null)
			return;
		
		Header header = message.getHeader(Header.class);
		long sessionId = message.getHeader().getSessionId();
		Connection connection = netSystem.getConnection(sessionId);
		ByteArrayBuffer request = message.getBuffer();
		int type = request.readInt();
		String account = request.readString();
		String password = request.readString();
		IAccountService service = getService(type);
		if (service == null)
		{
			ByteArrayBuffer response = new ByteArrayBuffer();
			response.writeInt(1);
			connection.send(header.getMainNo(), header.getSubNo(), response);
			return;
		}
		Player player = null;
		int userId;
		String accessToken;
		try
		{
			player = service.login(account, password, false);
			if (player == null)
			{
				ByteArrayBuffer response = new ByteArrayBuffer();
				response.writeInt(2);
				connection.send(header.getMainNo(), header.getSubNo(), response);
				return;
			}
			userSystem.putPlayer(sessionId, player);
			
			userId = player.getUserId();
			accessToken = TokenUtil.generateToken(userId);
			String clientIp = connection.getAddress();
			String serverIp = CommonUtil.getLocalIP();
			int serverId = App.getId();
			long updateTime = System.currentTimeMillis();
			Session session = Session.valueOf(userId, accessToken, updateTime, clientIp, serverIp, serverId);
			redisSystem.setExpire(session.getKey(), session, 15 * 60);
			
			connection.setToken(accessToken);
			connection.setUserId(userId);
			connection.setPlayer(player);
			
			if (StringUtil.isNullOrEmpty(player.getNickName()))
			{
				ByteArrayBuffer response = new ByteArrayBuffer();
				response.writeInt(51);
				response.writeInt(userId);
				response.writeString(accessToken);
				connection.send(header.getMainNo(), header.getSubNo(), response);
				return;
			}
		}
		catch (AccountException e)
		{
			ByteArrayBuffer response = new ByteArrayBuffer();
			response.writeInt(3);
			connection.send(header.getMainNo(), header.getSubNo(), response);
			return;
		}
		catch (UserException e)
		{
			ByteArrayBuffer response = new ByteArrayBuffer();
			response.writeInt(4);
			connection.send(header.getMainNo(), header.getSubNo(), response);
			return;
		}
		catch (RedisException e)
		{
			ByteArrayBuffer response = new ByteArrayBuffer();
			response.writeInt(5);
			connection.send(header.getMainNo(), header.getSubNo(), response);
			return;
		}
		catch (SqlException e)
		{
			ByteArrayBuffer response = new ByteArrayBuffer();
			response.writeInt(6);
			connection.send(header.getMainNo(), header.getSubNo(), response);
			return;
		}
		catch (Exception e)
		{
			ByteArrayBuffer response = new ByteArrayBuffer();
			response.writeInt(99);
			connection.send(header.getMainNo(), header.getSubNo(), response);
			return;
		}
		ByteArrayBuffer response = new ByteArrayBuffer();
		response.writeInt(0);
		response.writeInt(userId);
		response.writeString(accessToken);
		response.writeStruct(com.tinybee.common.enitiy.User.class, player.getUser());
		connection.send(header.getMainNo(), header.getSubNo(), response);
	}
	
	public void logout(Message message)
	{
		Net netSystem = getSystem(Net.class, SystemId.SYSTEM_NET);
		
		if (netSystem == null)
			return;
		
		Header header = message.getHeader(Header.class);
		long sessionId = message.getHeader().getSessionId();
		Connection connection = netSystem.getConnection(sessionId);
		ByteArrayBuffer request = message.getBuffer();
		int type = request.readInt();
		int userId = connection.getUserId();
		IAccountService service = getService(type);
		if (service == null)
		{
			ByteArrayBuffer response = new ByteArrayBuffer();
			response.writeInt(1);
			connection.send(header.getMainNo(), header.getSubNo(), response);
			return;
		}
		try
		{
			service.logout(userId);
		}
		catch (RedisException e)
		{
			mLogger.error(e.getMessage());
		}
		ByteArrayBuffer response = new ByteArrayBuffer();
		response.writeInt(0);
		connection.send(header.getMainNo(), header.getSubNo(), response);
	}
	
	public void register(Message message)
	{
		Net netSystem = getSystem(Net.class, SystemId.SYSTEM_NET);
		
		if (netSystem == null)
			return;
		
		Header header = message.getHeader(Header.class);
		long sessionId = message.getHeader().getSessionId();
		Connection connection = netSystem.getConnection(sessionId);
		ByteArrayBuffer request = message.getBuffer();
		int type = request.readInt();
		String acc = request.readString();
		String pwd = request.readString();
		IAccountService service = getService(type);
		if (service == null)
		{
			ByteArrayBuffer response = new ByteArrayBuffer();
			response.writeInt(1);
			connection.send(header.getMainNo(), header.getSubNo(), response);
			return;
		}
		if (!service.register(acc, pwd))
		{
			ByteArrayBuffer response = new ByteArrayBuffer();
			response.writeInt(2);
			connection.send(header.getMainNo(), header.getSubNo(), response);
			return;
		}
		ByteArrayBuffer response = new ByteArrayBuffer();
		response.writeInt(0);
		connection.send(header.getMainNo(), header.getSubNo(), response);
	}
	
	public void attachUserId(Message message)
	{
		Net netSystem = getSystem(Net.class, SystemId.SYSTEM_NET);
		
		if (netSystem == null)
			return;
		
		Header header = message.getHeader(Header.class);
		long sessionId = message.getHeader().getSessionId();
		Connection connection = netSystem.getConnection(sessionId);
		ByteArrayBuffer request = message.getBuffer();
		int type = request.readInt();
		String acc = request.readString();
		String pwd = request.readString();
		int userId = connection.getUserId();
		IAccountService service = getService(type);
		if (service == null)
		{
			ByteArrayBuffer response = new ByteArrayBuffer();
			response.writeInt(1);
			connection.send(header.getMainNo(), header.getSubNo(), response);
			return;
		}
		com.tinybee.common.enitiy.Account account = null;
		try
		{
			account = service.getAccount(acc);
		}
		catch (AccountException e)
		{
			ByteArrayBuffer response = new ByteArrayBuffer();
			response.writeInt(2);
			connection.send(header.getMainNo(), header.getSubNo(), response);
			return;
		}
		catch (RedisException e)
		{
			ByteArrayBuffer response = new ByteArrayBuffer();
			response.writeInt(3);
			connection.send(header.getMainNo(), header.getSubNo(), response);
			return;
		}
		catch (SqlException e)
		{
			ByteArrayBuffer response = new ByteArrayBuffer();
			response.writeInt(4);
			connection.send(header.getMainNo(), header.getSubNo(), response);
			return;
		}
		catch (Exception e)
		{
			ByteArrayBuffer response = new ByteArrayBuffer();
			response.writeInt(5);
			connection.send(header.getMainNo(), header.getSubNo(), response);
			return;
		}
		if (account == null)
		{
			ByteArrayBuffer response = new ByteArrayBuffer();
			response.writeInt(6);
			connection.send(header.getMainNo(), header.getSubNo(), response);
			return;
		}
		if (!pwd.equals(account.getToken()))
		{
			ByteArrayBuffer response = new ByteArrayBuffer();
			response.writeInt(7);
			connection.send(header.getMainNo(), header.getSubNo(), response);
			return;
		}
		if (account.getUserId() != 0)
		{
			ByteArrayBuffer response = new ByteArrayBuffer();
			response.writeInt(8);
			connection.send(header.getMainNo(), header.getSubNo(), response);
			return;
		}
		int id = account.getId();
		if (!service.attachUserId(id, userId))
		{
			ByteArrayBuffer response = new ByteArrayBuffer();
			response.writeInt(9);
			connection.send(header.getMainNo(), header.getSubNo(), response);
			return;
		}
		ByteArrayBuffer response = new ByteArrayBuffer();
		response.writeInt(0);
		connection.send(header.getMainNo(), header.getSubNo(), response);
	}
}
