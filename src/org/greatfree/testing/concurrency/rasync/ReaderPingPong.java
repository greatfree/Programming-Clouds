package org.greatfree.testing.concurrency.rasync;

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
		ReaderPool<Query, Answer> pingPool = new ReaderPool.ReaderPoolBuilder<Query, Answer>()
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
		ReaderPool<Query, Answer> pongPool = new ReaderPool.ReaderPoolBuilder<Query, Answer>()
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

		if (!pingPool.isReady())
		{
			pool.execute(pingPool);
		}

		if (!pongPool.isReady())
		{
			pool.execute(pongPool);
		}

		Query ping = new Query("ping");
		Query pong = new Query("pong");

//		PongTask pot = new PongTask(pir, ping, pongPool, pong, 10);
		PongTask pot = new PongTask(pir, pongPool, ping.getCollaboratorKey(), 10);
		Runner<PongTask> pongRunner = new Runner<PongTask>(pot);
		pongRunner.start();

//		PingTask pit = new PingTask(por, pong, pingPool, ping);
		PingTask pit = new PingTask(por, pingPool, pong.getCollaboratorKey());
		Runner<PingTask> pingRunner = new Runner<PingTask>(pit);
		pingRunner.start();

		pingPool.read(ping);

		Scanner in = new Scanner(System.in);
		System.out.println("Press enter to exit ...");
		in.nextLine();

		pingRunner.stop(1000);
		pongRunner.stop(1000);
		pingPool.dispose();
		pongPool.dispose();
		pool.shutdown(0);
		in.close();
	}

}
