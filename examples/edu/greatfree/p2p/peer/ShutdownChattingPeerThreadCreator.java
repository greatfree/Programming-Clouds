package edu.greatfree.p2p.peer;

import org.greatfree.concurrency.reactive.NotificationThreadCreatable;

import edu.greatfree.cs.multinode.message.ShutdownServerNotification;

// Created: 05/01/2017, Bing Li
class ShutdownChattingPeerThreadCreator implements NotificationThreadCreatable<ShutdownServerNotification, ShutdownChattingPeerThread>
{

	@Override
	public ShutdownChattingPeerThread createNotificationThreadInstance(int taskSize)
	{
		return new ShutdownChattingPeerThread(taskSize);
	}

}
