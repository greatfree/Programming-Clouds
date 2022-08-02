package org.greatfree.testing.concurrency;

import org.greatfree.concurrency.Notifier;

// Created: 09/10/2018, Bing Li
class MyNotifier implements Notifier<MyNotification>
{

	@Override
	public void notify(MyNotification notification)
	{
		try
		{
			Thread.sleep(1000);
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		System.out.println(notification.getLastName() + ", " + notification.getFirstName());
	}

	/*
	@Override
	public void perform(MyNotification message, int cryptoOption)
	{
		// TODO Auto-generated method stub
		
	}
	*/

}
