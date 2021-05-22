package org.greatfree.framework.p2p.peer;

import org.greatfree.concurrency.reactive.NotificationQueueCreator;
import org.greatfree.framework.p2p.message.ChatNotification;

// Created: 05/02/2017, Bing Li
class ChatThreadCreator implements NotificationQueueCreator<ChatNotification,  ChatThread>
{

	@Override
	public ChatThread createInstance(int taskSize)
	{
		return new ChatThread(taskSize);
	}

}
