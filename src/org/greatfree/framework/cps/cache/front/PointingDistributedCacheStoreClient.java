package org.greatfree.framework.cps.cache.front;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.framework.cps.cache.TestCacheConfig;
import org.greatfree.framework.cps.cache.data.MyCachePointing;
import org.greatfree.framework.cps.cache.message.front.CachePointingByIndexResponse;
import org.greatfree.framework.cps.cache.message.front.CachePointingByKeyResponse;
import org.greatfree.framework.cps.cache.message.front.ContainsKeyOfCachePointingResponse;
import org.greatfree.framework.cps.cache.message.front.IsCacheEmptyInPointingStoreResponse;
import org.greatfree.framework.cps.cache.message.front.IsCacheExistedInPointingStoreResponse;
import org.greatfree.framework.cps.cache.message.front.MaxCachePointingResponse;
import org.greatfree.framework.cps.cache.message.front.RangeCachePointingsResponse;
import org.greatfree.framework.cps.cache.message.front.TopCachePointingsResponse;
import org.greatfree.util.Rand;
import org.greatfree.util.Time;

// Created: 07/24/2018, Bing Li
class PointingDistributedCacheStoreClient
{
//	private final static cacheMaxNum = 20;
//	private final static int cacheMaxNum = 50;
	private final static int cacheMaxNum = 1;
	private final static int totalDataCount = 15000;
//	private final static int totalDataCount = 100;
//	private final static int totalDataCount = 5;
	private final static int maxValue = 2000;
	private final static int testCount = 50;
	private final static int range = 2000;
	
	public static void saveDataIntoPointingDistributedCacheStore() throws IOException, InterruptedException
	{
		MyCachePointing data;
		Date startTime;
		Date endTime;
//		int cacheMaxNum = 20;
		for (int i = 0; i < totalDataCount; i++)
		{
			data = new MyCachePointing("Cache" + Rand.getRandom(cacheMaxNum), "Key" + i, Rand.getRandom(maxValue));
			startTime = Calendar.getInstance().getTime();
			FrontEventer.RE().saveCachePointingIntoPointingDistributedCacheStore(data.getCacheKey(), data);
			endTime = Calendar.getInstance().getTime();
			System.out.println("It takes " + Time.getTimespanInMilliSecond(endTime, startTime) + "ms to save the data: cacheKey = " + data.getCacheKey() + ", points = " + data.getPoints());			
		}
	}

	public static void saveDataIntoPointingsDistributedCacheStore() throws IOException, InterruptedException
	{
		MyCachePointing data;
		Date startTime;
		Date endTime;
//		int cacheMaxNum = 20;
		String cacheKey;
		Map<String, List<MyCachePointing>> pointings = new HashMap<String, List<MyCachePointing>>();
		for (int i = 0; i < totalDataCount; i++)
		{
			cacheKey = "Cache" + Rand.getRandom(cacheMaxNum);
			if (!pointings.containsKey(cacheKey))
			{
				pointings.put(cacheKey, new ArrayList<MyCachePointing>());
			}
			data = new MyCachePointing(cacheKey, "Key" + i, Rand.getRandom(maxValue));
			pointings.get(cacheKey).add(data);
		}
		for (Map.Entry<String, List<MyCachePointing>> entry : pointings.entrySet())
		{
			startTime = Calendar.getInstance().getTime();
			FrontEventer.RE().saveCachePointingsIntoPointingDistributedCacheStore(entry.getKey(), entry.getValue());
			endTime = Calendar.getInstance().getTime();
			System.out.println("CacheKey = " + entry.getKey() + ": It takes " + Time.getTimespanInMilliSecond(endTime, startTime) + "ms to save the data ...");			
		}
	}

	public static void containsKey() throws ClassNotFoundException, RemoteReadException, IOException
	{
//		int cacheMaxNum = 20;
		String cacheKey;
		String dataKey;
//		int maxNum = 15000;
//		int testCount = 50;
		ContainsKeyOfCachePointingResponse response;
		for (int i = 0; i < testCount; i++)
		{
			cacheKey = "Cache" + Rand.getRandom(cacheMaxNum);
			dataKey = "Key" + Rand.getRandom(totalDataCount);
			response = FrontReader.RR().containsKeyOfCachePointing(cacheKey, dataKey);
			if (response.isExisted())
			{
				System.out.println(cacheKey + " DOES contain " + dataKey);
			}
			else
			{
				System.out.println(cacheKey + " DOES NOT contain " + dataKey);
			}
		}
	}
	
	public static void getMaxCachePointing() throws ClassNotFoundException, RemoteReadException, IOException
	{
//		int cacheMaxNum = 20;
		String cacheKey;
//		int maxNum = 15000;
		MaxCachePointingResponse response;
		for (int i = 0; i < cacheMaxNum; i++)
		{
			cacheKey = "Cache" + Rand.getRandom(cacheMaxNum);
			response = FrontReader.RR().getMaxCachePointing(cacheKey);
			System.out.println("The max value of " + cacheKey + " is " + response.getPointing().getPoints());
		}
	}
	
	public static void getCachePointingByKey() throws ClassNotFoundException, RemoteReadException, IOException
	{
//		int cacheMaxNum = 20;
		String cacheKey;
		String dataKey;
//		int maxNum = 15000;
//		int testCount = 1000;
//		int testCount = 5000;
		CachePointingByKeyResponse response;
		for (int i = 0; i < testCount; i++)
		{
			cacheKey = "Cache" + Rand.getRandom(cacheMaxNum);
			dataKey = "Key" + Rand.getRandom(totalDataCount);
			response = FrontReader.RR().getCachePointing(cacheKey, dataKey);
			if (response.getPointing() != null)
			{
				System.out.println("Cache Key = " + response.getPointing().getCacheKey() + ", key = " + response.getPointing().getKey() + ", points = " + response.getPointing().getPoints());
			}
			else
			{
				System.out.println("Cache key: " + cacheKey + " does not contain the key, " + dataKey);
			}
		}
	}

	public static void isCacheExisted() throws ClassNotFoundException, RemoteReadException, IOException
	{
		int currentCacheMaxNum = cacheMaxNum + 20;
		String cacheKey;
		IsCacheExistedInPointingStoreResponse response;
		for (int i = 0; i < currentCacheMaxNum; i++)
		{
			cacheKey = "Cache" + i;
			response = FrontReader.RR().isPointingCacheExisted(cacheKey);
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
	
	public static void isCacheEmpty() throws ClassNotFoundException, RemoteReadException, IOException
	{
//		int cacheMaxNum = 20;
		String cacheKey;
		IsCacheEmptyInPointingStoreResponse response;
		for (int i = 0; i < cacheMaxNum; i++)
		{
			cacheKey = "Cache" + i;
			response = FrontReader.RR().isPointingCacheEmpty(cacheKey);
			if (response.isEmpty())
			{
				System.out.println("Cache key = " + cacheKey + " is EMPTY in the store");
			}
			else
			{
				System.out.println("Cache key = " + cacheKey + " is NOT EMPTY in the store");
			}
		}
	}

	public static void getCachePointingByIndex() throws ClassNotFoundException, RemoteReadException, IOException
	{
//		int cacheMaxNum = 20;
		String cacheKey;
		CachePointingByIndexResponse response;
//		int range = 2000;
		for (int i = 0; i < cacheMaxNum; i++)
		{
			cacheKey = "Cache" + Rand.getRandom(cacheMaxNum);
			for (int index = 0; index < range; index++)
			{
				response = FrontReader.RR().getCachePointing(cacheKey, index);
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
		TopCachePointingsResponse response;
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
			response = FrontReader.RR().getTopCachePointing(cacheKey, topIndex);
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
		RangeCachePointingsResponse response;
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
			response = FrontReader.RR().getRangeCachePointing(cacheKey, startIndex, endIndex);
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
