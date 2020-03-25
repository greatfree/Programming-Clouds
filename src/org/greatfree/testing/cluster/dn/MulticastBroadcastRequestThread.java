package org.greatfree.testing.cluster.dn;

import java.io.IOException;

import org.greatfree.concurrency.reactive.BoundNotificationQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.message.MulticastMessageDisposer;
import org.greatfree.testing.message.DNBroadcastRequest;

/*
 * The thread broadcasts the instance of DNBroadcastRequest to the local node's children. 11/29/2014, Bing Li
 */

// Created: 11/25/2016, Bing Li
public class MulticastBroadcastRequestThread extends BoundNotificationQueue<DNBroadcastRequest, MulticastMessageDisposer<DNBroadcastRequest>>
{
	/*
	 * Initialize the bound thread. 11/29/2014, Bing Li
	 */
	public MulticastBroadcastRequestThread(int taskSize, String dispatcherKey, MulticastMessageDisposer<DNBroadcastRequest> binder)
	{
		super(taskSize, dispatcherKey, binder);
	}

	/*
	 * Broadcast the request concurrently. 11/29/2014, Bing Li
	 */
	public void run()
	{
		// The instance of the received broadcast request. 11/29/2014, Bing Li
		DNBroadcastRequest request;
		// The thread always runs until it is shutdown by the BoundNotificationDispatcher. 11/29/2014, Bing Li
		while (!this.isShutdown())
		{		
			// Check whether the notification queue is empty. 11/29/2014, Bing Li
			while (!this.isEmpty())
			{
				try
				{
					// Dequeue the request. 11/29/2014, Bing Li
					request = this.getNotification();
					// Disseminate the broadcast request to the local node's children. 11/29/2014, Bing Li
					DNMulticastor.CLUSTER().disseminateBroadcastRequest(request);
					// Notify the binder that the thread's task on the request has done. 11/29/2014, Bing Li
					this.bind(super.getDispatcherKey(), request);
				}
				catch (InterruptedException | InstantiationException | IllegalAccessException | IOException e)
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
