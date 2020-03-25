package org.greatfree.dip.cs.twonode.server;

import org.greatfree.concurrency.reactive.NotificationThreadCreatable;
import org.greatfree.dip.cs.multinode.message.ChatNotification;

// Created: 06/21/2018, Bing Li
public class ChatThreadCreator implements NotificationThreadCreatable<ChatNotification, ChatThread>
{

	@Override
	public ChatThread createNotificationThreadInstance(int taskSize)
	{
		return new ChatThread(taskSize);
	}

}
