package org.greatfree.testing.util;

import java.util.ArrayList;
import java.util.List;

import org.greatfree.util.Rand;
import org.greatfree.util.Tools;

/**
 * 
 * @author libing
 * 
 * 11/04/2022
 *
 */
final class RandomList
{
	public static void main(String[] args)
	{
		List<String> list = new ArrayList<String>();
		for (int i = 0; i < 10; i++)
//		for (int i = 0; i < 3; i++)
		{
			list.add("s" + i);
		}
		
		List<String> sl = Tools.getRandomList(list, 5);
//		List<String> sl = Rand.getRandomListElement(list, 5);
		for (String entry : sl)
		{
			System.out.println(entry);
		}
		
		System.out.println("-----------------------------------");

//		List<String> sl = Tools.getRandomList(list, 5);
		sl = Rand.getRandomList(list, 5);
		for (String entry : sl)
		{
			System.out.println(entry);
		}

		System.out.println("-----------------------------------");
		
		sl = Rand.getRandomListExcept(list, 5, "s0");
		for (String entry : sl)
		{
			System.out.println(entry);
		}
	}
}
