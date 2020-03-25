package org.greatfree.server;

import java.io.IOException;

import org.greatfree.concurrency.reactive.RequestQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.message.PortRequest;
import org.greatfree.message.PortResponse;
import org.greatfree.message.PortStream;

/*
 * The thread obtains an idle port and generates a response to the peer. 05/02/2017, Bing Li
 */

// Created: 05/02/2017, Bing Li
public class PortRequestThread extends RequestQueue<PortRequest, PortStream, PortResponse>
{

	public PortRequestThread(int maxTaskSize)
	{
		super(maxTaskSize);
	}

	@Override
	public void run()
	{
		PortStream request;
		PortResponse response;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				request = this.getRequest();
				
				System.out.println("PortRequestThread: peerKey = " + request.getMessage().getPeerKey());
				System.out.println("PortRequestThread: ip = " + request.getMessage().getIP());
				System.out.println("PortRequestThread: currentPort = " + request.getMessage().getCurrentPort());

				// Obtain an idle port and generate the response for the remote peer. 06/02/2017, Bing Li
				response = new PortResponse(PeerRegistry.SYSTEM().getIdlePort(request.getMessage().getPeerKey(), request.getMessage().getPortKey(), request.getMessage().getIP(), request.getMessage().getCurrentPort()));
				try
				{
					// Respond to the remote peer with the assigned idle port. 06/02/2017, Bing Li
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
