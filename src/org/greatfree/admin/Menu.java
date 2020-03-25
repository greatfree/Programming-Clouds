package org.greatfree.admin;

/*
 * This is a menu for the administrator to display a control interface. 11/27/2014, Bing Li
 */

// Created: 11/27/2014, Bing Li
public class Menu
{
	public final static String TAB = "	";
	public final static String MENU_HEAD = "\n========== Menu Head ===========";
	public final static String STOP_CLIENT = Menu.TAB + "1) Stop Client";
	public final static String STOP_SERVER = Menu.TAB + "2) Stop Server";
	public final static String STOP_CRAWLSERVER = Menu.TAB + "3) Stop CrawlServer";
	public final static String STOP_MSERVER = Menu.TAB + "4) Stop MServer";
	public final static String STOP_CSERVER = Menu.TAB + "5) Stop CServer";
	public final static String END = Menu.TAB + "0) End";
	public final static String MENU_TAIL = "========== Menu Tail ===========\n";
	public final static String INPUT_PROMPT = "Input an option:";
	
	public final static String WRONG_OPTION = "Wrong option!";
	public final static String NOTIFYING_SHUTDOWN_COORDINATOR = "Notifying shutdown CServer";
	public final static String NOTIFYING_SHUTDOWN_MEMSERVER = "Notifying shutdown MServer";
	public final static String NOTIFYING_SHUTDOWN_CRAWLSERVER = "Notifying shutdown CrawlServer";
	public final static String NOTIFYING_SHUTDOWN_SERVER = "Notifying shutdown Server";
	public final static String NOTIFYING_SHUTDOWN_CLIENT = "Notifying shutdown Client";
}
