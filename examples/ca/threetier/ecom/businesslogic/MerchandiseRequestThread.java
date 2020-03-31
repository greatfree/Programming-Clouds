package ca.threetier.ecom.businesslogic;

import java.io.IOException;

import org.greatfree.concurrency.reactive.RequestQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.RemoteReadException;

import ca.dp.tncs.message.MerchandiseRequest;
import ca.dp.tncs.message.MerchandiseResponse;
import ca.dp.tncs.message.MerchandiseStream;

// Created: 03/09/2020, Bing Li
class MerchandiseRequestThread extends RequestQueue<MerchandiseRequest, MerchandiseStream, MerchandiseResponse>
{

	public MerchandiseRequestThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		MerchandiseStream request;
		MerchandiseResponse response;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				request = this.getRequest();
				try
				{
					System.out.println("The query from client: " + request.getMessage().getBook());
					response = BusinessLogic.CPS().query(request.getMessage());
					System.out.println("The answer from database: " + response.getBook());
					this.respond(request.getOutStream(), request.getLock(), response);
					this.disposeMessage(request, response);
				}
				catch (IOException | ClassNotFoundException | RemoteReadException e)
				{
					e.printStackTrace();
				}
			}
			try
			{
				this.holdOn(ServerConfig.REQUEST_THREAD_WAIT_TIME);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}

}
