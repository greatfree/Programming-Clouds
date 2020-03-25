package org.greatfree.testing.util;

import java.util.HashMap;
import java.util.Map;

import org.greatfree.util.CollectionSorter;
import org.greatfree.util.Rand;

// Created: 10/13/2019, Bing Li
class SortMapAsLinkedHashMap
{

	public static void main(String[] args)
	{
		Map<String, Float> m = new HashMap<String, Float>();
		
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
		
		m = CollectionSorter.sortDescendantByValue(m);
		
		for (Map.Entry<String, Float> entry : m.entrySet())
		{
			System.out.println(entry.getKey() + ":" + entry.getValue());
		}

	}

}
