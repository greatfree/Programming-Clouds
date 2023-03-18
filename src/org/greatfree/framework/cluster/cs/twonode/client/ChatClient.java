package org.greatfree.framework.cluster.cs.twonode.client;

import java.io.IOException;

import org.greatfree.client.StandaloneClient;
import org.greatfree.exceptions.RemoteIPNotExistedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.framework.container.p2p.message.PeerAddressRequest;
import org.greatfree.framework.multicast.MulticastConfig;
import org.greatfree.framework.p2p.RegistryConfig;
import org.greatfree.message.PeerAddressResponse;
import org.greatfree.message.ServerMessage;
import org.greatfree.util.IPAddress;

// Created: 01/13/2019, Bing Li
public class ChatClient
{
	private IPAddress rootAddress;

	private ChatClient()
	{
	}

	/*
	 * Initialize a singleton. 04/23/2017, Bing Li
	 */
	private static ChatClient instance = new ChatClient();
	
	public static ChatClient CONTAINER()
	{
		if (instance == null)
		{
			instance = new ChatClient();
			return instance;
		}
		else
		{
			return instance;
		}
	}
	
	public void dispose() throws IOException, InterruptedException, ClassNotFoundException
	{
		StandaloneClient.CS().dispose();
	}
	
	public void initWithout() throws ClassNotFoundException, RemoteReadException, RemoteIPNotExistedException, IOException
	{
		StandaloneClient.CS().init();
		
		PeerAddressResponse response = (PeerAddressResponse)StandaloneClient.CS().read(RegistryConfig.PEER_REGISTRY_ADDRESS,  RegistryConfig.PEER_REGISTRY_PORT, new PeerAddressRequest(MulticastConfig.CLUSTER_SERVER_ROOT_KEY));
		this.rootAddress = response.getPeerAddress();
		System.out.println("root IP = " + this.rootAddress.getIP() + ", port = " + this.rootAddress.getPort());
	}

	public void init() throws ClassNotFoundException, RemoteReadException, RemoteIPNotExistedException, IOException
	{
//		StandaloneClient.CS().init(Scheduler.GREATFREE().getScheduler());
		StandaloneClient.CS().init();
		
		PeerAddressResponse response = (PeerAddressResponse)StandaloneClient.CS().read(RegistryConfig.PEER_REGISTRY_ADDRESS,  RegistryConfig.PEER_REGISTRY_PORT, new PeerAddressRequest(MulticastConfig.CLUSTER_SERVER_ROOT_KEY));
		this.rootAddress = response.getPeerAddress();
	}

	public void syncNotify(ServerMessage notification) throws IOException, InterruptedException
	{
		StandaloneClient.CS().syncNotify(this.rootAddress.getIP(), this.rootAddress.getPort(), notification);
	}
	
	public void asyncNotify(ServerMessage notification)
	{
		StandaloneClient.CS().asyncNotify(this.rootAddress.getIP(), this.rootAddress.getPort(), notification);
	}
	
	public ServerMessage read(ServerMessage request) throws ClassNotFoundException, RemoteReadException, RemoteIPNotExistedException
	{
		return StandaloneClient.CS().read(this.rootAddress.getIP(), this.rootAddress.getPort(), request);
	}
}
