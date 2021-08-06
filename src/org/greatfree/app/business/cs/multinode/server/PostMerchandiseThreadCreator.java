package org.greatfree.app.business.cs.multinode.server;

import org.greatfree.chat.message.cs.business.PostMerchandiseNotification;
import org.greatfree.concurrency.reactive.NotificationQueueCreator;

// Created: 12/05/2017, Bing Li
public class PostMerchandiseThreadCreator implements NotificationQueueCreator<PostMerchandiseNotification, PostMerchandiseThread>
{

	@Override
	public PostMerchandiseThread createInstance(int taskSize)
	{
		return new PostMerchandiseThread(taskSize);
	}

}
