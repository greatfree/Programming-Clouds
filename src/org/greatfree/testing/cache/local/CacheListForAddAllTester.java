package org.greatfree.testing.cache.local;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.greatfree.cache.local.CacheList;
import org.greatfree.util.Rand;

// Created: 02/15/2019, Bing Li
class CacheListForAddAllTester
{

	public static void main(String[] args) throws InterruptedException, IOException
	{
		CacheList<MyUKValue, MyUKFactory> list = new CacheList.CacheListBuilder<MyUKValue, MyUKFactory>()
				.factory(new MyUKFactory())
				.rootPath("/Users/libing/Temp/")
				.cacheKey("mylist")
				.cacheSize(100)
				.offheapSizeInMB(4)
				.diskSizeInMB(10)
				.build();

		MyUKValue p;
		List<MyUKValue> pointings = new ArrayList<MyUKValue>();
		for (int i = 0; i < 15; i++)
		{
			p = new MyUKValue(Rand.getRandom(100));
			pointings.add(p);
		}

		list.add(pointings);

		System.out.println("------------------------------");

		for (int i = 0; i < 10; i++)
		{
			p = list.get(i);
			System.out.println(p.getKey() + ", " + p.getPoints());
		}

		System.out.println("------------------------------");

		List<MyUKValue> points = list.getTop(3);
		for (int i = 0; i < 3; i++)
		{
			System.out.println(points.get(i).getKey() + ", " + points.get(i).getPoints());
		}
		
		System.out.println("------------------------------");

		points = list.get(0, 14);
		for (int i = 0; i < 15; i++)
		{
			System.out.println(points.get(i).getKey() + ", " + points.get(i).getPoints());
		}

		System.out.println("------------------------------");

		long size = list.getMemCacheSize();
		for (int i = 0; i < size; i++)
		{
			System.out.println(list.get(i).getKey() + ", " + list.get(i).getPoints());
		}

		list.shutdown();
	}

}
