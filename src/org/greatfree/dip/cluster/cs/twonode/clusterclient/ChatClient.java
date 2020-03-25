package org.greatfree.dip.cluster.cs.twonode.clusterclient;

import java.io.IOException;
import java.util.List;

import org.greatfree.cluster.root.container.ClusterPeerContainer;
import org.greatfree.data.ServerConfig;
import org.greatfree.dip.container.p2p.message.PeerAddressRequest;
import org.greatfree.dip.multicast.MulticastConfig;
import org.greatfree.dip.multicast.message.PeerAddressResponse;
import org.greatfree.dip.p2p.RegistryConfig;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.message.ServerMessage;
import org.greatfree.message.multicast.MulticastMessage;
import org.greatfree.message.multicast.MulticastRequest;
import org.greatfree.message.multicast.MulticastResponse;
import org.greatfree.util.IPAddress;

// Created: 01/15/2019, Bing Li
class ChatClient
{
	private IPAddress rootAddress;
	private ClusterPeerContainer client;

	private ChatClient()
	{
	}

	/*
	 * Initialize a singleton. 04/23/2017, Bing Li
	 */
	private static ChatClient instance = new ChatClient();
	
	public static ChatClient CCC()
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

	/*
	public void stopCluster() throws IOException, DistributedNodeFailedException
	{
		ServerStatus.FREE().setShutdown();
		this.client.stopCluster();
	}
	*/
	
	public void stop() throws ClassNotFoundException, IOException, InterruptedException, RemoteReadException, DistributedNodeFailedException
	{
		this.client.stop(ServerConfig.SERVER_SHUTDOWN_TIMEOUT);
	}
	
	public void init(String clientName) throws IOException, ClassNotFoundException, RemoteReadException, DistributedNodeFailedException
	{
		this.client = new ClusterPeerContainer(clientName, new ChatClientTask());
		this.client.start();
		
		PeerAddressResponse response = (PeerAddressResponse)this.client.read(RegistryConfig.PEER_REGISTRY_ADDRESS,  RegistryConfig.PEER_REGISTRY_PORT, new PeerAddressRequest(MulticastConfig.CLUSTER_SERVER_ROOT_KEY));
		this.rootAddress = response.getPeerAddress();
	}
	
	/*
	 * For testing only. 01/16/2019, Bing Li
	 */
	/*
	public CSClient getPeerClient()
	{
		return this.client.getPeerClient();
	}
	*/

	public void syncNotify(ServerMessage notification) throws IOException, InterruptedException
	{
		this.client.syncNotify(this.rootAddress.getIP(), this.rootAddress.getPort(), notification);
	}
	
	public void asyncNotify(ServerMessage notification)
	{
		this.client.asyncNotify(this.rootAddress.getIP(), this.rootAddress.getPort(), notification);
	}
	
	public ServerMessage read(ServerMessage request) throws ClassNotFoundException, RemoteReadException, IOException
	{
		return this.client.read(this.rootAddress.getIP(), this.rootAddress.getPort(), request);
	}
	
	public void asyncBroadcastNotify(MulticastMessage notification) throws IOException, DistributedNodeFailedException
	{
		this.client.asyncBroadcastNotify(notification);
	}
	
	public List<MulticastResponse> broadcastRead(MulticastRequest request) throws DistributedNodeFailedException, IOException
	{
		return this.client.broadcastRead(request);
	}
}

