package org.greatfree.server;

import java.io.IOException;

import org.greatfree.client.ClientPoolSingleton;
import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.message.InitReadFeedbackNotification;
import org.greatfree.message.InitReadNotification;

/*
 * This is an important thread since it ensure the local ObjectOutputStream is initialized and it notifies to the relevant remote ObjectInputStream can be initialized. 11/09/2014, Bing Li
 */

// Created: 11/09/2014, Bing Li
public class InitReadFeedbackThread extends NotificationQueue<InitReadNotification>
{
	/*
	 * Initialize the thread. The argument, taskSize, is used to limit the count of tasks to be queued. 11/09/2014, Bing Li
	 */
	public InitReadFeedbackThread(int taskSize)
	{
		super(taskSize);
	}
	
	/*
	 * Once if a node key notification is received, it is processed concurrently as follows. 11/09/2014, Bing Li
	 */
	public void run()
	{
		// Declare an instance of InitReadNotification. 11/09/2014, Bing Li
		InitReadNotification notification;
		// The thread always runs until it is shutdown by the NotificationDispatcher. 11/09/2014, Bing Li
		while (!this.isShutdown())
		{
			// Check whether the notification queue is empty. 11/09/2014, Bing Li
			while (!this.isEmpty())
			{
				try
				{
					// Dequeue the notification. 11/09/2014, Bing Li
					notification = this.dequeue();
					try
					{
						// Send the instance of InitReadFeedbackNotification to the client which needs to initialize the ObjectInputStream of an instance of FreeClient. 11/09/2014, Bing Li
						ClientPoolSingleton.SERVER().getPool().send(notification.getClientKey(), new InitReadFeedbackNotification());
						this.disposeMessage(notification);
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
			try
			{
				// Wait for a moment after all of the existing notifications are processed. 11/09/2014, Bing Li
				this.holdOn(ServerConfig.NOTIFICATION_THREAD_WAIT_TIME);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}
}
