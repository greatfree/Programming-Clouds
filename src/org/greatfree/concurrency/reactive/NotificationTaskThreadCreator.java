package org.greatfree.concurrency.reactive;

// Created: 01/21/2019, Bing Li
public interface NotificationTaskThreadCreator<Notification, NotificationThread extends NotificationTaskQueue<Notification>>
{
	public NotificationThread createThreadInstance(int taskSize, NotificationTask<Notification> task);
}
