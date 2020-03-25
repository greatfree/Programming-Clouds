package org.greatfree.concurrency.threading;

import java.util.Set;

import org.greatfree.util.UtilConfig;

// Created: 09/11/2019, Bing Li
public class ThreadConfig
{
	public final static String SLAVE = "Slave";
	public final static String MASTER = "Master";
	public final static String THREAD = "Thread";
	public final static int THREAD_PORT = 8944;
	public final static int WRONG_NOTIFICATION_TYPE = -1;
	public final static String WRONG_NOTIFICATION_KEY = "";
	
	public final static long TIMEOUT = 1000;
	public final static String NO_THREAD_KEY = UtilConfig.EMPTY_STRING;
	public final static Set<String> NO_THREAD_KEYS = null;
	public final static long READ_TIMEOUT = 5000;
	
	public final static Player NO_PLAYER = null;
	public final static Set<Player> NO_PLAYERS = null;
}
