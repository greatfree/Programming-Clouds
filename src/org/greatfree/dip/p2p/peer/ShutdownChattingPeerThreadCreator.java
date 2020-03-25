package org.greatfree.dip.p2p.peer;

import org.greatfree.chat.message.ShutdownServerNotification;
import org.greatfree.concurrency.reactive.NotificationThreadCreatable;

// Created: 05/01/2017, Bing Li
class ShutdownChattingPeerThreadCreator implements NotificationThreadCreatable<ShutdownServerNotification, ShutdownChattingPeerThread>
{

	@Override
	public ShutdownChattingPeerThread createNotificationThreadInstance(int taskSize)
	{
		return new ShutdownChattingPeerThread(taskSize);
	}

}
