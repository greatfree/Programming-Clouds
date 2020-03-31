package ca.dp.mncs.circle.server;

import org.greatfree.concurrency.reactive.NotificationThreadCreatable;

import ca.dp.mncs.circle.message.LikeNotification;

// Created: 02/26/2020, Bing Li
class LikeNotificationThreadCreator implements NotificationThreadCreatable<LikeNotification, LikeNotificationThread>
{

	@Override
	public LikeNotificationThread createNotificationThreadInstance(int taskSize)
	{
		return new LikeNotificationThread(taskSize);
	}

}
