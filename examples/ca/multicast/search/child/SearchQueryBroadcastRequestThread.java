package ca.multicast.search.child;

import java.io.IOException;

import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.util.Tools;

import ca.multicast.search.message.SearchConfig;
import ca.multicast.search.message.SearchQueryRequest;
import ca.multicast.search.message.SearchQueryResponse;

// Created: 03/16/2020, Bing Li
class SearchQueryBroadcastRequestThread extends NotificationQueue<SearchQueryRequest>
{

	public SearchQueryBroadcastRequestThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		SearchQueryRequest request;
		SearchQueryResponse response;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				try
				{
					request = this.getNotification();
					SearchChildMulticastor.CHILD().asyncRead(request);
					System.out.println("Query: " + request.getQuery());
					if (CrawlDB.DB().isExisted(Tools.getHash(request.getQuery())))
					{
						response = new SearchQueryResponse(CrawlDB.DB().getData(Tools.getHash(request.getQuery())), request.getCollaboratorKey());
					}
					else
					{
						response = new SearchQueryResponse(SearchConfig.NULL_RESULT, request.getCollaboratorKey());
					}
					System.out.println("Response: " + response.getResult());
					SearchChildPeer.CHILD().notifyRoot(response);
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
