package org.greatfree.cache.local.store;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.ehcache.Cache;
import org.ehcache.PersistentCacheManager;
import org.ehcache.event.EventFiring;
import org.ehcache.event.EventOrdering;
import org.ehcache.event.EventType;
import org.greatfree.cache.CacheConfig;
import org.greatfree.cache.CacheListener;
import org.greatfree.cache.CompoundKeyCreatable;
import org.greatfree.cache.db.MapKeysDB;
import org.greatfree.cache.local.CacheEventable;
import org.greatfree.cache.local.CacheMapFactorable;
import org.greatfree.exceptions.TerminalServerOverflowedException;
import org.greatfree.util.Builder;
import org.greatfree.util.FileManager;
import org.greatfree.util.UniqueKey;

import com.google.common.collect.Sets;

// Created: 11/02/2019, Bing Li
public class MapStore<Value extends UniqueKey, Factory extends CacheMapFactorable<String, Value>, CompoundKeyCreator extends CompoundKeyCreatable<String>> implements CacheEventable<String, Value>
{
	private final String storeKey;
	private Cache<String, Value> cache;
	private PersistentCacheManager manager;
	private final long perCacheSize;

	private MapKeysDB keysDB;
	private Map<String, Set<String>> keys;

	private CacheListener<String, Value, MapStore<Value, Factory, CompoundKeyCreator>> listener;
	
	private CompoundKeyCreator keyCreator;
	
	public MapStore(MapStoreBuilder<Value, Factory, CompoundKeyCreator> builder)
	{
		this.storeKey = builder.getStoreKey();
		this.manager = builder.getFactory().createManagerInstance(FileManager.appendSlash(builder.getRootPath()), builder.getStoreKey(), builder.getTotalStoreSize(), builder.getOffheapSizeInMB(), builder.getDiskSizeInMB());
		this.cache = builder.getFactory().createCache(this.manager, builder.getStoreKey());

		this.listener = new CacheListener<String, Value, MapStore<Value, Factory, CompoundKeyCreator>>(this);
		this.cache.getRuntimeConfiguration().registerCacheEventListener(this.listener, EventOrdering.ORDERED, EventFiring.ASYNCHRONOUS, EnumSet.of(EventType.CREATED, EventType.EVICTED, EventType.EXPIRED, EventType.REMOVED, EventType.UPDATED));

		this.perCacheSize = builder.getCacheSize();
		this.keyCreator = builder.getKeyCreator();

		this.keysDB = new MapKeysDB(CacheConfig.getStoreKeysRootPath(CacheConfig.getCachePath(builder.getRootPath(), this.storeKey), CacheConfig.MAP_KEYS));
		this.keys = this.keysDB.getAllKeys();
	}

	public static class MapStoreBuilder<Value extends UniqueKey, Factory extends CacheMapFactorable<String, Value>, CompoundKeyCreator extends CompoundKeyCreatable<String>> implements Builder<MapStore<Value, Factory, CompoundKeyCreator>>
	{
		private String storeKey;
		private Factory factory;
		private long cacheSize;
		private CompoundKeyCreator keyCreator;
		
		private String rootPath;
		private int totalStoreSize;
		private int offheapSizeInMB;
		private int diskSizeInMB;
		
		public MapStoreBuilder()
		{
		}
		
		public MapStoreBuilder<Value, Factory, CompoundKeyCreator> storeKey(String storeKey)
		{
			this.storeKey = storeKey;
			return this;
		}
		
		public MapStoreBuilder<Value, Factory, CompoundKeyCreator> factory(Factory factory)
		{
			this.factory = factory;
			return this;
		}
		
		public MapStoreBuilder<Value, Factory, CompoundKeyCreator> cacheSize(long cacheSize)
		{
			this.cacheSize = cacheSize;
			return this;
		}
		
		public MapStoreBuilder<Value, Factory, CompoundKeyCreator> keyCreator(CompoundKeyCreator keyCreator)
		{
			this.keyCreator = keyCreator;
			return this;
		}

		public MapStoreBuilder<Value, Factory, CompoundKeyCreator> rootPath(String rootPath)
		{
			this.rootPath = rootPath;
			return this;
		}
		
		public MapStoreBuilder<Value, Factory, CompoundKeyCreator> totalStoreSize(int totalStoreSize)
		{
			this.totalStoreSize = totalStoreSize;
			return this;
		}
		
		public MapStoreBuilder<Value, Factory, CompoundKeyCreator> offheapSizeInMB(int offheapSizeInMB)
		{
			this.offheapSizeInMB = offheapSizeInMB;
			return this;
		}

		public MapStoreBuilder<Value, Factory, CompoundKeyCreator> diskSizeInMB(int diskSizeInMB)
		{
			this.diskSizeInMB = diskSizeInMB;
			return this;
		}
		
		@Override
		public MapStore<Value, Factory, CompoundKeyCreator> build()
		{
			return new MapStore<Value, Factory, CompoundKeyCreator>(this);
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
		
	}
	
	public void shutdown()
	{
		this.keysDB.removeAll();
		this.keysDB.putAll(this.keys);
		this.keysDB.close();
		this.manager.close();
	}

	
	public Set<String> getCacheKeys()
	{
		return this.keys.keySet();
	}
	
	public boolean isEmpty()
	{
		return this.keys.size() <= 0;
	}

	public void put(String mapKey, Value v)
	{
		String key = this.keyCreator.createCompoundKey(mapKey, v.getKey());
		
		if (!this.keys.containsKey(mapKey))
		{
			Set<String> keys = Sets.newHashSet();
			this.keys.put(mapKey, keys);
		}
		
		Set<String> kSet = this.keys.get(mapKey);
		kSet.add(key);
		this.keys.put(mapKey, kSet);
		this.cache.put(key, v);
	}
	
	public void putAll(String mapKey, Map<String, Value> values)
	{
		if (!this.keys.containsKey(mapKey))
		{
			Set<String> keys = Sets.newHashSet();
			this.keys.put(mapKey, keys);
		}
		String key;
		for (Map.Entry<String, Value> entry : values.entrySet())
		{
			key = this.keyCreator.createCompoundKey(mapKey, entry.getKey());
			this.cache.put(key, entry.getValue());
			Set<String> kSet = this.keys.get(mapKey);
			kSet.add(key);
			this.keys.put(mapKey, kSet);
		}
	}

	public Value get(String mapKey, String k)
	{
		return this.cache.get(this.keyCreator.createCompoundKey(mapKey, k));
	}
	
	public Map<String, Value> get(String mapKey, Set<String> keys)
	{
		Set<String> cKeys = Sets.newHashSet();
		for (String key : keys)
		{
			cKeys.add(this.keyCreator.createCompoundKey(mapKey, key));
		}
		Map<String, Value> pValues = this.cache.getAll(cKeys);
		Map<String, Value> values = new HashMap<String, Value>();
		for (Value v : pValues.values())
		{
			values.put(v.getKey(), v);
		}
		return values;
	}

	public boolean isExisted(String mapKey, String key)
	{
		Value v = this.cache.get(this.keyCreator.createCompoundKey(mapKey, key));
		if (v != null)
		{
			return true;
		}
		return false;
	}
	
	public boolean isExisted(String mapKey)
	{
		return this.keys.containsKey(mapKey);
	}
	
	public long getSize(String mapKey)
	{
		if (this.keys.containsKey(mapKey))
		{
			return this.keys.get(mapKey).size();
		}
		return 0;
	}
	
	public long getEmptySize(String mapKey)
	{
		return this.perCacheSize - this.getSize(mapKey);
	}
	
	public long getLeftSize(String mapKey, int currentAccessedEndIndex)
	{
		return this.getSize(mapKey) - currentAccessedEndIndex - 1;
	}
	
	public boolean isMapFull(String mapKey)
	{
		return this.perCacheSize <= this.getSize(mapKey);
	}
	
	public Set<String> getKeys(String mapKey)
	{
		if (this.keys.containsKey(mapKey))
		{
			return this.cache.getAll(this.keys.get(mapKey)).keySet();
		}
		return null;
	}

	public Map<String, Value> getValues(String mapKey)
	{
		if (this.keys.containsKey(mapKey))
		{
			return this.cache.getAll(this.keys.get(mapKey));
		}
		return null;
	}
	
	public void remove(String mapKey, Set<String> keys)
	{
		String key;
		for (String k : keys)
		{
			if (this.keys.containsKey(mapKey))
			{
				key = this.keyCreator.createCompoundKey(mapKey, k);
				this.keys.get(mapKey).remove(key);
				this.cache.remove(key);
			}
		}
	}

	public void remove(String mapKey, String k)
	{
		if (this.keys.containsKey(mapKey))
		{
			String key = this.keyCreator.createCompoundKey(mapKey, k);
			this.keys.get(mapKey).remove(key);
			this.cache.remove(key);
		}
	}

	public void clear(String mapKey)
	{
		if (this.keys.containsKey(mapKey))
		{
			this.cache.removeAll(this.keys.get(mapKey));
			this.keys.remove(mapKey);
		}
	}

	/*
	 * The method is NOT tested yet. 08/10/2017, Bing Li
	 */
	public void clear()
	{
		Set<String> mapKeys = this.keys.keySet();
		for (String mapKey : mapKeys)
		{
			this.cache.removeAll(this.keys.get(mapKey));
			this.keys.remove(mapKey);
		}
	}

	@Override
	public void evict(String k, Value v) throws TerminalServerOverflowedException
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
