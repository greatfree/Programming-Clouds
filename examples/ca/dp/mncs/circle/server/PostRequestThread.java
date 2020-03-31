package ca.dp.mncs.circle.server;

import java.io.IOException;

import org.greatfree.concurrency.reactive.RequestQueue;
import org.greatfree.data.ServerConfig;

import ca.dp.mncs.circle.message.PostRequest;
import ca.dp.mncs.circle.message.PostResponse;
import ca.dp.mncs.circle.message.PostStream;

// Created: 02/26/2020, Bing Li
class PostRequestThread extends RequestQueue<PostRequest, PostStream, PostResponse>
{

	public PostRequestThread(int notificationQueueSize)
	{
		super(notificationQueueSize);
	}

	@Override
	public void run()
	{
		PostStream request;
		PostResponse response;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				request = this.getRequest();
				CirclePosts.CS().addPost(request.getMessage().getPost());
				response = new PostResponse(CirclePosts.CS().getLatestPosts());
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
