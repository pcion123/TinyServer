package com.tinybee.platform.requests.custom;

import com.tinybee.platform.requests.base.BaseRequest;

public class ModifyAboutme extends BaseRequest
{
	private static final long serialVersionUID = 1L;
	
	private String aboutme;
	
	public String getAboutme()
	{
		return aboutme;
	}
	
	public void setAboutme(String aboutme)
	{
		this.aboutme = aboutme;
	}
}
