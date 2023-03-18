package org.greatfree.cache.local.store;

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
import org.greatfree.util.Builder;
import org.greatfree.util.FileManager;
import org.greatfree.util.UtilConfig;

// Created: 06/20/2017, Bing Li
//public class PersistableStackStore<Resource extends SerializedKey<Integer>, Factory extends PersistableMapFactorable<String, Resource>, CompoundKeyCreator extends CompoundKeyCreatable<Integer>>
public class StackStore<Value extends Serializable, Factory extends CacheMapFactorable<String, Value>, CompoundKeyCreator extends CompoundKeyCreatable<Integer>> implements CacheEventable<String, Value>
{
	private final String storeKey;
	private Cache<String, Value> cache;
	private PersistentCacheManager manager;
//	private int cacheSize;
	private long cacheSize;

//	private PersistableMap<String, StackIndexes, StackIndexesFactory, StringKeyDB> keys;
	private LinearIndexDB keysDB;
	private Map<String, LinearIndexEntity> keys;
	
	private CacheListener<String, Value, StackStore<Value, Factory, CompoundKeyCreator>> listener;

	private CompoundKeyCreator keyCreator;

	private AtomicBoolean isDown;
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
		
		/*
		this.keys = new PersistableMap.PersistableMapBuilder<String, StackIndexes, StackIndexesFactory, StringKeyDB>()
				.factory(new StackIndexesFactory())
//				.rootPath(CacheConfig.getStoreKeysRootPath(rootPath, CacheConfig.STACK_KEYS))
				.rootPath(CacheConfig.getStoreKeysRootPath(CacheConfig.getCachePath(rootPath, storeKey), CacheConfig.STACK_KEYS))
				.cacheKey(CacheConfig.STACK_KEYS)
				.cacheSize(totalStoreSize)
				.offheapSizeInMB(offheapSizeInMB)
				.diskSizeInMB(diskSizeInMB)
//				.db(StringKeyDBPool.SHARED().getDB(CacheConfig.getStoreKeysDBPath(rootPath)))
				.db(StringKeyDBPool.SHARED().getDB(CacheConfig.getStoreKeysDBPath(CacheConfig.getCachePath(rootPath, storeKey))))
				.build();
				*/
		
		this.keysDB = new LinearIndexDB(CacheConfig.getStoreKeysRootPath(CacheConfig.getCachePath(rootPath, storeKey), CacheConfig.STACK_KEYS));
		this.keys = this.keysDB.getAllIndexes();

		this.isDown = new AtomicBoolean(false);
//		this.lock = new ReentrantReadWriteLock();
	}

	public StackStore(StackStoreBuilder<Value, Factory, CompoundKeyCreator> builder)
	{
		this.storeKey = builder.getStoreKey();
		this.manager = builder.getFactory().createManagerInstance(FileManager.appendSlash(builder.getRootPath()), builder.getStoreKey(), builder.getTotalStoreSize(), builder.getOffheapSizeInMB(), builder.getDiskSizeInMB());
		this.cache = builder.getFactory().createCache(this.manager, builder.getStoreKey());
		this.listener = new CacheListener<String, Value, StackStore<Value, Factory, CompoundKeyCreator>>(this);
		this.cache.getRuntimeConfiguration().registerCacheEventListener(this.listener, EventOrdering.ORDERED, EventFiring.ASYNCHRONOUS, EnumSet.of(EventType.CREATED, EventType.EVICTED, EventType.EXPIRED, EventType.REMOVED, EventType.UPDATED));

		this.cacheSize = builder.getCacheSize();
		this.keyCreator = builder.getKeyCreator();
		
		/*
		this.keys = new PersistableMap.PersistableMapBuilder<String, StackIndexes, StackIndexesFactory, StringKeyDB>()
				.factory(new StackIndexesFactory())
//				.rootPath(CacheConfig.getStoreKeysRootPath(builder.getRootPath(), CacheConfig.STACK_KEYS))
				.rootPath(CacheConfig.getStoreKeysRootPath(CacheConfig.getCachePath(builder.getRootPath(), builder.getStoreKey()), CacheConfig.STACK_KEYS))
				.cacheKey(CacheConfig.STACK_KEYS)
				.cacheSize(builder.getTotalStoreSize())
				.offheapSizeInMB(builder.getOffheapSizeInMB())
				.diskSizeInMB(builder.getDiskSizeInMB())
//				.db(StringKeyDBPool.SHARED().getDB(CacheConfig.getStoreKeysDBPath(builder.getRootPath())))
				.db(StringKeyDBPool.SHARED().getDB(CacheConfig.getStoreKeysDBPath(CacheConfig.getCachePath(builder.getRootPath(), builder.getStoreKey()))))
				.build();
				*/

		this.keysDB = new LinearIndexDB(CacheConfig.getStoreKeysRootPath(CacheConfig.getCachePath(builder.getRootPath(), builder.getStoreKey()), CacheConfig.STACK_KEYS));
		this.keys = this.keysDB.getAllIndexes();
		
		this.isDown = new AtomicBoolean(false);
//		this.lock = new ReentrantReadWriteLock();
	}

//	public static class PersistableStackStoreBuilder<Resource extends SerializedKey<Integer>, Factory extends PersistableMapFactorable<String, Resource>, CompoundKeyCreator extends CompoundKeyCreatable<Integer>> implements Builder<PersistableStackStore<Resource, Factory, CompoundKeyCreator>>
	public static class StackStoreBuilder<Resource extends Serializable, Factory extends CacheMapFactorable<String, Resource>, CompoundKeyCreator extends CompoundKeyCreatable<Integer>> implements Builder<StackStore<Resource, Factory, CompoundKeyCreator>>
	{
		private String storeKey;
		private Factory factory;
//		private int cacheSize;
		private long cacheSize;
		private CompoundKeyCreator keyCreator;
		
		private String rootPath;
//		private int totalStoreSize;
		private long totalStoreSize;
		private int offheapSizeInMB;
		private int diskSizeInMB;

		public StackStoreBuilder()
		{
		}
		
		public StackStoreBuilder<Resource, Factory, CompoundKeyCreator> storeKey(String storeKey)
		{
			this.storeKey = storeKey;
			return this;
		}
		
		public StackStoreBuilder<Resource, Factory, CompoundKeyCreator> factory(Factory factory)
		{
			this.factory = factory;
			return this;
		}
		
		public StackStoreBuilder<Resource, Factory, CompoundKeyCreator> cacheSize(long cacheSize)
		{
			this.cacheSize = cacheSize;
			return this;
		}
		
		public StackStoreBuilder<Resource, Factory, CompoundKeyCreator> keyCreator(CompoundKeyCreator keyCreator)
		{
			this.keyCreator = keyCreator;
			return this;
		}

		public StackStoreBuilder<Resource, Factory, CompoundKeyCreator> rootPath(String rootPath)
		{
			this.rootPath = rootPath;
			return this;
		}
		
		public StackStoreBuilder<Resource, Factory, CompoundKeyCreator> totalStoreSize(long totalStoreSize)
		{
			this.totalStoreSize = totalStoreSize;
			return this;
		}
		
		public StackStoreBuilder<Resource, Factory, CompoundKeyCreator> offheapSizeInMB(int offheapSizeInMB)
		{
			this.offheapSizeInMB = offheapSizeInMB;
			return this;
		}

		public StackStoreBuilder<Resource, Factory, CompoundKeyCreator> diskSizeInMB(int diskSizeInMB)
		{
			this.diskSizeInMB = diskSizeInMB;
			return this;
		}

		@Override
		public StackStore<Resource, Factory, CompoundKeyCreator> build()
		{
			return new StackStore<Resource, Factory, CompoundKeyCreator>(this);
		}

		public String getStoreKey()
		{
			return this.storeKey;
		}
		
		public Factory getFactory()
		{
			return this.factory;
		}
		
		public long getCacheSize()
		{
			return this.cacheSize;
		}
		
		public CompoundKeyCreator getKeyCreator()
		{
			return this.keyCreator;
		}
		
		public String getRootPath()
		{
			return this.rootPath;
		}
		
		public long getTotalStoreSize()
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
	}

	public void close()
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
	
	public Set<String> getStackKeys()
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

	public int getSize(String stackKey)
	{
		/*
		this.lock.readLock().lock();
		try
		{
//			String key = this.keyCreator.createPrefixKey(stackKey);
			if (this.keys.containsKey(stackKey))
			{
//				StackIndexes indexes = this.keys.get(stackKey);
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
		return UtilConfig.ZERO_SIZE;
	}

	public boolean isStackInStore(String stackKey)
	{
		/*
		this.lock.readLock().lock();
		try
		{
//			return this.keys.isExisted(this.keyCreator.createPrefixKey(stackKey));
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
//				StackIndexes indexes = this.keys.get(stackKey);
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
//			String stackKey = this.keyCreator.createPrefixKey(stackKey);
			if (this.keys.containsKey(stackKey))
			{
//				StackIndexes indexes = this.keys.get(stackKey);
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
		String key = UtilConfig.EMPTY_STRING;
//		this.lock.readLock().lock();
//		String stackKey = this.keyCreator.createPrefixKey(stackKey);
		if (!this.keys.containsKey(stackKey))
		{
//			this.keys.put(stackKey, new StackIndexes());
//			this.lock.readLock().unlock();
//			this.lock.writeLock().lock();
			this.keys.put(stackKey, new LinearIndexEntity(stackKey, 0, 0));
//			StackIndexes indexes = this.keys.get(stackKey);
			LinearIndexEntity indexes = this.keys.get(stackKey);
//			String key = this.keyCreator.createCompoundKey(stackKey, this.keys.get(stackKey).getTailIndex() + 1);
			key = this.keyCreator.createCompoundKey(stackKey, indexes.getTailIndex() + 1);
			indexes.setTailIndex(indexes.getTailIndex() + 1);
//			this.keys.put(stackKey, indexes);
			this.keys.put(stackKey, indexes);
//			this.lock.readLock().lock();
//			this.lock.writeLock().unlock();
		}
		else
		{
//			StackIndexes indexes = this.keys.get(stackKey);
			LinearIndexEntity indexes = this.keys.get(stackKey);
//			String key = this.keyCreator.createCompoundKey(stackKey, this.keys.get(stackKey).getTailIndex() + 1);
			key = this.keyCreator.createCompoundKey(stackKey, indexes.getTailIndex() + 1);
			indexes.setTailIndex(indexes.getTailIndex() + 1);
//			this.keys.put(stackKey, indexes);
//			this.lock.readLock().unlock();
//			this.lock.writeLock().lock();
			this.keys.put(stackKey, indexes);
//			this.lock.readLock().lock();
//			this.lock.writeLock().unlock();
		}
		if (!key.equals(UtilConfig.EMPTY_STRING))
		{
			this.cache.put(key, v);
		}
//		this.lock.readLock().unlock();
	}
	
	public void pushBottom(String stackKey, Value v)
	{
		String key = UtilConfig.EMPTY_STRING;
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
		if (!key.equals(UtilConfig.EMPTY_STRING))
		{
			this.cache.put(key, v);
		}
	}
	
	public void pushAllBottom(String stackKey, List<Value> rscs)
	{
		for (int i = 0; i < rscs.size(); i++)
		{
			this.pushBottom(stackKey, rscs.get(i));
		}
	}

	public void pushAll(String stackKey, List<Value> rscs)
	{
//		int maxIndex = rscs.size() - 1;
//		for (int i = maxIndex; i >= 0; i--)
		for (Value rsc : rscs)
		{
//			this.push(stackKey, rscs.get(i));
			this.push(stackKey, rsc);
		}
	}
	
	public List<Value> pop(String stackKey, int n)
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

	public Value pop(String stackKey)
	{
		String key = UtilConfig.EMPTY_STRING;
//			String stackKey = this.keyCreator.createPrefixKey(stackKey);
//		this.lock.readLock().lock();
		if (this.keys.containsKey(stackKey))
		{
//				StackIndexes indexes = this.keys.get(stackKey);
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
	
	public Value popBottom(String stackKey)
	{
		String key = UtilConfig.EMPTY_STRING;
//		this.lock.readLock().lock();
		if (this.keys.containsKey(stackKey))
		{
			LinearIndexEntity indexes = this.keys.get(stackKey);
			if (indexes.getHeadIndex() <= indexes.getTailIndex())
			{
				key = this.keyCreator.createCompoundKey(stackKey, indexes.getHeadIndex() + 1);
//				Value rsc = this.cache.get(key);
//				this.cache.remove(key);
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
			this.cache.remove(key);
			return rsc;
		}
		return null;
	}
	
	public List<Value> popBottom(String stackKey, int n)
	{
		List<Value> rscs = new ArrayList<Value>();
		Value rsc;
		for (int i = 0; i < n; i++)
		{
			rsc = this.popBottom(stackKey);
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
	
	public List<Value> peek(String stackKey, int n)
	{
		List<Value> rscs = new ArrayList<Value>();
		List<String> keys = new ArrayList<String>();
		Value rsc;
//		this.lock.readLock().lock();
//		String stackKey = this.keyCreator.createPrefixKey(stackKey);
		if (this.keys.containsKey(stackKey))
		{
//			StackIndexes indexes = this.keys.get(stackKey);
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
	
	public Value get(String stackKey, int index) throws IndexOutOfRangeException
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

	public List<Value> peek(String stackKey, int startIndex, int endIndex) throws IndexOutOfRangeException
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

	public Value peek(String stackKey)
	{
		String key = UtilConfig.EMPTY_STRING;
//		String stackKey = this.keyCreator.createPrefixKey(stackKey);
//		this.lock.readLock().lock();
		if (this.keys.containsKey(stackKey))
		{
//			StackIndexes indexes = this.keys.get(stackKey);
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
	
	public int getStackSize(String stackKey)
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
			return UtilConfig.NO_STACK_SIZE;
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
	
	public void removeStacks(Set<String> stackKeys)
	{
		for (String stackKey : stackKeys)
		{
			this.removeStack(stackKey);
		}
	}
	
	public void removeStack(String stackKey)
	{
//		Set<String> keys = Sets.newHashSet();
		Set<String> keys = new HashSet<String>();
//		this.lock.readLock().lock();
//		String stackKey = this.keyCreator.createPrefixKey(stackKey);
		if (this.keys.containsKey(stackKey))
		{
//			StackIndexes indexes = this.keys.get(stackKey);
			LinearIndexEntity indexes = this.keys.get(stackKey);
			for (int i = indexes.getHeadIndex(); i < indexes.getTailIndex(); i++)
			{
//				this.cache.remove(this.keyCreator.createCompoundKey(stackKey, i));
				keys.add(this.keyCreator.createCompoundKey(stackKey, i));
			}
			indexes.setHeadIndex(0);
			indexes.setTailIndex(-1);
//			this.keys.put(stackKey, indexes);
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

	@Override
	public void evict(String k, Value v)
	{
		// TODO Auto-generated method stub
		
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
