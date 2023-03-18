package org.greatfree.testing.server;

import java.io.IOException;

import org.greatfree.chat.message.ShutdownServerNotification;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.DuplicatePeerNameException;
import org.greatfree.exceptions.RemoteIPNotExistedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.exceptions.ServerPortConflictedException;
import org.greatfree.framework.p2p.RegistryConfig;
import org.greatfree.testing.message.TNNotification;
import org.greatfree.testing.message.TestRequest;
import org.greatfree.testing.message.TestResponse;
import org.greatfree.util.Tools;

/**
 * 
 * @author libing
 * 
 * 03/05/2023
 *
 */
final class DisabledPeerClient
{
	public static void main(String[] args) throws ClassNotFoundException, DuplicatePeerNameException, ServerPortConflictedException
	{
		System.out.println("UnaryPeer as client starting up ...");
//		UnaryPeer.CS().start("yyy", true, false);
		try
		{
			UnaryPeer.CS().start("yyy", true);
		}
		catch (RemoteReadException | RemoteIPNotExistedException e)
		{
			System.out.println("Registry Server is not available!");
		}
		System.out.println("UnaryPeer as client started ...");
		
		System.out.println("Say something to the disabled peer ...");
		String words = Tools.INPUT.nextLine();
		try
		{
			UnaryPeer.CS().notify(RegistryConfig.PEER_REGISTRY_ADDRESS, ServerConfig.COORDINATOR_PORT, new TNNotification(words));
		}
		catch (IOException | InterruptedException e)
		{
			System.out.println("Server is not available!");
		}
		
		System.out.println("Ask something to the disabled peer ...");
		words = Tools.INPUT.nextLine();
		try
		{
			TestResponse response = (TestResponse)UnaryPeer.CS().read(RegistryConfig.PEER_REGISTRY_ADDRESS, ServerConfig.COORDINATOR_PORT, new TestRequest(words), 1000);
			if (response != null)
			{
				System.out.println(response.getResponse());
			}
			else
			{
				System.out.println("No response!");
			}
		}
		catch (IOException | RemoteReadException | RemoteIPNotExistedException e)
		{
			System.out.println("Server is not available!");
		}

		System.out.println("Press Enter to shutdown server ...");
		Tools.INPUT.nextLine();
		try
		{
			UnaryPeer.CS().notify(RegistryConfig.PEER_REGISTRY_ADDRESS, ServerConfig.COORDINATOR_PORT, new ShutdownServerNotification());
		}
		catch (IOException | InterruptedException e)
		{
			System.out.println("Server is not available!");
		}
		System.out.println("Press Enter to quit ...");
		Tools.INPUT.nextLine();
		try
		{
			UnaryPeer.CS().stop();
		}
		catch (IOException | RemoteIPNotExistedException | InterruptedException | RemoteReadException e)
		{
			System.out.println("Registry Server is not available!");
		}
	}
}
