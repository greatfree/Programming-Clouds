package org.greatfree.framework.cps.cache.front;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.framework.cps.cache.TestCacheConfig;
import org.greatfree.framework.cps.cache.data.MyStoreData;
import org.greatfree.framework.cps.cache.message.front.PeekMyStoreDataResponse;
import org.greatfree.framework.cps.cache.message.front.PeekSingleMyStoreDataResponse;
import org.greatfree.framework.cps.cache.message.front.PopSingleMyStoreDataResponse;
import org.greatfree.framework.cps.cache.message.prefetch.PopMyStoreDataResponse;
import org.greatfree.util.Rand;

// Created: 08/09/2018, Bing Li
class DistributedStackStoreClient
{
	private final static int cacheMaxNum = 50;
//	private final static int cacheMaxNum = 500;
	private final static int totalDataCount = 15000;
//	private final static int totalDataCount = 1000;
	private final static int maxValue = 2000;
	private final static int testCount = 500;
//	private final static int testCount = 15000;
//	private final static int testCount = 15001;
//	private final static int testCount = 1001;
	private final static int range = TestCacheConfig.DISTRIBUTED_STACK_CACHE_SIZE;
//	private final static long sleepTime = 100;
	private final static long sleepTime = 0;

	/*
	 * The lock is added and no problems occur for that. Although evicting affecting the order, it does not affect too much. The stack in my system is used to keep pages temporarily. After being processed, pages are saved in sorted distributed caches. For a large-scale system like what I am doing, that is not a problem. 
	 * 
	 */
	
	/*
	 * According to the test cases, i.e., pushDataIntoOneStackStore(), popSingleFromOneStack() and popMuchFromOneStack(), the combination of DistributedStackStore and TerminalStackStore, is fine although the order is affected by evicted data. The affection is obvious when the concurrency degree is high because no locking is placed on the stack. I need to add the lock and test again. Another solution is to make a new combination of DistributedStackStore and a terminal list. Then, all of the data of DistributedStackStore is replicated to the terminal list. And, the DistributedStackStore prefetches and postfetches data from the terminal list with index. But the update is a little bit complicated. 08/10/2018, Bing Li 
	 */
	public static void pushDataIntoOneStackStore() throws IOException, InterruptedException
	{
		MyStoreData data;
		String stackKey = "Stack0";
		for (int i = 0; i < totalDataCount; i++)
		{
			data = new MyStoreData(stackKey, "Key" + i, Rand.getRandom(maxValue), Calendar.getInstance().getTime());
			FrontEventer.RE().push(data);
			Thread.sleep(sleepTime);
			System.out.println(data.getKey() + ", " + data.getCacheKey() + ", " + data.getValue() + " is pushed ......");
		}
	}

	public static void popSingleFromOneStack() throws ClassNotFoundException, RemoteReadException, IOException
	{
		PopSingleMyStoreDataResponse response;
		String stackKey = "Stack0";
		int index = 0;
		for (int i = 0; i < testCount; i++)
		{
			response = FrontReader.RR().popSingleFromStack(stackKey);
			if (response != null)
			{
				if (response.getData() != null)
				{
					System.out.println(index++ + ") " + response.getData().getCacheKey() + ", " + response.getData().getKey() + ", " + response.getData().getValue() + ", " + response.getData().getTime());
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

	public static void popMuchFromOneStack() throws ClassNotFoundException, RemoteReadException, IOException
	{
		String stackKey = "Stack0";
		PopMyStoreDataResponse response;
		int count;
		int index;
		int totalCount = 0;
//		Scanner in = new Scanner(System.in);
		for (int i = 0; i < testCount; i++)
		{
			count = Rand.getRandom(range);
			response = FrontReader.RR().popFromStack(stackKey, count);
			if (response != null)
			{
				if (response.getData() != null)
				{
					index = 0;
					for (MyStoreData entry : response.getData())
					{
						totalCount++;
						System.out.println(index++ + ") " + entry.getCacheKey() + ", " + entry.getKey() + ", " + entry.getValue() + ", " + entry.getTime());
					}
					System.out.println("==========================");
					System.out.println("The currently popped data size = " + index);
					System.out.println("The total popped data size = " + totalCount);
					if (totalCount >= totalDataCount)
					{
						break;
					}
					System.out.println("Enter to continue ...");
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

	public static void pushDataIntoStackStore() throws IOException, InterruptedException
	{
		MyStoreData data;
		for (int i = 0; i < totalDataCount; i++)
		{
			data = new MyStoreData("Stack" + Rand.getRandom(cacheMaxNum), "Key" + i, Rand.getRandom(maxValue), Calendar.getInstance().getTime());
			FrontEventer.RE().push(data);
			System.out.println(data.getKey() + ", " + data.getCacheKey() + ", " + data.getValue() + " is pushed ......");
		}
	}

	public static void pushMuchDataIntoStackStore() throws IOException, InterruptedException
	{
		MyStoreData data;
		String stackKey;
		Map<String, List<MyStoreData>> muchData = new HashMap<String, List<MyStoreData>>();
//		int count;
		for (int i = 0; i < totalDataCount; i++)
		{
			stackKey = "Stack" + Rand.getRandom(cacheMaxNum);
			if (!muchData.containsKey(stackKey))
			{
				muchData.put(stackKey, new ArrayList<MyStoreData>());
			}
			data = new MyStoreData(stackKey, "Key" + i, Rand.getRandom(maxValue), Calendar.getInstance().getTime());
			muchData.get(stackKey).add(data);
		}
		for (Map.Entry<String, List<MyStoreData>> entry : muchData.entrySet())
		{		
			FrontEventer.RE().push(entry.getKey(), entry.getValue());
		}
	}
	
	public static void popSingle() throws ClassNotFoundException, RemoteReadException, IOException
	{
		String stackKey;
		PopSingleMyStoreDataResponse response;
		for (int i = 0; i < testCount; i++)
		{
			stackKey = "Stack" + Rand.getRandom(cacheMaxNum);
			response = FrontReader.RR().popSingleFromStack(stackKey);
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
			response = FrontReader.RR().popFromStack(stackKey, count);
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
			response = FrontReader.RR().peekSingleFromStack(stackKey);
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
			response = FrontReader.RR().peekFromStack(stackKey, count);
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
			System.out.println("\nStack: " + stackKey + ", the size to be peeked = " + count);
			System.out.println("Stack: " + stackKey + ", startIndex = " + startIndex + ", endIndex = " + endIndex);
			response = FrontReader.RR().peekFromStack(stackKey, startIndex, endIndex);
			if (response != null)
			{
				if (response.getData() != null)
				{
					for (MyStoreData entry : response.getData())
					{
						System.out.println(entry.getCacheKey() + ", " + entry.getKey() + ", " + entry.getValue() + ", " + entry.getTime());
					}
					System.out.println("====================================");
					System.out.println("Stack: " + stackKey + ", peeked size = " + response.getData().size());
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
			response = FrontReader.RR().getFromStack(stackKey, index);
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
