package org.greatfree.cache.db;

import java.io.File;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.greatfree.cache.factory.SortedListIndexes;
import org.greatfree.util.FileManager;

import com.sleepycat.persist.EntityCursor;

// Created: 05/06/2018, Bing Li
public class SortedListIndexDB
{
	private File envPath;
	private DBEnv env;
	private SortedListIndexAccessor accessor;

	public SortedListIndexDB(String path)
	{
//		super(Tools.getAHash(path));
		if (!FileManager.isDirExisted(path))
		{
			FileManager.makeDir(path);
		}
		this.envPath = new File(path);
		this.env = new DBEnv(this.envPath, false, DBConfig.DB_CACHE_SIZE, DBConfig.LOCK_TIME_OUT, DBConfig.SORTED_LIST_INDEX_STORE);
		this.accessor = new SortedListIndexAccessor(this.env.getEntityStore());
	}

	public void dispose()
	{
		this.accessor.dispose();
		this.env.close();
	}
	
	public boolean containsKey(String key)
	{
		SortedListIndexEntity entity = this.accessor.getPrimaryKey().get(key);
		if (entity != DBConfig.NO_SORTED_LIST_INDEX_ENTITY)
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
		EntityCursor<SortedListIndexEntity> results = this.env.getEntityStore().getPrimaryIndex(String.class, SortedListIndexEntity.class).entities();
		int size = 0;
		for (SortedListIndexEntity entry : results)
		{
			entry.getListKey();
			size++;
		}
		results.close();
		return size;
	}
	
	public Set<String> getKeys()
	{
		EntityCursor<SortedListIndexEntity> results = this.env.getEntityStore().getPrimaryIndex(String.class, SortedListIndexEntity.class).entities();
//		Set<String> keys = Sets.newHashSet();
		Set<String> keys = new HashSet<String>();
		for (SortedListIndexEntity entry : results)
		{
			keys.add(entry.getListKey());
		}
		results.close();
		return keys;
	}
	
	public Map<String, SortedListIndexes> getAllIndexes()
	{
		EntityCursor<SortedListIndexEntity> results = this.env.getEntityStore().getPrimaryIndex(String.class, SortedListIndexEntity.class).entities();
		Map<String, SortedListIndexes> indexes = new ConcurrentHashMap<String, SortedListIndexes>();
		for (SortedListIndexEntity entry : results)
		{
			indexes.put(entry.getListKey(), new SortedListIndexes(entry.getKeys(), entry.getPoints(), entry.getObsoleteKeys()));
		}
		results.close();
		return indexes;
	}

	public SortedListIndexes get(String key)
	{
		SortedListIndexEntity entity = this.accessor.getPrimaryKey().get(key);
		if (entity != DBConfig.NO_SORTED_LIST_INDEX_ENTITY)
		{
			return new SortedListIndexes(entity.getKeys(), entity.getPoints(), entity.getObsoleteKeys());
		}
		return DBConfig.NO_SORTED_LIST_INDEXES;
	}
	
	public void put(String key, SortedListIndexes index)
	{
		this.accessor.getPrimaryKey().put(new SortedListIndexEntity(key, index.getKeys(), index.getPoints(), index.getObsoleteKeys()));
	}
	
	public void putAll(Map<String, SortedListIndexes> indexes)
	{
		for (Map.Entry<String, SortedListIndexes> entry : indexes.entrySet())
		{
			this.put(entry.getKey(), entry.getValue());
		}
	}

	public void remove(String key)
	{
		this.env.getEntityStore().getPrimaryIndex(String.class, SortedListIndexEntity.class).delete(key);
	}
	
	public void removeAll()
	{
		EntityCursor<SortedListIndexEntity> results = this.env.getEntityStore().getPrimaryIndex(String.class, SortedListIndexEntity.class).entities();
		for (SortedListIndexEntity entity : results)
		{
			// The below line aims to remove the warning messages. It means nothing. 12/22/2015, Bing Li
			entity.getListKey();
			results.delete();
		}
		results.close();
	}
}
