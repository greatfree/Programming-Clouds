package org.greatfree.client;

import java.io.IOException;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;

import org.greatfree.concurrency.ThreadPool;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.message.ServerMessage;
import org.greatfree.util.Builder;
import org.greatfree.util.NodeID;

// Created: 08/02/2018, Bing Li
public class CSClient
{
	// Declare an eventer to send notifications synchronously. 05/01/2017, Bing Li
	private SyncRemoteEventer<ServerMessage> syncEventer;

	// Declare an eventer to send notifications asynchronously. 05/01/2017, Bing Li
	private AsyncRemoteEventer<ServerMessage> asyncEventer;

	// The TCP client pool that sends notifications to the chatting server. 05/27/2017, Bing Li
	private FreeClientPool clientPool;

	// A thread pool to assist sending notification asynchronously. 11/07/2014, Bing Li
	private ThreadPool pool;
	
//	private ScheduledThreadPoolExecutor scheduler;

	public CSClient(CSClientBuilder builder)
	{
		this.clientPool = new FreeClientPool(builder.getClientPoolSize());
		this.clientPool.setIdleChecker(builder.getClientIdleCheckDelay(), builder.getClientIdleCheckPeriod(), builder.getClientMaxIdleTime());
		
//		this.pool = builder.getPool();
		
		this.syncEventer = new SyncRemoteEventer<ServerMessage>(this.clientPool);

		// I decide to assign the asynchronous eventer has a scheduler belonged to itself. It is not a heavy burden.er classes. 03/20/2019, Bing Li
		/*
		if (builder.getScheduler() != null)
		{
			this.scheduler = builder.getScheduler();
			this.asyncEventer = new AsyncRemoteEventer.AsyncRemoteEventerBuilder<ServerMessage>()
					.clientPool(this.clientPool)
					.eventQueueSize(builder.getAsyncEventQueueSize())
					.eventerSize(builder.getAsyncEventerSize())
					.eventingWaitTime(builder.getAsyncEventingWaitTime())
					.eventerWaitTime(builder.getAsyncEventerWaitTime())
					.waitRound(builder.getAsyncEventerWaitRound())
					.idleCheckDelay(builder.getAsyncEventIdleCheckDelay())
					.idleCheckPeriod(builder.getAsyncEventIdleCheckPeriod())
					.scheduler(builder.getScheduler())
					.schedulerShutdownTimeout(builder.getAsyncSchedulerShutdownTimeout())
					.build();
		}
		else
		{
			this.asyncEventer = new AsyncRemoteEventer.AsyncRemoteEventerBuilder<ServerMessage>()
					.clientPool(this.clientPool)
					.eventQueueSize(builder.getAsyncEventQueueSize())
					.eventerSize(builder.getAsyncEventerSize())
					.eventingWaitTime(builder.getAsyncEventingWaitTime())
					.eventerWaitTime(builder.getAsyncEventerWaitTime())
					.waitRound(builder.getAsyncEventerWaitRound())
					.idleCheckDelay(builder.getAsyncEventIdleCheckDelay())
					.idleCheckPeriod(builder.getAsyncEventIdleCheckPeriod())
					.schedulerPoolSize(builder.getSchedulerPoolSize())
					.schedulerKeepAliveTime(builder.getSchedulerKeepAliveTime())
					.schedulerShutdownTimeout(builder.getAsyncSchedulerShutdownTimeout())
					.build();
			
			this.scheduler = this.asyncEventer.getScheduler();
		}
		*/
		this.asyncEventer = new AsyncRemoteEventer.AsyncRemoteEventerBuilder<ServerMessage>()
				.clientPool(this.clientPool)
				.eventQueueSize(builder.getAsyncEventQueueSize())
				.eventerSize(builder.getAsyncEventerSize())
				.eventingWaitTime(builder.getAsyncEventingWaitTime())
				.eventerWaitTime(builder.getAsyncEventerWaitTime())
				.waitRound(builder.getAsyncEventerWaitRound())
				.idleCheckDelay(builder.getAsyncEventIdleCheckDelay())
				.idleCheckPeriod(builder.getAsyncEventIdleCheckPeriod())
				.schedulerPoolSize(builder.getSchedulerPoolSize())
				.schedulerKeepAliveTime(builder.getSchedulerKeepAliveTime())
				.schedulerShutdownTimeout(builder.getAsyncSchedulerShutdownTimeout())
				.build();
		
		RemoteReader.REMOTE().init(builder.getReaderClientSize());
	}
	
	public static class CSClientBuilder implements Builder<CSClient>
	{
		private int clientPoolSize;
		private long clientIdleCheckDelay;
		private long clientIdleCheckPeriod;
		private long clientMaxIdleTime;
		
		private int asyncEventQueueSize;
		private int asyncEventerSize;
		private long asyncEventingWaitTime;
		private long asyncEventerWaitTime;
		private int asyncEventerWaitRound;
		private long asyncEventIdleCheckDelay;
		private long asyncEventIdleCheckPeriod;
		private long asyncSchedulerShutdownTimeout;

//		private ScheduledThreadPoolExecutor scheduler;
		private int schedulerPoolSize;
		private long schedulerKeepAliveTime;

		private int readerClientSize;

//		private ThreadPool pool;
		
		public CSClientBuilder()
		{
		}

		/*
		public CSClientBuilder scheduler(ScheduledThreadPoolExecutor scheduler)
		{
			this.scheduler = scheduler;
			return this;
		}
		*/
		
		public CSClientBuilder freeClientPoolSize(int clientPoolSize)
		{
			this.clientPoolSize = clientPoolSize;
			return this;
		}
		
		public CSClientBuilder clientIdleCheckDelay(long clientIdleCheckDelay)
		{
			this.clientIdleCheckDelay = clientIdleCheckDelay;
			return this;
		}
		
		public CSClientBuilder clientIdleCheckPeriod(long clientIdleCheckPeriod)
		{
			this.clientIdleCheckPeriod = clientIdleCheckPeriod;
			return this;
		}
		
		public CSClientBuilder clientMaxIdleTime(long clientMaxIdleTime)
		{
			this.clientMaxIdleTime = clientMaxIdleTime;
			return this;
		}
		
		public CSClientBuilder asyncEventQueueSize(int asyncEventQueueSize)
		{
			this.asyncEventQueueSize = asyncEventQueueSize;
			return this;
		}
		
		public CSClientBuilder asyncEventerSize(int asyncEventerSize)
		{
			this.asyncEventerSize = asyncEventerSize;
			return this;
		}
		
		public CSClientBuilder asyncEventingWaitTime(long asyncEventingWaitTime)
		{
			this.asyncEventingWaitTime = asyncEventingWaitTime;
			return this;
		}
		
		public CSClientBuilder asyncEventerWaitTime(long asyncEventerWaitTime)
		{
			this.asyncEventerWaitTime = asyncEventerWaitTime;
			return this;
		}
		
		public CSClientBuilder asyncEventerWaitRound(int asyncEventerWaitRound)
		{
			this.asyncEventerWaitRound = asyncEventerWaitRound;
			return this;
		}
		
		public CSClientBuilder asyncEventIdleCheckDelay(long asyncEventIdleCheckDelay)
		{
			this.asyncEventIdleCheckDelay = asyncEventIdleCheckDelay;
			return this;
		}
		
		public CSClientBuilder asyncEventIdleCheckPeriod(long asyncEventIdleCheckPeriod)
		{
			this.asyncEventIdleCheckPeriod = asyncEventIdleCheckPeriod;
			return this;
		}
		
		public CSClientBuilder schedulerPoolSize(int schedulerPoolSize)
		{
			this.schedulerPoolSize = schedulerPoolSize;
			return this;
		}
		
		public CSClientBuilder schedulerKeepAliveTime(long schedulerKeepAliveTime)
		{
			this.schedulerKeepAliveTime = schedulerKeepAliveTime;
			return this;
		}
		
		public CSClientBuilder asyncSchedulerShutdownTimeout(long asyncSchedulerShutdownTimeout)
		{
			this.asyncSchedulerShutdownTimeout = asyncSchedulerShutdownTimeout;
			return this;
		}
		
		public CSClientBuilder readerClientSize(int readerClientSize)
		{
			this.readerClientSize = readerClientSize;
			return this;
		}

		/*
		public CSClientBuilder pool(ThreadPool pool)
		{
			this.pool = pool;
			return this;
		}
		*/

		@Override
		public CSClient build()
		{
			return new CSClient(this);
		}
		
		public int getClientPoolSize()
		{
			return this.clientPoolSize;
		}
		
		public long getClientIdleCheckDelay()
		{
			return this.clientIdleCheckDelay;
		}
		
		public long getClientIdleCheckPeriod()
		{
			return this.clientIdleCheckPeriod;
		}
		
		public long getClientMaxIdleTime()
		{
			return this.clientMaxIdleTime;
		}
		
		public int getAsyncEventQueueSize()
		{
			return this.asyncEventQueueSize;
		}
		
		public int getAsyncEventerSize()
		{
			return this.asyncEventerSize;
		}
		
		public long getAsyncEventingWaitTime()
		{
			return this.asyncEventingWaitTime;
		}
		
		public long getAsyncEventerWaitTime()
		{
			return this.asyncEventerWaitTime;
		}
		
		public int getAsyncEventerWaitRound()
		{
			return this.asyncEventerWaitRound;
		}
		
		public long getAsyncEventIdleCheckDelay()
		{
			return this.asyncEventIdleCheckDelay;
		}
		
		public long getAsyncEventIdleCheckPeriod()
		{
			return this.asyncEventIdleCheckPeriod;
		}

		public int getSchedulerPoolSize()
		{
			return this.schedulerPoolSize;
		}
		
		public long getSchedulerKeepAliveTime()
		{
			return this.schedulerKeepAliveTime;
		}

		/*
		public ScheduledThreadPoolExecutor getScheduler()
		{
			return this.scheduler;
		}
		*/
		
		public long getAsyncSchedulerShutdownTimeout()
		{
			return this.asyncSchedulerShutdownTimeout;
		}
		
		public int getReaderClientSize()
		{
			return this.readerClientSize;
		}

		/*
		public ThreadPool getPool()
		{
			return this.pool;
		}
		*/
	}

	public void dispose() throws IOException, InterruptedException
	{
		this.clientPool.dispose();
		this.syncEventer.dispose();
		this.asyncEventer.dispose();
		RemoteReader.REMOTE().shutdown();
	}
	
	public void init(ThreadPool pool)
	{
		this.pool = pool;
	}

	public void syncNotify(String ip, int port, ServerMessage notification) throws IOException, InterruptedException
	{
		this.syncEventer.notify(ip, port, notification);
	}

	/*
	 * Send notifications asynchronously. 05/01/2017, Bing Li
	 */
	public void asyncNotify(String ip, int port, ServerMessage notification) throws RejectedExecutionException
	{
		if (!this.asyncEventer.isReady())
		{
			this.pool.execute(this.asyncEventer);
		}
		this.asyncEventer.notify(ip, port, notification);
	}

	/*
	 * Read remotely, i.e., send a request and wait until a response is received. 05/01/2017, Bing Li
	 */
	public ServerMessage read(String ip, int port, ServerMessage request) throws ClassNotFoundException, RemoteReadException, IOException
	{
		return RemoteReader.REMOTE().read(NodeID.DISTRIBUTED().getKey(), ip, port, request);
	}
	
	public Future<ServerMessage> futureRead(String ip, int port, ServerMessage request)
	{
		return this.pool.getPool().submit(() ->
		{
			return this.read(ip, port, request);
		});
	}

	public ServerMessage read(String ip, int port, ServerMessage request, int timeout) throws ClassNotFoundException, RemoteReadException, IOException
	{
		return RemoteReader.REMOTE().read(NodeID.DISTRIBUTED().getKey(), ip, port, request, timeout);
	}

	public Future<ServerMessage> futureRead(String ip, int port, ServerMessage request, int timeout)
	{
		return this.pool.getPool().submit(() ->
		{
			return this.read(ip, port, request, timeout);
		});
	}

	/*
	 * Expose the TCP client pool. 05/08/2017, Bing Li
	 */
	public FreeClientPool getClientPool()
	{
		return this.clientPool;
	}

	/*
	 * The AsyncRemoteEventer needs a scheduler. Sometimes, when the client needs to do something periodically, the scheduler of AsyncRemoteEventer can be shared. 02/16/2019, Bing Li
	 */
	/*
	public ScheduledThreadPoolExecutor getScheduler()
	{
		return this.scheduler;
	}
	*/
}
