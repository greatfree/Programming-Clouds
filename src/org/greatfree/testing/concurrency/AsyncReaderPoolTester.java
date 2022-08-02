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
 * 08/01/2022
 *
 */
final class AsyncReaderPoolTester
{
	public static void main(String[] args) throws InterruptedException
	{
		ThreadPool pool = new ThreadPool(ClientConfig.EVENTER_THREAD_POOL_SIZE, ClientConfig.EVENTER_THREAD_POOL_ALIVE_TIME);

		MyReaderA ra = new MyReaderA();
		ReaderPool<Query, Answer> readerA = new ReaderPool.ReaderPoolBuilder<Query, Answer>()
				.queueSize(ClientConfig.ASYNC_EVENT_QUEUE_SIZE)
				.readerSize(ClientConfig.ASYNC_EVENTER_SIZE)
				.poolingWaitTime(ClientConfig.ASYNC_EVENTING_WAIT_TIME)
				.notifierWaitTime(ClientConfig.ASYNC_EVENTER_WAIT_TIME)
				.idleCheckDelay(ClientConfig.ASYNC_EVENT_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ClientConfig.ASYNC_EVENT_IDLE_CHECK_PERIOD)
				.schedulerPoolSize(ClientConfig.SCHEDULER_POOL_SIZE)
				.schedulerKeepAliveTime(ClientConfig.SCHEDULER_KEEP_ALIVE_TIME)
				.reader(ra)
				.build();

		if (!readerA.isReady())
		{
			pool.execute(readerA);
		}
		
		System.out.println("Start to read ...");
		Query query = new Query("hello query!");

		WaitTask wt = new WaitTask(ra, query);
		Runner<WaitTask> runner = new Runner<WaitTask>(wt);
		runner.start();

		readerA.read(query);

		Scanner in = new Scanner(System.in);
		System.out.println("Press enter to exit ...");
		in.nextLine();

		runner.stop(1000);
		readerA.dispose();
		pool.shutdown(0);
		in.close();
	}
}
