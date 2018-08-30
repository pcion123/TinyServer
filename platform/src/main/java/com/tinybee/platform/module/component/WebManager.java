package com.tinybee.platform.module.component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface WebManager
{
	HttpServletRequest getRequest();
	HttpServletResponse getResponse();
	String getWebContext();
	String getWebRealPath();
}
