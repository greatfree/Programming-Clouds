package org.greatfree.framework.cps.enterprise.db;

import org.greatfree.concurrency.reactive.NotificationQueueCreator;
import org.greatfree.framework.cps.enterprise.message.DeployEntityBeanNotification;

// Created: 04/22/2020, Bing Li
class DeployEntityBeanNotificationThreadCreator implements NotificationQueueCreator<DeployEntityBeanNotification, DeployEntityBeanNotificationThread>
{

	@Override
	public DeployEntityBeanNotificationThread createInstance(int taskSize)
	{
		return new DeployEntityBeanNotificationThread(taskSize);
	}

}
