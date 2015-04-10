package com.greatfree.util;

/*
 * This singleton is used to save a node's unique ID only. 11/07/2014, Bing Li
 */

// Created: 11/07/2014, Bing Li
public class NodeID
{
	// The unique key. 11/07/2014, Bing Li
	private String key;
	
	private NodeID()
	{
	}

	// A singleton implementation. 11/07/2014, Bing Li
	private static NodeID instance = new NodeID();
	
	public static NodeID DISTRIBUTED()
	{
		if (instance == null)
		{
			instance = new NodeID();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	/*
	 * Set the key. 11/07/2014, Bing Li
	 */
	public void setKey(String clientKey)
	{
		this.key = clientKey;
	}

	/*
	 * Expose the key. 11/07/2014, Bing Li
	 */
	public String getKey()
	{
		return this.key;
	}
}
