package org.greatfree.cache.factory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.greatfree.util.UtilConfig;

/*
 * The version is tested in the Clouds project. 08/22/2018, Bing Li
 */

// Created: 06/21/2017, Bing Li
public class SortedListIndexes implements Serializable
{
	private static final long serialVersionUID = 4796920455296031889L;
	
//	private double minPoints;
//	private Set<String> keys;
//	private Map<Integer, String> keys;
	private List<String> keys;
	private Map<String, Float> points;
	
	private List<String> obsoleteKeys;
	
//	private int count;
	
	public SortedListIndexes()
	{
//		this.minPoints = 0;
//		this.keys = new HashMap<Integer, String>();
		this.keys = new ArrayList<String>();
		this.points = new HashMap<String, Float>();
//		this.count = 0;
		this.obsoleteKeys = new ArrayList<String>();
	}
	
//	public PointingListIndexes(Map<Integer, String> keys, Map<String, Double> points)
	public SortedListIndexes(List<String> keys, Map<String, Float> points, List<String> obsKeys)
	{
		this.keys = keys;
		this.points = points;
		this.obsoleteKeys = obsKeys;
	}

	/*
	public double getMinPoints()
	{
		return this.minPoints;
	}
	
	public void setMinPoints(double points)
	{
		this.minPoints = points;
	}
	*/
	
	/*
	public void setCount(int count)
	{
		this.count = count;
	}
	
	public int getCount()
	{
		return this.count;
	}
	*/
	
	public String getKey(int index)
	{
		if (this.keys.size() > index)
		{
			try
			{
				return this.keys.get(index);
			}
			catch (ArrayIndexOutOfBoundsException e)
			{
				// The evicting might lead to the inconsistency here. 09/07/2019, Bing Li
				return UtilConfig.EMPTY_STRING;
			}
		}
		return UtilConfig.EMPTY_STRING;
	}
	
//	public void addKey(int index, String key)
	public void addKey(String key)
	{
//		this.keys.put(index, key);
		this.keys.add(key);
	}
	
	public void addObsKey(String key)
	{
		this.obsoleteKeys.add(key);
	}
	
	public void removeObsKey(int index)
	{
		this.obsoleteKeys.remove(index);
	}
	
	public int getObsSize()
	{
		return this.obsoleteKeys.size();
	}
	
	public void clearKeys()
	{
		this.keys.clear();
	}
	
	public int getKeySize()
	{
		return this.keys.size();
	}
	
	/*
	public void appendKey(String key)
	{
//		this.keys.put(this.keys.size(), key);
		this.keys.add(key);
	}
	*/
	
	public double get(String key)
	{
		return this.points.get(key);
	}

	public void put(String key, float points)
	{
		this.points.put(key, points);
	}
	
//	public Map<Integer, String> getKeys()
	public List<String> getKeys()
	{
		return this.keys;
	}
	
	public Map<String, Float> getPoints()
	{
		return this.points;
	}
	
	public List<String> getObsoleteKeys()
	{
		return this.obsoleteKeys;
	}
	
	public String getObsKey(int index)
	{
		if (this.obsoleteKeys.size() > index)
		{
			return this.obsoleteKeys.get(index);
		}
		return UtilConfig.EMPTY_STRING;
	}
	
	public void setPoints(Map<String, Float> points)
	{
		this.points = points;
	}

	public double getMinPoints()
	{
		return this.points.get(this.keys.get(this.keys.size() - 1));
	}
	
	public int getIndex(String key)
	{
		return this.keys.indexOf(key);
	}
	
	public void remove(String key)
	{
		this.points.remove(key);
	}
	
	public void remove(int index)
	{
		this.keys.remove(index);
	}
}
