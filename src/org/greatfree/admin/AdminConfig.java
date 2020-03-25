package org.greatfree.admin;

import java.util.ArrayList;
import java.util.List;

import org.greatfree.util.Tools;

/*
 * The configuration contains the constants of the administration program. 11/27/2014, Bing Li
 */

// Created: 11/27/2014, Bing Li
public class AdminConfig
{
	public final static int NO_OPTION = -1;
	public final static int STOP_CLIENT = 1;
	public final static int STOP_SERVER = 2;
	public final static int STOP_CRAWLSERVER = 3;
	public final static int STOP_MSERVER = 4;
	public final static int STOP_CSERVER = 5;
//	public final static int COORDINATOR = 6;
	public final static String COORDINATOR = Tools.generateUniqueKey();
//	public final static int DN = 7;
	public final static String DN = Tools.generateUniqueKey();
	public final static int END = 0;
	public final static int MANSERVER_CLIENT_POOL_SIZE = 50;
	
//	public final static int SERVER_ID = 0;
	public final static String SERVER_ID = Tools.generateUniqueKey();
//	public final static int CLIENT_ID = 1;
	public final static String CLIENT_ID = Tools.generateUniqueKey();

//	public final static List<Integer> SERVER_IDs = new ArrayList<Integer>();
	public final static List<String> SERVER_IDs = new ArrayList<String>();
	
	public final static long THREAD_POOL_SHUTDOWN_TIMEOUT = 2000;

	/*
	 * Keep all of the server IDs into a list. 02/06/2016, Bing Li
	 */
//	public static List<Integer> getServerIDs()
	public static List<String> getServerIDs()
	{
		SERVER_IDs.add(AdminConfig.SERVER_ID);
		SERVER_IDs.add(AdminConfig.CLIENT_ID);
		return SERVER_IDs;
	}
	
//	public static List<Integer> getClusterIDs()
	public static List<String> getClusterIDs()
	{
		SERVER_IDs.add(AdminConfig.COORDINATOR);
		SERVER_IDs.add(AdminConfig.DN);
		SERVER_IDs.add(AdminConfig.CLIENT_ID);
		return SERVER_IDs;
	}
}
