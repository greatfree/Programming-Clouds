package edu.greatfree.container.p2p.peer;

import java.io.IOException;
import java.util.Scanner;

import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.util.TerminateSignal;

import edu.greatfree.cs.multinode.ChatConfig;
import edu.greatfree.cs.multinode.ClientMenu;
import edu.greatfree.cs.multinode.MenuOptions;
import edu.greatfree.p2p.peer.ChatMaintainer;

// Created: 01/12/2019, Bing Li
class StartChatPeer
{

	public static void main(String[] args) throws ClassNotFoundException, IOException, RemoteReadException, InterruptedException
	{
		// Initialize the option which represents a user's intents of operations. 09/21/2014, Bing Li
		int option = MenuOptions.NO_OPTION;

		// Initialize a command input console for users to interact with the system. 09/21/2014, Bing Li
		Scanner in = new Scanner(System.in);
		String optionStr;

		System.out.println("Tell me your user name: ");
		
		String username = in.nextLine();

		System.out.println("Tell me your partner: ");
		
		String partner = in.nextLine();

		ChatMaintainer.PEER().init(username, partner);
		
		System.out.println("Chatting peer starting up ...");

		ChatPeer.CONTAINER().start(username, ChatConfig.CHAT_SERVER_PORT, new ChatTask(), true);
	
		System.out.println("Chatting peer started ...");

		// Keep the loop running to interact with users until an end option is selected. 09/21/2014, Bing Li
		while (option != MenuOptions.QUIT)
		{
			// Display the menu to users. 09/21/2014, Bing Li
			ClientUI.CONTAINER().printMenu();
			// Input a string that represents users' intents. 09/21/2014, Bing Li
			optionStr = in.nextLine();
			try
			{
				// Convert the input string to integer. 09/21/2014, Bing Li
				option = Integer.parseInt(optionStr);
				System.out.println("Your choice: " + option);
				
				// Send the option to the server. 09/21/2014, Bing Li
				ClientUI.CONTAINER().send(option);
			}
			catch (NumberFormatException | ClassNotFoundException | RemoteReadException | IOException e)
			{
				option = MenuOptions.NO_OPTION;
				System.out.println(ClientMenu.WRONG_OPTION);
			}
		}

		ChatPeer.CONTAINER().stop(ChatConfig.SERVER_SHUTDOWN_TIMEOUT);

		// After the server is started, the loop check whether the flag of terminating is set. If the terminating flag is true, the process is ended. Otherwise, the process keeps running. 08/22/2014, Bing Li
		while (!TerminateSignal.SIGNAL().isTerminated())
		{
			try
			{
				// If the terminating flag is false, it is required to sleep for some time. Otherwise, it might cause the high CPU usage. 08/22/2014, Bing Li
				Thread.sleep(ChatConfig.TERMINATE_SLEEP);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
		
		in.close();
	}

}
