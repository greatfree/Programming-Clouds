package org.greatfree.dip.cps.cache.terminal.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.greatfree.cache.db.CachedDBConfig;
import org.greatfree.cache.distributed.terminal.db.PostfetchDB;
import org.greatfree.dip.cps.cache.data.MyPointing;

import com.sleepycat.persist.EntityCursor;

// Created: 07/11/2018, Bing Li
public class MyPointingDB extends PostfetchDB<MyPointing>
{
	private MyPointingAccessor accessor;

	public MyPointingDB(String path, String storeName)
	{
		super(path, CachedDBConfig.DB_CACHE_SIZE, CachedDBConfig.LOCK_TIME_OUT, storeName);
		this.accessor = new MyPointingAccessor(this.getEnv().getEntityStore());
	}

	@Override
	public MyPointing get(String key)
	{
		MyPointingEntity entity = this.accessor.getPrimaryKey().get(key);
		if (entity != null)
		{
			return new MyPointing(entity.getKey(), entity.getPoints(), entity.getDescription());
		}
		return null;
	}

	@Override
	public List<MyPointing> getList(Set<String> keys)
	{
		List<MyPointing> rs = new ArrayList<MyPointing>();
		MyPointing r;
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
	public Map<String, MyPointing> getMap(Set<String> keys)
	{
		Map<String, MyPointing> rs = new HashMap<String, MyPointing>();
		MyPointing r;
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
		EntityCursor<MyPointingEntity> results = this.getEnv().getEntityStore().getPrimaryIndex(String.class, MyPointingEntity.class).entities();
		int count = 0;
		for (MyPointingEntity entry : results)
		{
			entry.getKey();
			count++;
		}
		results.close();
		return count;
	}

	@Override
	public void save(MyPointing value)
	{
		this.accessor.getPrimaryKey().put(new MyPointingEntity(value.getKey(), value.getPoints(), value.getDescription()));
	}

	@Override
	public void remove(String key)
	{
		this.getEnv().getEntityStore().getPrimaryIndex(String.class, MyPointingEntity.class).delete(key);
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
