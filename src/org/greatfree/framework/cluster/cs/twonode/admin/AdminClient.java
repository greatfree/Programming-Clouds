package org.greatfree.framework.cluster.cs.twonode.admin;

import java.io.IOException;

import org.greatfree.client.StandaloneClient;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.framework.container.p2p.message.PeerAddressRequest;
import org.greatfree.framework.multicast.MulticastConfig;
import org.greatfree.framework.p2p.RegistryConfig;
import org.greatfree.message.PeerAddressResponse;
import org.greatfree.message.ServerMessage;
import org.greatfree.util.IPAddress;

// Created: 01/13/2019, Bing Li
class AdminClient
{
	private IPAddress rootAddress;

	private AdminClient()
	{
	}

	/*
	 * Initialize a singleton. 04/23/2017, Bing Li
	 */
	private static AdminClient instance = new AdminClient();
	
	public static AdminClient CONTAINER()
	{
		if (instance == null)
		{
			instance = new AdminClient();
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
	}

	public void init() throws ClassNotFoundException, RemoteReadException, IOException
	{
		StandaloneClient.CS().init();
		
		PeerAddressResponse response = (PeerAddressResponse)StandaloneClient.CS().read(RegistryConfig.PEER_REGISTRY_ADDRESS,  RegistryConfig.PEER_REGISTRY_PORT, new PeerAddressRequest(MulticastConfig.CLUSTER_SERVER_ROOT_KEY));
		this.rootAddress = response.getPeerAddress();
	}
	
	public void asyncNotifyRegistry(ServerMessage notification)
	{
		StandaloneClient.CS().asyncNotify(RegistryConfig.PEER_REGISTRY_ADDRESS, RegistryConfig.PEER_REGISTRY_PORT, notification);
	}

	public void syncNotify(ServerMessage notification) throws IOException, InterruptedException
	{
		StandaloneClient.CS().syncNotify(this.rootAddress.getIP(), this.rootAddress.getPort(), notification);
	}
	
	public void asyncNotify(ServerMessage notification)
	{
		StandaloneClient.CS().asyncNotify(this.rootAddress.getIP(), this.rootAddress.getPort(), notification);
	}
	
	public ServerMessage read(ServerMessage request) throws ClassNotFoundException, RemoteReadException, IOException
	{
		return StandaloneClient.CS().read(this.rootAddress.getIP(), this.rootAddress.getPort(), request);
	}
}
