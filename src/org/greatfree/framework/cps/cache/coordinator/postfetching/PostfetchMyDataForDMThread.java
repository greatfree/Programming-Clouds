package org.greatfree.framework.cps.cache.coordinator.postfetching;

import org.greatfree.concurrency.reactive.NotificationObjectQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.RemoteIPNotExistedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.framework.cps.cache.coordinator.MyDistributedMap;
import org.greatfree.framework.cps.cache.message.postfetch.PostfetchMyDataNotification;

// Created: 07/09/2018, Bing Li
public class PostfetchMyDataForDMThread extends NotificationObjectQueue<PostfetchMyDataNotification>
{

	public PostfetchMyDataForDMThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		PostfetchMyDataNotification notification;
//		PostfetchMyDataResponse response;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				try
				{
					notification = this.dequeue();
					/*
					System.out.println("PostfetchMyDataThread: resourceKey = " + notification.getResourceKey());
					response = Coordinator.CPS().postfetchMyData(notification.getResourceKey());
					if (response != null)
					{
						MyDistributedMap.MIDDLE().save(notification.getKey(), response.getMyData().getKey(), response.getMyData());
					}
					*/
					MyDistributedMap.MIDDLE().postfetch(notification);
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
