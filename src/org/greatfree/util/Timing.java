package org.greatfree.util;

import java.util.Date;

// Created: 12/06/2015, Bing Li
//public abstract class Timing implements Comparable<Timing>
//public abstract class Timing implements Serializable
public abstract class Timing extends SerializedKey
{
	private static final long serialVersionUID = 3866391181897653996L;
	private Date time;
	
	public Timing(String key, Date time)
	{
		super(key);
		this.time = time;
	}
	
	public Date getTime()
	{
		return this.time;
	}

	public void setTime(Date time)
	{
		this.time = time;
	}

	/*
	@Override
	public int compareTo(Timing remoteObj)
	{
		if (remoteObj != null)
		{
			if (this.time.after(remoteObj.getTime()))
			{
				return 1;
			}
			else
			{
				if (this.time.equals(remoteObj.getTime()))
				{
					return 0;
				}
				return -1;
			}
		}
		else
		{
			return 1;
		}
	}
	*/
}
