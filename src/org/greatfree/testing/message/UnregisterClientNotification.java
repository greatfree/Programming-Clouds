package org.greatfree.testing.message;

import org.greatfree.message.ServerMessage;

/*
 * When a client shuts down, it needs to send the notification to its connected remote server. So, that server can remove relevant information and collect corresponding resources. 11/07/2014, Bing Li 
 */

// Created: 11/07/2014, Bing Li
public class UnregisterClientNotification extends ServerMessage
{
	private static final long serialVersionUID = -2397160617314342013L;

	// The client key to be sent to the remote server. 11/07/2014 Bing Li
	private String clientKey;
	
	/*
	 * Initialize. 11/07/2014, Bing Li
	 */
	public UnregisterClientNotification(String clientKey)
	{
		super(MessageType.UNREGISTER_CLIENT_NOTIFICATION);
		this.clientKey = clientKey;
	}

	/*
	 * Expose the client key. 11/07/2014, Bing Li
	 */
	public String getClientKey()
	{
		return this.clientKey;
	}
}
