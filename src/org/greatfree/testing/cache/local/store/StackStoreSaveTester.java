package org.greatfree.testing.cache.local.store;

import org.greatfree.cache.factory.StackKeyCreator;
import org.greatfree.cache.local.store.StackStore;
import org.greatfree.util.Rand;

//Created: 08/08/2018, Bing Li
class StackStoreSaveTester
{

	public static void main(String[] args)
	{
//		String root = "/Users/libing/Temp/";
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

		store.push("stack1", new MyStackObj(0, Rand.getFRandom()));
		store.push("stack1", new MyStackObj(1, Rand.getFRandom()));
		store.push("stack1", new MyStackObj(2, Rand.getFRandom()));
		store.push("stack1", new MyStackObj(3, Rand.getFRandom()));
		store.push("stack1", new MyStackObj(4, Rand.getFRandom()));
		store.push("stack1", new MyStackObj(5, Rand.getFRandom()));
		
		store.push("stack2", new MyStackObj(0, Rand.getFRandom()));
		store.push("stack2", new MyStackObj(1, Rand.getFRandom()));
		store.push("stack2", new MyStackObj(2, Rand.getFRandom()));
		store.push("stack2", new MyStackObj(3, Rand.getFRandom()));
		store.push("stack2", new MyStackObj(4, Rand.getFRandom()));
		store.push("stack2", new MyStackObj(5, Rand.getFRandom()));
		store.push("stack2", new MyStackObj(6, Rand.getFRandom()));
		store.push("stack2", new MyStackObj(7, Rand.getFRandom()));
		store.push("stack2", new MyStackObj(8, Rand.getFRandom()));

		store.push("stack3", new MyStackObj(0, Rand.getFRandom()));
		store.push("stack3", new MyStackObj(1, Rand.getFRandom()));
		store.push("stack3", new MyStackObj(2, Rand.getFRandom()));
		store.push("stack3", new MyStackObj(3, Rand.getFRandom()));
		store.push("stack3", new MyStackObj(4, Rand.getFRandom()));
		store.push("stack3", new MyStackObj(5, Rand.getFRandom()));
		store.push("stack3", new MyStackObj(6, Rand.getFRandom()));
		store.push("stack3", new MyStackObj(7, Rand.getFRandom()));
		store.push("stack3", new MyStackObj(8, Rand.getFRandom()));
		store.push("stack3", new MyStackObj(9, Rand.getFRandom()));
		store.push("stack3", new MyStackObj(10, Rand.getFRandom()));
		store.push("stack3", new MyStackObj(11, Rand.getFRandom()));
		store.push("stack3", new MyStackObj(12, Rand.getFRandom()));
		store.push("stack3", new MyStackObj(13, Rand.getFRandom()));
		
		store.close();
		
		System.out.println("Done!");
	}

}
