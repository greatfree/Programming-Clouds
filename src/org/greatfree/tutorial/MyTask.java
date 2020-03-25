package org.greatfree.tutorial;

import org.greatfree.concurrency.RunnerTask;

// Created: 05/10/2015, Bing Li
// public class MyTask implements Runnable
public class MyTask extends RunnerTask
{
	private boolean isShutdown;
	
	public MyTask()
	{
		this.isShutdown = false;
	}
	
	public void dispose()
	{
		this.isShutdown = true;
	}
	
	public void run()
	{
		int i = 0;
		while (!this.isShutdown)
		{
			System.out.println(++i + ") This is a thread ...");
			try
			{
				Thread.sleep(500);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}

	@Override
	public void dispose(long timeout) throws InterruptedException
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getWorkload()
	{
		// TODO Auto-generated method stub
		return 0;
	}
}
