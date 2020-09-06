package org.greatfree.dsf.cps.admin;

import org.greatfree.admin.Menu;

// Created: 07/07/2018, Bing Li
public class CPSMenu
{
	public final static String TAB = "	";
	public final static String MENU_HEAD = "\n========== Menu Head ===========";
	public final static String END = Menu.TAB + "0) End";
	public final static String MENU_TAIL = "========== Menu Tail ===========\n";
	public final static String INPUT_PROMPT = "Input an option:";
	public final static String WRONG_OPTION = "Wrong option!";

	public final static String STOP_COORDINATOR = CPSMenu.TAB + "1) Stop Coordinator";
	public final static String STOP_TERMINAL = CPSMenu.TAB + "2) Stop Terminal";
}
