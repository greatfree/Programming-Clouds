package org.greatfree.framework.old.multicast.child;

import java.io.IOException;

import org.greatfree.concurrency.reactive.BoundRequestQueue;
import org.greatfree.data.Constants;
import org.greatfree.data.ServerConfig;
import org.greatfree.framework.multicast.HelloWorld;
import org.greatfree.framework.multicast.message.HelloWorldBroadcastResponse;
import org.greatfree.framework.multicast.message.MessageDisposer;
import org.greatfree.framework.multicast.message.OldHelloWorldBroadcastRequest;

/*
 * The thread deals with the request and responds to the broadcast request initiator that sends the request. Since the request that contains the message must be broadcast to the local node's children, the disposal of the request must be controlled by the binder. 11/29/2014, Bing Li
 */

// Created: 05/20/2017, Bing Li
class HelloWorldBroadcastRequestThread extends BoundRequestQueue<OldHelloWorldBroadcastRequest, HelloWorldBroadcastResponse, MessageDisposer<OldHelloWorldBroadcastRequest>>
{
	/*
	 * Initialize the thread. The binder is assigned at this point. 11/29/2014, Bing Li
	 */
	public HelloWorldBroadcastRequestThread(int taskSize, String dispatcherKey, MessageDisposer<OldHelloWorldBroadcastRequest> reqBinder)
	{
		super(taskSize, dispatcherKey, reqBinder);
	}

	/*
	 * Deal with the request and respond to the broadcast request initiator concurrently. After that, it must notify the binder that its task is accomplished. 11/29/2014, Bing Li
	 */
	@Override
	public void run()
	{
		// The instance of the received broadcast request. 11/29/2014, Bing Li
		OldHelloWorldBroadcastRequest request;
		// The instance of the response to be responded to the broadcast request initiator. 11/29/2014, Bing Li
		HelloWorldBroadcastResponse response;
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
				
					// Print the query on the screen. 06/17/2017, Bing Li
					System.out.println("Query: " + request.getHelloWorld().getHelloWorld());
					
					// Create the response. The unique key is required so that the initiator is able to estimate the count of responses. That is different from the anycast response, which does not need to have the key. 11/29/2014, Bing Li
//					response = new HelloWorldBroadcastResponse(new HelloWorld(Constants.BROADCAST_RESPONSE + request.getHelloWorld().getHelloWorld() + " id: " + request.getKey()), Tools.generateUniqueKey(), request.getCollaboratorKey());
					response = new HelloWorldBroadcastResponse(new HelloWorld(Constants.BROADCAST_RESPONSE + request.getHelloWorld().getHelloWorld() + " id: " + request.getKey()), request.getCollaboratorKey());
					// Respond the root of the broadcast requesting. 11/29/2014, Bing Li
//					this.respond(response);
					ClusterChildSingleton.CLUSTER().respondToRoot(response);
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
