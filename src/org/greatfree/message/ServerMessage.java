package org.greatfree.message;

import java.io.Serializable;

import org.greatfree.util.Tools;

/*
 * The class is the base for all messages transmitted between remote clients/servers. 07/30/2014, Bing Li
 */

// Created: 07/30/2014, Bing Li
public abstract class ServerMessage implements Serializable
{
	private static final long serialVersionUID = 955012179332438088L;

	// The key of the message. When a message needs to be shared by multiple threads and the relevant synchronization control is required, the key must be initialized. It must be unique in the process. 09/19/2014, Bing Li
	private String key;
	// The type of the message. 09/19/2014, Bing Li
	private int type;

	/*
	 * Initialize the message without initializing the key. 09/19/2014, Bing Li
	 */
	public ServerMessage(int type)
	{
		this.type = type;
		this.key = Tools.generateUniqueKey();
	}

	/*
	 * Initialize the message. The constructor initializes the type and the key. 09/19/2014, Bing Li
	 */
	public ServerMessage(int type, String key)
	{
		this.type = type;
		this.key = key;
	}

	/*
	 * Expose the type. 09/19/2014, Bing Li
	 */
	public int getType()
	{
		return this.type;
	}

	/*
	 * Expose the key. 09/19/2014, Bing Li
	 */
	public String getKey()
	{
		return this.key;
	}
}
