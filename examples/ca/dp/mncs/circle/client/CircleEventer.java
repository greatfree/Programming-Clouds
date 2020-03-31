package ca.dp.mncs.circle.client;

import java.io.IOException;

import org.greatfree.client.AsyncRemoteEventer;
import org.greatfree.client.FreeClientPool;
import org.greatfree.concurrency.ThreadPool;
import org.greatfree.data.ClientConfig;
import org.greatfree.dip.p2p.RegistryConfig;

import ca.dp.mncs.circle.message.CircleConfig;
import ca.dp.mncs.circle.message.LikeNotification;

// Created: 02/28/2020, Bing Li
class CircleEventer
{
	private AsyncRemoteEventer<LikeNotification> eventer;
	private ThreadPool pool;
	private FreeClientPool clientPool;

	private CircleEventer()
	{
	}

	/*
	 * A singleton implementation. 11/07/2014, Bing Li
	 */
	private static CircleEventer instance = new CircleEventer();
	
	public static CircleEventer RE()
	{
		if (instance == null)
		{
			instance = new CircleEventer();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	public void dispose(long timeout) throws IOException, InterruptedException
	{
		this.clientPool.dispose();
		this.eventer.dispose();
		this.pool.shutdown(timeout);
	}
	
	public void init()
	{
		this.clientPool = new FreeClientPool(RegistryConfig.CLIENT_POOL_SIZE);
		this.clientPool.setIdleChecker(RegistryConfig.CLIENT_IDLE_CHECK_DELAY, RegistryConfig.CLIENT_IDLE_CHECK_PERIOD, RegistryConfig.CLIENT_MAX_IDLE_TIME);

		this.pool = new ThreadPool(ClientConfig.EVENTER_THREAD_POOL_SIZE, ClientConfig.EVENTER_THREAD_POOL_ALIVE_TIME);

		this.eventer = new AsyncRemoteEventer.AsyncRemoteEventerBuilder<LikeNotification>()
				.clientPool(this.clientPool)
				.eventQueueSize(RegistryConfig.ASYNC_EVENT_QUEUE_SIZE)
				.eventerSize(RegistryConfig.ASYNC_EVENTER_SIZE)
				.eventingWaitTime(RegistryConfig.ASYNC_EVENTING_WAIT_TIME)
				.eventerWaitTime(RegistryConfig.ASYNC_EVENTER_WAIT_TIME)
				.waitRound(RegistryConfig.ASYNC_EVENTER_WAIT_ROUND)
				.idleCheckDelay(RegistryConfig.ASYNC_EVENT_IDLE_CHECK_DELAY)
				.idleCheckPeriod(RegistryConfig.ASYNC_EVENT_IDLE_CHECK_PERIOD)
				.schedulerPoolSize(ClientConfig.SCHEDULER_POOL_SIZE)
				.schedulerKeepAliveTime(ClientConfig.SCHEDULER_KEEP_ALIVE_TIME)
				.schedulerShutdownTimeout(ClientConfig.ASYNC_SCHEDULER_SHUTDOWN_TIMEOUT)
				.build();
	}
	
	public void like(String postID)
	{
		if (!this.eventer.isReady())
		{
			this.pool.execute(this.eventer);
		}
		this.eventer.notify(CircleConfig.CIRCLE_SERVER_IP, CircleConfig.CIRCLE_SERVER_PORT, new LikeNotification(postID));
	}
	
}
