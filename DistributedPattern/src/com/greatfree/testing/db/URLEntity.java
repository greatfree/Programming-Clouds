package com.greatfree.testing.db;

import com.greatfree.testing.data.Constants;
import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;

/*
 * This is an entity to save URL in the object-oriented database, the Berkeley DB. 11/25/2014, Bing Li
 */

// Created: 11/23/2014, Bing Li
@Entity
public class URLEntity
{
	// The unique key that distinguishes from others. For an object to be persisted, it is required to design such a key. 11/25/2014, Bing Li
	@PrimaryKey
	private String key;

	// The URL. 11/25/2014, Bing Li
	private String url;
	// The updating period of the URL. 11/25/2014, Bing Li
	private long updatingPeriod;

	/*
	 * The empty constructor is required by the database, the Berkeley DB. 11/25/2014, Bing Li
	 */
	public URLEntity()
	{
		this.key = Constants.NO_URL_KEY;
		this.url = Constants.NO_URL;
		this.updatingPeriod = Constants.NO_UPDATING_PERIOD;
	}

	/*
	 * Initialize the entity. 11/25/2014, Bing Li
	 */
	public URLEntity(String key, String url, long updatingPeriod)
	{
		this.key = key;
		this.url = url;
		this.updatingPeriod = updatingPeriod;
	}
	
	/*
	 * The getter for the attribute of key. It is required by the Berkeley DB. 10/03/2014, Bing Li
	 */
	public String getKey()
	{
		return this.key;
	}
	
	/*
	 * The setter for the attribute of key. It is required by the Berkeley DB. 11/25/2014, Bing Li
	 */
	public void setKey(String key)
	{
		this.key = key;
	}
	
	/*
	 * The getter for the attribute of URL. It is required by the Berkeley DB. 11/25/2014, Bing Li
	 */
	public String getURL()
	{
		return this.url;
	}
	
	/*
	 * The setter for the attribute of URL. It is required by the Berkeley DB. 11/25/2014, Bing Li
	 */
	public void setURL(String url)
	{
		this.url = url;
	}
	
	/*
	 * The getter for the attribute of updating period. It is required by the Berkeley DB. 11/25/2014, Bing Li
	 */
	public long getUpdatingPeriod()
	{
		return this.updatingPeriod;
	}
	
	/*
	 * The setter for the attribute of updating period. It is required by the Berkeley DB. 11/25/2014, Bing Li
	 */
	public void setUpdatingPeriod(long updatingPeriod)
	{
		this.updatingPeriod = updatingPeriod;
	}
}
