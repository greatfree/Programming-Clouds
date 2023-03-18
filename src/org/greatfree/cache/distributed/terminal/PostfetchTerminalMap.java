package org.greatfree.cache.distributed.terminal;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

import org.greatfree.cache.CacheConfig;
import org.greatfree.cache.db.StringKeyDB;
import org.greatfree.cache.db.StringKeyDBPool;
import org.greatfree.cache.distributed.MapPostfetchNotification;
import org.greatfree.cache.distributed.terminal.db.PostfetchDB;
import org.greatfree.cache.local.CacheMapFactorable;
import org.greatfree.cache.local.RootCache;
import org.greatfree.concurrency.Sync;
import org.greatfree.concurrency.ThreadPool;
import org.greatfree.concurrency.reactive.NotificationObjectDispatcher;
import org.greatfree.concurrency.reactive.NotificationObjectQueue;
import org.greatfree.concurrency.reactive.NotificationObjectThreadCreatable;
import org.greatfree.exceptions.TerminalServerOverflowedException;
import org.greatfree.util.Builder;

/*
 * The version is updated in its locking only. 08/22/2018, Bing Li
 * 
 * The cache has NOT been tested yet. It might not be so useful. I need to check it later. 08/20/2018, Bing Li
 */

// Created: 06/15/2018, Bing Li
public class PostfetchTerminalMap<Value extends Serializable, Factory extends CacheMapFactorable<String, Value>, Notification extends MapPostfetchNotification, PostfetchThread extends NotificationObjectQueue<Notification>, PostfetchThreadCreator extends NotificationObjectThreadCreatable<Notification, PostfetchThread>, DB extends PostfetchDB<Value>> extends RootCache<String, Value, Factory, StringKeyDB>
{
	private NotificationObjectDispatcher<Notification, PostfetchThread, PostfetchThreadCreator> postfetchDispatcher;

	private ThreadPool threadPool;
	private Map<String, Sync> syncs;
	private final long postfetchTimeout;

	private DB db;
	private AtomicInteger currentEvictedCount;
	private final int alertEvictedCount;
//	private ReentrantReadWriteLock lock;

	public PostfetchTerminalMap(PostfetchTerminalMapBuilder<Value, Factory, Notification, PostfetchThread, PostfetchThreadCreator, DB> builder)
	{
		super(builder.getFactory(), builder.getRootPath(), builder.getCacheKey(), builder.getCacheSize(), builder.getOffheapSizeInMB(), builder.getDiskSizeInMB(), StringKeyDBPool.SHARED().getDB(CacheConfig.getMapCacheKeyDBPath(builder.getRootPath(), builder.getCacheKey())), true);
		
		this.postfetchDispatcher = new NotificationObjectDispatcher.NotificationObjectDispatcherBuilder<Notification, PostfetchThread, PostfetchThreadCreator>()
				.poolSize(builder.getPoolSize())
				.threadCreator(builder.getPostfetchThreadCreator())
				.notificationQueueSize(builder.getNotificationQueueSize())
				.dispatcherWaitTime(builder.getDispatcherWaitTime())
//				.waitRound(builder.getWaitRound())
				.idleCheckDelay(builder.getIdleCheckDelay())
				.idleCheckPeriod(builder.getIdleCheckPeriod())
				.scheduler(builder.getScheduler())
				.build();

		this.threadPool = builder.getThreadPool();
		this.syncs = new ConcurrentHashMap<String, Sync>();
		this.postfetchTimeout = builder.getPostfetchTimeout();

		this.db = builder.getDB();
		this.currentEvictedCount = new AtomicInteger(this.db.getSize());
		this.alertEvictedCount = builder.getAlertEvictedCount();
//		this.lock = new ReentrantReadWriteLock();
	}

	public static class PostfetchTerminalMapBuilder<Value extends Serializable, Factory extends CacheMapFactorable<String, Value>, Notification extends MapPostfetchNotification, PostfetchThread extends NotificationObjectQueue<Notification>, PostfetchThreadCreator extends NotificationObjectThreadCreatable<Notification, PostfetchThread>, DB extends PostfetchDB<Value>> implements Builder<PostfetchTerminalMap<Value, Factory, Notification, PostfetchThread, PostfetchThreadCreator, DB>>
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
		private int notificationQueueSize;
		private long dispatcherWaitTime;
//		private int waitRound;
		private long idleCheckDelay;
		private long idleCheckPeriod;
		private ScheduledThreadPoolExecutor scheduler;
		private ThreadPool threadPool;
		private long postfetchTimeout;

		private DB db;
		private int alertEvictedCount;

		public PostfetchTerminalMapBuilder()
		{
		}

		public PostfetchTerminalMapBuilder<Value, Factory,  Notification, PostfetchThread, PostfetchThreadCreator, DB> cacheKey(String cacheKey)
		{
			this.cacheKey = cacheKey;
			return this;
		}
		
		public PostfetchTerminalMapBuilder<Value, Factory,  Notification, PostfetchThread, PostfetchThreadCreator, DB> factory(Factory factory)
		{
			this.factory = factory;
			return this;
		}
		
		public PostfetchTerminalMapBuilder<Value, Factory,  Notification, PostfetchThread, PostfetchThreadCreator, DB> rootPath(String rootPath)
		{
			this.rootPath = rootPath;
			return this;
		}
		
		public PostfetchTerminalMapBuilder<Value, Factory,  Notification, PostfetchThread, PostfetchThreadCreator, DB> cacheSize(long cacheSize)
		{
			this.cacheSize = cacheSize;
			return this;
		}
		
		public PostfetchTerminalMapBuilder<Value, Factory,  Notification, PostfetchThread, PostfetchThreadCreator, DB> offheapSizeInMB(int offheapSizeInMB)
		{
			this.offheapSizeInMB = offheapSizeInMB;
			return this;
		}
		
		public PostfetchTerminalMapBuilder<Value, Factory,  Notification, PostfetchThread, PostfetchThreadCreator, DB> diskSizeInMB(int diskSizeInMB)
		{
			this.diskSizeInMB = diskSizeInMB;
			return this;
		}
		
		public PostfetchTerminalMapBuilder<Value, Factory,  Notification, PostfetchThread, PostfetchThreadCreator, DB> poolSize(int poolSize)
		{
			this.poolSize = poolSize;
			return this;
		}
		
		public PostfetchTerminalMapBuilder<Value, Factory,  Notification, PostfetchThread, PostfetchThreadCreator, DB> postfetchCreator(PostfetchThreadCreator postfetchCreator)
		{
			this.postfetchCreator = postfetchCreator;
			return this;
		}

		public PostfetchTerminalMapBuilder<Value, Factory,  Notification, PostfetchThread, PostfetchThreadCreator, DB> maxTaskSize(int maxTaskSize)
		{
			this.notificationQueueSize = maxTaskSize;
			return this;
		}
		
		public PostfetchTerminalMapBuilder<Value, Factory,  Notification, PostfetchThread, PostfetchThreadCreator, DB> dispatcherWaitTime(long dispatcherWaitTime)
		{
			this.dispatcherWaitTime = dispatcherWaitTime;
			return this;
		}

		/*
		public PostfetchTerminalMapBuilder<Value, Factory,  Notification, PostfetchThread, PostfetchThreadCreator, DB> waitRound(int waitRound)
		{
			this.waitRound = waitRound;
			return this;
		}
		*/
		
		public PostfetchTerminalMapBuilder<Value, Factory,  Notification, PostfetchThread, PostfetchThreadCreator, DB> idleCheckDelay(long idleCheckDelay)
		{
			this.idleCheckDelay = idleCheckDelay;
			return this;
		}
		
		public PostfetchTerminalMapBuilder<Value, Factory,  Notification, PostfetchThread, PostfetchThreadCreator, DB> idleCheckPeriod(long idleCheckPeriod)
		{
			this.idleCheckPeriod = idleCheckPeriod;
			return this;
		}
		
		public PostfetchTerminalMapBuilder<Value, Factory,  Notification, PostfetchThread, PostfetchThreadCreator, DB> scheduler(ScheduledThreadPoolExecutor scheduler)
		{
			this.scheduler = scheduler;
			return this;
		}
		
		public PostfetchTerminalMapBuilder<Value, Factory,  Notification, PostfetchThread, PostfetchThreadCreator, DB> threadPool(ThreadPool threadPool)
		{
			this.threadPool = threadPool;
			return this;
		}

		public PostfetchTerminalMapBuilder<Value, Factory,  Notification, PostfetchThread, PostfetchThreadCreator, DB> postfetchTimeout(long postfetchTimeout)
		{
			this.postfetchTimeout = postfetchTimeout;
			return this;
		}

		public PostfetchTerminalMapBuilder<Value, Factory,  Notification, PostfetchThread, PostfetchThreadCreator, DB> db(DB db)
		{
			this.db = db;
			return this;
		}

		public PostfetchTerminalMapBuilder<Value, Factory,  Notification, PostfetchThread, PostfetchThreadCreator, DB> alertEvictedCount(int alertEvictedCount)
		{
			this.alertEvictedCount = alertEvictedCount;
			return this;
		}

		@Override
		public PostfetchTerminalMap<Value, Factory, Notification, PostfetchThread, PostfetchThreadCreator, DB> build()
		{
			return new PostfetchTerminalMap<Value, Factory, Notification, PostfetchThread, PostfetchThreadCreator, DB>(this);
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
		
		public int getNotificationQueueSize()
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
		
		public DB getDB()
		{
			return this.db;
		}
		
		public int getAlertEvictedCount()
		{
			return this.alertEvictedCount;
		}
	}

	@Override
	public void shutdown() throws InterruptedException
	{
		super.closeAtBase();
//		this.lock.writeLock().lock();
		this.db.close();
//		this.lock.writeLock().unlock();
		this.postfetchDispatcher.dispose();
		for (Sync entry : this.syncs.values())
		{
			entry.signal();
		}
		this.syncs.clear();
		this.syncs = null;
	}
	
	public void signal(String key)
	{
		this.syncs.get(key).signal();
		this.syncs.remove(key);
	}
	
	public boolean isDown()
	{
		/*
		this.lock.readLock().lock();
		try
		{
			return super.isDownAtBase();
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		*/
		return super.isDownAtBase();
	}

	public void put(String k, Value v)
	{
//		this.lock.writeLock().lock();
		super.putAtBase(k, v);
//		this.lock.writeLock().unlock();
	}
	
	public void putAll(Map<String, Value> values)
	{
//		this.lock.writeLock().lock();
		super.putAllAtBase(values);
//		this.lock.writeLock().unlock();
	}

	public Value get(String key)
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

		/*
		this.lock.readLock().lock();
		try
		{
			v = this.db.get(notification.getResourceKey());
			if (v != null)
			{
				return v;
			}
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		*/
		v = this.db.get(notification.getResourceKey());
		if (v != null)
		{
			return v;
		}

		if (!this.postfetchDispatcher.isReady())
		{
			this.threadPool.execute(this.postfetchDispatcher);
		}
		this.postfetchDispatcher.enqueue(notification);
		this.syncs.put(notification.getKey(), new Sync(false));
		this.syncs.get(notification.getKey()).holdOn(this.postfetchTimeout);
		/*
		this.lock.readLock().lock();
		try
		{
			return super.getAtBase(notification.getResourceKey());
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		*/
		return super.getAtBase(notification.getResourceKey());
	}

	public Map<String, Value> getValues(Notification notification)
	{
//		this.lock.readLock().lock();
		Map<String, Value> v = super.getAtBase(notification.getResourceKeys());
//		this.lock.readLock().unlock();
		if (v != null)
		{
			if (v.size() >= notification.getResourceKeys().size())
			{
				return v;
			}
		}
		
		Set<String> obtainedRscKeys = v.keySet();
//		Set<String> unavailableRscKeys = Sets.difference(notification.getResourceKeys(), obtainedRscKeys);
		Set<String> unavailableRscKeys = new HashSet<String>(notification.getResourceKeys());
		unavailableRscKeys.removeAll(obtainedRscKeys);
		/*
		this.lock.readLock().lock();
		try
		{
			Map<String, Value> restRscs = this.db.getMap(unavailableRscKeys);
			if (v != null)
			{
				this.lock.readLock().unlock();
				this.lock.writeLock().lock();
				super.putAllAtBase(v);
				this.lock.readLock().lock();
				this.lock.writeLock().unlock();
				v.putAll(restRscs);
				if (v.size() >= notification.getResourceKeys().size())
				{
					return v;
				}
			}
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		*/

		Map<String, Value> restRscs = this.db.getMap(unavailableRscKeys);
		if (v != null)
		{
			super.putAllAtBase(v);
			v.putAll(restRscs);
			if (v.size() >= notification.getResourceKeys().size())
			{
				return v;
			}
		}

		if (!this.postfetchDispatcher.isReady())
		{
			this.threadPool.execute(this.postfetchDispatcher);
		}
		this.postfetchDispatcher.enqueue(notification);
		this.syncs.put(notification.getKey(), new Sync(false));
		this.syncs.get(notification.getKey()).holdOn(this.postfetchTimeout);
		/*
		this.lock.readLock().lock();
		try
		{
			return super.getAtBase(notification.getResourceKeys());
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		*/
		return super.getAtBase(notification.getResourceKeys());
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

//		this.lock.readLock().lock();
		Value v = this.db.get(notification.getKey());
//		this.lock.readLock().unlock();
		if (v != null)
		{
			// Now I decide to put the data form the DB to the cache. 06/15/2018, Bing Li
			// To simplify the code, the data loaded from the DB will NOT be put into the cache. The benefit to do that is not so obvious. In addition, the DB itself has the cache capability. My work just focus on designing a wrapper. 05/31/2018, Bing Li
//			this.put(rscKey, v);
			return true;
		}
		
		if (!this.postfetchDispatcher.isReady())
		{
			this.threadPool.execute(this.postfetchDispatcher);
		}
		this.postfetchDispatcher.enqueue(notification);
		this.syncs.put(notification.getKey(), new Sync(false));
		this.syncs.get(notification.getKey()).holdOn(this.postfetchTimeout);
		/*
		this.lock.readLock().lock();
		try
		{
			return super.containsKeyAtBase(notification.getResourceKey());
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		*/
		return super.containsKeyAtBase(notification.getResourceKey());
	}

	public boolean isEmpty()
	{
		/*
		this.lock.readLock().lock();
		try
		{
			return super.isEmptyAtBase();
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		*/
		return super.isEmptyAtBase();
	}
	
	public Map<String, Value> getValues()
	{
		/*
		this.lock.readLock().lock();
		try
		{
			return super.getValuesAtBase();
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		*/
		return super.getValuesAtBase();
	}
	
	// To be reasonable as a storage, the remove methods can be retained. But it suggests to invoke the methods as few as possible. 06/15/2018, Bing Li
	// It is not necessary to remove data for a terminal of a large-scale storage system. 06/15/2018, Bing Li
	public void remove(String key)
	{
//		this.lock.writeLock().lock();
		super.removeAtBase(key);
		this.db.remove(key);
//		this.lock.writeLock().unlock();
	}

	@Override
	public void evict(String k, Value v) throws TerminalServerOverflowedException
	{
//		this.lock.writeLock().lock();
//		super.removeKey(k);
		this.db.save(v);
//		this.lock.writeLock().unlock();
		this.currentEvictedCount.getAndIncrement();
		if (this.currentEvictedCount.get() >= this.alertEvictedCount)
		{
			// When the evicted data count exceeds the threshold, it needs to add a new terminal server to the system. I will do that later. 05/12/2018, Bing Li
			throw new TerminalServerOverflowedException(super.getCacheKeyAtBase());
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
