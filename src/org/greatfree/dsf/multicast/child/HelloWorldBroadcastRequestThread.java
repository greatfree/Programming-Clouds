package org.greatfree.dsf.multicast.child;

import java.io.IOException;

import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.data.Constants;
import org.greatfree.data.ServerConfig;
import org.greatfree.dsf.multicast.HelloWorld;
import org.greatfree.dsf.multicast.message.HelloWorldBroadcastRequest;
import org.greatfree.dsf.multicast.message.HelloWorldBroadcastResponse;

// Created: 09/10/2018, Bing Li
class HelloWorldBroadcastRequestThread extends NotificationQueue<HelloWorldBroadcastRequest>
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
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				try
				{
					request = this.getNotification();
					ChildMulticastor.CHILD().asyncRead(request);
//					ChildMulticastor.CHILD().read(request);

					// Blocking solution ......
					// Print the query on the screen. 06/17/2017, Bing Li
					System.out.println("Query: " + request.getHelloWorld().getHelloWorld());
					response = new HelloWorldBroadcastResponse(new HelloWorld(Constants.BROADCAST_RESPONSE + request.getHelloWorld().getHelloWorld() + " id: " + request.getKey()), request.getCollaboratorKey());
					// Respond the root of the broadcast requesting. 11/29/2014, Bing Li
					ChildPeer.CHILD().notifyRoot(response);
					this.disposeMessage(request);
					this.dispose(response);
				}
				catch (InterruptedException | IOException e)
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
