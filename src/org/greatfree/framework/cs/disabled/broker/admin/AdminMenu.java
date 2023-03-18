package org.greatfree.framework.cs.disabled.broker.admin;

/**
 * 
 * @author libing
 * 
 * 03/17/2023
 *
 */
final class AdminMenu
{
	public final static String TAB = "\t";
	public final static String MENU_HEAD = "\n========== Menu Head ===========";
	public final static String SHUTDOWN_BROKER_CHILDREN = TAB + "1) Shutdown broker children";
	public final static String SHUTDOWN_BROKER_ROOT = TAB + "2) Shutdown broker root";
	public final static String STOP_REGISTRY_SERVER = TAB + "3) Stop registry server";
	public final static String QUIT = TAB + "0) Quit";
	public final static String MENU_TAIL = "========== Menu Tail ===========\n";
	public final static String INPUT_PROMPT = "Input an option:";
	public final static String WRONG_OPTION = "Wrong option!";
}
