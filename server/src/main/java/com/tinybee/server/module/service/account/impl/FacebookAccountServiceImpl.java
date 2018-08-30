package com.tinybee.server.module.service.account.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.tinybee.common.enitiy.Account;
import com.tinybee.server.App;
import com.tinybee.server.constant.SystemId;
import com.tinybee.server.enitity.Player;
import com.tinybee.server.exception.AccountException;
import com.tinybee.server.exception.RedisException;
import com.tinybee.server.exception.SqlException;
import com.tinybee.server.exception.UserException;
import com.tinybee.server.mapper.FacebookAccountMapper;
import com.tinybee.server.module.service.account.IAccountService;
import com.tinybee.server.module.system.Redis;
import com.tinybee.server.module.system.User;
import com.tinybee.server.util.BeanHelper;

import io.netty.util.internal.StringUtil;

public class FacebookAccountServiceImpl implements IAccountService
{
	private static Logger mLogger = LoggerFactory.getLogger(FacebookAccountServiceImpl.class);
	
	private static IAccountService mInstance;
	
	private PlatformTransactionManager mTransactionManager;
	private FacebookAccountMapper mMapper;
	
	public FacebookAccountServiceImpl()
	{
		mTransactionManager = (PlatformTransactionManager)BeanHelper.getBean("platformTransactionManager");
		mMapper = (FacebookAccountMapper)BeanHelper.getBean("facebookAccountMapper");
	}
	
	public static IAccountService getInstance()
	{
        if (mInstance == null)
        {
            synchronized (FacebookAccountServiceImpl.class)
            {
        		if (mInstance == null)
        			mInstance = new FacebookAccountServiceImpl();
            }
        }
		return mInstance;
	}
	
	@Override
	public Account getAccount(String acc) throws AccountException, RedisException, SqlException
	{
		Redis redisSystem = App.getSubSystem(Redis.class, SystemId.SYSTEM_REDIS);
		if (redisSystem == null)
		{
			throw new RedisException("redis system is not ready"); 
		}
		
		Account account;
		try
		{
			account = redisSystem.get(Account.class, String.format("account:facebook:%s", acc));
			if (account != null)
				return account;
		}
		catch (Exception e)
		{
			throw new AccountException("get facebook account from redis fail"); 
		}
		try
		{
			account = mMapper.getAccountByAccount(acc);
			if (account != null)
				redisSystem.setExpire(String.format("account:facebook:%s", acc), account, 30 * 60);
		}
		catch (Exception e)
		{
			throw new AccountException("get facebook account from sql fail"); 
		}
		return account;
	}
	
	@Override
	public Player login(String acc, String pwd, boolean auto) throws AccountException, UserException, RedisException, SqlException
	{
		Account account = getAccount(acc);
		if (account == null)
		{
			if (auto)
			{
				if (!register(acc, pwd))
				{
					throw new AccountException("register facebook account fail");
				}
				account = getAccount(acc);
				if (account == null)
				{
					throw new AccountException("register facebook account fail");
				}
			}
			else
			{
				throw new AccountException("register facebook account is not exist");
			}
		}
		String token = account.getToken();
		if (!pwd.equals(token))
		{
			throw new AccountException("facebook password is not correct");
		}
		Redis redisSystem = App.getSubSystem(Redis.class, SystemId.SYSTEM_REDIS);
		if (redisSystem == null)
		{
			throw new RedisException("redis system is not ready");
		}
		User userSystem = App.getSubSystem(User.class, SystemId.SYSTEM_USER);
		if (userSystem == null)
		{
			throw new UserException("user system is not ready");
		}
		Player player = null;
		int userId = account.getUserId();
		if (userId == 0)
		{
			player = userSystem.genPlayer();
			if (player == null)
			{
				throw new AccountException("facebook create user fail");
			}
			int id = account.getId();
			if (!attachUserId(id, userId))
			{
				throw new AccountException("facebook attach userId fail");
			}
		}
		else
		{
			player = userSystem.getPlayer(userId);
			if (player == null)
			{
				throw new AccountException("facebook can not get user");
			}
		}
		return player;
	}
	
	@Override
	public void logout(int userId) throws RedisException
	{
		Redis redisSystem = App.getSubSystem(Redis.class, SystemId.SYSTEM_REDIS);
		if (redisSystem == null)
		{
			throw new RedisException("redis system is not ready");
		}
		redisSystem.remove(String.format("token:%d", userId));
	}
	
	@Override
	public boolean register(String acc, String pwd)
	{
		DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setIsolationLevel(TransactionDefinition.ISOLATION_REPEATABLE_READ);
		def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
		TransactionStatus status = mTransactionManager.getTransaction(def);
		boolean success = false;
		try
		{
			Account account = mMapper.getAccountByAccount(acc);
			if (acc != null)
			{
				throw new AccountException("facebook account already exist");
			}
			account = Account.valueOf(acc, pwd);
			success = mMapper.createAccount(account);
			if (!success)
			{
				throw new AccountException("facebook account create fail");
			}
			mTransactionManager.commit(status);
		}
		catch (AccountException e)
		{
			mTransactionManager.rollback(status);
			success = false;
		}
		catch (Exception e)
		{
			mTransactionManager.rollback(status);
			success = false;
		}
		return success;
	}

	@Override
	public boolean attachUserId(int id, int userId)
	{
		return mMapper.attachUserId(id, userId);
	}
}
