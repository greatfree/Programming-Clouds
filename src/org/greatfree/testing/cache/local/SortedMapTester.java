package org.greatfree.testing.cache.local;

import java.util.List;

import org.greatfree.cache.local.SortedMap;
import org.greatfree.util.Rand;


// Created: 02/16/2019, Bing Li
class SortedMapTester
{

	public static void main(String[] args) throws InterruptedException
	{
		String cacheRoot = "/Users/libing/Temp/";
		int cacheSize = 50;

		String cacheKey = "MyPointingMap";
		SortedMap<MyPointing, MyPointingFactory, MyPointingComp> map = new SortedMap.SortedMapBuilder<MyPointing, MyPointingFactory, MyPointingComp>()
				.factory(new MyPointingFactory())
				.rootPath(cacheRoot)
				.cacheKey(cacheKey)
				.cacheSize(cacheSize)
				.offheapSizeInMB(1)
				.diskSizeInMB(20)
				.comp(new MyPointingComp())
				.sortSize(10)
				.build();

		for (int i = 0; i < 15; i++)
		{
			map.put(new MyPointing("ID" + i, Rand.getRandom(100)));
			System.out.println("\n");
		}

		List<MyPointing> points = map.getTop(3);
		for (int i = 0; i < 3; i++)
		{
			System.out.println(points.get(i).getKey() + ", " + points.get(i).getPoints());
		}
		
		System.out.println("------------------------------");
		
		List<MyPointing> sList = map.getTop(10);
		for (int i = 0; i < sList.size(); i++)
		{
			System.out.println(i + ": " + sList.get(i).getPoints());
		}

		System.out.println("---------------------------------------");

		int index = 0;
		for (MyPointing p : sList)
		{
			System.out.println(index++ + ": " + p.getPoints());
		}
		
		/*
		List<MyPointing> tList = new ArrayList<MyPointing>();
		tList.add(new MyPointing("IDT0", 0));
		for (int i = 0; i < 10; i++)
		{
			tList.add(new MyPointing("IDT" + i, Rand.getRandom(10)));
		}
		
		Collections.sort(tList, new MyPointingComp());

		System.out.println("---------------------------------------");

		for (int i = 0; i < tList.size(); i++)
		{
			System.out.println(i + ": " + tList.get(i).getPoints());
		}

		map.put(tList);
		*/

		System.out.println("---------------------------------------");
		
		sList = map.getTop(20);
		for (int i = 0; i < sList.size(); i++)
		{
			System.out.println(i + ": " + sList.get(i).getPoints());
		}
		
		map.shutdown();
	}

}
