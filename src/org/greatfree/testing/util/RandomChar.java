package org.greatfree.testing.util;

import org.greatfree.util.Rand;
import org.greatfree.util.UtilConfig;

// Created: 09/19/2019, Bing Li
class RandomChar
{

	public static void main(String[] args)
	{
		for (int i = 0; i < UtilConfig.ALPHABET_SIZE; i++)
		{
			System.out.print(Rand.getRandomLowerChar());
		}
		System.out.println("\n==================");

		for (int i = 0; i < UtilConfig.ALPHABET_SIZE; i++)
		{
			System.out.print(Rand.getRandomUpperChar());
		}
		System.out.println("\n==================");
		
		for (int i = 0; i < UtilConfig.ALPHABET_SIZE; i++)
		{
			System.out.println(Rand.getRandomUpperString(3));
		}
		System.out.println("==================");
		
		for (int i = 0; i < UtilConfig.ALPHABET_SIZE; i++)
		{
			System.out.println(Rand.getRandomLowerString(3));
		}
	}

}
