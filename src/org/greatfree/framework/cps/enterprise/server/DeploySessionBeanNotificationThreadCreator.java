package org.greatfree.framework.cps.enterprise.server;

import org.greatfree.concurrency.reactive.NotificationThreadCreatable;
import org.greatfree.framework.cps.enterprise.message.DeploySessionBeanNotification;

// Created: 04/21/2020, Bing Li
class DeploySessionBeanNotificationThreadCreator implements NotificationThreadCreatable<DeploySessionBeanNotification, DeploySessionBeanNotificationThread>
{

	@Override
	public DeploySessionBeanNotificationThread createNotificationThreadInstance(int taskSize)
	{
		return new DeploySessionBeanNotificationThread(taskSize);
	}

}
