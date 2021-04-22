package org.greatfree.server.container;

import java.io.IOException;
import java.net.SocketException;

import org.greatfree.client.CSClient;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.framework.container.p2p.message.RegisterPeerRequest;
import org.greatfree.framework.container.p2p.message.UnregisterPeerRequest;
import org.greatfree.message.RegisterPeerResponse;
import org.greatfree.message.ServerMessage;
import org.greatfree.server.ServerDispatcher;
import org.greatfree.util.Tools;

// Created: 01/13/2019, Bing Li
class Register<Dispatcher extends ServerDispatcher<ServerMessage>>
{
	private final String peerID;
	// The name that makes sense to humans for the peer. 05/01/2017, Bing Li
	private final String peerName;
	// The system registry server IP. 05/01/2017, Bing Li
	private final String registryServerIP;
	// The system registry server port. 05/01/2017, Bing Li
	private final int registryServerPort;
	private final boolean isRegistryNeeded;
	// The peer IP. 05/01/2017, Bing Li
	private final String peerIP;
	// The local IP key. The key is used to avoid the local node sends messages to itself. 05/19/2017, Bing Li
	private String localIPKey;

	public Register(String peerName, String registryServerIP, int registryServerPort, boolean isRegistryNeeded) throws SocketException
	{
		this.peerName = peerName;
		this.peerID = Tools.getHash(this.peerName);
		this.peerIP = Tools.getLocalIP();
		this.registryServerIP = registryServerIP;
		this.registryServerPort = registryServerPort;
		this.isRegistryNeeded = isRegistryNeeded;
	}
	
	public String getPeerName()
	{
		return this.peerName;
	}
	
	public String getPeerID()
	{
		return this.peerID;
	}
	
	public String getPeerIP()
	{
		return this.peerIP;
	}
	
	public String getLocalIPKey()
	{
		return this.localIPKey;
	}
	
	public String getRegistryIP()
	{
		return this.registryServerIP;
	}
	
	public int getRegistryPort()
	{
		return this.registryServerPort;
	}
	
	public void unregister(CSClient client) throws ClassNotFoundException, RemoteReadException, IOException
	{
		if (this.isRegistryNeeded)
		{
			client.read(this.registryServerIP, this.registryServerPort, new UnregisterPeerRequest(this.peerID));
		}
	}
	
	public void register(CSClient client, Peer<Dispatcher> peer) throws IOException, ClassNotFoundException, RemoteReadException
	{
		// To avoid the possibility of port conflict, which happens when multiple peers run on the same machine, it is necessary to obtain an idle port from the registry server. 05/01/2017, Bing Li
		if (this.isRegistryNeeded)
		{
			RegisterPeerResponse response = (RegisterPeerResponse)client.read(this.registryServerIP, this.registryServerPort, new RegisterPeerRequest(this.peerID, this.peerName, this.peerIP, peer.getPort()));
			
			if (peer.getPort() != response.getPeerPort())
			{
				// Initialize the peer port, which is obtained from the registry server. 05/01/2017, Bing Li
				// Set the port of the parent class, CSServer, to get ready for startup. 05/01/2017, Bing Li
				peer.setPort(response.getPeerPort());
			}
			else
			{
				peer.setPort();
			}
			System.out.println("\n===================");
			System.out.println("Child IP Address: " + peer.getPeerIP() + ":" + peer.getPort());
			System.out.println("===================\n");
		}
		else
		{
			peer.setPort();
		}
			
		this.localIPKey = Tools.getKeyOfFreeClient(this.peerIP, peer.getPort());
	}
}
