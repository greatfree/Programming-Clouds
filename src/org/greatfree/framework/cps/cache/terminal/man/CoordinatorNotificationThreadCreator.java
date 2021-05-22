package org.greatfree.framework.cps.cache.terminal.man;

import org.greatfree.concurrency.reactive.NotificationQueueCreator;
import org.greatfree.framework.cps.threetier.message.CoordinatorNotification;

//Created: 07/07/2018, Bing Li
public class CoordinatorNotificationThreadCreator implements NotificationQueueCreator<CoordinatorNotification, CoordinatorNotificationThread>
{

	@Override
	public CoordinatorNotificationThread createInstance(int taskSize)
	{
		return new CoordinatorNotificationThread(taskSize);
	}

}
