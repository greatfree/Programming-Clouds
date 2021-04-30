package edu.chainnet.s3.admin;

import java.io.IOException;
import java.util.Scanner;
import java.util.Set;

import org.greatfree.admin.Menu;
import org.greatfree.chat.MenuOptions;
import org.greatfree.client.StandaloneClient;
import org.greatfree.cluster.message.HeavyWorkloadNotification;
import org.greatfree.dsf.container.cs.multinode.message.ShutdownServerNotification;
import org.greatfree.dsf.p2p.RegistryConfig;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.util.IPAddress;
import org.greatfree.util.XPathOnDiskReader;

import edu.chainnet.s3.S3Config;
import edu.chainnet.s3.Setup;
import edu.chainnet.s3.message.InitializeChildrenNotification;
import edu.chainnet.s3.message.ScaleChangedNotification;
import edu.chainnet.s3.message.ScaleChangingNotification;
import edu.chainnet.s3.message.ShutdownEDSAChildrenNotification;
import edu.chainnet.s3.message.ShutdownEDSARootNotification;
import edu.chainnet.s3.message.ShutdownMetaNotification;
import edu.chainnet.s3.message.ShutdownPoolChildrenNotification;
import edu.chainnet.s3.message.ShutdownPoolRootNotification;
import edu.chainnet.s3.message.ShutdownStorageChildrenNotification;
import edu.chainnet.s3.message.ShutdownStorageRootNotification;
import edu.chainnet.s3.message.StopOneStorageChildNotification;

/*
 * The program manages the entire system from a remote terminal. In the case, it is responsible for shutting down all of the subsystems. 07/20/2020, Bing Li
 */

// Created: 07/20/2020, Bing Li
class Admin
{
	private static String registryIP;
	private static int registryPort;
	private static IPAddress metaAddress;
	private static IPAddress edsaAddress;
	private static IPAddress storageAddress;
	private static IPAddress poolAddress;

	private static Scanner in = new Scanner(System.in);

	public static void main(String[] args) throws ClassNotFoundException, RemoteReadException, IOException, InterruptedException
	{
		
		if (Setup.setupS3())
		{
			XPathOnDiskReader reader = new XPathOnDiskReader(Setup.S3_CONFIG_XML, true);
			registryIP = reader.read(S3Config.SELECT_REGISTRY_SERVER_IP);
			registryPort = RegistryConfig.PEER_REGISTRY_PORT;
			reader.close();

			StandaloneClient.CS().init();

//			PeerAddressResponse response = (PeerAddressResponse)StandaloneClient.CS().read(registryIP, registryPort, new PeerAddressRequest(S3Config.META_SERVER_KEY));
//			metaAddress = response.getPeerAddress();
			metaAddress = StandaloneClient.CS().getIPAddress(registryIP, registryPort, S3Config.META_SERVER_KEY);
			System.out.println("metaAddress = " + metaAddress);

//			response = (PeerAddressResponse)StandaloneClient.CS().read(registryIP, registryPort, new PeerAddressRequest(S3Config.EDSA_SERVER_KEY));
//			edsaAddress = response.getPeerAddress();
			
			edsaAddress = StandaloneClient.CS().getIPAddress(registryIP, registryPort, S3Config.EDSA_SERVER_KEY);
			System.out.println("edsaAddress = " + edsaAddress);

//			response = (PeerAddressResponse)StandaloneClient.CS().read(registryIP, registryPort, new PeerAddressRequest(S3Config.STORAGE_SERVER_KEY));
//			storageAddress = response.getPeerAddress();
			
			storageAddress = StandaloneClient.CS().getIPAddress(registryIP, registryPort, S3Config.STORAGE_SERVER_KEY);
			System.out.println("storageAddress = " + storageAddress);
			
			poolAddress = StandaloneClient.CS().getIPAddress(registryIP, registryPort, S3Config.POOL_SERVER_KEY);
			System.out.println("poolAddress = " + poolAddress);

			int option = MenuOptions.NO_OPTION;
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
	}

	/*
	 * Print the console menu for the administrator. 11/27/2014, Bing Li
	 */
	private static void printMenu()
	{
		System.out.println(AdminMenu.MENU_HEAD);
		System.out.println(AdminMenu.ENLARGE_SCALE_OF_STORAGE_CLUSTER);
		System.out.println(AdminMenu.STOP_ONE_STORAGE_CHILD);
		System.out.println(AdminMenu.STOP_META_SERVER);
		System.out.println(AdminMenu.STOP_EDSA_CHILDREN);
		System.out.println(AdminMenu.STOP_EDSA_ROOT);
		System.out.println(AdminMenu.STOP_POOL_CHILDREN);
		System.out.println(AdminMenu.STOP_POOL_ROOT);
		System.out.println(AdminMenu.STOP_STORAGE_CHILDREN);
		System.out.println(AdminMenu.STOP_STORAGE_ROOT);
		System.out.println(AdminMenu.STOP_REGISTRY_SERVER);
		System.out.println(AdminMenu.QUIT);
		System.out.println(AdminMenu.MENU_TAIL);
		System.out.println(AdminMenu.INPUT_PROMPT);
	}

	private static void notifyServer(int option) throws IOException, InterruptedException, ClassNotFoundException, RemoteReadException
	{
		switch (option)
		{
			case Options.STOP_ONE_STORAGE_CHILD:
				System.out.println(AdminMenu.STOP_ONE_STORAGE_CHILD);
				StandaloneClient.CS().syncNotify(storageAddress.getIP(), storageAddress.getPort(), new StopOneStorageChildNotification());
				break;
				
			case Options.ENLARGE_SCALE_OF_STORAGE_CLUSTER:
				System.out.println(AdminMenu.ENLARGE_SCALE_OF_STORAGE_CLUSTER);
				StandaloneClient.CS().syncNotify(metaAddress.getIP(), metaAddress.getPort(), new ScaleChangingNotification());
				System.out.println("1) Meta Server is terminated temporarily ...");
				System.out.println("2) Press Enter to get the size of the Storage Cluster ...");
				in.nextLine();
				int clusterSize = StandaloneClient.CS().getClusterSize(storageAddress.getIP(), storageAddress.getPort());
				System.out.println("2) The scale of the Storage Cluster is " + clusterSize);
				System.out.println("3) Press Enter to initialize the selected children of the Pool Cluster ...");
				in.nextLine();
				Set<String> childrenKeys = StandaloneClient.CS().getChildrenKeys(poolAddress.getIP(), poolAddress.getPort(), clusterSize);
				StandaloneClient.CS().syncNotify(poolAddress.getIP(), poolAddress.getPort(), new InitializeChildrenNotification(storageAddress, childrenKeys));
				System.out.println("3) The Pool Cluster is notified to initialize ... ");
				System.out.println("4) Press Enter to force the selected children of the Pool Cluster to join the Storage Cluster ...");
				in.nextLine();
				StandaloneClient.CS().syncNotify(poolAddress.getIP(), poolAddress.getPort(), new HeavyWorkloadNotification(S3Config.STORAGE_SERVER_KEY, storageAddress, clusterSize));
				System.out.println("4) The Pool Cluster is notified to join the Storage Cluster ... ");
				System.out.println("5) Press Enter to start the Meta Server again continually ...");
				in.nextLine();
				StandaloneClient.CS().syncNotify(metaAddress.getIP(), metaAddress.getPort(), new ScaleChangedNotification());
				System.out.println("5) Meta Server continues to work ...");
				in.nextLine();
				break;

			case Options.STOP_META_SERVER:
				System.out.println(AdminMenu.STOP_META_SERVER);
				StandaloneClient.CS().syncNotify(metaAddress.getIP(), metaAddress.getPort(), new ShutdownMetaNotification());
				break;
				
			case Options.STOP_EDSA_CHILDREN:
				System.out.println(AdminMenu.STOP_EDSA_CHILDREN);
				StandaloneClient.CS().syncNotify(edsaAddress.getIP(), edsaAddress.getPort(), new ShutdownEDSAChildrenNotification());
				break;
				
			case Options.STOP_EDSA_ROOT:
				System.out.println(AdminMenu.STOP_EDSA_ROOT);
				StandaloneClient.CS().syncNotify(edsaAddress.getIP(), edsaAddress.getPort(), new ShutdownEDSARootNotification());
				break;

			case Options.STOP_POOL_CHILDREN:
				System.out.println(AdminMenu.STOP_POOL_CHILDREN);
				StandaloneClient.CS().syncNotify(poolAddress.getIP(), poolAddress.getPort(), new ShutdownPoolChildrenNotification());
				break;
				
			case Options.STOP_POOL_ROOT:
				System.out.println(AdminMenu.STOP_POOL_ROOT);
				StandaloneClient.CS().syncNotify(poolAddress.getIP(), poolAddress.getPort(), new ShutdownPoolRootNotification());
				break;
				
			case Options.STOP_STORAGE_CHILDREN:
				System.out.println(AdminMenu.STOP_STORAGE_CHILDREN);
				StandaloneClient.CS().syncNotify(storageAddress.getIP(), storageAddress.getPort(), new ShutdownStorageChildrenNotification());
				break;
				
			case Options.STOP_STORAGE_ROOT:
				System.out.println(AdminMenu.STOP_STORAGE_ROOT);
				StandaloneClient.CS().syncNotify(storageAddress.getIP(), storageAddress.getPort(), new ShutdownStorageRootNotification());
				break;
				
			case Options.STOP_REGISTRY_SERVER:
				System.out.println(AdminMenu.STOP_REGISTRY_SERVER);
				StandaloneClient.CS().syncNotify(registryIP, registryPort, new ShutdownServerNotification());
				break;
		}
	}
}
