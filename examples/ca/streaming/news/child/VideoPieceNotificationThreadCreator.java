package ca.streaming.news.child;

import org.greatfree.concurrency.reactive.NotificationThreadCreatable;

import ca.streaming.news.message.VideoPieceNotification;

// Created: 04/02/2020, Bing Li
class VideoPieceNotificationThreadCreator implements NotificationThreadCreatable<VideoPieceNotification, VideoPieceNotificationThread>
{

	@Override
	public VideoPieceNotificationThread createNotificationThreadInstance(int taskSize)
	{
		return new VideoPieceNotificationThread(taskSize);
	}

}
