package org.greatfree.dip.cps.cache.front;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.greatfree.dip.cps.cache.TestCacheConfig;
import org.greatfree.dip.cps.cache.data.MyPointing;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.util.Rand;
import org.greatfree.util.Time;

// Created: 08/04/2018, Bing Li
class PointingDistributedListClient
{
//	private final static int MAX_DATA_COUNT = 15000;
	private final static int MAX_DATA_COUNT = 150;
//	private final static int MAX_DATA_COUNT = 100000;
//	private final static int MAX_DATA_COUNT = 50000;
//	private final static int MAX_DATA_COUNT = 1500;
//	private final static int MAX_DATA_COUNT = 100;
	private final static int MAX_DATA_VALUE = 2000;
	private final static int batchCount = 100;
//	private final static int batchCount = 10;

	public static void saveDataIntoPointingDistributedList() throws IOException, InterruptedException
	{
		MyPointing data;
		Date startTime;
		Date endTime;
		int index = 0;
//		for (int i = 0; i < 100; i++)
//		for (int i = 0; i < 20000; i++)
//		for (int i = 0; i < 10000; i++)
		for (int i = 0; i < MAX_DATA_COUNT; i++)
		{
			data = new MyPointing("Key" + i, Rand.getRandom(MAX_DATA_VALUE), "Description" + i);
			startTime = Calendar.getInstance().getTime();
			FrontEventer.RE().saveMyPointingIntoPointingDistributedList(data);
			endTime = Calendar.getInstance().getTime();
			System.out.println(index++ + ") It takes " + Time.getTimespanInMilliSecond(endTime, startTime) + "ms to save the data ...");			
		}
	}

	public static void saveMuchDataIntoPointingDistributedList() throws IOException, InterruptedException
	{
		Date startTime;
		Date endTime;
		int times = MAX_DATA_COUNT / batchCount;
		List<MyPointing> pointings;
		int index = 0;
		for (int i = 0; i < times; i++)
		{
			pointings = new ArrayList<MyPointing>();
			for (int j = 0; j < batchCount; j++)
			{
				pointings.add(new MyPointing("Key" + index++, Rand.getRandom(MAX_DATA_VALUE), "Description" + i));
			}
			startTime = Calendar.getInstance().getTime();
			FrontEventer.RE().saveMyPointingsIntoPointingDistributedList(pointings);
			endTime = Calendar.getInstance().getTime();
			System.out.println("It takes " + Time.getTimespanInMilliSecond(endTime, startTime) + "ms to save the data ...");			
		}
	}

	public static void loadDataFromPointingDistributedList() throws ClassNotFoundException, RemoteReadException, IOException
	{
		MyPointing data;
		Date startTime;
		Date endTime;
//		for (int i = 0; i < 100; i++)
//		for (int i = 0; i < 20000; i++)
//		for (int i = 0; i < 10000; i++)
		for (int i = 0; i < MAX_DATA_COUNT; i++)
		{
			startTime = Calendar.getInstance().getTime();
			data = FrontReader.RR().loadMyPointingFromPointingDistributedList(i).getPointing();
			endTime = Calendar.getInstance().getTime();
			if (data != null)
			{
				System.out.println(i + ") " + data.getKey() + ", " + data.getPoints() + ", " + data.getDescription());
			}
			else
			{
				System.out.println(i + ") NULL");
			}
			System.out.println("It takes " + Time.getTimespanInMilliSecond(endTime, startTime) + "ms to load the data ...");
		}
	}

	public static void loadTopDataFromPointingDistributedList() throws ClassNotFoundException, RemoteReadException, IOException
	{
		List<MyPointing> pointings;
		Date startTime;
		Date endTime;
//		int maxIndex = 15000;
//		int maxIndex = 1000;
		int maxIndex = TestCacheConfig.SORTED_DISTRIBUTED_LIST_CACHE_SIZE;
		int endIndex;
//		for (int i = 0; i < 100; i++)
//		for (int i = 0; i < 20000; i++)
//		for (int i = 0; i < 10000; i++)
//		for (int i = 0; i < 15000; i++)
		for (int i = 0; i < 100; i++)
		{
			startTime = Calendar.getInstance().getTime();
			endIndex = Rand.getRandom(maxIndex);
			pointings = FrontReader.RR().loadTopMyPointingsFromPointingDistributedList(endIndex).getPointings();
			endTime = Calendar.getInstance().getTime();
			if (pointings != null)
			{
				for (MyPointing entry : pointings)
				{
					System.out.println(entry.getKey() + ", " + entry.getPoints() + ", " + entry.getDescription());
				}
				System.out.println(i + ") It takes " + Time.getTimespanInMilliSecond(endTime, startTime) + "ms to load the data: " + pointings.size() + " / endIndex = " + endIndex);
			}
			else
			{
				System.out.println("No top data is retrieved!");
				System.out.println("It takes " + Time.getTimespanInMilliSecond(endTime, startTime) + "ms to load NO data");
			}
		}
	}

	public static void loadRangeDataFromPointingDistributedList() throws ClassNotFoundException, RemoteReadException, IOException
	{
		List<MyPointing> pointings;
		Date startTime;
		Date endTime;
		int startIndex;
		int endIndex;
		int range;
//		int maxIndex = 100;
//		int maxIndex = 20000;
//		int maxIndex = 10000;
//		int maxIndex = 15000;
		int maxIndex = TestCacheConfig.SORTED_DISTRIBUTED_LIST_CACHE_SIZE;
		for (int i = 0; i < 100; i++)
		{
			startTime = Calendar.getInstance().getTime();
			endIndex = Rand.getRandom(maxIndex) + 1;
			range = Rand.getRandom(endIndex);
			startIndex = endIndex - range + 1;
			pointings = FrontReader.RR().loadRangeMyPointingsFromPointingDistributedList(startIndex, endIndex).getPointings();
			endTime = Calendar.getInstance().getTime();
			if (pointings != null)
			{
				for (MyPointing entry : pointings)
				{
					System.out.println(entry.getKey() + ", " + entry.getPoints() + ", " + entry.getDescription());
				}
				System.out.println("It takes " + Time.getTimespanInMilliSecond(endTime, startTime) + "ms to load the data: " + pointings.size() + " / endIndex = " + endIndex);
			}
			else
			{
				System.out.println("No top data is retrieved!");
				System.out.println("It takes " + Time.getTimespanInMilliSecond(endTime, startTime) + "ms to load NO data");
			}
		}
	}

	public static void loadDataFromPointingDistributedList(int index) throws ClassNotFoundException, RemoteReadException, IOException
	{
		MyPointing data = FrontReader.RR().loadMyPointingFromPointingDistributedList(index).getPointing();
		if (data != null)
		{
			System.out.println(data.getKey() + ", " + data.getPoints() + ", " + data.getDescription());
		}
		else
		{
			System.out.println("Data is Null ...");
		}
	}

	public static void loadTopDataFromPointingDistributedList(int endIndex) throws ClassNotFoundException, RemoteReadException, IOException
	{
		List<MyPointing> pointings;
		Date startTime;
		Date endTime;
		startTime = Calendar.getInstance().getTime();
		pointings = FrontReader.RR().loadTopMyPointingsFromPointingDistributedList(endIndex).getPointings();
		endTime = Calendar.getInstance().getTime();
		/*
		for (MyPointing entry : pointings)
		{
			System.out.println(entry.getKey() + ", " + entry.getPoints() + ", " + entry.getDescription());
		}
		*/
		if (pointings != null)
		{
			if (pointings.size() > 0)
			{
				MyPointing entry;
				for (int i = 0; i <= endIndex; i++)
				{
					entry = pointings.get(i);
					System.out.println(entry.getKey() + ", " + entry.getPoints() + ", " + entry.getDescription());
				}
				System.out.println("It takes " + Time.getTimespanInMilliSecond(endTime, startTime) + "ms to load the data: " + pointings.size() + " / endIndex = " + endIndex);
			}
			else
			{
				System.out.println("It takes " + Time.getTimespanInMilliSecond(endTime, startTime) + "ms to load the data: NO data loaded!");
			}
		}
		else
		{
			System.out.println("No top data is retrieved!");
			System.out.println("It takes " + Time.getTimespanInMilliSecond(endTime, startTime) + "ms to load NO data");
		}
	}

	public static void loadRangeDataFromPointingDistributedList(int startIndex, int endIndex) throws ClassNotFoundException, RemoteReadException, IOException
	{
		List<MyPointing> pointings;
		Date startTime;
		Date endTime;
		startTime = Calendar.getInstance().getTime();
		pointings = FrontReader.RR().loadRangeMyPointingsFromPointingDistributedList(startIndex, endIndex).getPointings();
		endTime = Calendar.getInstance().getTime();
		if (pointings != null)
		{
			MyPointing entry;
			for (int i = 0; i < pointings.size(); i++)
			{
				entry = pointings.get(i);
				System.out.println(entry.getKey() + ", " + entry.getPoints() + ", " + entry.getDescription());
			}
			System.out.println("It takes " + Time.getTimespanInMilliSecond(endTime, startTime) + "ms to load the data: " + pointings.size() + " / endIndex = " + endIndex);
		}
		else
		{
			System.out.println("No top data is retrieved!");
			System.out.println("It takes " + Time.getTimespanInMilliSecond(endTime, startTime) + "ms to load NO data");
		}
	}
}
