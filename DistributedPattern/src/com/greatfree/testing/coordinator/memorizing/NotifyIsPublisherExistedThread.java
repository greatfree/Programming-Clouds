package com.greatfree.testing.coordinator.memorizing;

import com.greatfree.concurrency.NotificationQueue;
import com.greatfree.testing.coordinator.searching.CoordinatorMulticastReader;
import com.greatfree.testing.data.ServerConfig;
import com.greatfree.testing.message.IsPublisherExistedAnycastResponse;

/*
 * The thread notifies the coordinator multicast reader that an anycast response is received. Then, it determines if the anycast requesting process is terminated or not. 11/29/2014, Bing Li
 * 
 * Usually, the notifying takes very short time. So it is not necessary to use the structure. 11/29/2014, Bing Li
 */

// Created: 11/29/2014, Bing Li
public class NotifyIsPublisherExistedThread extends NotificationQueue<IsPublisherExistedAnycastResponse>
{
	/*
	 * Initialize the thread. 11/28/2014, Bing Li
	 */
	public NotifyIsPublisherExistedThread(int taskSize)
	{
		super(taskSize);
	}
	
	/*
	 * Process the notification concurrently. 11/28/2014, Bing Li
	 */
	public void run()
	{
		// The instance of response. 11/28/2014, Bing Li
		IsPublisherExistedAnycastResponse response;
		// The thread always runs until it is shutdown by the NotificationDispatcher. 11/28/2014, Bing Li
		while (!this.isShutdown())
		{
			// Check whether the notification queue is empty. 11/28/2014, Bing Li
			while (!this.isEmpty())
			{
				try
				{
					// Dequeue the response. 11/29/2014, Bing Li
					response = this.getNotification();
					// Notify the coordinator multicast reader. 11/29/2014, Bing Li
					CoordinatorMulticastReader.COORDINATE().notifyResponseReceived(response);
					// Dispose the response. 11/29/2014, Bing Li
					this.disposeMessage(response);
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
			try
			{
				// Wait for a moment after all of the existing requests are processed. 11/29/2014, Bing Li
				this.holdOn(ServerConfig.NOTIFICATION_THREAD_WAIT_TIME);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}
}
