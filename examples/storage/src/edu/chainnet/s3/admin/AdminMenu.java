package edu.chainnet.s3.admin;

// Created: 07/20/2020, Bing Li
class AdminMenu
{
	public final static String TAB = "\t";
	public final static String MENU_HEAD = "\n========== Menu Head ===========";
	public final static String ENLARGE_SCALE_OF_STORAGE_CLUSTER = TAB + "1) Enlarge Scale of Storage Cluster";
//	public final static String REDUCE_SCALE_OF_STORAGE_CLUSTER = TAB + "2) Reduce Scale of Storage Cluster";
	public final static String STOP_ONE_STORAGE_CHILD = TAB + "2) Stop One Storage Child";
	public final static String STOP_META_SERVER = TAB + "3) Stop Meta Server";
	public final static String STOP_EDSA_CHILDREN = TAB + "4) Stop EDSA Children";
	public final static String STOP_EDSA_ROOT = TAB + "5) Stop EDSA Root";
	public final static String STOP_POOL_CHILDREN = TAB + "6) Stop Pool Children";
	public final static String STOP_POOL_ROOT = TAB + "7) Stop Pool Root";
	public final static String STOP_STORAGE_CHILDREN = TAB + "8) Stop Storage Children";	
	public final static String STOP_STORAGE_ROOT = TAB + "9) Stop Storage Root";
	public final static String STOP_REGISTRY_SERVER = TAB + "10) Stop Registry Server";
	public final static String QUIT = AdminMenu.TAB + "0) Quit";
	public final static String MENU_TAIL = "========== Menu Tail ===========\n";
	public final static String INPUT_PROMPT = "Input an option:";
	public final static String WRONG_OPTION = "Wrong option!";
}
