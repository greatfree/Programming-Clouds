package com.greatfree.testing.memory;

import com.greatfree.concurrency.BoundNotificationQueue;
import com.greatfree.reuse.MulticastMessageDisposer;
import com.greatfree.testing.data.ServerConfig;
import com.greatfree.testing.message.SearchKeywordBroadcastRequest;

/*
 * The thread broadcasts the instance of SearchKeywordBroadcastRequest to the local node's children. 11/29/2014, Bing Li
 */

// Created: 11/29/2014, Bing Li
public class BroadcastSearchKeywordRequestThread extends BoundNotificationQueue<SearchKeywordBroadcastRequest, MulticastMessageDisposer<SearchKeywordBroadcastRequest>>
{
	/*
	 * Initialize the bound thread. 11/29/2014, Bing Li
	 */
	public BroadcastSearchKeywordRequestThread(int taskSize, String dispatcherKey, MulticastMessageDisposer<SearchKeywordBroadcastRequest> binder)
	{
		super(taskSize, dispatcherKey, binder);
	}

	/*
	 * Broadcast the request concurrently. 11/29/2014, Bing Li
	 */
	public void run()
	{
		// The instance of the received broadcast request. 11/29/2014, Bing Li
		SearchKeywordBroadcastRequest request;
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
					MemoryMulticastor.STORE().disseminateSearchKeywordRequestAmongSubMemServers(request);
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
