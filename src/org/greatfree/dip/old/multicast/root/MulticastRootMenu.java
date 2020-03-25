package org.greatfree.dip.old.multicast.root;

/*
 * This is a simple menu for the multicastor which is operated by users. 04/23/2017, Bing Li
 */

// Created: 05/10/2017, Bing Li
public class MulticastRootMenu
{
	public final static String TAB = "	";
	public final static String MENU_HEAD = "\n========== Menu Head ===========";
	public final static String BROADCAST_NOTIFICATION = MulticastRootMenu.TAB + "1) Broadcast a notification";
	public final static String UNICAST_NOTIFICATION = MulticastRootMenu.TAB + "2) Unicast a notification";
	public final static String ANYCAST_NOTIFICATION = MulticastRootMenu.TAB + "3) Anycast a notification";
	public final static String BROADCAST_REQUEST = MulticastRootMenu.TAB + "4) Broadcast a request";
	public final static String UNICAST_REQUEST = MulticastRootMenu.TAB + "5) Unicast a request";
	public final static String ANYCAST_REQUEST = MulticastRootMenu.TAB + "6) Anycast a request";
	public final static String QUIT = MulticastRootMenu.TAB + "0) Quit";
	public final static String MENU_TAIL = "========== Menu Tail ===========\n";
	public final static String INPUT_PROMPT = "Input an option:";
	
	public final static String WRONG_OPTION = "Wrong option!";

}
