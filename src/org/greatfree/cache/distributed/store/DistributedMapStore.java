package org.greatfree.cache.distributed.store;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import org.ehcache.Cache;
import org.ehcache.PersistentCacheManager;
import org.ehcache.event.EventFiring;
import org.ehcache.event.EventOrdering;
import org.ehcache.event.EventType;
import org.greatfree.cache.CacheConfig;
import org.greatfree.cache.CacheListener;
import org.greatfree.cache.CompoundKeyCreatable;
import org.greatfree.cache.StoreElement;
import org.greatfree.cache.db.MapKeysDB;
import org.greatfree.cache.distributed.EvictedNotification;
import org.greatfree.cache.distributed.MapPostfetchNotification;
import org.greatfree.cache.distributed.MapReplicateNotification;
import org.greatfree.cache.local.CacheEventable;
import org.greatfree.cache.local.CacheMapFactorable;
import org.greatfree.concurrency.Sync;
import org.greatfree.concurrency.ThreadPool;
import org.greatfree.concurrency.reactive.NotificationObjectDispatcher;
import org.greatfree.concurrency.reactive.NotificationObjectQueue;
import org.greatfree.concurrency.reactive.NotificationObjectThreadCreatable;
import org.greatfree.exceptions.DistributedMapFetchException;
import org.greatfree.exceptions.TerminalServerOverflowedException;
import org.greatfree.util.Builder;
import org.greatfree.util.FileManager;
import org.greatfree.util.UtilConfig;

/*
 * The cache is created and tested in the Clouds project. 08/27/2018, Bing Li
 */

// Created: 08/24/2018, Bing Li
public class DistributedMapStore<Value extends StoreElement, Factory extends CacheMapFactorable<String, Value>, CompoundKeyCreator extends CompoundKeyCreatable<String>, ReplicateThread extends NotificationObjectQueue<MapReplicateNotification<Value>>, ReplicateThreadCreator extends NotificationObjectThreadCreatable<MapReplicateNotification<Value>, ReplicateThread>, PostfetchNotification extends MapPostfetchNotification, PostfetchThread extends NotificationObjectQueue<PostfetchNotification>, PostfetchThreadCreator extends NotificationObjectThreadCreatable<PostfetchNotification, PostfetchThread>, EvictThread extends NotificationObjectQueue<EvictedNotification<Value>>, EvictThreadCreator extends NotificationObjectThreadCreatable<EvictedNotification<Value>, EvictThread>> implements CacheEventable<String, Value>
{
	private final String storeKey;
	private Cache<String, Value> cache;
	private PersistentCacheManager manager;
//	private final int cacheSize;
	private final long cacheSize;
	
	private MapKeysDB keysDB;
	private Map<String, Set<String>> keys;
	
	private CacheListener<String, Value, DistributedMapStore<Value, Factory, CompoundKeyCreator, ReplicateThread, ReplicateThreadCreator, PostfetchNotification, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator>> listener;
	
	private CompoundKeyCreator keyCreator;
//	private ReentrantReadWriteLock lock;

	private NotificationObjectDispatcher<MapReplicateNotification<Value>, ReplicateThread, ReplicateThreadCreator> replicateDispatcher;
	private NotificationObjectDispatcher<PostfetchNotification, PostfetchThread, PostfetchThreadCreator> postfetchDispatcher;
	private NotificationObjectDispatcher<EvictedNotification<Value>, EvictThread, EvictThreadCreator> evictedDispatcher;

	private ThreadPool threadPool;
	private Map<String, Sync> syncs;
	private final long fetchTimeout;
	
	private Map<String, Map<String, Value>> postfetchedData;

	public DistributedMapStore(DistributedMapStoreBuilder<Value, Factory, CompoundKeyCreator, ReplicateThread, ReplicateThreadCreator, PostfetchNotification, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> builder)
	{
		this.storeKey = builder.getStoreKey();
		this.manager = builder.getFactory().createManagerInstance(FileManager.appendSlash(builder.getRootPath()), builder.getStoreKey(), builder.getTotalStoreSize(), builder.getOffheapSizeInMB(), builder.getDiskSizeInMB());
		this.cache = builder.getFactory().createCache(this.manager, this.storeKey);
		this.listener = new CacheListener<String, Value, DistributedMapStore<Value, Factory, CompoundKeyCreator, ReplicateThread, ReplicateThreadCreator, PostfetchNotification, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator>>(this);

		this.cacheSize = builder.getCacheSize();
		this.keyCreator = builder.getKeyCreator();
		
		this.keysDB = new MapKeysDB(CacheConfig.getStoreKeysRootPath(CacheConfig.getCachePath(builder.getRootPath(), builder.getStoreKey()), CacheConfig.MAP_KEYS));
		this.keys = this.keysDB.getAllKeys();

//		this.lock = new ReentrantReadWriteLock();

		this.cache.getRuntimeConfiguration().registerCacheEventListener(this.listener, EventOrdering.ORDERED, EventFiring.ASYNCHRONOUS, EnumSet.of(EventType.CREATED, EventType.EVICTED, EventType.EXPIRED, EventType.REMOVED, EventType.UPDATED));
		
		this.replicateDispatcher = new NotificationObjectDispatcher.NotificationObjectDispatcherBuilder<MapReplicateNotification<Value>, ReplicateThread, ReplicateThreadCreator>()
				.poolSize(builder.getPoolSize())
				.threadCreator(builder.getReplicateThreadCreator())
				.notificationQueueSize(builder.getMaxTaskSize())
				.dispatcherWaitTime(builder.getDispatcherWaitTime())
//				.waitRound(builder.getWaitRound())
				.idleCheckDelay(builder.getIdleCheckDelay())
				.idleCheckPeriod(builder.getIdleCheckPeriod())
				.scheduler(builder.getScheduler())
				.build();

		this.postfetchDispatcher = new NotificationObjectDispatcher.NotificationObjectDispatcherBuilder<PostfetchNotification, PostfetchThread, PostfetchThreadCreator>()
				.poolSize(builder.getPoolSize())
				.threadCreator(builder.getFetchThreadCreator())
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
		
		this.postfetchedData = new ConcurrentHashMap<String, Map<String, Value>>();
		
		this.keyCreator = builder.getKeyCreator();
		
		this.threadPool = builder.getThreadPool();
		this.syncs = new ConcurrentHashMap<String, Sync>();
		this.fetchTimeout = builder.getfetchTimeout();
	}

	public static class DistributedMapStoreBuilder<Value extends StoreElement, Factory extends CacheMapFactorable<String, Value>, CompoundKeyCreator extends CompoundKeyCreatable<String>, ReplicateThread extends NotificationObjectQueue<MapReplicateNotification<Value>>, ReplicateThreadCreator extends NotificationObjectThreadCreatable<MapReplicateNotification<Value>, ReplicateThread>, PostfetchNotification extends MapPostfetchNotification, PostfetchThread extends NotificationObjectQueue<PostfetchNotification>, PostfetchThreadCreator extends NotificationObjectThreadCreatable<PostfetchNotification, PostfetchThread>, EvictThread extends NotificationObjectQueue<EvictedNotification<Value>>, EvictThreadCreator extends NotificationObjectThreadCreatable<EvictedNotification<Value>, EvictThread>> implements Builder<DistributedMapStore<Value, Factory, CompoundKeyCreator, ReplicateThread, ReplicateThreadCreator, PostfetchNotification, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator>>
	{
		private String storeKey;
		private Factory factory;
		private String rootPath;
//		private int cacheSize;
		private long cacheSize;
//		private int totalStoreSize;
		private long totalStoreSize;
		private int offheapSizeInMB;
		private int diskSizeInMB;
		private CompoundKeyCreator keyCreator;

		private int poolSize;
		private ReplicateThreadCreator replicateCreator;
		private PostfetchThreadCreator postfetchCreator;
		private EvictThreadCreator evictCreator;
		private int maxTaskSize;
		private long dispatcherWaitTime;
//		private int waitRound;
		private long idleCheckDelay;
		private long idleCheckPeriod;
		private ScheduledThreadPoolExecutor scheduler;
		private ThreadPool threadPool;
		private long fetchTimeout;
		
		public DistributedMapStoreBuilder()
		{
		}

		public DistributedMapStoreBuilder<Value, Factory, CompoundKeyCreator, ReplicateThread, ReplicateThreadCreator, PostfetchNotification, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> storeKey(String storeKey)
		{
			this.storeKey = storeKey;
			return this;
		}

		public DistributedMapStoreBuilder<Value, Factory, CompoundKeyCreator, ReplicateThread, ReplicateThreadCreator, PostfetchNotification, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> factory(Factory factory)
		{
			this.factory = factory;
			return this;
		}

		public DistributedMapStoreBuilder<Value, Factory, CompoundKeyCreator, ReplicateThread, ReplicateThreadCreator, PostfetchNotification, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> rootPath(String rootPath)
		{
			this.rootPath = rootPath;
			return this;
		}

		public DistributedMapStoreBuilder<Value, Factory, CompoundKeyCreator, ReplicateThread, ReplicateThreadCreator, PostfetchNotification, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> cacheSize(long cacheSize)
		{
			this.cacheSize = cacheSize;
			return this;
		}

		public DistributedMapStoreBuilder<Value, Factory, CompoundKeyCreator, ReplicateThread, ReplicateThreadCreator, PostfetchNotification, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> totalStoreSize(long totalStoreSize)
		{
			this.totalStoreSize = totalStoreSize;
			return this;
		}

		public DistributedMapStoreBuilder<Value, Factory, CompoundKeyCreator, ReplicateThread, ReplicateThreadCreator, PostfetchNotification, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> offheapSizeInMB(int offheapSizeInMB)
		{
			this.offheapSizeInMB = offheapSizeInMB;
			return this;
		}

		public DistributedMapStoreBuilder<Value, Factory, CompoundKeyCreator, ReplicateThread, ReplicateThreadCreator, PostfetchNotification, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> diskSizeInMB(int diskSizeInMB)
		{
			this.diskSizeInMB = diskSizeInMB;
			return this;
		}

		public DistributedMapStoreBuilder<Value, Factory, CompoundKeyCreator, ReplicateThread, ReplicateThreadCreator, PostfetchNotification, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> keyCreator(CompoundKeyCreator keyCreator)
		{
			this.keyCreator = keyCreator;
			return this;
		}

		public DistributedMapStoreBuilder<Value, Factory, CompoundKeyCreator, ReplicateThread, ReplicateThreadCreator, PostfetchNotification, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> poolSize(int poolSize)
		{
			this.poolSize = poolSize;
			return this;
		}

		public DistributedMapStoreBuilder<Value, Factory, CompoundKeyCreator, ReplicateThread, ReplicateThreadCreator, PostfetchNotification, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> replicateCreator(ReplicateThreadCreator replicateCreator)
		{
			this.replicateCreator = replicateCreator;
			return this;
		}

		public DistributedMapStoreBuilder<Value, Factory, CompoundKeyCreator, ReplicateThread, ReplicateThreadCreator, PostfetchNotification, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> postfetchCreator(PostfetchThreadCreator postfetchCreator)
		{
			this.postfetchCreator = postfetchCreator;
			return this;
		}

		public DistributedMapStoreBuilder<Value, Factory, CompoundKeyCreator, ReplicateThread, ReplicateThreadCreator, PostfetchNotification, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> evictCreator(EvictThreadCreator evictCreator)
		{
			this.evictCreator = evictCreator;
			return this;
		}

		public DistributedMapStoreBuilder<Value, Factory, CompoundKeyCreator, ReplicateThread, ReplicateThreadCreator, PostfetchNotification, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> maxTaskSize(int maxTaskSize)
		{
			this.maxTaskSize = maxTaskSize;
			return this;
		}

		public DistributedMapStoreBuilder<Value, Factory, CompoundKeyCreator, ReplicateThread, ReplicateThreadCreator, PostfetchNotification, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> dispatcherWaitTime(long dispatcherWaitTime)
		{
			this.dispatcherWaitTime = dispatcherWaitTime;
			return this;
		}

		/*
		public DistributedMapStoreBuilder<Value, Factory, CompoundKeyCreator, ReplicateThread, ReplicateThreadCreator, PostfetchNotification, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> waitRound(int waitRound)
		{
			this.waitRound = waitRound;
			return this;
		}
		*/

		public DistributedMapStoreBuilder<Value, Factory, CompoundKeyCreator, ReplicateThread, ReplicateThreadCreator, PostfetchNotification, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> idleCheckDelay(long idleCheckDelay)
		{
			this.idleCheckDelay = idleCheckDelay;
			return this;
		}

		public DistributedMapStoreBuilder<Value, Factory, CompoundKeyCreator, ReplicateThread, ReplicateThreadCreator, PostfetchNotification, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> idleCheckPeriod(long idleCheckPeriod)
		{
			this.idleCheckPeriod = idleCheckPeriod;
			return this;
		}

		public DistributedMapStoreBuilder<Value, Factory, CompoundKeyCreator, ReplicateThread, ReplicateThreadCreator, PostfetchNotification, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> scheduler(ScheduledThreadPoolExecutor scheduler)
		{
			this.scheduler = scheduler;
			return this;
		}

		public DistributedMapStoreBuilder<Value, Factory, CompoundKeyCreator, ReplicateThread, ReplicateThreadCreator, PostfetchNotification, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> threadPool(ThreadPool threadPool)
		{
			this.threadPool = threadPool;
			return this;
		}

		public DistributedMapStoreBuilder<Value, Factory, CompoundKeyCreator, ReplicateThread, ReplicateThreadCreator, PostfetchNotification, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> postfetchTimeout(long fetchTimeout)
		{
			this.fetchTimeout = fetchTimeout;
			return this;
		}

		@Override
		public DistributedMapStore<Value, Factory, CompoundKeyCreator, ReplicateThread, ReplicateThreadCreator, PostfetchNotification, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> build()
		{
			return new DistributedMapStore<Value, Factory, CompoundKeyCreator, ReplicateThread, ReplicateThreadCreator, PostfetchNotification, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator>(this);
		}

		public String getStoreKey()
		{
			return this.storeKey;
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
		
		public long getTotalStoreSize()
		{
			return this.totalStoreSize;
		}
		
		public int getOffheapSizeInMB()
		{
			return this.offheapSizeInMB;
		}
		
		public int getDiskSizeInMB()
		{
			return this.diskSizeInMB;
		}

		public CompoundKeyCreator getKeyCreator()
		{
			return this.keyCreator;
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
		
		public int getMaxTaskSize()
		{
			return this.maxTaskSize;
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
		this.manager.close();
//		this.lock.writeLock().lock();
		
		this.keysDB.removeAll();
		this.keysDB.putAll(this.keys);
		this.keysDB.close();
		
//		this.lock.writeLock().unlock();
		
		this.postfetchedData.clear();
		this.postfetchedData = null;

		this.replicateDispatcher.dispose();
		this.postfetchDispatcher.dispose();
		this.evictedDispatcher.dispose();
		for (Sync entry : this.syncs.values())
		{
			entry.signal();
		}
		this.syncs.clear();
		this.syncs = null;
	}
	
	public long getCacheSize()
	{
		return this.cacheSize;
	}

	private void signal(String key)
	{
		this.syncs.get(key).signal();
		this.syncs.remove(key);
	}

	public void savePosfetchedData(String key, String cacheKey, Map<String, Value> values, boolean isBlocking)
	{
		if (isBlocking)
		{
			if (values != null)
			{
				// The notify/wait mechanism results in the concurrently accessing on the values. It might lead to ConcurrentModificationException. So it is necessary to send out the values first and then save them into the cache. Or make a copy. 08/04/2018, Bing Li
//				this.postfetchedData.put(key, values);
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
			this.putAllLocally(cacheKey, values);
		}
	}
	
	public void savePostfetchedData(String key, String cacheKey, Value v, boolean isBlocking)
	{
		if (v != null)
		{
			this.putLocally(v);
		}
		if (isBlocking)
		{
			this.signal(key);
		}
	}

	public Set<String> getCacheKeys()
	{
		/*
		this.lock.readLock().lock();
		try
		{
			return this.keys.keySet();
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		*/
		return this.keys.keySet();
	}
	
	public boolean isEmpty()
	{
		/*
		this.lock.readLock().lock();
		try
		{
			return this.keys.size() <= 0;
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		*/
		return this.keys.size() <= 0;
	}
	
	public Value get(String mapKey, String key)
	{
		return this.cache.get(this.keyCreator.createCompoundKey(mapKey, key));
	}
	
	public Map<String, Value> get(String mapKey, Set<String> keys)
	{
		/*
		 * The below lines always return the values whose size is equal to the number of keys even though one of the values is null. 08/25/2018, Bing Li
		 * 
		Set<String> rscKeys = Sets.newHashSet();
		for (String entry : keys)
		{
			rscKeys.add(this.keyCreator.createCompoundKey(mapKey, entry));
		}
		return this.cache.getAll(rscKeys);
		*/
		Map<String, Value> values = new HashMap<String, Value>();
		Value v;
		for (String key : keys)
		{
			v = this.get(mapKey, this.keyCreator.createCompoundKey(mapKey, key));
			if (v != null)
			{
				values.put(key, v);
			}
		}
		return values;
	}
	
	public Value get(PostfetchNotification notification)
	{
		String key = this.keyCreator.createCompoundKey(notification.getMapKey(), notification.getResourceKey());
		Value v = this.cache.get(key);
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
			this.syncs.get(notification.getKey()).holdOn(this.fetchTimeout);
			return this.get(notification.getMapKey(), notification.getResourceKey());
		}
		return v;
	}
	
	public Map<String, Value> getValues(PostfetchNotification notification) throws DistributedMapFetchException
	{
		if (notification.getResourceKeys().size() <= this.cacheSize)
		{
//			System.out.println("0) DistributedMapStore-getValues(): resourceKeys size = " + notification.getResourceKeys().size());
			Map<String, Value> vs = this.get(notification.getMapKey(), notification.getResourceKeys());
			if (vs != null)
			{
				if (vs.size() >= notification.getResourceKeys().size())
				{
//					System.out.println("1) DistributedMapStore-getValues(): values size = " + v.size());
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
			if (!this.postfetchDispatcher.isReady())
			{
				this.threadPool.execute(this.postfetchDispatcher);
			}
			this.postfetchDispatcher.enqueue(notification);
			if (notification.isBlocking())
			{
				this.syncs.put(notification.getKey(), new Sync(false));
				this.syncs.get(notification.getKey()).holdOn(this.fetchTimeout);
				Map<String, Value> values = this.postfetchedData.get(notification.getKey());
				this.postfetchedData.remove(notification.getKey());
//				System.out.println("2) DistributedMapStore-getValues(): values size = " + values.size());
				return values;
			}
			return vs;
		}
		else
		{
			throw new DistributedMapFetchException(this.cacheSize, notification.getResourceKeys().size());
		}
	}
	
	public Set<String> getKeys(String mapKey)
	{
		/* The below lines are not correct. The unlocking is not executed if the keys are returned inside the if condition. 08/26/2018, Bing Li
		this.lock.readLock().lock();
		if (this.keys.containsKey(mapKey))
		{
			return this.keys.get(mapKey);
		}
		System.out.println("DistributedMapStore-getKeys(): mapKey = " + mapKey);
		this.lock.readLock().unlock();
		return UtilConfig.NO_KEYS;
		*/
		/*
		this.lock.readLock().lock();
		try
		{
			if (this.keys.containsKey(mapKey))
			{
				return this.keys.get(mapKey);
			}
			return UtilConfig.NO_KEYS;
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		*/
		if (this.keys.containsKey(mapKey))
		{
			return this.keys.get(mapKey);
		}
		return UtilConfig.NO_KEYS;
	}
	
	public Set<String> getKeys(PostfetchNotification notification)
	{
		Set<String> keys = this.getKeys(notification.getMapKey());
//		Set<String> keys = Sets.newHashSet();
//		keys.add("");
//		Set<String> dataKeys = Sets.newHashSet();
		Set<String> dataKeys = new HashSet<String>();
		if (keys != UtilConfig.NO_KEYS)
		{
			if (keys.size() > 0)
			{
				Value v;
				for (String key : keys)
				{
					v = this.cache.get(key);
					if (v != null)
					{
						dataKeys.add(v.getKey());
					}
				}
				return dataKeys;
				/*
				for (int i = 0; i < 10; i++)
				{
					dataKeys.add("Key" + i);
				}
				if (dataKeys.size() > 0)
				{
					return dataKeys;
				}
				*/
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
			this.syncs.get(notification.getKey()).holdOn(this.fetchTimeout);
			Map<String, Value> values = this.postfetchedData.get(notification.getKey());
			this.postfetchedData.remove(notification.getKey());
			if (values != null)
			{
				for (Map.Entry<String, Value> entry : values.entrySet())
				{
					dataKeys.add(entry.getKey());
				}
			}
			return dataKeys;
		}
		return dataKeys;
	}

	public boolean containsKey(String mapKey, String key)
	{
		return this.cache.containsKey(this.keyCreator.createCompoundKey(mapKey, key));
	}
	
	public boolean containsKey(PostfetchNotification notification)
	{
		boolean isExisted = this.containsKey(notification.getMapKey(), notification.getResourceKey());
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
			return this.containsKey(notification.getMapKey(), notification.getResourceKey());
		}
		return isExisted;
	}
	
	public boolean isStoreEmpty(String mapKey)
	{
		/*
		this.lock.readLock().lock();
		try
		{
			return this.keys.get(mapKey).size() <= 0;
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		*/
		return this.keys.get(mapKey).size() <= 0;
	}

	public boolean isMapExisted(String mapKey)
	{
		/*
		this.lock.readLock().lock();
		try
		{
			return this.keys.containsKey(mapKey);
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		*/
		return this.keys.containsKey(mapKey);
	}

	public boolean isMapExisted(PostfetchNotification notification)
	{
		boolean isExisted = this.isMapExisted(notification.getMapKey());
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
			return this.isMapExisted(notification.getMapKey());
		}
		return isExisted;
	}

	public boolean isMapEmpty(PostfetchNotification notification)
	{
		boolean isEmpty = this.isStoreEmpty(notification.getMapKey());
		if (!isEmpty)
		{
			return false;
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
			return this.isStoreEmpty(notification.getMapKey());
		}
		return isEmpty;
	}

	/*
	 * If the key of evicted data is removed, the size is not the entire cache size. It is fine with the practical application. It is not reasonable to get the entire size in practice. So no postfetching is designed for that. 08/24/2018, Bing Li
	 */
	public long getSize(String mapKey)
	{
		/*
		this.lock.readLock().lock();
		try
		{
			if (this.keys.containsKey(mapKey))
			{
				return this.keys.get(mapKey).size();
			}
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		*/
		if (this.keys.containsKey(mapKey))
		{
			return this.keys.get(mapKey).size();
		}
		return 0;
	}

	/*
	 * If the key of evicted data is removed, the size is not the entire cache size. It is fine with the practical application. It is not reasonable to get the entire size in practice. So no postfetching is designed for that. 08/24/2018, Bing Li
	 */
	public long getEmptySize(String mapKey)
	{
		return this.cacheSize - this.getSize(mapKey);
	}
	
	/*
	 * If the key of evicted data is removed, the size is not the entire cache size. It is fine with the practical application. It is not reasonable to get the entire size in practice. So no postfetching is designed for that. 08/24/2018, Bing Li
	 */
	public long getLeftSize(String mapKey, int currentAccessedEndIndex)
	{
		return this.getSize(mapKey) - currentAccessedEndIndex - 1;
	}
	
	/*
	 * If the key of evicted data is removed, the size is not the entire cache size. It is fine with the practical application. It is not reasonable to get the entire size in practice. So no postfetching is designed for that. 08/24/2018, Bing Li
	 */
	public boolean isCacheFull(String mapKey)
	{
		return this.cacheSize <= this.getSize(mapKey);
	}

	public void put(MapReplicateNotification<Value> notification)
	{
		if (!this.replicateDispatcher.isReady())
		{
			this.threadPool.execute(this.replicateDispatcher);
		}
		this.replicateDispatcher.enqueue(notification);
		this.putLocally(notification.getValue());
	}
	
	public void putAll(MapReplicateNotification<Value> notification)
	{
		if (!this.replicateDispatcher.isReady())
		{
			this.threadPool.execute(this.replicateDispatcher);
		}
		this.replicateDispatcher.enqueue(notification);
		this.putAllLocally(notification.getCacheKey(), notification.getValues());
	}

	public void putLocally(Value v)
	{
		String key = this.keyCreator.createCompoundKey(v.getCacheKey(), v.getKey());

//		this.lock.readLock().lock();
		if (!this.keys.containsKey(v.getCacheKey()))
		{
//			Set<String> keys = Sets.newHashSet();
			Set<String> keys = new HashSet<String>();
//			this.lock.readLock().unlock();
//			this.lock.writeLock().lock();
			this.keys.put(v.getCacheKey(), keys);
			Set<String> kSet = this.keys.get(v.getCacheKey());
			kSet.add(key);
			this.keys.put(v.getCacheKey(), kSet);
//			this.lock.readLock().lock();
//			this.lock.writeLock().unlock();
		}
		else
		{
			Set<String> kSet = this.keys.get(v.getCacheKey());
			kSet.add(key);
//			this.lock.readLock().unlock();
//			this.lock.writeLock().lock();
			this.keys.put(v.getCacheKey(), kSet);
//			this.lock.readLock().lock();
//			this.lock.writeLock().unlock();
		}
//		this.lock.readLock().unlock();
		this.cache.put(key, v);
	}

	public void putAllLocally(String mapKey, Map<String, Value> values)
	{
		String key;
		for (Map.Entry<String, Value> entry : values.entrySet())
		{
			key = this.keyCreator.createCompoundKey(mapKey, entry.getKey());
			this.cache.put(key, entry.getValue());
		}
//		this.lock.readLock().lock();
		if (!this.keys.containsKey(mapKey))
		{
//			Set<String> keys = Sets.newHashSet();
			Set<String> keys = new HashSet<String>();
//			this.lock.readLock().unlock();
//			this.lock.writeLock().lock();
			this.keys.put(mapKey, keys);
			for (Map.Entry<String, Value> entry : values.entrySet())
			{
				key = this.keyCreator.createCompoundKey(mapKey, entry.getKey());
				Set<String> kSet = this.keys.get(mapKey);
				kSet.add(key);
				this.keys.put(mapKey, kSet);
			}
//			this.lock.readLock().lock();
//			this.lock.writeLock().unlock();
		}
		else
		{
			for (Map.Entry<String, Value> entry : values.entrySet())
			{
				key = this.keyCreator.createCompoundKey(mapKey, entry.getKey());
				Set<String> kSet = this.keys.get(mapKey);
				kSet.add(key);
//				this.lock.readLock().unlock();
//				this.lock.writeLock().lock();
				this.keys.put(mapKey, kSet);
//				this.lock.readLock().lock();
//				this.lock.writeLock().unlock();
			}
		}
//		this.lock.readLock().unlock();
	}
	
	private void removeKey(String mapKey, String k)
	{
		String key = this.keyCreator.createCompoundKey(mapKey, k);
//		this.lock.readLock().lock();
		if (this.keys.containsKey(mapKey))
		{
//			this.lock.readLock().unlock();
//			this.lock.writeLock().lock();
			this.keys.get(mapKey).remove(key);
//			this.lock.readLock().lock();
//			this.lock.writeLock().unlock();
		}
//		this.lock.readLock().unlock();
	}

	@Override
	public void evict(String k, Value v) throws TerminalServerOverflowedException
	{
		if (!this.evictedDispatcher.isReady())
		{
			this.threadPool.execute(this.evictedDispatcher);
		}
//		this.evictedDispatcher.enqueue(new EvictedNotification<Value>(k, v));
		// The Ehcache assigns the key through the evict(String k, Value v) interface. But it usually does not make sense to the application level. So I attempt the assign the cache key to replace it since the cache key sometimes is used on the application level, e.g., the CircleTimingPageCache. In the case, the cache key is not generated by the information from the Value, i.e., TimingPage. Thus, it is required to keep the cache key in the notification. Usually, the store needs to be updated for that. 08/09/2019, Bing Li
		this.evictedDispatcher.enqueue(new EvictedNotification<Value>(v.getCacheKey(), v));
		this.removeKey(v.getCacheKey(), v.getKey());
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
