package org.greatfree.cache.db;

import java.util.Date;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;

// Created: 05/07/2018, Bing Li
@Entity
class TimingListEntity
{
	@PrimaryKey
	private String key;
	
	private Date time;
	
	public TimingListEntity()
	{
	}
	
	public TimingListEntity(String key, Date time)
	{
		this.key = key;
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
	
	public void setTime(Date time)
	{
		this.time = time;
	}
	
	public Date getTime()
	{
		return this.time;
	}
}
