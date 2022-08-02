package org.greatfree.framework.cps.cache.coordinator.postfetching;

import java.io.IOException;

import org.greatfree.concurrency.reactive.NotificationObjectQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.framework.cps.cache.coordinator.MySortedDistributedList;
import org.greatfree.framework.cps.cache.message.FetchMyPointingListNotification;

// Created: 07/11/2018, Bing Li
public class PostfetchMyPointingListThread extends NotificationObjectQueue<FetchMyPointingListNotification>
{

	public PostfetchMyPointingListThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		FetchMyPointingListNotification notification;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				try
				{
					notification = this.dequeue();
					MySortedDistributedList.MIDDLE().postfetch(notification);
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
