package org.greatfree.dip.streaming.message;

import org.greatfree.message.ServerMessage;

// Created: 03/19/2020, Bing Li
public class ShutdownPubSubNotification extends ServerMessage
{
	private static final long serialVersionUID = 7637194670812573936L;

	public ShutdownPubSubNotification()
	{
		super(StreamMessageType.SHUTDOWN_PUBSUB_NOTIFICATION);
	}

}
