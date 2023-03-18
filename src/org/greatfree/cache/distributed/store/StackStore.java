package org.greatfree.cache.distributed.store;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.EnumSet;
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
import org.greatfree.cache.db.LinearIndexDB;
import org.greatfree.cache.db.LinearIndexEntity;
import org.greatfree.cache.local.CacheEventable;
import org.greatfree.cache.local.CacheMapFactorable;
import org.greatfree.exceptions.IndexOutOfRangeException;
import org.greatfree.util.FileManager;
import org.greatfree.util.UtilConfig;

/*
 * The version is tested in the Clouds project. 08/22/2018, Bing Li
 */

/*
 * The class is used to lower the effort to implement the full-featured or semi-comprehensive distributed stack. 06/19/2018, Bing Li
 */

// Created: 06/19/2018, Bing Li
abstract class StackStore<Value extends Serializable, Factory extends CacheMapFactorable<String, Value>, CompoundKeyCreator extends CompoundKeyCreatable<Integer>> implements CacheEventable<String, Value>
{
	private final String storeKey;
	private Cache<String, Value> cache;
	private PersistentCacheManager manager;
//	private final int cacheSize;
	private final long cacheSize;

	private LinearIndexDB keysDB;
	private Map<String, LinearIndexEntity> keys;
	
	private CacheListener<String, Value, StackStore<Value, Factory, CompoundKeyCreator>> listener;

	private CompoundKeyCreator keyCreator;

	private AtomicBoolean isDown;
	
	// The lock is updated to protect the DB. The cache is out of the scope of the lock. I need to test whether it solves the issue of blocking. 08/12/2018, Bing Li
//	private ReentrantReadWriteLock lock;

//	public StackStore(String storeKey, Factory factory, String rootPath, int totalStoreSize, int perCacheSize, int offheapSizeInMB, int diskSizeInMB, CompoundKeyCreator keyCreator)
	public StackStore(String storeKey, Factory factory, String rootPath, long totalStoreSize, long cacheSize, int offheapSizeInMB, int diskSizeInMB, CompoundKeyCreator keyCreator)
	{	
		this.storeKey = storeKey;
		this.manager = factory.createManagerInstance(FileManager.appendSlash(rootPath), storeKey, totalStoreSize, offheapSizeInMB, diskSizeInMB);
		this.cache = factory.createCache(this.manager, this.storeKey);
		this.listener = new CacheListener<String, Value, StackStore<Value, Factory, CompoundKeyCreator>>(this);
		this.cache.getRuntimeConfiguration().registerCacheEventListener(this.listener, EventOrdering.ORDERED, EventFiring.ASYNCHRONOUS, EnumSet.of(EventType.CREATED, EventType.EVICTED, EventType.EXPIRED, EventType.REMOVED, EventType.UPDATED));

		this.cacheSize = cacheSize;
		this.keyCreator = keyCreator;
		
		this.keysDB = new LinearIndexDB(CacheConfig.getStoreKeysRootPath(CacheConfig.getCachePath(rootPath, storeKey), CacheConfig.STACK_KEYS));
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
	
	protected Set<String> getCacheKeysAtBase()
	{
		return this.keys.keySet();
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

	protected int getStackSizeAtBase(String stackKey)
	{
		/*
		this.lock.readLock().lock();
		try
		{
			if (this.keys.containsKey(stackKey))
			{
				LinearIndexEntity indexes = this.keys.get(stackKey);
				return indexes.getTailIndex() - indexes.getHeadIndex() + 1;
			}
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		*/
		if (this.keys.containsKey(stackKey))
		{
			LinearIndexEntity indexes = this.keys.get(stackKey);
			return indexes.getTailIndex() - indexes.getHeadIndex() + 1;
		}
		return UtilConfig.NO_STACK_SIZE;
	}

	protected boolean isStackInStoreAtBase(String stackKey)
	{
		/*
		this.lock.readLock().lock();
		try
		{
			return this.keys.containsKey(stackKey);
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		*/
		return this.keys.containsKey(stackKey);
	}
	
	protected boolean isEmptyAtBase(String stackKey)
	{
		/*
		this.lock.readLock().lock();
		try
		{
			if (this.keys.containsKey(stackKey))
			{
				LinearIndexEntity indexes = this.keys.get(stackKey);
				return indexes.getTailIndex() - indexes.getHeadIndex() <= 0;
			}
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		*/
		if (this.keys.containsKey(stackKey))
		{
			LinearIndexEntity indexes = this.keys.get(stackKey);
			return indexes.getTailIndex() - indexes.getHeadIndex() <= 0;
		}
		return false;
	}
	
	protected boolean isFullAtBase(String stackKey)
	{
		/*
		this.lock.readLock().lock();
		try
		{
			if (this.keys.containsKey(stackKey))
			{
				LinearIndexEntity indexes = this.keys.get(stackKey);
				return indexes.getTailIndex() - indexes.getHeadIndex() >= this.cacheSize;
			}
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		*/
		if (this.keys.containsKey(stackKey))
		{
			LinearIndexEntity indexes = this.keys.get(stackKey);
			return indexes.getTailIndex() - indexes.getHeadIndex() >= this.cacheSize;
		}
		return false;
	}

	protected Value getAtBase(String stackKey, int index) throws IndexOutOfRangeException
	{
		if (index >= 0)
		{
			boolean isExceptional = false;
			int size = 0;
			String key = UtilConfig.EMPTY_STRING;
//			this.lock.readLock().lock();
			if (this.keys.containsKey(stackKey))
			{
				LinearIndexEntity indexes = this.keys.get(stackKey);
				int headIndex = indexes.getHeadIndex();
				int tailIndex = indexes.getTailIndex();
				size = tailIndex - headIndex;
				if (index < size)
				{
					key = this.keyCreator.createCompoundKey(stackKey, index + 1);
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
				throw new IndexOutOfRangeException(stackKey, index);
			}
		}
		else
		{
			throw new IndexOutOfRangeException(stackKey, index);
		}
	}

	protected void pushAtBase(String stackKey, Value v)
	{
//		System.out.println("StackStore-push(): value being pushed ...");
		String key;
//		this.lock.readLock().lock();
		if (!this.keys.containsKey(stackKey))
		{
//			this.lock.readLock().unlock();
//			this.lock.writeLock().lock();
			this.keys.put(stackKey, new LinearIndexEntity(stackKey, 0, 0));
			LinearIndexEntity indexes = this.keys.get(stackKey);
			key = this.keyCreator.createCompoundKey(stackKey, indexes.getTailIndex() + 1);
			indexes.setTailIndex(indexes.getTailIndex() + 1);
			this.keys.put(stackKey, indexes);
//			this.lock.readLock().lock();
//			this.lock.writeLock().unlock();
		}
		else
		{
			LinearIndexEntity indexes = this.keys.get(stackKey);
			key = this.keyCreator.createCompoundKey(stackKey, indexes.getTailIndex() + 1);
			indexes.setTailIndex(indexes.getTailIndex() + 1);
//			this.lock.readLock().unlock();
//			this.lock.writeLock().lock();
			this.keys.put(stackKey, indexes);
//			this.lock.readLock().lock();
//			this.lock.writeLock().unlock();
		}
//		this.lock.readLock().unlock();
		this.cache.put(key, v);
//		System.out.println("StackStore-push(): value is pushed ...");
	}

	protected void pushAllAtBase(String stackKey, List<Value> rscs)
	{
		for (Value rsc : rscs)
		{
			this.pushAtBase(stackKey, rsc);
		}
//		for (Value rsc : rscs)
		/*
		int maxIndex = rscs.size() - 1;
		for (int i = maxIndex; i >= 0; i--)
		{
			this.push(stackKey, rscs.get(i));
		}
		*/
	}
	
	protected void pushBottomAtBase(String stackKey, Value v)
	{
		String key;
//		this.lock.readLock().lock();
		if (!this.keys.containsKey(stackKey))
		{
//			this.lock.readLock().unlock();
//			this.lock.writeLock().lock();
			this.keys.put(stackKey, new LinearIndexEntity(stackKey, 0, 0));
			LinearIndexEntity indexes = this.keys.get(stackKey);
			key = this.keyCreator.createCompoundKey(stackKey, indexes.getHeadIndex());
			indexes.setHeadIndex(indexes.getHeadIndex() - 1);
			this.keys.put(stackKey, indexes);
//			this.lock.readLock().lock();
//			this.lock.writeLock().unlock();
		}
		else
		{
			LinearIndexEntity indexes = this.keys.get(stackKey);
			key = this.keyCreator.createCompoundKey(stackKey, indexes.getHeadIndex());
			indexes.setHeadIndex(indexes.getHeadIndex() - 1);
//			this.lock.readLock().unlock();
//			this.lock.writeLock().lock();
			this.keys.put(stackKey, indexes);
//			this.lock.readLock().lock();
//			this.lock.writeLock().unlock();
		}
//		this.lock.readLock().unlock();
		this.cache.put(key, v);
	}
	
	protected void pushAllBottomAtBase(String stackKey, List<Value> rscs)
	{
		for (int i = 0; i < rscs.size(); i++)
		{
			this.pushBottomAtBase(stackKey, rscs.get(i));
		}
	}

	protected List<Value> popAtBase(String stackKey, int n)
	{
		List<Value> rscs = new ArrayList<Value>();
		Value rsc;
		for (int i = 0; i < n; i++)
		{
			rsc = this.popAtBase(stackKey);
			if (rsc != null)
			{
				rscs.add(rsc);
			}
			else
			{
//				System.out.println("StackStore-pop(): stack = " + stackKey + ", rsc is NULL");
				break;
			}
		}
		return rscs;
	}

	protected Value popAtBase(String stackKey)
	{
		String key = UtilConfig.EMPTY_STRING;
//		System.out.println("1) StackStore-pop(): stackKey = " + stackKey);
//		this.lock.readLock().lock();
//			System.out.println("2) StackStore-pop(): stackKey = " + stackKey);
		if (this.keys.containsKey(stackKey))
		{
			LinearIndexEntity indexes = this.keys.get(stackKey);
			if (indexes.getHeadIndex() <= indexes.getTailIndex())
			{
				key = this.keyCreator.createCompoundKey(stackKey, indexes.getTailIndex());
//					Value rsc = this.cache.get(key);
				
				// The removal is critical. It should not be commented out. Otherwise, when popping data, the order messes up. I still do not understand the reason. 08/12/2018, Bing Li
				// It is not necessary to remove. New data must overwrite the popped data. 08/13/2018, Bing Li
//					this.cache.remove(key);
				indexes.setTailIndex(indexes.getTailIndex() - 1);
				if (indexes.getHeadIndex() > indexes.getTailIndex())
				{
					indexes.setHeadIndex(0);
					indexes.setTailIndex(-1);
				}
//				this.lock.readLock().unlock();
//				this.lock.writeLock().lock();
				this.keys.put(stackKey, indexes);
//				this.lock.readLock().lock();
//				this.lock.writeLock().unlock();
			}
		}
//		this.lock.readLock().unlock();
		if (!key.equals(UtilConfig.EMPTY_STRING))
		{
			Value v = this.cache.get(key);
			// The removal does not affect the order of the cache. But it is necessary to remove, especially when the disk space is limited. 08/12/2018, Bing Li
			// The removal is critical. It should not be commented out. Otherwise, when popping data, the order messes up. I still do not understand the reason. 08/12/2018, Bing Li
			// It is not necessary to remove. New data must overwrite the popped data. 08/13/2018, Bing Li
			this.cache.remove(key);
			return v;
		}
		return null;
	}
	
	protected Value popBottomAtBase(String stackKey)
	{
		String key = UtilConfig.EMPTY_STRING;
//		this.lock.readLock().lock();
		if (this.keys.containsKey(stackKey))
		{
			LinearIndexEntity indexes = this.keys.get(stackKey);
			if (indexes.getHeadIndex() <= indexes.getTailIndex())
			{
				key = this.keyCreator.createCompoundKey(stackKey, indexes.getHeadIndex() + 1);
//					Value rsc = this.cache.get(key);
				// The removal is critical. It should not be commented out. Otherwise, when popping data, the order messes up. I still do not understand the reason. 08/12/2018, Bing Li
				// It is not necessary to remove. New data must overwrite the popped data. 08/13/2018, Bing Li
//					this.cache.remove(key);
				indexes.setHeadIndex(indexes.getHeadIndex() + 1);
				if (indexes.getHeadIndex() > indexes.getTailIndex())
				{
					indexes.setHeadIndex(0);
					indexes.setTailIndex(-1);
				}
//				this.lock.readLock().unlock();
//				this.lock.writeLock().lock();
				this.keys.put(stackKey, indexes);
//				this.lock.readLock().lock();
//				this.lock.writeLock().unlock();
			}
		}
//		this.lock.readLock().unlock();
		if (!key.equals(UtilConfig.EMPTY_STRING))
		{
			Value rsc = this.cache.get(key);
			// The removal does not affect the order of the cache. But it is necessary to remove, especially when the disk space is limited. 08/12/2018, Bing Li
			// The removal is critical. It should not be commented out. Otherwise, when popping data, the order messes up. I still do not understand the reason. 08/12/2018, Bing Li
			// It is not necessary to remove. New data must overwrite the popped data. 08/13/2018, Bing Li
			this.cache.remove(key);
			return rsc;
		}
		return null;
	}
	
	protected List<Value> popBottomAtBase(String stackKey, int n)
	{
		List<Value> rscs = new ArrayList<Value>();
		Value rsc;
		for (int i = 0; i < n; i++)
		{
			rsc = this.popBottomAtBase(stackKey);
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

	protected List<Value> peekAtBase(String stackKey, int n)
	{
		List<Value> rscs = new ArrayList<Value>();
		Value rsc;
		List<String> keys = new ArrayList<String>();
//		this.lock.readLock().lock();
		if (this.keys.containsKey(stackKey))
		{
			LinearIndexEntity indexes = this.keys.get(stackKey);
			int headIndex = indexes.getHeadIndex();
			int tailIndex = indexes.getTailIndex();
			for (int i = 0; i < n; i++)
			{
				if (headIndex < tailIndex)
				{
//					rsc = this.cache.get(this.keyCreator.createCompoundKey(stackKey, tailIndex));
					keys.add(this.keyCreator.createCompoundKey(stackKey, tailIndex));
					/*
					if (rsc != null)
					{
						rscs.add(rsc);
						tailIndex--;
					}
					else
					{
						break;
					}
					*/
					tailIndex--;
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

	protected List<Value> peekAtBase(String stackKey, int startIndex, int endIndex) throws IndexOutOfRangeException
	{
		if (startIndex >= 0 && endIndex >= 0)
		{
			List<Value> rscs = new ArrayList<Value>();
			List<String> keys = new ArrayList<String>();
			Value rsc;
			boolean isExceptional = false;
			int size = 0;
//			this.lock.readLock().lock();
			if (this.keys.containsKey(stackKey))
			{
				LinearIndexEntity indexes = this.keys.get(stackKey);
				int headIndex = indexes.getHeadIndex();
				int tailIndex = indexes.getTailIndex();
				size = tailIndex - headIndex;
				int pSize = endIndex - startIndex + 1;
				if (startIndex < size && pSize <= size)
				{
					for (int i = startIndex; i <= endIndex; i++)
					{
						keys.add(this.keyCreator.createCompoundKey(stackKey, tailIndex - i));
					}
				}
				else if (startIndex < size && pSize > size)
				{
					endIndex = size - startIndex + 1;
					for (int i = startIndex; i < endIndex; i++)
					{
						keys.add(this.keyCreator.createCompoundKey(stackKey, tailIndex - i));
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
				throw new IndexOutOfRangeException(stackKey, startIndex, endIndex, size);
			}
		}
		else
		{
			throw new IndexOutOfRangeException(stackKey, startIndex, endIndex);
		}
	}

	protected Value peekAtBase(String stackKey)
	{
		String key = UtilConfig.EMPTY_STRING;
//		this.lock.readLock().lock();
		if (this.keys.containsKey(stackKey))
		{
			LinearIndexEntity indexes = this.keys.get(stackKey);
			if (indexes.getHeadIndex() <= indexes.getTailIndex())
			{
				key = this.keyCreator.createCompoundKey(stackKey, indexes.getTailIndex());
//				return this.cache.get(this.keyCreator.createCompoundKey(stackKey, indexes.getTailIndex()));
			}
		}
//		this.lock.readLock().unlock();
		if (!key.equals(UtilConfig.EMPTY_STRING))
		{
			return this.cache.get(key);
		}
		return null;
	}
	
	protected void removeStacksAtBase(Set<String> stackKeys)
	{
		for (String stackKey : stackKeys)
		{
			this.removeStackAtBase(stackKey);
		}
	}
	
	protected void removeStackAtBase(String stackKey)
	{
//		Set<String> keys = Sets.newHashSet();
		Set<String> keys = new HashSet<String>();
//		this.lock.readLock().lock();
		if (this.keys.containsKey(stackKey))
		{
			LinearIndexEntity indexes = this.keys.get(stackKey);
			for (int i = indexes.getHeadIndex(); i < indexes.getTailIndex(); i++)
			{
//				this.cache.remove(this.keyCreator.createCompoundKey(stackKey, i));
				keys.add(this.keyCreator.createCompoundKey(stackKey, i));
			}
			indexes.setHeadIndex(0);
			indexes.setTailIndex(-1);
//			this.lock.readLock().unlock();
//			this.lock.writeLock().lock();
			this.keys.remove(stackKey);
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
}
