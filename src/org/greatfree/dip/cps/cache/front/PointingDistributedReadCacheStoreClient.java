package org.greatfree.dip.cps.cache.front;

import java.io.IOException;
import java.util.List;

import org.greatfree.dip.cps.cache.TestCacheConfig;
import org.greatfree.dip.cps.cache.data.MyCachePointing;
import org.greatfree.dip.cps.cache.message.front.IsCacheReadExistedInPointingStoreResponse;
import org.greatfree.dip.cps.cache.message.front.RangeReadCachePointingsResponse;
import org.greatfree.dip.cps.cache.message.front.TopReadCachePointingsResponse;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.util.Rand;

// Created: 08/05/2018, Bing Li
class PointingDistributedReadCacheStoreClient
{
	private final static int cacheMaxNum = 50;
//	private final static int cacheMaxNum = 1;
//	private final static int totalDataCount = 15000;

	public static void isCacheExisted() throws ClassNotFoundException, RemoteReadException, IOException
	{
		int currentCacheMaxNum = cacheMaxNum + 20;
		String cacheKey;
		IsCacheReadExistedInPointingStoreResponse response;
		for (int i = 0; i < currentCacheMaxNum; i++)
		{
			cacheKey = "Cache" + i;
			response = FrontReader.RR().isCacheReadExisted(cacheKey);
			if (response.isExisted())
			{
				System.out.println("Cache key = " + cacheKey + " exists in the store");
			}
			else
			{
				System.out.println("Cache key = " + cacheKey + " does NOT exist in the store");
			}
		}
	}

	public static void getTopCachePointings() throws ClassNotFoundException, RemoteReadException, IOException
	{
//		int cacheMaxNum = 20;
		String cacheKey;
		TopReadCachePointingsResponse response;
//		int range = 1000;
		int topIndex;
		List<MyCachePointing> pointings;
		int index = 0;
		for (int i = 0; i < cacheMaxNum; i++)
		{
			cacheKey = "Cache" + Rand.getRandom(cacheMaxNum);
//			topIndex = Rand.getRandom(totalDataCount / cacheMaxNum);
			topIndex = Rand.getRandom(TestCacheConfig.SORTED_DISTRIBUTED_CACHE_SIZE);
			System.out.println("top index = " + topIndex);
			response = FrontReader.RR().getTopReadCachePointing(cacheKey, topIndex);
			if (response.getPointings() != null)
			{
				index = 0;
				pointings = response.getPointings();
				System.out.println(index + ") CacheKey = " + cacheKey + ", top data count = " + pointings.size());
				for (MyCachePointing pointing : pointings)
				{
					System.out.println(index++ + ") CacheKey = " + pointing.getCacheKey() + ", key = " + pointing.getKey() + ", points = " + pointing.getPoints());
				}
				System.out.println("The top index = " + topIndex);
			}
			else
			{
				System.out.println("There is no data for the cache " + cacheKey + " at the top index = " + topIndex);
			}
		}
	}
	
	public static void getRangeCachePointings() throws ClassNotFoundException, RemoteReadException, IOException
	{
//		int cacheMaxNum = 20;
		String cacheKey;
		RangeReadCachePointingsResponse response;
//		int range = 1000;
		int startIndex;
		int endIndex;
		int rangeCount;
		List<MyCachePointing> pointings;
		int index = 0;
		for (int i = 0; i < cacheMaxNum; i++)
		{
			cacheKey = "Cache" + Rand.getRandom(cacheMaxNum);
			
			// TestCacheConfig.POINTING_DISTRIBUTED_CACHE_SIZE
//			endIndex = Rand.getRandom(totalDataCount / cacheMaxNum);
			endIndex = Rand.getRandom(TestCacheConfig.SORTED_DISTRIBUTED_CACHE_SIZE);
			if (endIndex > 0)
			{
				rangeCount = Rand.getRandom(endIndex);
				startIndex = rangeCount + 1;
			}
			else
			{
				startIndex = 0;
			}
			response = FrontReader.RR().getRangeReadCachePointing(cacheKey, startIndex, endIndex);
			if (response.getPointings() != null)
			{
				index = startIndex;
				pointings = response.getPointings();
				for (MyCachePointing pointing : pointings)
				{
					System.out.println(index++ + ") CacheKey = " + pointing.getCacheKey() + ", key = " + pointing.getKey() + ", points = " + pointing.getPoints());
				}
				System.out.println("The range, start index = " + startIndex + ", end index = " + endIndex);
			}
			else
			{
				System.out.println("There is no data for the cache " + cacheKey + " at the range, start index = " + startIndex + ", end index = " + endIndex);
			}
		}
	}
}
