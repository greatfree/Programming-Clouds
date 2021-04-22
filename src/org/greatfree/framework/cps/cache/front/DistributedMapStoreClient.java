package org.greatfree.framework.cps.cache.front;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.framework.cps.cache.data.MyStoreData;
import org.greatfree.framework.cps.cache.message.front.LoadMapStoreDataKeysResponse;
import org.greatfree.framework.cps.cache.message.front.LoadMyDataByKeysResponse;
import org.greatfree.framework.cps.cache.message.front.LoadMyDataResponse;
import org.greatfree.util.Rand;
import org.greatfree.util.Time;

import com.google.common.collect.Sets;

// Created: 08/25/2018, Bing Li
class DistributedMapStoreClient
{
	private final static int MAX_DATA_COUNT = 15000;
//	private final static int MAX_DATA_COUNT = 1500;
//	private final static int MAX_DATA_COUNT = 100;
	private final static int MAX_DATA_VALUE = 2000;
	private final static int BATCH_COUNT = 100;
//	private final static int batchCount = 10;
	private final static int CACHE_MAX_NUM = 50;
	private final static String CACHE = "Cache";
	private final static String KEY = "Key";
	private final static int CACHE_DATA_NUM = MAX_DATA_COUNT / CACHE_MAX_NUM;

	public static void saveDataIntoDistributedMapStore() throws IOException, InterruptedException
	{
		MyStoreData data;
		Date startTime;
		Date endTime;
		String cacheKey;
		for (int k = 0; k < CACHE_MAX_NUM; k++)
		{
			cacheKey = CACHE + k;
			for (int i = 0; i < CACHE_DATA_NUM; i++)
			{
				data = new MyStoreData(cacheKey, KEY + i, Rand.getRandom(MAX_DATA_VALUE), Calendar.getInstance().getTime());
				startTime = Calendar.getInstance().getTime();
				FrontEventer.RE().saveDataIntoDistributedMapStore(data.getCacheKey(), data);
				endTime = Calendar.getInstance().getTime();
				System.out.println("It takes " + Time.getTimespanInMilliSecond(endTime, startTime) + "ms to save the data ...");
			}
		}
	}

	public static void saveMuchDataIntoDistributedMapStore() throws IOException, InterruptedException
	{
		MyStoreData data;
		Date startTime;
		Date endTime;
		String cacheKey;
		Map<String, Map<String, MyStoreData>> muchData = new HashMap<String, Map<String, MyStoreData>>();
		for (int k = 0; k < CACHE_MAX_NUM; k++)
		{
			cacheKey = CACHE + k;
			for (int i = 0; i < CACHE_DATA_NUM; i++)
			{
				if (!muchData.containsKey(cacheKey))
				{
					muchData.put(cacheKey, new HashMap<String, MyStoreData>());
				}
				data = new MyStoreData(cacheKey, KEY + i, Rand.getRandom(MAX_DATA_VALUE), Calendar.getInstance().getTime());
				muchData.get(cacheKey).put(data.getKey(), data);
			}
		}
		
		for (Map.Entry<String, Map<String, MyStoreData>> entry : muchData.entrySet())
		{
			startTime = Calendar.getInstance().getTime();
			FrontEventer.RE().saveDataIntoDistributedMapStore(entry.getKey(), entry.getValue());
			endTime = Calendar.getInstance().getTime();
			System.out.println("It takes " + Time.getTimespanInMilliSecond(endTime, startTime) + "ms to save the " + entry.getValue().size() + " pieces of data ...");
		}
	}

	public static void loadDataFromDistributedMapStore() throws ClassNotFoundException, RemoteReadException, IOException
	{
		String cacheKey;
		String dataKey;
		Date startTime;
		Date endTime;
		LoadMyDataResponse response;
		for (int k = 0; k < CACHE_MAX_NUM; k++)
		{
			cacheKey = CACHE + k;
			for (int i = 0; i < BATCH_COUNT; i++)
			{
				dataKey = KEY + Rand.getRandom(CACHE_DATA_NUM);
				startTime = Calendar.getInstance().getTime();
				response = FrontReader.RR().loadDataFromDistributedMapStore(cacheKey, dataKey);
				if (response.getMyStoreData() != null)
				{
					System.out.println("Cache Key = " + response.getMyStoreData().getCacheKey() + ", key = " + response.getMyStoreData().getKey() + ", points = " + response.getMyStoreData().getValue());
				}
				else
				{
					System.out.println("Cache key: " + cacheKey + " does not contain the key, " + dataKey);
				}
				endTime = Calendar.getInstance().getTime();
				System.out.println("It takes " + Time.getTimespanInMilliSecond(endTime, startTime) + "ms to load the data ...");
			}
		}
	}

	public static void loadDataByKeysFromDistributedMapStore() throws ClassNotFoundException, RemoteReadException, IOException
	{
		Set<String> keys = Sets.newHashSet();
		String cacheKey;
		Date startTime;
		Date endTime;
		LoadMyDataByKeysResponse response;
		for (int k = 0; k < CACHE_MAX_NUM; k++)
		{
			keys.clear();
			cacheKey = CACHE + Rand.getRandom(CACHE_MAX_NUM);
			for (int i = 0; i < BATCH_COUNT; i++)
			{
				keys.add(KEY + Rand.getRandom(CACHE_DATA_NUM));
			}
			startTime = Calendar.getInstance().getTime();
			response = FrontReader.RR().loadDataFromDistributedMapStore(cacheKey, keys);
			endTime = Calendar.getInstance().getTime();
			if (response.getStoreData() != null)
			{
				System.out.println("Loaded data size = " + response.getStoreData().size());
				for (MyStoreData entry : response.getStoreData().values())
				{
					if (entry.getCacheKey() == null)
					{
						System.out.println("DistributedMapStoreClient: cacheKey is NULL");
					}
					if (entry.getKey() == null)
					{
						System.out.println("DistributedMapStoreClient: key is NULL");
					}
					System.out.println("Cache Key = " + entry.getCacheKey() + ", key = " + entry.getKey() + ", points = " + entry.getValue());
				}
				System.out.println("Loaded data size = " + response.getStoreData().size());
			}
			System.out.println("It takes " + Time.getTimespanInMilliSecond(endTime, startTime) + "ms to load the data ...");
		}
	}
	
	public static void loadDataKeysFromDistributedMapStore() throws ClassNotFoundException, RemoteReadException, IOException
	{
		String cacheKey;
		LoadMapStoreDataKeysResponse response;
		for (int k = 0; k < CACHE_MAX_NUM; k++)
		{
			cacheKey = CACHE + Rand.getRandom(CACHE_MAX_NUM);
			response = FrontReader.RR().getDataKeys(cacheKey);
			for (String entry : response.getDataKeys())
			{
				System.out.println("Cache: " + cacheKey + ": " + entry);
			}
		}
	}
}
