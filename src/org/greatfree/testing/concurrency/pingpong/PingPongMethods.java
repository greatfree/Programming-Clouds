package org.greatfree.testing.concurrency.pingpong;

/**
 * 
 * @author libing
 * 
 * 08/07/2022
 *
 */
final class PingPongMethods
{
	public static String ping(String query) throws InterruptedException
	{
		System.out.println("Ping Method: processing " + query);
		Thread.sleep(500);
		System.out.println("Ping Method: " + query + " processed");
		return query;
	}

	public static String pong(String query) throws InterruptedException
	{
		System.out.println("Pong Method: processing " + query);
		Thread.sleep(500);
		System.out.println("Pong Method: " + query + " processed");
		return query;
	}
}
