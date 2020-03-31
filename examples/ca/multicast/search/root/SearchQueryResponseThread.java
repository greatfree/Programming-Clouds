package ca.multicast.search.root;

import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.data.ServerConfig;

import ca.multicast.search.message.SearchQueryResponse;

// Created: 03/15/2020, Bing Li
class SearchQueryResponseThread extends NotificationQueue<SearchQueryResponse>
{

	public SearchQueryResponseThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		SearchQueryResponse response;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				try
				{
					response = this.getNotification();
					SearchMulticastor.ROOT().getRP().saveResponse(response);
					this.disposeMessage(response);
				}
				catch (InterruptedException e)
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
