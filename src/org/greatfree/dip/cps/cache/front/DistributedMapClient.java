package org.greatfree.dip.cps.cache.front;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.greatfree.dip.cps.cache.data.MyData;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.util.Rand;
import org.greatfree.util.Time;

import com.google.common.collect.Sets;

// Created: 08/04/2018, Bing Li
class DistributedMapClient
{
	private final static int MAX_DATA_COUNT = 15000;
//	private final static int MAX_DATA_COUNT = 1500;
//	private final static int MAX_DATA_COUNT = 100;
	private final static int MAX_DATA_VALUE = 2000;
	private final static int batchCount = 100;
//	private final static int batchCount = 10;

	public static void saveDataIntoDistributedMap() throws IOException, InterruptedException
	{
		MyData data;
		Date startTime;
		Date endTime;
		for (int i = 0; i < MAX_DATA_COUNT; i++)
		{
			data = new MyData("Key" + i, Rand.getRandom(MAX_DATA_VALUE), Calendar.getInstance().getTime());
			startTime = Calendar.getInstance().getTime();
			FrontEventer.RE().saveDataIntoDistributedMap(data);
			endTime = Calendar.getInstance().getTime();
			System.out.println("It takes " + Time.getTimespanInMilliSecond(endTime, startTime) + "ms to save the data ...");
		}
	}

	public static void saveMuchDataIntoDistributedMap() throws IOException, InterruptedException
	{
		Map<String, MyData> muchData;
		MyData data;
		Date startTime;
		Date endTime;
		int times = MAX_DATA_COUNT / batchCount;
		int index = 0;
		for (int i = 0; i < times; i++)
		{
			muchData = new HashMap<String, MyData>();
			for (int j = 0; j < batchCount; j++)
			{
				data = new MyData("Key" + index++, Rand.getRandom(MAX_DATA_VALUE), Calendar.getInstance().getTime());
				muchData.put(data.getKey(), data);
			}
			startTime = Calendar.getInstance().getTime();
			FrontEventer.RE().saveDataIntoDistributedMap(muchData);
			endTime = Calendar.getInstance().getTime();
			System.out.println("It takes " + Time.getTimespanInMilliSecond(endTime, startTime) + "ms to save the " + muchData.size() + " pieces of data ...");
		}
		System.out.println("The total data count is " + index);
	}

	public static void loadDataFromDistributedMap() throws ClassNotFoundException, RemoteReadException, IOException
	{
		MyData data;
		Date startTime;
		Date endTime;
		for (int i = 0; i < MAX_DATA_COUNT; i++)
		{
			startTime = Calendar.getInstance().getTime();
			data = FrontReader.RR().loadDataFromDistributedMap("Key" + i).getMyData();
			endTime = Calendar.getInstance().getTime();
			System.out.println(data.getKey() + ", " + data.getNumber() + ", " + data.getTime());
			System.out.println("It takes " + Time.getTimespanInMilliSecond(endTime, startTime) + "ms to load the data ...");
		}
	}

	public static void loadDataByKeysFromDistributedMap() throws ClassNotFoundException, RemoteReadException, IOException
	{
//		int maxIndex = 15000;
		Set<String> keys = Sets.newHashSet();
		Map<String, MyData> data;
		for (int i = 0; i < 100; i++)
		{
			keys.add("Key" + Rand.getRandom(MAX_DATA_COUNT));
		}
		Date startTime = Calendar.getInstance().getTime();
		data = FrontReader.RR().loadDataFromDistributedMap(keys).getData();
		System.out.println("DataAccessor-loadDataByKeysFromDistributedMap(): loaded data size = " + data.size());
		Date endTime = Calendar.getInstance().getTime();
		for (MyData entry : data.values())
		{
			System.out.println(entry.getKey() + ", " + entry.getNumber() + ", " + entry.getTime());
		}
		System.out.println("It takes " + Time.getTimespanInMilliSecond(endTime, startTime) + "ms to load the data ...");
	}
}
