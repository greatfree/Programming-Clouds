package org.greatfree.testing.cache.local.store;

import org.greatfree.cache.factory.StackKeyCreator;
import org.greatfree.cache.local.store.StackStore;
import org.greatfree.exceptions.IndexOutOfRangeException;

// Created: 03/21/2019, Bing Li
class StackStoreGetTester
{

	public static void main(String[] args)
	{
		String root = "/Temp/";
		
		StackStore<MyStackObj, StackStoreFactory, StackKeyCreator> store = new StackStore.StackStoreBuilder<MyStackObj, StackStoreFactory, StackKeyCreator>()
				.storeKey("MyStackStore")
				.factory(new StackStoreFactory())
				.cacheSize(1000)
				.keyCreator(new StackKeyCreator())
				.rootPath(root)
				.totalStoreSize(10000)
				.offheapSizeInMB(10)
				.diskSizeInMB(100)
				.build();

		MyStackObj v;
		for (int i = 0; i <= 4; i++)
		{
			try
			{
				v = store.get("stack1", i);
				System.out.println(v.getKey() + ") " + v.getPoints());
			}
			catch (IndexOutOfRangeException e)
			{
//				e.printStackTrace();
				System.out.println("No such data!");
			}
			
		}

		System.out.println("-------------------------------------");
		
		for (int i = 0; i <= 10; i++)
		{
			try
			{
				v = store.get("stack2", i);
				System.out.println(v.getKey() + ") " + v.getPoints());
			}
			catch (IndexOutOfRangeException e)
			{
				System.out.println("No such data!");
			}
		}


		System.out.println("-------------------------------------");
		
		for (int i = 0; i <= 15; i++)
		{
			try
			{
				v = store.get("stack3", i);
				System.out.println(v.getKey() + ") " + v.getPoints());
			}
			catch (IndexOutOfRangeException e)
			{
				System.out.println("No such data!");
			}
		}

		store.close();

	}

}
