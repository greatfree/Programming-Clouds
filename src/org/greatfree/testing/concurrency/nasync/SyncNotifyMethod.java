package org.greatfree.testing.concurrency.nasync;

/**
 * 
 * @author libing
 * 
 * 08/04/2022
 *
 */
class SyncNotifyMethod
{
	public static void doSomething(String firstName, String lastName) throws InterruptedException
	{
		System.out.println(firstName + ", " + lastName);
		Thread.sleep(1000);
	}
}
