package org.greatfree.testing.coordinator;

import org.greatfree.data.ServerConfig;

/*
 * The class contains some configurations for the coordinator. 11/30/2014, Bing Li
 */

// Created: 11/24/2014, Bing Li
public class CoorConfig
{
	public final static String CSERVER_CONFIG = ServerConfig.CONFIG_HOME + "CServerConfig.xml";

	public final static int CSERVER_CLIENT_POOL_SIZE = 500;
	public final static long CSERVER_CLIENT_IDLE_CHECK_DELAY = 3000;
	public final static long CSERVER_CLIENT_IDLE_CHECK_PERIOD = 3000;
	public final static long CSERVER_CLIENT_MAX_IDLE_TIME = 6000;

	public final static String SELECT_CRAWLSERVER_COUNT = "/CConfig/CrawlServerCount/text()";
	public final static String SELECT_MEMORYSERVER_COUNT = "/CConfig/MemoryServerCount/text()";

	public final static long ANYCAST_REQUEST_WAIT_TIME = 5000;
	public final static long BROADCAST_REQUEST_WAIT_TIME = 5000;

	public final static int AUTHORITY_ANYCAST_READER_POOL_SIZE = 100;
	public final static int AUTHORITY_ANYCAST_READER_POOL_WAIT_TIME = 1000;

	public final static int BROADCAST_READER_POOL_SIZE = 100;
	public final static int BROADCAST_READER_POOL_WAIT_TIME = 1000;
	
	public final static int CLUSTER_SERVER_COUNT = 2;
}
