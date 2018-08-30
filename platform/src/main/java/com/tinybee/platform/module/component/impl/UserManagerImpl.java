package com.tinybee.platform.module.component.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tinybee.common.enitiy.User;
import com.tinybee.platform.mapper.UserMapper;
import com.tinybee.platform.module.component.UserManager;

@Component
public class UserManagerImpl implements UserManager
{
	@Autowired
	private UserMapper userMapper;
	
	@Override
	public User getUser(int userId)
	{
		return userMapper.getUserByUserId(userId);
	}
	
	@Override
	public User getUser(String fbid)
	{
		return userMapper.getUserByFbid(fbid);
	}
	
	@Override
	public User genUser()
	{
		User user = User.valueOf();
		boolean success = userMapper.genUser(user);
		int userId = user.getUserId();
		return (success && userId > 0) ? user : null;
	}
	
	@Override
	public boolean attachFbid(int userId, String fbid)
	{
		return userMapper.attachFbid(userId, fbid);
	}
	
	@Override
	public void updateVip(int userId, int value)
	{
		userMapper.updateVip(userId, value);
	}

	@Override
	public void updatePoint(int userId, int value1, int value2)
	{
		userMapper.updatePoint(userId, value1, value2);
	}

	@Override
	public void updateNickName(int userId, String nickName)
	{
		userMapper.updateNickName(userId, nickName);
	}

	@Override
	public void updateAboutme(int userId, String aboutme)
	{
		userMapper.updateAboutme(userId, aboutme);
	}

	@Override
	public void updateAvatarId(int userId, int avatarId)
	{
		userMapper.updateAvatarId(userId, avatarId);
	}

	@Override
	public void updateLv(int userId, int value)
	{
		userMapper.updateLv(userId, value);
	}

	@Override
	public void updateMoney(int userId, int value)
	{
		userMapper.updateMoney(userId, value);
	}
}
