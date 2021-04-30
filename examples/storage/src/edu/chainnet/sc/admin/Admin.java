package edu.chainnet.sc.admin;

import java.io.IOException;
import java.util.Scanner;

import org.greatfree.admin.Menu;
import org.greatfree.client.StandaloneClient;
import org.greatfree.dsf.container.cs.multinode.message.ShutdownServerNotification;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.util.IPAddress;

import edu.chainnet.sc.SCConfig;
import edu.chainnet.sc.message.StopCollaboratorClusterNotification;
import edu.chainnet.sc.message.StopCollaboratorRootNotification;

// Created: 10/20/2020, Bing Li
class Admin
{
	private static IPAddress collaboratorAddress;

	private static Scanner in = new Scanner(System.in);

	public static void main(String[] args) throws ClassNotFoundException, RemoteReadException, IOException, InterruptedException
	{
		StandaloneClient.CS().init();

		collaboratorAddress = StandaloneClient.CS().getIPAddress(SCConfig.REGISTRY_SERVER_IP, SCConfig.REGISTRY_SERVER_PORT, SCConfig.COLLABORATOR_ROOT_KEY);
		System.out.println("Collaborator Address = " + collaboratorAddress);
		
		int option = Options.NO_OPTION;
		String optionStr;
		
		while (option != Options.QUIT)
		{
			printMenu();
			optionStr = in.nextLine();
			try
			{
				option = Integer.parseInt(optionStr);
				System.out.println("Your choice is: " + option);
				notifyServer(option);
			}
			catch (NumberFormatException e)
			{
				option = Options.NO_OPTION;
				System.out.println(Menu.WRONG_OPTION);
			}
		}

		StandaloneClient.CS().dispose();
		in.close();
	}

	private static void printMenu()
	{
		System.out.println(AdminMenu.MENU_HEAD);

		System.out.println(AdminMenu.STOP_COLLABORATOR_CHILDREN);
		System.out.println(AdminMenu.STOP_COLLABORATOR_ROOT);
		System.out.println(AdminMenu.STOP_REGISTRY_SERVER);
		
		System.out.println(AdminMenu.QUIT);
		System.out.println(AdminMenu.MENU_TAIL);
		System.out.println(AdminMenu.INPUT_PROMPT);
	}
	
	private static void notifyServer(int option) throws IOException, InterruptedException
	{
		switch (option)
		{
			case Options.STOP_COLLABORATOR_CHILDREN:
				System.out.println(AdminMenu.STOP_COLLABORATOR_CHILDREN);
				StandaloneClient.CS().syncNotify(collaboratorAddress.getIP(), collaboratorAddress.getPort(), new StopCollaboratorClusterNotification());
				break;
				
			case Options.STOP_COLLABORATOR_ROOT:
				System.out.println(AdminMenu.STOP_COLLABORATOR_ROOT);
				StandaloneClient.CS().syncNotify(collaboratorAddress.getIP(), collaboratorAddress.getPort(), new StopCollaboratorRootNotification());
				break;
				
			case Options.STOP_REGISTRY_SERVER:
				System.out.println(AdminMenu.STOP_REGISTRY_SERVER);
				StandaloneClient.CS().syncNotify(SCConfig.REGISTRY_SERVER_IP, SCConfig.REGISTRY_SERVER_PORT, new ShutdownServerNotification());
				break;
		}
	}
}
