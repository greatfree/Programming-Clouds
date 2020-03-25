package org.greatfree.testing.concurrency;

import org.greatfree.concurrency.Async;

// Created: 09/10/2018, Bing Li
class MyActor extends Async<MyNotification>
{

	@Override
	public void perform(MyNotification notification)
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

}
