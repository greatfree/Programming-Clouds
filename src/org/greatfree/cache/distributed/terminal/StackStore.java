package org.greatfree.cache.distributed.terminal;

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
import org.greatfree.util.FileManager;
import org.greatfree.util.UtilConfig;

import com.google.common.collect.Sets;

/*
 * When designing the class, I attempt to use it to lower the load to implement a terminal cache. But since the DB needs to work with the internal algorithm of the class, it is not so convenient. So the class is not useful at this moment. Just keep it for a while before abandoning it. 06/18/2018, Bing Li
 */

// Created: 06/17/2018, Bing Li
abstract class StackStore<Value extends Serializable, Factory extends CacheMapFactorable<String, Value>, CompoundKeyCreator extends CompoundKeyCreatable<Integer>> implements CacheEventable<String, Value>
{
	private final String storeKey;
	private Cache<String, Value> cache;
	private PersistentCacheManager manager;
	private long cacheSize;

	private LinearIndexDB keysDB;
	private Map<String, LinearIndexEntity> keys;
	
	private CacheListener<String, Value, StackStore<Value, Factory, CompoundKeyCreator>> listener;

	private CompoundKeyCreator keyCreator;

	private AtomicBoolean isDown;
//	private ReentrantReadWriteLock lock;

//	public StackStore(String storeKey, Factory factory, String rootPath, int storeSize, int cacheSize, int offheapSizeInMB, int diskSizeInMB, CompoundKeyCreator keyCreator)
	public StackStore(String storeKey, Factory factory, String rootPath, long storeSize, long cacheSize, int offheapSizeInMB, int diskSizeInMB, CompoundKeyCreator keyCreator)
	{	
		this.storeKey = storeKey;
		this.manager = factory.createManagerInstance(FileManager.appendSlash(rootPath), storeKey, storeSize, offheapSizeInMB, diskSizeInMB);
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

	public void close()
	{
//		this.lock.writeLock().lock();
		
		this.keysDB.removeAll();
		this.keysDB.putAll(this.keys);
		this.keysDB.close();
		
		this.manager.close();
		this.isDown.set(true);
//		this.lock.writeLock().unlock();
//		this.lock = null;
	}
	
	public String getStoreKey()
	{
		return this.storeKey;
	}
	
	protected String getValueKey(String stackKey)
	{
		/*
		this.lock.readLock().lock();
		try
		{
			if (this.keys.containsKey(stackKey))
			{
				LinearIndexEntity indexes = this.keys.get(stackKey);
				if (indexes.getHeadIndex() <= indexes.getTailIndex())
				{
					return this.keyCreator.createCompoundKey(stackKey, indexes.getTailIndex());
				}
			}
			return UtilConfig.EMPTY_STRING;
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		*/
		if (this.keys.containsKey(stackKey))
		{
			LinearIndexEntity indexes = this.keys.get(stackKey);
			if (indexes.getHeadIndex() <= indexes.getTailIndex())
			{
				return this.keyCreator.createCompoundKey(stackKey, indexes.getTailIndex());
			}
		}
		return UtilConfig.EMPTY_STRING;
	}

	/*
	public ReentrantReadWriteLock getLock()
	{
		return this.lock;
	}
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

	public int getSize(String stackKey)
	{
		/*
		this.lock.readLock().lock();
		try
		{
			if (this.keys.containsKey(stackKey))
			{
				LinearIndexEntity indexes = this.keys.get(stackKey);
				return indexes.getTailIndex() - indexes.getHeadIndex();
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
			return indexes.getTailIndex() - indexes.getHeadIndex();
		}
		return UtilConfig.ZERO;
	}

	public boolean isStackInStore(String stackKey)
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
	
	public boolean isEmpty(String stackKey)
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
	
	public boolean isFull(String stackKey)
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

	public void push(String stackKey, Value v)
	{
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
	}

	public void push(String stackKey, List<Value> rscs)
	{
		for (Value rsc : rscs)
		{
			this.push(stackKey, rsc);
		}
	}
	
	protected List<Value> pop(String stackKey, int n)
	{
		List<Value> rscs = new ArrayList<Value>();
		Value rsc;
		for (int i = 0; i < n; i++)
		{
			rsc = this.pop(stackKey);
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

	protected Value pop(String stackKey)
	{
		String key = UtilConfig.EMPTY_STRING;
//		this.lock.readLock().lock();
		if (this.keys.containsKey(stackKey))
		{
			LinearIndexEntity indexes = this.keys.get(stackKey);
			if (indexes.getHeadIndex() <= indexes.getTailIndex())
			{
				key = this.keyCreator.createCompoundKey(stackKey, indexes.getTailIndex());
//				Value rsc = this.cache.get(key);
//				this.cache.remove(key);
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
			Value rsc = this.cache.get(key);
			this.cache.remove(key);
			return rsc;
		}
		return null;
	}
	
	protected List<Value> peek(String stackKey, int n)
	{
		List<Value> rscs = new ArrayList<Value>();
		List<String> keys = new ArrayList<String>();
		Value rsc;
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
					/*
					rsc = this.cache.get(this.keyCreator.createCompoundKey(stackKey, tailIndex));
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
					keys.add(this.keyCreator.createCompoundKey(stackKey, tailIndex));
					tailIndex--;
				}
			}
		}
//		this.lock.readLock().unlock();
		
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

	protected Value peek(String stackKey)
	{
		String key = UtilConfig.EMPTY_STRING;
//		this.lock.readLock().lock();
		if (this.keys.containsKey(stackKey))
		{
			LinearIndexEntity indexes = this.keys.get(stackKey);
			if (indexes.getHeadIndex() <= indexes.getTailIndex())
			{
//				return this.cache.get(this.keyCreator.createCompoundKey(stackKey, indexes.getTailIndex()));
				key = this.keyCreator.createCompoundKey(stackKey, indexes.getTailIndex());
			}
		}
//		this.lock.readLock().unlock();
		if (!key.equals(UtilConfig.EMPTY_STRING))
		{
			return this.cache.get(key);
		}
		return null;
	}
	
	protected void removeStacks(Set<String> stackKeys)
	{
		for (String stackKey : stackKeys)
		{
			this.removeStack(stackKey);
		}
	}
	
	protected void removeStack(String stackKey)
	{
		Set<String> keys = Sets.newHashSet();
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

	public boolean isEmpty()
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
