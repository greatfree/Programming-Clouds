package org.greatfree.cache;

import org.greatfree.util.FileManager;
import org.greatfree.util.UtilConfig;

/*
 * The version is tested in the Clouds project. 08/22/2018, Bing Li
 */

// Created: 05/23/2017, Bing Li
public class CacheConfig
{
	public final static String KEY_PATH = "/keys/";
	
	public final static String LIST_POINTS_CACHE = "points";
	public final static String LIST_TIMES_CACHE = "times";
	public final static String LIST_KEYS_CACHE = "keys";
	public final static String LIST_OBSOLETE_KEYS_CACHE = "obs_keys";
	
	public final static String MAP_KEYS = "map_keys";
	public final static String QUEUE_KEYS = "queue_keys";
	public final static String STACK_KEYS = "stack_keys";
	public final static String LIST_KEYS = "list_keys";

	public final static String TERMINAL_DB = "terminal_db";
	
	private final static String IS_EVICTED = "is_evicted";
	
	public final static String getIsEvictedPath(String cachePath)
	{
		return cachePath + UtilConfig.FORWARD_SLASH + IS_EVICTED;
	}

	public final static String getCachePath(String cacheRoot, String cacheKey)
	{
//		return cacheRoot + cacheKey;
		return FileManager.appendSlash(cacheRoot) + cacheKey;
	}
	
	public final static String getPointingListPointsCacheKey(String cacheKey)
	{
		return cacheKey + CacheConfig.LIST_POINTS_CACHE;
	}

	public final static String getTimingListTimesCacheKey(String cacheKey)
	{
		return cacheKey + CacheConfig.LIST_TIMES_CACHE;
	}

	public final static String getPointingListPointsCachePath(String cacheRoot, String cacheKey)
	{
//		return cacheRoot + cacheKey + UtilConfig.FORWARD_SLASH + CacheConfig.LIST_POINTS_CACHE + UtilConfig.FORWARD_SLASH;
		return FileManager.appendSlash(cacheRoot) + cacheKey + UtilConfig.FORWARD_SLASH + CacheConfig.LIST_POINTS_CACHE + UtilConfig.FORWARD_SLASH;
	}

	public final static String getTimingListTimesCachePath(String cacheRoot, String cacheKey)
	{
//		return cacheRoot + cacheKey + UtilConfig.FORWARD_SLASH + CacheConfig.LIST_TIMES_CACHE + UtilConfig.FORWARD_SLASH;
		return FileManager.appendSlash(cacheRoot) + cacheKey + UtilConfig.FORWARD_SLASH + CacheConfig.LIST_TIMES_CACHE + UtilConfig.FORWARD_SLASH;
	}

	public final static String getTerminalDBPath(String cacheRoot, String cacheKey)
	{
		return FileManager.appendSlash(cacheRoot) + cacheKey + UtilConfig.FORWARD_SLASH + CacheConfig.TERMINAL_DB + UtilConfig.FORWARD_SLASH;
	}

	/*
	public final static String getPointingListKeysCacheKey(String cacheKey)
	{
		return cacheKey + CacheConfig.LIST_KEYS_CACHE;
	}

	public final static String getTimingListKeysCacheKey(String cacheKey)
	{
		return cacheKey + CacheConfig.LIST_KEYS_CACHE;
	}
	*/

	public final static String getListKeysCacheKey(String cacheKey)
	{
		return cacheKey + CacheConfig.LIST_KEYS_CACHE;
	}

	/*
	public final static String getPointingListKeysCachePath(String cacheRoot, String cacheKey)
	{
		return cacheRoot + cacheKey + UtilConfig.FORWARD_SLASH + CacheConfig.LIST_KEYS_CACHE + UtilConfig.FORWARD_SLASH;
	}

	public final static String getTimingListKeysCachePath(String cacheRoot, String cacheKey)
	{
		return cacheRoot + cacheKey + UtilConfig.FORWARD_SLASH + CacheConfig.LIST_KEYS_CACHE + UtilConfig.FORWARD_SLASH;
	}
	*/

	public final static String getListKeysCachePath(String cacheRoot, String cacheKey)
	{
//		return cacheRoot + cacheKey + UtilConfig.FORWARD_SLASH + CacheConfig.LIST_KEYS_CACHE + UtilConfig.FORWARD_SLASH;
		return FileManager.appendSlash(cacheRoot) + cacheKey + UtilConfig.FORWARD_SLASH + CacheConfig.LIST_KEYS_CACHE + UtilConfig.FORWARD_SLASH;
	}


	public final static String getObsoleteListKeysCachePath(String cacheRoot, String cacheKey)
	{
//		return cacheRoot + cacheKey + UtilConfig.FORWARD_SLASH + CacheConfig.LIST_KEYS_CACHE + UtilConfig.FORWARD_SLASH;
		return FileManager.appendSlash(cacheRoot) + cacheKey + UtilConfig.FORWARD_SLASH + CacheConfig.LIST_OBSOLETE_KEYS_CACHE + UtilConfig.FORWARD_SLASH;
	}

	public final static String getMapCacheKeyDBPath(String cacheRoot, String cacheKey)
	{
//		return cacheRoot + cacheKey + CacheConfig.KEY_PATH;
		return FileManager.appendSlash(cacheRoot) + cacheKey + CacheConfig.KEY_PATH;
	}
	
	public final static String getPointingListPointsKeyDBPath(String cacheRoot, String cacheKey)
	{
		return CacheConfig.getPointingListPointsCachePath(cacheRoot, cacheKey) + CacheConfig.KEY_PATH;
	}

	public final static String getTimingListTimesKeyDBPath(String cacheRoot, String cacheKey)
	{
		return CacheConfig.getTimingListTimesCachePath(cacheRoot, cacheKey) + CacheConfig.KEY_PATH;
	}

	/*
	public final static String getPointingListKeysKeyDBPath(String cacheRoot, String cacheKey)
	{
		return CacheConfig.getListKeysCachePath(cacheRoot, cacheKey) + CacheConfig.KEY_PATH;
	}

	public final static String getTimingListKeysKeyDBPath(String cacheRoot, String cacheKey)
	{
		return CacheConfig.getListKeysCachePath(cacheRoot, cacheKey) + CacheConfig.KEY_PATH;
	}
	*/
	public final static String getListKeysKeyDBPath(String cacheRoot, String cacheKey)
	{
		return CacheConfig.getListKeysCachePath(cacheRoot, cacheKey) + CacheConfig.KEY_PATH;
	}
	
	/*
	public final static String getPointingListCachePath(String cacheRoot, String cacheKey)
	{
		return cacheRoot + cacheKey;
	}
	
	public final static String getPointingListCacheKernelKeysPath(String cacheRoot, String cacheKey)
	{
		return cacheRoot + cacheKey + CacheConfig.KEY_PATH;
	}
	
	public final static String getPointingListCachePointKeysPath(String cacheRoot, String cacheKey)
	{
		return cacheRoot + cacheKey + CacheConfig.LIST_POINTS_CACHE + CacheConfig.KEY_PATH;
	}
	
	public final static String getPointingListCacheSecondaryKeysPath(String cacheRoot, String cacheKey)
	{
		return cacheRoot + cacheKey + CacheConfig.LIST_KEYS_CACHE + CacheConfig.KEY_PATH;
	}
	*/
	
	/*
	public final static String getCompoundMapKey(String rootKey, String childKey)
	{
		return Tools.getAHash(rootKey + childKey);
	}
	*/
	
	public final static String getStoreKeysRootPath(String mapStorePath, String keys_path)
	{
//		return FileManager.appendSlash(mapStorePath) + CacheConfig.MAP_KEYS + UtilConfig.FORWARD_SLASH;
		return FileManager.appendSlash(mapStorePath) + keys_path + UtilConfig.FORWARD_SLASH;
	}
	
	public final static String getStoreKeysDBPath(String mapStorePath)
	{
		return FileManager.appendSlash(mapStorePath) + "db/";
	}

	public final static int DISTRIBUTED_PERSISTABLE_MAP = 0;
}
