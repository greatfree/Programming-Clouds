package org.greatfree.framework.p2p.registry;

import java.io.IOException;

import org.greatfree.concurrency.reactive.RequestQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.Prompts;
import org.greatfree.message.UnregisterPeerRequest;
import org.greatfree.message.UnregisterPeerResponse;
import org.greatfree.message.UnregisterPeerStream;

/*
 * The thread unregisters the peer which just leaves the peer based distributed system and generates a response to the peer. 05/20/2017, Bing Li
 */

// Created: 05/20/2017, Bing Li
class UnregisterPeerThread extends RequestQueue<UnregisterPeerRequest, UnregisterPeerStream, UnregisterPeerResponse>
{

	public UnregisterPeerThread(int maxTaskSize)
	{
		super(maxTaskSize);
	}

	@Override
	public void run()
	{
		UnregisterPeerStream request;
		UnregisterPeerResponse response;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				request = this.dequeue();
				
				// Unregister the peer. 05/20/2017, Bing Li

				PeerRegistry.SYSTEM().unregister(request.getMessage().getPeerKey());
				response = new UnregisterPeerResponse(true);
				try
				{
					this.respond(request.getOutStream(), request.getLock(), response);
				}
				catch (IOException e)
				{
//					e.printStackTrace();
					System.out.println(Prompts.PEER_DISCONNECTED);
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
