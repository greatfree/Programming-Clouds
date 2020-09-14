package org.greatfree.cluster;

import java.util.Set;

import org.greatfree.message.multicast.container.Response;

// Created: 09/23/2018, Bing Li
public class ClusterConfig
{
	public final static Response NO_RESPONSE = null;

	public final static int ANYCAST_REQUEST_LEAST_COUNT = 1;
	public final static int NO_REPLICAS = 0;
	public final static int NO_PARTITION_INDEX = -1;
	
	public final static Set<String> NO_CHILDREN_KEYS = null;
}
