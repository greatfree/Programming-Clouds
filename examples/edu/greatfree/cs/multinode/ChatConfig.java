package edu.greatfree.cs.multinode;

import org.greatfree.util.Tools;

// Created: 10/09/2018, Bing Li
public class ChatConfig
{
	public final static String CHAT_SERVER_ADDRESS = "127.0.0.1";

	public final static String CHAT_SERVER_KEY = Tools.generateUniqueKey();

	public final static String CHAT_REGISTRY_SERVER_KEY = Tools.generateUniqueKey();
	public final static String CHAT_REGISTRY_NAME = Tools.generateUniqueKey();

	public final static String CHAT_REGISTRY_PORT_KEY = Tools.generateUniqueKey();

	public final static int CHAT_SERVER_PORT = 8944;
	public final static int CHAT_ADMIN_PORT = 8943;
	public final static int CHAT_REGISTRY_PORT = 8942;

	public final static long CHAT_POLLING_DELAY = 0;
	public final static long CHAT_POLLING_PERIOD = 10000;

	public final static int STOP_CS_CHATTING_SERVER = 1;
	public final static int STOP_CHATTING_REGISTRY_SERVER = 2;

	public final static int SCHEDULER_THREAD_POOL_SIZE = 50;
	public final static long SCHEDULER_THREAD_POOL_KEEP_ALIVE_TIME = 5000;
	public final static long SCHEDULER_SHUTDOWN_TIMEOUT = 2000;

	public final static long SERVER_SHUTDOWN_TIMEOUT = 3000;
	public final static long TERMINATE_SLEEP = 2000;
}
