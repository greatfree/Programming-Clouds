package ca.streaming.news.subscriber;

import java.io.IOException;

import org.greatfree.concurrency.reactive.RequestQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.dip.streaming.subscriber.SubscriberDB;

import ca.streaming.news.message.IsVideoExistedRequest;
import ca.streaming.news.message.IsVideoExistedResponse;
import ca.streaming.news.message.IsVideoExistedStream;

// Created: 04/05/2020, Bing Li
class IsVideoExistedRequestThread extends RequestQueue<IsVideoExistedRequest, IsVideoExistedStream, IsVideoExistedResponse>
{

	public IsVideoExistedRequestThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		IsVideoExistedStream request;
		IsVideoExistedResponse response;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				try
				{
					request = this.getRequest();
					if (SubscriberDB.DB().search(request.getMessage().getKeyword()).size() > 0)
					{
						response = new IsVideoExistedResponse(true);
					}
					else
					{
						response = new IsVideoExistedResponse(false);
					}
					this.respond(request.getOutStream(), request.getLock(), response);
					this.disposeMessage(request, response);
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
			try
			{
				this.holdOn(ServerConfig.NOTIFICATION_THREAD_WAIT_TIME);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}

}
