package org.greatfree.app.cps.coordinator;

import java.io.IOException;

import org.greatfree.app.cps.message.MerchandiseRequest;
import org.greatfree.app.cps.message.MerchandiseResponse;
import org.greatfree.app.cps.message.MerchandiseStream;
import org.greatfree.concurrency.reactive.RequestQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.RemoteIPNotExistedException;
import org.greatfree.exceptions.RemoteReadException;

// Created: 08/14/2018, Bing Li
public class MerchandiseRequestThread extends RequestQueue<MerchandiseRequest, MerchandiseStream, MerchandiseResponse>
{

	public MerchandiseRequestThread(int maxTaskSize)
	{
		super(maxTaskSize);
	}

	@Override
	public void run()
	{
		MerchandiseStream request;
		MerchandiseResponse response;
		MerchandiseResponse coordinatorResponse;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				request = this.dequeue();
				System.out.println("Merchandise query: " + request.getMessage().getQuery());
				try
				{
					coordinatorResponse = VendorCoordinator.CPS().query(request.getMessage().getQuery());
					response = new MerchandiseResponse(coordinatorResponse.getMerchandise(), coordinatorResponse.getPrice());
					this.respond(request.getOutStream(), request.getLock(), response);
					this.disposeMessage(request, response);
				}
				catch (ClassNotFoundException | RemoteReadException | IOException | RemoteIPNotExistedException e)
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
