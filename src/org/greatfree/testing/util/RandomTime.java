package org.greatfree.testing.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.greatfree.util.Time;

// Created: 08/18/2018, Bing Li
class RandomTime
{

	public static void main(String[] args)
	{
		List<Long> times = new ArrayList<Long>();
		Date time;
		for (int i = 0; i < 10; i++)
		{
			time = Time.getRandomTime();
			System.out.println(time + ": " + time.getTime());
			times.add(time.getTime());
		}
		
		System.out.println("------------------------------");

		for (Long entry : times)
		{
			System.out.println("Time: " + Time.getTime(entry));
		}
	}

}
