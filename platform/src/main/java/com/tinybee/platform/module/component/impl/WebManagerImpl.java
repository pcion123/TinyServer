package com.tinybee.platform.module.component.impl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.tinybee.platform.module.component.WebManager;

@Component
public class WebManagerImpl implements WebManager
{
	@Override
	public HttpServletRequest getRequest()
	{
		HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
		return request;
	}

	@Override
	public HttpServletResponse getResponse()
	{
		HttpServletResponse response = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getResponse();
		return response;
	}

	@Override
	public String getWebContext()
	{
		return getRequest().getContextPath();
	}

	@Override
	public String getWebRealPath()
	{
		String path = getRequest().getSession().getServletContext().getRealPath("");
		// path = path.replace("/", "\\");
		return path + "/";
	}
}
