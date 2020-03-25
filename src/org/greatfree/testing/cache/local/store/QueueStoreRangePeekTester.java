package org.greatfree.testing.cache.local.store;

import java.util.List;

import org.greatfree.cache.factory.StackKeyCreator;
import org.greatfree.cache.local.store.QueueStore;
import org.greatfree.exceptions.IndexOutOfRangeException;

// Created: 03/17/2019, Bing Li
class QueueStoreRangePeekTester
{

	public static void main(String[] args)
	{
		String root = "/Users/libing/Temp/";
		
		QueueStore<MyStackObj, StackStoreFactory, StackKeyCreator> store = new QueueStore.QueueStoreBuilder<MyStackObj, StackStoreFactory, StackKeyCreator>()
				.storeKey("MyQueueStore")
				.factory(new StackStoreFactory())
				.cacheSize(1000)
				.keyCreator(new StackKeyCreator())
				.rootPath(root)
				.totalStoreSize(10000)
				.offheapSizeInMB(10)
				.diskSizeInMB(100)
				.build();
		
		List<MyStackObj> rscs1;
		try
		{
			rscs1 = store.peek("queue1", 1, 3);
			for (MyStackObj rsc : rscs1)
			{
				System.out.println(rsc.getKey() + ": " + rsc.getPoints());
			}
		}
		catch (IndexOutOfRangeException e)
		{
			e.printStackTrace();
		}
		
		System.out.println("=========================");

		try
		{
			rscs1 = store.peek("queue1", 8, 13);
			for (MyStackObj rsc : rscs1)
			{
				System.out.println(rsc.getKey() + ": " + rsc.getPoints());
			}
		}
		catch (IndexOutOfRangeException e)
		{
			System.out.println("Index Error!");
		}
		
		System.out.println("=========================");

		try
		{
			rscs1 = store.peek("queue1", 0, 0);
			for (MyStackObj rsc : rscs1)
			{
				System.out.println(rsc.getKey() + ": " + rsc.getPoints());
			}
		}
		catch (IndexOutOfRangeException e)
		{
			System.out.println("Index Error!");
		}
		
		System.out.println("=========================");

		try
		{
			rscs1 = store.peek("queue1", -1, 0);
			for (MyStackObj rsc : rscs1)
			{
				System.out.println(rsc.getKey() + ": " + rsc.getPoints());
			}
		}
		catch (IndexOutOfRangeException e)
		{
			System.out.println("Index Error!");
		}

		System.out.println("=========================");

		List<MyStackObj> rscs2;
		try
		{
			rscs2 = store.peek("queue2", 2, 4);
			for (MyStackObj rsc : rscs2)
			{
				System.out.println(rsc.getKey() + ": " + rsc.getPoints());
			}
		}
		catch (IndexOutOfRangeException e)
		{
			e.printStackTrace();
		}
		
		System.out.println("=========================");
		
		List<MyStackObj> rscs3;
		try
		{
			rscs3 = store.peek("queue3", 5, 8);
			for (MyStackObj rsc : rscs3)
			{
				System.out.println(rsc.getKey() + ": " + rsc.getPoints());
			}
		}
		catch (IndexOutOfRangeException e)
		{
			e.printStackTrace();
		}

		store.close();
	}

}
