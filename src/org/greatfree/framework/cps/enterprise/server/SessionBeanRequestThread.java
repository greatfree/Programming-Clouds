package org.greatfree.framework.cps.enterprise.server;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.greatfree.concurrency.reactive.RequestQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.framework.cps.enterprise.message.SessionBeanRequest;
import org.greatfree.framework.cps.enterprise.message.SessionBeanResponse;
import org.greatfree.framework.cps.enterprise.message.SessionBeanStream;

// Created: 04/21/2020, Bing Li
class SessionBeanRequestThread extends RequestQueue<SessionBeanRequest, SessionBeanStream, SessionBeanResponse>
{

	public SessionBeanRequestThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		SessionBeanStream request;
		SessionBeanResponse response;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				request = this.dequeue();
				try
				{
					response = new SessionBeanResponse(BusinessLogic.ENTERPRISE().invoke(request.getMessage().getClientKey(), request.getMessage().getClassName(), request.getMessage().getMethodName(), request.getMessage().getParameterTypes(), request.getMessage().getParameterValues()));
					this.respond(request.getOutStream(), request.getLock(), response);
					this.disposeMessage(request, response);
				}
				catch (ClassNotFoundException | NoSuchMethodException | SecurityException | IllegalAccessException
						| IllegalArgumentException | InvocationTargetException | IOException e)
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
