package org.greatfree.framework.cs.multinode.server;

import org.greatfree.concurrency.reactive.NotificationQueueCreator;
import org.greatfree.framework.cs.multinode.message.ChatNotification;

// Created: 04/27/2017, Bing Li
class ChatThreadCreator implements NotificationQueueCreator<ChatNotification, ChatThread>
{

	@Override
	public ChatThread createInstance(int taskSize)
	{
		return new ChatThread(taskSize);
	}

}
