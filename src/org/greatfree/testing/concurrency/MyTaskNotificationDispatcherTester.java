package org.greatfree.testing.concurrency;

import java.util.Scanner;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.greatfree.concurrency.SharedThreadPool;
import org.greatfree.concurrency.reactive.NotificationTaskDispatcher;
import org.greatfree.data.ServerConfig;

// Created: 01/22/2019, Bing Li
class MyTaskNotificationDispatcherTester
{

	public static void main(String[] args) throws InterruptedException
	{
		ScheduledThreadPoolExecutor scheduler = new ScheduledThreadPoolExecutor(100);
		scheduler.setKeepAliveTime(1000, TimeUnit.MILLISECONDS);
		scheduler.allowCoreThreadTimeOut(true);
		
		NotificationTaskDispatcher<MyTaskNotification, MyTaskNotificationThread, MyTaskNotificationThreadCreator> dispatcher = new NotificationTaskDispatcher.NotificationTaskDispatcherBuilder<MyTaskNotification, MyTaskNotificationThread, MyTaskNotificationThreadCreator>()
				.poolSize(ServerConfig.REQUEST_DISPATCHER_POOL_SIZE)
				.threadCreator(new MyTaskNotificationThreadCreator())
//				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.notificationQueueSize(1)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
//				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(scheduler)
				.task(new MyNotificationTask())
				.build();

		Scanner in = new Scanner(System.in);
		
		SharedThreadPool.SHARED().init(100, 5000);

		for (int i = 0; i < 10; i++)
		{
			if (!dispatcher.isReady())
			{
				SharedThreadPool.SHARED().getPool().execute(dispatcher);
			}
			dispatcher.enqueue(new MyTaskNotification("key" + i));
		}
		
		System.out.println("Press Enter to exit ...");
		in.nextLine();
		
		dispatcher.dispose();
		SharedThreadPool.SHARED().dispose(1000);
		scheduler.shutdownNow();
		in.close();
	}

}
