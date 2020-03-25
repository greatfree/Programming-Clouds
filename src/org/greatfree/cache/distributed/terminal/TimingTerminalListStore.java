package org.greatfree.cache.distributed.terminal;

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
import org.greatfree.util.FileManager;
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
 * Its counterpart is TimingListStore, in which one key might not have the value because of eviction. 05/31/2018, Bing Li
 *  
 * The terminal cache keeps the evicted in a DB. Thus, no data will be lost unless the local preset disk is overloaded. So usually, any keys have their own values strictly. 05/31/2018, Bing Li
 *
 * The cache is usually used to keep data at the terminal server in a distributed storage system, such as MServer. It can be used at a standalone server only if the key is required to match with one value. 05/31/2018, Bing Li
 * 
 */

// Created: 05/15/2018, Bing Li
class TimingTerminalListStore<Value extends Timing, Factory extends CacheMapFactorable<String, Value>, CompoundKeyCreator extends CompoundKeyCreatable<String>, DescendantComp extends Comparator<Value>, DB extends PostfetchDB<Value>> implements CacheEventable<String, Value>
{
	private final String storeKey;
	private Cache<String, Value> cache;
	private PersistentCacheManager manager;
	private final long perCacheSize;

	private TimingListIndexDB listIndexes;

	private DescendantComp comp;

	private CompoundKeyCreator keyCreator;

	private boolean isDown;
	private ReentrantReadWriteLock lock;
	
	private CacheListener<String, Value, TimingTerminalListStore<Value, Factory, CompoundKeyCreator, DescendantComp, DB>> listener;

	private DB db;
	private AtomicInteger currentEvictedCount;
	private final int alertEvictedCount;
	
	public TimingTerminalListStore(TimingTerminalListStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, DB> builder)
	{
		this.storeKey = builder.getStoreKey();
		this.manager = builder.getFactory().createManagerInstance(FileManager.appendSlash(builder.getRootPath()), builder.getStoreKey(), builder.getTotalStoreSize(), builder.getOffheapSizeInMB(), builder.getDiskSizeInMB());
		this.cache = builder.getFactory().createCache(this.manager, builder.getStoreKey());
		this.listener = new CacheListener<String, Value, TimingTerminalListStore<Value, Factory, CompoundKeyCreator, DescendantComp, DB>>(this);
		this.cache.getRuntimeConfiguration().registerCacheEventListener(this.listener, EventOrdering.ORDERED, EventFiring.ASYNCHRONOUS, EnumSet.of(EventType.CREATED, EventType.EVICTED, EventType.EXPIRED, EventType.REMOVED, EventType.UPDATED));

		this.perCacheSize = builder.getPerCacheSize();
		this.keyCreator = builder.getKeyCreator();
		
		this.listIndexes = new TimingListIndexDB(CacheConfig.getStoreKeysRootPath(CacheConfig.getCachePath(builder.getRootPath(), builder.getStoreKey()), CacheConfig.LIST_KEYS));
		
		this.comp = builder.getComp();
		this.isDown = false;
		this.lock = new ReentrantReadWriteLock();
		
		this.db = builder.getDB();
		this.currentEvictedCount = new AtomicInteger(this.db.getSize());
		this.alertEvictedCount = builder.getAlertEvictedCount();
	}
	
	public static class TimingTerminalListStoreBuilder<Value extends Timing, Factory extends CacheMapFactorable<String, Value>, CompoundKeyCreator extends CompoundKeyCreatable<String>, DescendantComp extends Comparator<Value>, DB extends PostfetchDB<Value>> implements Builder<TimingTerminalListStore<Value, Factory, CompoundKeyCreator, DescendantComp, DB>>
	{
		private String storeKey;
		private Factory factory;
		private long perCacheSize;
		private CompoundKeyCreator keyCreator;
		
		private String rootPath;
		private int totalStoreSize;
		private int offheapSizeInMB;
		private int diskSizeInMB;
		
		private DescendantComp comp;
		private DB db;
		private int alertEvictedCount;

		public TimingTerminalListStoreBuilder()
		{
		}
		
		public TimingTerminalListStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, DB> storeKey(String storeKey)
		{
			this.storeKey = storeKey;
			return this;
		}

		public TimingTerminalListStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, DB> factory(Factory factory)
		{
			this.factory = factory;
			return this;
		}
		
		public TimingTerminalListStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, DB> perCacheSize(long perCacheSize)
		{
			this.perCacheSize = perCacheSize;
			return this;
		}
		
		public TimingTerminalListStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, DB> keyCreator(CompoundKeyCreator keyCreator)
		{
			this.keyCreator = keyCreator;
			return this;
		}

		public TimingTerminalListStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, DB> rootPath(String rootPath)
		{
			this.rootPath = rootPath;
			return this;
		}
		
		public TimingTerminalListStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, DB> totalStoreSize(int totalStoreSize)
		{
			this.totalStoreSize = totalStoreSize;
			return this;
		}
		
		public TimingTerminalListStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, DB> offheapSizeInMB(int offheapSizeInMB)
		{
			this.offheapSizeInMB = offheapSizeInMB;
			return this;
		}

		public TimingTerminalListStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, DB> diskSizeInMB(int diskSizeInMB)
		{
			this.diskSizeInMB = diskSizeInMB;
			return this;
		}

		public TimingTerminalListStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, DB> comp(DescendantComp comp)
		{
			this.comp = comp;
			return this;
		}

		public TimingTerminalListStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, DB> db(DB db)
		{
			this.db = db;
			return this;
		}

		public TimingTerminalListStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, DB> alertEvictedCount(int alertEvictedCount)
		{
			this.alertEvictedCount = alertEvictedCount;
			return this;
		}

		@Override
		public TimingTerminalListStore<Value, Factory, CompoundKeyCreator, DescendantComp, DB> build()
		{
			return new TimingTerminalListStore<Value, Factory, CompoundKeyCreator, DescendantComp, DB>(this);
		}

		public String getStoreKey()
		{
			return this.storeKey;
		}
		
		public Factory getFactory()
		{
			return this.factory;
		}
		
		public long getPerCacheSize()
		{
			return this.perCacheSize;
		}
		
		public CompoundKeyCreator getKeyCreator()
		{
			return this.keyCreator;
		}
		
		public String getRootPath()
		{
			return this.rootPath;
		}
		
		public int getTotalStoreSize()
		{
			return this.totalStoreSize;
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
		
		public DB getDB()
		{
			return this.db;
		}
		
		public int getAlertEvictedCount()
		{
			return this.alertEvictedCount;
		}
	}

	public void close()
	{
		this.manager.close();
		this.lock.writeLock().lock();
		this.listIndexes.close();
		this.db.close();
		this.isDown = true;
		this.lock.writeLock().unlock();
//		this.lock = null;
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
	
	public boolean isEmpty(String listKey)
	{
		this.lock.readLock().lock();
		try
		{
			TimingListIndexes indexes = this.listIndexes.get(listKey);
			if (indexes != null)
			{
				return indexes.getTimings().size() > 0;
			}
			return false;
		}
		finally
		{
			this.lock.readLock().unlock();
		}
	}

	public boolean isFull(String listKey)
	{
		this.lock.readLock().lock();
		try
		{
			if (this.listIndexes.containsKey(listKey))
			{
				TimingListIndexes indexes = this.listIndexes.get(listKey);
				if (indexes != null)
				{
					return indexes.getTimings().size() >= this.perCacheSize;
				}
				return false;
			}
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		return false;
	}

	public int getLeftSize(String listKey, int endIndex)
	{
		this.lock.readLock().lock();
		try
		{
			if (this.listIndexes.containsKey(listKey))
			{		
				return this.listIndexes.get(listKey).getTimings().size() - endIndex;
			}
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		return UtilConfig.NO_COUNT;
	}
	
	public int getMaxIndex(String listKey)
	{
		this.lock.readLock().lock();
		try
		{
			if (this.listIndexes.containsKey(listKey))
			{
				return this.listIndexes.get(listKey).getTimings().size() - 1;
			}
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		return UtilConfig.INIT_INDEX;
	}
	public boolean isListInStore(String listKey)
	{
		this.lock.readLock().lock();
		try
		{
//			return this.listIndexes.isExisted(this.keyCreator.createPrefixKey(listKey));
			return this.listIndexes.containsKey(listKey);
		}
		finally
		{
			this.lock.readLock().unlock();
		}
	}

	public Set<String> getListKeys()
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

	public void add(String listKey, Value rsc)
	{
//		String listKey = this.keyCreator.createPrefixKey(listKey);
		String key = this.keyCreator.createCompoundKey(listKey, rsc.getKey());
		this.cache.put(key, rsc);

		this.lock.readLock().lock();
		if (!this.listIndexes.containsKey(listKey))
		{
			this.listIndexes.put(listKey, new TimingListIndexes());
			// It is required to take it out from the cache. Otherwise, the updates cannot be retained in the cache. 06/23/2017, Bing Li
			TimingListIndexes indexes = this.listIndexes.get(listKey);
//			indexes.addKey(0, key);
			indexes.addKey(key);
			indexes.setTime(key, rsc.getTime());
			this.lock.readLock().unlock();
			this.lock.writeLock().lock();
			this.listIndexes.put(listKey, indexes);
			this.lock.readLock().lock();
			this.lock.writeLock().unlock();
//			this.cache.put(key, rsc);
		}
		else
		{
			TimingListIndexes indexes = this.listIndexes.get(listKey);
			if (rsc.getTime().before(indexes.getOldestTime()))
			{
//				Map<String, Date> allTimings = indexes.getTimings();
				// The operation aims to avoid the exception of ConcurrentModificationException. 10/13/2019, Bing Li
				Map<String, Date> allTimings = new HashMap<String, Date>(indexes.getTimings());
				allTimings.put(key, rsc.getTime());
//				allTimings = CollectionSorter.sortDescendantByValue(allTimings);
				Map<String, Date> sp = CollectionSorter.sortDescendantByValue(allTimings);
				indexes.setTimings(allTimings);
//				int index = 0;
				for (Map.Entry<String, Date> entry : sp.entrySet())
				{
//					indexes.addKey(index++, entry.getKey());
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
			this.listIndexes.put(listKey, indexes);
			this.lock.readLock().lock();
			this.lock.writeLock().unlock();
//			this.cache.put(key, rsc);
		}
		this.lock.readLock().unlock();
	}
	
	public void add(String listKey, List<Value> rscs)
	{
		Collections.sort(rscs, this.comp);
		String key;
		for (Value rsc : rscs)
		{
			key = this.keyCreator.createCompoundKey(listKey, rsc.getKey());
			this.cache.put(key, rsc);
		}
		this.lock.readLock().lock();
		if (!this.listIndexes.containsKey(listKey))
		{
			this.lock.readLock().unlock();
			this.lock.writeLock().lock();
			this.listIndexes.put(listKey, new TimingListIndexes());
			// It is required to take it out from the cache. Otherwise, the updates cannot be retained in the cache. 06/23/2017, Bing Li
			TimingListIndexes indexes = this.listIndexes.get(listKey);
			for (Value rsc : rscs)
			{
				key = this.keyCreator.createCompoundKey(listKey, rsc.getKey());
//				this.cache.put(key, rsc);
				indexes.addKey(key);
				indexes.setTime(key, rsc.getTime());
			}
			this.listIndexes.put(listKey, indexes);
			this.lock.readLock().lock();
			this.lock.writeLock().unlock();
		}
		else
		{
			TimingListIndexes indexes = this.listIndexes.get(listKey);
//			Map<String, Date> allTimings = indexes.getTimings();
			// The operation aims to avoid the exception of ConcurrentModificationException. 10/13/2019, Bing Li
			Map<String, Date> allTimings = new HashMap<String, Date>(indexes.getTimings());
			for (Value rsc : rscs)
			{
				key = this.keyCreator.createCompoundKey(listKey, rsc.getKey());
//				this.cache.put(key, rsc);
				allTimings.put(key, rsc.getTime());
			}
			Map<String, Date> sp = CollectionSorter.sortDescendantByValue(allTimings);
			indexes.setTimings(allTimings);
			for (Map.Entry<String, Date> entry : sp.entrySet())
			{
				indexes.addKey(entry.getKey());
			}
			this.lock.readLock().unlock();
			this.lock.writeLock().lock();
			this.listIndexes.put(listKey, indexes);
			this.lock.readLock().lock();
			this.lock.writeLock().unlock();
		}
		this.lock.readLock().unlock();
	}

	public Value get(String listKey, int index)
	{
		String key = null;
		this.lock.readLock().lock();
		try
		{
			if (this.listIndexes.containsKey(listKey))
			{
				TimingListIndexes indexes = this.listIndexes.get(listKey);
				key = indexes.getKey(index);
			}
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		if (key != null)
		{
			Value rsc = this.cache.get(key);
			if (rsc != null)
			{
				return rsc;
			}
			else
			{
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
		}
		return null;
	}

	public List<Value> get(String listKey, int startIndex, int endIndex)
	{
		List<Value> rscs = new ArrayList<Value>();
		Value rsc;
		for (int i = startIndex; i <= endIndex; i++)
		{
			rsc = this.get(listKey, i);
			if (rsc != null)
			{
				rscs.add(rsc);
			}
		}
		return rscs;
	}

	public List<String> getKeys(String listKey, int startIndex, int endIndex)
	{
		List<String> rscs = new ArrayList<String>();
		Value rsc;
		for (int i = startIndex; i <= endIndex; i++)
		{
			rsc = this.get(listKey, i);
			if (rsc != null)
			{
				rscs.add(rsc.getKey());
			}
		}
		return rscs;
	}

	public List<Value> getTop(String listKey, int endIndex)
	{
		List<Value> rscs = new ArrayList<Value>();
		Value rsc;
		for (int i = 0; i <= endIndex; i++)
		{
			rsc = this.get(listKey, i);
			if (rsc != null)
			{
				rscs.add(rsc);
			}
		}
		return rscs;
	}

	/*
	 * It is not reasonable to get all of the data from a cache. 08/21/2018, Bing Li
	 */
	/*
	public List<Value> get(String listKey)
	{
		this.lock.readLock().lock();
		try
		{
			if (this.listIndexes.containsKey(listKey))
			{
				List<Value> rscs = new ArrayList<Value>();
				int lastIndex = this.listIndexes.get(listKey).getTimings().size() - 1;
				String key;
				Value rsc;
				for (int i = 0; i <= lastIndex; i++)
				{
					key = this.listIndexes.get(listKey).getKey(i);
					if (key != null)
					{
						rsc = this.cache.get(key);
						if (rsc != null)
						{
							rscs.add(rsc);
						}
						else
						{
							rsc = this.db.get(key);
							if (rsc != null)
							{
								rscs.add(rsc);
							}
						}
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

	public List<String> getKeys(String listKey)
	{
		List<String> rscs = new ArrayList<String>();
		List<String> keys = new ArrayList<String>();
		this.lock.readLock().lock();
		if (this.listIndexes.containsKey(listKey))
		{
			int lastIndex = this.listIndexes.get(listKey).getTimings().size() - 1;
			String key;
			for (int i = 0; i <= lastIndex; i++)
			{
				key = this.listIndexes.get(listKey).getKey(i);
				if (key != null)
				{
					keys.add(key);
				}
			}
		}
		this.lock.readLock().unlock();

		for (String entry : keys)
		{
			Value rsc = this.cache.get(entry);
			if (rsc != null)
			{
				rscs.add(rsc.getKey());
			}
			else
			{
				rsc = this.db.get(entry);
				if (rsc != null)
				{
					rscs.add(rsc.getKey());
				}
			}
		}
		return rscs;
	}

	public int getSize(String listKey)
	{
		this.lock.readLock().lock();
		try
		{
			if (this.listIndexes.containsKey(listKey))
			{
				return this.listIndexes.get(listKey).getTimings().size();
			}
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		return UtilConfig.NO_COUNT;
	}

	public void remove(String listKey, int index)
	{
		String key = null;
		this.lock.readLock().lock();
		if (this.listIndexes.containsKey(listKey))
		{
			TimingListIndexes indexes = this.listIndexes.get(listKey);
			key = indexes.getKey(index);
			indexes.remove(index);
			indexes.remove(key);
			if (indexes.getTimings().size() <= 0)
			{
				this.lock.readLock().unlock();
				this.lock.writeLock().lock();
				this.listIndexes.remove(listKey);
				this.lock.readLock().lock();
				this.lock.writeLock().unlock();
			}
			else
			{
				this.lock.readLock().unlock();
				this.lock.writeLock().lock();
				this.listIndexes.put(listKey, indexes);
				this.lock.readLock().lock();
				this.lock.writeLock().unlock();
			}
		}
		this.lock.readLock().unlock();
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
	
	public void remove(String listKey, int startIndex, int endIndex)
	{
		for (int i = startIndex; i <= endIndex; i++)
		{
			this.remove(listKey, i);
		}
	}
	
	public void clear(Set<String> listKeys)
	{
		for (String listKey: listKeys)
		{
			this.clear(listKey);
		}
	}

	public void clear(String listKey)
	{
		Set<String> keys = Sets.newHashSet();
		this.lock.readLock().lock();
//		String listKey = this.keyCreator.createPrefixKey(listKey);
		if (this.listIndexes.containsKey(listKey))
		{
			TimingListIndexes indexes = this.listIndexes.get(listKey);
			int size = indexes.getTimings().size() - 1;
			String key;
			for (int i = 0; i <= size; i++)
			{
				key = indexes.getKey(i);
				if (key != null)
				{
					keys.add(key);
				}
				indexes.remove(i);
				indexes.remove(key);
			}
			this.lock.readLock().unlock();
			this.lock.writeLock().lock();
			this.listIndexes.remove(listKey);
			this.lock.readLock().lock();
			this.lock.writeLock().unlock();
		}
		this.lock.readLock().unlock();

		this.lock.readLock().unlock();
		this.lock.writeLock().lock();
		this.db.removeAll(keys);
		this.lock.readLock().lock();
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
