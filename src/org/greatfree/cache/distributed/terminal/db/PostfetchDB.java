package org.greatfree.cache.distributed.terminal.db;

import java.io.File;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.greatfree.cache.db.DBEnv;
import org.greatfree.util.FileManager;

// Created: 05/12/2018, Bing Li
// public interface PostfetchDBable<Value extends Pointing>
public abstract class PostfetchDB<Value extends Serializable>
{
	private File envPath;
	private DBEnv env;
	
	public PostfetchDB(String path, long cacheSize, long lockTimeout, String storeName)
	{
		if (!FileManager.isDirExisted(path))
		{
			FileManager.makeDir(path);
		}
		this.envPath = new File(path);
		this.env = new DBEnv(this.envPath, false, cacheSize, lockTimeout, storeName);
	}
	
	public File getEnvPath()
	{
		return this.envPath;
	}
	
	public DBEnv getEnv()
	{
		return this.env;
	}
	
	public void close()
	{
		this.env.close();
	}

	public abstract Value get(String key);
	public abstract List<Value> getList(Set<String> keys);
	public abstract Map<String, Value> getMap(Set<String> keys);
	public abstract int getSize();
	public abstract void save(Value value);
	public abstract void remove(String key);
	public abstract void removeAll(Set<String> keys);
	public abstract void dispose();
}
