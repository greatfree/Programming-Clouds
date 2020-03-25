package org.greatfree.dip.multicast.bound.child;

import java.io.IOException;

import org.greatfree.concurrency.reactive.BoundRequestQueue;
import org.greatfree.data.Constants;
import org.greatfree.data.ServerConfig;
import org.greatfree.dip.multicast.HelloWorld;
import org.greatfree.dip.multicast.message.HelloWorldBroadcastResponse;
import org.greatfree.dip.multicast.message.MessageDisposer;
import org.greatfree.dip.multicast.message.OldHelloWorldBroadcastRequest;

// Created: 08/26/2018, Bing Li
public class HelloWorldBroadcastRequestThread extends BoundRequestQueue<OldHelloWorldBroadcastRequest, HelloWorldBroadcastResponse, MessageDisposer<OldHelloWorldBroadcastRequest>>
{

	public HelloWorldBroadcastRequestThread(int taskSize, String dispatcherKey, MessageDisposer<OldHelloWorldBroadcastRequest> reqBinder)
	{
		super(taskSize, dispatcherKey, reqBinder);
	}

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
					ChildPeer.CHILD().notifyRoot(response);
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
