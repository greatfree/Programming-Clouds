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
import org.greatfree.framework.cps.cache.data.MyCacheTiming;
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

// Created: 08/18/2018, Bing Li
class TimingDistributedCacheStoreClient
{
//	private final static cacheMaxNum = 20;
	private final static int cacheMaxNum = 50;
//	private final static int cacheMaxNum = 1;
//	private final static int totalDataCount = 15000;
	private final static int totalDataCount = 50000;
//	private final static int totalDataCount = 100;
//	private final static int totalDataCount = 5;
//	private final static int maxValue = 2000;
	private final static int testCount = 50;
	private final static int range = 2000;

	public static void saveDataIntoTimingDistributedCacheStore() throws IOException, InterruptedException
	{
		MyCacheTiming data;
		Date startTime;
		Date endTime;
//		int cacheMaxNum = 20;
		for (int i = 0; i < totalDataCount; i++)
		{
			data = new MyCacheTiming("Cache" + Rand.getRandom(cacheMaxNum), "Key" + i, Time.getRandomTime());
			startTime = Calendar.getInstance().getTime();
			FrontEventer.RE().saveCacheTimingIntoTimingDistributedCacheStore(data.getCacheKey(), data);
			endTime = Calendar.getInstance().getTime();
			System.out.println("It takes " + Time.getTimespanInMilliSecond(endTime, startTime) + "ms to save the data ...");			
		}
	}

	public static void saveDataIntoTimingsDistributedCacheStore() throws IOException, InterruptedException
	{
		MyCacheTiming data;
		Date startTime;
		Date endTime;
//		int cacheMaxNum = 20;
		String cacheKey;
		Map<String, List<MyCacheTiming>> pointings = new HashMap<String, List<MyCacheTiming>>();
		for (int i = 0; i < totalDataCount; i++)
		{
			cacheKey = "Cache" + Rand.getRandom(cacheMaxNum);
			if (!pointings.containsKey(cacheKey))
			{
				pointings.put(cacheKey, new ArrayList<MyCacheTiming>());
			}
			data = new MyCacheTiming(cacheKey, "Key" + i, Time.getRandomTime());
			pointings.get(cacheKey).add(data);
		}
		for (Map.Entry<String, List<MyCacheTiming>> entry : pointings.entrySet())
		{
			startTime = Calendar.getInstance().getTime();
			FrontEventer.RE().saveCacheTimingsIntoTimingingDistributedCacheStore(entry.getKey(), entry.getValue());
			endTime = Calendar.getInstance().getTime();
			System.out.println("CacheKey = " + entry.getKey() + ": It takes " + Time.getTimespanInMilliSecond(endTime, startTime) + "ms to save the data ...");			
		}
	}

	public static void saveSortedDataIntoTimingsDistributedCacheStore() throws IOException, InterruptedException
	{
		MyCacheTiming data;
		Date startTime;
		Date endTime;
//		int cacheMaxNum = 20;
		String cacheKey;
		Map<String, List<MyCacheTiming>> pointings = new HashMap<String, List<MyCacheTiming>>();
		for (int i = 0; i < totalDataCount; i++)
		{
			cacheKey = "Cache" + Rand.getRandom(cacheMaxNum);
			if (!pointings.containsKey(cacheKey))
			{
				pointings.put(cacheKey, new ArrayList<MyCacheTiming>());
			}
			Thread.sleep(5);
			data = new MyCacheTiming(cacheKey, "Key" + i, Calendar.getInstance().getTime());
			System.out.println("Data = " + data.getKey() + ", " + data.getTime());
			pointings.get(cacheKey).add(data);
		}
		for (Map.Entry<String, List<MyCacheTiming>> entry : pointings.entrySet())
		{
			startTime = Calendar.getInstance().getTime();
			FrontEventer.RE().saveCacheTimingsIntoTimingingDistributedCacheStore(entry.getKey(), entry.getValue());
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
			response = FrontReader.RR().containsKeyOfCacheTiming(cacheKey, dataKey);
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
	
	public static void getMaxCacheTiming() throws ClassNotFoundException, RemoteReadException, IOException
	{
//		int cacheMaxNum = 20;
		String cacheKey;
//		int maxNum = 15000;
		MaxCachePointingResponse response;
		for (int i = 0; i < cacheMaxNum; i++)
		{
			cacheKey = "Cache" + Rand.getRandom(cacheMaxNum);
			response = FrontReader.RR().getMaxCacheTiming(cacheKey);
			System.out.println("The max value of " + cacheKey + " is " + Time.getTime((long)response.getTiming().getPoints()));
		}
	}
	
	public static void getCacheTimingByKey() throws ClassNotFoundException, RemoteReadException, IOException
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
			response = FrontReader.RR().getCacheTiming(cacheKey, dataKey);
			if (response.getTiming() != null)
			{
				System.out.println("Cache Key = " + response.getTiming().getCacheKey() + ", key = " + response.getTiming().getKey() + ", points = " + Time.getTime((long)response.getTiming().getPoints()));
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
			response = FrontReader.RR().isTimingCacheExisted(cacheKey);
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
			response = FrontReader.RR().isTimingCacheEmpty(cacheKey);
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

	public static void getCacheTimingByIndex() throws ClassNotFoundException, RemoteReadException, IOException
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
				response = FrontReader.RR().getCacheTiming(cacheKey, index);
				if (response.getTiming() != null)
				{
					System.out.println("index = " + index + ") Cache Key = " + response.getTiming().getCacheKey() + ", key = " + response.getTiming().getKey() + ", points = " + Time.getTime((long)response.getTiming().getPoints()));
				}
				else
				{
					System.out.println("index = " + index + ") Cache key: " + cacheKey + " does not contain the index, " + index);
				}
			}
		}
	}
	
	public static void getTopCacheTimings() throws ClassNotFoundException, RemoteReadException, IOException
	{
//		int cacheMaxNum = 20;
		String cacheKey;
		TopCachePointingsResponse response;
//		int range = 1000;
		int topIndex;
		List<MyCacheTiming> timings;
		int index = 0;
		for (int i = 0; i < cacheMaxNum; i++)
		{
			cacheKey = "Cache" + Rand.getRandom(cacheMaxNum);
//			topIndex = Rand.getRandom(totalDataCount / cacheMaxNum);
			topIndex = Rand.getRandom(TestCacheConfig.SORTED_DISTRIBUTED_CACHE_SIZE);
			System.out.println("top index = " + topIndex);
			response = FrontReader.RR().getTopCacheTiming(cacheKey, topIndex);
			if (response.getTimings() != null)
			{
				index = 0;
				timings = response.getTimings();
				System.out.println(index + ") CacheKey = " + cacheKey + ", top data count = " + timings.size());
				for (MyCacheTiming entry : timings)
				{
					System.out.println(index++ + ") CacheKey = " + entry.getCacheKey() + ", key = " + entry.getKey() + ", time = " + Time.getTime((long)entry.getPoints()));
				}
				System.out.println("The top index = " + topIndex);
			}
			else
			{
				System.out.println("There is no data for the cache " + cacheKey + " at the top index = " + topIndex);
			}
		}
	}
	
	public static void getRangeCacheTimings() throws ClassNotFoundException, RemoteReadException, IOException
	{
//		int cacheMaxNum = 20;
		String cacheKey;
		RangeCachePointingsResponse response;
//		int range = 1000;
		int startIndex;
		int endIndex;
		int rangeCount;
		List<MyCacheTiming> timings;
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
			response = FrontReader.RR().getRangeCacheTiming(cacheKey, startIndex, endIndex);
			if (response.getTimings() != null)
			{
				index = startIndex;
				timings = response.getTimings();
				for (MyCacheTiming entry : timings)
				{
					System.out.println(index++ + ") CacheKey = " + entry.getCacheKey() + ", key = " + entry.getKey() + ", time = " + Time.getTime((long)entry.getPoints()));
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
