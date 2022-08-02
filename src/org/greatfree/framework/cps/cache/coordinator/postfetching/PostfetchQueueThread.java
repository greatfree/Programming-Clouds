package org.greatfree.framework.cps.cache.coordinator.postfetching;

import java.io.IOException;

import org.greatfree.concurrency.reactive.NotificationObjectQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.framework.cps.cache.coordinator.MyDistributedQueueStore;
import org.greatfree.framework.cps.cache.message.FetchQueueNotification;

// Created: 08/13/2018, Bing Li
public class PostfetchQueueThread extends NotificationObjectQueue<FetchQueueNotification>
{

	public PostfetchQueueThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		FetchQueueNotification notification;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				try
				{
					notification = this.dequeue();
					MyDistributedQueueStore.MIDDLESTORE().postfetch(notification);
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
