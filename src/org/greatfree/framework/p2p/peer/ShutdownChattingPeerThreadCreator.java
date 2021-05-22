package org.greatfree.framework.p2p.peer;

import org.greatfree.chat.message.ShutdownServerNotification;
import org.greatfree.concurrency.reactive.NotificationQueueCreator;

// Created: 05/01/2017, Bing Li
class ShutdownChattingPeerThreadCreator implements NotificationQueueCreator<ShutdownServerNotification, ShutdownChattingPeerThread>
{

	@Override
	public ShutdownChattingPeerThread createInstance(int taskSize)
	{
		return new ShutdownChattingPeerThread(taskSize);
	}

}
