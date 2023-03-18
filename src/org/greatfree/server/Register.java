package org.greatfree.server;

import java.net.SocketException;

import org.greatfree.client.CSClient;
import org.greatfree.exceptions.RemoteIPNotExistedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.exceptions.ServerPortConflictedException;
import org.greatfree.message.RegisterPeerRequest;
import org.greatfree.message.RegisterPeerResponse;
import org.greatfree.message.ServerMessage;
import org.greatfree.message.UnregisterPeerRequest;
import org.greatfree.util.Tools;

// Created: 08/06/2018, Bing Li
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
	// The application registry server IP. 05/01/2017, Bing Li
//	private String appRegistryServerIP;
	// The application registry server port. 05/01/2017, Bing Li
//	private int appRegistryServerPort;
	// The peer IP. 05/01/2017, Bing Li
	private final String peerIP;
	// The peer port. 05/01/2017, Bing Li
//	private int peerPort;
//	private int adminPort;
	
//	private IPAddress ipAddress;

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
//		System.out.println("Register(): isRegistryNeeded = " + this.isRegistryNeeded);
	}
	
	public String getPeerID()
	{
		return this.peerID;
	}
	
	public String getPeerName()
	{
		return this.peerName;
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
	
	public void unregister(CSClient client) throws ClassNotFoundException, RemoteReadException, RemoteIPNotExistedException
	{
		if (this.isRegistryNeeded)
		{
			client.read(this.registryServerIP, this.registryServerPort, new UnregisterPeerRequest(this.peerID));
		}
	}
	
	public void register(CSClient client, Peer<Dispatcher> peer) throws ClassNotFoundException, RemoteReadException, RemoteIPNotExistedException, ServerPortConflictedException
	{
		// To avoid the possibility of port conflict, which happens when multiple peers run on the same machine, it is necessary to obtain an idle port from the registry server. 05/01/2017, Bing Li
//		System.out.println("1) Register-register(): starting to register ...");
		if (this.isRegistryNeeded)
		{
//				System.out.println("2) Register-register(): starting to register ...");
			RegisterPeerResponse response = (RegisterPeerResponse)client.read(this.registryServerIP, this.registryServerPort, new RegisterPeerRequest(this.peerID, this.peerName, this.peerIP, peer.getPort()));
//			System.out.println("\n===================");
//			System.out.println("response.getPeerPort() = " + response.getPeerPort());
//			System.out.println("response.getAdminPort() = " + response.getAdminPort());
//			System.out.println("===================\n");
			
			if (peer.getPort() != response.getPeerPort())
			{
					// Initialize the peer port, which is obtained from the registry server. 05/01/2017, Bing Li
	//				super.getPort() = response.getPort();
					// Set the port of the parent class, CSServer, to get ready for startup. 05/01/2017, Bing Li
	//				this.setPort(this.peerPort);
	//				this.adminPort = response.getAdminPort();
				peer.setPort(response.getPeerPort());
			}
			else
			{
				/*
				 * Since the ServerSocket is initialized, it is not reasonable to initialize it again with the same port. 06/18/2022, Bing Li
				 */
				peer.setPort();
			}
			System.out.println("\n===================");
			System.out.println("Child IP Address: " + peer.getPeerIP() + ":" + peer.getPort());
			System.out.println("===================\n");
		}
		else
		{
			/*
			 * Since the ServerSocket is initialized, it is not reasonable to initialize it again with the same port. 06/18/2022, Bing Li
			 */
			peer.setPort();
		}
			
		this.localIPKey = Tools.getKeyOfFreeClient(this.peerIP, peer.getPort());
	}
}
