package com.tinybee.platform.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.tinybee.platform.annotation.NeedSession;
import com.tinybee.platform.enums.ResponseEnum;
import com.tinybee.platform.module.service.AccountService;
import com.tinybee.platform.requests.custom.LoginReq;
import com.tinybee.platform.requests.custom.LogoutReq;
import com.tinybee.platform.requests.custom.RegisterReq;
import com.tinybee.platform.responses.ResponseData;

@Controller
@RequestMapping(value = "/FacebookAccount")
public class FacebookAccountController
{
	private static Logger logger = LoggerFactory.getLogger(FacebookAccountController.class);
    
	@Autowired
	@Qualifier("facebookAccountServiceImpl")
	private AccountService accountService;
    
	@RequestMapping(value = "/login.do", method = RequestMethod.POST)
	@ResponseBody
	public ResponseData login(LoginReq request)
	{
		String account = request.getAccount();
		String password = request.getPassword();
		return accountService.login(account, password, false);
	}
	
	@RequestMapping(value = "/logout.do", method = RequestMethod.POST)
	@ResponseBody
	@NeedSession
	public ResponseData logout(LogoutReq request)
	{
		int userId = request.getUserId();
		return accountService.logout(userId);
	}
	
	@RequestMapping(value = "/register.do", method = RequestMethod.POST)
	@ResponseBody
	public ResponseData register(RegisterReq request)
	{
		String account = request.getAccount();
		String password = request.getPassword();
		boolean success = accountService.register(account, password);
		if (success)
		{
			return ResponseData.create(ResponseEnum.SUCCESS);
		}
		else
		{
			return ResponseData.create(ResponseEnum.ACCOUNT_REGISTER_FAIL);
		}
	}
}
