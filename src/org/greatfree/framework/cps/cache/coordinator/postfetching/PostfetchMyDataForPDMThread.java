package org.greatfree.framework.cps.cache.coordinator.postfetching;

import org.greatfree.concurrency.reactive.NotificationObjectQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.RemoteIPNotExistedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.framework.cps.cache.coordinator.MyReadDistributedMap;
import org.greatfree.framework.cps.cache.message.postfetch.PostfetchMyDataNotification;

// Created: 07/21/2018, Bing Li
public class PostfetchMyDataForPDMThread extends NotificationObjectQueue<PostfetchMyDataNotification>
{

	public PostfetchMyDataForPDMThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		PostfetchMyDataNotification notification;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				try
				{
					notification = this.dequeue();
					MyReadDistributedMap.MIDDLE().postfetch(notification);
					this.disposeObject(notification);
				}
				catch (InterruptedException | ClassNotFoundException | RemoteReadException | RemoteIPNotExistedException e)
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
