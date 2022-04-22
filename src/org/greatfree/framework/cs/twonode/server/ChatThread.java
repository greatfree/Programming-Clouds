package org.greatfree.framework.cs.twonode.server;

import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.framework.cs.multinode.message.ChatNotification;

/*
 * The thread deals with chatting notification from the client. 06/21/2018, Bing Li
 */

// Created: 06/21/2018, Bing Li
class ChatThread extends NotificationQueue<ChatNotification>
{
//	private final static Logger log = Logger.getLogger("org.greatfree.framework.cs.twonode.server");

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
//			log.info("1) Here, here, here ...");
			while (!this.isEmpty())
			{
//				log.info("2) Here, here, here ...");
				try
				{
					// Dequeue. 02/19/2020, Bing Li
					notification = this.dequeue();
					
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
