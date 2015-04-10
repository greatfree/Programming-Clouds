package com.greatfree.testing.db;

import com.greatfree.testing.data.ServerConfig;

/*
 * It keeps some configuration constants for the object-oriented database, the Berkeley DB. 11/03/2014, Bing Li
 */

// Created: 11/03/2014, Bing Li
public class DBConfig
{
	public static final String DB_HOME = ServerConfig.ROOT_PATH + "DB/";

	public final static long DB_CACHE_SIZE = 1000000;
	public final static long LOCK_TIME_OUT = 0;
	public final static int DB_POOL_SIZE = 100;

	public final static String NODE_STORE = "NodeStore";
	public final static NodeDB NO_NODE_DB = null;
	public final static String NODE_DB_PATH = DBConfig.DB_HOME + "NodeDB/";
	
	public final static String URL_STORE = "URLStore";
	public final static URLDB NO_URL_DB = null;
	public final static String URL_DB_PATH = DBConfig.DB_HOME + "URLDB/";
}
