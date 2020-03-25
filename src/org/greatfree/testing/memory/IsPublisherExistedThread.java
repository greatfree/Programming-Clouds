package org.greatfree.testing.memory;

import java.io.IOException;

import org.greatfree.client.FreeClientPool;
import org.greatfree.client.IPResource;
import org.greatfree.concurrency.reactive.AnycastRequestQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.testing.message.IsPublisherExistedAnycastRequest;
import org.greatfree.testing.message.IsPublisherExistedAnycastResponse;

/*
 * The thread is responsible for processing the anycast requests in a concurrent way. 11/29/2014, Bing Li
 */

// Created: 11/29/2014, Bing Li
public class IsPublisherExistedThread extends AnycastRequestQueue<IsPublisherExistedAnycastRequest, IsPublisherExistedAnycastResponse>
{
	/*
	 * Initialize the thread. 11/29/2014, Bing Li
	 */
	public IsPublisherExistedThread(IPResource ipPort, FreeClientPool pool, int taskSize)
	{
		super(ipPort, pool, taskSize);
	}

	/*
	 * Process the anycast concurrently. 11/29/2014, Bing Li
	 */
	public void run()
	{
		// The instance of the received anycast request. 11/29/2014, Bing Li
		IsPublisherExistedAnycastRequest request;
		// The retrieval result. 11/29/2014, Bing Li
		boolean isExisted;
		// The thread always runs until it is shutdown by the AnycastRequestDispatcher. 11/29/2014, Bing Li
		while (!this.isShutdown())
		{
			// Check whether the notification queue is empty. 11/29/2014, Bing Li
			while (!this.isEmpty())
			{
				try
				{
					// Dequeue the request. 11/29/2014, Bing Li
					request = this.getRequest();
					// Retrieve whether the publisher exists. 11/29/2014, Bing Li
					isExisted = LinkPond.STORE().isPublisherExisted(request.getURL());
					// Check whether the publisher exists. 11/29/2014, Bing Li
					if (isExisted)
					{
						try
						{
							// If the publisher exists, it is required to send the response back to the initiator immediately. Then, the anycast requesting process can be terminated. 11/29/2014, Bing Li
							this.respond(new IsPublisherExistedAnycastResponse(isExisted, request.getCollaboratorKey()));
						}
						catch (IOException e)
						{
							e.printStackTrace();
						}
					}
					else
					{
						// If the publisher does not exist, it is required to forward the request to its children for further retrieval. 11/29/2014, Bing Li
						MemoryMulticastor.STORE().disseminateIsPublisherExistedRequestAmongSubMemServers(request);
					}
					// Dispose the request. 11/29/2014, Bing Li
					this.disposeMessage(request);
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
			try
			{
				// Wait for a moment after all of the existing requests are processed. 11/29/2014, Bing Li
				this.holdOn(ServerConfig.REQUEST_THREAD_WAIT_TIME);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}
}
