package edu.greatfree.p2p;

import org.greatfree.util.Tools;

/*
 * The class is the entry to the server process. 08/22/2014, Bing Li
 */

// Created: 04/15/2017, Bing Li
public class RegistryConfig
{
//	public final static String PEER_REGISTRY_ADDRESS = "192.168.0.100";
//	public final static String PEER_REGISTRY_ADDRESS = "192.168.0.117";
//	public final static String PEER_REGISTRY_ADDRESS = "192.168.0.113";
	public final static String PEER_REGISTRY_ADDRESS = "127.0.0.1";
//	public final static String PEER_REGISTRY_ADDRESS = "192.168.0.111";
//	public final static String PEER_REGISTRY_ADDRESS = "192.168.88.105";
//	public final static String PEER_REGISTRY_ADDRESS = "192.168.2.211";
	
	public final static String PEER_REGISTRY_KEY = Tools.generateUniqueKey();
	public final static String PEER_ADMIN_PORT_KEY = Tools.generateUniqueKey();
	public final static String REGISTRY_ADMIN_PORT_KEY = Tools.generateUniqueKey();

	public final static int PEER_REGISTRY_PORT = 8941;
	
	public final static int DISPATCHER_THREAD_POOL_SIZE = 500;
	public final static long DISPATCHER_THREAD_POOL_KEEP_ALIVE_TIME = 2000;
	
//	public final static int CLIENT_POOL_SIZE = 500;
	public final static int CLIENT_POOL_SIZE = 100;
	public final static long CLIENT_IDLE_CHECK_DELAY = 3000;
	public final static long CLIENT_IDLE_CHECK_PERIOD = 5000;
	public final static long CLIENT_MAX_IDLE_TIME = 3000;

	public final static int EVENTER_THREAD_POOL_SIZE = 10;
	public final static long EVENTER_THREAD_POOL_ALIVE_TIME = 10000;
	
	public final static int ASYNC_EVENT_QUEUE_SIZE = 100;
//	public final static int ASYNC_EVENTER_SIZE = 10;
	public final static int ASYNC_EVENTER_SIZE = 50;
	public final static int ASYNC_EVENTING_WAIT_TIME = 1000;
	public final static int ASYNC_EVENTER_WAIT_TIME = 1000;
	public final static int ASYNC_EVENTER_WAIT_ROUND = 5;
	public final static long ASYNC_EVENT_IDLE_CHECK_DELAY = 3000;
	public final static long ASYNC_EVENT_IDLE_CHECK_PERIOD = 3000;

	public final static int SCHEDULER_THREAD_POOL_SIZE = 50;
	public final static long SCHEDULER_THREAD_POOL_KEEP_ALIVE_TIME = 5000;
	public final static long SCHEDULER_SHUTDOWN_TIMEOUT = 2000;
	
//	public final static int SYNC_EVENTER_CLIENT_SIZE = 100;
//	public final static int READER_CLIENT_SIZE = 100;
	public final static int READER_CLIENT_SIZE = 500;
	public final static long SYNC_EVENTER_IDLE_CHECK_DELAY = 4000;
	public final static long SYNC_EVENTER_IDLE_CHECK_PERIOD = 5000;
	public final static long SYNC_EVENTER_MAX_IDLE_TIME = 3000;
	public final static int CLIENT_THREAD_POOL_SIZE = 100;
	public final static long CLIENT_THREAD_KEEP_ALIVE_TIME = 3000;

	public final static long THREAD_POOL_SHUTDOWN_TIMEOUT = 2000;
}
