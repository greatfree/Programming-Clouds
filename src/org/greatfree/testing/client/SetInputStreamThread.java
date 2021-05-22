package org.greatfree.testing.client;

import org.greatfree.client.RemoteReader;
import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.message.InitReadFeedbackNotification;

/*
 * The notification thread intends to set up the ObjectInputStream of a FreeClient instance after receiving the relevant feedback notification from the remote server. 11/07/2014, Bing Li
 */

// Created: 11/07/2014, Bing Li
public class SetInputStreamThread extends NotificationQueue<InitReadFeedbackNotification>
{
	// Initialize. 11/07/2014, Bing Li
	public SetInputStreamThread(int taskSize)
	{
		super(taskSize);
	}

	/*
	 * The thread to process notifications asynchronously. 11/07/2014, Bing Li
	 */
	public void run()
	{
		// Declare a notification instance. 11/07/2014, Bing Li
		InitReadFeedbackNotification notification;
		// The thread always runs until it is shutdown by the NotificationDispatcher. 11/07/2014, Bing Li
		while (!this.isShutdown())
		{
			// Check whether the notification queue is empty. 11/07/2014, Bing Li
			while (!this.isEmpty())
			{
				try
				{
					// Dequeue the notification. 11/07/2014, Bing Li
					notification = this.dequeue();
					// Notify the instance of FreeClient that it is time to initialize the ObjectInputStream. 11/07/2014, Bing Li
					RemoteReader.REMOTE().notifyOutStreamDone();
					// Dispose the notification. 11/07/2014, Bing Li
					this.disposeMessage(notification);
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
			try
			{
				// Wait for a moment after all of the existing notifications are processed. 11/07/2014, Bing Li
				this.holdOn(ServerConfig.NOTIFICATION_THREAD_WAIT_TIME);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}
}
