package com.tinybee.server.module.service.account;

import com.tinybee.common.enitiy.Account;
import com.tinybee.server.enitity.Player;
import com.tinybee.server.exception.AccountException;
import com.tinybee.server.exception.RedisException;
import com.tinybee.server.exception.SqlException;
import com.tinybee.server.exception.UserException;

public interface IAccountService
{
	Account getAccount(String acc) throws AccountException, RedisException, SqlException;
	Player login(String acc, String pwd, boolean auto) throws AccountException, UserException, RedisException, SqlException;
	void logout(int userId) throws RedisException;
	boolean register(String acc, String pwd);
	boolean attachUserId(int id, int userId);
}
