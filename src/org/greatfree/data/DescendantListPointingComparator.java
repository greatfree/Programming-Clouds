package org.greatfree.data;

import java.util.Comparator;

import org.greatfree.util.Pointing;

// Created: 07/11/2018, Bing Li
public class DescendantListPointingComparator<ObjPointing extends Pointing> implements Comparator<ObjPointing>
{

	@Override
	public int compare(ObjPointing o1, ObjPointing o2)
	{
		if (o1.getPoints() > o2.getPoints())
		{
//			return 1;
			return -1;
		}
		else
		{
			if (o1.getPoints() < o2.getPoints())
			{
//				return -1;
				return 1;
			}
			else
			{
				return 0;
			}
		}
	}

}
