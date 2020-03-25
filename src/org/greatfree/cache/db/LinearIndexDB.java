package org.greatfree.cache.db;

import java.io.File;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.greatfree.util.FileManager;

import com.google.common.collect.Sets;
import com.sleepycat.persist.EntityCursor;

// Created: 05/07/2018, Bing Li
public class LinearIndexDB
{
	private File envPath;
	private DBEnv env;
	private LinearIndexAccessor accessor;
	
	public LinearIndexDB(String path)
	{
		if (!FileManager.isDirExisted(path))
		{
			FileManager.makeDir(path);
		}
		this.envPath = new File(path);
		this.env = new DBEnv(this.envPath, false, DBConfig.DB_CACHE_SIZE, DBConfig.LOCK_TIME_OUT, DBConfig.QUEUE_INDEX_STORE);
		this.accessor = new LinearIndexAccessor(this.env.getEntityStore());
	}
	
	public void close()
	{
		this.accessor.dispose();
		this.env.close();
	}
	
	public LinearIndexEntity get(String key)
	{
		return this.accessor.getPrimaryKey().get(key);
	}

	public boolean containsKey(String key)
	{
		LinearIndexEntity entity = this.accessor.getPrimaryKey().get(key);
		if (entity != DBConfig.NO_QUEUE_INDEX_ENTITY)
		{		
			return true;
		}
		return false;
	}
	
	public boolean isEmpty()
	{
		return this.getSize() <= 0;
	}
	
	public Map<String, LinearIndexEntity> getAllIndexes()
	{
		EntityCursor<LinearIndexEntity> results = this.env.getEntityStore().getPrimaryIndex(String.class, LinearIndexEntity.class).entities();
		Map<String, LinearIndexEntity> indexes = new ConcurrentHashMap<String, LinearIndexEntity>();
		for (LinearIndexEntity entry : results)
		{
			indexes.put(entry.getKey(), entry);
		}
		results.close();
		return indexes;
	}

	public Set<String> getKeys()
	{
		EntityCursor<LinearIndexEntity> results = this.env.getEntityStore().getPrimaryIndex(String.class, LinearIndexEntity.class).entities();
		Set<String> keys = Sets.newHashSet();
		for (LinearIndexEntity entry : results)
		{
			keys.add(entry.getKey());
		}
		results.close();
		return keys;
	}

	public int getSize()
	{
		EntityCursor<LinearIndexEntity> results = this.env.getEntityStore().getPrimaryIndex(String.class, LinearIndexEntity.class).entities();
		int size = 0;
		for (LinearIndexEntity entry : results)
		{
			entry.getKey();
			size++;
		}
		results.close();
		return size;
	}
	
	public void put(String key, int headIndex, int tailIndex)
	{
		this.accessor.getPrimaryKey().put(new LinearIndexEntity(key, headIndex, tailIndex));
	}
	
	public void put(LinearIndexEntity indexes)
	{
		this.accessor.getPrimaryKey().put(indexes);
	}
	
	public void putAll(Map<String, LinearIndexEntity> indexes)
	{
		for (LinearIndexEntity entry : indexes.values())
		{
			this.put(entry);
		}
	}

	public void remove(String key)
	{
		this.env.getEntityStore().getPrimaryIndex(String.class, LinearIndexEntity.class).delete(key);
	}

	public void removeAll()
	{
		EntityCursor<LinearIndexEntity> results = this.env.getEntityStore().getPrimaryIndex(String.class, LinearIndexEntity.class).entities();
		for (LinearIndexEntity entity : results)
		{
			// The below line aims to remove the warning messages. It means nothing. 12/22/2015, Bing Li
			entity.getKey();
			results.delete();
		}
		results.close();
	}
}
