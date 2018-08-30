package com.tinybee.platform.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config
{
    @Value("${web.id}")
    private int id;
    
    public int getId()
    {
    	return id;
    }
    
	public void setId(int id)
	{
		this.id = id;
	}
}
