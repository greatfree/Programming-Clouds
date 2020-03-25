package org.greatfree.cache.db;

import java.io.File;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.greatfree.cache.factory.TimingListIndexes;
import org.greatfree.util.FileManager;

import com.google.common.collect.Sets;
import com.sleepycat.persist.EntityCursor;

// Created: 05/08/2018, Bing Li
public class TimingListIndexDB
{
	private File envPath;
	private DBEnv env;
	private TimingListIndexAccessor accessor;

	public TimingListIndexDB(String path)
	{
		if (!FileManager.isDirExisted(path))
		{
			FileManager.makeDir(path);
		}
		this.envPath = new File(path);
		this.env = new DBEnv(this.envPath, false, DBConfig.DB_CACHE_SIZE, DBConfig.LOCK_TIME_OUT, DBConfig.TIMING_LIST_INDEX_STORE);
		this.accessor = new TimingListIndexAccessor(this.env.getEntityStore());
	}

	public void close()
	{
		this.accessor.dispose();
		this.env.close();
	}
	
	public boolean containsKey(String key)
	{
		TimingListIndexEntity entity = this.accessor.getPrimaryKey().get(key);
		if (entity != DBConfig.NO_TIMING_LIST_INDEX_ENTITY)
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
		EntityCursor<TimingListIndexEntity> results = this.env.getEntityStore().getPrimaryIndex(String.class, TimingListIndexEntity.class).entities();
		int size = 0;
		for (TimingListIndexEntity entry : results)
		{
			entry.getCacheKey();
			size++;
		}
		results.close();
		return size;
	}
	
	public Set<String> getKeys()
	{
		EntityCursor<TimingListIndexEntity> results = this.env.getEntityStore().getPrimaryIndex(String.class, TimingListIndexEntity.class).entities();
		Set<String> keys = Sets.newHashSet();
		for (TimingListIndexEntity entry : results)
		{
			keys.add(entry.getCacheKey());
		}
		results.close();
		return keys;
	}
	
	public Map<String, TimingListIndexes> getAllIndexes()
	{
		EntityCursor<TimingListIndexEntity> results = this.env.getEntityStore().getPrimaryIndex(String.class, TimingListIndexEntity.class).entities();
		Map<String, TimingListIndexes> indexes = new ConcurrentHashMap<String, TimingListIndexes>();
		for (TimingListIndexEntity entry : results)
		{
			indexes.put(entry.getCacheKey(), new TimingListIndexes(entry.getKeys(), entry.getPoints()));
		}
		results.close();
		return indexes;
	}

	public TimingListIndexes get(String key)
	{
		TimingListIndexEntity entity = this.accessor.getPrimaryKey().get(key);
		if (entity != DBConfig.NO_TIMING_LIST_INDEX_ENTITY)
		{
			return new TimingListIndexes(entity.getKeys(), entity.getPoints());
		}
		return DBConfig.NO_TIMING_LIST_INDEXES;
	}
	
	public void put(String key, TimingListIndexes index)
	{
		this.accessor.getPrimaryKey().put(new TimingListIndexEntity(key, index.getKeys(), index.getTimings()));
	}
	
	public void putAll(Map<String, TimingListIndexes> indexes)
	{
		for (Map.Entry<String, TimingListIndexes> entry : indexes.entrySet())
		{
			this.put(entry.getKey(), entry.getValue());
		}
	}

	public void remove(String key)
	{
		this.env.getEntityStore().getPrimaryIndex(String.class, TimingListIndexEntity.class).delete(key);
	}
	
	public void removeAll()
	{
		EntityCursor<TimingListIndexEntity> results = this.env.getEntityStore().getPrimaryIndex(String.class, TimingListIndexEntity.class).entities();
		for (TimingListIndexEntity entity : results)
		{
			// The below line aims to remove the warning messages. It means nothing. 12/22/2015, Bing Li
			entity.getCacheKey();
			results.delete();
		}
		results.close();
	}
}
