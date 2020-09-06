package org.greatfree.dsf.multicast.root;

import java.io.IOException;

import org.greatfree.concurrency.reactive.RequestQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.dsf.multicast.message.ClientHelloWorldUnicastRequest;
import org.greatfree.dsf.multicast.message.ClientHelloWorldUnicastResponse;
import org.greatfree.dsf.multicast.message.ClientHelloWorldUnicastStream;
import org.greatfree.dsf.multicast.message.HelloWorldUnicastRequest;
import org.greatfree.exceptions.DistributedNodeFailedException;

// Created: 08/26/2018, Bing Li
class ClientHelloWorldUnicastRequestThread extends RequestQueue<ClientHelloWorldUnicastRequest, ClientHelloWorldUnicastStream, ClientHelloWorldUnicastResponse>
{

	public ClientHelloWorldUnicastRequestThread(int maxTaskSize)
	{
		super(maxTaskSize);
	}

	@Override
	public void run()
	{
		ClientHelloWorldUnicastStream request;
		ClientHelloWorldUnicastResponse response;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				request = this.getRequest();
				try
				{
//					response = new ClientHelloWorldUnicastResponse(Tools.filter(RootClient.ROOT().unicastRead(new HelloWorldUnicastRequest(Tools.generateUniqueKey(), request.getMessage().getHello())), HelloWorldUnicastResponse.class));
//					response = new ClientHelloWorldUnicastResponse(Tools.filter(RootMulticastor.ROOT().unicastRead(new HelloWorldUnicastRequest(request.getMessage().getHello())), HelloWorldUnicastResponse.class));
					response = new ClientHelloWorldUnicastResponse(RootMulticastor.ROOT().uniRead(new HelloWorldUnicastRequest(request.getMessage().getHello())));
					this.respond(request.getOutStream(), request.getLock(), response);
					this.disposeMessage(request, response);
				}
				catch (InstantiationException | IllegalAccessException | InterruptedException | IOException | DistributedNodeFailedException e)
				{
					e.printStackTrace();
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

}
