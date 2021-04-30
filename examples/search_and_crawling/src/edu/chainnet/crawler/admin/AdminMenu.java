package edu.chainnet.crawler.admin;

// Created: 04/25/2021, Bing Li
final class AdminMenu
{
	public final static String TAB = "\t";
	public final static String MENU_HEAD = "\n========== Menu Head ===========";
	public final static String STOP_ONE_CRAWLING_CHILD = TAB + "1) Stop One Crawling Child";
	public final static String STOP_CRAWLING_CHILDREN = TAB + "2) Stop Crawling Children";
	public final static String STOP_CRAWLING_COORDINATOR = TAB + "3) Stop Crawling Coordinator";
	public final static String STOP_ONE_DATA_CENTER_CHILD = TAB + "4) Stop One Data Center Child";
	public final static String STOP_DATA_CENTER_CHILDREN = TAB + "5) Stop Data Center Children";
	public final static String STOP_DATA_CENTER_COORDINATOR = TAB + "6) Stop Data Center Coordinator";
	public final static String STOP_REGISTRY_SERVER = TAB + "7) Stop Registry Server";
	public final static String QUIT = AdminMenu.TAB + "0) Quit";
	public final static String MENU_TAIL = "========== Menu Tail ===========\n";
	public final static String INPUT_PROMPT = "Input an option:";
	public final static String WRONG_OPTION = "Wrong option!";
}
