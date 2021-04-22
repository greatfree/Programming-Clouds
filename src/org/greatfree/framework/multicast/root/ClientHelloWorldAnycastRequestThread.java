package org.greatfree.framework.multicast.root;

import java.io.IOException;

import org.greatfree.concurrency.reactive.RequestQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.framework.multicast.message.ClientHelloWorldAnycastRequest;
import org.greatfree.framework.multicast.message.ClientHelloWorldAnycastResponse;
import org.greatfree.framework.multicast.message.ClientHelloWorldAnycastStream;
import org.greatfree.framework.multicast.message.HelloWorldAnycastRequest;
import org.greatfree.util.UtilConfig;

// Created: 08/26/2018, Bing Li
class ClientHelloWorldAnycastRequestThread extends RequestQueue<ClientHelloWorldAnycastRequest, ClientHelloWorldAnycastStream, ClientHelloWorldAnycastResponse>
{

	public ClientHelloWorldAnycastRequestThread(int maxTaskSize)
	{
		super(maxTaskSize);
	}

	@Override
	public void run()
	{
		ClientHelloWorldAnycastStream request;
		ClientHelloWorldAnycastResponse response;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				request = this.getRequest();
				try
				{
//					response = new ClientHelloWorldAnycastResponse(Tools.filter(RootClient.ROOT().anycastRead(new HelloWorldAnycastRequest(Tools.generateUniqueKey(), request.getMessage().getHello()), UtilConfig.ONE), HelloWorldAnycastResponse.class));
//					response = new ClientHelloWorldAnycastResponse(Tools.filter(RootMulticastor.ROOT().anycastRead(new HelloWorldAnycastRequest(request.getMessage().getHello()), UtilConfig.ONE), HelloWorldAnycastResponse.class));
					response = new ClientHelloWorldAnycastResponse(RootMulticastor.ROOT().anyRead(new HelloWorldAnycastRequest(request.getMessage().getHello()), UtilConfig.ONE));
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
