package org.greatfree.dip.cps.enterprise.server;

import org.greatfree.concurrency.reactive.NotificationThreadCreatable;
import org.greatfree.dip.cps.enterprise.message.DeployEntityBeanNotification;

// Created: 04/21/2020, Bing Li
class DeployEntityBeanNotificationThreadCreator implements NotificationThreadCreatable<DeployEntityBeanNotification, DeployEntityBeanNotificationThread>
{

	@Override
	public DeployEntityBeanNotificationThread createNotificationThreadInstance(int taskSize)
	{
		return new DeployEntityBeanNotificationThread(taskSize);
	}

}
