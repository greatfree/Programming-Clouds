package org.greatfree.cache.distributed.store;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.EnumSet;
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
import org.greatfree.cache.db.LinearIndexDB;
import org.greatfree.cache.db.LinearIndexEntity;
import org.greatfree.cache.local.CacheEventable;
import org.greatfree.cache.local.CacheMapFactorable;
import org.greatfree.exceptions.IndexOutOfRangeException;
import org.greatfree.util.FileManager;
import org.greatfree.util.UtilConfig;

import com.google.common.collect.Sets;

/*
 * The version is created and tested in the Clouds project. 08/22/2018, Bing Li
 */

// Created: 08/13/2018, Bing Li
abstract class QueueStore<Value extends Serializable, Factory extends CacheMapFactorable<String, Value>, CompoundKeyCreator extends CompoundKeyCreatable<Integer>> implements CacheEventable<String, Value>
{
	private final String storeKey;
	private Cache<String, Value> cache;
	private PersistentCacheManager manager;
//	private final int cacheSize;
	private final long cacheSize;
	
//	private PersistableMap<String, QueueIndexes, QueueIndexesFactory, StringKeyDB> keys;
	private LinearIndexDB keysDB;
	private Map<String, LinearIndexEntity> keys;

	private CacheListener<String, Value, QueueStore<Value, Factory, CompoundKeyCreator>> listener;

	private CompoundKeyCreator keyCreator;
	
	private AtomicBoolean isDown;
//	private ReentrantReadWriteLock lock;

//	public QueueStore(String storeKey, Factory factory, String rootPath, int totalStoreSize, int cacheSize, int offheapSizeInMB, int diskSizeInMB, CompoundKeyCreator keyCreator)
	public QueueStore(String storeKey, Factory factory, String rootPath, long totalStoreSize, long cacheSize, int offheapSizeInMB, int diskSizeInMB, CompoundKeyCreator keyCreator)
	{	
		this.storeKey = storeKey;
		this.manager = factory.createManagerInstance(FileManager.appendSlash(rootPath), storeKey, totalStoreSize, offheapSizeInMB, diskSizeInMB);
		this.cache = factory.createCache(this.manager, this.storeKey);
		this.listener = new CacheListener<String, Value, QueueStore<Value, Factory, CompoundKeyCreator>>(this);
		this.cache.getRuntimeConfiguration().registerCacheEventListener(this.listener, EventOrdering.ORDERED, EventFiring.ASYNCHRONOUS, EnumSet.of(EventType.CREATED, EventType.EVICTED, EventType.EXPIRED, EventType.REMOVED, EventType.UPDATED));

		this.cacheSize = cacheSize;
		this.keyCreator = keyCreator;
		
		/*
		this.keys = new PersistableMap.PersistableMapBuilder<String, QueueIndexes, QueueIndexesFactory, StringKeyDB>()
				.factory(new QueueIndexesFactory())
//				.rootPath(CacheConfig.getStoreKeysRootPath(rootPath, CacheConfig.QUEUE_KEYS))
				.rootPath(CacheConfig.getStoreKeysRootPath(CacheConfig.getCachePath(rootPath, storeKey), CacheConfig.QUEUE_KEYS))
				.cacheKey(CacheConfig.QUEUE_KEYS)
				.cacheSize(totalStoreSize)
				.offheapSizeInMB(offheapSizeInMB)
				.diskSizeInMB(diskSizeInMB)
//				.db(StringKeyDBPool.SHARED().getDB(CacheConfig.getStoreKeysDBPath(rootPath)))
				.db(StringKeyDBPool.SHARED().getDB(CacheConfig.getStoreKeysDBPath(CacheConfig.getCachePath(rootPath, storeKey))))
				.build();
				*/
		
		this.keysDB = new LinearIndexDB(CacheConfig.getStoreKeysRootPath(CacheConfig.getCachePath(rootPath, storeKey), CacheConfig.QUEUE_KEYS));
		this.keys = this.keysDB.getAllIndexes();
		
		this.isDown = new AtomicBoolean(false);
//		this.lock = new ReentrantReadWriteLock();
	}

	protected void closeAtBase()
	{
		this.manager.close();
//		this.lock.writeLock().lock();
		
		this.keysDB.removeAll();
		this.keysDB.putAll(this.keys);
		this.keysDB.close();
		
		this.isDown.set(true);
//		this.lock.writeLock().unlock();
//		this.lock = null;
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

	protected boolean isQueueInStoreAtBase(String queueKey)
	{
		/*
		this.lock.readLock().lock();
		try
		{
//			return this.keys.isExisted(this.keyCreator.createPrefixKey(queueKey));
			return this.keys.containsKey(queueKey);
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		*/
		return this.keys.containsKey(queueKey);
	}
	
	protected int getQueueSizeAtBase(String queueKey)
	{
		/*
		this.lock.readLock().lock();
		try
		{
			if (this.keys.containsKey(queueKey))
			{
//				QueueIndexes indexes = this.keys.get(queueKey);
				LinearIndexEntity indexes = this.keys.get(queueKey);
				return indexes.getTailIndex() - indexes.getHeadIndex() + 1;
			}
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		*/
		if (this.keys.containsKey(queueKey))
		{
			LinearIndexEntity indexes = this.keys.get(queueKey);
			return indexes.getTailIndex() - indexes.getHeadIndex() + 1;
		}
		return UtilConfig.NO_QUEUE_SIZE;
	}
	
	protected boolean isEmptyAtBase(String queueKey)
	{
		/*
		this.lock.readLock().lock();
		try
		{
			if (this.keys.containsKey(queueKey))
			{
//				QueueIndexes indexes = this.keys.get(queueKey);
				LinearIndexEntity indexes = this.keys.get(queueKey);
				return indexes.getTailIndex() - indexes.getHeadIndex() <= 0;
			}
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		*/
		if (this.keys.containsKey(queueKey))
		{
			LinearIndexEntity indexes = this.keys.get(queueKey);
			return indexes.getTailIndex() - indexes.getHeadIndex() <= 0;
		}
		return false;
	}

	protected long getEmptySizeAtBase(String queueKey)
	{
		/*
		this.lock.readLock().lock();
		try
		{
//			String queueKey = this.keyCreator.createPrefixKey(queueKey);
			if (this.keys.containsKey(queueKey))
			{
//				QueueIndexes indexes = this.keys.get(queueKey);
				LinearIndexEntity indexes = this.keys.get(queueKey);
				return this.cacheSize - (indexes.getTailIndex() - indexes.getHeadIndex());
			}
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		*/
		if (this.keys.containsKey(queueKey))
		{
			LinearIndexEntity indexes = this.keys.get(queueKey);
			return this.cacheSize - (indexes.getTailIndex() - indexes.getHeadIndex());
		}
		return UtilConfig.NO_QUEUE_SIZE;
	}

	protected Set<String> getCacheKeysAtBase()
	{
		/*
		this.lock.readLock().lock();
		try
		{
			return this.keys.keySet();
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		*/
		return this.keys.keySet();
	}
	
	protected boolean isFullAtBase(String queueKey)
	{
		/*
		this.lock.readLock().lock();
		try
		{
//			String queueKey = this.keyCreator.createPrefixKey(queueKey);
			if (this.keys.containsKey(queueKey))
			{
//				QueueIndexes indexes = this.keys.get(queueKey);
				LinearIndexEntity indexes = this.keys.get(queueKey);
				return indexes.getTailIndex() - indexes.getHeadIndex() >= this.cacheSize;
			}
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		*/
		if (this.keys.containsKey(queueKey))
		{
			LinearIndexEntity indexes = this.keys.get(queueKey);
			return indexes.getTailIndex() - indexes.getHeadIndex() >= this.cacheSize;
		}
		return false;
	}

	protected Value getAtBase(String queueKey, int index) throws IndexOutOfRangeException
	{
		if (index >= 0)
		{
			boolean isExceptional = false;
			int size = 0;
			String key = UtilConfig.EMPTY_STRING;
//			this.lock.readLock().lock();
			if (this.keys.containsKey(queueKey))
			{
				LinearIndexEntity indexes = this.keys.get(queueKey);
				int headIndex = indexes.getHeadIndex();
				int tailIndex = indexes.getTailIndex();
				size = tailIndex - headIndex;
				if (index < size)
				{
					key = this.keyCreator.createCompoundKey(queueKey, index);
				}
				else
				{
					isExceptional = true;
				}
			}
//			this.lock.readLock().unlock();
			if (!isExceptional)
			{
				return this.cache.get(key);
			}
			else
			{
				throw new IndexOutOfRangeException(queueKey, index);
			}
		}
		else
		{
			throw new IndexOutOfRangeException(queueKey, index);
		}
	}

	protected void enqueueAtBase(String queueKey, Value v)
	{
		String key;
//		this.lock.readLock().lock();
		if (!this.keys.containsKey(queueKey))
		{
//			Set<String> keys = Sets.newHashSet();
//			this.keys.put(queueKey, new QueueIndexes(keys));
//			this.keys.put(queueKey, new QueueIndexes());
//			this.lock.readLock().unlock();
//			this.lock.writeLock().lock();
			this.keys.put(queueKey, new LinearIndexEntity(queueKey, 0, 0));
			key = this.keyCreator.createCompoundKey(queueKey, this.keys.get(queueKey).getTailIndex());
			LinearIndexEntity indexes = this.keys.get(queueKey);
			indexes.setTailIndex(indexes.getTailIndex() + 1);
			this.keys.put(queueKey, indexes);
//			this.lock.readLock().lock();
//			this.lock.writeLock().unlock();
		}
		else
		{
//			this.lock.readLock().unlock();
//			this.lock.writeLock().lock();
			key = this.keyCreator.createCompoundKey(queueKey, this.keys.get(queueKey).getTailIndex());
			LinearIndexEntity indexes = this.keys.get(queueKey);
			indexes.setTailIndex(indexes.getTailIndex() + 1);
			this.keys.put(queueKey, indexes);
//			this.lock.readLock().lock();
//			this.lock.writeLock().unlock();
		}
//		this.lock.readLock().unlock();
//		QueueIndexes indexes = this.keys.get(queueKey);
		this.cache.put(key, v);
	}
	
	protected void enqueueAtBase(String queueKey, List<Value> rscs)
	{
		for (Value rsc : rscs)
		{
			this.enqueueAtBase(queueKey, rsc);
		}
	}
	
	protected List<Value> dequeueAtBase(String queueKey, int n)
	{
		List<Value> rscs = new ArrayList<Value>();
		Value rsc;
		for (int i = 0; i < n; i++)
		{
			rsc = this.dequeueAtBase(queueKey);
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
	
	protected Value dequeueAtBase(String queueKey)
	{
		String key = UtilConfig.EMPTY_STRING;
//			String queueKey = this.keyCreator.createPrefixKey(queueKey);
//		this.lock.readLock().lock();
		if (this.keys.containsKey(queueKey))
		{
			LinearIndexEntity indexes = this.keys.get(queueKey);
			if (indexes.getHeadIndex() < indexes.getTailIndex())
			{
				key = this.keyCreator.createCompoundKey(queueKey, indexes.getHeadIndex());
				indexes.setHeadIndex(indexes.getHeadIndex() + 1);
				if (indexes.getHeadIndex() >= indexes.getTailIndex())
				{
					indexes.setHeadIndex(0);
					indexes.setTailIndex(0);
				}
//				this.lock.readLock().unlock();
//				this.lock.writeLock().lock();
				this.keys.put(queueKey, indexes);
//				this.lock.readLock().lock();
//				this.lock.writeLock().unlock();
			}
		}
//		this.lock.readLock().unlock();
		if (!key.equals(UtilConfig.EMPTY_STRING))
		{
			Value rsc = this.cache.get(key);
			this.cache.remove(key);
			return rsc;
		}
		return null;
	}
	
	protected List<Value> peekAtBase(String queueKey, int n)
	{
		List<Value> rscs = new ArrayList<Value>();
		Value rsc;
		List<String> keys = new ArrayList<String>();
//		String queueKey = this.keyCreator.createPrefixKey(queueKey);
//		this.lock.readLock().lock();
		if (this.keys.containsKey(queueKey))
		{
//			QueueIndexes indexes = this.keys.get(queueKey);
			LinearIndexEntity indexes = this.keys.get(queueKey);
			
			int headIndex = indexes.getHeadIndex();
			int tailIndex = indexes.getTailIndex();
			for (int i = 0; i < n; i++)
			{
				if (headIndex < tailIndex)
				{
//					rsc = this.cache.get(this.keyCreator.createCompoundKey(queueKey, headIndex));
					keys.add(this.keyCreator.createCompoundKey(queueKey, headIndex));
					/*
					if (rsc != null)
					{
						rscs.add(rsc);
						headIndex++;
					}
					else
					{
						break;
					}
					*/
					headIndex++;
				}
			}
		}
//		this.lock.readLock().unlock();
		for (String key : keys)
		{
			rsc = this.cache.get(key);
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
	
	protected Value peekAtBase(String queueKey)
	{
		String key = UtilConfig.EMPTY_STRING;
//		this.lock.readLock().lock();
//			String queueKey = this.keyCreator.createPrefixKey(queueKey);
		if (this.keys.containsKey(queueKey))
		{
//				QueueIndexes indexes = this.keys.get(queueKey);
			LinearIndexEntity indexes = this.keys.get(queueKey);
			if (indexes.getHeadIndex() < indexes.getTailIndex())
			{
//					return this.cache.get(this.keyCreator.createCompoundKey(queueKey, indexes.getHeadIndex()));
				key = this.keyCreator.createCompoundKey(queueKey, indexes.getHeadIndex());
			}
		}
//		this.lock.readLock().unlock();
		if (!key.equals(UtilConfig.EMPTY_STRING))
		{
			return this.cache.get(key);
		}
		return null;
	}

	protected List<Value> peekAtBase(String queueKey, int startIndex, int endIndex) throws IndexOutOfRangeException
	{
		if (startIndex >= 0 && endIndex >= 0)
		{
			List<Value> rscs = new ArrayList<Value>();
			List<String> keys = new ArrayList<String>();
			Value rsc;
			boolean isExceptional = false;
			int size = 0;
//			this.lock.readLock().lock();
			if (this.keys.containsKey(queueKey))
			{
				LinearIndexEntity indexes = this.keys.get(queueKey);
				int headIndex = indexes.getHeadIndex();
				int tailIndex = indexes.getTailIndex();
				size = tailIndex - headIndex;
				int pSize = endIndex - startIndex + 1;
				if (startIndex < size && pSize <= size)
				{
					for (int i = startIndex; i <= endIndex; i++)
					{
						keys.add(this.keyCreator.createCompoundKey(queueKey, headIndex + i));
					}
				}
				else if (startIndex < size && pSize > size)
				{
					endIndex = size - startIndex + 1;
					for (int i = startIndex; i < endIndex; i++)
					{
						keys.add(this.keyCreator.createCompoundKey(queueKey, headIndex + i));
					}
				}
				else
				{
					isExceptional = true;
				}
			}
//			this.lock.readLock().unlock();
			if (!isExceptional)
			{
				for (String entry : keys)
				{
					rsc = this.cache.get(entry);
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
			else
			{
				throw new IndexOutOfRangeException(queueKey, startIndex, endIndex, size);
			}
		}
		else
		{
			throw new IndexOutOfRangeException(queueKey, startIndex, endIndex);
		}
	}

	protected void removeQueuesAtBase(Set<String> queueKeys)
	{
		for (String queueKey : queueKeys)
		{
			this.removeQueueAtBase(queueKey);
		}
	}
	
	protected void removeQueueAtBase(String queueKey)
	{
		Set<String> keys = Sets.newHashSet();
//		this.lock.readLock().lock();
		if (this.keys.containsKey(queueKey))
		{
//			QueueIndexes indexes = this.keys.get(queueKey);
			LinearIndexEntity indexes = this.keys.get(queueKey);
			for (int i = indexes.getHeadIndex(); i < indexes.getTailIndex(); i++)
			{
//				this.cache.remove(this.keyCreator.createCompoundKey(queueKey, i));
				keys.add(this.keyCreator.createCompoundKey(queueKey, i));
			}
			indexes.setHeadIndex(0);
			indexes.setTailIndex(0);
//			this.keys.put(queueKey, indexes);
//			this.lock.readLock().unlock();
//			this.lock.writeLock().lock();
			this.keys.remove(queueKey);
//			this.lock.readLock().lock();
//			this.lock.writeLock().unlock();
		}
//		this.lock.readLock().unlock();
		this.cache.removeAll(keys);
	}
	
	protected boolean isEmptyAtBase()
	{
		/*
		this.lock.readLock().lock();
		try
		{
			return this.keys.isEmpty();
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		*/
		return this.keys.isEmpty();
	}

	protected int getStoreSizeAtBase()
	{
		/*
		this.lock.readLock().lock();
		try
		{
			return this.keys.size();
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		*/
		return this.keys.size();
	}
}
