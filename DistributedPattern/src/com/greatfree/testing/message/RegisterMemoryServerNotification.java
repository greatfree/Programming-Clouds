package com.greatfree.testing.message;

import com.greatfree.multicast.ServerMessage;

/*
 * This is the notification for a memory server to register on the coordinator. 11/28/2014, Bing Li
 */

// Created: 11/28/2014, Bing Li
public class RegisterMemoryServerNotification extends ServerMessage
{
	private static final long serialVersionUID = -6985005756162124112L;

	// The key of the memory server. Here, DC stands for the term, distributed component. 11/28/2014, Bing Li
	private String dcKey;

	/*
	 * Initialize. 11/28/2014, Bing Li
	 */
	public RegisterMemoryServerNotification(String dcKey)
	{
		super(MessageType.REGISTER_MEMORY_SERVER_NOTIFICATION);
		this.dcKey = dcKey;
	}

	/*
	 * Expose the key of the memory. 11/28/2014, Bing Li
	 */
	public String getDCKey()
	{
		return this.dcKey;
	}
}
