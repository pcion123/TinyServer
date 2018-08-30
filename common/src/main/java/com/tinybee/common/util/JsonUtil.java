package com.tinybee.common.util;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;

public class JsonUtil
{
	private static Logger mLogger = LoggerFactory.getLogger(JsonUtil.class);
	
	public static String obj2JsonStr(Object obj)
	{

		String resultText = JSON.toJSONString(obj,
				SerializerFeature.WriteNullListAsEmpty, SerializerFeature.WriteMapNullValue,
				SerializerFeature.WriteNullNumberAsZero, SerializerFeature.WriteNullStringAsEmpty,
				SerializerFeature.QuoteFieldNames, SerializerFeature.DisableCircularReferenceDetect);
		return resultText;
	}

	public static <T> T toObject(String json, Class<T> clazz)
	{
		return JSON.parseObject(json, clazz);
	}
	
	public static <T> List<T> toListObject(String json, Class<T> clazz)
	{
		return JSON.parseArray(json, clazz);
	}

	public static JSONObject toObject(String json)
	{
		return JSON.parseObject(json);
	}
}

