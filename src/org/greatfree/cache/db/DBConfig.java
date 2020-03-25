package org.greatfree.cache.db;

import org.greatfree.cache.factory.SortedListIndexes;
import org.greatfree.cache.factory.TimingListIndexes;
import org.greatfree.data.ServerConfig;
import org.greatfree.testing.db.NodeDB;
import org.greatfree.testing.db.URLDB;

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
	
	public final static String POINTING_LIST_STORE = "PointingListStore";
	public final static String LIST_KEY_STORE = "ListKeyStore";
	public final static String TIMING_LIST_STORE = "TimingListStore";
	public final static String SORTED_LIST_INDEX_STORE = "PointingListIndexStore";
	public final static String QUEUE_INDEX_STORE = "QueueIndexStore";
	public final static String MAP_KEYS_STORE = "MapKeysStore";
	public final static String TIMING_LIST_INDEX_STORE = "TimingListIndexStore";
	public final static String LIST_INDEX_STORE = "ListIndexStore";

	public final static SortedListEntity NO_POINTING_LIST_ENTITY = null;
	public final static ListKeyEntity NO_LIST_KEY_ENTITY = null;
	public final static TimingListEntity NO_TIMING_LIST_ENTITY = null;
	public final static SortedListIndexEntity NO_SORTED_LIST_INDEX_ENTITY = null;
	public final static LinearIndexEntity NO_QUEUE_INDEX_ENTITY = null;
	public final static MapKeysEntity NO_MAP_KEYS_ENTITY = null;
	public final static TimingListIndexEntity NO_TIMING_LIST_INDEX_ENTITY = null;
	public final static ListIndexEntity NO_LIST_INDEX_ENTITY = null;

	public final static SortedListIndexes NO_SORTED_LIST_INDEXES = null;
	public final static TimingListIndexes NO_TIMING_LIST_INDEXES = null;
	
	public final static String MAP_KEY = "mapKey";
}
