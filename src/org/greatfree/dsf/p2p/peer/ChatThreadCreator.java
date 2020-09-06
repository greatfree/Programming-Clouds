package org.greatfree.dsf.p2p.peer;

import org.greatfree.concurrency.reactive.NotificationThreadCreatable;
import org.greatfree.dsf.p2p.message.ChatNotification;

// Created: 05/02/2017, Bing Li
class ChatThreadCreator implements NotificationThreadCreatable<ChatNotification,  ChatThread>
{

	@Override
	public ChatThread createNotificationThreadInstance(int taskSize)
	{
		return new ChatThread(taskSize);
	}

}
