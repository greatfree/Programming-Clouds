package org.greatfree.framework.multicast.bound.child;

import org.greatfree.concurrency.reactive.BoundNotificationQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.framework.multicast.message.MessageDisposer;
import org.greatfree.framework.multicast.message.OldHelloWorldBroadcastRequest;

// Created: 08/26/2018, Bing Li
public class BroadcastHelloWorldRequestThread extends BoundNotificationQueue<OldHelloWorldBroadcastRequest, MessageDisposer<OldHelloWorldBroadcastRequest>>
{

	public BroadcastHelloWorldRequestThread(int taskSize, String dispatcherKey, MessageDisposer<OldHelloWorldBroadcastRequest> binder)
	{
		super(taskSize, dispatcherKey, binder);
	}

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
					/*
					 * All the code in the class is out of date. Since the message is upgraded, the code here is abandoned. 12/15/2018, Bing Li
					 */
//					ChildMulticastor.CHILD().read(request);
					// Notify the binder that the thread's task on the request has done. 11/29/2014, Bing Li
					this.bind(super.getDispatcherKey(), request);
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
