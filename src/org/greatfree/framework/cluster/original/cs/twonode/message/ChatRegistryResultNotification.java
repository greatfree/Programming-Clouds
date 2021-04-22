package org.greatfree.framework.cluster.original.cs.twonode.message;

import org.greatfree.message.multicast.MulticastMessageType;
import org.greatfree.message.multicast.container.Notification;

// Created: 01/15/2019, Bing Li
public class ChatRegistryResultNotification extends Notification
{
	private static final long serialVersionUID = 4675477264955635558L;
	
	private boolean isSucceeded;

	public ChatRegistryResultNotification(boolean isSucceeded)
	{
		super(MulticastMessageType.BROADCAST_NOTIFICATION, ChatApplicationID.CHAT_REGISTRY_RESULT_NOTIFICATION);
		this.isSucceeded = isSucceeded;
	}

	public boolean isSucceeded()
	{
		return this.isSucceeded;
	}
}
