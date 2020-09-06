package org.greatfree.dsf.cluster.scalable.admin;

// Created: 09/06/2020, Bing Li
class AdminMenu
{
	public final static String TAB = "\t";
	public final static String MENU_HEAD = "\n========== Menu Head ===========";
	public final static String STOP_POOL_CLUSTER_CHILDREN = TAB + "1) Stop Pool Cluster Children";
	public final static String STOP_POOL_ROOT = TAB + "2) Stop Pool Root";
	public final static String STOP_TASK_CLUSTER_CHILDREN = TAB + "3) Stop Task Cluster Children";
	public final static String STOP_TASK_ROOT = TAB + "4) Stop Task Root";	
	public final static String STOP_REGISTRY_SERVER = TAB + "5) Stop Registry Server";
	public final static String QUIT = AdminMenu.TAB + "0) Quit";
	public final static String MENU_TAIL = "========== Menu Tail ===========\n";
	public final static String INPUT_PROMPT = "Input an option:";
	public final static String WRONG_OPTION = "Wrong option!";
}
