package org.greatfree.testing.cache.local.store;

import org.greatfree.cache.factory.StoreKeyCreator;
import org.greatfree.cache.local.store.MapStore;
import org.greatfree.testing.cache.local.MyUKFactory;
import org.greatfree.testing.cache.local.MyUKValue;

// Created: 11/03/2019, Bing Li
class MapStoreLoadTester
{

	public static void main(String[] args)
	{
		String home = "/Users/libing/GreatFreeLabs/Temp/";
		MapStore<MyUKValue, MyUKFactory, StoreKeyCreator> store = new MapStore.MapStoreBuilder<MyUKValue, MyUKFactory, StoreKeyCreator>()
				.storeKey("mystore")
				.factory(new MyUKFactory())
				.cacheSize(1000)
				.keyCreator(new StoreKeyCreator())
				.rootPath(home)
				.totalStoreSize(10000)
				.offheapSizeInMB(4)
				.diskSizeInMB(10)
				.build();

		MyUKValue p;

		String map1 = "map1";
		String map2 = "map2";

		for (int i = 0; i < 10; i++)
		{
			p = store.get(map1, map1 + i);
			System.out.println(p.getKey() + ", " + p.getPoints());
		}

		System.out.println("------------------------------");
		
		for (int i = 0; i < 20; i++)
		{
			p = store.get(map2, map2 + i);
			if (p != null)
			{
				System.out.println(p.getKey() + ", " + p.getPoints());
			}
		}
		
		store.shutdown();
	}

}
