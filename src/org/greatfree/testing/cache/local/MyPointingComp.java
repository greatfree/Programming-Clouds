package org.greatfree.testing.cache.local;

import java.util.Comparator;

// Created: 02/14/2019, Bing Li
class MyPointingComp implements Comparator<MyPointing>
{

	@Override
	public int compare(MyPointing o1, MyPointing o2)
	{
		if (o1.getPoints() < o2.getPoints())
		{
			return 1;
		}
		else
		{
			if (o1.getPoints() > o2.getPoints())
			{			
				return -1;
			}
			else
			{
				return 0;
			}
		}
	}

}
