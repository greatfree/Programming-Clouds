package org.greatfree.server;

import java.io.IOException;
import java.util.logging.Logger;

import org.greatfree.concurrency.reactive.RequestQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.message.LeaveRequest;
import org.greatfree.message.LeaveResponse;
import org.greatfree.message.LeaveStream;

/**
 * 
 * @author libing
 * 
 * 10/11/2022
 *
 */
final class LeaveRequestThread extends RequestQueue<LeaveRequest, LeaveStream, LeaveResponse>
{
	private final static Logger log = Logger.getLogger("org.greatfree.server");
	
	public LeaveRequestThread(int queueSize)
	{
		super(queueSize);
	}

	@Override
	public void run()
	{
		LeaveStream request;
		LeaveResponse response;
		while (!super.isShutdown())
		{
			while (!super.isEmpty())
			{
				request = super.dequeue();
				response = new LeaveResponse();
				try
				{
					super.respond(request.getOutStream(), request.getLock(), response);
				}
				catch (IOException e)
				{
//					e.printStackTrace();
					log.info("Server is down!");
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
