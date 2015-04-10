package com.greatfree.testing.data;

/*
 * The class contains all of constants at the client end. 09/21/2014, Bing Li
 */

// Created: 09/21/2014, Bing Li
public class ClientConfig
{
	public final static int CLIENT_POOL_SIZE = 500;
	public final static String USERNAME = "greatfree";
	public final static String PASSWORD = "19890604";
	public final static int CLIENT_LISTENER_THREAD_POOL_SIZE = 80;
	public final static long CLIENT_LISTENER_THREAD_ALIVE_TIME = 10000;

	public final static long CLIENT_IDLE_CHECK_DELAY = 3000;
	public final static long CLIENT_IDLE_CHECK_PERIOD = 3000;
	public final static long CLIENT_MAX_IDLE_TIME = 3000;

	public final static int NOTIFICATION_DISPATCHER_POOL_SIZE = 30;
	public final static long NOTIFICATION_DISPATCHER_THREAD_ALIVE_TIME = 2000;

	public final static int MAX_NOTIFICATION_TASK_SIZE = 500;
	public final static int MAX_NOTIFICATION_THREAD_SIZE = 10;

	public final static long NOTIFICATION_DISPATCHER_WAIT_TIME = 1000;
	public final static long NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY = 3000;
	public final static long NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD = 3000;

	public final static int TIME_TO_WAIT_FOR_THREAD_TO_DIE = 10;

	public final static int SERVER_CLIENT_POOL_SIZE = 10;

	public final static int EVENTER_THREAD_POOL_SIZE = 10;
	public final static long EVENTER_THREAD_POOL_ALIVE_TIME = 10000;

	public final static int SERVER_IO_POOL_SIZE = 100;

	public final static int MULTICASTOR_POOL_SIZE = 100;
	public final static long MULTICASTOR_WAIT_TIME = 1000;
	public static final int MULTICAST_BRANCH_COUNT = 16;

	public final static int EVENT_QUEUE_SIZE = 100;
	public final static int EVENTER_SIZE = 10;
	public final static int EVENTING_WAIT_TIME = 1000;
	public final static int EVENTER_WAIT_TIME = 1000;
	public final static long EVENT_IDLE_CHECK_DELAY = 3000;
	public final static long EVENT_IDLE_CHECK_PERIOD = 3000;
}
