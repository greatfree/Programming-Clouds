package org.greatfree.util;

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
		this.key = Tools.generateUniqueKey();
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
		// The key can be created by the remote peer according to the local peer's IP. It can also be generated locally if the local node is a client only. 05/01/2017, Bing Li
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
