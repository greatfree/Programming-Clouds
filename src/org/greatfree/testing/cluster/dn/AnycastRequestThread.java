package org.greatfree.testing.cluster.dn;

import java.io.IOException;

import org.greatfree.client.FreeClientPool;
import org.greatfree.client.IPResource;
import org.greatfree.concurrency.reactive.BoundRequestQueue;
import org.greatfree.data.Constants;
import org.greatfree.data.ServerConfig;
import org.greatfree.message.MulticastMessageDisposer;
import org.greatfree.testing.message.DNAnycastRequest;
import org.greatfree.testing.message.DNAnycastResponse;
import org.greatfree.util.Tools;

/*
 * The thread deals with the request and responds to the anycast request initiator that sends the request. Since the request that contains the message must be anycast to the local node's children, the disposal of the request must be controlled by the binder. 11/29/2014, Bing Li
 */

// Created: 11/27/2016, Bing Li
public class AnycastRequestThread extends BoundRequestQueue<DNAnycastRequest, DNAnycastResponse, MulticastMessageDisposer<DNAnycastRequest>>
{
	/*
	 * Initialize the thread. The binder is assigned at this point. 11/29/2014, Bing Li
	 */
	public AnycastRequestThread(IPResource ipPort, FreeClientPool pool, int taskSize, String dispatcherKey, MulticastMessageDisposer<DNAnycastRequest> reqBinder)
	{
		super(ipPort, pool, taskSize, dispatcherKey, reqBinder);
	}

	/*
	 * Deal with the request and respond to the anycast request initiator concurrently. After that, it must notify the binder that its task is accomplished. 11/29/2014, Bing Li
	 */
	public void run()
	{
		// The instance of the received anycast request. 11/29/2014, Bing Li
		DNAnycastRequest request;
		// The instance of the response to be responded to the anycast request initiator. 11/29/2014, Bing Li
		DNAnycastResponse response;
		// The thread always runs until it is shutdown by the BoundBroadcastRequestDispatcher. 11/29/2014, Bing Li
		while (!this.isShutdown())
		{
			// Check whether the request queue is empty. 11/29/2014, Bing Li
			while (!this.isEmpty())
			{
				// Dequeue the request. 11/29/2014, Bing Li
				try
				{
					request = this.getRequest();
					
					System.out.println("AnycastRequestThread: request = " + request.getRequest());

					// Create the response. The unique key is required so that the initiator is able to estimate the count of responses. That is different from the anycast response, which does not need to have the key. 11/29/2014, Bing Li
					response = new DNAnycastResponse(Constants.ANYCAST_RESPONSE, Tools.generateUniqueKey(), request.getCollaboratorKey());
					// Respond the initiator of the broadcast requesting. 11/29/2014, Bing Li
					this.respond(response);
					// Notify the binder that the thread task on the request has done. 11/29/2014, Bing Li
					this.bind(super.getDispatcherKey(), request);
					// Dispose the response. 11/29/2014, Bing Li
					this.disposeResponse(response);
				}
				catch (InterruptedException | IOException e)
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
