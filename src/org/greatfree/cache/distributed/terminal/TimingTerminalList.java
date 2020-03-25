package org.greatfree.cache.distributed.terminal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.greatfree.cache.CacheConfig;
import org.greatfree.cache.db.ListKeyDB;
import org.greatfree.cache.db.StringKeyDB;
import org.greatfree.cache.db.StringKeyDBPool;
import org.greatfree.cache.db.TimingListDB;
import org.greatfree.cache.distributed.terminal.db.PostfetchDB;
import org.greatfree.cache.local.CacheMapFactorable;
import org.greatfree.cache.local.RootCache;
import org.greatfree.exceptions.TerminalServerOverflowedException;
import org.greatfree.util.Builder;
import org.greatfree.util.CollectionSorter;
import org.greatfree.util.Time;
import org.greatfree.util.Timing;
import org.greatfree.util.UtilConfig;

import com.google.common.collect.Sets;

/*
 * It is replaced by the Pointing-Based caches. 08/22/2018, Bing Li
 */


/*
 * The locking is updated in the Clouds project. 08/22/2018, Bing Li
 */

/*
 * Its counterpart is TimingList, in which one key might not have the value because of eviction. 05/31/2018, Bing Li
 *  
 * The terminal cache keeps the evicted in a DB. Thus, no data will be lost unless the local preset disk is overloaded. So usually, any keys have their own values strictly. 05/31/2018, Bing Li
 *
 * The cache is usually used to keep data at the terminal server in a distributed storage system, such as MServer. It can be used at a standalone server only if the key is required to match with one value. 05/31/2018, Bing Li
 * 
 */

/*
 * The terminal cache keeps the evicted in a DB. Thus, no data will be lost unless the local preset disk is overloaded. So usually, any keys have their own values strictly. 05/31/2018, Bing Li
 */

// Created: 05/30/2018, Bing Li
class TimingTerminalList<Value extends Timing, Factory extends CacheMapFactorable<String, Value>, DescendantComp extends Comparator<Value>, DB extends PostfetchDB<Value>> extends RootCache<String, Value, Factory, StringKeyDB>
{
	private TimingListDB times;
	private ListKeyDB keys;
	private Date currentOldestTime;
	private DescendantComp comp;
	
	private DB db;
	private AtomicInteger currentEvictedCount;
	private final int alertEvictedCount;

	private ReentrantReadWriteLock lock;
	
	public TimingTerminalList(TimingPersistableListBuilder<Value, Factory, DescendantComp, DB> builder)
	{
		super(builder.getFactory(), builder.getRootPath(), builder.getCacheKey(), builder.getCacheSize(), builder.getOffheapSizeInMB(), builder.getDiskSizeInMB(), StringKeyDBPool.SHARED().getDB(CacheConfig.getMapCacheKeyDBPath(builder.getRootPath(), builder.getCacheKey())), true);
		this.times = new TimingListDB(CacheConfig.getTimingListTimesCachePath(builder.getRootPath(), builder.getCacheKey()));
		
		this.keys = new ListKeyDB(CacheConfig.getListKeysCachePath(builder.getRootPath(), builder.getCacheKey()));

		this.comp = builder.getComp();
		this.currentOldestTime = Time.INIT_TIME;
		this.lock = new ReentrantReadWriteLock();

		this.db = builder.getDB();
		this.currentEvictedCount = new AtomicInteger(this.db.getSize());
		this.alertEvictedCount = builder.getAlertEvictedCount();
	}

	public static class TimingPersistableListBuilder<Value extends Timing, Factory extends CacheMapFactorable<String, Value>, DescendantComp extends Comparator<Value>, DB extends PostfetchDB<Value>> implements Builder<TimingTerminalList<Value, Factory, DescendantComp, DB>>
	{
		private Factory factory;
		private String rootPath;
		private String cacheKey;
//		private int cacheSize;
		private long cacheSize;
		private int offheapSizeInMB;
		private int diskSizeInMB;
		private DescendantComp comp;
		private DB db;
		private int alertEvictedCount;

		public TimingPersistableListBuilder()
		{
		}

		public TimingPersistableListBuilder<Value, Factory, DescendantComp, DB> factory(Factory factory)
		{
			this.factory = factory;
			return this;
		}
		
		public TimingPersistableListBuilder<Value, Factory, DescendantComp, DB> rootPath(String rootPath)
		{
			this.rootPath = rootPath;
			return this;
		}

		public TimingPersistableListBuilder<Value, Factory, DescendantComp, DB> cacheKey(String cacheKey)
		{
			this.cacheKey = cacheKey;
			return this;
		}
		
		public TimingPersistableListBuilder<Value, Factory, DescendantComp, DB> cacheSize(long cacheSize)
		{
			this.cacheSize = cacheSize;
			return this;
		}
		
		public TimingPersistableListBuilder<Value, Factory, DescendantComp, DB> offheapSizeInMB(int offheapSizeInMB)
		{
			this.offheapSizeInMB = offheapSizeInMB;
			return this;
		}
		
		public TimingPersistableListBuilder<Value, Factory, DescendantComp, DB> diskSizeInMB(int diskSizeInMB)
		{
			this.diskSizeInMB = diskSizeInMB;
			return this;
		}
		
		public TimingPersistableListBuilder<Value, Factory, DescendantComp, DB> comp(DescendantComp comp)
		{
			this.comp = comp;
			return this;
		}
		
		public TimingPersistableListBuilder<Value, Factory, DescendantComp, DB> db(DB db)
		{
			this.db = db;
			return this;
		}
		
		public TimingPersistableListBuilder<Value, Factory, DescendantComp, DB> alertEvictedCount(int alertEvictedCount)
		{
			this.alertEvictedCount = alertEvictedCount;
			return this;
		}

		@Override
		public TimingTerminalList<Value, Factory, DescendantComp, DB> build()
		{
			return new TimingTerminalList<Value, Factory, DescendantComp, DB>(this);
		}
		
		public Factory getFactory()
		{
			return this.factory;
		}
		
		public String getRootPath()
		{
			return this.rootPath;
		}

		public String getCacheKey()
		{
			return this.cacheKey;
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

		public DescendantComp getComp()
		{
			return this.comp;
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
		this.lock.writeLock().lock();
		this.times.close();
		this.keys.close();
		this.db.close();
		this.lock.writeLock().unlock();
	}

	public void add(Value rsc)
	{
		if (super.getMemCacheSizeAtBase() <= 0)
		{
			super.putAtBase(rsc.getKey(), rsc);
			this.lock.writeLock().lock();
			this.times.put(rsc.getKey(), rsc.getTime());
			this.keys.put(0, rsc.getKey());
			this.currentOldestTime = rsc.getTime();
			this.lock.writeLock().unlock();
		}
		else
		{
			super.putAtBase(rsc.getKey(), rsc);
			this.lock.readLock().lock();
			if (rsc.getTime().after(this.currentOldestTime))
			{
//				Map<String, Date> allTimes = this.times.getValues();
				// The operation aims to avoid the exception of ConcurrentModificationException. 10/13/2019, Bing Li
				Map<String, Date> allTimes = new HashMap<String, Date>(this.times.getValues());
				allTimes.put(rsc.getKey(), rsc.getTime());
				Map<String, Date> sp = CollectionSorter.sortDescendantByValue(allTimes);
				
				this.lock.readLock().unlock();
				this.lock.writeLock().lock();
				this.times.putAll(allTimes);
				int index = 0;
				for (Map.Entry<String, Date> entry : sp.entrySet())
				{
					this.keys.put(index++, entry.getKey());
				}
				this.currentOldestTime = allTimes.get(this.keys.get(index - 1));
				this.lock.readLock().lock();
				this.lock.writeLock().unlock();
			}
			else
			{
				this.lock.readLock().unlock();
				this.lock.writeLock().lock();
				this.times.put(rsc.getKey(), rsc.getTime());
				this.lock.readLock().lock();
				this.lock.writeLock().unlock();
			}
			this.lock.readLock().unlock();
		}
	}

	public void add(List<Value> rscs)
	{
		if (rscs.size() > 0)
		{
			Collections.sort(rscs, this.comp);
			for (Value rsc : rscs)
			{
				super.putAtBase(rsc.getKey(), rsc);
			}
			this.lock.readLock().lock();
			if (rscs.get(0).getTime().after(this.currentOldestTime))
			{
				int index = this.keys.getSize();
				for (Value rsc : rscs)
				{
					this.lock.readLock().unlock();
					this.lock.writeLock().lock();

//					super.put(rsc.getKey(), rsc);
					this.times.put(rsc.getKey(), rsc.getTime());
					this.keys.put(index++, rsc.getKey());
					
					this.lock.readLock().lock();
					this.lock.writeLock().unlock();
				}
			}
			else
			{
//				Map<String, Date> allTimes = this.times.getValues();
				// The operation aims to avoid the exception of ConcurrentModificationException. 10/13/2019, Bing Li
				Map<String, Date> allTimes = new HashMap<String, Date>(this.times.getValues());
				for (Value rsc : rscs)
				{
//					super.put(rsc.getKey(), rsc);
					allTimes.put(rsc.getKey(), rsc.getTime());
				}
//				allTimes = CollectionSorter.sortDescendantByValue(allTimes);
				Map<String, Date> sp = CollectionSorter.sortDescendantByValue(allTimes);
				this.lock.readLock().unlock();
				this.lock.writeLock().lock();
				this.times.putAll(allTimes);
				int index = 0;
				for (Map.Entry<String, Date> entry : sp.entrySet())
				{
					this.keys.put(index++, entry.getKey());
				}
				this.currentOldestTime = allTimes.get(this.keys.get(index - 1));
				this.lock.readLock().lock();
				this.lock.writeLock().unlock();
			}
			this.lock.readLock().unlock();
		}
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
	
	public Value get(int index)
	{
		String key = UtilConfig.NO_KEY;
		this.lock.readLock().lock();
		key = this.keys.get(index);
		this.lock.readLock().unlock();

		if (!key.equals(UtilConfig.NO_KEY))
		{
			Value v = super.getAtBase(key);
			if (v != null)
			{
				return v;
			}
			this.lock.readLock().lock();
			try
			{
				return this.db.get(key);
			}
			finally
			{
				this.lock.readLock().unlock();
			}
		}
		return null;
	}
	
	public List<Value> get(int startIndex, int endIndex)
	{
		List<Value> rscs = new ArrayList<Value>();
		Value rsc;
		for (int i = startIndex; i <= endIndex; i++)
		{
			rsc = this.get(i);
			if (rsc != null)
			{
				rscs.add(rsc);
			}
		}
		return rscs;
	}

	public List<String> getResourceKeys(int startIndex, int endIndex)
	{
		List<String> rscs = new ArrayList<String>();
		Value rsc;
		for (int i = startIndex; i <= endIndex; i++)
		{
			rsc = this.get(i);
			if (rsc != null)
			{
				rscs.add(rsc.getKey());
			}
		}
		return rscs;
	}

	public List<Value> getTop(int endIndex)
	{
		List<Value> rscs = new ArrayList<Value>();
		Value rsc;
		for (int i = 0; i <= endIndex; i++)
		{
			rsc = this.get(i);
			if (rsc != null)
			{
				rscs.add(rsc);
			}
		}
		return rscs;
	}

	/*
	 * It is not reasonable to get all of the data from a cache. 08/21/2018, Bing Li
	 */
	/*
	public List<Value> get()
	{
		List<Value> rscs = new ArrayList<Value>();
		String key;
		this.lock.readLock().lock();
		try
		{
			int index = this.keys.getSize() - 1;
			for (int i = 0; i <= index; i++)
			{
				key = this.keys.get(i);
				if (key != null)
				{
					rscs.add(super.get(key));
				}
			}
			return rscs;
		}
		finally
		{
			this.lock.readLock().unlock();
		}
	}
	*/

	public List<String> getResourceKeys()
	{
		List<String> rscs = new ArrayList<String>();
		String key;
		List<String> keys = new ArrayList<String>();
		this.lock.readLock().lock();
		try
		{
			int index = this.keys.getSize() - 1;
			for (int i = 0; i <= index; i++)
			{
				key = this.keys.get(i);
				if (key != null)
				{
					keys.add(key);
				}
			}
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		for (String entry : keys)
		{
			rscs.add(super.getAtBase(entry).getKey());
		}
		return rscs;
	}

	public List<String> getResourceKeys(int endIndex)
	{
		List<String> rscs = new ArrayList<String>();
		String key;
		List<String> keys = new ArrayList<String>();
		this.lock.readLock().lock();
		try
		{
			for (int i = 0; i <= endIndex; i++)
			{
				key = this.keys.get(i);
				if (key != null)
				{
//					rscs.add(super.get(key).getKey());
					keys.add(key);
				}
			}
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		for (String entry : keys)
		{
			rscs.add(super.getAtBase(entry).getKey());
		}
		return rscs;
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

	public void remove(int index)
	{
		String key = UtilConfig.NO_KEY;
		this.lock.readLock().lock();
		if (this.keys.containsKey(index))
		{
			key = this.keys.get(index);

			this.lock.readLock().unlock();
			this.lock.writeLock().lock();
			this.db.remove(key);
			this.keys.remove(index);
			this.times.remove(key);
			this.lock.readLock().lock();
			this.lock.writeLock().unlock();
		}
		this.lock.readLock().unlock();
		if (!key.equals(UtilConfig.NO_KEY))
		{
			super.removeAtBase(key);
		}
	}

	public void remove(int startIndex, int endIndex)
	{
		for (int i = startIndex; i <= endIndex; i++)
		{
			this.remove(i);
		}
	}

	public void clear()
	{
		String key;
		Set<String> removalKeys = Sets.newHashSet();
		this.lock.readLock().lock();
		Set<Integer> keys = this.keys.getKeys();
		for (Integer index : keys)
		{
			key = this.keys.get(index);
			
			this.lock.readLock().unlock();
			this.lock.writeLock().lock();
			this.db.remove(key);
			this.keys.remove(index);
			this.times.remove(key);
			removalKeys.add(key);
			this.lock.readLock().lock();
			this.lock.writeLock().unlock();
		}
		this.lock.readLock().unlock();
		super.removeAllAtBase(removalKeys);
	}

	@Override
	public void evict(String k, Value v) throws TerminalServerOverflowedException
	{
		this.lock.writeLock().lock();
		this.db.save(v);
		this.lock.writeLock().unlock();
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
