package org.greatfree.framework.old.multicast.root;

import java.io.IOException;
import java.util.Map;
import java.util.Scanner;

import org.greatfree.chat.ChatMenu;
import org.greatfree.chat.ChatOptions;
import org.greatfree.framework.multicast.HelloWorld;
import org.greatfree.framework.multicast.MulticastConfig;
import org.greatfree.framework.multicast.message.HelloWorldAnycastResponse;
import org.greatfree.framework.multicast.message.HelloWorldBroadcastResponse;
import org.greatfree.framework.multicast.message.HelloWorldUnicastResponse;

/*
 * The class aims to print a menu list on the screen for users to interact with the users and chat with the distributed noodes. The menu is unique in the multicastor such that it is implemented in the pattern of a singleton. 04/27/2017, Bing Li
 */

// Created: 05/10/2017, Bing Li
class MulticastInputUI
{
	private Scanner in = new Scanner(System.in);

	/*
	 * Initialize. 04/27/2017, Bing Li
	 */
	private MulticastInputUI()
	{
	}

	/*
	 * Initialize a singleton. 04/23/2017, Bing Li
	 */
	private static MulticastInputUI instance = new MulticastInputUI();
	
	public static MulticastInputUI CLUSTER()
	{
		if (instance == null)
		{
			instance = new MulticastInputUI();
			return instance;
		}
		else
		{
			return instance;
		}
	}
	
	public void dispose()
	{
		this.in.close();
	}

	/*
	 * Print the menu list on the screen. 04/23/2017, Bing Li
	 */
	public void printMenu()
	{
		System.out.println(ChatMenu.MENU_HEAD);
		System.out.println(ChatMenu.TYPE_MESSAGE);
		System.out.println(ChatMenu.QUIT);
		System.out.println(ChatMenu.MENU_TAIL);
		System.out.println(ChatMenu.INPUT_PROMPT);
	}
	
	/*
	 * Send messages to the distributed nodes within the cluster. 04/23/2017, Bing Li
	 */
	public void send(int highOption, int option) throws InstantiationException, IllegalAccessException, IOException, InterruptedException
	{
		int index = 0;
		Map<String, HelloWorldBroadcastResponse> broadcastResponses;
		Map<String, HelloWorldAnycastResponse> anycastResponses;
		Map<String, HelloWorldUnicastResponse> unicastResponses;
		switch (option)
		{
			case ChatOptions.TYPE_CHAT:
				System.out.println("Please type your message: ");
				String message = in.nextLine();
				switch (highOption)
				{
					case MulticastOptions.BROADCAST_NOTIFICATION:
						ClusterRootSingleton.CLUSTER().broadcastNotify(new HelloWorld(message), MulticastConfig.ROOT_BRANCH_COUNT, MulticastConfig.SUB_BRANCH_COUNT);
						System.out.println("You notification is broadcast!");
						break;
						
					case MulticastOptions.ANYCAST_NOTIFICATION:
						ClusterRootSingleton.CLUSTER().anycastNotify(new HelloWorld(message), MulticastConfig.ROOT_BRANCH_COUNT, MulticastConfig.SUB_BRANCH_COUNT);
						System.out.println("You notification is anycast!");
						break;
						
					case MulticastOptions.UNICAST_NOTIFICATION:
						ClusterRootSingleton.CLUSTER().unicastNotify(new HelloWorld(message), MulticastConfig.ROOT_BRANCH_COUNT, MulticastConfig.SUB_BRANCH_COUNT);
						System.out.println("You notification is unicast!");
						break;
						
					case MulticastOptions.BROADCAST_REQUEST:
						broadcastResponses = ClusterRootSingleton.CLUSTER().broadcastRead(new HelloWorld(message), MulticastConfig.ROOT_BRANCH_COUNT, MulticastConfig.SUB_BRANCH_COUNT);
						System.out.println("You request is broadcast!");
						System.out.println("You got " + broadcastResponses.size() + " responses");
						for (HelloWorldBroadcastResponse response : broadcastResponses.values())
						{
							System.out.println(++index + ") response = " + response.getHelloWorld().getHelloWorld());
						}
						break;
						
					case MulticastOptions.UNICAST_REQUEST:
						unicastResponses = ClusterRootSingleton.CLUSTER().unicastRead(new HelloWorld(message), MulticastConfig.ROOT_BRANCH_COUNT, MulticastConfig.SUB_BRANCH_COUNT);
						System.out.println("You request is unicast!");
						System.out.println("You got " + unicastResponses.size() + " responses");
						for (HelloWorldUnicastResponse response : unicastResponses.values())
						{
							System.out.println(++index + ") response = " + response.getHelloWorld().getHelloWorld());
						}
						break;
						
					case MulticastOptions.ANYCAST_REQUEST:
						anycastResponses = ClusterRootSingleton.CLUSTER().anycastRead(new HelloWorld(message), MulticastConfig.ROOT_BRANCH_COUNT, MulticastConfig.SUB_BRANCH_COUNT);
						System.out.println("You request is anycast!");
						System.out.println("You got " + anycastResponses.size() + " responses");
						for (HelloWorldAnycastResponse response : anycastResponses.values())
						{
							System.out.println(++index + ") response = " + response.getHelloWorld().getHelloWorld());
						}
						break;
				}
				break;

			case ChatOptions.QUIT_CHAT:
				break;
		}
	}
}
