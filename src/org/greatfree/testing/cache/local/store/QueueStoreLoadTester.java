package org.greatfree.testing.cache.local.store;

import java.util.List;

import org.greatfree.cache.factory.StackKeyCreator;
import org.greatfree.cache.local.store.QueueStore;

// Created: 03/16/2019, Bing Li
class QueueStoreLoadTester
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
		
		List<MyStackObj> rscs1 = store.dequeue("queue1", 3);
		for (MyStackObj rsc : rscs1)
		{
			System.out.println(rsc.getKey() + ": " + rsc.getPoints());
		}
		
		System.out.println("=========================");
		
		List<MyStackObj> rscs2 = store.dequeue("queue2", 4);
		for (MyStackObj rsc : rscs2)
		{
			System.out.println(rsc.getKey() + ": " + rsc.getPoints());
		}
		
		System.out.println("=========================");
		
		List<MyStackObj> rscs3 = store.dequeue("queue3", 5);
		for (MyStackObj rsc : rscs3)
		{
			System.out.println(rsc.getKey() + ": " + rsc.getPoints());
		}

		store.close();
	}

}
