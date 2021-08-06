package org.greatfree.app.search.multicast.root.entry;

import org.greatfree.app.search.multicast.message.SearchMultiResponse;
import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.framework.multicast.root.RootMulticastor;

// Created: 10/08/2018, Bing Li
class SearchMultiResponseThread extends NotificationQueue<SearchMultiResponse>
{

	public SearchMultiResponseThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		SearchMultiResponse notification;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				try
				{
					notification = this.dequeue();
					RootMulticastor.ROOT().getRP().saveResponse(notification);
					this.disposeMessage(notification);
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
