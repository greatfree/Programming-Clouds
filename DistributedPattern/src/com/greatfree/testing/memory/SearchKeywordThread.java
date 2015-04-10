package com.greatfree.testing.memory;

import java.io.IOException;
import java.util.Set;

import com.greatfree.concurrency.BoundBroadcastRequestQueue;
import com.greatfree.remote.FreeClientPool;
import com.greatfree.remote.IPPort;
import com.greatfree.reuse.MulticastMessageDisposer;
import com.greatfree.testing.data.ServerConfig;
import com.greatfree.testing.message.SearchKeywordBroadcastRequest;
import com.greatfree.testing.message.SearchKeywordBroadcastResponse;
import com.greatfree.util.Tools;

/*
 * The thread retrieves links by the keyword. Since the request that contains the keyword must be broadcast to the local node's children, the disposal of the request must be controlled by the binder. 11/29/2014, Bing Li
 */

// Created: 11/29/2014, Bing Li
public class SearchKeywordThread extends BoundBroadcastRequestQueue<SearchKeywordBroadcastRequest, SearchKeywordBroadcastResponse, MulticastMessageDisposer<SearchKeywordBroadcastRequest>>
{
	/*
	 * Initialize the thread. The binder is assigned at this point. 11/29/2014, Bing Li
	 */
	public SearchKeywordThread(IPPort ipPort, FreeClientPool pool, int taskSize, String dispatcherKey, MulticastMessageDisposer<SearchKeywordBroadcastRequest> reqBinder)
	{
		super(ipPort, pool, taskSize, dispatcherKey, reqBinder);
	}

	/*
	 * Retrieve links concurrently. After that, it must notify the binder that its task is accomplished. 11/29/2014, Bing Li
	 */
	public void run()
	{
		// The instance of the received broadcast request. 11/29/2014, Bing Li
		SearchKeywordBroadcastRequest request;
		// The instance of the response to be responded to the broadcast request initiator. 11/29/2014, Bing Li
		SearchKeywordBroadcastResponse response;
		// The set to take the results. 11/29/2014, Bing Li
		Set<String> links;
		// The thread always runs until it is shutdown by the BoundBroadcastRequestDispatcher. 11/29/2014, Bing Li
		while (!this.isShutdown())
		{
			// Check whether the notification queue is empty. 11/29/2014, Bing Li
			while (!this.isEmpty())
			{
				try
				{
					// Dequeue the request. 11/29/2014, Bing Li
					request = this.getRequest();
					// Retrieve the links. 11/29/2014, Bing Li
					links = LinkPond.STORE().getLinksByKeyword(request.getKeyword());
					// Form the response. The unique key is required so that the initiator is able to estimate the count of responses. That is different from the anycast response, which does not need to have the key. 11/29/2014, Bing Li
					response = new SearchKeywordBroadcastResponse(links, Tools.generateUniqueKey(), request.getCollaboratorKey());
					try
					{
						// Respond the initiator of the broadcast requesting. 11/29/2014, Bing Li
						this.respond(response);
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
					// Notify the binder that the thread task on the request has done. 11/29/2014, Bing Li
					this.bind(super.getDispatcherKey(), request);
					// Dispose the response. 11/29/2014, Bing Li
					this.disposeResponse(response);
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
