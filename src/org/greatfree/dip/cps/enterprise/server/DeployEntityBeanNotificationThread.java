package org.greatfree.dip.cps.enterprise.server;

import java.io.IOException;

import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.dip.cps.enterprise.message.DeployEntityBeanNotification;

// Created: 04/21/2020, Bing Li
class DeployEntityBeanNotificationThread extends NotificationQueue<DeployEntityBeanNotification>
{

	public DeployEntityBeanNotificationThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		DeployEntityBeanNotification notification;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				try
				{
					notification = this.getNotification();
					EnterpriseServer.CPS().forward(notification);
					this.disposeMessage(notification);
				}
				catch (InterruptedException | IOException e)
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
