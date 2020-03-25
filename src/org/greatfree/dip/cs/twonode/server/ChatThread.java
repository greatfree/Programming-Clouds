package org.greatfree.dip.cs.twonode.server;

import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.dip.cs.multinode.message.ChatNotification;

/*
 * The thread deals with chatting notification from the client. 06/21/2018, Bing Li
 */

// Created: 06/21/2018, Bing Li
public class ChatThread extends NotificationQueue<ChatNotification>
{

	public ChatThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		ChatNotification notification;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				try
				{
					// Dequeue. 02/19/2020, Bing Li
					notification = this.getNotification();
					
					// When new messages are available, they are displayed. 06/21/2018, Bing Li
					System.out.println(notification.getSenderName() + " says, " + notification.getMessage());
					this.disposeMessage(notification);
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
			try
			{
				// Wait for a moment after all of the existing notifications are processed. 01/20/2016, Bing Li
				this.holdOn(ServerConfig.NOTIFICATION_THREAD_WAIT_TIME);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}

}
