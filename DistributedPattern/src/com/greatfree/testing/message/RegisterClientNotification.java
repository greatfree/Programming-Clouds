package com.greatfree.testing.message;

import com.greatfree.multicast.ServerMessage;

/*
 * This notification sends its unique key to a remote server to register its necessary information on the server. 11/07/2014, Bing Li
 */

// Created: 11/07/2014, Bing Li
public class RegisterClientNotification extends ServerMessage
{
	private static final long serialVersionUID = -6716159464958246998L;

	// The unique key of a client. 11/07/2014, Bing Li
	private String clientKey;

	/*
	 * Initialize. 11/07/2014, Bing Li
	 */
	public RegisterClientNotification(String clientKey)
	{
		super(MessageType.REGISTER_CLIENT_NOTIFICATION);
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
