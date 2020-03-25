package org.greatfree.concurrency.reactive;

// Created: 01/21/2019, Bing Li
public abstract class NotificationTaskQueue<Notification> extends NotificationObjectQueue<Notification>
{
	private NotificationTask<Notification> task;

	public NotificationTaskQueue(int taskSize, NotificationTask<Notification> task)
	{
		super(taskSize);
		this.task = task;
	}

	protected void processNotification(Notification notification)
	{
		this.task.processNotification(notification);
	}
}
