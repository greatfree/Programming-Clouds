package org.greatfree.testing.concurrency;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.greatfree.concurrency.Scheduler;
import org.greatfree.concurrency.mapreduce.MRCore;
import org.greatfree.data.ServerConfig;
import org.greatfree.util.Time;

// Created: 04/22/2018, Bing Li
class MapReduceTester
{

	public static void main(String[] args) throws InterruptedException
	{
		// Initialize the thread. 04/22/2018, Bing Li
//		SharedThreadPool.SHARED().init(ServerConfig.SHARED_THREAD_POOL_SIZE, ServerConfig.SHARED_THREAD_POOL_KEEP_ALIVE_TIME);
		// Initialize the scheduler. 04/22/2018, Bing Li
		Scheduler.PERIOD().init(ServerConfig.SCHEDULER_POOL_SIZE, ServerConfig.SCHEDULER_KEEP_ALIVE_TIME);

		// Initialize one instance of the MRCore. 04/22/2018, Bing Li
		MRCore<MyTask, MyResult, MyMRThread, MyMRThreadCreator> mp = new MRCore.MRCoreBuilder<MyTask, MyResult, MyMRThread, MyMRThreadCreator>()
//				.threadPool(SharedThreadPool.SHARED().getPool())
				.poolSize(100)
				.threadCreator(new MyMRThreadCreator())
				.taskSizePerThread(ServerConfig.TASK_SIZE_PER_THREAD)
				.waitTime(ServerConfig.MR_WAIT_TIME)
				.shutdownTime(ServerConfig.MR_SHUTDOWN_TIME)
				.scheduler(Scheduler.PERIOD().getScheduler())
				.build();

		// Assign the tasks for map/reduce. 04/22/2018, Bing Li
		Map<Integer, MyTask> tasks = new HashMap<Integer, MyTask>();
//		for (int i = 0; i < 50; i ++)
		for (int i = 0; i < 10; i ++)
		{
			tasks.put(i, new MyTask("task1", i, "a" + i));
		}

		Date startTime = Calendar.getInstance().getTime();
		// Perform the map/reduce and wait until the results are obtained. 04/22/2018, Bing Li
		Map<Integer, MyResult> results = mp.map(tasks);
		Date endTime = Calendar.getInstance().getTime();
		
		System.out.println("It takes " + Time.getTimespanInMilliSecond(endTime, startTime) + " ms");
		// Print the size of the result. It usually should be equal to the size of the size of the tasks. 04/22/2018, Bing Li
		System.out.println("results size: " + results.size());

		// Print the results. 04/22/2018, Bing Li
		for (Map.Entry<Integer, MyResult> entry : results.entrySet())
		{
			System.out.println(entry.getKey() + ": " + entry.getValue().getResult());
		}
		
		// Dispose the instance of MRCore. 04/22/2018, Bing Li
		mp.dispose();
		// Dispose the thread pool. 04/22/2018, Bing Li
//		SharedThreadPool.SHARED().dispose(ServerConfig.SERVER_SHUTDOWN_TIMEOUT);
		// Shutdown the scheduler. 04/22/2018, Bing Li
		Scheduler.PERIOD().shutdown(ServerConfig.SCHEDULER_SHUTDOWN_TIMEOUT);
	}

}
