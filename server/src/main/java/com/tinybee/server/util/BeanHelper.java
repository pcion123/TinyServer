package com.tinybee.server.util;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class BeanHelper
{
    private static ApplicationContext mContext = null;

    private static ApplicationContext getApplicationContext()
    {
        if (mContext == null)
        {
            synchronized (ApplicationContext.class)
            {
                if (mContext == null)
                {
                	mContext = new ClassPathXmlApplicationContext("applicationContext.xml");
                }
            }
        }
        return mContext;
    }

    public static Object getBean(String beanName)
    {
        return getApplicationContext().getBean(beanName);
    }
}
