package org.greatfree.testing.cluster.coordinator.client;

import java.io.IOException;

import org.greatfree.concurrency.reactive.RequestQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.testing.cluster.coordinator.CoordinatorMulticastReader;
import org.greatfree.testing.cluster.coordinator.dn.DNServerClientPool;
import org.greatfree.testing.message.ClientForUnicastRequest;
import org.greatfree.testing.message.ClientForUnicastResponse;
import org.greatfree.testing.message.ClientForUnicastStream;
import org.greatfree.util.Tools;
import org.greatfree.util.UtilConfig;

/*
 * The thread derives the RequestQueue. It receives the request of ClientForUnicastRequest and responds to users with the response of ClientForUnicastResponse. 02/15/2016, Bing Li
 */

// Created: 11/22/2016, Bing Li
public class ClientForUnicastRequestThread extends RequestQueue<ClientForUnicastRequest, ClientForUnicastStream, ClientForUnicastResponse>
{
	/*
	 * Initialize the thread of request queue. The value of maxTaskSize is the length of the queue to take the count of requests. 02/15/2016, Bing Li
	 */
	public ClientForUnicastRequestThread(int maxTaskSize)
	{
		super(maxTaskSize);
	}

	/*
	 * Respond users' requests concurrently. 02/15/2015, Bing Li
	 */
	public void run()
	{
		// Declare the request stream. 02/15/2015, Bing Li
		ClientForUnicastStream request;
		// Declare the response. 02/15/2014, Bing Li
		ClientForUnicastResponse response;
		// Declare the result from the cluster. 11/25/2016, Bing Li
		String result = UtilConfig.EMPTY_STRING;
		// The thread is shutdown when it is idle long enough or when the server is shut down. Before that, the thread keeps alive. It is necessary to detect whether it is time to end the task. 02/15/2014, Bing Li
		while (!this.isShutdown())
		{
			// The loop detects whether the queue is empty or not. 02/15/2016, Bing Li
			while (!this.isEmpty())
			{
				// Dequeue a request. 02/15/2016, Bing Li
				request = this.dequeue();

				// Unicasting request is performed here ... 11/25/2016, Bing Li
				result = CoordinatorMulticastReader.COORDINATE().unicastRequest(request.getMessage().getMessage(), Tools.getRandomSetElement(DNServerClientPool.COORDINATE().getPool().getClientKeys()));
				
				// Initialize an instance of ClientForUnicastResponse. 02/15/2016, Bing Li
				response = new ClientForUnicastResponse(result);

				try
				{
					// Respond the response to the remote client. 02/15/2016, Bing Li
					this.respond(request.getOutStream(), request.getLock(), response);
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
				// Dispose the messages after the responding is performed. 02/15/2016, Bing Li
				this.disposeMessage(request, response);
			}
		}
		try
		{
			// Wait for some time when the queue is empty. During the period and before the thread is killed, some new requests might be received. If so, the thread can keep working. 02/15/2016, Bing Li
			this.holdOn(ServerConfig.REQUEST_THREAD_WAIT_TIME);
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}
}
