package org.greatfree.dsf.cps.cache.terminal.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.greatfree.cache.db.CachedDBConfig;
import org.greatfree.cache.distributed.terminal.db.PostfetchDB;
import org.greatfree.testing.cache.local.MyUKValue;

import com.sleepycat.persist.EntityCursor;

// Created: 02/25/2019, Bing Li
public class MyUKDB extends PostfetchDB<MyUKValue>
{
	private MyUKAccessor accessor;

	public MyUKDB(String path, String storeName)
	{
		super(path, CachedDBConfig.DB_CACHE_SIZE, CachedDBConfig.LOCK_TIME_OUT, storeName);
		this.accessor = new MyUKAccessor(this.getEnv().getEntityStore());
	}

	@Override
	public MyUKValue get(String key)
	{
		MyUKEntity entity = this.accessor.getPrimaryKey().get(key);
		if (entity != null)
		{
			return new MyUKValue(entity.getKey(), entity.getPoints());
		}
		return null;
	}

	@Override
	public List<MyUKValue> getList(Set<String> keys)
	{
		List<MyUKValue> rs = new ArrayList<MyUKValue>();
		MyUKValue r;
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
	public Map<String, MyUKValue> getMap(Set<String> keys)
	{
		Map<String, MyUKValue> rs = new HashMap<String, MyUKValue>();
		MyUKValue r;
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
		EntityCursor<MyUKEntity> results = this.getEnv().getEntityStore().getPrimaryIndex(String.class, MyUKEntity.class).entities();
		int count = 0;
		for (MyUKEntity entry : results)
		{
			entry.getKey();
			count++;
		}
		results.close();
		return count;
	}

	@Override
	public void save(MyUKValue value)
	{
		this.accessor.getPrimaryKey().put(new MyUKEntity(value.getKey(), value.getPoints()));
	}

	@Override
	public void remove(String key)
	{
		this.getEnv().getEntityStore().getPrimaryIndex(String.class, MyUKEntity.class).delete(key);
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
