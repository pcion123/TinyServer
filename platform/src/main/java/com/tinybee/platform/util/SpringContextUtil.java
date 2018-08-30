package com.tinybee.platform.util;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class SpringContextUtil implements ServletContextListener
{
	public SpringContextUtil()
	{
		System.out.println("**************初始化SpringContextUtil*****************");
	}

	private static ApplicationContext ctx;

	public static ApplicationContext getCtx()
	{
		return ctx;
	}

	@Override
	public void contextInitialized(ServletContextEvent sce)
	{
		System.out.println("**************contextInitialized*****************");
		
//		ResourceLoader resourceLoader = new PlatformResourceLoader();
//		ResourceLoaderManager.setResourceLoader(resourceLoader);
		
		SpringContextUtil.ctx = WebApplicationContextUtils.getWebApplicationContext(sce.getServletContext());
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce)
	{
		System.out.println("**************contextDestroyed*****************");
	}
	
	public static boolean containsBean(String arg0)
	{
		return ctx.containsBean(arg0);
	}
	
	public static String[] getAliases(String arg0)
	{
		return ctx.getAliases(arg0);
	}
	
	public static <T> T getBean(Class<T> arg0, Object... arg1) throws BeansException
	{
		return ctx.getBean(arg0, arg1);
	}
	
	public static Object getBean(String arg0) throws BeansException
	{
		return ctx.getBean(arg0);
	}
	
	public static <T> T getBean(Class<T> cls) throws BeansException
	{
		return ctx.getBean(cls);
	}
	
	public static <T> T getBean(String arg0, Class<T> arg1) throws BeansException
	{
		return ctx.getBean(arg0, arg1);
	}
	
	public Object getBean(String arg0, Object... arg1) throws BeansException
	{
		return ctx.getBean(arg0, arg1);
	}
	
	public static int getBeanDefinitionCount()
	{
		return ctx.getBeanDefinitionCount();
	}
	
	public String[] getBeanNamesForType(Class<?> arg0)
	{
		return ctx.getBeanNamesForType(arg0);
	}
	
	public static String getId()
	{
		return ctx.getId();
	}
	
	public boolean isPrototype(String arg0) throws NoSuchBeanDefinitionException
	{
		return ctx.isPrototype(arg0);
	}
	
	public static boolean isSingleton(String arg0) throws NoSuchBeanDefinitionException
	{
		return ctx.isSingleton(arg0);
	}
}
