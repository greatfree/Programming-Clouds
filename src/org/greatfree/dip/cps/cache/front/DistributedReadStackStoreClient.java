package org.greatfree.dip.cps.cache.front;

import java.io.IOException;

import org.greatfree.dip.cps.cache.TestCacheConfig;
import org.greatfree.dip.cps.cache.data.MyStoreData;
import org.greatfree.dip.cps.cache.message.front.PeekMyStoreDataResponse;
import org.greatfree.dip.cps.cache.message.front.PeekSingleMyStoreDataResponse;
import org.greatfree.dip.cps.cache.message.front.PopSingleMyStoreDataResponse;
import org.greatfree.dip.cps.cache.message.prefetch.PopMyStoreDataResponse;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.util.Rand;

// Created: 08/23/2018, Bing Li
class DistributedReadStackStoreClient
{
	private final static int cacheMaxNum = 50;
//	private final static int cacheMaxNum = 500;
//	private final static int totalDataCount = 15000;
//	private final static int totalDataCount = 1000;
//	private final static int maxValue = 2000;
	private final static int testCount = 500;
//	private final static int testCount = 15000;
//	private final static int testCount = 15001;
//	private final static int testCount = 1001;
	private final static int range = TestCacheConfig.DISTRIBUTED_STACK_CACHE_SIZE;
//	private final static long sleepTime = 100;
//	private final static long sleepTime = 0;

	public static void popSingle() throws ClassNotFoundException, RemoteReadException, IOException
	{
		String stackKey;
		PopSingleMyStoreDataResponse response;
		for (int i = 0; i < testCount; i++)
		{
			stackKey = "Stack" + Rand.getRandom(cacheMaxNum);
			response = FrontReader.RR().popSingleFromReadStack(stackKey);
			if (response != null)
			{
				if (response.getData() != null)
				{
					System.out.println(response.getData().getCacheKey() + ", " + response.getData().getKey() + ", " + response.getData().getValue() + ", " + response.getData().getTime());
				}
				else
				{
					System.out.println("Stack: " + stackKey + " is empty now!");
				}
			}
			else
			{
				System.out.println("Stack: " + stackKey + " does not exist!");
			}
		}
	}
	
	public static void popMuch() throws ClassNotFoundException, RemoteReadException, IOException
	{
		String stackKey;
		PopMyStoreDataResponse response;
		int count;
//		Scanner in = new Scanner(System.in);
		for (int i = 0; i < testCount; i++)
		{
			count = Rand.getRandom(range);
			stackKey = "Stack" + Rand.getRandom(cacheMaxNum);
			response = FrontReader.RR().popFromReadStack(stackKey, count);
			if (response != null)
			{
				if (response.getData() != null)
				{
					for (MyStoreData entry : response.getData())
					{
						System.out.println(entry.getCacheKey() + ", " + entry.getKey() + ", " + entry.getValue() + ", " + entry.getTime());
					}
					System.out.println("====================================");
					System.out.println("Stack: " + stackKey + ", the size to be loaded = " + count);
					System.out.println("Stack: " + stackKey + ", loaded size = " + response.getData().size());
//					in.nextLine();
				}
				else
				{
					System.out.println("Stack: " + stackKey + " is empty now!");
				}
			}
			else
			{
				System.out.println("Stack: " + stackKey + " does not exist!");
			}
		}
//		in.close();
	}
	
	public static void peekSingle() throws ClassNotFoundException, RemoteReadException, IOException
	{
		String stackKey;
		PeekSingleMyStoreDataResponse response;
		for (int i = 0; i < testCount; i++)
		{
			stackKey = "Stack" + Rand.getRandom(cacheMaxNum);
			response = FrontReader.RR().peekSingleFromReadStack(stackKey);
			if (response != null)
			{
				if (response.getData() != null)
				{
					System.out.println(response.getData().getCacheKey() + ", " + response.getData().getKey() + ", " + response.getData().getValue() + ", " + response.getData().getTime());
				}
				else
				{
					System.out.println("Stack: " + stackKey + " is empty now!");
				}
			}
			else
			{
				System.out.println("Stack: " + stackKey + " does not exist!");
			}
		}
	}
	
	public static void peekMuch() throws ClassNotFoundException, RemoteReadException, IOException
	{
		String stackKey;
		PeekMyStoreDataResponse response;
		int count;
		for (int i = 0; i < testCount; i++)
		{
			count = Rand.getRandom(range);
			stackKey = "Stack" + Rand.getRandom(cacheMaxNum);
			response = FrontReader.RR().peekFromReadStack(stackKey, count);
			if (response != null)
			{
				if (response.getData() != null)
				{
					for (MyStoreData entry : response.getData())
					{
						System.out.println(entry.getCacheKey() + ", " + entry.getKey() + ", " + entry.getValue() + ", " + entry.getTime());
					}
					System.out.println("====================================");
					System.out.println("Stack: " + stackKey + ", the size to be peeked = " + count);
					System.out.println("Stack: " + stackKey + ", peeked size = " + response.getData().size());
				}
				else
				{
					System.out.println("Stack: " + stackKey + " is empty now!");
				}
			}
			else
			{
				System.out.println("Stack: " + stackKey + " does not exist!");
			}
		}
	}
	
	public static void peekRange() throws ClassNotFoundException, RemoteReadException, IOException
	{
		String stackKey;
		PeekMyStoreDataResponse response;
		int count;
		int startIndex;
		int endIndex;
		for (int i = 0; i < testCount; i++)
		{
			count = Rand.getRandom(range) + 1;
			startIndex = Rand.getRandom(count);
			endIndex = startIndex + count - 1;
			stackKey = "Stack" + Rand.getRandom(cacheMaxNum);
			response = FrontReader.RR().peekFromReadStack(stackKey, startIndex, endIndex);
			if (response != null)
			{
				if (response.getData() != null)
				{
					for (MyStoreData entry : response.getData())
					{
						System.out.println(entry.getCacheKey() + ", " + entry.getKey() + ", " + entry.getValue() + ", " + entry.getTime());
					}
					System.out.println("====================================");
					System.out.println("Stack: " + stackKey + ", the size to be peeked = " + count);
					System.out.println("Stack: " + stackKey + ", peeked size = " + response.getData().size());
				}
				else
				{
					System.out.println("Stack: " + stackKey + " is empty now!");
				}
			}
			else
			{
				System.out.println("Stack: " + stackKey + " does not exist!");
			}
		}
	}
	
	public static void get() throws ClassNotFoundException, RemoteReadException, IOException
	{
		String stackKey;
		PeekMyStoreDataResponse response;
		int count;
		int index;
		for (int i = 0; i < testCount; i++)
		{
			count = Rand.getRandom(range) + 1;
			index = Rand.getRandom(count);
			stackKey = "Stack" + Rand.getRandom(cacheMaxNum);
			System.out.println("Stack: " + stackKey + ", index = " + index);
			response = FrontReader.RR().getFromReadStack(stackKey, index);
			if (response != null)
			{
				if (response.getValue() != null)
				{
					System.out.println(response.getValue().getCacheKey() + ", " + response.getValue().getKey() + ", " + response.getValue().getValue() + ", " + response.getValue().getTime());
					System.out.println("====================================");
				}
				else
				{
					System.out.println("Stack: " + stackKey + " is empty now!");
				}
			}
			else
			{
				System.out.println("Stack: " + stackKey + " does not exist!");
			}
		}
	}
}
