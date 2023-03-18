package org.greatfree.framework.cs.disabled.client;

import java.io.IOException;

import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.BrokerClusterNotAvailableException;
import org.greatfree.exceptions.DuplicatePeerNameException;
import org.greatfree.exceptions.RemoteIPNotExistedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.exceptions.ServerPortConflictedException;
import org.greatfree.framework.cs.disabled.Config;
import org.greatfree.framework.cs.disabled.message.DSNotification;
import org.greatfree.framework.cs.disabled.message.DSRequest;
import org.greatfree.framework.cs.disabled.message.DSResponse;
import org.greatfree.framework.cs.disabled.message.DSShutdownNotification;
import org.greatfree.server.container.UnaryDisabledPeer;
import org.greatfree.util.Tools;

/**
 * 
 * @author libing
 * 
 * 03/06/2023
 *
 */
final class StartPeerAsClient
{
	public static void main(String[] args) throws ClassNotFoundException, IOException, RemoteReadException, DuplicatePeerNameException, RemoteIPNotExistedException, ServerPortConflictedException, InterruptedException
	{
		UnaryDisabledPeer.CS().start(Config.FRIEND_A, new DServerTask(), false, Config.WAIT_TIME);
		System.out.println("Say something to " + Config.FRIEND_B + " as notifications");
		String words = Tools.INPUT.nextLine();

		try
		{
			UnaryDisabledPeer.CS().notify(Config.FRIEND_B, new DSNotification(words));
		}
		catch (BrokerClusterNotAvailableException e)
		{
			System.out.println(e);
		}

		System.out.println("Say something to " + Config.FRIEND_B + " as requests");
		words = Tools.INPUT.nextLine();

		try
		{
			DSResponse res = (DSResponse)UnaryDisabledPeer.CS().read(Config.FRIEND_B, new DSRequest(words));
			System.out.println(Config.FRIEND_B + " responds, " + res.getMessage());
		}
		catch (BrokerClusterNotAvailableException e)
		{
			System.out.println(e);
		}

		System.out.println("Press Enter to shutdown the peer ...");
		Tools.INPUT.nextLine();
		
		try
		{
			UnaryDisabledPeer.CS().notify(Config.FRIEND_B, new DSShutdownNotification());
		}
		catch (BrokerClusterNotAvailableException e)
		{
			System.out.println(e);
		}

		System.out.println("Press Enter to exit ...");
		Tools.INPUT.nextLine();
		
		UnaryDisabledPeer.CS().stop(ServerConfig.SERVER_SHUTDOWN_TIMEOUT);
	}
}
