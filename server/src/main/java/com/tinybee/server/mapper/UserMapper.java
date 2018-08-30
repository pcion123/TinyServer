package com.tinybee.server.mapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.tinybee.common.enitiy.User;

@Repository
public class UserMapper
{
	private static final String namespace = "userMapper.";
	
	@Autowired
	private SqlSessionTemplate sessionSQLTemplate;
	
	public User getUserByUserId(int userId)
	{
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", userId);
		List<User> list = sessionSQLTemplate.selectList(namespace + "getUserByUserId", params);
		return list.size() > 0 ? list.get(0) : null;
	}
	
	public User getUserByFbid(String fbid)
	{
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("fbid", fbid);
		List<User> list = sessionSQLTemplate.selectList(namespace + "getUserByFbid", params);
		return list.size() > 0 ? list.get(0) : null;
	}
	
	public boolean genUser(User user)
	{
		int result = sessionSQLTemplate.insert(namespace + "genUser", user);
		return result > 0;
	}

	public boolean attachFbid(int userId, String fbid)
	{
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", userId);
		params.put("fbid", fbid);
		int result = sessionSQLTemplate.update(namespace + "attachFbid", params);
		return result > 0;
	}

	public void updateVip(int userId, int value)
	{
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", userId);
		params.put("value", value);
		sessionSQLTemplate.update(namespace + "updateVip", params);
	}

	public void updatePoint(int userId, int value1, int value2)
	{
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", userId);
		params.put("value1", value1);
		params.put("value2", value2);
		sessionSQLTemplate.update(namespace + "updatePoint", params);
	}

	public void updateNickName(int userId, String nickName)
	{
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", userId);
		params.put("nickName", nickName);
		sessionSQLTemplate.update(namespace + "updateNickName", params);
	}

	public void updateAboutme(int userId, String aboutme)
	{
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", userId);
		params.put("aboutme", aboutme);
		sessionSQLTemplate.update(namespace + "updateAboutme", params);
	}

	public void updateAvatarId(int userId, int avatarId)
	{
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", userId);
		params.put("avatarId", avatarId);
		sessionSQLTemplate.update(namespace + "updateAvatarId", params);
	}

	public void updateLv(int userId, int value)
	{
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", userId);
		params.put("value", value);
		sessionSQLTemplate.update(namespace + "updateLv", params);
	}

	public void updateMoney(int userId, int value)
	{
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", userId);
		params.put("value", value);
		sessionSQLTemplate.update(namespace + "updateMoney", params);
	}
}
