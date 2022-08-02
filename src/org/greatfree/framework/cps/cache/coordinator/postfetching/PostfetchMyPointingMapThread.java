package org.greatfree.framework.cps.cache.coordinator.postfetching;

import java.io.IOException;

import org.greatfree.concurrency.reactive.NotificationObjectQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.framework.cps.cache.coordinator.MySortedDistributedMap;
import org.greatfree.framework.cps.cache.message.postfetch.PostfetchMyPointingMapNotification;

// Created: 07/19/2018, Bing Li
public class PostfetchMyPointingMapThread extends NotificationObjectQueue<PostfetchMyPointingMapNotification>
{

	public PostfetchMyPointingMapThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		PostfetchMyPointingMapNotification notification;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				try
				{
					notification = this.dequeue();
					MySortedDistributedMap.MIDDLE().postfetch(notification);
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
