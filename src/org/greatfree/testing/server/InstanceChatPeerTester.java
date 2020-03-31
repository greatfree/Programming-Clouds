package org.greatfree.testing.server;

import java.io.IOException;
import java.util.Scanner;

import org.greatfree.chat.ClientMenu;
import org.greatfree.chat.MenuOptions;
import org.greatfree.data.ClientConfig;
import org.greatfree.data.ServerConfig;
import org.greatfree.dip.p2p.peer.ChatMaintainer;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.server.ChatPeer;
import org.greatfree.util.ServerStatus;
import org.greatfree.util.TerminateSignal;

// Created: 05/08/2017, Bing Li
class InstanceChatPeerTester
{

	public static void main(String[] args)
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

		ChatPeer peer = new ChatPeer(username);
		try
		{
			peer.start();
		}
		catch (ClassNotFoundException | IOException | RemoteReadException e)
		{
			e.printStackTrace();
		}

		System.out.println("Chatting peer started ...");
		
		// Keep the loop running to interact with users until an end option is selected. 09/21/2014, Bing Li
		while (option != MenuOptions.QUIT)
		{
			// Display the menu to users. 09/21/2014, Bing Li
			ClientUI.TEST().printMenu();
			// Input a string that represents users' intents. 09/21/2014, Bing Li
			optionStr = in.nextLine();
			try
			{
				// Convert the input string to integer. 09/21/2014, Bing Li
				option = Integer.parseInt(optionStr);
				System.out.println("Your choice: " + option);
				
				// Send the option to the polling server. 09/21/2014, Bing Li
				ClientUI.TEST().send(peer, option);
			}
			catch (NumberFormatException | ClassNotFoundException | RemoteReadException | IOException e)
			{
				option = MenuOptions.NO_OPTION;
				System.out.println(ClientMenu.WRONG_OPTION);
			}
		}

		TerminateSignal.SIGNAL().setTerminated();
		ServerStatus.FREE().setShutdown();
		try
		{
			peer.stop(ClientConfig.THREAD_POOL_SHUTDOWN_TIMEOUT);
		}
		catch (IOException | InterruptedException | ClassNotFoundException | RemoteReadException e)
		{
			e.printStackTrace();
		}

		// After the server is started, the loop check whether the flag of terminating is set. If the terminating flag is true, the process is ended. Otherwise, the process keeps running. 08/22/2014, Bing Li
		while (!TerminateSignal.SIGNAL().isTerminated())
		{
			try
			{
				// If the terminating flag is false, it is required to sleep for some time. Otherwise, it might cause the high CPU usage. 08/22/2014, Bing Li
				Thread.sleep(ServerConfig.TERMINATE_SLEEP);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
		
		in.close();
	}

}
