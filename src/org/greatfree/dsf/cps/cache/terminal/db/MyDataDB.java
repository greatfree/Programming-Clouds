package org.greatfree.dsf.cps.cache.terminal.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.greatfree.cache.db.CachedDBConfig;
import org.greatfree.cache.distributed.terminal.db.PostfetchDB;
import org.greatfree.dsf.cps.cache.data.MyData;

import com.sleepycat.persist.EntityCursor;

// Created: 07/08/2018, Bing Li
public class MyDataDB extends PostfetchDB<MyData>
{
	private MyDataAccessor accessor;

	public MyDataDB(String path, String storeName)
	{
		super(path, CachedDBConfig.DB_CACHE_SIZE, CachedDBConfig.LOCK_TIME_OUT, storeName);
		this.accessor = new MyDataAccessor(this.getEnv().getEntityStore());
	}

	@Override
	public MyData get(String key)
	{
		MyDataEntity entity = this.accessor.getPrimaryKey().get(key);
		if (entity != null)
		{
			return new MyData(entity.getKey(), entity.getNumber(), entity.getTime());
		}
		return null;
	}

	@Override
	public List<MyData> getList(Set<String> keys)
	{
		List<MyData> rs = new ArrayList<MyData>();
		MyData r;
		for (String key : keys)
		{
			r = this.get(key);
			if (r != null)
			{
				rs.add(r);
			}
		}
		return rs;
	}

	@Override
	public Map<String, MyData> getMap(Set<String> keys)
	{
		Map<String, MyData> rs = new HashMap<String, MyData>();
		MyData r;
		for (String key : keys)
		{
			r = this.get(key);
			if (r != null)
			{
				rs.put(r.getKey(), r);
			}
		}
		return rs;
	}

	@Override
	public int getSize()
	{
		EntityCursor<MyDataEntity> results = this.getEnv().getEntityStore().getPrimaryIndex(String.class, MyDataEntity.class).entities();
		int count = 0;
		for (MyDataEntity entry : results)
		{
			entry.getKey();
			count++;
		}
		results.close();
		return count;
	}

	@Override
	public void save(MyData value)
	{
		this.accessor.getPrimaryKey().put(new MyDataEntity(value.getKey(), value.getNumber(), value.getTime()));
	}

	@Override
	public void remove(String key)
	{
		this.getEnv().getEntityStore().getPrimaryIndex(String.class, MyDataEntity.class).delete(key);
	}

	@Override
	public void removeAll(Set<String> keys)
	{
		for (String key : keys)
		{
			this.remove(key);
		}
	}

	@Override
	public void dispose()
	{
		this.accessor.dispose();
		this.close();
	}

}
