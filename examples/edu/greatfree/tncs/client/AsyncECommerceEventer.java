package edu.greatfree.tncs.client;

import java.io.IOException;

import org.greatfree.client.AsyncRemoteEventer;
import org.greatfree.client.FreeClientPool;
import org.greatfree.concurrency.ThreadPool;
import org.greatfree.data.ClientConfig;

import edu.greatfree.tncs.message.Merchandise;
import edu.greatfree.tncs.message.PostMerchandiseNotification;

// Created: 05/18/2019, Bing Li
class AsyncECommerceEventer
{
	private AsyncRemoteEventer<PostMerchandiseNotification> eventer;
	
	private ThreadPool pool;
	private FreeClientPool clientPool;

	private AsyncECommerceEventer()
	{
	}

	private static AsyncECommerceEventer instance = new AsyncECommerceEventer();
	
	public static AsyncECommerceEventer CS()
	{
		if (instance == null)
		{
			instance = new AsyncECommerceEventer();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	public void dispose(long timeout) throws InterruptedException, IOException
	{
		this.eventer.dispose();
		this.clientPool.dispose();
		this.pool.shutdown(timeout);
	}

	public void init()
	{
		this.clientPool = new FreeClientPool(ClientConfig.CLIENT_POOL_SIZE);
		this.clientPool.setIdleChecker(ClientConfig.CLIENT_IDLE_CHECK_DELAY, ClientConfig.CLIENT_IDLE_CHECK_PERIOD, ClientConfig.CLIENT_MAX_IDLE_TIME);

		this.pool = new ThreadPool(ClientConfig.EVENTER_THREAD_POOL_SIZE, ClientConfig.EVENTER_THREAD_POOL_ALIVE_TIME);

		this.eventer = new AsyncRemoteEventer.AsyncRemoteEventerBuilder<PostMerchandiseNotification>()
				.clientPool(this.clientPool)
				.eventQueueSize(ClientConfig.ASYNC_EVENT_QUEUE_SIZE)
				.eventerSize(ClientConfig.ASYNC_EVENTER_SIZE)
				.eventingWaitTime(ClientConfig.ASYNC_EVENTING_WAIT_TIME)
				.eventerWaitTime(ClientConfig.ASYNC_EVENTER_WAIT_TIME)
				.waitRound(ClientConfig.ASYNC_EVENTER_WAIT_ROUND)
				.idleCheckDelay(ClientConfig.ASYNC_EVENT_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ClientConfig.ASYNC_EVENT_IDLE_CHECK_PERIOD)
				.schedulerPoolSize(ClientConfig.SCHEDULER_POOL_SIZE)
				.schedulerKeepAliveTime(ClientConfig.SCHEDULER_KEEP_ALIVE_TIME)
				.schedulerShutdownTimeout(ClientConfig.ASYNC_SCHEDULER_SHUTDOWN_TIMEOUT)
				.build();
	}
	
	public void postMerchandise(Merchandise mcd)
	{
		if (!this.eventer.isReady())
		{
			this.pool.execute(this.eventer);
		}
		this.eventer.notify(ECommerceClientConfig.ECOMMERCE_SERVER_ADDRESS, ECommerceClientConfig.ECOMMERCE_SERVER_PORT, new PostMerchandiseNotification(mcd));
	}
}
