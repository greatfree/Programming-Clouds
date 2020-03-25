package org.greatfree.testing.cache.local.store;

import java.util.ArrayList;
import java.util.List;

import org.greatfree.cache.factory.StackKeyCreator;
import org.greatfree.cache.local.store.StackStore;
import org.greatfree.util.Rand;

// Created: 08/09/2018, Bing Li
class StackStorePushAllBottomTester
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
		
		List<MyStackObj> values = new ArrayList<MyStackObj>();
		values.add(new MyStackObj(6, Rand.getFRandom()));
		values.add(new MyStackObj(7, Rand.getFRandom()));
		values.add(new MyStackObj(8, Rand.getFRandom()));
		values.add(new MyStackObj(9, Rand.getFRandom()));

		store.pushAllBottom("stack1", values);
		
		store.close();
		System.out.println("Done!");
	}

}
