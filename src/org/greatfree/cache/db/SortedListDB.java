package org.greatfree.cache.db;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.greatfree.util.FileManager;
import org.greatfree.util.Points;

import com.sleepycat.persist.EntityCursor;

// Created: 05/07/2018, Bing Li
public class SortedListDB
{
	private File envPath;
	private DBEnv env;
	private SortedListAccessor accessor;

	public SortedListDB(String path)
	{
		if (!FileManager.isDirExisted(path))
		{
			FileManager.makeDir(path);
		}
		this.envPath = new File(path);
		this.env = new DBEnv(this.envPath, false, DBConfig.DB_CACHE_SIZE, DBConfig.LOCK_TIME_OUT, DBConfig.POINTING_LIST_STORE);
		this.accessor = new SortedListAccessor(this.env.getEntityStore());
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
	
	public Map<String, Float> getValues()
	{
		EntityCursor<SortedListEntity> results = this.env.getEntityStore().getPrimaryIndex(String.class, SortedListEntity.class).entities();
//		Map<String, Double> points = new HashMap<String, Double>();
		Map<String, Float> points = new ConcurrentHashMap<String, Float>();
//		Map<String, Float> points = new HashMap<String, Float>();
//		Map<String, Long> points = new ConcurrentHashMap<String, Long>();
		for (SortedListEntity entry : results)
		{
			points.put(entry.getKey(), entry.getPoints());
		}
		results.close();
		return points;
	}
	
	public double get(String key)
	{
		SortedListEntity entity = this.accessor.getPrimaryKey().get(key);
		if (entity != DBConfig.NO_POINTING_LIST_ENTITY)
		{
			return entity.getPoints();
		}
		return Points.NO_POINT;
	}
	
	public int getSize()
	{
		EntityCursor<SortedListEntity> results =  this.env.getEntityStore().getPrimaryIndex(Integer.class, SortedListEntity.class).entities();
		int size = 0;
		for (SortedListEntity entry : results)
		{
			entry.getKey();
			size++;
		}
		results.close();
		return size;
	}
	
	public void putAll(Map<String, Float> points)
	{
		for (Map.Entry<String, Float> entry : points.entrySet())
		{
			this.put(entry.getKey(), entry.getValue());
		}
	}
	
//	public void put(String key, double points)
	public void put(String key, float points)
	{
		this.accessor.getPrimaryKey().put(new SortedListEntity(key, points));
	}

	public void remove(String key)
	{
		this.env.getEntityStore().getPrimaryIndex(String.class, SortedListEntity.class).delete(key);
	}

	public void removeAll()
	{
		EntityCursor<SortedListEntity> results = this.env.getEntityStore().getPrimaryIndex(String.class, SortedListEntity.class).entities();
		for (SortedListEntity entity : results)
		{
			// The below line aims to remove the warning messages. It means nothing. 12/22/2015, Bing Li
			entity.getKey();
			results.delete();
		}
		results.close();
	}
}
