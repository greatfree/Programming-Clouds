package org.greatfree.testing.cache.local.store;

import java.io.IOException;
import java.util.List;

import org.greatfree.cache.factory.StoreKeyCreator;
import org.greatfree.cache.local.store.ListStore;
import org.greatfree.testing.cache.local.MyUKFactory;
import org.greatfree.testing.cache.local.MyUKValue;
import org.greatfree.util.Rand;

// Created: 02/21/2019, Bing Li
class CacheListStoreTester
{

	public static void main(String[] args) throws InterruptedException, IOException
	{
		ListStore<MyUKValue, MyUKFactory, StoreKeyCreator> store = new ListStore.CacheListStoreBuilder<MyUKValue, MyUKFactory, StoreKeyCreator>()
//		CacheListStore<MyUKValue, MyUKFactory> store = new CacheListStore.CacheListStoreBuilder<MyUKValue, MyUKFactory>()
				.storeKey("mystore")
				.factory(new MyUKFactory())
				.cacheSize(1000)
				.keyCreator(new StoreKeyCreator())
				.rootPath("/Users/libing/GreatFreeLabs/Temp/")
//				.rootPath("/Temp/")
				.totalStoreSize(10000)
				.offheapSizeInMB(4)
				.diskSizeInMB(10)
				.build();
		
		MyUKValue p;
		for (int i = 0; i < 15; i++)
		{
			p = new MyUKValue(Rand.getRandom(100));
			store.add("list1", p);
		}

		for (int i = 0; i < 15; i++)
		{
			p = new MyUKValue(Rand.getRandom(100));
			store.add("list2", p);
		}

		System.out.println("------------------------------");

		for (int i = 0; i < 10; i++)
		{
			p = store.get("list1", i);
			System.out.println(p.getKey() + ", " + p.getPoints());
		}
	
		System.out.println("------------------------------");

		List<MyUKValue> points = store.getTop("list1", 19);
		
		System.out.println("points size = " + points.size());
		for (int i = 0; i < 20; i++)
		{
			if (i < points.size())
			{
				System.out.println(points.get(i).getKey() + ", " + points.get(i).getPoints());
			}
		}
		
		System.out.println("------------------------------");

		points = store.getTop("list2", 19);
		
		System.out.println("points size = " + points.size());
		for (int i = 0; i < 20; i++)
		{
			if (i < points.size())
			{
				System.out.println(points.get(i).getKey() + ", " + points.get(i).getPoints());
			}
		}

		store.shutdown();
	}

}
