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
import org.greatfree.exceptions.QueueEmptyException;
import org.greatfree.util.Builder;
import org.greatfree.util.FileManager;
import org.greatfree.util.UtilConfig;

//It is required to design such a store to replace the ones in com.greatfree.util.persistcache.enhance since those ones get exceptions of "too many file opened". The store does not create a directory/folder for each persistable queue. On the other hand, those queues in the store is placed into one directory only. 06/20/2017, Bing Li

// Created: 06/07/2017, Bing Li
//public class PersistableQueueStore<Resource extends SerializedKey<Integer>, Factory extends PersistableMapFactorable<String, Resource>, CompoundKeyCreator extends CompoundKeyCreatable<Integer>>
public class QueueStore<Value extends Serializable, Factory extends CacheMapFactorable<String, Value>, CompoundKeyCreator extends CompoundKeyCreatable<Integer>> implements CacheEventable<String, Value>
{
	private final String storeKey;
	private Cache<String, Value> cache;
	private PersistentCacheManager manager;
	private final long cacheSize;
	
//	private PersistableMap<String, QueueIndexes, QueueIndexesFactory, StringKeyDB> keys;
	private LinearIndexDB keysDB;
	private Map<String, LinearIndexEntity> keys;

	private CacheListener<String, Value, QueueStore<Value, Factory, CompoundKeyCreator>> listener;

	private CompoundKeyCreator keyCreator;
	
	private AtomicBoolean isDown;
//	private ReentrantReadWriteLock lock;

//	public QueueStore(String storeKey, Factory factory, String rootPath, int totalStoreSize, int perCacheSize, int offheapSizeInMB, int diskSizeInMB, CompoundKeyCreator keyCreator)
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
		
		this.isDown.set(false);
//		this.lock = new ReentrantReadWriteLock();
	}
	
	public QueueStore(QueueStoreBuilder<Value, Factory, CompoundKeyCreator> builder)
	{
		this.storeKey = builder.getStoreKey();
		this.manager = builder.getFactory().createManagerInstance(FileManager.appendSlash(builder.getRootPath()), builder.getStoreKey(), builder.getTotalStoreSize(), builder.getOffheapSizeInMB(), builder.getDiskSizeInMB());
		this.cache = builder.getFactory().createCache(this.manager, builder.getStoreKey());

		this.listener = new CacheListener<String, Value, QueueStore<Value, Factory, CompoundKeyCreator>>(this);
		this.cache.getRuntimeConfiguration().registerCacheEventListener(this.listener, EventOrdering.ORDERED, EventFiring.ASYNCHRONOUS, EnumSet.of(EventType.CREATED, EventType.EVICTED, EventType.EXPIRED, EventType.REMOVED, EventType.UPDATED));

		this.cacheSize = builder.getCacheSize();
		this.keyCreator = builder.getKeyCreator();
		
		/*
		this.keys = new PersistableMap.PersistableMapBuilder<String, QueueIndexes, QueueIndexesFactory, StringKeyDB>()
				.factory(new QueueIndexesFactory())
//				.rootPath(CacheConfig.getStoreKeysRootPath(builder.getRootPath(), CacheConfig.QUEUE_KEYS))
				.rootPath(CacheConfig.getStoreKeysRootPath(CacheConfig.getCachePath(builder.getRootPath(), builder.getStoreKey()), CacheConfig.QUEUE_KEYS))
				.cacheKey(CacheConfig.QUEUE_KEYS)
				.cacheSize(builder.getTotalStoreSize())
				.offheapSizeInMB(builder.getOffheapSizeInMB())
				.diskSizeInMB(builder.getDiskSizeInMB())
//				.db(StringKeyDBPool.SHARED().getDB(CacheConfig.getStoreKeysDBPath(builder.getRootPath())))
				.db(StringKeyDBPool.SHARED().getDB(CacheConfig.getStoreKeysDBPath(CacheConfig.getCachePath(builder.getRootPath(), builder.getStoreKey()))))
				.build();
				*/
		
		this.keysDB = new LinearIndexDB(CacheConfig.getStoreKeysRootPath(CacheConfig.getCachePath(builder.getRootPath(), builder.getStoreKey()), CacheConfig.QUEUE_KEYS));
		this.keys = this.keysDB.getAllIndexes();

		this.isDown = new AtomicBoolean(false);
//		this.lock = new ReentrantReadWriteLock();
	}
	
//	public static class PersistableQueueStoreBuilder<Resource extends SerializedKey<Integer>, Factory extends PersistableMapFactorable<String, Resource>, CompoundKeyCreator extends CompoundKeyCreatable<Integer>> implements Builder<PersistableQueueStore<Resource, Factory, CompoundKeyCreator>>
	public static class QueueStoreBuilder<Resource extends Serializable, Factory extends CacheMapFactorable<String, Resource>, CompoundKeyCreator extends CompoundKeyCreatable<Integer>> implements Builder<QueueStore<Resource, Factory, CompoundKeyCreator>>
	{
		private String storeKey;
		private Factory factory;
		private long cacheSize;
		private CompoundKeyCreator keyCreator;
		
		private String rootPath;
//		private int totalStoreSize;
		private long totalStoreSize;
		private int offheapSizeInMB;
		private int diskSizeInMB;

		public QueueStoreBuilder()
		{
		}
		
		public QueueStoreBuilder<Resource, Factory, CompoundKeyCreator> storeKey(String storeKey)
		{
			this.storeKey = storeKey;
			return this;
		}
		
		public QueueStoreBuilder<Resource, Factory, CompoundKeyCreator> factory(Factory factory)
		{
			this.factory = factory;
			return this;
		}
		
		public QueueStoreBuilder<Resource, Factory, CompoundKeyCreator> cacheSize(long cacheSize)
		{
			this.cacheSize = cacheSize;
			return this;
		}
		
		public QueueStoreBuilder<Resource, Factory, CompoundKeyCreator> keyCreator(CompoundKeyCreator keyCreator)
		{
			this.keyCreator = keyCreator;
			return this;
		}

		public QueueStoreBuilder<Resource, Factory, CompoundKeyCreator> rootPath(String rootPath)
		{
			this.rootPath = rootPath;
			return this;
		}
		
		public QueueStoreBuilder<Resource, Factory, CompoundKeyCreator> totalStoreSize(long totalStoreSize)
		{
			this.totalStoreSize = totalStoreSize;
			return this;
		}
		
		public QueueStoreBuilder<Resource, Factory, CompoundKeyCreator> offheapSizeInMB(int offheapSizeInMB)
		{
			this.offheapSizeInMB = offheapSizeInMB;
			return this;
		}

		public QueueStoreBuilder<Resource, Factory, CompoundKeyCreator> diskSizeInMB(int diskSizeInMB)
		{
			this.diskSizeInMB = diskSizeInMB;
			return this;
		}

		@Override
		public QueueStore<Resource, Factory, CompoundKeyCreator> build()
		{
			return new QueueStore<Resource, Factory, CompoundKeyCreator>(this);
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

	public boolean isQueueInStore(String queueKey)
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
	
	public int getQueueSize(String queueKey)
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
	
	public boolean isEmpty(String queueKey)
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
	
	public long getEmptySize(String queueKey)
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

	public Set<String> getCacheKeys()
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
	
	public boolean isFull(String queueKey)
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
	
	public void enqueue(String queueKey, Value v)
	{
		String key = UtilConfig.EMPTY_STRING;
//		this.lock.readLock().lock();
//		String queueKey = this.keyCreator.createPrefixKey(queueKey);
		if (!this.keys.containsKey(queueKey))
		{
//			Set<String> keys = Sets.newHashSet();
//			this.keys.put(queueKey, new QueueIndexes(keys));
//			this.keys.put(queueKey, new QueueIndexes());
//			this.lock.readLock().unlock();
//			this.lock.writeLock().lock();
//			this.keysDB.put(queueKey, 0, 0);
			this.keys.put(queueKey, new LinearIndexEntity(queueKey, 0, 0));
			key = this.keyCreator.createCompoundKey(queueKey, this.keys.get(queueKey).getTailIndex());
//			QueueIndexes indexes = this.keys.get(queueKey);
			LinearIndexEntity indexes = this.keys.get(queueKey);
//			indexes.getKeys().add(key);
			indexes.setTailIndex(indexes.getTailIndex() + 1);
//			this.keys.put(queueKey, indexes);
//			this.keysDB.put(indexes);
//			this.lock.readLock().lock();
//			this.lock.writeLock().unlock();
		}
		else
		{
			key = this.keyCreator.createCompoundKey(queueKey, this.keys.get(queueKey).getTailIndex());
//			QueueIndexes indexes = this.keys.get(queueKey);
			LinearIndexEntity indexes = this.keys.get(queueKey);
//			indexes.getKeys().add(key);
			indexes.setTailIndex(indexes.getTailIndex() + 1);
//			this.keys.put(queueKey, indexes);
//			this.lock.readLock().unlock();
//			this.lock.writeLock().lock();
//			this.keysDB.put(indexes);
			this.keys.put(queueKey, indexes);
//			this.lock.readLock().lock();
//			this.lock.writeLock().unlock();
		}
//		this.lock.readLock().unlock();
		if (!key.equals(UtilConfig.EMPTY_STRING))
		{
			this.cache.put(key, v);
		}
	}
	
	public void enqueue(String queueKey, List<Value> rscs)
	{
		for (Value rsc : rscs)
		{
			this.enqueue(queueKey, rsc);
		}
	}
	
	public List<Value> dequeue(String queueKey, int n)
	{
		List<Value> rscs = new ArrayList<Value>();
		Value rsc;
		for (int i = 0; i < n; i++)
		{
			rsc = this.dequeue(queueKey);
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
	
	public Value dequeue(String queueKey)
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
//				this.keysDB.put(indexes);
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

	public Value get(String queueKey, int index) throws IndexOutOfRangeException
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

	public List<Value> peek(String queueKey, int n)
	{
		List<Value> rscs = new ArrayList<Value>();
		List<String> keys = new ArrayList<String>();
		Value rsc;
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
					keys.add(this.keyCreator.createCompoundKey(queueKey, headIndex));
					/*
					rsc = this.cache.get(this.keyCreator.createCompoundKey(queueKey, headIndex));
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

	public List<Value> peek(String queueKey, int startIndex, int endIndex) throws IndexOutOfRangeException
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

	public Value peek(String queueKey) throws QueueEmptyException
	{
		String key = UtilConfig.EMPTY_STRING;
//		this.lock.readLock().lock();
		if (this.keys.containsKey(queueKey))
		{
			LinearIndexEntity indexes = this.keys.get(queueKey);
			int headIndex = indexes.getHeadIndex();
			int tailIndex = indexes.getTailIndex();
			if (headIndex < tailIndex)
			{
				key = this.keyCreator.createCompoundKey(queueKey, headIndex);
				headIndex++;
			}
			else
			{
				throw new QueueEmptyException(queueKey);
			}
		}
//		this.lock.readLock().unlock();
		return this.cache.get(key);
	}


	/*
	 * It is not reasonable to load all of the data from a cache. 08/20/2018, Bing Li
	 * 
	public Value peek(String queueKey)
	{
		this.lock.readLock().lock();
		try
		{
//			String queueKey = this.keyCreator.createPrefixKey(queueKey);
			if (this.keys.containsKey(queueKey))
			{
//				QueueIndexes indexes = this.keys.get(queueKey);
				LinearIndexEntity indexes = this.keys.get(queueKey);
				if (indexes.getHeadIndex() < indexes.getTailIndex())
				{
					return this.cache.get(this.keyCreator.createCompoundKey(queueKey, indexes.getHeadIndex()));
				}
			}
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		return null;
	}
	*/
	
	public void removeQueues(Set<String> queueKeys)
	{
		for (String queueKey : queueKeys)
		{
			this.removeQueue(queueKey);
		}
	}
	
	public void removeQueue(String queueKey)
	{
//		Set<String> keys = Sets.newHashSet();
		Set<String> keys = new HashSet<String>();
//		this.lock.readLock().lock();
//		String queueKey = this.keyCreator.createPrefixKey(queueKey);
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

	public int getStoreSize()
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
