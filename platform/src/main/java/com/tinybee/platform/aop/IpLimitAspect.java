package com.tinybee.platform.aop;

import java.util.concurrent.TimeUnit;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.tinybee.common.util.CommonUtil;
import com.tinybee.platform.module.component.WebManager;

@Aspect
@Component
@Order(1)
public class IpLimitAspect
{
	private static Logger logger = LoggerFactory.getLogger(IpLimitAspect.class);
	
    private final static int LIMIT_REQUEST_TIMES = 60;
    private final static int LIMIT_REQUEST_IN_TIME = 20;
    private final static int LIMIT_IP_TIME = 5 * 60;
    private final static String LIMIT_PREFIX = "limitIp:";
    private final static String LIMIT_FIELD_LIMIT = "limit:";
    private final static String UNLIMITED_VALUE = "0";
    private final static String LIMITED_VALUE = "1";
    private final static String LIMIT_FIELD_REQTIMES = "reqTimes:";
	
    @Autowired
    private StringRedisTemplate sessionRedisTemplate;
    
    @Autowired
    private WebManager webManager;
    
    @Pointcut("execution(* com.tinybee.platform.controller.*Controller.*(..)) && @annotation(org.springframework.web.bind.annotation.RequestMapping)")
    public void executeMethod()
    {
    	System.out.println("IpLimitAspect executeMethod");
    	logger.info("IpLimitAspect executeMethod");
    }
    
    @Around(value = "executeMethod()")
    public Object doAround(ProceedingJoinPoint pjp) throws Throwable
    {
    	// 取得clientIp
        String clientIp = CommonUtil.getIpAddr(webManager.getRequest());
        // 取得redis鍵值
        String limitKey = LIMIT_PREFIX + clientIp;
        // 取得限制狀態
        String limitState = (String)sessionRedisTemplate.boundHashOps(limitKey).get(LIMIT_FIELD_LIMIT);
        // 檢查限制狀態
        if (StringUtils.isEmpty(limitState))
        {
            initLimit(limitKey);
        }
        else if (UNLIMITED_VALUE.equals(limitState))
        {
        	// 增加紀錄次數
            Long reqTimes = sessionRedisTemplate.boundHashOps(limitKey).increment(LIMIT_FIELD_REQTIMES, 1);
            // 檢查是否超過限制
            if (reqTimes > LIMIT_REQUEST_TIMES)
            {
            	// 更新限制狀態
                sessionRedisTemplate.boundHashOps(limitKey).put(LIMIT_FIELD_LIMIT, LIMITED_VALUE);
                // 定時清除時間
                sessionRedisTemplate.boundHashOps(limitKey).expire(LIMIT_IP_TIME, TimeUnit.SECONDS);
            }
        }
        else if (LIMITED_VALUE.equals(limitState))
        {
        	// 增加紀錄次數
            sessionRedisTemplate.boundHashOps(limitKey).increment(LIMIT_FIELD_REQTIMES, 1);
            // 定時清除時間
            sessionRedisTemplate.boundHashOps(limitKey).expire(LIMIT_IP_TIME, TimeUnit.SECONDS);
            throw new Exception("请求过于频繁，5分钟后再试");
        }
        return pjp.proceed();
    }
    
    private Object initLimit(final String limitKey)
    {
        Object obj = sessionRedisTemplate.execute(new SessionCallback<Object>()
        {
            @Override
            public <K, V> Object execute(RedisOperations<K, V> operations) throws DataAccessException
            {
                operations.watch((K)limitKey);
                operations.multi();
                operations.boundHashOps((K)limitKey).put(LIMIT_FIELD_LIMIT, UNLIMITED_VALUE);
                operations.boundHashOps((K)limitKey).increment(LIMIT_FIELD_REQTIMES, 1);
                operations.boundHashOps((K)limitKey).expire(LIMIT_REQUEST_IN_TIME, TimeUnit.SECONDS);
                return operations.exec();
            }
        });
        return obj;
    }
}
