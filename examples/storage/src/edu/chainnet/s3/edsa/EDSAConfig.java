package edu.chainnet.s3.edsa;

/*
 * The cluster is used to encode/decode data blocks. 07/11/2020, Bing Li
 */

// Created: 07/09/2020, Bing Li
public class EDSAConfig
{
	public final static String PUBLIC_KEY = "";
	public final static String PRIVATE_KEY = "";
	
	public final static String EDID_PREFIX = "EDID";
	
	public final static long ENCODING_THREAD_TIMEOUT = 2000;

	public final static int ENCODING_DISPATCHER_POOL_SIZE = 100;
	public final static long ENCODING_DISPATCHER_THREAD_ALIVE_TIME = 2000;
	public final static int ENCODING_QUEUE_SIZE = 20;
	public final static long ENCODING_DISPATCHER_WAIT_TIME = 10000;
	public final static int ENCODING_DISPATCHER_WAIT_ROUND = 5;
	public final static long ENCODING_DISPATCHER_IDLE_CHECK_DELAY = 3000;
	public final static long ENCODING_DISPATCHER_IDLE_CHECK_PERIOD = 3000;

	public final static int SCHEDULER_THREAD_POOL_SIZE = 50;
	public final static long SCHEDULER_THREAD_POOL_KEEP_ALIVE_TIME = 5000;
	public final static long SCHEDULER_SHUTDOWN_TIMEOUT = 2000;
	
	public final static int ENCODING_THREAD_POOL_SIZE = 100;
	public final static long ENCODING_THREAD_POOL_ALIVE_TIME = 5000;
	public final static long ENCODING_THREAD_POOL_SHUTDOWN_TIMEOUT = 2000;
}
