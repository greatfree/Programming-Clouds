package org.greatfree.framework.cs.twonode.server;

import java.io.IOException;

import org.greatfree.concurrency.reactive.RequestQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.framework.cs.multinode.message.ChatRegistryRequest;
import org.greatfree.framework.cs.multinode.message.ChatRegistryResponse;
import org.greatfree.framework.cs.multinode.message.ChatRegistryStream;
import org.greatfree.framework.cs.multinode.server.CSAccount;

/*
 * The thread registers the chatting account concurrently. 04/15/2017, Bing Li
 */

// Created: 04/15/2017, Bing Li
public class ChatRegistryThread extends RequestQueue<ChatRegistryRequest, ChatRegistryStream, ChatRegistryResponse>
{
//	private final static Logger log = Logger.getLogger("org.greatfree.framework.cs.twonode.server");

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
//			log.info("1) Here, here, here ...");
			while (!this.isEmpty())
			{
//				log.info("2) Here, here, here ...");
				request = this.dequeue();
				
				AccountRegistry.CS().add(new CSAccount(request.getMessage().getUserKey(), request.getMessage().getUserName(), request.getMessage().getDescription()));
				
				/*
				 * I want to test whether the timeout works or not. 03/25/2020, Bing Lis
				 */
				/*
				try
				{
					Thread.sleep(5000);
				}
				catch (InterruptedException e1)
				{
					e1.printStackTrace();
				}
				*/

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
