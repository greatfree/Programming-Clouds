package org.greatfree.concurrency.reactive;

// Created: 01/21/2019, Bing Li
public interface NotificationTask<Notification>
{
	public void processNotification(Notification notification);
}
