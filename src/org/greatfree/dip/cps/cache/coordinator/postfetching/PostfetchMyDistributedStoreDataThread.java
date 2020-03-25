package org.greatfree.dip.cps.cache.coordinator.postfetching;

import java.io.IOException;

import org.greatfree.concurrency.reactive.NotificationObjectQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.dip.cps.cache.coordinator.MyDistributedMapStore;
import org.greatfree.dip.cps.cache.message.postfetch.PostfetchMyStoreDataNotification;
import org.greatfree.exceptions.RemoteReadException;

// Created: 08/24/2018, Bing Li
public class PostfetchMyDistributedStoreDataThread extends NotificationObjectQueue<PostfetchMyStoreDataNotification>
{

	public PostfetchMyDistributedStoreDataThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		PostfetchMyStoreDataNotification notification;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				try
				{
					notification = this.getNotification();
					MyDistributedMapStore.MIDDLE().postfetch(notification);
					this.disposeObject(notification);
				}
				catch (InterruptedException | ClassNotFoundException | RemoteReadException | IOException e)
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
