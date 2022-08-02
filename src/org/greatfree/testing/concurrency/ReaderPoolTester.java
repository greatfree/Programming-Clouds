package org.greatfree.testing.concurrency;

import java.util.Scanner;

import org.greatfree.concurrency.ReaderPool;
import org.greatfree.concurrency.ThreadPool;
import org.greatfree.data.ClientConfig;

/**
 * 
 * @author libing
 * 
 * 08/01/2022
 *
 */
final class ReaderPoolTester
{

	public static void main(String[] args) throws InterruptedException
	{
		ThreadPool pool = new ThreadPool(ClientConfig.EVENTER_THREAD_POOL_SIZE, ClientConfig.EVENTER_THREAD_POOL_ALIVE_TIME);

		MyReaderA mr = new MyReaderA();
		ReaderPool<Query, Answer> reader = new ReaderPool.ReaderPoolBuilder<Query, Answer>()
				.queueSize(ClientConfig.ASYNC_EVENT_QUEUE_SIZE)
				.readerSize(ClientConfig.ASYNC_EVENTER_SIZE)
				.poolingWaitTime(ClientConfig.ASYNC_EVENTING_WAIT_TIME)
				.notifierWaitTime(ClientConfig.ASYNC_EVENTER_WAIT_TIME)
				.idleCheckDelay(ClientConfig.ASYNC_EVENT_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ClientConfig.ASYNC_EVENT_IDLE_CHECK_PERIOD)
				.schedulerPoolSize(ClientConfig.SCHEDULER_POOL_SIZE)
				.schedulerKeepAliveTime(ClientConfig.SCHEDULER_KEEP_ALIVE_TIME)
				.reader(mr)
				.build();

		if (!reader.isReady())
		{
			pool.execute(reader);
		}

		System.out.println("Start to read ...");
		Query query = new Query("hello query!");
		reader.read(query);
		Answer ma = (Answer)mr.waitForResponse(query.getCollaboratorKey(), 6000);
		if (ma != null)
		{
			System.out.println("answer = " + ma.getAnswer());
		}
		else
		{
			System.out.println("answer is null");
		}

		Scanner in = new Scanner(System.in);
		System.out.println("Press enter to exit ...");
		in.nextLine();

		pool.shutdown(0);
		reader.dispose();
		in.close();
	}

}
