package org.greatfree.cache.distributed;

// Created: 02/25/2018, Bing Li
public class FetchConfig
{
	public final static int POSTFETCH_RESOURCE_BY_KEY = 0;
//	public final static int POSTFETCH_ALL_RESOURCES_BY_CACHE_KEY = 1;
	public final static int FETCH_RESOURCE_BY_INDEX = 2;
	// To avoid confusions, retrieving by top is merged with retrieving by range. 07/16/2018, Bing Li
//	public final static int FETCH_RESOURCES_BY_TOP = 3;
	public final static int FETCH_RESOURCES_BY_RANGE = 4;
}
