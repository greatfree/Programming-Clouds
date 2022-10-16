package org.greatfree.testing.concurrency.pingpong;

/**
 * 
 * @author libing
 * 
 * 08/01/2022
 *
 */
final class NotifierPoolConfig
{
	public final static int THREAD_POOL_SIZE = 50;
	public final static long THREAD_POOL_KEEP_ALIVE_TIME = 10000;

//	public final static int QUEUE_SIZE = 100;
	public final static int QUEUE_SIZE = 1;
	public final static int NOTIFIER_SIZE = 100;
	public final static int POOLING_WAIT_TIME = 1000;
	public final static int NOTIFIER_WAIT_TIME = 1000;
	public final static long IDLE_CHECK_DELAY = 3000;
	public final static long IDLE_CHECK_PERIOD = 3000;
	public final static int SCHEDULER_POOL_SIZE = 50;
	public final static long SCHEDULER_KEEP_ALIVE_TIME = 5000;
}
