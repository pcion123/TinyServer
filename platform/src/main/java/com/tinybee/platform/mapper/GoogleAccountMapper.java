package com.tinybee.platform.mapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.tinybee.common.enitiy.Account;

@Repository
public class GoogleAccountMapper
{
	private static final String namespace = "googleAccountMapper.";
	
	@Autowired
	private SqlSessionTemplate sessionSQLTemplate;
	
	public Account getAccountByAccount(String account)
	{
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("account", account);
		List<Account> list = sessionSQLTemplate.selectList(namespace + "getAccountByAccount", params);
		return list.size() > 0 ? list.get(0) : null;
	}
	
	public Account getAccountByUserId(int userId)
	{
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", userId);
		List<Account> list = sessionSQLTemplate.selectList(namespace + "getAccountByUserId", params);
		return list.size() > 0 ? list.get(0) : null;
	}
	
	public boolean createAccount(Account account)
	{
		int result = sessionSQLTemplate.insert(namespace + "createAccount", account);
		return result > 0;
	}

	public boolean attachUserId(int id, int userId)
	{
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", id);
		params.put("userId", userId);
		int result = sessionSQLTemplate.update(namespace + "attachUserId", params);
		return result > 0;
	}
}
