package org.greatfree.dip.cps.enterprise.db;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.dip.cps.enterprise.message.DeployEntityBeanNotification;

// Created: 04/22/2020, Bing Li
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
					DBAccessor.ENTERPRISE().initEntityBean(notification.getClientKey(), notification.geClasstName(), notification.getParameterTypes(), notification.getParameterValues(), notification.getClassBytes());
					this.disposeMessage(notification);
				}
				catch (IOException | InterruptedException | ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
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
