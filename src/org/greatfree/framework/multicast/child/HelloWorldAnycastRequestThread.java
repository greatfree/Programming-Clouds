package org.greatfree.framework.multicast.child;

import java.io.IOException;

import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.data.Constants;
import org.greatfree.data.ServerConfig;
import org.greatfree.framework.multicast.HelloWorld;
import org.greatfree.framework.multicast.message.HelloWorldAnycastRequest;
import org.greatfree.framework.multicast.message.HelloWorldAnycastResponse;

// Created: 08/26/2018, Bing Li
public class HelloWorldAnycastRequestThread extends NotificationQueue<HelloWorldAnycastRequest>
{

	public HelloWorldAnycastRequestThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		HelloWorldAnycastRequest request;
		HelloWorldAnycastResponse response;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				try
				{
					// Dequeue the request. 11/29/2014, Bing Li
					request = this.getNotification();

					// Print the query on the screen. 06/17/2017, Bing Li
					System.out.println("Query: " + request.getHelloWorld().getHelloWorld());

					// Create the response. The unique key is required so that the initiator is able to estimate the count of responses. That is different from the anycast response, which does not need to have the key. 11/29/2014, Bing Li
					response = new HelloWorldAnycastResponse(new HelloWorld(Constants.ANYCAST_RESPONSE + request.getHelloWorld().getHelloWorld() + " id: " + request.getKey()), request.getCollaboratorKey());
					
					// Respond the root of the anycast requesting. 11/29/2014, Bing Li
					ChildPeer.CHILD().notifyRoot(response);

					this.disposeMessage(request);
				}
				catch (InterruptedException | IOException e)
				{
					e.printStackTrace();
				}
			}
			try
			{
				// Wait for a moment after all of the existing notifications are processed. 01/20/2016, Bing Li
				this.holdOn(ServerConfig.NOTIFICATION_THREAD_WAIT_TIME);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
		
	}
}
