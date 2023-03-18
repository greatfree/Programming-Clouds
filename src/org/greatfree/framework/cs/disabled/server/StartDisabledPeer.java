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
 * 03/06/2023
 *
 */
final class StartDisabledPeer
{
	public static void main(String[] args) throws IOException, ClassNotFoundException, RemoteReadException, DuplicatePeerNameException, RemoteIPNotExistedException, ServerPortConflictedException
	{
		System.out.println("Disabled Peer starting ...");
		UnaryDisabledPeer.CS().start(Config.FRIEND_B, new DServerTask(), true, Config.WAIT_TIME);
		System.out.println("Disabled Peer started ...");
		TerminateSignal.SIGNAL().waitTermination();
	}
}
