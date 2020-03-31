package edu.greatfree.cs.multinode.server;

import org.greatfree.concurrency.reactive.NotificationThreadCreatable;

import edu.greatfree.cs.multinode.message.ChatNotification;

// Created: 04/27/2017, Bing Li
class ChatThreadCreator implements NotificationThreadCreatable<ChatNotification, ChatThread>
{

	@Override
	public ChatThread createNotificationThreadInstance(int taskSize)
	{
		return new ChatThread(taskSize);
	}

}
