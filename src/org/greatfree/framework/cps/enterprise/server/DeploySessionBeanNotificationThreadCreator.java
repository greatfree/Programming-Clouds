package org.greatfree.framework.cps.enterprise.server;

import org.greatfree.concurrency.reactive.NotificationQueueCreator;
import org.greatfree.framework.cps.enterprise.message.DeploySessionBeanNotification;

// Created: 04/21/2020, Bing Li
class DeploySessionBeanNotificationThreadCreator implements NotificationQueueCreator<DeploySessionBeanNotification, DeploySessionBeanNotificationThread>
{

	@Override
	public DeploySessionBeanNotificationThread createInstance(int taskSize)
	{
		return new DeploySessionBeanNotificationThread(taskSize);
	}

}
