package org.greatfree.framework.cs.multinode.server;

import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.framework.cs.multinode.message.AddPartnerNotification;

/*
 * The thread deals with adding friends notification from the client. 04/23/2017, Bing Li
 */

// Created: 04/23/2017, Bing Li
class AddPartnerThread extends NotificationQueue<AddPartnerNotification>
{

	public AddPartnerThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		AddPartnerNotification notification;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				try
				{
					notification = this.getNotification();
					
//					PrivateChatSessions.HUNGARY().addMessage(notification.getLocalUserKey(), notification.getFriendKey(), notification.getInvitation());
					// When the notification is received, a new chatting session is created. 05/25/2017, Bing Li
					PrivateChatSessions.HUNGARY().addSession(notification.getPartnerKey(), notification.getLocalUserKey());
					
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
