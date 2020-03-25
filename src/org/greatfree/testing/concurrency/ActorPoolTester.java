package org.greatfree.testing.concurrency;

import java.util.Scanner;

import org.greatfree.concurrency.AsyncPool;
import org.greatfree.concurrency.ThreadPool;
import org.greatfree.data.ClientConfig;

// Created: 09/10/2018, Bing Li
class ActorPoolTester
{

	public static void main(String[] args) throws InterruptedException
	{
		ThreadPool pool = new ThreadPool(ClientConfig.EVENTER_THREAD_POOL_SIZE, ClientConfig.EVENTER_THREAD_POOL_ALIVE_TIME);

		AsyncPool<MyNotification> actor = new AsyncPool.ActorPoolBuilder<MyNotification>()
				.messageQueueSize(ClientConfig.ASYNC_EVENT_QUEUE_SIZE)
				.actorSize(ClientConfig.ASYNC_EVENTER_SIZE)
				.poolingWaitTime(ClientConfig.ASYNC_EVENTING_WAIT_TIME)
				.actorWaitTime(ClientConfig.ASYNC_EVENTER_WAIT_TIME)
				.waitRound(ClientConfig.ASYNC_EVENTER_WAIT_ROUND)
				.idleCheckDelay(ClientConfig.ASYNC_EVENT_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ClientConfig.ASYNC_EVENT_IDLE_CHECK_PERIOD)
				.schedulerPoolSize(ClientConfig.SCHEDULER_POOL_SIZE)
				.schedulerKeepAliveTime(ClientConfig.SCHEDULER_KEEP_ALIVE_TIME)
				.actor(new MyActor())
				.build();
		
		if (!actor.isReady())
		{
			pool.execute(actor);
		}
		
		System.out.println("Start to perform ...");
		actor.perform(new MyNotification("Li", "Bing"));
		actor.perform(new MyNotification("Great", "Free"));
		System.out.println("Performed ...");

		Scanner in = new Scanner(System.in);

		System.out.println("Press enter to exit ...");
		in.nextLine();
		
		in.close();
		
		pool.shutdown(0);
		actor.dispose();
	}

}
