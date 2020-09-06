package org.greatfree.dsf.p2p.peer;

import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.dsf.p2p.message.AddPartnerNotification;

/*
 * The thread deals with adding chatting friend notification from the other peer. 04/30/2017, Bing Li
 */

// Created: 05/02/2017, Bing Li
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
					// Just show the invitation on the screen. 06/11/2017, Bing Li
					System.out.println(notification.getLocalUserName() + " says: " + notification.getInvitation());
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
