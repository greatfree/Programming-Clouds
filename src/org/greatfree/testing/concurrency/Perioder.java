package org.greatfree.testing.concurrency;

/**
 * 
 * @author Bing Li
 * 
 * 02/03/2022, Bing Li
 *
 */
class Perioder implements Runnable
{

	@Override
	public void run()
	{
		System.out.println("Hello, World!");
		try
		{
			Thread.sleep(2000);
		}
		catch (InterruptedException e)
		{
//			e.printStackTrace();
			System.out.println("Done!");
		}
	}

}
