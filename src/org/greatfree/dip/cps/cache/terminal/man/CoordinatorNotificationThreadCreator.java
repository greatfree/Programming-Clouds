package org.greatfree.dip.cps.cache.terminal.man;

import org.greatfree.concurrency.reactive.NotificationThreadCreatable;
import org.greatfree.dip.cps.threetier.message.CoordinatorNotification;

//Created: 07/07/2018, Bing Li
public class CoordinatorNotificationThreadCreator implements NotificationThreadCreatable<CoordinatorNotification, CoordinatorNotificationThread>
{

	@Override
	public CoordinatorNotificationThread createNotificationThreadInstance(int taskSize)
	{
		return new CoordinatorNotificationThread(taskSize);
	}

}
