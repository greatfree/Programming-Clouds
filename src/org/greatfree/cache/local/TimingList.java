package org.greatfree.cache.local;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.greatfree.cache.CacheConfig;
import org.greatfree.cache.db.ListKeyDB;
import org.greatfree.cache.db.StringKeyDB;
import org.greatfree.cache.db.StringKeyDBPool;
import org.greatfree.cache.db.TimingListDB;
import org.greatfree.util.Builder;
import org.greatfree.util.CollectionSorter;
import org.greatfree.util.Time;
import org.greatfree.util.Timing;
import org.greatfree.util.UtilConfig;

/*
 * It is replaced by the Pointing-Based caches. 08/22/2018, Bing Li
 */

/*
 * The locking is updated for the current version. 08/21/2018, Bing Li
 */

/*
 * The class does NOT keep the consistency between the Cache and the StringKeyDB after evicting. But the class has a counterpart, PointingTerminalList, which saves evicted data into a DB. Then, the previous inconsistency issue is solved. 05/30/2018, Bing Li
 */

/*
 * In the case, since it is a heavy burden to deal with the issue of updating the indexes when a resource is evicted since the key should be updated for half the total size of the cache on average, I decide to give it up. Thus, some keys do not have values. For a local cache, the return value for such a key is null. I will design a distributed cache for each type. Then, the null value can be retrieved from the backend server. In this system for the WWW, it is not a problem since the data updates frequently and the amount is large. Missing some data is not a problem. At least, important data is retained. With the distributed cache's support, the missed data issue can be resolved. 04/28/2018, Bing Li
 * 
 */

// Created: 05/26/2017, Bing Li
class TimingList<Value extends Timing, Factory extends CacheMapFactorable<String, Value>, DescendantComp extends Comparator<Value>> extends RootCache<String, Value, Factory, StringKeyDB>
{
	private TimingListDB times;
	private ListKeyDB keys;
	private Date currentOldestTime;
	private DescendantComp comp;
	private ReentrantReadWriteLock lock;

	public TimingList(TimingPersistableListBuilder<Value, Factory, DescendantComp> builder)
	{
		// Since when processing evicted data, the DB needs to be accessed. So the eventing should be asynchronous. 05/13/2018, Bing Li
		super(builder.getFactory(), builder.getRootPath(), builder.getCacheKey(), builder.getCacheSize(), builder.getOffheapSizeInMB(), builder.getDiskSizeInMB(), StringKeyDBPool.SHARED().getDB(CacheConfig.getMapCacheKeyDBPath(builder.getRootPath(), builder.getCacheKey())), true);
		
		this.times = new TimingListDB(CacheConfig.getTimingListTimesCachePath(builder.getRootPath(), builder.getCacheKey()));

		this.keys = new ListKeyDB(CacheConfig.getListKeysCachePath(builder.getRootPath(), builder.getCacheKey()));

		this.comp = builder.getComp();
		this.currentOldestTime = Time.INIT_TIME;
		this.lock = new ReentrantReadWriteLock();
	}
	
	public static class TimingPersistableListBuilder<Value extends Timing, Factory extends CacheMapFactorable<String, Value>, DescendantComp extends Comparator<Value>> implements Builder<TimingList<Value, Factory, DescendantComp>>
	{
		private Factory factory;
		private String rootPath;
		private String cacheKey;
//		private int cacheSize;
		private long cacheSize;
		private int offheapSizeInMB;
		private int diskSizeInMB;
		private DescendantComp comp;

		public TimingPersistableListBuilder()
		{
		}

		public TimingPersistableListBuilder<Value, Factory, DescendantComp> factory(Factory factory)
		{
			this.factory = factory;
			return this;
		}
		
		public TimingPersistableListBuilder<Value, Factory, DescendantComp> rootPath(String rootPath)
		{
			this.rootPath = rootPath;
			return this;
		}

		public TimingPersistableListBuilder<Value, Factory, DescendantComp> cacheKey(String cacheKey)
		{
			this.cacheKey = cacheKey;
			return this;
		}
		
		public TimingPersistableListBuilder<Value, Factory, DescendantComp> cacheSize(long cacheSize)
		{
			this.cacheSize = cacheSize;
			return this;
		}
		
		public TimingPersistableListBuilder<Value, Factory, DescendantComp> offheapSizeInMB(int offheapSizeInMB)
		{
			this.offheapSizeInMB = offheapSizeInMB;
			return this;
		}
		
		public TimingPersistableListBuilder<Value, Factory, DescendantComp> diskSizeInMB(int diskSizeInMB)
		{
			this.diskSizeInMB = diskSizeInMB;
			return this;
		}
		
		public TimingPersistableListBuilder<Value, Factory, DescendantComp> comp(DescendantComp comp)
		{
			this.comp = comp;
			return this;
		}

		@Override
		public TimingList<Value, Factory, DescendantComp> build()
		{
			return new TimingList<Value, Factory, DescendantComp>(this);
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
	}

	public void shutdown() throws InterruptedException
	{
		super.closeAtBase();
		this.lock.writeLock().lock();
		this.times.close();
		this.keys.close();
		this.lock.writeLock().unlock();
	}
	
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
	
	public long getLeftSize(int currentIndex)
	{
		/*
		this.lock.readLock().lock();
		try
		{
			return super.getLeftSize(currentIndex);
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		*/
		return super.getLeftSizeAtBase(currentIndex);
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

				this.lock.readLock().unlock();
				
				for (Value rsc : rscs)
				{
//					super.put(rsc.getKey(), rsc);
					this.lock.readLock().unlock();
					this.lock.writeLock().lock();
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
		this.lock.readLock().lock();
		String key = this.keys.get(index);
		this.lock.readLock().unlock();
		if (key != null)
		{
			return super.getAtBase(key);
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
			else
			{
				break;
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
			else
			{
				break;
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
			else
			{
				break;
			}
		}
		return rscs;
	}

	/*
	 * It is not reasonable to load all of the data from a cache. 08/20/2018, Bing Li
	 */
	/*
	public List<Value> get()
	{
		List<Value> rscs = new ArrayList<Value>();
		String key;
		this.lock.readLock().lock();
		int index = this.keys.getSize() - 1;
		this.lock.readLock().unlock();
		
		for (int i = 0; i <= index; i++)
		{
			this.lock.readLock().lock();
			key = this.keys.get(i);
			this.lock.readLock().unlock();
			if (key != null)
			{
				rscs.add(super.get(key));
			}
			else
			{
				break;
			}
		}
		return rscs;
	}
	*/

	public List<String> getResourceKeys()
	{
		List<String> rscs = new ArrayList<String>();
		String key;
		this.lock.readLock().lock();
		int index = this.keys.getSize() - 1;
		this.lock.readLock().unlock();
		
		for (int i = 0; i <= index; i++)
		{
			this.lock.readLock().lock();
			key = this.keys.get(i);
			this.lock.readLock().unlock();
			if (key != null)
			{
				rscs.add(super.getAtBase(key).getKey());
			}
			else
			{
				break;
			}
		}
		return rscs;
	}
	
	public List<String> getResourceKeys(int endIndex)
	{
		List<String> rscs = new ArrayList<String>();
		String key;
		for (int i = 0; i <= endIndex; i++)
		{
			this.lock.readLock().lock();
			key = this.keys.get(i);
			this.lock.readLock().unlock();
			if (key != null)
			{
				rscs.add(super.getAtBase(key).getKey());
			}
			else
			{
				break;
			}
		}
		return rscs;
	}

	public long getMemCacheSize()
	{
		/*
		this.lock.readLock().lock();
		try
		{
//			return this.keys.getSize();
//			return this.times.getSize();
			return super.getMemCacheSize();
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		*/
		return super.getMemCacheSizeAtBase();
	}
	
	public void remove(int index)
	{
		String key = UtilConfig.EMPTY_STRING;
		this.lock.readLock().lock();
		if (this.keys.containsKey(index))
		{
			key = this.keys.get(index);

			this.lock.readLock().unlock();
			this.lock.writeLock().lock();
			this.keys.remove(index);
			this.times.remove(key);
			this.lock.readLock().lock();
			this.lock.writeLock().unlock();

//			super.remove(key);
		}
		this.lock.readLock().unlock();
		if (!key.equals(UtilConfig.EMPTY_STRING))
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
		this.lock.readLock().lock();
		Set<Integer> keys = this.keys.getKeys();
		this.lock.readLock().unlock();
		
		for (Integer index : keys)
		{
			this.lock.readLock().lock();
			key = this.keys.get(index);
			this.lock.readLock().unlock();

			this.lock.writeLock().lock();
			this.keys.remove(index);
			this.times.remove(key);
			this.lock.writeLock().unlock();
			
			super.removeAtBase(key);
		}
	}

	@Override
	public void evict(String k, Value v)
	{
		super.removeAtBase(k);
		this.lock.writeLock().lock();
		this.times.remove(k);
		this.lock.writeLock().unlock();
		// The load to check the key of ListKeyDB is high. So I ignore it. 05/12/2018, Bing Li
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
