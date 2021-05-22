package org.greatfree.framework.cps.enterprise.db;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.greatfree.concurrency.reactive.RequestQueue;
import org.greatfree.data.ServerConfig;
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
					response = new EntityBeanResponse(DBAccessor.ENTERPRISE().invoke(request.getMessage().getClientKey(), request.getMessage().getClassName(), request.getMessage().getMethodName(), request.getMessage().getParameterTypes(), request.getMessage().getParameterValues()));
					this.respond(request.getOutStream(), request.getLock(), response);
					this.disposeMessage(request, response);
				}
				catch (ClassNotFoundException | IOException | NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
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
