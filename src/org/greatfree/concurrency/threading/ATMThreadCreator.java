package org.greatfree.concurrency.threading;

import org.greatfree.concurrency.threading.message.ATMNotification;

// Created: 09/13/2019, Bing Li
class ATMThreadCreator implements NotificationThreadCreatable<ATMNotification, ATMThread>
{

	@Override
	public ATMThread createNotificationThreadInstance(int taskSize)
	{
		return new ATMThread(taskSize);
	}

}
