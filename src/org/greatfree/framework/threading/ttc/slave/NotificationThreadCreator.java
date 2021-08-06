package org.greatfree.framework.threading.ttc.slave;

import org.greatfree.concurrency.threading.NotificationThreadCreatable;
import org.greatfree.concurrency.threading.message.ATMNotification;

// Created: 09/11/2019, Bing Li
//class FreeThreadCreator implements NotificationThreadCreatable<SyncInstructNotification, FreeThread>
class NotificationThreadCreator implements NotificationThreadCreatable<ATMNotification, NotificationThread>
{

	@Override
	public NotificationThread createNotificationThreadInstance(int taskSize)
	{
		return new NotificationThread(taskSize);
	}

}
