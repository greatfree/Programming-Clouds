package org.greatfree.testing.cache.local.store;

import org.greatfree.cache.CacheElement;

// Created: 08/08/2018, Bing Li
class MyStackObj extends CacheElement<Integer>
{
	private static final long serialVersionUID = 7319702878480305213L;

	private double points;

	public MyStackObj(Integer key, double points)
	{
		super(key);
		this.points = points;
	}

	public double getPoints()
	{
		return this.points;
	}
}
