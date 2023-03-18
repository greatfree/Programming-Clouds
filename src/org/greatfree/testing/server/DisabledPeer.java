package org.greatfree.testing.server;

import java.io.IOException;

import org.greatfree.exceptions.DuplicatePeerNameException;
import org.greatfree.exceptions.RemoteIPNotExistedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.exceptions.ServerPortConflictedException;
import org.greatfree.util.TerminateSignal;

/**
 * 
 * @author libing
 * 
 * 03/05/2023
 *
 */
final class DisabledPeer
{
	public static void main(String[] args) throws IOException, ClassNotFoundException, RemoteReadException, DuplicatePeerNameException, RemoteIPNotExistedException, ServerPortConflictedException
	{
		System.out.println("UnaryPeer starting up ...");
//		UnaryPeer.CS().start("xxx", true, true);
//		UnaryPeer.CS().start("xxx", true);
		UnaryPeer.CS().start("xxx", false);
		System.out.println("UnaryPeer started ...");
		TerminateSignal.SIGNAL().waitTermination();
	}
}
