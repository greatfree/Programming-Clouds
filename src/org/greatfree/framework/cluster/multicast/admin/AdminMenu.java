package org.greatfree.framework.cluster.multicast.admin;

/**
 * 
 * @author libing
 * 
 * 03/10/2023
 *
 */
public final class AdminMenu
{
	public final static String TAB = "\t";
	public final static String MENU_HEAD = "\n========== Menu Head ===========";
	public final static String SHUTDOWN_CHILDREN = TAB + "1) Shutdown children";
	public final static String SHUTDOWN_ROOT = TAB + "2) Shutdown root";
	public final static String STOP_REGISTRY_SERVER = TAB + "3) Stop registry server";
	public final static String QUIT = TAB + "0) Quit";
	public final static String MENU_TAIL = "========== Menu Tail ===========\n";
	public final static String INPUT_PROMPT = "Input an option:";
	public final static String WRONG_OPTION = "Wrong option!";
}
