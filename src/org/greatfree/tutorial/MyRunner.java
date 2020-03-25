package org.greatfree.tutorial;

import org.greatfree.concurrency.Runner;

// Created: 05/10/2015, Bing Li
public class MyRunner
{
	public static void main(String[] args)
	{
//		Runner<MyTask, MyTaskDisposer> runner = new Runner<MyTask, MyTaskDisposer>(new MyTask(), new MyTaskDisposer());
		Runner<MyTask> runner = new Runner<MyTask>(new MyTask());
		runner.start();
		try
		{
			Thread.sleep(10000);
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}

		try
		{
			runner.stop();
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		
		System.out.println("Ended!");
	}
}