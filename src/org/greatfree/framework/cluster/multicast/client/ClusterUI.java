package org.greatfree.framework.cluster.multicast.client;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.greatfree.exceptions.RemoteIPNotExistedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.framework.cluster.ClusterConfig;
import org.greatfree.framework.cluster.multicast.message.HelloAnycastNotification;
import org.greatfree.framework.cluster.multicast.message.HelloAnycastRequest;
import org.greatfree.framework.cluster.multicast.message.HelloAnycastResponse;
import org.greatfree.framework.cluster.multicast.message.HelloBroadcastNotification;
import org.greatfree.framework.cluster.multicast.message.HelloBroadcastRequest;
import org.greatfree.framework.cluster.multicast.message.HelloBroadcastResponse;
import org.greatfree.framework.cluster.multicast.message.HelloInterAnycastNotification;
import org.greatfree.framework.cluster.multicast.message.HelloInterAnycastRequest;
import org.greatfree.framework.cluster.multicast.message.HelloInterAnycastResponse;
import org.greatfree.framework.cluster.multicast.message.HelloInterBroadcastNotification;
import org.greatfree.framework.cluster.multicast.message.HelloInterBroadcastRequest;
import org.greatfree.framework.cluster.multicast.message.HelloInterBroadcastResponse;
import org.greatfree.framework.cluster.multicast.message.HelloInterUnicastNotification;
import org.greatfree.framework.cluster.multicast.message.HelloInterUnicastRequest;
import org.greatfree.framework.cluster.multicast.message.HelloInterUnicastResponse;
import org.greatfree.framework.cluster.multicast.message.HelloUnicastNotification;
import org.greatfree.framework.cluster.multicast.message.HelloUnicastRequest;
import org.greatfree.framework.cluster.multicast.message.HelloUnicastResponse;
import org.greatfree.util.IPAddress;
import org.greatfree.util.Tools;

/**
 * 
 * @author libing
 * 
 * 10/02/2022
 *
 */
final class ClusterUI
{
	private IPAddress rootAddress;
	
	private ClusterUI()
	{
	}

	private static ClusterUI instance = new ClusterUI();
	
	public static ClusterUI MULTI()
	{
		if (instance == null)
		{
			instance = new ClusterUI();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	public void init() throws ClassNotFoundException, RemoteReadException, IOException, RemoteIPNotExistedException
	{
		this.rootAddress = ClusterClient.MULTI().getAddress(ClusterConfig.REGISTRY_IP, ClusterConfig.REGISTRY_PORT, ClusterConfig.ROOT_NAME);
	}

	public void printMenu()
	{
		System.out.println(ClusterMenu.MENU_HEAD);
		System.out.println(ClusterMenu.BROADCAST_NOTIFICATION);
		System.out.println(ClusterMenu.ANYCAST_NOTIFICATION);
		System.out.println(ClusterMenu.UNICAST_NOTIFICATION);
		System.out.println(ClusterMenu.BROADCAST_REQUEST);
		System.out.println(ClusterMenu.ANYCAST_REQUEST);
		System.out.println(ClusterMenu.UNICAST_REQUEST);
		System.out.println(ClusterMenu.INTER_BROADCAST_NOTIFICATION);
		System.out.println(ClusterMenu.INTER_ANYCAST_NOTIFICATION);
		System.out.println(ClusterMenu.INTER_UNICAST_NOTIFICATION);
		System.out.println(ClusterMenu.INTER_BROADCAST_REQUEST);
		System.out.println(ClusterMenu.INTER_ANYCAST_REQUEST);
		System.out.println(ClusterMenu.INTER_UNICAST_REQUEST);
		System.out.println(ClusterMenu.QUIT);
		System.out.println(ClusterMenu.MENU_TAIL);
	}
	
	public void execute(int option) throws IOException, InterruptedException, ClassNotFoundException, RemoteReadException, RemoteIPNotExistedException
	{
		Set<String> destinationKeys;
		int index = 0;
		String message = null;
		switch (option)
		{
			case MenuOptions.BROADCAST_NOTIFICATION:
				System.out.println("BROADCAST_NOTIFICATION sent @" + Calendar.getInstance().getTime());
				System.out.println("Please type your message: ");
				message = Tools.INPUT.nextLine();
				ClusterClient.MULTI().syncNotify(this.rootAddress.getIP(), this.rootAddress.getPort(), new HelloBroadcastNotification(message));
				break;

			case MenuOptions.ANYCAST_NOTIFICATION:
				System.out.println("ANYCAST_NOTIFICATION sent @" + Calendar.getInstance().getTime());
				System.out.println("Please type your message: ");
				message = Tools.INPUT.nextLine();
				ClusterClient.MULTI().syncNotify(this.rootAddress.getIP(), this.rootAddress.getPort(), new HelloAnycastNotification(message));
				break;
				
			case MenuOptions.UNICAST_NOTIFICATION:
				System.out.println("UNICAST_NOTIFICATION sent @" + Calendar.getInstance().getTime());
				System.out.println("Please type your message: ");
				message = Tools.INPUT.nextLine();
				ClusterClient.MULTI().syncNotify(this.rootAddress.getIP(), this.rootAddress.getPort(), new HelloUnicastNotification(message));
				break;
				
			case MenuOptions.BROADCAST_REQUEST:
				System.out.println("BROADCAST_REQUEST sent @" + Calendar.getInstance().getTime());
				System.out.println("Please type your message: ");
				message = Tools.INPUT.nextLine();
				List<HelloBroadcastResponse> hbrs = ClusterClient.MULTI().read(this.rootAddress.getIP(), this.rootAddress.getPort(), new HelloBroadcastRequest(message), HelloBroadcastResponse.class);
				index = 0;
				System.out.println("HelloBroadcastResponse size = " + hbrs.size());
				for (HelloBroadcastResponse entry : hbrs)
				{
					System.out.println(++index + ") response = " + entry.getMessage());
				}
				break;
				
			case MenuOptions.ANYCAST_REQUEST:
				System.out.println("ANYCAST_REQUEST sent @" + Calendar.getInstance().getTime());
				System.out.println("Please type your message: ");
				message = Tools.INPUT.nextLine();
				List<HelloAnycastResponse> hars = ClusterClient.MULTI().read(this.rootAddress.getIP(), this.rootAddress.getPort(), new HelloAnycastRequest(message), HelloAnycastResponse.class);
				index = 0;
				System.out.println("HelloAnycastResponse size = " + hars.size());
				for (HelloAnycastResponse entry : hars)
				{
					System.out.println(++index + ") response = " + entry.getMessage());
				}
				break;
				
			case MenuOptions.UNICAST_REQUEST:
				System.out.println("UNICAST_REQUEST sent @" + Calendar.getInstance().getTime());
				System.out.println("Please type your message: ");
				message = Tools.INPUT.nextLine();
				List<HelloUnicastResponse> hurs = ClusterClient.MULTI().read(this.rootAddress.getIP(), this.rootAddress.getPort(), new HelloUnicastRequest(message), HelloUnicastResponse.class);
				index = 0;
				System.out.println("HelloUnicastResponse size = " + hurs.size());
				for (HelloUnicastResponse entry : hurs)
				{
					System.out.println(++index + ") response = " + entry.getMessage());
				}
				break;
				
			case MenuOptions.INTER_BROADCAST_NOTIFICATION:
				System.out.println("INTER_BROADCAST_NOTIFICATION sent @" + Calendar.getInstance().getTime());
				System.out.println("Please type your message: ");
				message = Tools.INPUT.nextLine();
				destinationKeys = new HashSet<String>();
				for (int i = 0; i < ClusterConfig.INTER_CHILDREN_COUNT; i++)
				{
					destinationKeys.add(Tools.generateUniqueKey());
				}
				System.out.println("destinationKeys' size = " + destinationKeys.size());
				ClusterClient.MULTI().syncNotify(this.rootAddress.getIP(), this.rootAddress.getPort(), new HelloInterBroadcastNotification(message, destinationKeys));
				break;
				
			case MenuOptions.INTER_ANYCAST_NOTIFICATION:
				System.out.println("INTER_ANYCAST_NOTIFICATION sent @" + Calendar.getInstance().getTime());
				System.out.println("Please type your message: ");
				message = Tools.INPUT.nextLine();
				destinationKeys = new HashSet<String>();
				for (int i = 0; i < ClusterConfig.INTER_CHILDREN_COUNT; i++)
				{
					destinationKeys.add(Tools.generateUniqueKey());
				}
				System.out.println("destinationKeys' size = " + destinationKeys.size());
				ClusterClient.MULTI().syncNotify(this.rootAddress.getIP(), this.rootAddress.getPort(), new HelloInterAnycastNotification(message, destinationKeys));
				break;
				
			case MenuOptions.INTER_UNICAST_NOTIFICATION:
				System.out.println("INTER_UNICAST_NOTIFICATION sent @" + Calendar.getInstance().getTime());
				System.out.println("Please type your message: ");
				message = Tools.INPUT.nextLine();
				ClusterClient.MULTI().syncNotify(this.rootAddress.getIP(), this.rootAddress.getPort(), new HelloInterUnicastNotification(message));
				break;
				
			case MenuOptions.INTER_BROADCAST_REQUEST:
				System.out.println("INTER_BROADCAST_REQUEST sent @" + Calendar.getInstance().getTime());
				System.out.println("Please type your message: ");
				message = Tools.INPUT.nextLine();
				destinationKeys = new HashSet<String>();
				for (int i = 0; i < ClusterConfig.INTER_CHILDREN_COUNT; i++)
				{
					destinationKeys.add(Tools.generateUniqueKey());
				}
				System.out.println("destinationKeys' size = " + destinationKeys.size());
				List<HelloInterBroadcastResponse> hibrs = ClusterClient.MULTI().read(this.rootAddress.getIP(), this.rootAddress.getPort(), new HelloInterBroadcastRequest(message, destinationKeys), HelloInterBroadcastResponse.class);
				index = 0;
				System.out.println("HelloInterBroadcastResponse size = " + hibrs.size());
				for (HelloInterBroadcastResponse entry : hibrs)
				{
					for (String msg : entry.getMessages())
					{
						System.out.println(++index + ") response = " + msg);
					}
				}
				System.out.println("INTER_BROADCAST_RESPONSE received @" + Calendar.getInstance().getTime());
				break;
				
			case MenuOptions.INTER_ANYCAST_REQUEST:
				System.out.println("INTER_ANYCAST_REQUEST sent @" + Calendar.getInstance().getTime());
				System.out.println("Please type your message: ");
				message = Tools.INPUT.nextLine();
				destinationKeys = new HashSet<String>();
				for (int i = 0; i < ClusterConfig.INTER_CHILDREN_COUNT; i++)
				{
					destinationKeys.add(Tools.generateUniqueKey());
				}
				List<HelloInterAnycastResponse> hiars = ClusterClient.MULTI().read(this.rootAddress.getIP(), this.rootAddress.getPort(), new HelloInterAnycastRequest(message, destinationKeys), HelloInterAnycastResponse.class);
				index = 0;
				System.out.println("HelloInterAnycastResponse size = " + hiars.size());
				for (HelloInterAnycastResponse entry : hiars)
				{
					for (String msg : entry.getMessages())
					{
						System.out.println(++index + ") response = " + msg);
					}
				}
				break;
				
			case MenuOptions.INTER_UNICAST_REQUEST:
				System.out.println("INTER_UNICAST_REQUEST sent @" + Calendar.getInstance().getTime());
				System.out.println("Please type your message: ");
				message = Tools.INPUT.nextLine();
				List<HelloInterUnicastResponse> hiurs = ClusterClient.MULTI().read(this.rootAddress.getIP(), this.rootAddress.getPort(), new HelloInterUnicastRequest(message), HelloInterUnicastResponse.class);
				index = 0;
				System.out.println("HelloInterUnicastResponse size = " + hiurs.size());
				for (HelloInterUnicastResponse entry : hiurs)
				{
					System.out.println(++index + ") response = " + entry.getMessage());
				}
				break;
		}
	}
}

