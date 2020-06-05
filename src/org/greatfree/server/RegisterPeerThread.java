package org.greatfree.server;

import java.io.IOException;

import org.greatfree.concurrency.reactive.RequestQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.message.RegisterPeerRequest;
import org.greatfree.message.RegisterPeerResponse;
import org.greatfree.message.RegisterPeerStream;

/*
 * The thread registers the peer which just signs in to the peer based distributed system and generates a response to the peer. 05/01/2017, Bing Li
 */

// Created: 05/01/2017, Bing Li
public class RegisterPeerThread extends RequestQueue<RegisterPeerRequest, RegisterPeerStream, RegisterPeerResponse>
{

	public RegisterPeerThread(int maxTaskSize)
	{
		super(maxTaskSize);
	}

	@Override
	public void run()
	{
		RegisterPeerStream request;
		RegisterPeerResponse response;
//		PeerAccount account;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				request = this.getRequest();
//				PeerRegistry.SYSTEM().register(request.getMessage().getPeerKey(), request.getMessage().getPeerName(), request.getMessage().getIP(), request.getMessage().getPort());
//				account = PeerRegistry.SYSTEM().get(request.getMessage().getPeerKey());
//				response = new RegisterPeerResponse(account.getPeerPort(), account.getAdminPort());
				
				System.out.println("RegisterPeerThread: peerKey = " + request.getMessage().getPeerKey());
				System.out.println("RegisterPeerThread: peerName = " + request.getMessage().getPeerName());
				System.out.println("RegisterPeerThread: ip = " + request.getMessage().getIP());
				System.out.println("RegisterPeerThread: port = " + request.getMessage().getPort());
				
				// Register the peer. 06/02/2017, Bing Li
				response = new RegisterPeerResponse(PeerRegistry.SYSTEM().register(request.getMessage().getPeerKey(), request.getMessage().getPeerName(), request.getMessage().getIP(), request.getMessage().getPort()));
				try
				{
					// Respond to the peer. 06/02/2017, Bing Li
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
