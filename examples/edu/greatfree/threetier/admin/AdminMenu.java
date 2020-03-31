package edu.greatfree.threetier.admin;

import org.greatfree.admin.Menu;

// Created: 05/07/2019, Bing Li
public class AdminMenu
{
	public final static String TAB = "	";
	public final static String MENU_HEAD = "\n========== Menu Head ===========";
	public final static String END = Menu.TAB + "0) End";
	public final static String MENU_TAIL = "========== Menu Tail ===========\n";
	public final static String INPUT_PROMPT = "Input an option:";
	public final static String WRONG_OPTION = "Wrong option!";

	public final static String STOP_COORDINATOR = AdminMenu.TAB + "1) Stop Coordinator";
	public final static String STOP_TERMINAL = AdminMenu.TAB + "2) Stop Terminal";
}
