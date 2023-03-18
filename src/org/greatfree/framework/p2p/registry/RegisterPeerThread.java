package org.greatfree.framework.p2p.registry;

import java.io.IOException;
import java.util.logging.Logger;

import org.greatfree.concurrency.reactive.RequestQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.message.RegisterPeerRequest;
import org.greatfree.message.RegisterPeerResponse;
import org.greatfree.message.RegisterPeerStream;

/*
 * The thread registers the peer which just signs in to the peer based distributed system and generates a response to the peer. 05/01/2017, Bing Li
 */

// Created: 05/01/2017, Bing Li
class RegisterPeerThread extends RequestQueue<RegisterPeerRequest, RegisterPeerStream, RegisterPeerResponse>
{
	private final static Logger log = Logger.getLogger("org.greatfree.framework.p2p.registry");

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
				request = this.dequeue();
//				PeerRegistry.SYSTEM().register(request.getMessage().getPeerKey(), request.getMessage().getPeerName(), request.getMessage().getIP(), request.getMessage().getPort());
//				account = PeerRegistry.SYSTEM().get(request.getMessage().getPeerKey());
//				response = new RegisterPeerResponse(account.getPeerPort(), account.getAdminPort());
				
				log.info("RegisterPeerThread: peerKey = " + request.getMessage().getPeerKey());
				log.info("RegisterPeerThread: peerName = " + request.getMessage().getPeerName());
				log.info("RegisterPeerThread: ip = " + request.getMessage().getIP());
				log.info("RegisterPeerThread: port = " + request.getMessage().getPort());
				
				// Register the peer. 06/02/2017, Bing Li
//				response = new RegisterPeerResponse(PeerRegistry.SYSTEM().register(request.getMessage().getPeerKey(), request.getMessage().getPeerName(), request.getMessage().getIP(), request.getMessage().getPort(), request.getMessage().isServerDisabled(), request.getMessage().isClientDisabled()));
				response = new RegisterPeerResponse(PeerRegistry.SYSTEM().register(request.getMessage().getPeerKey(), request.getMessage().getPeerName(), request.getMessage().getIP(), request.getMessage().getPort(), request.getMessage().isServerDisabled(), request.getMessage().isBroker()));
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
