package org.greatfree.testing.cache.local;

import org.greatfree.util.UniqueKey;

// Created: 02/15/2019, Bing Li
public class MyUKValue extends UniqueKey
{
	private static final long serialVersionUID = -2504450050898536875L;
	
	private int points;
	
	public MyUKValue(int points)
	{
		this.points = points;
	}
	
	/*
	 * The constructor can also be used to initializing the value of map store. 11/03/2019, Bing Li
	 * 
	 * The constructor is used only when the object is loaded from DB to restore its original key. 02/28/2019, Bing Li
	 */
	public MyUKValue(String key, int points)
	{
		super(key);
		this.points = points;
	}
	
	public int getPoints()
	{
		return this.points;
	}

}
