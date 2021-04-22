package org.greatfree.framework.cps.cache.terminal.db;

import java.util.Date;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;

// Created: 07/08/2018, Bing Li
@Entity
class MyDataEntity
{
	@PrimaryKey
	private String key;
	
	private int number;
	private Date time;
	
	public MyDataEntity()
	{
	}
	
	public MyDataEntity(String key, int number, Date time)
	{
		this.key = key;
		this.number = number;
		this.time = time;
	}
	
	public void setKey(String key)
	{
		this.key = key;
	}
	
	public String getKey()
	{
		return this.key;
	}
	
	public void setNumber(int number)
	{
		this.number = number;
	}
	
	public int getNumber()
	{
		return this.number;
	}
	
	public void setTime(Date time)
	{
		this.time = time;
	}
	
	public Date getTime()
	{
		return this.time;
	}

}
