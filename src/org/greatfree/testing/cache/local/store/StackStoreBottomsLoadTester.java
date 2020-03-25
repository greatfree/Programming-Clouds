package org.greatfree.testing.cache.local.store;

import java.util.List;

import org.greatfree.cache.factory.StackKeyCreator;
import org.greatfree.cache.local.store.StackStore;

// Created: 08/08/2018, Bing Li
class StackStoreBottomsLoadTester
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
		
		List<MyStackObj> values = store.popBottom("stack1", 4);
//		List<MyStackObj> values = store.popBottom("stack1", 10);
		
		for (MyStackObj entry : values)
		{
			System.out.println("stack1's bottom = " + entry.getKey() + ", " + entry.getPoints());
		}

		store.close();
	}

}
