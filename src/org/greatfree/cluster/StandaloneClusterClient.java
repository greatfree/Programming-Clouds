package org.greatfree.cluster;

import java.io.IOException;
import java.util.logging.Logger;

import org.greatfree.client.StandaloneClient;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.framework.container.p2p.message.PeerAddressRequest;
import org.greatfree.message.PeerAddressResponse;
import org.greatfree.message.ServerMessage;
import org.greatfree.util.IPAddress;

// Created: 01/14/2019, Bing Li
public class StandaloneClusterClient
{
	private final static Logger log = Logger.getLogger("org.greatfree.cluster");

	private IPAddress rootAddress;
	private String registryIP;
	private int registryPort;

	private StandaloneClusterClient()
	{
	}

	/*
	 * Initialize a singleton. 04/23/2017, Bing Li
	 */
	private static StandaloneClusterClient instance = new StandaloneClusterClient();
	
	public static StandaloneClusterClient CONTAINER()
	{
		if (instance == null)
		{
			instance = new StandaloneClusterClient();
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
	
	public void init(String registryIP, int registryPort, String rootKey) throws ClassNotFoundException, RemoteReadException, IOException
	{
		StandaloneClient.CS().init();
		this.registryIP = registryIP;
		this.registryPort = registryPort;
		PeerAddressResponse response = (PeerAddressResponse)StandaloneClient.CS().read(registryIP,  registryPort, new PeerAddressRequest(rootKey));
		this.rootAddress = response.getPeerAddress();
	}

	public void syncNotify(String ip, int port, ServerMessage notification) throws IOException, InterruptedException
	{
		StandaloneClient.CS().syncNotify(ip, port, notification);
	}
	
	public void asyncNotify(String ip, int port, ServerMessage notification)
	{
		StandaloneClient.CS().asyncNotify(ip, port, notification);
	}
	
	public ServerMessage read(String ip, int port, ServerMessage request) throws ClassNotFoundException, RemoteReadException, IOException
	{
		return StandaloneClient.CS().read(ip, port, request);
	}

	public void syncNotifyRoot(ServerMessage notification) throws IOException, InterruptedException
	{
		StandaloneClient.CS().syncNotify(this.rootAddress.getIP(), this.rootAddress.getPort(), notification);
	}
	
	public void asyncNotifyRoot(ServerMessage notification)
	{
		StandaloneClient.CS().asyncNotify(this.rootAddress.getIP(), this.rootAddress.getPort(), notification);
	}
	
	public ServerMessage readRoot(ServerMessage request) throws ClassNotFoundException, RemoteReadException, IOException
	{
//		return StandaloneClient.CS().read(this.rootAddress.getIP(), this.rootAddress.getPort(), request);
		ServerMessage msg = StandaloneClient.CS().read(this.rootAddress.getIP(), this.rootAddress.getPort(), request);
		log.info("StandaloneClusterClient-readRoot(): response received!");
		return msg;
	}

	public void syncNotifyRegistry(ServerMessage notification) throws IOException, InterruptedException
	{
		StandaloneClient.CS().syncNotify(this.registryIP, this.registryPort, notification);
	}
	
	public void asyncNotifyRegistry(ServerMessage notification)
	{
		StandaloneClient.CS().asyncNotify(this.registryIP, this.registryPort, notification);
	}
	
	public ServerMessage readRegistry(ServerMessage request) throws ClassNotFoundException, RemoteReadException, IOException
	{
		return StandaloneClient.CS().read(this.registryIP, this.registryPort, request);
	}
}
