package com.tinybee.common.util;

import java.util.Random;

public class TokenUtil
{
	private static final String TOKEN_PREFIX = "token";
	private static final String RANDOM_STR_BASE = "abcdefghijklmnopqrstuvwxyz0123456789";
	private static final String RANDOM_NUM_BASE = "0123456789";
	
	public static String generateToken(int userId)
	{
		String plaintext = userId + System.currentTimeMillis() + generateRandomStr(6, false);
		String token = TOKEN_PREFIX + ":" + userId + ":" + Md5Uitl.parseStrToMd5U32(plaintext);
		return token;
	}
	
	private static String generateRandomStr(int length, boolean isNum)
	{
		String random_base = isNum ? RANDOM_NUM_BASE : RANDOM_STR_BASE;
		Random random = new Random();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < length; i++)
		{
			int number = random.nextInt(random_base.length());
			sb.append(random_base.charAt(number));
		}
		return sb.toString();
	}
}
