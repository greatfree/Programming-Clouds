package org.greatfree.exceptions;

// Created: 05/15/2018, Bing Li
public class TerminalServerOverflowedException extends Exception
{
	private static final long serialVersionUID = -6589387915706332198L;
	
//	private String serverID;
	private String cacheKey;
	
//	public TerminalServerOverflowedException(String serverID, String cacheKey)
	public TerminalServerOverflowedException(String cacheKey)
	{
//		this.serverID = serverID;
		this.cacheKey = cacheKey;
	}

	/*
	public String getServerID()
	{
		return this.serverID;
	}
	*/
	
	public String getCacheKey()
	{
		return this.cacheKey;
	}
}
