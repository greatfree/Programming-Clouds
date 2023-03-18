package org.greatfree.framework.p2p.registry;

import java.io.IOException;

import org.greatfree.concurrency.reactive.RequestQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.framework.p2p.message.ChatRegistryRequest;
import org.greatfree.framework.p2p.message.ChatRegistryResponse;
import org.greatfree.framework.p2p.message.ChatRegistryStream;

/*
 * The thread registers the chatting account concurrently. 04/30/2017, Bing Li
 */

// Created: 04/30/2017, Bing Li
class ChatRegistryThread extends RequestQueue<ChatRegistryRequest, ChatRegistryStream, ChatRegistryResponse>
{

	public ChatRegistryThread(int maxTaskSize)
	{
		super(maxTaskSize);
	}

	@Override
	public void run()
	{
		ChatRegistryStream request;
		ChatRegistryResponse response;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				request = this.dequeue();
				
				System.out.println("ChatRegistryThread: peerID = " + request.getMessage().getPeerID() + "; peerName = " + request.getMessage().getPeerName());

				// Register the chatting peer. 05/01/2017, Bing Li
				AccountRegistry.APP().add(new PeerChatAccount(request.getMessage().getPeerID(), request.getMessage().getPeerName(), request.getMessage().getPeerDescription(), request.getMessage().getPreference()));
				
				response = new ChatRegistryResponse(true);
				try
				{
					this.respond(request.getOutStream(), request.getLock(), response);
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
				this.disposeMessage(request, response);
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
