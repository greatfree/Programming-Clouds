package org.greatfree.cache.local;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.greatfree.cache.CacheConfig;
import org.greatfree.cache.db.IntegerKeyDB;
import org.greatfree.cache.db.IntegerKeyDBPool;
import org.greatfree.util.Builder;
import org.greatfree.util.CollectionSorter;
import org.greatfree.util.FileManager;
import org.greatfree.util.UtilConfig;

/*
* The locking is updated for the current version. 08/21/2018, Bing Li
* 

/*
 * The class does NOT keep the consistency between the Cache and the StringKeyDB after evicting. 05/30/2018, Bing Li
 */

/*
 * In the case, since it is a heavy burden to deal with the issue of updating the indexes when a resource is evicted since the key should be updated for half the total size of the cache on average, I decide to give it up. Thus, some keys do not have values. For a local cache, the return value for such a key is null. I will design a distributed cache for each type. Then, the null value can be retrieved from the backend server. In this system for the WWW, it is not a problem since the data updates frequently and the amount is large. Missing some data is not a problem. At least, important data is retained. With the distributed cache's support, the missed data issue can be resolved. 04/28/2018, Bing Li
 * 
 */

// Created: 05/22/2017, Bing Li
public class CacheQueue<Value extends Serializable, Factory extends CacheMapFactorable<Integer, Value>> extends RootCache<Integer, Value, Factory, IntegerKeyDB>
{
	private AtomicInteger headIndex;
	private AtomicInteger tailIndex;
	private AtomicBoolean isClosed;
//	private ReentrantReadWriteLock lock;
	
	public CacheQueue(PersistableQueueBuilder<Value, Factory> builder)
	{
		super(builder.getFactory(), builder.getRootPath(), builder.getQueueKey(), builder.getCacheSize(), builder.getOffheapSizeInMB(), builder.getDiskSizeInMB(), IntegerKeyDBPool.SHARED().getDB(CacheConfig.getMapCacheKeyDBPath(builder.getRootPath(), builder.getQueueKey())), false);
		Set<Integer> keys = super.getKeysAtBase();
		if (keys.size() > 0)
		{
			List<Integer> keyList = new ArrayList<Integer>(keys);
			this.headIndex = new AtomicInteger(CollectionSorter.minValueKey(keyList));
			this.tailIndex = new AtomicInteger(CollectionSorter.maxValueKey(keyList));
		}
		else
		{
			this.headIndex = new AtomicInteger(0);
			this.tailIndex = new AtomicInteger(0);
		}
		this.isClosed = new AtomicBoolean(false);
//		this.lock = new ReentrantReadWriteLock();
	}
	
	public static class PersistableQueueBuilder<Resource extends Serializable, Factory extends CacheMapFactorable<Integer, Resource>> implements Builder<CacheQueue<Resource, Factory>>
	{
		private Factory factory;
		private String rootPath;
		private String queueKey;
//		private int cacheSize;
		private long cacheSize;
		private int offheapSizeInMB;
		private int diskSizeInMB;

		public PersistableQueueBuilder()
		{
		}

		public PersistableQueueBuilder<Resource, Factory> factory(Factory factory)
		{
			this.factory = factory;
			return this;
		}

		public PersistableQueueBuilder<Resource, Factory> rootPath(String rootPath)
		{
			this.rootPath = FileManager.appendSlash(rootPath);
			return this;
		}

		public PersistableQueueBuilder<Resource, Factory> queueKey(String queueKey)
		{
			this.queueKey = queueKey;
			return this;
		}

		public PersistableQueueBuilder<Resource, Factory> cacheSize(long cacheSize)
		{
			this.cacheSize = cacheSize;
			return this;
		}

		public PersistableQueueBuilder<Resource, Factory> offheapSizeInMB(int offheapSizeInMB)
		{
			this.offheapSizeInMB = offheapSizeInMB;
			return this;
		}

		public PersistableQueueBuilder<Resource, Factory> diskSizeInMB(int diskSizeInMB)
		{
			this.diskSizeInMB = diskSizeInMB;
			return this;
		}

		@Override
		public CacheQueue<Resource, Factory> build()
		{
			return new CacheQueue<Resource, Factory>(this);
		}

		public Factory getFactory()
		{
			return this.factory;
		}
		
		public String getRootPath()
		{
			return this.rootPath;
		}

		public String getQueueKey()
		{
			return this.queueKey;
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
	}

	public void shutdown() throws InterruptedException
	{
		super.closeAtBase();
//		this.lock.writeLock().lock();
		this.isClosed.set(true);;
//		this.lock.writeLock().unlock();
	}
	
	public boolean isClosed()
	{
		/*
		this.lock.readLock().lock();
		try
		{
			return this.isClosed;
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		*/
		return this.isClosed.get();
	}

	public void enqueue(Value rsc)
	{
		super.putAtBase(this.tailIndex.get(), rsc);
//		this.lock.writeLock().lock();
		this.tailIndex.incrementAndGet();
//		super.put(this.tailIndex++, rsc);
//		this.lock.writeLock().unlock();
	}

	public void enqueue(List<Value> rscs)
	{
		for (Value rsc : rscs)
		{
			this.enqueue(rsc);
		}
	}
	
	public void enqueue(Collection<Value> rscs)
	{
		for (Value rsc : rscs)
		{
			this.enqueue(rsc);
		}
	}

	public List<Value> dequeue(int n)
	{
		List<Value> rscs = new ArrayList<Value>();
		Value rsc;
		for (int i = 0; i < n; i++)
		{
			rsc = this.dequeue();
			if (rsc != null)
			{
				rscs.add(rsc);
			}
			else
			{
				break;
			}
		}
		return rscs;
	}
	
	public Value dequeue()
	{
		int head = UtilConfig.NO_INDEX;
//		this.lock.readLock().unlock();
		if (this.headIndex.get() <= this.tailIndex.get())
		{
			head = this.headIndex.get();
			
//			this.lock.readLock().unlock();
//			this.lock.writeLock().lock();
			this.headIndex.getAndIncrement();
			if (this.headIndex.get() > this.tailIndex.get())
			{
				this.headIndex.set(0);
				this.tailIndex.set(0);
			}
//			this.lock.readLock().lock();
//			this.lock.writeLock().unlock();
		}
//		this.lock.readLock().unlock();
		
		if (head != UtilConfig.NO_INDEX)
		{
			Value rsc = super.getAtBase(head);
			super.removeAtBase(head);
			return rsc;
		}
		return null;
	}

	public List<Value> peek(int n)
	{
		List<Value> rscs = new ArrayList<Value>();
		Value rsc;
//		this.lock.readLock().lock();
		int headIndex = this.headIndex.get();
		int tailIndex = this.tailIndex.get();
//		this.lock.readLock().unlock();
		for (int i = 0; i < n; i++)
		{
			if (headIndex < tailIndex)
			{
				rsc = super.getAtBase(headIndex);
				if (rsc != null)
				{
					rscs.add(rsc);
					headIndex++;
				}
				else
				{
					break;
				}
			}
		}
		return rscs;
	}

	public Value peek()
	{
//		this.lock.readLock().lock();
		boolean isContained = this.headIndex.get() < this.tailIndex.get();
		int head = this.headIndex.get();
//		this.lock.readLock().unlock();
		
		if (isContained)
		{
			return super.getAtBase(head);
		}
		return null;
	}

	/*
	 * It is not reasonable to return all of the values in the queue. 08/20/2018, Bing Li
	 * 
	public Map<Integer, Value> getValues()
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
	
	public long getEmptySize()
	{
		/*
		this.lock.readLock().lock();
		try
		{
			return super.getEmptySize();
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		*/
		return super.getEmptySizeAtBase();
	}

	public void clear()
	{
//		this.lock.writeLock().lock();
		this.headIndex.set(0);
		this.tailIndex.set(0);
//		this.lock.writeLock().unlock();
		super.clearAtBase();
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
	
	public long getMemCacheSize()
	{
		/*
		this.lock.readLock().lock();
		try
		{
			return super.getMemCacheSize();
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		*/
		return super.getMemCacheSizeAtBase();
	}

	@Override
	public void evict(Integer k, Value v)
	{
//		this.rwLock.writeLock().lock();
		
		// Although it takes time to keep data sorted after evicted data is captured, it is necessary to remove the key. 05/11/2018, Bing Li
//		this.lock.writeLock().lock();
		super.removeAtBase(k);
//		this.lock.writeLock().unlock();

		// Since it is a heavy burden to deal with the issue of updating the indexes when a resource is evicted since the key should be updated for half the total size of the cache on average, I decide to give it up. Thus, some keys do not have values. For a local cache, the return value for such a key is null. I will design a distributed cache for each type. Then, the null value can be retrieved from the backend server. In this system for the WWW, it is not a problem since the data updates frequently and the amount is large. Missing some data is not a problem. At least, important data is retained. With the distributed cache's support, the missed data issue can be resolved. 04/28/2018, Bing Li
//		System.out.println("\nPersistentQueue-evict(): key = " + k);
//		System.out.println("PersistentQueue-evict(): headIndex = " + this.headIndex);
//		System.out.println("PersistentQueue-evict(): tailIndex = " + this.tailIndex);
		
//		this.rwLock.writeLock().unlock();
		
	}

	@Override
	public void forward(Integer k, Value v)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void remove(Integer k, Value v)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void expire(Integer k, Value v)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update(Integer k, Value v)
	{
		// TODO Auto-generated method stub
		
	}

}
