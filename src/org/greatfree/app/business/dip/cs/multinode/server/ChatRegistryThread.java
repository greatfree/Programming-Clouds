package org.greatfree.app.business.dip.cs.multinode.server;

import java.io.IOException;

import org.greatfree.concurrency.reactive.RequestQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.dsf.cs.multinode.message.ChatRegistryRequest;
import org.greatfree.dsf.cs.multinode.message.ChatRegistryResponse;
import org.greatfree.dsf.cs.multinode.message.ChatRegistryStream;

/*
 * The thread registers the chatting account concurrently. 04/15/2017, Bing Li
 */

// Created: 04/15/2017, Bing Li
public class ChatRegistryThread extends RequestQueue<ChatRegistryRequest, ChatRegistryStream, ChatRegistryResponse>
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
				request = this.getRequest();
				
				AccountRegistry.CS().add(new CSAccount(request.getMessage().getUserKey(), request.getMessage().getUserName(), request.getMessage().getDescription()));

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
