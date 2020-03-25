package org.greatfree.dip.multicast.client;

import java.io.IOException;
import java.util.Scanner;

import org.greatfree.chat.ChatMenu;
import org.greatfree.chat.ChatOptions;
import org.greatfree.dip.multicast.HelloWorld;
import org.greatfree.dip.multicast.message.ClientHelloWorldAnycastRequest;
import org.greatfree.dip.multicast.message.ClientHelloWorldAnycastResponse;
import org.greatfree.dip.multicast.message.ClientHelloWorldBroadcastRequest;
import org.greatfree.dip.multicast.message.ClientHelloWorldBroadcastResponse;
import org.greatfree.dip.multicast.message.ClientHelloWorldUnicastRequest;
import org.greatfree.dip.multicast.message.ClientHelloWorldUnicastResponse;
import org.greatfree.dip.multicast.message.HelloWorldAnycastNotification;
import org.greatfree.dip.multicast.message.HelloWorldAnycastResponse;
import org.greatfree.dip.multicast.message.HelloWorldBroadcastNotification;
import org.greatfree.dip.multicast.message.HelloWorldBroadcastResponse;
import org.greatfree.dip.multicast.message.HelloWorldUnicastNotification;
import org.greatfree.dip.multicast.message.HelloWorldUnicastResponse;
import org.greatfree.dip.old.multicast.root.MulticastOptions;
import org.greatfree.exceptions.RemoteReadException;

// Created: 08/26/2018, Bing Li
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
	public void send(int highOption, int option) throws InstantiationException, IllegalAccessException, IOException, InterruptedException, ClassNotFoundException, RemoteReadException
	{
		int index = 0;
		ClientHelloWorldBroadcastResponse broadcastResponse;
		ClientHelloWorldAnycastResponse anycastResponse;
		ClientHelloWorldUnicastResponse unicastResponse;
		switch (option)
		{
			case ChatOptions.TYPE_CHAT:
				System.out.println("Please type your message: ");
				String message = in.nextLine();
				switch (highOption)
				{
					case MulticastOptions.BROADCAST_NOTIFICATION:
						MulticastClient.FRONT().syncNotify(new HelloWorldBroadcastNotification(new HelloWorld(message)));
						System.out.println("You notification is broadcast!");
						break;
						
					case MulticastOptions.ANYCAST_NOTIFICATION:
						MulticastClient.FRONT().syncNotify(new HelloWorldAnycastNotification(new HelloWorld(message)));
						System.out.println("You notification is anycast!");
						break;
						
					case MulticastOptions.UNICAST_NOTIFICATION:
						MulticastClient.FRONT().syncNotify(new HelloWorldUnicastNotification(new HelloWorld(message)));
						System.out.println("You notification is unicast!");
						break;
						
					case MulticastOptions.BROADCAST_REQUEST:
						broadcastResponse = (ClientHelloWorldBroadcastResponse)MulticastClient.FRONT().read(new ClientHelloWorldBroadcastRequest(new HelloWorld(message)));
						System.out.println("You request is broadcast!");
						System.out.println("You got " + broadcastResponse.getResponses().size() + " responses");
						for (HelloWorldBroadcastResponse response : broadcastResponse.getResponses())
						{
							System.out.println(++index + ") response = " + response.getHelloWorld().getHelloWorld());
						}
						break;
						
					case MulticastOptions.UNICAST_REQUEST:
						unicastResponse = (ClientHelloWorldUnicastResponse)MulticastClient.FRONT().read(new ClientHelloWorldUnicastRequest(new HelloWorld(message)));
						System.out.println("You request is unicast!");
						System.out.println("You got " + unicastResponse.getResponses().size() + " responses");
						for (HelloWorldUnicastResponse response : unicastResponse.getResponses())
						{
							System.out.println(++index + ") response = " + response.getHelloWorld().getHelloWorld());
						}
						break;
						
					case MulticastOptions.ANYCAST_REQUEST:
						anycastResponse = (ClientHelloWorldAnycastResponse)MulticastClient.FRONT().read(new ClientHelloWorldAnycastRequest(new HelloWorld(message)));
						System.out.println("You request is anycast!");
						System.out.println("You got " + anycastResponse.getResponses().size() + " responses");
						for (HelloWorldAnycastResponse response : anycastResponse.getResponses())
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
