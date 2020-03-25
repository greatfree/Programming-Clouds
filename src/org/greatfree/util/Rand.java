package org.greatfree.util;

import java.util.List;
import java.util.Random;
import java.util.Set;

import com.google.common.collect.Sets;

/*
 * This code is used to generate different random number in integer, float and double by enclosing the one, Random, in JDK. 11/10/2014, Bing Li
 */

// Created: 11/10/2014, Bing Li
public class Rand
{
	// Initialize an instance of Random. 11/10/2014, Bing Li
	private static Random random = new Random();
	
	public static char getRandomLowerChar()
	{
		return (char)(random.nextInt(UtilConfig.ALPHABET_SIZE) + UtilConfig.CHAR_A_LOWERCASE);
	}
	
	public static char getRandomUpperChar()
	{
		return (char)((int)UtilConfig.CHAR_A_UPPERCASE + Math.random() * ((int)UtilConfig.CHAR_Z_UPPERCASE - (int)UtilConfig.CHAR_A_UPPERCASE + 1));
	}
	
	public static String getRandomUpperString(int length)
	{
		String str = UtilConfig.EMPTY_STRING;
		for (int i = 0; i < length; i++)
		{
			str += getRandomUpperChar();
		}
		return str;
	}

	public static String getRandomLowerString(int length)
	{
		String str = UtilConfig.EMPTY_STRING;
		for (int i = 0; i < length; i++)
		{
			str += getRandomLowerChar();
		}
		return str;
	}

	/*
	 * Get a random integer which is less than the value of max. 11/10/2014, Bing Li
	 */
	public static int getRandom(int max)
	{
		return random.nextInt(max);
	}

	public static long getNextLong()
	{
		return random.nextLong();
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

	/*
	 * Get one element from a set randomly. 04/23/2018, Bing Li
	 */
	public static <Element> Element getRandomSetElement(Set<Element> set)
	{
		if (set.size() > 0)
		{
			int size = set.size();
			int item = new Random().nextInt(size);
			
			int i = 0;
			for (Element obj : set)
			{
				if (i == item)
				{
					return obj;
				}
				i++;
			}
		}
		return null;
	}

	/*
	 * Get one String from a set whose elements are String randomly. 04/23/2018, Bing Li
	 */
	public static String getRandomStringInSet(Set<String> set)
	{
		if (set.size() > 0)
		{
			int size = set.size();
			int item = new Random().nextInt(size);
			
			int i = 0;
			for (String obj : set)
			{
				if (i == item)
				{
					return obj;
				}
				i++;
			}
		}
		return UtilConfig.EMPTY_STRING;
	}
	
	public static Set<String> getRandomSet(Set<String> set, int size)
	{
		Set<String> keys = Sets.newHashSet();
//		for (int i = 0; i < size; i++)
		do
		{
			keys.add(getRandomStringInSet(set));
		}
		while (keys.size() < size);
		return keys;
	}

	/*
	 * Get one String from a list whose elements are String randomly. 04/23/2018, Bing Li
	 */
	public static String getRandomListElement(List<String> list)
	{
		if (list.size() > 0)
		{
			int size = list.size();
			int item = new Random().nextInt(size);
			
			int i = 0;
			for (String obj : list)
			{
				if (i == item)
				{
					return obj;
				}
				i++;
			}
		}
		return UtilConfig.EMPTY_STRING;
	}

	/*
	 * Get one String from a set whose elements are String randomly. But the specified one should be be selected. 04/23/2018, Bing Li
	 */
	public static String getRandomSetElementExcept(Set<String> set, String elementKey)
	{
		// The reason to initialize a new set to keep the set is to avoid updating the input set. Otherwise, it is possible to affect other code. 08/01/2015, Bing Li
		Set<String> backupSet = Sets.newHashSet();
		backupSet.addAll(set);
		backupSet.remove(elementKey);
		return getRandomStringInSet(backupSet);
	}

	/*
	 * Get one String from a set whose elements are String randomly. But the specified ones should be be selected. 04/23/2018, Bing Li
	 */
	public static String getRandomSetElementExcept(Set<String> set, Set<String> elementKeys)
	{
		// The reason to initialize a new set to keep the set is to avoid updating the input set. Otherwise, it is possible to affect other code. 08/01/2015, Bing Li
		Set<String> backupSet = Sets.newHashSet();
		backupSet.addAll(set);
		return getRandomStringInSet(Sets.difference(backupSet, elementKeys));
	}

}
