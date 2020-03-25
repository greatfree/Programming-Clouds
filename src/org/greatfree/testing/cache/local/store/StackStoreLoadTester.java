package org.greatfree.testing.cache.local.store;

import java.util.List;

import org.greatfree.cache.factory.StackKeyCreator;
import org.greatfree.cache.local.store.StackStore;

// Created: 08/08/2018, Bing Li
class StackStoreLoadTester
{

	public static void main(String[] args)
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
		
		List<MyStackObj> rscs1 = store.pop("stack1", 3);
		for (MyStackObj rsc : rscs1)
		{
			System.out.println(rsc.getKey() + ": " + rsc.getPoints());
		}
		
		System.out.println("=========================");
		
		List<MyStackObj> rscs2 = store.pop("stack2", 4);
		for (MyStackObj rsc : rscs2)
		{
			System.out.println(rsc.getKey() + ": " + rsc.getPoints());
		}
		
		System.out.println("=========================");
		
		List<MyStackObj> rscs3 = store.pop("stack3", 5);
		for (MyStackObj rsc : rscs3)
		{
			System.out.println(rsc.getKey() + ": " + rsc.getPoints());
		}

		store.close();
	}

}
