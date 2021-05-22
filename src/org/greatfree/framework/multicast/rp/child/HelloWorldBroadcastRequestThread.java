package org.greatfree.framework.multicast.rp.child;

import java.io.IOException;

import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.data.Constants;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.framework.multicast.HelloWorld;
import org.greatfree.framework.multicast.message.HelloWorldBroadcastResponse;
import org.greatfree.framework.multicast.rp.message.HelloWorldBroadcastRequest;

// Created: 10/22/2018, Bing Li
public class HelloWorldBroadcastRequestThread extends NotificationQueue<HelloWorldBroadcastRequest>
{

	public HelloWorldBroadcastRequestThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		// The instance of the received broadcast request. 11/29/2014, Bing Li
		HelloWorldBroadcastRequest request;
		// The instance of the response to be responded to the broadcast request initiator. 11/29/2014, Bing Li
		HelloWorldBroadcastResponse response;
//		RPHelloWorldBroadcastResponse rpResponse;
//		List<MulticastResponse> responses;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				try
				{
					request = this.dequeue();

					// Blocking solution ......
					// Print the query on the screen. 06/17/2017, Bing Li
					System.out.println("Query: " + request.getHelloWorld().getHelloWorld());
					response = new HelloWorldBroadcastResponse(new HelloWorld(Constants.BROADCAST_RESPONSE + request.getHelloWorld().getHelloWorld() + " id: " + request.getKey()), request.getCollaboratorKey());
					/*
					responses = new ArrayList<MulticastResponse>();
					responses.add(response);
					rpResponse = new RPHelloWorldBroadcastResponse(request.getCollaboratorKey(), responses);
					*/
					
					// Respond the RP of the broadcast requesting. 11/29/2014, Bing Li
//					ChildPeer.CHILD().notifyRP(request.getRPAddress(), response);
					ChildMulticastor.CHILD().getRP().saveResponse(response);
					
					System.out.println("HelloWorldBroadcastRequestThread-RP IPAddress = " + request.getRPAddress());
					
					// Since each child in the broadcast tree is potentially a rendezvous point and it needs to respond its parent with its own response and the responses from its children, the reading from children should be done after its own response is saved. The below reading is not a purely asynchronous operation. So it cannot be placed in front of the method of saveResponse(...) as what has done in the root-based-RP-multicasting. Only forwarding the request is done asynchronously. The reading is blocking or waiting. 10/22/2018, Bing Li
					ChildMulticastor.CHILD().asyncRead(request, ChildPeer.CHILD().getLocalIP());
					this.disposeMessage(request);
					this.dispose(response);
				}
				catch (InterruptedException | IOException | InstantiationException | IllegalAccessException | DistributedNodeFailedException e)
				{
					e.printStackTrace();
				}
			}
			try
			{
				this.holdOn(ServerConfig.NOTIFICATION_THREAD_WAIT_TIME);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}

}
