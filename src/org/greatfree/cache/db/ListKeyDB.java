package org.greatfree.cache.db;

import java.io.File;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.greatfree.util.FileManager;
import org.greatfree.util.UtilConfig;

import com.google.common.collect.Sets;
import com.sleepycat.persist.EntityCursor;

// Created: 05/07/2018, Bing Li
public class ListKeyDB
{
	private File envPath;
	private DBEnv env;
	private ListKeyAccessor accessor;
	
	public ListKeyDB(String path)
	{
		if (!FileManager.isDirExisted(path))
		{
			FileManager.makeDir(path);
		}
		this.envPath = new File(path);
		this.env = new DBEnv(this.envPath, false, DBConfig.DB_CACHE_SIZE, DBConfig.LOCK_TIME_OUT, DBConfig.LIST_KEY_STORE);
		this.accessor = new ListKeyAccessor(this.env.getEntityStore());
	}

	public String getPath()
	{
		return this.envPath.getPath();
	}

	public void close()
	{
		this.accessor.dispose();
		this.env.close();
	}

	public String get(Integer index)
	{
		ListKeyEntity entity = this.accessor.getPrimaryKey().get(index);
		if (entity != DBConfig.NO_LIST_KEY_ENTITY)
		{
			return entity.getKey();
		}
		return UtilConfig.NO_KEY;
	}
	
	public Map<Integer, String> getAllKeys()
	{
//		Map<Integer, String> keys = new HashMap<Integer, String>();
		Map<Integer, String> keys = new ConcurrentHashMap<Integer, String>();
		EntityCursor<ListKeyEntity> results = this.env.getEntityStore().getPrimaryIndex(Integer.class, ListKeyEntity.class).entities();
		for (ListKeyEntity entry : results)
		{
			keys.put(entry.getIndex(), entry.getKey());
		}
		results.close();
		return keys;
	}
	
	public Set<Integer> getKeys()
	{
		EntityCursor<ListKeyEntity> results = this.env.getEntityStore().getPrimaryIndex(Integer.class, ListKeyEntity.class).entities();
		Set<Integer> keys = Sets.newHashSet();
		for (ListKeyEntity entry : results)
		{
			keys.add(entry.getIndex());
		}
		results.close();
		return keys;
	}
	
	public boolean containsKey(Integer index)
	{
		ListKeyEntity entity = this.accessor.getPrimaryKey().get(index);
		if (entity != DBConfig.NO_LIST_KEY_ENTITY)
		{
			return true;
		}
		return false;
	}
	
	public int getSize()
	{
		EntityCursor<ListKeyEntity> results =  this.env.getEntityStore().getPrimaryIndex(Integer.class, ListKeyEntity.class).entities();
		int size = 0;
		for (ListKeyEntity entry : results)
		{
			entry.getIndex();
			size++;
		}
		results.close();
		return size;
	}
	
	public void putAll(Map<Integer, String> keys)
	{
		for (Map.Entry<Integer, String> entry : keys.entrySet())
		{
			this.put(entry.getKey(), entry.getValue());
		}
	}
	
	public void put(Integer index, String key)
	{
		this.accessor.getPrimaryKey().put(new ListKeyEntity(index, key));
	}

	public void remove(Integer index)
	{
		this.env.getEntityStore().getPrimaryIndex(Integer.class, ListKeyEntity.class).delete(index);
	}
	
	public void removeAll()
	{
		EntityCursor<ListKeyEntity> results = this.env.getEntityStore().getPrimaryIndex(String.class, ListKeyEntity.class).entities();
		for (ListKeyEntity entity : results)
		{
			// The below line aims to remove the warning messages. It means nothing. 12/22/2015, Bing Li
			entity.getKey();
			results.delete();
		}
		results.close();
	}
}
