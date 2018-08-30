package com.tinybee.platform.aop;

import java.lang.reflect.Field;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import com.tinybee.common.constant.Versions;
import com.tinybee.common.enitiy.Session;
import com.tinybee.common.util.JsonUtil;
import com.tinybee.platform.enums.ResponseEnum;
import com.tinybee.platform.excepiton.GlobalException;
import com.tinybee.platform.module.component.RedisManager;
import com.tinybee.platform.requests.base.BaseRequest;
import com.tinybee.platform.responses.ResponseData;

@Aspect
@Component
@Order(2)
public class SessionRequestAspect
{
	private static Logger logger = LoggerFactory.getLogger(SessionRequestAspect.class);
	
	@Autowired
	private RedisManager redisManager;
    
    @Around("@annotation(com.tinybee.platform.annotation.NeedSession)")
    public Object doAround(ProceedingJoinPoint point) throws Throwable
    {
    	Object[] params = point.getArgs();
        Object request = params[params.length - 1];
        if (!request.getClass().getSuperclass().getName().equals(BaseRequest.class.getName()))
        {
        	throw new GlobalException(ResponseEnum.PARAM_ERROR);
        }
        ResponseData response = checkSession(point, request);
        if (response == null)
        {
        	return point.proceed();
        }
        else
        {
            return response;
        }
    }
    
    private ResponseData checkSession(ProceedingJoinPoint pjp, Object request)
    {
    	BaseRequest baseRequest = null;
        ResponseData response = null;
        try
        {
        	baseRequest = new BaseRequest();
            Field[] fields = request.getClass().getSuperclass().getDeclaredFields();
            for (Field field : fields)
            {
                field.setAccessible(true);
                String fieldName = field.getName();
                switch (fieldName)
                {
                    case "token":
                    	baseRequest.setToken((String)field.get(request));
                        break;
                    case "requestId":
                    	baseRequest.setRequestId((long)field.get(request));
                        break;
                    case "deviceId":
                    	baseRequest.setDeviceId((String)field.get(request));
                        break;
                    case "platform":
                    	baseRequest.setPlatform((String)field.get(request));
                        break;
                    case "version":
                    	baseRequest.setVersion((String)field.get(request));
                        break;
                }
            }
            
            logger.debug("收到基本參數={}", JsonUtil.obj2JsonStr(baseRequest));
            
            String token = baseRequest.getToken();
            long requestId = baseRequest.getRequestId();
            String deviceId = baseRequest.getDeviceId();
            String platform = baseRequest.getPlatform();
            String version = baseRequest.getVersion();
            
            checkToken(token);
            checkRequestId(requestId);
            checkDeviceId(deviceId);
            checkPlatform(platform);
            checkVersion(version);
        }
        catch (Exception e)
        {
            if (e instanceof GlobalException)
            {
            	response = ResponseData.create(((GlobalException)e).getCode(), e.getMessage());
            }
            else
            {
            	response = ResponseData.create(ResponseEnum.SYSTEM_ERROR);
            }
        }
        return response;
    }
    
    private void checkToken(String token)
    {
        if (StringUtils.isEmpty(token))
        {
        	throw new GlobalException(ResponseEnum.PARAM_TOKEN_ERROR);
        }
        else
        {
        	// 拆分token
        	String[] tokenArray = token.split("\\:");
        	// 檢查token長度
        	if (tokenArray.length != 3)
        	{
        		throw new GlobalException(ResponseEnum.PARAM_TOKEN_ERROR);
        	}
        	// 組合token鍵值
        	String key = tokenArray[0] + ":" + tokenArray[1];
        	// 取得session
        	Session session = redisManager.get(Session.class, key);
        	// 檢查session
            if (ObjectUtils.isEmpty(session))
            {
            	throw new GlobalException(ResponseEnum.PARAM_TOKEN_ERROR);
            }
            // 檢查token是否相符
            if (!token.equals(session.getToken()))
            {
            	throw new GlobalException(ResponseEnum.PARAM_TOKEN_ERROR);
            }
            // 更新session
            redisManager.setExpire(session.getKey(), session, 30 * 60);
        }
    }
    
    private void checkRequestId(long requestId)
    {
    	//校验requestId是否可以转化为时间，生成yyyy-MM-dd HH:mm:ss.SSS格式的时间
    }
    
    private void checkDeviceId(String deviceId)
    {
    	//校验deviceId是否有该设备
    }
    
    private void checkPlatform(String platform)
    {
        if (!("android".equals(platform) || "ios".equals(platform)))
        {
            throw new GlobalException(ResponseEnum.PARAM_PLATFORM_ERROR);
        }
    }
    
    private void checkVersion(String version)
    {
        String[] versionArry = version.split("\\.");
        if (versionArry.length != 3)
        {
        	throw new GlobalException(ResponseEnum.PARAM_VERSION_ERROR);
        }
        else
        {
            // 檢查版本號第一位
        	if (Integer.valueOf(versionArry[0]) <= 0)
        	{
        		throw new GlobalException(ResponseEnum.PARAM_VERSION_ERROR);
        	}
            // 檢查版本號第二位
        	if (Integer.valueOf(versionArry[1]) < 0 || Integer.valueOf(versionArry[1]) > 9)
        	{
        		throw new GlobalException(ResponseEnum.PARAM_VERSION_ERROR);
        	}
        	// 檢查版本號第三位
        	if (Integer.valueOf(versionArry[2]) < 0 || Integer.valueOf(versionArry[2]) > 9)
        	{
        		throw new GlobalException(ResponseEnum.PARAM_VERSION_ERROR);
        	}
        	// 檢查版本編號
            if (!version.equals(Versions.ver))
            {
            	throw new GlobalException(ResponseEnum.PARAM_VERSION_ERROR);
            }
        }
    }
}
