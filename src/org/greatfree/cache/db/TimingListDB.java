package org.greatfree.cache.db;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.greatfree.util.FileManager;
import org.greatfree.util.Time;

import com.sleepycat.persist.EntityCursor;

// Created: 05/07/2018, Bing Li
public class TimingListDB
{
	private File envPath;
	private DBEnv env;
	private TimingListAccessor accessor;

	public TimingListDB(String path)
	{
		if (!FileManager.isDirExisted(path))
		{
			FileManager.makeDir(path);
		}
		this.envPath = new File(path);
		this.env = new DBEnv(this.envPath, false, DBConfig.DB_CACHE_SIZE, DBConfig.LOCK_TIME_OUT, DBConfig.TIMING_LIST_STORE);
		this.accessor = new TimingListAccessor(this.env.getEntityStore());
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
	
	public Map<String, Date> getValues()
	{
		EntityCursor<TimingListEntity> results = this.env.getEntityStore().getPrimaryIndex(String.class, TimingListEntity.class).entities();
		Map<String, Date> times = new HashMap<String, Date>();
		for (TimingListEntity entry : results)
		{
			times.put(entry.getKey(), entry.getTime());
		}
		results.close();
		return times;
	}
	
	public Date get(String key)
	{
		TimingListEntity entity = this.accessor.getPrimaryKey().get(key);
		if (entity != DBConfig.NO_TIMING_LIST_ENTITY)
		{
			return entity.getTime();
		}
		return Time.INIT_TIME;
	}
	
	public int getSize()
	{
		EntityCursor<TimingListEntity> results =  this.env.getEntityStore().getPrimaryIndex(Integer.class, TimingListEntity.class).entities();
		int size = 0;
		for (TimingListEntity entry : results)
		{
			entry.getKey();
			size++;
		}
		results.close();
		return size;
	}
	
	public void putAll(Map<String, Date> times)
	{
		for (Map.Entry<String, Date> entry : times.entrySet())
		{
			this.put(entry.getKey(), entry.getValue());
		}
	}
	
	public void put(String key, Date points)
	{
		this.accessor.getPrimaryKey().put(new TimingListEntity(key, points));
	}

	public void remove(String key)
	{
		this.env.getEntityStore().getPrimaryIndex(String.class, TimingListEntity.class).delete(key);
	}
}
