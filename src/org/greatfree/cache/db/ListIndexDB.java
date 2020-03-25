package org.greatfree.cache.db;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.greatfree.util.FileManager;

import com.google.common.collect.Sets;
import com.sleepycat.persist.EntityCursor;

// Created: 02/21/2019, Bing Li
public class ListIndexDB
{
	private File envPath;
	private DBEnv env;
	private ListIndexAccessor accessor;
	
	public ListIndexDB(String path)
	{
		if (!FileManager.isDirExisted(path))
		{
			FileManager.makeDir(path);
		}
		this.envPath = new File(path);
		this.env = new DBEnv(this.envPath, false, DBConfig.DB_CACHE_SIZE, DBConfig.LOCK_TIME_OUT, DBConfig.LIST_INDEX_STORE);
		this.accessor = new ListIndexAccessor(this.env.getEntityStore());
	}

	public void dispose()
	{
		this.accessor.dispose();
		this.env.close();
	}
	
	public boolean containsKey(String key)
	{
		ListIndexEntity entity = this.accessor.getPrimaryKey().get(key);
		if (entity != DBConfig.NO_LIST_INDEX_ENTITY)
		{
			return true;
		}
		return false;
	}
	
	public boolean isEmpty()
	{
		return this.getSize() <= 0;
	}
	
	public int getSize()
	{
		EntityCursor<ListIndexEntity> results = this.env.getEntityStore().getPrimaryIndex(String.class, ListIndexEntity.class).entities();
		int size = 0;
		for (ListIndexEntity entry : results)
		{
			entry.getListKey();
			size++;
		}
		results.close();
		return size;
	}
	
	public Map<String, List<String>> getAllKeys()
	{
		EntityCursor<ListIndexEntity> results = this.env.getEntityStore().getPrimaryIndex(String.class, ListIndexEntity.class).entities();
		Map<String, List<String>> keys = new ConcurrentHashMap<String, List<String>>();
		for (ListIndexEntity entry : results)
		{
			keys.put(entry.getListKey(), entry.getKeys());
		}
		results.close();
		return keys;
	}
	
	public Set<String> getKeys()
	{
		EntityCursor<ListIndexEntity> results = this.env.getEntityStore().getPrimaryIndex(String.class, ListIndexEntity.class).entities();
		Set<String> keys = Sets.newHashSet();
		for (ListIndexEntity entry : results)
		{
			keys.add(entry.getListKey());
		}
		results.close();
		return keys;
	}

	public ListIndexEntity get(String key)
	{
		return this.accessor.getPrimaryKey().get(key);
	}
	
//	public void put(String key, ListIndexEntity index)
	public void put(ListIndexEntity index)
	{
		this.accessor.getPrimaryKey().put(index);
	}
	
	public void putAll(Map<String, List<String>> keys)
	{
		for (Map.Entry<String, List<String>> entry : keys.entrySet())
		{
			this.put(new ListIndexEntity(entry.getKey(), entry.getValue()));
		}
	}

	public void remove(String key)
	{
		this.env.getEntityStore().getPrimaryIndex(String.class, ListIndexEntity.class).delete(key);
	}
	
	public void removeAll()
	{
		EntityCursor<ListIndexEntity> results = this.env.getEntityStore().getPrimaryIndex(String.class, ListIndexEntity.class).entities();
		for (ListIndexEntity entity : results)
		{
			// The below line aims to remove the warning messages. It means nothing. 12/22/2015, Bing Li
			entity.getListKey();
			results.delete();
		}
		results.close();
	}
}
