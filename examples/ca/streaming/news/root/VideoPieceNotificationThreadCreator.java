package ca.streaming.news.root;

import org.greatfree.concurrency.reactive.NotificationThreadCreatable;

import ca.streaming.news.message.VideoPieceNotification;

// Crearted: 04/01/2020, Bing Li
class VideoPieceNotificationThreadCreator implements NotificationThreadCreatable<VideoPieceNotification, VideoPieceNotificationThread>
{

	@Override
	public VideoPieceNotificationThread createNotificationThreadInstance(int taskSize)
	{
		return new VideoPieceNotificationThread(taskSize);
	}

}
