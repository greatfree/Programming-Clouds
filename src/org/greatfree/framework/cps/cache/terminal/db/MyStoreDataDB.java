package org.greatfree.framework.cps.cache.terminal.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.greatfree.cache.db.CachedDBConfig;
import org.greatfree.cache.distributed.terminal.db.PostfetchDB;
import org.greatfree.framework.cps.cache.data.MyStoreData;

import com.sleepycat.persist.EntityCursor;

// Created: 08/08/2018, Bing Li
public class MyStoreDataDB extends PostfetchDB<MyStoreData>
{
	private MyStoreDataAccessor accessor;

	public MyStoreDataDB(String path, String storeName)
	{
		super(path, CachedDBConfig.DB_CACHE_SIZE, CachedDBConfig.LOCK_TIME_OUT, storeName);
		this.accessor = new MyStoreDataAccessor(this.getEnv().getEntityStore());
	}

	@Override
	public MyStoreData get(String key)
	{
		MyStoreDataEntity entity = this.accessor.getPrimaryKey().get(key);
		if (entity != null)
		{
			return new MyStoreData(entity.getKey(), entity.getCacheKey(), entity.getValue(), entity.getTime());
		}
		return null;
	}

	@Override
	public List<MyStoreData> getList(Set<String> keys)
	{
		List<MyStoreData> rs = new ArrayList<MyStoreData>();
		MyStoreData r;
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
	public Map<String, MyStoreData> getMap(Set<String> keys)
	{
		Map<String, MyStoreData> rs = new HashMap<String, MyStoreData>();
		MyStoreData r;
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
		EntityCursor<MyStoreDataEntity> results = this.getEnv().getEntityStore().getPrimaryIndex(String.class, MyStoreDataEntity.class).entities();
		int count = 0;
		for (MyStoreDataEntity entry : results)
		{
			entry.getKey();
			count++;
		}
		results.close();
		return count;
	}

	@Override
	public void save(MyStoreData value)
	{
		this.accessor.getPrimaryKey().put(new MyStoreDataEntity(value.getKey(), value.getCacheKey(), value.getValue(), value.getTime()));
	}

	@Override
	public void remove(String key)
	{
		this.getEnv().getEntityStore().getPrimaryIndex(String.class, MyStoreDataEntity.class).delete(key);
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
