package com.greatfree.testing.memory;

/*
 * The class contains the constants and configurations for the memory server. 11/28/2014, Bing Li
 */

// Created: 11/28/2014, Bing Li
public class MemConfig
{
	public final static int MEMORY_LISTENER_THREAD_POOL_SIZE = 100;
	public final static long MEMORY_LISTENER_THREAD_ALIVE_TIME = 10000;

	public final static int SUB_CLIENT_POOL_SIZE = 500;
	public final static long SUB_CLIENT_IDLE_CHECK_DELAY = 3000;
	public final static long SUB_CLIENT_IDLE_CHECK_PERIOD = 3000;
	public final static long SUB_CLIENT_MAX_IDLE_TIME = 3000;
	
	public final static int MEMORY_CACHE_SIZE = 10000;
}
