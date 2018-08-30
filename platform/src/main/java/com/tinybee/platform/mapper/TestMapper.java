package com.tinybee.platform.mapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class TestMapper
{
	private static final String namespace = "testMapper.";
	
	@Autowired
	private SqlSessionTemplate sessionSQLTemplate;
	
	public List<String> getAll()
	{
		return sessionSQLTemplate.selectList(namespace + "getAll");
	}
	
	public boolean insert(String context)
	{
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("context", context);
		int result = sessionSQLTemplate.insert(namespace + "insert", params);
		System.out.println(result);
		return result > 0;
	}
}
