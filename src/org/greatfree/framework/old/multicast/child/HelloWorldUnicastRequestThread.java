package org.greatfree.framework.old.multicast.child;

import java.io.IOException;

import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.data.Constants;
import org.greatfree.data.ServerConfig;
import org.greatfree.framework.multicast.HelloWorld;
import org.greatfree.framework.multicast.message.HelloWorldUnicastRequest;
import org.greatfree.framework.multicast.message.HelloWorldUnicastResponse;

/*
 * Process the unicast request and reply to the root concurrently. 05/21/2017, Bing Li
 */

// Created: 05/21/2017, Bing Li
class HelloWorldUnicastRequestThread extends NotificationQueue<HelloWorldUnicastRequest>
{

	public HelloWorldUnicastRequestThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		HelloWorldUnicastRequest request;
		HelloWorldUnicastResponse response;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				try
				{
					// Dequeue the request. 11/29/2014, Bing Li
					request = this.dequeue();

					// Print the query on the screen. 06/17/2017, Bing Li
					System.out.println("Query: " + request.getHelloWorld().getHelloWorld());
					
					// Create the response. The unique key is required so that the initiator is able to estimate the count of responses. That is different from the anycast response, which does not need to have the key. 11/29/2014, Bing Li
//					response = new HelloWorldUnicastResponse(new HelloWorld(Constants.UNICAST_RESPONSE + request.getHelloWorld().getHelloWorld() + " id: " + request.getKey()), Tools.generateUniqueKey(), request.getCollaboratorKey());
					response = new HelloWorldUnicastResponse(new HelloWorld(Constants.UNICAST_RESPONSE + request.getHelloWorld().getHelloWorld() + " id: " + request.getKey()), request.getCollaboratorKey());

					// Respond the root of the unicast requesting. 11/29/2014, Bing Li
					ClusterChildSingleton.CLUSTER().respondToRoot(response);

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
