package org.greatfree.testing.concurrency.nasync;

import java.util.Scanner;

import org.greatfree.concurrency.NotifierPool;
import org.greatfree.concurrency.ThreadPool;
import org.greatfree.data.ClientConfig;

// Created: 09/10/2018, Bing Li
class NotifierPoolTester
{

	public static void main(String[] args) throws InterruptedException
	{
		ThreadPool pool = new ThreadPool(ClientConfig.EVENTER_THREAD_POOL_SIZE, ClientConfig.EVENTER_THREAD_POOL_ALIVE_TIME);

		NotifierPool<MyNotification> notifier = new NotifierPool.NotifierPoolBuilder<MyNotification>()
				.queueSize(ClientConfig.ASYNC_EVENT_QUEUE_SIZE)
				.notifierSize(ClientConfig.ASYNC_EVENTER_SIZE)
				.poolingWaitTime(ClientConfig.ASYNC_EVENTING_WAIT_TIME)
				.notifierWaitTime(ClientConfig.ASYNC_EVENTER_WAIT_TIME)
				.idleCheckDelay(ClientConfig.ASYNC_EVENT_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ClientConfig.ASYNC_EVENT_IDLE_CHECK_PERIOD)
				.schedulerPoolSize(ClientConfig.SCHEDULER_POOL_SIZE)
				.schedulerKeepAliveTime(ClientConfig.SCHEDULER_KEEP_ALIVE_TIME)
				.notifier(new MyNotifier())
				.build();
		
		if (!notifier.isReady())
		{
			pool.execute(notifier);
		}
		
		System.out.println("Start to perform ...");
		notifier.notify(new MyNotification("Li", "Bing"));
		notifier.notify(new MyNotification("Great", "Free"));
		System.out.println("Performed ...");

		Scanner in = new Scanner(System.in);

		System.out.println("Press enter to exit ...");
		in.nextLine();
		
		in.close();
		
		pool.shutdown(0);
		notifier.dispose();
	}

}
