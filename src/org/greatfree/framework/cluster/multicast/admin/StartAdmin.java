package org.greatfree.framework.cluster.multicast.admin;

import java.io.IOException;

import org.greatfree.exceptions.RemoteIPNotExistedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.framework.cluster.ClusterConfig;
import org.greatfree.framework.cluster.multicast.client.ClusterClient;
import org.greatfree.framework.cluster.multicast.message.ShutdownRootNotification;
import org.greatfree.framework.cluster.multicast.message.ShutdownChildrenNotification;
import org.greatfree.framework.container.cs.multinode.message.ShutdownServerNotification;
import org.greatfree.util.IPAddress;
import org.greatfree.util.Tools;

/**
 * 
 * @author libing
 * 
 * 03/10/2023
 *
 */
final class StartAdmin
{
	private static IPAddress rootAddress;

	public static void main(String[] args) throws ClassNotFoundException, RemoteReadException, IOException, RemoteIPNotExistedException, InterruptedException
	{
		ClusterClient.MULTI().init();
		int option = AdminOptions.NO_OPTION;
		String optionStr;
		rootAddress = ClusterClient.MULTI().getAddress(ClusterConfig.REGISTRY_IP, ClusterConfig.REGISTRY_PORT, ClusterConfig.ROOT_NAME);
		while (option != AdminOptions.QUIT)
		{
			printMenu();
			optionStr = Tools.INPUT.nextLine();
			try
			{
				option = Integer.parseInt(optionStr);
				System.out.println("Your choice is: " + option);
				execute(option);
			}
			catch (NumberFormatException e)
			{
				option = AdminOptions.NO_OPTION;
				System.out.println(AdminMenu.WRONG_OPTION);
			}
		}
		ClusterClient.MULTI().dispose();
	}

	private static void printMenu()
	{
		System.out.println(AdminMenu.MENU_HEAD);
		System.out.println(AdminMenu.SHUTDOWN_CHILDREN);
		System.out.println(AdminMenu.SHUTDOWN_ROOT);
		System.out.println(AdminMenu.STOP_REGISTRY_SERVER);
		System.out.println(AdminMenu.QUIT);
		System.out.println(AdminMenu.MENU_TAIL);
		System.out.println(AdminMenu.INPUT_PROMPT);
	}

	private static void execute(int option) throws IOException, InterruptedException
	{
		switch (option)
		{
			case AdminOptions.SHUTDOWN_CHILDREN:
				ClusterClient.MULTI().syncNotify(rootAddress.getIP(), rootAddress.getPort(), new ShutdownChildrenNotification());
				break;
				
			case AdminOptions.SHUTDOWN_ROOT:
				ClusterClient.MULTI().syncNotify(rootAddress.getIP(), rootAddress.getPort(), new ShutdownRootNotification());
				break;
				
			case AdminOptions.SHUTDOWN_REGISTRY_SERVER:
				ClusterClient.MULTI().syncNotify(ClusterConfig.REGISTRY_IP, ClusterConfig.REGISTRY_PORT, new ShutdownServerNotification());
				break;
		}
	}
}
