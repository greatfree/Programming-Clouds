package org.greatfree.testing.cluster.coordinator;

import org.greatfree.testing.coordinator.CoorConfig;

/*
 * This is a configuration file that contains the predefined information about the distributed system. 11/25/2014, Bing Li
 */

// Created: 11/22/2016, Bing Li
public class Profile
{
	// The cluster server count to participate the system. 11/28/2014, Bing Li
	private int clusterServerCount;
	
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

	public void init()
	{
		this.clusterServerCount = CoorConfig.CLUSTER_SERVER_COUNT;
	}

	public int getClusterServerCount()
	{
		return this.clusterServerCount;
	}
}
