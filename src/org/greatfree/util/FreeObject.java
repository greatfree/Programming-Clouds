package org.greatfree.util;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

/*
 * Serializable is added. I noticed that when getting the keys of the object and send them over the network, the error of being not serializable is caused. This is an old design. I hope I could upgrade it with the pools from some open source. 09/13/2020, Bing Li
 * 
 * The class is designed in the system to fit object reusing, caching and so on. 07/30/2014, Bing Li
 */

// Created: 07/17/2014, Bing Li
public class FreeObject implements Comparable<FreeObject>, Serializable
{
	private static final long serialVersionUID = -5133784154054922445L;
	// The object key is created uniquely with respect to a unique character of the instance of the class. 08/10/2014, Bing Li
	private String objectKey;
	// The hash key is created uniquely arbitrarily. 08/10/2014, Bing Li
	private String hashKey;
	// An integer that represents the type of the class. 08/10/2014, Bing Li
	public int type;
	// The read or write accessed time stamp, which assists the caching technique to differentiate frequently-used objects from seldom-used ones. 08/10/2014, Bing Li
	private Date accessedTime;

	/*
	 * Initialize the object with an explicit type. 08/10/2014, Bing Li
	 */
	public FreeObject(int type, String objectKey)
	{
		this.objectKey = objectKey;
		this.hashKey = Tools.generateUniqueKey();
		this.type = type;
		this.accessedTime = Calendar.getInstance().getTime();
	}

	/*
	 * Initialize the object. When initializing the object with the constructor, the type is ignored. 08/10/2014, Bing Li
	 */
	public FreeObject(String objectKey)
	{
		this.objectKey = objectKey;
		this.hashKey = Tools.generateUniqueKey();
		this.type = UtilConfig.NO_TYPE;
		this.accessedTime = Calendar.getInstance().getTime();
	}

	/*
	 * Initialize the object. The constructor cares about neither the object key nor the type. 11/10/2014, Bing Li 
	 */
	public FreeObject()
	{
		this.objectKey = UtilConfig.NO_KEY;
		this.hashKey = Tools.generateUniqueKey();
		this.type = UtilConfig.NO_TYPE;
		this.accessedTime = Calendar.getInstance().getTime();
	}

	/*
	 * Expose the hash key. 08/10/2014, Bing Li
	 */
	public String getHashKey()
	{
		return this.hashKey;
	}

	/*
	 * Expose the object key. 08/10/2014, Bing Li
	 */
	public String getObjectKey()
	{
		return this.objectKey;
	}

	/*
	 * Set the object key. 08/10/2014, Bing Li
	 */
	public void setObjectKey(String objectKey)
	{
		this.objectKey = objectKey;
	}

	/*
	 * Get the type of the object. 08/10/2014, Bing Li
	 */
	public int getType()
	{
		return this.type;
	}

	/*
	 * Get the last accessed time of the object. 08/10/2014, Bing Li
	 */
	public Date getAccessedTime()
	{
		return this.accessedTime;
	}

	/*
	 * Set the accessed time of the object. Usually, it is performed when the object is read and written. 08/10/2014, Bing Li
	 */
	public void setAccessedTime()
	{
		this.accessedTime = Calendar.getInstance().getTime();
	}

	/*
	 * The method achieves the goal that objects that extend the class can be compared upon their accessed times. 08/10/2014, Bing Li
	 */
	@Override
	public int compareTo(FreeObject obj)
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
