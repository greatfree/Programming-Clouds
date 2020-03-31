package edu.greatfree.container.cps.coordinator;

import java.io.IOException;

import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.message.ServerMessage;
import org.greatfree.server.container.PeerContainer;
import org.greatfree.server.container.ServerTask;
import org.greatfree.util.TerminateSignal;

import edu.greatfree.container.cps.CPSConfig;

// Created: 12/31/2018, Bing Li
class Coordinator
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

	public void stop(long timeout) throws ClassNotFoundException, IOException, InterruptedException, RemoteReadException
	{
		TerminateSignal.SIGNAL().setTerminated();
		this.peer.stop(timeout);
	}

	public void start(String peerName, int port, ServerTask task, boolean isRegistryNeeded) throws IOException, ClassNotFoundException, RemoteReadException
	{
		this.peer = new PeerContainer(peerName, port, task, isRegistryNeeded);
		this.peer.start();
	}

	public void notify(ServerMessage notification) throws IOException, InterruptedException
	{
		this.peer.syncNotify(CPSConfig.TERMINAL_ADDRESS, CPSConfig.TERMINAL_PORT, notification);
	}
	
	public ServerMessage read(ServerMessage request) throws ClassNotFoundException, RemoteReadException, IOException
	{
		return (ServerMessage)this.peer.read(CPSConfig.TERMINAL_ADDRESS, CPSConfig.TERMINAL_PORT, request);
	}
}
