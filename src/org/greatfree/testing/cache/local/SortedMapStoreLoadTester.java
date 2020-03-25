package org.greatfree.testing.cache.local;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.greatfree.cache.factory.StoreKeyCreator;
import org.greatfree.cache.local.store.SortedMapStore;

// Created: 03/07/2019, Bing Li
class SortedMapStoreLoadTester
{

	public static void main(String[] args) throws IOException
	{
		String root = "/Temp/";
		
		SortedMapStore<MyPointing, MyPointingFactory, StoreKeyCreator, MyPointingComp> store = new SortedMapStore.SortedMapStoreBuilder<MyPointing, MyPointingFactory, StoreKeyCreator, MyPointingComp>()
				.storeKey("MyMapStore")
				.factory(new MyPointingFactory())
				.cacheSize(1000)
				.keyCreator(new StoreKeyCreator())
				.rootPath(root)
				.totalStoreSize(10000)
				.offheapSizeInMB(1)
				.diskSizeInMB(20)
				.comp(new MyPointingComp())
				.sortSize(100)
				.build();
		
		Map<String, MyPointing> map1 = store.get("map1");

		for (Map.Entry<String, MyPointing> entry : map1.entrySet())
		{
			System.out.println(entry.getKey() + ": " + entry.getValue().getPoints());
		}
		
		MyPointing maxPoint1 = store.get("map1", 0);
		System.out.println("maxPoint1 = : " + maxPoint1.getPoints());
		
		List<MyPointing> m1 = store.get("map1", 1, 4);
		for (MyPointing entry : m1)
		{
			System.out.println(entry.getKey() + ": " + entry.getPoints());
		}

		System.out.println("======================");

		Map<String, MyPointing> map2 = store.get("map2");

		for (Map.Entry<String, MyPointing> entry : map2.entrySet())
		{
			System.out.println(entry.getKey() + ": " + entry.getValue().getPoints());
		}

		MyPointing maxPoint2 = store.get("map2", 0);
		System.out.println("maxPoint2 = : " + maxPoint2.getPoints());

		List<MyPointing> m2 = store.get("map2", 1, 20);
		for (MyPointing entry : m2)
		{
			System.out.println(entry.getKey() + ": " + entry.getPoints());
		}

		System.out.println("======================");

		Map<String, MyPointing> map3 = store.get("map3");

		for (Map.Entry<String, MyPointing> entry : map3.entrySet())
		{
			System.out.println(entry.getKey() + ": " + entry.getValue().getPoints());
		}

		MyPointing maxPoint3 = store.get("map3", 0);
		System.out.println("maxPoint3 = : " + maxPoint3.getPoints());

		List<MyPointing> m3 = store.get("map3", 1, 6);
		for (MyPointing entry : m3)
		{
			System.out.println(entry.getKey() + ": " + entry.getPoints());
		}

		store.close();
	}

}
