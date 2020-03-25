package org.greatfree.admin;

import java.util.Scanner;

/*
 * The program is controlled by the administrator to manage the distributed system manually. 11/27/2014, Bing Li
 */

// Created: 11/27/2014, Bing Li
public class Administrator
{
	public static void main(String[] args) throws InterruptedException
	{
		// Initialize the option, which represents the commands. 11/27/2014, Bing Li
		int option = AdminConfig.NO_OPTION;
		// Initialize a scanner to wait for the administrator's commands. 11/27/2014, Bing Li
		Scanner in = new Scanner(System.in);
		String optionStr;

		// Initialize the scheduler to do something periodical. 02/02/2016, Bing Li
//		Scheduler.GREATFREE().init(ServerConfig.SCHEDULER_POOL_SIZE, ServerConfig.SCHEDULER_KEEP_ALIVE_TIME);

		// Initialize the client pool. 11/27/2014, Bing Li
		ClientPool.ADMIN().init();
		// Initialize the eventer. 11/27/2014, Bing Li
		AdminEventer.CONSOLE().init();

		// Check whether the administrator selects the end command. 11/27/2014, Bing Li
		while (option != AdminConfig.END)
		{
			// Print the console menu. 11/27/2014, Bing Li
			printMenu();
			// Wait for the administrator's input from the console. 11/27/2014, Bing Li
			optionStr = in.nextLine();
			try
			{
				// Convert the string to the integer. 11/27/2014, Bing Li
				option = Integer.parseInt(optionStr);
				// Print the option. 11/27/2014, Bing Li
				System.out.println("Your choice is: " + option);
				// Notify the coordinator. 11/27/2014, Bing Li
				notifyServer(option);
			}
			catch (NumberFormatException e)
			{
				option = AdminConfig.NO_OPTION;
				System.out.println(Menu.WRONG_OPTION);
			}
		}

		// Shutdown the scheduler. 02/02/2016, Bing Li
//		Scheduler.GREATFREE().shutdown();
		// Dispose the client pool. 11/27/2014, Bing Li
		ClientPool.ADMIN().dispose();
		// Dispose the eventer. 11/27/2014, Bing Li
		AdminEventer.CONSOLE().dispose(AdminConfig.THREAD_POOL_SHUTDOWN_TIMEOUT);
		in.close();
	}

	/*
	 * Print the console menu for the administrator. 11/27/2014, Bing Li
	 */
	private static void printMenu()
	{
		System.out.println(Menu.MENU_HEAD);
		System.out.println(Menu.STOP_CLIENT);
		System.out.println(Menu.STOP_SERVER);
		System.out.println(Menu.STOP_CRAWLSERVER);
		System.out.println(Menu.STOP_MSERVER);
		System.out.println(Menu.STOP_CSERVER);
		System.out.println(Menu.END);
		System.out.println(Menu.MENU_TAIL);
		System.out.println(Menu.INPUT_PROMPT);
	}

	/*
	 * Notify the coordinator to manage the distributed system. 11/27/2014, Bing Li
	 */
	private static void notifyServer(int option)
	{
		// Check the option of the administrator. 11/27/2014, Bing Li
		switch (option)
		{
			// Shutdown the coordinator. 11/27/2014, Bing Li
			case AdminConfig.STOP_CSERVER:
				System.out.println(Menu.NOTIFYING_SHUTDOWN_COORDINATOR);
				// Notify the coordinator to shutdown the coordinator. 11/27/2014, Bing Li
				AdminEventer.CONSOLE().notifyShutdownCoordinator();
				break;

			// Shutdown the memory servers. 11/27/2014, Bing Li
			case AdminConfig.STOP_MSERVER:
				System.out.println(Menu.NOTIFYING_SHUTDOWN_MEMSERVER);
				// Notify the coordinator to shutdown the cluster of memory servers. 11/27/2014, Bing Li
				AdminEventer.CONSOLE().notifyShutdownMemoryServer();
				break;

			// Shutdown the crawling servers. 11/27/2014, Bing Li
			case AdminConfig.STOP_CRAWLSERVER:
				System.out.println(Menu.NOTIFYING_SHUTDOWN_CRAWLSERVER);
				// Notify the coordinator to shutdown the cluster of crawling servers. 11/27/2014, Bing Li
				AdminEventer.CONSOLE().notifyShutdownCrawlServer();
				break;

			// Shutdown the server. 01/20/2016, Bing Li
			case AdminConfig.STOP_SERVER:
				System.out.println(Menu.NOTIFYING_SHUTDOWN_SERVER);
				// Notify the coordinator to shutdown the server. 01/20/2016, Bing Li
				AdminEventer.CONSOLE().notifyShutdownServer();
				break;
				
			// Shutdown the client. 01/20/2016, Bing Li
			case AdminConfig.STOP_CLIENT:
				System.out.println(Menu.NOTIFYING_SHUTDOWN_CLIENT);
				break;
		}
	}
}
