package org.greatfree.dip.cps.cache.coordinator.postfetching;

import java.io.IOException;

import org.greatfree.concurrency.reactive.NotificationObjectQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.dip.cps.cache.coordinator.MyDistributedStackStore;
import org.greatfree.dip.cps.cache.message.FetchStackNotification;
import org.greatfree.exceptions.RemoteReadException;

// Created: 08/07/2018, Bing Li
public class PostfetchStackThread extends NotificationObjectQueue<FetchStackNotification>
{

	public PostfetchStackThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		FetchStackNotification notification;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				try
				{
					notification = this.getNotification();
					MyDistributedStackStore.MIDDLESTORE().postfetch(notification);
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
