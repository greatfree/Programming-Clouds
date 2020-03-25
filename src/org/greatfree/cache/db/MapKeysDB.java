package org.greatfree.cache.db;

import java.io.File;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.greatfree.util.FileManager;

import com.google.common.collect.Sets;
import com.sleepycat.persist.EntityCursor;

// Created: 05/07/2018, Bing Li
public class MapKeysDB
{
	private File envPath;
	private DBEnv env;
	private MapKeysAccessor accessor;

	public MapKeysDB(String path)
	{
		if (!FileManager.isDirExisted(path))
		{
			FileManager.makeDir(path);
		}
		this.envPath = new File(path);
		this.env = new DBEnv(this.envPath, false, DBConfig.DB_CACHE_SIZE, DBConfig.LOCK_TIME_OUT, DBConfig.MAP_KEYS_STORE);
		this.accessor = new MapKeysAccessor(this.env.getEntityStore());
	}
	
	// For testing. 05/07/2018, Bing Li
	public String getPath()
	{
		return this.envPath.getPath();
	}

	public void close()
	{
		this.accessor.dispose();
		this.env.close();
	}

	/*
	public Map<String, Set<String>> getValues()
	{
		EntityCursor<MapKeysEntity> results = this.env.getEntityStore().getPrimaryIndex(String.class, MapKeysEntity.class).entities();
		Map<String, Set<String>> mapKeys = new HashMap<String, Set<String>>();
		Set<String> valueKeys;
		for (MapKeysEntity entry : results)
		{
			if (!mapKeys.containsKey(entry.getMapKey()))
			{
				valueKeys = Sets.newHashSet();
				valueKeys.add(entry.getValueKey());
				mapKeys.put(entry.getMapKey(), valueKeys);
			}
			else
			{
				mapKeys.get(entry.getMapKey()).add(entry.getValueKey());
			}
		}
		results.close();
		return mapKeys;
	}
	*/
	
	public Set<String> getKeys()
	{
		EntityCursor<MapKeysEntity> results = this.env.getEntityStore().getPrimaryIndex(String.class, MapKeysEntity.class).entities();
		Set<String> keys = Sets.newHashSet();
		for (MapKeysEntity entry : results)
		{
			keys.add(entry.getMapKey());
		}
		results.close();
		return keys;
	}
	
	public int getSize()
	{
		EntityCursor<MapKeysEntity> results = this.env.getEntityStore().getPrimaryIndex(String.class, MapKeysEntity.class).entities();
		int size = 0;
		for (MapKeysEntity entry : results)
		{
			entry.getMapKey();
			size++;
		}
		results.close();
		return size;
	}

	/*
	public Set<String> get(String key)
	{
		MapKeysEntity entity = this.accessor.getPrimaryKey().get(key);
		if (entity != DBConfig.NO_MAP_KEYS_ENTITY)
		{
			return entity.getValueKey();
		}
		return UtilConfig.NO_KEYS;
	}
	*/
	
	public Map<String, Set<String>> getAllKeys()
	{
		EntityCursor<MapKeysEntity> results = this.env.getEntityStore().getPrimaryIndex(String.class, MapKeysEntity.class).entities();
		Map<String, Set<String>> mapKeys = new ConcurrentHashMap<String, Set<String>>();
		Set<String> valueKeys;
		for (MapKeysEntity entry : results)
		{
			if (!mapKeys.containsKey(entry.getMapKey()))
			{
				valueKeys = Sets.newHashSet();
				valueKeys.add(entry.getValueKey());
				mapKeys.put(entry.getMapKey(), valueKeys);
			}
			else
			{
				mapKeys.get(entry.getMapKey()).add(entry.getValueKey());
			}
		}
		results.close();
		return mapKeys;
	}
	
	public boolean containsKey(String key)
	{
		MapKeysEntity entity = this.accessor.getPrimaryKey().get(key);
		if (entity != DBConfig.NO_MAP_KEYS_ENTITY)
		{		
			return true;
		}
		return false;
	}
	
	public void putAll(Map<String, Set<String>> mapKeys)
	{
		for (Map.Entry<String, Set<String>> entry : mapKeys.entrySet())
		{
			this.put(entry.getKey(), entry.getValue());
		}
	}

	public void put(String key, Set<String> valueKeys)
	{
		for (String entry : valueKeys)
		{
			this.accessor.getPrimaryKey().put(new MapKeysEntity(key, entry));
		}
	}

	public void remove(String key)
	{
		this.env.getEntityStore().getPrimaryIndex(String.class, MapKeysEntity.class).delete(key);
	}
	
	public void removeAll()
	{
		EntityCursor<MapKeysEntity> results = this.env.getEntityStore().getPrimaryIndex(String.class, MapKeysEntity.class).entities();
		for (MapKeysEntity entity : results)
		{
			// The below line aims to remove the warning messages. It means nothing. 12/22/2015, Bing Li
			entity.getMapKey();
			results.delete();
		}
		results.close();
	}
}
