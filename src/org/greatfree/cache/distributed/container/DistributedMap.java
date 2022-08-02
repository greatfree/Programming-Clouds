package org.greatfree.cache.distributed.container;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import org.greatfree.cache.CacheConfig;
import org.greatfree.cache.StoreElement;
import org.greatfree.cache.db.StringKeyDB;
import org.greatfree.cache.db.StringKeyDBPool;
import org.greatfree.cache.distributed.EvictedNotification;
import org.greatfree.cache.distributed.MapPostfetchNotification;
import org.greatfree.cache.distributed.MapReplicateNotification;
import org.greatfree.cache.local.CacheMapFactorable;
import org.greatfree.cache.local.RootCache;
import org.greatfree.concurrency.Sync;
import org.greatfree.concurrency.ThreadPool;
import org.greatfree.concurrency.reactive.NotificationTask;
import org.greatfree.concurrency.reactive.NotificationTaskDispatcher;
import org.greatfree.concurrency.reactive.NotificationTaskQueue;
import org.greatfree.concurrency.reactive.NotificationTaskThreadCreator;
import org.greatfree.exceptions.DistributedMapFetchException;
import org.greatfree.exceptions.TerminalServerOverflowedException;
import org.greatfree.util.Builder;

/*
 * The cache is not sharable since it is designed to support the DistributedMapContainer. In the cache, NotificationTaskDispatcher is designed to leave the task interface such that developers can design their own replications, postfetching, prefetching and evicting algorithms. 01/22/2019, Bing Li
 */

// Created: 01/22/2019, Bing Li
class DistributedMap<Value extends StoreElement, Factory extends CacheMapFactorable<String, Value>, ReplicateThread extends NotificationTaskQueue<MapReplicateNotification<Value>>, ReplicateThreadCreator extends NotificationTaskThreadCreator<MapReplicateNotification<Value>, ReplicateThread>, PostfetchNotification extends MapPostfetchNotification, PostfetchThread extends NotificationTaskQueue<PostfetchNotification>, PostfetchThreadCreator extends NotificationTaskThreadCreator<PostfetchNotification, PostfetchThread>, EvictThread extends NotificationTaskQueue<EvictedNotification<Value>>, EvictThreadCreator extends NotificationTaskThreadCreator<EvictedNotification<Value>, EvictThread>> extends RootCache<String, Value, Factory, StringKeyDB>
{
	private NotificationTaskDispatcher<MapReplicateNotification<Value>, ReplicateThread, ReplicateThreadCreator> replicateDispatcher;
	private NotificationTaskDispatcher<PostfetchNotification, PostfetchThread, PostfetchThreadCreator> postfetchDispatcher;
	private NotificationTaskDispatcher<EvictedNotification<Value>, EvictThread, EvictThreadCreator> evictedDispatcher;

	private ThreadPool threadPool;
	private Map<String, Sync> syncs;
	private final long fetchTimeout;
	
	private Map<String, Map<String, Value>> postfetchedData;

	public DistributedMap(DistributedMapBuilder<Value, Factory, ReplicateThread, ReplicateThreadCreator, PostfetchNotification, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> builder)
	{
		// Since an explicit thread is employed, the synchronous eventing is fine. 05/13/2018, Bing Li
		super(builder.getFactory(), builder.getRootPath(), builder.getCacheKey(), builder.getCacheSize(), builder.getOffheapSizeInMB(), builder.getDiskSizeInMB(), StringKeyDBPool.SHARED().getDB(CacheConfig.getMapCacheKeyDBPath(builder.getRootPath(), builder.getCacheKey())), false);

		this.replicateDispatcher = new NotificationTaskDispatcher.NotificationTaskDispatcherBuilder<MapReplicateNotification<Value>, ReplicateThread, ReplicateThreadCreator>()
				.poolSize(builder.getPoolSize())
				.threadCreator(builder.getReplicateThreadCreator())
				.notificationQueueSize(builder.geNotificationQueueSize())
				.dispatcherWaitTime(builder.getDispatcherWaitTime())
//				.waitRound(builder.getWaitRound())
				.idleCheckDelay(builder.getIdleCheckDelay())
				.idleCheckPeriod(builder.getIdleCheckPeriod())
				.scheduler(builder.getScheduler())
				.task(builder.getReplicateTask())
				.build();

		this.postfetchDispatcher = new NotificationTaskDispatcher.NotificationTaskDispatcherBuilder<PostfetchNotification, PostfetchThread, PostfetchThreadCreator>()
				.poolSize(builder.getPoolSize())
				.threadCreator(builder.getFetchThreadCreator())
				.notificationQueueSize(builder.geNotificationQueueSize())
				.dispatcherWaitTime(builder.getDispatcherWaitTime())
//				.waitRound(builder.getWaitRound())
				.idleCheckDelay(builder.getIdleCheckDelay())
				.idleCheckPeriod(builder.getIdleCheckPeriod())
				.scheduler(builder.getScheduler())
				.task(builder.getPostfetchTask())
				.build();

		this.evictedDispatcher = new NotificationTaskDispatcher.NotificationTaskDispatcherBuilder<EvictedNotification<Value>, EvictThread, EvictThreadCreator>()
				.poolSize(builder.getPoolSize())
				.threadCreator(builder.getEvictThreadCreator())
				.notificationQueueSize(builder.geNotificationQueueSize())
				.dispatcherWaitTime(builder.getDispatcherWaitTime())
//				.waitRound(builder.getWaitRound())
				.idleCheckDelay(builder.getIdleCheckDelay())
				.idleCheckPeriod(builder.getIdleCheckPeriod())
				.scheduler(builder.getScheduler())
				.task(builder.getEvictTask())
				.build();
		
		this.postfetchedData = new ConcurrentHashMap<String, Map<String, Value>>();
		
		this.threadPool = builder.getThreadPool();
		this.syncs = new ConcurrentHashMap<String, Sync>();
		this.fetchTimeout = builder.getfetchTimeout();
	}

	public static class DistributedMapBuilder<Value extends StoreElement, Factory extends CacheMapFactorable<String, Value>, ReplicateThread extends NotificationTaskQueue<MapReplicateNotification<Value>>, ReplicateThreadCreator extends NotificationTaskThreadCreator<MapReplicateNotification<Value>, ReplicateThread>, PostfetchNotification extends MapPostfetchNotification, PostfetchThread extends NotificationTaskQueue<PostfetchNotification>, PostfetchThreadCreator extends NotificationTaskThreadCreator<PostfetchNotification, PostfetchThread>, EvictThread extends NotificationTaskQueue<EvictedNotification<Value>>, EvictThreadCreator extends NotificationTaskThreadCreator<EvictedNotification<Value>, EvictThread>> implements Builder<DistributedMap<Value, Factory, ReplicateThread, ReplicateThreadCreator, PostfetchNotification, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator>>
	{
		private String cacheKey;
		private Factory factory;
		private String rootPath;
		private int cacheSize;
		private int offheapSizeInMB;
		private int diskSizeInMB;

		private int poolSize;
		private ReplicateThreadCreator replicateCreator;
		private PostfetchThreadCreator postfetchCreator;
		private EvictThreadCreator evictCreator;
		private int notificationQueueSize;
		private long dispatcherWaitTime;
//		private int waitRound;
		private long idleCheckDelay;
		private long idleCheckPeriod;
		private ScheduledThreadPoolExecutor scheduler;
		private ThreadPool threadPool;
		private long fetchTimeout;
		private NotificationTask<MapReplicateNotification<Value>> replicateTask;
		private NotificationTask<PostfetchNotification> postfetchTask;
		private NotificationTask<EvictedNotification<Value>> evictTask;
		
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

		/*
		public DistributedMapBuilder<Value, Factory, ReplicateThread, ReplicateThreadCreator, PostfetchNotification, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> waitRound(int waitRound)
		{
			this.waitRound = waitRound;
			return this;
		}
		*/

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

		public DistributedMapBuilder<Value, Factory, ReplicateThread, ReplicateThreadCreator, PostfetchNotification, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> replicateTask(NotificationTask<MapReplicateNotification<Value>> replicateTask)
		{
			this.replicateTask = replicateTask;
			return this;
		}

		public DistributedMapBuilder<Value, Factory, ReplicateThread, ReplicateThreadCreator, PostfetchNotification, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> postfetchTask(NotificationTask<PostfetchNotification> postfetchTask)
		{
			this.postfetchTask = postfetchTask;
			return this;
		}

		public DistributedMapBuilder<Value, Factory, ReplicateThread, ReplicateThreadCreator, PostfetchNotification, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> evictTask(NotificationTask<EvictedNotification<Value>> evictTask)
		{
			this.evictTask = evictTask;
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
		
		public int getCacheSize()
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
		
		public int geNotificationQueueSize()
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

		public ThreadPool getThreadPool()
		{
			return this.threadPool;
		}

		public long getfetchTimeout()
		{
			return this.fetchTimeout;
		}
		
		public NotificationTask<MapReplicateNotification<Value>> getReplicateTask()
		{
			return this.replicateTask;
		}
		
		public NotificationTask<PostfetchNotification> getPostfetchTask()
		{
			return this.postfetchTask;
		}
		
		public NotificationTask<EvictedNotification<Value>> getEvictTask()
		{
			return this.evictTask;
		}
	}


	public boolean isDownAtBase()
	{
		return super.isDownAtBase();
	}
	
	public long getSize()
	{
		return super.getMemCacheSizeAtBase();
	}
	
	private void signal(String key)
	{
		this.syncs.get(key).signal();
		this.syncs.remove(key);
	}
	
	public void savePosfetchedData(String key, Map<String, Value> values)
	{
		if (values != null)
		{
			// The notify/wait mechanism results in the concurrently accessing on the values. It might lead to ConcurrentModificationException. So it is necessary to send out the values first and then save them into the cache. Or make a copy. 08/04/2018, Bing Li
			this.postfetchedData.put(key, new HashMap<String, Value>(values));
		}
		this.signal(key);
		if (values != null)
		{
			super.putAllAtBase(values);
		}
	}
	
	public void savePostfetchedData(String key, String dataKey, Value v)
	{
		if (v != null)
		{
			super.putAtBase(dataKey, v);
		}
		this.signal(key);
	}
	
	public void put(MapReplicateNotification<Value> notification)
	{
		super.putAtBase(notification.getKey(), notification.getValue());
		if (!this.replicateDispatcher.isReady())
		{
			this.threadPool.execute(this.replicateDispatcher);
		}
		this.replicateDispatcher.enqueue(notification);
	}
	
	public void putAll(MapReplicateNotification<Value> notification)
	{
		super.putAllAtBase(notification.getValues());
		if (!this.replicateDispatcher.isReady())
		{
			this.threadPool.execute(this.replicateDispatcher);
		}
		this.replicateDispatcher.enqueue(notification);
	}

	public void putLocally(String k, Value v)
	{
		super.putAtBase(k, v);
	}
	
	public void putAllLocally(Map<String, Value> values)
	{
		super.putAllAtBase(values);
	}
	
	/*
	 * Through testing, the locking I added has something WRONG. Sometimes, when the amount of data to be written is large, the read-lock cannot be entered. 07/09/2018, Bing Li
	 */
	public Value get(PostfetchNotification notification)
	{
		Value v = super.getAtBase(notification.getResourceKey());
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
		this.syncs.get(notification.getKey()).holdOn(this.fetchTimeout);
		return super.getAtBase(notification.getResourceKey());
	}
	
	public Map<String, Value> getValues(PostfetchNotification notification) throws DistributedMapFetchException
	{
		if (notification.getResourceKeys().size() <= super.getCacheSizeAtBase())
		{
			System.out.println("DistributedMap-getValues(): resource keys size = " + notification.getResourceKeys().size());
			Map<String, Value> v = super.getAtBase(notification.getResourceKeys());
			if (v != null)
			{
				System.out.println("DistributedMap-getValues(): values size = " + v.size());
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
			this.syncs.get(notification.getKey()).holdOn(this.fetchTimeout);
			Map<String, Value> values = this.postfetchedData.get(notification.getKey());
			this.postfetchedData.remove(notification.getKey());
			return values;
		}
		else
		{
			throw new DistributedMapFetchException(super.getCacheSizeAtBase(), notification.getResourceKeys().size());
		}
	}
	
	public Value getAtBase(String key)
	{
		return super.getAtBase(key);
	}

	public boolean containsKeyAtBase(String key)
	{
		return super.containsKeyAtBase(key);
	}
	
	public boolean containsKey(PostfetchNotification notification)
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
		this.syncs.put(notification.getKey(), new Sync(false));
		this.syncs.get(notification.getKey()).holdOn(this.fetchTimeout);
		return super.containsKeyAtBase(notification.getResourceKey());
	}
	
	public boolean isEmptyAtBase()
	{
		return super.isEmptyAtBase();
	}

	public Set<String> getKeysAtBase()
	{
		return super.getKeysAtBase();
	}

	@Override
	public void evict(String k, Value v) throws TerminalServerOverflowedException
	{
		// Commenting the below line does not affect the functions of the cache. But I think it might occupy much memory when the size becomes large. 08/01/2018, Bing Li
		// Since postfetching is performed, it is not necessary to remove the key? I need to ensure that. 07/31/2018, Bing Li
		super.removeKeyAtBase(k);
		if (!this.evictedDispatcher.isReady())
		{
			this.threadPool.execute(this.evictedDispatcher);
		}
		this.evictedDispatcher.enqueue(new EvictedNotification<Value>(k, v));
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

	@Override
	public void shutdown() throws InterruptedException, IOException
	{
		super.closeAtBase();
		
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
}
