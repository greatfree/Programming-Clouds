package org.greatfree.cache.distributed.terminal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.EnumSet;
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
import org.greatfree.cache.db.LinearIndexDB;
import org.greatfree.cache.db.LinearIndexEntity;
import org.greatfree.cache.distributed.terminal.db.PostfetchDB;
import org.greatfree.cache.local.CacheEventable;
import org.greatfree.cache.local.CacheMapFactorable;
import org.greatfree.exceptions.IndexOutOfRangeException;
import org.greatfree.exceptions.TerminalServerOverflowedException;
import org.greatfree.util.Builder;
import org.greatfree.util.UtilConfig;

/*
 * The cache is tested in the Clouds project. 08/22/2018, Bing Li
 * 
 * Its counterpart is QueueStore, in which one key might not have the value because of eviction. 05/31/2018, Bing Li
 * 
 * The terminal cache keeps the evicted in a DB. Thus, no data will be lost unless the local preset disk is overloaded. So usually, any keys have their own values strictly. 05/31/2018, Bing Li
 *
 * The cache is usually used to keep data at the terminal server in a distributed storage system, such as MServer. It can be used at a standalone server only if the key is required to match with one value. 05/31/2018, Bing Li
 * 
 */

// Created: 05/15/2018, Bing Li
public class TerminalQueueStore<Value extends Serializable, Factory extends CacheMapFactorable<String, Value>, CompoundKeyCreator extends CompoundKeyCreatable<Integer>, DB extends PostfetchDB<Value>> implements CacheEventable<String, Value>
{
	private final String storeKey;
	private Cache<String, Value> cache;
	private PersistentCacheManager manager;
//	private final int cacheSize;
	private final long cacheSize;
	
	private LinearIndexDB keysDB;
	private Map<String, LinearIndexEntity> keys;

	private CacheListener<String, Value, TerminalQueueStore<Value, Factory, CompoundKeyCreator, DB>> listener;

	private CompoundKeyCreator keyCreator;
	
	private AtomicBoolean isDown;
//	private ReentrantReadWriteLock lock;

	private DB db;

	private AtomicInteger currentEvictedCount;
	private final int alertEvictedCount;
	
	public TerminalQueueStore(TerminalQueueStoreBuilder<Value, Factory, CompoundKeyCreator, DB> builder)
	{
		this.storeKey = builder.getStoreKey();
		this.manager = builder.getFactory().createManagerInstance(builder.getRootPath(), builder.getStoreKey(), builder.getTotalStoreSize(), builder.getOffheapSizeInMB(), builder.getDiskSizeInMB());
		this.cache = builder.getFactory().createCache(this.manager, this.storeKey);
		this.listener = new CacheListener<String, Value, TerminalQueueStore<Value, Factory, CompoundKeyCreator, DB>>(this);
		this.cache.getRuntimeConfiguration().registerCacheEventListener(this.listener, EventOrdering.ORDERED, EventFiring.ASYNCHRONOUS, EnumSet.of(EventType.CREATED, EventType.EVICTED, EventType.EXPIRED, EventType.REMOVED, EventType.UPDATED));

		this.cacheSize = builder.getCacheSize();
		this.keyCreator = builder.getCreator();
		
		this.keysDB = new LinearIndexDB(CacheConfig.getStoreKeysRootPath(CacheConfig.getCachePath(builder.getRootPath(), builder.getStoreKey()), CacheConfig.STACK_KEYS));
		this.keys = this.keysDB.getAllIndexes();
		
		this.db = builder.getDB();
		this.isDown = new AtomicBoolean(false);
//		this.lock = new ReentrantReadWriteLock();
		this.currentEvictedCount = new AtomicInteger(this.db.getSize());
		this.alertEvictedCount = builder.getAlertEvictedCount();
	}

	public static class TerminalQueueStoreBuilder<Value extends Serializable, Factory extends CacheMapFactorable<String, Value>, CompoundKeyCreator extends CompoundKeyCreatable<Integer>, DB extends PostfetchDB<Value>> implements Builder<TerminalQueueStore<Value, Factory, CompoundKeyCreator, DB>>
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
		private DB db;
		private int alertEvictedCount;
		
		public TerminalQueueStoreBuilder()
		{
		}
		
		public TerminalQueueStoreBuilder<Value, Factory, CompoundKeyCreator, DB> storeKey(String storeKey)
		{
			this.storeKey = storeKey;
			return this;
		}
		
		public TerminalQueueStoreBuilder<Value, Factory, CompoundKeyCreator, DB> factory(Factory factory)
		{
			this.factory = factory;
			return this;
		}
		
		public TerminalQueueStoreBuilder<Value, Factory, CompoundKeyCreator, DB> rootPath(String rootPath)
		{
			this.rootPath = rootPath;
			return this;
		}
		
		public TerminalQueueStoreBuilder<Value, Factory, CompoundKeyCreator, DB> totalStoreSize(long totalStoreSize)
		{
			this.totalStoreSize = totalStoreSize;
			return this;
		}
		
		public TerminalQueueStoreBuilder<Value, Factory, CompoundKeyCreator, DB> cacheSize(long cacheSize)
		{
			this.cacheSize = cacheSize;
			return this;
		}
		
		public TerminalQueueStoreBuilder<Value, Factory, CompoundKeyCreator, DB> offheapSizeInMB(int offheapSizeInMB)
		{
			this.offheapSizeInMB = offheapSizeInMB;
			return this;
		}
		
		public TerminalQueueStoreBuilder<Value, Factory, CompoundKeyCreator, DB> diskSizeInMB(int diskSizeInMB)
		{
			this.diskSizeInMB = diskSizeInMB;
			return this;
		}

		public TerminalQueueStoreBuilder<Value, Factory, CompoundKeyCreator, DB> keyCreator(CompoundKeyCreator keyCreator)
		{
			this.keyCreator = keyCreator;
			return this;
		}

		public TerminalQueueStoreBuilder<Value, Factory, CompoundKeyCreator, DB> db(DB db)
		{
			this.db = db;
			return this;
		}

		public TerminalQueueStoreBuilder<Value, Factory, CompoundKeyCreator, DB> alertEvictedCount(int alertEvictedCount)
		{
			this.alertEvictedCount = alertEvictedCount;
			return this;
		}

		@Override
		public TerminalQueueStore<Value, Factory, CompoundKeyCreator, DB> build()
		{
			return new TerminalQueueStore<Value, Factory, CompoundKeyCreator, DB>(this);
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
				LinearIndexEntity indexes = this.keys.get(queueKey);
				if (indexes != null)
				{
					return indexes.getTailIndex() - indexes.getHeadIndex() + 1;
				}
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
			if (indexes != null)
			{
				return indexes.getTailIndex() - indexes.getHeadIndex() + 1;
			}
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
				LinearIndexEntity indexes = this.keys.get(queueKey);
				if (indexes != null)
				{
					return indexes.getTailIndex() - indexes.getHeadIndex() <= 0;
				}
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
			if (indexes != null)
			{
				return indexes.getTailIndex() - indexes.getHeadIndex() <= 0;
			}
		}
		return false;
	}

	public long getEmptySize(String queueKey)
	{
		/*
		this.lock.readLock().lock();
		try
		{
			if (this.keys.containsKey(queueKey))
			{
				LinearIndexEntity indexes = this.keys.get(queueKey);
				if (indexes != null)
				{
					return this.cacheSize - (indexes.getTailIndex() - indexes.getHeadIndex());
				}
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
			if (indexes != null)
			{
				return this.cacheSize - (indexes.getTailIndex() - indexes.getHeadIndex());
			}
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
			if (this.keys.containsKey(queueKey))
			{
				LinearIndexEntity indexes = this.keys.get(queueKey);
				if (indexes != null)
				{
					return indexes.getTailIndex() - indexes.getHeadIndex() >= this.cacheSize;
				}
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
			if (indexes != null)
			{
				return indexes.getTailIndex() - indexes.getHeadIndex() >= this.cacheSize;
			}
		}
		return false;
	}

	/*
	public void enqueue(String queueKey, Value v)
	{
		this.lock.writeLock().lock();
		this.put(queueKey, v);
		this.lock.writeLock().unlock();
	}
	*/
	
	public void enqueue(String queueKey, Value v)
	{
		String key = UtilConfig.EMPTY_STRING;
//		this.lock.readLock().lock();
		if (!this.keys.containsKey(queueKey))
		{
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
			key = this.keyCreator.createCompoundKey(queueKey, this.keys.get(queueKey).getTailIndex());
			LinearIndexEntity indexes = this.keys.get(queueKey);
			indexes.setTailIndex(indexes.getTailIndex() + 1);

//			this.lock.readLock().unlock();
//			this.lock.writeLock().lock();
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
	
	public void enqueueAll(String queueKey, List<Value> rscs)
	{
		for (Value rsc : rscs)
		{
			this.enqueue(queueKey, rsc);
		}
	}

	public Value dequeue(String queueKey)
	{
		String key = UtilConfig.EMPTY_STRING;
//		this.lock.readLock().lock();
		if (this.keys.containsKey(queueKey))
		{
			LinearIndexEntity indexes = this.keys.get(queueKey);
			if (indexes.getHeadIndex() < indexes.getTailIndex())
			{
				key = this.keyCreator.createCompoundKey(queueKey, indexes.getHeadIndex());
				/*
				Value rsc = this.cache.get(key);
				if (rsc != null)
				{
					this.cache.remove(key);
				}
				else
				{
					rsc = this.db.get(key);
					// To simplify the code, the data loaded from the DB will NOT be put into the cache. The benefit to do that is not so obvious. In addition, the DB itself has the cache capability. My work just focus on designing a wrapper. 05/31/2018, Bing Li
				}
				*/
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
			if (rsc != null)
			{
				this.cache.remove(key);
			}
			else
			{
//				this.lock.readLock().lock();
				rsc = this.db.get(key);
//				this.lock.readLock().unlock();
				
//				this.lock.writeLock().lock();
				this.db.remove(key);
//				this.lock.writeLock().unlock();
				// To simplify the code, the data loaded from the DB will NOT be put into the cache. The benefit to do that is not so obvious. In addition, the DB itself has the cache capability. My work just focus on designing a wrapper. 05/31/2018, Bing Li
			}
			return rsc;
		}
		return null;
	}

	public List<Value> dequeueAll(String queueKey, int n)
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
		}
		return rscs;
	}

	public List<Value> peekAll(String queueKey, int n)
	{
		List<Value> rscs = new ArrayList<Value>();
		Value rsc;
//		String key;
		List<String> keys = new ArrayList<String>();
//		this.lock.readLock().lock();
		if (this.keys.containsKey(queueKey))
		{
			LinearIndexEntity indexes = this.keys.get(queueKey);
			
			int headIndex = indexes.getHeadIndex();
			int tailIndex = indexes.getTailIndex();
			for (int i = 0; i < n; i++)
			{
				if (headIndex < tailIndex)
				{
//					key = this.keyCreator.createCompoundKey(queueKey, headIndex);
					keys.add(this.keyCreator.createCompoundKey(queueKey, headIndex));
					/*
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
							// To simplify the code, the data loaded from the DB will NOT be put into the cache. The benefit to do that is not so obvious. In addition, the DB itself has the cache capability. My work just focus on designing a wrapper. 05/31/2018, Bing Li
//							this.put(queueKey, rsc);
							rscs.add(rsc);
						}
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
//				this.lock.readLock().lock();
				rsc = this.db.get(key);
//				this.lock.readLock().unlock();
				if (rsc != null)
				{
					// To simplify the code, the data loaded from the DB will NOT be put into the cache. The benefit to do that is not so obvious. In addition, the DB itself has the cache capability. My work just focus on designing a wrapper. 05/31/2018, Bing Li
//					this.put(queueKey, rsc);
					rscs.add(rsc);
				}
			}
		}
		return rscs;
	}
	
	public List<Value> peekRange(String queueKey, int startIndex, int endIndex) throws IndexOutOfRangeException
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
				for (String key : keys)
				{
					rsc = this.cache.get(key);
					if (rsc != null)
					{
						rscs.add(rsc);
					}
					else
					{
//						this.lock.readLock().lock();
						rsc = this.db.get(key);
//						this.lock.readLock().unlock();
						if (rsc != null)
						{
							// To simplify the code, the data loaded from the DB will NOT be put into the cache. The benefit to do that is not so obvious. In addition, the DB itself has the cache capability. My work just focus on designing a wrapper. 05/31/2018, Bing Li
//							this.put(queueKey, rsc);
							rscs.add(rsc);
						}
					}
				}
				return rscs;
			}
			else
			{
				throw new IndexOutOfRangeException(queueKey, startIndex, endIndex);
			}
		}
		else
		{
			throw new IndexOutOfRangeException(queueKey, startIndex, endIndex);
		}
	}

	public Value peek(String queueKey)
	{
		Value rsc;
		String key = UtilConfig.EMPTY_STRING;
//		this.lock.readLock().lock();
		if (this.keys.containsKey(queueKey))
		{
			LinearIndexEntity indexes = this.keys.get(queueKey);
			if (indexes != null)
			{
				if (indexes.getHeadIndex() < indexes.getTailIndex())
				{
					key = this.keyCreator.createCompoundKey(queueKey, indexes.getHeadIndex());
					/*
					rsc = this.cache.get(key);
					if (rsc != null)
					{
						return rsc;
					}
					else
					{
						// To simplify the code, the data loaded from the DB will NOT be put into the cache. The benefit to do that is not so obvious. In addition, the DB itself has the cache capability. My work just focus on designing a wrapper. 05/31/2018, Bing Li
						return this.db.get(key);
					}
					*/
				}
			}
		}
//		this.lock.readLock().unlock();

		if (!key.equals(UtilConfig.EMPTY_STRING))
		{
			rsc = this.cache.get(key);
			if (rsc != null)
			{
				return rsc;
			}
			else
			{
				// To simplify the code, the data loaded from the DB will NOT be put into the cache. The benefit to do that is not so obvious. In addition, the DB itself has the cache capability. My work just focus on designing a wrapper. 05/31/2018, Bing Li
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
				Value rsc = this.cache.get(key);
				if (rsc != null)
				{
					return rsc;
				}
				else
				{
					// To simplify the code, the data loaded from the DB will NOT be put into the cache. The benefit to do that is not so obvious. In addition, the DB itself has the cache capability. My work just focus on designing a wrapper. 05/31/2018, Bing Li
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

	// To be reasonable as a storage, the remove methods can be retained. But it suggests to invoke the methods as few as possible. 06/15/2018, Bing Li
	// It is not necessary to remove data for a terminal of a large-scale storage system. 06/15/2018, Bing Li
	public void removeQueues(Set<String> queueKeys)
	{
		for (String queueKey : queueKeys)
		{
			this.removeQueue(queueKey);
		}
	}

	public void removeQueue(String queueKey)
	{
//		String key;
		List<String> keys = new ArrayList<String>();
//		this.lock.readLock().lock();
		if (this.keys.containsKey(queueKey))
		{
			LinearIndexEntity indexes = this.keys.get(queueKey);
			
			for (int i = indexes.getHeadIndex(); i < indexes.getTailIndex(); i++)
			{
//				key = this.keyCreator.createCompoundKey(queueKey, i);
				keys.add(this.keyCreator.createCompoundKey(queueKey, i));
				/*
				if (this.cache.containsKey(key))
				{
					this.cache.remove(key);
				}
				else
				{
					this.db.remove(key);
				}
				*/
			}
			indexes.setHeadIndex(0);
			indexes.setTailIndex(0);
			
//			this.lock.readLock().unlock();
//			this.lock.writeLock().lock();
			this.keys.remove(queueKey);
//			this.lock.readLock().lock();
//			this.lock.writeLock().unlock();
		}
//		this.lock.readLock().unlock();
		
		for (String key : keys)
		{
			if (this.cache.containsKey(key))
			{
				this.cache.remove(key);
			}
			else
			{
//				this.lock.writeLock().lock();
				this.db.remove(key);
//				this.lock.writeLock().unlock();
			}
		}
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
			return this.keys.keySet().size();
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		*/
		return this.keys.keySet().size();
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
