package org.greatfree.cache.distributed.terminal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.ehcache.Cache;
import org.ehcache.PersistentCacheManager;
import org.ehcache.event.EventFiring;
import org.ehcache.event.EventOrdering;
import org.ehcache.event.EventType;
import org.greatfree.cache.CacheConfig;
import org.greatfree.cache.CacheListener;
import org.greatfree.cache.CompoundKeyCreatable;
import org.greatfree.cache.db.LinearIndexDB;
import org.greatfree.cache.db.LinearIndexEntity;
import org.greatfree.cache.distributed.KickOffNotification;
import org.greatfree.cache.distributed.terminal.db.PostfetchDB;
import org.greatfree.cache.local.CacheEventable;
import org.greatfree.cache.local.CacheMapFactorable;
import org.greatfree.concurrency.ThreadPool;
import org.greatfree.concurrency.reactive.NotificationObjectDispatcher;
import org.greatfree.concurrency.reactive.NotificationObjectQueue;
import org.greatfree.concurrency.reactive.NotificationObjectThreadCreatable;
import org.greatfree.exceptions.TerminalServerOverflowedException;
import org.greatfree.util.Builder;
import org.greatfree.util.FileManager;
import org.greatfree.util.UtilConfig;

/*
 * The version is updated in its locking only. 08/22/2018, Bing Li
 * 
 * The cache has NOT been tested yet. It might not be so useful. I need to check it later. 08/20/2018, Bing Li
 */

// Created: 06/16/2018, Bing Li
public class PrefetchTerminalStackStore<Value extends Serializable, Factory extends CacheMapFactorable<String, Value>, CompoundKeyCreator extends CompoundKeyCreatable<Integer>, DB extends PostfetchDB<Value>, PrefetchNotification extends KickOffNotification, PrefetchThread extends NotificationObjectQueue<PrefetchNotification>, PrefetchThreadCreator extends NotificationObjectThreadCreatable<PrefetchNotification, PrefetchThread>> implements CacheEventable<String, Value>
{
	private final String storeKey;
	private Cache<String, Value> cache;
	private PersistentCacheManager manager;
	private long cacheSize;

	private LinearIndexDB keys;
	
	private CacheListener<String, Value, PrefetchTerminalStackStore<Value, Factory, CompoundKeyCreator, DB, PrefetchNotification, PrefetchThread, PrefetchThreadCreator>> listener;

	private CompoundKeyCreator keyCreator;

	private AtomicBoolean isDown;
//	private ReentrantReadWriteLock lock;

	private DB db;

	private AtomicInteger currentEvictedCount;
	private final int alertEvictedCount;

	private NotificationObjectDispatcher<PrefetchNotification, PrefetchThread, PrefetchThreadCreator> prefetchDispatcher;
	private final int prefetchThresholdSize;
	private ThreadPool threadPool;
	private final int prefetchCount;
	
	public PrefetchTerminalStackStore(PrefetchTerminalStackStoreBuilder<Value, Factory, CompoundKeyCreator, DB, PrefetchNotification, PrefetchThread, PrefetchThreadCreator> builder)
	{
		this.storeKey = builder.getStoreKey();
		this.manager = builder.getFactory().createManagerInstance(builder.getRootPath(), builder.getStoreKey(), builder.getStoreSize(), builder.getOffheapSizeInMB(), builder.getDiskSizeInMB());
		this.cache = builder.getFactory().createCache(this.manager, this.storeKey);
		this.listener = new CacheListener<String, Value, PrefetchTerminalStackStore<Value, Factory, CompoundKeyCreator, DB, PrefetchNotification, PrefetchThread, PrefetchThreadCreator>>(this);
		this.cache.getRuntimeConfiguration().registerCacheEventListener(this.listener, EventOrdering.ORDERED, EventFiring.ASYNCHRONOUS, EnumSet.of(EventType.CREATED, EventType.EVICTED, EventType.EXPIRED, EventType.REMOVED, EventType.UPDATED));

		this.cacheSize = builder.getCacheSize();
		this.keyCreator = builder.getKeyCreator();
		this.keys = new LinearIndexDB(CacheConfig.getStoreKeysRootPath(CacheConfig.getCachePath(builder.getRootPath(), builder.getStoreKey()), CacheConfig.STACK_KEYS));
		
		this.db = builder.getDB();
		this.isDown = new AtomicBoolean(false);
//		this.lock = new ReentrantReadWriteLock();
		this.currentEvictedCount = new AtomicInteger(this.db.getSize());
		this.alertEvictedCount = builder.getAlertEvictedCount();
		
		this.prefetchDispatcher = new NotificationObjectDispatcher.NotificationObjectDispatcherBuilder<PrefetchNotification, PrefetchThread, PrefetchThreadCreator>()
				.poolSize(builder.getPoolSize())
				.threadCreator(builder.getPrefetchThreadCreator())
				.notificationQueueSize(builder.getMaxTaskSize())
				.dispatcherWaitTime(builder.getDispatcherWaitTime())
				.waitRound(builder.getWaitRound())
				.idleCheckDelay(builder.getIdleCheckDelay())
				.idleCheckPeriod(builder.getIdleCheckPeriod())
				.scheduler(builder.getScheduler())
				.build();

		this.prefetchThresholdSize = builder.getPrefetchThresholdSize();
		this.threadPool = builder.getThreadPool();
		this.prefetchCount = builder.getPrefetchCount();
	}
	
	public static class PrefetchTerminalStackStoreBuilder<Value extends Serializable, Factory extends CacheMapFactorable<String, Value>, CompoundKeyCreator extends CompoundKeyCreatable<Integer>, DB extends PostfetchDB<Value>, PrefetchNotification extends KickOffNotification, PrefetchThread extends NotificationObjectQueue<PrefetchNotification>, PrefetchThreadCreator extends NotificationObjectThreadCreatable<PrefetchNotification, PrefetchThread>> implements Builder<PrefetchTerminalStackStore<Value, Factory, CompoundKeyCreator, DB, PrefetchNotification, PrefetchThread, PrefetchThreadCreator>>
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

		private int notificationQueueSize;
		private long dispatcherWaitTime;
		private int waitRound;
		private long idleCheckDelay;
		private long idleCheckPeriod;
		private ScheduledThreadPoolExecutor scheduler;
		private int prefetchThresholdSize;
		private ThreadPool threadPool;
		private int prefetchCount;

		private DB db;
		private int alertEvictedCount;

		public PrefetchTerminalStackStoreBuilder()
		{
		}

		public PrefetchTerminalStackStoreBuilder<Value, Factory, CompoundKeyCreator, DB, PrefetchNotification, PrefetchThread, PrefetchThreadCreator> storeKey(String storeKey)
		{
			this.storeKey = storeKey;
			return this;
		}

		public PrefetchTerminalStackStoreBuilder<Value, Factory, CompoundKeyCreator, DB, PrefetchNotification, PrefetchThread, PrefetchThreadCreator> factory(Factory factory)
		{
			this.factory = factory;
			return this;
		}

		public PrefetchTerminalStackStoreBuilder<Value, Factory, CompoundKeyCreator, DB, PrefetchNotification, PrefetchThread, PrefetchThreadCreator> rootPath(String rootPath)
		{
			this.rootPath = FileManager.appendSlash(rootPath);
			return this;
		}

		public PrefetchTerminalStackStoreBuilder<Value, Factory, CompoundKeyCreator, DB, PrefetchNotification, PrefetchThread, PrefetchThreadCreator> storeSize(long storeSize)
		{
			this.storeSize = storeSize;
			return this;
		}

		public PrefetchTerminalStackStoreBuilder<Value, Factory, CompoundKeyCreator, DB, PrefetchNotification, PrefetchThread, PrefetchThreadCreator> cacheSize(long cacheSize)
		{
			this.cacheSize = cacheSize;
			return this;
		}

		public PrefetchTerminalStackStoreBuilder<Value, Factory, CompoundKeyCreator, DB, PrefetchNotification, PrefetchThread, PrefetchThreadCreator> offheapSizeInMB(int offheapSizeInMB)
		{
			this.offheapSizeInMB = offheapSizeInMB;
			return this;
		}

		public PrefetchTerminalStackStoreBuilder<Value, Factory, CompoundKeyCreator, DB, PrefetchNotification, PrefetchThread, PrefetchThreadCreator> diskSizeInMB(int diskSizeInMB)
		{
			this.diskSizeInMB = diskSizeInMB;
			return this;
		}

		public PrefetchTerminalStackStoreBuilder<Value, Factory, CompoundKeyCreator, DB, PrefetchNotification, PrefetchThread, PrefetchThreadCreator> keyCreator(CompoundKeyCreator keyCreator)
		{
			this.keyCreator = keyCreator;
			return this;
		}

		public PrefetchTerminalStackStoreBuilder<Value, Factory, CompoundKeyCreator, DB, PrefetchNotification, PrefetchThread, PrefetchThreadCreator> poolSize(int poolSize)
		{
			this.poolSize = poolSize;
			return this;
		}

		public PrefetchTerminalStackStoreBuilder<Value, Factory, CompoundKeyCreator, DB, PrefetchNotification, PrefetchThread, PrefetchThreadCreator> prefetchThreadCreator(PrefetchThreadCreator prefetchCreator)
		{
			this.prefetchCreator = prefetchCreator;
			return this;
		}

		public PrefetchTerminalStackStoreBuilder<Value, Factory, CompoundKeyCreator, DB, PrefetchNotification, PrefetchThread, PrefetchThreadCreator> notificationQueueSize(int notificationQueueSize)
		{
			this.notificationQueueSize = notificationQueueSize;
			return this;
		}

		public PrefetchTerminalStackStoreBuilder<Value, Factory, CompoundKeyCreator, DB, PrefetchNotification, PrefetchThread, PrefetchThreadCreator> dispatcherWaitTime(long dispatcherWaitTime)
		{
			this.dispatcherWaitTime = dispatcherWaitTime;
			return this;
		}

		public PrefetchTerminalStackStoreBuilder<Value, Factory, CompoundKeyCreator, DB, PrefetchNotification, PrefetchThread, PrefetchThreadCreator> waitRound(int waitRound)
		{
			this.waitRound = waitRound;
			return this;
		}

		public PrefetchTerminalStackStoreBuilder<Value, Factory, CompoundKeyCreator, DB, PrefetchNotification, PrefetchThread, PrefetchThreadCreator> idleCheckDelay(long idleCheckDelay)
		{
			this.idleCheckDelay = idleCheckDelay;
			return this;
		}

		public PrefetchTerminalStackStoreBuilder<Value, Factory, CompoundKeyCreator, DB, PrefetchNotification, PrefetchThread, PrefetchThreadCreator> idleCheckPeriod(long idleCheckPeriod)
		{
			this.idleCheckPeriod = idleCheckPeriod;
			return this;
		}

		public PrefetchTerminalStackStoreBuilder<Value, Factory, CompoundKeyCreator, DB, PrefetchNotification, PrefetchThread, PrefetchThreadCreator> scheduler(ScheduledThreadPoolExecutor scheduler)
		{
			this.scheduler = scheduler;
			return this;
		}

		public PrefetchTerminalStackStoreBuilder<Value, Factory, CompoundKeyCreator, DB, PrefetchNotification, PrefetchThread, PrefetchThreadCreator> prefetchThresholdSize(int prefetchThresholdSize)
		{
			this.prefetchThresholdSize = prefetchThresholdSize;
			return this;
		}

		public PrefetchTerminalStackStoreBuilder<Value, Factory, CompoundKeyCreator, DB, PrefetchNotification, PrefetchThread, PrefetchThreadCreator> threadPool(ThreadPool threadPool)
		{
			this.threadPool = threadPool;
			return this;
		}

		public PrefetchTerminalStackStoreBuilder<Value, Factory, CompoundKeyCreator, DB, PrefetchNotification, PrefetchThread, PrefetchThreadCreator> prefetchCount(int prefetchCount)
		{
			this.prefetchCount = prefetchCount;
			return this;
		}

		public PrefetchTerminalStackStoreBuilder<Value, Factory, CompoundKeyCreator, DB, PrefetchNotification, PrefetchThread, PrefetchThreadCreator> db(DB db)
		{
			this.db = db;
			return this;
		}

		public PrefetchTerminalStackStoreBuilder<Value, Factory, CompoundKeyCreator, DB, PrefetchNotification, PrefetchThread, PrefetchThreadCreator> alertEvictedCount(int alertEvictedCount)
		{
			this.alertEvictedCount = alertEvictedCount;
			return this;
		}

		@Override
		public PrefetchTerminalStackStore<Value, Factory, CompoundKeyCreator, DB, PrefetchNotification, PrefetchThread, PrefetchThreadCreator> build() 
		{
			return new PrefetchTerminalStackStore<Value, Factory, CompoundKeyCreator, DB, PrefetchNotification, PrefetchThread, PrefetchThreadCreator>(this);
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

		public int getMaxTaskSize()
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

		public ThreadPool getThreadPool()
		{
			return this.threadPool;
		}
		
		public DB getDB()
		{
			return this.db;
		}
		
		public int getAlertEvictedCount()
		{
			return this.alertEvictedCount;
		}
	}
	
	public void dispose() throws InterruptedException
	{
//		this.lock.writeLock().lock();
		this.db.close();
		this.keys.close();
		this.manager.close();
		this.isDown.set(true);
//		this.lock.writeLock().unlock();
//		this.lock = null;
		this.prefetchDispatcher.dispose();
	}

	public boolean isDown()
	{
		/*
		this.lock.readLock().lock();
		try
		{
			return this.isDown;
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		*/
		return this.isDown.get();
	}

	public int getSize(String stackKey)
	{
		/*
		this.lock.readLock().lock();
		try
		{
			if (this.keys.containsKey(stackKey))
			{
				LinearIndexEntity indexes = this.keys.get(stackKey);
				if (indexes != null)
				{
					return indexes.getTailIndex() - indexes.getHeadIndex();
				}
			}
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		*/
		if (this.keys.containsKey(stackKey))
		{
			LinearIndexEntity indexes = this.keys.get(stackKey);
			if (indexes != null)
			{
				return indexes.getTailIndex() - indexes.getHeadIndex();
			}
		}
		return UtilConfig.ZERO;
	}

	public boolean isStackInStore(String stackKey)
	{
		/*
		this.lock.readLock().lock();
		try
		{
			return this.keys.containsKey(stackKey);
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		*/
		return this.keys.containsKey(stackKey);
	}

	public boolean isEmpty()
	{
		/*
		this.lock.readLock().lock();
		try
		{
			return this.keys.isEmpty();
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		*/
		return this.keys.isEmpty();
	}

	public boolean isEmpty(String stackKey)
	{
		/*
		this.lock.readLock().lock();
		try
		{
			if (this.keys.containsKey(stackKey))
			{
				LinearIndexEntity indexes = this.keys.get(stackKey);
				if (indexes != null)
				{
					return indexes.getTailIndex() - indexes.getHeadIndex() <= 0;
				}
			}
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		*/
		if (this.keys.containsKey(stackKey))
		{
			LinearIndexEntity indexes = this.keys.get(stackKey);
			if (indexes != null)
			{
				return indexes.getTailIndex() - indexes.getHeadIndex() <= 0;
			}
		}
		return false;
	}

	public boolean isFull(String stackKey)
	{
		/*
		this.lock.readLock().lock();
		try
		{
			if (this.keys.containsKey(stackKey))
			{
				LinearIndexEntity indexes = this.keys.get(stackKey);
				if (indexes != null)
				{
					return indexes.getTailIndex() - indexes.getHeadIndex() >= this.cacheSize;
				}
			}
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		*/
		if (this.keys.containsKey(stackKey))
		{
			LinearIndexEntity indexes = this.keys.get(stackKey);
			if (indexes != null)
			{
				return indexes.getTailIndex() - indexes.getHeadIndex() >= this.cacheSize;
			}
		}
		return false;
	}
	
	public int getPrefetchCount()
	{
		return this.prefetchCount;
	}

	public void push(String stackKey, Value v)
	{
//		this.lock.writeLock().lock();
		if (!this.keys.containsKey(stackKey))
		{
			this.keys.put(stackKey, 0, 0);
		}
		LinearIndexEntity indexes = this.keys.get(stackKey);
		String key = this.keyCreator.createCompoundKey(stackKey, indexes.getTailIndex() + 1);
		indexes.setTailIndex(indexes.getTailIndex() + 1);
		this.keys.put(indexes);
		this.cache.put(key, v);
//		this.lock.writeLock().unlock();
	}

	public void push(String stackKey, List<Value> rscs)
	{
		for (Value rsc : rscs)
		{
			this.push(stackKey, rsc);
		}
	}

	public List<Value> pop(String stackKey, int n)
	{
		List<Value> rscs = new ArrayList<Value>();
		Value rsc;
		for (int i = 0; i < n; i++)
		{
			rsc = this.pop(stackKey);
			if (rsc != null)
			{
				rscs.add(rsc);
			}
		}
		return rscs;
	}

	public Value pop(String stackKey)
	{
		if (this.keys.containsKey(stackKey))
		{
			LinearIndexEntity indexes = this.keys.get(stackKey);
			if (indexes.getHeadIndex() <= indexes.getTailIndex())
			{
				String key = this.keyCreator.createCompoundKey(stackKey, indexes.getTailIndex());
				Value rsc = this.cache.get(key);
				if (rsc != null)
				{
					this.cache.remove(key);
				}
				else
				{
					rsc = this.db.get(key);
				}
				indexes.setTailIndex(indexes.getTailIndex() - 1);
				if (indexes.getHeadIndex() > indexes.getTailIndex())
				{
					indexes.setHeadIndex(0);
					indexes.setTailIndex(-1);
				}
				this.keys.put(indexes);
				return rsc;
			}
		}
		return null;
	}

	public Value pop(PrefetchNotification notification)
	{
		if (this.getSize(notification.getCacheKey()) <= this.prefetchThresholdSize)
		{
			if (!this.prefetchDispatcher.isReady())
			{
				this.threadPool.execute(this.prefetchDispatcher);
			}
			this.prefetchDispatcher.enqueue(notification);
		}
		return this.pop(notification.getCacheKey());
	}
	
	public List<Value> popAll(PrefetchNotification notification)
	{
		if (this.getSize(notification.getCacheKey()) <= this.prefetchThresholdSize)
		{
			if (!this.prefetchDispatcher.isReady())
			{
				this.threadPool.execute(this.prefetchDispatcher);
			}
			this.prefetchDispatcher.enqueue(notification);
		}
		return this.pop(notification.getCacheKey(), notification.getKickOffCount());
	}

	public List<Value> peek(String stackKey, int n)
	{
		List<Value> rscs = new ArrayList<Value>();
		Value rsc;
		String key;
//		this.lock.readLock().lock();
		if (this.keys.containsKey(stackKey))
		{
			LinearIndexEntity indexes = this.keys.get(stackKey);
			int headIndex = indexes.getHeadIndex();
			int tailIndex = indexes.getTailIndex();
			for (int i = 0; i < n; i++)
			{
				if (headIndex < tailIndex)
				{
					key = this.keyCreator.createCompoundKey(stackKey, tailIndex);
					rsc = this.cache.get(key);
					if (rsc != null)
					{
						rscs.add(rsc);
					}
					else
					{
						rsc = this.db.get(key);
						if (rsc != null)
						{
							rscs.add(rsc);
						}
					}
					tailIndex--;
				}
			}
		}
//		this.lock.readLock().unlock();
		return rscs;
	}

	public Value peek(String stackKey)
	{
//		this.lock.readLock().lock();
		if (this.keys.containsKey(stackKey))
		{
			LinearIndexEntity indexes = this.keys.get(stackKey);
			if (indexes.getHeadIndex() <= indexes.getTailIndex())
			{
				String key = this.keyCreator.createCompoundKey(stackKey, indexes.getTailIndex());
				Value rsc = this.cache.get(key);
				if (rsc != null)
				{
					return rsc;
				}
				else
				{
					return this.db.get(key);
				}
			}
		}
//		this.lock.readLock().unlock();
		return null;
	}

	public void removeStacks(Set<String> stackKeys)
	{
		for (String stackKey : stackKeys)
		{
			this.removeStack(stackKey);
		}
	}

	public void removeStack(String stackKey)
	{
//		this.lock.writeLock().lock();
		if (this.keys.containsKey(stackKey))
		{
			LinearIndexEntity indexes = this.keys.get(stackKey);
			String key;
			for (int i = indexes.getHeadIndex(); i < indexes.getTailIndex(); i++)
			{
				key = this.keyCreator.createCompoundKey(stackKey, i);
				if (this.cache.containsKey(key))
				{
					this.cache.remove(key);
				}
				else
				{
					this.db.remove(key);
				}
			}
			indexes.setHeadIndex(0);
			indexes.setTailIndex(-1);
			this.keys.remove(stackKey);
		}
//		this.lock.writeLock().unlock();
	}

	@Override
	public void evict(String k, Value v) throws TerminalServerOverflowedException
	{
//		this.lock.writeLock().lock();
		this.db.save(v);
//		this.lock.writeLock().unlock();
		this.currentEvictedCount.getAndIncrement();
		if (this.currentEvictedCount.get() >= this.alertEvictedCount)
		{
			// When the evicted data count exceeds the threshold, it needs to add a new terminal server to the system. I will do that later. 05/12/2018, Bing Li
			throw new TerminalServerOverflowedException(this.storeKey);
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
