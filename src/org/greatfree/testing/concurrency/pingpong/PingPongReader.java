package org.greatfree.testing.concurrency.pingpong;

import org.greatfree.concurrency.ReaderPool;
import org.greatfree.concurrency.Runner;
import org.greatfree.concurrency.ThreadPool;

/**
 * 
 * @author libing
 * 
 * 08/07/2022
 *
 */
class PingPongReader
{
	public static void main(String[] args) throws InterruptedException
	{
		ThreadPool pool = new ThreadPool(NotifierPoolConfig.THREAD_POOL_SIZE, NotifierPoolConfig.THREAD_POOL_KEEP_ALIVE_TIME);

		PingReader pir = new PingReader();
		ReaderPool<PingRequest, PingResponse> pingPool = new ReaderPool.ReaderPoolBuilder<PingRequest, PingResponse>()
				.queueSize(NotifierPoolConfig.QUEUE_SIZE)
				.readerSize(ReaderPoolConfig.READER_SIZE)
				.poolingWaitTime(NotifierPoolConfig.POOLING_WAIT_TIME)
				.notifierWaitTime(ReaderPoolConfig.READER_WAIT_TIME)
				.idleCheckDelay(NotifierPoolConfig.IDLE_CHECK_DELAY)
				.idleCheckPeriod(NotifierPoolConfig.IDLE_CHECK_PERIOD)
				.schedulerPoolSize(NotifierPoolConfig.SCHEDULER_POOL_SIZE)
				.schedulerKeepAliveTime(NotifierPoolConfig.SCHEDULER_KEEP_ALIVE_TIME)
				.reader(pir)
				.build();

		PongReader por = new PongReader();
		ReaderPool<PongRequest, PongResponse> pongPool = new ReaderPool.ReaderPoolBuilder<PongRequest, PongResponse>()
				.queueSize(NotifierPoolConfig.QUEUE_SIZE)
				.readerSize(ReaderPoolConfig.READER_SIZE)
				.poolingWaitTime(NotifierPoolConfig.POOLING_WAIT_TIME)
				.notifierWaitTime(ReaderPoolConfig.READER_WAIT_TIME)
				.idleCheckDelay(NotifierPoolConfig.IDLE_CHECK_DELAY)
				.idleCheckPeriod(NotifierPoolConfig.IDLE_CHECK_PERIOD)
				.schedulerPoolSize(NotifierPoolConfig.SCHEDULER_POOL_SIZE)
				.schedulerKeepAliveTime(NotifierPoolConfig.SCHEDULER_KEEP_ALIVE_TIME)
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

		PingRequest ping = new PingRequest("ping");
		PongRequest pong = new PongRequest("pong");

//		PongTask pot = new PongTask(pir, pongPool, ping.getCollaboratorKey(), pong.getCollaboratorKey(), 10);
		PongTask pot = new PongTask(pir, pongPool, ping.getCollaboratorKey(), pong.getCollaboratorKey(), 5);
		Runner<PongTask> pongRunner = new Runner<PongTask>(pot);
		pongRunner.start();

		PingTask pit = new PingTask(por, pingPool, ping.getCollaboratorKey(), pong.getCollaboratorKey());
		Runner<PingTask> pingRunner = new Runner<PingTask>(pit);
		pingRunner.start();

		pingPool.read(ping);

		System.out.println("Press enter to exit ...");
		ClientConfig.INPUT.nextLine();

		pingRunner.stop(ServerConfig.SERVER_SHUTDOWN_TIMEOUT);
		pongRunner.stop(ServerConfig.SERVER_SHUTDOWN_TIMEOUT);
		pingPool.dispose();
		pongPool.dispose();
		pool.shutdown(ServerConfig.SERVER_SHUTDOWN_TIMEOUT);
	}
}
