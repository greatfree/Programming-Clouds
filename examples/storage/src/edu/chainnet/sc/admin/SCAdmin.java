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

// Created: 10/29/2020, Bing Li
class SCAdmin
{
	private IPAddress collaboratorAddress;
	private Scanner in = new Scanner(System.in);

	private SCAdmin()
	{
	}

	private static SCAdmin instance = new SCAdmin();
	
	public static SCAdmin BC()
	{
		if (instance == null)
		{
			instance = new SCAdmin();
			return instance;
		}
		else
		{
			return instance;
		}
	}
	
	public void dispose() throws IOException, InterruptedException
	{
		StandaloneClient.CS().dispose();
		this.in.close();
	}

	public void init(String registryIP, int registryPort) throws ClassNotFoundException, RemoteReadException, IOException
	{
		StandaloneClient.CS().init();
		this.collaboratorAddress = StandaloneClient.CS().getIPAddress(registryIP, registryPort, SCConfig.COLLABORATOR_ROOT_KEY);
	}
	
	public void start() throws IOException, InterruptedException
	{
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
	}

	private void printMenu()
	{
		System.out.println(AdminMenu.MENU_HEAD);

		System.out.println(AdminMenu.STOP_COLLABORATOR_CHILDREN);
		System.out.println(AdminMenu.STOP_COLLABORATOR_ROOT);
		System.out.println(AdminMenu.STOP_REGISTRY_SERVER);
		
		System.out.println(AdminMenu.QUIT);
		System.out.println(AdminMenu.MENU_TAIL);
		System.out.println(AdminMenu.INPUT_PROMPT);
	}
	
	private void notifyServer(int option) throws IOException, InterruptedException
	{
		switch (option)
		{
			case Options.STOP_COLLABORATOR_CHILDREN:
				System.out.println(AdminMenu.STOP_COLLABORATOR_CHILDREN);
				StandaloneClient.CS().syncNotify(this.collaboratorAddress.getIP(), this.collaboratorAddress.getPort(), new StopCollaboratorClusterNotification());
				break;
				
			case Options.STOP_COLLABORATOR_ROOT:
				System.out.println(AdminMenu.STOP_COLLABORATOR_ROOT);
				StandaloneClient.CS().syncNotify(this.collaboratorAddress.getIP(), this.collaboratorAddress.getPort(), new StopCollaboratorRootNotification());
				break;
				
			case Options.STOP_REGISTRY_SERVER:
				System.out.println(AdminMenu.STOP_REGISTRY_SERVER);
				StandaloneClient.CS().syncNotify(SCConfig.REGISTRY_SERVER_IP, SCConfig.REGISTRY_SERVER_PORT, new ShutdownServerNotification());
				break;
		}
	}
}
