package com.greatfree.util;

import java.util.Random;

/*
 * This code is used to generate different random number in integer, float and double by enclosing the one, Random, in JDK. 11/10/2014, Bing Li
 */

// Created: 11/10/2014, Bing Li
public class Rand
{
	// Initialize an instance of Random. 11/10/2014, Bing Li
	private static Random random = new Random();

	/*
	 * Get a random integer which is less than the value of max. 11/10/2014, Bing Li
	 */
	public static int getRandom(int max)
	{
		return random.nextInt(max);
	}

	/*
	 * Get a random integer between the value of startInt and that of endInt. 11/10/2014, Bing Li
	 */
	public static int getRandom(int startInt, int endInt)
	{
		// Check whether startInt is greater than endInt. 11/10/2014, Bing Li
		if (startInt > endInt)
		{
			// If so, it does not make sense. Return -1. 11/10/2014, Bing Li
			return -1;
		}
		// Generate the random integer. 11/10/2014, Bing Li
		return startInt + (int)((endInt - startInt) * random.nextDouble());
	}

	/*
	 * Get a random double. 11/10/2014, Bing Li
	 */
	public static double getDRandom()
	{
		return random.nextDouble();
	}

	/*
	 * Get a random float. 11/10/2014, Bing Li
	 */
	public static float getFRandom()
	{
		return random.nextFloat();
	}
}
