package org.greatfree.testing.cache.local;

import java.util.ArrayList;
import java.util.List;

import org.greatfree.cache.local.SortedList;
import org.greatfree.util.Rand;

// Created: 02/15/2019, Bing Li
class SortedListForAddAllTester
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
		List<MyPointing> pointings = new ArrayList<MyPointing>();
		for (int i = 0; i < 15; i++)
		{
			p = new MyPointing(Rand.getRandom(100));
			pointings.add(p);
		}
		
		list.addAll(pointings);

		System.out.println("------------------------------");

		for (int i = 0; i < 10; i++)
		{
			p = list.get(i);
			System.out.println(p.getKey() + ", " + p.getPoints());
		}

		System.out.println("------------------------------");

//		List<MyPointing> points = list.getTop(3);
//		List<MyPointing> points = list.getTop(15);
		List<MyPointing> points = list.getTop(20);
		System.out.println("pointings size = " + pointings.size());
//		for (int i = 0; i < 3; i++)
		for (int i = 0; i < pointings.size(); i++)
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

		list.shutdown();
	}

}
