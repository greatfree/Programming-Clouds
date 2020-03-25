package org.greatfree.cache.distributed;

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
import org.greatfree.cache.local.CacheMapFactorable;
import org.greatfree.cache.local.RootCache;
import org.greatfree.util.CollectionSorter;
import org.greatfree.util.Pointing;
import org.greatfree.util.UtilConfig;

import com.google.common.collect.Sets;

/*
 * The list cache that sorts only a predefined count of data is implemented. When data is saved in the list, each data should have a unique key. It must employ the constructor Pointing(long points) of Pointing. 02/15/2019, Bing Li
 */

/*
 * The version is tested in the Clouds project. 08/22/2018, Bing Li
 */

/*
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

// Created: 07/04/2018, Bing Li
abstract class SortedList<Value extends Pointing, Factory extends CacheMapFactorable<String, Value>, DescendantComp extends Comparator<Value>> extends RootCache<String, Value, Factory, StringKeyDB>
{
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

//	public SortedList(Factory factory, String rootPath, String cacheKey, int cacheSize, int offheapSizeInMB, int diskSizeInMB, StringKeyDB db, DescendantComp comp, int sortSize)
	public SortedList(Factory factory, String rootPath, String cacheKey, long cacheSize, int offheapSizeInMB, int diskSizeInMB, StringKeyDB db, DescendantComp comp, int sortSize)
	{
		super(factory, rootPath, cacheKey, cacheSize, offheapSizeInMB, diskSizeInMB, db, true);
		this.sortedPointsDB = new SortedListDB(CacheConfig.getPointingListPointsCachePath(rootPath, cacheKey));
		this.sortedPoints = this.sortedPointsDB.getValues();
		this.sortedKeysDB = new ListKeyDB(CacheConfig.getListKeysCachePath(rootPath, cacheKey));
		this.sortedKeys = this.sortedKeysDB.getAllKeys();
		this.obsoleteKeysDB = new ListKeyDB(CacheConfig.getObsoleteListKeysCachePath(rootPath, cacheKey));
		this.obsoleteKeys = this.obsoleteKeysDB.getAllKeys();
		this.comp = comp;
		this.currentMinPoints = new AtomicInteger(UtilConfig.ZERO);
//		this.lock = new ReentrantReadWriteLock();
		this.sortSize = sortSize;
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
	}

	/*
	protected String getCacheKey()
	{
		return super.getCacheKeyAtBase();
	}
	
	protected long getCacheSize()
	{
		return super.getCacheSizeAtBase();
	}
	*/
	
	protected DescendantComp getComp()
	{
		return this.comp;
	}

	/*
	protected long getEmptySize()
	{
		return super.getEmptySizeAtBase();
	}
	*/
	
	protected int getSizeAt2ndBase()
	{
		return super.getKeysAtBase().size();
	}
	
	/*
	public int getLastIndex()
	{
		return this.points.getSize() - 1;
	}
	*/

	/*
	protected long getLeftSize(int currentIndex)
	{
		return super.getLeftSizeAtBase(currentIndex);
	}
	*/

	/*
	private void assignMin(Value v)
	{
		// The only possibility that v is NULL is due to the fact that it is removed when eviction occurs. 07/29/2018, Bing Li
		// Sometimes v is NULL! I don't understand. 07/29/2018, Bing Li
		this.lock.writeLock().lock();
		if (v != null)
		{
			this.currentMinValue = v;
			System.out.println("PointingList-assignMin(): currentMinValue points = " + v.getPoints());
		}
		else
		{
			System.out.println("PointingList-assignMin(): v is NULL");
		}
		this.lock.writeLock().unlock();
	}
	
	public Value getMin()
	{
		this.lock.readLock().lock();
		try
		{
			return this.currentMinValue;
		}
		finally
		{
			this.lock.readLock().unlock();
		}
	}
	
	private double getMinPoints()
	{
		this.lock.readLock().lock();
		try
		{
			if (this.currentMinValue != null)
			{
				return this.currentMinValue.getPoints();
			}
			return UtilConfig.ZERO;
		}
		finally
		{
			this.lock.readLock().unlock();
		}
	}
	*/

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

	protected void addAt2ndBase(Value rsc)
	{
		if (super.getMemCacheSizeAtBase() <= 0)
		{
			super.putAtBase(rsc.getKey(), rsc);
//			this.lock.writeLock().lock();
			this.sortedPoints.put(rsc.getKey(), rsc.getPoints());
//			System.out.println("0) index = " + 0 + "), value = " + rsc.getPoints());
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
//				Map<String, Float> allPoints = this.sortedPointsDB.getValues();
				
				this.sortedPoints.put(rsc.getKey(), rsc.getPoints());
				Map<String, Float> sp = CollectionSorter.sortDescendantByValue(this.sortedPoints);
				int obsSize = this.obsoleteKeys.size();

//				System.out.println("1) allPoints size = " + allPoints.size());

//				System.out.println("1) obsSize = " + obsSize);

//				this.lock.readLock().unlock();
//				this.lock.writeLock().lock();

				int index = 0;
				Set<String> removedKeys = Sets.newHashSet();
				for (Map.Entry<String, Float> entry : sp.entrySet())
				{
					if (index < this.sortSize)
					{
//						System.out.println("1) index = " + index + "), value = " + entry.getValue());
						this.sortedKeys.put(index++, entry.getKey());
					}
					else
					{
//						System.out.println("1) obsIndex = " + obsSize + "), value = " + entry.getValue());
						// The obsolete data is not so important. So just put to the end of the obsolete keys. 02/14/2019, Bing Li
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
				
//				System.out.println("1.1) allPoints size = " + allPoints.size());

//				this.sortedPointsDB.putAll(allPoints);
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
				int currentSize = this.sortedPoints.size();
				int obsSize = this.obsoleteKeys.size();

//				System.out.println("2) obsSize = " + obsSize);

//				this.lock.readLock().unlock();
//				this.lock.writeLock().lock();
				if (currentSize < this.sortSize)
				{
					this.sortedPoints.put(rsc.getKey(), rsc.getPoints());
//					System.out.println("2) index = " + currentSize + "), value = " + rsc.getPoints());
					this.sortedKeys.put(currentSize, rsc.getKey());
					this.currentMinPoints.set((int)rsc.getPoints());
				}
				else
				{
//					System.out.println("2) obsIndex = " + obsSize + "), value = " + rsc.getPoints());
					this.obsoleteKeys.put(obsSize, rsc.getKey());
				}
//				this.lock.readLock().lock();
//				this.lock.writeLock().unlock();
			}
//			this.lock.readLock().unlock();
		}
	}

	protected void addAllAt2ndBase(List<Value> rscs)
	{
		if (rscs.size() > 0)
		{
			// The below line aims to avoid the exception, ConcurrentModificationException. 10/13/2019, Bing Li
			List<Value> sortedList = new ArrayList<Value>(rscs);
//			System.out.println("PointingList-addAll(): rscs size = " + rscs.size() + " is being saved ...");
			Collections.sort(sortedList, this.comp);
			for (Value rsc : sortedList)
			{
				super.putAtBase(rsc.getKey(), rsc);
			}
//			this.lock.readLock().lock();
			// If the below condition is fulfilled, it is unnecessary to sort. It assumes that the added data is already sorted. 02/14/2019, Bing Li
			if (sortedList.get(0).getPoints() < this.currentMinPoints.get())
			{
				int index = this.sortedKeys.size();
				int obsSize = this.obsoleteKeys.size();
				int toppedIndex = -1;
	
//				this.lock.readLock().unlock();
//				this.lock.writeLock().lock();
				for (Value rsc : sortedList)
				{
					if (index < this.sortSize)
					{
	//					super.put(rsc.getKey(), rsc);
						this.sortedPoints.put(rsc.getKey(), rsc.getPoints());
						this.sortedKeys.put(index++, rsc.getKey());
						toppedIndex++;
					}
					else
					{
						this.obsoleteKeys.put(obsSize++, rsc.getKey());
					}
				}
				
				/*
				 * The below index is out of bound. 02/18/2019, Bing Li
				 */
				if (toppedIndex >= 0)
				{
					this.currentMinPoints.set((int)sortedList.get(toppedIndex).getPoints());
				}
//				this.lock.readLock().lock();
//				this.lock.writeLock().unlock();
			}
			else
			{
//				Map<String, Float> allPoints = this.sortedPointsDB.getValues();
				int obsSize = this.obsoleteKeys.size();
	
				// The new data is added without considering the sort size. It assumes that the size of added data is not large. 02/14/2019, Bing Li
				for (Value rsc : sortedList)
				{
	//					super.put(rsc.getKey(), rsc);
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
						this.sortedKeys.put(index++, entry.getKey());
					}
					else
					{
						this.obsoleteKeys.put(obsSize++, entry.getKey());
	//					allPoints.remove(entry.getKey());
						removedKeys.add(entry.getKey());
					}
				}
				
				for (String entry : removedKeys)
				{
					this.sortedPoints.remove(entry);
//					this.sortedPointsDB.remove(entry);
				}
//				this.sortedPointsDB.putAll(allPoints);
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

	/*
	protected boolean containsKeyAtBase(String key)
	{
		return super.containsKeyAtBase(key);
	}
	*/

	/*
	protected boolean isEmptyAtBase()
	{
		return super.isEmptyAtBase();
	}
	*/
	
	protected Value getAt2ndBase(int index)
	{
		/*
		this.lock.readLock().lock();
		try
		{
			String key = this.keys.get(index);
			if (key != null)
			{
				return super.get(key);
			}
			return null;
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		*/
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
	
	protected List<Value> getAt2ndBase(int startIndex, int endIndex)
	{
		List<Value> rscs = new ArrayList<Value>();
		Value rsc;
		for (int i = startIndex; i <= endIndex; i++)
		{
			rsc = this.getAt2ndBase(i);
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
	
	protected List<String> getResourceKeysAt2ndBase(int startIndex, int endIndex)
	{
		List<String> rscs = new ArrayList<String>();
		Value rsc;
		for (int i = startIndex; i <= endIndex; i++)
		{
			rsc = this.getAt2ndBase(i);
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

	protected List<Value> getTopAt2ndBase(int endIndex)
	{
		List<Value> rscs = new ArrayList<Value>();
		Value rsc;
		for (int i = 0; i <= endIndex; i++)
		{
			rsc = this.getAt2ndBase(i);
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
	 * It is not reasonable to get all of values from the cache. 08/20/2018, Bing Li
	 */
	/*
	protected List<Value> get()
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
					rscs.add(this.get(key));
				}
				else
				{
					break;
				}
			}
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		return rscs;
	}
	*/

	/*
	 * The method returns the keys that is sorted. In practice, for a cache, it is not necessary to return all of the keys, especially for a list. 02/14/2019, Bing Li
	 */
	protected List<String> getResourceKeysAt2ndBase()
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
	
	protected List<String> getResourceKeysAt2ndBase(int endIndex)
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

	/*
	protected long getMemCacheSizeAtBase()
	{
		return super.getMemCacheSizeAtBase();
	}
	*/
	
	protected void removeAt2ndBase(int index)
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
//			key = this.obsoleteKeys.get(index - this.sortSize);
			
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
	
	protected void removeAt2ndBase(int startIndex, int endIndex)
	{
		for (int i = startIndex; i <= endIndex; i++)
		{
			this.removeAt2ndBase(i);
		}
	}
	
	protected void removeKeyAt2ndBase(String key)
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
		this.sortedPoints.remove(key);
//		this.lock.writeLock().unlock();
		super.removeKeyAtBase(key);
	}
	
	protected void clearAt2ndBase()
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

	public abstract void evict(String k, Value v);
	public abstract void forward(String k, Value v);
	public abstract void remove(String k, Value v);
	public abstract void expire(String k, Value v);
	public abstract void update(String k, Value v);
}
