package org.greatfree.testing.server;

import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.testing.message.TestNotification;

// Created: 12/10/2016, Bing Li
public class TestNotificationThread extends NotificationQueue<TestNotification> 
{
	public TestNotificationThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		TestNotification notification;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				try
				{
					notification = this.getNotification();

					// Process the received notification here. 12/10/2016, Bing Li
					System.out.println(notification.getMessage());
					
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

	/*
	 * The method is invoked the NotificationDispatcher to create a new instance when necessary. 05/19/2018, Bing Li
	 */
	/*
	@Override
	public TestNotificationThread createNotificationThreadInstance()
	{
		return new TestNotificationThread(this.getTaskSize());
	}
	*/

}
