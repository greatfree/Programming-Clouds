package org.greatfree.testing.cache.local.store;

import java.util.List;

import org.greatfree.cache.factory.StackKeyCreator;
import org.greatfree.cache.local.store.StackStore;
import org.greatfree.exceptions.IndexOutOfRangeException;

// Created: 03/16/2019, Bing Li
class StackStoreRangePeekTester
{

	public static void main(String[] args) throws IndexOutOfRangeException
	{
		String root = "/Users/libing/Temp/";
		
		StackStore<MyStackObj, StackStoreFactory, StackKeyCreator> store = new StackStore.StackStoreBuilder<MyStackObj, StackStoreFactory, StackKeyCreator>()
				.storeKey("MyStackStore")
				.factory(new StackStoreFactory())
				.cacheSize(1000)
				.keyCreator(new StackKeyCreator())
//				.rootPath("/Temp/MyStackStore")
				.rootPath(root)
				.totalStoreSize(10000)
				.offheapSizeInMB(10)
				.diskSizeInMB(100)
				.build();
		
//		List<MyStackObj> rscs1 = store.peek("stack1", 0, 4);
		List<MyStackObj> rscs1 = store.peek("stack1", 0, 0);
		for (MyStackObj rsc : rscs1)
		{
			System.out.println(rsc.getKey() + ": " + rsc.getPoints());
		}

		System.out.println("=========================");

		rscs1 = store.peek("stack1", 0, 10);
		for (MyStackObj rsc : rscs1)
		{
			System.out.println(rsc.getKey() + ": " + rsc.getPoints());
		}

		System.out.println("=========================");

		try
		{
			rscs1 = store.peek("stack1", 10, 10);
			if (rscs1 != null)
			{
				for (MyStackObj rsc : rscs1)
				{
					System.out.println(rsc.getKey() + ": " + rsc.getPoints());
				}
			}
			else
			{
				System.out.println("rscs1 is NULL");
			}
		}
		catch (IndexOutOfRangeException e)
		{
			System.out.println("Index Error!");
		}

		System.out.println("=========================");

		rscs1 = store.peek("stack1", 2, 10);
		for (MyStackObj rsc : rscs1)
		{
			System.out.println(rsc.getKey() + ": " + rsc.getPoints());
		}

		System.out.println("=========================");

		rscs1 = store.peek("stack1", -1, 10);
		for (MyStackObj rsc : rscs1)
		{
			System.out.println(rsc.getKey() + ": " + rsc.getPoints());
		}

		System.out.println("=========================");
		
		List<MyStackObj> rscs2 = store.peek("stack2", 2, 4);
		for (MyStackObj rsc : rscs2)
		{
			System.out.println(rsc.getKey() + ": " + rsc.getPoints());
		}
		
		System.out.println("=========================");
		
		List<MyStackObj> rscs3 = store.peek("stack3", 1, 5);
		for (MyStackObj rsc : rscs3)
		{
			System.out.println(rsc.getKey() + ": " + rsc.getPoints());
		}

		store.close();
	}

}
