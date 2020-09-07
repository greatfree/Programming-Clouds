package org.greatfree.dsf.cluster.replication.admin;

// Created: 09/07/2020, Bing Li
class AdminMenu
{
	public final static String TAB = "\t";
	public final static String MENU_HEAD = "\n========== Menu Head ===========";
	public final static String STOP_REPLICATION_CLUSTER_CHILDREN = TAB + "1) Stop Replication Cluster Children";
	public final static String STOP_REPLICATION_ROOT = TAB + "2) Stop Replication Root";
	public final static String STOP_REGISTRY_SERVER = TAB + "3) Stop Registry Server";
	public final static String QUIT = AdminMenu.TAB + "0) Quit";
	public final static String MENU_TAIL = "========== Menu Tail ===========\n";
	public final static String INPUT_PROMPT = "Input an option:";
	public final static String WRONG_OPTION = "Wrong option!";
}
