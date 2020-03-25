package org.greatfree.concurrency;

import org.greatfree.concurrency.reactive.NotificationObjectQueue;

// Created: 09/09/2018, Bing Li
class AsyncActor<Notification> extends NotificationObjectQueue<Notification>
{
	private Async<Notification> actor;
	private long waitTime;

	public AsyncActor(int taskSize, long waitTime, Async<Notification> actor)
	{
		super(taskSize);
		this.waitTime = waitTime;
		this.actor = actor;
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
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				try
				{
					notification = this.getNotification();
					this.actor.perform(notification);
					this.disposeObject(notification);
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
			try
			{
				this.holdOn(this.waitTime);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
		
	}

//	public abstract void perform(Notification notification);

}
