package org.greatfree.dsf.cps.cache.front;

import java.io.IOException;
import java.util.List;

import org.greatfree.dsf.cps.cache.data.MyCachePointing;
import org.greatfree.dsf.cps.cache.message.front.PointingByIndexPrefetchListResponse;
import org.greatfree.dsf.cps.cache.message.front.RangePointingsPrefetchListResponse;
import org.greatfree.dsf.cps.cache.message.front.TopPointingsPrefetchListResponse;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.util.Rand;

// Created: 08/04/2018, Bing Li
class PointingPrefetchListStoreClient
{
//	private final static cacheMaxNum = 20;
//	private final static int cacheMaxNum = 50;
	private final static int cacheMaxNum = 1;
//	private final static int totalDataCount = 15000;
	private final static int totalDataCount = 100;
//	private final static int totalDataCount = 5;

	public static void getCachePointingByIndex() throws ClassNotFoundException, RemoteReadException, IOException
	{
//		int cacheMaxNum = 20;
		String cacheKey;
		PointingByIndexPrefetchListResponse response;
		int range = 2000;
		for (int i = 0; i < cacheMaxNum; i++)
		{
			cacheKey = "Cache" + Rand.getRandom(cacheMaxNum);
			for (int index = 0; index < range; index++)
			{
				response = FrontReader.RR().getPointingPrefetchList(cacheKey, index);
				if (response.getPointing() != null)
				{
					System.out.println("index = " + index + ") Cache Key = " + response.getPointing().getCacheKey() + ", key = " + response.getPointing().getKey() + ", points = " + response.getPointing().getPoints());
				}
				else
				{
					System.out.println("index = " + index + ") Cache key: " + cacheKey + " does not contain the index, " + index);
				}
			}
		}
	}

	public static void getTopCachePointings() throws ClassNotFoundException, RemoteReadException, IOException
	{
//		int cacheMaxNum = 20;
		String cacheKey;
		TopPointingsPrefetchListResponse response;
//		int range = 1000;
		int topIndex;
		List<MyCachePointing> pointings;
		int index = 0;
		for (int i = 0; i < cacheMaxNum; i++)
		{
			cacheKey = "Cache" + Rand.getRandom(cacheMaxNum);
			topIndex = Rand.getRandom(totalDataCount);
			System.out.println("top index = " + topIndex);
			response = FrontReader.RR().getTopPointingsPrefetchList(cacheKey, topIndex);
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
		RangePointingsPrefetchListResponse response;
//		int range = 1000;
		int startIndex;
		int endIndex;
		int rangeCount;
		List<MyCachePointing> pointings;
		int index = 0;
		for (int i = 0; i < cacheMaxNum; i++)
		{
			cacheKey = "Cache" + Rand.getRandom(cacheMaxNum);
			endIndex = Rand.getRandom(totalDataCount);
			if (endIndex > 0)
			{
				rangeCount = Rand.getRandom(endIndex);
				startIndex = rangeCount + 1;
			}
			else
			{
				startIndex = 0;
			}
			response = FrontReader.RR().getRangePointingPrefetchList(cacheKey, startIndex, endIndex);
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
