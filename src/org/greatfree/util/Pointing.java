package org.greatfree.util;

import java.util.Date;

import org.greatfree.cache.StoreElement;

// Created: 12/06/2015, Bing Li
//public abstract class Pointing implements Serializable
//public abstract class Pointing extends SerializedKey<String>
//public abstract class Pointing extends SerializedKey
public abstract class Pointing extends StoreElement
{
	private static final long serialVersionUID = -5549376092897969493L;
	//	private int index;
//	private double points;
//	private long points;
	private float points;
//	private int maxIndex;
	
//	public Pointing(int index, double points, int maxIndex)
//	public Pointing(int index, double points)
//	public Pointing(String key, double points)
	
	/*
	 * The key should be unique. Sometimes it is easy to make mistakes for the uniqueness issue. 08/30/2019, Bing Li
	 * 
	 * When pointing is saved in store, the constructor needs to be used. 08/24/2018, Bing Li
	 */
	public Pointing(String cacheKey, String key, float points)
	{
		super(cacheKey, key);
//		this.index = index;
		this.points = points;
//		this.maxIndex = maxIndex;
	}
	
	/*
	 * When pointing is saved in cache based on map, the constructor needs to be used. 08/24/2018, Bing Li
	 */
	public Pointing(String key, float points)
	{
		super(UtilConfig.NO_KEY, key);
		this.points = points;
	}
	
	/*
	 * When pointing is saved in cache based on list, the constructor needs to be used. 08/24/2018, Bing Li
	 */
	public Pointing(float points)
	{
		super(UtilConfig.NO_KEY, Tools.generateUniqueKey());
		this.points = points;
	}

	/*
	public Pointing(double points)
	{
		this.points = points;
		this.index = SocialRanks.NO_INDEX;
	}
	
	public int getIndex()
	{
		return this.index;
	}
	
	public void setIndex(int index)
	{
		this.index = index;
	}
	*/
	
	public float getPoints()
	{
		return this.points;
	}
	
	public void setPoints(float points)
	{
		this.points = points;
	}

	/*
	public int getMaxIndex()
	{
		return this.maxIndex;
	}
	*/
	
	public Date getTime()
	{
		return Time.getTime((long)this.points);
	}
}
