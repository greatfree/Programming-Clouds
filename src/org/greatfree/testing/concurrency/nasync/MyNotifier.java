package org.greatfree.testing.concurrency.nasync;

import org.greatfree.concurrency.Notifier;

// Created: 09/10/2018, Bing Li
class MyNotifier implements Notifier<MyNotification>
{

	@Override
	public void notify(MyNotification notification)
	{
		try
		{
			SyncNotifyMethod.doSomething(notification.getFirstName(), notification.getLastName());
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}

	/*
	@Override
	public void perform(MyNotification message, int cryptoOption)
	{
		// TODO Auto-generated method stub
		
	}
	*/

}
