package org.greatfree.framework.container.multicast.client;

/**
 * 
 * @author libing
 * 
 * 05/10/2022
 *
 */
final class MultiMenu
{
	public final static String TAB = "	";
	public final static String MENU_HEAD = "\n========== Menu Head ===========";
	public final static String BROADCAST_NOTIFICATION = TAB + "1) Broadcast a notification";
	public final static String ANYCAST_NOTIFICATION = TAB + "2) Anycast a notification";
	public final static String UNICAST_NOTIFICATION = TAB + "3) Unicast a notification";
	public final static String BROADCAST_REQUEST = TAB + "4) Broadcast a request";
	public final static String ANYCAST_REQUEST = TAB + "5) Anycast a request";
	public final static String UNICAST_REQUEST = TAB + "6) Unicast a request";
	public final static String STOP_CHILDREN = TAB + "7) Stop children";
	public final static String STOP_ROOT = TAB + "8) Stop root";
	public final static String STOP_REGISTRY = TAB + "9) Stop registry";
	public final static String QUIT = TAB + "0) Quit";
	public final static String MENU_TAIL = "========== Menu Tail ===========\n";
	public final static String INPUT_PROMPT = "Input an option:";
	
	public final static String WRONG_OPTION = "Wrong option!";
}
