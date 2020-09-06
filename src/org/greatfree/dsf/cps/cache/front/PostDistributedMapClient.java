package org.greatfree.dsf.cps.cache.front;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import org.greatfree.dsf.cps.cache.data.MyData;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.util.Rand;
import org.greatfree.util.Time;

import com.google.common.collect.Sets;

// Created: 08/04/2018, Bing Li
class PostDistributedMapClient
{
//	private final static int MAX_DATA_COUNT = 15000;
//	private final static int MAX_DATA_COUNT = 1500;
	private final static int MAX_DATA_COUNT = 100;

	public static void loadDataFromPostDistributedMap() throws ClassNotFoundException, RemoteReadException, IOException
	{
		MyData data;
		Date startTime;
		Date endTime;
		for (int i = 0; i < MAX_DATA_COUNT; i++)
		{
			startTime = Calendar.getInstance().getTime();
			data = FrontReader.RR().loadDataFromPostDistributedMap("Key" + i).getMyData();
			endTime = Calendar.getInstance().getTime();
			if (data != null)
			{
				System.out.println(data.getKey() + ", " + data.getNumber() + ", " + data.getTime());
			}
			else
			{
				System.out.println("No such data!");
			}
			System.out.println("It takes " + Time.getTimespanInMilliSecond(endTime, startTime) + "ms to load the data ...");
		}
	}

	public static void loadDataByKeysFromPostDistributedMap() throws ClassNotFoundException, RemoteReadException, IOException
	{
//		int maxIndex = 15000;
		Set<String> keys = Sets.newHashSet();
		Map<String, MyData> data;
		for (int i = 0; i < 100; i++)
		{
			keys.add("Key" + Rand.getRandom(MAX_DATA_COUNT));
		}
		Date startTime = Calendar.getInstance().getTime();
		data = FrontReader.RR().loadDataFromPostDistributedMap(keys).getData();
		System.out.println("DataAccessor-loadDataByKeysFromPostDistributedMap(): loaded data size = " + data.size());
		Date endTime = Calendar.getInstance().getTime();
		for (MyData entry : data.values())
		{
			System.out.println(entry.getKey() + ", " + entry.getNumber() + ", " + entry.getTime());
		}
		System.out.println("It takes " + Time.getTimespanInMilliSecond(endTime, startTime) + "ms to load the data ...");
	}

}
