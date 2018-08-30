package com.tinybee.common.enitiy;

import java.util.Date;

import com.tinybee.common.netty.annotation.Member;

public class User
{
	@Member(order=1)
	private int userId;
	@Member(order=2)
	private Date createTime;
	@Member(order=3)
	private Date updateTime;
	@Member(order=4)
	private Date lockTime;
	@Member(order=5)
	private Date monthTime;
	@Member(order=6)
	private String fbid;
	@Member(order=7)
	private short vip;
	@Member(order=8)
	private int realPoint;
	@Member(order=9)
	private int fakePoint;
	@Member(order=10)
	private String nickName;
	@Member(order=11)
	private String aboutme;
	@Member(order=12)
	private short avatarId;
	@Member(order=13)
	private short lv;
	@Member(order=14)
	private int money;
	
	public static User valueOf()
	{
		User user = new User();
		user.createTime = new Date();
		user.updateTime = new Date();
		user.lockTime = new Date(0);
		user.monthTime = new Date(0);
		user.avatarId = 10001;
		user.lv = 1;
		return user;
	}
	
	public static User valueOf(String fbid)
	{
		User user = new User();
		user.createTime = new Date();
		user.updateTime = new Date();
		user.lockTime = new Date(0);
		user.monthTime = new Date(0);
		user.fbid = fbid;
		user.avatarId = 10001;
		user.lv = 1;
		return user;
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

	public Date getLockTime()
	{
		return lockTime;
	}

	public void setLockTime(Date lockTime)
	{
		this.lockTime = lockTime;
	}

	public Date getMonthTime()
	{
		return monthTime;
	}

	public void setMonthTime(Date monthTime)
	{
		this.monthTime = monthTime;
	}

	public String getFbid()
	{
		return fbid;
	}

	public void setFbid(String fbid)
	{
		this.fbid = fbid;
	}

	public short getVip()
	{
		return vip;
	}

	public void setVip(short vip)
	{
		this.vip = vip;
	}

	public int getRealPoint()
	{
		return realPoint;
	}

	public void setRealPoint(int realPoint)
	{
		this.realPoint = realPoint;
	}

	public int getFakePoint()
	{
		return fakePoint;
	}

	public void setFakePoint(int fakePoint)
	{
		this.fakePoint = fakePoint;
	}

	public String getNickName()
	{
		return nickName;
	}

	public void setNickName(String nickName)
	{
		this.nickName = nickName;
	}

	public String getAboutme()
	{
		return aboutme;
	}

	public void setAboutme(String aboutme)
	{
		this.aboutme = aboutme;
	}

	public short getAvatarId()
	{
		return avatarId;
	}

	public void setAvatarId(short avatarId)
	{
		this.avatarId = avatarId;
	}

	public short getLv()
	{
		return lv;
	}

	public void setLv(short lv)
	{
		this.lv = lv;
	}

	public int getMoney()
	{
		return money;
	}

	public void setMoney(int money)
	{
		this.money = money;
	}
}
