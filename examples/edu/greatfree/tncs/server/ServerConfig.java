package edu.greatfree.tncs.server;

// Created: 05/01/2019, Bing Li
class ServerConfig
{
	public final static int ECOMMERCE_SERVER_PORT = 8944;
	public final static int LISTENING_THREAD_COUNT = 5;

	public final static int DISPATCHER_THREAD_POOL_SIZE = 500;
	public final static long DISPATCHER_THREAD_POOL_KEEP_ALIVE_TIME = 2000;

	public final static int SCHEDULER_THREAD_POOL_SIZE = 50;
	public final static long SCHEDULER_THREAD_POOL_KEEP_ALIVE_TIME = 5000;
	public final static long SCHEDULER_SHUTDOWN_TIMEOUT = 2000;
	
	public final static long REQUEST_THREAD_WAIT_TIME = 5000;

	public final static int REQUEST_DISPATCHER_POOL_SIZE = 100;
	public final static long REQUEST_DISPATCHER_THREAD_ALIVE_TIME = 500;
	public final static int REQUEST_QUEUE_SIZE = 20;
	public final static long REQUEST_DISPATCHER_WAIT_TIME = 1000;
	public final static int REQUEST_DISPATCHER_WAIT_ROUND = 5;
	public final static long REQUEST_DISPATCHER_IDLE_CHECK_DELAY = 3000;
	public final static long REQUEST_DISPATCHER_IDLE_CHECK_PERIOD = 3000;
	
	public final static long NOTIFICATION_THREAD_WAIT_TIME = 1000;
}
