package org.greatfree.testing.cache.local;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.greatfree.cache.factory.StoreKeyCreator;
import org.greatfree.cache.local.store.SortedListStore;
import org.greatfree.util.Rand;

// Created: 02/16/2019, Bing Li
class SortedListStoreForAddAllTester
{

	public static void main(String[] args) throws InterruptedException, IOException
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
		List<MyPointing> pointings = new ArrayList<MyPointing>();
		for (int i = 0; i < 15; i++)
		{
			p = new MyPointing(Rand.getRandom(100));
			pointings.add(p);
		}
		store.addAll("list1", pointings);

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

		points = store.getTop("list1", 20);
//		for (int i = 0; i < 21; i++)
		for (int i = 0; i < points.size(); i++)
		{
			System.out.println(points.get(i).getKey() + ", " + points.get(i).getPoints());
		}

		System.out.println("------------------------------");

		List<String> obsKeys = store.getObsKey("list1");
		for (String entry : obsKeys)
		{
			System.out.println(entry + ", " + entry + ": " + store.get(entry).getPoints());
		}

		System.out.println("------------------------------");
		
		points = store.get("list1", 0, 25);
		System.out.println("points size = " + points.size());
		for (int i = 0; i < points.size(); i++)
		{
			System.out.println(points.get(i).getKey() + ", " + points.get(i).getPoints());
		}
		System.out.println("------------------------------");
		
		points = store.get("list1", 21, 25);
		System.out.println("points size = " + points.size());
		for (int i = 0; i < points.size(); i++)
		{
			System.out.println(points.get(i).getKey() + ", " + points.get(i).getPoints());
		}

		store.close();
	}

}
