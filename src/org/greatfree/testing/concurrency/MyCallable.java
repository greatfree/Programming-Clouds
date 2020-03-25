package org.greatfree.testing.concurrency;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

// Created: 03/25/2020, Bing Li
class MyCallable implements Callable<String>
{

	@Override
	public String call() throws Exception
	{
		Thread.sleep(1000);
		return Thread.currentThread().getName();
	}

	public static void main(String[] args)
	{
		ExecutorService executor = Executors.newFixedThreadPool(10);
		// create a list to hold the Future object associated with Callable
		List<Future<String>> list = new ArrayList<Future<String>>();
		// Create MyCallable instance
		Callable<String> callable = new MyCallable();
		for (int i = 0; i < 100; i++)
		{
			// submit Callable tasks to be executed by thread pool
			Future<String> future = executor.submit(callable);
			// add Future to the list, we can get return value using Future
			list.add(future);
		}
		for (Future<String> fut : list)
		{
			// print the return value of Future, notice the output delay in console
			// because Future.get() waits for task to get completed
			try
			{
				System.out.println(Calendar.getInstance().getTime() + "::" + fut.get());
			}
			catch (InterruptedException | ExecutionException e)
			{
				e.printStackTrace();
			}
		}
		// shut down the executor service now
		executor.shutdown();
	}

}
