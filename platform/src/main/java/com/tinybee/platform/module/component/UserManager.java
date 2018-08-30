package com.tinybee.platform.module.component;

import com.tinybee.common.enitiy.User;

public interface UserManager
{
	User getUser(int userId);
	User getUser(String fbid);
	User genUser();
	boolean attachFbid(int userId, String fbid);
	void updateVip(int userId, int value);
	void updatePoint(int userId, int value1, int value2);
	void updateNickName(int userId, String nickName);
	void updateAboutme(int userId, String aboutme);
	void updateAvatarId(int userId, int avatarId);
	void updateLv(int userId, int value);
	void updateMoney(int userId, int value);
}
