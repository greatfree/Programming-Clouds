package org.greatfree.testing.concurrency.pingpong;

/**
 * 
 * @author libing
 * 
 * 06/30/2022
 *
 */
final class ServerConfig
{
	public final static int SERVER_PORT = 8000;
	public final static int LISTENING_THREAD_COUNT = 5;
	public final static int MAX_IO_COUNT = 500;

	public final static int SERVER_THREAD_POOL_SIZE = 500;
	public final static long SERVER_THREAD_POOL_KEEP_ALIVE_TIME = 10000;
	public final static int SCHEDULER_THREAD_POOL_SIZE = 100;
	public final static long SCHEDULER_THREAD_POOL_KEEP_ALIVE_TIME = 3000;

	public final static int NOTIFICATION_DISPATCHER_POOL_SIZE = 500;
	public final static int NOTIFICATION_QUEUE_SIZE = 50;
	public final static long NOTIFICATION_DISPATCHER_WAIT_TIME = 1000;
	public final static long NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY = 2000;
	public final static long NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD = 2000;
	public final static long NOTIFICATION_THREAD_WAIT_TIME = 2000;

	public final static int REQUEST_DISPATCHER_POOL_SIZE = 500;
	public final static int REQUEST_QUEUE_SIZE = 50;
	public final static long REQUEST_DISPATCHER_WAIT_TIME = 1000;
	public final static long REQUEST_DISPATCHER_IDLE_CHECK_DELAY = 2000;
	public final static long REQUEST_DISPATCHER_IDLE_CHECK_PERIOD = 2000;
	public final static long REQUEST_THREAD_WAIT_TIME = 2000;

	public final static long SERVER_SHUTDOWN_TIMEOUT = 3000;
}
