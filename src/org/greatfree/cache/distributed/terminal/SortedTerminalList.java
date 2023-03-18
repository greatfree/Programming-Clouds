package org.greatfree.cache.distributed.terminal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.greatfree.cache.CacheConfig;
import org.greatfree.cache.db.ListKeyDB;
import org.greatfree.cache.db.SortedListDB;
import org.greatfree.cache.db.StringKeyDB;
import org.greatfree.cache.db.StringKeyDBPool;
import org.greatfree.cache.distributed.terminal.db.PostfetchDB;
import org.greatfree.cache.local.CacheMapFactorable;
import org.greatfree.cache.local.RootCache;
import org.greatfree.exceptions.TerminalServerOverflowedException;
import org.greatfree.util.Builder;
import org.greatfree.util.CollectionSorter;
import org.greatfree.util.Pointing;
import org.greatfree.util.UtilConfig;

/*
 * The list cache that sorts only a predefined count of data is implemented. When data is saved in the list, each data should have a unique key. It must employ the constructor Pointing(long points) of Pointing. 02/15/2019, Bing Li
 */

/*
 * The version is tested in the Clouds project. 08/22/2018, Bing Li
 */

/*
 * Its counterpart is PointingList, in which one key might not have the value because of eviction. 05/31/2018, Bing Li
 * 
 * The terminal cache keeps the evicted in a DB. Thus, no data will be lost unless the local preset disk is overloaded. So usually, any keys have their own values strictly. 05/31/2018, Bing Li
 *
 * The cache is usually used to keep data at the terminal server in a distributed storage system, such as MServer. It can be used at a standalone server only if the key is required to match with one value. 05/31/2018, Bing Li
 * 
 */

// Created: 05/16/2018, Bing Li
public class SortedTerminalList<Value extends Pointing, Factory extends CacheMapFactorable<String, Value>, DescendantComp extends Comparator<Value>, DB extends PostfetchDB<Value>> extends RootCache<String, Value, Factory, StringKeyDB>
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
	
	private DB db;
	private AtomicInteger currentEvictedCount;
	private final int alertEvictedCount;

//	private ReentrantReadWriteLock lock;

	private final int sortSize;

	public SortedTerminalList(SortedTerminalListBuilder<Value,  Factory, DescendantComp, DB> builder)
	{
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

		this.db = builder.getDB();
		this.currentEvictedCount = new AtomicInteger(this.db.getSize());
		this.alertEvictedCount = builder.getAlertEvictedCount();
		
		this.sortSize = builder.getSortSize();
	}
	
	public static class SortedTerminalListBuilder<Value extends Pointing, Factory extends CacheMapFactorable<String, Value>, DescendantComp extends Comparator<Value>, DB extends PostfetchDB<Value>> implements Builder<SortedTerminalList<Value, Factory, DescendantComp, DB>>
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

		private DB db;
		private int alertEvictedCount;
		
		public SortedTerminalListBuilder()
		{
		}

		public SortedTerminalListBuilder<Value, Factory, DescendantComp, DB> factory(Factory factory)
		{
			this.factory = factory;
			return this;
		}

		public SortedTerminalListBuilder<Value, Factory, DescendantComp, DB> rootPath(String rootPath)
		{
			this.rootPath = rootPath;
			return this;
		}
		
		public SortedTerminalListBuilder<Value, Factory, DescendantComp, DB> cacheKey(String cacheKey)
		{
			this.cacheKey = cacheKey;
			return this;
		}
		
		public SortedTerminalListBuilder<Value, Factory, DescendantComp, DB> cacheSize(long cacheSize)
		{
			this.cacheSize = cacheSize;
			return this;
		}
		
		public SortedTerminalListBuilder<Value, Factory, DescendantComp, DB> offheapSizeInMB(int offheapSizeInMB)
		{
			this.offheapSizeInMB = offheapSizeInMB;
			return this;
		}

		public SortedTerminalListBuilder<Value, Factory, DescendantComp, DB> diskSizeInMB(int diskSizeInMB)
		{
			this.diskSizeInMB = diskSizeInMB;
			return this;
		}

		public SortedTerminalListBuilder<Value, Factory, DescendantComp, DB> comp(DescendantComp comp)
		{
			this.comp = comp;
			return this;
		}

		public SortedTerminalListBuilder<Value, Factory, DescendantComp, DB> sortSize(int sortSize)
		{
			this.sortSize = sortSize;
			return this;
		}

		public SortedTerminalListBuilder<Value, Factory, DescendantComp, DB> db(DB db)
		{
			this.db = db;
			return this;
		}

		public SortedTerminalListBuilder<Value, Factory, DescendantComp, DB> alertEvictedCount(int alertEvictedCount)
		{
			this.alertEvictedCount = alertEvictedCount;
			return this;
		}

		@Override
		public SortedTerminalList<Value, Factory, DescendantComp, DB> build()
		{
			return new SortedTerminalList<Value, Factory, DescendantComp, DB>(this);
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
		
		public DB getDB()
		{
			return this.db;
		}
		
		public int getAlertEvictedCount()
		{
			return this.alertEvictedCount;
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

		this.db.close();
//		this.lock.writeLock().unlock();
//		this.lock = null;
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

	public void add(Value rsc)
	{
		if (super.getMemCacheSizeAtBase() <= 0)
		{
			super.putAtBase(rsc.getKey(), rsc);
//			this.lock.writeLock().lock();
			this.sortedPoints.put(rsc.getKey(), rsc.getPoints());
			this.sortedKeys.put(0, rsc.getKey());
			this.currentMinPoints.set((int)rsc.getPoints());
//			this.setMinPoints(rsc.getPoints());
//			this.lock.writeLock().unlock();
		}
		else
		{
//			this.lock.writeLock().lock();
			super.putAtBase(rsc.getKey(), rsc);
//			this.lock.readLock().lock();
			if (rsc.getPoints() > this.currentMinPoints.get())
			{
//				Map<String, Float> allPoints = this.sortedPointsDB.getValues();
				
//				this.lock.readLock().unlock();
//				this.lock.writeLock().lock();
				
				this.sortedPoints.put(rsc.getKey(), rsc.getPoints());
				Map<String, Float> sp = CollectionSorter.sortDescendantByValue(this.sortedPoints);
				int obsSize = this.obsoleteKeys.size();

				int index = 0;
//				Set<String> removedKeys = Sets.newHashSet();
				Set<String> removedKeys = new HashSet<String>();
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
//				this.setMinPoints(allPoints.get(this.keys.get(index - 1)));
			}
			else
			{
				int currentSize = this.sortedPoints.size();
				int obsSize = this.obsoleteKeys.size();

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

	public void addAll(List<Value> rscs)
	{
//		System.out.println("PointingTerminalList-addAll(): " + rscs.size() + " is being saved!");
		if (rscs.size() > 0)
		{
			Collections.sort(rscs, this.comp);
			
			for (Value rsc : rscs)
			{
				super.putAtBase(rsc.getKey(), rsc);
			}
			
//			this.lock.readLock().lock();
			if (rscs.get(0).getPoints() < this.currentMinPoints.get())
			{
				int index = this.sortedKeys.size();
				int obsSize = this.obsoleteKeys.size();
				int toppedIndex = -1;
				
//				this.lock.readLock().unlock();
//				this.lock.writeLock().lock();
				for (Value rsc : rscs)
				{
					if (index < this.sortSize)
					{
//						super.put(rsc.getKey(), rsc);
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
				if (index < this.sortSize)
				{
					this.currentMinPoints = rscs.get(index - this.sortSize - 1).getPoints();
				}
				else
				{
					this.currentMinPoints = rscs.get(index - 1).getPoints();
				}
				*/
//				this.setMinPoints(rscs.get(rscs.size() - 1).getPoints());
//				this.currentMinPoints = rscs.get(rscs.size() - 1).getPoints();
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
				int obsSize = this.obsoleteKeys.size();

				for (Value rsc : rscs)
				{
//					super.put(rsc.getKey(), rsc);
					this.sortedPoints.put(rsc.getKey(), rsc.getPoints());
				}
				Map<String, Float> sp = CollectionSorter.sortDescendantByValue(this.sortedPoints);
//				this.lock.readLock().unlock();

				int index = 0;
//				Set<String> removedKeys = Sets.newHashSet();
				Set<String> removedKeys = new HashSet<String>();
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
						removedKeys.add(entry.getKey());
					}
				}
				
				for (String entry : removedKeys)
				{
					this.sortedPoints.remove(entry);
				}
//				this.sortedPointsDB.putAll(this.sortedPoints);
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
//		System.out.println("PointingTerminalList-addAll(): " + rscs.size() + " is saved!");
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
//			System.out.println("1) SortedTerminalList-get(): index = " + index);
			key = this.sortedKeys.get(index);
		}
		else
		{
//			System.out.println("2) SortedTerminalList-get(): index = " + index);
			key = this.obsoleteKeys.get(index - this.sortSize);
		}
		
//		System.out.println("SortedTerminalList-get(): key = " + key);
//		this.lock.readLock().unlock();
		if (key != null)
		{
			Value rsc = super.getAtBase(key);
			if (rsc != null)
			{
				return rsc;
			}
			// In short, A cache should NOT rely on the DB heavily. 06/02/2018, Bing Li 
			// Sometimes I think it is superior to put the data loaded from DB into the cache. However, it is required to avoid the possibility of eviction. Moreover, when a certain large amount of data is evicted, it is an alert that the size of cache disk is not large enough. Or it is time to add more storage servers. 06/02/2018, Bing Li
			// To simplify the code, the data loaded from the DB will NOT be put into the cache. The benefit to do that is not so obvious. In addition, the DB itself has the cache capability. My work just focuses on designing a wrapper. 05/31/2018, Bing Li
			/*
			rsc = this.db.get(key);
			if (rsc != null)
			{
				super.put(key, rsc);
			}
			return rsc;
			*/
			/*
			this.lock.readLock().lock();
			try
			{
				return this.db.get(key);
			}
			finally
			{
				this.lock.readLock().unlock();
			}
			*/
			return this.db.get(key);
		}
		return null;
	}
	
	public Value get(String key)
	{
		Value rsc = super.getAtBase(key);
		if (rsc != null)
		{
			return rsc;
		}
		/*
		this.lock.readLock().lock();
		try
		{
			return this.db.get(key);
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		*/
		return this.db.get(key);
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

	public List<String> getKeys(int startIndex, int endIndex)
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
	 * It is not reasonable to load all of the data of a cache. 08/29/2018, Bing Li
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

	/*
	 * The method returns the keys that is sorted. In practice, for a cache, it is not necessary to return all of the keys, especially for a list. 02/14/2019, Bing Li
	 */
	public List<String> getResourceKeys()
	{
		List<String> rscs = new ArrayList<String>();
//		List<String> keys = new ArrayList<String>();
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
		/*
		for (String entry : keys)
		{
			rscs.add(super.get(entry).getKey());
		}
		*/
		return rscs;
	}

	public List<String> getKeys(int endIndex)
	{
		List<String> rscs = new ArrayList<String>();
//		List<String> keys = new ArrayList<String>();
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
		/*
		for (String entry : keys)
		{
			rscs.add(super.get(entry).getKey());
		}
		*/
		return rscs;
	}

	/*
	 * Since the sorting is performed within a predefined range, the minimum data index should be smaller than the sort size. 02/18/2019, Bing Li
	 */
	/*
	 * When evicted data is overflowed, the approach to get the minimum value need to consider newly added terminal caches. At this time, to save time, I have NOT implemented the newly added distributed terminal caches. 08/01/2018, Bing Li 
	 */
	public Value getMinValue()
	{
//		return this.get(super.getKeys().size() - 1);
		String minKey;
		if (this.sortedKeys.size() >= this.sortSize)
		{
			minKey = this.sortedKeys.get(this.sortSize - 1);
		}
		else
		{
			minKey = this.sortedKeys.get(this.sortedKeys.size() - 1);
		}
		return this.get(minKey);
	}

	public long getMemCacheSize()
	{
		/*
		this.lock.readLock().lock();
		try
		{
			return super.size();
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		*/
		return super.getMemCacheSizeAtBase();
	}

	// To be reasonable as a storage, the remove methods can be retained. But it suggests to invoke the methods as few as possible. 06/15/2018, Bing Li
	// It is not necessary to remove data for a terminal of a large-scale storage system. 06/15/2018, Bing Li
	public void remove(int index)
	{
		String key = null;
//		this.lock.readLock().lock();
		if (this.sortedKeys.containsKey(index))
		{
			key = this.sortedKeys.get(index);
			if (key != null)
			{
//				this.lock.readLock().unlock();
//				this.lock.writeLock().lock();
				this.db.remove(key);
				this.sortedKeys.remove(index);
				this.sortedPoints.remove(key);
//				this.lock.readLock().lock();
//				this.lock.writeLock().unlock();
			}
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
		if (key != null)
		{
			super.removeAtBase(key);
		}
	}

	public int getEvictedCount()
	{
		return this.currentEvictedCount.get();
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
	
	public void remove(String key)
	{
//		this.lock.writeLock().lock();
		super.removeAtBase(key);
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
