package org.greatfree.util;

import java.util.Calendar;
import java.util.Date;

/*
 * This is another general object that defines some fundamental information that is required to manage in a pool. Different from the one, FreeObject, this object is managed by the hash key rather than the object key. 11/26/2014, Bing Li
 * 
 * A hash key means that each object has a unique key for any instances without taking care of their types. Well, the object key is assigned to those objects which are classified in the same type. 11/26/2014, Bing Li
 *  
 */

// Created: 11/26/2014, Bing Li
public abstract class HashFreeObject implements Comparable<HashFreeObject>
{
	// The hash key is created uniquely arbitrarily. 11/26/2014, Bing Li
	private String hashKey;
	// An integer that represents the type of the class. 11/26/2014, Bing Li
	private int type;
	// The read or write accessed time stamp, which assists the caching technique to differentiate frequently-used objects from seldom-used ones. 11/26/2014, Bing Li
	private Date accessedTime;

	/*
	 * Initialize the object. 11/26/2014, Bing Li
	 */
	public HashFreeObject()
	{
		this.hashKey = Tools.generateUniqueKey();
		this.accessedTime = Calendar.getInstance().getTime();
	}

	/*
	 * Dispose. 11/26/2014, Bing Li
	 */
	public void dispose() throws InterruptedException
	{
	}
	
	/*
	 * Expose the hash key. 11/26/2014, Bing Li
	 */
	public String getHashKey()
	{
		return this.hashKey;
	}
	
	/*
	 * Get the type of the object. 11/26/2014, Bing Li
	 */
	public int getType()
	{
		return this.type;
	}
	
	/*
	 * Get the last accessed time of the object. 11/26/2014, Bing Li
	 */
	public Date getAccessedTime()
	{
		return this.accessedTime;
	}
	
	/*
	 * Set the accessed time of the object. Usually, it is performed when the object is read and written. 11/26/2014, Bing Li
	 */
	public void setAccessedTime()
	{
		this.accessedTime = Calendar.getInstance().getTime();
	}

	/*
	 * The method achieves the goal that objects that extend the class can be compared upon their accessed times. 11/26/2014, Bing Li
	 */
	@Override
	public int compareTo(HashFreeObject obj)
	{
		if (obj != null)
		{
			if (this.accessedTime.equals(obj.getAccessedTime()))
			{
				return 0;
			}
			else if (this.accessedTime.after(obj.getAccessedTime()))
			{
				return 1;
			}
			else
			{
				return -1;
			}
		}
		else
		{
			return 1;
		}
	}
}
