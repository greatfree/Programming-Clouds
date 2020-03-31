package edu.greatfree.cs.admin;

import org.greatfree.admin.Menu;

//Created: 05/13/2018, Bing Li
class AdminMenu
{
	public final static String TAB = "	";
	public final static String MENU_HEAD = "\n========== Menu Head ===========";
	public final static String END = Menu.TAB + "0) End";
	public final static String MENU_TAIL = "========== Menu Tail ===========\n";
	public final static String INPUT_PROMPT = "Input an option:";
	public final static String WRONG_OPTION = "Wrong option!";

	public final static String STOP_CHATTING_SERVER = AdminMenu.TAB + "1) Stop CS Chatting Server";
	public final static String STOP_REGISTRY_SERVER = AdminMenu.TAB + "2) Stop Registry Server";
}
