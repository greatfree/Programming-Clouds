package org.greatfree.testing.cache.local;

import java.io.IOException;
import java.util.List;

import org.greatfree.cache.local.CacheList;
import org.greatfree.util.Rand;

// Created: 02/15/2019, Bing Li
class CacheListTester
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
		for (int i = 0; i < 15; i++)
		{
			p = new MyUKValue(Rand.getRandom(100));
			list.add(p);
		}

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

		int index = 0;
		do
		{
			p = list.get(index++);
			if (p != null)
			{
				System.out.println(p.getKey() + ", " + p.getPoints());
			}
			else
			{
				break;
			}
		}
		while (p != null);

		list.shutdown();
	}

}
