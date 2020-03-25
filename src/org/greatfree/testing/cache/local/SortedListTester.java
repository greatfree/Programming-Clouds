package org.greatfree.testing.cache.local;

import java.util.List;

import org.greatfree.cache.local.SortedList;
import org.greatfree.util.Rand;

/*
 * The tester focuses on the method add(). 02/15/2019, Bing Li
 */

// Created: 02/14/2019, Bing Li
class SortedListTester
{

	public static void main(String[] args) throws InterruptedException
	{
		SortedList<MyPointing, MyPointingFactory, MyPointingComp> list = new SortedList.SortedListBuilder<MyPointing, MyPointingFactory, MyPointingComp>()
				.factory(new MyPointingFactory())
				.rootPath("/Users/libing/Temp/")
				.cacheKey("mylist")
				.cacheSize(100)
				.offheapSizeInMB(4)
				.diskSizeInMB(10)
				.comp(new MyPointingComp())
				.sortSize(10)
				.build();

		MyPointing p;
		for (int i = 0; i < 15; i++)
		{
//			p = new MyPointing("key" + i, Rand.getRandom(100));
			p = new MyPointing(Rand.getRandom(100));
//			System.out.println(p.getKey() + ", " + p.getPoints());
//			list.add(new MyPointing("key" + i, Rand.getNextLong()));
			list.add(p);
		}

		System.out.println("------------------------------");

		for (int i = 0; i < 10; i++)
		{
			p = list.get(i);
			System.out.println(p.getKey() + ", " + p.getPoints());
		}

		System.out.println("------------------------------");

		List<MyPointing> points = list.getTop(3);
		for (int i = 0; i < 3; i++)
		{
			System.out.println(points.get(i).getKey() + ", " + points.get(i).getPoints());
		}
		
		System.out.println("------------------------------");

		/*
		Set<Integer> obsKeys = list.getObsKeys();
		for (Integer entry : obsKeys)
		{
			System.out.println(entry + ", " + list.getKey(entry) + ": " + list.get(list.getKey(entry)).getPoints());
		}
		*/

		System.out.println("------------------------------");

		/*
//		points = list.get(1, 13);
		points = list.get(0, 14);
		for (int i = 0; i < 15; i++)
		{
			System.out.println(points.get(i).getKey() + ", " + points.get(i).getPoints());
		}
		*/

		list.shutdown();
	}

}
