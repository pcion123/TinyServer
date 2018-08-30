package com.tinybee.common.enitiy;

import java.util.Date;

public class Account
{
	private int id;
	private String account;
	private String token;
	private int userId;
	private Date createTime;
	private Date updateTime;
	
	public static Account valueOf(String account, String token)
	{
		Account acc = new Account();
		acc.account = account;
		acc.token = token;
		acc.createTime = new Date();
		acc.updateTime = new Date();
		return acc;
	}
	
	public static Account valueOf(String account, String token, int userId)
	{
		Account acc = new Account();
		acc.account = account;
		acc.token = token;
		acc.userId = userId;
		acc.createTime = new Date();
		acc.updateTime = new Date();
		return acc;
	}

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}
	
	public String getAccount()
	{
		return account;
	}
	
	public void setAccount(String account)
	{
		this.account = account;
	}
	
	public String getToken()
	{
		return token;
	}
	
	public void setToken(String token)
	{
		this.token = token;
	}
	
	public int getUserId()
	{
		return userId;
	}
	
	public void setUserId(int userId)
	{
		this.userId = userId;
	}
	
	public Date getCreateTime()
	{
		return createTime;
	}
	
	public void setCreateTime(Date createTime)
	{
		this.createTime = createTime;
	}
	
	public Date getUpdateTime()
	{
		return updateTime;
	}
	
	public void setUpdateTime(Date updateTime)
	{
		this.updateTime = updateTime;
	}
}
