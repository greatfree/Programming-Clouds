package org.greatfree.dip.cps.cache.coordinator.prefetching;

import java.io.IOException;

import org.greatfree.concurrency.reactive.NotificationObjectQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.dip.cps.cache.coordinator.MySortedDistributedList;
import org.greatfree.dip.cps.cache.message.FetchMyPointingListNotification;
import org.greatfree.exceptions.RemoteReadException;

// Created: 0711/2018, Bing Li
public class PrefetchMyPointingListThread extends NotificationObjectQueue<FetchMyPointingListNotification>
{

	public PrefetchMyPointingListThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		FetchMyPointingListNotification notification;
//		PrefetchMyPointingsResponse response;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				try
				{
					notification = this.getNotification();
					/*
//					response = Coordinator.CPS().prefetch(notification.getCurrentCacheSize() - 1, notification.getPrefetchCount());
					response = Coordinator.CPS().prefetch(notification.getPrefetchStartIndex(), notification.getPrefetchEndIndex());
					System.out.println("PrefetchMyPointingThread: data size = " + response.getPointings().size());
					MyPointingDistributedList.MIDDLE().prefetch(response.getPointings());
					*/
					MySortedDistributedList.MIDDLE().prefetch(notification);
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
