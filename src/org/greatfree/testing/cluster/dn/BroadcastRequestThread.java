package org.greatfree.testing.cluster.dn;

import java.io.IOException;

import org.greatfree.client.FreeClientPool;
import org.greatfree.client.IPResource;
import org.greatfree.concurrency.reactive.BoundRequestQueue;
import org.greatfree.data.Constants;
import org.greatfree.data.ServerConfig;
import org.greatfree.message.MulticastMessageDisposer;
import org.greatfree.testing.message.DNBroadcastRequest;
import org.greatfree.testing.message.DNBroadcastResponse;
import org.greatfree.util.Tools;

/*
 * The thread deals with the request and responds to the broadcast request initiator that sends the request. Since the request that contains the message must be broadcast to the local node's children, the disposal of the request must be controlled by the binder. 11/29/2014, Bing Li
 */

// Created: 11/25/2016, Bing Li
public class BroadcastRequestThread extends BoundRequestQueue<DNBroadcastRequest, DNBroadcastResponse, MulticastMessageDisposer<DNBroadcastRequest>>
{
	/*
	 * Initialize the thread. The binder is assigned at this point. 11/29/2014, Bing Li
	 */
	public BroadcastRequestThread(IPResource ipPort, FreeClientPool pool, int taskSize, String dispatcherKey, MulticastMessageDisposer<DNBroadcastRequest> reqBinder)
	{
		super(ipPort, pool, taskSize, dispatcherKey, reqBinder);
	}

	/*
	 * Deal with the request and respond to the broadcast request initiator concurrently. After that, it must notify the binder that its task is accomplished. 11/29/2014, Bing Li
	 */
	public void run()
	{
		// The instance of the received broadcast request. 11/29/2014, Bing Li
		DNBroadcastRequest request;
		// The instance of the response to be responded to the broadcast request initiator. 11/29/2014, Bing Li
		DNBroadcastResponse response;
		// The thread always runs until it is shutdown by the BoundBroadcastRequestDispatcher. 11/29/2014, Bing Li
		while (!this.isShutdown())
		{
			// Check whether the request queue is empty. 11/29/2014, Bing Li
			while (!this.isEmpty())
			{
				try
				{
					// Dequeue the request. 11/29/2014, Bing Li
					request = this.getRequest();

					System.out.println("BroadcastRequestThread: request = " + request.getRequest());
					
					// Create the response. The unique key is required so that the initiator is able to estimate the count of responses. That is different from the anycast response, which does not need to have the key. 11/29/2014, Bing Li
					response = new DNBroadcastResponse(Constants.BROADCAST_RESPONSE, Tools.generateUniqueKey(), request.getCollaboratorKey());
					
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
