package org.greatfree.dip.old.multicast.child;

import java.io.IOException;

import org.greatfree.concurrency.reactive.BoundNotificationQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.dip.multicast.MulticastConfig;
import org.greatfree.dip.multicast.message.MessageDisposer;
import org.greatfree.dip.multicast.message.OldHelloWorldBroadcastRequest;

/*
 * The thread broadcasts the instance of HelloWorldBroadcastRequest to the local node's children. 11/29/2014, Bing Li
 */

// Created: 05/20/2017, Bing Li
class BroadcastHelloWorldRequestThread extends BoundNotificationQueue<OldHelloWorldBroadcastRequest, MessageDisposer<OldHelloWorldBroadcastRequest>>
{
	/*
	 * Initialize the bound thread. 11/29/2014, Bing Li
	 */
	public BroadcastHelloWorldRequestThread(int taskSize, String dispatcherKey, MessageDisposer<OldHelloWorldBroadcastRequest> binder)
	{
		super(taskSize, dispatcherKey, binder);
	}

	/*
	 * Broadcast the request concurrently. 11/29/2014, Bing Li
	 */
	@Override
	public void run()
	{
		// The instance of the received broadcast request. 11/29/2014, Bing Li
		OldHelloWorldBroadcastRequest request;
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
					ClusterChildSingleton.CLUSTER().broadcastRead(request, MulticastConfig.SUB_BRANCH_COUNT);
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
