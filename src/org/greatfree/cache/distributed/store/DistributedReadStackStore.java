package org.greatfree.cache.distributed.store;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import org.greatfree.cache.CompoundKeyCreatable;
import org.greatfree.cache.StoreElement;
import org.greatfree.cache.distributed.EvictedNotification;
import org.greatfree.cache.distributed.KickOffNotification;
import org.greatfree.cache.local.CacheMapFactorable;
import org.greatfree.concurrency.Sync;
import org.greatfree.concurrency.ThreadPool;
import org.greatfree.concurrency.reactive.NotificationObjectDispatcher;
import org.greatfree.concurrency.reactive.NotificationObjectQueue;
import org.greatfree.concurrency.reactive.NotificationObjectThreadCreatable;
import org.greatfree.exceptions.DistributedListFetchException;
import org.greatfree.exceptions.IndexOutOfRangeException;
import org.greatfree.exceptions.TerminalServerOverflowedException;
import org.greatfree.util.Builder;
import org.greatfree.util.FileManager;

/*
 * The version is tested in the Clouds project. 08/22/2018, Bing Li
 */

/*
 * This cache is not a comprehensive one, which has the four features, replicating, prefetching, postfetching and evicting. Rather, it has no the feature of replicating. That indicates that no new data is created from the cache. All of the data it retains is replicated from others. 06/19/2018, Bing Li
 */

// Created: 06/19/2018, Bing Li
// public class SemiPointingDistributedStackStore<Value extends CacheElementing, Factory extends CacheMapFactorable<String, Value>, CompoundKeyCreator extends CompoundKeyCreatable<Integer>, Notification extends KickOffPrefetchNotification, PrefetchThread extends NotificationObjectQueue<Notification>, PrefetchThreadCreator extends NotificationObjectThreadCreatable<Notification, PrefetchThread>, PostfetchThread extends NotificationObjectQueue<Notification>, PostfetchThreadCreator extends NotificationObjectThreadCreatable<Notification, PostfetchThread>, EvictThread extends NotificationObjectQueue<EvictedNotification<Value>>, EvictThreadCreator extends NotificationObjectThreadCreatable<EvictedNotification<Value>, EvictThread>> extends StackStore<Value, Factory, CompoundKeyCreator>

// public class PointingDistributedReadStackStore<Value extends CachePointing, Factory extends CacheMapFactorable<String, Value>, CompoundKeyCreator extends CompoundKeyCreatable<Integer>, StackNotification extends KickOffNotification, PrefetchThread extends NotificationObjectQueue<StackNotification>, PrefetchThreadCreator extends NotificationObjectThreadCreatable<StackNotification, PrefetchThread>, PostfetchThread extends NotificationObjectQueue<StackNotification>, PostfetchThreadCreator extends NotificationObjectThreadCreatable<StackNotification, PostfetchThread>, EvictThread extends NotificationObjectQueue<EvictedNotification<Value>>, EvictThreadCreator extends NotificationObjectThreadCreatable<EvictedNotification<Value>, EvictThread>> extends StackStore<Value, Factory, CompoundKeyCreator>

//Since data is pushed on the top of a stack, it is not proper to push evicted data on the top. Because of the replicating, it is also unnecessary to evict data. 08/08/2018, Bing Li
//public class PointingDistributedReadStackStore<Value extends CachePointing, Factory extends CacheMapFactorable<String, Value>, CompoundKeyCreator extends CompoundKeyCreatable<Integer>, StackNotification extends KickOffNotification, PrefetchThread extends NotificationObjectQueue<StackNotification>, PrefetchThreadCreator extends NotificationObjectThreadCreatable<StackNotification, PrefetchThread>, PostfetchThread extends NotificationObjectQueue<StackNotification>, PostfetchThreadCreator extends NotificationObjectThreadCreatable<StackNotification, PostfetchThread>> extends StackStore<Value, Factory, CompoundKeyCreator>
public class DistributedReadStackStore<Value extends StoreElement, Factory extends CacheMapFactorable<String, Value>, CompoundKeyCreator extends CompoundKeyCreatable<Integer>, StackNotification extends KickOffNotification, PrefetchThread extends NotificationObjectQueue<StackNotification>, PrefetchThreadCreator extends NotificationObjectThreadCreatable<StackNotification, PrefetchThread>, PostfetchThread extends NotificationObjectQueue<StackNotification>, PostfetchThreadCreator extends NotificationObjectThreadCreatable<StackNotification, PostfetchThread>, EvictThread extends NotificationObjectQueue<EvictedNotification<Value>>, EvictThreadCreator extends NotificationObjectThreadCreatable<EvictedNotification<Value>, EvictThread>> extends StackStore<Value, Factory, CompoundKeyCreator>
{
	private NotificationObjectDispatcher<StackNotification, PrefetchThread, PrefetchThreadCreator> prefetchDispatcher;
	private final int prefetchThresholdSize;
	private final int prefetchCount;

	private NotificationObjectDispatcher<StackNotification, PostfetchThread, PostfetchThreadCreator> postfetchDispatcher;
	private final long postfetchTimeout;

	private NotificationObjectDispatcher<EvictedNotification<Value>, EvictThread, EvictThreadCreator> evictedDispatcher;

	private Map<String, List<Value>> postfetchedData;

	private ThreadPool threadPool;
	private Map<String, Sync> syncs;

//	public PointingDistributedReadStackStore(PointingDistributedReadStackStoreBuilder<Value, Factory, CompoundKeyCreator, StackNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> builder)
	public DistributedReadStackStore(DistributedReadStackStoreBuilder<Value, Factory, CompoundKeyCreator, StackNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> builder)
	{
		super(builder.getStoreKey(), builder.getFactory(), builder.getRootPath(), builder.getStoreSize(), builder.getCacheSize(), builder.getOffheapSizeInMB(), builder.getDiskSizeInMB(), builder.getKeyCreator());
		
		this.prefetchDispatcher = new NotificationObjectDispatcher.NotificationObjectDispatcherBuilder<StackNotification, PrefetchThread, PrefetchThreadCreator>()
//				.threadPool(builder.getThreadPool())
				.poolSize(builder.getPoolSize())
				.threadCreator(builder.getPrefetchThreadCreator())
				.notificationQueueSize(builder.getNotificationQueueSize())
				.dispatcherWaitTime(builder.getDispatcherWaitTime())
				.waitRound(builder.getWaitRound())
				.idleCheckDelay(builder.getIdleCheckDelay())
				.idleCheckPeriod(builder.getIdleCheckPeriod())
				.scheduler(builder.getScheduler())
				.build();

		this.postfetchDispatcher = new NotificationObjectDispatcher.NotificationObjectDispatcherBuilder<StackNotification, PostfetchThread, PostfetchThreadCreator>()
//				.threadPool(builder.getThreadPool())
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
//				.threadPool(builder.getThreadPool())
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

		this.threadPool = builder.getThreadPool();
		this.prefetchCount = builder.getPrefetchCount();
		this.postfetchTimeout = builder.getPostfetchTimeout();

		this.syncs = new ConcurrentHashMap<String, Sync>();
	}

//	public static class SemiDistributedStackStoreBuilder<Value extends CacheElementing, Factory extends CacheMapFactorable<String, Value>, CompoundKeyCreator extends CompoundKeyCreatable<Integer>, Notification extends KickOffPrefetchNotification, PrefetchThread extends NotificationObjectQueue<Notification>, PrefetchThreadCreator extends NotificationObjectThreadCreatable<Notification, PrefetchThread>, PostfetchThread extends NotificationObjectQueue<Notification>, PostfetchThreadCreator extends NotificationObjectThreadCreatable<Notification, PostfetchThread>, EvictThread extends NotificationObjectQueue<EvictedNotification<Value>>, EvictThreadCreator extends NotificationObjectThreadCreatable<EvictedNotification<Value>, EvictThread>> implements Builder<SemiPointingDistributedStackStore<Value, Factory, CompoundKeyCreator, Notification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator>>
//	public static class PointingDistributedReadStackStoreBuilder<Value extends CachePointing, Factory extends CacheMapFactorable<String, Value>, CompoundKeyCreator extends CompoundKeyCreatable<Integer>, Notification extends KickOffNotification, PrefetchThread extends NotificationObjectQueue<Notification>, PrefetchThreadCreator extends NotificationObjectThreadCreatable<Notification, PrefetchThread>, PostfetchThread extends NotificationObjectQueue<Notification>, PostfetchThreadCreator extends NotificationObjectThreadCreatable<Notification, PostfetchThread>, EvictThread extends NotificationObjectQueue<EvictedNotification<Value>>, EvictThreadCreator extends NotificationObjectThreadCreatable<EvictedNotification<Value>, EvictThread>> implements Builder<PointingDistributedReadStackStore<Value, Factory, CompoundKeyCreator, Notification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator>>
	public static class DistributedReadStackStoreBuilder<Value extends StoreElement, Factory extends CacheMapFactorable<String, Value>, CompoundKeyCreator extends CompoundKeyCreatable<Integer>, Notification extends KickOffNotification, PrefetchThread extends NotificationObjectQueue<Notification>, PrefetchThreadCreator extends NotificationObjectThreadCreatable<Notification, PrefetchThread>, PostfetchThread extends NotificationObjectQueue<Notification>, PostfetchThreadCreator extends NotificationObjectThreadCreatable<Notification, PostfetchThread>, EvictThread extends NotificationObjectQueue<EvictedNotification<Value>>, EvictThreadCreator extends NotificationObjectThreadCreatable<EvictedNotification<Value>, EvictThread>> implements Builder<DistributedReadStackStore<Value, Factory, CompoundKeyCreator, Notification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator>>
	{
		private String storeKey;
		private Factory factory;
		private String rootPath;
//		private int storeSize;
		private long storeSize;
//		private int cacheSize;
		private long cacheSize;
		private int offheapSizeInMB;
		private int diskSizeInMB;
		private CompoundKeyCreator keyCreator;

		private int poolSize;
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

		public DistributedReadStackStoreBuilder()
		{
		}

		public DistributedReadStackStoreBuilder<Value, Factory, CompoundKeyCreator, Notification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> storeKey(String storeKey)
		{
			this.storeKey = storeKey;
			return this;
		}

		public DistributedReadStackStoreBuilder<Value, Factory, CompoundKeyCreator, Notification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> factory(Factory factory)
		{
			this.factory = factory;
			return this;
		}

		public DistributedReadStackStoreBuilder<Value, Factory, CompoundKeyCreator, Notification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> keyCreator(CompoundKeyCreator keyCreator)
		{
			this.keyCreator = keyCreator;
			return this;
		}

		public DistributedReadStackStoreBuilder<Value, Factory, CompoundKeyCreator, Notification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> poolSize(int poolSize)
		{
			this.poolSize = poolSize;
			return this;
		}

		public DistributedReadStackStoreBuilder<Value, Factory, CompoundKeyCreator, Notification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> rootPath(String rootPath)
		{
			this.rootPath = FileManager.appendSlash(rootPath);
			return this;
		}

		public DistributedReadStackStoreBuilder<Value, Factory, CompoundKeyCreator, Notification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> storeSize(long storeSize)
		{
			this.storeSize = storeSize;
			return this;
		}

		public DistributedReadStackStoreBuilder<Value, Factory, CompoundKeyCreator, Notification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> cacheSize(long cacheSize)
		{
			this.cacheSize = cacheSize;
			return this;
		}

		public DistributedReadStackStoreBuilder<Value, Factory, CompoundKeyCreator, Notification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> offheapSizeInMB(int offheapSizeInMB)
		{
			this.offheapSizeInMB = offheapSizeInMB;
			return this;
		}

		public DistributedReadStackStoreBuilder<Value, Factory, CompoundKeyCreator, Notification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> diskSizeInMB(int diskSizeInMB)
		{
			this.diskSizeInMB = diskSizeInMB;
			return this;
		}

		public DistributedReadStackStoreBuilder<Value, Factory, CompoundKeyCreator, Notification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> prefetchThreadCreator(PrefetchThreadCreator prefetchCreator)
		{
			this.prefetchCreator = prefetchCreator;
			return this;
		}

		public DistributedReadStackStoreBuilder<Value, Factory, CompoundKeyCreator, Notification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> postfetchThreadCreator(PostfetchThreadCreator postfetchCreator)
		{
			this.postfetchCreator = postfetchCreator;
			return this;
		}

		public DistributedReadStackStoreBuilder<Value, Factory, CompoundKeyCreator, Notification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> evictCreator(EvictThreadCreator evictCreator)
		{
			this.evictCreator = evictCreator;
			return this;
		}

		public DistributedReadStackStoreBuilder<Value, Factory, CompoundKeyCreator, Notification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> notificationQueueSize(int notificationQueueSize)
		{
			this.notificationQueueSize = notificationQueueSize;
			return this;
		}

		public DistributedReadStackStoreBuilder<Value, Factory, CompoundKeyCreator, Notification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> dispatcherWaitTime(long dispatcherWaitTime)
		{
			this.dispatcherWaitTime = dispatcherWaitTime;
			return this;
		}

		public DistributedReadStackStoreBuilder<Value, Factory, CompoundKeyCreator, Notification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> waitRound(int waitRound)
		{
			this.waitRound = waitRound;
			return this;
		}

		public DistributedReadStackStoreBuilder<Value, Factory, CompoundKeyCreator, Notification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> idleCheckDelay(long idleCheckDelay)
		{
			this.idleCheckDelay = idleCheckDelay;
			return this;
		}

		public DistributedReadStackStoreBuilder<Value, Factory, CompoundKeyCreator, Notification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> idleCheckPeriod(long idleCheckPeriod)
		{
			this.idleCheckPeriod = idleCheckPeriod;
			return this;
		}

		public DistributedReadStackStoreBuilder<Value, Factory, CompoundKeyCreator, Notification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> scheduler(ScheduledThreadPoolExecutor scheduler)
		{
			this.scheduler = scheduler;
			return this;
		}

		public DistributedReadStackStoreBuilder<Value, Factory, CompoundKeyCreator, Notification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> prefetchThresholdSize(int prefetchThresholdSize)
		{
			this.prefetchThresholdSize = prefetchThresholdSize;
			return this;
		}

		public DistributedReadStackStoreBuilder<Value, Factory, CompoundKeyCreator, Notification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> threadPool(ThreadPool threadPool)
		{
			this.threadPool = threadPool;
			return this;
		}

		public DistributedReadStackStoreBuilder<Value, Factory, CompoundKeyCreator, Notification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> prefetchCount(int prefetchCount)
		{
			this.prefetchCount = prefetchCount;
			return this;
		}

		public DistributedReadStackStoreBuilder<Value, Factory, CompoundKeyCreator, Notification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> postfetchTimeout(long postfetchTimeout)
		{
			this.postfetchTimeout = postfetchTimeout;
			return this;
		}

		@Override
		public DistributedReadStackStore<Value, Factory, CompoundKeyCreator, Notification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> build()
		{
			return new DistributedReadStackStore<Value, Factory, CompoundKeyCreator, Notification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator>(this);
		}

		public String getStoreKey()
		{
			return this.storeKey;
		}

		public Factory getFactory()
		{
			return this.factory;
		}

		public CompoundKeyCreator getKeyCreator()
		{
			return this.keyCreator;
		}

		public String getRootPath()
		{
			return this.rootPath;
		}

		public long getStoreSize()
		{
			return this.storeSize;
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
	
	public void dispose() throws InterruptedException
	{
		super.closeAtBase();
		this.postfetchedData.clear();
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
	
	public boolean isDown()
	{
		return super.isDownAtBase();
	}
	
	public int getPrefetchCount()
	{
		return this.prefetchCount;
	}

	private void signal(String key)
	{
		this.syncs.get(key).signal();
		this.syncs.remove(key);
	}

	public void savePostfetchedData(String key, String cacheKey, int postfetchCount, List<Value> values, boolean isBlocking)
	{
		// I forget why the below line is required. 03/19/2019, Bing Li
		int poppingCount = postfetchCount - this.prefetchCount;
		
		if (isBlocking)
		{
			if (values != null)
			{
				// The notify/wait mechanism results in the concurrently accessing on the values. It might lead to ConcurrentModificationException. So it is necessary to send out the values first and then save them into the cache. Or make a copy. 08/04/2018, Bing Li
				
				if (this.postfetchedData.containsKey(key))
				{
					this.postfetchedData.remove(key);
				}
				
				if (values.size() <= poppingCount)
				{
					this.postfetchedData.put(key, new ArrayList<Value>(values));
				}
				else
				{
					if (poppingCount > 0)
					{
						this.postfetchedData.put(key, new ArrayList<Value>(values.subList(0, poppingCount)));
					}
				}
			}
			this.signal(key);
		}
		if (values != null)
		{
			if (values.size() > poppingCount && poppingCount >= 0)
			{
//				this.pushAllBottomLocally(cacheKey, values.subList(values.size() - poppingCount - 1, values.size() - 1));
				super.pushAllBottomAtBase(cacheKey, values.subList(poppingCount, values.size()));
			}
		}
	}

	public Value pop(StackNotification notification)
	{
		if (super.getStackSizeAtBase(notification.getCacheKey()) <= this.prefetchThresholdSize)
		{
			if (!this.prefetchDispatcher.isReady())
			{
				this.threadPool.execute(this.prefetchDispatcher);
			}
			this.prefetchDispatcher.enqueue(notification);
		}
		Value v = super.popAtBase(notification.getCacheKey());
		if (v != null)
		{
			return v;
		}
		if (!this.postfetchDispatcher.isReady())
		{
			this.threadPool.execute(this.postfetchDispatcher);
		}
		notification.setPostfetchCount(this.prefetchCount + 1);
		this.postfetchDispatcher.enqueue(notification);
		if (notification.isBlocking())
		{
			this.syncs.put(notification.getKey(), new Sync(false));
			this.syncs.get(notification.getKey()).holdOn(this.postfetchTimeout);
			if (this.postfetchedData.get(notification.getKey()).size() > 0)
			{
				v = this.postfetchedData.get(notification.getKey()).get(0);
//				System.out.println("DistributedStackStore-pop() [single]: " + v.getCacheKey() + "'s element is obtained!");
			}
			else
			{
//				System.out.println("DistributedStackStore-pop() [single]: " + notification.getCacheKey() + "'s element is NOT obtained!");
			}
			this.postfetchedData.remove(notification.getKey());
			return v;
		}
		return v;
	}
	
	public List<Value> popAll(StackNotification notification) throws DistributedListFetchException
	{
		if (super.getStackSizeAtBase(notification.getCacheKey()) <= this.prefetchThresholdSize)
		{
			if (!this.prefetchDispatcher.isReady())
			{
				this.threadPool.execute(this.prefetchDispatcher);
			}
			this.prefetchDispatcher.enqueue(notification);
		}
		if (notification.getKickOffCount() < super.getCacheSizeAtBase())
		{
			List<Value> values = super.popAtBase(notification.getCacheKey(), notification.getKickOffCount());
			if (values != null)
			{
				if (values.size() >= notification.getKickOffCount())
				{
					return values;
				}
				else
				{
					if (notification.isBlocking())
					{
						/*
						 * In the previous solution, it assumes that the terminal cache has the data that covers its distributed ones. But in practice, the assumption might not be correct. So it is reasonable to put the loaded data into the postfetchedData. Otherwise, the data from the terminal might overwrite the loaded data. 04/30/2019, Bing Li
						 */
						this.postfetchedData.put(notification.getKey(), values);
					}
				}
			}
			if (!this.postfetchDispatcher.isReady())
			{
				this.threadPool.execute(this.postfetchDispatcher);
			}
			notification.setPostfetchCount(notification.getKickOffCount() - values.size() + this.prefetchCount);
			this.postfetchDispatcher.enqueue(notification);
			if (notification.isBlocking())
			{
				this.syncs.put(notification.getKey(), new Sync(false));
				this.syncs.get(notification.getKey()).holdOn(this.postfetchTimeout);
				List<Value> restValues = this.postfetchedData.get(notification.getKey());
				this.postfetchedData.remove(notification.getKey());
				if (restValues != null)
				{
					values.addAll(restValues);
				}
				return values;
			}
			return values;
		}
		else
		{
			throw new DistributedListFetchException(super.getCacheSizeAtBase(), notification.getKickOffCount());
		}
	}
	
	public Value peekLocally(String stackKey)
	{
		return super.peekAtBase(stackKey);
	}
	
	public Value peek(StackNotification notification)
	{
		if (super.getStackSizeAtBase(notification.getCacheKey()) <= this.prefetchThresholdSize)
		{
			if (!this.prefetchDispatcher.isReady())
			{
				this.threadPool.execute(this.prefetchDispatcher);
			}
			this.prefetchDispatcher.enqueue(notification);
		}
		Value v = super.peekAtBase(notification.getCacheKey());
		if (v != null)
		{
			return v;
		}
		if (!this.postfetchDispatcher.isReady())
		{
			this.threadPool.execute(this.postfetchDispatcher);
		}
		notification.setPostfetchCount(this.prefetchCount + 1);
		this.postfetchDispatcher.enqueue(notification);
		if (notification.isBlocking())
		{
			this.syncs.put(notification.getKey(), new Sync(false));
			this.syncs.get(notification.getKey()).holdOn(this.postfetchTimeout);
//			return super.peek(notification.getCacheKey());
//			v = this.postfetchedSingleData.get(notification.getKey());
//			this.postfetchedSingleData.remove(notification.getKey());
			if (this.postfetchedData.get(notification.getKey()).size() > 0)
			{
				v = this.postfetchedData.get(notification.getKey()).get(0);
			}
			this.postfetchedData.remove(notification.getKey());
			return v;
		}
		return v;
	}
	
	public List<Value> peekAllLocally(String stackKey, int endIndex)
	{
		return super.peekAtBase(stackKey, endIndex);
	}
	
	public List<Value> peekAll(StackNotification notification) throws DistributedListFetchException
	{
		if (super.getStackSizeAtBase(notification.getCacheKey()) <= this.prefetchThresholdSize)
		{
			if (!this.prefetchDispatcher.isReady())
			{
				this.threadPool.execute(this.prefetchDispatcher);
			}
			this.prefetchDispatcher.enqueue(notification);
		}
		if (notification.getKickOffCount() < super.getCacheSizeAtBase())
		{
			List<Value> values = super.peekAtBase(notification.getCacheKey(), notification.getKickOffCount());
			if (values != null)
			{
				if (values.size() >= notification.getKickOffCount())
				{
					return values;
				}
				else
				{
					if (notification.isBlocking())
					{
						/*
						 * In the previous solution, it assumes that the terminal cache has the data that covers its distributed ones. But in practice, the assumption might not be correct. So it is reasonable to put the loaded data into the postfetchedData. Otherwise, the data from the terminal might overwrite the loaded data. 04/30/2019, Bing Li
						 */
						this.postfetchedData.put(notification.getKey(), values);
					}
				}
			}
			if (!this.postfetchDispatcher.isReady())
			{
				this.threadPool.execute(this.postfetchDispatcher);
			}
			notification.setPostfetchCount(notification.getKickOffCount() - values.size() + this.prefetchCount);
			this.postfetchDispatcher.enqueue(notification);
			if (notification.isBlocking())
			{
				this.syncs.put(notification.getKey(), new Sync(false));
				this.syncs.get(notification.getKey()).holdOn(this.postfetchTimeout);
				List<Value> restValues = this.postfetchedData.get(notification.getKey());
				this.postfetchedData.remove(notification.getKey());
				if (restValues != null)
				{
					values.addAll(restValues);
				}
				return values;
			}
			return values;
		}
		else
		{
			throw new DistributedListFetchException(super.getCacheSizeAtBase(), notification.getKickOffCount());
		}
	}

	/*
	 * The method is designed for Testing only. 05/30/2019, Bing Li
	 */
	public List<Value> peekAll(String key, StackNotification notification) throws DistributedListFetchException
	{
		if (super.getStackSizeAtBase(notification.getCacheKey()) <= this.prefetchThresholdSize)
		{
			if (!this.prefetchDispatcher.isReady())
			{
				this.threadPool.execute(this.prefetchDispatcher);
			}
			this.prefetchDispatcher.enqueue(notification);
		}
		if (notification.getKickOffCount() < super.getCacheSizeAtBase())
		{
			List<Value> values = super.peekAtBase(notification.getCacheKey(), notification.getKickOffCount());
//			System.out.println("DistributedReadStackStore-peekAll(): channel = " + key);
//			System.out.println("DistributedReadStackStore-peekAll(): values' size = " + values.size());
//			System.out.println("DistributedReadStackStore-peekAll(): kickOffCount = " + notification.getKickOffCount());
			if (values != null)
			{
				if (values.size() >= notification.getKickOffCount())
				{
					return values;
				}
				else
				{
					if (notification.isBlocking())
					{
						/*
						 * In the previous solution, it assumes that the terminal cache has the data that covers its distributed ones. But in practice, the assumption might not be correct. So it is reasonable to put the loaded data into the postfetchedData. Otherwise, the data from the terminal might overwrite the loaded data. 04/30/2019, Bing Li
						 */
						this.postfetchedData.put(notification.getKey(), values);
					}
				}
			}
			if (!this.postfetchDispatcher.isReady())
			{
				this.threadPool.execute(this.postfetchDispatcher);
			}
			notification.setPostfetchCount(notification.getKickOffCount() - values.size() + this.prefetchCount);
			this.postfetchDispatcher.enqueue(notification);
			if (notification.isBlocking())
			{
				this.syncs.put(notification.getKey(), new Sync(false));
				this.syncs.get(notification.getKey()).holdOn(this.postfetchTimeout);
				List<Value> restValues = this.postfetchedData.get(notification.getKey());
				this.postfetchedData.remove(notification.getKey());
				if (restValues != null)
				{
					values.addAll(restValues);
				}
				return values;
			}
			return values;
		}
		else
		{
			throw new DistributedListFetchException(super.getCacheSizeAtBase(), notification.getKickOffCount());
		}
	}

	public List<Value> peekRangeLocally(String stackKey, int startIndex, int endIndex) throws IndexOutOfRangeException
	{
		return super.peekAtBase(stackKey, startIndex, endIndex);
	}

	public List<Value> peekRange(StackNotification notification) throws IndexOutOfRangeException
	{
		if ((super.getStackSizeAtBase(notification.getCacheKey()) - notification.getEndIndex()) <= this.prefetchThresholdSize)
		{
			if (!this.prefetchDispatcher.isReady())
			{
				this.threadPool.execute(this.prefetchDispatcher);
			}
			this.prefetchDispatcher.enqueue(notification);
		}
		List<Value> values = super.peekAtBase(notification.getCacheKey(), notification.getStartIndex(), notification.getEndIndex());
		if (values != null)
		{
//			if (values.size() >= notification.getPeekRangeSize())
			if (values.size() >= notification.getKickOffCount())
			{
				return values;
			}
			else
			{
				if (notification.isBlocking())
				{
					/*
					 * In the previous solution, it assumes that the terminal cache has the data that covers its distributed ones. But in practice, the assumption might not be correct. So it is reasonable to put the loaded data into the postfetchedData. Otherwise, the data from the terminal might overwrite the loaded data. 04/30/2019, Bing Li
					 */
					this.postfetchedData.put(notification.getKey(), values);
				}
			}
		}
		if (!this.postfetchDispatcher.isReady())
		{
			this.threadPool.execute(this.postfetchDispatcher);
		}
//		notification.setPostfetchCount(notification.getPeekRangeSize() - values.size()  + this.prefetchCount);
		notification.setPostfetchCount(notification.getKickOffCount() - values.size()  + this.prefetchCount);
		this.postfetchDispatcher.enqueue(notification);
		if (notification.isBlocking())
		{
			this.syncs.put(notification.getKey(), new Sync(false));
			this.syncs.get(notification.getKey()).holdOn(this.postfetchTimeout);
			List<Value> restValues = this.postfetchedData.get(notification.getKey());
			this.postfetchedData.remove(notification.getKey());
			if (restValues != null)
			{
				values.addAll(restValues);
			}
			return values;
		}
		return values;
	}
	
	public Value get(StackNotification notification) throws IndexOutOfRangeException
	{
		if ((super.getStackSizeAtBase(notification.getCacheKey()) - notification.getStartIndex() - 1) <= this.prefetchThresholdSize)
		{
			if (!this.prefetchDispatcher.isReady())
			{
				this.threadPool.execute(this.prefetchDispatcher);
			}
			this.prefetchDispatcher.enqueue(notification);
		}
		Value v = super.getAtBase(notification.getCacheKey(), notification.getStartIndex());
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
			this.postfetchedData.remove(notification.getKey());
			return super.getAtBase(notification.getCacheKey(), notification.getStartIndex());
		}
		return v;
	}

	public void pushAllLocally(String stackKey, List<Value> values)
	{
		super.pushAllAtBase(stackKey, values);
	}
	
	public void pushLocally(String stackKey, Value v)
	{
		super.pushAtBase(stackKey, v);
	}

	public void pushAllBottomLocally(String cacheKey, List<Value> values)
	{
		super.pushAllBottomAtBase(cacheKey, values);
	}
	
	public void pushBottomLocally(String cacheKey, Value v)
	{
		super.pushBottomAtBase(cacheKey, v);
	}

	@Override
	public void evict(String k, Value v) throws TerminalServerOverflowedException
	{
//		EvictedNotification<Value> notification = new EvictedNotification<Value>(k, v);
		if (!this.evictedDispatcher.isReady())
		{
			this.threadPool.execute(this.evictedDispatcher);
		}
//		this.evictedDispatcher.enqueue(new EvictedNotification<Value>(k, v));
		// The Ehcache assigns the key through the evict(String k, Value v) interface. But it usually does not make sense to the application level. So I attempt the assign the cache key to replace it since the cache key sometimes is used on the application level, e.g., the CircleTimingPageCache. In the case, the cache key is not generated by the information from the Value, i.e., TimingPage. Thus, it is required to keep the cache key in the notification. Usually, the store needs to be updated for that. 08/09/2019, Bing Li
		this.evictedDispatcher.enqueue(new EvictedNotification<Value>(v.getCacheKey(), v));
		if (super.isEmptyAtBase(v.getCacheKey()))
		{
			super.removeStackAtBase(v.getCacheKey());
		}
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
