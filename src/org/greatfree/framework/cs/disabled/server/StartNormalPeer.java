package org.greatfree.framework.cs.disabled.server;

import java.io.IOException;

import org.greatfree.exceptions.DuplicatePeerNameException;
import org.greatfree.exceptions.RemoteIPNotExistedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.exceptions.ServerPortConflictedException;
import org.greatfree.framework.cs.disabled.Config;
import org.greatfree.server.container.UnaryDisabledPeer;
import org.greatfree.util.TerminateSignal;

/**
 * 
 * @author libing
 * 
 * 03/08/2023
 *
 */
final class StartNormalPeer
{

	public static void main(String[] args) throws ClassNotFoundException, IOException, RemoteReadException, DuplicatePeerNameException, RemoteIPNotExistedException, ServerPortConflictedException
	{
		System.out.println("Disabled Peer starting ...");
		UnaryDisabledPeer.CS().start(Config.FRIEND_B, new DServerTask(), false, Config.WAIT_TIME);
		System.out.println("Disabled Peer started ...");
		TerminateSignal.SIGNAL().waitTermination();
	}

}
