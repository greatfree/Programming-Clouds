package org.greatfree.testing.coordinator.searching;

import java.io.IOException;

import org.greatfree.concurrency.reactive.RequestQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.testing.message.SearchKeywordRequest;
import org.greatfree.testing.message.SearchKeywordResponse;
import org.greatfree.testing.message.SearchKeywordStream;

/*
 * This is an example to use RequestQueue, which receives users' requests and responds concurrently. 11/29/2014, Bing Li
 */

// Created: 11/29/2014, Bing Li
public class SearchKeywordThread extends RequestQueue<SearchKeywordRequest, SearchKeywordStream, SearchKeywordResponse>
{
	/*
	 * Initialize the thread. The value of maxTaskSize is the length of the queue to take the count of requests. 11/29/2014, Bing Li
	 */
	public SearchKeywordThread(int maxTaskSize)
	{
		super(maxTaskSize);
	}

	/*
	 * Respond users' requests concurrently. 11/29/2014, Bing Li
	 */
	public void run()
	{
		// Declare the request stream. 11/29/2014, Bing Li
		SearchKeywordStream request;
		// Declare the response. 11/29/2014, Bing Li
		SearchKeywordResponse response;
		// The thread is shutdown when it is idle long enough. Before that, the thread keeps alive. It is necessary to detect whether it is time to end the task. 11/29/2014, Bing Li
		while (!this.isShutdown())
		{
			// The loop detects whether the queue is empty or not. 11/29/2014, Bing Li
			while (!this.isEmpty())
			{
				// Dequeue a request. 11/29/2014, Bing Li
				request = this.dequeue();
				// Invoke the multicastor reader to retrieve the data in the cluster of the memory nodes in an anycast manner. 11/29/2014, Bing Li
				response = new SearchKeywordResponse(request.getMessage().getKeyword(), CoordinatorMulticastReader.COORDINATE().searchKeyword(request.getMessage().getKeyword()));
				try
				{
					// Respond to the client. 11/29/2014, Bing Li
					this.respond(request.getOutStream(), request.getLock(), response);
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
				// Dispose the request and the response. 11/29/2014, Bing Li
				this.disposeMessage(request, response);
			}
			try
			{
				// Wait for some time when the queue is empty. During the period and before the thread is killed, some new requests might be received. If so, the thread can keep working. 11/29/2014, Bing Li
				this.holdOn(ServerConfig.REQUEST_THREAD_WAIT_TIME);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}
}
