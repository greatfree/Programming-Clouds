package org.greatfree.testing.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.greatfree.util.Rand;

// Created: 10/13/2019, Bing Li
class SortMapAsConcurrentHashMap
{

	public static void main(String[] args)
	{
		Map<String, Float> m = new ConcurrentHashMap<String, Float>();
		
		m.put("0", Rand.getFRandom());
		m.put("1", Rand.getFRandom());
		m.put("2", Rand.getFRandom());
		m.put("3", Rand.getFRandom());
		m.put("4", Rand.getFRandom());
		m.put("5", Rand.getFRandom());
		m.put("6", Rand.getFRandom());
		m.put("7", Rand.getFRandom());
		m.put("8", Rand.getFRandom());
		m.put("9", Rand.getFRandom());
		
		/*
		 * The method is not valid. When data is put into the concurrent hash map, the order is ruined. 10/13/2019, Bing Li
		 */
//		m = CollectionSorter.sortDescendantAsConcurrency(m);
		
		for (Map.Entry<String, Float> entry : m.entrySet())
		{
			System.out.println(entry.getKey() + ":" + entry.getValue());
		}
	}

}
