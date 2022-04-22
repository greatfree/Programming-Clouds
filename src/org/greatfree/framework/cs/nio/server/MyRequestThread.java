package org.greatfree.framework.cs.nio.server;

import java.io.IOException;
import java.util.logging.Logger;

import org.greatfree.concurrency.reactive.nio.RequestQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.framework.cs.nio.message.MyRequest;
import org.greatfree.framework.cs.nio.message.MyResponse;
import org.greatfree.framework.cs.nio.message.MyStream;

/**
 * 
 * @author Bing Li
 * 
 * 02/05/2022
 *
 */
class MyRequestThread extends RequestQueue<MyRequest, MyStream, MyResponse>
{
	private final static Logger log = Logger.getLogger("org.greatfree.framework.cs.nio.server");

	public MyRequestThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		MyStream request;
		MyResponse response;
		while (!super.isShutdown())
		{
			while (!super.isEmpty())
			{
				request = super.dequeue();
				log.info(request.getMessage().getMessage());
				response = new MyResponse("What's up?"); 
				try
				{
					super.respond(request.getChannel(), response);
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
				super.disposeMessage(request, response);
			}
			try
			{
				super.holdOn(ServerConfig.REQUEST_THREAD_WAIT_TIME);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}
}
