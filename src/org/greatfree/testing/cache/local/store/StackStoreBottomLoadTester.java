package org.greatfree.testing.cache.local.store;

import org.greatfree.cache.factory.StackKeyCreator;
import org.greatfree.cache.local.store.StackStore;

// Created: 08/08/2018, Bing Li
class StackStoreBottomLoadTester
{

	public static void main(String[] args)
	{
		String root = "/Users/libing/Temp/";
		
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
		for (int i = 0; i < 10; i++)
		{
			v = store.popBottom("stack1");
			if (v != null)
			{
				System.out.println("stack1's bottom = " + v.getKey() + ", " + v.getPoints());
			}
			else
			{
				System.out.println("No bottom!");
			}
		}

		store.close();
	}

}
