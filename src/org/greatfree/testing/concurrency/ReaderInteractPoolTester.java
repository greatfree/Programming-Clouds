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
final class ReaderInteractPoolTester
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

		MyReaderB rb = new MyReaderB();
		ReaderPool<Query, Answer> readerB = new ReaderPool.ReaderPoolBuilder<Query, Answer>()
				.queueSize(ClientConfig.ASYNC_EVENT_QUEUE_SIZE)
				.readerSize(ClientConfig.ASYNC_EVENTER_SIZE)
				.poolingWaitTime(ClientConfig.ASYNC_EVENTING_WAIT_TIME)
				.notifierWaitTime(ClientConfig.ASYNC_EVENTER_WAIT_TIME)
				.idleCheckDelay(ClientConfig.ASYNC_EVENT_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ClientConfig.ASYNC_EVENT_IDLE_CHECK_PERIOD)
				.schedulerPoolSize(ClientConfig.SCHEDULER_POOL_SIZE)
				.schedulerKeepAliveTime(ClientConfig.SCHEDULER_KEEP_ALIVE_TIME)
				.reader(rb)
				.build();

		if (!readerA.isReady())
		{
			pool.execute(readerA);
		}
		
		if (!readerB.isReady())
		{
			pool.execute(readerB);
		}

		System.out.println("Start to read ...");
		Query query = new Query("hello query!");
		readerA.read(query);
		Answer answer = (Answer)ra.waitForResponse(query.getCollaboratorKey(), 6000);
		if (answer != null)
		{
			System.out.println("answer = " + answer.getAnswer());
			query = new Query(answer.getAnswer());
			readerB.read(query);
			answer = (Answer)rb.waitForResponse(query.getCollaboratorKey(), 6000);
			if (answer != null)
			{
				System.out.println("answer = " + answer.getAnswer());
			}
			else
			{
				System.out.println("answer is null");
			}
		}
		else
		{
			System.out.println("answer is null");
		}

		Scanner in = new Scanner(System.in);
		System.out.println("Press enter to exit ...");
		in.nextLine();

		readerA.dispose();
		readerB.dispose();
		pool.shutdown(0);
		in.close();
	}

}
