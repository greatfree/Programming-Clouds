package org.greatfree.cache.distributed.terminal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.ehcache.Cache;
import org.ehcache.PersistentCacheManager;
import org.ehcache.event.EventFiring;
import org.ehcache.event.EventOrdering;
import org.ehcache.event.EventType;
import org.greatfree.cache.CacheConfig;
import org.greatfree.cache.CacheListener;
import org.greatfree.cache.CompoundKeyCreatable;
import org.greatfree.cache.db.TimingListIndexDB;
import org.greatfree.cache.distributed.terminal.db.PostfetchDB;
import org.greatfree.cache.factory.TimingListIndexes;
import org.greatfree.cache.local.CacheEventable;
import org.greatfree.cache.local.CacheMapFactorable;
import org.greatfree.exceptions.TerminalServerOverflowedException;
import org.greatfree.util.Builder;
import org.greatfree.util.CollectionSorter;
import org.greatfree.util.Timing;
import org.greatfree.util.UtilConfig;

import com.google.common.collect.Sets;

/*
 * It is replaced by the Pointing-Based caches. 08/22/2018, Bing Li
 */

/*
 * 
 *  The locking is updated in the Clouds project. 08/22/2018, Bing Li
 * 
 * Its counterpart is TimingMapStore, in which one key might not have the value because of eviction. 05/31/2018, Bing Li
 *  
 * The terminal cache keeps the evicted in a DB. Thus, no data will be lost unless the local preset disk is overloaded. So usually, any keys have their own values strictly. 05/31/2018, Bing Li
 *
 * The cache is usually used to keep data at the terminal server in a distributed storage system, such as MServer. It can be used at a standalone server only if the key is required to match with one value. 05/31/2018, Bing Li
 * 
 */

// Created: 05/14/2018, Bing Li
class TimingTerminalMapStore<Value extends Timing, Factory extends CacheMapFactorable<String, Value>, CompoundKeyCreator extends CompoundKeyCreatable<String>, DescendantComp extends Comparator<Value>, DB extends PostfetchDB<Value>> implements CacheEventable<String, Value>
{
	private final String storeKey;
	private Cache<String, Value> cache;
	private PersistentCacheManager manager;
	private final long perCacheSize;

	private TimingListIndexDB listIndexes;

	private DescendantComp comp;
	
	private CacheListener<String, Value, TimingTerminalMapStore<Value, Factory, CompoundKeyCreator, DescendantComp, DB>> listener;

	private CompoundKeyCreator keyCreator;
	
	private DB db;
	
	private boolean isDown;
	private ReentrantReadWriteLock lock;
	
	private AtomicInteger currentEvictedCount;
	private final int alertEvictedCount;

	public TimingTerminalMapStore(TimingTerminalMapStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, DB> builder)
	{
		this.storeKey = builder.getStoreKey();
		this.manager = builder.getFactory().createManagerInstance(builder.getRootPath(), builder.getStoreKey(), builder.getTotalStoreSize(), builder.getOffheapSizeInMB(), builder.getDiskSizeInMB());
		this.cache = builder.getFactory().createCache(this.manager, this.storeKey);
		this.listener = new CacheListener<String, Value, TimingTerminalMapStore<Value, Factory, CompoundKeyCreator, DescendantComp, DB>>(this);
		this.cache.getRuntimeConfiguration().registerCacheEventListener(this.listener, EventOrdering.ORDERED, EventFiring.ASYNCHRONOUS, EnumSet.of(EventType.CREATED, EventType.EVICTED, EventType.EXPIRED, EventType.REMOVED, EventType.UPDATED));

		this.perCacheSize = builder.getCacheSize();
		this.keyCreator = builder.getCreator();
		this.listIndexes = new TimingListIndexDB(CacheConfig.getStoreKeysRootPath(CacheConfig.getCachePath(builder.getRootPath(), builder.getStoreKey()), CacheConfig.LIST_KEYS));
		
		this.comp = builder.getComp();
		this.db = builder.getDB();
		this.isDown = false;
		this.lock = new ReentrantReadWriteLock();
		this.currentEvictedCount = new AtomicInteger(this.db.getSize());
		this.alertEvictedCount = builder.getAlertEvictedCount();
	}

	public static class TimingTerminalMapStoreBuilder<Value extends Timing, Factory extends CacheMapFactorable<String, Value>, CompoundKeyCreator extends CompoundKeyCreatable<String>, DescendantComp extends Comparator<Value>, DB extends PostfetchDB<Value>> implements Builder<TimingTerminalMapStore<Value, Factory, CompoundKeyCreator, DescendantComp, DB>>
	{
		private String storeKey;
		private Factory factory;
		private String rootPath;
		private int totalStoreSize;
		private int cacheSize;
		private int offheapSizeInMB;
		private int diskSizeInMB;

		private CompoundKeyCreator keyCreator;
		private DescendantComp comp;
		private DB db;
		private int alertEvictedCount;
		
		public TimingTerminalMapStoreBuilder()
		{
		}
		
		public TimingTerminalMapStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, DB> storeKey(String storeKey)
		{
			this.storeKey = storeKey;
			return this;
		}
		
		public TimingTerminalMapStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, DB> factory(Factory factory)
		{
			this.factory = factory;
			return this;
		}
		
		public TimingTerminalMapStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, DB> rootPath(String rootPath)
		{
			this.rootPath = rootPath;
			return this;
		}
		
		public TimingTerminalMapStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, DB> totalStoreSize(int totalStoreSize)
		{
			this.totalStoreSize = totalStoreSize;
			return this;
		}
		
		public TimingTerminalMapStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, DB> cacheSize(int cacheSize)
		{
			this.cacheSize = cacheSize;
			return this;
		}
		
		public TimingTerminalMapStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, DB> offheapSizeInMB(int offheapSizeInMB)
		{
			this.offheapSizeInMB = offheapSizeInMB;
			return this;
		}
		
		public TimingTerminalMapStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, DB> diskSizeInMB(int diskSizeInMB)
		{
			this.diskSizeInMB = diskSizeInMB;
			return this;
		}

		public TimingTerminalMapStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, DB> comp(DescendantComp comp)
		{
			this.comp = comp;
			return this;
		}

		public TimingTerminalMapStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, DB> keyCreator(CompoundKeyCreator keyCreator)
		{
			this.keyCreator = keyCreator;
			return this;
		}

		public TimingTerminalMapStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, DB> db(DB db)
		{
			this.db = db;
			return this;
		}

		public TimingTerminalMapStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, DB> alertEvictedCount(int alertEvictedCount)
		{
			this.alertEvictedCount = alertEvictedCount;
			return this;
		}

		@Override
		public TimingTerminalMapStore<Value, Factory, CompoundKeyCreator, DescendantComp, DB> build()
		{
			return new TimingTerminalMapStore<Value, Factory, CompoundKeyCreator, DescendantComp, DB>(this);
		}
		
		public String getStoreKey()
		{
			return this.storeKey;
		}
		
		public Factory getFactory()
		{
			return this.factory;
		}
		
		public String getRootPath()
		{
			return this.rootPath;
		}
		
		public int getTotalStoreSize()
		{
			return this.totalStoreSize;
		}
		
		public int getCacheSize()
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
		
		public CompoundKeyCreator getCreator()
		{
			return this.keyCreator;
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

	public void dispose() throws InterruptedException, IOException
	{
		this.manager.close();
		this.lock.writeLock().lock();
		this.listIndexes.close();
		this.isDown = true;
		this.db.dispose();
		this.lock.writeLock().unlock();
	}

	public boolean isDown()
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

	public boolean isEmpty()
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
	
	public boolean isExisted(String mapKey)
	{
		this.lock.readLock().lock();
		try
		{
			return this.listIndexes.containsKey(mapKey);
		}
		finally
		{
			this.lock.readLock().unlock();
		}
	}
	
	public boolean isEmpty(String mapKey)
	{
		this.lock.readLock().lock();
		try
		{
			if (this.listIndexes.containsKey(mapKey))
			{
				return this.listIndexes.get(mapKey).getKeySize() > 0;
			}
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		return true;
	}
	
	public boolean isFull(String mapKey)
	{
		this.lock.readLock().lock();
		try
		{
			if (this.listIndexes.containsKey(mapKey))
			{
				TimingListIndexes indexes = this.listIndexes.get(mapKey);
				return indexes.getKeySize() >= this.perCacheSize;
			}
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		return false;
	}
	
	public Set<String> getMapKeys()
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

	public int size()
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

	public boolean isMapInStore(String mapKey)
	{
		this.lock.readLock().lock();
		try
		{
			if (this.listIndexes.containsKey(mapKey))
			{
				return this.listIndexes.get(mapKey).getKeySize() > 0;
			}
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		return false;
	}

	public boolean containsKey(String mapKey, String rscKey)
	{
		String key = this.keyCreator.createCompoundKey(mapKey, rscKey);
		boolean isExisted = this.cache.containsKey(key);
		if (isExisted)
		{
			return true;
		}
		this.lock.readLock().lock();
		Value value = this.db.get(key);
		this.lock.readLock().unlock();
		if (value != null)
		{
			// To simplify the code, the data loaded from the DB will NOT be put into the cache. The benefit to do that is not so obvious. In addition, the DB itself has the cache capability. My work just focus on designing a wrapper. 05/31/2018, Bing Li
//				this.cache.put(key, value);
			return true;
		}
		return false;
	}

	public int getSize(String mapKey)
	{
		this.lock.readLock().lock();
		try
		{
			if (this.listIndexes.containsKey(mapKey))
			{
				return this.listIndexes.get(mapKey).getKeySize();
			}
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		return UtilConfig.NO_COUNT;
	}

	public int getLeftSize(String mapKey, int endIndex)
	{
		this.lock.readLock().lock();
		try
		{
			if (this.listIndexes.containsKey(mapKey))
			{		
				return this.listIndexes.get(mapKey).getKeySize() - endIndex;
			}
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		return UtilConfig.NO_COUNT;
	}

	public Value get(String mapKey, String rscKey)
	{
		String key = this.keyCreator.createCompoundKey(mapKey, rscKey);
		Value rsc = this.cache.get(key);
		if (rsc != null)
		{
			return rsc;
		}
		// To simplify the code, the data loaded from the DB will NOT be put into the cache. The benefit to do that is not so obvious. In addition, the DB itself has the cache capability. My work just focus on designing a wrapper. 05/31/2018, Bing Li
		/*
		Value value = this.db.get(key);
		if (value != null)
		{
			this.cache.put(key, value);
		}
		return value;
		*/
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
	
	private Value getByKey(String key)
	{
		Value rsc = this.cache.get(key);
		if (rsc != null)
		{
			return rsc;
		}
		// To simplify the code, the data loaded from the DB will NOT be put into the cache. The benefit to do that is not so obvious. In addition, the DB itself has the cache capability. My work just focus on designing a wrapper. 05/31/2018, Bing Li
		/*
		Value value = this.db.get(key);
		if (value != null)
		{
			this.cache.put(key, value);
		}
		return value;
		*/
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
	
	/*
	 * It is not reasonable to get all of the data from the cache. 08/21/2018, Bing Li
	 */
	/*
	public Map<String, Value> get(String mapKey)
	{
		this.lock.readLock().lock();
		try
		{
			if (this.listIndexes.containsKey(mapKey))
			{
				Map<String, Value> rscs = new HashMap<String, Value>();
				TimingListIndexes indexes = this.listIndexes.get(mapKey);
//				Set<String> rscKeys = indexes.getTimings().keySet();
				List<String> rscKeys = indexes.getKeys();
				Value rsc;
				for (String key : rscKeys)
				{
					rsc = this.get(mapKey, key);
					rscs.put(key, rsc);
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
	public Value getMaxValueResource(String mapKey, Comparator<Map.Entry<String, Value>> c)
	{
		Map<String, Value> rscs = this.get(mapKey);
		String key = CollectionSorter.maxValueKey(rscs, c);
		if (key != null)
		{
			return rscs.get(key);
		}
		return null;
	}
	*/
	
	public int getMaxIndex(String mapKey)
	{
		this.lock.readLock().lock();
		try
		{
			return this.listIndexes.get(mapKey).getKeySize() - 1;
		}
		finally
		{
			this.lock.readLock().unlock();
		}
	}
	
	public Value get(String mapKey, int index)
	{
		this.lock.readLock().lock();
		try
		{
			if (this.listIndexes.containsKey(mapKey))
			{
				TimingListIndexes indexes = this.listIndexes.get(mapKey);
				String key = indexes.getKey(index);
				if (key != null)
				{
					return this.getByKey(key);
				}
			}
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		return null;
	}

	public List<Value> get(String mapKey, int startIndex, int endIndex)
	{
		List<Value> rscs = new ArrayList<Value>();
		Value rsc;
		for (int i = startIndex; i <= endIndex; i++)
		{
			rsc = this.get(mapKey, i);
			if (rsc != null)
			{
				rscs.add(rsc);
			}
		}
		return rscs;
	}
	
	public List<String> getKeys(String mapKey, int startIndex, int endIndex)
	{
		List<String> rscs = new ArrayList<String>();
		Value rsc;
		for (int i = startIndex; i <= endIndex; i++)
		{
			rsc = this.get(mapKey, i);
			if (rsc != null)
			{
				rscs.add(rsc.getKey());
			}
		}
		return rscs;
	}

	public List<Value> getTop(String mapKey, int endIndex)
	{
		List<Value> rscs = new ArrayList<Value>();
		Value rsc;
		for (int i = 0; i <= endIndex; i++)
		{
			rsc = this.get(mapKey, i);
			if (rsc != null)
			{
				rscs.add(rsc);
			}
		}
		return rscs;
	}

	/*
	 * It is not reasonable to get all of the data from the cache. 08/21/2018, Bing Li
	 */
	/*
	public List<Value> getList(String mapKey)
	{
		this.lock.readLock().lock();
		try
		{
			if (this.listIndexes.containsKey(mapKey))
			{
				List<Value> rscs = new ArrayList<Value>();
				int lastIndex = this.listIndexes.get(mapKey).getKeySize() - 1;
				String key;
				for (int i = 0; i <= lastIndex; i++)
				{
					key = this.listIndexes.get(mapKey).getKey(i);
					if (key != null)
					{
						rscs.add(this.getByKey(key));
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

	public List<String> getKeys(String mapKey)
	{
		List<String> rscs = new ArrayList<String>();
		List<String> keys = new ArrayList<String>();
		this.lock.readLock().lock();
		if (this.listIndexes.containsKey(mapKey))
		{
			int lastIndex = this.listIndexes.get(mapKey).getKeySize() - 1;
			String key;
			for (int i = 0; i <= lastIndex; i++)
			{
				key = this.listIndexes.get(mapKey).getKey(i);
				if (key != null)
				{
//						rscs.add(this.getByKey(key).getKey());
					keys.add(key);
				}
			}
			return rscs;
		}
		this.lock.readLock().unlock();
		for (String entry : keys)
		{
			rscs.add(this.getByKey(entry).getKey());
		}
		return rscs;
	}

	public void put(String mapKey, Value rsc)
	{
		String key = this.keyCreator.createCompoundKey(mapKey, rsc.getKey());
		this.cache.put(key, rsc);
		this.lock.readLock().lock();
		if (!this.listIndexes.containsKey(mapKey))
		{
			this.lock.readLock().unlock();
			this.lock.writeLock().lock();
			this.listIndexes.put(mapKey, new TimingListIndexes());
			// It is required to take it out from the cache. Otherwise, the updates cannot be retained in the cache. 06/23/2017, Bing Li
			TimingListIndexes indexes = this.listIndexes.get(mapKey);
			indexes.addKey(key);
			indexes.setTime(key, rsc.getTime());
			this.listIndexes.put(mapKey, indexes);
//			this.cache.put(key, rsc);
			this.lock.readLock().lock();
			this.lock.writeLock().unlock();
		}
		else
		{
			TimingListIndexes indexes = this.listIndexes.get(mapKey);
			if (rsc.getTime().after(indexes.getOldestTime()))
			{
//				Map<String, Date> allTimings = indexes.getTimings();
				// The operation aims to avoid the exception of ConcurrentModificationException. 10/13/2019, Bing Li
				Map<String, Date> allTimings = new HashMap<String, Date>(indexes.getTimings());
				allTimings.put(key, rsc.getTime());
//				allTimings = CollectionSorter.sortDescendantByValue(allTimings);
				Map<String, Date> sp = CollectionSorter.sortDescendantByValue(allTimings);
				indexes.setTimings(allTimings);
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
			this.listIndexes.put(mapKey, indexes);
//			this.cache.put(key, rsc);
			this.lock.readLock().lock();
			this.lock.writeLock().unlock();
		}
		this.lock.readLock().unlock();
	}

	public void putAll(String mapKey, List<Value> rscs)
	{
		Collections.sort(rscs, this.comp);
		String key;
		for (Value rsc : rscs)
		{
			key = this.keyCreator.createCompoundKey(mapKey, rsc.getKey());
			this.cache.put(key, rsc);
		}
		this.lock.readLock().lock();
		if (!this.listIndexes.containsKey(mapKey))
		{
			this.lock.readLock().unlock();
			this.lock.writeLock().lock();
			this.listIndexes.put(mapKey, new TimingListIndexes());
			// It is required to take it out from the cache. Otherwise, the updates cannot be retained in the cache. 06/23/2017, Bing Li
			TimingListIndexes indexes = this.listIndexes.get(mapKey);
			for (Value rsc : rscs)
			{
				key = this.keyCreator.createCompoundKey(mapKey, rsc.getKey());
//				this.cache.put(key, rsc);
				indexes.addKey(key);
				indexes.setTime(key, rsc.getTime());
			}
			this.listIndexes.put(mapKey, indexes);
			this.lock.readLock().lock();
			this.lock.writeLock().unlock();
		}
		else
		{
			TimingListIndexes indexes = this.listIndexes.get(mapKey);
//			Map<String, Date> allTimings = indexes.getTimings();
			// The operation aims to avoid the exception of ConcurrentModificationException. 10/13/2019, Bing Li
			Map<String, Date> allTimings = new HashMap<String, Date>(indexes.getTimings());
			for (Value rsc : rscs)
			{
				key = this.keyCreator.createCompoundKey(mapKey, rsc.getKey());
//				this.cache.put(key, rsc);
				allTimings.put(key, rsc.getTime());
			}
//			allTimings = CollectionSorter.sortDescendantByValue(allTimings);
			Map<String, Date> sp = CollectionSorter.sortDescendantByValue(allTimings);
			indexes.setTimings(allTimings);
			for (Map.Entry<String, Date> entry : sp.entrySet())
			{
				indexes.addKey(entry.getKey());
			}
			this.lock.readLock().unlock();
			this.lock.writeLock().lock();
			this.listIndexes.put(mapKey, indexes);
			this.lock.readLock().lock();
			this.lock.writeLock().unlock();
		}
		this.lock.readLock().unlock();
	}
	
	public void remove(String mapKey, Set<String> rscKeys)
	{
		Set<String> keys = Sets.newHashSet();
		this.lock.readLock().lock();
		if (this.listIndexes.containsKey(mapKey))
		{
			String key;
			TimingListIndexes indexes = this.listIndexes.get(mapKey);
			for (String rscKey : rscKeys)
			{
				key = this.keyCreator.createCompoundKey(mapKey, rscKey);
				keys.add(key);
				indexes.remove(indexes.getIndex(key));
				indexes.remove(key);
				if (indexes.getKeySize() <= 0)
				{
					this.lock.readLock().unlock();
					this.lock.writeLock().lock();
					this.listIndexes.remove(mapKey);
					this.lock.readLock().lock();
					this.lock.writeLock().unlock();
				}
				else
				{
					this.lock.readLock().unlock();
					this.lock.writeLock().lock();
					this.listIndexes.put(mapKey, indexes);
					this.lock.readLock().lock();
					this.lock.writeLock().unlock();
				}
			}
		}
		this.lock.readLock().unlock();
		this.cache.removeAll(keys);
		this.db.removeAll(keys);
	}
	
	public void remove(String mapKey, String rscKey)
	{
		String key = UtilConfig.EMPTY_STRING;
		this.lock.readLock().lock();
		if (this.listIndexes.containsKey(mapKey))
		{
			key = this.keyCreator.createCompoundKey(mapKey, rscKey);
			TimingListIndexes indexes = this.listIndexes.get(mapKey);
			indexes.remove(indexes.getIndex(key));
			indexes.remove(key);
			if (indexes.getKeySize() <= 0)
			{
				this.lock.readLock().unlock();
				this.lock.writeLock().lock();
				this.listIndexes.remove(mapKey);
				this.lock.readLock().lock();
				this.lock.writeLock().unlock();
			}
			else
			{
				this.lock.readLock().unlock();
				this.lock.writeLock().lock();
				this.listIndexes.put(mapKey, indexes);
				this.lock.readLock().lock();
				this.lock.writeLock().unlock();
			}
		}
		this.lock.readLock().unlock();
		if (!key.equals(UtilConfig.EMPTY_STRING))
		{
			if (this.cache.containsKey(key))
			{
				this.cache.remove(key);
			}
			else
			{
				this.lock.readLock().unlock();
				this.lock.writeLock().lock();
				this.db.remove(key);
				this.lock.readLock().lock();
				this.lock.writeLock().unlock();
			}
		}
	}

	public void remove(String mapKey, int index)
	{
		String key = null;
		this.lock.readLock().lock();
		if (this.listIndexes.containsKey(mapKey))
		{
			TimingListIndexes indexes = this.listIndexes.get(mapKey);
			key = indexes.getKey(index);
			indexes.remove(index);
			indexes.remove(key);
			if (indexes.getKeySize() <= 0)
			{
				this.lock.readLock().unlock();
				this.lock.writeLock().lock();
				this.listIndexes.remove(mapKey);
				this.lock.readLock().lock();
				this.lock.writeLock().unlock();
			}
			else
			{
				this.lock.readLock().unlock();
				this.lock.writeLock().lock();
				this.listIndexes.put(mapKey, indexes);
				this.lock.readLock().lock();
				this.lock.writeLock().unlock();
			}
		}
		this.lock.readLock().unlock();
		if (key != null)
		{
			if (this.cache.containsKey(key))
			{
				this.cache.remove(key);
			}
			else
			{
				this.lock.readLock().unlock();
				this.lock.writeLock().lock();
				this.db.remove(key);
				this.lock.readLock().lock();
				this.lock.writeLock().unlock();
			}
		}
	}

	public void remove(String mapKey, int startIndex, int endIndex)
	{
		for (int i = startIndex; i <= endIndex; i++)
		{
			this.remove(mapKey, i);
		}
	}

	public void clear(Set<String> mapKeys)
	{
		for (String listKey: mapKeys)
		{
			this.clear(listKey);
		}
	}

	public void clear(String mapKey)
	{
		Set<String> keys = Sets.newHashSet();
		this.lock.readLock().lock();
		if (this.listIndexes.containsKey(mapKey))
		{
			TimingListIndexes indexes = this.listIndexes.get(mapKey);
			int size = indexes.getKeySize() - 1;
			String key;
			for (int i = 0; i <= size; i++)
			{
				key = indexes.getKey(i);
				keys.add(key);
				indexes.remove(i);
				indexes.remove(key);
			}
			this.lock.readLock().unlock();
			this.lock.writeLock().lock();
			this.listIndexes.remove(mapKey);
			this.lock.readLock().lock();
			this.lock.writeLock().unlock();
		}
		this.lock.readLock().unlock();

		this.lock.writeLock().lock();
		this.db.removeAll(keys);
		this.lock.writeLock().unlock();

		this.cache.removeAll(keys);
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
			throw new TerminalServerOverflowedException(this.storeKey);
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
