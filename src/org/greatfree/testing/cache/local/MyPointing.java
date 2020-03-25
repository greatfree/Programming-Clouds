package org.greatfree.testing.cache.local;

import org.greatfree.util.Pointing;
import org.greatfree.util.Tools;

// Created: 02/14/2019, Bing Li
class MyPointing extends Pointing
{
	private static final long serialVersionUID = 3355824887784504010L;

	public MyPointing(long points)
	{
		super(Tools.generateUniqueKey(), points);
	}
	
	public MyPointing(String key, float points)
	{
		super(key, points);
	}

}
