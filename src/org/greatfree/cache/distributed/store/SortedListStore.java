package org.greatfree.cache.distributed.store;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.ehcache.Cache;
import org.ehcache.PersistentCacheManager;
import org.ehcache.event.EventFiring;
import org.ehcache.event.EventOrdering;
import org.ehcache.event.EventType;
import org.greatfree.cache.CacheConfig;
import org.greatfree.cache.CacheListener;
import org.greatfree.cache.CompoundKeyCreatable;
import org.greatfree.cache.db.SortedListIndexDB;
import org.greatfree.cache.factory.SortedListIndexes;
import org.greatfree.cache.local.CacheEventable;
import org.greatfree.cache.local.CacheMapFactorable;
import org.greatfree.util.CollectionSorter;
import org.greatfree.util.FileManager;
import org.greatfree.util.Pointing;
import org.greatfree.util.UtilConfig;

/*
 * The version is tested in the Clouds project. 08/22/2018, Bing Li
 */

// Created: 06/07/2018, Bing Li
// abstract class PointingCacheStore<Value extends CachePointing, Factory extends CacheMapFactorable<String, Value>, CompoundKeyCreator extends CompoundKeyCreatable<String>, DescendantComp extends Comparator<Value>> implements CacheEventable<String, Value>
abstract class SortedListStore<Value extends Pointing, Factory extends CacheMapFactorable<String, Value>, CompoundKeyCreator extends CompoundKeyCreatable<String>, DescendantComp extends Comparator<Value>> implements CacheEventable<String, Value>
{
	private final String storeKey;
	private Cache<String, Value> cache;
	private PersistentCacheManager manager;
//	private final int cacheSize;
	private final long cacheSize;

	private SortedListIndexDB listIndexesDB;
	private Map<String, SortedListIndexes> listIndexes;

	private DescendantComp comp;

	private CacheListener<String, Value, SortedListStore<Value, Factory, CompoundKeyCreator, DescendantComp>> listener;

	private CompoundKeyCreator keyCreator;
	
	private AtomicBoolean isDown;
//	private ReentrantReadWriteLock lock;
	private final int sortSize;

//	public SortedListStore(String storeKey, Factory factory, String rootPath, int totalStoreSize, int cacheSize, int offheapSizeInMB, int diskSizeInMB, CompoundKeyCreator keyCreator, DescendantComp comp, int sortSize)
	public SortedListStore(String storeKey, Factory factory, String rootPath, long totalStoreSize, long cacheSize, int offheapSizeInMB, int diskSizeInMB, CompoundKeyCreator keyCreator, DescendantComp comp, int sortSize)
	{
		this.storeKey = storeKey;
		this.manager = factory.createManagerInstance(FileManager.appendSlash(rootPath), storeKey, totalStoreSize, offheapSizeInMB, diskSizeInMB);
		this.cache = factory.createCache(this.manager, this.storeKey);
		this.listener = new CacheListener<String, Value, SortedListStore<Value, Factory, CompoundKeyCreator, DescendantComp>>(this);
		this.cache.getRuntimeConfiguration().registerCacheEventListener(this.listener, EventOrdering.ORDERED, EventFiring.ASYNCHRONOUS, EnumSet.of(EventType.CREATED, EventType.EVICTED, EventType.EXPIRED, EventType.REMOVED, EventType.UPDATED));

		this.cacheSize = cacheSize;
		this.keyCreator = keyCreator;

		this.listIndexesDB = new SortedListIndexDB(CacheConfig.getStoreKeysRootPath(CacheConfig.getCachePath(rootPath, storeKey), CacheConfig.LIST_KEYS));
		this.listIndexes = this.listIndexesDB.getAllIndexes();

		this.comp = comp;
		this.isDown = new AtomicBoolean(false);
//		this.lock = new ReentrantReadWriteLock();
		this.sortSize = sortSize;
	}

	protected void closeAtBase() throws IOException
	{
		this.manager.close();
//		this.lock.writeLock().lock();

		this.listIndexesDB.removeAll();
		this.listIndexesDB.putAll(this.listIndexes);
		this.listIndexesDB.dispose();

		this.isDown.set(true);
//		this.lock.writeLock().unlock();
	}
	
	protected long getCacheSizeAtBase()
	{
		return this.cacheSize;
	}
	
	protected boolean isDownAtBase()
	{
		/*
		this.lock.readLock().lock();
		try
		{
			return this.isDown;
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		*/
		return this.isDown.get();
	}
	
	protected boolean isEmptyAtBase()
	{
		/*
		this.lock.readLock().lock();
		try
		{
			return this.listIndexes.isEmpty();
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		*/
		return this.listIndexes.isEmpty();
	}
	
	protected boolean isEmptyAtBase(String listKey)
	{
		/*
		this.lock.readLock().lock();
		try
		{
			if (this.listIndexes.containsKey(listKey))
			{
	//			System.out.println("PointingCacheStore-isEmpty(): key size = " + this.listIndexes.get(cacheKey).getKeySize());
				return !(this.listIndexes.get(listKey).getKeySize() > 0);
			}
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		*/
		if (this.listIndexes.containsKey(listKey))
		{
			return !(this.listIndexes.get(listKey).getKeySize() > 0);
		}
		return true;
	}
	
	protected boolean isFullAtBase(String listKey)
	{
		/*
		this.lock.readLock().lock();
		try
		{
			if (this.listIndexes.containsKey(listKey))
			{
				SortedListIndexes indexes = this.listIndexes.get(listKey);
				return indexes.getPoints().size() >= this.cacheSize;
			}
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		*/
		if (this.listIndexes.containsKey(listKey))
		{
			SortedListIndexes indexes = this.listIndexes.get(listKey);
			return indexes.getPoints().size() >= this.cacheSize;
		}
		return false;
	}
	
	protected Set<String> getListKeysAtBase()
	{
		/*
		this.lock.readLock().lock();
		try
		{
			return this.listIndexes.keySet();
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		*/
		return this.listIndexes.keySet();
	}
	
	protected int sizeAtBase()
	{
		/*
		this.lock.readLock().lock();
		try
		{
			Set<String> listKeys = this.listIndexes.keySet();
			int size = 0;
			for (String entry : listKeys)
			{
				size += this.listIndexes.get(entry).getKeySize();
			}
			return size;
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		*/
		Set<String> listKeys = this.listIndexes.keySet();
		int size = 0;
		for (String entry : listKeys)
		{
			size += this.listIndexes.get(entry).getKeySize();
		}
		return size;
	}
	
	protected boolean isListInStoreAtBase(String listKey)
	{
		/*
		this.lock.readLock().lock();
		try
		{
			if (this.listIndexes.containsKey(listKey))
			{
				return this.listIndexes.get(listKey).getKeySize() > 0;
			}
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		*/
		if (this.listIndexes.containsKey(listKey))
		{
			return this.listIndexes.get(listKey).getKeySize() > 0;
		}
		return false;
	}
	
	protected boolean containsKeyAtBase(String listKey, String rscKey)
	{
		/*
		this.lock.readLock().lock();
		try
		{
			return this.cache.containsKey(this.keyCreator.createCompoundKey(cacheKey, rscKey));
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		*/
		return this.cache.containsKey(this.keyCreator.createCompoundKey(listKey, rscKey));
	}
	
	protected int getSizeAtBase(String listKey)
	{
		/*
		this.lock.readLock().lock();
		try
		{
			if (this.listIndexes.containsKey(listKey))
			{
				return this.listIndexes.get(listKey).getKeySize();
			}
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		*/
		if (this.listIndexes.containsKey(listKey))
		{
			return this.listIndexes.get(listKey).getKeySize();
		}
		return UtilConfig.NO_COUNT;
	}

	protected int getLeftSizeAtBase(String listKey, int endIndex)
	{
		/*
		this.lock.readLock().lock();
		try
		{
			if (this.listIndexes.containsKey(listKey))
			{		
				return this.listIndexes.get(listKey).getPoints().size() - endIndex;
			}
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		*/
		if (this.listIndexes.containsKey(listKey))
		{		
			return this.listIndexes.get(listKey).getPoints().size() - endIndex;
		}
		return UtilConfig.NO_COUNT;
	}

	protected void addAtBase(String listKey, Value rsc)
	{
//		this.lock.writeLock().lock();
		String key = this.keyCreator.createCompoundKey(listKey, rsc.getKey());
		this.cache.put(key, rsc);
//		this.lock.readLock().lock();
		if (!this.listIndexes.containsKey(listKey))
		{
//			this.lock.readLock().unlock();
//			this.lock.writeLock().lock();
			this.listIndexes.put(listKey, new SortedListIndexes());
			// It is required to take it out from the cache. Otherwise, the updates cannot be retained in the cache. 06/23/2017, Bing Li
			SortedListIndexes indexes = this.listIndexes.get(listKey);
			indexes.addKey(key);
			indexes.put(key, rsc.getPoints());
			this.listIndexes.put(listKey, indexes);
//			this.lock.readLock().lock();
//			this.lock.writeLock().unlock();
//			this.cache.put(key, rsc);
		}
		else
		{
			SortedListIndexes indexes = this.listIndexes.get(listKey);
			if (rsc.getPoints() > indexes.getMinPoints())
			{
//				Map<String, Float> allPoints = indexes.getPoints();
				// The operation aims to avoid the exception of ConcurrentModificationException. 10/13/2019, Bing Li
				Map<String, Float> allPoints = new HashMap<String, Float>(indexes.getPoints());
//				System.out.println("PointingCacheStore-add(): rsc points = " + rsc.getPoints());
//				System.out.println("PointingCacheStore-add(): min points = " + indexes.getMinPoints());
				allPoints.put(key, rsc.getPoints());
//				allPoints = CollectionSorter.sortDescendantByValue(allPoints);
				Map<String, Float> sp = CollectionSorter.sortDescendantByValue(allPoints);
				int index = 0;
//				Set<String> removedKeys = Sets.newHashSet();
				Set<String> removedKeys = new HashSet<String>();
				indexes.clearKeys();
				for (Map.Entry<String, Float> entry : sp.entrySet())
				{
					if (index < this.sortSize)
					{
						indexes.addKey(entry.getKey());
					}
					else
					{
						indexes.addObsKey(entry.getKey());
						removedKeys.add(entry.getKey());
					}
					index++;
				}
				
				for (String entry : removedKeys)
				{
					allPoints.remove(entry);
				}
				indexes.setPoints(allPoints);
			}
			else
			{
				if (indexes.getKeySize() < this.sortSize)
				{
					indexes.addKey(key);
					indexes.put(key, rsc.getPoints());
				}
				else
				{
					indexes.addObsKey(key);
				}
			}
			/*
			Map<String, Double> allPoints = indexes.getPoints();
			for (Map.Entry<String, Double> entry : allPoints.entrySet())
			{
				System.out.println("PointingCacheStore-add(): key = " + entry.getKey() + ", value = " + entry.getValue());
			}
			*/
//			this.lock.readLock().unlock();
//			this.lock.writeLock().lock();
			this.listIndexes.put(listKey, indexes);
//			this.lock.readLock().lock();
//			this.lock.writeLock().unlock();
//			this.cache.put(key, rsc);
		}
//		this.lock.readLock().unlock();
	}

	protected void addAllAtBase(String listKey, List<Value> rscs)
	{
		// The below line aims to avoid the exception, ConcurrentModificationException. 10/13/2019, Bing Li
		List<Value> sortedList = new ArrayList<Value>(rscs);
		Collections.sort(sortedList, this.comp);
		String key;
		for (Value rsc : sortedList)
		{
			key = this.keyCreator.createCompoundKey(listKey, rsc.getKey());
			this.cache.put(key, rsc);
		}
		int index = 0;
//		this.lock.readLock().lock();
		if (!this.listIndexes.containsKey(listKey))
		{
//			this.lock.readLock().unlock();
//			this.lock.writeLock().lock();
			this.listIndexes.put(listKey, new SortedListIndexes());
			// It is required to take it out from the cache. Otherwise, the updates cannot be retained in the cache. 06/23/2017, Bing Li
			SortedListIndexes indexes = this.listIndexes.get(listKey);
			for (Value rsc : sortedList)
			{
				key = this.keyCreator.createCompoundKey(listKey, rsc.getKey());
//				this.cache.put(key, rsc);
				if (index < this.sortSize)
				{
					indexes.addKey(key);
//					this.listIndexes.get(listKey).setPoints(key, rsc.getPoints());
					indexes.put(key, rsc.getPoints());
				}
				else
				{
					indexes.addObsKey(key);
				}
				index++;
			}
			this.listIndexes.put(listKey, indexes);
//			this.lock.readLock().lock();
//			this.lock.writeLock().unlock();
		}
		else
		{
			SortedListIndexes indexes = this.listIndexes.get(listKey);
//			Map<String, Float> allPoints = indexes.getPoints();
			// The operation aims to avoid the exception of ConcurrentModificationException. 10/13/2019, Bing Li
			Map<String, Float> allPoints = new HashMap<String, Float>(indexes.getPoints());
			for (Value rsc : sortedList)
			{
				key = this.keyCreator.createCompoundKey(listKey, rsc.getKey());
//				this.cache.put(key, rsc);
				allPoints.put(key, rsc.getPoints());
			}
//			allPoints = CollectionSorter.sortDescendantByValue(allPoints);
			Map<String, Float> sp = CollectionSorter.sortDescendantByValue(allPoints);
//			Set<String> removedKeys = Sets.newHashSet();
			Set<String> removedKeys = new HashSet<String>();
			indexes.clearKeys();
//			for (Map.Entry<String, Float> entry : allPoints.entrySet())
			for (Map.Entry<String, Float> entry : sp.entrySet())
			{
				if (index < this.sortSize)
				{
//					indexes.addKey(index++, entry.getKey());
					indexes.addKey(entry.getKey());
				}
				else
				{
					indexes.addObsKey(entry.getKey());
					removedKeys.add(entry.getKey());
				}
				index++;
			}
			
			for (String entry : removedKeys)
			{
				allPoints.remove(entry);
			}
			indexes.setPoints(allPoints);
//			this.lock.readLock().unlock();
//			this.lock.writeLock().lock();
			this.listIndexes.put(listKey, indexes);
//			this.lock.readLock().lock();
//			this.lock.writeLock().unlock();
		}
//		this.lock.readLock().unlock();
	}
	
	protected Value getAtBase(String listKey, String rscKey)
	{
		/*
		this.lock.readLock().lock();
		try
		{
			if (this.listIndexes.containsKey(cacheKey))
			{
				return this.cache.get(this.keyCreator.createCompoundKey(cacheKey, rscKey));
			}
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		*/
		String key = UtilConfig.EMPTY_STRING;
//		this.lock.readLock().lock();
		if (this.listIndexes.containsKey(listKey))
		{
//			return this.cache.get(this.keyCreator.createCompoundKey(cacheKey, rscKey));
			key = this.keyCreator.createCompoundKey(listKey, rscKey);
		}
//		this.lock.readLock().unlock();
		if (!key.equals(UtilConfig.EMPTY_STRING))
		{
			return this.cache.get(key);
		}
		return null;
	}
	
	/*
	 * It is not reasonable to load all of the data from a cache. 08/20/2018, Bing Li
	 */
	/*
	protected Map<String, Value> get(String cacheKey)
	{
		this.lock.readLock().lock();
		if (this.listIndexes.containsKey(cacheKey))
		{		
			Map<String, Value> rscs = new HashMap<String, Value>();
			PointingListIndexes indexes = this.listIndexes.get(cacheKey);
			Set<String> rscKeys = indexes.getPoints().keySet();
			Value rsc;
			for (String key : rscKeys)
			{
				rsc = this.cache.get(key);
				rscs.put(rsc.getKey(), rsc);
			}
			return rscs;
		}
		return null;
	}
	*/

	/*
	protected Value getMaxValueResource(String cacheKey, Comparator<Map.Entry<String, Value>> c)
	{
		Map<String, Value> rscs = this.get(cacheKey);
		String key = CollectionSorter.maxValueKey(rscs, c);
		if (key != null)
		{
			return rscs.get(key);
		}
		return null;
	}
	*/

	protected Value getAtBase(String listKey, int index)
	{
		String key = UtilConfig.EMPTY_STRING;
		/*
		this.lock.readLock().lock();
		try
		{
//			String listKey = this.keyCreator.createPrefixKey(listKey, this.isHash);
			if (this.listIndexes.containsKey(listKey))
			{
				SortedListIndexes indexes = this.listIndexes.get(listKey);
				if (index < this.sortSize)
				{
					key = indexes.getKey(index);
				}
				else
				{
					key = indexes.getObsKey(index - this.sortSize);
				}
			}
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		*/
		if (this.listIndexes.containsKey(listKey))
		{
			SortedListIndexes indexes = this.listIndexes.get(listKey);
			if (index < this.sortSize)
			{
				key = indexes.getKey(index);
			}
			else
			{
				key = indexes.getObsKey(index - this.sortSize);
			}
		}
		if (!key.equals(UtilConfig.EMPTY_STRING))
		{
			return this.cache.get(key);
		}
		return null;
	}
	
	protected List<Value> getRangeAtBase(String listKey, int startIndex, int endIndex)
	{
		List<Value> rscs = new ArrayList<Value>();
		Value rsc;
		for (int i = startIndex; i <= endIndex; i++)
		{
			rsc = this.getAtBase(listKey, i);
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
	
	protected List<String> getKeysAtBase(String listKey, int startIndex, int endIndex)
	{
		List<String> rscs = new ArrayList<String>();
		Value rsc;
		for (int i = startIndex; i <= endIndex; i++)
		{
			rsc = this.getAtBase(listKey, i);
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
	
	protected List<Value> getTopAtBase(String listKey, int endIndex)
	{
//		System.out.println("PointingCacheStore-getTop(): endIndex = " + endIndex);
		List<Value> rscs = new ArrayList<Value>();
		Value rsc;
		for (int i = 0; i <= endIndex; i++)
		{
			rsc = this.getAtBase(listKey, i);
			if (rsc != null)
			{
//				System.out.println("PointingCacheStore-getTop(): rsc = " + rsc.getPoints());
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
	 * The method does not make sense. It is not proper to load all of the data from one cache. 07/22/2018, Bing Li
	 */
	/*
	public List<Value> getList(String cacheKey)
	{
		if (this.listIndexes.containsKey(cacheKey))
		{
			List<Value> rscs = new ArrayList<Value>();
			int lastIndex = this.listIndexes.get(cacheKey).getPoints().size() - 1;
			String key;
			for (int i = 0; i <= lastIndex; i++)
			{
				key = this.listIndexes.get(cacheKey).getKey(i);
				if (key != null)
				{
					rscs.add(this.cache.get(key));
				}
			}
			return rscs;
		}
		return null;
	}
	*/

	/*
	 * The method does not make sense. It is not proper to load all of the data from one cache. 07/22/2018, Bing Li
	 */
	/*
	protected List<String> getKeys(String cacheKey)
	{
		this.lock.readLock().lock();
		boolean isContained = this.listIndexes.containsKey(cacheKey);
		this.lock.readLock().unlock();
		if (isContained)
		{
			List<String> rscs = new ArrayList<String>();
			this.lock.readLock().lock();
			int lastIndex = this.listIndexes.get(cacheKey).getPoints().size() - 1;
			this.lock.readLock().unlock();
			String key;
			for (int i = 0; i <= lastIndex; i++)
			{
				this.lock.readLock().lock();
				key = this.listIndexes.get(cacheKey).getKey(i);
				this.lock.readLock().unlock();
				if (!key.equals(UtilConfig.EMPTY_STRING))
				{
					rscs.add(this.cache.get(key).getKey());
				}
			}
			return rscs;
		}
		return null;
	}
	*/
	
	protected void removeAtBase(String listKey, Set<String> rscKeys)
	{
//		Set<String> keys = Sets.newHashSet();
		Set<String> keys = new HashSet<String>();
//		this.lock.readLock().lock();
		if (this.listIndexes.containsKey(listKey))
		{
			String key;
			SortedListIndexes indexes = this.listIndexes.get(listKey);
			int index;
			for (String rscKey : rscKeys)
			{
				key = this.keyCreator.createCompoundKey(listKey, rscKey);
//				this.cache.remove(key);
				keys.add(key);
				// Since no lock is set for consistency, it is possible that the index is -1. I am not sure whether that is the reason. I need to ensure that. 08/04/2018, Bing Li
				index = indexes.getIndex(key);
				if (index != -1)
				{
					indexes.remove(indexes.getIndex(key));
				}
				indexes.remove(key);
				if (indexes.getPoints().size() <= 0)
				{
//					this.lock.readLock().unlock();
//					this.lock.writeLock().lock();
					this.listIndexes.remove(listKey);
//					this.lock.readLock().lock();
//					this.lock.writeLock().unlock();
				}
				else
				{
//					this.lock.readLock().unlock();
//					this.lock.writeLock().lock();
					this.listIndexes.put(listKey, indexes);
//					this.lock.readLock().lock();
//					this.lock.writeLock().unlock();
				}
			}
		}
//		this.lock.readLock().unlock();
		this.cache.removeAll(keys);
	}

	/*
	 * The method is invoked ONLY when data is evicted. 06/07/2018, Bing Li
	 */
	/*
	protected void removeCacheKey(String cacheKey)
	{
		this.lock.writeLock().lock();
		this.listIndexes.remove(cacheKey);
		this.lock.writeLock().unlock();
	}
	*/
	
	protected void removeKeyAtBase(String listKey, String rscKey)
	{
//		this.lock.readLock().lock();
		if (this.listIndexes.containsKey(listKey))
		{
			String key = this.keyCreator.createCompoundKey(listKey, rscKey);
			SortedListIndexes indexes = this.listIndexes.get(listKey);
			// Since no lock is set for consistency, it is possible that the index is -1. I am not sure whether that is the reason. I need to ensure that. 08/04/2018, Bing Li
			int index = indexes.getIndex(key);
			if (index != -1)
			{
				indexes.remove(index);
			}
			indexes.remove(key);
			if (indexes.getPoints().size() <= 0)
			{
//				this.lock.readLock().unlock();
//				this.lock.writeLock().lock();
				this.listIndexes.remove(listKey);
//				this.lock.readLock().lock();
//				this.lock.writeLock().unlock();
			}
			else
			{
//				this.lock.readLock().unlock();
//				this.lock.writeLock().lock();
				this.listIndexes.put(listKey, indexes);
//				this.lock.readLock().lock();
//				this.lock.writeLock().unlock();
			}
		}
//		this.lock.readLock().unlock();
	}
	
	protected void removeAtBase(String listKey, String rscKey)
	{
		String key = UtilConfig.EMPTY_STRING;
//		this.lock.readLock().lock();
		if (this.listIndexes.containsKey(listKey))
		{
			key = this.keyCreator.createCompoundKey(listKey, rscKey);
//			this.cache.remove(key);

			SortedListIndexes indexes = this.listIndexes.get(listKey);
			// Since no lock is set for consistency, it is possible that the index is -1. I am not sure whether that is the reason. I need to ensure that. 08/04/2018, Bing Li
			int index = indexes.getIndex(key);
			if (index != -1)
			{
				indexes.remove(index);
			}
			indexes.remove(key);
			if (indexes.getPoints().size() <= 0)
			{
//				this.lock.readLock().unlock();
//				this.lock.writeLock().lock();
				this.listIndexes.remove(listKey);
//				this.lock.readLock().lock();
//				this.lock.writeLock().unlock();
			}
			else
			{
//				this.lock.readLock().unlock();
//				this.lock.writeLock().lock();
				this.listIndexes.put(listKey, indexes);
//				this.lock.readLock().lock();
//				this.lock.writeLock().unlock();
			}
		}
//		this.lock.readLock().unlock();
		if (!key.equals(UtilConfig.EMPTY_STRING))
		{
			this.cache.remove(key);
		}
	}

	protected void removeAtBase(String listKey, int index)
	{
		String key = UtilConfig.EMPTY_STRING;
//		this.lock.readLock().lock();
//		String listKey = this.keyCreator.createPrefixKey(listKey);
		if (this.listIndexes.containsKey(listKey))
		{
			SortedListIndexes indexes = this.listIndexes.get(listKey);
			if (index < this.sortSize)
			{
				key = indexes.getKey(index);
				if (!key.equals(UtilConfig.EMPTY_STRING))
				{
//					this.cache.remove(key);
					indexes.remove(index);
					indexes.remove(key);
				}
			}
			else
			{
				key = indexes.getObsKey(index);
				if (!key.equals(UtilConfig.EMPTY_STRING))
				{
					indexes.removeObsKey(index);
				}
			}
			if (indexes.getPoints().size() <= 0)
			{
//				this.lock.readLock().unlock();
//				this.lock.writeLock().lock();
				this.listIndexes.remove(listKey);
//				this.lock.readLock().lock();
//				this.lock.writeLock().unlock();
			}
			else
			{
//				this.lock.readLock().unlock();
//				this.lock.writeLock().lock();
				this.listIndexes.put(listKey, indexes);
//				this.lock.readLock().lock();
//				this.lock.writeLock().unlock();
			}
		}
//		this.lock.readLock().unlock();
		if (!key.equals(UtilConfig.EMPTY_STRING))
		{
			this.cache.remove(key);
		}
	}
	
	protected void removeAtBase(String listKey, int startIndex, int endIndex)
	{
		for (int i = startIndex; i <= endIndex; i++)
		{
			this.removeAtBase(listKey, i);
		}
	}
	
	protected void clearAtBase(Set<String> listKeys)
	{
		for (String entry: listKeys)
		{
			this.clearAtBase(entry);
		}
	}
	
	protected void clearAtBase(String listKey)
	{
//		Set<String> keys = Sets.newHashSet();
		Set<String> keys = new HashSet<String>();
//		this.lock.readLock().lock();
		if (this.listIndexes.containsKey(listKey))
		{
			SortedListIndexes indexes = this.listIndexes.get(listKey);
			int size = indexes.getPoints().size() - 1;
			String key;
			for (int i = 0; i <= size; i++)
			{
				key = indexes.getKey(i);
//				this.cache.remove(key);
				keys.add(key);
				indexes.remove(i);
				indexes.remove(key);
			}
//			this.lock.readLock().unlock();
//			this.lock.writeLock().lock();
			this.listIndexes.remove(listKey);
//			this.lock.readLock().lock();
//			this.lock.writeLock().unlock();
		}
//		this.lock.readLock().unlock();
		this.cache.removeAll(keys);
	}
	
	public abstract void evict(String k, Value v);
	public abstract void forward(String k, Value v);
	public abstract void remove(String k, Value v);
	public abstract void expire(String k, Value v);
	public abstract void update(String k, Value v);
	public abstract void shutdown() throws InterruptedException, IOException;
}
