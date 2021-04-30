package edu.chainnet.sc.admin;

// Created: 10/20/2020, Bing Li
class AdminMenu
{
	public final static String TAB = "\t";
	public final static String MENU_HEAD = "\n========== Menu Head ===========";
	
	public final static String STOP_COLLABORATOR_CHILDREN = AdminMenu.TAB + "1) Stop Collaborator Children";
	public final static String STOP_COLLABORATOR_ROOT = AdminMenu.TAB + "2) Stop Collaborator Root";
	public final static String STOP_REGISTRY_SERVER = AdminMenu.TAB + "3) Stop Registry Server";

	public final static String QUIT = AdminMenu.TAB + "0) Quit";
	public final static String MENU_TAIL = "========== Menu Tail ===========\n";
	public final static String INPUT_PROMPT = "Input an option:";
	public final static String WRONG_OPTION = "Wrong option!";
}
