package org.greatfree.server.container;

import java.io.IOException;

import org.greatfree.exceptions.BrokerClusterNotAvailableException;
import org.greatfree.exceptions.DuplicatePeerNameException;
import org.greatfree.exceptions.RemoteIPNotExistedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.exceptions.ServerPortConflictedException;
import org.greatfree.framework.p2p.RegistryConfig;
import org.greatfree.message.ServerMessage;
import org.greatfree.message.container.Notification;
import org.greatfree.message.container.Request;

/**
 * 
 * @author libing
 * 
 * 03/06/2023
 *
 */
public final class UnaryDisabledPeer
{
	private DisabledPeer peer;
	
	private UnaryDisabledPeer()
	{
	}
	
	private static UnaryDisabledPeer instance = new UnaryDisabledPeer();
	
	public static UnaryDisabledPeer CS()
	{
		if (instance == null)
		{
			instance = new UnaryDisabledPeer();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	public void stop(long timeout) throws ClassNotFoundException, InterruptedException, RemoteReadException, RemoteIPNotExistedException, IOException
	{
		this.peer.stop(timeout);
	}

	public void start(String name, ServerTask task, boolean isServerDisabled, long waitTime) throws IOException, ClassNotFoundException, RemoteReadException, DuplicatePeerNameException, RemoteIPNotExistedException, ServerPortConflictedException
	{
		this.peer = new DisabledPeer.DisabledPeerBuilder()
				.peerName(name)
				.peerPort(8900)
				.listenerCount(10)
				.maxIOCount(10)
				.registryIP(RegistryConfig.PEER_REGISTRY_ADDRESS)
				.registryPort(RegistryConfig.PEER_REGISTRY_PORT)
				.task(task)
				.isServerDisabled(isServerDisabled)
				.waitTime(waitTime)
				.build();
		this.peer.start();
	}
	
	public void notify(String peerName, Notification notification) throws ClassNotFoundException, RemoteReadException, RemoteIPNotExistedException, IOException, InterruptedException, BrokerClusterNotAvailableException
	{
		this.peer.notify(peerName, notification);
	}
	
	public ServerMessage read(String peerName, Request request) throws ClassNotFoundException, RemoteReadException, RemoteIPNotExistedException, BrokerClusterNotAvailableException, IOException, InterruptedException
	{
		return this.peer.read(peerName, request);
	}
}
