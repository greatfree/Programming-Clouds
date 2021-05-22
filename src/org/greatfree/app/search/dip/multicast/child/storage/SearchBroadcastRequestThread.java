package org.greatfree.app.search.dip.multicast.child.storage;

import java.io.IOException;

import org.greatfree.app.search.dip.multicast.message.SearchMultiRequest;
import org.greatfree.app.search.dip.multicast.message.SearchMultiResponse;
import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.framework.multicast.child.ChildMulticastor;

// Created: 09/28/2018, Bing Li
class SearchBroadcastRequestThread extends NotificationQueue<SearchMultiRequest>
{

	public SearchBroadcastRequestThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		SearchMultiRequest request;
		SearchMultiResponse response;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				try
				{
					request = this.dequeue();
					ChildMulticastor.CHILD().asyncRead(request);
					response = new SearchMultiResponse(PageStorage.STORAGE().search(request.getUserKey(), request.getQuery()), request.getCollaboratorKey());
					StorageNode.STORAGE().notifyRoot(response);
					this.disposeMessage(request);
					this.dispose(response);
				}
				catch (InterruptedException | IOException e)
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
