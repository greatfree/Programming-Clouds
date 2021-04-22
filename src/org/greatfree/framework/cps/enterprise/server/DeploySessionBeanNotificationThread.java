package org.greatfree.framework.cps.enterprise.server;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.framework.cps.enterprise.message.DeploySessionBeanNotification;

// Created: 04/21/2020, Bing Li
class DeploySessionBeanNotificationThread extends NotificationQueue<DeploySessionBeanNotification>
{

	public DeploySessionBeanNotificationThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		DeploySessionBeanNotification notification;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				try
				{
					notification = this.getNotification();
					BusinessLogic.ENTERPRISE().initSessionBean(notification.getClientKey(), notification.getClassName(), notification.getParameterTypes(), notification.getParameterValues(), notification.getClassBytes());
					this.disposeMessage(notification);
				}
				catch (InterruptedException | IOException | ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
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
