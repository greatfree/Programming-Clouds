package ca.dp.mncs.circle.server;

import java.io.IOException;

import org.greatfree.concurrency.reactive.RequestQueue;
import org.greatfree.data.ServerConfig;

import ca.dp.mncs.circle.message.PollPostRequest;
import ca.dp.mncs.circle.message.PollPostResponse;
import ca.dp.mncs.circle.message.PollPostStream;

// Created: 02/25/2020, Bing Li
class PollPostRequestThread extends RequestQueue<PollPostRequest, PollPostStream, PollPostResponse>
{

	public PollPostRequestThread(int notificationQueueSize)
	{
		super(notificationQueueSize);
	}

	@Override
	public void run()
	{
		PollPostStream request;
		PollPostResponse response;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				request = this.getRequest();
				response = new PollPostResponse(CirclePosts.CS().getLatestPosts());
				try
				{
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
				this.holdOn(ServerConfig.REQUEST_THREAD_WAIT_TIME);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
		
	}

}
