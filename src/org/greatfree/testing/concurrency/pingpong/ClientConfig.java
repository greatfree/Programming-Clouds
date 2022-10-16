package org.greatfree.testing.concurrency.pingpong;

import java.util.Scanner;

import org.greatfree.util.Tools;

/**
 * 
 * @author libing
 * 
 * 05/01/2022
 *
 */
final class ClientConfig
{
	public final static String CLIENT_ID = Tools.generateUniqueKey();
	public final static int CLIENT_READER_POOL_SIZE = 10;
	
	public final static int CLIENT_EVENTER_POOL_SIZE = 10;
	public final static long CLIENT_IDLE_CHECK_DELAY = 3000;
	public final static long CLIENT_IDLE_CHECK_PERIOD = 10000;
	public final static long CLIENT_MAX_IDLE_TIME = 50000;

	public final static Scanner INPUT = new Scanner(System.in);
	
	public final static int SCHEDULER_POOL_SIZE = 5;
	public final static long SCHEDULER_KEEP_ALIVE_TIME = 50000;
	public final static long CHAT_POLLING_DELAY = 2000;
	public final static long CHAT_POLLING_PERIOD = 10000;
	public final static long SCHEDULER_SHUTDOWN_TIMEOUT = 2000;
}

