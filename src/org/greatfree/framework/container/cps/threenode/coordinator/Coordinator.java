package org.greatfree.framework.container.cps.threenode.coordinator;

import java.io.IOException;

import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.DuplicatePeerNameException;
import org.greatfree.exceptions.RemoteIPNotExistedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.exceptions.ServerPortConflictedException;
import org.greatfree.message.ServerMessage;
import org.greatfree.server.container.PeerContainer;
import org.greatfree.server.container.ServerTask;
import org.greatfree.util.TerminateSignal;

// Created: 12/31/2018, Bing Li
public class Coordinator
{
	private PeerContainer peer;
	
	public Coordinator()
	{
	}
	
	private static Coordinator instance = new Coordinator();
	
	public static Coordinator CPS_CONTAINER()
	{
		if (instance == null)
		{
			instance = new Coordinator();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	public void stop(long timeout) throws ClassNotFoundException, IOException, InterruptedException, RemoteReadException, RemoteIPNotExistedException
	{
//		TerminateSignal.SIGNAL().setTerminated();
		TerminateSignal.SIGNAL().notifyAllTermination();
		this.peer.stop(timeout);
	}

	public void start(String peerName, int port, ServerTask task, boolean isRegistryNeeded) throws IOException, ClassNotFoundException, RemoteReadException, DuplicatePeerNameException, RemoteIPNotExistedException, ServerPortConflictedException
	{
		this.peer = new PeerContainer(peerName, port, task, isRegistryNeeded);
		this.peer.start();
	}

	public void notify(ServerMessage notification) throws IOException, InterruptedException
	{
		this.peer.syncNotify(ServerConfig.TERMINAL_ADDRESS, ServerConfig.TERMINAL_PORT, notification);
	}
	
	public ServerMessage read(ServerMessage request) throws ClassNotFoundException, RemoteReadException, RemoteIPNotExistedException
	{
		return (ServerMessage)this.peer.read(ServerConfig.TERMINAL_ADDRESS, ServerConfig.TERMINAL_PORT, request);
	}
}
