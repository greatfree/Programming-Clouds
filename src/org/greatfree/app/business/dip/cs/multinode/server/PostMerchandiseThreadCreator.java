package org.greatfree.app.business.dip.cs.multinode.server;

import org.greatfree.chat.message.cs.business.PostMerchandiseNotification;
import org.greatfree.concurrency.reactive.NotificationThreadCreatable;

// Created: 12/05/2017, Bing Li
public class PostMerchandiseThreadCreator implements NotificationThreadCreatable<PostMerchandiseNotification, PostMerchandiseThread>
{

	@Override
	public PostMerchandiseThread createNotificationThreadInstance(int taskSize)
	{
		return new PostMerchandiseThread(taskSize);
	}

}
