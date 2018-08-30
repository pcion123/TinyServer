package com.tinybee.server.module.system;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tinybee.common.netty.buffer.ByteArrayBuffer;
import com.tinybee.common.netty.message.Message;
import com.tinybee.common.system.SubSystem;
import com.tinybee.server.constant.EventId;
import com.tinybee.server.constant.SystemId;
import com.tinybee.server.enitity.Player;
import com.tinybee.server.exception.UserException;
import com.tinybee.server.mapper.UserMapper;
import com.tinybee.server.netty.server.connection.Connection;
import com.tinybee.server.netty.server.header.Header;
import com.tinybee.server.util.BeanHelper;

import io.netty.util.internal.StringUtil;

public class User extends SubSystem
{
	private static Logger mLogger = LoggerFactory.getLogger(User.class);
	
	private static User mInstance;
	
	private UserMapper mMapper;
	private Map<Long,Player> mUserMapBySessionId = new ConcurrentHashMap<>();
	private Map<Integer,Player> mUserMapById = new ConcurrentHashMap<>();
	private Map<String,Player> mUserMapByFbid = new ConcurrentHashMap<>();
	protected ScheduledExecutorService mScheduledThread = Executors.newSingleThreadScheduledExecutor();
	
	public User() throws Exception
	{
		super(SystemId.SYSTEM_USER);
		
		mMapper = (UserMapper)BeanHelper.getBean("userMapper");
	}
	
	public static User getInstance() throws Exception
	{
		if (mInstance == null)
		{
			synchronized (User.class)
			{
				if (mInstance == null)
				{
					mInstance = new User();
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
	public void work(Object[] message)
	{
		mScheduledThread.scheduleAtFixedRate(new CheckTimeout(), 5 * 60 * 1000, 5 * 60 * 1000, TimeUnit.MILLISECONDS);
	}
	
	@Override
	public void shutdown(Object[] message)
	{
		mScheduledThread.shutdown();
		
		removeSystem(SystemId.SYSTEM_USER);
	}
	
	public boolean playerExist(long sessionId)
	{
		return mUserMapBySessionId.containsKey(sessionId);
	}
	
	public boolean playerExist(int userId)
	{
		return mUserMapById.containsKey(userId);
	}
	
	public boolean playerExist(String fbid)
	{
		return mUserMapByFbid.containsKey(fbid);
	}
	
	public Player[] getPlayers()
	{
		Player[] array = new Player[mUserMapById.size()];
		return mUserMapById.values().toArray(array);
	}
	
	public Player getPlayer(long sessionId)
	{
		return mUserMapBySessionId.get(sessionId);
	}
	
	public Player getPlayer(int userId)
	{
		Player player = mUserMapById.get(userId);
		if (player != null)
			return player;
		return Player.valueOf(mMapper.getUserByUserId(userId));
	}
	
	public Player getPlayer(String fbid)
	{
		Player player = mUserMapByFbid.get(fbid);
		if (player != null)
			return player;
		return Player.valueOf(mMapper.getUserByFbid(fbid));
	}
	
	public void putPlayer(long sessionId, Player player) throws UserException
	{
		if (player == null)
			return;
		
		int userId = player.getUserId();
		String fbid = player.getFbid();
		if (!mUserMapBySessionId.containsKey(sessionId))
		{
			mUserMapBySessionId.put(sessionId, player);
		}
		if (!mUserMapById.containsKey(userId))
		{
			mUserMapById.put(userId, player);
		}
		if (!StringUtil.isNullOrEmpty(fbid))
		{
			if (!mUserMapByFbid.containsKey(fbid))
			{
				mUserMapByFbid.put(fbid, player);
			}
		}
	}
	
	public void removePlayer(Player player)
	{
		if (player == null)
			return;
		
		long sessionId = player.getSessionId();
		int userId = player.getUserId();
		String fbid = player.getFbid();
		if (mUserMapBySessionId.containsKey(sessionId))
		{
			mUserMapBySessionId.remove(sessionId);
		}
		if (mUserMapById.containsKey(userId))
		{
			mUserMapById.remove(userId);
		}
		if (!StringUtil.isNullOrEmpty(fbid))
		{
			if (mUserMapByFbid.containsKey(fbid))
			{
				mUserMapByFbid.remove(fbid);
			}
		}
	}
	
	public void replacePlayer(long oldId, long newId, Player player)
	{
		if (player == null)
			return;
		
		if (mUserMapBySessionId.containsKey(oldId))
		{
			mUserMapBySessionId.remove(oldId);
		}
		
		if (!mUserMapBySessionId.containsKey(newId))
		{
			mUserMapBySessionId.put(newId, player);
		}
	}
	
	public Player genPlayer()
	{
		com.tinybee.common.enitiy.User user = com.tinybee.common.enitiy.User.valueOf();
		boolean success = mMapper.genUser(user);
		int userId = user.getUserId();
		Player player = Player.valueOf(user);
		return (success && userId > 0) ? player : null;
	}
	
	public void createPlayer(Message message)
	{
		Net netSystem = getSystem(Net.class, SystemId.SYSTEM_NET);
		Redis redisSystem = getSystem(Redis.class, SystemId.SYSTEM_REDIS);
		
		if (netSystem == null || redisSystem == null)
			return;
		
		Header header = message.getHeader(Header.class);
		long sessionId = message.getHeader().getSessionId();
		Connection connection = netSystem.getConnection(sessionId);
		ByteArrayBuffer request = message.getBuffer();
		int userId = request.readInt();
		String nickname = request.readString();
		Player player = getPlayer(userId);
		if (player == null)
		{
			ByteArrayBuffer response = new ByteArrayBuffer();
			response.writeInt(1);
			connection.send(header.getMainNo(), header.getSubNo(), response);
			return;
		}
		if (!StringUtil.isNullOrEmpty(player.getNickName()))
		{
			ByteArrayBuffer response = new ByteArrayBuffer();
			response.writeInt(2);
			connection.send(header.getMainNo(), header.getSubNo(), response);
			return;
		}
		updateNickName(userId, nickname);
		ByteArrayBuffer response = new ByteArrayBuffer();
		response.writeInt(0);
		response.writeStruct(com.tinybee.common.enitiy.User.class, player.getUser());
		connection.send(header.getMainNo(), header.getSubNo(), response);
	}
	
	public boolean attachFbid(int userId, String fbid)
	{
		boolean success = mMapper.attachFbid(userId, fbid);
		if (success)
		{
			Player player = getPlayer(userId);
			if (player != null)
				player.setFbid(fbid);
			
			if (!mUserMapByFbid.containsKey(fbid))
				mUserMapByFbid.put(fbid, player);
		}
		return success;
	}
	
	public void updateVip(int userId, int value)
	{
		
	}
	
	public void updatePoint(int userId, int value1, int value2)
	{
		
	}
	
	public void updateNickName(int userId, String nickName)
	{
		Player player = getPlayer(userId);
		if (player != null)
		{
			player.setNickName(nickName);
		}
		mMapper.updateNickName(userId, nickName);
	}
	
	public void updateAboutme(int userId, String aboutme)
	{
		
	}
	
	public void updateAvatarId(int userId, int avatarId)
	{
		
	}
	
	public void updateLv(int userId, int value)
	{
		
	}
	
	public void updateMoney(int userId, int value)
	{
		
	}
	
	public void updateBluestone(int userId, int value)
	{
		
	}
	
	public void updateChess(int userId, int value)
	{
		
	}
	
	public void updateWorth(int userId, int value)
	{
		
	}
	
	public void updatePassenger(int userId, int value)
	{
		
	}
	
	public void updateFame(int userId, int value)
	{
		
	}
	
	public void updateMap(int userId, int mapId, int mapNode)
	{
		
	}
	
	public void showPlayers()
	{
		Player[] players = getPlayers();
		if (players == null || players.length == 0)
		{
			System.out.println("no player");
		}
		else
		{
			for (Player player : players)
			{
				System.out.println(player.toString());
			}
		}
	}
	
	private class CheckTimeout implements Runnable
	{
		@Override
		public void run()
		{
			try
			{
				Player[] players = getPlayers();
				if (players != null && players.length > 0)
				{
					long now = System.currentTimeMillis();
					for (Player player : players)
					{
						if (now > player.getTimeout())
						{
							removePlayer(player);
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
