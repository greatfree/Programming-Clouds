package org.greatfree.framework.cps.cache.data;

import java.io.Serializable;
import java.util.Date;

// Created: 07/08/2018, Bing Li
public class MyData implements Serializable
{
	private static final long serialVersionUID = -8217077203029605560L;
	
	private String key;
	private int number;
	private Date time;
	
	public MyData(String key, int number, Date time)
	{
		this.key = key;
		this.number = number;
		this.time = time;
	}
	
	public String getKey()
	{
		return this.key;
	}
	
	public int getNumber()
	{
		return this.number;
	}
	
	public Date getTime()
	{
		return this.time;
	}
}
