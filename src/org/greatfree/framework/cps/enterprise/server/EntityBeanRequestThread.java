package org.greatfree.framework.cps.enterprise.server;

import java.io.IOException;

import org.greatfree.concurrency.reactive.RequestQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.framework.cps.enterprise.message.EntityBeanRequest;
import org.greatfree.framework.cps.enterprise.message.EntityBeanResponse;
import org.greatfree.framework.cps.enterprise.message.EntityBeanStream;

// Created: 04/23/2020, Bing Li
class EntityBeanRequestThread extends RequestQueue<EntityBeanRequest, EntityBeanStream, EntityBeanResponse>
{

	public EntityBeanRequestThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		EntityBeanStream request;
		EntityBeanResponse response;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				request = this.dequeue();
				try
				{
					response = (EntityBeanResponse)EnterpriseServer.CPS().query(request.getMessage());
					this.respond(request.getOutStream(), request.getLock(), response);
					this.disposeMessage(request, response);
				}
				catch (ClassNotFoundException | RemoteReadException | IOException e)
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
