package org.greatfree.testing.concurrency;

import java.util.Scanner;

import org.greatfree.concurrency.ReaderPool;
import org.greatfree.concurrency.Runner;
import org.greatfree.concurrency.ThreadPool;
import org.greatfree.data.ClientConfig;

/**
 * 
 * @author libing
 * 
 * 08/02/2022
 *
 */
final class ReaderPingPong
{

	public static void main(String[] args) throws InterruptedException
	{
		ThreadPool pool = new ThreadPool(ClientConfig.EVENTER_THREAD_POOL_SIZE, ClientConfig.EVENTER_THREAD_POOL_ALIVE_TIME);

		PingReader pir = new PingReader();
		ReaderPool<Query, Answer> pirPool = new ReaderPool.ReaderPoolBuilder<Query, Answer>()
				.queueSize(ClientConfig.ASYNC_EVENT_QUEUE_SIZE)
				.readerSize(ClientConfig.ASYNC_EVENTER_SIZE)
				.poolingWaitTime(ClientConfig.ASYNC_EVENTING_WAIT_TIME)
				.notifierWaitTime(ClientConfig.ASYNC_EVENTER_WAIT_TIME)
				.idleCheckDelay(ClientConfig.ASYNC_EVENT_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ClientConfig.ASYNC_EVENT_IDLE_CHECK_PERIOD)
				.schedulerPoolSize(ClientConfig.SCHEDULER_POOL_SIZE)
				.schedulerKeepAliveTime(ClientConfig.SCHEDULER_KEEP_ALIVE_TIME)
				.reader(pir)
				.build();

		PongReader por = new PongReader();
		ReaderPool<Query, Answer> porPool = new ReaderPool.ReaderPoolBuilder<Query, Answer>()
				.queueSize(ClientConfig.ASYNC_EVENT_QUEUE_SIZE)
				.readerSize(ClientConfig.ASYNC_EVENTER_SIZE)
				.poolingWaitTime(ClientConfig.ASYNC_EVENTING_WAIT_TIME)
				.notifierWaitTime(ClientConfig.ASYNC_EVENTER_WAIT_TIME)
				.idleCheckDelay(ClientConfig.ASYNC_EVENT_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ClientConfig.ASYNC_EVENT_IDLE_CHECK_PERIOD)
				.schedulerPoolSize(ClientConfig.SCHEDULER_POOL_SIZE)
				.schedulerKeepAliveTime(ClientConfig.SCHEDULER_KEEP_ALIVE_TIME)
				.reader(por)
				.build();

		if (!pirPool.isReady())
		{
			pool.execute(pirPool);
		}

		if (!porPool.isReady())
		{
			pool.execute(porPool);
		}

		Query ping = new Query("ping");
		Query pong = new Query();

		PongTask pot = new PongTask(pir, ping, porPool, pong, 10);
		Runner<PongTask> pongRunner = new Runner<PongTask>(pot);
		pongRunner.start();

		PingTask pit = new PingTask(por, pong, pirPool, ping);
		Runner<PingTask> pingRunner = new Runner<PingTask>(pit);
		pingRunner.start();

		pirPool.read(ping);

		Scanner in = new Scanner(System.in);
		System.out.println("Press enter to exit ...");
		in.nextLine();

		pingRunner.stop(1000);
		pongRunner.stop(1000);
		pirPool.dispose();
		porPool.dispose();
		pool.shutdown(0);
		in.close();
	}

}
