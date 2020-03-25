package org.greatfree.cache.db;

// Created: 03/15/2016, Bing Li
public class CachedDBConfig
{
	public final static long DB_CACHE_SIZE = 1000000;
	public final static long LOCK_TIME_OUT = 0;

	public final static int DB_POOL_SIZE = 100;

	public final static String KEY = "key";
	public final static String ACCESSED_TIME = "accessedTime";
	public final static String STACK_KEY = "stackKey";
	public final static String INDEX = "index";

	public final static String CACHED_STRING_KEYS_STORE = "StringCachedKeysStore";
	public final static String CACHED_INTEGER_KEYS_STORE = "IntegerCachedKeysStore";

	public final static IntegerKeyDB NO_INTEGER_KEY_DB = null;
	public final static StringKeyDB NO_STRING_KEY_DB = null;

	public final static int DELTA_TIMES = 2;
}
