package org.greatfree.cache.distributed.terminal;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.greatfree.cache.CacheConfig;
import org.greatfree.cache.db.StringKeyDB;
import org.greatfree.cache.db.StringKeyDBPool;
import org.greatfree.cache.distributed.terminal.db.PostfetchDB;
import org.greatfree.cache.local.CacheMapFactorable;
import org.greatfree.cache.local.RootCache;
import org.greatfree.exceptions.TerminalServerOverflowedException;
import org.greatfree.util.Builder;

/*
 * The version is tested in the Clouds project. 08/22/2018, Bing Li
 * 
 * 
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

/*
 * Its counterpart is CacheMap, in which one key might not have the value because of eviction. 05/31/2018, Bing Li
 * 
 * The terminal cache keeps the evicted in a DB. Thus, no data will be lost unless the local preset disk is overloaded. So usually, any keys have their own values strictly. 05/31/2018, Bing Li
 *
 * The cache is usually used to keep data at the terminal server in a distributed storage system, such as MServer. It can be used at a standalone server only if the key is required to match with one value. 05/31/2018, Bing Li
 * 
 */

/*
 * The name is changed to CacheTerminalMap. 05/12/2018, Bing Li
 * 
 * The so-called CacheEndMap is the one that is located at the MServer in my system. It cannot postfetch from any other servers. It can perform postfetching from its local disk ONLY. The evicted data is saved at the local disk ONLY. For some sorted data, the prefetching should be allowed. Since this cache is a map, it cannot prefetch. Other caches for prefetching will be designed for the end caches. 05/04/2018, Bing Li
 */

// Created: 05/03/2018, Bing Li
public class TerminalMap<Value extends Serializable, Factory extends CacheMapFactorable<String, Value>, DB extends PostfetchDB<Value>> extends RootCache<String, Value, Factory, StringKeyDB>
{
	private DB db;

	private AtomicInteger currentEvictedCount;
	private final int alertEvictedCount;
//	private ReentrantReadWriteLock lock;

	public TerminalMap(TerminalMapBuilder<Value, Factory, DB> builder)
	{
		super(builder.getFactory(), builder.getRootPath(), builder.getCacheKey(), builder.getCacheSize(), builder.getOffheapSizeInMB(), builder.getDiskSizeInMB(), StringKeyDBPool.SHARED().getDB(CacheConfig.getMapCacheKeyDBPath(builder.getRootPath(), builder.getCacheKey())), true);

		this.db = builder.getDB();
		this.currentEvictedCount = new AtomicInteger(this.db.getSize());
		this.alertEvictedCount = builder.getAlertEvictedCount();
//		this.lock = new ReentrantReadWriteLock();
	}
	
	public static class TerminalMapBuilder<Value extends Serializable, Factory extends CacheMapFactorable<String, Value>, DB extends PostfetchDB<Value>> implements Builder<TerminalMap<Value, Factory, DB>>
	{
		private String cacheKey;
		private Factory factory;
		private String rootPath;
//		private int cacheSize;
		private long cacheSize;
		private int offheapSizeInMB;
		private int diskSizeInMB;

		private DB db;
		private int alertEvictedCount;
		
		public TerminalMapBuilder()
		{
		}

		public TerminalMapBuilder<Value, Factory, DB> cacheKey(String cacheKey)
		{
			this.cacheKey = cacheKey;
			return this;
		}

		public TerminalMapBuilder<Value, Factory, DB> factory(Factory factory)
		{
			this.factory = factory;
			return this;
		}

		public TerminalMapBuilder<Value, Factory, DB> rootPath(String rootPath)
		{
			this.rootPath = rootPath;
			return this;
		}

		public TerminalMapBuilder<Value, Factory, DB> cacheSize(long cacheSize)
		{
			this.cacheSize = cacheSize;
			return this;
		}

		public TerminalMapBuilder<Value, Factory, DB> offheapSizeInMB(int offheapSizeInMB)
		{
			this.offheapSizeInMB = offheapSizeInMB;
			return this;
		}

		public TerminalMapBuilder<Value, Factory, DB> diskSizeInMB(int diskSizeInMB)
		{
			this.diskSizeInMB = diskSizeInMB;
			return this;
		}

		public TerminalMapBuilder<Value, Factory, DB> db(DB db)
		{
			this.db = db;
			return this;
		}

		public TerminalMapBuilder<Value, Factory, DB> alertEvictedCount(int alertEvictedCount)
		{
			this.alertEvictedCount = alertEvictedCount;
			return this;
		}

		@Override
		public TerminalMap<Value, Factory, DB> build()
		{
			return new TerminalMap<Value, Factory, DB>(this);
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
	
	public Map<String, Value> getValues()
	{
		/*
		this.lock.readLock().lock();
		try
		{
			return super.getValues();
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		*/
		return super.getValuesAtBase();
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
	
	public int getSize()
	{
		return super.getKeysAtBase().size();
	}

	public Value get(String rscKey)
	{
//		this.lock.readLock().lock();
		Value v = super.getAtBase(rscKey);
//		this.lock.readLock().unlock();
		if (v != null)
		{
			return v;
		}
//		this.lock.readLock().lock();
		v = this.db.get(rscKey);
//		this.lock.readLock().unlock();
		if (v != null)
		{
			super.putAtBase(rscKey, v);
		}
//		this.lock.readLock().unlock();
//		this.lock.writeLock().lock();
//		this.lock.writeLock().unlock();
		return v;
	}
	
	public Map<String, Value> getValues(Set<String> rscKeys)
	{
//		System.out.println("1) TerminalMap-getValues(): rsc keys size = " + rscKeys.size());
//		this.lock.readLock().lock();
		Map<String, Value> vs = super.getAtBase(rscKeys);
//		this.lock.readLock().unlock();
		if (vs != null)
		{
//			System.out.println("2) TerminalMap-getValues(): v size = " + v.size());
			if (vs.size() >= rscKeys.size())
			{
				return vs;
			}
		}
		Set<String> obtainedRscKeys = vs.keySet();
//		Set<String> unavailableRscKeys = Sets.difference(rscKeys, obtainedRscKeys);
		Set<String> unavailableRscKeys = new HashSet<String>(rscKeys);
		rscKeys.removeAll(obtainedRscKeys);
//		this.lock.readLock().lock();
		Map<String, Value> restRscs = this.db.getMap(unavailableRscKeys);
//		System.out.println("3) TerminalMap-getValues(): restRscs size = " + restRscs.size());
//		this.lock.readLock().unlock();
//		this.lock.writeLock().lock();
		if (restRscs != null)
		{
			super.putAllAtBase(restRscs);
		}
//		this.lock.writeLock().unlock();
		vs.putAll(restRscs);
//		System.out.println("4) TerminalMap-getValues(): v size = " + v.size());
		return vs;
	}
	
	public boolean containsKey(String rscKey)
	{
//		this.lock.readLock().lock();
		boolean isExisted = super.containsKeyAtBase(rscKey);
//		this.lock.readLock().unlock();
		if (isExisted)
		{
			return true;
		}
//		this.lock.readLock().lock();
		Value v = this.db.get(rscKey);
//		this.lock.readLock().unlock();
		if (v != null)
		{
			// Now I decide to put the data form the DB to the cache. 06/15/2018, Bing Li
			// To simplify the code, the data loaded from the DB will NOT be put into the cache. The benefit to do that is not so obvious. In addition, the DB itself has the cache capability. My work just focus on designing a wrapper. 05/31/2018, Bing Li
//			this.put(rscKey, v);
//			this.lock.writeLock().lock();
			super.putAtBase(rscKey, v);
//			this.lock.writeLock().unlock();
			return true;
		}
		return false;
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

	// To be reasonable as a storage, the remove methods can be retained. But it suggests to invoke the methods as few as possible. 06/15/2018, Bing Li
	// It is not necessary to remove data for a terminal of a large-scale storage system. 06/15/2018, Bing Li
	public void remove(String key)
	{
		super.removeAtBase(key);
//		this.lock.writeLock().lock();
		this.db.remove(key);
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
