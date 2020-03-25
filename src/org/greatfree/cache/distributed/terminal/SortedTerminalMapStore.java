package org.greatfree.cache.distributed.terminal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.ehcache.Cache;
import org.ehcache.PersistentCacheManager;
import org.ehcache.event.EventFiring;
import org.ehcache.event.EventOrdering;
import org.ehcache.event.EventType;
import org.greatfree.cache.CacheConfig;
import org.greatfree.cache.CacheListener;
import org.greatfree.cache.CompoundKeyCreatable;
import org.greatfree.cache.db.SortedListIndexDB;
import org.greatfree.cache.distributed.terminal.db.PostfetchDB;
import org.greatfree.cache.factory.SortedListIndexes;
import org.greatfree.cache.local.CacheEventable;
import org.greatfree.cache.local.CacheMapFactorable;
import org.greatfree.exceptions.TerminalServerOverflowedException;
import org.greatfree.util.Builder;
import org.greatfree.util.CollectionSorter;
import org.greatfree.util.Pointing;
import org.greatfree.util.UtilConfig;

import com.google.common.collect.Sets;

/*
 * The version is tested in the Clouds project. 08/22/2018, Bing Li
 */

/*
 * Its counterpart is PointingMapStore, in which one key might not have the value because of eviction. 05/31/2018, Bing Li
 * 
 * The terminal cache keeps the evicted in a DB. Thus, no data will be lost unless the local preset disk is overloaded. So usually, any keys have their own values strictly. 05/31/2018, Bing Li
 *
 * The cache is usually used to keep data at the terminal server in a distributed storage system, such as MServer. It can be used at a standalone server only if the key is required to match with one value. 05/31/2018, Bing Li
 * 
 */

/*
 * For sorted caches, it is preferred to support prefetching ONLY. 05/05/2018, Bing Li
 * 
 * The map-store is used by the MServer, which is the terminal of the entire cluser such that it cannot prefetch and postfetch from any other servers. Instead, it can perform the operations from the local disk ONLY. When data is evicted, it should be kept at the local disk as well. Since the map is sorted, the prefetching is valid. The one, CacheEndMap, cannot do that because data is retrieved by key, which is not sorted. 05/05/2018, Bing Li
 */

// Created: 05/04/2018, Bing Li
public class SortedTerminalMapStore<Value extends Pointing, Factory extends CacheMapFactorable<String, Value>, CompoundKeyCreator extends CompoundKeyCreatable<String>, DescendantComp extends Comparator<Value>, DB extends PostfetchDB<Value>> implements CacheEventable<String, Value>
{
	private final String storeKey;
	private Cache<String, Value> cache;
	private PersistentCacheManager manager;
//	private final int cacheSize;
	private final long cacheSize;
	
	private SortedListIndexDB listIndexesDB;
	private Map<String, SortedListIndexes> listIndexes;

	private DescendantComp comp;
	
	private CacheListener<String, Value, SortedTerminalMapStore<Value, Factory, CompoundKeyCreator, DescendantComp, DB>> listener;

	private CompoundKeyCreator keyCreator;
	
	private DB db;
	
	private AtomicBoolean isDown;
//	private ReentrantReadWriteLock lock;
	
	private final int sortSize;

	private AtomicInteger currentEvictedCount;
	private final int alertEvictedCount;

	public SortedTerminalMapStore(SortedTerminalMapStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, DB> builder)
	{
		this.storeKey = builder.getStoreKey();
		this.manager = builder.getFactory().createManagerInstance(builder.getRootPath(), builder.getStoreKey(), builder.getTotalStoreSize(), builder.getOffheapSizeInMB(), builder.getDiskSizeInMB());
		this.cache = builder.getFactory().createCache(this.manager, this.storeKey);
		this.listener = new CacheListener<String, Value, SortedTerminalMapStore<Value, Factory, CompoundKeyCreator, DescendantComp, DB>>(this);
		this.cache.getRuntimeConfiguration().registerCacheEventListener(this.listener, EventOrdering.ORDERED, EventFiring.ASYNCHRONOUS, EnumSet.of(EventType.CREATED, EventType.EVICTED, EventType.EXPIRED, EventType.REMOVED, EventType.UPDATED));

		this.cacheSize = builder.getCacheSize();
		this.keyCreator = builder.getCreator();
		this.listIndexesDB = new SortedListIndexDB(CacheConfig.getStoreKeysRootPath(CacheConfig.getCachePath(builder.getRootPath(), builder.getStoreKey()), CacheConfig.LIST_KEYS));
		this.listIndexes = this.listIndexesDB.getAllIndexes();
		
		this.comp = builder.getComp();
		this.sortSize = builder.getSortSize();

		this.db = builder.getDB();
		this.isDown = new AtomicBoolean(false);
//		this.lock = new ReentrantReadWriteLock();
		this.currentEvictedCount = new AtomicInteger(this.db.getSize());
		this.alertEvictedCount = builder.getAlertEvictedCount();
	}
	
	public static class SortedTerminalMapStoreBuilder<Value extends Pointing, Factory extends CacheMapFactorable<String, Value>, CompoundKeyCreator extends CompoundKeyCreatable<String>, DescendantComp extends Comparator<Value>, DB extends PostfetchDB<Value>> implements Builder<SortedTerminalMapStore<Value, Factory, CompoundKeyCreator, DescendantComp, DB>>
	{
		private String storeKey;
		private Factory factory;
		private String rootPath;
//		private int totalStoreSize;
		private long totalStoreSize;
//		private int cacheSize;
		private long cacheSize;
		private int offheapSizeInMB;
		private int diskSizeInMB;

		private CompoundKeyCreator keyCreator;
		private DescendantComp comp;
		private int sortSize;

		private DB db;
		private int alertEvictedCount;
		
		public SortedTerminalMapStoreBuilder()
		{
		}
		
		public SortedTerminalMapStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, DB> storeKey(String storeKey)
		{
			this.storeKey = storeKey;
			return this;
		}
		
		public SortedTerminalMapStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, DB> factory(Factory factory)
		{
			this.factory = factory;
			return this;
		}
		
		public SortedTerminalMapStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, DB> rootPath(String rootPath)
		{
			this.rootPath = rootPath;
			return this;
		}
		
		public SortedTerminalMapStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, DB> totalStoreSize(long totalStoreSize)
		{
			this.totalStoreSize = totalStoreSize;
			return this;
		}
		
		public SortedTerminalMapStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, DB> cacheSize(long cacheSize)
		{
			this.cacheSize = cacheSize;
			return this;
		}
		
		public SortedTerminalMapStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, DB> offheapSizeInMB(int offheapSizeInMB)
		{
			this.offheapSizeInMB = offheapSizeInMB;
			return this;
		}
		
		public SortedTerminalMapStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, DB> diskSizeInMB(int diskSizeInMB)
		{
			this.diskSizeInMB = diskSizeInMB;
			return this;
		}

		public SortedTerminalMapStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, DB> comp(DescendantComp comp)
		{
			this.comp = comp;
			return this;
		}

		public SortedTerminalMapStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, DB> sortSize(int sortSize)
		{
			this.sortSize = sortSize;
			return this;
		}

		public SortedTerminalMapStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, DB> keyCreator(CompoundKeyCreator keyCreator)
		{
			this.keyCreator = keyCreator;
			return this;
		}

		public SortedTerminalMapStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, DB> db(DB db)
		{
			this.db = db;
			return this;
		}

		public SortedTerminalMapStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, DB> alertEvictedCount(int alertEvictedCount)
		{
			this.alertEvictedCount = alertEvictedCount;
			return this;
		}

		@Override
		public SortedTerminalMapStore<Value, Factory, CompoundKeyCreator, DescendantComp, DB> build()
		{
			return new SortedTerminalMapStore<Value, Factory, CompoundKeyCreator, DescendantComp, DB>(this);
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
		
		public long getTotalStoreSize()
		{
			return this.totalStoreSize;
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
//		this.lock.writeLock().lock();
		
		this.listIndexesDB.removeAll();
		this.listIndexesDB.putAll(this.listIndexes);
		this.listIndexesDB.dispose();
		
		this.isDown.set(true);
		this.db.dispose();
//		this.lock.writeLock().unlock();
	}

	/*
	 * 
	 * Since now no parent class, the method needs to be implemented. 05/08/2018, Bing Li 
	 *
	 * isDown() is derived from RootCache. 05/08/2018, Bing Li
	 * 
	 */
	public boolean isDown()
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
	
	/*
	 * 
	 * Since now no parent class, the method needs to be implemented. 05/08/2018, Bing Li
	 * 
	 * isEmpty() is derived from RootCache. 05/08/2018, Bing Li
	 * 
	 */
	public boolean isEmpty()
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
	
	public boolean isExisted(String mapKey)
	{
		/*
		this.lock.readLock().lock();
		try
		{
			return this.listIndexes.containsKey(mapKey);
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		*/
		return this.listIndexes.containsKey(mapKey);
	}
	
	public boolean isEmpty(String mapKey)
	{
		/*
		this.lock.readLock().lock();
		try
		{
			if (this.listIndexes.containsKey(mapKey))
			{
				return !(this.listIndexes.get(mapKey).getKeySize() > 0);
			}
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		*/
		if (this.listIndexes.containsKey(mapKey))
		{
			return this.listIndexes.get(mapKey).getKeySize() > 0;
		}
		return true;
	}
	
	public boolean isFull(String mapKey)
	{
		/*
		this.lock.readLock().lock();
		try
		{
			if (this.listIndexes.containsKey(mapKey))
			{
				SortedListIndexes indexes = this.listIndexes.get(mapKey);
				if (indexes != null)
				{
					return indexes.getKeySize() >= this.cacheSize;
				}
				return false;
			}
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		*/
		if (this.listIndexes.containsKey(mapKey))
		{
			SortedListIndexes indexes = this.listIndexes.get(mapKey);
			if (indexes != null)
			{
				return indexes.getKeySize() >= this.cacheSize;
			}
			return false;
		}
		return false;
	}
	
	public Set<String> getMapKeys()
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

	/*
	 * 
	 * Since now no parent class, the method needs to be implemented. 05/08/2018, Bing Li
	 * 
	 * size() is derived from RootCache. 05/08/2018, Bing Li
	 * 
	 */
	public int size()
	{
//		this.lock.readLock().lock();
		Set<String> cacheKeys = this.listIndexes.keySet();
//		this.lock.readLock().unlock();
		int size = 0;
		for (String cacheKey : cacheKeys)
		{
//			this.lock.readLock().lock();
			size += this.listIndexes.get(cacheKey).getKeySize();
//			this.lock.readLock().unlock();
		}
		return size;
	}

	public boolean isMapInStore(String mapKey)
	{
		/*
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
		*/
		if (this.listIndexes.containsKey(mapKey))
		{
			return this.listIndexes.get(mapKey).getKeySize() > 0;
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
//		this.lock.readLock().lock();
		Value value = this.db.get(key);
//		this.lock.readLock().unlock();
		if (value != null)
		{
			// To simplify the code, the data loaded from the DB will NOT be put into the cache. The benefit to do that is not so obvious. In addition, the DB itself has the cache capability. My work just focus on designing a wrapper. 05/31/2018, Bing Li
			return true;
		}
		return false;
	}
	
	public int getSize(String mapKey)
	{
		/*
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
		*/
		if (this.listIndexes.containsKey(mapKey))
		{
			return this.listIndexes.get(mapKey).getKeySize();
		}
		return UtilConfig.NO_COUNT;
	}

	public int getLeftSize(String mapKey, int endIndex)
	{
		/*
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
		*/
		if (this.listIndexes.containsKey(mapKey))
		{		
			return this.listIndexes.get(mapKey).getKeySize() - endIndex;
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
	
	/*
	 * The method is not a proper design. It assumes that the data is huge such that it is impossible and unnecessary to load entire data from the cache. 07/24/2018, Bing Li
	 */
	/*
	public Map<String, Value> get(String mapKey)
	{
		if (this.listIndexes.containsKey(mapKey))
		{
			Map<String, Value> rscs = new HashMap<String, Value>();
			PointingListIndexes indexes = this.listIndexes.get(mapKey);
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
	*/
	
//	public Value getMaxValueResource(String mapKey, Comparator<Map.Entry<String, Value>> c)
	public Value getMaxValueResource(String mapKey)
	{
		/*
		Map<String, Value> rscs = this.get(mapKey);
		String key = CollectionSorter.maxValueKey(rscs, c);
		if (key != null)
		{
			return rscs.get(key);
		}
		return null;
		*/
		return this.get(mapKey, 0);
	}
	
	public Value get(String mapKey, int index)
	{
		String key = UtilConfig.EMPTY_STRING;
//		this.lock.readLock().lock();
		if (this.listIndexes.containsKey(mapKey))
		{
			SortedListIndexes indexes = this.listIndexes.get(mapKey);
			if (index < this.sortSize)
			{
				key = indexes.getKey(index);
			}
			else
			{
				key = indexes.getObsKey(index - this.sortSize);
			}
		}
//		this.lock.readLock().unlock();
		if (!key.equals(UtilConfig.EMPTY_STRING))
		{
			return this.getByKey(key);
		}
		return null;
	}

	public List<Value> get(String mapKey, int startIndex, int endIndex)
	{
		List<Value> rscs = new ArrayList<Value>();
		Value rsc;
		for (int i = startIndex; i <= endIndex; i++)
		{
//			System.out.println("PointingTerminalMapStore-get(): i = " + i);
			rsc = this.get(mapKey, i);
			if (rsc != null)
			{
//				System.out.println("PointingTerminalMapStore-get(): " + i + ") rsc = " + rsc.getPoints());
				rscs.add(rsc);
			}
			else
			{
//				System.out.println("PointingTerminalMapStore-get(): " + i + ") rsc = NULL");
				break;
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
	 * The method is not a proper design. It assumes that the data is huge such that it is impossible and unnecessary to load entire data from the cache. 07/24/2018, Bing Li
	 */
	/*
	public List<Value> getList(String mapKey)
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
		return null;
	}
	*/

	/*
	 * It is not reasonable to load all of the data from a cache. 08/20/2018, Bing Li
	 */
	/*
	public List<String> getKeys(String mapKey)
	{
		String key = UtilConfig.EMPTY_STRING;
		this.lock.readLock().lock();
		if (this.listIndexes.containsKey(mapKey))
		{
			List<String> rscs = new ArrayList<String>();
			int lastIndex = this.listIndexes.get(mapKey).getKeySize() - 1;
			for (int i = 0; i <= lastIndex; i++)
			{
				key = this.listIndexes.get(mapKey).getKey(i);
				if (!key.equals(UtilConfig.EMPTY_STRING))
				{
					rscs.add(this.getByKey(key).getKey());
				}
			}
			return rscs;
		}
		return null;
	}
	*/

	public void put(String mapKey, Value rsc)
	{
//		System.out.println("PointingTerminalMapStore-put(): " + rsc.getKey() + " is being saved ...");
//		this.lock.writeLock().lock();
		String key = this.keyCreator.createCompoundKey(mapKey, rsc.getKey());
		this.cache.put(key, rsc);
//		this.lock.readLock().lock();
		if (!this.listIndexes.containsKey(mapKey))
		{
//			this.lock.readLock().unlock();
//			this.lock.writeLock().lock();
			this.listIndexes.put(mapKey, new SortedListIndexes());
			// It is required to take it out from the cache. Otherwise, the updates cannot be retained in the cache. 06/23/2017, Bing Li
			SortedListIndexes indexes = this.listIndexes.get(mapKey);
			indexes.addKey(key);
			indexes.put(key, rsc.getPoints());
			this.listIndexes.put(mapKey, indexes);
//			this.lock.readLock().lock();
//			this.lock.writeLock().unlock();
		}
		else
		{
			SortedListIndexes indexes = this.listIndexes.get(mapKey);
			// The operation aims to avoid the exception of ConcurrentModificationException. 10/13/2019, Bing Li
			Map<String, Float> allPoints = new HashMap<String, Float>(indexes.getPoints());
			allPoints.put(key, rsc.getPoints());
//			allPoints = CollectionSorter.sortDescendantByValue(allPoints);
			Map<String, Float> sp = CollectionSorter.sortDescendantByValue(allPoints);
			
			int index = 0;
			Set<String> removedKeys = Sets.newHashSet();
			indexes.clearKeys();
//			for (Map.Entry<String, Float> entry : allPoints.entrySet())
			for (Map.Entry<String, Float> entry : sp.entrySet())
			{
				if (index < this.sortSize)
				{
					indexes.addKey(entry.getKey());
				}
				else
				{
//					System.out.println("1) SortedTerminalMapStore-put(): entry = " + entry.getValue() + " is saved in obsolete cache ...");
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

			/*
			if (rsc.getPoints() > indexes.getMinPoints())
			{
				Map<String, Float> allPoints = indexes.getPoints();
				allPoints.put(key, rsc.getPoints());
				allPoints = CollectionSorter.sortDescendantByValue(allPoints);
				indexes.setPoints(allPoints);
				indexes.clearKeys();
				for (Map.Entry<String, Float> entry : allPoints.entrySet())
				{
					indexes.addKey(entry.getKey());
				}
			}
			else
			{
				indexes.addKey(key);
				indexes.put(key, rsc.getPoints());
			}
			*/
//			this.lock.readLock().unlock();
//			this.lock.writeLock().lock();
			this.listIndexes.put(mapKey, indexes);
//			this.lock.readLock().lock();
//			this.lock.writeLock().unlock();
		}
//		this.lock.readLock().unlock();
	}

	public void putAll(String mapKey, List<Value> rscs)
	{
//		System.out.println("PointingTerminalMapStore-putAll(): " + rscs.size() + " values are being saved ...");
		Collections.sort(rscs, this.comp);
		String key;
		for (Value rsc : rscs)
		{
			key = this.keyCreator.createCompoundKey(mapKey, rsc.getKey());
			this.cache.put(key, rsc);
		}
//		this.lock.readLock().lock();
		if (!this.listIndexes.containsKey(mapKey))
		{
//			this.lock.readLock().unlock();
//			this.lock.writeLock().lock();
			this.listIndexes.put(mapKey, new SortedListIndexes());

			// It is required to take it out from the cache. Otherwise, the updates cannot be retained in the cache. 06/23/2017, Bing Li
			SortedListIndexes indexes = this.listIndexes.get(mapKey);
			for (Value rsc : rscs)
			{
				key = this.keyCreator.createCompoundKey(mapKey, rsc.getKey());
//				this.cache.put(key, rsc);
				indexes.addKey(key);
				indexes.put(key, rsc.getPoints());
			}
			
			this.listIndexes.put(mapKey, indexes);
//			this.lock.readLock().lock();
//			this.lock.writeLock().unlock();
		}
		else
		{
			SortedListIndexes indexes = this.listIndexes.get(mapKey);
//			Map<String, Float> allPoints = indexes.getPoints();
			// The operation aims to avoid the exception of ConcurrentModificationException. 10/13/2019, Bing Li
			Map<String, Float> allPoints = new HashMap<String, Float>(indexes.getPoints());
			for (Value rsc : rscs)
			{
				key = this.keyCreator.createCompoundKey(mapKey, rsc.getKey());
//				this.cache.put(key, rsc);
				allPoints.put(key, rsc.getPoints());
			}
//			allPoints = CollectionSorter.sortDescendantByValue(allPoints);
			Map<String, Float> sp = CollectionSorter.sortDescendantByValue(allPoints);
			Set<String> removedKeys = Sets.newHashSet();
			indexes.clearKeys();
			int index = 0;
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
//					System.out.println("2) SortedTerminalMapStore-put(): entry = " + entry.getValue() + " is saved in obsolete cache ...");
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
			this.listIndexes.put(mapKey, indexes);
//			this.lock.readLock().lock();
//			this.lock.writeLock().unlock();
		}
//		this.lock.readLock().unlock();
//		System.out.println("PointingTerminalMapStore-putAll(): " + rscs.size() + " values are saved ...");
	}
	
	// To be reasonable as a storage, the remove methods can be retained. But it suggests to invoke the methods as few as possible. 06/15/2018, Bing Li
	// It is not necessary to remove data for a terminal of a large-scale storage system. 06/15/2018, Bing Li
	public void remove(String mapKey, Set<String> rscKeys)
	{
		Set<String> keys = Sets.newHashSet();
//		this.lock.readLock().lock();
		if (this.listIndexes.containsKey(mapKey))
		{
			String key;
			SortedListIndexes indexes = this.listIndexes.get(mapKey);
			for (String rscKey : rscKeys)
			{
				key = this.keyCreator.createCompoundKey(mapKey, rscKey);
				keys.add(key);
				indexes.remove(indexes.getIndex(key));
				indexes.remove(key);
				if (indexes.getKeySize() <= 0)
				{
//					this.lock.readLock().unlock();
//					this.lock.writeLock().lock();
					this.listIndexes.remove(mapKey);
//					this.lock.readLock().lock();
//					this.lock.writeLock().unlock();
				}
				else
				{
//					this.lock.readLock().unlock();
//					this.lock.writeLock().lock();
					this.listIndexes.put(mapKey, indexes);
//					this.lock.readLock().lock();
//					this.lock.writeLock().unlock();
				}
			}
		}
//		this.lock.readLock().unlock();
		
//		this.lock.writeLock().lock();
		this.db.removeAll(keys);
//		this.lock.writeLock().unlock();
		
		this.cache.removeAll(keys);
	}

	// To be reasonable as a storage, the remove methods can be retained. But it suggests to invoke the methods as few as possible. 06/15/2018, Bing Li
	// It is not necessary to remove data for a terminal of a large-scale storage system. 06/15/2018, Bing Li
	public void remove(String mapKey, String rscKey)
	{
		String key = this.keyCreator.createCompoundKey(mapKey, rscKey);
		if (this.cache.containsKey(key))
		{
			this.cache.remove(key);
		}
		else
		{
//			this.lock.writeLock().lock();
			this.db.remove(key);
//			this.lock.writeLock().unlock();
		}
//		this.lock.readLock().lock();
		if (this.listIndexes.containsKey(mapKey))
		{
			SortedListIndexes indexes = this.listIndexes.get(mapKey);
			indexes.remove(indexes.getIndex(key));
			indexes.remove(key);
			if (indexes.getKeySize() <= 0)
			{
//				this.lock.readLock().unlock();
//				this.lock.writeLock().lock();
				this.listIndexes.remove(mapKey);
//				this.lock.readLock().lock();
//				this.lock.writeLock().unlock();
			}
			else
			{
//				this.lock.readLock().unlock();
//				this.lock.writeLock().lock();
				this.listIndexes.put(mapKey, indexes);
//				this.lock.readLock().lock();
//				this.lock.writeLock().unlock();
			}
		}
//		this.lock.readLock().unlock();
	}

	// To be reasonable as a storage, the remove methods can be retained. But it suggests to invoke the methods as few as possible. 06/15/2018, Bing Li
	// It is not necessary to remove data for a terminal of a large-scale storage system. 06/15/2018, Bing Li
	public void remove(String mapKey, int index)
	{
		String key = UtilConfig.EMPTY_STRING;
//		this.lock.readLock().lock();
		if (this.listIndexes.containsKey(mapKey))
		{
			SortedListIndexes indexes = this.listIndexes.get(mapKey);
			if (index < this.sortSize)
			{
				key = indexes.getKey(index);
				if (!key.equals(UtilConfig.EMPTY_STRING))
				{
					indexes.remove(index);
					indexes.remove(key);
				}
			}
			else
			{
				key = indexes.getKey(index);
				indexes.remove(index);
				indexes.remove(key);
			}
			if (indexes.getKeySize() <= 0)
			{
//				this.lock.readLock().unlock();
//				this.lock.writeLock().lock();
				this.listIndexes.remove(mapKey);
//				this.lock.readLock().lock();
//				this.lock.writeLock().unlock();
			}
			else
			{
//				this.lock.readLock().unlock();
//				this.lock.writeLock().lock();
				this.listIndexes.put(mapKey, indexes);
//				this.lock.readLock().lock();
//				this.lock.writeLock().unlock();
			}
		}
//		this.lock.readLock().unlock();
		
		if (this.cache.containsKey(key))
		{
			this.cache.remove(key);
		}
		else
		{
//			this.lock.writeLock().lock();
			this.db.remove(key);
//			this.lock.writeLock().unlock();
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
//		this.lock.readLock().lock();
		if (this.listIndexes.containsKey(mapKey))
		{
			SortedListIndexes indexes = this.listIndexes.get(mapKey);
			int size = indexes.getKeySize() - 1;
			String key;
			for (int i = 0; i <= size; i++)
			{
				key = indexes.getKey(i);
				keys.add(key);
				indexes.remove(i);
				indexes.remove(key);
			}
//			this.lock.readLock().unlock();
//			this.lock.writeLock().lock();
			this.listIndexes.remove(mapKey);
//			this.lock.readLock().lock();
//			this.lock.writeLock().unlock();
		}
//		this.lock.readLock().unlock();
		
//		this.lock.writeLock().lock();
		this.db.removeAll(keys);
//		this.lock.writeLock().unlock();
		
		this.cache.removeAll(keys);
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
			throw new TerminalServerOverflowedException(this.storeKey);
		}
	}

	@Override
	public void forward(String k, Value v)
	{
	}

	@Override
	public void remove(String k, Value v)
	{
	}

	@Override
	public void expire(String k, Value v)
	{
	}

	@Override
	public void update(String k, Value v)
	{
	}

}
