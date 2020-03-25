package org.greatfree.cache.distributed;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import org.greatfree.cache.CacheConfig;
import org.greatfree.cache.db.StringKeyDBPool;
import org.greatfree.cache.local.CacheMapFactorable;
import org.greatfree.concurrency.Sync;
import org.greatfree.concurrency.ThreadPool;
import org.greatfree.concurrency.reactive.NotificationObjectDispatcher;
import org.greatfree.concurrency.reactive.NotificationObjectQueue;
import org.greatfree.concurrency.reactive.NotificationObjectThreadCreatable;
import org.greatfree.exceptions.DistributedListFetchException;
import org.greatfree.util.Builder;
import org.greatfree.util.UniqueKey;

// Created: 02/21/2019, Bing Li
public class DistributedList<Value extends UniqueKey, Factory extends CacheMapFactorable<String, Value>, ReplicateThread extends NotificationObjectQueue<ListReplicateNotification<Value>>, ReplicateThreadCreator extends NotificationObjectThreadCreatable<ListReplicateNotification<Value>, ReplicateThread>, FetchNotification extends ListFetchNotification, PrefetchThread extends NotificationObjectQueue<FetchNotification>, PrefetchThreadCreator extends NotificationObjectThreadCreatable<FetchNotification, PrefetchThread>, PostfetchThread extends NotificationObjectQueue<FetchNotification>, PostfetchThreadCreator extends NotificationObjectThreadCreatable<FetchNotification, PostfetchThread>, EvictThread extends NotificationObjectQueue<EvictedNotification<Value>>, EvictThreadCreator extends NotificationObjectThreadCreatable<EvictedNotification<Value>, EvictThread>> extends CacheList<Value, Factory>
{
	private NotificationObjectDispatcher<ListReplicateNotification<Value>, ReplicateThread, ReplicateThreadCreator> replicateDispatcher;
	
	private NotificationObjectDispatcher<FetchNotification, PrefetchThread, PrefetchThreadCreator> prefetchDispatcher;
	private final int prefetchThresholdSize;
	private final int prefetchCount;
	
	private NotificationObjectDispatcher<FetchNotification, PostfetchThread, PostfetchThreadCreator> postfetchDispatcher;
	private final long postfetchTimeout;

	private NotificationObjectDispatcher<EvictedNotification<Value>, EvictThread, EvictThreadCreator> evictedDispatcher;
	
	// When postfetched data is large, some of them might be evicted. So it is necessary to put them into the map temporarily. 07/18/2018, Bing Li
	private Map<String, List<Value>> postfetchedData;
	private Map<String, Value> postfetchedSingleData;

	private ThreadPool threadPool;
	
	private Map<String, Sync> syncs;

	public DistributedList(DistributedListBuilder<Value, Factory, ReplicateThread, ReplicateThreadCreator, FetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> builder)
	{
		super(builder.getFactory(), builder.getRootPath(), builder.getCacheKey(), builder.getCacheSize(), builder.getOffheapSizeInMB(), builder.getDiskSizeInMB(), StringKeyDBPool.SHARED().getDB(CacheConfig.getMapCacheKeyDBPath(builder.getRootPath(), builder.getCacheKey())), true);
		
		this.replicateDispatcher = new NotificationObjectDispatcher.NotificationObjectDispatcherBuilder<ListReplicateNotification<Value>, ReplicateThread, ReplicateThreadCreator>()
				.poolSize(builder.getPoolSize())
				.threadCreator(builder.getReplicateThreadCreator())
				.notificationQueueSize(builder.getNotificationQueueSize())
				.dispatcherWaitTime(builder.getDispatcherWaitTime())
				.waitRound(builder.getWaitRound())
				.idleCheckDelay(builder.getIdleCheckDelay())
				.idleCheckPeriod(builder.getIdleCheckPeriod())
				.scheduler(builder.getScheduler())
				.build();

		this.prefetchDispatcher = new NotificationObjectDispatcher.NotificationObjectDispatcherBuilder<FetchNotification, PrefetchThread, PrefetchThreadCreator>()
				.poolSize(builder.getPoolSize())
				.threadCreator(builder.getPrefetchThreadCreator())
				.notificationQueueSize(builder.getNotificationQueueSize())
				.dispatcherWaitTime(builder.getDispatcherWaitTime())
				.waitRound(builder.getWaitRound())
				.idleCheckDelay(builder.getIdleCheckDelay())
				.idleCheckPeriod(builder.getIdleCheckPeriod())
				.scheduler(builder.getScheduler())
				.build();

		this.postfetchDispatcher = new NotificationObjectDispatcher.NotificationObjectDispatcherBuilder<FetchNotification, PostfetchThread, PostfetchThreadCreator>()
				.poolSize(builder.getPoolSize())
				.threadCreator(builder.getPostfetchThreadCreator())
				.notificationQueueSize(builder.getNotificationQueueSize())
				.dispatcherWaitTime(builder.getDispatcherWaitTime())
				.waitRound(builder.getWaitRound())
				.idleCheckDelay(builder.getIdleCheckDelay())
				.idleCheckPeriod(builder.getIdleCheckPeriod())
				.scheduler(builder.getScheduler())
				.build();

		this.evictedDispatcher = new NotificationObjectDispatcher.NotificationObjectDispatcherBuilder<EvictedNotification<Value>, EvictThread, EvictThreadCreator>()
				.poolSize(builder.getPoolSize())
				.threadCreator(builder.getEvictThreadCreator())
				.notificationQueueSize(builder.getNotificationQueueSize())
				.dispatcherWaitTime(builder.getDispatcherWaitTime())
				.waitRound(builder.getWaitRound())
				.idleCheckDelay(builder.getIdleCheckDelay())
				.idleCheckPeriod(builder.getIdleCheckPeriod())
				.scheduler(builder.getScheduler())
				.build();

		this.prefetchThresholdSize = builder.getPrefetchThresholdSize();
		
		this.postfetchedData = new ConcurrentHashMap<String, List<Value>>();
		this.postfetchedSingleData = new ConcurrentHashMap<String, Value>();

		this.threadPool = builder.getThreadPool();
		this.prefetchCount = builder.getPrefetchCount();
		this.postfetchTimeout = builder.getPostfetchTimeout();
		
		this.syncs = new ConcurrentHashMap<String, Sync>();
	}
	
	public static class DistributedListBuilder<Value extends UniqueKey, Factory extends CacheMapFactorable<String, Value>, ReplicateThread extends NotificationObjectQueue<ListReplicateNotification<Value>>, ReplicateThreadCreator extends NotificationObjectThreadCreatable<ListReplicateNotification<Value>, ReplicateThread>, FetchNotification extends ListFetchNotification, PrefetchThread extends NotificationObjectQueue<FetchNotification>, PrefetchThreadCreator extends NotificationObjectThreadCreatable<FetchNotification, PrefetchThread>, PostfetchThread extends NotificationObjectQueue<FetchNotification>, PostfetchThreadCreator extends NotificationObjectThreadCreatable<FetchNotification, PostfetchThread>, EvictThread extends NotificationObjectQueue<EvictedNotification<Value>>, EvictThreadCreator extends NotificationObjectThreadCreatable<EvictedNotification<Value>, EvictThread>> implements Builder<DistributedList<Value, Factory, ReplicateThread, ReplicateThreadCreator, FetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator>>
	{
		private String cacheKey;
		private Factory factory;
		private String rootPath;
//		private int cacheSize;
		private long cacheSize;
		private int offheapSizeInMB;
		private int diskSizeInMB;

		private int poolSize;
		private ReplicateThreadCreator replicateCreator;
		private PrefetchThreadCreator prefetchCreator;
		private PostfetchThreadCreator postfetchCreator;
		private EvictThreadCreator evictCreator;
		private int notificationQueueSize;
		private long dispatcherWaitTime;
		private int waitRound;
		private long idleCheckDelay;
		private long idleCheckPeriod;
		private ScheduledThreadPoolExecutor scheduler;
		private int prefetchThresholdSize;
		private ThreadPool threadPool;
		private int prefetchCount;
		private long postfetchTimeout;

		public DistributedListBuilder()
		{
		}
		
		public DistributedListBuilder<Value, Factory, ReplicateThread, ReplicateThreadCreator, FetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> cacheKey(String cacheKey)
		{
			this.cacheKey = cacheKey;
			return this;
		}
		
		public DistributedListBuilder<Value, Factory, ReplicateThread, ReplicateThreadCreator, FetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> factory(Factory factory)
		{
			this.factory = factory;
			return this;
		}
		
		public DistributedListBuilder<Value, Factory, ReplicateThread, ReplicateThreadCreator, FetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> rootPath(String rootPath)
		{
			this.rootPath = rootPath;
			return this;
		}
		
		public DistributedListBuilder<Value, Factory, ReplicateThread, ReplicateThreadCreator, FetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> cacheSize(long cacheSize)
		{
			this.cacheSize = cacheSize;
			return this;
		}
		
		public DistributedListBuilder<Value, Factory, ReplicateThread, ReplicateThreadCreator, FetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> offheapSizeInMB(int offheapSizeInMB)
		{
			this.offheapSizeInMB = offheapSizeInMB;
			return this;
		}
		
		public DistributedListBuilder<Value, Factory, ReplicateThread, ReplicateThreadCreator, FetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> diskSizeInMB(int diskSizeInMB)
		{
			this.diskSizeInMB = diskSizeInMB;
			return this;
		}

		public DistributedListBuilder<Value, Factory, ReplicateThread, ReplicateThreadCreator, FetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> replicateCreator(ReplicateThreadCreator replicateCreator)
		{
			this.replicateCreator = replicateCreator;
			return this;
		}
		
		public DistributedListBuilder<Value, Factory, ReplicateThread, ReplicateThreadCreator, FetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> poolSize(int poolSize)
		{
			this.poolSize = poolSize;
			return this;
		}
		
		public DistributedListBuilder<Value, Factory, ReplicateThread, ReplicateThreadCreator, FetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> prefetchCreator(PrefetchThreadCreator prefetchCreator)
		{
			this.prefetchCreator = prefetchCreator;
			return this;
		}
		
		public DistributedListBuilder<Value, Factory, ReplicateThread, ReplicateThreadCreator, FetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> postfetchCreator(PostfetchThreadCreator postfetchCreator)
		{
			this.postfetchCreator = postfetchCreator;
			return this;
		}
		
		public DistributedListBuilder<Value, Factory, ReplicateThread, ReplicateThreadCreator, FetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> evictCreator(EvictThreadCreator evictCreator)
		{
			this.evictCreator = evictCreator;
			return this;
		}
		
		public DistributedListBuilder<Value, Factory, ReplicateThread, ReplicateThreadCreator, FetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> notificationQueueSize(int nqSize)
		{
			this.notificationQueueSize = nqSize;
			return this;
		}
		
		public DistributedListBuilder<Value, Factory, ReplicateThread, ReplicateThreadCreator, FetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> dispatcherWaitTime(long dispatcherWaitTime)
		{
			this.dispatcherWaitTime = dispatcherWaitTime;
			return this;
		}
		
		public DistributedListBuilder<Value, Factory, ReplicateThread, ReplicateThreadCreator, FetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> waitRound(int waitRound)
		{
			this.waitRound = waitRound;
			return this;
		}
		
		public DistributedListBuilder<Value, Factory, ReplicateThread, ReplicateThreadCreator, FetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> idleCheckDelay(long idleCheckDelay)
		{
			this.idleCheckDelay = idleCheckDelay;
			return this;
		}
		
		public DistributedListBuilder<Value, Factory, ReplicateThread, ReplicateThreadCreator, FetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> idleCheckPeriod(long idleCheckPeriod)
		{
			this.idleCheckPeriod = idleCheckPeriod;
			return this;
		}
		
		public DistributedListBuilder<Value, Factory, ReplicateThread, ReplicateThreadCreator, FetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> scheduler(ScheduledThreadPoolExecutor scheduler)
		{
			this.scheduler = scheduler;
			return this;
		}
		
		public DistributedListBuilder<Value, Factory, ReplicateThread, ReplicateThreadCreator, FetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> prefetchThresholdSize(int prefetchThresholdSize)
		{
			this.prefetchThresholdSize = prefetchThresholdSize;
			return this;
		}
		
		public DistributedListBuilder<Value, Factory, ReplicateThread, ReplicateThreadCreator, FetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> threadPool(ThreadPool threadPool)
		{
			this.threadPool = threadPool;
			return this;
		}
		
		public DistributedListBuilder<Value, Factory, ReplicateThread, ReplicateThreadCreator, FetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> prefetchCount(int prefetchCount)
		{
			this.prefetchCount = prefetchCount;
			return this;
		}

		public DistributedListBuilder<Value, Factory, ReplicateThread, ReplicateThreadCreator, FetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> postfetchTimeout(long postfetchTimeout)
		{
			this.postfetchTimeout = postfetchTimeout;
			return this;
		}
		
		@Override
		public DistributedList<Value, Factory, ReplicateThread, ReplicateThreadCreator, FetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> build()
		{
			return new DistributedList<Value, Factory, ReplicateThread, ReplicateThreadCreator, FetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator>(this);
		}
		
		public String getCacheKey()
		{
			return this.cacheKey;
		}
		
		public Factory getFactory()
		{
			return this.factory;
		}
		
		public String getRootPath()
		{
			return this.rootPath;
		}
		
		public long getCacheSize()
		{
			return this.cacheSize;
		}
		
		public int getOffheapSizeInMB()
		{
			return this.offheapSizeInMB;
		}
		
		public int getDiskSizeInMB()
		{
			return this.diskSizeInMB;
		}
		
		public int getPoolSize()
		{
			return this.poolSize;
		}
		
		public ReplicateThreadCreator getReplicateThreadCreator()
		{
			return this.replicateCreator;
		}
		
		public PrefetchThreadCreator getPrefetchThreadCreator()
		{
			return this.prefetchCreator;
		}
	
		public PostfetchThreadCreator getPostfetchThreadCreator()
		{
			return this.postfetchCreator;
		}
		
		public EvictThreadCreator getEvictThreadCreator()
		{
			return this.evictCreator;
		}

		public int getNotificationQueueSize()
		{
			return this.notificationQueueSize;
		}
		
		public long getDispatcherWaitTime()
		{
			return this.dispatcherWaitTime;
		}
		
		public int getWaitRound()
		{
			return this.waitRound;
		}
		
		public long getIdleCheckDelay()
		{
			return this.idleCheckDelay;
		}
		
		public long getIdleCheckPeriod()
		{
			return this.idleCheckPeriod;
		}
		
		public ScheduledThreadPoolExecutor getScheduler()
		{
			return this.scheduler;
		}
		
		public int getPrefetchThresholdSize()
		{
			return this.prefetchThresholdSize;
		}

		public int getPrefetchCount()
		{
			return this.prefetchCount;
		}

		public long getPostfetchTimeout()
		{
			return this.postfetchTimeout;
		}

		public ThreadPool getThreadPool()
		{
			return this.threadPool;
		}
	}
	
	public void shutdown() throws InterruptedException, IOException
	{
		super.shutdown();
		this.postfetchedData.clear();
		this.postfetchedData = null;
		this.postfetchedSingleData.clear();
		this.postfetchedSingleData = null;
		this.replicateDispatcher.dispose();
		this.prefetchDispatcher.dispose();
		this.postfetchDispatcher.dispose();
		this.evictedDispatcher.dispose();
		for (Sync entry : this.syncs.values())
		{
			entry.signal();
		}
		this.syncs.clear();
		this.syncs = null;
	}
	
	public String getCacheKey()
	{
		return super.getCacheKeyAtBase();
	}
	
	public long getMemCacheSize()
	{
		return super.getMemCacheSizeAtBase();
	}
	
	private void signal(String key)
	{
		this.syncs.get(key).signal();
		this.syncs.remove(key);
	}

	public void savePostfetchedData(String key, List<Value> values, boolean isBlocking)
	{
		if (isBlocking)
		{
			if (values != null)
			{
				// The notify/wait mechanism results in the concurrently accessing on the values. It might lead to ConcurrentModificationException. So it is necessary to send out the values first and then save them into the cache. Or make a copy. 08/04/2018, Bing Li
				if (this.postfetchedData.containsKey(key))
				{
					this.postfetchedData.remove(key);
				}
				this.postfetchedData.put(key, new ArrayList<Value>(values));
			}
			this.signal(key);
		}
		if (values != null)
		{
			super.addAllAt2ndBase(values);
		}
	}
	
	public void savePostfetchedData(String key, Value v, boolean isBlocking)
	{
		if (v != null)
		{
			// The notify/wait mechanism results in the concurrently accessing on the value. It might lead to ConcurrentModificationException. Since only one piece of data is posfetched, it can be saved into the cache first. 08/04/2018, Bing Li
			super.addAt2ndBase(v);
			if (isBlocking)
			{
				this.postfetchedSingleData.put(key, v);
			}
		}
		if (isBlocking)
		{
			this.signal(key);
		}
	}

	public int getPrefetchCount()
	{
		return this.prefetchCount;
	}
	
	public Value get(FetchNotification notification)
	{
		if (super.getLeftSizeAtBase(notification.getResourceIndex()) <= this.prefetchThresholdSize)
		{
			if (!this.prefetchDispatcher.isReady())
			{
				this.threadPool.execute(this.prefetchDispatcher);
			}
			this.prefetchDispatcher.enqueue(notification);
		}
//		Date startTime = Calendar.getInstance().getTime();
		Value v = super.getAt2ndBase(notification.getResourceIndex());
//		Date endTime = Calendar.getInstance().getTime();
//		System.out.println("DistributedList-get(): It takes " + Time.getTimespanInMilliSecond(endTime, startTime) + " ms to get the data at index, " + notification.getResourceIndex());
		if (v != null)
		{
			return v;
		}
		if (!this.postfetchDispatcher.isReady())
		{
			this.threadPool.execute(this.postfetchDispatcher);
		}
		this.postfetchDispatcher.enqueue(notification);
		if (notification.isBlocking())
		{
			this.syncs.put(notification.getKey(), new Sync(false));
			this.syncs.get(notification.getKey()).holdOn(this.postfetchTimeout);
			
			/*
			 * It needs to make the below update. The postfetched data might not be sorted at the index because evicting results in some keys are removed. 07/29/2018, Bing Li 
			 */
			v = this.postfetchedSingleData.get(notification.getKey());
			this.postfetchedSingleData.remove(notification.getKey());
		}
		return v;
	}
	
	public List<Value> getTop(FetchNotification notification) throws DistributedListFetchException
	{
		if (super.getLeftSizeAtBase(notification.getEndIndex()) <= this.prefetchThresholdSize)
		{
			if (!this.prefetchDispatcher.isReady())
			{
				this.threadPool.execute(this.prefetchDispatcher);
			}
			this.prefetchDispatcher.enqueue(notification);
		}
		if (notification.getEndIndex() < super.getCacheSizeAtBase())
		{
			List<Value> rscs = super.getTopAt2ndBase(notification.getEndIndex());
			if (rscs != null)
			{
				// Since the top index is required to be less than the cache size, the later condition is not necessary. 08/04/2018, Bing Li
				// When data is not evicted, the condition, rscs.size() < super.getCacheSize(), is fine. But if data has ever been evicted, the condition is not reasonable since the amount of loaded data must be less than the cache size and some required data is located at the terminal caches. 08/04/2018, Bing Li
//				if (rscs.size() >= notification.getEndIndex() || rscs.size() <= super.getCacheSize())
				if (rscs.size() > notification.getEndIndex())
				{
					return rscs;
				}
				else
				{
					if (notification.isBlocking())
					{
						/*
						 * In the previous solution, it assumes that the terminal cache has the data that covers its distributed ones. But in practice, the assumption might not be correct. So it is reasonable to put the loaded data into the postfetchedData. Otherwise, the data from the terminal might overwrite the loaded data. 04/30/2019, Bing Li
						 */
						this.postfetchedData.put(notification.getKey(), rscs);
					}
				}
			}
			if (!this.postfetchDispatcher.isReady())
			{
				this.threadPool.execute(this.postfetchDispatcher);
			}
			this.postfetchDispatcher.enqueue(notification);
			if (notification.isBlocking())
			{
				this.syncs.put(notification.getKey(), new Sync(false));
				this.syncs.get(notification.getKey()).holdOn(this.postfetchTimeout);
				List<Value> values = this.postfetchedData.get(notification.getKey());
				this.postfetchedData.remove(notification.getKey());
				return values;
			}
			return rscs;
		}
		else
		{
			throw new DistributedListFetchException(super.getCacheSizeAtBase(), notification.getEndIndex());
		}
	}

	/*
	 * I think the exception is required. It is not reasonable to load huge data from the cache. So the range should be located within a certain range. 08/02/2018, Bing Li
	 * It is not necessary to throw the exception. 08/02/2018, Bing Li
	 */
	public List<Value> getRange(FetchNotification notification) throws DistributedListFetchException
	{
		if (super.getLeftSizeAtBase(notification.getEndIndex()) <= this.prefetchThresholdSize)
		{
			if (!this.prefetchDispatcher.isReady())
			{
				this.threadPool.execute(this.prefetchDispatcher);
			}
			this.prefetchDispatcher.enqueue(notification);
		}
		if (notification.getEndIndex() - notification.getStartIndex() < super.getCacheSizeAtBase())
		{
			List<Value> rscs = super.getRangeAt2ndBase(notification.getStartIndex(), notification.getEndIndex());
			if (rscs != null)
			{
				// Since the top index is required to be less than the cache size, the later condition is not necessary. 08/04/2018, Bing Li
				// When data is not evicted, the condition, rscs.size() < super.getCacheSize(), is fine. But if data has ever been evicted, the condition is not reasonable since the amount of loaded data must be less than the cache size and some required data is located at the terminal caches. 08/04/2018, Bing Li
//				if (rscs.size() >= notification.getEndIndex() - notification.getStartIndex() || rscs.size() < super.getCacheSize())
				if (rscs.size() > notification.getEndIndex() - notification.getStartIndex())
				{
					return rscs;
				}
				else
				{
					if (notification.isBlocking())
					{
						/*
						 * In the previous solution, it assumes that the terminal cache has the data that covers its distributed ones. But in practice, the assumption might not be correct. So it is reasonable to put the loaded data into the postfetchedData. Otherwise, the data from the terminal might overwrite the loaded data. 04/30/2019, Bing Li
						 */
						this.postfetchedData.put(notification.getKey(), rscs);
					}
				}
			}
			if (!this.postfetchDispatcher.isReady())
			{
				this.threadPool.execute(this.postfetchDispatcher);
			}
			this.postfetchDispatcher.enqueue(notification);
			if (notification.isBlocking())
			{
				this.syncs.put(notification.getKey(), new Sync(false));
				this.syncs.get(notification.getKey()).holdOn(this.postfetchTimeout);
				List<Value> values = this.postfetchedData.get(notification.getKey());
				this.postfetchedData.remove(notification.getKey());
				return values;
			}
			return rscs;
		}
		else
		{
			throw new DistributedListFetchException(super.getCacheSizeAtBase(), notification.getEndIndex() - notification.getStartIndex() + 1);
		}
	}

	public void add(ListReplicateNotification<Value> notification)
	{
		if (!this.replicateDispatcher.isReady())
		{
			this.threadPool.execute(this.replicateDispatcher);
		}
		this.replicateDispatcher.enqueue(notification);
		super.addAt2ndBase(notification.getValue());
	}
	
	public void addAll(ListReplicateNotification<Value> notification)
	{
		if (!this.replicateDispatcher.isReady())
		{
			this.threadPool.execute(this.replicateDispatcher);
		}
		this.replicateDispatcher.enqueue(notification);
		super.addAllAt2ndBase(notification.getValues());
	}
	
	public void addAllLocally(List<Value> values)
	{
		super.addAllAt2ndBase(values);
	}

	@Override
	public void evict(String k, Value v)
	{
//		EvictedNotification<Value> notification = new EvictedNotification<Value>(k, v);
		if (!this.evictedDispatcher.isReady())
		{
			this.threadPool.execute(this.evictedDispatcher);
		}
		this.evictedDispatcher.enqueue(new EvictedNotification<Value>(k, v));
		super.removeKeyAtBase(k);
	}

	@Override
	public void forward(String k, Value v)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void remove(String k, Value v)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void expire(String k, Value v)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update(String k, Value v)
	{
		// TODO Auto-generated method stub
		
	}
}
