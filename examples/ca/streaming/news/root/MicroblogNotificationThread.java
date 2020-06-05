package ca.streaming.news.root;

import java.io.IOException;

import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.dip.streaming.Stream;
import org.greatfree.exceptions.DistributedNodeFailedException;

import ca.streaming.news.message.MicroblogNotification;

// Created: 04/01/2020, Bing Li
class MicroblogNotificationThread extends NotificationQueue<MicroblogNotification>
{

	public MicroblogNotificationThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		MicroblogNotification notification;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				try
				{
					notification = this.getNotification();
					System.out.println("MicroblogNotificationThread: stream received: " + notification.getPost());
					NewsRootMulticastor.STREAM().unicastNotify(Stream.generateKey(notification.getPost().getPublisher(), notification.getPost().getCategory()), notification);
					this.disposeMessage(notification);
				}
				catch (InterruptedException | IOException | DistributedNodeFailedException e)
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
