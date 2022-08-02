package org.greatfree.cache.distributed;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
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
 * Compared with DistributedMap, SemiDistributedMap has no the feature of replicating. 06/30/2018, Bing Li
 */

/*
 * The cache is used in the case when no new data is created through the writing methods of the cache. It seldom happens. At this moment, it is used in com.greatfree.aserver.caching.SuperHumanNeighborCache only. 06/10/2018, Bing Li
 */

// Created: 03/10/2018, Bing Li
public class ReadDistributedMap<Value extends Serializable, Factory extends CacheMapFactorable<String, Value>, Notification extends MapPostfetchNotification, PostfetchThread extends NotificationObjectQueue<Notification>, PostfetchThreadCreator extends NotificationObjectThreadCreatable<Notification, PostfetchThread>, EvictThread extends NotificationObjectQueue<EvictedNotification<Value>>, EvictThreadCreator extends NotificationObjectThreadCreatable<EvictedNotification<Value>, EvictThread>> extends RootCache<String, Value, Factory, StringKeyDB>
{
	private NotificationObjectDispatcher<Notification, PostfetchThread, PostfetchThreadCreator> postfetchDispatcher;
	private NotificationObjectDispatcher<EvictedNotification<Value>, EvictThread, EvictThreadCreator> evictedDispatcher;
	
	private ThreadPool threadPool;
	private Map<String, Sync> syncs;
	private final long postfetchTimeout;
//	private ReentrantReadWriteLock lock;

	private Map<String, Map<String, Value>> postfetchedData;

	public ReadDistributedMap(ReadDistributedMapBuilder<Value, Factory, Notification, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> builder)
	{
		// Since an explicit thread is employed, the synchronous eventing is fine. 05/13/2018, Bing Li
		super(builder.getFactory(), builder.getRootPath(), builder.getCacheKey(), builder.getCacheSize(), builder.getOffheapSizeInMB(), builder.getDiskSizeInMB(), StringKeyDBPool.SHARED().getDB(CacheConfig.getMapCacheKeyDBPath(builder.getRootPath(), builder.getCacheKey())), false);
	
		this.postfetchDispatcher = new NotificationObjectDispatcher.NotificationObjectDispatcherBuilder<Notification, PostfetchThread, PostfetchThreadCreator>()
				.poolSize(builder.getPoolSize())
				.threadCreator(builder.getPostfetchThreadCreator())
				.notificationQueueSize(builder.getMaxTaskSize())
				.dispatcherWaitTime(builder.getDispatcherWaitTime())
//				.waitRound(builder.getWaitRound())
				.idleCheckDelay(builder.getIdleCheckDelay())
				.idleCheckPeriod(builder.getIdleCheckPeriod())
				.scheduler(builder.getScheduler())
				.build();

		this.evictedDispatcher = new NotificationObjectDispatcher.NotificationObjectDispatcherBuilder<EvictedNotification<Value>, EvictThread, EvictThreadCreator>()
				.poolSize(builder.getPoolSize())
				.threadCreator(builder.getEvictThreadCreator())
				.notificationQueueSize(builder.getMaxTaskSize())
				.dispatcherWaitTime(builder.getDispatcherWaitTime())
//				.waitRound(builder.getWaitRound())
				.idleCheckDelay(builder.getIdleCheckDelay())
				.idleCheckPeriod(builder.getIdleCheckPeriod())
				.scheduler(builder.getScheduler())
				.build();

		this.threadPool = builder.getThreadPool();
		this.syncs = new ConcurrentHashMap<String, Sync>();
		this.postfetchTimeout = builder.getPostfetchTimeout();
//		this.lock = new ReentrantReadWriteLock();
		
		this.postfetchedData = new ConcurrentHashMap<String, Map<String, Value>>();
	}
	
	public static class ReadDistributedMapBuilder<Value extends Serializable, Factory extends CacheMapFactorable<String, Value>, Notification extends MapPostfetchNotification, PostfetchThread extends NotificationObjectQueue<Notification>, PostfetchThreadCreator extends NotificationObjectThreadCreatable<Notification, PostfetchThread>, EvictThread extends NotificationObjectQueue<EvictedNotification<Value>>, EvictThreadCreator extends NotificationObjectThreadCreatable<EvictedNotification<Value>, EvictThread>> implements Builder<ReadDistributedMap<Value, Factory, Notification, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator>>
	{
		private String cacheKey;
		private Factory factory;
		private String rootPath;
//		private int cacheSize;
		private long cacheSize;
		private int offheapSizeInMB;
		private int diskSizeInMB;

		private int poolSize;
		private PostfetchThreadCreator postfetchCreator;
		private EvictThreadCreator evictCreator;
		private int notificationQueueSize;
		private long dispatcherWaitTime;
//		private int waitRound;
		private long idleCheckDelay;
		private long idleCheckPeriod;
		private ScheduledThreadPoolExecutor scheduler;
		private ThreadPool threadPool;
		private long postfetchTimeout;
		
		public ReadDistributedMapBuilder()
		{
		}
		
		public ReadDistributedMapBuilder<Value, Factory,  Notification, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> cacheKey(String cacheKey)
		{
			this.cacheKey = cacheKey;
			return this;
		}
		
		public ReadDistributedMapBuilder<Value, Factory,  Notification, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> factory(Factory factory)
		{
			this.factory = factory;
			return this;
		}
		
		public ReadDistributedMapBuilder<Value, Factory,  Notification, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> rootPath(String rootPath)
		{
			this.rootPath = rootPath;
			return this;
		}
		
		public ReadDistributedMapBuilder<Value, Factory,  Notification, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> cacheSize(long cacheSize)
		{
			this.cacheSize = cacheSize;
			return this;
		}
		
		public ReadDistributedMapBuilder<Value, Factory,  Notification, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> offheapSizeInMB(int offheapSizeInMB)
		{
			this.offheapSizeInMB = offheapSizeInMB;
			return this;
		}
		
		public ReadDistributedMapBuilder<Value, Factory,  Notification, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> diskSizeInMB(int diskSizeInMB)
		{
			this.diskSizeInMB = diskSizeInMB;
			return this;
		}

		public ReadDistributedMapBuilder<Value, Factory,  Notification, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> poolSize(int poolSize)
		{
			this.poolSize = poolSize;
			return this;
		}

		public ReadDistributedMapBuilder<Value, Factory,  Notification, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> postfetchCreator(PostfetchThreadCreator postfetchCreator)
		{
			this.postfetchCreator = postfetchCreator;
			return this;
		}

		public ReadDistributedMapBuilder<Value, Factory,  Notification, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> evictCreator(EvictThreadCreator evictCreator)
		{
			this.evictCreator = evictCreator;
			return this;
		}

		public ReadDistributedMapBuilder<Value, Factory,  Notification, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> notificationQueueSize(int nqSize)
		{
			this.notificationQueueSize = nqSize;
			return this;
		}
		
		public ReadDistributedMapBuilder<Value, Factory,  Notification, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> dispatcherWaitTime(long dispatcherWaitTime)
		{
			this.dispatcherWaitTime = dispatcherWaitTime;
			return this;
		}

		/*
		public ReadDistributedMapBuilder<Value, Factory,  Notification, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> waitRound(int waitRound)
		{
			this.waitRound = waitRound;
			return this;
		}
		*/
		
		public ReadDistributedMapBuilder<Value, Factory,  Notification, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> idleCheckDelay(long idleCheckDelay)
		{
			this.idleCheckDelay = idleCheckDelay;
			return this;
		}
		
		public ReadDistributedMapBuilder<Value, Factory,  Notification, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> idleCheckPeriod(long idleCheckPeriod)
		{
			this.idleCheckPeriod = idleCheckPeriod;
			return this;
		}
		
		public ReadDistributedMapBuilder<Value, Factory,  Notification, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> scheduler(ScheduledThreadPoolExecutor scheduler)
		{
			this.scheduler = scheduler;
			return this;
		}
		
		public ReadDistributedMapBuilder<Value, Factory,  Notification, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> threadPool(ThreadPool threadPool)
		{
			this.threadPool = threadPool;
			return this;
		}

		public ReadDistributedMapBuilder<Value, Factory,  Notification, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> postfetchTimeout(long postfetchTimeout)
		{
			this.postfetchTimeout = postfetchTimeout;
			return this;
		}

		@Override
		public ReadDistributedMap<Value, Factory, Notification, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> build()
		{
			return new ReadDistributedMap<Value, Factory, Notification, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator>(this);
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

		public PostfetchThreadCreator getPostfetchThreadCreator()
		{
			return this.postfetchCreator;
		}
		
		public EvictThreadCreator getEvictThreadCreator()
		{
			return this.evictCreator;
		}
		
		public int getMaxTaskSize()
		{
			return this.notificationQueueSize;
		}
		
		public long getDispatcherWaitTime()
		{
			return this.dispatcherWaitTime;
		}

		/*
		public int getWaitRound()
		{
			return this.waitRound;
		}
		*/
		
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

		public long getPostfetchTimeout()
		{
			return this.postfetchTimeout;
		}

		public ThreadPool getThreadPool()
		{
			return this.threadPool;
		}
	}

	public void shutdown() throws InterruptedException
	{
//		this.lock.writeLock().lock();
		super.closeAtBase();
//		this.lock.writeLock().unlock();

		this.postfetchedData.clear();
		this.postfetchedData = null;

		this.postfetchDispatcher.dispose();
		this.evictedDispatcher.dispose();
		for (Sync entry : this.syncs.values())
		{
			entry.signal();
		}
		this.syncs.clear();
		this.syncs = null;
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
				// The notify/wait mechanism results in the concurrently accessing on the value. It might lead to ConcurrentModificationException. Since only one piece of data is posfetched, it can be saved into the cache first. 08/04/2018, Bing Li
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
	
	public void savePostfetchedData(String key, String dataKey, boolean isBlocking)
	{
		super.putAtBase(dataKey, null);
		if (isBlocking)
		{
			this.signal(key);
		}
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
	
	/*
	public String getCacheKey()
	{
		return this.getCacheKey();
	}
	*/
	
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

	public Value get(Notification notification)
	{
//		this.lock.readLock().lock();
		Value v = super.getAtBase(notification.getResourceKey());
//		this.lock.readLock().unlock();
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
			return super.getAtBase(notification.getResourceKey());
		}
		return v;
	}
	
	public Map<String, Value> getValues(Notification notification) throws DistributedMapFetchException
	{
		if (notification.getResourceKeys().size() <= super.getCacheSizeAtBase())
		{
//			this.lock.readLock().lock();
			Map<String, Value> vs = super.getAtBase(notification.getResourceKeys());
	//			this.lock.readLock().unlock();
			if (vs != null)
			{
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
				this.syncs.get(notification.getKey()).holdOn(this.postfetchTimeout);
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
	
	public boolean containsKey(Notification notification)
	{
//		this.lock.readLock().lock();
		boolean isExisted = super.containsKeyAtBase(notification.getResourceKey());
//		this.lock.readLock().unlock();
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
			this.syncs.get(notification.getKey()).holdOn(this.postfetchTimeout);
			return super.containsKeyAtBase(notification.getResourceKey());
		}
		return isExisted;
	}

	/*
	public boolean containsKey(String key)
	{
		this.lock.readLock().lock();
		try
		{
			return super.containsKey(key);
		}
		finally
		{
			this.lock.readLock().unlock();
		}
	}
	*/
	
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

	/*
	public int size()
	{
		return this.size();
	}
	
	public long getEmptySize()
	{
		return this.getEmptySize();
	}

	public boolean isCacheFull()
	{
		return this.isCacheFull();
	}
	
	public Set<String> getKeys()
	{
		return this.getKeys();
	}
	*/

	/*
	 * It is not proper to return all of the values in the caches. It might overload the memory. Meanwhile, for a distributed cache, it is not possible to return all of the values without postfetching. The keys of the values are required when loading data from the cache. 07/03/2018, Bing Li
	 */
	/*
	public Map<String, Value> getValues()
	{
		this.lock.readLock().lock();
		try
		{
			return super.getValues();
		}
		finally
		{
			this.lock.readLock().unlock();
		}
	}
	*/

	/*
	public void remove(Set<String> keys)
	{
		this.remove(keys);
	}
	
	public void remote(String key)
	{
		this.remove(key);
	}
	
	public void clear()
	{
		this.clear();
	}
	*/

	@Override
	public void evict(String k, Value v)
	{
//		this.lock.writeLock().lock();

//		this.lock.writeLock().unlock();
//		EvictedNotification<Value> notification = new EvictedNotification<Value>(k, v);
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