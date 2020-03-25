package org.greatfree.testing.cluster.admin;

// Created: 11/30/2016, Bing Li
public class Menu
{
	public final static String TAB = "	";
	public final static String MENU_HEAD = "\n========== Menu Head ===========";
	public final static String STOP_DN = Menu.TAB + "1) Stop DN";
	public final static String STOP_COORDINATOR = Menu.TAB + "2) Stop Coordinator";
	public final static String END = Menu.TAB + "0) End";
	public final static String MENU_TAIL = "========== Menu Tail ===========\n";
	public final static String INPUT_PROMPT = "Input an option:";
	
	public final static String WRONG_OPTION = "Wrong option!";
	public final static String NOTIFYING_SHUTDOWN_COORDINATOR = "Notifying shutdown Coordinator";
	public final static String NOTIFYING_SHUTDOWN_DN = "Notifying shutdown DN";
}
