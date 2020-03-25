package org.greatfree.cache.distributed.store;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.ehcache.Cache;
import org.ehcache.PersistentCacheManager;
import org.ehcache.event.EventFiring;
import org.ehcache.event.EventOrdering;
import org.ehcache.event.EventType;
import org.greatfree.cache.CacheConfig;
import org.greatfree.cache.CacheListener;
import org.greatfree.cache.CacheTiming;
import org.greatfree.cache.CompoundKeyCreatable;
import org.greatfree.cache.db.TimingListIndexDB;
import org.greatfree.cache.factory.TimingListIndexes;
import org.greatfree.cache.local.CacheEventable;
import org.greatfree.cache.local.CacheMapFactorable;
import org.greatfree.util.CollectionSorter;
import org.greatfree.util.FileManager;
import org.greatfree.util.UtilConfig;

import com.google.common.collect.Sets;

/*
 * The version is tested in the Clouds project. 08/22/2018, Bing Li
 */

// Created: 06/08/2018, Bing Li
abstract class TimingCacheStore<Value extends CacheTiming, Factory extends CacheMapFactorable<String, Value>, CompoundKeyCreator extends CompoundKeyCreatable<String>, DescendantComp extends Comparator<Value>> implements CacheEventable<String, Value>
{
	private final String storeKey;
	private Cache<String, Value> cache;
	private PersistentCacheManager manager;
	private final int perCacheSize;

	private TimingListIndexDB listIndexes;
	
	private DescendantComp comp;

	private CacheListener<String, Value, TimingCacheStore<Value, Factory, CompoundKeyCreator, DescendantComp>> listener;

	private CompoundKeyCreator keyCreator;
	
	private boolean isDown;
	private ReentrantReadWriteLock lock;

	public TimingCacheStore(String storeKey, Factory factory, String rootPath, int totalStoreSize, int perCacheSize, int offheapSizeInMB, int diskSizeInMB, CompoundKeyCreator keyCreator, DescendantComp comp)
	{
		this.storeKey = storeKey;
		this.manager = factory.createManagerInstance(FileManager.appendSlash(rootPath), storeKey, totalStoreSize, offheapSizeInMB, diskSizeInMB);
		this.cache = factory.createCache(this.manager, this.storeKey);
		this.listener = new CacheListener<String, Value, TimingCacheStore<Value, Factory, CompoundKeyCreator, DescendantComp>>(this);
		this.cache.getRuntimeConfiguration().registerCacheEventListener(this.listener, EventOrdering.ORDERED, EventFiring.ASYNCHRONOUS, EnumSet.of(EventType.CREATED, EventType.EVICTED, EventType.EXPIRED, EventType.REMOVED, EventType.UPDATED));

		this.perCacheSize = perCacheSize;
		this.keyCreator = keyCreator;

		this.listIndexes = new TimingListIndexDB(CacheConfig.getStoreKeysRootPath(CacheConfig.getCachePath(rootPath, storeKey), CacheConfig.LIST_KEYS));
		
		this.comp = comp;
		this.isDown = false;
		this.lock = new ReentrantReadWriteLock();
	}

	protected void close()
	{
		this.manager.close();
		this.lock.writeLock().lock();
		this.listIndexes.close();
		this.isDown = true;
		this.lock.writeLock().unlock();
	}
	
	protected int getCacheSize()
	{
		return this.perCacheSize;
	}
	
	protected boolean isDown()
	{
		this.lock.readLock().lock();
		try
		{
			return this.isDown;
		}
		finally
		{
			this.lock.readLock().unlock();
		}
	}
	
	protected boolean isEmpty()
	{
		this.lock.readLock().lock();
		try
		{
			return this.listIndexes.isEmpty();
		}
		finally
		{
			this.lock.readLock().unlock();
		}
	}
	
	protected boolean isEmpty(String cacheKey)
	{
		this.lock.readLock().lock();
		try
		{
			if (this.listIndexes.containsKey(cacheKey))
			{
//				return this.listIndexes.get(listKey).getTimings().size() > 0;
				return !(this.listIndexes.get(cacheKey).getKeySize() > 0);
			}
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		return true;
	}

	protected boolean isFull(String cacheKey)
	{
		this.lock.readLock().lock();
		try
		{
			if (this.listIndexes.containsKey(cacheKey))
			{
				TimingListIndexes indexes = this.listIndexes.get(cacheKey);
				return indexes.getTimings().size() >= this.perCacheSize;
			}
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		return false;
	}

	/*
	protected Set<String> getCacheKeys()
	{
		this.lock.readLock().lock();
		try
		{
			return this.listIndexes.getKeys();
		}
		finally
		{
			this.lock.readLock().unlock();
		}
	}
	*/
	
	protected int size()
	{
		this.lock.readLock().lock();
		try
		{
			Set<String> cacheKeys = this.listIndexes.getKeys();
			int size = 0;
			for (String cacheKey : cacheKeys)
			{
				size += this.listIndexes.get(cacheKey).getKeySize();
			}
			return size;
		}
		finally
		{
			this.lock.readLock().unlock();
		}
	}

	protected int getLeftSize(String cacheKey, int endIndex)
	{
		this.lock.readLock().lock();
		try
		{
			if (this.listIndexes.containsKey(cacheKey))
			{		
				return this.listIndexes.get(cacheKey).getTimings().size() - endIndex;
			}
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		return UtilConfig.NO_COUNT;
	}
	
	protected boolean isCacheInStore(String cacheKey)
	{
		this.lock.readLock().lock();
		try
		{
			if (this.listIndexes.containsKey(cacheKey))
			{
				return this.listIndexes.get(cacheKey).getKeySize() > 0;
			}
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		return false;
	}

	protected boolean containsKey(String cacheKey, String rscKey)
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
		return this.cache.containsKey(this.keyCreator.createCompoundKey(cacheKey, rscKey));
	}
	
	protected int getSize(String cacheKey)
	{
		this.lock.readLock().lock();
		try
		{
			if (this.listIndexes.containsKey(cacheKey))
			{
				return this.listIndexes.get(cacheKey).getKeySize();
			}
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		return UtilConfig.NO_COUNT;
	}

	protected void add(String cacheKey, Value rsc)
	{
//		this.lock.writeLock().lock();
		String key = this.keyCreator.createCompoundKey(cacheKey, rsc.getKey());
		this.cache.put(key, rsc);
		this.lock.readLock().lock();
		if (!this.listIndexes.containsKey(cacheKey))
		{
			this.lock.readLock().unlock();
			this.lock.writeLock().lock();
			this.listIndexes.put(cacheKey, new TimingListIndexes());
			
			// It is required to take it out from the cache. Otherwise, the updates cannot be retained in the cache. 06/23/2017, Bing Li
			TimingListIndexes indexes = this.listIndexes.get(cacheKey);
			
			indexes.addKey(key);
			indexes.setTime(key, rsc.getTime());
			
			this.listIndexes.put(cacheKey, indexes);
			this.lock.readLock().lock();
			this.lock.writeLock().unlock();
//			this.cache.put(key, rsc);
		}
		else
		{
			TimingListIndexes indexes = this.listIndexes.get(cacheKey);
			if (rsc.getTime().before(indexes.getOldestTime()))
			{
				// The operation aims to avoid the exception of ConcurrentModificationException. 10/13/2019, Bing Li
//				Map<String, Date> allTimings = indexes.getTimings();
				Map<String, Date> allTimings = new HashMap<String, Date>(indexes.getTimings());
				allTimings.put(key, rsc.getTime());
//				allTimings = CollectionSorter.sortDescendantByValue(allTimings);
				Map<String, Date> sp = CollectionSorter.sortDescendantByValue(allTimings);
				indexes.setTimings(allTimings);
//				for (Map.Entry<String, Date> entry : allTimings.entrySet())
				for (Map.Entry<String, Date> entry : sp.entrySet())
				{
					indexes.addKey(entry.getKey());
				}
			}
			else
			{
				indexes.appendKey(key);
				indexes.setTime(key, rsc.getTime());
			}
			this.lock.readLock().unlock();
			this.lock.writeLock().lock();
			this.listIndexes.put(cacheKey, indexes);
			this.lock.readLock().lock();
			this.lock.writeLock().unlock();
//			this.cache.put(key, rsc);
		}
		this.lock.readLock().unlock();
	}

	protected void addAll(String cacheKey, List<Value> rscs)
	{
		// The below line aims to avoid the exception, ConcurrentModificationException. 10/13/2019, Bing Li
		List<Value> sortedList = new ArrayList<Value>(rscs);
		Collections.sort(sortedList, this.comp);
		String key;
		for (Value rsc : sortedList)
		{
			key = this.keyCreator.createCompoundKey(cacheKey, rsc.getKey());
			this.cache.put(key, rsc);
		}
		this.lock.readLock().lock();
		if (!this.listIndexes.containsKey(cacheKey))
		{
			this.lock.readLock().unlock();
			this.lock.writeLock().lock();
			this.listIndexes.put(cacheKey, new TimingListIndexes());
			
			// It is required to take it out from the cache. Otherwise, the updates cannot be retained in the cache. 06/23/2017, Bing Li
			TimingListIndexes indexes = this.listIndexes.get(cacheKey);
			for (Value rsc : sortedList)
			{
				key = this.keyCreator.createCompoundKey(cacheKey, rsc.getKey());
//				this.cache.put(key, rsc);
				indexes.addKey(key);
				indexes.setTime(key, rsc.getTime());
			}
			this.listIndexes.put(cacheKey, indexes);
			this.lock.readLock().lock();
			this.lock.writeLock().unlock();
		}
		else
		{
			TimingListIndexes indexes = this.listIndexes.get(cacheKey);
//			Map<String, Date> allTimings = indexes.getTimings();
			// The operation aims to avoid the exception of ConcurrentModificationException. 10/13/2019, Bing Li
//			Map<String, Date> allTimings = indexes.getTimings();
			Map<String, Date> allTimings = new HashMap<String, Date>(indexes.getTimings());
			for (Value rsc : sortedList)
			{
				key = this.keyCreator.createCompoundKey(cacheKey, rsc.getKey());
//				this.cache.put(key, rsc);
				if (!allTimings.containsKey(key))
				{
					allTimings.put(key, rsc.getTime());
				}
			}
//			allTimings = CollectionSorter.sortDescendantByValue(allTimings);
			Map<String, Date> sp = CollectionSorter.sortDescendantByValue(allTimings);
			indexes.setTimings(allTimings);
//			for (Map.Entry<String, Date> entry : allTimings.entrySet())
			for (Map.Entry<String, Date> entry : sp.entrySet())
			{
				indexes.addKey(entry.getKey());
			}
			this.lock.readLock().unlock();
			this.lock.writeLock().lock();
			this.listIndexes.put(cacheKey, indexes);
			this.lock.readLock().lock();
			this.lock.writeLock().unlock();
		}
		this.lock.readLock().unlock();
	}

	/*
	protected Date getLatestTime(String cacheKey)
	{
		Value rsc = this.get(cacheKey, this.getMaxIndex(cacheKey));
		if (rsc != null)
		{
			return rsc.getTime();
		}
		return Time.INIT_TIME;
	}
	*/
	
	/*
	protected Date getEarliestTime(String cacheKey)
	{
		Value rsc = this.get(cacheKey, 0);
		if (rsc != null)
		{
			return rsc.getTime();
		}
		return Time.INIT_TIME;
	}
	*/
	
	protected Value get(String cacheKey, int index)
	{
		String key = null;
		this.lock.readLock().lock();
		if (this.listIndexes.containsKey(cacheKey))
		{
			TimingListIndexes indexes = this.listIndexes.get(cacheKey);
			key = indexes.getKey(index);
		}
		this.lock.readLock().unlock();
		if (key != null)
		{
			return this.cache.get(key);
		}
		return null;
	}

	protected List<Value> get(String cacheKey, int startIndex, int endIndex)
	{
		List<Value> rscs = new ArrayList<Value>();
		Value rsc;
		for (int i = startIndex; i <= endIndex; i++)
		{
			rsc = this.get(cacheKey, i);
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

	protected List<String> getKeys(String cacheKey, int startIndex, int endIndex)
	{
		List<String> rscs = new ArrayList<String>();
		Value rsc;
		for (int i = startIndex; i <= endIndex; i++)
		{
			rsc = this.get(cacheKey, i);
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

	protected List<Value> getTop(String cacheKey, int endIndex)
	{
		List<Value> rscs = new ArrayList<Value>();
		Value rsc;
		for (int i = 0; i <= endIndex; i++)
		{
			rsc = this.get(cacheKey, i);
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
	 * The method does not make sense. It is not proper to load all of the data from one cache. 07/22/2018, Bing Li
	 */
	/*
	public List<Value> getList(String cacheKey)
	{
		this.lock.readLock().lock();
		try
		{
			if (this.listIndexes.containsKey(cacheKey))
			{
				List<Value> rscs = new ArrayList<Value>();
				int lastIndex = this.listIndexes.get(cacheKey).getTimings().size() - 1;
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
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		return null;
	}
	*/

	/*
	 * The method does not make sense. It is not proper to load all of the data from one cache. 07/22/2018, Bing Li
	 */
	/*
	public List<String> getKeys(String cacheKey)
	{
		this.lock.readLock().lock();
		try
		{
			if (this.listIndexes.containsKey(cacheKey))
			{
				List<String> rscs = new ArrayList<String>();
				int lastIndex = this.listIndexes.get(cacheKey).getTimings().size() - 1;
				String key;
				for (int i = 0; i <= lastIndex; i++)
				{
					key = this.listIndexes.get(cacheKey).getKey(i);
					if (key != null)
					{
						rscs.add(this.cache.get(key).getKey());
					}
				}
				return rscs;
			}
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		return null;
	}
	*/
	
	protected int getMaxIndex(String cacheKey)
	{
		this.lock.readLock().lock();
		try
		{
			if (this.listIndexes.containsKey(cacheKey))
			{
				return this.listIndexes.get(cacheKey).getTimings().size() - 1;
			}
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		return UtilConfig.NO_INDEX;
	}
	
	protected Value getMinResource(String cacheKey)
	{
		return this.get(cacheKey, 0);
	}

	protected Value get(String cacheKey, String rscKey)
	{
		String key = UtilConfig.EMPTY_STRING;
		this.lock.readLock().lock();
		if (this.listIndexes.containsKey(cacheKey))
		{
//			return this.cache.get(this.keyCreator.createCompoundKey(cacheKey, rscKey));
			key = this.keyCreator.createCompoundKey(cacheKey, rscKey);
		}
		this.lock.readLock().unlock();
		if (!key.equals(UtilConfig.EMPTY_STRING))
		{
			return this.cache.get(key);
		}
		return null;
	}

	/*
	 * The method does not make sense. It is not proper to load all of the data from one cache. 07/22/2018, Bing Li
	 */
	/*
	public Map<String, Value> get(String cacheKey)
	{
		this.lock.readLock().lock();
		try
		{
			if (this.listIndexes.containsKey(cacheKey))
			{		
				Map<String, Value> rscs = new HashMap<String, Value>();
				TimingListIndexes indexes = this.listIndexes.get(cacheKey);
				Set<String> rscKeys = indexes.getTimings().keySet();
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
		finally
		{
			this.lock.readLock().unlock();
		}
	}
	*/

	/*
	public Value getMaxValueResource(String cacheKey, Comparator<Map.Entry<String, Value>> c)
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
	
	protected void removeKey(String cacheKey, String rscKey)
	{
		this.lock.readLock().lock();
		if (this.listIndexes.containsKey(cacheKey))
		{
			String key = this.keyCreator.createCompoundKey(cacheKey, rscKey);
			TimingListIndexes indexes = this.listIndexes.get(cacheKey);
			// Since no lock is set for consistency, it is possible that the index is -1. I am not sure whether that is the reason. I need to ensure that. 08/04/2018, Bing Li
			int index = indexes.getIndex(key);
			if (index != -1)
			{
				indexes.remove(index);
			}
			indexes.remove(key);
			if (indexes.getTimings().size() <= 0)
			{
				this.lock.readLock().unlock();
				this.lock.writeLock().lock();
				this.listIndexes.remove(cacheKey);
				this.lock.readLock().lock();
				this.lock.writeLock().unlock();
			}
			else
			{
				this.lock.readLock().unlock();
				this.lock.writeLock().lock();
				this.listIndexes.put(cacheKey, indexes);
				this.lock.readLock().lock();
				this.lock.writeLock().unlock();
			}
		}
		this.lock.readLock().unlock();
	}

	protected void remove(String cacheKey, Set<String> rscKeys)
	{
		Set<String> keys = Sets.newHashSet();
		this.lock.readLock().lock();
		if (this.listIndexes.containsKey(cacheKey))
		{
			for (String rscKey : rscKeys)
			{
				String key = this.keyCreator.createCompoundKey(cacheKey, rscKey);
//				this.cache.remove(key);
				keys.add(key);
				Map<String, Date> timings = this.listIndexes.get(cacheKey).getTimings();
				int index = 0;
				for (Map.Entry<String, Date> entry : timings.entrySet())
				{
					if (entry.getKey().equals(key))
					{
						break;
					}
					else
					{
						index++;
					}
				}
				this.lock.readLock().unlock();
				this.lock.writeLock().lock();
				this.listIndexes.get(cacheKey).remove(key);
				this.listIndexes.get(cacheKey).remove(index);
				this.lock.readLock().lock();
				this.lock.writeLock().unlock();
			}
		}
		this.lock.readLock().unlock();
		this.cache.removeAll(keys);
	}
	
	protected void remove(String cacheKey, String rscKey)
	{
		String key = UtilConfig.EMPTY_STRING;
		this.lock.readLock().lock();
		if (this.listIndexes.containsKey(cacheKey))
		{
			key = this.keyCreator.createCompoundKey(cacheKey, rscKey);
//			this.cache.remove(key);
			
			Map<String, Date> timings = this.listIndexes.get(cacheKey).getTimings();
			int index = 0;
			for (Map.Entry<String, Date> entry : timings.entrySet())
			{
				if (entry.getKey().equals(key))
				{
					break;
				}
				else
				{
					index++;
				}
			}
			this.lock.readLock().unlock();
			this.lock.writeLock().lock();
			this.listIndexes.get(cacheKey).remove(key);
			this.listIndexes.get(cacheKey).remove(index);
			this.lock.readLock().lock();
			this.lock.writeLock().unlock();
		}
		this.lock.readLock().lock();
		if (!key.equals(UtilConfig.EMPTY_STRING))
		{
			this.cache.remove(key);
		}
	}
	
	protected void removeCacheKey(String cacheKey)
	{
		this.lock.writeLock().lock();
		this.listIndexes.remove(cacheKey);
		this.lock.writeLock().unlock();
	}

	/*
	protected void removeByKey(String cacheKey, String key)
	{
		this.lock.readLock().lock();
		boolean isContained = this.listIndexes.containsKey(cacheKey);
		this.lock.readLock().unlock();
		
		if (isContained)
		{
			this.cache.remove(key);

			this.lock.readLock().lock();
			Map<String, Date> timings = this.listIndexes.get(cacheKey).getTimings();
			this.lock.readLock().unlock();
			
			int index = 0;
			for (Map.Entry<String, Date> entry : timings.entrySet())
			{
				if (entry.getKey().equals(entry.getKey()))
				{
					break;
				}
				else
				{
					index++;
				}
			}
			this.lock.writeLock().lock();
			this.listIndexes.get(cacheKey).remove(key);
			this.listIndexes.get(cacheKey).remove(index);
			this.lock.writeLock().unlock();
		}
	}
	*/

	protected void remove(String cacheKey, int index)
	{
		String key = UtilConfig.EMPTY_STRING;
		this.lock.readLock().lock();
		if (this.listIndexes.containsKey(cacheKey))
		{
			TimingListIndexes indexes = this.listIndexes.get(cacheKey);
			
			key = indexes.getKey(index);
//			this.cache.remove(key);
			indexes.remove(index);
			indexes.remove(key);
			if (indexes.getTimings().size() <= 0)
			{
				this.lock.readLock().unlock();
				this.lock.writeLock().lock();
				this.listIndexes.remove(cacheKey);
				this.lock.readLock().lock();
				this.lock.writeLock().unlock();
			}
			else
			{
				this.lock.readLock().unlock();
				this.lock.writeLock().lock();
				this.listIndexes.put(cacheKey, indexes);
				this.lock.readLock().lock();
				this.lock.writeLock().unlock();
			}
		}
		this.lock.readLock().unlock();
		if (!key.equals(UtilConfig.EMPTY_STRING))
		{
			this.cache.remove(key);
		}
	}
	
	protected void remove(String cacheKey, int startIndex, int endIndex)
	{
		for (int i = startIndex; i <= endIndex; i++)
		{
			this.remove(cacheKey, i);
		}
	}
	
	protected void clear(Set<String> cacheKeys)
	{
		for (String mapKey: cacheKeys)
		{
			this.clear(mapKey);
		}
	}
	
	protected void clear(String cacheKey)
	{
		Set<String> keys = Sets.newHashSet();
		this.lock.readLock().lock();
		if (this.listIndexes.containsKey(cacheKey))
		{
			TimingListIndexes indexes = this.listIndexes.get(cacheKey);
			
			int size = indexes.getTimings().size() - 1;
			String key;
			for (int i = 0; i <= size; i++)
			{
				key = indexes.getKey(i);
//				this.cache.remove(key);
				keys.add(key);
				indexes.remove(i);
				indexes.remove(key);
			}
			this.lock.readLock().unlock();
			this.lock.writeLock().lock();
			this.listIndexes.remove(cacheKey);
			this.lock.readLock().lock();
			this.lock.writeLock().unlock();
		}
		this.lock.readLock().unlock();
		this.cache.removeAll(keys);
	}

	public abstract void evict(String k, Value v);
	public abstract void forward(String k, Value v);
	public abstract void remove(String k, Value v);
	public abstract void expire(String k, Value v);
	public abstract void update(String k, Value v);
}
