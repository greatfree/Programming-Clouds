package com.greatfree.util;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Timer;

import org.w3c.dom.NodeList;

import com.greatfree.remote.FreeClient;
import com.greatfree.remote.IPPort;

/*
 * The class keeps relevant configurations and constants of the solution. 07/30/2014, Bing Li
 */

// Created: 07/17/2014, Bing Li
public class UtilConfig
{
	public final static String A = "a";
	public final static String PRIVATE_KEY = "06/04/1989";
	public final static int ADDTIONAL_THREAD_POOL_SIZE = 0;
	public final static String HMAC_MD5 = "HmacMD5";
	public final static String EMPTY_STRING = "";
	public final static String HEX_DIGIT_CHARS = "0123456789abcdef";
	
	public final static int NO_TYPE = 0;
	public final static String NO_IP = "";

	public final static Timer NO_TIMER = null;
	public final static Date NO_TIME = null;

	public final static String NO_KEY = "";

	public final static long ONE_SECOND = 1000;

	public final static String NO_DIR = "";
	
	public final static FreeClient NO_CLIENT = null;

	public final static String NO_QUEUE_KEY = "";
	public final static int NO_QUEUE_SIZE = -1;

	public final static IPPort NO_IPPORT = null;

	public final static long INIT_READ_WAIT_TIME = 2000;

	public final static String MERGE_SORT = "java.util.Arrays.useLegacyMergeSort";
	public final static String TRUE = "true";

	public final static HashMap<String, String> NO_NODES = null;

	public final static List<String> NO_CHILDREN_KEYS = null;
	public final static Set<String> NO_NODE_KEYS = null;
	public final static String ROOT_KEY = "RootKey";
	public final static String LOCAL_KEY = "LocalKey";

	public static final String UTF_8 = "UTF-8";

	public static final NodeList NO_MULTI_RESULTS = null;
	public static final List<String> NO_STRINGS = null;
	
	public final static String TRIM_EXPRESSION = "^([\\W]+)<";
	public final static String LESS_THAN = "<";
}
