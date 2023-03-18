package org.greatfree.cache.distributed;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import org.greatfree.cache.CacheConfig;
import org.greatfree.cache.db.ListKeyDB;
import org.greatfree.cache.db.SortedListDB;
import org.greatfree.cache.db.StringKeyDB;
import org.greatfree.cache.db.StringKeyDBPool;
import org.greatfree.cache.local.CacheMapFactorable;
import org.greatfree.cache.local.RootCache;
import org.greatfree.concurrency.Sync;
import org.greatfree.concurrency.ThreadPool;
import org.greatfree.concurrency.reactive.NotificationObjectDispatcher;
import org.greatfree.concurrency.reactive.NotificationObjectQueue;
import org.greatfree.concurrency.reactive.NotificationObjectThreadCreatable;
import org.greatfree.exceptions.TerminalServerOverflowedException;
import org.greatfree.util.Builder;
import org.greatfree.util.CollectionSorter;
import org.greatfree.util.FileManager;
import org.greatfree.util.Pointing;
import org.greatfree.util.UtilConfig;

/*
 * The list cache that sorts only a predefined count of data is implemented. When data is saved in the list, each data should have a unique key. It must employ the constructor Pointing(long points) of Pointing. 02/15/2019, Bing Li
 */

/*
 * The version is tested in the Clouds project. 08/22/2018, Bing Li
 */

/*
 * One other issue is that the cache is a sorted one such that it is better to have the prefetching thread. At this time, the cache is used only by MemoryLocation. In the case, the minimum key is useful. But it does not get data by range. So no prefetching is designed. To design a comprehensive development environment, it is better to design those APIs. I will do that later. 06/11/2018, Bing Li
 * 
 * The cache is used ONLY for MemoryLocation, which has only one replica on the CServer. Although it is unnecessary to replicate, replicating is added to keep it perfect. 06/10/2018, Bing Li
 */

// Created: 06/02/2018, Bing Li
public class SortedDistributedMap<Value extends Pointing, Factory extends CacheMapFactorable<String, Value>, DescendantComp extends Comparator<Value>, ReplicateThread extends NotificationObjectQueue<PointingReplicateNotification<Value>>, ReplicateThreadCreator extends NotificationObjectThreadCreatable<PointingReplicateNotification<Value>, ReplicateThread>, PostfetchNotification extends MapPostfetchNotification, PostfetchThread extends NotificationObjectQueue<PostfetchNotification>, PostfetchThreadCreator extends NotificationObjectThreadCreatable<PostfetchNotification, PostfetchThread>, EvictThread extends NotificationObjectQueue<EvictedNotification<Value>>, EvictThreadCreator extends NotificationObjectThreadCreatable<EvictedNotification<Value>, EvictThread>> extends RootCache<String, Value, Factory, StringKeyDB>
{
	private SortedListDB sortedPointsDB;
	private Map<String, Float> sortedPoints;
	
	private ListKeyDB sortedKeysDB;
	private Map<Integer, String> sortedKeys;
	
	// The DB saves the keys that are not necessary to be sorted. 02/14/2019, Bing Li
	private ListKeyDB obsoleteKeysDB;
	private Map<Integer, String> obsoleteKeys;

//	private double currentMinPoints;
	private DescendantComp comp;
	private final int sortSize;

	private NotificationObjectDispatcher<PointingReplicateNotification<Value>, ReplicateThread, ReplicateThreadCreator> replicateDispatcher;
	private NotificationObjectDispatcher<PostfetchNotification, PostfetchThread, PostfetchThreadCreator> postfetchDispatcher;
	private NotificationObjectDispatcher<EvictedNotification<Value>, EvictThread, EvictThreadCreator> evictedDispatcher;

	private ThreadPool threadPool;
	private Map<String, Sync> syncs;
	private final long postfetchTimeout;
//	private ReentrantReadWriteLock lock;
	
	// When postfetched data is large, some of them might be evicted. So it is necessary to put them into the map temporarily. 07/18/2018, Bing Li
	private Map<String, List<Value>> postfetchedData;
	private Map<String, Value> postfetchedSingleData;
	
	private IsEvicted isEvicted;

	public SortedDistributedMap(SortedDistributedMapBuilder<Value, Factory, DescendantComp, ReplicateThread, ReplicateThreadCreator, PostfetchNotification, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> builder)
	{
		super(builder.getFactory(), builder.getRootPath(), builder.getCacheKey(), builder.getCacheSize(), builder.getOffheapSizeInMB(), builder.getDiskSizeInMB(), StringKeyDBPool.SHARED().getDB(CacheConfig.getMapCacheKeyDBPath(builder.getRootPath(), builder.getCacheKey())), true);

		this.replicateDispatcher = new NotificationObjectDispatcher.NotificationObjectDispatcherBuilder<PointingReplicateNotification<Value>, ReplicateThread, ReplicateThreadCreator>()
				.poolSize(builder.getPoolSize())
				.threadCreator(builder.getReplicateThreadCreator())
				.notificationQueueSize(builder.getNotificationQueueSize())
				.dispatcherWaitTime(builder.getDispatcherWaitTime())
//				.waitRound(builder.getWaitRound())
				.idleCheckDelay(builder.getIdleCheckDelay())
				.idleCheckPeriod(builder.getIdleCheckPeriod())
				.scheduler(builder.getScheduler())
				.build();

		this.postfetchDispatcher = new NotificationObjectDispatcher.NotificationObjectDispatcherBuilder<PostfetchNotification, PostfetchThread, PostfetchThreadCreator>()
				.poolSize(builder.getPoolSize())
				.threadCreator(builder.getPostfetchThreadCreator())
				.notificationQueueSize(builder.getNotificationQueueSize())
				.dispatcherWaitTime(builder.getDispatcherWaitTime())
//				.waitRound(builder.getWaitRound())
				.idleCheckDelay(builder.getIdleCheckDelay())
				.idleCheckPeriod(builder.getIdleCheckPeriod())
				.scheduler(builder.getScheduler())
				.build();

		this.evictedDispatcher = new NotificationObjectDispatcher.NotificationObjectDispatcherBuilder<EvictedNotification<Value>, EvictThread, EvictThreadCreator>()
				.poolSize(builder.getPoolSize())
				.threadCreator(builder.getEvictThreadCreator())
				.notificationQueueSize(builder.getNotificationQueueSize())
				.dispatcherWaitTime(builder.getDispatcherWaitTime())
//				.waitRound(builder.getWaitRound())
				.idleCheckDelay(builder.getIdleCheckDelay())
				.idleCheckPeriod(builder.getIdleCheckPeriod())
				.scheduler(builder.getScheduler())
				.build();

		this.postfetchedData = new ConcurrentHashMap<String, List<Value>>();
		this.postfetchedSingleData = new ConcurrentHashMap<String, Value>();

		this.threadPool = builder.getThreadPool();
		this.syncs = new ConcurrentHashMap<String, Sync>();
		this.postfetchTimeout = builder.getPostfetchTimeout();
//		this.lock = new ReentrantReadWriteLock();

		this.sortedPointsDB = new SortedListDB(CacheConfig.getPointingListPointsCachePath(builder.getRootPath(), builder.getCacheKey()));
		this.sortedPoints = this.sortedPointsDB.getValues();
		
		this.sortedKeysDB = new ListKeyDB(CacheConfig.getListKeysCachePath(builder.getRootPath(), builder.getCacheKey()));
		this.sortedKeys = this.sortedKeysDB.getAllKeys();
		
		this.obsoleteKeysDB = new ListKeyDB(CacheConfig.getObsoleteListKeysCachePath(builder.getRootPath(), builder.getCacheKey()));
		this.obsoleteKeys = this.obsoleteKeysDB.getAllKeys();
		
		this.comp = builder.getComp();
		this.sortSize = builder.getSortSize();
//		this.currentMinPoints = UtilConfig.ZERO;
		
		try
		{
			this.isEvicted = (IsEvicted)FileManager.readObject(CacheConfig.getIsEvictedPath(super.getCachePathAtBase()));
		}
		catch (IOException e)
		{
			this.isEvicted = new IsEvicted();
		}
	}

	public static class SortedDistributedMapBuilder<Value extends Pointing, Factory extends CacheMapFactorable<String, Value>, DescendantComp extends Comparator<Value>, ReplicateThread extends NotificationObjectQueue<PointingReplicateNotification<Value>>, ReplicateThreadCreator extends NotificationObjectThreadCreatable<PointingReplicateNotification<Value>, ReplicateThread>, PostfetchNotification extends MapPostfetchNotification, PostfetchThread extends NotificationObjectQueue<PostfetchNotification>, PostfetchThreadCreator extends NotificationObjectThreadCreatable<PostfetchNotification, PostfetchThread>, EvictThread extends NotificationObjectQueue<EvictedNotification<Value>>, EvictThreadCreator extends NotificationObjectThreadCreatable<EvictedNotification<Value>, EvictThread>> implements Builder<SortedDistributedMap<Value, Factory, DescendantComp, ReplicateThread, ReplicateThreadCreator, PostfetchNotification, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator>>
	{
		private String cacheKey;
		private Factory factory;
		private String rootPath;
//		private int cacheSize;
		private long cacheSize;
		private int offheapSizeInMB;
		private int diskSizeInMB;

		private DescendantComp comp;
		private int sortSize;

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
		private long postfetchTimeout;
		
		public SortedDistributedMapBuilder()
		{
		}
		
		public SortedDistributedMapBuilder<Value, Factory, DescendantComp, ReplicateThread, ReplicateThreadCreator, PostfetchNotification, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> cacheKey(String cacheKey)
		{
			this.cacheKey = cacheKey;
			return this;
		}
		
		public SortedDistributedMapBuilder<Value, Factory, DescendantComp, ReplicateThread, ReplicateThreadCreator, PostfetchNotification, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> factory(Factory factory)
		{
			this.factory = factory;
			return this;
		}
		
		public SortedDistributedMapBuilder<Value, Factory, DescendantComp, ReplicateThread, ReplicateThreadCreator, PostfetchNotification, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> rootPath(String rootPath)
		{
			this.rootPath = rootPath;
			return this;
		}
		
		public SortedDistributedMapBuilder<Value, Factory, DescendantComp, ReplicateThread, ReplicateThreadCreator, PostfetchNotification, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> cacheSize(long cacheSize)
		{
			this.cacheSize = cacheSize;
			return this;
		}
		
		public SortedDistributedMapBuilder<Value, Factory, DescendantComp, ReplicateThread, ReplicateThreadCreator, PostfetchNotification, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> offheapSizeInMB(int offheapSizeInMB)
		{
			this.offheapSizeInMB = offheapSizeInMB;
			return this;
		}
		
		public SortedDistributedMapBuilder<Value, Factory, DescendantComp, ReplicateThread, ReplicateThreadCreator, PostfetchNotification, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> diskSizeInMB(int diskSizeInMB)
		{
			this.diskSizeInMB = diskSizeInMB;
			return this;
		}
		
		public SortedDistributedMapBuilder<Value, Factory, DescendantComp, ReplicateThread, ReplicateThreadCreator, PostfetchNotification, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> poolSize(int poolSize)
		{
			this.poolSize = poolSize;
			return this;
		}

		public SortedDistributedMapBuilder<Value, Factory, DescendantComp, ReplicateThread, ReplicateThreadCreator, PostfetchNotification, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> replicateCreator(ReplicateThreadCreator replicateCreator)
		{
			this.replicateCreator = replicateCreator;
			return this;
		}

		public SortedDistributedMapBuilder<Value, Factory, DescendantComp, ReplicateThread, ReplicateThreadCreator, PostfetchNotification, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> postfetchCreator(PostfetchThreadCreator postfetchCreator)
		{
			this.postfetchCreator = postfetchCreator;
			return this;
		}

		public SortedDistributedMapBuilder<Value, Factory, DescendantComp, ReplicateThread, ReplicateThreadCreator, PostfetchNotification, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> evictCreator(EvictThreadCreator evictCreator)
		{
			this.evictCreator = evictCreator;
			return this;
		}

		public SortedDistributedMapBuilder<Value, Factory, DescendantComp, ReplicateThread, ReplicateThreadCreator, PostfetchNotification, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> notificationQueueSize(int nqSize)
		{
			this.notificationQueueSize = nqSize;
			return this;
		}
		
		public SortedDistributedMapBuilder<Value, Factory, DescendantComp, ReplicateThread, ReplicateThreadCreator, PostfetchNotification, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> dispatcherWaitTime(long dispatcherWaitTime)
		{
			this.dispatcherWaitTime = dispatcherWaitTime;
			return this;
		}

		/*
		public SortedDistributedMapBuilder<Value, Factory, DescendantComp, ReplicateThread, ReplicateThreadCreator, PostfetchNotification, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> waitRound(int waitRound)
		{
			this.waitRound = waitRound;
			return this;
		}
		*/
		
		public SortedDistributedMapBuilder<Value, Factory, DescendantComp, ReplicateThread, ReplicateThreadCreator, PostfetchNotification, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> idleCheckDelay(long idleCheckDelay)
		{
			this.idleCheckDelay = idleCheckDelay;
			return this;
		}
		
		public SortedDistributedMapBuilder<Value, Factory, DescendantComp, ReplicateThread, ReplicateThreadCreator, PostfetchNotification, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> idleCheckPeriod(long idleCheckPeriod)
		{
			this.idleCheckPeriod = idleCheckPeriod;
			return this;
		}
		
		public SortedDistributedMapBuilder<Value, Factory, DescendantComp, ReplicateThread, ReplicateThreadCreator, PostfetchNotification, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> scheduler(ScheduledThreadPoolExecutor scheduler)
		{
			this.scheduler = scheduler;
			return this;
		}
		
		public SortedDistributedMapBuilder<Value, Factory, DescendantComp, ReplicateThread, ReplicateThreadCreator, PostfetchNotification, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> threadPool(ThreadPool threadPool)
		{
			this.threadPool = threadPool;
			return this;
		}

		public SortedDistributedMapBuilder<Value, Factory, DescendantComp, ReplicateThread, ReplicateThreadCreator, PostfetchNotification, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> postfetchTimeout(long postfetchTimeout)
		{
			this.postfetchTimeout = postfetchTimeout;
			return this;
		}

		public SortedDistributedMapBuilder<Value, Factory, DescendantComp, ReplicateThread, ReplicateThreadCreator, PostfetchNotification, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> comp(DescendantComp comp)
		{
			this.comp = comp;
			return this;
		}

		public SortedDistributedMapBuilder<Value, Factory, DescendantComp, ReplicateThread, ReplicateThreadCreator, PostfetchNotification, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> sortSize(int sortSize)
		{
			this.sortSize = sortSize;
			return this;
		}

		@Override
		public SortedDistributedMap<Value, Factory, DescendantComp, ReplicateThread, ReplicateThreadCreator, PostfetchNotification, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> build()
		{
			return new SortedDistributedMap<Value, Factory, DescendantComp, ReplicateThread, ReplicateThreadCreator, PostfetchNotification, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator>(this);
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
		
		public DescendantComp getComp()
		{
			return this.comp;
		}
		
		public int getSortSize()
		{
			return this.sortSize;
		}
	}

	public void shutdown() throws InterruptedException, IOException
	{
//		this.lock.writeLock().lock();
		super.closeAtBase();
//		this.lock.writeLock().unlock();

		FileManager.writeObject(CacheConfig.getIsEvictedPath(super.getCachePathAtBase()), this.isEvicted);
//		this.lock.writeLock().lock();
		
		this.sortedPointsDB.removeAll();
		this.sortedPointsDB.putAll(this.sortedPoints);
		this.sortedPointsDB.close();

		this.sortedKeysDB.removeAll();
		this.sortedKeysDB.putAll(this.sortedKeys);
		this.sortedKeysDB.close();

		this.obsoleteKeysDB.removeAll();
		this.obsoleteKeysDB.putAll(this.obsoleteKeys);
		this.obsoleteKeysDB.close();
		
//		this.lock.writeLock().unlock();

		this.postfetchedData.clear();
		this.postfetchedData = null;
		this.postfetchedSingleData.clear();
		this.postfetchedSingleData = null;

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
	
	public String getCacheKey()
	{
		return super.getCacheKeyAtBase();
	}

	private void signal(String key)
	{
		this.syncs.get(key).signal();
		this.syncs.remove(key);
	}

	/*
	private void setMinPoints(double points)
	{
		this.lock.writeLock().lock();
		this.currentMinPoints = points;
		this.lock.writeLock().unlock();
	}
	*/
	
	/*
	private double getMinPoints()
	{
		this.lock.readLock().lock();
		try
		{
			return this.currentMinPoints;
		}
		finally
		{
			this.lock.readLock().unlock();
		}
	}
	*/

	public void savePostfetchedData(String key, List<Value> values, boolean isBlocking)
	{
		if (isBlocking)
		{
			if (values != null)
			{
				// The notify/wait mechanism results in the concurrently accessing on the values. It might lead to ConcurrentModificationException. So it is necessary to send out the values first and then save them into the cache. Or make a copy. 08/04/2018, Bing Li
//				this.postfetchedData.put(key, values);
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
			this.putAllLocally(values);
		}
	}
	
	public void savePostfetchedData(String key, Value v, boolean isBlocking)
	{
		if (v != null)
		{
			// The notify/wait mechanism results in the concurrently accessing on the value. It might lead to ConcurrentModificationException. Since only one piece of data is posfetched, it can be saved into the cache first. 08/04/2018, Bing Li
			this.putLocally(v);
			if (isBlocking)
			{
				this.postfetchedSingleData.put(key, v);
			}
		}
		if (isBlocking)
		{
			this.signal(key);
		}
		/*
		if (v != null)
		{
			this.put(v);
		}
		*/
	}

//	public void put(Value rsc)
	public void put(PointingReplicateNotification<Value> notification)
	{
		super.putAtBase(notification.getValue().getKey(), notification.getValue());
//		if (super.getMemCacheSize() <= 0)
//		this.lock.readLock().lock();
		if (this.sortedKeys.size() <= 0)
		{
//			this.lock.readLock().unlock();
//			this.lock.writeLock().lock();
			this.sortedPoints.put(notification.getValue().getKey(), notification.getValue().getPoints());
			this.sortedKeys.put(0, notification.getValue().getKey());
//			this.currentMinPoints = notification.getValue().getPoints();
//			this.setMinPoints(notification.getValue().getPoints());
//			this.lock.readLock().lock();
//			this.lock.writeLock().unlock();
		}
		else
		{
//			this.lock.readLock().unlock();
//			this.lock.writeLock().lock();
//			Map<String, Float> allPoints = this.sortedPointsDB.getValues();
			this.sortedPoints.put(notification.getValue().getKey(), notification.getValue().getPoints());
//			this.sortedPoints = CollectionSorter.sortDescendantAsConcurrency(this.sortedPoints);
			Map<String, Float> sp = CollectionSorter.sortDescendantByValue(this.sortedPoints);
			int obsSize = this.obsoleteKeys.size();

			int index = 0;
//			Set<String> removedKeys = Sets.newHashSet();
			Set<String> removedKeys = new HashSet<String>();

//			for (Map.Entry<String, Float> entry : this.sortedPoints.entrySet())
			for (Map.Entry<String, Float> entry : sp.entrySet())
			{
//				System.out.println(index + ") points = " + entry.getValue());
				if (index < this.sortSize)
				{
					this.sortedKeys.put(index++, entry.getKey());
				}
				else
				{
					this.obsoleteKeys.put(obsSize++, entry.getKey());
					removedKeys.add(entry.getKey());
				}
			}
			
//			System.out.println("------------------------------");

			for (String entry : removedKeys)
			{
				this.sortedPoints.remove(entry);
//				this.sortedPointsDB.remove(entry);
			}

//			this.sortedPointsDB.putAll(allPoints);

//			this.currentMinPoints = allPoints.get(this.sortedKeys.get(index - 1));
//			this.setMinPoints(allPoints.get(this.keys.get(index - 1)));

//			this.lock.readLock().lock();
//			this.lock.writeLock().unlock();
		}
//		this.lock.readLock().unlock();
		if (!this.replicateDispatcher.isReady())
		{
			this.threadPool.execute(this.replicateDispatcher);
		}
		this.replicateDispatcher.enqueue(notification);
	}

	public void putLocally(Value rsc)
	{
		super.putAtBase(rsc.getKey(), rsc);
//		this.lock.readLock().lock();
		if (this.sortedKeys.size() <= 0)
		{
//			this.lock.readLock().unlock();
//			this.lock.writeLock().lock();
			this.sortedPoints.put(rsc.getKey(), rsc.getPoints());
			this.sortedKeys.put(0, rsc.getKey());
//			this.currentMinPoints = rsc.getPoints();
//			this.lock.readLock().lock();
//			this.lock.writeLock().unlock();
//			this.setMinPoints(rsc.getPoints());
		}
		else
		{
//			this.lock.readLock().unlock();
//			this.lock.writeLock().lock();
//			super.put(rsc.getKey(), rsc);
//			Map<String, Float> allPoints = this.sortedPointsDB.getValues();
			this.sortedPoints.put(rsc.getKey(), rsc.getPoints());
//			this.sortedPoints = CollectionSorter.sortDescendantAsConcurrency(this.sortedPoints);
			Map<String, Float> sp = CollectionSorter.sortDescendantByValue(this.sortedPoints);
//			int obsSize = this.obsoleteKeysDB.getSize();
			int obsSize = this.obsoleteKeys.size();

			int index = 0;
//			Set<String> removedKeys = Sets.newHashSet();
			Set<String> removedKeys = new HashSet<String>();
//			this.lock.readLock().unlock();
//			this.lock.writeLock().lock();
//			for (Map.Entry<String, Float> entry : this.sortedPoints.entrySet())
			for (Map.Entry<String, Float> entry : sp.entrySet())
			{
				if (index < this.sortSize)
				{
					this.sortedKeys.put(index++, entry.getKey());
				}
				else
				{
					this.obsoleteKeys.put(obsSize++, entry.getKey());
					removedKeys.add(entry.getKey());
				}
			}

			for (String entry : removedKeys)
			{
				this.sortedPoints.remove(entry);
//				this.sortedPointsDB.remove(entry);
			}

//			this.sortedPointsDB.putAll(allPoints);

//			this.lock.readLock().lock();
//			this.lock.writeLock().unlock();
		}
//		this.lock.readLock().unlock();
	}

//	public void putAll(List<Value> rscs)
	public void putAll(PointingReplicateNotification<Value> notification)
	{
		// The below line aims to avoid the exception, ConcurrentModificationException. 10/13/2019, Bing Li
		List<Value> sortedList = new ArrayList<Value>(notification.getValues());
		Collections.sort(sortedList, this.comp);
		for (Value rsc : sortedList)
		{
			super.putAtBase(rsc.getKey(), rsc);
		}

//		this.lock.readLock().lock();
//		Map<String, Float> allPoints = this.sortedPointsDB.getValues();
		int obsSize = this.obsoleteKeys.size();
//		this.lock.readLock().unlock();

		for (Value rsc : sortedList)
		{
			this.sortedPoints.put(rsc.getKey(), rsc.getPoints());
		}
		Map<String, Float> sp = CollectionSorter.sortDescendantByValue(this.sortedPoints);

		int index = 0;
//		Set<String> removedKeys = Sets.newHashSet();
		Set<String> removedKeys = new HashSet<String>();
//		for (Map.Entry<String, Float> entry : this.sortedPoints.entrySet())
		for (Map.Entry<String, Float> entry : sp.entrySet())
		{
			if (index < this.sortSize)
			{
//				this.lock.writeLock().lock();
				this.sortedKeys.put(index++, entry.getKey());
//				this.lock.writeLock().unlock();
			}
			else
			{
//				this.lock.writeLock().lock();
				this.obsoleteKeys.put(obsSize++, entry.getKey());
//				this.lock.writeLock().unlock();
				removedKeys.add(entry.getKey());
			}
		}

		for (String entry : removedKeys)
		{
//			this.lock.writeLock().lock();
			this.sortedPoints.remove(entry);
//			this.lock.writeLock().unlock();
		}
//		this.lock.writeLock().lock();
//		this.sortedPointsDB.putAll(allPoints);
//		this.lock.writeLock().unlock();

		if (!this.replicateDispatcher.isReady())
		{
			this.threadPool.execute(this.replicateDispatcher);
		}
		this.replicateDispatcher.enqueue(notification);
	}

	public void putAllLocally(List<Value> rscs)
	{
		for (Value rsc : rscs)
		{
			super.putAtBase(rsc.getKey(), rsc);
		}
		
//		this.lock.readLock().lock();
//		Map<String, Float> allPoints = this.sortedPointsDB.getValues();
		int obsSize = this.obsoleteKeys.size();
//		this.lock.readLock().unlock();

		for (Value rsc : rscs)
		{
			this.sortedPoints.put(rsc.getKey(), rsc.getPoints());
		}
		Map<String, Float> sp = CollectionSorter.sortDescendantByValue(this.sortedPoints);

		int index = 0;
//		Set<String> removedKeys = Sets.newHashSet();
		Set<String> removedKeys = new HashSet<String>();
		for (Map.Entry<String, Float> entry : sp.entrySet())
		{
			if (index < this.sortSize)
			{
//				this.lock.writeLock().lock();
				this.sortedKeys.put(index++, entry.getKey());
//				this.lock.writeLock().unlock();
			}
			else
			{
//				this.lock.writeLock().lock();
				this.obsoleteKeys.put(obsSize++, entry.getKey());
//				this.lock.writeLock().unlock();
				removedKeys.add(entry.getKey());
			}
		}

		for (String entry : removedKeys)
		{
			this.sortedPoints.remove(entry);
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
	
	public Value get(PostfetchNotification notification)
	{
//		this.lock.readLock().lock();
//		System.out.println("PointingDistributedMap-get(...): resourceKey = " + notification.getResourceKey()); 
		Value v = super.getAtBase(notification.getResourceKey());
//		this.lock.readLock().unlock();
		if (v != null)
		{
//			System.out.println("PointingDistributedMap-get(...): value is NOT NULL ..."); 
			return v;
		}
//		System.out.println("PointingDistributedMap-get(...): value is NULL ..."); 
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
			this.lock.readLock().lock();
			try
			{
				return super.get(notification.getResourceKey());
			}
			finally
			{
				this.lock.readLock().unlock();
			}
			*/
//			System.out.println("PointingDistributedMap-get(...): after postfetching ..."); 
//			return super.get(notification.getResourceKey());
			v = this.postfetchedSingleData.get(notification.getKey());
			this.postfetchedSingleData.remove(notification.getKey());
			return v;
		}
		return v;
	}
	
	public String getMaximumKey()
	{
		/*
		this.lock.readLock().lock();
		try
		{
			return this.sortedKeys.get(0);
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		*/
		return this.sortedKeys.get(0);
	}

	public String getMinimumKey()
	{
		/*
		this.lock.readLock().lock();
		try
		{
			return this.keys.get(super.size() - 1);
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		*/
		/*
		 * Since the predefined sorting size is employed, the minimum data is defined as the one in the range, not the real one in the global range. So it is not necessary to make a judgment whether data is evicted or not. 02/18/2019, Bing Li
		 * 
		 * When no data is evicted, the below solution is correct. 08/01/2018, Bing Li
		 */
		/*
		if (!this.isEvicted.isHappened())
		{
//			System.out.println("PointingDistributedMap-getMinimumKey(): keys.getSize() = " + this.keys.getSize());
			this.lock.readLock().lock();
			try
			{
				return this.sortedKeys.get(this.sortedKeys.getSize() - 1);
			}
			finally
			{
				this.lock.readLock().unlock();
			}
		}
		else
		{
			return UtilConfig.EMPTY_STRING;
		}
		*/
		/*
		this.lock.readLock().lock();
		try
		{
			return this.sortedKeys.get(this.sortedKeys.size() - 1);
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		*/
		if (this.sortedKeys.size() >= this.sortSize)
		{
			return this.sortedKeys.get(this.sortSize - 1);
		}
		else
		{
			return this.sortedKeys.get(this.sortedKeys.size() - 1);
		}
	}
	
	/*
	 * When the size of data is huge, the min data must be saved to a remote terminal server. Its performance should be slow. For the case of my New WWW, it is not useful. Only limited data caches need the cache. 08/24/2018, Bing Li
	 */
	
	public Value getMinPointing(PostfetchNotification notification)
	{
		String minKey = this.getMinimumKey();
		if (!minKey.equals(UtilConfig.EMPTY_STRING))
		{
			return super.getAtBase(minKey);
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
			Value v = this.postfetchedSingleData.get(notification.getKey());
			this.postfetchedSingleData.remove(notification.getKey());
			return v;
		}
		return null;
	}

	/*
	public void put(String k, Value v)
	{
		this.lock.writeLock().lock();
		try
		{
			super.put(k, v);
		}
		finally
		{
			this.lock.writeLock().unlock();
		}
	}
	*/
	
	public boolean containsKey(String key)
	{
		/*
		this.lock.readLock().lock();
		try
		{
			return super.containsKey(key);
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		*/
		return super.containsKeyAtBase(key);
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

	@Override
	public void evict(String k, Value v) throws TerminalServerOverflowedException
	{
		this.isEvicted.setHappened(true);
//		EvictedNotification<Value> notification = new EvictedNotification<Value>(k, v);
		if (!this.evictedDispatcher.isReady())
		{
			this.threadPool.execute(this.evictedDispatcher);
		}
		this.evictedDispatcher.enqueue(new EvictedNotification<Value>(k, v));
		
//		this.lock.writeLock().lock();
		this.sortedPoints.remove(k);
//		this.lock.writeLock().unlock();
		
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
