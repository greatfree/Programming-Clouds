package org.greatfree.framework.cluster.multicast.client;

/**
 * 
 * @author libing
 * 
 * 10/02/2022
 *
 */
final class ClusterMenu
{
	public final static String TAB = "\t";
	public final static String MENU_HEAD = "\n========== Menu Head ===========";

	public final static String BROADCAST_NOTIFICATION = TAB + "1) Broadcast a notification";
	public final static String ANYCAST_NOTIFICATION = TAB + "2) Anycast a notification";
	public final static String UNICAST_NOTIFICATION = TAB + "3) Unicast a notification";
	public final static String BROADCAST_REQUEST = TAB + "4) Broadcast a request";
	public final static String ANYCAST_REQUEST = TAB + "5) Anycast a request";
	public final static String UNICAST_REQUEST = TAB + "6) Unicast a request";

	public final static String INTER_BROADCAST_NOTIFICATION = TAB + "7) Inter-Broadcast a notification";
	public final static String INTER_ANYCAST_NOTIFICATION = TAB + "8) Inter-Anycast a notification";
	public final static String INTER_UNICAST_NOTIFICATION = TAB + "9) Inter-Unicast a notification";
	public final static String INTER_BROADCAST_REQUEST = TAB + "10) Inter-Broadcast a request";
	public final static String INTER_ANYCAST_REQUEST = TAB + "11) Inter-Anycast a request";
	public final static String INTER_UNICAST_REQUEST = TAB + "12) Inter-Unicast a request";

	public final static String QUIT = TAB + "0) Quit";
	public final static String MENU_TAIL = "========== Menu Tail ===========\n";
	public final static String INPUT_PROMPT = "Input an option:";
	public final static String WRONG_OPTION = "Wrong option!";
}

