package edu.greatfree.notification.server;

import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.data.ServerConfig;

import edu.greatfree.notification.message.MyNotification;

class MyNotificationThread extends NotificationQueue<MyNotification>
{
	public MyNotificationThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		MyNotification notification;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				try
				{
					notification = this.getNotification();
					
					// When new messages are available, they are displayed. 06/21/2018, Bing Li
					System.out.println(notification.getBook().getAuthor() + ", " + notification.getBook().getTitle() + ", " + notification.getMessage());
					this.disposeMessage(notification);
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
			try
			{
				// Wait for a moment after all of the existing notifications are processed. 01/20/2016, Bing Li
				this.holdOn(ServerConfig.NOTIFICATION_THREAD_WAIT_TIME);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}

}
