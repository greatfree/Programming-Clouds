package org.greatfree.testing.concurrency;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import org.greatfree.concurrency.Scheduler;
import org.greatfree.concurrency.mapreduce.MapReduce;
import org.greatfree.concurrency.mapreduce.MapReduceBuilder;
import org.greatfree.concurrency.mapreduce.Sequence;
import org.greatfree.data.ServerConfig;
import org.greatfree.util.Time;
import org.greatfree.util.Tools;

// Created: 01/27/2019, Bing Li
class ContainedMRTester
{

	public static void main(String[] args) throws InterruptedException
	{
		// Initialize the thread. 04/22/2018, Bing Li
//		SharedThreadPool.SHARED().init(ServerConfig.SHARED_THREAD_POOL_SIZE, ServerConfig.SHARED_THREAD_POOL_KEEP_ALIVE_TIME);
		// Initialize the scheduler. 04/22/2018, Bing Li
		Scheduler.GREATFREE().init(ServerConfig.SCHEDULER_POOL_SIZE, ServerConfig.SCHEDULER_KEEP_ALIVE_TIME);

//		MapReduce mr = new MapReduce(SharedThreadPool.SHARED().getPool(), Scheduler.GREATFREE().getSchedulerPool(), new MyMRService(), 1);
//		MapReduce mr = new MapReduce(SharedThreadPool.SHARED().getPool(), Scheduler.GREATFREE().getSchedulerPool(), 1, 2000, 4000);
		MapReduce mr = new MapReduceBuilder()
//				.threadPool(SharedThreadPool.SHARED().getPool())
				.poolSize(100)
				.scheduler(Scheduler.GREATFREE().getScheduler())
				.queueSize(1)
//				.waitTime(20000)
				.waitTime(2000)
				.shutdownTime(100000)
//				.shutdownTime(100)
				.schedulerDelay(2000)
				.schedulerPeriod(4000)
				.build();
		
		String taskKey = "task1";

//		Map<Integer, MyTask> tasks = new HashMap<Integer, MyTask>();
//		for (int i = 0; i < 50; i++)
//		for (int i = 0; i < 10; i++)
		for (int i = 0; i < 7; i++)
		{
			mr.add(new MyTask(taskKey, i, "a" + i));
//			tasks.put(i, new MyTask(i, "a" + i));
		}

		System.out.println("tasks size: " + mr.getTaskSize(taskKey));
//		System.out.println("tasks size: " + tasks.size());

		Date startTime = Calendar.getInstance().getTime();
		// Perform the map/reduce and wait until the results are obtained. 04/22/2018, Bing Li
//		Map<Integer, Sequence> results = mr.compute(taskKey, new MyMRService());
		Map<Integer, Sequence> results = mr.compute(new MyMRService(taskKey));
		System.out.println("results size: " + results.size());
		Map<Integer, MyResult> myResults = Tools.filter(results, MyResult.class);
		Date endTime = Calendar.getInstance().getTime();
		
		System.out.println("It takes " + Time.getTimespanInMilliSecond(endTime, startTime) + " ms");
		// Print the size of the result. It usually should be equal to the size of the size of the tasks. 04/22/2018, Bing Li
		System.out.println("myResults size: " + myResults.size());

		// Print the results. 04/22/2018, Bing Li
		for (Map.Entry<Integer, MyResult> entry : myResults.entrySet())
		{
			System.out.println(entry.getKey() + ": " + entry.getValue().getResult());
		}

		mr.dispose();

		// Dispose the thread pool. 04/22/2018, Bing Li
//		SharedThreadPool.SHARED().dispose(ServerConfig.SERVER_SHUTDOWN_TIMEOUT);
		// Shutdown the scheduler. 04/22/2018, Bing Li
		Scheduler.GREATFREE().shutdown(ServerConfig.SCHEDULER_SHUTDOWN_TIMEOUT);
	}

}
