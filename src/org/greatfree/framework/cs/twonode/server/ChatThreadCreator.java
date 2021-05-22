package org.greatfree.framework.cs.twonode.server;

import org.greatfree.concurrency.reactive.NotificationQueueCreator;
import org.greatfree.framework.cs.multinode.message.ChatNotification;

// Created: 06/21/2018, Bing Li
public class ChatThreadCreator implements NotificationQueueCreator<ChatNotification, ChatThread>
{

	@Override
	public ChatThread createInstance(int taskSize)
	{
		return new ChatThread(taskSize);
	}

}
