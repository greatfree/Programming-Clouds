package org.greatfree.framework.container.multicast.client;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import org.greatfree.cluster.StandaloneClusterClient;
import org.greatfree.exceptions.RemoteIPNotExistedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.framework.container.cs.multinode.message.ShutdownServerNotification;
import org.greatfree.framework.container.multicast.message.HelloWorldAnycastNotification;
import org.greatfree.framework.container.multicast.message.HelloWorldAnycastRequest;
import org.greatfree.framework.container.multicast.message.HelloWorldAnycastResponse;
import org.greatfree.framework.container.multicast.message.HelloWorldBroadcastNotification;
import org.greatfree.framework.container.multicast.message.HelloWorldBroadcastRequest;
import org.greatfree.framework.container.multicast.message.HelloWorldBroadcastResponse;
import org.greatfree.framework.container.multicast.message.HelloWorldUnicastNotification;
import org.greatfree.framework.container.multicast.message.HelloWorldUnicastRequest;
import org.greatfree.framework.container.multicast.message.HelloWorldUnicastResponse;
import org.greatfree.framework.container.multicast.message.StopChildrenNotification;
import org.greatfree.framework.container.multicast.message.StopRootNotification;
import org.greatfree.framework.multicast.HelloWorld;
import org.greatfree.message.multicast.container.CollectedClusterResponse;
import org.greatfree.util.Tools;

/**
 * 
 * @author libing
 * 
 * 05/10/2022
 *
 */
final class ClientUI
{
	private Scanner in = new Scanner(System.in);

	private ClientUI()
	{
	}

	/*
	 * Initialize a singleton. 04/23/2017, Bing Li
	 */
	private static ClientUI instance = new ClientUI();
	
	public static ClientUI MC()
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

	public void printMenu()
	{
		System.out.println(MultiMenu.MENU_HEAD);
		System.out.println(MultiMenu.BROADCAST_NOTIFICATION);
		System.out.println(MultiMenu.ANYCAST_NOTIFICATION);
		System.out.println(MultiMenu.UNICAST_NOTIFICATION);
		System.out.println(MultiMenu.BROADCAST_REQUEST);
		System.out.println(MultiMenu.ANYCAST_REQUEST);
		System.out.println(MultiMenu.UNICAST_REQUEST);
		System.out.println(MultiMenu.STOP_CHILDREN);
		System.out.println(MultiMenu.STOP_ROOT);
		System.out.println(MultiMenu.STOP_REGISTRY);
		System.out.println(MultiMenu.QUIT);
		System.out.println(MultiMenu.MENU_TAIL);
	}
	
	public void send(int option) throws IOException, InterruptedException, ClassNotFoundException, RemoteReadException, RemoteIPNotExistedException
	{
		CollectedClusterResponse response;
		List<HelloWorldBroadcastResponse> hwbrs;
		List<HelloWorldAnycastResponse> hwars;
		List<HelloWorldUnicastResponse> hwurs;
		int index = 0;
		switch (option)
		{
			case MultiOptions.BROADCAST_NOTIFICATION:
				System.out.println("BROADCAST_NOTIFICATION is selected ...");
				StandaloneClusterClient.CONTAINER().syncNotifyRoot(new HelloWorldBroadcastNotification(new HelloWorld("xxxxxxxx")));
				break;
				
			case MultiOptions.ANYCAST_NOTIFICATION:
				System.out.println("ANYCAST_NOTIFICATION is selected ...");
				StandaloneClusterClient.CONTAINER().syncNotifyRoot(new HelloWorldAnycastNotification(new HelloWorld("yyyyyyyyy")));
				break;
				
			case MultiOptions.UNICAST_NOTIFICATION:
				System.out.println("UNICAST_NOTIFICATION is selected ...");
				StandaloneClusterClient.CONTAINER().syncNotifyRoot(new HelloWorldUnicastNotification(new HelloWorld("zzzzzzzzz")));
				break;
				
			case MultiOptions.BROADCAST_REQUEST:
				System.out.println("BROADCAST_REQUEST is selected ...");
				response = (CollectedClusterResponse)StandaloneClusterClient.CONTAINER().readRoot(new HelloWorldBroadcastRequest(new HelloWorld("xxxxxxxx")));
				hwbrs = Tools.filter(response.getResponses(), HelloWorldBroadcastResponse.class);
				index = 0;
				for (HelloWorldBroadcastResponse entry : hwbrs)
				{
					System.out.println(++index + ") " + entry.getHelloWorld().getHelloWorld());
				}
				break;
				
			case MultiOptions.ANYCAST_REQUEST:
				System.out.println("ANYCAST_REQUEST is selected ...");
				response = (CollectedClusterResponse)StandaloneClusterClient.CONTAINER().readRoot(new HelloWorldAnycastRequest(new HelloWorld("yyyyyyyyy")));
				hwars = Tools.filter(response.getResponses(), HelloWorldAnycastResponse.class);
				index = 0;
				for (HelloWorldAnycastResponse entry : hwars)
				{
					System.out.println(++index + ") " + entry.getHelloWorld().getHelloWorld());
				}
				break;
				
			case MultiOptions.UNICAST_REQUEST:
				System.out.println("UNICAST_REQUEST is selected ...");
				response = (CollectedClusterResponse)StandaloneClusterClient.CONTAINER().readRoot(new HelloWorldUnicastRequest(new HelloWorld("zzzzzzzzz")));
				hwurs = Tools.filter(response.getResponses(), HelloWorldUnicastResponse.class);
				index = 0;
				for (HelloWorldUnicastResponse entry : hwurs)
				{
					System.out.println(++index + ") " + entry.getHelloWorld().getHelloWorld());
				}
				break;
				
			case MultiOptions.STOP_CHILDREN:
				System.out.println("STOP_CHILDREN is selected ...");
				StandaloneClusterClient.CONTAINER().syncNotifyRoot(new StopChildrenNotification());
				break;
				
			case MultiOptions.STOP_ROOT:
				System.out.println("STOP_ROOT is selected ...");
				StandaloneClusterClient.CONTAINER().syncNotifyRoot(new StopRootNotification());
				break;
				
			case MultiOptions.STOP_REGISTRY:
				System.out.println("STOP_REGISTRY is selected ...");
				StandaloneClusterClient.CONTAINER().syncNotifyRegistry(new ShutdownServerNotification());
				break;
				
			case MultiOptions.QUIT:
				break;
		}
		
	}
	
}
