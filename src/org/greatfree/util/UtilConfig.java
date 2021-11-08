package org.greatfree.util;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Timer;

import org.greatfree.client.FreeClient;
import org.greatfree.concurrency.mapreduce.Sequence;
import org.greatfree.server.PeerAccount;
import org.w3c.dom.NodeList;

/*
 * The class keeps relevant configurations and constants of the solution. 07/30/2014, Bing Li
 */

/*
 * Revision Log
 * 
 * INFINITE_WAIT_ROUND is added. 01/13/2016, Bing Li
 * 
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
	public final static int NO_PORT = -1;
	public final static String LOCAL_IP = "127.0.0.1";
	public final static int DEFAULT_REGISTRY_PORT = 8941;
	public final static String COLON = ":";
	public final static String SEMI_COLON = ";";

	public final static Timer NO_TIMER = null;
	public final static Date NO_TIME = null;

	public final static String NO_KEY = "";

	public final static long ONE_SECOND = 1000;

	public final static String NO_DIR = "";
	
	public final static FreeClient NO_CLIENT = null;

	public final static String NO_QUEUE_KEY = "";
	public final static int NO_QUEUE_SIZE = -1;
	public final static int NO_STACK_SIZE = -1;

	public final static int NO_LIST_SIZE = -1;

	public final static IPAddress NO_IPPORT = null;

	public final static long INIT_READ_WAIT_TIME = 2000;

	public final static String MERGE_SORT = "java.util.Arrays.useLegacyMergeSort";
	public final static String TRUE = "true";

	public final static HashMap<String, String> NO_NODES = null;
	public final static HashMap<String, IPAddress> NO_IPS = null;

	public final static List<String> NO_CHILDREN_KEYS = null;
	public final static Set<String> NO_NODE_KEYS = null;
	public final static Set<String> NO_KEYS = null;
	public final static String ROOT_KEY = "RootKey";
	public final static String LOCAL_KEY = "LocalKey";

	public static final String UTF_8 = "UTF-8";

	public static final NodeList NO_MULTI_RESULTS = null;
	public static final List<String> NO_STRINGS = null;
	
	public final static String TRIM_EXPRESSION = "^([\\W]+)<";
	public final static String LESS_THAN = "<";

	public static final int INFINITE_WAIT_ROUND = -1;

	public final static String OS_NAME = "os.name";
	public final static String WIN = "win";
	public final static String MAC = "mac";
	public final static String NIX = "nix";
	public final static String NUX = "nux";
	public final static String AIX = "aix";
	public final static String SUN_OS = "sunos";

	public final static String USER_HOME = "user.home";
	
	public final static int ZERO = 0;
	public final static int ONE = 1;
	
	public final static PeerAccount NO_PEER_ACCOUNT = null;
	public final static IPAddress NO_IP_ADDRESS = null;
	
	public final static String FORWARD_SLASH = "/";

	public final static int NO_COUNT = -1;
	public final static int INIT_INDEX = 0;
	
	public final static int ZERO_SIZE = 0;
	public final static int NO_INDEX = -1;
	
	public final static Sequence NO_MR_RESULT = null;
	
	public final static char CHAR_A_LOWERCASE = 'a';
	public final static char CHAR_A_UPPERCASE = 'A';
	public final static char CHAR_Z_UPPERCASE = 'Z';
	public final static int ALPHABET_SIZE = 26;
	public final static String COMMA = ",";
	public final static String BAR = "|";
	public final static String NEW_LINE = "\n";
	public final static String DOT = ".";
	public final static String TILDE = "~";
	public final static String UNDERSCORE = "_";
	
	public final static String REGISTRY_IP_CONFIG = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<GreatFree>\n\t<RegistryIP>127.0.0.1</RegistryIP>\n\t<RegistryPort>8941</RegistryPort>\n</GreatFree>\n";
	public final static String SELECT_REGISTRY_SERVER_IP = "/GreatFree/RegistryIP/text()";
	public final static String SELECT_REGISTRY_SERVER_PORT = "/GreatFree/RegistryPort/text()";
	
//	public final static String CONFIG_FILE = Env.freeHome + "config.xml";
	public final static String CONFIG_FILE = "config.xml";

	public final static String QUESTION_MARK = "?";
	public final static String AND_SYMBOL = "&";
	public final static String EQUAL_SIGN = "=";
}
