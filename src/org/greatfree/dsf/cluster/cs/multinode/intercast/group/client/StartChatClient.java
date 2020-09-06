package org.greatfree.dsf.cluster.cs.multinode.intercast.group.client;

import java.io.IOException;
import java.util.Scanner;

import org.greatfree.chat.ChatConfig;
import org.greatfree.chat.ClientMenu;
import org.greatfree.chat.MenuOptions;
import org.greatfree.concurrency.Scheduler;
import org.greatfree.dsf.cluster.cs.twonode.client.ChatClient;
import org.greatfree.dsf.p2p.RegistryConfig;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.util.TerminateSignal;

// Created: 04/06/2019, Bing Li
class StartChatClient
{

	public static void main(String[] args) throws ClassNotFoundException, RemoteReadException, IOException, InterruptedException
	{
		// Initialize the option which represents a user's intents of operations. 09/21/2014, Bing Li
		int option = MenuOptions.NO_OPTION;

		// Initialize a command input console for users to interact with the system. 09/21/2014, Bing Li
		Scanner in = new Scanner(System.in);
		
		System.out.println("Are you a group creator? (y or n)");
		String isGroupCreator = in.nextLine();
		if (isGroupCreator.equals("y"))
		{
			ChatMaintainer.GROUP().setIsGroupCreator(true);
		}
		else
		{
			ChatMaintainer.GROUP().setIsGroupCreator(false);
		}
		
		// Input the local user name. 05/26/2017, Bing Li
		System.out.println("Tell me your user name: ");
		
		String userName = in.nextLine();
		String groupName;
		
		if (ChatMaintainer.GROUP().isGroupCreator())
		{
			// Input the chatting partner name. 05/26/2017, Bing Li
			System.out.println("Tell me the group you attempt to create: ");
			groupName = in.nextLine();
			
			System.out.println("Tell me one user you attempt to invite: ");
			String oneMemberName = in.nextLine();
			ChatMaintainer.GROUP().setOneMemberName(oneMemberName);
		}
		else
		{
			// Input the chatting partner name. 05/26/2017, Bing Li
			System.out.println("Tell me the group you attempt to join: ");
			groupName = in.nextLine();
		}
		
		ChatMaintainer.GROUP().init(userName, groupName);

		/*
		 * Comment the scheduling temporarily for testing. 04/21/2019, Bing Li
		 */
		Scheduler.GREATFREE().init(RegistryConfig.SCHEDULER_THREAD_POOL_SIZE, RegistryConfig.SCHEDULER_THREAD_POOL_KEEP_ALIVE_TIME);

		ChatClient.CONTAINER().init();

		/*
		 * Comment the scheduling temporarily for testing. 04/21/2019, Bing Li
		 */
		Scheduler.GREATFREE().submit(new Checker(), ChatConfig.CHAT_POLLING_DELAY, ChatConfig.CHAT_POLLING_PERIOD);
		
		String optionStr;
		// Keep the loop running to interact with users until an end option is selected. 09/21/2014, Bing Li
		while (option != MenuOptions.QUIT)
		{
			ClientUI.GROUP().printMenu();
			
			optionStr = in.nextLine();
			try
			{
				// Convert the input string to integer. 09/21/2014, Bing Li
				option = Integer.parseInt(optionStr);
				System.out.println("Your choice: " + option);
				
				// Send the option to the polling server. 09/21/2014, Bing Li
				ClientUI.GROUP().send(option);
			}
			catch (NumberFormatException e)
			{
				option = MenuOptions.NO_OPTION;
				System.out.println(ClientMenu.WRONG_OPTION);
			}
		}

		TerminateSignal.SIGNAL().setTerminated();
		ChatMaintainer.GROUP().dispose();
		/*
		 * Comment the scheduling temporarily for testing. 04/21/2019, Bing Li
		 */
		Scheduler.GREATFREE().shutdown(RegistryConfig.SCHEDULER_SHUTDOWN_TIMEOUT);

		ChatClient.CONTAINER().dispose();

		in.close();
	}

}
