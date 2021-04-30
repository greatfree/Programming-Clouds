package edu.chainnet.s3.meta.table;

import java.util.Date;
import java.util.Set;

import org.greatfree.cache.factory.StoreKeyCreator;
import org.greatfree.cache.local.CacheMap;
import org.greatfree.cache.local.store.MapStore;
import org.greatfree.util.Rand;
import org.greatfree.util.UtilConfig;

import edu.chainnet.s3.meta.MetaConfig;

/*
 * All of states of storage servers are retained in the cache. 07/10/2020, Bing Li
 */

// Created: 07/10/2020, Bing Li
public class MetaCache
{
	private CacheMap<SSState, SSStateFactory> states;
	private CacheMap<FileMeta, FileMetaFactory> metas;
	private MapStore<SlicePartition, SlicePartitionFactory, StoreKeyCreator> partitionIndexes;
	
	private MetaCache()
	{
	}

	private static MetaCache instance = new MetaCache();
	
	public static MetaCache META()
	{
		if (instance == null)
		{
			instance = new MetaCache();
			return instance;
		}
		else
		{
			return instance;
		}
	}
	
	public void dispose()
	{
		this.states.close();
		this.metas.close();
		this.partitionIndexes.shutdown();
	}

	public void init(String path)
	{
		this.states = new CacheMap.CacheMapBuilder<SSState, SSStateFactory>()
				.factory(new SSStateFactory())
				.rootPath(path)
				.cacheKey(MetaConfig.SSSTATE_CACHE)
				.cacheSize(MetaConfig.SSSTATE_CACHE_SIZE)
				.offheapSizeInMB(MetaConfig.SSSTATE_OFFHEAP_SIZE_IN_MB)
				.diskSizeInMB(MetaConfig.SSSTATE_DISK_SIZE_IN_MB)
				.build();

		this.metas = new CacheMap.CacheMapBuilder<FileMeta, FileMetaFactory>()
				.factory(new FileMetaFactory())
				.rootPath(path)
				.cacheKey(MetaConfig.FILEMETA_CACHE)
				.cacheSize(MetaConfig.FILEMETA_CACHE_SIZE)
				.offheapSizeInMB(MetaConfig.FILEMETA_OFFHEAP_SIZE_IN_MB)
				.diskSizeInMB(MetaConfig.FILEMETA_DISK_SIZE_IN_MB)
				.build();

		this.partitionIndexes = new MapStore.MapStoreBuilder<SlicePartition, SlicePartitionFactory, StoreKeyCreator>()
				.rootPath(path)
				.storeKey(MetaConfig.SLICE_PARTITION_STORE_KEY)
				.factory(new SlicePartitionFactory())
				.cacheSize(MetaConfig.SLICE_PARTITION_CACHE_SIZE)
				.keyCreator(new StoreKeyCreator())
				.totalStoreSize(MetaConfig.SLICE_PARTITION_STORE_SIZE)
				.offheapSizeInMB(MetaConfig.SLICE_PARTITION_CACHE_OFFHEAP_SIZE)
				.diskSizeInMB(MetaConfig.SLICE_PARTITION_CACHE_DISK_SIZE)
				.build();
	}
	
	/*
	 * The below methods for Storage State cache. 07/12/2020, Bing Li
	 */
	public Set<String> getSSStateKeys()
	{
		return this.states.getKeys();
	}
	
	public int getSSSize()
	{
		return this.states.getSize();
	}
	
	public void put(SSState dss)
	{
		this.states.put(dss.getKey(), dss);
	}
	
	public SSState getState(String key)
	{
		return this.states.get(key);
	}
	
	public boolean isAlive(String key)
	{
		return this.states.get(key).isAlive();
	}
	
	public long getFreeSpace(String key)
	{
		return this.states.get(key).getFreeStorageSpace();
	}
	
	public Date getLastUpdated()
	{
		Set<String> keys = this.states.getKeys();
		if (keys.size() > 0)
		{
			return this.states.get(Rand.getRandomStringInSet(keys)).getUpdatedTime();
		}
		return UtilConfig.NO_TIME;
	}
	
	/*
	 * The below methods for File Meta cache. 07/12/2020, Bing Li
	 */
	public void put(FileMeta meta)
	{
		this.metas.put(meta.getKey(), meta);
	}
	
	public FileMeta getMeta(String key)
	{
		return this.metas.get(key);
	}
	
	public String getFileName(String key)
	{
		return this.metas.get(key).getFileName();
	}
	
	public int getK(String key)
	{
		return this.metas.get(key).getK();
	}
	
	public int getN(String key)
	{
		return this.metas.get(key).getN();
	}
	
	public int getBlockSize(String key)
	{
		return this.metas.get(key).getAddresses().size();
	}
	
	public String getAddress(String key, int index)
	{
		return this.metas.get(key).getAddresses().get(index);
	}
	
	public void put(String sessionKey, String sliceKey, int partitionIndex)
	{
		this.partitionIndexes.put(sessionKey, new SlicePartition(sessionKey, sliceKey, partitionIndex));
	}
	
	public int get(String sessionKey, String sliceKey)
	{
		if (this.partitionIndexes.isExisted(sessionKey))
		{
			return this.partitionIndexes.get(sessionKey, sliceKey).getPartitionIndex();
		}
		return UtilConfig.NO_INDEX;
	}
}
