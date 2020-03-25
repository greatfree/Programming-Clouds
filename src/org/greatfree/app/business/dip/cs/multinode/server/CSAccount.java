package org.greatfree.app.business.dip.cs.multinode.server;

/*
 * The account of the chatting system for the C/S based chatting. 04/16/2017, Bing Li
 */

// Created: 04/16/2017, Bing Li
public class CSAccount
{
	private String userKey;
	private String userName;
	private String description;
	
	public CSAccount(String userKey, String userName, String description)
	{
		this.userKey = userKey;
		this.userName = userName;
		this.description = description;
	}
	
	public String getUserKey()
	{
		return this.userKey;
	}
	
	public String getUserName()
	{
		return this.userName;
	}
	
	public String getDescription()
	{
		return this.description;
	}
}
