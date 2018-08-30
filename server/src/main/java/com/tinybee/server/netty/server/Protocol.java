package com.tinybee.server.netty.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tinybee.common.netty.message.Message;
import com.tinybee.server.App;
import com.tinybee.server.constant.SystemId;
import com.tinybee.server.module.system.Account;
import com.tinybee.server.module.system.User;

public class Protocol
{
	private static Logger mLogger = LoggerFactory.getLogger(Protocol.class);
	
	public static void rcv_002_000(Message message)
	{
		Account accountSystem = App.getSubSystem(Account.class, SystemId.SYSTEM_ACCOUNT);
		if (accountSystem != null)
			accountSystem.loginByToken(message);
	}
	
	public static void rcv_002_001(Message message)
	{
		Account accountSystem = App.getSubSystem(Account.class, SystemId.SYSTEM_ACCOUNT);
		if (accountSystem != null)
			accountSystem.loginByAccount(message);
	}
	
	public static void rcv_002_002(Message message)
	{
		Account accountSystem = App.getSubSystem(Account.class, SystemId.SYSTEM_ACCOUNT);
		if (accountSystem != null)
			accountSystem.logout(message);
	}
	
	public static void rcv_002_003(Message message)
	{
		Account accountSystem = App.getSubSystem(Account.class, SystemId.SYSTEM_ACCOUNT);
		if (accountSystem != null)
			accountSystem.register(message);
	}
	
	public static void rcv_002_004(Message message)
	{
		Account accountSystem = App.getSubSystem(Account.class, SystemId.SYSTEM_ACCOUNT);
		if (accountSystem != null)
			accountSystem.attachUserId(message);
	}
	
	public static void rcv_002_005(Message message)
	{
		User userSystem = App.getSubSystem(User.class, SystemId.SYSTEM_USER);
		if (userSystem != null)
			userSystem.createPlayer(message);
	}
}
