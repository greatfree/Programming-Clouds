package org.greatfree.util;

/*
 * This is an object that can be compared by its key. 11/28/2014, Bing Li
 */

// Created: 11/28/2014, Bing Li
public class StringObj implements Comparable<StringObj>
{
	// The key of the object. 11/28/2014, Bing Li
	private String key;

	/*
	 * Initialize. 11/28/2014, Bing Li
	 */
	public StringObj(String key)
	{
		this.key = key;
	}

	/*
	 * Expose the key. 11/28/2014, Bing Li
	 */
	public String getKey()
	{
		return this.key;
	}

	/*
	 * Compare the object with another one. 11/28/2014, Bing Li
	 */
	@Override
	public int compareTo(StringObj remoteObj)
	{
		if (remoteObj != null)
		{
			return this.key.compareTo(remoteObj.getKey());
		}
		else
		{
			return 1;
		}
	}
}
