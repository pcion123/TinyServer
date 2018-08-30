package com.tinybee.platform.module.service;

import com.tinybee.common.enitiy.Account;
import com.tinybee.platform.excepiton.AccountException;
import com.tinybee.platform.responses.ResponseData;

public interface AccountService
{
	Account getAccount(String acc) throws AccountException;
	ResponseData login(String acc, String pwd, boolean auto);
	ResponseData logout(int userId);
	boolean register(String acc, String pwd);
	boolean attachUserId(int id, int userId);
}