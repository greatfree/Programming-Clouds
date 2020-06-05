package ca.streaming.news.subscriber;

import org.greatfree.concurrency.reactive.NotificationThreadCreatable;

import ca.streaming.news.message.VideoPieceNotification;

// Created: 04/03/2020, Bing Li
class VideoPieceNotificationThreadCreator implements NotificationThreadCreatable<VideoPieceNotification, VideoPieceNotificationThread>
{

	@Override
	public VideoPieceNotificationThread createNotificationThreadInstance(int taskSize)
	{
		return new VideoPieceNotificationThread(taskSize);
	}

}
