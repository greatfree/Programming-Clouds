package org.greatfree.framework.multicast.rp.root;

import java.io.IOException;

import org.greatfree.concurrency.reactive.RequestQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.framework.multicast.message.ClientHelloWorldBroadcastRequest;
import org.greatfree.framework.multicast.message.ClientHelloWorldBroadcastResponse;
import org.greatfree.framework.multicast.message.ClientHelloWorldBroadcastStream;
import org.greatfree.framework.multicast.message.HelloWorldBroadcastResponse;
import org.greatfree.framework.multicast.rp.message.HelloWorldBroadcastRequest;
import org.greatfree.util.Tools;

// Created: 10/21/2018, Bing Li
public class ClientHelloWorldBroadcastRequestThread extends RequestQueue<ClientHelloWorldBroadcastRequest, ClientHelloWorldBroadcastStream, ClientHelloWorldBroadcastResponse>
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
				request = this.dequeue();
				try
				{
//					response = new ClientHelloWorldBroadcastResponse(Tools.filter(RootMulticastor.ROOT().broadcastRead(new HelloWorldBroadcastRequest(Tools.getLocalIP(ChatConfig.CHAT_SERVER_PORT), request.getMessage().getHello())), HelloWorldBroadcastResponse.class));
					response = new ClientHelloWorldBroadcastResponse(Tools.filter(RootMulticastor.ROOT().broadcastRead(new HelloWorldBroadcastRequest(RootPeer.ROOT().getLocalIP(), request.getMessage().getHello())), HelloWorldBroadcastResponse.class));
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
