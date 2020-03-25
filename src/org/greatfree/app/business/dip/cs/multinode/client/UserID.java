package org.greatfree.app.business.dip.cs.multinode.client;

import org.greatfree.util.Tools;

// Created: 12/22/2017, Bing Li
public class UserID
{
	private String userName;
	private String userKey;
	
	private UserID()
	{
	}

	/*
	 * A singleton implementation. 11/07/2014, Bing Li
	 */
	private static UserID instance = new UserID();
	
	public static UserID CID()
	{
		if (instance == null)
		{
			instance = new UserID();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	public String getUserName()
	{
		return this.userName;
	}
	
	public void setUserName(String userName)
	{
		this.userName = userName;
		this.userKey = Tools.getHash(userName);
	}
	
	public String getUserKey()
	{
		return this.userKey;
	}
}
