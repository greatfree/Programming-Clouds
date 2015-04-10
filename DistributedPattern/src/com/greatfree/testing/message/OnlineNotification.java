package com.greatfree.testing.message;

import com.greatfree.multicast.ServerMessage;

/*
 * This notification is sent to a remote server that the client expects to connect. Its goal is to establish a connection. Moreover, if a peer-to-peer architecture needs to be constructed between them, the server starts to connect to the client after receiving the notification. The notification works like a stimulus. 11/07/2014, Bing Li
 */

// Created: 11/07/2014, Bing Li
public class OnlineNotification extends ServerMessage
{
	private static final long serialVersionUID = -8946571501653241937L;

	public OnlineNotification()
	{
		super(MessageType.ONLINE_NOTIFICATION);
	}
}
