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
 * The locking is updated for the current version. 08/21/2018, Bing Li
 */

/*
 *  The class is useful. It should be retained. 06/02/2018, Bing Li
 *  
 */

// Created: 01/28/2018, Bing Li
class TimingMap<Resource extends Timing, Factory extends CacheMapFactorable<String, Resource>, DescendantComp extends Comparator<Resource>> extends RootCache<String, Resource, Factory, StringKeyDB>
{
	private TimingListDB times;
	private ListKeyDB keys;
	private Date currentOldestTime;
	private DescendantComp comp;
	private ReentrantReadWriteLock lock;

	public TimingMap(TimingMapBuilder<Resource, Factory, DescendantComp> builder)
	{
		super(builder.getFactory(), builder.getRootPath(), builder.getCacheKey(), builder.getCacheSize(), builder.getOffheapSizeInMB(), builder.getDiskSizeInMB(), StringKeyDBPool.SHARED().getDB(CacheConfig.getMapCacheKeyDBPath(builder.getRootPath(), builder.getCacheKey())), true);
		
		this.times = new TimingListDB(CacheConfig.getTimingListTimesCachePath(builder.getRootPath(), builder.getCacheKey()));
		this.keys = new ListKeyDB(CacheConfig.getListKeysCachePath(builder.getRootPath(), builder.getCacheKey()));

		this.comp = builder.getComp();
		this.currentOldestTime = Time.INIT_TIME;
		this.lock = new ReentrantReadWriteLock();
	}
	
	public static class TimingMapBuilder<Resource extends Timing, Factory extends CacheMapFactorable<String, Resource>, DescendantComp extends Comparator<Resource>> implements Builder<TimingMap<Resource, Factory, DescendantComp>>
	{
		private Factory factory;
		private String rootPath;
		private String cacheKey;
//		private int cacheSize;
		private long cacheSize;
		private int offheapSizeInMB;
		private int diskSizeInMB;
		private DescendantComp comp;

		public TimingMapBuilder()
		{
		}

		public TimingMapBuilder<Resource, Factory, DescendantComp> factory(Factory factory)
		{
			this.factory = factory;
			return this;
		}
		
		public TimingMapBuilder<Resource, Factory, DescendantComp> rootPath(String rootPath)
		{
			this.rootPath = rootPath;
			return this;
		}

		public TimingMapBuilder<Resource, Factory, DescendantComp> cacheKey(String cacheKey)
		{
			this.cacheKey = cacheKey;
			return this;
		}
		
		public TimingMapBuilder<Resource, Factory, DescendantComp> cacheSize(long cacheSize)
		{
			this.cacheSize = cacheSize;
			return this;
		}
		
		public TimingMapBuilder<Resource, Factory, DescendantComp> offheapSizeInMB(int offheapSizeInMB)
		{
			this.offheapSizeInMB = offheapSizeInMB;
			return this;
		}
		
		public TimingMapBuilder<Resource, Factory, DescendantComp> diskSizeInMB(int diskSizeInMB)
		{
			this.diskSizeInMB = diskSizeInMB;
			return this;
		}
		
		public TimingMapBuilder<Resource, Factory, DescendantComp> comp(DescendantComp comp)
		{
			this.comp = comp;
			return this;
		}

		@Override
		public TimingMap<Resource, Factory, DescendantComp> build()
		{
			return new TimingMap<Resource, Factory, DescendantComp>(this);
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

	public void close() throws InterruptedException
	{
		super.closeAtBase();

		this.lock.writeLock().lock();
		this.times.close();
		this.keys.close();
		this.lock.writeLock().unlock();
		this.lock = null;
	}

	public void put(Resource rsc)
	{
		if (this.getSize() <= 0)
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
	
	public void put(List<Resource> rscs)
	{
		if (rscs.size() > 0)
		{
			Collections.sort(rscs, this.comp);
			for (Resource rsc : rscs)
			{
				super.putAtBase(rsc.getKey(), rsc);
			}

			this.lock.readLock().lock();
			if (rscs.get(0).getTime().after(this.currentOldestTime))
			{
				int index = this.keys.getSize();
				for (Resource rsc : rscs)
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
				for (Resource rsc : rscs)
				{
					allTimes.put(rsc.getKey(), rsc.getTime());
//					super.put(rsc.getKey(), rsc);
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
		this.lock.readLock().lock();
		try
		{
			return this.keys.getSize() <= 0;
		}
		finally
		{
			this.lock.readLock().unlock();
		}
	}

	public Resource get(int index)
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
	
	public List<Resource> get(int startIndex, int endIndex)
	{
		List<Resource> rscs = new ArrayList<Resource>();
		Resource rsc;
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
		Resource rsc;
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

	public List<Resource> getTop(int endIndex)
	{
		List<Resource> rscs = new ArrayList<Resource>();
		Resource rsc;
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
	public List<Resource> get()
	{
		List<Resource> rscs = new ArrayList<Resource>();
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
					rscs.add(this.get(key));
				}
				else
				{
					break;
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
		this.lock.readLock().lock();
		try
		{
			int index = this.keys.getSize() - 1;
			for (int i = 0; i <= index; i++)
			{
				key = this.keys.get(i);
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
		finally
		{
			this.lock.readLock().unlock();
		}
	}
	
	public List<String> getResourceKeys(int endIndex)
	{
		List<String> rscs = new ArrayList<String>();
		String key;
		this.lock.readLock().lock();
		try
		{
			for (int i = 0; i <= endIndex; i++)
			{
				key = this.keys.get(i);
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
		finally
		{
			this.lock.readLock().unlock();
		}
	}

	public int getSize()
	{
		this.lock.readLock().lock();
		try
		{
			return this.keys.getSize();
		}
		finally
		{
			this.lock.readLock().unlock();
		}
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
//			super.remove(key);
			
			this.lock.readLock().lock();
			this.lock.writeLock().unlock();
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
		Set<String> removedKeys = Sets.newHashSet();
		this.lock.readLock().lock();
		Set<Integer> keys = this.keys.getKeys();
		for (Integer index : keys)
		{
			key = this.keys.get(index);
			removedKeys.add(key);

			this.lock.readLock().unlock();
			this.lock.writeLock().lock();
			
			this.keys.remove(index);
			this.times.remove(key);
//			super.remove(key);
			
			this.lock.readLock().lock();
			this.lock.writeLock().unlock();
		}
		this.lock.readLock().unlock();
		super.removeAllAtBase(removedKeys);
	}

	@Override
	public void evict(String k, Resource v) throws TerminalServerOverflowedException
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void forward(String k, Resource v)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void remove(String k, Resource v)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void expire(String k, Resource v)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update(String k, Resource v)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void shutdown() throws InterruptedException
	{
		// TODO Auto-generated method stub
		
	}
}
