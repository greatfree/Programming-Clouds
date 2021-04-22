package org.greatfree.framework.cs.multinode.server;

import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.framework.cs.multinode.message.ChatNotification;

/*
 * The thread deals with chatting notification from the client. 04/23/2017, Bing Li
 */

// Created: 04/27/2017, Bing Li
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
					
					// When new messages are available, they are retained in the server for polling. 05/25/2017, Bing Li
					PrivateChatSessions.HUNGARY().addMessage(notification.getSessionKey(), notification.getSenderKey(), notification.getReceiverKey(), notification.getMessage());
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
