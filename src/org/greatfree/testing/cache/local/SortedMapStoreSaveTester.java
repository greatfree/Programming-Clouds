package org.greatfree.testing.cache.local;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.greatfree.cache.factory.StoreKeyCreator;
import org.greatfree.cache.local.store.SortedMapStore;
import org.greatfree.util.Rand;

// Created: 03/07/2019, Bing Li
class SortedMapStoreSaveTester
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

		List<MyPointing> cache1Values = new ArrayList<MyPointing>();
		
		MyPointing pointing;
		for (int i = 0; i < 10; i++)
		{
			pointing = new MyPointing("ID" + i, Rand.getRandom(100));
//			cache1Values.put(pointing.getKey(), pointing);
			cache1Values.add(pointing);
		}

		store.putAll("map1", cache1Values);
		
//		Map<String, MyPointing> cache2Values = new HashMap<String, MyPointing>();
		List<MyPointing> cache2Values = new ArrayList<MyPointing>();

		for (int i = 0; i < 5; i++)
		{
			pointing = new MyPointing("ID" + i, Rand.getRandom(100));
//			cache2Values.put(pointing.getKey(), pointing);
			cache2Values.add(pointing);
		}

		store.putAll("map2", cache2Values);

//		Map<String, MyPointing> cache3Values = new HashMap<String, MyPointing>();
		List<MyPointing> cache3Values = new ArrayList<MyPointing>();

		for (int i = 0; i < 15; i++)
		{
			pointing = new MyPointing("ID" + i, Rand.getRandom(100));
//			cache3Values.put(pointing.getKey(), pointing);
			cache3Values.add(pointing);
		}

		store.putAll("map3", cache3Values);
		
		store.close();
		
		System.out.println("Done!");
		
		
	}

}
