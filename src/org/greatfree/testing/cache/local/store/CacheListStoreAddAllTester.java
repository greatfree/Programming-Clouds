package org.greatfree.testing.cache.local.store;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.greatfree.cache.factory.StoreKeyCreator;
import org.greatfree.cache.local.store.ListStore;
import org.greatfree.testing.cache.local.MyUKFactory;
import org.greatfree.testing.cache.local.MyUKValue;
import org.greatfree.util.Rand;

// Created: 11/22/2019, Bing Li
class CacheListStoreAddAllTester
{

	public static void main(String[] args) throws InterruptedException, IOException
	{
		ListStore<MyUKValue, MyUKFactory, StoreKeyCreator> store = new ListStore.CacheListStoreBuilder<MyUKValue, MyUKFactory, StoreKeyCreator>()
//				CacheListStore<MyUKValue, MyUKFactory> store = new CacheListStore.CacheListStoreBuilder<MyUKValue, MyUKFactory>()
			.storeKey("mystore")
			.factory(new MyUKFactory())
			.cacheSize(1000)
			.keyCreator(new StoreKeyCreator())
			.rootPath("/Users/libing/GreatFreeLabs/Temp/")
	//						.rootPath("/Temp/")
			.totalStoreSize(10000)
			.offheapSizeInMB(4)
			.diskSizeInMB(10)
			.build();
				
		MyUKValue p;
		List<MyUKValue> ps = new ArrayList<MyUKValue>();
		for (int i = 0; i < 15; i++)
		{
			p = new MyUKValue(Rand.getRandom(100));
			ps.add(p);
		}

		store.addAll("list1", ps);

		ps.clear();
		
		for (int i = 0; i < 15; i++)
		{
			p = new MyUKValue(Rand.getRandom(100));
			ps.add(p);
		}

		store.addAll("list2", ps);

		System.out.println("------------------------------");
		
		ps = store.getTop("list1", 10);

		for (int i = 0; i < 10; i++)
		{
			p = ps.get(i);
			System.out.println(p.getKey() + ", " + p.getPoints());
		}
	
		System.out.println("------------------------------");

		ps = store.getTop("list1", 19);
		
		System.out.println("points size = " + ps.size());
		for (int i = 0; i < 20; i++)
		{
			if (i < ps.size())
			{
				System.out.println(ps.get(i).getKey() + ", " + ps.get(i).getPoints());
			}
		}
		
		System.out.println("------------------------------");

		ps = store.getTop("list2", 19);
		
		System.out.println("points size = " + ps.size());
		for (int i = 0; i < 20; i++)
		{
			if (i < ps.size())
			{
				System.out.println(ps.get(i).getKey() + ", " + ps.get(i).getPoints());
			}
		}

		store.shutdown();
	}

}
