package ca.dp.mncs.circle.server;

import java.io.IOException;

import org.greatfree.concurrency.reactive.RequestQueue;
import org.greatfree.data.ServerConfig;

import ca.dp.mncs.circle.message.PollLikeRequest;
import ca.dp.mncs.circle.message.PollLikeResponse;
import ca.dp.mncs.circle.message.PollLikeStream;

// Created: 02/25/2020, Bing Li
class PollLikeRequestThread extends RequestQueue<PollLikeRequest, PollLikeStream, PollLikeResponse>
{

	public PollLikeRequestThread(int notificationQueueSize)
	{
		super(notificationQueueSize);
	}

	@Override
	public void run()
	{
		PollLikeStream request;
		PollLikeResponse response;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				request = this.getRequest();
				response = new PollLikeResponse(CirclePosts.CS().getLikes(request.getMessage().getPostID()));
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
