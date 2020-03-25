package org.greatfree.testing.concurrency;

import org.greatfree.concurrency.RunnerTask;
import org.greatfree.util.Tools;

// Created: 11/07/2019, Bing Li
class InterTask extends RunnerTask
{

	@Override
	public void run()
	{
		while (true)
		{
			try
			{
				System.out.println(Tools.generateUniqueKey());
				Thread.sleep(1000);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
//				return;
			}
		}
		
	}
	
	/*
	 * The method is useless. 11/08/2019, Bing Li
	 */
	/*
	public void interrupt()
	{
		Thread.currentThread().interrupt();
	}
	*/

	@Override
	public int getWorkload()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void dispose() throws InterruptedException
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose(long timeout) throws InterruptedException
	{
		// TODO Auto-generated method stub
		
	}

}
