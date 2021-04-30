package edu.chainnet.sc.collaborator.child.db;

import org.greatfree.util.Tools;
import org.greatfree.util.UtilConfig;

import edu.chainnet.sc.SCConfig;

// Created: 10/18/2020, Bing Li
public class DBConfig
{
	public final static long DB_CACHE_SIZE = 1000000;
	public final static long LOCK_TIME_OUT = 1000;

	public final static String BCNODE_STORE = "BCNodeStore";
	public final static String DSNODE_STORE = "DSNodeStore";
	public final static String HISTORY_BCNODE_STORE = "HistoryBCNodeStore";
	public final static String HISTORY_DSNODE_STORE = "HistoryDSNodeStore";

	public final static String SERVICE_NAME = "serviceName";
	public final static String NODE_TYPE = "nodeType";
	public final static String VERSION = "version";
	public final static String NODE_ID = "nodeID";
	
	public final static int SC_NODE = 0;
	public final static int ON_NODE = 1;
	public final static int DL_NODE = 2;
	public final static int BC_NODE = 3;

	/*
	public final static String BCNODE_DB_PATH = SCConfig.SC_HOME + "BCNodeDB";
	public final static String DSNODE_DB_PATH = SCConfig.SC_HOME + "DSNodeDB";
	public final static String HISTORY_BCNODE_DB_PATH = SCConfig.SC_HOME + "HistoryBCNodeDB";
	public final static String HISTORY_DSNODE_DB_PATH = SCConfig.SC_HOME + "HistoryDSNodeDB";
	*/

	public final static String DB_HOME = SCConfig.SC_HOME + "DB/";
	public final static String BCNODE_DB_PATH = "BCNodeDB/";
	public final static String DSNODE_DB_PATH = "DSNodeDB/";
	public final static String HISTORY_BCNODE_DB_PATH = "HistoryBCNodeDB/";
	public final static String HISTORY_DSNODE_DB_PATH = "HistoryDSNodeDB/";
	
	public static String getHistoryKey(String nodeKey, int version)
	{
		return Tools.getHash(nodeKey + UtilConfig.COLON + version);
	}

	/*
	public static String getHistoryDSKey(String nodeKey, int version, int nodeType)
	{
		return Tools.getHash(nodeKey + UtilConfig.COLON + version + UtilConfig.COLON + nodeType);
	}
	*/
}
