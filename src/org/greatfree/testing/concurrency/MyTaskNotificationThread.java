package org.greatfree.testing.concurrency;

import org.greatfree.concurrency.reactive.NotificationTask;
import org.greatfree.concurrency.reactive.NotificationTaskQueue;
import org.greatfree.data.ServerConfig;

// Created: 01/22/2019, Bing Li
class MyTaskNotificationThread extends NotificationTaskQueue<MyTaskNotification>
{
	public MyTaskNotificationThread(int taskSize, NotificationTask<MyTaskNotification> task)
	{
		super(taskSize, task);
	}

	@Override
	public void run()
	{
		MyTaskNotification notification;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				try
				{
					notification = this.getNotification();
					super.processNotification(notification);
					this.disposeObject(notification);
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
			try
			{
				this.holdOn(ServerConfig.NOTIFICATION_THREAD_WAIT_TIME);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}
}
