package org.greatfree.data;

import java.util.Comparator;

import org.greatfree.util.Timing;

// Created: 08/19/2018, Bing Li
public class DescendantListTimingComparator<ObjTiming extends Timing> implements Comparator<ObjTiming>
{

	@Override
	public int compare(ObjTiming o1, ObjTiming o2)
	{
		if (o1.getTime().after(o2.getTime()))
		{
			return -1;
		}
		else
		{
			if (o1.getTime().before(o2.getTime()))
			{			
				return 1;
			}
			else
			{
				return 0;
			}
		}
	}

}
