package org.greatfree.dip.multicast.root;

import java.io.IOException;

import org.greatfree.concurrency.reactive.RequestQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.dip.multicast.message.ClientHelloWorldBroadcastRequest;
import org.greatfree.dip.multicast.message.ClientHelloWorldBroadcastResponse;
import org.greatfree.dip.multicast.message.ClientHelloWorldBroadcastStream;
import org.greatfree.dip.multicast.message.HelloWorldBroadcastRequest;
import org.greatfree.exceptions.DistributedNodeFailedException;

// Created: 08/26/2018, Bing Li
class ClientHelloWorldBroadcastRequestThread extends RequestQueue<ClientHelloWorldBroadcastRequest, ClientHelloWorldBroadcastStream, ClientHelloWorldBroadcastResponse>
{

	public ClientHelloWorldBroadcastRequestThread(int maxTaskSize)
	{
		super(maxTaskSize);
	}

	@Override
	public void run()
	{
		ClientHelloWorldBroadcastStream request;
		ClientHelloWorldBroadcastResponse response;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				request = this.getRequest();
				try
				{
//					response = new ClientHelloWorldBroadcastResponse(Tools.filter(RootClient.ROOT().broadcastRead(new HelloWorldBroadcastRequest(Tools.generateUniqueKey(), request.getMessage().getHello())), HelloWorldBroadcastResponse.class));
					
//					response = new ClientHelloWorldBroadcastResponse(Tools.filter(RootMulticastor.ROOT().broadcastRead(new HelloWorldBroadcastRequest(request.getMessage().getHello())), HelloWorldBroadcastResponse.class));
					response = new ClientHelloWorldBroadcastResponse(RootMulticastor.ROOT().broadRead(new HelloWorldBroadcastRequest(request.getMessage().getHello())));
					this.respond(request.getOutStream(), request.getLock(), response);
					this.disposeMessage(request, response);
				}
				catch (IOException | DistributedNodeFailedException e)
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
