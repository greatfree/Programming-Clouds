package edu.greatfree.p2p.peer;

import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.data.ServerConfig;

import edu.greatfree.p2p.message.ChatNotification;

/*
* The thread deals with the chatting notification from the other peer. 04/30/2017, Bing Li
 */

// Created: 05/02/2017, Bing Li
class ChatThread extends NotificationQueue<ChatNotification>
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
					notification = this.getNotification();
					// Show the chatting message on the screen. 06/11/2017, Bing Li
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
