package org.greatfree.cache.local;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.greatfree.cache.CacheConfig;
import org.greatfree.cache.db.ListKeyDB;
import org.greatfree.cache.db.SortedListDB;
import org.greatfree.cache.db.StringKeyDB;
import org.greatfree.cache.db.StringKeyDBPool;
import org.greatfree.util.Builder;
import org.greatfree.util.CollectionSorter;
import org.greatfree.util.Pointing;
import org.greatfree.util.UtilConfig;

/*
 * The list cache that sorts only a predefined count of data is implemented. When data is saved in the list, each data should have a unique key. It must employ the constructor Pointing(long points) of Pointing. 02/15/2019, Bing Li
 */

/*
 * The locking is updated for the current version. 08/21/2018, Bing Li
 */

/*
 * The class is useful. It should be retained. 06/02/2018, Bing Li
 * 
 */

// Created: 01/27/2018, Bing Li
public class SortedMap<Value extends Pointing, Factory extends CacheMapFactorable<String, Value>, DescendantComp extends Comparator<Value>> extends RootCache<String, Value, Factory, StringKeyDB>
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
//	private ReentrantReadWriteLock lock;

	private final int sortSize;

	public SortedMap(SortedMapBuilder<Value, Factory, DescendantComp> builder)
	{
		super(builder.getFactory(), builder.getRootPath(), builder.getCacheKey(), builder.getCacheSize(), builder.getOffheapSizeInMB(), builder.getDiskSizeInMB(), StringKeyDBPool.SHARED().getDB(CacheConfig.getMapCacheKeyDBPath(builder.getRootPath(), builder.getCacheKey())), false);

		this.sortedPointsDB = new SortedListDB(CacheConfig.getPointingListPointsCachePath(builder.getRootPath(), builder.getCacheKey()));
		this.sortedPoints = this.sortedPointsDB.getValues();

		this.sortedKeysDB = new ListKeyDB(CacheConfig.getListKeysCachePath(builder.getRootPath(), builder.getCacheKey()));
		this.sortedKeys = this.sortedKeysDB.getAllKeys();
		
		this.obsoleteKeysDB = new ListKeyDB(CacheConfig.getObsoleteListKeysCachePath(builder.getRootPath(), builder.getCacheKey()));
		this.obsoleteKeys = this.obsoleteKeysDB.getAllKeys();

		this.comp = builder.getComp();
//		this.currentMinPoints = UtilConfig.ZERO;
//		this.lock = new ReentrantReadWriteLock();
		this.sortSize = builder.getSortSize();
	}
	
	public static class SortedMapBuilder<Resource extends Pointing, Factory extends CacheMapFactorable<String, Resource>, DescendantComp extends Comparator<Resource>> implements Builder<SortedMap<Resource, Factory, DescendantComp>>
	{
		private Factory factory;
		private String rootPath;
		private String cacheKey;
//		private int cacheSize;
		private long cacheSize;
		private int offheapSizeInMB;
		private int diskSizeInMB;
		
		private DescendantComp comp;
		private int sortSize;

		public SortedMapBuilder()
		{
		}

		public SortedMapBuilder<Resource, Factory, DescendantComp> factory(Factory factory)
		{
			this.factory = factory;
			return this;
		}
		
		public SortedMapBuilder<Resource, Factory, DescendantComp> rootPath(String rootPath)
		{
			this.rootPath = rootPath;
			return this;
		}

		public SortedMapBuilder<Resource, Factory, DescendantComp> cacheKey(String cacheKey)
		{
			this.cacheKey = cacheKey;
			return this;
		}
		
		public SortedMapBuilder<Resource, Factory, DescendantComp> cacheSize(long cacheSize)
		{
			this.cacheSize = cacheSize;
			return this;
		}
		
		public SortedMapBuilder<Resource, Factory, DescendantComp> offheapSizeInMB(int offheapSizeInMB)
		{
			this.offheapSizeInMB = offheapSizeInMB;
			return this;
		}
		
		public SortedMapBuilder<Resource, Factory, DescendantComp> diskSizeInMB(int diskSizeInMB)
		{
			this.diskSizeInMB = diskSizeInMB;
			return this;
		}
		
		public SortedMapBuilder<Resource, Factory, DescendantComp> comp(DescendantComp comp)
		{
			this.comp = comp;
			return this;
		}
		
		public SortedMapBuilder<Resource, Factory, DescendantComp> sortSize(int sortSize)
		{
			this.sortSize = sortSize;
			return this;
		}

		@Override
		public SortedMap<Resource, Factory, DescendantComp> build()
		{
			return new SortedMap<Resource, Factory, DescendantComp>(this);
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
		
		public int getSortSize()
		{
			return this.sortSize;
		}
	}

	public void shutdown() throws InterruptedException
	{
		super.closeAtBase();
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
//		this.lock = null;
	}
	
	public void put(Value rsc)
	{
//		System.out.println("----> key = " + rsc.getKey() + "), value = " + rsc.getPoints());
		super.putAtBase(rsc.getKey(), rsc);
//		this.lock.readLock().lock();
//		if (this.sortedKeysDB.getSize() <= 0)
		if (this.sortedKeys.size() <= 0)
		{
//			this.lock.readLock().unlock();
//			this.lock.writeLock().lock();
//			this.sortedPointsDB.put(rsc.getKey(), rsc.getPoints());
			this.sortedPoints.put(rsc.getKey(), rsc.getPoints());
//			this.sortedKeysDB.put(0, rsc.getKey());
			this.sortedKeys.put(0, rsc.getKey());
//			this.currentMinPoints = rsc.getPoints();
//			this.lock.readLock().lock();
//			this.lock.writeLock().unlock();
		}
		else
		{
//			super.put(rsc.getKey(), rsc);
//			Map<String, Float> allPoints = this.sortedPointsDB.getValues();
			this.sortedPoints.put(rsc.getKey(), rsc.getPoints());
			Map<String, Float> sp = CollectionSorter.sortDescendantByValue(this.sortedPoints);
//			int obsSize = this.obsoleteKeysDB.getSize();
			int obsSize = this.obsoleteKeys.size();
			
			int index = 0;
//			Set<String> removedKeys = Sets.newHashSet();
			Set<String> removedKeys = new HashSet<String>();
//			this.lock.readLock().unlock();
//			this.lock.writeLock().lock();
			for (Map.Entry<String, Float> entry : sp.entrySet())
			{
				if (index < this.sortSize)
				{
//					this.sortedKeysDB.put(index++, entry.getKey());
					this.sortedKeys.put(index++, entry.getKey());
				}
				else
				{
//					this.obsoleteKeysDB.put(obsSize++, entry.getKey());
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
//				this.currentMinPoints = allPoints.get(this.sortedKeys.get(index - 1));
//			this.lock.readLock().lock();
//			this.lock.writeLock().unlock();
		}
//		this.lock.readLock().unlock();
	}

	public void putAll(List<Value> rscs)
	{
		Collections.sort(rscs, this.comp);
		for (Value rsc : rscs)
		{
			super.putAtBase(rsc.getKey(), rsc);
		}
//		this.lock.readLock().lock();
//		Map<String, Float> allPoints = this.sortedPointsDB.getValues();
//		int obsSize = this.obsoleteKeysDB.getSize();
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
//				this.sortedKeysDB.put(index++, entry.getKey());
				this.sortedKeys.put(index++, entry.getKey());
//				this.lock.writeLock().unlock();
			}
			else
			{
//				this.lock.writeLock().lock();
//				this.obsoleteKeysDB.put(obsSize++, entry.getKey());
				this.obsoleteKeys.put(obsSize++, entry.getKey());
//				this.lock.writeLock().unlock();
				removedKeys.add(entry.getKey());
			}
		}

		for (String entry : removedKeys)
		{
			this.sortedPoints.remove(entry);
//			this.lock.writeLock().lock();
//			this.sortedPointsDB.remove(entry);
//			this.lock.writeLock().unlock();
		}
		/*
		this.lock.writeLock().lock();
		this.sortedPointsDB.putAll(allPoints);
		this.lock.writeLock().unlock();
		*/
	}

	public boolean isEmpty()
	{
		/*
		this.lock.readLock().lock();
		try
		{
//			return this.sortedKeysDB.getSize() <= 0;
			return this.sortedKeys.size() <= 0;
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		*/
		return this.sortedKeys.size() <= 0;
	}
	
	public Value get(int index)
	{
		String key;
//		this.lock.readLock().lock();
		if (index < this.sortSize)
		{
			key = this.sortedKeys.get(index);
		}
		else
		{
			key = this.obsoleteKeys.get(index - this.sortSize);
		}
//		this.lock.readLock().unlock();
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

	/*
	 * The method returns the keys that is sorted. In practice, for a cache, it is not necessary to return all of the keys, especially for a list. 02/14/2019, Bing Li
	 */
	public List<String> getResourceKeys()
	{
		List<String> rscs = new ArrayList<String>();
		String key;
//		this.lock.readLock().lock();
		int index = this.sortedKeys.size() - 1;
//		this.lock.readLock().unlock();
		
		for (int i = 0; i <= index; i++)
		{
//			this.lock.readLock().lock();
			key = this.sortedKeys.get(i);
//			this.lock.readLock().unlock();

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
		if (endIndex < this.sortSize)
		{
			for (int i = 0; i <= endIndex; i++)
			{
//				this.lock.readLock().lock();
				key = this.sortedKeys.get(i);
//				this.lock.readLock().unlock();
				if (key != null)
				{
					rscs.add(super.getAtBase(key).getKey());
				}
				else
				{
					break;
				}
			}
		}
		else
		{
			for (int i = 0; i < this.sortSize; i++)
			{
//				this.lock.readLock().lock();
				key = this.sortedKeys.get(i);
//				this.lock.readLock().unlock();
				
				if (key != null)
				{
					rscs.add(super.getAtBase(key).getKey());
				}
				else
				{
					break;
				}
			}

			for (int i = 0; i <= endIndex - this.sortSize; i++)
			{
//				this.lock.readLock().lock();
				key = this.obsoleteKeys.get(i);
//				this.lock.readLock().unlock();
				
				if (key != null)
				{
					rscs.add(super.getAtBase(key).getKey());
				}
				else
				{
					break;
				}
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
			return this.sortedKeys.getSize();
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
		String key = UtilConfig.EMPTY_STRING;
//		this.lock.readLock().lock();
		if (this.sortedKeys.containsKey(index))
		{
			key = this.sortedKeys.get(index);
			
//			this.lock.readLock().unlock();
//			this.lock.writeLock().lock();
			this.sortedKeys.remove(index);
			this.sortedPoints.remove(key);
//			this.lock.readLock().lock();
//			this.lock.writeLock().unlock();

//			super.remove(key);
		}
		else
		{
//			this.lock.readLock().unlock();
//			this.lock.writeLock().lock();
//			this.sortedKeys.remove(index);
			this.obsoleteKeys.remove(index);
//			this.lock.readLock().lock();
//			this.lock.writeLock().unlock();
		}
//		this.lock.readLock().unlock();
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
//		this.lock.readLock().lock();
		Set<Integer> keys = this.sortedKeys.keySet();
//		this.lock.readLock().unlock();
		
		for (Integer index : keys)
		{
//			this.lock.readLock().lock();
			key = this.sortedKeys.get(index);
//			this.lock.readLock().unlock();

//			this.lock.writeLock().lock();
			this.sortedKeys.remove(index);
			this.sortedPoints.remove(key);
//			this.lock.writeLock().unlock();

			super.removeAtBase(key);
		}

//		this.lock.readLock().lock();
		keys = this.obsoleteKeys.keySet();
//		this.lock.readLock().unlock();
		
		for (Integer index : keys)
		{
//			this.lock.readLock().lock();
			key = this.obsoleteKeys.get(index);
//			this.lock.readLock().unlock();

//			this.lock.writeLock().lock();
			this.sortedKeys.remove(index);
//			this.lock.writeLock().unlock();

			super.removeAtBase(key);
		}
	}

	@Override
	public void evict(String k, Value v)
	{
		super.removeAtBase(k);
//		this.lock.writeLock().lock();
		this.sortedPoints.remove(k);
//		this.lock.writeLock().unlock();
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
