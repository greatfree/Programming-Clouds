package org.greatfree.cache.local;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.greatfree.cache.CacheConfig;
import org.greatfree.cache.db.ListKeyDB;
import org.greatfree.cache.db.SortedListDB;
import org.greatfree.cache.db.StringKeyDB;
import org.greatfree.cache.db.StringKeyDBPool;
import org.greatfree.util.Builder;
import org.greatfree.util.CollectionSorter;
import org.greatfree.util.Pointing;
import org.greatfree.util.UtilConfig;

import com.google.common.collect.Sets;

/*
 * The list cache that sorts only a predefined count of data is implemented. When data is saved in the list, each data should have a unique key. It must employ the constructor Pointing(long points) of Pointing. 02/15/2019, Bing Li
 */

/*
 * 
I need to update the cache again, especially for the lists which have the sorting feature.

First, the sorting should be performed on a predefined number of elements rather than all of the data in the caching.

Since the size of the distributed caches is unlimited, it is not reasonable to sort in memory for all of the data because of the performance and the limited size of main memory.

In practice, a user can hardly retrieve all of the data in the cache.

Even though some scarcely accessed data are loaded from disk, it can be sorted again.

So it is required to predefine the count of data entries to be sorted.

It is also necessary to implement a list that is not needed to sort when data is added. In the list, the order is determined by the added order.

The list is used for saving data sorted by timing.

The above updates are used for raising the performance.

 */

/*
 * The locking is updated for the current version. 08/21/2018, Bing Li
 */

/*
 * The class does NOT keep the consistency between the Cache and the StringKeyDB after evicting. But the class has a counterpart, PointingTerminalList, which saves evicted data into a DB. Then, the previous inconsistency issue is solved. 05/30/2018, Bing Li
 * 
 * 05/30/2018, Bing Li
 */

/*
 * In the case, since it is a heavy burden to deal with the issue of updating the indexes when a resource is evicted since the key should be updated for half the total size of the cache on average, I decide to give it up. Thus, some keys do not have values. For a local cache, the return value for such a key is null. I will design a distributed cache for each type. Then, the null value can be retrieved from the backend server. In this system for the WWW, it is not a problem since the data updates frequently and the amount is large. Missing some data is not a problem. At least, important data is retained. With the distributed cache's support, the missed data issue can be resolved. 04/28/2018, Bing Li
 * 
 */

// Created: 05/25/2017, Bing Li
public class SortedList<Value extends Pointing, Factory extends CacheMapFactorable<String, Value>, DescendantComp extends Comparator<Value>> extends RootCache<String, Value, Factory, StringKeyDB>
{
	/*
	 * Maybe the DB can be replaced by Enhanced Cache to raise the performance? I will consider the issue later when upgrading. Now I hope I could finish the fundamental application ASAP. 02/14/2019, Bing Li
	 */
	private SortedListDB sortedPointsDB;
	private Map<String, Float> sortedPoints;
	
	private ListKeyDB sortedKeysDB;
	private Map<Integer, String> sortedKeys;
	
	// The DB saves the keys that are not necessary to be sorted. 02/14/2019, Bing Li
	private ListKeyDB obsoleteKeysDB;
	private Map<Integer, String> obsoleteKeys;
	
	private AtomicInteger currentMinPoints;
	private DescendantComp comp;
//	private ReentrantReadWriteLock lock;
	
	private final int sortSize;

	public SortedList(SortedListBuilder<Value, Factory, DescendantComp> builder)
	{
		// Since when processing evicted data, the DB needs to be accessed. So the eventing should be asynchronous. 05/13/2018, Bing Li
		super(builder.getFactory(), builder.getRootPath(), builder.getCacheKey(), builder.getCacheSize(), builder.getOffheapSizeInMB(), builder.getDiskSizeInMB(), StringKeyDBPool.SHARED().getDB(CacheConfig.getMapCacheKeyDBPath(builder.getRootPath(), builder.getCacheKey())), true);

		this.sortedPointsDB = new SortedListDB(CacheConfig.getPointingListPointsCachePath(builder.getRootPath(), builder.getCacheKey()));
		this.sortedPoints = this.sortedPointsDB.getValues();
		
		this.sortedKeysDB = new ListKeyDB(CacheConfig.getListKeysCachePath(builder.getRootPath(), builder.getCacheKey()));
		this.sortedKeys = this.sortedKeysDB.getAllKeys();
		
		this.obsoleteKeysDB = new ListKeyDB(CacheConfig.getObsoleteListKeysCachePath(builder.getRootPath(), builder.getCacheKey()));
		this.obsoleteKeys = this.obsoleteKeysDB.getAllKeys();
		
		this.comp = builder.getComp();
		this.currentMinPoints = new AtomicInteger(UtilConfig.ZERO);
//		this.lock = new ReentrantReadWriteLock();
		
		this.sortSize = builder.getSortSize();
	}
	
	public static class SortedListBuilder<Value extends Pointing, Factory extends CacheMapFactorable<String, Value>, DescendantComp extends Comparator<Value>> implements Builder<SortedList<Value, Factory, DescendantComp>>
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

		public SortedListBuilder()
		{
		}

		public SortedListBuilder<Value, Factory, DescendantComp> factory(Factory factory)
		{
			this.factory = factory;
			return this;
		}
		
		public SortedListBuilder<Value, Factory, DescendantComp> rootPath(String rootPath)
		{
			this.rootPath = rootPath;
			return this;
		}

		public SortedListBuilder<Value, Factory, DescendantComp> cacheKey(String cacheKey)
		{
			this.cacheKey = cacheKey;
			return this;
		}
		
		public SortedListBuilder<Value, Factory, DescendantComp> cacheSize(long cacheSize)
		{
			this.cacheSize = cacheSize;
			return this;
		}
		
		public SortedListBuilder<Value, Factory, DescendantComp> offheapSizeInMB(int offheapSizeInMB)
		{
			this.offheapSizeInMB = offheapSizeInMB;
			return this;
		}
		
		public SortedListBuilder<Value, Factory, DescendantComp> diskSizeInMB(int diskSizeInMB)
		{
			this.diskSizeInMB = diskSizeInMB;
			return this;
		}
		
		public SortedListBuilder<Value, Factory, DescendantComp> comp(DescendantComp comp)
		{
			this.comp = comp;
			return this;
		}
		
		public SortedListBuilder<Value, Factory, DescendantComp> sortSize(int sortSize)
		{
			this.sortSize = sortSize;
			return this;
		}

		@Override
		public SortedList<Value, Factory, DescendantComp> build()
		{
			return new SortedList<Value, Factory, DescendantComp>(this);
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
		this.sortedKeysDB.removeAll();
		this.sortedKeysDB.putAll(this.sortedKeys);
		this.sortedPointsDB.close();

		this.sortedKeysDB.removeAll();
		this.sortedKeysDB.putAll(this.sortedKeys);
		this.sortedKeysDB.close();

		this.obsoleteKeysDB.removeAll();
		this.obsoleteKeysDB.putAll(this.obsoleteKeys);
		this.obsoleteKeysDB.close();
//		this.lock.writeLock().unlock();
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
	
	/*
	 * For testing only. 02/14/2019, Bing Li
	 */
	/*
	public Set<Integer> getObsKeys()
	{
		return this.obsoleteKeys.getKeys();
	}
	*/
	
	/*
	 * For testing only. 02/14/2019, Bing Li
	 */
	/*
	public String getKey(int index)
	{
		return this.obsoleteKeys.get(index);
	}
	*/
	
	/*
	 * For testing only. 02/14/2019, Bing Li
	 */
	/*
	public Value get(String key)
	{
		return super.get(key);
	}
	*/

	public void add(Value rsc)
	{
//		System.out.println("\nValue to be added: " + rsc.getKey() + ", " + rsc.getPoints());
		if (super.getMemCacheSizeAtBase() <= 0)
		{
			super.putAtBase(rsc.getKey(), rsc);
//			this.lock.writeLock().lock();
//			this.sortedPointsDB.put(rsc.getKey(), rsc.getPoints());
			this.sortedPoints.put(rsc.getKey(), rsc.getPoints());
			
//			System.out.println("0) index = " + 0 + "), value = " + rsc.getPoints());
//			this.sortedKeysDB.put(0, rsc.getKey());
			this.sortedKeys.put(0, rsc.getKey());
			
//			this.obsoleteKeys.put(0, rsc.getKey());
			this.currentMinPoints.set((int)rsc.getPoints());
//			this.lock.writeLock().unlock();
		}
		else
		{
			super.putAtBase(rsc.getKey(), rsc);
//			this.lock.readLock().lock();
			if (rsc.getPoints() > this.currentMinPoints.get())
			{
				/*
				Map<String, Float> allPoints = this.sortedPointsDB.getValues();
				
				allPoints.put(rsc.getKey(), rsc.getPoints());
				allPoints = CollectionSorter.sortDescendantByValue(allPoints);
				*/
				this.sortedPoints.put(rsc.getKey(), rsc.getPoints());
				Map<String, Float> sp = CollectionSorter.sortDescendantByValue(this.sortedPoints);
				int obsSize = this.obsoleteKeys.size();

//				System.out.println("1) allPoints size = " + allPoints.size());

//				System.out.println("1) obsSize = " + obsSize);

//				this.lock.readLock().unlock();
//				this.lock.writeLock().lock();

				int index = 0;
				Set<String> removedKeys = Sets.newHashSet();
//				for (Map.Entry<String, Float> entry : allPoints.entrySet())
				for (Map.Entry<String, Float> entry : sp.entrySet())
				{
					if (index < this.sortSize)
					{
//						System.out.println("1) index = " + index + "), value = " + entry.getValue());
//						this.sortedKeysDB.put(index++, entry.getKey());
						this.sortedKeys.put(index++, entry.getKey());
					}
					else
					{
//						System.out.println("1) obsIndex = " + obsSize + "), value = " + entry.getValue());
						// The obsolete data is not so important. So just put to the end of the obsolete keys. 02/14/2019, Bing Li
//						this.obsoleteKeysDB.put(obsSize++, entry.getKey());
						this.obsoleteKeys.put(obsSize++, entry.getKey());
//						allPoints.remove(entry.getKey());
						removedKeys.add(entry.getKey());
					}
				}
				
				for (String entry : removedKeys)
				{
//					allPoints.remove(entry);
					this.sortedPoints.remove(entry);
//					this.sortedPointsDB.remove(entry);
				}
				
//				System.out.println("1.1) allPoints size = " + allPoints.size());

//				this.sortedPointsDB.putAll(allPoints);
//				this.currentMinPoints = allPoints.get(this.sortedKeysDB.get(index - 1));
				if (this.sortedKeys.size() > 0)
				{
					String minKey = this.sortedKeys.get(this.sortedKeys.size() - 1);
					if (this.sortedPoints.containsKey(minKey))
					{
						this.currentMinPoints.set(this.sortedPoints.get(minKey).intValue());
					}
				}

//				this.lock.readLock().lock();
//				this.lock.writeLock().unlock();
			}
			else
			{
//				int currentSize = this.sortedPointsDB.getSize();
//				int obsSize = this.obsoleteKeysDB.getSize();
				int currentSize = this.sortedPoints.size();
				int obsSize = this.obsoleteKeys.size();

//				System.out.println("2) obsSize = " + obsSize);

//				this.lock.readLock().unlock();
//				this.lock.writeLock().lock();
				if (currentSize < this.sortSize)
				{
//					this.sortedPointsDB.put(rsc.getKey(), rsc.getPoints());
					this.sortedPoints.put(rsc.getKey(), rsc.getPoints());
//					System.out.println("2) index = " + currentSize + "), value = " + rsc.getPoints());
//					this.sortedKeysDB.put(currentSize, rsc.getKey());
					this.sortedKeys.put(currentSize, rsc.getKey());
					this.currentMinPoints.set((int)rsc.getPoints());
				}
				else
				{
//					System.out.println("2) obsIndex = " + obsSize + "), value = " + rsc.getPoints());
//					this.obsoleteKeysDB.put(obsSize, rsc.getKey());
					this.obsoleteKeys.put(obsSize, rsc.getKey());
				}
//				this.lock.readLock().lock();
//				this.lock.writeLock().unlock();
			}
//			this.lock.readLock().unlock();
		}
	}

	public void addAll(List<Value> rscs)
	{
		if (rscs.size() > 0)
		{
			Collections.sort(rscs, this.comp);
			for (Value rsc : rscs)
			{
				super.putAtBase(rsc.getKey(), rsc);
			}
//			this.lock.readLock().lock();
			// If the below condition is fulfilled, it is unnecessary to sort. It assumes that the added data is already sorted. 02/14/2019, Bing Li
			if (rscs.get(0).getPoints() < this.currentMinPoints.get())
			{
//				int index = this.sortedKeysDB.getSize();
				int index = this.sortedKeys.size();
//				int obsSize = this.obsoleteKeysDB.getSize();
				int obsSize = this.obsoleteKeys.size();
				int toppedIndex = -1;

//				this.lock.readLock().unlock();
//				this.lock.writeLock().lock();
				for (Value rsc : rscs)
				{
					if (index < this.sortSize)
					{
//						super.put(rsc.getKey(), rsc);
//						this.sortedPointsDB.put(rsc.getKey(), rsc.getPoints());
						this.sortedPoints.put(rsc.getKey(), rsc.getPoints());
//						this.sortedKeysDB.put(index++, rsc.getKey());
						this.sortedKeys.put(index++, rsc.getKey());
						toppedIndex++;
					}
					else
					{
//						this.obsoleteKeysDB.put(obsSize++, rsc.getKey());
						this.obsoleteKeys.put(obsSize++, rsc.getKey());
					}
				}
				/*
				if (index < this.sortSize)
				{
					this.currentMinPoints = rscs.get(index - this.sortSize - 1).getPoints();
				}
				else
				{
					this.currentMinPoints = rscs.get(index - 1).getPoints();
				}
				*/
				if (toppedIndex >= 0)
				{
					this.currentMinPoints.set((int)rscs.get(toppedIndex).getPoints());
				}
//				this.lock.readLock().lock();
//				this.lock.writeLock().unlock();
			}
			else
			{
//				Map<String, Float> allPoints = this.sortedPointsDB.getValues();
//				int obsSize = this.obsoleteKeysDB.getSize();
				int obsSize = this.obsoleteKeys.size();

				// The new data is added without considering the sort size. It assumes that the size of added data is not large. 02/14/2019, Bing Li
				for (Value rsc : rscs)
				{
//						super.put(rsc.getKey(), rsc);
					this.sortedPoints.put(rsc.getKey(), rsc.getPoints());
				}
				Map<String, Float> sp = CollectionSorter.sortDescendantByValue(this.sortedPoints);

				int index = 0;
				Set<String> removedKeys = Sets.newHashSet();
//				this.lock.readLock().unlock();
//				this.lock.writeLock().lock();
				for (Map.Entry<String, Float> entry : sp.entrySet())
				{
					if (index < this.sortSize)
					{
//						this.sortedKeysDB.put(index++, entry.getKey());
						this.sortedKeys.put(index++, entry.getKey());
					}
					else
					{
//						this.obsoleteKeysDB.put(obsSize++, entry.getKey());
						this.obsoleteKeys.put(obsSize++, entry.getKey());
//						allPoints.remove(entry.getKey());
						removedKeys.add(entry.getKey());
					}
				}
				
				for (String entry : removedKeys)
				{
					this.sortedPoints.remove(entry);
//					this.sortedPointsDB.remove(entry);
				}
//				this.sortedPointsDB.putAll(allPoints);
//				this.currentMinPoints = allPoints.get(this.sortedKeysDB.get(index - 1));
				if (this.sortedKeys.size() > 0)
				{
					String minKey = this.sortedKeys.get(this.sortedKeys.size() - 1);
					if (this.sortedPoints.containsKey(minKey))
					{
						this.currentMinPoints.set(this.sortedPoints.get(minKey).intValue());
					}
				}
//				this.lock.readLock().lock();
//				this.lock.writeLock().unlock();
			}
//			this.lock.readLock().unlock();
		}
	}
	
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
		String key;
//		this.lock.readLock().lock();
		if (index < this.sortSize)
		{
//			key = this.sortedKeysDB.get(index);
			key = this.sortedKeys.get(index);
		}
		else
		{
//			key = this.obsoleteKeysDB.get(index - this.sortSize);
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
	 * It is not reasonable to get all of the values from the cache. 08/20/2018, Bing Li
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
				rscs.add(this.get(key));
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
//		int index = this.sortedKeysDB.getSize() - 1;
		int index = this.sortedKeys.size() - 1;
//		this.lock.readLock().unlock();
		
		for (int i = 0; i <= index; i++)
		{
//			this.lock.readLock().lock();
//			key = this.sortedKeysDB.get(i);
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
//				key = this.sortedKeysDB.get(i);
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
//				key = this.sortedKeysDB.get(i);
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
//				key = this.obsoleteKeysDB.get(i);
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
			return super.getMemCacheSize();
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		*/
		return super.getMemCacheSizeAtBase();
	}
	
	/*
	 * To save CPU resources, after removal, no sorting is performed. It does not cause serious problem. Usually, in a large scale system, data is not removed. 02/14/2019, Bing Li
	 */
	public void remove(int index)
	{
		String key = UtilConfig.EMPTY_STRING;
//		this.lock.readLock().lock();
//		if (this.sortedKeysDB.containsKey(index))
		if (this.sortedKeys.containsKey(index))
		{
//			key = this.sortedKeysDB.get(index);
			key = this.sortedKeys.get(index);
			
//			this.lock.readLock().unlock();
//			this.lock.writeLock().lock();
//			this.sortedKeysDB.remove(index);
			this.sortedKeys.remove(index);
//			this.sortedPointsDB.remove(key);
			this.sortedPoints.remove(key);
//			this.lock.readLock().lock();
//			this.lock.writeLock().unlock();
			
//			super.remove(key);
		}
		else
		{
//			key = this.obsoleteKeys.get(index - this.sortSize);
			
//			this.lock.readLock().unlock();
//			this.lock.writeLock().lock();
//			this.sortedKeys.remove(index);
//			this.obsoleteKeysDB.remove(index);
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
	
	public void remove(String key)
	{
		/*
		this.lock.writeLock().lock();
		try
		{
			super.remove(key);
		}
		finally
		{
			this.lock.writeLock().unlock();
		}
		*/
//		this.lock.writeLock().lock();
//		this.sortedPointsDB.remove(key);
		this.sortedPoints.remove(key);
//		this.lock.writeLock().unlock();
		super.removeAtBase(key);
	}

	/*
	 * The method is usually not useful in practice. 02/14/2019, Bing Li
	 */
	public void clear()
	{
		String key;
//		this.lock.readLock().lock();
//		Set<Integer> keys = this.sortedKeysDB.getKeys();
		Set<Integer> keys = this.sortedKeys.keySet();
//		this.lock.readLock().unlock();
		
		for (Integer index : keys)
		{
//			this.lock.readLock().lock();
//			key = this.sortedKeysDB.get(index);
			key = this.sortedKeys.get(index);
//			this.lock.readLock().unlock();

//			this.lock.writeLock().lock();
//			this.sortedKeysDB.remove(index);
			this.sortedKeys.remove(index);
//			this.sortedPointsDB.remove(key);
			this.sortedPoints.remove(key);
//			this.lock.writeLock().unlock();

			super.removeAtBase(key);
		}

//		this.lock.readLock().lock();
//		keys = this.obsoleteKeysDB.getKeys();
		keys = this.obsoleteKeys.keySet();
//		this.lock.readLock().unlock();
		
		for (Integer index : keys)
		{
//			this.lock.readLock().lock();
//			key = this.obsoleteKeysDB.get(index);
			key = this.obsoleteKeys.get(index);
//			this.lock.readLock().unlock();

//			this.lock.writeLock().lock();
//			this.sortedKeysDB.remove(index);
			this.sortedKeys.remove(index);
//			this.lock.writeLock().unlock();

			super.removeAtBase(key);
		}
	}

	@Override
	public void evict(String k, Value v)
	{
		// Although it takes time to keep data sorted after evicted data is captured, it is necessary to remove the key. 05/11/2018, Bing Li
		super.removeAtBase(k);
//		this.lock.writeLock().lock();
//		this.sortedPointsDB.remove(k);
		this.sortedPoints.remove(k);
//		this.lock.writeLock().unlock();
		
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
