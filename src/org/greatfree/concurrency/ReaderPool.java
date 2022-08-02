package org.greatfree.concurrency;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import org.greatfree.client.IdleChecker;
import org.greatfree.util.Builder;
import org.greatfree.util.CollectionSorter;
import org.greatfree.util.UtilConfig;

/**
 * 
 * @author libing
 * 
 * 08/01/2022
 *
 */
public class ReaderPool<Query extends Request, Answer extends Response> extends Thread implements  IdleCheckable
{
	private Map<String, Runner<RequestThread<Query, Answer>>> threads;
	private Queue<Query> requestQueue;
	private final int queueSize;
	private final int readerSize;
	private ScheduledFuture<?> idleCheckingTask;
	private IdleChecker<ReaderPool<Query, Answer>> idleChecker;
	private Sync collaborator;
	private final long poolingWaitTime;
	private final long readerWaitTime;
	private final long idleCheckDelay;
	private final long idleCheckPeriod;
	private ReentrantLock monitorLock;
	private Reader<Query, Answer> reader;
	
	public ReaderPool(ReaderPoolBuilder<Query, Answer> builder)
	{
		this.threads = new ConcurrentHashMap<String, Runner<RequestThread<Query, Answer>>>();
		this.requestQueue = new LinkedBlockingQueue<Query>();
		this.queueSize = builder.getQueueSize();
		this.readerSize = builder.getReaderSize();
		Scheduler.PERIOD().init(builder.getSchedulerPoolSize(), builder.getSchedulerKeepAliveTime());
		this.idleChecker = new IdleChecker<ReaderPool<Query, Answer>>(this);
		this.collaborator = new Sync(true);
		this.poolingWaitTime = builder.getPoolingWaitTime();
		this.readerWaitTime = builder.getReaderWaitTime();
		this.idleCheckDelay = builder.getIdleCheckDelay();
		this.idleCheckPeriod = builder.getIdleCheckPeriod();
		this.monitorLock = new ReentrantLock();
		this.reader = builder.getReader();
	}

	public static class ReaderPoolBuilder<Query extends Request, Answer extends Response> implements Builder<ReaderPool<Query, Answer>>
	{
		private int queueSize;
		private int readerSize;
		private long poolingWaitTime;
		private long readerWaitTime;
		private long idleCheckDelay;
		private long idleCheckPeriod;
		private int schedulerPoolSize;
		private long schedulerKeepAliveTime;
		private Reader<Query, Answer> reader;
		
		public ReaderPoolBuilder()
		{
		}
		
		public ReaderPoolBuilder<Query, Answer> queueSize(int queueSize)
		{
			this.queueSize = queueSize;
			return this;
		}
		
		public ReaderPoolBuilder<Query, Answer> readerSize(int readerSize)
		{
			this.readerSize = readerSize;
			return this;
		}

		public ReaderPoolBuilder<Query, Answer> poolingWaitTime(long poolingWaitTime)
		{
			this.poolingWaitTime = poolingWaitTime;
			return this;
		}

		public ReaderPoolBuilder<Query, Answer> notifierWaitTime(long notifierWaitTime)
		{
			this.readerWaitTime = notifierWaitTime;
			return this;
		}

		public ReaderPoolBuilder<Query, Answer> idleCheckDelay(long idleCheckDelay)
		{
			this.idleCheckDelay = idleCheckDelay;
			return this;
		}

		public ReaderPoolBuilder<Query, Answer> idleCheckPeriod(long idleCheckPeriod)
		{
			this.idleCheckPeriod = idleCheckPeriod;
			return this;
		}

		public ReaderPoolBuilder<Query, Answer> schedulerPoolSize(int schedulerPoolSize)
		{
			this.schedulerPoolSize = schedulerPoolSize;
			return this;
		}

		public ReaderPoolBuilder<Query, Answer> schedulerKeepAliveTime(long schedulerKeepAliveTime)
		{
			this.schedulerKeepAliveTime = schedulerKeepAliveTime;
			return this;
		}
		
		public ReaderPoolBuilder<Query, Answer> reader(Reader<Query, Answer> reader)
		{
			this.reader = reader;
			return this;
		}

		@Override
		public ReaderPool<Query, Answer> build()
		{
			return new ReaderPool<Query, Answer>(this);
		}
		
		public Reader<Query, Answer> getReader()
		{
			return this.reader;
		}
		
		public int getQueueSize()
		{
			return this.queueSize;
		}
		
		public int getReaderSize()
		{
			return this.readerSize;
		}
		
		public long getPoolingWaitTime()
		{
			return this.poolingWaitTime;
		}
		
		public long getReaderWaitTime()
		{
			return this.readerWaitTime;
		}
		
		public long getIdleCheckDelay()
		{
			return this.idleCheckDelay;
		}
		
		public long getIdleCheckPeriod()
		{
			return this.idleCheckPeriod;
		}

		public int getSchedulerPoolSize()
		{
			return this.schedulerPoolSize;
		}
		
		public long getSchedulerKeepAliveTime()
		{
			return this.schedulerKeepAliveTime;
		}

	}

	public synchronized void restart()
	{
		this.collaborator.reset();
		if (this.threads == null)
		{
			this.threads = new ConcurrentHashMap<String, Runner<RequestThread<Query, Answer>>>();
		}
		if (this.requestQueue == null)
		{
			this.requestQueue = new LinkedBlockingQueue<Query>();
		}
		this.setIdleChecker(this.idleCheckDelay, this.idleCheckPeriod);
	}

	public synchronized boolean isReady()
	{
		if (!this.collaborator.isShutdown())
		{
			return true;
		}
		else
		{
			this.restart();
			return false;
		}
	}

	public synchronized void dispose() throws InterruptedException
	{
		this.collaborator.shutdown();
		if (this.requestQueue != null)
		{
			this.requestQueue.clear();
		}
		if (this.idleCheckingTask != null)
		{
			this.idleCheckingTask.cancel(true);
		}
		for (Runner<RequestThread<Query, Answer>> entry : this.threads.values())
		{
			entry.stop();
		}
		this.threads.clear();
	}

	@Override
	public void checkIdle() throws InterruptedException
	{
		for (Runner<RequestThread<Query, Answer>> entry : this.threads.values())
		{
			if (entry.getFunction().isEmpty() && entry.getFunction().isIdle())
			{
				this.threads.remove(entry.getFunction().getKey());
				entry.stop();
				entry = null;
			}
		}
	}

	@Override
	public void setIdleChecker(long idleCheckDelay, long idleCheckPeriod)
	{
		this.idleCheckingTask = Scheduler.PERIOD().getScheduler().scheduleAtFixedRate(this.idleChecker, idleCheckDelay, idleCheckPeriod, TimeUnit.MILLISECONDS);
	}

	public synchronized void read(Query query)
	{
		this.monitorLock.lock();
		this.requestQueue.add(query);
		this.monitorLock.unlock();
		this.collaborator.signal();
	}

	public void run()
	{
		String selectedThreadKey = UtilConfig.NO_KEY;
		Runner<RequestThread<Query, Answer>> runner;
		while (!this.collaborator.isShutdown())
		{
			try
			{
				while (!this.requestQueue.isEmpty())
				{
					while (this.threads.size() > 0)
					{
						selectedThreadKey = CollectionSorter.minValueKey(this.threads);
						if (selectedThreadKey != null)
						{
							if (this.threads.containsKey(selectedThreadKey))
							{
								try
								{
									if (this.threads.get(selectedThreadKey).getFunction().isFull())
									{
										if (this.threads.size() < this.readerSize)
										{
											RequestThread<Query, Answer> thread = new RequestThread<Query, Answer>(this.queueSize, this.readerWaitTime, this.reader);
											runner = new Runner<RequestThread<Query, Answer>>(thread);
											runner.start();
											this.threads.put(thread.getKey(), runner);
											this.threads.get(thread.getKey()).getFunction().enqueue(this.requestQueue.poll());
										}
										else
										{
											this.threads.get(selectedThreadKey).getFunction().enqueue(this.requestQueue.poll());
										}
									}
									else
									{
										this.threads.get(selectedThreadKey).getFunction().enqueue(this.requestQueue.poll());
									}
									break;
								}
								catch (NullPointerException e)
								{
									continue;
								}
							}
						}
					}
					if (this.threads.size() <= 0)
					{
						RequestThread<Query, Answer> thread = new RequestThread<Query, Answer>(this.queueSize, this.readerWaitTime, this.reader);
						runner = new Runner<RequestThread<Query, Answer>>(thread);
						runner.start();
						this.threads.put(thread.getKey(), runner);
						this.threads.get(thread.getKey()).getFunction().enqueue(this.requestQueue.poll());
					}
					if (this.collaborator.isShutdown())
					{
						break;
					}
				}

				if (!this.collaborator.isShutdown())
				{
					this.collaborator.holdOn(this.poolingWaitTime);
				}
			}
			catch (NullPointerException e)
			{
				e.printStackTrace();
			}
		}
	}
}
