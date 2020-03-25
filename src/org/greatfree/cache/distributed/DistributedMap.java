package org.greatfree.cache.distributed;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import org.greatfree.cache.CacheConfig;
import org.greatfree.cache.db.StringKeyDB;
import org.greatfree.cache.db.StringKeyDBPool;
import org.greatfree.cache.local.CacheMapFactorable;
import org.greatfree.cache.local.RootCache;
import org.greatfree.concurrency.Sync;
import org.greatfree.concurrency.ThreadPool;
import org.greatfree.concurrency.reactive.NotificationObjectDispatcher;
import org.greatfree.concurrency.reactive.NotificationObjectQueue;
import org.greatfree.concurrency.reactive.NotificationObjectThreadCreatable;
import org.greatfree.exceptions.DistributedMapFetchException;
import org.greatfree.util.Builder;

/*
 * The version is tested in the Clouds project. 08/22/2018, Bing Li
 */

/*
 * 07/10/2018, Bing Li
 * 
 * One tough problem is that the lock I added causes the reading operations cannot be performed when the amount of data to be written is large.
 * 
 * In the test cases, the reading cannot be done for the locking when the number of data reaches 20000.
 * 
 * I decide to remove the lock.
 * 
 * Although it might cause the problem of inconsistency, it is not a critical issue in my system.
 * 
 */

// Created: 03/15/2018, Bing Li

/*
 * I attempt to replace EvictThread with ReplicateThread. But the approach to process replicated data and evicted data might be different. So I have to keep both of them. 06/10/2018, Bing Li
 */
public class DistributedMap<Value extends Serializable, Factory extends CacheMapFactorable<String, Value>, ReplicateThread extends NotificationObjectQueue<MapReplicateNotification<Value>>, ReplicateThreadCreator extends NotificationObjectThreadCreatable<MapReplicateNotification<Value>, ReplicateThread>, PostfetchNotification extends MapPostfetchNotification, PostfetchThread extends NotificationObjectQueue<PostfetchNotification>, PostfetchThreadCreator extends NotificationObjectThreadCreatable<PostfetchNotification, PostfetchThread>, EvictThread extends NotificationObjectQueue<EvictedNotification<Value>>, EvictThreadCreator extends NotificationObjectThreadCreatable<EvictedNotification<Value>, EvictThread>> extends RootCache<String, Value, Factory, StringKeyDB>
{
	private NotificationObjectDispatcher<MapReplicateNotification<Value>, ReplicateThread, ReplicateThreadCreator> replicateDispatcher;
	private NotificationObjectDispatcher<PostfetchNotification, PostfetchThread, PostfetchThreadCreator> postfetchDispatcher;
	private NotificationObjectDispatcher<EvictedNotification<Value>, EvictThread, EvictThreadCreator> evictedDispatcher;
	
	private ThreadPool threadPool;
	private Map<String, Sync> syncs;
	private final long fetchTimeout;
	
	private Map<String, Map<String, Value>> postfetchedData;

//	private ReentrantReadWriteLock lock;

	public DistributedMap(DistributedMapBuilder<Value, Factory, ReplicateThread, ReplicateThreadCreator, PostfetchNotification, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> builder)
	{
		// Since an explicit thread is employed, the synchronous eventing is fine. 05/13/2018, Bing Li
		super(builder.getFactory(), builder.getRootPath(), builder.getCacheKey(), builder.getCacheSize(), builder.getOffheapSizeInMB(), builder.getDiskSizeInMB(), StringKeyDBPool.SHARED().getDB(CacheConfig.getMapCacheKeyDBPath(builder.getRootPath(), builder.getCacheKey())), false);

		this.replicateDispatcher = new NotificationObjectDispatcher.NotificationObjectDispatcherBuilder<MapReplicateNotification<Value>, ReplicateThread, ReplicateThreadCreator>()
				.poolSize(builder.getPoolSize())
				.threadCreator(builder.getReplicateThreadCreator())
				.notificationQueueSize(builder.getNotificationQueueSize())
				.dispatcherWaitTime(builder.getDispatcherWaitTime())
				.waitRound(builder.getWaitRound())
				.idleCheckDelay(builder.getIdleCheckDelay())
				.idleCheckPeriod(builder.getIdleCheckPeriod())
				.scheduler(builder.getScheduler())
				.build();

		this.postfetchDispatcher = new NotificationObjectDispatcher.NotificationObjectDispatcherBuilder<PostfetchNotification, PostfetchThread, PostfetchThreadCreator>()
				.poolSize(builder.getPoolSize())
				.threadCreator(builder.getFetchThreadCreator())
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
		
		this.postfetchedData = new ConcurrentHashMap<String, Map<String, Value>>();
		
		this.threadPool = builder.getThreadPool();
		this.syncs = new ConcurrentHashMap<String, Sync>();
		this.fetchTimeout = builder.getfetchTimeout();
//		this.lock = new ReentrantReadWriteLock();
	}
	
	public static class DistributedMapBuilder<Value extends Serializable, Factory extends CacheMapFactorable<String, Value>, ReplicateThread extends NotificationObjectQueue<MapReplicateNotification<Value>>, ReplicateThreadCreator extends NotificationObjectThreadCreatable<MapReplicateNotification<Value>, ReplicateThread>, PostfetchNotification extends MapPostfetchNotification, PostfetchThread extends NotificationObjectQueue<PostfetchNotification>, PostfetchThreadCreator extends NotificationObjectThreadCreatable<PostfetchNotification, PostfetchThread>, EvictThread extends NotificationObjectQueue<EvictedNotification<Value>>, EvictThreadCreator extends NotificationObjectThreadCreatable<EvictedNotification<Value>, EvictThread>> implements Builder<DistributedMap<Value, Factory, ReplicateThread, ReplicateThreadCreator, PostfetchNotification, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator>>
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
		private PostfetchThreadCreator postfetchCreator;
		private EvictThreadCreator evictCreator;
		private int notificationQueueSize;
		private long dispatcherWaitTime;
		private int waitRound;
		private long idleCheckDelay;
		private long idleCheckPeriod;
		private ScheduledThreadPoolExecutor scheduler;
		private ThreadPool threadPool;
		private long fetchTimeout;
		
		public DistributedMapBuilder()
		{
		}

		public DistributedMapBuilder<Value, Factory, ReplicateThread, ReplicateThreadCreator, PostfetchNotification, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> cacheKey(String cacheKey)
		{
			this.cacheKey = cacheKey;
			return this;
		}

		public DistributedMapBuilder<Value, Factory, ReplicateThread, ReplicateThreadCreator, PostfetchNotification, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> factory(Factory factory)
		{
			this.factory = factory;
			return this;
		}

		public DistributedMapBuilder<Value, Factory, ReplicateThread, ReplicateThreadCreator, PostfetchNotification, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> rootPath(String rootPath)
		{
			this.rootPath = rootPath;
			return this;
		}

		public DistributedMapBuilder<Value, Factory, ReplicateThread, ReplicateThreadCreator, PostfetchNotification, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> cacheSize(int cacheSize)
		{
			this.cacheSize = cacheSize;
			return this;
		}

		public DistributedMapBuilder<Value, Factory, ReplicateThread, ReplicateThreadCreator, PostfetchNotification, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> offheapSizeInMB(int offheapSizeInMB)
		{
			this.offheapSizeInMB = offheapSizeInMB;
			return this;
		}

		public DistributedMapBuilder<Value, Factory, ReplicateThread, ReplicateThreadCreator, PostfetchNotification, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> diskSizeInMB(int diskSizeInMB)
		{
			this.diskSizeInMB = diskSizeInMB;
			return this;
		}

		public DistributedMapBuilder<Value, Factory, ReplicateThread, ReplicateThreadCreator, PostfetchNotification, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> poolSize(int poolSize)
		{
			this.poolSize = poolSize;
			return this;
		}

		public DistributedMapBuilder<Value, Factory, ReplicateThread, ReplicateThreadCreator, PostfetchNotification, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> replicateCreator(ReplicateThreadCreator replicateCreator)
		{
			this.replicateCreator = replicateCreator;
			return this;
		}

		public DistributedMapBuilder<Value, Factory, ReplicateThread, ReplicateThreadCreator, PostfetchNotification, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> postfetchCreator(PostfetchThreadCreator postfetchCreator)
		{
			this.postfetchCreator = postfetchCreator;
			return this;
		}

		public DistributedMapBuilder<Value, Factory, ReplicateThread, ReplicateThreadCreator, PostfetchNotification, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> evictCreator(EvictThreadCreator evictCreator)
		{
			this.evictCreator = evictCreator;
			return this;
		}

		public DistributedMapBuilder<Value, Factory, ReplicateThread, ReplicateThreadCreator, PostfetchNotification, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> maxTaskSize(int maxTaskSize)
		{
			this.notificationQueueSize = maxTaskSize;
			return this;
		}

		public DistributedMapBuilder<Value, Factory, ReplicateThread, ReplicateThreadCreator, PostfetchNotification, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> dispatcherWaitTime(long dispatcherWaitTime)
		{
			this.dispatcherWaitTime = dispatcherWaitTime;
			return this;
		}

		public DistributedMapBuilder<Value, Factory, ReplicateThread, ReplicateThreadCreator, PostfetchNotification, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> waitRound(int waitRound)
		{
			this.waitRound = waitRound;
			return this;
		}

		public DistributedMapBuilder<Value, Factory, ReplicateThread, ReplicateThreadCreator, PostfetchNotification, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> idleCheckDelay(long idleCheckDelay)
		{
			this.idleCheckDelay = idleCheckDelay;
			return this;
		}

		public DistributedMapBuilder<Value, Factory, ReplicateThread, ReplicateThreadCreator, PostfetchNotification, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> idleCheckPeriod(long idleCheckPeriod)
		{
			this.idleCheckPeriod = idleCheckPeriod;
			return this;
		}

		public DistributedMapBuilder<Value, Factory, ReplicateThread, ReplicateThreadCreator, PostfetchNotification, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> scheduler(ScheduledThreadPoolExecutor scheduler)
		{
			this.scheduler = scheduler;
			return this;
		}

		public DistributedMapBuilder<Value, Factory, ReplicateThread, ReplicateThreadCreator, PostfetchNotification, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> threadPool(ThreadPool threadPool)
		{
			this.threadPool = threadPool;
			return this;
		}

		public DistributedMapBuilder<Value, Factory, ReplicateThread, ReplicateThreadCreator, PostfetchNotification, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> postfetchTimeout(long fetchTimeout)
		{
			this.fetchTimeout = fetchTimeout;
			return this;
		}

		@Override
		public DistributedMap<Value, Factory, ReplicateThread, ReplicateThreadCreator, PostfetchNotification, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> build()
		{
			return new DistributedMap<Value, Factory, ReplicateThread, ReplicateThreadCreator, PostfetchNotification, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator>(this);
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
		
		public PostfetchThreadCreator getFetchThreadCreator()
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

		public ThreadPool getThreadPool()
		{
			return this.threadPool;
		}

		public long getfetchTimeout()
		{
			return this.fetchTimeout;
		}
	}
	
	public void shutdown() throws InterruptedException
	{
//		this.lock.writeLock().lock();
		super.closeAtBase();
//		this.lock.writeLock().unlock();
		
		this.postfetchedData.clear();
//		this.postfetchedData = null;

		this.replicateDispatcher.dispose();
		this.postfetchDispatcher.dispose();
		this.evictedDispatcher.dispose();
		for (Sync entry : this.syncs.values())
		{
			entry.signal();
		}
		this.syncs.clear();
//		this.syncs = null;
	}

	public boolean isDown()
	{
		/*
		this.lock.readLock().lock();
		try
		{
			return super.isDown();
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		*/
		return super.isDownAtBase();
	}
	
	public long getSize()
	{
		/*
		this.lock.readLock().lock();
		try
		{
			return super.size();
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		*/
		return super.getMemCacheSizeAtBase();
	}
	
	private void signal(String key)
	{
		this.syncs.get(key).signal();
		this.syncs.remove(key);
	}
	
	public void savePosfetchedData(String key, Map<String, Value> values, boolean isBlocking)
	{
		if (isBlocking)
		{
			if (values != null)
			{
			// The notify/wait mechanism results in the concurrently accessing on the values. It might lead to ConcurrentModificationException. So it is necessary to send out the values first and then save them into the cache. Or make a copy. 08/04/2018, Bing Li
//			this.postfetchedData.put(key, values);
				if (!this.postfetchedData.containsKey(key))
				{
					this.postfetchedData.put(key, new HashMap<String, Value>(values));
				}
				else
				{
					this.postfetchedData.get(key).putAll(values);
				}
			}
			this.signal(key);
		}
		if (values != null)
		{
			super.putAllAtBase(values);
		}
	}
	
	public void savePostfetchedData(String key, String dataKey, Value v, boolean isBlocking)
	{
		if (v != null)
		{
			super.putAtBase(dataKey, v);
		}
		if (isBlocking)
		{
			this.signal(key);
		}
	}

	/*
	 * The method is designed for testing. 07/25/2019, Bing Li
	 */
	public void put(String key, MapReplicateNotification<Value> notification)
	{
//		System.out.println("DistributedMap-put(): key = " + notification.getKey());
		if (!this.replicateDispatcher.isReady())
		{
			this.threadPool.execute(this.replicateDispatcher);
		}
		this.replicateDispatcher.enqueue(notification);
		super.putAtBase(notification.getKey(), notification.getValue());
	}
	
	public void put(MapReplicateNotification<Value> notification)
	{
//		this.lock.writeLock().lock();
//		this.lock.writeLock().unlock();
		if (!this.replicateDispatcher.isReady())
		{
			this.threadPool.execute(this.replicateDispatcher);
		}
		this.replicateDispatcher.enqueue(notification);
		super.putAtBase(notification.getKey(), notification.getValue());
	}
	
	public void putAll(MapReplicateNotification<Value> notification)
	{
//		this.lock.writeLock().lock();
//		this.lock.writeLock().unlock();
		if (!this.replicateDispatcher.isReady())
		{
			this.threadPool.execute(this.replicateDispatcher);
		}
		this.replicateDispatcher.enqueue(notification);
		super.putAllAtBase(notification.getValues());
	}

	public void putLocally(String k, Value v)
	{
//		this.lock.writeLock().lock();
		super.putAtBase(k, v);
//		this.lock.writeLock().unlock();
	}
	
	public void putAllLocally(Map<String, Value> values)
	{
//		this.lock.writeLock().lock();
		super.putAllAtBase(values);
//		this.lock.writeLock().unlock();
	}
	
	/*
	 * The method is designed for testing. 07/25/2019, Bing Li
	 */
	public Value get(String key, PostfetchNotification notification)
	{
//		System.out.println("1) DistributedMap-get(): resourceKey = " + notification.getResourceKey());
		Value v = super.getAtBase(notification.getResourceKey());
		if (v != null)
		{
//			System.out.println("2) DistributedMap-get(): resourceKey = " + notification.getResourceKey());
			return v;
		}
//		System.out.println("3) DistributedMap-get(): resourceKey = " + notification.getResourceKey());
		if (!this.postfetchDispatcher.isReady())
		{
			this.threadPool.execute(this.postfetchDispatcher);
		}
		this.postfetchDispatcher.enqueue(notification);
		if (notification.isBlocking())
		{
			this.syncs.put(notification.getKey(), new Sync(false));
			this.syncs.get(notification.getKey()).holdOn(this.fetchTimeout);
			return super.getAtBase(notification.getResourceKey());
		}
		return v;
	}

	/*
	 * Through testing, the locking I added has something WRONG. Sometimes, when the amount of data to be written is large, the read-lock cannot be entered. 07/09/2018, Bing Li
	 */
	
	public Value get(PostfetchNotification notification)
	{
//		System.out.println("1) DistributedMap-get(PostfetchNotification): resourceKey = " + notification.getResourceKey());
//		this.lock.readLock().lock();
//		System.out.println("1.1) DistributedMap-get(PostfetchNotification): resourceKey = " + notification.getResourceKey());
		Value v = super.getAtBase(notification.getResourceKey());
//		System.out.println("1.2) DistributedMap-get(PostfetchNotification): resourceKey = " + notification.getResourceKey());
//		this.lock.readLock().unlock();
		if (v != null)
		{
//			System.out.println("2) DistributedMap-get(PostfetchNotification): value is obtained!");
			return v;
		}
//		System.out.println("2.5) DistributedMap-get(PostfetchNotification): value is obtained!");
		if (!this.postfetchDispatcher.isReady())
		{
			this.threadPool.execute(this.postfetchDispatcher);
		}
		this.postfetchDispatcher.enqueue(notification);
		if (notification.isBlocking())
		{
//			System.out.println("3) DistributedMap-get(PostfetchNotification): postfetching ...");
			this.syncs.put(notification.getKey(), new Sync(false));
			this.syncs.get(notification.getKey()).holdOn(this.fetchTimeout);
//			System.out.println("4) DistributedMap-get(PostfetchNotification): postfetching is DONE!");
			/*
			this.lock.readLock().lock();
			try
			{
				System.out.println("5) DistributedMap-get(PostfetchNotification): returning ...");
				return super.get(notification.getResourceKey());
			}
			finally
			{
				this.lock.readLock().unlock();
			}
			*/
			return super.getAtBase(notification.getResourceKey());
		}
		return v;
	}
	
	public Map<String, Value> getValues(PostfetchNotification notification) throws DistributedMapFetchException
	{
		if (notification.getResourceKeys().size() <= super.getCacheSizeAtBase())
		{
//			this.lock.readLock().lock();
//			System.out.println("DistributedMap-getValues(): resource keys size = " + notification.getResourceKeys().size());
			Map<String, Value> vs = super.getAtBase(notification.getResourceKeys());
	//			this.lock.readLock().unlock();
			if (vs != null)
			{
	//				System.out.println("DistributedMap-getValues(): values size = " + v.size());
				if (vs.size() >= notification.getResourceKeys().size())
				{
					return vs;
				}
				else
				{
					if (notification.isBlocking())
					{
						/*
						 * In the previous solution, it assumes that the terminal cache has the data that covers its distributed ones. But in practice, the assumption might not be correct. So it is reasonable to put the loaded data into the postfetchedData. Otherwise, the data from the terminal might overwrite the loaded data. 04/30/2019, Bing Li
						 */
						this.postfetchedData.put(notification.getKey(), vs);
					}
				}
			}
	//			notification.getResourceKeys().removeAll(v.keySet());
			if (!this.postfetchDispatcher.isReady())
			{
				this.threadPool.execute(this.postfetchDispatcher);
			}
			this.postfetchDispatcher.enqueue(notification);
			if (notification.isBlocking())
			{
				this.syncs.put(notification.getKey(), new Sync(false));
				this.syncs.get(notification.getKey()).holdOn(this.fetchTimeout);
				/*
				this.lock.readLock().lock();
				try
				{
					return super.get(notification.getResourceKeys());
				}
				finally
				{
					this.lock.readLock().unlock();
				}
				*/
		//			return super.get(notification.getResourceKeys());
				Map<String, Value> values = this.postfetchedData.get(notification.getKey());
				this.postfetchedData.remove(notification.getKey());
				return values;
			}
			return vs;
		}
		else
		{
			throw new DistributedMapFetchException(super.getCacheSizeAtBase(), notification.getResourceKeys().size());
		}
	}
	
	public Value getLocally(String key)
	{
		/*
		this.lock.readLock().lock();
		try
		{
			return super.get(key);
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		*/
		return super.getAtBase(key);
	}
	
	/*
	 * The method is used to return a random value from the cache. It is seldom used. In the N3W case, a random human is returned to borrow the logo. 08/27/2019, Bing Li
	 */
	public Value getRandomLocally()
	{
		return super.getRandomValue();
	}
	
	public Map<String, Value> getAllLocally(Set<String> keys)
	{
		return super.getAtBase(keys);
	}

	public boolean containsKey(String key)
	{
		return super.containsKeyAtBase(key);
	}
	
	public boolean containsKey(PostfetchNotification notification)
	{
//		this.lock.readLock().lock();
		boolean isExisted = super.containsKeyAtBase(notification.getResourceKey());
//		this.lock.readLock().unlock();
		if (isExisted)
		{
//			System.out.println("DistributedMap-containsKey(): It is existed LOCALLY ...");
			return true;
		}
//		System.out.println("DistributedMap-containsKey(): It is NOT existed LOCALLY ...");
		if (!this.postfetchDispatcher.isReady())
		{
			this.threadPool.execute(this.postfetchDispatcher);
		}
		this.postfetchDispatcher.enqueue(notification);
		if (notification.isBlocking())
		{
			this.syncs.put(notification.getKey(), new Sync(false));
			this.syncs.get(notification.getKey()).holdOn(this.fetchTimeout);
			/*
			this.lock.readLock().lock();
			try
			{
				return super.containsKey(notification.getResourceKey());
			}
			finally
			{
				this.lock.readLock().unlock();
			}
			*/
			return super.containsKeyAtBase(notification.getResourceKey());
		}
		return isExisted;
	}

	/*
	 * For testing. 08/23/2019, Bing Li
	 */
	public boolean containsKey(String key, PostfetchNotification notification)
	{
		boolean isExisted = super.containsKeyAtBase(notification.getResourceKey());
		if (isExisted)
		{
			return true;
		}
		if (!this.postfetchDispatcher.isReady())
		{
			this.threadPool.execute(this.postfetchDispatcher);
		}
		this.postfetchDispatcher.enqueue(notification);
		if (notification.isBlocking())
		{
			this.syncs.put(notification.getKey(), new Sync(false));
			this.syncs.get(notification.getKey()).holdOn(this.fetchTimeout);
			return super.containsKeyAtBase(notification.getResourceKey());
		}
		return isExisted;
	}

	public boolean isEmpty()
	{
		/*
		this.lock.readLock().lock();
		try
		{
			return super.isEmpty();
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		*/
		return super.isEmptyAtBase();
	}

	public Set<String> getKeys()
	{
		/*
		this.lock.readLock().lock();
		try
		{
			return super.getKeys();
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		*/
		return super.getKeysAtBase();
	}

	@Override
	public void evict(String k, Value v)
	{
//		this.lock.writeLock().lock();
		
//		this.lock.writeLock().unlock();
		if (!this.evictedDispatcher.isReady())
		{
			this.threadPool.execute(this.evictedDispatcher);
		}
		this.evictedDispatcher.enqueue(new EvictedNotification<Value>(k, v));
		// Commenting the below line does not affect the functions of the cache. But I think it might occupy much memory when the size becomes large. 08/01/2018, Bing Li
		// Since postfetching is performed, it is not necessary to remove the key? I need to ensure that. 07/31/2018, Bing Li
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
