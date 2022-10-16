package org.greatfree.data;

/*
 * The class keeps constants of the testing sample code. 08/04/2014, Bing Li
 */

// Created: 08/04/2014, Bing Li
public class ServerConfig
{
//	public final static String SERVER_IP = "192.168.1.104";
	public final static String SERVER_IP = "127.0.0.1";
	public final static int SERVER_PORT = 8964;
	public final static int ADMIN_PORT = 8947;
	public final static int CLIENT_PORT = 8949;

	public final static int DISPATCHER_POOL_SIZE = 500;
	public final static long DISPATCHER_POOL_THREAD_POOL_ALIVE_TIME = 2000;

	public final static int LISTENING_THREAD_COUNT = 5;
	public final static int SINGLE_THREAD_COUNT = 1;
	public final static int LISTENER_THREAD_POOL_SIZE = 50;
	public final static long LISTENER_THREAD_ALIVE_TIME = 10000;
	
	public final static long CHECK_MESSAGE_TIMEOUT = 1000;
	/*
	 * The value is for testing the CPU usage. 04/21/2022, Bing Li
	 */
//	public final static long CHECK_MESSAGE_TIMEOUT = 100000000;
	
	public final static int MAX_SERVER_IO_COUNT = 500;

	public final static long TERMINATE_SLEEP = 2000;

//	public final static int REMOTE_SERVER_PORT = 8962;
	
//	public final static FreeClient NO_CLIENT = null;

	public final static int MY_SERVER = 1;

	public final static int MAX_CLIENT_LISTEN_THREAD_COUNT = 5;

//	public final static String ROOT_PATH = "/home/libing/GreatFreeLabs/Clouds/";
	public final static String ROOT_PATH = "/home/libing/Wind/";
	public final static String CONFIG_HOME = ServerConfig.ROOT_PATH + "Config/";
//	public final static String CACHE_HOME = ServerConfig.ROOT_PATH + "Cache/";
	public final static String CACHE_HOME = ServerConfig.ROOT_PATH + "Cache/";

//	public final static String COORDINATOR_ADDRESS = "192.168.0.113";
//	public final static String COORDINATOR_ADDRESS = "192.168.2.211";
//	public final static String COORDINATOR_ADDRESS = "192.168.0.117";
//	public final static String COORDINATOR_ADDRESS = "192.168.1.100";
//	public final static String COORDINATOR_ADDRESS = "192.168.1.107";
//	public final static String COORDINATOR_ADDRESS = "192.168.3.8";
	public final static String COORDINATOR_ADDRESS = "192.168.1.18";
//	public final static String COORDINATOR_ADDRESS = "192.168.3.9";
//	public final static String COORDINATOR_ADDRESS = "127.0.0.1";
//	public final static String COORDINATOR_ADDRESS = "192.168.88.115";
//	public final static String COORDINATOR_ADDRESS = "192.168.0.100";
	public final static int COORDINATOR_PORT = 8944;
	public final static int COORDINATOR_ADMIN_PORT = 8941;
	
//	public final static String TERMINAL_ADDRESS = "192.168.0.111";
//	public final static String TERMINAL_ADDRESS = "192.168.1.101";
//	public final static String TERMINAL_ADDRESS = "192.168.1.105";
//	public final static String TERMINAL_ADDRESS = "192.168.3.9";
//	public final static String TERMINAL_ADDRESS = "192.168.3.19";
//	public final static String TERMINAL_ADDRESS = "192.168.88.125";
//	public final static String TERMINAL_ADDRESS = "127.0.0.1";
//	public final static String TERMINAL_ADDRESS = "192.168.3.4";
	public final static String TERMINAL_ADDRESS = "192.168.1.18";
	public final static int TERMINAL_PORT = 8943;
	public final static int TERMINAL_ADMIN_PORT = 8942;
	
//	public final static String COORDINATOR_ADDRESS = "127.0.0.1";
	public final static int COORDINATOR_PORT_FOR_CRAWLER = 8963;
	public final static int COORDINATOR_PORT_FOR_MEMORY = 8965;
	public final static int COORDINATOR_PORT_FOR_ADMIN = 8951;
	public final static int COORDINATOR_PORT_FOR_SEARCH = 8950;
	public final static int CRAWL_SERVER_PORT = 8960;
	public final static int MEMORY_SERVER_PORT = 8961;
	public final static int SEARCH_CLIENT_PORT = 8948;
	public final static int COORDINATOR_DN_PORT = 8946;
	public final static int DN_PORT = 8945;

//	public final static long REQUEST_THREAD_WAIT_TIME = 1000;
	public final static long REQUEST_THREAD_WAIT_TIME = 5000;
	/*
	 * The value is for testing the CPU usage. 04/21/2022, Bing Li
	 */
//	public final static long REQUEST_THREAD_WAIT_TIME = 100000000;
//	public final static long NOTIFICATION_THREAD_WAIT_TIME = 1000;
	public final static long NOTIFICATION_THREAD_WAIT_TIME = 5000;
	/*
	 * The value is for testing the CPU usage. 04/21/2022, Bing Li
	 */
//	public final static long NOTIFICATION_THREAD_WAIT_TIME = 100000000;

//	public final static int REQUEST_DISPATCHER_POOL_SIZE = 50;
	public final static int REQUEST_DISPATCHER_POOL_SIZE = 100;
//	public final static long REQUEST_DISPATCHER_THREAD_ALIVE_TIME = 500;
//	public final static int REQUEST_QUEUE_SIZE = 500;
	public final static int REQUEST_QUEUE_SIZE = 20;
//	public final static int MAX_REQUEST_THREAD_SIZE = 50;
//	public final static long REQUEST_DISPATCHER_WAIT_TIME = 1000;
	public final static long REQUEST_DISPATCHER_WAIT_TIME = 5000;

	/*
	 * The value is for testing the CPU usage. 04/21/2022, Bing Li
	 */
//	public final static long REQUEST_DISPATCHER_WAIT_TIME = 1000000000;
//	public final static int REQUEST_DISPATCHER_WAIT_ROUND = 5;
//	public final static long REQUEST_DISPATCHER_IDLE_CHECK_DELAY = 2000;
	public final static long REQUEST_DISPATCHER_IDLE_CHECK_DELAY = 3000;
//	public final static long REQUEST_DISPATCHER_IDLE_CHECK_PERIOD = 2000;
	public final static long REQUEST_DISPATCHER_IDLE_CHECK_PERIOD = 3000;

//	public final static int NOTIFICATION_DISPATCHER_POOL_SIZE = 30;
	public final static int NOTIFICATION_DISPATCHER_POOL_SIZE = 100;
	public final static long NOTIFICATION_DISPATCHER_THREAD_ALIVE_TIME = 2000;

//	public final static int MAX_NOTIFICATION_TASK_SIZE = 500;
//	public final static int MAX_NOTIFICATION_TASK_SIZE = 50;
//	public final static int NOTIFICATION_QUEUE_SIZE = 300;
//	public final static int NOTIFICATION_QUEUE_SIZE = 50;
	public final static int NOTIFICATION_QUEUE_SIZE = 20;
//	public final static int MAX_NOTIFICATION_THREAD_SIZE = 10;

//	public final static long NOTIFICATION_DISPATCHER_WAIT_TIME = 1000;
	public final static long NOTIFICATION_DISPATCHER_WAIT_TIME = 5000;
//	public final static long NOTIFICATION_DISPATCHER_WAIT_TIME = 10000;

	/*
	 * The value is for testing the CPU usage. 04/21/2022, Bing Li
	 */
//	public final static long NOTIFICATION_DISPATCHER_WAIT_TIME = 1000000000;
//	public final static int NOTIFICATION_DISPATCHER_WAIT_ROUND = 5;
	public final static long NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY = 3000;
	public final static long NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD = 3000;

	public final static int CLIENT_POOL_SIZE = 500;
	public final static long CLIENT_IDLE_CHECK_DELAY = 3000;
//	public final static long CLIENT_IDLE_CHECK_PERIOD = 3000;
	public final static long CLIENT_IDLE_CHECK_PERIOD = 30000;
	public final static long CLIENT_MAX_IDLE_TIME = 3000;

	public final static long DISTRIBUTE_DATA_WAIT_TIME = 2000;
	public final static int MULTICASTOR_POOL_SIZE = 100;
	public final static long MULTICASTOR_POOL_WAIT_TIME = 1000;

	public final static int ROOT_MULTICAST_BRANCH_COUNT = 100;
	public final static int MULTICAST_BRANCH_COUNT = 16;

	public final static int SCHEDULER_POOL_SIZE = 50;
	public final static long SCHEDULER_KEEP_ALIVE_TIME = 5000;
	public final static long SCHEDULER_SHUTDOWN_TIMEOUT = 2000;

//	public final static int SHARED_THREAD_POOL_SIZE = 50;
	public final static int SHARED_THREAD_POOL_SIZE = 100;
	public final static long SHARED_THREAD_POOL_KEEP_ALIVE_TIME = 5000;
	public final static long SHARED_THREAD_POOL_SHUTDOWN_TIMEOUT = 3000;
	
//	public final static long SERVER_SHUTDOWN_TIMEOUT = 30000;
	public final static long SERVER_SHUTDOWN_TIMEOUT = 3000;
	
	public final static int TASK_SIZE_PER_THREAD = 1;
//	public final static long MR_WAIT_TIME = 100;
	public final static long MR_WAIT_TIME = 2000;
	public final static long MR_SHUTDOWN_TIME = 10000;
	
}
