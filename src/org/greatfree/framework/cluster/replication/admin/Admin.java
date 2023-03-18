package org.greatfree.framework.cluster.replication.admin;

import java.io.IOException;
import java.util.Scanner;

import org.greatfree.admin.Menu;
import org.greatfree.chat.MenuOptions;
import org.greatfree.client.StandaloneClient;
import org.greatfree.exceptions.RemoteIPNotExistedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.framework.cluster.replication.ReplicationConfig;
import org.greatfree.framework.cluster.replication.message.StopReplicationClusterNotification;
import org.greatfree.framework.cluster.replication.message.StopReplicationRootNotification;
import org.greatfree.framework.container.cs.multinode.message.ShutdownServerNotification;
import org.greatfree.framework.p2p.RegistryConfig;
import org.greatfree.util.IPAddress;

// Created: 09/07/2020, Bing Li
class Admin
{
	private static String registryIP = RegistryConfig.PEER_REGISTRY_ADDRESS;
	private static int registryPort = RegistryConfig.PEER_REGISTRY_PORT;
	private static IPAddress replicationClusterIP;

	public static void main(String[] args) throws ClassNotFoundException, RemoteReadException, IOException, InterruptedException, RemoteIPNotExistedException
	{
		StandaloneClient.CS().init();
		replicationClusterIP = StandaloneClient.CS().getIPAddress(registryIP, registryPort, ReplicationConfig.REPLICATION_ROOT_KEY);
		System.out.println("replicationClusterIP = " + replicationClusterIP);
		
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
		System.out.println(AdminMenu.STOP_REPLICATION_CLUSTER_CHILDREN);
		System.out.println(AdminMenu.STOP_REPLICATION_ROOT);
		System.out.println(AdminMenu.STOP_REGISTRY_SERVER);
		System.out.println(AdminMenu.QUIT);
		System.out.println(AdminMenu.MENU_TAIL);
		System.out.println(AdminMenu.INPUT_PROMPT);
	}

	private static void notifyServer(int option) throws IOException, InterruptedException
	{
		switch (option)
		{
			case Options.STOP_REPLICATION_CLUSTER_CHILDREN:
				System.out.println(AdminMenu.STOP_REPLICATION_CLUSTER_CHILDREN);
				StandaloneClient.CS().syncNotify(replicationClusterIP.getIP(), replicationClusterIP.getPort(), new StopReplicationClusterNotification());
				break;
				
			case Options.STOP_REPLICATION_ROOT:
				System.out.println(AdminMenu.STOP_REPLICATION_ROOT);
				StandaloneClient.CS().syncNotify(replicationClusterIP.getIP(), replicationClusterIP.getPort(), new StopReplicationRootNotification());
				break;
				
			case Options.STOP_REGISTRY_SERVER:
				System.out.println(AdminMenu.STOP_REGISTRY_SERVER);
				StandaloneClient.CS().syncNotify(registryIP, registryPort, new ShutdownServerNotification());
				break;
		}
	}
}
