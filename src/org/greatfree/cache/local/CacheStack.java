package org.greatfree.cache.local;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
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
 */

/*
 * The class does NOT keep the consistency between the Cache and the StringKeyDB after evicting. 05/30/2018, Bing Li
 */

/*
 * In the case, since it is a heavy burden to deal with the issue of updating the indexes when a resource is evicted since the key should be updated for half the total size of the cache on average, I decide to give it up. Thus, some keys do not have values. For a local cache, the return value for such a key is null. I will design a distributed cache for each type. Then, the null value can be retrieved from the backend server. In this system for the WWW, it is not a problem since the data updates frequently and the amount is large. Missing some data is not a problem. At least, important data is retained. With the distributed cache's support, the missed data issue can be resolved. 04/28/2018, Bing Li
 * 
 */

// Created: 05/23/2017, Bing Li
// public class PersistableStack<Resource extends Serializable, Factory extends PersistableMapFactorable<Integer, Resource>> extends PersistableMap<Integer, Resource, Factory, IntegerKeyDB>
public class CacheStack<Value extends Serializable, Factory extends CacheMapFactorable<Integer, Value>> extends RootCache<Integer, Value, Factory, IntegerKeyDB>
{
	private AtomicInteger headIndex;
	private AtomicInteger tailIndex;
//	private ReentrantReadWriteLock lock;

	public CacheStack(PersistableStackBuilder<Value, Factory> builder)
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
			this.tailIndex = new AtomicInteger(-1);
		}
//		this.lock = new ReentrantReadWriteLock();
	}

	public static class PersistableStackBuilder<Resource extends Serializable, Factory extends CacheMapFactorable<Integer, Resource>> implements Builder<CacheStack<Resource, Factory>>
	{
		private Factory factory;
		private String rootPath;
		private String queueKey;
//		private int cacheSize;
		private long cacheSize;
		private int offheapSizeInMB;
		private int diskSizeInMB;
		
		public PersistableStackBuilder()
		{
		}

		public PersistableStackBuilder<Resource, Factory> factory(Factory factory)
		{
			this.factory = factory;
			return this;
		}

		public PersistableStackBuilder<Resource, Factory> rootPath(String rootPath)
		{
			this.rootPath = FileManager.appendSlash(rootPath);
			return this;
		}

		public PersistableStackBuilder<Resource, Factory> queueKey(String queueKey)
		{
			this.queueKey = queueKey;
			return this;
		}

		public PersistableStackBuilder<Resource, Factory> cacheSize(long cacheSize)
		{
			this.cacheSize = cacheSize;
			return this;
		}

		public PersistableStackBuilder<Resource, Factory> offheapSizeInMB(int offheapSizeInMB)
		{
			this.offheapSizeInMB = offheapSizeInMB;
			return this;
		}

		public PersistableStackBuilder<Resource, Factory> diskSizeInMB(int diskSizeInMB)
		{
			this.diskSizeInMB = diskSizeInMB;
			return this;
		}

		@Override
		public CacheStack<Resource, Factory> build()
		{
			return new CacheStack<Resource, Factory>(this);
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
//		this.lock.writeLock().lock();
		super.closeAtBase();
//		this.lock.writeLock().unlock();
//		this.rwLock = null;
	}
	
	public boolean isEmpty()
	{
		/*
		this.lock.readLock().lock();
		try
		{
			return this.headIndex > this.tailIndex;
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		*/
		return this.headIndex.get() > this.tailIndex.get();
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

	public void push(Value rsc)
	{
//		this.lock.writeLock().lock();
		int tail = this.tailIndex.incrementAndGet();
//		this.lock.writeLock().unlock();
		super.putAtBase(tail, rsc);
	}
	
	public void push(List<Value> rscs)
	{
		for (Value rsc : rscs)
		{
			this.push(rsc);
		}
	}
	
	public Value pop()
	{
//			System.out.println("PersistableStack headIndex = " + headIndex);
//			System.out.println("PersistableStack tailIndex = " + tailIndex);
		int tail = UtilConfig.NO_INDEX;
//		this.lock.readLock().lock();
		if (this.headIndex.get() <= this.tailIndex.get())
		{
			tail = this.tailIndex.get();

//			this.lock.readLock().unlock();
//			this.lock.writeLock().lock();
			this.tailIndex.getAndDecrement();
			if (this.headIndex.get() > this.tailIndex.get())
			{
				this.headIndex.set(0);
				this.tailIndex.set(-1);
			}
//			this.lock.readLock().lock();
//			this.lock.writeLock().unlock();
		}
//		this.lock.readLock().unlock();

		if (tail != UtilConfig.NO_INDEX)
		{
			Value rsc = super.getAtBase(tail);
			super.removeAtBase(tail);
			return rsc;
		}
		return null;
	}
	
	public List<Value> pop(int n)
	{
		List<Value> rscs = new ArrayList<Value>();
		Value rsc;
		for (int i = 0; i < n; i++)
		{
			rsc = this.pop();
			if (rsc != null)
			{
				rscs.add(rsc);
			}
			else
			{
				break;
			}
		}
//		System.out.println("PersistaleStack-pop(n) = rscs size = " + rscs.size());
		return rscs;
	}
	
	public Value peek()
	{
//		this.lock.readLock().lock();
		boolean isLess = this.headIndex.get() <= this.tailIndex.get();
		int tail = this.tailIndex.get();
//		this.lock.readLock().unlock();
		if (isLess)
		{
			return super.getAtBase(tail);
		}
		return null;
	}
	
	public List<Value> peek(int n)
	{
		List<Value> rscs = new ArrayList<Value>();
//		this.lock.readLock().lock();
		int headIndex = this.headIndex.get();
		int tailIndex = this.tailIndex.get();
//		this.lock.readLock().unlock();
		
		Value rsc;
		for (int i = 0; i < n; i++)
		{
			if (headIndex <= tailIndex)
			{
				rsc = super.getAtBase(tailIndex);
				if (rsc != null)
				{
					rscs.add(rsc);
					tailIndex--;
				}
				else
				{
					break;
				}
			}
		}
		return rscs;
	}

	@Override
	public void evict(Integer k, Value v)
	{
		// Although it takes time to keep data sorted after evicted data is captured, it is necessary to remove the key. 05/11/2018, Bing Li
		super.removeAtBase(k);
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
