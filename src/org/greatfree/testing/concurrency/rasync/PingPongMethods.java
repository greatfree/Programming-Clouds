package org.greatfree.testing.concurrency.rasync;

/**
 * 
 * @author libing
 * 
 * 08/02/2022
 *
 */
final class PingPongMethods
{
	public static String ping(String query) throws InterruptedException
	{
		System.out.println("Ping Method: processing " + query);
		Thread.sleep(2000);
		System.out.println("Ping Method: " + query + " processed");
		return query;
	}

	public static String pong(String query) throws InterruptedException
	{
		System.out.println("Pong Method: processing " + query);
		Thread.sleep(2000);
		System.out.println("Pong Method: " + query + " processed");
		return query;
	}
}
