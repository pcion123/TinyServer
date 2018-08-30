package com.tinybee.platform.module.service.impl;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.tinybee.common.enitiy.Account;
import com.tinybee.common.enitiy.GameMachine;
import com.tinybee.common.enitiy.Session;
import com.tinybee.common.enitiy.User;
import com.tinybee.common.util.CommonUtil;
import com.tinybee.common.util.TokenUtil;
import com.tinybee.platform.enitiy.Server;
import com.tinybee.platform.enums.ResponseEnum;
import com.tinybee.platform.excepiton.AccountException;
import com.tinybee.platform.mapper.IosAccountMapper;
import com.tinybee.platform.module.component.LoadBalanceManager;
import com.tinybee.platform.module.component.RedisManager;
import com.tinybee.platform.module.component.UserManager;
import com.tinybee.platform.module.component.WebManager;
import com.tinybee.platform.module.service.AccountService;
import com.tinybee.platform.responses.ResponseData;
import com.tinybee.platform.responses.custom.LoginRes;

@Service
public class IosAccountServiceImpl implements AccountService
{
	private Logger logger = LoggerFactory.getLogger(IosAccountServiceImpl.class);
	
	@Autowired
	private UserManager userManager;
	@Autowired
	private WebManager webManager;
	@Autowired
	private RedisManager redisManager;
	@Autowired
	private LoadBalanceManager loadBalanceManager;
	@Autowired
	private IosAccountMapper mapper;
	@Autowired
	private PlatformTransactionManager transactionManager;
	
	@Override
	public Account getAccount(String acc) throws AccountException
	{
		Account account;
		try
		{
			account = redisManager.get(Account.class, String.format("account:ios:%s", acc));
			if (account != null)
				return account;
		}
		catch (Exception e)
		{
			throw new AccountException("get ios account from redis fail"); 
		}
		try
		{
			account = mapper.getAccountByAccount(acc);
			if (account != null)
			{
				redisManager.setExpire(String.format("account:ios:%s", acc), account, 30 * 60);
			}
		}
		catch (Exception e)
		{
			throw new AccountException("get ios account from sql fail"); 
		}
		return account;
	}

	@Override
	public ResponseData login(String acc, String pwd, boolean auto)
	{
		Account account;
		try
		{
			// 取得帳號
			account = getAccount(acc);
			// 檢查帳號是否存在
			if (account == null)
			{
				if (auto)
				{
					if (!register(acc, pwd))
					{
						return ResponseData.create(ResponseEnum.ACCOUNT_REGISTER_FAIL);
					}
					account = getAccount(acc);
					if (account == null)
					{
						return ResponseData.create(ResponseEnum.ACCOUNT_REGISTER_FAIL);
					}
				}
				else
				{
					return ResponseData.create(ResponseEnum.ACCOUNT_NOT_EXIST);
				}
			}
		}
		catch (AccountException e)
		{
			return ResponseData.create(ResponseEnum.ACCOUNT_NOT_EXIST);
		}
		// 取得密碼
		String token = account.getToken();
		// 檢查密碼是否正確
		if (!pwd.equals(token))
		{
			return ResponseData.create(ResponseEnum.ACCOUNT_PASSWORD_ERROR);
		}
		User user = null;
		// 取得userId
		int userId = account.getUserId();
		// 檢查是否需要建立user
		if (userId == 0)
		{
			// 生成新user
			user = userManager.genUser();
			// 檢查建立user
			if (user == null)
			{
				return ResponseData.create(ResponseEnum.USER_CREATE_FAIL);
			}
			// 取得帳號編號
			int id = account.getId();
			// 綁定帳號
			if (!attachUserId(id, userId))
			{
				return ResponseData.create(ResponseEnum.ACCOUNT_ATTACH_FAIL);
			}
		}
		else
		{
			// 取得user資料
			user = userManager.getUser(userId);
			// 檢查user資料
			if (user == null)
			{
				return ResponseData.create(ResponseEnum.USER_NOT_EXIST);
			}
		}
		
		Session session = redisManager.get(Session.class, String.format("token:%d", userId));
		GameMachine server;
		String accessToken;
		if (session == null)
		{
			server = loadBalanceManager.get(GameMachine.class);
			if (server == null)
			{
				return ResponseData.create(ResponseEnum.LOGIN_SERVER_NOT_EXIST);
			}
			HttpServletRequest request = webManager.getRequest();
			String clientIp = CommonUtil.getIpAddr(request);
			String serverIp = server.getMachineIp();
			int serverId = server.getMachineId();
			long updateTime = System.currentTimeMillis();
			accessToken = TokenUtil.generateToken(userId);
			session = Session.valueOf(userId, accessToken, updateTime, clientIp, serverIp, serverId);
			if (session == null)
			{
				return ResponseData.create(ResponseEnum.LOGIN_FAIL);
			}
			redisManager.setExpire(session.getKey(), session, 15 * 60);
		}
		else
		{
			server = loadBalanceManager.get(GameMachine.class, session.getServerId());
			if (server == null)
			{
				server = loadBalanceManager.get(GameMachine.class);
				if (server == null)
				{
					return ResponseData.create(ResponseEnum.LOGIN_SERVER_NOT_EXIST);
				}
			}
			accessToken = TokenUtil.generateToken(userId);
			session.setServerId(server.getMachineId());
			session.setServerIp(server.getMachineIp());
			session.setToken(accessToken);
			redisManager.setExpire(session.getKey(), session, 15 * 60);
		}
		return ResponseData.create(ResponseEnum.SUCCESS, LoginRes.valueOf(Server.valueOf(server)), accessToken);
	}
	
	@Override
	public ResponseData logout(int userId)
	{
		try
		{
			redisManager.remove(String.format("token:%d", userId));
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ResponseData.create(ResponseEnum.SUCCESS);
	}
	
	@Override
	public boolean register(String acc, String pwd)
	{
		DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setIsolationLevel(TransactionDefinition.ISOLATION_REPEATABLE_READ);
		def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
		TransactionStatus status = transactionManager.getTransaction(def);
		boolean success = false;
		try
		{
			Account account = mapper.getAccountByAccount(acc);
			if (acc != null)
			{
				throw new AccountException("ios account already exist");
			}
			account = Account.valueOf(acc, pwd);
			success = mapper.createAccount(account);
			if (!success)
			{
				throw new AccountException("ios account create fail");
			}
			transactionManager.commit(status);
		}
		catch (AccountException e)
		{
			transactionManager.rollback(status);
			success = false;
		}
		catch (Exception e)
		{
			transactionManager.rollback(status);
			success = false;
		}
		return success;
	}

	public boolean attachUserId(int id, int userId)
	{
		return mapper.attachUserId(id, userId);
	}
}
