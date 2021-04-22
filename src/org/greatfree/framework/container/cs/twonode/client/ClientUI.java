package org.greatfree.framework.container.cs.twonode.client;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.greatfree.chat.ChatConfig;
import org.greatfree.chat.ChatOptions;
import org.greatfree.client.StandaloneClient;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.framework.container.cs.twonode.message.ChatRegistryRequest;
import org.greatfree.framework.container.cs.twonode.message.ShutdownChatServerNotification;
import org.greatfree.framework.cs.multinode.message.ChatRegistryResponse;
import org.greatfree.framework.cs.twonode.client.ClientMenu;
import org.greatfree.framework.cs.twonode.client.MenuOptions;
import org.greatfree.message.ServerMessage;

// Created: 12/18/2018, Bing Li
class ClientUI
{
	private Scanner in = new Scanner(System.in);

	/*
	 * Initialize. 04/23/2017, Bing Li
	 */
	private ClientUI()
	{
	}

	/*
	 * Initialize a singleton. 04/23/2017, Bing Li
	 */
	private static ClientUI instance = new ClientUI();
	
	public static ClientUI CS()
	{
		if (instance == null)
		{
			instance = new ClientUI();
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
		System.out.println(ClientMenu.MENU_HEAD);
		System.out.println(ClientMenu.REGISTER_CHATTING + ChatMaintainer.CS_CONTAINER().getLocalUsername());
		System.out.println(ClientMenu.START_CHATTING + ChatMaintainer.CS_CONTAINER().getPartner());
		System.out.println(ClientMenu.SHUTDOWN_CHAT_SERVER);
		System.out.println(ClientMenu.QUIT);
		System.out.println(ClientMenu.MENU_TAIL);
		System.out.println(ClientMenu.INPUT_PROMPT);
	}

	public void send(int option) throws ClassNotFoundException, RemoteReadException, IOException, InterruptedException
	{
		ChatRegistryResponse response;
		switch (option)
		{
			case MenuOptions.REGISTER_CHATTING:
//				response = (ChatRegistryResponse)ChatClient.CS_FRONT().read(new ChatRegistryRequest(ChatMaintainer.CS().getLocalUserKey(), ChatMaintainer.CS().getLocalUsername(), ChatMaintainer.CS().getLocalUsername() + " is a great & free guy!"));

//				response = (ChatRegistryResponse)StandaloneClient.CS().read(ChatConfig.CHAT_SERVER_ADDRESS, ChatConfig.CHAT_SERVER_PORT, new ChatRegistryRequest(ChatMaintainer.CS_CONTAINER().getLocalUserKey(), ChatMaintainer.CS_CONTAINER().getLocalUsername(), ChatMaintainer.CS_CONTAINER().getLocalUsername() + " is a great & free guy!"));

//				System.out.println("Chatting registry status: " + response.isSucceeded());

				/*
				 * The below code is used to test the future read. 03/25/2020, Bing Li
				 */
//				System.out.println("AGAIN: Chatting registry status: " + response.isSucceeded());

//				Future<ServerMessage> res = StandaloneClient.CS().futureRead(ChatConfig.CHAT_SERVER_ADDRESS, ChatConfig.CHAT_SERVER_PORT, new ChatRegistryRequest(ChatMaintainer.CS_CONTAINER().getLocalUserKey(), ChatMaintainer.CS_CONTAINER().getLocalUsername(), ChatMaintainer.CS_CONTAINER().getLocalUsername() + " is a great & free guy!"));
				Future<ServerMessage> res = StandaloneClient.CS().futureRead(ChatConfig.CHAT_SERVER_ADDRESS, ChatConfig.CHAT_SERVER_PORT, new ChatRegistryRequest(ChatMaintainer.CS_CONTAINER().getLocalUserKey(), ChatMaintainer.CS_CONTAINER().getLocalUsername(), ChatMaintainer.CS_CONTAINER().getLocalUsername() + " is a great & free guy!"), 2000);

				/*
				while (!res.isDone())
				{
					System.out.println("Waiting ...");
				}
				*/
				
				try
				{
					ServerMessage ms = res.get();
					response = (ChatRegistryResponse)ms;
					System.out.println("AGAIN: Chatting registry status: " + response.isSucceeded());
				}
				catch (ExecutionException e)
				{
//					e.printStackTrace();
					System.out.println("Time out ...");
				}
				break;

			case MenuOptions.START_CHATTING:
				int chatOption = ChatOptions.NO_OPTION;
				String optionStr;
				while (chatOption != ChatOptions.QUIT_CHAT)
				{
					ChatUI.CS_CONTAINER().printMenu();
					optionStr = in.nextLine();
					try
					{
						chatOption = Integer.parseInt(optionStr);
						System.out.println("Your choice: " + option);
						ChatUI.CS_CONTAINER().sent(chatOption);
					}
					catch (NumberFormatException e)
					{
						chatOption = MenuOptions.NO_OPTION;
						System.out.println(ClientMenu.WRONG_OPTION);
					}
				}
				break;
				
			case MenuOptions.SHUTDOWN_CHATTING:
//				ChatClient.CS_FRONT().syncNotify(new ShutdownChatServerNotification());
				StandaloneClient.CS().syncNotify(ChatConfig.CHAT_SERVER_ADDRESS, ChatConfig.CHAT_SERVER_PORT, new ShutdownChatServerNotification());
				break;
				
			case MenuOptions.QUIT:
				break;
			
		}
	}
	
}
