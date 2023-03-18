package org.greatfree.framework.cluster.scalable.admin;

import java.io.IOException;
import java.util.Scanner;

import org.greatfree.admin.Menu;
import org.greatfree.chat.MenuOptions;
import org.greatfree.client.StandaloneClient;
import org.greatfree.exceptions.RemoteIPNotExistedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.framework.cluster.scalable.ScalableConfig;
import org.greatfree.framework.cluster.scalable.message.StopPoolClusterNotification;
import org.greatfree.framework.cluster.scalable.message.StopPoolRootNotification;
import org.greatfree.framework.cluster.scalable.message.StopTaskClusterNotification;
import org.greatfree.framework.cluster.scalable.message.StopTaskRootNotification;
import org.greatfree.framework.container.cs.multinode.message.ShutdownServerNotification;
import org.greatfree.framework.p2p.RegistryConfig;
import org.greatfree.util.IPAddress;

// Created: 09/06/2020, Bing Li
class Admin
{
	private static String registryIP = RegistryConfig.PEER_REGISTRY_ADDRESS;
	private static int registryPort = RegistryConfig.PEER_REGISTRY_PORT;
	private static IPAddress poolClusterIP;
	private static IPAddress taskClusterIP;

	public static void main(String[] args) throws ClassNotFoundException, RemoteReadException, InterruptedException, RemoteIPNotExistedException, IOException
	{
		StandaloneClient.CS().init();
		poolClusterIP = StandaloneClient.CS().getIPAddress(registryIP, registryPort, ScalableConfig.POOL_CLUSTER_ROOT_KEY);
		System.out.println("poolClusterIP = " + poolClusterIP);
		taskClusterIP = StandaloneClient.CS().getIPAddress(registryIP, registryPort, ScalableConfig.TASK_CLUSTER_ROOT_KEY);
		System.out.println("taskClusterIP = " + taskClusterIP);
		
		int option = MenuOptions.NO_OPTION;
		Scanner in = new Scanner(System.in);
		String optionStr;
		
		while (option != MenuOptions.QUIT)
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
		System.out.println(AdminMenu.STOP_POOL_CLUSTER_CHILDREN);
		System.out.println(AdminMenu.STOP_POOL_ROOT);
		System.out.println(AdminMenu.STOP_TASK_CLUSTER_CHILDREN);
		System.out.println(AdminMenu.STOP_TASK_ROOT);
		System.out.println(AdminMenu.STOP_REGISTRY_SERVER);
		System.out.println(AdminMenu.QUIT);
		System.out.println(AdminMenu.MENU_TAIL);
		System.out.println(AdminMenu.INPUT_PROMPT);
	}

	private static void notifyServer(int option) throws IOException, InterruptedException
	{
		switch (option)
		{
			case Options.STOP_POOL_CLUSTER_CHILDREN:
				System.out.println(AdminMenu.STOP_POOL_CLUSTER_CHILDREN);
				StandaloneClient.CS().syncNotify(poolClusterIP.getIP(), poolClusterIP.getPort(), new StopPoolClusterNotification());
				break;
				
			case Options.STOP_POOL_ROOT:
				System.out.println(AdminMenu.STOP_POOL_ROOT);
				StandaloneClient.CS().syncNotify(poolClusterIP.getIP(), poolClusterIP.getPort(), new StopPoolRootNotification());
				break;
				
			case Options.STOP_TASK_CLUSTER_CHIDLREN:
				System.out.println(AdminMenu.STOP_TASK_CLUSTER_CHILDREN);
				StandaloneClient.CS().syncNotify(taskClusterIP.getIP(), taskClusterIP.getPort(), new StopTaskClusterNotification());
				break;
				
			case Options.STOP_TASK_ROOT:
				System.out.println(AdminMenu.STOP_TASK_ROOT);
				StandaloneClient.CS().syncNotify(taskClusterIP.getIP(), taskClusterIP.getPort(), new StopTaskRootNotification());
				break;
				
			case Options.STOP_REGISTRY_SERVER:
				System.out.println(AdminMenu.STOP_REGISTRY_SERVER);
				StandaloneClient.CS().syncNotify(registryIP, registryPort, new ShutdownServerNotification());
				break;
		}
	}
}

