package org.greatfree.dip.admin;

import java.io.IOException;
import java.util.Scanner;

import org.greatfree.admin.AdminConfig;
import org.greatfree.admin.Menu;
import org.greatfree.chat.ChatConfig;
import org.greatfree.dip.p2p.RegistryConfig;

/*
 * This is the administrator to manage the chatting system. 04/17/2016, Bing Li
 */

// Created: 04/17/2017, Bing Li
class Administrator
{

	public static void main(String[] args)
	{
		// Initialize the option, which represents the commands. 11/27/2014, Bing Li
		int option = AdminConfig.NO_OPTION;
		// Initialize a scanner to wait for the administrator's commands. 11/27/2014, Bing Li
		Scanner in = new Scanner(System.in);
		String optionStr;

		// Initialize the scheduler to do something periodical. 02/02/2016, Bing Li
//		Scheduler.GREATFREE().init(ChatConfig.SCHEDULER_POOL_SIZE, ChatConfig.SCHEDULER_KEEP_ALIVE_TIME);

		// Initialize the TCP client pool to send messages efficiently. 02/02/2016, Bing Li
//		AdminClientPool.ADMIN().init();
		
		// Initialize the administrator eventer. 05/27/2017, Bing Li
		ChatAdminEventer.RE().init();
		
		// Initialize the remote reader to send requests and receive responses from the register server. Usually, it needs to get IPs of the peer based servers. 11/23/2014, Bing Li
		ChatAdminReader.RR().init();
		
//		RemoteReader.REMOTE().init(ClientConfig.CLIENT_READER_POOL_SIZE);
		
		while (option != AdminConfig.END)
		{
			// Print the console menu. 04/17/2017, Bing Li
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
//		AdminClientPool.ADMIN().dispose();
		// Dispose the eventer. 11/27/2014, Bing Li
		try
		{
			ChatAdminEventer.RE().dispose(RegistryConfig.THREAD_POOL_SHUTDOWN_TIMEOUT);
		}
		catch (InterruptedException | IOException e)
		{
			e.printStackTrace();
		}
		
		// Shutdown the remote reader. 11/23/2014, Bing Li
		ChatAdminReader.RR().shutdown();
		
		// Shutdown the remote reader. 11/23/2014, Bing Li
		/*
		try
		{
			RemoteReader.REMOTE().shutdown();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		*/

		in.close();
	}

	/*
	 * Print the console menu for the administrator. 11/27/2014, Bing Li
	 */
	private static void printMenu()
	{
		System.out.println(Menu.MENU_HEAD);
		System.out.println(AdminMenu.STOP_CHATTING_SERVER);
//		System.out.println(ChatMenu.STOP_CHATTING_PEER);
		System.out.println(AdminMenu.STOP_MULTICAST_CHILDREN);
		System.out.println(AdminMenu.STOP_MULTICAST_ROOT);
		System.out.println(AdminMenu.STOP_PUBSUB_SERVER);
		System.out.println(AdminMenu.STOP_CHATTING_REGISTRY_SERVER);
		System.out.println(Menu.END);
		System.out.println(Menu.MENU_TAIL);
		System.out.println(Menu.INPUT_PROMPT);
	}

	/*
	 * Notify the chatting server to manage the distributed system. 11/27/2014, Bing Li
	 */
	private static void notifyServer(int option)
	{
		// Check the option of the administrator. 11/27/2014, Bing Li
		switch (option)
		{
			// Shutdown the chatting server. 11/27/2014, Bing Li
			case ChatConfig.STOP_CS_CHATTING_SERVER:
				System.out.println(AdminMenu.STOP_CHATTING_SERVER);
				// Notify the CS chatting server to shutdown. 11/27/2014, Bing Li
				ChatAdminEventer.RE().notifyShutdownCSChatServer();
				break;

			// Shutdown the chatting peer. 05/01/2017, Bing Li
				/*
			case ChatConfig.STOP_CHATTING_PEER:
				System.out.println(ChatMenu.STOP_CHATTING_PEER);
				// Notify the chatting peer to shutdown. 11/27/2014, Bing Li
				ChatAdminEventer.CONSOLE().notifyShutdownChattingPeer();
				break;
				*/
				
				// Shutdown the cluster server. 05/01/2017, Bing Li
			case ChatConfig.STOP_MULTICASTING_CHILDREN:
				System.out.println(AdminMenu.STOP_MULTICAST_CHILDREN);
				// Notify the cluster root to shutdown children. 11/27/2014, Bing Li
				ChatAdminEventer.RE().notifyShutdownMulticastChildren();
				break;
				
			case ChatConfig.STOP_MULTICASTING_ROOT:
				System.out.println(AdminMenu.STOP_MULTICAST_ROOT);
				ChatAdminEventer.RE().notifyShutdownMulticastRoot();
				break;
				
			case ChatConfig.STOP_PUBSUB_NOTIFICATION:
				System.out.println(AdminMenu.STOP_PUBSUB_SERVER);
				ChatAdminEventer.RE().notifyPubSubServer();
				break;

			// Shutdown the chatting registry server. 05/01/2017, Bing Li
			case ChatConfig.STOP_CHATTING_REGISTRY_SERVER:
				System.out.println(AdminMenu.STOP_CHATTING_REGISTRY_SERVER);
				// Notify the chatting registry server to shutdown. 11/27/2014, Bing Li
				ChatAdminEventer.RE().notifyShutdownChattingRegistryServer();
				break;
		}
	}
}
