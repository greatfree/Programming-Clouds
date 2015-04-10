package com.greatfree.util;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/*
 * The class aims to sort a collection in the ascending or descending manner. It is also able to select the maximum or minimum value from the collection. 08/26/2014, Bing Li
 */

// Created: 08/26/2014, Bing Li
public class CollectionSorter
{
	/*
	 * Sort a map in the ascending order. 08/26/2014, Bing Li
	 */
	public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map)
	{
		List<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>(map.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<K, V>>()
		{
			public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2)
			{
				return (o1.getValue()).compareTo(o2.getValue());
			}
		});
		
		Map<K, V> result = new LinkedHashMap<K, V>();
		for (Map.Entry<K, V> entry : list)
		{
			result.put(entry.getKey(), entry.getValue());
		}
		return result;
	}

	/*
	 * Sort a map in the ascending order and only the top ranked are returned. The returned count is equal to topCount. 08/26/2014, Bing Li
	 */
	public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map, double topCount)
	{
		List<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>(map.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<K, V>>()
		{
			public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2)
			{
				return (o1.getValue()).compareTo(o2.getValue());
			}
		});
		
		Map<K, V> result = new LinkedHashMap<K, V>();
		for (Map.Entry<K, V> entry : list)
		{
			result.put(entry.getKey(), entry.getValue());
			if (result.size() >= topCount)
			{
				return result;
			}
		}
		return result;
	}

	/*
	 * Retrieve the minimum value from a map. 08/26/2014, Bing Li
	 */
	public static <K, V extends Comparable<? super V>> K minValueKey(Map<K, V> map)
	{
		if (map.size() > 0)
		{
			List<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>(map.entrySet());
			Collections.sort(list, new Comparator<Map.Entry<K, V>>()
			{
				public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2)
				{
					return (o1.getValue()).compareTo(o2.getValue());
				}
			});
			
			return list.get(0).getKey();
		}
		else
		{
			return null;
		}
	}

	/*
	 * Sort a map in the descending order. 08/26/2014, Bing Li
	 */
	public static <K, V extends Comparable<? super V>> Map<K, V> sortDescendantByValue(Map<K, V> map)
	{
		List<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>(map.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<K, V>>()
		{
			public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2)
			{
				return (o2.getValue()).compareTo(o1.getValue());
			}
		});
		
		Map<K, V> result = new LinkedHashMap<K, V>();
		for (Map.Entry<K, V> entry : list)
		{
			result.put(entry.getKey(), entry.getValue());
		}
		return result;
	}

	/*
	 * Sort a map in the descending order and only the top ranked are returned. The returned count is equal to topCount. 08/26/2014, Bing Li
	 */
	public static <K, V extends Comparable<? super V>> Map<K, V> sortDescendantByValue(Map<K, V> map, double topCount)
	{
		List<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>(map.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<K, V>>()
		{
			public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2)
			{
				return (o2.getValue()).compareTo(o1.getValue());
			}
		});
		
		Map<K, V> result = new LinkedHashMap<K, V>();
		for (Map.Entry<K, V> entry : list)
		{
			result.put(entry.getKey(), entry.getValue());
			if (result.size() >= topCount)
			{
				return result;
			}
		}
		return result;
	}

	/*
	 * Retrieve the maximum value from a map. 08/26/2014, Bing Li
	 */
	public static <K, V extends Comparable<? super V>> K maxValueKey(Map<K, V> map)
	{
		if (map.size() > 0)
		{
			List<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>(map.entrySet());
			Collections.sort(list, new Comparator<Map.Entry<K, V>>()
			{
				public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2)
				{
					return (o2.getValue()).compareTo(o1.getValue());
				}
			});
			
			return list.get(0).getKey();
		}
		else
		{
			return null;
		}
	}
}
