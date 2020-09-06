package org.greatfree.dsf.cps.cache.front;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.greatfree.dsf.cps.cache.data.MyPointing;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.util.Rand;
import org.greatfree.util.Time;

// Created: 08/04/2018, Bing Li
class DistributedPointingMapClient
{
	private final static int MAX_DATA_COUNT = 15000;
//	private final static int MAX_DATA_COUNT = 500;
//	private final static int MAX_DATA_COUNT = 1500;
//	private final static int MAX_DATA_COUNT = 100;
//	private final static int MAX_DATA_COUNT = 50;
//	private final static int MAX_DATA_COUNT = 10;
	private final static int MAX_DATA_VALUE = 2000;
	private final static int batchCount = 100;
//	private final static int batchCount = 10;

	public static void savePointingIntoDistributedPointingMap() throws IOException, InterruptedException
	{
		MyPointing data;
		Date startTime;
		Date endTime;
		for (int i = 0; i < MAX_DATA_COUNT; i++)
		{
			data = new MyPointing("Key" + i, Rand.getRandom(2000), "Description" + i);
			startTime = Calendar.getInstance().getTime();
			FrontEventer.RE().saveMyPointingIntoPointingDistributedMap(data);
			endTime = Calendar.getInstance().getTime();
			System.out.println("It takes " + Time.getTimespanInMilliSecond(endTime, startTime) + "ms to save the data ...");			
		}
	}

	public static void savePointingsIntoDistributedPointingMap() throws IOException, InterruptedException
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
			FrontEventer.RE().saveMyPointingsIntoPointingDistributedMap(pointings);
			endTime = Calendar.getInstance().getTime();
			System.out.println("It takes " + Time.getTimespanInMilliSecond(endTime, startTime) + "ms to save the data ...");			
		}
	}

	public static void loadMinPointingFromDistributedPointingMap() throws ClassNotFoundException, RemoteReadException, IOException
	{
		Date startTime;
		Date endTime;
		startTime = Calendar.getInstance().getTime();
		MyPointing pointing = FrontReader.RR().loadMinMyPointingFromPointingDistributedMap().getPointing();
		endTime = Calendar.getInstance().getTime();
		System.out.println(pointing.getKey() + ", " + pointing.getPoints() + ", " + pointing.getDescription());
		System.out.println("It takes " + Time.getTimespanInMilliSecond(endTime, startTime) + "ms to save the data ...");			
	}

	public static void loadMaxPointingFromDistributedPointingMap() throws ClassNotFoundException, RemoteReadException, IOException
	{
		Date startTime;
		Date endTime;
		startTime = Calendar.getInstance().getTime();
		MyPointing pointing = FrontReader.RR().loadMaxMyPointingFromPointingDistributedMap().getPointing();
		endTime = Calendar.getInstance().getTime();
		System.out.println(pointing.getKey() + ", " + pointing.getPoints() + ", " + pointing.getDescription());
		System.out.println("It takes " + Time.getTimespanInMilliSecond(endTime, startTime) + "ms to save the data ...");			
	}

	public static void loadPointingFromDistributedPointingMap() throws ClassNotFoundException, RemoteReadException, IOException
	{
		Date startTime;
		Date endTime;
		MyPointing pointing;
//		for (int i = 0; i < 15000; i++)
		for (int i = 0; i < 1000; i++)
		{
			startTime = Calendar.getInstance().getTime();
			pointing = FrontReader.RR().loadMyPointingFromPointingDistributedMap("Key" + i).getPointing();
			endTime = Calendar.getInstance().getTime();
			System.out.println(pointing.getKey() + ", " + pointing.getPoints() + ", " + pointing.getDescription());
			System.out.println("It takes " + Time.getTimespanInMilliSecond(endTime, startTime) + "ms to save the data ...");			
		}
	}

}
