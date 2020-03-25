package org.greatfree.testing.cache.local;

import java.io.IOException;
import java.util.List;

import org.greatfree.cache.factory.StoreKeyCreator;
import org.greatfree.cache.local.store.SortedListStore;
import org.greatfree.util.Rand;

// Created: 02/15/2019, Bing Li
class SortedListStoreTester
{

	public static void main(String[] args) throws IOException
	{
		SortedListStore<MyPointing, MyPointingFactory, StoreKeyCreator, MyPointingComp> store = new SortedListStore.SortedListStoreBuilder<MyPointing, MyPointingFactory, StoreKeyCreator, MyPointingComp>()
				.storeKey("mystore")
				.factory(new MyPointingFactory())
				.cacheSize(1000)
				.keyCreator(new StoreKeyCreator())
				.rootPath("/Users/libing/Temp/")
				.totalStoreSize(10000)
				.offheapSizeInMB(4)
				.diskSizeInMB(10)
				.comp(new MyPointingComp())
				.sortSize(10)
				.build();

		MyPointing p;
		for (int i = 0; i < 15; i++)
		{
			p = new MyPointing(Rand.getRandom(100));
			store.add("list1", p);
			System.out.println("\n");
		}
		
		System.out.println("------------------------------");

		for (int i = 0; i < 10; i++)
		{
			p = store.get("list1", i);
			System.out.println(p.getKey() + ", " + p.getPoints());
		}
	
		System.out.println("------------------------------");

		List<MyPointing> points = store.getTop("list1", 3);
		for (int i = 0; i < 3; i++)
		{
			System.out.println(points.get(i).getKey() + ", " + points.get(i).getPoints());
		}
		
		System.out.println("------------------------------");

		List<String> obsKeys = store.getObsKey("list1");
		for (String entry : obsKeys)
		{
			System.out.println(entry + ", " + entry + ": " + store.get(entry).getPoints());
		}
		
		store.close();
	}

}
