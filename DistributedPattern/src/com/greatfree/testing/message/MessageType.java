package com.greatfree.testing.message;

/*
 * The class contains the identification of each type of messages. 11/28/2014, Bing Li
 */

// Created: 09/20/2014, Bing Li
public class MessageType
{
	public final static int NODE_KEY_NOTIFICATION = 0;
	public final static int SIGN_UP_REQUEST = 1;
	public final static int SIGN_UP_RESPONSE = 2;
	public final static int INIT_READ_NOTIFICATION = 3;
	public final static int INIT_READ_FEEDBACK_NOTIFICATION = 4;
	public final static int ONLINE_NOTIFICATION = 5;
	public final static int REGISTER_CLIENT_NOTIFICATION = 6;
	public final static int UNREGISTER_CLIENT_NOTIFICATION = 7;
	public final static int CRAWLED_LINKS_NOTIFICATION = 8;
	public final static int REGISTER_CRAWL_SERVER_NOTIFICATION = 9;
	public final static int UNREGISTER_CRAWL_SERVER_NOTIFICATION = 10;
	public final static int CRAWL_LOAD_NOTIFICATION = 11;
	public final static int START_CRAWL_MULTI_NOTIFICATION = 12;
	public final static int SHUTDOWN_CRAWL_SERVER_NOTIFICATION = 13;
	public final static int SHUTDOWN_MEMORY_SERVER_NOTIFICATION = 14;
	public final static int SHUTDOWN_COORDINATOR_SERVER_NOTIFICATION = 15;
	public final static int STOP_CRAWL_MULTI_NOTIFICATION = 16;
	public final static int STOP_MEMORY_SERVER_NOTIFICATION = 17;
	public final static int REGISTER_MEMORY_SERVER_NOTIFICATION = 18;
	public final static int UNREGISTER_MEMORY_SERVER_NOTIFICATION = 19;
	public final static int ADD_CRAWLED_LINK_NOTIFICATION = 20;
	public final static int IS_PUBLISHER_EXISTED_REQUEST = 21;
	public final static int IS_PUBLISHER_EXISTED_RESPONSE = 22;
	public final static int SEARCH_KEYWORD_REQUEST = 23;
	public final static int SEARCH_KEYWORD_RESPONSE = 24;
	public final static int IS_PUBLISHER_EXISTED_ANYCAST_REQUEST = 25;
	public final static int IS_PUBLISHER_EXISTED_ANYCAST_RESPONSE = 26;
	public final static int SEARCH_KEYWORD_BROADCAST_REQUEST = 27;
	public final static int SEARCH_KEYWORD_BROADCAST_RESPONSE = 28;
}
