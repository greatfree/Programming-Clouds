package com.greatfree.testing.coordinator;

import com.greatfree.util.XMLReader;

/*
 * This is a configuration file that contains the predefined information about the distributed system. 11/25/2014, Bing Li
 */

// Created: 11/25/2014, Bing Li
public class Profile
{
	// The crawler count to participate the system. 11/25/2014, Bing Li
	private int crawlServerCount;
	// The memory server count to participate the system. 11/28/2014, Bing Li
	private int memoryServerCount;
	
	private Profile()
	{
	}

	/*
	 * A singleton definition. 11/25/2014, Bing Li
	 */
	private static Profile instance = new Profile();
	
	public static Profile CONFIG()
	{
		if (instance == null)
		{
			instance = new Profile();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	/*
	 * Dispose the profile. 11/25/2014, Bing Li
	 */
	public void dispose()
	{
	}

	/*
	 * Initialize the profile. 11/25/2014, Bing Li
	 */
	public void init(String path)
	{
		// An XML reader is defined to retrieved predefined information from the XML file, which is managed by the administrator manually. 11/25/2014, Bing Li
		XMLReader reader = new XMLReader(path, true);
		this.crawlServerCount = new Integer(reader.read(CoorConfig.SELECT_CRAWLSERVER_COUNT));
		this.memoryServerCount = new Integer(reader.read(CoorConfig.SELECT_MEMORYSERVER_COUNT));
		reader.close();
	}

	/*
	 * Get the count of the crawlers that participate the crawling cluster. 11/25/2014, Bing Li
	 */
	public int getCrawlServerCount()
	{
		return this.crawlServerCount;
	}
	
	/*
	 * Get the count of the memory servers that participate the crawling cluster. 11/28/2014, Bing Li
	 */
	public int getMemoryServerCount()
	{
		return this.memoryServerCount;
	}
}
