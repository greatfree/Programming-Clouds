package edu.greatfree.p2p.peer;

import java.io.IOException;
import java.util.Scanner;

import org.greatfree.exceptions.RemoteReadException;

import edu.greatfree.cs.multinode.ChatOptions;
import edu.greatfree.cs.multinode.ClientMenu;
import edu.greatfree.cs.multinode.MenuOptions;
import edu.greatfree.p2p.message.ChatPartnerResponse;
import edu.greatfree.p2p.message.ChatRegistryResponse;

/*
 * The class aims to print a menu list on the screen for users to interact with the client and communicate with the chatting peer. The menu is unique in the client such that it is implemented in the pattern of a singleton. 05/02/2017, Bing Li
 */

// Created: 05/02/2017, Bing Li
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
	
	public static ClientUI PEER()
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
		System.out.println(ClientMenu.REGISTER_CHATTING + ChatMaintainer.PEER().getLocalUsername());
		System.out.println(ClientMenu.SEARCH_USER + ChatMaintainer.PEER().getPartner());
		System.out.println(ClientMenu.ADD_FRIEND + ChatMaintainer.PEER().getPartner());
		System.out.println(ClientMenu.START_CHATTING + ChatMaintainer.PEER().getPartner());
		System.out.println(ClientMenu.QUIT);
		System.out.println(ClientMenu.MENU_TAIL);
		System.out.println(ClientMenu.INPUT_PROMPT);
	}
	
	/*
	 * Send the users' option to the chatting server. 04/23/2017, Bing Li
	 */
	public void send(int option) throws ClassNotFoundException, RemoteReadException, IOException
	{
		ChatRegistryResponse chatRegistryResponse;
		ChatPartnerResponse chatPartnerResponse = null;

		switch (option)
		{
			// Register the local user for chatting. 06/11/2017, Bing Li
			case MenuOptions.REGISTER_CHATTING:
				chatRegistryResponse = ChatPeer.PEER().registerChat(ChatMaintainer.PEER().getLocalUserKey(), ChatMaintainer.PEER().getLocalUsername(), ChatMaintainer.PEER().getLocalUsername() + " is a good guy", "Computer science is interesting?");
				System.out.println("Chatting registry status: " + chatRegistryResponse.isSucceeded());
				break;

			// Search the potential chatting partner. 06/11/2017, Bing Li
			case MenuOptions.SEARCH_USER:
				chatPartnerResponse = ChatPeer.PEER().searchUser(ChatMaintainer.PEER().getPartnerKey());
				System.out.println(chatPartnerResponse.getUserName() + ": " + chatPartnerResponse.getDescription());
				System.out.println(chatPartnerResponse.getUserName() + ": " + chatPartnerResponse.getPreference());
				System.out.println(chatPartnerResponse.getUserName() + " is located at " + chatPartnerResponse.getIP() + ":" + chatPartnerResponse.getPort());
				ChatMaintainer.PEER().setPartnerIP(chatPartnerResponse.getIP());
				ChatMaintainer.PEER().setPartnerPort(chatPartnerResponse.getPort());
				break;

			// Add the partner for chatting. 06/11/2017, Bing Li
			case MenuOptions.ADD_FRIEND:
				if (ChatMaintainer.PEER().getPartnerIP() != null)
				{
					ChatPeer.PEER().notifyAddFriend(ChatMaintainer.PEER().getPartnerIP(), ChatMaintainer.PEER().getPartnerPort());
				}
				else
				{
					System.out.println("You need to add search your partner, " + ChatMaintainer.PEER().getPartner() + ", first before add her as a friend!");
				}
				break;

			// Start to chat. 06/11/2017, Bing Li
			case MenuOptions.START_CHATTING:
				int chatOption = ChatOptions.NO_OPTION;
				String optionStr;
				// Present a sub menu for chatting. 06/11/2017, Bing Li
				while (chatOption != ChatOptions.QUIT_CHAT)
				{
					ChatUI.PEER().printMenu();
					optionStr = in.nextLine();
					try
					{
						chatOption = Integer.parseInt(optionStr);
						System.out.println("Your choice: " + option);
						ChatUI.PEER().sent(chatOption);
					}
					catch (NumberFormatException e)
					{
						chatOption = MenuOptions.NO_OPTION;
						System.out.println(ClientMenu.WRONG_OPTION);
					}
				}
				break;
				
			case MenuOptions.QUIT:
				break;
		}
	}

}
