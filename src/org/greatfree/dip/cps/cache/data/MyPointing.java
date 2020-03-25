package org.greatfree.dip.cps.cache.data;

import org.greatfree.util.Pointing;

// Created: 07/11/2018, Bing Li
public class MyPointing extends Pointing
{
	private static final long serialVersionUID = 3408208356996829221L;
	
	private String description;

	public MyPointing(String key, float points, String description)
	{
		super(key, points);
		this.description = description;
	}

	public String getDescription()
	{
		return this.description;
	}
}
