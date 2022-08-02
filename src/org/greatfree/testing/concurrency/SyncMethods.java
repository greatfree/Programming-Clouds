package org.greatfree.testing.concurrency;

/**
 * 
 * @author libing
 * 
 * 08/01/2022
 *
 */
final class SyncMethods
{
	public static String doSomethingA(String query) throws InterruptedException
	{
		System.out.println("Method A processing: " + query);
		Thread.sleep(2000);
		return "Response A: " + query;
	}

	public static String doSomethingB(String query) throws InterruptedException
	{
		System.out.println("Method B processing: " + query);
		Thread.sleep(2000);
		return "Response B: " + query;
	}
}
