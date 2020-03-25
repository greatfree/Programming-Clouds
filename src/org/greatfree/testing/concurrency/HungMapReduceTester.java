package org.greatfree.testing.concurrency;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.greatfree.concurrency.Scheduler;
import org.greatfree.concurrency.mapreduce.MRCore;
import org.greatfree.data.ServerConfig;
import org.greatfree.util.Time;

// Created: 11/06/2019, Bing Li
class HungMapReduceTester
{

	public static void main(String[] args) throws InterruptedException
	{
		Scheduler.GREATFREE().init(ServerConfig.SCHEDULER_POOL_SIZE, ServerConfig.SCHEDULER_KEEP_ALIVE_TIME);

		// Initialize one instance of the MRCore. 04/22/2018, Bing Li
		MRCore<MyHungTask, MyHungResult, MyHungMRThread, MyHungMRThreadCreator> mp = new MRCore.MRCoreBuilder<MyHungTask, MyHungResult, MyHungMRThread, MyHungMRThreadCreator>()
				.poolSize(100)
				.threadCreator(new MyHungMRThreadCreator())
				.taskSizePerThread(ServerConfig.TASK_SIZE_PER_THREAD)
				.waitTime(ServerConfig.MR_WAIT_TIME)
				.shutdownTime(ServerConfig.MR_SHUTDOWN_TIME)
				.scheduler(Scheduler.GREATFREE().getScheduler())
				.build();

		System.out.println("MR starting ...");
		// Assign the tasks for map/reduce. 04/22/2018, Bing Li
		Map<Integer, MyHungTask> tasks = new HashMap<Integer, MyHungTask>();
		for (int i = 0; i < 10; i ++)
		{
			if (i % 2 == 0)
			{
				tasks.put(i, new MyHungTask("task" + i, i, "a" + i, false));
			}
			else
			{
				tasks.put(i, new MyHungTask("task" + i, i, "a" + i, true));
			}
		}

		Date startTime = Calendar.getInstance().getTime();
		// Perform the map/reduce and wait until the results are obtained. 04/22/2018, Bing Li
		Map<Integer, MyHungResult> results = mp.map(tasks);
		Date endTime = Calendar.getInstance().getTime();
		
		System.out.println("It takes " + Time.getTimespanInMilliSecond(endTime, startTime) + " ms");
		// Print the size of the result. It usually should be equal to the size of the size of the tasks. 04/22/2018, Bing Li
		System.out.println("results size: " + results.size());

		// Print the results. 04/22/2018, Bing Li
		for (Map.Entry<Integer, MyHungResult> entry : results.entrySet())
		{
			System.out.println(entry.getKey() + ": " + entry.getValue().getResult());
		}
		
		// Dispose the instance of MRCore. 04/22/2018, Bing Li
		try
		{
			mp.dispose();
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		// Shutdown the scheduler. 04/22/2018, Bing Li
		Scheduler.GREATFREE().shutdown(ServerConfig.SCHEDULER_SHUTDOWN_TIMEOUT);
		
		System.out.println("Enter to exit ...");
		Scanner in = new Scanner(System.in);
		in.nextLine();
		in.close();
	}

}
