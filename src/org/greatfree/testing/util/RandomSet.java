package org.greatfree.testing.util;

import java.util.Set;

import org.greatfree.util.Rand;

import com.google.common.collect.Sets;

// Created: 09/26/2019, Bing Li
class RandomSet
{

	public static void main(String[] args)
	{
		Set<String> keys = Sets.newHashSet();
		keys.add("AAA");
		keys.add("BBB");

		for (int i = 0; i < 50; i++)
		{
			Set<String> r = Rand.getRandomSet(keys, 2);
			for (String entry : r)
			{
				System.out.print(entry + ", ");
			}
			System.out.println("\n-----------");
		}

		/*
		for (int i = 0; i < 100; i++)
		{
			String key = Rand.getRandomStringInSet(keys);
			System.out.println(i + ") " + key);
		}
		*/
	}

}
