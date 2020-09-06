package org.greatfree.dsf.threading;

import org.greatfree.util.UtilConfig;

// Created: 09/12/2019, Bing Li
public class ThreadInfo
{
	private String threadAKey;
	private String threadBKey;
	
	private String threadA;
	private String threadB;
	
	private ThreadInfo()
	{
		this.threadA = "T1";
		this.threadB = "T2";
		
		this.threadAKey = UtilConfig.EMPTY_STRING;
		this.threadBKey = UtilConfig.EMPTY_STRING;
	}

	/*
	 * A singleton definition. 04/17/2017, Bing Li
	 */
	private static ThreadInfo instance = new ThreadInfo();
	
	public static ThreadInfo ASYNC()
	{
		if (instance == null)
		{
			instance = new ThreadInfo();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	public void init(String aKey, String bKey)
	{
		this.threadAKey = aKey;
		this.threadBKey = bKey;
	}
	
	public void init(String key)
	{
		if (this.threadAKey.equals(UtilConfig.EMPTY_STRING))
		{
			this.threadAKey = key;
		}
		else if (this.threadBKey.equals(UtilConfig.EMPTY_STRING))
		{
			this.threadBKey = key;
		}
	}
	
	public String getThreadName(String threadKey)
	{
		if (this.threadAKey.equals(threadKey))
		{
			return this.threadA;
		}
		return this.threadB;
	}
	
	public String getThreadAKey()
	{
		return this.threadAKey;
	}
	
	public String getThreadBKey()
	{
		return this.threadBKey;
	}
	
	public String getThreadA()
	{
		return this.threadA;
	}
	
	public String getThreadB()
	{
		return this.threadB;
	}
}
