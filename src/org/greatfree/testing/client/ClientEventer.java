package org.greatfree.testing.client;

import java.io.IOException;

import org.greatfree.client.AsyncRemoteEventer;
import org.greatfree.client.SyncRemoteEventer;
import org.greatfree.concurrency.ThreadPool;
import org.greatfree.data.ClientConfig;
import org.greatfree.testing.data.Weather;
import org.greatfree.testing.message.ClientForAnycastNotification;
import org.greatfree.testing.message.ClientForBroadcastNotification;
import org.greatfree.testing.message.ClientForUnicastNotification;
import org.greatfree.testing.message.OnlineNotification;
import org.greatfree.testing.message.RegisterClientNotification;
import org.greatfree.testing.message.TestNotification;
import org.greatfree.testing.message.UnregisterClientNotification;
import org.greatfree.testing.message.WeatherNotification;
import org.greatfree.util.NodeID;

/*
 * The class is an example that applies SynchRemoteEventer and AsyncRemoteEventer. 11/05/2014, Bing Li
 */

// Created: 11/05/2014, Bing Li
public class ClientEventer
{
	// Declare the ip of the remote server. 11/07/2014, Bing Li
	private String ip;
	// Declare the port of the remote server. 11/07/2014, Bing Li
	private int port;
	// The synchronous eventer to send the online notification. 11/07/2014, Bing Li
	private SyncRemoteEventer<OnlineNotification> onlineEventer;
	// The synchronous eventer to send the registering notification. 11/07/2014, Bing Li
	private SyncRemoteEventer<RegisterClientNotification> registerClientEventer;
	// The synchronous eventer to send the unregistering notification. 11/07/2014, Bing Li
	private SyncRemoteEventer<UnregisterClientNotification> unregisterClientEventer;
	// The asynchronous eventer to send one instance of WeatherNotification to the remote server to set the value of the weather. 02/15/2016, Bing Li
	private AsyncRemoteEventer<WeatherNotification> weatherEventer;

	// The asynchronous eventer to send one instance of ClientForBroadcastNotification to the remote server. 02/15/2016, Bing Li
	private AsyncRemoteEventer<ClientForBroadcastNotification> broadcastEventer;
	// The asynchronous eventer to send one instance of ClientForUnicastNotification to the remote server. 02/15/2016, Bing Li
	private AsyncRemoteEventer<ClientForUnicastNotification> unicastEventer;
	// The asynchronous eventer to send one instance of ClientForAnycastNotification to the remote server. 02/15/2016, Bing Li
	private AsyncRemoteEventer<ClientForAnycastNotification> anycastEventer;
	
	private AsyncRemoteEventer<TestNotification> testEventer;

	// A thread pool to assist sending notification asynchronously. 11/07/2014, Bing Li
	private ThreadPool pool;

	/*
	 * Initialize. 11/07/2014, Bing Li
	 */
	private ClientEventer()
	{
	}

	/*
	 * A singleton implementation. 11/07/2014, Bing Li
	 */
	private static ClientEventer instance = new ClientEventer();
	
	public static ClientEventer NOTIFY()
	{
		if (instance == null)
		{
			instance = new ClientEventer();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	/*
	 * Dispose the eventers. 11/07/2014, Bing Li
	 */
	public void dispose(long timeout) throws InterruptedException, IOException
	{
		this.onlineEventer.dispose();
		this.registerClientEventer.dispose();
		this.unregisterClientEventer.dispose();
		this.weatherEventer.dispose();

		this.broadcastEventer.dispose();
		this.unicastEventer.dispose();
		this.anycastEventer.dispose();
		
		this.testEventer.dispose();

		// Shutdown the thread pool. 11/07/2014, Bing Li
		this.pool.shutdown(timeout);
	}

	/*
	 * Initialize the eventers. 11/07/2014, Bing Li
	 */
	public void init(String ip, int port)
	{
		this.ip = ip;
		this.port = port;
		this.pool = new ThreadPool(ClientConfig.EVENTER_THREAD_POOL_SIZE, ClientConfig.EVENTER_THREAD_POOL_ALIVE_TIME);
		// Initialize the synchronous eventer. The FreeClient pool is one parameter for the initialization. The clients needs to be managed by the pool. 02/15/2016, Bing Li
		this.onlineEventer = new SyncRemoteEventer<OnlineNotification>(ClientPool.LOCAL().getPool());
		// Initialize the synchronous eventer. The FreeClient pool is one parameter for the initialization. The clients needs to be managed by the pool. 02/15/2016, Bing Li
		this.registerClientEventer = new SyncRemoteEventer<RegisterClientNotification>(ClientPool.LOCAL().getPool());
		// Initialize the synchronous eventer. The FreeClient pool is one parameter for the initialization. The clients needs to be managed by the pool. 02/15/2016, Bing Li
		this.unregisterClientEventer = new SyncRemoteEventer<UnregisterClientNotification>(ClientPool.LOCAL().getPool());

		// Initialize the asynchronous eventer. Since the eventer is sent out to the remote server asynchronously, more parameters are required to set. 02/15/2016, Bing Li
//		this.weatherEventer = new AsyncRemoteEventer<WeatherNotification>(ClientPool.LOCAL().getPool(), this.pool, ClientConfig.ASYNC_EVENT_QUEUE_SIZE, ClientConfig.ASYNC_EVENTER_SIZE, ClientConfig.ASYNC_EVENTING_WAIT_TIME, ClientConfig.ASYNC_EVENTER_WAIT_TIME, ClientConfig.ASYNC_EVENTER_WAIT_ROUND, ClientConfig.ASYNC_EVENT_IDLE_CHECK_DELAY, ClientConfig.ASYNC_EVENT_IDLE_CHECK_PERIOD, Scheduler.GREATFREE().getSchedulerPool());
//		this.weatherEventer = new AsyncRemoteEventer<WeatherNotification>(ClientPool.LOCAL().getPool(), this.pool, ClientConfig.ASYNC_EVENT_QUEUE_SIZE, ClientConfig.ASYNC_EVENTER_SIZE, ClientConfig.ASYNC_EVENTING_WAIT_TIME, ClientConfig.ASYNC_EVENTER_WAIT_TIME, ClientConfig.ASYNC_EVENTER_WAIT_ROUND, ClientConfig.ASYNC_EVENT_IDLE_CHECK_DELAY, ClientConfig.ASYNC_EVENT_IDLE_CHECK_PERIOD, ClientConfig.SCHEDULER_POOL_SIZE, ClientConfig.SCHEDULER_KEEP_ALIVE_TIME);
		
		this.weatherEventer = new AsyncRemoteEventer.AsyncRemoteEventerBuilder<WeatherNotification>()
				.clientPool(ClientPool.LOCAL().getPool())
//				.threadPool(this.pool)
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

		// Initialize the asynchronous eventer. Since the eventer is sent out to the remote server asynchronously, more parameters are required to set. 02/15/2016, Bing Li
//		this.broadcastEventer = new AsyncRemoteEventer<ClientForBroadcastNotification>(ClientPool.LOCAL().getPool(), this.pool, ClientConfig.ASYNC_EVENT_QUEUE_SIZE, ClientConfig.ASYNC_EVENTER_SIZE, ClientConfig.ASYNC_EVENTING_WAIT_TIME, ClientConfig.ASYNC_EVENTER_WAIT_TIME, ClientConfig.ASYNC_EVENTER_WAIT_ROUND, ClientConfig.ASYNC_EVENT_IDLE_CHECK_DELAY, ClientConfig.ASYNC_EVENT_IDLE_CHECK_PERIOD, Scheduler.GREATFREE().getSchedulerPool());
//		this.broadcastEventer = new AsyncRemoteEventer<ClientForBroadcastNotification>(ClientPool.LOCAL().getPool(), this.pool, ClientConfig.ASYNC_EVENT_QUEUE_SIZE, ClientConfig.ASYNC_EVENTER_SIZE, ClientConfig.ASYNC_EVENTING_WAIT_TIME, ClientConfig.ASYNC_EVENTER_WAIT_TIME, ClientConfig.ASYNC_EVENTER_WAIT_ROUND, ClientConfig.ASYNC_EVENT_IDLE_CHECK_DELAY, ClientConfig.ASYNC_EVENT_IDLE_CHECK_PERIOD, ClientConfig.SCHEDULER_POOL_SIZE, ClientConfig.SCHEDULER_KEEP_ALIVE_TIME);
		
		this.broadcastEventer = new AsyncRemoteEventer.AsyncRemoteEventerBuilder<ClientForBroadcastNotification>()
				.clientPool(ClientPool.LOCAL().getPool())
//				.threadPool(this.pool)
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

		// Initialize the asynchronous eventer. Since the eventer is sent out to the remote server asynchronously, more parameters are required to set. 02/15/2016, Bing Li
//		this.unicastEventer = new AsyncRemoteEventer<ClientForUnicastNotification>(ClientPool.LOCAL().getPool(), this.pool, ClientConfig.ASYNC_EVENT_QUEUE_SIZE, ClientConfig.ASYNC_EVENTER_SIZE, ClientConfig.ASYNC_EVENTING_WAIT_TIME, ClientConfig.ASYNC_EVENTER_WAIT_TIME, ClientConfig.ASYNC_EVENTER_WAIT_ROUND, ClientConfig.ASYNC_EVENT_IDLE_CHECK_DELAY, ClientConfig.ASYNC_EVENT_IDLE_CHECK_PERIOD, Scheduler.GREATFREE().getSchedulerPool());
//		this.unicastEventer = new AsyncRemoteEventer<ClientForUnicastNotification>(ClientPool.LOCAL().getPool(), this.pool, ClientConfig.ASYNC_EVENT_QUEUE_SIZE, ClientConfig.ASYNC_EVENTER_SIZE, ClientConfig.ASYNC_EVENTING_WAIT_TIME, ClientConfig.ASYNC_EVENTER_WAIT_TIME, ClientConfig.ASYNC_EVENTER_WAIT_ROUND, ClientConfig.ASYNC_EVENT_IDLE_CHECK_DELAY, ClientConfig.ASYNC_EVENT_IDLE_CHECK_PERIOD, ClientConfig.SCHEDULER_POOL_SIZE, ClientConfig.SCHEDULER_KEEP_ALIVE_TIME);
		
		this.unicastEventer = new AsyncRemoteEventer.AsyncRemoteEventerBuilder<ClientForUnicastNotification>()
				.clientPool(ClientPool.LOCAL().getPool())
//				.threadPool(this.pool)
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

		// Initialize the asynchronous eventer. Since the eventer is sent out to the remote server asynchronously, more parameters are required to set. 02/15/2016, Bing Li
//		this.anycastEventer = new AsyncRemoteEventer<ClientForAnycastNotification>(ClientPool.LOCAL().getPool(), this.pool, ClientConfig.ASYNC_EVENT_QUEUE_SIZE, ClientConfig.ASYNC_EVENTER_SIZE, ClientConfig.ASYNC_EVENTING_WAIT_TIME, ClientConfig.ASYNC_EVENTER_WAIT_TIME, ClientConfig.ASYNC_EVENTER_WAIT_ROUND, ClientConfig.ASYNC_EVENT_IDLE_CHECK_DELAY, ClientConfig.ASYNC_EVENT_IDLE_CHECK_PERIOD, Scheduler.GREATFREE().getSchedulerPool());
//		this.anycastEventer = new AsyncRemoteEventer<ClientForAnycastNotification>(ClientPool.LOCAL().getPool(), this.pool, ClientConfig.ASYNC_EVENT_QUEUE_SIZE, ClientConfig.ASYNC_EVENTER_SIZE, ClientConfig.ASYNC_EVENTING_WAIT_TIME, ClientConfig.ASYNC_EVENTER_WAIT_TIME, ClientConfig.ASYNC_EVENTER_WAIT_ROUND, ClientConfig.ASYNC_EVENT_IDLE_CHECK_DELAY, ClientConfig.ASYNC_EVENT_IDLE_CHECK_PERIOD, ClientConfig.SCHEDULER_POOL_SIZE, ClientConfig.SCHEDULER_KEEP_ALIVE_TIME);
		
		this.anycastEventer = new AsyncRemoteEventer.AsyncRemoteEventerBuilder<ClientForAnycastNotification>()
				.clientPool(ClientPool.LOCAL().getPool())
//				.threadPool(this.pool)
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
		
//		this.testEventer = new AsyncRemoteEventer<TestNotification>(ClientPool.LOCAL().getPool(), this.pool, ClientConfig.ASYNC_EVENT_QUEUE_SIZE, ClientConfig.ASYNC_EVENTER_SIZE, ClientConfig.ASYNC_EVENTING_WAIT_TIME, ClientConfig.ASYNC_EVENTER_WAIT_TIME, ClientConfig.ASYNC_EVENTER_WAIT_ROUND, ClientConfig.ASYNC_EVENT_IDLE_CHECK_DELAY, ClientConfig.ASYNC_EVENT_IDLE_CHECK_PERIOD, Scheduler.GREATFREE().getSchedulerPool());
//		this.testEventer = new AsyncRemoteEventer<TestNotification>(ClientPool.LOCAL().getPool(), this.pool, ClientConfig.ASYNC_EVENT_QUEUE_SIZE, ClientConfig.ASYNC_EVENTER_SIZE, ClientConfig.ASYNC_EVENTING_WAIT_TIME, ClientConfig.ASYNC_EVENTER_WAIT_TIME, ClientConfig.ASYNC_EVENTER_WAIT_ROUND, ClientConfig.ASYNC_EVENT_IDLE_CHECK_DELAY, ClientConfig.ASYNC_EVENT_IDLE_CHECK_PERIOD, ClientConfig.SCHEDULER_POOL_SIZE, ClientConfig.SCHEDULER_KEEP_ALIVE_TIME);
		
		this.testEventer = new AsyncRemoteEventer.AsyncRemoteEventerBuilder<TestNotification>()
				.clientPool(ClientPool.LOCAL().getPool())
//				.threadPool(this.pool)
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

	/*
	 * Send the online notification to the remote server in a synchronous manner. 11/07/2014, Bing Li
	 */
	public void notifyOnline() throws IOException, InterruptedException
	{
		this.onlineEventer.notify(this.ip, this.port, new OnlineNotification());
	}

	/*
	 * Send the registering notification to the remote server in a synchronous manner. 11/07/2014, Bing Li
	 */
	public void register()
	{
		try
		{
			this.registerClientEventer.notify(this.ip, this.port, new RegisterClientNotification(NodeID.DISTRIBUTED().getKey()));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}

	/*
	 * Send the unregistering notification to the remote server in a synchronous manner. 11/07/2014, Bing Li
	 */
	public void unregister()
	{
		try
		{
			this.unregisterClientEventer.notify(this.ip, this.port, new UnregisterClientNotification(NodeID.DISTRIBUTED().getKey()));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}

	/*
	 * Send the weather notification to the remote server in an asynchronous manner. 11/07/2014, Bing Li
	 */
	public void notifyWeather(Weather weather)
	{
		if (!this.weatherEventer.isReady())
		{
			this.pool.execute(this.weatherEventer);
		}
		this.weatherEventer.notify(this.ip, this.port, new WeatherNotification(weather));
	}

	/*
	 * Send the broadcast notification to the remote server in an asynchronous manner. 11/07/2014, Bing Li
	 */
	public void notifyBroadcastly(String message)
	{
		if (!this.broadcastEventer.isReady())
		{
			this.pool.execute(this.broadcastEventer);
		}
		this.broadcastEventer.notify(this.ip, this.port, new ClientForBroadcastNotification(message));
	}

	/*
	 * Send the unicast notification to the remote server in an asynchronous manner. 11/07/2014, Bing Li
	 */
	public void notifyUnicastly(String message)
	{
		if (!this.unicastEventer.isReady())
		{
			this.pool.execute(this.unicastEventer);
		}
		this.unicastEventer.notify(this.ip, this.port, new ClientForUnicastNotification(message));
	}

	/*
	 * Send the unicast notification to the remote server in an asynchronous manner. 11/07/2014, Bing Li
	 */
	public void notifyAnycastly(String message)
	{
		if (!this.anycastEventer.isReady())
		{
			this.pool.execute(this.anycastEventer);
		}
		this.anycastEventer.notify(this.ip, this.port, new ClientForAnycastNotification(message));
	}
	
	public void notifyTest(String message)
	{
		if (!this.testEventer.isReady())
		{
			this.pool.execute(this.testEventer);
		}
		this.testEventer.notify(this.ip, this.port, new TestNotification(message));
	}
}
