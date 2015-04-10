package com.greatfree.testing.message;

import com.greatfree.multicast.ServerMessage;

/*
 * This is the notification for a memory server to unregister on the coordinator. 11/28/2014, Bing Li
 */

// Created: 11/28/2014, Bing Li
public class UnregisterMemoryServerNotification extends ServerMessage
{
	private static final long serialVersionUID = -1541793327700936820L;
	
	// The key of the memory server. Here, DC stands for the term, distributed component. 11/28/2014, Bing Li
	private String dcKey;

	/*
	 * Initialize. 11/28/2014, Bing Li
	 */
	public UnregisterMemoryServerNotification(String dcKey)
	{
		super(MessageType.UNREGISTER_MEMORY_SERVER_NOTIFICATION);
		this.dcKey = dcKey;
	}

	/*
	 * Expose the key of the memory server. 11/28/2014, Bing Li
	 */
	public String getDCKey()
	{
		return this.dcKey;
	}
}
