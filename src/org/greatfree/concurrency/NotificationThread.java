package org.greatfree.concurrency;

import org.greatfree.concurrency.reactive.NotificationObjectQueue;

// Created: 09/09/2018, Bing Li
final class NotificationThread<Notification> extends NotificationObjectQueue<Notification>
{
	private Notifier<Notification> notifier;
	private long waitTime;

	public NotificationThread(int queueSize, long waitTime, Notifier<Notification> notifier)
	{
		super(queueSize);
		this.waitTime = waitTime;
		this.notifier = notifier;
	}

	/*
	public long getWaitTime()
	{
		return this.waitTime;
	}
	*/

	@Override
	public void run()
	{
		Notification notification;
		while (!super.isShutdown())
		{
			while (!super.isEmpty())
			{
				try
				{
					notification = super.dequeue();
					this.notifier.notify(notification);
					super.disposeObject(notification);
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
			try
			{
				super.holdOn(this.waitTime);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}

//	public abstract void perform(Notification notification);

}
