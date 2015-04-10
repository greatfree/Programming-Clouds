package com.greatfree.testing.data;

import com.greatfree.remote.FreeClient;

/*
 * The class keeps constants of the testing sample code. 08/04/2014, Bing Li
 */

// Created: 08/04/2014, Bing Li
public class ServerConfig
{
	public final static String SERVER_IP = "192.168.1.106";
	public final static int SERVER_PORT = 8964;
	public final static int CLIENT_PORT = 8949;
	
	public final static int DISPATCHER_POOL_SIZE = 500;
	public final static long DISPATCHER_POOL_THREAD_POOL_ALIVE_TIME = 2000;

	public final static int LISTENING_THREAD_COUNT = 5;
	public final static int LISTENER_THREAD_POOL_SIZE = 50;
	public final static long LISTENER_THREAD_ALIVE_TIME = 10000;
	
	public final static int MAX_SERVER_IO_COUNT = 500;

	public final static long TERMINATE_SLEEP = 2000;

	public final static int REMOTE_SERVER_PORT = 8962;
	
	public final static FreeClient NO_CLIENT = null;

	public final static int MY_SERVER = 1;

	public final static int MAX_CLIENT_LISTEN_THREAD_COUNT = 5;

	public final static String ROOT_PATH = "/home/libing/GreatFreeLabs/";
	public final static String CONFIG_HOME = ServerConfig.ROOT_PATH + "Config/";

	public final static String COORDINATOR_ADDRESS = "192.168.1.113";
	public final static int COORDINATOR_PORT_FOR_CRAWLER = 8963;
	public final static int COORDINATOR_PORT_FOR_MEMORY = 8965;
	public final static int COORDINATOR_PORT_FOR_ADMIN = 8951;
	public final static int COORDINATOR_PORT_FOR_SEARCH = 8950;
	public final static int CRAWL_SERVER_PORT = 8960;
	public final static int MEMORY_SERVER_PORT = 8961;
	public final static int SEARCH_CLIENT_PORT = 8948;

	public final static long RETRIEVE_THREAD_WAIT_TIME = 1000;
	public final static long NOTIFICATION_THREAD_WAIT_TIME = 1000;

	public final static int REQUEST_DISPATCHER_POOL_SIZE = 50;
	public final static long REQUEST_DISPATCHER_THREAD_ALIVE_TIME = 500;
	public final static int MAX_REQUEST_TASK_SIZE = 500;
	public final static int MAX_REQUEST_THREAD_SIZE = 50;
	public final static long REQUEST_DISPATCHER_WAIT_TIME = 1000;
	public final static long REQUEST_DISPATCHER_IDLE_CHECK_DELAY = 2000;
	public final static long REQUEST_DISPATCHER_IDLE_CHECK_PERIOD = 2000;

	public final static int NOTIFICATION_DISPATCHER_POOL_SIZE = 30;
	public final static long NOTIFICATION_DISPATCHER_THREAD_ALIVE_TIME = 2000;

	public final static int MAX_NOTIFICATION_TASK_SIZE = 500;
	public final static int MAX_NOTIFICATION_THREAD_SIZE = 10;

	public final static long NOTIFICATION_DISPATCHER_WAIT_TIME = 1000;
	public final static long NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY = 3000;
	public final static long NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD = 3000;

	public final static int CLIENT_POOL_SIZE = 500;
	public final static long CLIENT_IDLE_CHECK_DELAY = 3000;
	public final static long CLIENT_IDLE_CHECK_PERIOD = 3000;
	public final static long CLIENT_MAX_IDLE_TIME = 3000;

	public final static long DISTRIBUTE_DATA_WAIT_TIME = 2000;
	public final static int MULTICASTOR_POOL_SIZE = 100;
	public final static long MULTICASTOR_POOL_WAIT_TIME = 1000;

	public final static int ROOT_MULTICAST_BRANCH_COUNT = 100;
	public final static int MULTICAST_BRANCH_COUNT = 16;

}
