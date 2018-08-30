package com.tinybee.platform.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.tinybee.platform.annotation.NeedSession;
import com.tinybee.platform.enums.ResponseEnum;
import com.tinybee.platform.module.component.UserManager;
import com.tinybee.platform.requests.custom.CreateUserReq;
import com.tinybee.platform.requests.custom.ModifyAboutme;
import com.tinybee.platform.requests.custom.ModifyAvatarReq;
import com.tinybee.platform.requests.custom.ModifyNickName;
import com.tinybee.platform.responses.ResponseData;
import com.tinybee.platform.responses.custom.ServerTimeRes;

@Controller
@RequestMapping(value = "/User")
public class UserController
{
	private static Logger logger = LoggerFactory.getLogger(UserController.class);
	
	@Autowired
	private UserManager userManager;
	
	@RequestMapping(value = "/getServerTime.do", method = RequestMethod.POST)
	@ResponseBody
	public ResponseData getServerTime()
	{
		return ResponseData.create(ResponseEnum.SUCCESS, ServerTimeRes.valueOf());
	}
	
	@RequestMapping(value = "/getPing.do", method = RequestMethod.POST)
	@ResponseBody
	public ResponseData getPing()
	{
		return ResponseData.create(ResponseEnum.SUCCESS);
	}

	@RequestMapping(value = "/createUser.do", method = RequestMethod.POST)
	@ResponseBody
	@NeedSession
	public ResponseData create(CreateUserReq request)
	{
		int userId = request.getUserId();
		int avatarId = request.getAvatarId();
		String nickName = request.getNickName();
		userManager.updateAvatarId(userId, avatarId);
		return ResponseData.create(ResponseEnum.SUCCESS, request.getToken());
	}

	@RequestMapping(value = "/modifyAvatar.do", method = RequestMethod.POST)
	@ResponseBody
	@NeedSession
	public ResponseData modifyAvatar(ModifyAvatarReq request)
	{
		int userId = request.getUserId();
		int avatarId = request.getAvatarId();
		userManager.updateAvatarId(userId, avatarId);
		return ResponseData.create(ResponseEnum.SUCCESS, request.getToken());
	}
	
	@RequestMapping(value = "/modifyNickName.do", method = RequestMethod.POST)
	@ResponseBody
	@NeedSession
	public ResponseData modifyNickName(ModifyNickName request)
	{
		int userId = request.getUserId();
		String nickName = request.getNickName();
		userManager.updateNickName(userId, nickName);
		return ResponseData.create(ResponseEnum.SUCCESS, request.getToken());
	}
	
	@RequestMapping(value = "/modifyAboutme.do", method = RequestMethod.POST)
	@ResponseBody
	@NeedSession
	public ResponseData modifyAboutme(ModifyAboutme request)
	{
		int userId = request.getUserId();
		String aboutme = request.getAboutme();
		userManager.updateAboutme(userId, aboutme);
		return ResponseData.create(ResponseEnum.SUCCESS, request.getToken());
	}

//    GameInfos.NetMgr.AddEvent(Const_Net.PRO_USER, 000, Rcv_002_000, false); //玩家 登入結果
//    GameInfos.NetMgr.AddEvent(Const_Net.PRO_USER, 001, Rcv_002_001, false); //玩家 登出結果
//    GameInfos.NetMgr.AddEvent(Const_Net.PRO_USER, 002, Rcv_002_002, false); //玩家 通知創角
//    GameInfos.NetMgr.AddEvent(Const_Net.PRO_USER, 003, Rcv_002_003, false); //玩家 創角結果
//    GameInfos.NetMgr.AddEvent(Const_Net.PRO_USER, 010, Rcv_002_010, false); //玩家 連線驗證碼
//    GameInfos.NetMgr.AddEvent(Const_Net.PRO_USER, 011, Rcv_002_011, false); //玩家 更新物品
//    GameInfos.NetMgr.AddEvent(Const_Net.PRO_USER, 012, Rcv_002_012, false); //玩家 更新卡片
//    GameInfos.NetMgr.AddEvent(Const_Net.PRO_USER, 101, Rcv_002_101, false); //玩家 綁定帳號
//    GameInfos.NetMgr.AddEvent(Const_Net.PRO_USER, 255, Rcv_002_255, false); //玩家 更新玩家資料
}
