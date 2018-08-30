package com.tinybee.common.util;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StringUtil
{
	private static Logger mLogger = LoggerFactory.getLogger(StringUtil.class);
	
	public static boolean isNotNullOrNotEmpty(String string)
	{
		return (string != null && string.length() > 0);
	}

	public static boolean isNullOrEmpty(String string)
	{
		return (string == null || string.length() == 0);
	}
	
	public static boolean isNotNullOrNotEmpty(List list)
	{
		return (list != null && list.size() > 0);
	}
}
