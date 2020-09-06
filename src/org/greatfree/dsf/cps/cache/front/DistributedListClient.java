package org.greatfree.dsf.cps.cache.front;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.greatfree.dsf.cps.cache.TestCacheConfig;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.testing.cache.local.MyUKValue;
import org.greatfree.util.Rand;
import org.greatfree.util.Time;

// Created: 02/27/2019, Bing Li
class DistributedListClient
{
//	private final static int MAX_DATA_COUNT = 15000;
	private final static int MAX_DATA_COUNT = 1500;
//	private final static int MAX_DATA_COUNT = 100;
	private final static int MAX_DATA_VALUE = 10000;
//	private final static int MAX_DATA_VALUE = 1000;
	private final static int batchCount = 100;

	public static void saveUKIntoDistributedList() throws IOException, InterruptedException
	{
		MyUKValue data;
		Date startTime;
		Date endTime;
		for (int i = 0; i < MAX_DATA_COUNT; i++)
		{
			data = new MyUKValue(Rand.getRandom(MAX_DATA_VALUE));
			startTime = Calendar.getInstance().getTime();
			FrontEventer.RE().saveUKIntoDistributedList(data);
			endTime = Calendar.getInstance().getTime();
			System.out.println("It takes " + Time.getTimespanInMilliSecond(endTime, startTime) + "ms to save the data ...");
		}
	}
	
	public static void saveMuchUKIntoDistributedList() throws IOException, InterruptedException
	{
		List<MyUKValue> muchData;
		MyUKValue data;
		Date startTime;
		Date endTime;
		int times = MAX_DATA_COUNT / batchCount;
		int index = 0;
		for (int i = 0; i < times; i++)
		{
			muchData = new ArrayList<MyUKValue>();
			for (int j = 0; j < batchCount; j++)
			{
				data = new MyUKValue(Rand.getRandom(MAX_DATA_VALUE));
				muchData.add(data);
			}
			startTime = Calendar.getInstance().getTime();
			FrontEventer.RE().saveUKsIntoDistributedList(muchData);
			endTime = Calendar.getInstance().getTime();
			System.out.println("It takes " + Time.getTimespanInMilliSecond(endTime, startTime) + "ms to save the " + muchData.size() + " pieces of data ...");
			index += muchData.size();
		}
		System.out.println("The total data count is " + index);
	}
	
	public static void loadDataFromDistributedList() throws ClassNotFoundException, RemoteReadException, IOException
	{
		MyUKValue data;
		Date startTime;
		Date endTime;
		for (int i = 0; i < MAX_DATA_COUNT; i++)
		{
			startTime = Calendar.getInstance().getTime();
			data = FrontReader.RR().loadUKFromDistributedList(i).getUK();
			endTime = Calendar.getInstance().getTime();
			System.out.println(data.getKey() + ", " + data.getPoints());
			System.out.println("It takes " + Time.getTimespanInMilliSecond(endTime, startTime) + "ms to load the data ...");
		}
	}

	public static void loadTopDataFromDistributedList() throws ClassNotFoundException, RemoteReadException, IOException
	{
		List<MyUKValue> uks;
		Date startTime;
		Date endTime;
		int maxIndex = TestCacheConfig.SORTED_DISTRIBUTED_LIST_CACHE_SIZE;
		int endIndex;
		for (int i = 0; i < 100; i++)
		{
			startTime = Calendar.getInstance().getTime();
			endIndex = Rand.getRandom(maxIndex);
			System.out.println("To retrieve top data at end index = " + endIndex);
			uks = FrontReader.RR().loadTopUKsFromDistributedList(endIndex).getUKs();
			endTime = Calendar.getInstance().getTime();
			if (uks != null)
			{
				for (MyUKValue entry : uks)
				{
					System.out.println(entry.getKey() + ", " + entry.getPoints());
				}
				System.out.println(i + ") It takes " + Time.getTimespanInMilliSecond(endTime, startTime) + "ms to load the data: " + uks.size() + " / endIndex = " + endIndex);
			}
			else
			{
				System.out.println("No top data is retrieved!");
				System.out.println("It takes " + Time.getTimespanInMilliSecond(endTime, startTime) + "ms to load NO data");
			}
		}
		
	}
	
	public static void loadRangeDataFromDistributedList() throws ClassNotFoundException, RemoteReadException, IOException
	{
		List<MyUKValue> uks;
		Date startTime;
		Date endTime;
		int startIndex;
		int endIndex;
		int range;
		int maxIndex = TestCacheConfig.SORTED_DISTRIBUTED_LIST_CACHE_SIZE;
		for (int i = 0; i < 100; i++)
		{
			startTime = Calendar.getInstance().getTime();
			endIndex = Rand.getRandom(maxIndex) + 1;
			range = Rand.getRandom(endIndex);
			startIndex = endIndex - range + 1;
			uks = FrontReader.RR().loadRangeUKsFromDistributedList(startIndex, endIndex).getUKs();
			endTime = Calendar.getInstance().getTime();
			if (uks != null)
			{
				for (MyUKValue entry : uks)
				{
					System.out.println(entry.getKey() + ", " + entry.getPoints());
				}
				System.out.println("It takes " + Time.getTimespanInMilliSecond(endTime, startTime) + "ms to load the data: " + uks.size() + " / endIndex = " + endIndex);
			}
			else
			{
				System.out.println("No top data is retrieved!");
				System.out.println("It takes " + Time.getTimespanInMilliSecond(endTime, startTime) + "ms to load NO data");
			}
		}
	}
}
